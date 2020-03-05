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

public class EnvironmentBuilderBladeTest {

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();
	private Path repository;
	private Path target;

	@Before
	public void setUp() throws IOException {
		ResourceExtractor extractor = new ResourceExtractor();
		repository = extractor
				.extractTarGZ("blade/blade.tar.gz")
				.resolve("blade");
		target = Paths.get(folder.getRoot().getAbsolutePath(), "target");
	}

	@Test
	public void nullPointerInUpdateFrom202To204() throws BuildException {
		VCSEngine engine = VCSEngineBuilder
				.ofGit(repository.toString())
				.withRoot("src/main/java")
				.withTarget(target.toAbsolutePath())
				.withStartIdx(202)
				.withEndIdx(204)
				.build();

		EnvironmentBuilder builder = new EnvironmentBuilder();
		for (RevisionRange range : engine) {
			assertThat(builder.update(range).getCtModel()).isNotNull();
		}
	}
}
