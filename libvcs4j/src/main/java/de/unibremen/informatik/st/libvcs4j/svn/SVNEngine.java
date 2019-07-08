package de.unibremen.informatik.st.libvcs4j.svn;

import de.unibremen.informatik.st.libvcs4j.LineInfo;
import de.unibremen.informatik.st.libvcs4j.VCSEngine;
import de.unibremen.informatik.st.libvcs4j.VCSEngineBuilder;
import de.unibremen.informatik.st.libvcs4j.VCSFile;
import de.unibremen.informatik.st.libvcs4j.Validate;
import de.unibremen.informatik.st.libvcs4j.data.CommitImpl;
import de.unibremen.informatik.st.libvcs4j.engine.AbstractIntervalVCSEngine;
import de.unibremen.informatik.st.libvcs4j.engine.Changes;
import de.unibremen.informatik.st.libvcs4j.exception.IllegalIntervalException;
import de.unibremen.informatik.st.libvcs4j.exception.IllegalRepositoryException;
import de.unibremen.informatik.st.libvcs4j.exception.IllegalRevisionException;
import de.unibremen.informatik.st.libvcs4j.exception.IllegalTargetException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.ISVNAnnotateHandler;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNStatusType;
import org.tmatesoft.svn.core.wc2.SvnCat;
import org.tmatesoft.svn.core.wc2.SvnCheckout;
import org.tmatesoft.svn.core.wc2.SvnDiffSummarize;
import org.tmatesoft.svn.core.wc2.SvnLog;
import org.tmatesoft.svn.core.wc2.SvnOperationFactory;
import org.tmatesoft.svn.core.wc2.SvnRevisionRange;
import org.tmatesoft.svn.core.wc2.SvnTarget;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * An {@link VCSEngine} that is supposed to extract file changes from SVN
 * repositories.
 *
 * @author Marcel Steinbeck
 */
public class SVNEngine extends AbstractIntervalVCSEngine {

	private static final Logger log = LoggerFactory.getLogger(SVNEngine.class);

	private static final Predicate<String> SUPPORTED_PROTOCOLS = Pattern
			.compile("file://.*|http://.*|https://.*|svn://.*|svn+ssh://.*")
			.asPredicate();

	private static final Predicate<String> FILE_PROTOCOL = Pattern
			.compile("file://.*")
			.asPredicate();

	public static final LocalDateTime MINIMUM_DATETIME =
			LocalDateTime.of(1980, 1, 1, 0, 0, 0);

	/**
	 * Creates a new SVN engine that processes all commits of the given root
	 * directory. Use {@link VCSEngineBuilder} for convenience.
	 *
	 * @param pRepository
	 * 		The repository to process (see {@link VCSEngine#getRepository()}).
	 * @param pRoot
	 * 		The root directory (see {@link VCSEngine#getRoot()}.
	 * @param pTarget
	 * 		The target directory (see {@link VCSEngine#getTarget()}).
	 * @throws NullPointerException
	 * 		If any of the given arguments is {@code null}.
	 * @throws IllegalArgumentException
	 * 		If any of the given arguments is invalid.
	 */
	public SVNEngine(final String pRepository, final String pRoot,
			final Path pTarget) throws NullPointerException,
			IllegalArgumentException {
		super(pRepository, pRoot, pTarget);
	}

	/**
	 * Creates a new SVN engine that processes all commits of the given root
	 * directory within the given time range. Use {@link VCSEngineBuilder} for
	 * convenience.
	 *
	 * @param pRepository
	 * 		The repository to process (see {@link VCSEngine#getRepository()}).
	 * @param pRoot
	 * 		The root directory (see {@link VCSEngine#getRoot()}.
	 * @param pTarget
	 * 		The target directory (see {@link VCSEngine#getTarget()}).
	 * @param pSince
	 * 		The since date.
	 * @param pUntil
	 * 		The until date.
	 * @throws NullPointerException
	 * 		If any of the given arguments is {@code null}.
	 * @throws IllegalArgumentException
	 * 		If any of the given arguments is invalid.
	 */
	public SVNEngine(final String pRepository, final String pRoot,
			final Path pTarget, final LocalDateTime pSince,
			final LocalDateTime pUntil) throws NullPointerException,
			IllegalRepositoryException, IllegalTargetException,
			IllegalIntervalException {
		super(pRepository, pRoot, pTarget, pSince, pUntil);
	}

