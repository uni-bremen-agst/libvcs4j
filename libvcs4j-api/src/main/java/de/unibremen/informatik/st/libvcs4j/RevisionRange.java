package de.unibremen.informatik.st.libvcs4j;

import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static de.unibremen.informatik.st.libvcs4j.FileChange.Type.*;

/**
 * This class represents the state transition between two revisions. The "from"
 * revision (if any) is available with {@link #getPredecessorRevision()}. The
 * "to" revision is available with {@link #getRevision()}.
 *
 * A single range may subsume several commits to merge commits on, for
 * instance, a monthly basis.
 */
public interface RevisionRange extends VCSModelElement {

	/**
	 * Returns the ordinal of this range. Ordinals are used to identify
	 * individual ranges with a serial number when processing a VCS. The origin
	 * is 1.
	 *
	 * @return
	 * 		The ordinal of this range ({@code >= 1}).
	 */
	int getOrdinal();

	/**
	 * Returns the "to" revision the file changes of this range belong to.
	 *
	 * @return
	 * 		The "to" revision the file changes of this range belong to.
	 */
	Revision getRevision();

	/**
	 * Returns the "from" revision the file changes of this range belong to.
	 *
	 * @return
	 * 		The "from" revision the file changes of this range belong to or an
	 * 		empty {@link Optional} if this is the first range.
	 */
	Optional<Revision> getPredecessorRevision();

	/**
	 * Returns the commits that have been applied to
	 * {@link #getPredecessorRevision()} so that {@link #getRevision()} results
	 * from it. The order of the returned list is from oldest to latest.
	 * Contains at least one commit.
	 *
	 * @return
	 * 		The list of commits.
	 */
	List<Commit> getCommits();

	/**
	 * Returns the latest commit of {@link #getCommits()}.
	 *
	 * @return
	 * 		The latest commit of {@link #getCommits()}.
	 */
	default Commit getLatestCommit() {
		final List<Commit> commits = getCommits();
		Validate.validateState(!commits.isEmpty(),
				"Unexpected empty list of commits");
		return commits.get(commits.size() - 1);
	}

	/**
	 * Returns all files that have changed between
	 * {@link #getPredecessorRevision()} and {@link #getRevision()}. The
	 * default implementation, if necessary, merges the file changes of all
	 * commits listed in {@link #getCommits()}.
	 *
	 * @return
	 * 		The list of file changes.
	 */
	default List<FileChange> getFileChanges() {
		if (getCommits().size() == 1) {
			return getCommits().get(0).getFileChanges();
		} else {
			// Accumulates the result
			final List<FileChange> accum = new ArrayList<>(
					getCommits().get(0).getFileChanges());
			// Merge subsequent commits one after another
			for (int i = 1; i < getCommits().size(); i++) {
				// Try to find a match in this list
				final List<FileChange> toProcess = new ArrayList<>(
						getCommits().get(i).getFileChanges());
				// Use a ListIterator to remove/set the current value
				final ListIterator<FileChange> iter = accum.listIterator();
				while (iter.hasNext()) {
					final FileChange change = iter.next();
					final FileChange.Type type = change.getType();
					// Used to export the match from within a lambda
					final Deque<FileChange> matches = new ArrayDeque<>();
					if (type == ADD || type == REMOVE) {
						final Path path = type == ADD
								? change.getNewFile()
									.orElseThrow(IllegalStateException::new)
									.toRelativePath()
								: change.getOldFile()
									.orElseThrow(IllegalStateException::new)
									.toRelativePath();
						toProcess.stream().filter(c ->
								(type == ADD
										? c.getOldFile()
										: c.getNewFile())
								.map(VCSFile::toRelativePath)
								.map(p -> p.equals(path))
								.orElse(false))
						.findAny().ifPresent(match -> {
							final FileChange.Type oType = match.getType();
							Validate.validateState(
									!(type == ADD && oType == ADD),
									"'%s' has been added after being added",
									path);
							Validate.validateState(
									!(type == REMOVE && oType == REMOVE),
									"'%s' has been removed after being removed",
									path);
							toProcess.remove(match);
							if (oType == REMOVE) {
								iter.remove();
							} else {
								matches.add(match);
							}
						});
					} else {
						final Path path = change.getNewFile()
								.orElseThrow(IllegalStateException::new)
								.toRelativePath();
						toProcess.stream().filter(c ->
								(c.getType() != ADD
										? c.getOldFile()
										: c.getNewFile()) // < indicates a bug
								.map(VCSFile::toRelativePath)
								.map(p -> p.equals(path))
								.orElse(false)
						).findAny().ifPresent(match -> {
							final FileChange.Type oType = match.getType();
							Validate.validateState(
									!(type == MODIFY && oType == ADD),
									"'%s' has been added after being modified",
									path);
							Validate.validateState(
									!(type == RELOCATE && oType == ADD),
									"'%s' has been added after being relocated to this path",
									path);
							toProcess.remove(match);
							matches.add(match);
						});
					}
					Validate.validateState(matches.size() <= 1,
							"Unexpected number of matches (%d)",
							matches.size());
					if (!matches.isEmpty()) {
						final FileChange match = matches.poll();
						final VCSEngine engine = getVCSEngine();
						iter.set(engine.getModelFactory().createFileChange(
								change.getOldFile().orElse(null),
								match.getNewFile().orElse(null),
								engine));
					}
				}
				accum.addAll(toProcess);
			}
			// Postprocessing: Replace accumulated file changes such that the
			// revisions of the referenced files match with the predecessor and
			// successor revision of this range.
			final Revision predRev = getPredecessorRevision().orElse(null);
			final Revision rev = getRevision();
			final VCSEngine engine = getVCSEngine();
			final VCSModelFactory factory = engine.getModelFactory();
			final ListIterator<FileChange> it = accum.listIterator();
			while (it.hasNext()) {
				final FileChange change = it.next();
				final VCSFile newOldFile = change.getOldFile()
						.map(file -> {
							Validate.validateState(predRev != null);
							final String relPath = file.getRelativePath();
							final boolean revMatch = file.getRevision()
									.getId().equals(predRev.getId());
							return revMatch
									? file
									: factory.createVCSFile(
											relPath, predRev, engine);
						})
						.orElse(null);
				final VCSFile newNewFile = change.getNewFile()
						.map(file -> {
							final String relPath = file.getRelativePath();
							final boolean revMatch = file.getRevision()
									.getId().equals(rev.getId());
							return revMatch
									? file
									: factory.createVCSFile(
											relPath, rev, engine);
						})
						.orElse(null);
				final FileChange newChange = factory.createFileChange(
						newOldFile, newNewFile, engine);
				it.set(newChange);
			}
			return accum;
		}
	}

