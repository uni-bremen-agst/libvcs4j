package de.unibremen.informatik.st.libvcs4j.git;

import com.google.common.collect.Iterators;
import com.google.common.collect.PeekingIterator;
import de.unibremen.informatik.st.libvcs4j.Commit;
import de.unibremen.informatik.st.libvcs4j.FileChange;
import de.unibremen.informatik.st.libvcs4j.Issue;
import de.unibremen.informatik.st.libvcs4j.LineInfo;
import de.unibremen.informatik.st.libvcs4j.VCSEngine;
import de.unibremen.informatik.st.libvcs4j.VCSEngineBuilder;
import de.unibremen.informatik.st.libvcs4j.VCSFile;
import de.unibremen.informatik.st.libvcs4j.Validate;
import de.unibremen.informatik.st.libvcs4j.engine.AbstractIntervalVCSEngine;
import de.unibremen.informatik.st.libvcs4j.exception.IllegalRevisionException;
import de.unibremen.informatik.st.libvcs4j.exception.IllegalTargetException;
import de.unibremen.informatik.st.libvcs4j.exception.IllegalIntervalException;
import de.unibremen.informatik.st.libvcs4j.exception.IllegalRepositoryException;
import de.unibremen.informatik.st.libvcs4j.engine.Changes;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.LogCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.blame.BlameResult;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.RenameDetector;
import org.eclipse.jgit.lib.AnyObjectId;
import org.eclipse.jgit.lib.MutableObjectId;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.PathFilter;
import org.eclipse.jgit.treewalk.filter.TreeFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.AbstractMap.SimpleEntry;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author Marcel Steinbeck
 */
public class GitEngine extends AbstractIntervalVCSEngine {

	private static final Logger log = LoggerFactory.getLogger(GitEngine.class);

	private static final Predicate<String> SUPPORTED_PROTOCOLS =
			Pattern.compile("file://.*|http.*|ssh.*|git@.*").asPredicate();

	private static final Predicate<String> FILE_PROTOCOL =
			Pattern.compile("file://.*").asPredicate();

	private static final String DEFAULT_BRANCH = "master";

	/**
	 * Examined branch, for instance, 'master'.
	 */
	private final String branch;

	/**
	 * Creates a new Git engine that processes all commits of the given root
	 * directory and branch. Use {@link VCSEngineBuilder} for convenience.
	 *
	 * @param pRepository
	 * 		The repository to process (see {@link VCSEngine#getRepository()}).
	 * @param pRoot
	 * 		The root directory (see {@link VCSEngine#getRoot()}.
	 * @param pTarget
	 * 		The target directory (see {@link VCSEngine#getTarget()}).
	 * @param pBranch
	 * 		The branch to process.
	 * @throws NullPointerException
	 * 		If any of the given arguments is {@code null}.
	 * @throws IllegalArgumentException
	 * 		If any of the given arguments is invalid.
	 */
	public GitEngine(final String pRepository, final String pRoot,
			final Path pTarget, final String pBranch)
			throws NullPointerException, IllegalArgumentException {
		super(pRepository, pRoot, pTarget);
		branch = pBranch == null ? DEFAULT_BRANCH : pBranch;
	}

	/**
	 * Creates a new Git engine that processes all commits of the given root
	 * directory and branch within the given time range. Use
	 * {@link VCSEngineBuilder} for convenience.
	 *
	 * @param pRepository
	 * 		The repository to process (see {@link VCSEngine#getRepository()}).
	 * @param pRoot
	 * 		The root directory (see {@link VCSEngine#getRoot()}.
	 * @param pTarget
	 * 		The target directory (see {@link VCSEngine#getTarget()}).
	 * @param pBranch
	 * 		The branch to process.
	 * @param pSince
	 * 		The since date.
	 * @param pUntil
	 * 		The until date.
	 * @throws NullPointerException
	 * 		If any of the given arguments is {@code null}.
	 * @throws IllegalArgumentException
	 * 		If any of the given arguments is invalid.
	 */
	public GitEngine(final String pRepository, final String pRoot,
			final Path pTarget, final String pBranch,
			final LocalDateTime pSince, final LocalDateTime pUntil)
			throws NullPointerException, IllegalRepositoryException,
			IllegalTargetException {
		super(pRepository, pRoot, pTarget, pSince, pUntil);
		branch = pBranch == null ? DEFAULT_BRANCH : pBranch;
	}

