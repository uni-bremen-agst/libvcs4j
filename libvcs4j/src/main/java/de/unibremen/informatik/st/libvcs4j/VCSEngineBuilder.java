package de.unibremen.informatik.st.libvcs4j;

import de.unibremen.informatik.st.libvcs4j.engine.AbstractIntervalVCSEngine;
import de.unibremen.informatik.st.libvcs4j.filesystem.SingleEngine;
import de.unibremen.informatik.st.libvcs4j.git.GitEngine;
import de.unibremen.informatik.st.libvcs4j.hg.HGEngine;
import de.unibremen.informatik.st.libvcs4j.svn.SVNEngine;

import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.Optional;
import java.util.UUID;

/**
 * A generic builder for {@link VCSEngine} instances that has been designed to
 * handle user input values and, thus, is more forgiving with improper
 * parameters. That is, for instance, {@code null} and empty parameter values
 * are mapped to default values.
 */
public class VCSEngineBuilder {

	/**
	 * The supported VCS engines.
	 */
	private enum Engine { SINGLE, SVN, GIT, HG }

	/**
	 * The supported commit intervals.
	 */
	private enum Interval { DATE, REVISION, RANGE, LATEST }

	//////////////////////////////// Defaults /////////////////////////////////

	/**
	 * The default root (see {@link #withRoot(String)}).
	 */
	public static final String DEFAULT_ROOT = "";

	/**
	 * The default since (see {@link #withSince(LocalDateTime)}) date. Only SVN
	 * has restrictions regarding the minimum date.
	 */
	public static final LocalDateTime DEFAULT_SINCE =
			SVNEngine.MINIMUM_DATETIME;

	/**
	 * The default until (see {@link #withUntil(LocalDateTime)}) date. Year
	 * 2200 should be sufficient.
	 */
	public static final LocalDateTime DEFAULT_UNTIL =
			LocalDateTime.of(2200, 1, 1, 0, 0);

	/**
	 * The default start index (see {@link #withStartIdx(int)}).
	 */
	public static final int DEFAULT_START_IDX =
			AbstractIntervalVCSEngine.MIN_START_IDX;

	/**
	 * The default end index (see {@link #withEndIdx(int)}).
	 */
	public static final int DEFAULT_END_IDX = Integer.MAX_VALUE;

	/**
	 * The default target (see {@link #withTarget(Path)}). Cannot be static
	 * since every instance needs its own default value!
	 */
	private final String defaultTarget = Paths.get(
			System.getProperty("java.io.tmpdir"),          // system tmp dir
			"libvcs4j").toString() +                       // prefix
			UUID.randomUUID().toString().replace("-", ""); // unique id;

	////////////////////////////// Configuration //////////////////////////////

	/**
	 * Stores the repository to process.
	 */
	private String repository;

	/**
	 * Stores the engine that should be used to process {@link #repository}.
	 * The default engine is {@link Engine#SINGLE}
	 */
	private Engine engine = Engine.SINGLE;

	/**
	 * Stores the root directory.
	 */
	private String root = DEFAULT_ROOT;

	/**
	 * Stores the target.
	 */
	private String target = defaultTarget;

	/**
	 * Stores the branch.
	 */
	private String branch = null;

	/**
	 * Stores the interval type.
	 */
	private Interval interval = Interval.DATE;

	/**
	 * Stores the since date.
	 */
	private LocalDateTime since = LocalDateTime.from(DEFAULT_SINCE);

	/**
	 * Stores the until date.
	 */
	private LocalDateTime until = LocalDateTime.from(DEFAULT_UNTIL);

	/**
	 * Stores the start index.
	 */
	private int startIdx = DEFAULT_START_IDX;

	/**
	 * Stores the end index.
	 */
	private int endIdx = DEFAULT_END_IDX;

	/**
	 * Stores the from commit.
	 */
	private String from = null;

	/**
	 * Stores the to commit.
	 */
	private String to = null;

