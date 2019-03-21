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

public class DataClassDetectorTest {

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	@Test
	public void tripleDataClass() throws IOException {
		final File file = folder.newFile("Triple.java");
		try (FileWriter fw = new FileWriter(file)) {
			IOUtils.copy(getClass().getResourceAsStream(
					"/dataclass/Triple.java"), fw, UTF_8);
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
		DataClassDetector dcDetector = new DataClassDetector(revision);
		dcDetector.scan(model);
		List<CodeSmell> codeSmells = dcDetector.getCodeSmells();

		assertThat(codeSmells).hasSize(1);
		CodeSmell codeSmell = codeSmells.get(0);
		assertThat(codeSmell.getRanges()).hasSize(1).first()
				.matches(range -> range.getBegin().getLine() == 1)
				.matches(range -> range.getBegin().getColumn() == 1)
				.matches(range -> range.getEnd().getLine() == 30)
				.matches(range -> range.getEnd().getColumn() == 1);
	}
}
