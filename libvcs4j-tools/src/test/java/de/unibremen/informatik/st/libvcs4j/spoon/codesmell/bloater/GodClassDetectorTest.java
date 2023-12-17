package de.unibremen.informatik.st.libvcs4j.spoon.codesmell.bloater;

import de.unibremen.informatik.st.libvcs4j.RevisionRange;
import de.unibremen.informatik.st.libvcs4j.spoon.Environment;
import de.unibremen.informatik.st.libvcs4j.spoon.codesmell.CodeSmell;
import de.unibremen.informatik.st.libvcs4j.spoon.codesmell.RevisionMock;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import spoon.Launcher;
import spoon.reflect.CtModel;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Paths;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GodClassDetectorTest {

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	@Test
	public void test001() throws IOException {
		RevisionMock revision = new RevisionMock(folder);
		revision.addFile(Paths.get("godclass", "GodClass.java"));

		RevisionRange revisionRange = mock(RevisionRange.class);
		when(revisionRange.getCurrent()).thenReturn(revision);

		Launcher launcher = new Launcher();
		launcher.addInputResource(folder.getRoot().getAbsolutePath());
		CtModel model = launcher.buildModel();

		Environment environment = new Environment(model, revisionRange);

		GodClassDetector gcDetector = new GodClassDetector(environment,
				5, 15, 5, new BigDecimal("0.6"));
		gcDetector.scan(model);
		List<CodeSmell> codeSmells = gcDetector.getCodeSmells();

		assertThat(codeSmells).hasSize(1);
		CodeSmell codeSmell = codeSmells.get(0);
		assertThat(codeSmell.getMetrics()).containsOnlyOnce(
				gcDetector.createNOAMetric(5),
				gcDetector.createWMCMetric(15),
				gcDetector.createATFDMetric(5),
				gcDetector.createTCCMetric(new BigDecimal("0.6")));
		assertThat(codeSmell.getRanges()).hasSize(1).first()
				.matches(range -> range.getBegin().getLine() == 2)
				.matches(range -> range.getBegin().getColumn() == 9)
				.matches(range -> range.getEnd().getLine() == 66)
				.matches(range -> range.getEnd().getColumn() == 1);
	}
}