	/**
	 * Stores the {@link ITEngine} that should be used to extract issues.
	 */
	private ITEngine itEngine = null;

	////////////////////////////// Constructors ///////////////////////////////

	/**
	 * Creates a new builder with given repository path.
	 *
	 * @param repository
	 * 		Path to the repository.
	 * @throws NullPointerException
	 * 		If {@code repository} is {@code null}.
	 */
	public VCSEngineBuilder(final String repository)
			throws NullPointerException {
		withRepository(repository);
	}

	/**
	 * Creates a new builder with given repository path.
	 *
	 * @param repository
	 * 		Path to the repository.
	 * @return
	 * 		The created builder.
	 * @throws NullPointerException
	 * 		If {@code repository} is {@code null}.
	 */
	public static VCSEngineBuilder of(final String repository)
			throws NullPointerException{
		return new VCSEngineBuilder(repository);
	}

	/**
	 * Creates a new Git builder with given repository path.
	 *
	 * @param repository
	 * 		Path to the repository.
	 * @return
	 * 		The created Git builder.
	 * @throws NullPointerException
	 * 		If {@code repository} is {@code null}.
	 */
	public static VCSEngineBuilder ofGit(final String repository)
			throws NullPointerException{
		return of(repository).withGit();
	}

	/**
	 * Creates a new Mercurial builder with given repository path.
	 *
	 * @param repository
	 * 		Path to the repository.
	 * @return
	 * 		The created Mercurial builder.
	 * @throws NullPointerException
	 * 		If {@code repository} is {@code null}.
	 */
	public static VCSEngineBuilder ofHG(final String repository)
			throws NullPointerException{
		return of(repository).withHG();
	}

	/**
	 * Creates a new single input file/directory builder with given path.
	 *
	 * @param repository
	 * 		Path to the file or directory.
	 * @return
	 * 		The created single input file/directory builder.
	 * @throws NullPointerException
	 * 		If {@code repository} is {@code null}.
	 */
	public static VCSEngineBuilder ofSingle(final String repository)
			throws NullPointerException{
		return of(repository).withSingle();
	}

	/**
	 * Creates a new SVN builder with given repository path.
	 *
	 * @param repository
	 * 		Path to the repository.
	 * @return
	 * 		The created SVN builder.
	 * @throws NullPointerException
	 * 		If {@code repository} is {@code null}.
	 */
	public static VCSEngineBuilder ofSVN(final String repository)
			throws NullPointerException{
		return of(repository).withSVN();
	}

	/////////////////////////////// Fluent API ////////////////////////////////

	/**
	 * Sets the path to repository.
	 *
	 * @param repository
	 * 		Path to the repository.
	 * @return
	 * 		This builder.
	 */
	public VCSEngineBuilder withRepository(final String repository) {
		this.repository = Validate.notNull(repository).trim();
		return this;
	}

	/**
	 * Sets the engine to {@link SingleEngine}.
	 *
	 * @return
	 * 		This builder.
	 */
	public VCSEngineBuilder withSingle() {
		engine = Engine.SINGLE;
		return this;
	}

	/**
	 * Sets the engine to {@link SVNEngine}.
	 *
	 * @return
	 * 		This builder.
	 */
	public VCSEngineBuilder withSVN() {
		engine = Engine.SVN;
		return this;
	}

	/**
	 * Sets the engine to {@link GitEngine}.
	 *
	 * @return
	 * 		This builder.
	 */
	public VCSEngineBuilder withGit() {
		engine = Engine.GIT;
		return this;
	}

	/**
	 * Sets the engine to {@link HGEngine}.
	 *
	 * @return
	 * 		This builder.
	 */
	public VCSEngineBuilder withHG() {
		engine = Engine.HG;
		return this;
	}

	/**
	 * Sets the root directory. If {@code root} is {@code null},
	 * {@link #DEFAULT_ROOT} is used as fallback. The given string is trimmed
	 * using {@link String#trim()}.
	 *
	 * @param root
	 * 		The root directory.
	 * @return
	 * 		This builder.
	 */
	public VCSEngineBuilder withRoot(final String root) {
		this.root = root == null ? DEFAULT_ROOT : root.trim();
		return this;
	}