	/**
	 * Creates a new SVN engine that processes all commits of the given root
	 * directory within the given revision range (inclusive). Use
	 * {@link VCSEngineBuilder} for convenience.
	 *
	 * @param pRepository
	 * 		The repository to process (see {@link VCSEngine#getRepository()}).
	 * @param pRoot
	 * 		The root directory (see {@link VCSEngine#getRoot()}.
	 * @param pTarget
	 * 		The target directory (see {@link VCSEngine#getTarget()}).
	 * @param pFrom
	 * 		The start revision.
	 * @param pTo
	 * 		The end revision (inclusive).
	 * @throws NullPointerException
	 * 		If any of the given arguments is {@code null}.
	 * @throws IllegalArgumentException
	 * 		If any of the given arguments is invalid.
	 */
	public SVNEngine(final String pRepository, final String pRoot,
			final Path pTarget, final String pFrom, final String pTo)
			throws NullPointerException, IllegalRepositoryException,
			IllegalTargetException, IllegalIntervalException {
		super(pRepository, pRoot, pTarget, pFrom, pTo);
	}

	/**
	 * Creates a new SVN engine that processes all commits of the given root
	 * directory within the given revision index range (exclusive). Use
	 * {@link VCSEngineBuilder} for convenience.
	 *
	 * @param pRepository
	 * 		The repository to process (see {@link VCSEngine#getRepository()}).
	 * @param pRoot
	 * 		The root directory (see {@link VCSEngine#getRoot()}.
	 * @param pTarget
	 * 		The target directory (see {@link VCSEngine#getTarget()}).
	 * @param pStartIdx
	 * 		The index of the start revision ({@code >= 0}).
	 * @param pEndIdx
	 * 		The index of the end revision (exclusive).
	 * @throws NullPointerException
	 * 		If any of the given arguments is {@code null}.
	 * @throws IllegalArgumentException
	 * 		If any of the given arguments is invalid.
	 */
	public SVNEngine(final String pRepository, final String pRoot,
			final Path pTarget, final int pStartIdx, int pEndIdx)
			throws NullPointerException, IllegalIntervalException {
		super(pRepository, pRoot, pTarget, pStartIdx, pEndIdx);
	}

	///////////////////////// Validation and mapping //////////////////////////

	@Override
	protected String validateMapRepository(final String pRepository) {
		Validate.notEmpty(pRepository);
		IllegalRepositoryException.isTrue(
				SUPPORTED_PROTOCOLS.test(pRepository),
				"Unsupported protocol: '%s'", pRepository);
		if (FILE_PROTOCOL.test(pRepository)) {
			final File file = new File(pRepository.substring(7));
			IllegalRepositoryException.isTrue(file.isDirectory(),
					"'%s' is not a directory", pRepository);
			IllegalRepositoryException.isTrue(file.canRead(),
					"'%s' is not readable", pRepository);
		}
		return normalizePath(pRepository);
	}

	@Override
	protected String validateMapRoot(final String pRoot) {
		Validate.notNull(pRoot);
		return normalizePath(pRoot);
	}

	@Override
	protected Path validateMapTarget(final Path pTarget) {
		Validate.notNull(pTarget);
		IllegalTargetException.isTrue(!pTarget.toFile().exists(),
				"'%s' already exists", pTarget);
		IllegalTargetException.isTrue(Files.isWritable(pTarget.getParent()),
				"Parent of '%s' is not writable", pTarget);
		return pTarget.toAbsolutePath();
	}

