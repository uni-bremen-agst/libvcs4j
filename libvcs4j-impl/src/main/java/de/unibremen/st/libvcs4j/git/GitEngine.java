package de.unibremen.st.libvcs4j.git;

import de.unibremen.st.libvcs4j.data.CommitImpl;
import de.unibremen.st.libvcs4j.exception.IllegalTargetException;
import de.unibremen.st.libvcs4j.engine.AbstractIntervalVCSEngine;
import de.unibremen.st.libvcs4j.engine.Changes;
import org.apache.commons.lang3.Validate;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.LogCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.RenameDetector;
import org.eclipse.jgit.lib.AnyObjectId;
import org.eclipse.jgit.lib.MutableObjectId;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.ObjectReader;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.Validate.validState;

/**
 * @author Marcel Steinbeck
 */
public class GitEngine extends AbstractIntervalVCSEngine {

	private static final Logger log = LoggerFactory.getLogger(GitEngine.class);

	private static final Predicate<String> SUPPORTED_PROTOCOLS =
			Pattern.compile("file://.*|http.*|ssh.*|git@.*").asPredicate();

	private static final Predicate<String> FILE_PROTOCOL =
			Pattern.compile("file://.*").asPredicate();

	/**
	 * Examined branch, for instance, 'master'.
	 */
	private final String branch;

    /**
     * Use {@link de.unibremen.st.libvcs4j.VCSEngineBuilder} instead.
     */
    @Deprecated
    @SuppressWarnings("DeprecatedIsStillUsed")
	public GitEngine(
			final String pRepository,
			final String pRoot,
			final Path pTarget,
			final String pBranch,
			final LocalDateTime pSince,
			final LocalDateTime pUntil)
				throws NullPointerException, IllegalArgumentException {
		super(parseRepository(pRepository),
				parseRoot(pRepository, pRoot),
				parseAndValidateTarget(pTarget),
				parseDateTime(pSince),
				parseDateTime(pUntil));
		branch = Validate.notEmpty(pBranch);
	}

    /**
     * Use {@link de.unibremen.st.libvcs4j.VCSEngineBuilder} instead.
     */
    @Deprecated
    @SuppressWarnings("DeprecatedIsStillUsed")
	public GitEngine(
			final String pRepository,
			final String pRoot,
			final Path pTarget,
			final String pBranch,
			final String pFrom,
			final String pTo)
				throws NullPointerException, IllegalArgumentException {
		super(parseRepository(pRepository),
				parseRoot(pRepository, pRoot),
				parseAndValidateTarget(pTarget),
				Validate.notEmpty(pFrom),
				Validate.notEmpty(pTo));
		branch = Validate.notEmpty(pBranch);
	}

	///////////////////////// Parsing and validation //////////////////////////

	private static String parseRepository(final String pRepository) {
		Validate.notNull(pRepository);
		Validate.isTrue(SUPPORTED_PROTOCOLS.test(pRepository),
				"Unsupported protocol for '%s'", pRepository);
		if (FILE_PROTOCOL.test(pRepository)) {
		    final String repo = pRepository.substring(7);
			final Path path = Paths.get(repo).toAbsolutePath();
			Validate.isTrue(Files.exists(path),
					"'%s' does not exist", pRepository);
			Validate.isTrue(Files.isDirectory(path),
					"'%s' is not a directory", pRepository);
			Validate.isTrue(Files.isReadable(path),
					"'%s' is not readable", pRepository);
		}
		return pRepository;
	}

