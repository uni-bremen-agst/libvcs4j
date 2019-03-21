package de.unibremen.informatik.st.libvcs4j.spoon.codesmell.bloater;

import de.unibremen.informatik.st.libvcs4j.Revision;
import de.unibremen.informatik.st.libvcs4j.VCSFile;
import de.unibremen.informatik.st.libvcs4j.spoon.codesmell.CodeSmell;
import org.apache.commons.io.IOUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import spoon.Launcher;
import spoon.reflect.CtModel;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.Files.readAllBytes;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GodClassDetectorTest {

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	@Test
	public void test001() throws IOException {
		final File file = folder.newFile("GodClass.java");
		try (FileWriter fw = new FileWriter(file)) {
			IOUtils.copy(getClass().getResourceAsStream(
					"/godclass/GodClass.java"), fw, UTF_8);
		}

		Launcher launcher = new Launcher();
		launcher.addInputResource(folder.getRoot().getAbsolutePath());
		CtModel model = launcher.buildModel();

		VCSFile vcsFile = mock(VCSFile.class);
		when(vcsFile.getRelativePath()).thenReturn(file.getName());
		when(vcsFile.getPath()).thenReturn(file.getPath());
		when(vcsFile.toFile()).thenCallRealMethod();
		when(vcsFile.readAllBytes()).thenReturn(readAllBytes(file.toPath()));
		when(vcsFile.readeContent()).thenCallRealMethod();
		when(vcsFile.readLinesWithEOL()).thenCallRealMethod();
		when(vcsFile.guessCharset()).thenReturn(Optional.of(UTF_8));
		when(vcsFile.positionOf(anyInt(), anyInt())).thenCallRealMethod();
		Revision revision = mock(Revision.class);
		when(revision.getOutput()).thenReturn(folder.getRoot().toPath());
		when(revision.getFiles()).thenReturn(singletonList(vcsFile));
		GodClassDetector gcDetector = new GodClassDetector(revision,
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
