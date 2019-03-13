package de.unibremen.informatik.st.libvcs4j.filesystem;

import de.unibremen.informatik.st.libvcs4j.LineInfo;
import de.unibremen.informatik.st.libvcs4j.VCSEngineBuilder;
import de.unibremen.informatik.st.libvcs4j.VCSFile;
import de.unibremen.informatik.st.libvcs4j.Validate;
import de.unibremen.informatik.st.libvcs4j.data.CommitImpl;
import de.unibremen.informatik.st.libvcs4j.data.LineInfoImpl;
import de.unibremen.informatik.st.libvcs4j.engine.AbstractVSCEngine;
import de.unibremen.informatik.st.libvcs4j.engine.Changes;
import de.unibremen.informatik.st.libvcs4j.exception.IllegalRepositoryException;
import de.unibremen.informatik.st.libvcs4j.exception.IllegalTargetException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Marcel Steinbeck
 */
public class SingleEngine extends AbstractVSCEngine {

	private static final String DEFAULT_REVISION = "0";

	private static final String DEFAULT_MESSAGE = "(no log)";

	private static final String DEFAULT_AUTHOR = "(no author)";

	/**
	 * Use {@link VCSEngineBuilder} instead.
	 */
	@Deprecated
	@SuppressWarnings("DeprecatedIsStillUsed")
	public SingleEngine(final Path pPath) throws NullPointerException,
			IllegalArgumentException {
		super(pPath.toString(), "", pPath);
	}

	///////////////////////// Validation and mapping //////////////////////////

	@Override
	protected String validateMapRepository(final String pRepository) {
		Validate.notNull(pRepository);
		final Path path = Paths.get(pRepository);
		IllegalRepositoryException.isTrue(Files.exists(path),
				"'%s' does not exist", path);
		IllegalRepositoryException.isTrue(
				Files.isReadable(path),
				"'%s' is not readable", path);
		return path.toAbsolutePath().toString();
	}

	@Override
	protected String validateMapRoot(final String pRoot) {
		return pRoot;
	}

	@Override
	protected Path validateMapTarget(final Path pTarget) {
		try {
			return Paths.get(validateMapRepository(pTarget.toString()));
		} catch (IllegalRepositoryException e) {
			IllegalTargetException.isTrue(false, e.getMessage());
			return null; // just for the compiler
		}
	}

	@Override
	protected List<String> validateMapRevisions(
			final List<String> pRevisions) {
		Validate.fail("This method should not have been called");
		return null; // just for the compiler
	}

	///////////////////////////////////////////////////////////////////////////

	@Override
	public Path getOutput() {
		return getTarget();
	}

	@Override
	public List<String> listRevisionsImpl() {
		return Collections.singletonList(DEFAULT_REVISION);
	}

	@Override
	protected void checkoutImpl(final String revision) {}

	@Override
	protected Changes createChangesImpl(final String fromRev,
			final String toRev) throws IllegalStateException {
		throw new IllegalStateException(
				"This method should not have been called");
	}

	@Override
	protected byte[] readAllBytesImpl(final String pPath,
			final String pRevision) throws IOException {
		final Path path = getOutput().resolve(pPath);
		Validate.isTrue(Files.isRegularFile(path),
				"'%s' is not a regular file", path);
		return Files.readAllBytes(path);
	}

	@Override
	public List<LineInfo> readLineInfoImpl(final VCSFile pFile)
			throws IOException {
		final Path path = pFile.toPath();
		Validate.isTrue(Files.isRegularFile(path),
				"'%s' is not a regular file", path);
		final List<LineInfo> lineInfo = new ArrayList<>();
		final List<String> lines = pFile.readLinesWithEOL();
		for (int i = 0; i < lines.size(); i++) {
			final LocalDateTime dt = LocalDateTime.ofInstant(
					Files.getLastModifiedTime(pFile.toPath()).toInstant(),
					ZoneId.systemDefault());
			final LineInfo info = new LineInfoImpl(
					DEFAULT_REVISION,
					DEFAULT_AUTHOR,
					DEFAULT_MESSAGE,
					dt,
					i + 1,
					lines.get(i).replaceAll("\r\n$|\n$", ""),
					pFile);
			lineInfo.add(info);
		}
		return lineInfo;
	}

	@Override
	protected CommitImpl createCommitImpl(final String pRevision)
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
