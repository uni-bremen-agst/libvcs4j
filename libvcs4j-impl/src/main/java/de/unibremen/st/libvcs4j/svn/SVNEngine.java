package de.unibremen.st.libvcs4j.svn;

import de.unibremen.st.libvcs4j.VCSEngine;
import de.unibremen.st.libvcs4j.data.CommitImpl;
import de.unibremen.st.libvcs4j.exception.IllegalIntervalException;
import de.unibremen.st.libvcs4j.exception.IllegalRepositoryException;
import de.unibremen.st.libvcs4j.exception.IllegalTargetException;
import de.unibremen.st.libvcs4j.engine.AbstractIntervalVCSEngine;
import de.unibremen.st.libvcs4j.engine.Changes;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import static org.apache.commons.lang3.Validate.notNull;

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
	 * Use {@link de.unibremen.st.libvcs4j.VCSEngineBuilder} instead.
	 */
	@Deprecated
	@SuppressWarnings("DeprecatedIsStillUsed")
	public SVNEngine(
			final String pRepository,
			final String pRoot,
			final Path pTarget,
			final LocalDateTime pSince,
			final LocalDateTime pUntil)
				throws NullPointerException, IllegalStateException,
				IllegalRepositoryException, IllegalTargetException,
				IllegalIntervalException {
		super(parseAndValidateRepository(pRepository),
				parseAndValidateRoot(pRoot),
				parseAndValidateTarget(pTarget),
				parseAndValidateDatetime(pSince),
				parseAndValidateDatetime(pUntil));
		IllegalIntervalException.isTrue(!pSince.isAfter(pUntil),
				"Since (%s) after until (%s)", pSince, pUntil);
	}

	/**
	 * Use {@link de.unibremen.st.libvcs4j.VCSEngineBuilder} instead.
	 */
	@Deprecated
	@SuppressWarnings("DeprecatedIsStillUsed")
	public SVNEngine(
			final String pRepository,
			final String pRoot,
			final Path pTarget,
			final String pFrom,
			final String pTo)
				throws NullPointerException, IllegalStateException,
				IllegalRepositoryException, IllegalTargetException,
				IllegalIntervalException {
		super(parseAndValidateRepository(pRepository),
				parseAndValidateRoot(pRoot),
				parseAndValidateTarget(pTarget),
				parseAndValidateRevision(pFrom),
				parseAndValidateRevision(pTo));
		final int from = Integer.parseInt(pFrom);
		final int to = Integer.parseInt(pTo);
		IllegalIntervalException.isTrue(from <= to,
				"From (%s) > to (%s)", from, to);
	}

	///////////////////////// Parsing and validation //////////////////////////

	private static String parseAndValidateRepository(
			final String pRepository) {
		Validate.notNull(pRepository);
		IllegalRepositoryException.isTrue(
				SUPPORTED_PROTOCOLS.test(pRepository),
				"Unsupported protocol for '%s'", pRepository);
		if (FILE_PROTOCOL.test(pRepository)) {
			final String path = pRepository.substring(7);
			final Path p = Paths.get(path).toAbsolutePath();
			IllegalRepositoryException.isTrue(Files.isDirectory(p),
					"'%s' is not a directory", pRepository);
			IllegalRepositoryException.isTrue(Files.isReadable(p),
					"'%s' is not readable", pRepository);
		}
		// '\' (Windows file separator) is not supported by SVNKit.
		String repo = pRepository.replace("\\", "/");
		// Remove trailing '/'.
		if (repo.endsWith("/")) {
			repo = repo.substring(0, repo.length() - 1);
		}
		return repo;
	}

	private static String parseAndValidateRoot(
			final String pRoot) {
		Validate.notNull(pRoot);
		// '\' (Windows file separator) is not supported by SVNKit.
		String root = pRoot.replace("\\", "/");
		// Remove trailing '/'.
		if (root.endsWith("/")) {
			root = root.substring(0, root.length() - 1);
		}
		return root;
	}

	private static Path parseAndValidateTarget(
			final Path pTarget) {
		Validate.notNull(pTarget);
		IllegalTargetException.isTrue(!Files.exists(pTarget),
				"'%s' already exists", pTarget);
		IllegalTargetException.isTrue(Files.isWritable(pTarget.getParent()),
				"Parent of '%s' is not writable", pTarget);
		return pTarget.toAbsolutePath();
	}

	private static LocalDateTime parseAndValidateDatetime(
			final LocalDateTime pDatetime) {
		if (pDatetime.isBefore(MINIMUM_DATETIME)) {
			log.debug("Mapping datetime {} to {}",
					pDatetime, MINIMUM_DATETIME);
			return MINIMUM_DATETIME;
		}
		return pDatetime;
	}

	private static String parseAndValidateRevision(
			final String pRevision) {
		Validate.notNull(pRevision);
		try {
			int revision = Integer.parseInt(pRevision);
			if (revision < 1) {
				log.debug("Mapping revision {} to {}", revision, 1);
				revision = 1;
			}
			return String.valueOf(revision);
		} catch (final NumberFormatException e) {
			IllegalIntervalException.isTrue(false,
					"'%s' is not a valid svn revision", pRevision);
			return null; // just for the compiler
		}
	}

	///////////////////////////////////////////////////////////////////////////

	////////////////////////////////// Utils //////////////////////////////////

	private String getInput() {
		// `Paths.get` breaks protocol prefix
		final String repository = getRepository();
		final String root = getRoot();
		return root.isEmpty() ? repository : repository + "/" + root;
	}

	private String toSVNPath(final String pPath) {
		notNull(pPath);
		if (pPath.isEmpty()) {
			return getInput();
		}
		// '\' (Windows file separator) is not supported by SVNKit.
		String path = pPath.replace("\\", "/");
		// Remove trailing '/'.
		if (path.endsWith("/")) {
			path = path.substring(0, path.length() - 1);
		}
		return getInput() + "/" + path;
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

	///////////////////////////////////////////////////////////////////////////

	@Override
	protected Path getOutputImpl() {
		return getTarget();
	}

	@Override
	protected void checkoutImpl(
			final String pRevision)
				throws IOException {
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
			throw new IOException(e);
		} finally {
			factory.dispose();
		}
	}

	@Override
	protected Changes createChanges(
			final String pFrom,
			final String pTo)
				throws IOException {
		final SvnOperationFactory factory = new SvnOperationFactory();

		final Changes changes = new Changes();
		try {
			final SVNRevision from = createSVNRevision(pFrom);
			final SVNRevision to = createSVNRevision(pTo);
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
						System.out.println("Unsupported change type");
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
	protected List<String> listRevisionsImpl(
			final LocalDateTime pSince,
			final LocalDateTime pUntil)
				throws IOException {
		final SvnOperationFactory factory = new SvnOperationFactory();

		final List<String> revs = new ArrayList<>();
		try {
			final SVNRevision since = createSVNRevision(pSince);
			final SVNRevision until = createSVNRevision(pUntil);
			final SvnTarget input = SvnTarget.fromURL(
					createSVNURL(getInput()));

			final SvnLog log = factory.createLog();
			log.addRange(SvnRevisionRange.create(since, until));
			log.setSingleTarget(input);
			log.setReceiver((__, entry) -> {
				if (entry.getRevision() != 0) {
					revs.add(String.valueOf(entry.getRevision()));
				}
			});
			log.run();
		} catch (final SVNException e) {
			// Avoid file not found exception which is thrown if there is not
			// a single revision for `root`. Return an empty collection of
			// revisions instead.
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

	@Override
	protected List<String> listRevisionsImpl(
			final String pFrom,
			final String pTo)
				throws IOException {
		final long head;
		try {
			head = SVNRepositoryFactory
					.create(createSVNURL(getInput()))
					.getLatestRevision();
		} catch (final SVNException e) {
			throw new IOException(e);
		}

		final SvnOperationFactory factory = new SvnOperationFactory();

		final List<String> revs = new ArrayList<>();
		try {
			final SVNRevision from = createSVNRevision(pFrom);
			final SVNRevision to = createSVNRevision(String.valueOf(
					Math.min(Long.parseLong(pTo), head)));
			final SvnTarget input = SvnTarget.fromURL(
					createSVNURL(getInput()));

			final SvnLog log = factory.createLog();
			log.addRange(SvnRevisionRange.create(from, to));
			log.setSingleTarget(input);
			log.setReceiver((__, entry) -> {
				if (entry.getRevision() != 0) {
					revs.add(String.valueOf(entry.getRevision()));
				}
			});
			log.run();
		} catch (final SVNException e) {
			throw new IOException(e);
		} finally {
			factory.dispose();
		}
		return revs;
	}

	@Override
	protected byte[] readAllBytesImpl(
			final String pPath,
			final String pRevision)
				throws IOException {
		final SvnOperationFactory factory = new SvnOperationFactory();

		try {
			final SVNRevision revision = createSVNRevision(pRevision);
			final SvnTarget path = SvnTarget.fromURL(
					createSVNURL(toSVNPath(pPath)), revision);
			final ByteArrayOutputStream bos = new ByteArrayOutputStream();

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
	protected CommitImpl createCommitImpl(
			final String pRevision)
			throws IOException {
		final SvnOperationFactory factory = new SvnOperationFactory();

		try {
			final SVNRevision revision = createSVNRevision(pRevision);
			final SvnTarget input = SvnTarget.fromURL(
					createSVNURL(getInput()), revision);
			final CommitImpl commit = new CommitImpl();

			final SvnLog log = factory.createLog();
			log.addRange(SvnRevisionRange.create(revision, revision));
			log.setSingleTarget(input);
			log.setReceiver((__, entry) -> {
				commit.setAuthor(entry.getAuthor());
				commit.setMessage(entry.getMessage());
				commit.setDateTime(LocalDateTime.ofInstant(
						entry.getDate().toInstant(),
						ZoneId.systemDefault()));
				if (entry.getRevision() > 1) {
					commit.setParentIds(Collections.singletonList(
							String.valueOf(entry.getRevision() - 1)));
				}
			});
			log.run();

			return commit;
		} catch (final SVNException e) {
			throw new IOException(e);
		} finally {
			factory.dispose();
		}
	}

	@Override
	public FilenameFilter createVCSFileFilterImpl() {
		return (pDir, pName) -> !pName.endsWith(".svn");
	}
}