	/**
	 * Sets the target directory. That is, the directory where the revisions
	 * will be checked out. If {@code target} is null or empty,
	 * {@link #defaultTarget} is used as fallback. The given string is trimmed
	 * using {@link String#trim()}.
	 *
	 * @param target
	 * 		The directory where the revisions will be checked out.
	 * @return
	 * 		This builder.
	 */
	public VCSEngineBuilder withTarget(final String target) {
		this.target = target == null ? defaultTarget : target.trim();
		this.target = this.target.isEmpty() ? defaultTarget : this.target;
		return this;
	}

	/**
	 * Sets the target directory. That is, the directory where the revisions
	 * will be checked out. If {@code target} is null, {@link #defaultTarget}
	 * is used as fallback.
	 *
	 * @param target
	 * 		The directory where the revisions will be checked out.
	 * @return
	 * 		This builder.
	 */
	public VCSEngineBuilder withTarget(final Path target) {
		return withTarget(Optional.ofNullable(target)
				.map(Path::toString)
				.orElse(null));
	}

	/**
	 * Sets the branch to process. If {@code branch} is {@code null}, the
	 * engine's default branch is used as fallback.
	 *
	 * @param branch
	 * 		The branch to process.
	 * @return
	 * 		This builder.
	 */
	public VCSEngineBuilder withBranch(final String branch) {
		this.branch = branch;
		return this;
	}

	/**
	 * Sets the since date as {@link LocalDateTime}. If {@code since} is
	 * {@code null} or before {@link #DEFAULT_SINCE}, {@link #DEFAULT_SINCE} is
	 * used as fallback.
	 *
	 * @param since
	 * 		The since date.
	 * @return
	 * 		This builder.
	 */
	public VCSEngineBuilder withSince(final LocalDateTime since) {
		this.since = since == null || since.isBefore(DEFAULT_SINCE)
				? DEFAULT_SINCE
				: since;
		interval = Interval.DATE;
		return this;
	}

	/**
	 * Sets the since date as {@link LocalDate}. If {@code since} is
	 * {@code null} or before {@link #DEFAULT_SINCE}, {@link #DEFAULT_SINCE} is
	 * used as fallback.
	 *
	 * @param since
	 * 		The since date.
	 * @return
	 * 		This builder.
	 */
	public VCSEngineBuilder withSince(final LocalDate since) {
		final LocalDate defaultSince = DEFAULT_SINCE.toLocalDate();
		this.since = since == null || since.isBefore(defaultSince)
				? DEFAULT_SINCE
				: parseDateTime(since.toString(), DEFAULT_SINCE);
		interval = Interval.DATE;
		return this;
	}

	/**
	 * Sets the since date as {@link String}. If {@code since} is {@code null}
	 * or before {@link #DEFAULT_SINCE}, {@link #DEFAULT_SINCE} is used as
	 * fallback.
	 *
	 * @param since
	 * 		The since date.
	 * @return
	 * 		This builder.
	 * @throws IllegalArgumentException
	 * 		If parsing {@code since} fails.
	 */
	public VCSEngineBuilder withSince(final String since)
			throws IllegalArgumentException {
		this.since = parseDateTime(since, DEFAULT_SINCE);
		interval = Interval.DATE;
		return this;
	}

	/**
	 * Sets the until date as {@link LocalDateTime}. If {@code until} is
	 * {@code null} or after {@link #DEFAULT_UNTIL}, {@link #DEFAULT_UNTIL} is
	 * used as fallback.
	 *
	 * @param until
	 * 		The until date.
	 * @return
	 * 		This builder.
	 */
	public VCSEngineBuilder withUntil(final LocalDateTime until) {
		this.until = until == null || until.isAfter(DEFAULT_UNTIL)
				? DEFAULT_UNTIL
				: until;
		interval = Interval.DATE;
		return this;
	}

