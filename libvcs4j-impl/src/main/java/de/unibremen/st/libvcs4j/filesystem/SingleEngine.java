package de.unibremen.st.libvcs4j.filesystem;

import de.unibremen.st.libvcs4j.data.CommitImpl;
import de.unibremen.st.libvcs4j.engine.AbstractVSCEngine;
import de.unibremen.st.libvcs4j.engine.Changes;
import org.apache.commons.lang3.Validate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;

import static org.apache.commons.lang3.Validate.isTrue;
import static org.apache.commons.lang3.Validate.notNull;

/**
 * @author Marcel Steinbeck
 */
public class SingleEngine extends AbstractVSCEngine {

	private final static String DEFAULT_REVISION = "0";

	private final static String DEFAULT_MESSAGE = "(no log)";

	private final static String DEFAULT_AUTHOR = "(no author)";

    /**
     * Use {@link de.unibremen.st.libvcs4j.VCSEngineBuilder} instead.
     */
    @Deprecated
    @SuppressWarnings("DeprecatedIsStillUsed")
	public SingleEngine(final Path pPath) throws NullPointerException,
			IllegalArgumentException {
		super(parsePath(pPath).toString(), "", parsePath(pPath));
	}

	private static Path parsePath(final Path pPath) {
		notNull(pPath);
		isTrue(Files.exists(pPath), "'%s' does not exist", pPath);
		isTrue(Files.isReadable(pPath), "'%s' is not readable", pPath);
		return pPath.toAbsolutePath();
	}

	@Override
	public Path getOutputImpl() {
		return getTarget();
	}

	@Override
	public List<String> listRevisionsImpl() {
		return Collections.singletonList(DEFAULT_REVISION);
	}

	@Override
	protected void checkoutImpl(final String pRevision) {}

	@Override
	protected Changes createChanges(
	        final String pFrom,
            final String pTo) {
		throw new IllegalStateException(
				"This mothod should not have been called");
	}

	@Override
	protected byte[] readAllBytesImpl(
			final String pPath,
			final String pRevision)
				throws IOException {
		final Path path = getOutput().resolve(pPath);
		Validate.isTrue(Files.isRegularFile(path),
				"'%s' is not a regular file", path);
		return Files.readAllBytes(path);
	}

	@Override
	protected CommitImpl createCommitImpl(
			final String pRevision)
				throws IOException {
		final CommitImpl commit = new CommitImpl();
		commit.setId(DEFAULT_REVISION);
		commit.setAuthor(DEFAULT_AUTHOR);
		commit.setMessage(DEFAULT_MESSAGE);
		final LocalDateTime datetime = LocalDateTime.ofInstant(
				Files.getLastModifiedTime(getTarget()).toInstant(),
				ZoneId.systemDefault());
		commit.setDateTime(datetime);
		return commit;
	}
}
