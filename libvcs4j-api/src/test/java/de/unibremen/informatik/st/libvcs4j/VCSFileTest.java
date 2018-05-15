package de.unibremen.informatik.st.libvcs4j;

import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class VCSFileTest {

	@Test
	public void readLinesUnixEOL() throws IOException {
		VCSFile file = mock(VCSFile.class);
		when(file.readeContent()).thenReturn("first line\nsecond line\n");
		when(file.readLinesWithEOL()).thenCallRealMethod();

		List<String> lines = file.readLinesWithEOL();
		assertThat(lines)
				.hasSize(2)
				.contains("first line\n", "second line\n");
	}

	@Test
	public void readLinesWindowsEOL() throws IOException {
		VCSFile file = mock(VCSFile.class);
		when(file.readeContent()).thenReturn("first line\r\nsecond line\r\n");
		when(file.readLinesWithEOL()).thenCallRealMethod();

		List<String> lines = file.readLinesWithEOL();
		assertThat(lines)
				.hasSize(2)
				.contains("first line\r\n", "second line\r\n");
	}

	@Test
	public void readLinesOldMacEOL() throws IOException {
		VCSFile file = mock(VCSFile.class);
		when(file.readeContent()).thenReturn("first line\rsecond line\r");
		when(file.readLinesWithEOL()).thenCallRealMethod();

		List<String> lines = file.readLinesWithEOL();
		assertThat(lines)
				.hasSize(2)
				.contains("first line\r", "second line\r");
	}

	@Test
	public void readLinesMixedEOL() throws IOException {
		VCSFile file = mock(VCSFile.class);
		when(file.readeContent()).thenReturn("first\rsecond\r\nthird\n");
		when(file.readLinesWithEOL()).thenCallRealMethod();

		List<String> lines = file.readLinesWithEOL();
		assertThat(lines)
				.hasSize(3)
				.contains("first\r", "second\r\n", "third\n");
	}

	@Test
	public void readLinesWithoutLastEOL() throws IOException {
		VCSFile file = mock(VCSFile.class);
		when(file.readeContent()).thenReturn("foo\nbar");
		when(file.readLinesWithEOL()).thenCallRealMethod();

		List<String> lines = file.readLinesWithEOL();
		assertThat(lines)
				.hasSize(2)
				.contains("foo\n", "bar");
	}

	@Test
	public void readLinesEmptyContent() throws IOException {
		VCSFile file = mock(VCSFile.class);
		when(file.readeContent()).thenReturn("");
		when(file.readLinesWithEOL()).thenCallRealMethod();

		List<String> lines = file.readLinesWithEOL();
		assertThat(lines).isEmpty();
	}

	@Test
	public void offsetToPosition() throws IOException {
		VCSFile file = mock(VCSFile.class);
		when(file.readeContent()).thenReturn("first line\nsecond line");
		when(file.readLinesWithEOL()).thenCallRealMethod();
		when(file.getPosition(4, 4)).thenCallRealMethod();
		when(file.getPosition(14, 4)).thenCallRealMethod();

		VCSFile.Position p1 = file.getPosition(4, 4);
		assertThat(p1.getLine()).isEqualTo(1);
		assertThat(p1.getColumn()).isEqualTo(5);
		assertThat(p1.getTabSize()).isEqualTo(4);

		VCSFile.Position p2 = file.getPosition(14, 4);
		assertThat(p2.getLine()).isEqualTo(2);
		assertThat(p2.getColumn()).isEqualTo(4);
		assertThat(p2.getTabSize()).isEqualTo(4);
	}
}
