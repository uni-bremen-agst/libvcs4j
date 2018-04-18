package de.unibremen.informatik.st.libvcs4j.engine;

import bmsi.util.Diff;
import de.unibremen.informatik.st.libvcs4j.Commit;
import de.unibremen.informatik.st.libvcs4j.data.FileChangeImpl;
import de.unibremen.informatik.st.libvcs4j.data.VCSFileImpl;
import de.unibremen.informatik.st.libvcs4j.FileChange;
import de.unibremen.informatik.st.libvcs4j.ITEngine;
import de.unibremen.informatik.st.libvcs4j.LineChange;
import de.unibremen.informatik.st.libvcs4j.Revision;
import de.unibremen.informatik.st.libvcs4j.VCSEngine;
import de.unibremen.informatik.st.libvcs4j.VCSFile;
import de.unibremen.informatik.st.libvcs4j.Version;
import de.unibremen.informatik.st.libvcs4j.data.CommitImpl;
import de.unibremen.informatik.st.libvcs4j.data.LineChangeImpl;
import de.unibremen.informatik.st.libvcs4j.data.RevisionImpl;
import de.unibremen.informatik.st.libvcs4j.data.VersionImpl;
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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * An abstract base implementation of a {@link VCSEngine}. This class assumes
 * that an engine has a fixed number of revisions to process. Consequently, an
 * additional method is added to the public API, namely
 * {@link #listRevisions()} which returns the revisions to process.
 */
public abstract class AbstractVSCEngine implements VCSEngine {

	private static final Logger log = LoggerFactory.getLogger(VCSEngine.class);

	private final String repository;
	private final String root;
	private final Path target;

	private ITEngine itEngine = null;

	private int ordinal = 1;
	private boolean initialized = false;
	private List<String> revisions = null;
	private int revisionIdx = -1;
	private String revision = null;
	private Revision currentRevision = null;

	public AbstractVSCEngine(
	        final String pRepository, final String pRoot, final Path pTarget)
            throws NullPointerException {
		repository = Validate.notNull(pRepository);
		root = Validate.notNull(pRoot);
		target = Validate.notNull(pTarget).toAbsolutePath();
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
	public final Optional<Version> next() throws IOException {
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
			//validate(changes);
		}
		final Version version = createVersion(changes);
		currentRevision = version.getRevision();
		return Optional.of(version);
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
	public final Iterator<Version> iterator() {
		return new Iterator<Version>() {
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
			public Version next() {
				try {
					final Optional<Version> version =
							AbstractVSCEngine.this.next();
					if (!version.isPresent()) {
						throw new NoSuchElementException();
					}
					return version.get();
				} catch (final IOException e) {
					throw new UncheckedIOException(
							"Error while reading next version", e);
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

		final VCSFileImpl file = new VCSFileImpl(this);
		file.setPath(pPath.toString());
		file.setRelativePath(relPath.toString());
		file.setRevision(pRevision);
		return file;
	}

	private Revision createRevision() throws NullPointerException,
			IllegalArgumentException, IOException {
		final RevisionImpl rev = new RevisionImpl(this);
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
			revisions = listRevisionsImpl();
			IllegalReturnException.noNullElements(revisions);
			initialized = true;
		}
	}

//	private void validate(final Changes pChanges) throws IOException {
//		Validate.validState(revisionIdx > 0);
//
//		if (revisionIdx == 0) {
//			Validate.validState(pChanges.getRemoved().isEmpty(),
//					"Detected removals in first version");
//			Validate.validState(pChanges.getModified().isEmpty(),
//					"Detected modifications in first version");
//			Validate.validState(pChanges.getRelocated().isEmpty(),
//					"Detected relocations in first version");
//		}
//
//		Iterator<String> it;
//		it = pChanges.getAdded().iterator();
//		while (it.hasNext()) {
//			final Path add = Paths.get(it.next());
//			IllegalReturnException.isTrue(add.isAbsolute(),
//				"'%s' is not an absolute path", add);
//			IllegalReturnException.isTrue(Files.exists(add),
//				"'%s' has been recorded as added but does not exist", add);
//		}
//		it = pChanges.getRemoved().iterator();
//		while (it.hasNext()) {
//			final Path remove = Paths.get(it.next());
//			IllegalReturnException.isTrue(remove.isAbsolute(),
//				"'%s' is not an absolute path", remove);
//			IllegalReturnException.isTrue(Files.notExists(remove),
//				"'%s' has been recorded as removed but exists", remove);
//		}
//		it = pChanges.getModified().iterator();
//		while (it.hasNext()) {
//			final Path modify = Paths.get(it.next());
//			IllegalReturnException.isTrue(modify.isAbsolute(),
//					"'%s' is not an absolute path", modify);
//			IllegalReturnException.isTrue(Files.exists(modify),
//					"'%s' has been recorded as modified but does not exist",
//					modify);
//		}
//		Iterator<Entry<String, String>> itr =
//				pChanges.getRelocated().iterator();
//		while (itr.hasNext()) {
//			final Entry<String, String> relocate = itr.next();
//			final Path from = Paths.get(relocate.getKey());
//			final Path to = Paths.get(relocate.getValue());
//			IllegalReturnException.isTrue(from.isAbsolute(),
//					"'%s' is not an absolute path", from);
//			IllegalReturnException.isTrue(to.isAbsolute(),
//					"'%s' is not an absolute path", from);
//			IllegalReturnException.isTrue(Files.notExists(from),
//					"'%s' has been recorded as relocation to '%s' but exists",
//					from, to);
//			IllegalReturnException.isTrue(Files.exists(to),
//					"'%s' has been recorded as relocation from '%s' but does not exist",
//					to, from);
//		}
//	}

	private Version createVersion(final Changes pChanges) throws IOException {
		final Revision rev = createRevision();
		final VersionImpl version = new VersionImpl();
		version.setOrdinal(ordinal++);
		version.setRevision(rev);
		version.setPredecessorRevision(currentRevision);

		final Map<Path, VCSFile> path2File = new HashMap<>();
		rev.getFiles().forEach(f -> path2File.put(f.toPath(), f));
		final List<FileChange> fileChanges = new ArrayList<>();
		pChanges.getAdded().stream()
				.map(Paths::get)
				.map(a -> {
					final FileChangeImpl fc = new FileChangeImpl();
					fc.setEngine(this);
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
						fc.setEngine(this);
						fc.setOldFile(path2File.computeIfAbsent(
								r, p -> createFile(p, currentRevision)));
						return fc;
					})
					.forEach(fileChanges::add);
			pChanges.getModified().stream()
					.map(Paths::get)
					.map(m -> {
						final FileChangeImpl fc = new FileChangeImpl();
						fc.setEngine(this);
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
						fc.setEngine(this);
						fc.setOldFile(path2File.computeIfAbsent(
								old, p -> createFile(p, currentRevision)));
						fc.setNewFile(path2File.computeIfAbsent(
								nev, p -> createFile(p, rev)));
						return fc;
					})
					.forEach(fileChanges::add);
		}

		final Commit commit = createCommit(fileChanges);
		version.setCommits(Collections.singletonList(commit));
		return version;
	}

	private Commit createCommit(final List<FileChange> pFileChanges)
			throws IOException {
		final CommitImpl commit = createCommitImpl(revision);
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

	/////////////////////////// optional overrides ////////////////////////////

	protected void initImpl() throws IOException {}

	/////////////////////////// required overrides ////////////////////////////

	protected abstract void checkoutImpl(final String revision)
			throws IOException;

	protected abstract Changes createChangesImpl(
	        final String fromRev, final String toRev)
			throws IOException;

	protected abstract byte[] readAllBytesImpl(
			final String path, final String revision) throws IOException;

	protected abstract CommitImpl createCommitImpl(final String revision)
            throws IOException;

	protected abstract List<String> listRevisionsImpl() throws IOException;
}