	@Override
	protected List<String> validateMapRevisions(final List<String> pRevisions) {
		return Validate.noNullElements(pRevisions).stream()
				.map(r -> {
					IllegalRevisionException.isTrue(isInteger(r),
							"'%s' is not a valid svn revision", r);
					return r;
				})
				.map(Integer::parseInt)
				.map(r -> {
					IllegalRevisionException.isTrue(r >= 1, "%d < 1", r);
					return r;
				})
				.map(String::valueOf)
				.collect(Collectors.toList());
	}

	@Override
	protected LocalDateTime validateMapDateTime(final LocalDateTime pDateTime) {
		if (pDateTime.isBefore(MINIMUM_DATETIME)) {
			log.debug("Mapping datetime value '{}' to '{}'",
					pDateTime, MINIMUM_DATETIME);
			return MINIMUM_DATETIME;
		}
		return pDateTime;
	}

	@Override
	protected String validateMapIntervalRevision(final String pRevision) {
		return pRevision == null ? "" : validateMapRevisions(
				Collections.singletonList(pRevision)).get(0);
	}

	////////////////////////////////// Utils //////////////////////////////////

	private String getInput() {
		// `Paths.get` breaks protocol prefix
		final String repository = getRepository();
		final String root = getRoot();
		return root.isEmpty() ? repository : repository + "/" + root;
	}

	private String toSVNPath(final String pPath) {
		Validate.notNull(pPath);
		return normalizePath(getInput() + "/" + pPath);
	}

	private String toAbsolutePath(final String pPath) {
		return getOutput().resolve(pPath).toString();
	}

	private SVNRevision createSVNRevision(final String pRevision) {
		return SVNRevision.create(Long.parseLong(pRevision));
	}

	private SVNRevision createSVNRevision(final LocalDateTime pDateTime) {
		return SVNRevision.create(toDate(pDateTime));
	}

	private SVNURL createSVNURL(final String pURL) throws SVNException {
		return SVNURL.parseURIEncoded(pURL);
	}

	private File createTargetFile() {
		return getTarget().toFile();
	}

	private List<String> listRevisions(final SVNRevision from,
			final SVNRevision to) throws IOException {
		final SvnOperationFactory factory = new SvnOperationFactory();
		final List<String> revs = new ArrayList<>();
		try {
			final SVNURL inputUrl = createSVNURL(getInput());
			final SvnTarget input = SvnTarget.fromURL(inputUrl);
			final SvnLog svnLog = factory.createLog();
			svnLog.addRange(SvnRevisionRange.create(from, to));
			svnLog.setSingleTarget(input);
			svnLog.setReceiver((__, entry) -> {
				if (entry.getRevision() != 0) {
					revs.add(String.valueOf(entry.getRevision()));
				}
			});
			svnLog.run();
		} catch (final SVNException e) {
			// Avoid file not found exception which is thrown if there is not
			// a single revision for `root` available. Return an empty
			// collection instead.
			if (e.getErrorMessage()
					.getErrorCode()
					.getCode() == 160013) {
				return Collections.emptyList();
			}
			throw new IOException(e);
		} finally {
			factory.dispose();
		}
		return revs;
	}

	@AllArgsConstructor
	private class AnnotateHandler implements ISVNAnnotateHandler {

		@NonNull
		private final String revision;

		@NonNull
		private final VCSFile file;

		@Getter
		private final List<LineInfo> lineInfoList = new ArrayList<>();

		@Override
		public void handleLine(final Date pDate, long pRevision,
				final String pAuthor, final String pLine)
				throws SVNException {
			// Do nothing.
		}

