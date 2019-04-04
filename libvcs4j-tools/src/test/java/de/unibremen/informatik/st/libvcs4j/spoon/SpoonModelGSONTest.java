package de.unibremen.informatik.st.libvcs4j.spoon;

import de.unibremen.informatik.st.libvcs4j.RevisionRange;
import de.unibremen.informatik.st.libvcs4j.VCSEngine;
import de.unibremen.informatik.st.libvcs4j.VCSEngineBuilder;
import de.unibremen.informatik.st.libvcs4j.testutils.ResourceExtractor;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

public class SpoonModelGSONTest {

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();
	private Path repository;
	private Path target;

	@Before
	public void setUp() throws IOException {
		ResourceExtractor extractor = new ResourceExtractor();
		repository = extractor
				.extractTarGZ("gson/gson.tar.gz")
				.resolve("gson");
		target = Paths.get(folder.getRoot().getAbsolutePath(), "target");
	}

	@Test
	public void nullPointerInUpdateFrom4To6() throws BuildException {
		VCSEngine engine = VCSEngineBuilder
				.ofGit(repository.toString())
				.withTarget(target.toAbsolutePath())
				.withStartIdx(4)
				.withEndIdx(6)
				.build();

		SpoonModel model = new SpoonModel();
		model.setIncremental(true);
		for (RevisionRange range : engine) {
			assertThat(model.update(range)).isNotNull();
		}
	}
}
