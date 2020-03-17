package de.unibremen.informatik.st.libvcs4j;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * A {@link VCSEngine} is supposed to extract a linear sequence of
 * {@link RevisionRange} instances for arbitrary version control systems (VCS)
 * as well as single input files and directories. In order to calculate the
 * file changes of a particular {@link RevisionRange}, real VCS (or similar)
 * operations are performed. Consequently, all files of the currently processed
 * revision are physically available at {@link #getTarget()} and
 * {@link #getOutput()}.
 *
 * Due to the complexity of some VCS, the generated sequence of revision ranges
 * is a "one-way-iterator" that allows forward directed processing only.
 *
 * By extending the {@link Iterable} interface, a {@link VCSEngine} allows to
 * retrieve revision ranges using foreach loops. There are several things to
 * consider, though. First of all, {@link java.util.Iterator} instances
 * returned by {@link #iterator()} depend on the state of the corresponding
 * engine. Consequently, calling {@link #next()} on an engine {@code e},
 * modifies all available iterators of {@code e}, too. Similarly, calling
 * {@link java.util.Iterator#next()} on an iterator {@code i}, modifies any
 * other iterator sharing {@code i's} engine. Due to the circumstance that
 * {@link java.util.Iterator#hasNext()} and {@link java.util.Iterator#next()}
 * perform real VCS (or similar) operations an {@link IOException} may be
 * thrown by these methods which, in order to fulfill the {@link Iterable}
 * interface, is encapsulated in an {@link java.io.UncheckedIOException}. Long
 * story short, you should not use several iterators at once.
 *
 * Note: Neither {@link #getRepository()} nor {@link #getRoot()} nor
 * {@link #getTarget()} nor {@link #getOutput()} may change at any time. This
 * is an important property because it allows users of this API to maintain
 * incrementally updated models reflecting the current state of a VCS.
 *
 * Note 2: {@link #readAllBytes(VCSFile)}, {@link #readLineInfo(VCSFile)}, and
 * {@link #computeDiff(FileChange)} are stateless operations. That is, one may
 * read any file in any state.
 */
public interface VCSEngine extends Iterable<RevisionRange> {

	/**
	 * Extracts the next revision range, if any. If necessary, the first call
	 * of this method initializes the repository---for instance, cloning the
	 * repository to {@link #getTarget()}.
	 *
	 * @return
	 * 		The next revision range, if any.
	 * @throws IOException
	 * 		If an error occurred while extracting the next revision range.
	 */
	Optional<RevisionRange> next() throws IOException;

	/**
	 * Reads the contents of the given file. This method does not depend on the
	 * current state of this engine. (see Note 2 above).
	 *
	 * @param file
	 *      The file to read the contents from.
	 * @return
	 * 		A byte array containing the bytes read from the file.
	 * @throws NullPointerException
	 * 		If {@code file} is {@code null}.
	 * @throws IllegalArgumentException
	 * 		If {@code file} is unknown to this engine.
	 * @throws IOException
	 * 		If an error occurred while reading the contents.
	 */
	byte[] readAllBytes(VCSFile file) throws NullPointerException,
			IllegalArgumentException, IOException;

	/**
	 * Reads the line information of the given file.
	 *
	 * @param file
	 * 		The file to read the line information from.
	 * @return
	 * 		The line information of {@code file}.
	 * @throws NullPointerException
	 * 		If {@code file} is {@code null}.
	 * @throws IllegalArgumentException
	 * 		If {@code file} is unknown to this engine.
	 * @throws IOException
	 * 		If an error occurred while reading the line information.
	 */
	List<LineInfo> readLineInfo(VCSFile file) throws NullPointerException,
			IllegalArgumentException, IOException;

	/**
	 * Tries to guess the charset of {@code file}.
	 *
	 * @return
	 * 		The guessed charset.
	 * @throws IOException
	 * 		If an error occurred while reading the contents of {@code file}.
	 */
	Optional<Charset> guessCharset(VCSFile file) throws IOException;

	/**
	 * Returns the currently checked out revision.
	 *
	 * @return
	 * 		The currently checked out revision or an empty {@link Optional} if
	 * 		{@link #next()} has not been called yet.
	 */
	Optional<Revision> getRevision();

	/**
	 * Returns the path to the processed Repository.
	 *
	 * Note: This path never changes.
	 *
	 * @return
	 * 		Path to the processed repository.
	 */
	String getRepository();

	/**
	 * Returns the path (relative to {@link #getRepository()}) to the tracked
	 * file or directory. For instance, if "/path/to/git/src" is the directory
	 * you want to track, "src" is the value returned by this method. For the
	 * sake of convenience, an empty String rather than {@code null} is
	 * returned if a provider tracks the root (top) directory.
	 *
	 * Note: This path never changes.
	 *
	 * @return
	 * 		Path to the tracked file or directory within
	 * 		{@link #getRepository()}.
	 */
	String getRoot();

	/**
	 * Returns the path to the output directory of the currently processed
	 * revision. For example, "/tmp/git-clone", "/tmp/svn-working-copy", and so
	 * on. Some VCS engines (Git for instance) do not support partial
	 * checkouts. Therefore, use {@link #getOutput()} to retrieve the path to
	 * the tracked files and directories within 'target' rather than the root
	 * (top) of the processed directory.
	 *
	 * Note: This path never changes.
	 *
	 * @return
	 * 		Path to the currently processed revision.
	 */
	Path getTarget();

	/**
	 * This method is somewhat similar to {@link #getTarget()}, but may return
	 * the composite of {@link #getTarget()} and {@link #getRoot()}  if the
	 * underlying VCS does not support partial checkouts (just like Git, for
	 * instance). If the VCS supports partial checkouts, {@link #getTarget()}
	 * is returned. Use this method if you want to access the tracked files
	 * and directories of the currently processed revision.
	 *
	 * Note: This path never changes.
	 *
	 * @return
	 * 		Path to tracked files and directories of the currently processed
	 * 		revision.
	 */
	Path getOutput();

	/**
	 * Computes the changed lines of the given file change.
	 *
	 * @param fileChange
	 * 		The file change to compute the line diff for.
	 * @return
	 * 		A sequence of {@link LineChange} objects.
	 * @throws NullPointerException
	 * 		If {@code fileChange} is {@code null}.
	 * @throws IOException
	 * 		If an error occurred while reading the content of the old or new
	 * 		file (see {@link VCSFile#readContent()}).
	 */
	List<LineChange> computeDiff(FileChange fileChange) throws
			NullPointerException, IOException;

	/**
	 * Sets the engine used to extract issues from an issue tracker. If
	 * {@code null} is passed, the currently set engine is removed.
	 *
	 * @param itEngine
	 * 		The engine used to extract issues from an issue tracker or
	 * 		{@code null} to unset the currently set engine.
	 */
	void setITEngine(ITEngine itEngine);

	/**
	 * Returns the engine used to extract issues from an issue tracker.
	 *
	 * @return
	 * 		The engine used to extract issues from an issue tracker.
	 */
	Optional<ITEngine> getITEngine();

	/**
	 * Returns the factory used to create vcs model instances.
	 *
	 * @return
	 * 		The factory used to create vcs model instances.
	 */
	VCSModelFactory getModelFactory();

	/**
	 * Sets the factory used to create vcs model instances.
	 *
	 * @param factory
	 * 		The factory used to create vcs model instances.
	 * @throws NullPointerException
	 * 		If {@code factory} is {@code null}.
	 */
	void setModelFactory(VCSModelFactory factory) throws NullPointerException;

	/**
	 * Returns a {@link FilenameFilter} that is supposed to exclude VCS
	 * specific files and directories. The default implementation creates a
	 * filter that does not exclude any file or directory.
	 *
	 * Note: A file or directory is excluded if and only if
	 * {@link FilenameFilter#accept(File, String)} returns {@code false}.
	 *
	 * @return
	 * 		A {@link FilenameFilter} to exclude VCS specific files and
	 * 		directories.
	 */
	default FilenameFilter createVCSFileFilter() {
		return (dir, name) -> true;
	}

	/**
	 * Returns all non-VCS-specific files located in {@link #getOutput()}. All
	 * paths of the returned list are absolute and the list does not contain
	 * any directory path. The default implementation uses the
	 * {@link FilenameFilter} returned by {@link #createVCSFileFilter()} to
	 * exclude particular files from {@link #getOutput()}. {@link #getOutput()}
	 * itself can not be excluded.
	 *
	 * @return
	 * 		All non-VCS-specific files located in {@link #getOutput()}.
	 * @throws FileNotFoundException
	 * 		If {@link #getOutput()} does not exist.
	 * @throws IOException
	 * 		If an error occurred while collecting files.
	 */
	default List<Path> listFilesInOutput() throws IOException {
		final File output = getOutput().toFile();
		if (!output.exists()) {
			throw new FileNotFoundException(
					String.format("'%s' does not exist", getOutput()));
		} else if (output.isFile()) {
			return Collections.singletonList(getOutput().toAbsolutePath());
		} else {
			final List<Path> filesInOutput = new ArrayList<>();
			// For the sake of stability, handle null filters.
			final Optional<FilenameFilter> filter =
					Optional.ofNullable(createVCSFileFilter());
			final Path rootDir = getOutput();
			Files.walkFileTree(rootDir, new FileVisitor<Path>() {
				@Override
				public FileVisitResult preVisitDirectory(
						final Path dir, final BasicFileAttributes attrs)
						throws IOException {
					if (dir.equals(rootDir)) {
						return FileVisitResult.CONTINUE;
					}
					return filter
							.map(f -> f.accept(dir.getParent().toFile(),
									dir.getFileName().toString()))
							.filter(b -> !b)
							.map(b -> FileVisitResult.SKIP_SUBTREE)
							.orElse(FileVisitResult.CONTINUE);
				}

				@Override
				public FileVisitResult visitFile(
						final Path file, final BasicFileAttributes attrs)
						throws IOException {
					filter
							.map(f -> f.accept(file.getParent().toFile(),
									file.getFileName().toString()))
							.filter(b -> b)
							.map(__ -> file.toAbsolutePath())
							.ifPresent(filesInOutput::add);
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult visitFileFailed(
						final Path file, final IOException exc)
						throws IOException {
					throw exc;
				}

				@Override
				public FileVisitResult postVisitDirectory(
						final Path dir, final IOException exc)
						throws IOException {
					if (exc != null) {
						throw exc;
					}
					return FileVisitResult.CONTINUE;
				}
			});
			return filesInOutput;
		}
	}
}