	/**
	 * Sets the until date as {@link LocalDate}. If {@code until} is
	 * {@code null} or after {@link #DEFAULT_UNTIL}, {@link #DEFAULT_UNTIL} is
	 * used as fallback.
	 *
	 * @param until
	 * 		The until date.
	 * @return
	 * 		This builder.
	 */
	public VCSEngineBuilder withUntil(final LocalDate until) {
		final LocalDate defaultUntil = DEFAULT_UNTIL.toLocalDate();
		this.until = until == null || until.isAfter(defaultUntil)
				? DEFAULT_UNTIL
				: parseDateTime(until.toString(), DEFAULT_UNTIL);
		interval = Interval.DATE;
		return this;
	}

	/**
	 * Sets the until date as {@link String}. If {@code until} is {@code null}
	 * or after {@link #DEFAULT_UNTIL}, {@link #DEFAULT_UNTIL} is used as
	 * fallback.
	 *
	 * @param until
	 * 		The until date.
	 * @return
	 * 		This builder.
	 * @throws IllegalArgumentException
	 * 		If parsing {@code until} fails.
	 */
	public VCSEngineBuilder withUntil(final String until)
			throws IllegalArgumentException {
		this.until = parseDateTime(until, DEFAULT_UNTIL);
		interval = Interval.DATE;
		return this;
	}

	/**
	 * Sets the from revision.
	 *
	 * @param from
	 * 		The from revision.
	 * @return
	 * 		This builder.
	 */
	public VCSEngineBuilder withFrom(final String from) {
		this.from = Validate.notEmpty(from);
		interval = Interval.REVISION;
		return this;
	}

	/**
	 * Sets the inclusive to revision.
	 *
	 * @param to
	 * 		The inclusive to revision.
	 * @return
	 * 		This builder.
	 */
	public VCSEngineBuilder withTo(final String to) {
		this.to = Validate.notEmpty(to);
		interval = Interval.REVISION;
		return this;
	}

	/**
	 * Sets the start index. The origin is {@code 0}.
	 *
	 * @param start
	 * 		The start index ({@code >= 0}).
	 * @return
	 * 		This builder.
	 */
	public VCSEngineBuilder withStartIdx(final int start) {
		this.startIdx = start;
		interval = Interval.RANGE;
		return this;
	}

	/**
	 * Sets the exclusive end index.
	 *
	 * @param end
	 * 		The exclusive end index ({@code >= 1}).
	 * @return
	 * 		This builder.
	 */
	public VCSEngineBuilder withEndIdx(final int end) {
		this.endIdx = end;
		interval = Interval.RANGE;
		return this;
	}

	/**
	 * Configures the engine such that only the latest revision is checked out.
	 *
	 * @return
	 * 		This builder.
	 */
	public VCSEngineBuilder withLatestRevision() {
		interval = Interval.LATEST;
		return this;
	}

	/**
	 * Sets the {@link ITEngine}. {@code null} values are permitted.
	 *
	 * @param itEngine
	 * 		The {@link ITEngine}.
	 * @return
	 * 		This builder.
	 */
	public VCSEngineBuilder withITEngine(final ITEngine itEngine) {
		this.itEngine = itEngine;
		return this;
	}

