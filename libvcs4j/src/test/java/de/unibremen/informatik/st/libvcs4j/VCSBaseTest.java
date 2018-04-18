package de.unibremen.informatik.st.libvcs4j;

import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.ExpectedException;

import java.nio.file.Files;
import java.nio.file.Path;

public abstract class VCSBaseTest {

	public Path input;

	public Path output;

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Before
	public void setUp() throws Exception {
		final Extractor extractor = new Extractor();
		input = extractor.extractTarGZResource(getTarGZFile());
		input = input.resolve(getFolderInTarGZ());
		// create a random path that does not exist
		output = Files.createTempDirectory(null);
		Files.delete(output);
	}

	public abstract String getTarGZFile();

	public abstract String getFolderInTarGZ();
}
