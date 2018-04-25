package de.unibremen.informatik.st.libvcs4j.svn;

import de.unibremen.informatik.st.libvcs4j.VCSEngineBuilder;
import de.unibremen.informatik.st.libvcs4j.exception.IllegalRepositoryException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@SuppressWarnings("deprecation")
public class ConstructorTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private void createEngine(final String pInput) {
		VCSEngineBuilder.ofSVN(pInput).build();
	}

	@Test
	public void unsupportedProtocol() {
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