		@Override
		public void handleLine(final Date pDate, final long pRevision,
				final String pAuthor, final String pLine,
				final Date pMergedDate, final long mergedRevision,
				final String pMergedAuthor, final String pMergedPath,
				final int pLineNumber) throws SVNException {
			try {
				final LineInfo li = getModelFactory().createLineInfo(
						revision, pAuthor,
						createCommitImpl(revision).getMessage(),
						LocalDateTime.ofInstant(
								pDate.toInstant(),
								ZoneId.systemDefault()),
						pLineNumber + 1, pLine, file, SVNEngine.this);
				lineInfoList.add(li);
			} catch (final IOException e) {
				throw new UncheckedIOException(e);
			}
		}

		@Override
		public boolean handleRevision(final Date pDate, final long pRevision,
				final String pAuthor, final File pContents)
				throws SVNException {
			return true;
		}

		@Override
		public void handleEOF() throws SVNException {
			// Do nothing.
		}
	}

	///////////////////////////////////////////////////////////////////////////

	@Override
	public Path getOutput() {
		return getTarget();
	}

	@Override
	protected void checkoutImpl(final String pRevision) throws IOException {
		final SvnOperationFactory factory = new SvnOperationFactory();

		try {
			final SVNRevision revision = createSVNRevision(pRevision);
			final SvnTarget input = SvnTarget.fromURL(
					createSVNURL(getInput()));
			final SvnTarget target = SvnTarget.fromFile(
					createTargetFile());

			final SvnCheckout checkout = factory.createCheckout();
			checkout.setRevision(revision);
			checkout.setSource(input);
			checkout.setSingleTarget(target);
			checkout.run();
		} catch (final SVNException e) {
			if (e.getErrorMessage()
					.getErrorCode()
					.getCode() != 155000) {
				throw new IOException(e);
			}
		} finally {
			factory.dispose();
		}
	}

	@Override
	protected Changes createChangesImpl(final String fromRev,
			final String toRev) throws IOException {
		final SvnOperationFactory factory = new SvnOperationFactory();

		final Changes changes = new Changes();
		try {
			final SVNRevision from = createSVNRevision(fromRev);
			final SVNRevision to = createSVNRevision(toRev);
			final SvnTarget input = SvnTarget.fromURL(
					createSVNURL(getInput()), from);

			final SvnDiffSummarize diff = factory.createDiffSummarize();
			diff.setSource(input, from, to);
			diff.setRecurseIntoDeletedDirectories(true);
			diff.setReceiver((__, entry) -> {
				final SVNNodeKind kind = entry.getKind();
				final SVNStatusType status = entry.getModificationType();
				if (kind.equals(SVNNodeKind.FILE)) {
					if (status.equals(SVNStatusType.STATUS_ADDED)) {
						changes.getAdded().add(toAbsolutePath(entry.getPath()));
					} else if (status.equals(SVNStatusType.STATUS_DELETED)) {
						changes.getRemoved().add(toAbsolutePath(entry.getPath()));
					} else if (status.equals(SVNStatusType.STATUS_MODIFIED) ||
							status.equals(SVNStatusType.STATUS_REPLACED)) {
						changes.getModified().add(toAbsolutePath(entry.getPath()));
					} else {
						log.warn("Unsupported change type");
					}
				}
			});
			diff.run();
		} catch (final SVNException e) {
			throw new IOException(e);
		} finally {
			factory.dispose();
		}
		return changes;
	}

	@Override
	protected Optional<String> getLatestRevision() throws IOException {
		final List<String> revs = listRevisions(
				SVNRevision.HEAD, SVNRevision.HEAD);
		return revs.isEmpty()
				? Optional.empty()
				: Optional.of(revs.get(0));
	}

	@Override
	protected List<String> listRevisionsImpl(final LocalDateTime pSince,
			final LocalDateTime pUntil) throws IOException {
		final SVNRevision since = createSVNRevision(pSince);
		final SVNRevision until = createSVNRevision(pUntil);
		return listRevisions(since, until);
	}

