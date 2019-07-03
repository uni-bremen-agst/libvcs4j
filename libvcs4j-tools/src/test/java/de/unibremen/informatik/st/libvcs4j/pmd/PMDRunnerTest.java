package de.unibremen.informatik.st.libvcs4j.pmd;

import de.unibremen.informatik.st.libvcs4j.VCSEngine;
import de.unibremen.informatik.st.libvcs4j.VCSEngineBuilder;
import de.unibremen.informatik.st.libvcs4j.VCSFile;
import de.unibremen.informatik.st.libvcs4j.spoon.codesmell.RevisionMock;
import de.unibremen.informatik.st.libvcs4j.testutils.ResourceExtractor;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

public class PMDRunnerTest {

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	@Test
	public void emptyIfBlocks() throws IOException {
		RevisionMock revision = new RevisionMock(folder);
		revision.addFile(Paths.get("godclass", "GodClass.java"));
		VCSFile file = revision.getFiles().get(0);

		PMDRunner pmdRunner = new PMDRunner();
		PMDDetectionResult result = pmdRunner.run(revision);
		assertThat(result.getViolationsOf(file)).hasSize(2);

		PMDViolation v1 = result.getViolationsOf(file).get(0);
		assertThat(v1.getRange().getBegin().getLine()).isEqualTo(29);
		assertThat(v1.getRange().getBegin().getColumn()).isEqualTo(40);
		assertThat(v1.getRange().getEnd().getLine()).isEqualTo(29);
		assertThat(v1.getRange().getEnd().getColumn()).isEqualTo(41);
		assertThat(v1.getRange().readContent()).isEqualTo("{}");

		PMDViolation v2 = result.getViolationsOf(file).get(1);
		assertThat(v2.getRange().getBegin().getLine()).isEqualTo(33);
		assertThat(v2.getRange().getBegin().getColumn()).isEqualTo(53);
		assertThat(v2.getRange().getEnd().getLine()).isEqualTo(33);
		assertThat(v2.getRange().getEnd().getColumn()).isEqualTo(54);
		assertThat(v2.getRange().readContent()).isEqualTo("{}");
	}

	@Test
	public void bladeRevision10To13() throws IOException {
		ResourceExtractor extractor = new ResourceExtractor();
		Path repository = extractor
				.extractTarGZ("blade/blade.tar.gz")
				.resolve("blade");

		VCSEngine vcs = VCSEngineBuilder
				.ofGit(repository.toString())
				.withStartIdx(10)
				.withEndIdx(13)
				.build();
		String r10 = "5f8c9dc42fcda237aaf5db6a3c0febf6752a8382";
		String r11 = "9a00007572ae142202b9f3db81582961950fdea5";
		String r12 = "7571c0ef1671bdb338dd501711d0f3c9ab682aa7";

		PMDRunner runner = new PMDRunner();
		PMDDetectionResult result = runner.run(vcs);
		assertThat(result.getRevisions())
				.containsExactlyInAnyOrder(r10, r11, r12);
		assertThat(result.getViolationsOf(r10)).hasSize(121);
		assertThat(result.getViolationsOf(r11)).hasSize(121);
		assertThat(result.getViolationsOf(r12)).hasSize(121);
	}
}
