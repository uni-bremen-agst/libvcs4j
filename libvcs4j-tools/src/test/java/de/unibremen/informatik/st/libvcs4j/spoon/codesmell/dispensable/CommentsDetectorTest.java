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
import java.math.BigDecimal;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CommentsDetectorTest {

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	@Test
	public void classWithSingleMethod() throws IOException {
		RevisionMock revision = new RevisionMock(folder);
		revision.addFile(Paths.get("comments", "Comment.java"));

		RevisionRange revisionRange = mock(RevisionRange.class);
		when(revisionRange.getRevision()).thenReturn(revision);

		Launcher launcher = new Launcher();
		launcher.addInputResource(folder.getRoot().getAbsolutePath());
		CtModel model = launcher.buildModel();

		Environment environment = new Environment(model, revisionRange);

		CommentsDetector cmDetector = new CommentsDetector(
				environment, 2, BigDecimal.valueOf(0.4));
		cmDetector.scan(model);
		List<CodeSmell> codeSmells = cmDetector.getCodeSmells();

		assertThat(codeSmells).hasSize(1);
		CodeSmell codeSmell = codeSmells.get(0);
		assertThat(codeSmell.getMetrics()).containsOnlyOnce(
				cmDetector.createLocMetric(2),
				cmDetector.createRatioMetric(new BigDecimal("0.5")));
		assertThat(codeSmell.getRanges()).hasSize(1).first()
				.matches(range -> range.getBegin().getLine() == 3)
				.matches(range -> range.getBegin().getColumn() == 5)
				.matches(range -> range.getEnd().getLine() == 10)
				.matches(range -> range.getEnd().getColumn() == 5);
	}

	@Test
	public void testTwice() throws IOException {
		RevisionMock revision = new RevisionMock(folder);
		revision.addFile(Paths.get("comments", "Comments.java"));

		RevisionRange revisionRange = mock(RevisionRange.class);
		when(revisionRange.getRevision()).thenReturn(revision);

		Launcher launcher = new Launcher();
		launcher.addInputResource(folder.getRoot().getAbsolutePath());
		CtModel model = launcher.buildModel();

		Environment environment = new Environment(model, revisionRange);

		CommentsDetector cmDetector = new CommentsDetector(environment,
				5,
				BigDecimal.valueOf(0.4));
		cmDetector.scan(model);
		List<CodeSmell> codeSmells = cmDetector.getCodeSmells();

		assertThat(codeSmells).hasSize(1);
		CodeSmell codeSmell = codeSmells.get(0);
		assertThat(codeSmell.getSignature())
				.isEqualTo(Optional.of("Comments#method(java.lang.String)"));
		VCSFile.Range range = codeSmell.getRanges().get(0);
		assertThat(range.getBegin().getLine()).isEqualTo(23);
		assertThat(range.getBegin().getColumn()).isEqualTo(5);
		assertThat(range.getEnd().getLine()).isEqualTo(47);
		assertThat(range.getEnd().getColumn()).isEqualTo(5);
	}
}