	@Override
	protected List<String> listRevisionsImpl(final String pFrom,
			final String pTo) throws IOException {
		final long head;
		try {
			head = SVNRepositoryFactory
					.create(createSVNURL(getInput()))
					.getLatestRevision();
		} catch (final SVNException e) {
			throw new IOException(e);
		}

		final long from = pFrom.isEmpty() ? 1 : Long.parseLong(pFrom);
		long to = pTo.isEmpty() ? head : Long.parseLong(pTo);
		to = Math.min(to, head);
		if (from > to) {
			return Collections.emptyList();
		}

		final SVNRevision fromRev = createSVNRevision(String.valueOf(from));
		final SVNRevision toRev = createSVNRevision(String.valueOf(to));
		return listRevisions(fromRev, toRev);
	}

	@Override
	protected byte[] readAllBytesImpl(final String pPath,
			final String pRevision) throws IOException {
		final SvnOperationFactory factory = new SvnOperationFactory();

		try(final ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
			final SVNRevision revision = createSVNRevision(pRevision);
			final SvnTarget path = SvnTarget.fromURL(
					createSVNURL(toSVNPath(pPath)), revision);

			final SvnCat cat = factory.createCat();
			cat.setRevision(revision);
			cat.setSingleTarget(path);
			cat.setOutput(bos);
			cat.run();
			return bos.toByteArray();
		} catch (final SVNException e) {
			throw new IOException(e);
		} finally {
			factory.dispose();
		}
	}

	@Override
	public List<LineInfo> readLineInfoImpl(final VCSFile pFile)
			throws NullPointerException, IllegalArgumentException,
			IOException {
		final SvnOperationFactory factory = new SvnOperationFactory();

		try {
			final String rev = pFile.getRevision().getId();
			final String relPath = pFile.getRelativePath();
			final SVNRevision revision = createSVNRevision(rev);
			final SvnTarget path = SvnTarget.fromURL(
					createSVNURL(toSVNPath(relPath)), revision);
			final AnnotateHandler handler = new AnnotateHandler(rev, pFile);

			SVNClientManager
					.newInstance()
					.getLogClient()
					.doAnnotate(path.getURL(), revision,
					SVNRevision.create(0), revision, handler);
			final List<String> lines = pFile.readLinesWithEOL();
			final List<LineInfo> lineInfoList = handler.lineInfoList;
			Validate.validateState(lines.size() == lineInfoList.size());
			return lineInfoList;
		} catch (final SVNException | UncheckedIOException e) {
			throw new IOException(e);
		} finally {
			factory.dispose();
		}
	}

	@Override
	protected CommitImpl createCommitImpl(final String pRevision)
			throws IOException {
		final SvnOperationFactory factory = new SvnOperationFactory();

		try {
			final SVNRevision revision = createSVNRevision(pRevision);
			final SvnTarget input = SvnTarget.fromURL(
					createSVNURL(getRepository()), revision);
			final CommitImpl commit = new CommitImpl();

			final SvnLog svnLog = factory.createLog();
			svnLog.addRange(SvnRevisionRange.create(revision, revision));
			svnLog.setSingleTarget(input);
			svnLog.setReceiver((__, entry) -> {
				final String author = entry.getAuthor() == null
						? "(no author)" : entry.getAuthor();
				commit.setAuthor(author);
				commit.setMessage(entry.getMessage());
				commit.setDateTime(LocalDateTime.ofInstant(
						entry.getDate().toInstant(),
						ZoneId.systemDefault()));
				if (entry.getRevision() > 1) {
					commit.setParentIds(Collections.singletonList(
							String.valueOf(entry.getRevision() - 1)));
				}
			});
			svnLog.run();

			return commit;
		} catch (final SVNException e) {
			throw new IOException(e);
		} finally {
			factory.dispose();
		}
	}

	@Override
	public FilenameFilter createVCSFileFilter() {
		return (dir, name) -> !name.endsWith(".svn");
	}
}
