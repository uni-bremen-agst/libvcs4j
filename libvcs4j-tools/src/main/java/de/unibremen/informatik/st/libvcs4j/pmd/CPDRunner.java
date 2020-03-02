package de.unibremen.informatik.st.libvcs4j.pmd;

import de.unibremen.informatik.st.libvcs4j.Revision;
import de.unibremen.informatik.st.libvcs4j.Validate;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.pmd.cpd.CPD;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Allows to configure and run PMD on {@link Revision} instances.
 */
@Slf4j
public class CPDRunner {

    /**
     * List of default rules.
     */
    private static final List<String> DEFAULT_RULES = List.of(
            "category/java/design.xml/GodClass",
            "category/java/design.xml/ExcessiveClassLength",
            "category/java/design.xml/ExcessiveMethodLength",
            "category/java/design.xml/ExcessiveParameterList",
            "category/java/bestpractices.xml/UnusedFormalParameter",
            "category/java/bestpractices.xml/UnusedLocalVariable",
            "category/java/bestpractices.xml/UnusedPrivateField",
            "category/java/bestpractices.xml/UnusedPrivateMethod");

    /**
     * PMD rules to apply.
     */
    private final List<String> rules;

    /**
     * PMD cache file (incremental analysis). Is set lazily.
     */
    private Path cacheFile = null;

    /**
     * Minimum Tokens that have to match for duplicate to be reported
     */
    @Getter
    @Setter
    private int minimumTokens = 100;

    /**
     * Creates a new runner with given PMD rules. {@code null} values in
     * {@code rules} are filtered out. Non-Null values are trimmed with
     * {@link String#trim()}. If {@code rules} is empty, the rules listed in
     * {@link #DEFAULT_RULES} are used.
     *
     * @param rules
     * 		The PMD rules to apply.
     * @throws NullPointerException
     * 		If {@code rules} is {@code null}.
     */
    public CPDRunner(@NonNull final List<String> rules)
            throws NullPointerException {
        this.rules = rules.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .collect(Collectors.toList());
        if (this.rules.isEmpty()) {
            this.rules.addAll(DEFAULT_RULES);
        }
    }

    /**
     * Creates a new runner with given PMD rules. {@code null} values in
     * {@code rules} are filtered out. Non-Null values are trimmed with
     * {@link String#trim()}. If {@code rules} is empty, the ruleset
     * "rulesets/java/basic.xml" is used as fallback.
     *
     * @param rules
     * 		The PMD rules to apply.
     * @throws NullPointerException
     * 		If {@code rules} is {@code null}.
     */
    public CPDRunner(@NonNull final String... rules)
            throws NullPointerException {
        this(Arrays.asList(rules));
    }

    /**
     * Analyzes the given revision.
     *
     * @param revision
     * 		The revision to analyze.
     * @return
     * 		The detection result.
     * @throws NullPointerException
     * 		If {@code revision} is {@code null}.
     * @throws IOException
     * 		If an error occurred while analyzing {@code revision}.
     */
    protected CPDDetectionResult analyze(@NonNull final Revision revision)
            throws IOException {
        Validate.validateState(!rules.isEmpty());

        final List<String> args = new ArrayList<>();
        // language
        // tokens
        args.add("--minimum-tokens");
        args.add(String.valueOf(minimumTokens));
        // input
        args.add("--files");
        args.add(revision.getOutput().toString());
        // format
        args.add("--format");
        args.add("xml");

        // Temporarily redirect stdout to a string.
        final PrintStream stdout = System.out;
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        final PrintStream ps = new PrintStream(bos);
        System.setOut(ps);
        try {
            CPD.main(args.toArray(String[]::new));
            // According to PMD the resulting xml is UTF-8 encoded.
            final String output = bos.toString(StandardCharsets.UTF_8.name());

            // Parse output
            final SAXParserFactory factory = SAXParserFactory.newInstance();
            final SAXParser saxParser = factory.newSAXParser();
            final InputStream bis = new ByteArrayInputStream(
                    output.getBytes(StandardCharsets.UTF_8.name()));
            CPDSaxHandler handler = new CPDSaxHandler(revision.getFiles());
            saxParser.parse(bis, handler);

            // Result
            return new CPDDetectionResult(handler.getViolations());
        } catch (final UnsupportedOperationException | SAXException
                | ParserConfigurationException e) {
            throw new IOException(e);
        } finally {
            // Make sure to reset stdout.
            System.setOut(stdout);
        }
    }
}
