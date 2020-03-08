package de.unibremen.informatik.st.olfaction;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import de.unibremen.informatik.st.libvcs4j.VCSEngineBuilder;
import de.unibremen.informatik.st.libvcs4j.VCSFile.Position;
import de.unibremen.informatik.st.libvcs4j.VCSFile.Range;
import de.unibremen.informatik.st.libvcs4j.mapping.Mappable;
import de.unibremen.informatik.st.libvcs4j.mapping.Mapping;
import de.unibremen.informatik.st.libvcs4j.pmd.PMDRunner;
import de.unibremen.informatik.st.libvcs4j.pmd.CPDRunner;

/**
 * Fills the Olfaction DB with violations found by PMD and LibVCS4J.
 */
public class Seed {

    private static class GraphQLException extends Exception {
        private static final long serialVersionUID = 2346292760885729306L;
        public JSONArray errors;

        GraphQLException(final JSONArray errors) {
            super(
                (String) errors.stream()
                    .map(error -> {
                        final var jsonError = (JSONObject) error;
                        var message = "Error: " + jsonError.get("message");
                        final var path = (JSONArray) jsonError.get("path");
                        if (path != null) {
                            message += "\n\tAt field " + String.join(".", path);
                        }
                        return message;
                    })
                    .collect(Collectors.joining("\n"))
            );
            this.errors = errors;
        }
    }

    /**
     * Converts a LibVCS4J Position object to an Olfaction GraphQL API Position
     * object.
     */
    private static JSONObject convertPositionToJSON(final Position position) {
        return new JSONObject(
            Map.ofEntries(
                Map.entry("line", position.getLine() - 1),
                Map.entry("character", position.getLineOffset())
            )
        );
    }

    /**
     * Converts a LibVCS4J Range object to an Olfaction GraphQL API Location object.
     */
    private static JSONObject convertRangeToJSON(final Range range) {
        return new JSONObject(
            Map.ofEntries(
                Map.entry("file", range.getFile().getRelativePath()),
                Map.entry(
                    "range",
                    new JSONObject(
                        Map.ofEntries(
                            Map.entry("start", convertPositionToJSON(range.getBegin())),
                            Map.entry("end", convertPositionToJSON(range.getEnd()))
                        )
                    )
                )
            )
        );
    }

    public static JSONObject makeGraphQLRequest(final String query, final JSONObject variables, final HttpClient client,
        final URI endpoint)
        throws Exception {

        final var request = new HttpPost(endpoint.resolve("/graphql"));
        final var jsonBody = new JSONObject(
            Map.ofEntries(
                Map.entry("query", query),
                Map.entry(
                    "variables", variables
                )
            )
        );
        final var bodyString = jsonBody.toString();
        final var entity = new StringEntity(bodyString);
        entity.setContentType("application/json");
        request.setEntity(entity);
        final var response = client.execute(request);
        System.out.println(
            "Response: " + response.getStatusLine().getStatusCode() + " "
                + response.getStatusLine().getReasonPhrase()
        );
        final var responseEntity = response.getEntity();
        final var responseBody = EntityUtils.toString(responseEntity);
        if (response.getStatusLine().getStatusCode() == 401) {
            throw new Exception("Server is password protected. Please provide correct --username and --password.");
        }
        if (!responseEntity.getContentType().getValue().contains("application/json")) {
            throw new Exception("Response not JSON: " + responseBody);
        }
        final var jsonParser = new JSONParser();
        final var parsedBody = (JSONObject) jsonParser.parse(responseBody);
        final var errors = (JSONArray) parsedBody.get("errors");
        if (errors != null) {
            throw new GraphQLException(errors);
        }
        if (response.getStatusLine().getStatusCode() != 200) {
            throw new Exception(responseBody);
        }
        final var data = (JSONObject) parsedBody.get("data");
        return data;

    }

