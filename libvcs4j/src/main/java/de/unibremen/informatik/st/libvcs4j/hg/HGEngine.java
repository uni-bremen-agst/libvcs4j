package de.unibremen.informatik.st.libvcs4j.hg;

import com.aragost.javahg.Changeset;
import com.aragost.javahg.Repository;
import com.aragost.javahg.commands.CatCommand;
import com.aragost.javahg.commands.LogCommand;
import com.aragost.javahg.commands.StatusCommand;
import com.aragost.javahg.commands.StatusResult;
import com.aragost.javahg.commands.flags.CatCommandFlags;
import com.aragost.javahg.commands.flags.LogCommandFlags;
import com.aragost.javahg.commands.flags.StatusCommandFlags;
import com.aragost.javahg.commands.flags.UpdateCommandFlags;
import com.google.common.collect.Streams;
import de.unibremen.informatik.st.libvcs4j.VCSEngineBuilder;
import de.unibremen.informatik.st.libvcs4j.engine.AbstractIntervalVCSEngine;
import de.unibremen.informatik.st.libvcs4j.exception.IllegalIntervalException;
import de.unibremen.informatik.st.libvcs4j.exception.IllegalRepositoryException;
import de.unibremen.informatik.st.libvcs4j.exception.IllegalRevisionException;
import de.unibremen.informatik.st.libvcs4j.exception.IllegalTargetException;
import de.unibremen.informatik.st.libvcs4j.data.CommitImpl;
import de.unibremen.informatik.st.libvcs4j.engine.Changes;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.logging.LogManager;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HGEngine extends AbstractIntervalVCSEngine {

	private static final Logger log = LoggerFactory.getLogger(HGEngine.class);

	private static final LocalDateTime MAX_DATETIME =
			LocalDateTime.of(2038, 1, 1, 0, 0);

	static {
		// Disable the logger (java.util.logging) used by JavaHG.
		LogManager.getLogManager().reset();

		// ... there still is cobol software in use...
		if (LocalDateTime.now().isAfter(MAX_DATETIME)) {
			throw new IllegalStateException(String.format(
					"Mercurial does not support datetime values after '%s'",
					MAX_DATETIME));
		}
	}

	private static final Predicate<String> SUPPORTED_PROTOCOLS =
			Pattern.compile("file://.*|http.*|https.*|ssh.*").asPredicate();

	private static final Predicate<String> FILE_PROTOCOL =
			Pattern.compile("file://.*").asPredicate();

	private Repository repository = null;

	/**
	 * Use {@link VCSEngineBuilder} instead.
	 */
	@Deprecated
	@SuppressWarnings("DeprecatedIsStillUsed")
	public HGEngine(
			final String pRepository, final String pRoot, final Path pTarget,
			final LocalDateTime pSince, final LocalDateTime pUntil)
			throws NullPointerException, IllegalIntervalException {
		super(pRepository, pRoot,pTarget, pSince, pUntil);
	}

	/**
	 * Use {@link VCSEngineBuilder} instead.
	 */
	@Deprecated
	@SuppressWarnings("DeprecatedIsStillUsed")
	public HGEngine(
			final String pRepository, final String pRoot, final Path pTarget,
			final String pFrom, final String pTo) throws NullPointerException {
		super(pRepository, pRoot,pTarget, pFrom, pTo);
	}

	/**
	 * Use {@link VCSEngineBuilder} instead.
	 */
	@Deprecated
	@SuppressWarnings("DeprecatedIsStillUsed")
	public HGEngine(
			final String pRepository, final String pRoot, final Path pTarget,
			final int pStart, final int pEnd) throws NullPointerException,
			IllegalIntervalException {
		super(pRepository, pRoot,pTarget, pStart, pEnd);
	}

	/**
	 * Use {@link VCSEngineBuilder} instead.
	 */
	@Deprecated
	@SuppressWarnings("DeprecatedIsStillUsed")
	public HGEngine(
			final String pRepository, final String pRoot, final Path pTarget,
			final List<String> pRevisions) throws NullPointerException,
			IllegalArgumentException {
		super(pRepository, pRoot,pTarget, pRevisions);
	}

	///////////////////////// Validation and mapping //////////////////////////

	@Override
	@SuppressWarnings("Duplicates")
	protected String validateMapRepository(final String pRepository) {
		Validate.notEmpty(pRepository);
		Validate.isTrue(SUPPORTED_PROTOCOLS.test(pRepository),
				"Unsupported protocol: '%s'", pRepository);
		if (FILE_PROTOCOL.test(pRepository)) {
			final String repository = pRepository.substring(7);
			final Path path = Paths.get(repository).toAbsolutePath();
			IllegalRepositoryException.isTrue(Files.exists(path),
					"'%s' does not exist", pRepository);
			IllegalRepositoryException.isTrue(Files.isDirectory(path),
					"'%s' is not a directory", pRepository);
			IllegalRepositoryException.isTrue(Files.isReadable(path),
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
		IllegalTargetException.isTrue(!Files.exists(pTarget),
				"'%s' already exists", pTarget);
		IllegalTargetException.isTrue(Files.isWritable(pTarget.getParent()),
				"Parent of '%s' is not writable", pTarget);
		return pTarget.toAbsolutePath();
	}

	@Override
	protected List<String> validateMapRevisions(final List<String> pRevisions) {
		return Validate.noNullElements(pRevisions).stream()
				.peek(r -> {
					if (isInteger(r)) {
						final int rev = Integer.parseInt(r);
						IllegalRevisionException.isTrue(rev >= 0,
								"'%d' is not a valid changeset number", rev);
					} else {
						IllegalRevisionException.isTrue(
								r.matches("[0-9a-f]{12,40}"),
								"'%s' is not a valid changeset id", r);
					}
				})
				.collect(Collectors.toList());
	}

	@Override
	protected LocalDateTime validateMapDateTime(final LocalDateTime pDateTime) {
		return Validate.notNull(pDateTime);
	}

	@Override
	protected String validateMapIntervalRevision(final String pRevision) {
		return pRevision == null ? "" : validateMapRevisions(
				Collections.singletonList(pRevision)).get(0);
	}

	////////////////////////////////// Utils //////////////////////////////////

	private String toAbsolutePath(final String pPath) {
		return getTarget().resolve(pPath).toString();
	}

	///////////////////////////////////////////////////////////////////////////

	@Override
	public Path getOutput() {
		return getTarget().resolve(getRoot());
	}

	@Override
	protected void checkoutImpl(final String pRevision) throws IOException {
		Validate.validState(repository != null);
		try {
			UpdateCommandFlags.on(repository).rev(pRevision).execute();
		} catch (final RuntimeException e) {
			throw new IOException(e);
		}
	}

	@Override
	protected Changes createChangesImpl(
			final String pFromRev, final String pToRev)
			throws IOException {
		Validate.validState(repository != null);

		final StatusResult result;
		try {
			final StatusCommand cmd = StatusCommandFlags.on(repository);
			result = cmd.rev(pFromRev, pToRev).execute();
		} catch (final RuntimeException e) {
			throw new IOException(e);
		}
		Validate.validState(result.getClean().isEmpty());
		Validate.validState(result.getIgnored().isEmpty());
		Validate.validState(result.getMissing().isEmpty());
		Validate.validState(result.getUnknown().isEmpty());

		final Changes changes = new Changes();
		result.getAdded().stream()
				.filter(p -> normalizePath(p).startsWith(getRoot()))
				.map(this::toAbsolutePath)
				.forEach(changes.getAdded()::add);
		result.getCopied().entrySet().stream()
				.map(Map.Entry::getValue)
				.filter(p -> normalizePath(p).startsWith(getRoot()))
				.map(this::toAbsolutePath)
				.forEach(changes.getAdded()::add);
		result.getRemoved().stream()
				.filter(p -> normalizePath(p).startsWith(getRoot()))
				.map(this::toAbsolutePath)
				.forEach(changes.getRemoved()::add);
		result.getModified().stream()
				.filter(p -> normalizePath(p).startsWith(getRoot()))
				.map(this::toAbsolutePath)
				.forEach(changes.getModified()::add);
		return changes;
	}

	@Override
	protected byte[] readAllBytesImpl(
			final String pPath, final String pRevision)
			throws IOException {
		Validate.validState(repository != null);

		final InputStream is;
		try {
			final CatCommand cmd = CatCommandFlags.on(repository);
			is = cmd.rev(pRevision).execute(pPath);
		} catch (final RuntimeException e) {
			throw new IOException(e);
		}

		final BufferedInputStream bis = new BufferedInputStream(is);
		final ByteArrayOutputStream bos = new ByteArrayOutputStream();
		final byte[] buffer = new byte[4096];
		int n;
		while ((n = bis.read(buffer, 0, buffer.length)) != -1) {
			bos.write(buffer, 0, n);
		}
		return bos.toByteArray();
	}

	@Override
	protected CommitImpl createCommitImpl(final String pRevision)
			throws IOException {
		Validate.validState(repository != null);

		final List<Changeset> changes;
		try {
			final LogCommand cmd = LogCommandFlags.on(repository);
			changes = cmd.rev(pRevision).execute();
		} catch (final RuntimeException e) {
			throw new IOException(e);
		}
		Validate.validState(changes.size() == 1,
				"Unexpected number of log entries: Expected %d, Actual %d",
				1, changes.size());
		final Changeset changeset = changes.get(0);

		final CommitImpl commit = new CommitImpl();
		commit.setAuthor(changeset.getUser().replaceAll(" <.*@.*>$", ""));
		commit.setMessage(changeset.getMessage());
		final List<String> parents = new ArrayList<>();
		Stream.of(changeset.getParent1(), changeset.getParent2())
				.filter(Objects::nonNull)
				.map(Changeset::getRevision)
				.map(String::valueOf)
				.forEach(parents::add);
		commit.setParentIds(parents);
		final LocalDateTime dateTime = LocalDateTime.ofInstant(
				changeset.getTimestamp().getDate().toInstant(),
				ZoneId.systemDefault());
		commit.setDateTime(dateTime);
		return commit;
	}

	@Override
	protected List<String> listRevisionsImpl(
			final LocalDateTime pSince, final LocalDateTime pUntil)
			throws IOException {
		Validate.validState(repository != null);

		LocalDateTime xUntil = pUntil;
		if (xUntil.isAfter(MAX_DATETIME)) {
			xUntil = MAX_DATETIME;
		}

		final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
		final String since = formatter.format(pSince);
		final String until = formatter.format(xUntil);
		final String date = since + " to " + until;

		// Keep in mind that 'hg log' returns changesets in the following
		// order: [n, n-1, ..., 0] (or corresponding changeset id)

		final List<String> revisions;
		try {
			revisions = LogCommandFlags.on(repository)
					.date(date)
					.execute()
					.stream()
					.filter(cs -> Streams.concat(
								cs.getAddedFiles().stream(),
								cs.getDeletedFiles().stream(),
								cs.getModifiedFiles().stream())
							.anyMatch(p -> p.startsWith(getRoot())))
					.map(Changeset::getRevision)
					.map(String::valueOf)
					.collect(Collectors.toList());
		} catch (final RuntimeException e) {
			throw new IOException(e);
		}

		Collections.reverse(revisions);
		return revisions;
	}

	@Override
	protected List<String> listRevisionsImpl(
			final String pFromRev, final String pToRev)
			throws IOException {
		Validate.validState(repository != null);

		final boolean fromIsInteger = isInteger(pFromRev);
		final boolean toIsInteger = isInteger(pToRev);

		// Keep in mind that 'hg log' returns changesets in the following
		// order: [n, n-1, ..., 0] (or corresponding changeset id)

		final List<String> revisions = new ArrayList<>();
		try {
			List<Changeset> changesets = LogCommandFlags.on(repository)
					.execute()
					.stream()
					.filter(cs -> Streams.concat(
								cs.getAddedFiles().stream(),
								cs.getDeletedFiles().stream(),
								cs.getModifiedFiles().stream())
							.anyMatch(p -> p.startsWith(getRoot())))
					.collect(Collectors.toList());

			// The following code does not fail if `pFromRev` > `pToRev`, but
			// the resulting list will be empty.

			// If `pToRev` is empty, we assume latest changeset.
			boolean include = pToRev.isEmpty();
			for (final Changeset cs : changesets) {
				final String revNumber = String.valueOf(cs.getRevision());
				final String revId = cs.getNode();
				if (!include && (
						// changeset number
						// https://www.mercurial-scm.org/wiki/RevisionNumber
						toIsInteger && revNumber.equals(pToRev)
						||
						// changeset id
						// https://www.mercurial-scm.org/wiki/ChangeSetID
						!toIsInteger && revId.startsWith(pToRev))) {
					include = true;
				}
				if (include) {
					revisions.add(revNumber);
				}
				// Likewise, compare number and id.
				if (fromIsInteger && revNumber.equals(pFromRev)
					||
					!fromIsInteger && revId.startsWith(pFromRev)) {
					break;
				}
			}
		} catch (final RuntimeException e) {
			throw new IOException(e);
		}
		Collections.reverse(revisions);
		return revisions;
	}

	@Override
	protected void initImpl() throws IOException {
		Validate.validState(repository == null);
		try {
			repository = Repository.clone(
					getTarget().toFile(), getRepository());
		} catch (final RuntimeException e) {
			throw new IOException(e);
		}
	}
}
