package de.unibremen.informatik.st.libvcs4j;

import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PositionTest {

	@Test
	public void applyFileChange() throws IOException {
		Revision oldRevision = mock(Revision.class);
		when(oldRevision.getId()).thenReturn("1");

		VCSFile oldFile = mock(VCSFile.class);
		when(oldFile.getRevision()).thenReturn(oldRevision);
		when(oldFile.getRelativePath()).thenReturn("A.java");
		when(oldFile.readLinesWithEOL()).thenReturn(Arrays.asList(
				"first line\n",
				"second line\n",
				"third line"));
		when(oldFile.positionOf(2, 8, 4)).thenCallRealMethod();

		Revision newRevision = mock(Revision.class);
		when(newRevision.getId()).thenReturn("2");

		VCSFile newFile = mock(VCSFile.class);
		when(newFile.getRevision()).thenReturn(newRevision);
		when(newFile.getRelativePath()).thenReturn("A.java");
		when(newFile.readLinesWithEOL()).thenReturn(Arrays.asList(
				"first line\n",
				"new line\n",
				"second line"));
		when(newFile.positionOf(anyInt(), anyInt(), anyInt()))
				.thenCallRealMethod();

		LineChange l1 = mock(LineChange.class);
		when(l1.getType()).thenReturn(LineChange.Type.INSERT);
		when(l1.getLine()).thenReturn(2);

		LineChange l2 = mock(LineChange.class);
		when(l2.getType()).thenReturn(LineChange.Type.DELETE);
		when(l2.getLine()).thenReturn(3);

		FileChange fileChange = mock(FileChange.class);
		when(fileChange.getOldFile()).thenReturn(Optional.of(oldFile));
		when(fileChange.getNewFile()).thenReturn(Optional.of(newFile));
		when(fileChange.computeDiff()).thenReturn(Arrays.asList(l1, l2));

		VCSFile.Position oldPosition = oldFile.positionOf(2, 8, 4)
				.orElseThrow(AssertionError::new);
		VCSFile.Position newPosition = oldPosition.apply(fileChange)
				.orElseThrow(AssertionError::new);

		assertThat(newPosition.getFile()).isSameAs(newFile);
		assertThat(newPosition.getLine()).isEqualTo(3);
		assertThat(newPosition.getColumn()).isEqualTo(8);
		assertThat(newPosition.getOffset()).isEqualTo(27);
		assertThat(newPosition.getTabSize()).isEqualTo(4);
	}

	@Test
	public void nextLine() throws IOException {
		List<String> lines = Arrays.asList("foo", "bar");
		List<String> linesEOL = Arrays.asList("foo\n", "bar");

		VCSFile file = mock(VCSFile.class);
		when(file.readLines()).thenReturn(lines);
		when(file.readLinesWithEOL()).thenReturn(linesEOL);
		when(file.positionOf(1, 2, 3)).thenCallRealMethod();
		when(file.positionOf(2, 1, 3)).thenCallRealMethod();

		VCSFile.Position position = file.positionOf(1, 2, 3)
				.orElseThrow(AssertionError::new);
		VCSFile.Position next = position.nextLine()
				.orElseThrow(AssertionError::new);
		assertThat(next.getLine()).isEqualTo(2);
		assertThat(next.getColumn()).isEqualTo(1);
		assertThat(next.getTabSize()).isEqualTo(3);
		assertThat(next.getOffset()).isEqualTo(4);
	}

	@Test
	public void previousLine() throws IOException {
		List<String> lines = Arrays.asList("foo", "bar");
		List<String> linesEOL = Arrays.asList("foo\n", "bar");

		VCSFile file = mock(VCSFile.class);
		when(file.readLines()).thenReturn(lines);
		when(file.readLinesWithEOL()).thenReturn(linesEOL);
		when(file.positionOf(2, 2, 3)).thenCallRealMethod();
		when(file.positionOf(1, 1, 3)).thenCallRealMethod();

		VCSFile.Position position = file.positionOf(2, 2, 3)
				.orElseThrow(AssertionError::new);
		VCSFile.Position previous = position.previousLine()
				.orElseThrow(AssertionError::new);
		assertThat(previous.getLine()).isEqualTo(1);
		assertThat(previous.getColumn()).isEqualTo(1);
		assertThat(previous.getTabSize()).isEqualTo(3);
		assertThat(previous.getOffset()).isEqualTo(0);
	}

	@Test
	public void beginOfLine() throws IOException {
		List<String> lines = Collections.singletonList(
				"lorem ipsum dolor sit");

		VCSFile file = mock(VCSFile.class);
		when(file.readLines()).thenReturn(lines);
		when(file.readLinesWithEOL()).thenReturn(lines);
		when(file.positionOf(1, 7, 8)).thenCallRealMethod();
		when(file.positionOf(1, 1, 8)).thenCallRealMethod();

		VCSFile.Position position = file.positionOf(1, 7, 8)
				.orElseThrow(AssertionError::new);
		VCSFile.Position beginOfLine = position.beginOfLine();
		assertThat(beginOfLine.getLine()).isEqualTo(1);
		assertThat(beginOfLine.getColumn()).isEqualTo(1);
		assertThat(beginOfLine.getTabSize()).isEqualTo(8);
		assertThat(beginOfLine.getOffset()).isEqualTo(0);
	}

	@Test
	public void endOfLine() throws IOException {
		List<String> lines = Collections.singletonList(
				"lorem ipsum dolor sit");

		VCSFile file = mock(VCSFile.class);
		when(file.readLines()).thenReturn(lines);
		when(file.readLinesWithEOL()).thenReturn(lines);
		when(file.positionOf(1, 9, 7)).thenCallRealMethod();
		when(file.positionOf(1, 21, 7)).thenCallRealMethod();

		VCSFile.Position position = file.positionOf(1, 9, 7)
				.orElseThrow(AssertionError::new);
		VCSFile.Position endOfLine = position.endOfLine();
		assertThat(endOfLine.getLine()).isEqualTo(1);
		assertThat(endOfLine.getColumn()).isEqualTo(21);
		assertThat(endOfLine.getTabSize()).isEqualTo(7);
		assertThat(endOfLine.getOffset()).isEqualTo(20);
	}
}