    public static void main(final String[] args) throws Exception {
        final var options = new Options();

        final var helpOption = new Option("h", "help", false, "Show help");
        options.addOption(helpOption);

        final var endpointOption = new Option("e", "endpoint", true, "Olfaction server endpoint");
        endpointOption.setRequired(true);
        options.addOption(endpointOption);

        final var usernameOption = new Option("u", "username", true, "Username (optional)");
        options.addOption(usernameOption);

        final var passwordOption = new Option("p", "password", true, "Password (optional)");
        options.addOption(passwordOption);

        final var analysisOption = new Option("a", "analysis", true, "Name of the analysis to create");
        analysisOption.setRequired(true);
        options.addOption(analysisOption);

        final var rulesetOption = new Option("rs", "ruleset", true, "Path to PMD ruleset file");
        rulesetOption.setRequired(true);
        options.addOption(rulesetOption);

        final var repositoryOption = new Option("r", "repository", true, "Path or Git clone URL of repository");
        repositoryOption.setRequired(true);
        options.addOption(repositoryOption);

        final var repositoryNameOption = new Option("n", "repository-name", true, "Name for the repository");
        repositoryNameOption.setRequired(true);
        options.addOption(repositoryNameOption);

        final var parser = new DefaultParser();
        final var formatter = new HelpFormatter();
        CommandLine cmd;
        try {
            cmd = parser.parse(options, args);
        } catch (final org.apache.commons.cli.ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("olfaction-seed", options);
            System.exit(1);
            return;
        }

        final var endpoint = new URI(cmd.getOptionValue("endpoint"));
        final var ruleset = cmd.getOptionValue("ruleset");
        final var repository = cmd.getOptionValue("repository");
        final var repositoryName = cmd.getOptionValue("repository-name");
        final var analysisName = cmd.getOptionValue("analysis");
        final var username = cmd.getOptionValue("username");
        final var password = cmd.getOptionValue("password");

        final var clientBuilder = HttpClientBuilder.create();
        if (username != null && password != null) {
            final var provider = new BasicCredentialsProvider();
            final var credentials = new UsernamePasswordCredentials(username, password);
            provider.setCredentials(AuthScope.ANY, credentials);
            clientBuilder.setDefaultCredentialsProvider(provider);
        }
        final var client = clientBuilder.build();
        final var engine = VCSEngineBuilder.ofGit(repository).build();

        try {
            makeGraphQLRequest(
                String.join(
                    "\n",
                    "mutation CreateAnalysis($input: CreateAnalysisInput!) {",
                    "  createAnalysis(input: $input) {",
                    "    analysis {",
                    "      name",
                    "    }",
                    "  }",
                    "}"
                ),
                new JSONObject(
                    Map.ofEntries(
                        Map.entry(
                            "input", new JSONObject(
                                Map.ofEntries(
                                    Map.entry("name", analysisName)
                                )
                            )
                        )
                    )

                ),
                client,
                endpoint
            );
        } catch (GraphQLException e) {
            System.out.println("Analysis already exists");
        }

        //final var pmd = new PMDRunner(ruleset);
        final var pmd = new CPDRunner();
        // Associates violations with previous violations
        final var mapping = new Mapping<String>();
        // Tracks lifespan IDs for violations
        final var violationLifespans = new IdentityHashMap<Mappable<String>, UUID>();

        final var commitCodeSmells = new ArrayList<JSONObject>();

        // Check which commits were already analyzed to skip them
        final var analyzedCommitsResult = makeGraphQLRequest(
            String.join(
                "\n",
                "query($analysis: String!, $repository: String!) {",
                "  analysis(name: $analysis) {",
                "    analyzedCommits(repository: $repository) {",
                "      edges {",
                "        node {",
                "          oid",
                "        }",
                "      }",
                "    }",
                "  }",
                "}"
            ),
            new JSONObject(
                Map.ofEntries(
                    Map.entry("analysis", analysisName),
                    Map.entry("repository", repositoryName)
                )
            ),
            client,
            endpoint
        );
        final var analyzedOids = (Set<String>) ((JSONArray) ((JSONObject) ((JSONObject) analyzedCommitsResult
            .get("analysis"))
                .get("analyzedCommits"))
                    .get("edges"))
                        .stream()
                        .map(edge -> (String) ((JSONObject) ((JSONObject) edge).get("node")).get("oid"))
                        .collect(Collectors.toCollection(HashSet::new));
        System.out.println("" + analyzedOids.size() + " commits were already analyzed");

        var i = 0;
        for (final var revisionRange : engine) {
            i++;
            final var revision = revisionRange.getRevision();
            if (analyzedOids.contains(revision.getId())) {
                System.out.println("Skipping revision " + revision.getId() + ", already analyzed");
                continue;
            }
            System.out.println("Analyzing " + i + ". revision " + revision.getId());
            // Violations found by PMD in this revision
            try {
                System.out.println("pre-analysis");
                final var violations = pmd.analyze(revision).getViolations();
                System.out.println("analyze done");
                System.out.println("pre-mapping");
                final var mappingResult = mapping.map(violations, revisionRange);
                System.out.println("mapping done");

                final var jsonCodeSmells = new ArrayList<JSONObject>();

                for (final var violation : violations) {
                    var lifespanID = UUID.randomUUID();
                    final var predecessor = mappingResult.getPredecessor(violation);
                    if (predecessor.isPresent()) {
                        lifespanID = violationLifespans.get(predecessor.get());
                        if (lifespanID == null) {
                            throw new RuntimeException("Missing lifespan id for predecessor finding");
                        }
                    }
                    violationLifespans.put(violation, lifespanID);
                    System.out.println("Lifespan assigned");

                    final var jsonLocations = violation.getRanges().stream()
                        .map(Seed::convertRangeToJSON)
                        .collect(Collectors.toList());

                    final var jsonCodeSmell = new JSONObject(
                        Map.ofEntries(
                            Map.entry("kind", violation.getMetadata().get()),
                            Map.entry("lifespan", lifespanID.toString()),
                            Map.entry("ordinal", mappingResult.getOrdinal()),
                            Map.entry("locations", jsonLocations)
                        )
                    );
                    jsonCodeSmells.add(jsonCodeSmell);
                }
                commitCodeSmells.add(
                    new JSONObject(
                        Map.ofEntries(
                            Map.entry("oid", revision.getId()),
                            Map.entry("codeSmells", jsonCodeSmells)
                        )
                    )
                );
                System.out.println("Found " + jsonCodeSmells.size() + " code smells");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.out.println("Making AddCodeSmells request");
        final var addCodeSmellsResult = makeGraphQLRequest(
            String.join(
                "\n",
                "mutation AddCodeSmells($input: AddCodeSmellsInput!) {",
                "  addCodeSmells(input: $input) {",
                "    codeSmells {",
                "      id",
                "    }",
                "  }",
                "}"
            ),
            new JSONObject(
                Map.ofEntries(
                    Map.entry(
                        "input", new JSONObject(
                            Map.ofEntries(
                                Map.entry(
                                    "repositories", List.of(
                                        new JSONObject(
                                            Map.ofEntries(
                                                Map.entry("name", repositoryName),
                                                Map.entry("commits", commitCodeSmells)
                                            )
                                        )
                                    )
                                ),
                                Map.entry("analysis", analysisName)
                            )
                        )
                    )
                )
            ),
            client,
            endpoint
        );

        final var mutationPayload = (JSONObject) addCodeSmellsResult.get("addCodeSmells");
        final var codeSmells = (JSONArray) mutationPayload.get("codeSmells");
        System.out.println("Created " + codeSmells.size() + " code smells");
    }
}
