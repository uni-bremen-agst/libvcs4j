package de.unibremen.st.libvcs4j.hg;

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
import de.unibremen.st.libvcs4j.data.CommitImpl;
import de.unibremen.st.libvcs4j.engine.AbstractIntervalVCSEngine;
import de.unibremen.st.libvcs4j.engine.Changes;
import de.unibremen.st.libvcs4j.exception.IllegalIntervalException;
import de.unibremen.st.libvcs4j.exception.IllegalRepositoryException;
import de.unibremen.st.libvcs4j.exception.IllegalTargetException;
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
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class HGEngine extends AbstractIntervalVCSEngine {

	private static final Logger log = LoggerFactory.getLogger(HGEngine.class);

	private static final Predicate<String> SUPPORTED_PROTOCOLS =
			Pattern.compile("file://.*|http.*|https.*|ssh.*").asPredicate();

	private static final Predicate<String> FILE_PROTOCOL =
			Pattern.compile("file://.*").asPredicate();

	private Repository repository = null;

    public HGEngine(
    		final String pRepository, final String pRoot, final Path pTarget,
			final LocalDateTime pSince, final LocalDateTime pUntil)
			throws NullPointerException, IllegalIntervalException {
        super(parseAndValidateRepository(pRepository),
				parseAndValidateRoot(pRoot),
				parseAndValidateTarget(pTarget),
				parseAndValidateDateTime(pSince),
				parseAndValidateDateTime(pUntil));
		IllegalIntervalException.isTrue(!pSince.isAfter(pUntil),
				"Since (%s) after until (%s)", pSince, pUntil);
    }

    public HGEngine(final String pRepository, final String pRoot,
					final Path pTarget, final String pFrom, final String pTo)
			throws NullPointerException {
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

    @SuppressWarnings("Duplicates")
	private static String parseAndValidateRepository(final String pRepository) {
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

	private static String parseAndValidateRoot(final String pRoot) {
		Validate.notNull(pRoot);
		return normalizePath(pRoot);
	}

	@SuppressWarnings("Duplicates")
	private static Path parseAndValidateTarget(final Path pTarget) {
		Validate.notNull(pTarget);
		Validate.notEmpty(pTarget.toString());
		IllegalTargetException.isTrue(!Files.exists(pTarget),
				"'%s' already exists", pTarget);
		IllegalTargetException.isTrue(Files.isWritable(pTarget.getParent()),
				"Parent of '%s' is not writable", pTarget);
		return pTarget.toAbsolutePath();
	}

	private static LocalDateTime parseAndValidateDateTime(
			final LocalDateTime pDateTime) {
    	return Validate.notNull(pDateTime);
	}

	private static String parseAndValidateRevision(final String pRevision) {
		Validate.notEmpty(pRevision);
		try {
			int revision = Integer.parseInt(pRevision);
			if (revision < 0) {
				log.debug("Mapping revision {} to {}", revision, 0);
				revision = 0;
			}
			return String.valueOf(revision);
		} catch (final NumberFormatException e) {
			IllegalIntervalException.isTrue(false,
					"'%s' is not a valid mercurial revision", pRevision);
			return null; // just for the compiler
		}
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
        UpdateCommandFlags.on(repository).rev(pRevision).execute();
    }

    @Override
    protected Changes createChangesImpl(
    		final String pFromRev, final String pToRev)
			throws IOException {
		Validate.validState(repository != null);
		final StatusCommand cmd = StatusCommandFlags.on(repository);
		final StatusResult result = cmd.rev(pFromRev, pToRev).execute();

		final Changes changes = new Changes();
		result.getAdded().stream()
				.map(this::toAbsolutePath)
				.forEach(changes.getAdded()::add);
		result.getCopied().forEach((__, c) ->
				changes.getAdded().add(toAbsolutePath(c)));
		result.getRemoved().stream()
				.map(this::toAbsolutePath)
				.forEach(changes.getRemoved()::add);
		result.getModified().stream()
				.map(this::toAbsolutePath)
				.forEach(changes.getModified()::add);

		Validate.validState(result.getClean().isEmpty());
		Validate.validState(result.getIgnored().isEmpty());
		Validate.validState(result.getMissing().isEmpty());
		Validate.validState(result.getUnknown().isEmpty());
		return changes;
    }

    @Override
    protected byte[] readAllBytesImpl(
    		final String pPath, final String pRevision)
			throws IOException {
		Validate.validState(repository != null);
        final CatCommand cmd = CatCommandFlags.on(repository);
        final InputStream is = cmd.rev(pRevision).execute(pPath);

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
        final LogCommand cmd = LogCommandFlags.on(repository);
        final List<Changeset> changes = cmd.rev(pRevision).execute();
        Validate.validState(changes.size() == 1,
				"Unexpected number of log entries: Expected %d, Actual %d",
				1, changes.size());
        final Changeset cs = changes.get(0);

		final CommitImpl commit = new CommitImpl();
		commit.setAuthor(cs.getUser());
		commit.setMessage(cs.getMessage());
		final List<String> parents = new ArrayList<>();
		if (cs.getParent1() != null) {
			parents.add(String.valueOf(cs.getParent1().getRevision()));
		}
		if (cs.getParent2() != null) {
			parents.add(String.valueOf(cs.getParent2().getRevision()));
		}
		commit.setParentIds(parents);
		final LocalDateTime dateTime = LocalDateTime.ofInstant(
				cs.getTimestamp().getDate().toInstant(),
				ZoneId.systemDefault());
		commit.setDateTime(dateTime);
		return commit;
    }

    @Override
    protected List<String> listRevisionsImpl(
    		final LocalDateTime pSince, final LocalDateTime pUntil)
			throws IOException {
		Validate.validState(repository != null);
		final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
    	final String since = formatter.format(pSince);
    	final String until = formatter.format(pUntil);
    	final String date = since + " to " + until;
        final List<String> revisions = LogCommandFlags.on(repository)
				.date(date)
				.execute()
				.stream()
				.map(Changeset::getRevision)
				.map(String::valueOf)
				.collect(Collectors.toList());
		Collections.reverse(revisions);
		return revisions;
	}

    @Override
    protected List<String> listRevisionsImpl(
    		final String pFromRev, final String pToRev)
			throws IOException {
		Validate.validState(repository != null);
		final List<String> revisions = LogCommandFlags.on(repository)
				.rev(pFromRev, pToRev)
				.execute()
				.stream()
				.map(Changeset::getRevision)
				.map(String::valueOf)
				.collect(Collectors.toList());
		Collections.reverse(revisions);
		return revisions;
    }

	@Override
	protected void initImpl() throws IOException {
    	Validate.validState(repository == null);
		repository = Repository.clone(getTarget().toFile(), getRepository());
	}
}
