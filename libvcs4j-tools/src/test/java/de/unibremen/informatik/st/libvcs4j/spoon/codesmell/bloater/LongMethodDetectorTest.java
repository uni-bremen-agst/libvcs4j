package de.unibremen.informatik.st.libvcs4j.spoon.codesmell.bloater;

import de.unibremen.informatik.st.libvcs4j.spoon.codesmell.CodeSmell;
import de.unibremen.informatik.st.libvcs4j.spoon.codesmell.RevisionMock;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import spoon.Launcher;
import spoon.reflect.CtModel;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class LongMethodDetectorTest {

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	@Test
	public void test001() throws IOException {
		RevisionMock revision = new RevisionMock(folder);
		revision.addFile(Paths.get("long_method",
				"LongMethodWithComments.java"));

		Launcher launcher = new Launcher();
		launcher.addInputResource(folder.getRoot().getAbsolutePath());
		CtModel model = launcher.buildModel();

		LongMethodDetector lmDetector = new LongMethodDetector(revision, 4);
		lmDetector.scan(model);

		List<CodeSmell> codeSmells = lmDetector.getCodeSmells();

		assertThat(codeSmells).hasSize(1);
		CodeSmell codeSmell = codeSmells.get(0);
		assertThat(codeSmell.getMetrics()).containsOnlyOnce(
				lmDetector.createMetric(4));
		assertThat(codeSmell.getRanges()).hasSize(1).first()
				.matches(range -> range.getBegin().getLine() == 3)
				.matches(range -> range.getBegin().getColumn() == 5)
				.matches(range -> range.getEnd().getLine() == 18)
				.matches(range -> range.getEnd().getColumn() == 5);
	}
}
