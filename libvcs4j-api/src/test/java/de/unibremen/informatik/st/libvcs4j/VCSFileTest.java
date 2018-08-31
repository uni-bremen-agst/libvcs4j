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
		when(file.mapOffset(4, 4)).thenCallRealMethod();
		when(file.mapOffset(14, 4)).thenCallRealMethod();
		when(file.mapOffset(11, 4)).thenCallRealMethod();

		VCSFile.Position p1 = file.mapOffset(4, 4);
		assertThat(p1.getLine()).isEqualTo(1);
		assertThat(p1.getColumn()).isEqualTo(5);
		assertThat(p1.getTabSize()).isEqualTo(4);

		VCSFile.Position p2 = file.mapOffset(14, 4);
		assertThat(p2.getLine()).isEqualTo(2);
		assertThat(p2.getColumn()).isEqualTo(4);
		assertThat(p2.getTabSize()).isEqualTo(4);

		VCSFile.Position p3 = file.mapOffset(11, 4);
		assertThat(p3.getLine()).isEqualTo(2);
		assertThat(p3.getColumn()).isEqualTo(1);
		assertThat(p3.getTabSize()).isEqualTo(4);
	}

	@Test
	public void mapOffsetEmptyString() throws IOException {
		VCSFile file = mock(VCSFile.class);
		when(file.readeContent()).thenReturn("");
		when(file.readLinesWithEOL()).thenCallRealMethod();
		when(file.mapOffset(1, 4)).thenCallRealMethod();

		assertThatExceptionOfType(IndexOutOfBoundsException.class)
				.isThrownBy(() -> file.mapOffset(1, 4));
	}

	@Test
	public void mapPosition() throws IOException {
		VCSFile file = mock(VCSFile.class);
		when(file.readeContent()).thenReturn("first line\nsecond line");
		when(file.readLinesWithEOL()).thenCallRealMethod();

		VCSFile.Position p1 = new VCSFile.Position(1, 6, 4);
		VCSFile.Position p2 = new VCSFile.Position(2, 2, 4);
		VCSFile.Position p3 = new VCSFile.Position(1, 11, 4);
		VCSFile.Position p4 = new VCSFile.Position(2, 12, 4);
		when(file.mapPosition(p1)).thenCallRealMethod();
		when(file.mapPosition(p2)).thenCallRealMethod();
		when(file.mapPosition(p3)).thenCallRealMethod();
		when(file.mapPosition(p4)).thenCallRealMethod();

		assertThat(file.mapPosition(p1)).isEqualTo(5);
		assertThat(file.mapPosition(p2)).isEqualTo(12);
		assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(() -> file.mapPosition(p3));
		assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(() -> file.mapPosition(p4));
	}

	@Test
	public void mapPositionWithTabs() throws IOException {
		VCSFile file = mock(VCSFile.class);
		when(file.readeContent()).thenReturn("foo\tbar\tfoobar");
		when(file.readLinesWithEOL()).thenCallRealMethod();

		VCSFile.Position p1 = new VCSFile.Position(1, 8, 4);
		VCSFile.Position p2 = new VCSFile.Position(1, 12, 8);
		VCSFile.Position p3 = new VCSFile.Position(1, 1, 1);
		VCSFile.Position p4 = new VCSFile.Position(1, 2, 1);
		VCSFile.Position p5 = new VCSFile.Position(1, 3, 1);
		VCSFile.Position p6 = new VCSFile.Position(1, 12, 2);
		when(file.mapPosition(p1)).thenCallRealMethod();
		when(file.mapPosition(p2)).thenCallRealMethod();
		when(file.mapPosition(p3)).thenCallRealMethod();
		when(file.mapPosition(p4)).thenCallRealMethod();
		when(file.mapPosition(p5)).thenCallRealMethod();
		when(file.mapPosition(p6)).thenCallRealMethod();

		assertThat(file.mapPosition(p1)).isEqualTo(5);
		assertThat(file.mapPosition(p2)).isEqualTo(5);
		assertThat(file.mapPosition(p3)).isEqualTo(0);
		assertThat(file.mapPosition(p4)).isEqualTo(1);
		assertThat(file.mapPosition(p5)).isEqualTo(2);
		assertThat(file.mapPosition(p6)).isEqualTo(11);
	}
}
