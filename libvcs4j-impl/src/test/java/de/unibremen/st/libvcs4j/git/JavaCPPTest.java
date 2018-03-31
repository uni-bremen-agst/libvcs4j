package de.unibremen.st.libvcs4j.git;

import de.unibremen.st.libvcs4j.VCSBaseTest;
import de.unibremen.st.libvcs4j.VCSEngine;
import de.unibremen.st.libvcs4j.Version;
import de.unibremen.st.libvcs4j.VCSEngineBuilder;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertFalse;

@Ignore
public class JavaCPPTest extends VCSBaseTest {

	@Override
	public String getTarGZFile() {
		return "javacpp.tar.gz";
	}

	@Override
	public String getFolderInTarGZ() {
		return "javacpp";
	}

	private VCSEngine createProvider(
			final String pRoot) {
		return VCSEngineBuilder
				.of(input.toString())
				.withGit()
				.withRoot(pRoot)
				.withTarget(output.toString())
				.build();
	}

	@Test
	@Ignore
	public void testNumberOfRevisions() throws IOException {
		final VCSEngine vp = createProvider("");
		assertFalse(vp.next().isPresent());
	}

	@Test
	public void testForEachIteration() throws IOException {
		for (final Version v : createProvider("")) {}
	}

	@Test
	public void testForEachSubDir() throws IOException {
		for (final Version v : createProvider("src")) {}
	}
}