	/**
	 * Creates the engine.
	 *
	 * @return
	 * 		The created engine.
	 */
	public VCSEngine build() {
		final VCSEngine vcsEngine;
		if (engine == Engine.SINGLE) {
			vcsEngine = new SingleEngine(Paths.get(repository, root));
		} else {
			Optional<Path> path = Optional.empty();
			try {
				path = Optional.of(Paths.get(repository));
			} catch (final InvalidPathException ignored) { /* ignored */ }
			final String repo = path
					.filter(p -> p.toFile().exists())
					.map(r -> "file://" + r)
					.orElse(repository);
			if (engine == Engine.SVN) {
				if (interval == Interval.DATE) {
					vcsEngine = new SVNEngine(
							repo, root,
							Paths.get(target),
							since, until);
				} else if (interval == Interval.REVISION) {
					vcsEngine = new SVNEngine(
							repo, root,
							Paths.get(target),
							from, to);
				} else if (interval == Interval.RANGE) {
					vcsEngine = new SVNEngine(
							repo, root,
							Paths.get(target),
							startIdx, endIdx);
				} else if (interval == Interval.LATEST) {
					vcsEngine = new SVNEngine(
							repo, root,
							Paths.get(target));
				} else {
					throw new IllegalStateException(String.format(
							"Unknown interval '%s'", interval));
				}
			} else if (engine == Engine.GIT) {
				if (interval == Interval.DATE) {
					vcsEngine = new GitEngine(
							repo, root,
							Paths.get(target),
							branch,
							since, until);
				} else if (interval == Interval.REVISION) {
					vcsEngine = new GitEngine(
							repo, root,
							Paths.get(target),
							branch,
							from, to);
				} else if (interval == Interval.RANGE) {
					vcsEngine = new GitEngine(
							repo, root,
							Paths.get(target),
							branch,
							startIdx, endIdx);
				} else if (interval == Interval.LATEST) {
					vcsEngine = new GitEngine(
							repo, root,
							Paths.get(target),
							branch);
				} else {
					throw new IllegalStateException(String.format(
							"Unknown interval '%s'", interval));
				}
			} else if (engine == Engine.HG) {
				if (interval == Interval.DATE) {
					vcsEngine = new HGEngine(
							repo, root,
							Paths.get(target),
							branch,
							since, until);
				} else if (interval == Interval.REVISION) {
					vcsEngine = new HGEngine(
							repo, root,
							Paths.get(target),
							branch,
							from, to);
				} else if (interval == Interval.RANGE) {
					vcsEngine = new HGEngine(
							repo, root,
							Paths.get(target),
							branch,
							startIdx, endIdx);
				} else if (interval == Interval.LATEST) {
					vcsEngine = new HGEngine(
							repo, root,
							Paths.get(target),
							branch);
				} else {
					throw new IllegalStateException(String.format(
							"Unknown interval '%s'", interval));
				}
			} else {
				throw new IllegalStateException(String.format(
						"Unknown VCS engine '%s'", engine));
			}
		}

		// delete temporary directory when shutting down application
		if (target.equals(defaultTarget)) {
			Runtime.getRuntime().addShutdownHook(new DeleteTask(target));
		}

		if (itEngine != null) {
			vcsEngine.setITEngine(itEngine);
		}
		return vcsEngine;
	}

	///////////////////////////////// Helper //////////////////////////////////

	private LocalDateTime parseDateTime(final String pDateTime,
			final LocalDateTime pDefault) throws IllegalArgumentException {
		if (pDateTime == null) {
			return pDefault;
		}
		final String tmp = pDateTime.trim();
		if (pDateTime.isEmpty()) {
			return pDefault;
		}
		try {
			return LocalDateTime.parse(tmp);
		} catch (final DateTimeParseException ignored) { /* try next */ }
		try {
			final LocalDate date = LocalDate.parse(tmp);
			return LocalDateTime.of(
					date.getYear(),
					date.getMonth(),
					date.getDayOfMonth(),
					0, 0);
		}catch (final DateTimeParseException ignored) { /* try next */ }
		try {
			final DateFormat dateFormat = new SimpleDateFormat();
			return LocalDateTime.ofInstant(
					dateFormat.parse(pDateTime).toInstant(),
					ZoneId.systemDefault());
		} catch (final ParseException ignored) { /* try next */ }
		try {
			final int year = Integer.parseInt(pDateTime);
			return LocalDateTime.of(year, 1, 1, 0, 0);
		} catch (final NumberFormatException |
				DateTimeParseException ignored) { /* try next */ }
		throw new IllegalArgumentException(String.format(
				"Unable to parse '%s'", pDateTime));
	}
}
