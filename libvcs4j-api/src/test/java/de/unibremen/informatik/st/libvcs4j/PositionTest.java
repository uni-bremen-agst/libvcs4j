package de.unibremen.informatik.st.libvcs4j;

import org.junit.Test;

import java.io.IOException;
import java.nio.file.Paths;
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
		when(oldFile.readLines()).thenReturn(Arrays.asList(
				"first line",
				"second line",
				"third line"));
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
		when(newFile.readLines()).thenReturn(Arrays.asList(
				"first line",
				"new line",
				"second line"));
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
		assertThat(newPosition.getLineOffset()).isEqualTo(7);
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
		assertThat(next.getLineOffset()).isEqualTo(0);
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
		assertThat(previous.getLineOffset()).isEqualTo(0);
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
		assertThat(beginOfLine.getLineOffset()).isEqualTo(0);
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
		assertThat(endOfLine.getLineOffset()).isEqualTo(20);

		lines = Collections.singletonList(
				"\t\t\t} else {");

		VCSFile file2 = mock(VCSFile.class);
		when(file2.readLines()).thenReturn(lines);
		when(file2.readLinesWithEOL()).thenReturn(lines);
		when(file2.positionOf(1, 9, 4)).thenCallRealMethod();
		when(file2.positionOf(1, 20, 4)).thenCallRealMethod();

		VCSFile.Position position2 = file.positionOf(1, 9, 4)
				.orElseThrow(AssertionError::new);
		VCSFile.Position endOfLine2 = position.endOfLine();
		assertThat(endOfLine2.getLine()).isEqualTo(1);
		assertThat(endOfLine2.getColumn()).isEqualTo(20);
		assertThat(endOfLine2.getTabSize()).isEqualTo(4);
		assertThat(endOfLine2.getOffset()).isEqualTo(19);
		assertThat(endOfLine2.getLineOffset()).isEqualTo(19);
	}

	@Test
	public void nextLineBlank() throws IOException {
		List<String> lines = Arrays.asList("some text", "");
		List<String> linesEOL = Arrays.asList("some text\n", "\n");

		VCSFile file = mock(VCSFile.class);
		when(file.readLines()).thenReturn(lines);
		when(file.readLinesWithEOL()).thenReturn(linesEOL);
		when(file.positionOf(1, 2, 4)).thenCallRealMethod();
		when(file.positionOf(2, 1, 4)).thenCallRealMethod();

		VCSFile.Position position = file.positionOf(1, 2, 4)
				.orElseThrow(AssertionError::new);
		assertThat(position.nextLine()).isEmpty();
	}

	@Test
	public void previousLineBlank() throws IOException {
		List<String> lines = Arrays.asList("", "some text");
		List<String> linesEOL = Arrays.asList("\n", "some text");

		VCSFile file = mock(VCSFile.class);
		when(file.readLines()).thenReturn(lines);
		when(file.readLinesWithEOL()).thenReturn(linesEOL);
		when(file.positionOf(2, 2, 4)).thenCallRealMethod();
		when(file.positionOf(1, 1, 4)).thenCallRealMethod();

		VCSFile.Position position = file.positionOf(2, 2, 4)
				.orElseThrow(AssertionError::new);
		assertThat(position.previousLine()).isEmpty();
	}

	@Test
	public void mapToExistingPosition() throws IOException {
		VCSFile file = mock(VCSFile.class);
		when(file.getRelativePath()).thenReturn("File.java");
		when(file.readLinesWithEOL()).thenReturn(
				Arrays.asList("some\n", "content"));

		VCSFile.Position position = mock(VCSFile.Position.class);
		when(position.getLine()).thenReturn(2);
		when(position.getColumn()).thenReturn(4);
		when(position.getOffset()).thenReturn(8);
		when(position.getLineOffset()).thenReturn(3);
		when(position.getTabSize()).thenReturn(2);
		when(position.mapTo(file)).thenCallRealMethod();

		when(file.positionOf(position.getLine(), position.getColumn(),
				position.getTabSize())).thenCallRealMethod();

		VCSFile.Position mapped = position.mapTo(file)
				.orElseThrow(AssertionError::new);
		assertThat(mapped.getFile()).isSameAs(file);
		assertThat(mapped.getLine()).isEqualTo(position.getLine());
		assertThat(mapped.getColumn()).isEqualTo(position.getColumn());
		assertThat(mapped.getOffset()).isEqualTo(position.getOffset());
		assertThat(mapped.getLineOffset()).isEqualTo(position.getLineOffset());
		assertThat(mapped.getTabSize()).isEqualTo(position.getTabSize());
	}

	@Test
	public void mapToNotExistingPosition() throws IOException {
		VCSFile file = mock(VCSFile.class);
		when(file.getRelativePath()).thenReturn("File.java");
		when(file.readLinesWithEOL()).thenReturn(
				Arrays.asList("come\n", "content"));

		VCSFile.Position position = mock(VCSFile.Position.class);
		when(position.getLine()).thenReturn(1);
		when(position.getColumn()).thenReturn(5);
		when(position.getOffset()).thenReturn(4);
		when(position.getTabSize()).thenReturn(2);
		when(position.mapTo(file)).thenCallRealMethod();

		when(file.positionOf(position.getLine(), position.getColumn(),
				position.getTabSize())).thenCallRealMethod();

		assertThat(position.mapTo(file)).isEmpty();
		verify(file, times(1)).positionOf(position.getLine(),
				position.getColumn(), position.getTabSize());
	}

	@Test
	public void relativePathPredicate() {
		VCSFile file = mock(VCSFile.class);
		when(file.toRelativePath()).thenReturn(
				Paths.get("src", "main", "File.java"));

		VCSFile.Position p1 = mock(VCSFile.Position.class);
		when(p1.getFile()).thenReturn(file);
		when(p1.getOffset()).thenReturn(1000);
		VCSFile.Position p2 = mock(VCSFile.Position.class);
		when(p2.getFile()).thenReturn(file);
		when(p2.getOffset()).thenReturn(1000, 1001, 999);

		// Only the first call matches.
		assertThat(VCSFile.Position.RELATIVE_PATH_PREDICATE
				.test(p1, p2)).isTrue();
		assertThat(VCSFile.Position.RELATIVE_PATH_PREDICATE
				.test(p1, p2)).isFalse();
		assertThat(VCSFile.Position.RELATIVE_PATH_PREDICATE
				.test(p1, p2)).isFalse();
		verify(p2, times(3)).getOffset();
	}

	@Test
	public void relativePathPredicateNullWithNull() {
		assertThat(VCSFile.Position.RELATIVE_PATH_PREDICATE.test(null, null))
				.isTrue();
	}

	@Test
	public void relativePathPredicateNullWithNonNull() {
		assertThat(VCSFile.Position.RELATIVE_PATH_PREDICATE.test(null,
				mock(VCSFile.Position.class))).isFalse();
	}

	@Test
	public void createPositionFromOffset() throws IOException {
		List<String> linesEOL = Arrays.asList("foo\n", "\tbar");

		VCSFile file = mock(VCSFile.class);
		when(file.readLinesWithEOL()).thenReturn(linesEOL);
		when(file.positionOf(6, 4)).thenCallRealMethod();

		VCSFile.Position position = file.positionOf(6, 4)
				.orElseThrow(AssertionError::new);
		assertThat(position.getLine()).isEqualTo(2);
		assertThat(position.getColumn()).isEqualTo(6);
		assertThat(position.getOffset()).isEqualTo(6);
		assertThat(position.getLineOffset()).isEqualTo(2);
		assertThat(position.getTabSize()).isEqualTo(4);
	}

	@Test
	public void readChar() throws IOException {
		String content = "31415";
		VCSFile file = mock(VCSFile.class);
		when(file.readAllBytes()).thenReturn(content.getBytes());
		when(file.readContent()).thenCallRealMethod();
		when(file.readLinesWithEOL()).thenCallRealMethod();
		when(file.positionOf(2, 4)).thenCallRealMethod();

		VCSFile.Position position = file.positionOf(2, 4)
				.orElseThrow(AssertionError::new);
		assertThat(position.readChar()).isEqualTo('4');
	}
}