	/**
	 * Returns all files that have been added.
	 *
	 * @return
	 * 		All files that have been added.
	 */
	default List<FileChange> getAddedFiles() {
		return getFileChanges().stream()
				.filter(fc -> fc.getType() == ADD)
				.collect(Collectors.toList());
	}

	/**
	 * Returns all files that have been removed.
	 *
	 * @return
	 * 		All files that have been removed.
	 */
	default List<FileChange> getRemovedFiles() {
		return getFileChanges().stream()
				.filter(fc -> fc.getType() == REMOVE)
				.collect(Collectors.toList());
	}

	/**
	 * Returns all files that have been modified.
	 *
	 * @return
	 * 		All files that have been modified.
	 */
	default List<FileChange> getModifiedFiles() {
		return getFileChanges().stream()
				.filter(fc -> fc.getType() == MODIFY)
				.collect(Collectors.toList());
	}

	/**
	 * Returns all files that have been relocated.
	 *
	 * @return
	 * 		All files that have been relocated.
	 */
	default List<FileChange> getRelocatedFiles() {
		return getFileChanges().stream()
				.filter(fc -> fc.getType() == RELOCATE)
				.collect(Collectors.toList());
	}

	/**
	 * Filters the list of file changes returned by {@link #getFileChanges()}
	 * and returns only those whose old or the new relative path end with
	 * {@code suffix}.
	 *
	 * You may use this method to analyze file changes affecting a certain file
	 * type only. For instance, call {@code getFileChangesBySuffix(".java")} to
	 * get only the file changes affecting Java files.
	 *
	 * @param suffix
	 * 		The suffix used to filter the file changes.
	 * @return
	 * 		All file changes whose old or the new relative path end with
	 * 		{@code suffix}.
	 */
	default List<FileChange> getFileChangesBySuffix(final String suffix) {
		return getFileChanges().stream()
				.filter(fc -> {
					final boolean old = fc.getOldFile()
							.map(VCSFile::getRelativePath)
							.map(p -> p.endsWith(suffix))
							.orElse(false);
					final boolean nev = fc.getNewFile()
							.map(VCSFile::getRelativePath)
							.map(p -> p.endsWith(suffix))
							.orElse(false);
					return old || nev;
				})
				.collect(Collectors.toList());
	}

