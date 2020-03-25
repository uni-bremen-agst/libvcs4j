package de.unibremen.informatik.st.libvcs4j.engine;

import de.unibremen.informatik.st.libvcs4j.RevisionRange;
import de.unibremen.informatik.st.libvcs4j.VCSEngine;
import de.unibremen.informatik.st.libvcs4j.VCSEngineBuilder;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class JsonSurferTest {

	@Test
	public void createOutputDirectory() {
		final String repo = getClass()
				.getResource("/jsonsurfer/jsonsurfer.bundle")
				.getFile();

		final VCSEngine vcs = VCSEngineBuilder
				.ofGit(repo)
				.withRoot("jsurfer-core/src/main")
				.build();

		for (RevisionRange range : vcs) {
			assertTrue(range.getRevision().getOutput().toFile().exists());
		}
	}
}