	/**
	 * Creates a new Git engine that processes all commits of the given root
	 * directory and branch within the given revision range (inclusive). Use
	 * {@link VCSEngineBuilder} for convenience.
	 *
	 * @param pRepository
	 * 		The repository to process (see {@link VCSEngine#getRepository()}).
	 * @param pRoot
	 * 		The root directory (see {@link VCSEngine#getRoot()}.
	 * @param pTarget
	 * 		The target directory (see {@link VCSEngine#getTarget()}).
	 * @param pBranch
	 * 		The branch to process.
	 * @param pFrom
	 * 		The start revision.
	 * @param pTo
	 * 		The end revision (inclusive).
	 * @throws NullPointerException
	 * 		If any of the given arguments is {@code null}.
	 * @throws IllegalArgumentException
	 * 		If any of the given arguments is invalid.
	 */
	public GitEngine(final String pRepository, final String pRoot,
			final Path pTarget, final String pBranch, final String pFrom,
			final String pTo) throws NullPointerException,
			IllegalRepositoryException, IllegalTargetException {
		super(pRepository, pRoot, pTarget, pFrom, pTo);
		branch = pBranch == null ? DEFAULT_BRANCH : pBranch;
	}

	/**
	 * Creates a new Git engine that processes all commits of the given root
	 * directory and branch within the given revision index range (exclusive).
	 * Use {@link VCSEngineBuilder} for convenience.
	 *
	 * @param pRepository
	 * 		The repository to process (see {@link VCSEngine#getRepository()}).
	 * @param pRoot
	 * 		The root directory (see {@link VCSEngine#getRoot()}.
	 * @param pTarget
	 * 		The target directory (see {@link VCSEngine#getTarget()}).
	 * @param pBranch
	 * 		The branch to process.
	 * @param pStartIdx
	 * 		The index of the start revision ({@code >= 0}).
	 * @param pEndIdx
	 * 		The index of the end revision (exclusive).
	 * @throws NullPointerException
	 * 		If any of the given arguments is {@code null}.
	 * @throws IllegalArgumentException
	 * 		If any of the given arguments is invalid.
	 */
	public GitEngine(final String pRepository, final String pRoot,
			final Path pTarget, final String pBranch, final int pStartIdx,
			final int pEndIdx) throws NullPointerException,
			IllegalIntervalException {
		super(pRepository, pRoot, pTarget, pStartIdx, pEndIdx);
		branch = pBranch == null ? DEFAULT_BRANCH : pBranch;
	}

	///////////////////////// Validation and mapping //////////////////////////

