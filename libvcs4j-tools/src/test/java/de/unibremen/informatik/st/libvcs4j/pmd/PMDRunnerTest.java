package de.unibremen.informatik.st.libvcs4j.pmd;

import de.unibremen.informatik.st.libvcs4j.VCSFile;
import de.unibremen.informatik.st.libvcs4j.spoon.codesmell.RevisionMock;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;
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

		PMDRunner pmdRunner = new PMDRunner(
				"category/java/errorprone.xml/EmptyIfStmt");
		PMDDetectionResult result = pmdRunner.analyze(revision);
		assertThat(result.violationsOf(file)).hasSize(2);

		PMDViolation v1 = result.violationsOf(file).get(0);
		assertThat(v1.getRange().getBegin().getLine()).isEqualTo(29);
		assertThat(v1.getRange().getBegin().getColumn()).isEqualTo(40);
		assertThat(v1.getRange().getEnd().getLine()).isEqualTo(29);
		assertThat(v1.getRange().getEnd().getColumn()).isEqualTo(41);
		assertThat(v1.getRange().readContent()).isEqualTo("{}");

		PMDViolation v2 = result.violationsOf(file).get(1);
		assertThat(v2.getRange().getBegin().getLine()).isEqualTo(33);
		assertThat(v2.getRange().getBegin().getColumn()).isEqualTo(53);
		assertThat(v2.getRange().getEnd().getLine()).isEqualTo(33);
		assertThat(v2.getRange().getEnd().getColumn()).isEqualTo(54);
		assertThat(v2.getRange().readContent()).isEqualTo("{}");
	}

	@Test
	public void defaultCategories() throws IOException {
		RevisionMock revision = new RevisionMock(folder);
		revision.addFile(Paths.get("godclass", "GodClass.java"));

		PMDRunner pmdRunner = new PMDRunner();
		PMDDetectionResult result = pmdRunner.analyze(revision);
		assertThat(result).isNotNull();
	}
}
