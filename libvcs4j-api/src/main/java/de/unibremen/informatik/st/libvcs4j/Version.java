package de.unibremen.informatik.st.libvcs4j;

import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static de.unibremen.informatik.st.libvcs4j.FileChange.Type.*;

/**
 * This class represents a single version extracted by a call of
 * {@link VCSEngine#next()}. We use the term "Version" instead of "Revision" to
 * differ between the state of a repository at a certain point in time, and
 * this class, that allows to retrieve the changes between two revisions. That
 * being said, there is a class {@link Revision} that is supposed to represent
 * the sate of a VCS and is accessible with, for instance,
 * {@link #getRevision()} and {@link #getPredecessorRevision()}.
 *
 * Note: A version must not necessarily track the changes between consecutive
 * revisions but track the changes between arbitrary revisions of a single VCS.
 * Thus, a version may subsume several commits.
 */
@SuppressWarnings("unused")
public interface Version {

	/**
	 * Returns the ordinal of this version. Ordinals are used to identify
	 * individual versions with a serial number when processing a VCS. The
	 * origin is 1.
	 *
	 * @return
	 * 		The ordinal of this version ({@code >= 1}).
	 */
	int getOrdinal();

	/**
	 * Returns the "to" {@link Revision} the file changes of this version
	 * belong to. The {@link Revision} returned by this method is considered as
	 * "the revision of this version".
	 *
	 * @return
	 * 		The "to" {@link Revision} the file changes of this version belong
	 * 		to.
	 */
	Revision getRevision();

	/**
	 * Returns the "from" {@link Revision} the file changes of this version
	 * belong to.
	 *
	 * @return
	 * 		The "from" {@link Revision} the file changes of this version belong
	 * 		to or an empty {@link Optional} if this is the first version.
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
						@SuppressWarnings("ConstantConditions")
						final Path path = type == ADD
								? change.getNewFile().get().toPath()
								: change.getOldFile().get().toPath();
						toProcess.stream().filter(c ->
								(type == ADD
										? c.getOldFile()
										: c.getNewFile())
								.map(VCSFile::toPath)
								.map(p -> p.equals(path))
								.orElse(false))
						.findAny().ifPresent(match -> {
							final FileChange.Type oType = match.getType();
							if (type == ADD && oType == ADD) {
								throw new IllegalStateException(String.format(
										"'%s' has been added after being added",
										path));
							} else if (type == REMOVE && oType == REMOVE) {
								throw new IllegalStateException(String.format(
										"'%s' has been removed after being removed",
										path));
							}
							toProcess.remove(match);
							if (oType == REMOVE) {
								iter.remove();
							} else {
								matches.add(match);
							}
						});
					} else {
						@SuppressWarnings("ConstantConditions")
						final Path path = change.getNewFile().get().toPath();
						toProcess.stream().filter(c ->
								(c.getType() != ADD
										? c.getOldFile()
										: c.getNewFile()) // < indicates a bug
								.map(VCSFile::toPath)
								.map(p -> p.equals(path))
								.orElse(false)
						).findAny().ifPresent(match -> {
							final FileChange.Type oType = match.getType();
							if (type == MODIFY && oType == ADD) {
								throw new IllegalStateException(String.format(
										"'%s' has been added after being modified",
										path));
							} else if (type == RELOCATE && oType == ADD) {
								throw new IllegalStateException(String.format(
										"'%s' has been added after being relocated to this path",
										path));
							}
							toProcess.remove(match);
							matches.add(match);
						});
					}
					if (matches.size() > 1) {
						throw new IllegalStateException(String.format(
								"Unexpected number of matches (%d)",
								matches.size()));
					} else if (!matches.isEmpty()) {
						final FileChange match = matches.poll();
						iter.set(new FileChange() {
							@Override
							public VCSEngine getEngine() {
								return change.getEngine();
							}

							@Override
							public Optional<VCSFile> getOldFile() {
								return change.getOldFile();
							}

							@Override
							public Optional<VCSFile> getNewFile() {
								return match.getNewFile();
							}
						});
					}
				}
				accum.addAll(toProcess);
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
	 * Returns all files that have been added, modified, or relocated. This
	 * method is in particular useful when processing file changes providing a
	 * new file ({@link FileChange#getNewFile()}).
	 *
	 * @return
	 * 		All files that have been added, modified, or relocated.
	 */
	default List<FileChange> getOutdatedFiles() {
		return getFileChanges().stream()
				.filter(fc -> fc.getType() != REMOVE)
				.collect(Collectors.toList());
	}

	/**
	 * Filters the list of file changes returned by {@link #getFileChanges()}
	 * and returns only those whose old or the new relative file path ends with
	 * {@code suffix}.
	 *
	 * You may use this method to analyze file changes affecting a certain file
	 * type only. For instance, call {@code getFileChangesBySuffix(".java")} to
	 * get only the file changes affecting Java files.
	 *
	 * @param suffix
	 * 		The suffix used to filter the file changes.
	 * @return
	 * 		All file changes whose old or the new relative file path ends with
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
	 * and returns only those whose old or new relative path starts with
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
	 * 		All file changes whose old or new relative file path ends with
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
	 * Returns whether this version is the first one. That is, there is no
	 * predecessor {@link Revision} and, consequently, all changes returned by
	 * {@link #getFileChanges()} are additions. The default implementation
	 * simply checks whether {@link #getPredecessorRevision()} returns an empty
	 * {@link Optional}.
	 *
	 * @return
	 * 		{@code true} if this version is the first one, {@code false}
	 * 		otherwise.
	 */
	default boolean isFirst() {
		return !getPredecessorRevision().isPresent();
	}

	/**
	 * Runs the given action if this version is the first one.
	 *
	 * @param action
	 * 		The action to run if this is the first version.
	 */
	default void ifFirst(final Consumer<Version> action) {
		if (isFirst()) {
			action.accept(this);
		}
	}

	/**
	 * Runs the given action if this version is not the first one.
	 *
	 * @param action
	 * 		The action to run if this is not the first version.
	 */
	default void ifNotFirst(final Consumer<Version> action) {
		if (!isFirst()) {
			action.accept(this);
		}
	}
}
