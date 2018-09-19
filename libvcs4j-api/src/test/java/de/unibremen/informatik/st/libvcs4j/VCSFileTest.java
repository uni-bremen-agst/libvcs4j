package de.unibremen.informatik.st.libvcs4j;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class VCSFileTest {

	@Test
	public void readLinesEOLUnixEOL() throws IOException {
		VCSFile file = mock(VCSFile.class);
		when(file.readeContent()).thenReturn("first line\nsecond line\n");
		when(file.readLinesWithEOL()).thenCallRealMethod();

		List<String> lines = file.readLinesWithEOL();
		assertThat(lines)
				.hasSize(2)
				.contains("first line\n", "second line\n");
	}

	@Test
	public void readLinesEOLWindowsEOL() throws IOException {
		VCSFile file = mock(VCSFile.class);
		when(file.readeContent()).thenReturn("first line\r\nsecond line\r\n");
		when(file.readLinesWithEOL()).thenCallRealMethod();

		List<String> lines = file.readLinesWithEOL();
		assertThat(lines)
				.hasSize(2)
				.contains("first line\r\n", "second line\r\n");
	}

	@Test
	public void readLinesEOLOldMacEOL() throws IOException {
		VCSFile file = mock(VCSFile.class);
		when(file.readeContent()).thenReturn("first line\rsecond line\r");
		when(file.readLinesWithEOL()).thenCallRealMethod();

		List<String> lines = file.readLinesWithEOL();
		assertThat(lines)
				.hasSize(2)
				.contains("first line\r", "second line\r");
	}

	@Test
	public void readLinesEOLMixedEOL() throws IOException {
		VCSFile file = mock(VCSFile.class);
		when(file.readeContent()).thenReturn("first\rsecond\r\nthird\n");
		when(file.readLinesWithEOL()).thenCallRealMethod();

		List<String> lines = file.readLinesWithEOL();
		assertThat(lines)
				.hasSize(3)
				.contains("first\r", "second\r\n", "third\n");
	}

	@Test
	public void readLinesEOLWithoutLastEOL() throws IOException {
		VCSFile file = mock(VCSFile.class);
		when(file.readeContent()).thenReturn("foo\nbar");
		when(file.readLinesWithEOL()).thenCallRealMethod();

		List<String> lines = file.readLinesWithEOL();
		assertThat(lines)
				.hasSize(2)
				.contains("foo\n", "bar");
	}

	@Test
	public void readLinesEOLEmptyContent() throws IOException {
		VCSFile file = mock(VCSFile.class);
		when(file.readeContent()).thenReturn("");
		when(file.readLinesWithEOL()).thenCallRealMethod();

		List<String> lines = file.readLinesWithEOL();
		assertThat(lines).isEmpty();
	}

	@Test
	public void readLinesMixedEOL() throws IOException {
		VCSFile file = mock(VCSFile.class);
		when(file.readeContent()).thenReturn("first\rsecond\r\nthird\n");
		when(file.readLines()).thenCallRealMethod();

		List<String> lines = file.readLines();
		assertThat(lines)
				.hasSize(3)
				.contains("first", "second", "third");
	}

	@Test
	public void isBinaryFromString() throws IOException {
		VCSFile file = mock(VCSFile.class);
		when(file.readAllBytes()).thenReturn(
				"Some arbitrary text".getBytes(StandardCharsets.UTF_8));
		when(file.toPath()).thenReturn(Paths.get("Mock.java"));
		when(file.isBinary()).thenCallRealMethod();

		assertThat(file.isBinary()).isFalse();
	}

	@Test
	public void mapOffset() throws IOException {
		VCSFile file = mock(VCSFile.class);
		when(file.readeContent()).thenReturn("first line\nsecond line");
		when(file.readLinesWithEOL()).thenCallRealMethod();
		when(file.positionOf(4, 4)).thenCallRealMethod();
		when(file.positionOf(14, 4)).thenCallRealMethod();
		when(file.positionOf(11, 4)).thenCallRealMethod();

		VCSFile.Position p1 = file.positionOf(4, 4);
		assertThat(p1.getOffset()).isEqualTo(4);
		assertThat(p1.getLine()).isEqualTo(1);
		assertThat(p1.getColumn()).isEqualTo(5);
		assertThat(p1.getTabSize()).isEqualTo(4);

		VCSFile.Position p2 = file.positionOf(14, 4);
		assertThat(p2.getOffset()).isEqualTo(14);
		assertThat(p2.getLine()).isEqualTo(2);
		assertThat(p2.getColumn()).isEqualTo(4);
		assertThat(p2.getTabSize()).isEqualTo(4);

		VCSFile.Position p3 = file.positionOf(11, 4);
		assertThat(p3.getOffset()).isEqualTo(11);
		assertThat(p3.getLine()).isEqualTo(2);
		assertThat(p3.getColumn()).isEqualTo(1);
		assertThat(p3.getTabSize()).isEqualTo(4);
	}

	@Test
	public void mapOffsetEmptyString() throws IOException {
		VCSFile file = mock(VCSFile.class);
		when(file.readeContent()).thenReturn("");
		when(file.readLinesWithEOL()).thenCallRealMethod();
		when(file.positionOf(1, 4)).thenCallRealMethod();

		assertThatExceptionOfType(IndexOutOfBoundsException.class)
				.isThrownBy(() -> file.positionOf(1, 4));
	}

	@Test
	public void mapLineAndColumn() throws IOException {
		VCSFile file = mock(VCSFile.class);
		when(file.readeContent()).thenReturn("first line\nsecond line");
		when(file.readLinesWithEOL()).thenCallRealMethod();

		when(file.positionOf(1, 6, 4)).thenCallRealMethod();
		when(file.positionOf(2, 2, 4)).thenCallRealMethod();
		when(file.positionOf(1, 11, 4)).thenCallRealMethod();
		when(file.positionOf(2, 12, 4)).thenCallRealMethod();

		VCSFile.Position p1 = file.positionOf(1, 6, 4);
		assertThat(p1.getLine()).isEqualTo(1);
		assertThat(p1.getColumn()).isEqualTo(6);
		assertThat(p1.getTabSize()).isEqualTo(4);
		assertThat(p1.getOffset()).isEqualTo(5);

		VCSFile.Position p2 = file.positionOf(2, 2, 4);
		assertThat(p2.getLine()).isEqualTo(2);
		assertThat(p2.getColumn()).isEqualTo(2);
		assertThat(p2.getTabSize()).isEqualTo(4);
		assertThat(p2.getOffset()).isEqualTo(12);

		assertThatExceptionOfType(IndexOutOfBoundsException.class)
				.isThrownBy(() -> file.positionOf(1, 11, 4));
		assertThatExceptionOfType(IndexOutOfBoundsException.class)
				.isThrownBy(() -> file.positionOf(2, 12, 4));
	}

	@Test
	public void mapLineAndColumnWithDifferentTabSizes() throws IOException {
		VCSFile file = mock(VCSFile.class);
		when(file.readeContent()).thenReturn("foo\tbar\tfoobar");
		when(file.readLinesWithEOL()).thenCallRealMethod();

		when(file.positionOf(1, 8, 4)).thenCallRealMethod();
		when(file.positionOf(1, 12, 8)).thenCallRealMethod();
		when(file.positionOf(1, 1, 1)).thenCallRealMethod();
		when(file.positionOf(1, 2, 1)).thenCallRealMethod();
		when(file.positionOf(1, 3, 1)).thenCallRealMethod();
		when(file.positionOf(1, 12, 2)).thenCallRealMethod();

		VCSFile.Position p1 = file.positionOf(1, 8, 4);
		assertThat(p1.getLine()).isEqualTo(1);
		assertThat(p1.getColumn()).isEqualTo(8);
		assertThat(p1.getTabSize()).isEqualTo(4);
		assertThat(p1.getOffset()).isEqualTo(5);

		VCSFile.Position p2 = file.positionOf(1, 12, 8);
		assertThat(p2.getLine()).isEqualTo(1);
		assertThat(p2.getColumn()).isEqualTo(12);
		assertThat(p2.getTabSize()).isEqualTo(8);
		assertThat(p2.getOffset()).isEqualTo(5);

		VCSFile.Position p3 = file.positionOf(1, 1, 1);
		assertThat(p3.getLine()).isEqualTo(1);
		assertThat(p3.getColumn()).isEqualTo(1);
		assertThat(p3.getTabSize()).isEqualTo(1);
		assertThat(p3.getOffset()).isEqualTo(0);

		VCSFile.Position p4 = file.positionOf(1, 2, 1);
		assertThat(p4.getLine()).isEqualTo(1);
		assertThat(p4.getColumn()).isEqualTo(2);
		assertThat(p4.getTabSize()).isEqualTo(1);
		assertThat(p4.getOffset()).isEqualTo(1);

		VCSFile.Position p5 = file.positionOf(1, 3, 1);
		assertThat(p5.getLine()).isEqualTo(1);
		assertThat(p5.getColumn()).isEqualTo(3);
		assertThat(p5.getTabSize()).isEqualTo(1);
		assertThat(p5.getOffset()).isEqualTo(2);

		VCSFile.Position p6 = file.positionOf(1, 12, 2);
		assertThat(p6.getLine()).isEqualTo(1);
		assertThat(p6.getColumn()).isEqualTo(12);
		assertThat(p6.getTabSize()).isEqualTo(2);
		assertThat(p6.getOffset()).isEqualTo(11);
	}
}
