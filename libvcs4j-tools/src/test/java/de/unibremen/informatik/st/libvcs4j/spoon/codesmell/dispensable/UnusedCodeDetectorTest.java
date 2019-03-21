package de.unibremen.informatik.st.libvcs4j.spoon.codesmell.dispensable;

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
import java.util.List;
import java.util.Optional;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.Files.readAllBytes;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UnusedCodeDetectorTest {

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	@Test
	public void test001() throws IOException {
		final File file = folder.newFile("PrivateOnly.java");
		try (FileWriter fw = new FileWriter(file)) {
			IOUtils.copy(getClass().getResourceAsStream(
					"/unused/PrivateOnly.java"), fw, UTF_8);
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
		UnusedCodeDetector dcDetector = new UnusedCodeDetector(revision);
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