	/**
	 * Filters the list of file changes returned by {@link #getFileChanges()}
	 * and returns only those whose old or new relative path start with
	 * {@code prefix}.
	 *
	 * You may use this method to analyze file changes affecting a certain
	 * directory (and its sub-directories) only. For instance, call
	 * {@code getFileChangesByPrefix("src/main/java")} to get only the file
	 * changes affecting files located in "src/main/java".
	 *
	 * @param prefix
	 * 		The prefix used to filter the file changes.
	 * @return
	 * 		All file changes whose old or new relative file path start with
	 * 		{@code prefix}.
	 */
	default List<FileChange> getFileChangesByPrefix(final String prefix) {
		return getFileChanges().stream()
				.filter(fc -> {
					final boolean old = fc.getOldFile()
							.map(VCSFile::getRelativePath)
							.map(p -> p.startsWith(prefix))
							.orElse(false);
					final boolean nev = fc.getNewFile()
							.map(VCSFile::getRelativePath)
							.map(p -> p.startsWith(prefix))
							.orElse(false);
					return old || nev;
				})
				.collect(Collectors.toList());
	}

	/**
	 * Filters the list of file changes returned by {@link #getFileChanges()}
	 * and returns only those whose old or new relative path match
	 * {@code regex}. Paths are matched using {@link String#matches(String)}.
	 *
	 * @param regex
	 * 		The regular expression used to filter the file changes.
	 * @return
	 * 		All file changes whose old or new relative path match
	 * 		{@code regex}.
	 */
	default List<FileChange> getFileChangesByRegex(final String regex) {
		return getFileChanges().stream()
				.filter(fc -> {
					final boolean old = fc.getOldFile()
							.map(VCSFile::getRelativePath)
							.map(p -> p.matches(regex))
							.orElse(false);
					final boolean nev = fc.getNewFile()
							.map(VCSFile::getRelativePath)
							.map(p -> p.matches(regex))
							.orElse(false);
					return old || nev;
				})
				.collect(Collectors.toList());
	}

	/**
	 * Returns the issues referenced by the commits of this range. To enable
	 * this feature when processing a repository, set an appropriate
	 * {@link ITEngine} with {@link VCSEngine#setITEngine(ITEngine)}. The
	 * returned list does not contain the same issue (according to
	 * {@link Issue#getId()}) twice.
	 *
	 * @return
	 * 		The issues referenced by the commits of this range.
	 */
	default List<Issue> getIssues() {
		final List<Issue> issues = getCommits().stream()
				.map(Commit::getIssues)
				.flatMap(Collection::stream)
				.collect(Collectors.toList());
		final Map<String, Issue> idToIssue = new HashMap<>();
		issues.forEach(i -> idToIssue.put(i.getId(), i));
		return new ArrayList<>(idToIssue.values());
	}

	/**
	 * Returns whether this range is the first one. That is, there is no
	 * predecessor revision and, consequently, all changes returned by
	 * {@link #getFileChanges()} are additions. The default implementation
	 * simply checks whether {@link #getPredecessorRevision()} returns an empty
	 * {@link Optional}.
	 *
	 * @return
	 * 		{@code true} if this range is the first one, {@code false}
	 * 		otherwise.
	 */
	default boolean isFirst() {
		return !getPredecessorRevision().isPresent();
	}

	/**
	 * Runs the given action if this range is the first one.
	 *
	 * @param action
	 * 		The action to run if this is the first range.
	 */
	default void ifFirst(final Consumer<RevisionRange> action) {
		if (isFirst()) {
			action.accept(this);
		}
	}

	/**
	 * Runs the given action if this range is not the first one.
	 *
	 * @param action
	 * 		The action to run if this is not the first range.
	 */
	default void ifNotFirst(final Consumer<RevisionRange> action) {
		if (!isFirst()) {
			action.accept(this);
		}
	}

	/**
	 * Merges the given revision range and returns a new one, representing the
	 * state transition from {@code predecessor.getPredecessorRevision} to
	 * {@code this.getRevision()}. The commits of {@code predecessor} and
	 * {@code this} are combined such that the commits of {@code this} are
	 * applied onto the commits of {@code predecessor}.
	 *
	 * @param predecessor
	 * 		The predecessor range to merge.
	 * @return
	 * 		A new revision range representing the state transition from
	 * 		{@code predecessor.getPredecessorRevision} to
	 * 		{@code this.getRevision()}.
	 * @throws NullPointerException
	 * 		If {@code predecessor} is {@code null}.
	 */
	default RevisionRange merge(final RevisionRange predecessor)
			throws NullPointerException {
		Validate.notNull(predecessor);
		final VCSEngine engine = getVCSEngine();
		final List<Commit> commits = predecessor.getCommits();
		commits.addAll(getCommits());
		return engine.getModelFactory().createRevisionRange(getOrdinal(),
				getRevision(),
				predecessor.getPredecessorRevision().orElse(null), commits,
				getVCSEngine());
	}
}
