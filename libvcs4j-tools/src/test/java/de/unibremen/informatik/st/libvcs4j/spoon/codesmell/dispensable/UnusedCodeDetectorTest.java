package de.unibremen.informatik.st.libvcs4j.spoon.codesmell.dispensable;

import de.unibremen.informatik.st.libvcs4j.RevisionRange;
import de.unibremen.informatik.st.libvcs4j.VCSFile;
import de.unibremen.informatik.st.libvcs4j.spoon.Environment;
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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UnusedCodeDetectorTest {

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	@Test
	public void test001() throws IOException {
		RevisionMock revision = new RevisionMock(folder);
		revision.addFile(Paths.get("unused", "PrivateOnly.java"));

		RevisionRange revisionRange = mock(RevisionRange.class);
		when(revisionRange.getCurrent()).thenReturn(revision);

		Launcher launcher = new Launcher();
		launcher.addInputResource(folder.getRoot().getAbsolutePath());
		CtModel model = launcher.buildModel();

		Environment environment = new Environment(model, revisionRange);

		UnusedCodeDetector dcDetector = new UnusedCodeDetector(environment);
		dcDetector.scan(model);
		List<CodeSmell> codeSmells = dcDetector.getCodeSmells();
		codeSmells.sort((c1, c2) -> VCSFile.Range.BEGIN_COMPARATOR.compare(
				c1.getRanges().get(0), c2.getRanges().get(0)));

		assertThat(codeSmells).hasSize(5);
		assertThat(codeSmells).matches(css -> {
			assertThat(css.get(0).getRanges()).hasSize(1).first()
					.matches(range -> range.getBegin().getLine() == 5)
					.matches(range -> range.getBegin().getColumn() == 5)
					.matches(range -> range.getEnd().getLine() == 5)
					.matches(range -> range.getEnd().getColumn() == 28);
			assertThat(css.get(1).getRanges()).hasSize(1).first()
					.matches(range -> range.getBegin().getLine() == 7)
					.matches(range -> range.getBegin().getColumn() == 5)
					.matches(range -> range.getEnd().getLine() == 9)
					.matches(range -> range.getEnd().getColumn() == 5);
			assertThat(css.get(2).getRanges()).hasSize(1).first()
					.matches(range -> range.getBegin().getLine() == 8)
					.matches(range -> range.getBegin().getColumn() == 9)
					.matches(range -> range.getEnd().getLine() == 8)
					.matches(range -> range.getEnd().getColumn() == 35);
			assertThat(css.get(3).getRanges()).hasSize(1).first()
					.matches(range -> range.getBegin().getLine() == 11)
					.matches(range -> range.getBegin().getColumn() == 5)
					.matches(range -> range.getEnd().getLine() == 13)
					.matches(range -> range.getEnd().getColumn() == 5);
			assertThat(css.get(4).getRanges()).hasSize(1).first()
					.matches(range -> range.getBegin().getLine() == 11)
					.matches(range -> range.getBegin().getColumn() == 31)
					.matches(range -> range.getEnd().getLine() == 11)
					.matches(range -> range.getEnd().getColumn() == 49);
			return true;
		});
	}
}
