package de.unibremen.informatik.st.libvcs4j.engine;

import bmsi.util.Diff;
import de.unibremen.informatik.st.libvcs4j.Commit;
import de.unibremen.informatik.st.libvcs4j.FileChange;
import de.unibremen.informatik.st.libvcs4j.ITEngine;
import de.unibremen.informatik.st.libvcs4j.LineChange;
import de.unibremen.informatik.st.libvcs4j.Revision;
import de.unibremen.informatik.st.libvcs4j.RevisionRange;
import de.unibremen.informatik.st.libvcs4j.VCSEngine;
import de.unibremen.informatik.st.libvcs4j.VCSFile;
import de.unibremen.informatik.st.libvcs4j.data.FileChangeImpl;
import de.unibremen.informatik.st.libvcs4j.data.VCSFileImpl;
import de.unibremen.informatik.st.libvcs4j.data.CommitImpl;
import de.unibremen.informatik.st.libvcs4j.data.LineChangeImpl;
import de.unibremen.informatik.st.libvcs4j.data.RevisionImpl;
import de.unibremen.informatik.st.libvcs4j.data.RevisionRangeImpl;
import de.unibremen.informatik.st.libvcs4j.exception.IllegalReturnException;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * An abstract base implementation of a {@link VCSEngine}. This class assumes
 * that an engine has a fixed number of revisions to process. Consequently, an
 * additional method is added to the public API, namely
 * {@link #listRevisions()} which returns the revisions to process.
 */
public abstract class AbstractVSCEngine implements VCSEngine {

	private static final Logger log = LoggerFactory.getLogger(VCSEngine.class);

	/* VCS related configurations. */
	private final String repository;
	private final String root;
	private final Path target;

	/* External engines. */
	private ITEngine itEngine = null;

	/* Internal state of this engine. */
	private int ordinal = 1;
	private boolean initialized = false;
	private List<String> revisions = null;
	private int revisionIdx = -1;
	private String revision = null;
	private Revision currentRevision = null;

	public AbstractVSCEngine(
	        final String pRepository, final String pRoot, final Path pTarget)
            throws NullPointerException {
		repository = Validate.notNull(validateMapRepository(pRepository));
		root = Validate.notNull(validateMapRoot(pRoot));
		target = Validate.notNull(validateMapTarget(pTarget)).toAbsolutePath();
	}

	public AbstractVSCEngine(
			final String pRepository, final String pRoot, final Path pTarget,
			final List<String> pRevisions) throws NullPointerException,
			IllegalArgumentException {
		this(pRepository, pRoot, pTarget);
		revisions = validateMapRevisions(pRevisions).stream()
				.map(Validate::notNull)
				.collect(Collectors.toList());
	}

	@Override
	public final String getRepository() {
		return repository;
	}

	@Override
	public final String getRoot() {
		return root;
	}

	@Override
	public final Path getTarget() {
		return target;
	}

	@Override
	public final Optional<Revision> getRevision() {
		return Optional.ofNullable(currentRevision);
	}

	@Override
	public final Optional<RevisionRange> next() throws IOException {
		init();

		revisionIdx++;
		Validate.validState(revisionIdx >= 0, // just to be sure
				"Attribute `revisionIdx` must not be negative");
		// there are not more revisions available
		if (revisionIdx >= revisions.size()) {
			revisionIdx = revisions.size(); // prevent overflows
			return Optional.empty();
		}

		log.info("Checking out {} ({}/{})",
				revisions.get(revisionIdx),
				revisionIdx+1,
				revisions.size());
		checkoutImpl(revisions.get(revisionIdx));
		revision = revisions.get(revisionIdx);

		final Changes changes;
		// the first revision can only have additions
		if (revisionIdx == 0) {
			changes = new Changes();
			listFilesInOutput().stream()
					.map(Path::toString)
					.forEach(f -> changes.getAdded().add(f));
		} else {
			changes = createChangesImpl(getPreviousRevision(), revision);
			mapChanges(changes);
			//validate(changes);
		}
		final RevisionRange range = createRevisionRange(changes);
		currentRevision = range.getRevision();
		return Optional.of(range);
	}

	@Override
	public final byte[] readAllBytes(final VCSFile pFile) throws
            NullPointerException, IllegalArgumentException, IOException {
		Validate.notNull(pFile);
		final String rev = pFile.getRevision().getId();
		init();
		Validate.isTrue(revisions.contains(rev));
		if (revision != null && revision.equals(rev)) {
			Validate.isTrue(Files.isRegularFile(pFile.toPath()),
					"'%s' is not a regular file", pFile.toPath());
			return Files.readAllBytes(pFile.toPath());
		} else {
			final byte[] bytes = readAllBytesImpl(
					pFile.getRelativePath(), rev);
			IllegalReturnException.notNull(bytes);
			return bytes;
		}
	}

	@Override
	public final Iterator<RevisionRange> iterator() {
		return new Iterator<RevisionRange>() {
			@Override
			public boolean hasNext() {
				try {
					init();
					return (revisionIdx + 1) < revisions.size();
				} catch (final IOException e) {
					throw new UncheckedIOException(
							"Unable to init engine", e);
				}
			}

			@Override
			public RevisionRange next() {
				try {
					final Optional<RevisionRange> range =
							AbstractVSCEngine.this.next();
					if (!range.isPresent()) {
						throw new NoSuchElementException();
					}
					return range.get();
				} catch (final IOException e) {
					throw new UncheckedIOException(
							"Error while reading next revision", e);
				}
			}
		};
	}

	@Override
	public List<LineChange> computeDiff(final FileChange pFileChange)
			throws NullPointerException, IOException {
		Validate.notNull(pFileChange);
		final String LINE_SEPARATOR = "\\r?\\n";

		final String[] old = pFileChange.getOldFile().isPresent()
				? pFileChange.getOldFile().get()
					.readeContent().split(LINE_SEPARATOR)
				: new String[0];
		final String[] nev = pFileChange.getNewFile().isPresent()
				? pFileChange.getNewFile().get()
					.readeContent().split(LINE_SEPARATOR)
				: new String[0];

		final Diff diff = new Diff(old, nev);
		Diff.change change = diff.diff_2(false);
		final List<LineChange> lineChanges = new ArrayList<>();
		while (change != null) {
			final Diff.change c = change;
			for (int i = 0; i < change.deleted; i++) {
				final LineChangeImpl lineChange = new LineChangeImpl();
				lineChange.setType(LineChange.Type.DELETE);
				lineChange.setLine(c.line0 + i + 1);
				lineChange.setContent(old[lineChange.getLine() - 1]);
				lineChange.setFile(pFileChange.getOldFile()
						.orElseThrow(IllegalStateException::new));
				lineChanges.add(lineChange);
			}
			for (int i = 0; i < change.inserted; i++) {
				final LineChangeImpl lineChange = new LineChangeImpl();
				lineChange.setType(LineChange.Type.INSERT);
				lineChange.setLine(c.line1 + i + 1);
				lineChange.setContent(nev[lineChange.getLine() - 1]);
				lineChange.setFile(pFileChange.getNewFile()
						.orElseThrow(IllegalStateException::new));
				lineChanges.add(lineChange);
			}
			change = change.link;
		}

		return lineChanges;
	}

	@Override
	public void setITEngine(final ITEngine pITEngine) {
		itEngine = pITEngine;
	}

	@Override
	public Optional<ITEngine> getITEngine() {
		return Optional.ofNullable(itEngine);
	}

	/**
	 * Returns the revisions to process. If necessary, the first call of this
	 * method initializes the repository---for instance, cloning the repository
	 * to {@link #getTarget()}.
	 *
	 * @return
	 * 		The revisions to process.
	 * @throws IOException
	 * 		If an error occurred while initializing the repository.
	 */
	public List<String> listRevisions() throws IOException {
		init();
		return new ArrayList<>(revisions);
	}

	///////////////////////////// helping methods /////////////////////////////

	private void mapChanges(final Changes pChanges) throws IOException {
		// canonical path -> path
		final Map<String, String> added = new HashMap<>();
		for (final String a : pChanges.getAdded()) {
			added.put(Paths.get(a).toFile().getCanonicalPath(), a);
		}
		// canonical path -> path
		final Map<String, String> removed = new HashMap<>();
		for (final String r : pChanges.getRemoved()) {
			removed.put(Paths.get(r).toFile().getCanonicalPath(), r);
		}
		// add (path) -> remove (path)
		final Map<String, String> addRemoveMatches = new HashMap<>();
		added.forEach((ak, av) -> {
			final String rv = removed.get(ak);
			if (rv != null) {
				addRemoveMatches.put(av, rv);
			}
		});
		addRemoveMatches.forEach((a, r) -> {
			pChanges.getAdded().remove(a);
			pChanges.getRemoved().remove(r);
			pChanges.getModified().add(a);
		});
	}

	private VCSFile createFile(final Path pPath, final Revision pRevision) {
		final Path output = getOutput();
		if (!pPath.isAbsolute()) {
			throw new IllegalArgumentException(String.format(
					"'%s' is not an absolute path", pPath));
		} else if (!pPath.startsWith(output)) {
			throw new IllegalArgumentException(String.format(
					"'%s' is not a file located in '%s'", pPath, output));
		}
		final Path relPath = output.relativize(pPath);

		final VCSFileImpl file = new VCSFileImpl();
		file.setVCSEngine(this);
		file.setRelativePath(relPath.toString());
		file.setRevision(pRevision);
		return file;
	}

	private Revision createRevision() throws IOException {
		final RevisionImpl rev = new RevisionImpl();
		rev.setVCSEngine(this);
		final List<VCSFile> files = listFilesInOutput().stream()
				.map(f -> createFile(f, rev))
				.collect(Collectors.toList());
		rev.setId(revision);
		rev.setFiles(files);
		return rev;
	}

	private void init() throws IOException {
		if (!initialized) {
			initImpl();
			if (revisions == null) {
				revisions = listRevisionsImpl();
				IllegalReturnException.noNullElements(revisions);
			}
			initialized = true;
		}
	}

	private RevisionRange createRevisionRange(final Changes pChanges)
			throws IOException {
		final Revision rev = createRevision();
		final RevisionRangeImpl range = new RevisionRangeImpl();
		range.setVCSEngine(this);
		range.setOrdinal(ordinal++);
		range.setRevision(rev);
		range.setPredecessorRevision(currentRevision);

		final Map<Path, VCSFile> path2File = new HashMap<>();
		rev.getFiles().forEach(f -> path2File.put(f.toPath(), f));
		final List<FileChange> fileChanges = new ArrayList<>();
		pChanges.getAdded().stream()
				.map(Paths::get)
				.map(a -> {
					final FileChangeImpl fc = new FileChangeImpl();
					fc.setVCSEngine(this);
					fc.setNewFile(path2File.computeIfAbsent(
							a, p -> createFile(p, rev)));
					return fc;
				})
				.forEach(fileChanges::add);
		if (revisionIdx > 0) {
			Validate.validState(currentRevision != null);
			pChanges.getRemoved().stream()
					.map(Paths::get)
					.map(r -> {
						final FileChangeImpl fc = new FileChangeImpl();
						fc.setVCSEngine(this);
						fc.setOldFile(path2File.computeIfAbsent(
								r, p -> createFile(p, currentRevision)));
						return fc;
					})
					.forEach(fileChanges::add);
			pChanges.getModified().stream()
					.map(Paths::get)
					.map(m -> {
						final FileChangeImpl fc = new FileChangeImpl();
						fc.setVCSEngine(this);
						fc.setOldFile(createFile(m, currentRevision));
						fc.setNewFile(path2File.computeIfAbsent(
								m, p -> createFile(p, rev)));
						return fc;
					})
					.forEach(fileChanges::add);
			pChanges.getRelocated().stream()
					.map(e -> new AbstractMap.SimpleEntry<>(
							Paths.get(e.getKey()),
							Paths.get(e.getValue())))
					.map(e -> {
						final Path old = e.getKey();
						final Path nev = e.getValue();
						final FileChangeImpl fc = new FileChangeImpl();
						fc.setVCSEngine(this);
						fc.setOldFile(path2File.computeIfAbsent(
								old, p -> createFile(p, currentRevision)));
						fc.setNewFile(path2File.computeIfAbsent(
								nev, p -> createFile(p, rev)));
						return fc;
					})
					.forEach(fileChanges::add);
		}

		final Commit commit = createCommit(fileChanges);
		range.setCommits(Collections.singletonList(commit));
		return range;
	}

	private Commit createCommit(final List<FileChange> pFileChanges)
			throws IOException {
		final CommitImpl commit = createCommitImpl(revision);
		commit.setVCSEngine(this);
		IllegalReturnException.notNull(commit);
		IllegalReturnException.notNull(commit.getAuthor());
		IllegalReturnException.notNull(commit.getMessage());
		IllegalReturnException.notNull(commit.getDateTime());
		IllegalReturnException.noNullElements(commit.getParentIds());
		commit.setId(revision);
		commit.setFileChanges(pFileChanges);
		if (itEngine != null) {
			commit.setIssues(itEngine.getIssuesFor(commit));
		}
		return commit;
	}

	private String getPreviousRevision() {
		Validate.validState(revisionIdx >= 1,
				"There is no previous revision available");
		return revisions.get(revisionIdx - 1);
	}

	protected static Date toDate(final LocalDateTime pDateTime) {
		Validate.notNull(pDateTime);
		return Date.from(pDateTime.atZone(
				ZoneId.systemDefault()).toInstant());
	}

	protected static String normalizePath(final String pPath) {
		Validate.notNull(pPath);
		String path = pPath.replace("\\", "/");
		if (path.endsWith("/")) {
			path = path.substring(0, path.length() - 1);
		}
		return path;
	}

	protected boolean isInteger(final String pValue) {
		try {
			Integer.parseInt(pValue);
			return true;
		} catch (final NumberFormatException e) {
			return false;
		}
	}

	/////////////////////////// optional overrides ////////////////////////////

	protected void initImpl() throws IOException {}

	/////////////////////////// required overrides ////////////////////////////

	/**
	 * Validates and, if necessary, maps the given repository.
	 *
	 * @param repository
	 * 		The repository to validate and, if necessary, map.
	 * @return
	 * 		The mapped repository.
	 */
	protected abstract String validateMapRepository(String repository);

	/**
	 * Validates and, if necessary, maps the given root.
	 *
	 * @param root
	 * 		The root to validate and, if necessary, map.
	 * @return
	 * 		The mapped root.
	 */
	protected abstract String validateMapRoot(String root);

	/**
	 * Validates and, if necessary, maps the given target.
	 *
	 * @param target
	 * 		The target to validate and, if necessary, map.
	 * @return
	 * 		The mapped target.
	 */
	protected abstract Path validateMapTarget(Path target);

	/**
	 * Validates and, if necessary, maps the given revisions.
	 *
	 * @param revisions
	 * 		The revisions to validate and, if necessary, map.
	 * @return
	 * 		the mapped revisions.
	 */
	protected abstract List<String> validateMapRevisions(
			List<String> revisions);

	/**
	 * Checks out the given revision.
	 *
	 * @param revision
	 * 		The revision to checkout.
	 * @throws IOException
	 * 		If an error occurred while checking out the given revision.
	 */
	protected abstract void checkoutImpl(String revision) throws IOException;

	/**
	 * Creates the change between {@code fromRev} and {@code toRev}.
	 *
	 * @param fromRev
	 * 		The from revision.
	 * @param toRev
	 * 		The to revision.
	 * @return
	 * 		The changes between {@code fromRev} and {@code toRev}.
	 * @throws IOException
	 * 		If an error occurred while parsing the changes.
	 */
	protected abstract Changes createChangesImpl(String fromRev, String toRev)
			throws IOException;

	/**
	 * Reads the contents of the file located at {@code path} in revision
	 * {@code revision}.
	 *
	 * @param path
	 * 		The (relative) path of the file to read.
	 * @param revision
	 * 		The file's revision.
	 * @return
	 * 		The contents of the file located at {@code path} in revision
	 * 		{@code revision}.
	 * @throws IOException
	 * 		If an error occurred while reading the contents.
	 */
	protected abstract byte[] readAllBytesImpl(String path, String revision)
			throws IOException;

	/**
	 * Creates a commit storing the engine specific values. This method is used
	 * by {@link #createCommit(List)}.
	 *
	 * @param revision
	 * 		The corresponding revision value.
	 * @return
	 * 		A {@link CommitImpl} storing the engine specific values.
	 * @throws IOException
	 * 		If an error occurred while parsing a commit.
	 */
	protected abstract CommitImpl createCommitImpl(String revision)
			throws IOException;

	/**
	 * Returns the list of revisions to process.
	 *
	 * @return
	 * 		The revisions to process.
	 * @throws IOException
	 * 		If an error occurred while retrieving the list of revisions to
	 * 		process.
	 */
	protected abstract List<String> listRevisionsImpl() throws IOException;
}