	private static String parseRoot(
			final String pRepository,
			final String pRoot) {
		Validate.notNull(pRepository);
		Validate.notNull(pRoot);
		final Path repository = Paths.get(pRepository);
		if (Files.isDirectory(repository)) {
			final Path root = Paths.get(pRepository, pRoot).toAbsolutePath();
			Validate.isTrue(Files.isDirectory(root),
					"'%s' is not a directory", pRoot);
			Validate.isTrue(Files.isReadable(root),
					"'%s' is not readable", pRoot);
		}
		return Paths.get(pRoot).toString();
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

	private static LocalDateTime parseDateTime(
			final LocalDateTime pDatetime) {
		return pDatetime.getHour() == 0
				? pDatetime.plusHours(1)
				: pDatetime;
	}

	///////////////////////////////////////////////////////////////////////////

	////////////////////////////////// Utils //////////////////////////////////

	private LogCommand addRootPath(final LogCommand pLogCmd) {
		if (!getRoot().isEmpty()) {
			pLogCmd.addPath(getRoot().replace(File.separator, "/"));
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

	///////////////////////////////////////////////////////////////////////////

	@Override
	public Path getOutputImpl() {
		return getTarget().resolve(getRoot());
	}

	@Override
	protected void checkoutImpl(
			final String pRevision)
				throws IOException {
		try {
			openRepository()
                    .checkout()
					.setName(pRevision)
                    .call();
		} catch (final GitAPIException e) {
			throw new IOException(e);
		}
	}

	@Override
	protected Changes createChanges(
			final String pFrom,
			final String pTo)
				throws IOException {
		final AnyObjectId from = createId(pFrom);
		final AnyObjectId to = createId(pTo);
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
								validState(false,
										"Unexpected change type '%c'",
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
							Validate.validState(added,
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
	protected List<String> listRevisionsImpl(
			final LocalDateTime pSince,
			final LocalDateTime pUntil)
				throws IOException {
		final Date since = toDate(pSince);
		final Date until = toDate(pUntil);

		final List<String> revs = new ArrayList<>();
		try {
			final LogCommand logCmd = openRepository()
					.log();
			addRootPath(logCmd)
					.call()
					.forEach(rv -> {
						final Date date = rv.getAuthorIdent().getWhen();
						if (!date.before(since) && !date.after(until)) {
							revs.add(rv.getName());
						}
					});
		} catch (NoHeadException e) {
		    return Collections.emptyList();
        } catch (final GitAPIException e) {
			throw new IOException(e);
		}
        Collections.reverse(revs);
		return revs;
	}

	@Override
	protected List<String> listRevisionsImpl(
			final String pFrom,
			final String pTo)
				throws IOException {
		final AnyObjectId from = createId(pFrom);
		final AnyObjectId to = createId(pTo);

		final List<String> revs = new ArrayList<>();
		try {
			final LogCommand logCmd = openRepository()
					.log()
					.addRange(from, to);
			addRootPath(logCmd)
					.call()
					.forEach(rv -> revs.add(rv.getName()));
		} catch (NoHeadException e) {
            return Collections.emptyList();
        } catch (final GitAPIException e) {
			throw new IOException(e);
		}
        Collections.reverse(revs);
		return revs;
	}

	@Override
	protected byte[] readAllBytesImpl(
			final String pPath,
			final String pRevision)
				throws IOException {
		final String path = Paths.get(getRoot(), pPath).toString();
		final AnyObjectId rev = createId(pRevision);
		final Repository repo = openRepository().getRepository();

		try (RevWalk revWalk = new RevWalk(repo)) {
			final RevCommit commit = revWalk.parseCommit(rev);
			final RevTree tree = commit.getTree();

			try (TreeWalk treeWalk = new TreeWalk(repo)) {
				treeWalk.addTree(tree);
				treeWalk.setRecursive(true);
				treeWalk.setFilter(PathFilter.create(path));
				if (!treeWalk.next()) {
					throw new IllegalArgumentException(
							String.format("Unable to find '%s'", pPath));
				}
				final ObjectId id = treeWalk.getObjectId(0);
				final ObjectLoader loader = repo.open(id);
				return loader.getBytes();
			}
		}
	}

	@Override
	protected CommitImpl createCommitImpl(
			final String pRevision)
				throws IOException {
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
		validState(commits.size() == 1, String.format(
				"Unexpected number of commits: Expected %d, Actual %d",
				1, commits.size()));
		validState(commits.get(0).getName().equals(pRevision), String.format(
				"Unexpected revision: Expected '%s', Actual '%s'",
				pRevision, commits.get(0).getName()));

		final RevCommit rc = commits.get(0);
		final List<String> parentIds =
				Arrays.stream(rc.getParents())
						.map(AnyObjectId::getName)
						.collect(Collectors.toList());
		final LocalDateTime dt = LocalDateTime.ofInstant(
				rc.getAuthorIdent().getWhen().toInstant(),
				rc.getAuthorIdent().getTimeZone().toZoneId());

		final CommitImpl commit = new CommitImpl();
		commit.setAuthor(rc.getName());
		commit.setMessage(rc.getFullMessage());
		commit.setDateTime(dt);
		commit.setParentIds(parentIds);
		return commit;
	}

	@Override
	protected void initImpl() throws IOException {
		try {
			log.info("Cloning {} to {}", getRepository(), getTarget());
			Git.cloneRepository()
                    .setURI(getRepository())
                    .setDirectory(getTarget().toFile())
                    .setBranch(branch)
                    .call();
		} catch (final GitAPIException e) {
			throw new IOException(e);
		}
	}

	@Override
	public FilenameFilter createVCSFileFilterImpl() {
		return (pDir, pName) -> !pName.equals(".git") &&
				!pName.equals(".gitignore");
	}
}
