package de.unibremen.informatik.st.libvcs4j.git;

import de.unibremen.informatik.st.libvcs4j.VCSBaseTest;
import de.unibremen.informatik.st.libvcs4j.VCSEngineBuilder;
import de.unibremen.informatik.st.libvcs4j.exception.IllegalRepositoryException;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;

@SuppressWarnings("deprecation")
public class ConstructorTest extends VCSBaseTest {

	@Override
	protected String getTarGZFile() {
		return "javacpp.tar.gz";
	}

	@Override
	protected String getFolderInTarGZ() {
		return "javacpp";
	}

	@Override
	protected void setEngine(final VCSEngineBuilder pBuilder) {
		pBuilder.withGit();
	}

	private void createEngine(final String pInput) {
		VCSEngineBuilder.ofGit(pInput).build();
	}

	@Test
	public void testUnsupportedProtocol() {
		thrown.expect(IllegalRepositoryException.class);
		createEngine("unsupported://path");
	}

	@Test
	public void regularFileRepository() throws IOException {
		Path file = Files.createTempFile(null, null);
		file.toFile().deleteOnExit();
		thrown.expect(IllegalRepositoryException.class);
		createEngine("file://" + file.toString());
	}
}
