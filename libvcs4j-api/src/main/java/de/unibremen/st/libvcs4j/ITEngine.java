package de.unibremen.st.libvcs4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This engine is supposed to extract issues from an issue tracker, such as
 * Github, Gitlab, and so on.
 *
 * Implementations may support username/password or access token
 * authentication.
 */
@SuppressWarnings("unused")
public interface ITEngine {

	/**
     * Returns the processed repository.
     *
     * @return
     *      The processed repository.
     */
    String getRepository();

    /**
     * Returns the username used for authentication, if any.
     *
     * @return
     *      The username used for authentication, if any.
     */
    Optional<String> getUsername();

    /**
     * Returns the password used for authentication, if any.
     *
     * @return
     *      The password used for authentication, if any.
     */
    Optional<String> getPassword();

    /**
     * Returns the access token used for authentication, if any.
     *
     * @return
     *      The access token used for authentication, if any.
     */
    Optional<String> getToken();

    /**
     * Returns the issue with the given id (see {@link Issue#getId()}). Returns
     * an empty {@link Optional} if there is no such issue. This method does
     * not fail if {@code id} is {@code null}.
     *
     * @param id
     *      The id of the issue to fetch.
     * @return
     *      The issue with the given id.
     * @throws IOException
     *      If an error occurred while retrieving the issue.
     */
    Optional<Issue> getIssueById(final String id) throws IOException;

    /**
     * Returns all issues referenced by the given {@link Commit}. This method
     * does not fail if {@code commit} is {@code null}.
     *
     * The default implementation delegates {@link Commit#getMessage()} to
     * {@link #parseIssueIds(String)} and creates an issue instance for each
     * id found using {@link #getIssueById(String)}.
     *
     * @param commit
     *      The commit to parse.
     * @return
     *      The referenced issues.
     * @throws IOException
     *      If an error occurred while retrieving an issue.
     */
    default List<Issue> getIssuesFor(final Commit commit) throws IOException {
        final List<Issue> issues = new ArrayList<>();
        if (commit != null) {
            final List<String> ids = parseIssueIds(commit.getMessage());
            for (final String id : ids) {
                getIssueById(id).ifPresent(issues::add);
            }
        }
        return issues;
    }

    /**
     * Parses the given text and returns all referenced issue ids. This method
     * does not fail if {@code text} is {@code null}.
     *
     * The default implementation searches for patterns like '#6'.
     *
     * @param text
     *      The text to parse.
     * @return
     *      The referenced issue ids.
     */
    default List<String> parseIssueIds(final String text) {
        final List<String> ids = new ArrayList<>();
        if (text != null) {
            final Matcher matcher = Pattern
                    .compile("#(\\d+)")
                    .matcher(text);
            while (matcher.find()) {
                String id = matcher.group();
                id = id.substring(1, id.length());
                ids.add(id);
            }
        }
        return ids;
    }
}
