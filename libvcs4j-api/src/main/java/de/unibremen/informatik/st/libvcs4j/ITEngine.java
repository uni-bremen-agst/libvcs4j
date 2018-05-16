package de.unibremen.informatik.st.libvcs4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * This engine is supposed to extract issues from an issue tracker, such as
 * Github, Gitlab, and so on.
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
	Optional<Issue> getIssueById(String id) throws IOException;

	/**
	 * Returns all issues referenced by the given commit. This method does not
	 * fail if {@code commit} is {@code null} and does not return the same
	 * issue (according to {@link Issue#getId()}) twice.
	 *
	 * The default implementation delegates {@code commit} to
	 * {@link #getIssuesFor(List)}.
	 *
	 * @param commit
	 *      The commit to parse.
	 * @return
	 *      The referenced issues.
	 * @throws IOException
	 *      If an error occurred while retrieving an issue.
	 */
	default List<Issue> getIssuesFor(Commit commit) throws IOException {
		return getIssuesFor(Collections.singletonList(commit));
	}

	/**
	 * Returns all issues referenced by the given list of commits. This method
	 * does not fail if {@code commits} is {@code null} or contains
	 * {@code null} values and does not return the same issue (according to
	 * {@link Issue#getId()}) twice.
	 *
	 * The default implementation parses the commit messages using
	 * {@link #parseIssueIds(String)} and creates an {@link Issue} instance for
	 * each id found using {@link #getIssueById(String)}.
	 *
	 * @param commits
	 *      The commits to parse.
	 * @return
	 *      The referenced issues.
	 * @throws IOException
	 *      If an error occurred while retrieving an issue.
	 */
	default List<Issue> getIssuesFor(final List<Commit> commits)
			throws IOException {
		final List<String> ids =
				(commits == null ? Collections.<Commit>emptyList() : commits)
				.stream()
				.filter(Objects::nonNull)
				.map(Commit::getId)
				.collect(Collectors.toList());
		final Map<String, Issue> issues = new HashMap<>();
		for (final String id : ids) {
			if (!issues.containsKey(id)) {
				issues.put(id, getIssueById(id).orElse(null));
			}
		}
		issues.values().removeIf(Objects::isNull);
		return new ArrayList<>(issues.values());
	}

	/**
	 * Returns all issues referenced by the given revision range. This method
	 * does not fail if {@code range} is {@code null} and does not return the
	 * same issue (according to {@link Issue#getId()}) twice.
	 *
	 * The default implementations delegates the referenced commits
	 * ({@link RevisionRange#getCommits()}) to {@link #getIssuesFor(List)}.
	 *
	 * @param range
	 *      The revision range to parse.
	 * @return
	 *      The referenced issues.
	 * @throws IOException
	 *      If an error occurred while retrieving an issue.
	 */
	default List<Issue> getIssuesFor(final RevisionRange range)
			throws IOException {
		if (range == null) {
			return new ArrayList<>();
		} else {
			return getIssuesFor(range.getCommits());
		}
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