	@Override
	@SuppressWarnings("Duplicates")
	protected String validateMapRepository(final String pRepository) {
		Validate.notEmpty(pRepository);
		IllegalRepositoryException.isTrue(
				SUPPORTED_PROTOCOLS.test(pRepository),
				"Unsupported protocol: '%s'", pRepository);
		if (FILE_PROTOCOL.test(pRepository)) {
			final String repository = pRepository.substring(7);
			final File file = new File(repository);
			IllegalRepositoryException.isTrue(file.exists(),
					"'%s' does not exist", pRepository);
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
	@SuppressWarnings("Duplicates")
	protected Path validateMapTarget(final Path pTarget) {
		Validate.notNull(pTarget);
		Validate.notEmpty(pTarget.toString());
		final File file = pTarget.toFile();
		IllegalTargetException.isTrue(!file.exists(),
				"'%s' already exists", pTarget);
		IllegalTargetException.isTrue(file.getParentFile().canWrite(),
				"Parent of '%s' is not writable", pTarget);
		return pTarget.toAbsolutePath();
	}

	@Override
	protected List<String> validateMapRevisions(final List<String> pRevisions) {
		return Validate.noNullElements(pRevisions).stream()
				.map(r -> {
					IllegalRevisionException.isTrue(
							Validate.notNull(r).matches("[0-9a-f]{5,40}"),
							String.format("'%s' is not a valid commit hash",r));
					return r;
				})
				.collect(Collectors.toList());
	}

	@Override
	protected LocalDateTime validateMapDateTime(final LocalDateTime pDateTime) {
		Validate.notNull(pDateTime);
		return pDateTime.getHour() == 0
				? pDateTime.plusHours(1)
				: pDateTime;
	}

	@Override
	protected String validateMapIntervalRevision(final String pRevision) {
		return pRevision == null ? "" : validateMapRevisions(
				Collections.singletonList(pRevision)).get(0);
	}

	////////////////////////////////// Utils //////////////////////////////////

	private LogCommand addRootPath(final LogCommand pLogCmd) {
		if (!getRoot().isEmpty()) {
			pLogCmd.addPath(getRoot());
		}
		return pLogCmd;
	}

	private TreeFilter createTreeFilter() {
		return getRoot().isEmpty()
				? TreeFilter.ANY_DIFF :
				PathFilter.create(getRoot());
	}

	private String toAbsolutePath(final String pPath) {
		return getTarget().resolve(pPath).toString();
	}

	private AnyObjectId createId(final String pRevision) {
		final MutableObjectId id = new MutableObjectId();
		id.fromString(pRevision);
		return id;
	}

	private Git openRepository() throws IOException {
		return Git.open(getTarget().toFile());
	}

	private String toGitPath(final String pPath) {
		Validate.notNull(pPath);
		return normalizePath(Paths.get(getRoot(), pPath).toString());
	}

	///////////////////////////////////////////////////////////////////////////

	@Override
	public Path getOutput() {
		return getTarget().resolve(getRoot());
	}

	@Override
	protected void checkoutImpl(final String revision) throws IOException {
		try {
			openRepository()
					.checkout()
					.setName(revision)
					.call();
		} catch (final GitAPIException e) {
			throw new IOException(e);
		}
	}

	@Override
	protected Changes createChangesImpl(final String fromRev,
			final String toRev) throws IOException {
		final AnyObjectId from = createId(fromRev);
		final AnyObjectId to = createId(toRev);
		final Repository repo = openRepository().getRepository();

		final Changes changes = new Changes();
		try (RevWalk revWalk = new RevWalk(repo);
			 ObjectReader reader = repo.newObjectReader()) {
			final RevTree prevTree = revWalk.parseCommit(from).getTree();
			final CanonicalTreeParser oldTree = new CanonicalTreeParser();
			oldTree.reset(reader, prevTree);

			final RevTree revTree = revWalk.parseCommit(to).getTree();
			final CanonicalTreeParser newTree = new CanonicalTreeParser();
			newTree.reset(reader, revTree);

			final List<DiffEntry> diffEntries = new ArrayList<>();
			openRepository()
					.diff()
					.setPathFilter(createTreeFilter())
					.setShowNameAndStatusOnly(true)
					.setOldTree(oldTree)
					.setNewTree(newTree)
					.call()
					.forEach(entry -> {
						diffEntries.add(entry);
						switch (entry.getChangeType()) {
							case ADD:
							case COPY:
								changes.getAdded().add(
										toAbsolutePath(entry.getNewPath()));
								break;
							case DELETE:
								changes.getRemoved().add(
										toAbsolutePath(entry.getOldPath()));
								break;
							case MODIFY:
								changes.getModified().add(
										toAbsolutePath(entry.getOldPath()));
								break;
							case RENAME:
								changes.getRelocated().add(new SimpleEntry<>(
										toAbsolutePath(entry.getOldPath()),
										toAbsolutePath(entry.getNewPath())));
								break;
							default:
								Validate.fail("Unexpected change type '%c'",
										entry.getChangeType());
						}
					});

			final RenameDetector rd = new RenameDetector(repo);
			rd.addAll(diffEntries);
			rd.compute().stream()
					.filter(entry -> entry.getScore() >= rd.getRenameScore())
					.forEach(rename -> {
						final boolean removed = changes.getRemoved().remove(
								toAbsolutePath(rename.getOldPath()));
						if (removed) {
							final boolean added = changes.getAdded().remove(
									toAbsolutePath(rename.getNewPath()));
							Validate.validateState(added,
									"Found rename with missing add part");
							changes.getRelocated().add(new SimpleEntry<>(
									toAbsolutePath(rename.getOldPath()),
									toAbsolutePath(rename.getNewPath())));
						}
					});
		} catch (final GitAPIException e) {
			throw new IOException(e);
		}
		return changes;
	}

	@Override
	protected Optional<String> getLatestRevision() throws IOException {
		// Keep in mind that 'git log' returns commits in the following
		// order: [HEAD, HEAD^1, ..., initial]

		try {
			final LogCommand logCmd = openRepository().log();
			final Iterable<RevCommit> it = addRootPath(logCmd).call();
			final List<RevCommit> revs = new ArrayList<>();
			it.forEach(revs::add);
			return revs.isEmpty()
					? Optional.empty()
					: Optional.of(revs.get(0).getName());
		} catch (NoHeadException e) {
			return Optional.empty();
		} catch (final GitAPIException e) {
			throw new IOException(e);
		}
	}

	@Override
	protected List<String> listRevisionsImpl(final LocalDateTime pSince,
			final LocalDateTime pUntil) throws IOException {
		final Date since = toDate(pSince);
		final Date until = toDate(pUntil);

		// Keep in mind that:
		// - 'git log' returns commits in the following order:
		//     [HEAD, HEAD^1, ..., initial]
		// , i.e. the commits are traversed from newest to oldest.
		// - The start predicate become true for the newest commit to include,
		// and the end predicate must become true for the oldest commit to
		// include.
		final Predicate<RevCommit> startPredicate = commit ->
				(commit.getAuthorIdent().getWhen().compareTo(until) <= 0);
		final Predicate<RevCommit> endPredicate = commit ->
				(commit.getAuthorIdent().getWhen().compareTo(since) <= 0);

		final LogCommand logCmd = openRepository().log();
		addRootPath(logCmd);
		return enumerateRevisions(logCmd, startPredicate, endPredicate);
	}

	@Override
	protected List<String> listRevisionsImpl(final String pFrom,
			final String pTo) throws IOException {

		// Keep in mind that:
		// - 'git log' returns commits in the following order:
		//     [HEAD, HEAD^1, ..., initial]
		// , i.e. the commits are traversed from newest to oldest.
		// - The start predicate become true for the newest commit to include,
		// and the end predicate must become true for the oldest commit to
		// include.

		// If no start commit is given, assume HEAD (i.e. the first commit
		// that is encountered).
		final Predicate<RevCommit> startPredicate = pTo.isEmpty()
				? commit -> true
				: commit -> (commit.getName().startsWith(pTo));
		// If no end commit is given, assume the initial commit.
		final Predicate<RevCommit> endPredicate = pFrom.isEmpty()
				? commit -> false
				: commit -> (commit.getName().startsWith(pFrom));

		final LogCommand logCmd = openRepository().log();
		addRootPath(logCmd);
		final List<String> revs = enumerateRevisions(
				logCmd, startPredicate, endPredicate);

		if (pFrom.isEmpty() || pTo.isEmpty()) {
			return revs;
		}

		// Check if there is a linear sequence between `pFrom` and `pTo`.
		String fromRev = null;
		String toRev = null;
		for (final String rv : revs) {
			if (rv.startsWith(pFrom)) {
				fromRev = rv;
			}
			if (rv.startsWith(pTo)) {
				toRev = rv;
			}
		}
		if (toRev != null && fromRev == null) { // no linear sequence
			log.info("There is no linear sequence from '{}' to '{}'",
					pFrom, pTo);
			log.info("Falling back to direct processing");
			revs.clear();
			addRevisionTo(revs, pFrom);
			if (revs.isEmpty()) {
				log.info("`From` revision '{}' does not exist", pFrom);
				log.info("Processing `to` revision '{}' only", pTo);
			}
			revs.add(toRev);
		}
		if (toRev == null) { // implies fromRev == null
			Validate.validateState(fromRev == null);
			Validate.validateState(revs.isEmpty());
			log.info("`To` revision '{}' does not exist", pTo);
			log.info("Processing `from` revision '{}' only", pFrom);
			addRevisionTo(revs, pFrom);
			if (revs.isEmpty()) {
				log.info("`From` revision '{}' does not exist as well", pFrom);
			}
		}
		return revs;
	}

	private void addRevisionTo(final List<String> revisions,
			final String revision) throws IOException {
		try {
			final LogCommand log = openRepository().log();
			addRootPath(log);
			log.call().forEach(rev -> {
				final String revName = rev.getName();
				if (revName.startsWith(revision)) {
					revisions.add(revName);
				}
			});
		} catch (final GitAPIException e) {
			throw new IOException(e);
		}
	}

	private List<String> enumerateRevisions(final LogCommand logCommand,
			final Predicate<RevCommit> startPredicate,
			final Predicate<RevCommit> endPredicate) throws IOException {
		final List<String> revs = new ArrayList<>();

		try {
			final PeekingIterator<RevCommit> revisions =
					Iterators.peekingIterator(logCommand.call().iterator());

			// Iterate over the commits until the start predicate is satisfied
			while (revisions.hasNext()) {
				// Only advance the iteration if the next commit does NOT satisfy the
				// start predicate. Thus, the iterator is positioned at the first
				// commit to include for the following loop.
				final RevCommit rv = revisions.peek();
				if (startPredicate.test(rv)) {
					break;
				} else {
					// Advance the iteration
					revisions.next();
				}
			}

			// Add commits until the end predicate is satisfied or no commits remain. In this loop,
			// we emulate the "--first-parent" behavior from "git log" to avoid mixing commits
			// from concurrent branches. This guarantees that two consecutive commits in the result list
			// are in a parent-child relation.
			RevCommit nextRevision = null;
			while (revisions.hasNext()) {
				RevCommit rv = revisions.next();

				if (nextRevision != null && rv != nextRevision) {
					// Immediately skip "unexpected" commits
					continue;
				}

				// Add the current commit to the result list
				revs.add(rv.getName());

				if (endPredicate.test(rv)) {
					break;
				}

				if (rv.getParentCount() > 0) {
					// Always choose the first parent as the next commit to include
					nextRevision = rv.getParent(0);
				} else {
					nextRevision = null;
				}
			}
		} catch (NoHeadException e) {
			return Collections.emptyList();
		} catch (final GitAPIException e) {
			throw new IOException(e);
		}

		Collections.reverse(revs);
		return revs;
	}

	@Override
	protected byte[] readAllBytesImpl(final String pPath,
			final String pRevision) throws IOException {
		final String path = toGitPath(pPath);
		final AnyObjectId rev = createId(pRevision);
		final Repository repo = openRepository().getRepository();

		try (RevWalk revWalk = new RevWalk(repo)) {
			final RevCommit commit = revWalk.parseCommit(rev);
			final RevTree tree = commit.getTree();

			try (TreeWalk treeWalk = new TreeWalk(repo)) {
				treeWalk.addTree(tree);
				treeWalk.setRecursive(true);
				treeWalk.setFilter(PathFilter.create(path));
				Validate.isTrue(treeWalk.next(), "Unable to find '%s'", pPath);
				final ObjectId id = treeWalk.getObjectId(0);
				final ObjectLoader loader = repo.open(id);
				return loader.getBytes();
			}
		}
	}

	@Override
	public List<LineInfo> readLineInfoImpl(final VCSFile pFile)
			throws IOException {
		final String path = toGitPath(pFile.getRelativePath());
		final AnyObjectId rev = createId(pFile.getRevision().getId());

		try {
			final BlameResult result = openRepository()
					.blame()
					.setFilePath(path)
					.setStartCommit(rev)
					.call();

			Validate.isTrue(result != null, "Unable to find '%s'", path);
			final int blameNumLines = result.getResultContents().size();
			final List<String> lines = pFile.readLines();
			final List<LineInfo> lineInfo = new ArrayList<>();

			/* Copy result from blame. */
			for (int i = 0; i < blameNumLines; i++) {
				final PersonIdent pi = result.getSourceAuthor(i);
				final RevCommit rc = result.getSourceCommit(i);
				final LocalDateTime dt = LocalDateTime.ofInstant(
						pi.getWhen().toInstant(),
						pi.getTimeZone().toZoneId());
				final LineInfo li = getModelFactory().createLineInfo(
						rc.getName(), pi.getName(),
						rc.getFullMessage().replaceAll("\r\n$|\n$", ""),
						dt, i + 1, lines.get(i), pFile, this);
				lineInfo.add(li);
			}
			/* Handle EOL fails by duplicating the last blame result. */
			if (blameNumLines > 0) { // Consider empty files.
				for (int i = blameNumLines; i < lines.size(); i++) {
					final LineInfo prev = lineInfo.get(i - 1);
					final LineInfo next = getModelFactory().createLineInfo(
							prev.getId(), prev.getAuthor(), prev.getMessage(),
							prev.getDateTime(), prev.getLine(),  lines.get(i),
							prev.getFile(), this);
					lineInfo.add(next);
				}
			}
			return lineInfo;
		} catch (final GitAPIException e) {
			throw new IOException(e);
		}
	}

	@Override
	protected Commit createCommitImpl(final String pRevision,
			final List<FileChange> pFileChanges, final List<Issue> pIssues)
			throws IllegalArgumentException, IOException {
		final AnyObjectId rev = createId(pRevision);
		final List<RevCommit> commits = new ArrayList<>();

		try {
			openRepository()
					.log()
					.setMaxCount(1)
					.add(rev)
					.call()
					.forEach(commits::add);
		} catch (final GitAPIException e) {
			throw new IOException(e);
		}
		Validate.validateState(commits.size() == 1, String.format(
				"Unexpected number of commits: Expected %d, Actual %d",
				1, commits.size()));
		Validate.validateState(commits.get(0).getName().equals(pRevision),
				String.format("Unexpected revision: Expected '%s', Actual '%s'",
				pRevision, commits.get(0).getName()));

		final RevCommit rc = commits.get(0);
		final List<String> parentIds =
				Arrays.stream(rc.getParents())
						.map(AnyObjectId::getName)
						.collect(Collectors.toList());
		final LocalDateTime dt = LocalDateTime.ofInstant(
				rc.getAuthorIdent().getWhen().toInstant(),
				rc.getAuthorIdent().getTimeZone().toZoneId());

		return getModelFactory().createCommit(pRevision,
				rc.getAuthorIdent().getName(),
				rc.getFullMessage().replaceAll("\r\n$|\n$", ""),
				dt, parentIds, pFileChanges, pIssues, this);
	}

	@Override
	protected void initImpl() throws IOException {
		try {
			log.info("Cloning {} to {}", getRepository(), getTarget());
			Git.cloneRepository()
					.setURI(getRepository())
					.setDirectory(getTarget().toFile())
					.setBranchesToClone(Collections.singleton(branch))
					.setBranch(branch)
					.call();
		} catch (final GitAPIException e) {
			throw new IOException(e);
		}
	}

	@Override
	public FilenameFilter createVCSFileFilter() {
		return (pDir, pName) -> !pName.equals(".git");
	}
}
