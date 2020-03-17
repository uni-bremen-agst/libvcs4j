package de.unibremen.informatik.st.libvcs4j;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class VCSFileTest {

	private static class VCSFileMock implements VCSFile {
		private final String content;

		public VCSFileMock(final String content) {
			this.content = Validate.notNull(content);
		}

		@Override
		public Optional<Charset> guessCharset() {
			return Optional.of(StandardCharsets.UTF_8);
		}

		@Override
		public byte[] readAllBytes() {
			return content.getBytes(StandardCharsets.UTF_8);
		}

		@Override
		public String getRelativePath() {
			throw new UnsupportedOperationException();
		}

		@Override
		public Revision getRevision() {
			throw new UnsupportedOperationException();
		}

		@Override
		public VCSEngine getVCSEngine() {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean isBinary() {
			return false;
		}
	}



	@Test
	public void readLinesWithEOLUnixEOL() throws IOException {
		VCSFile file = new VCSFileMock("first line\nsecond line\n");

		List<String> lines = file.readLinesWithEOL();
		assertThat(lines)
				.hasSize(2)
				.contains("first line\n", "second line\n");
	}

	@Test
	public void readLinesWithEOLWindowsEOL() throws IOException {
		VCSFile file = new VCSFileMock("first line\r\nsecond line\r\n");

		List<String> lines = file.readLinesWithEOL();
		assertThat(lines)
				.hasSize(2)
				.contains("first line\r\n", "second line\r\n");
	}

	@Test
	public void readLinesWithEOLOldMacEOL() throws IOException {
		VCSFile file = new VCSFileMock("first line\rsecond line\r");

		List<String> lines = file.readLinesWithEOL();
		assertThat(lines)
				.hasSize(2)
				.contains("first line\r", "second line\r");
	}

	@Test
	public void readLinesWithEOLMixedEOL() throws IOException {
		VCSFile file = new VCSFileMock("first\rsecond\r\nthird\n");

		List<String> lines = file.readLinesWithEOL();
		assertThat(lines)
				.hasSize(3)
				.contains("first\r", "second\r\n", "third\n");
	}

	@Test
	public void readLinesWithEOLWithoutLastEOL() throws IOException {
		VCSFile file = new VCSFileMock("foo\nbar");

		List<String> lines = file.readLinesWithEOL();
		assertThat(lines)
				.hasSize(2)
				.contains("foo\n", "bar");
	}

	@Test
	public void readLinesWithEOLEmptyContent() throws IOException {
		VCSFile file = new VCSFileMock("");

		List<String> lines = file.readLinesWithEOL();
		assertThat(lines).isEmpty();
	}

	@Test
	public void readLinesMixedEOL() throws IOException {
		VCSFile file = new VCSFileMock("first\rsecond\r\nthird\n");

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
	public void positionOfOffsetRegularCase() throws IOException {
		VCSFile file = new VCSFileMock("foobar");

		VCSFile.Position p1 = file.positionOf(3, 4)
				.orElseThrow(AssertionError::new);
		assertThat(p1.getOffset()).isEqualTo(3);
		assertThat(p1.getLine()).isEqualTo(1);
		assertThat(p1.getColumn()).isEqualTo(4);
		assertThat(p1.getTabSize()).isEqualTo(4);

		VCSFile.Position p2 = file.positionOf(5, 8)
				.orElseThrow(AssertionError::new);
		assertThat(p2.getOffset()).isEqualTo(5);
		assertThat(p2.getLine()).isEqualTo(1);
		assertThat(p2.getColumn()).isEqualTo(6);
		assertThat(p2.getTabSize()).isEqualTo(8);
	}

	@Test
	public void positionOfOffsetLastChar() throws IOException {
		VCSFile file = new VCSFileMock("foobar");

		VCSFile.Position p1 = file.positionOf(5, 4)
				.orElseThrow(AssertionError::new);
		assertThat(p1.getOffset()).isEqualTo(5);
		assertThat(p1.getLine()).isEqualTo(1);
		assertThat(p1.getColumn()).isEqualTo(6);
		assertThat(p1.getTabSize()).isEqualTo(4);
		assertThat(file.readContent().charAt(p1.getOffset()))
				.isEqualTo('r');
	}

	@Test
	public void positionOfOffsetMultipleLine() throws IOException {
		VCSFile file = new VCSFileMock("first line\nsecond line");

		VCSFile.Position p3 = file.positionOf(11, 4)
				.orElseThrow(AssertionError::new);
		assertThat(p3.getOffset()).isEqualTo(11);
		assertThat(p3.getLine()).isEqualTo(2);
		assertThat(p3.getColumn()).isEqualTo(1);
		assertThat(p3.getTabSize()).isEqualTo(4);

		VCSFile.Position p2 = file.positionOf(14, 8)
				.orElseThrow(AssertionError::new);
		assertThat(p2.getOffset()).isEqualTo(14);
		assertThat(p2.getLine()).isEqualTo(2);
		assertThat(p2.getColumn()).isEqualTo(4);
		assertThat(p2.getTabSize()).isEqualTo(8);
	}

	@Test
	public void positionOfOffsetWithTabPrefix() throws IOException {
		VCSFile file = new VCSFileMock("\tabc");

		VCSFile.Position p1 = file.positionOf(1, 4)
				.orElseThrow(AssertionError::new);
		assertThat(p1.getOffset()).isEqualTo(1);
		assertThat(p1.getLine()).isEqualTo(1);
		assertThat(p1.getColumn()).isEqualTo(5);
		assertThat(p1.getTabSize()).isEqualTo(4);

		VCSFile.Position p2 = file.positionOf(1, 8)
				.orElseThrow(AssertionError::new);
		assertThat(p2.getOffset()).isEqualTo(1);
		assertThat(p2.getLine()).isEqualTo(1);
		assertThat(p2.getColumn()).isEqualTo(9);
		assertThat(p2.getTabSize()).isEqualTo(8);
	}

	@Test
	public void positionOfOffsetWithTabInfix() throws IOException {
		VCSFile file = new VCSFileMock("a\tbb\tc");

		VCSFile.Position p1 = file.positionOf(2, 4)
				.orElseThrow(AssertionError::new);
		assertThat(p1.getOffset()).isEqualTo(2);
		assertThat(p1.getLine()).isEqualTo(1);
		assertThat(p1.getColumn()).isEqualTo(5);
		assertThat(p1.getTabSize()).isEqualTo(4);

		VCSFile.Position p2 = file.positionOf(5, 8)
				.orElseThrow(AssertionError::new);
		assertThat(p2.getOffset()).isEqualTo(5);
		assertThat(p2.getLine()).isEqualTo(1);
		assertThat(p2.getColumn()).isEqualTo(17);
		assertThat(p2.getTabSize()).isEqualTo(8);
	}

	@Test
	public void positionOfOffsetMultipleTabs() throws IOException {
		VCSFile file = new VCSFileMock("abc\t\tdef");

		VCSFile.Position p1 = file.positionOf(5, 4)
				.orElseThrow(AssertionError::new);
		assertThat(p1.getOffset()).isEqualTo(5);
		assertThat(p1.getLine()).isEqualTo(1);
		assertThat(p1.getColumn()).isEqualTo(9);
		assertThat(p1.getTabSize()).isEqualTo(4);
	}

	@Test
	public void positionOfOffsetEmptyString() throws IOException {
		VCSFile file = new VCSFileMock("");
		assertThat(file.positionOf(1, 4)).isEmpty();
	}

	@Test
	public void positionOfOffsetInEOL() throws IOException {
		VCSFile file = new VCSFileMock("first\nsecond\rthird\r\nforth");

		assertThat(file.positionOf(5, 4)).isEmpty();
		assertThat(file.positionOf(12, 4)).isEmpty();
		assertThat(file.positionOf(18, 4)).isEmpty();
		assertThat(file.positionOf(19, 4)).isEmpty();
	}



	@Test
	public void positionOfLineAndColumnRegularCase() throws IOException {
		VCSFile file = new VCSFileMock("first line\nsecond line");

		VCSFile.Position p1 = file.positionOf(1, 6, 4)
				.orElseThrow(AssertionError::new);
		assertThat(p1.getLine()).isEqualTo(1);
		assertThat(p1.getColumn()).isEqualTo(6);
		assertThat(p1.getOffset()).isEqualTo(5);
		assertThat(p1.getTabSize()).isEqualTo(4);

		VCSFile.Position p2 = file.positionOf(2, 2, 8)
				.orElseThrow(AssertionError::new);
		assertThat(p2.getLine()).isEqualTo(2);
		assertThat(p2.getColumn()).isEqualTo(2);
		assertThat(p2.getOffset()).isEqualTo(12);
		assertThat(p2.getTabSize()).isEqualTo(8);
	}

	@Test
	public void positionOfLineAndColumnOutOfColumn() throws IOException {
		VCSFile file = new VCSFileMock("foobar\nbar");
		assertThat(file.positionOf(1, 7, 4)).isEmpty();
		assertThat(file.positionOf(2, 4, 4)).isEmpty();
	}

	@Test
	public void positionOfLineAndColumnWithTabPrefix() throws IOException {
		VCSFile file = new VCSFileMock("\tabc");

		VCSFile.Position p1 = file.positionOf(1, 5, 4)
				.orElseThrow(AssertionError::new);
		assertThat(p1.getLine()).isEqualTo(1);
		assertThat(p1.getColumn()).isEqualTo(5);
		assertThat(p1.getOffset()).isEqualTo(1);
		assertThat(p1.getTabSize()).isEqualTo(4);

		VCSFile.Position p2 = file.positionOf(1, 9, 8)
				.orElseThrow(AssertionError::new);
		assertThat(p2.getLine()).isEqualTo(1);
		assertThat(p2.getColumn()).isEqualTo(9);
		assertThat(p2.getOffset()).isEqualTo(1);
		assertThat(p2.getTabSize()).isEqualTo(8);
	}

	@Test
	public void positionOfLineAndColumnWithTabInfix() throws IOException {
		VCSFile file = new VCSFileMock("a\tbb\tc");

		VCSFile.Position p1 = file.positionOf(1, 5, 4)
				.orElseThrow(AssertionError::new);
		assertThat(p1.getLine()).isEqualTo(1);
		assertThat(p1.getColumn()).isEqualTo(5);
		assertThat(p1.getOffset()).isEqualTo(2);
		assertThat(p1.getTabSize()).isEqualTo(4);

		VCSFile.Position p2 = file.positionOf(1, 17, 8)
				.orElseThrow(AssertionError::new);
		assertThat(p2.getLine()).isEqualTo(1);
		assertThat(p2.getColumn()).isEqualTo(17);
		assertThat(p2.getOffset()).isEqualTo(5);
		assertThat(p2.getTabSize()).isEqualTo(8);
	}

	@Test
	public void positionOfLineAndColumnMultipleTabs() throws IOException {
		VCSFile file = new VCSFileMock("abc\t\tdef");

		VCSFile.Position p1 = file.positionOf(1, 9, 4)
				.orElseThrow(AssertionError::new);
		assertThat(p1.getLine()).isEqualTo(1);
		assertThat(p1.getColumn()).isEqualTo(9);
		assertThat(p1.getOffset()).isEqualTo(5);
		assertThat(p1.getTabSize()).isEqualTo(4);
	}

	@Test
	public void positionOfLineAndColumnEmptyString() throws IOException {
		VCSFile file = new VCSFileMock("");
		assertThat(file.positionOf(1, 1, 4)).isEmpty();
	}

	@Test
	public void positionOfLineAndColumnInEOL() throws IOException {
		VCSFile file = new VCSFileMock("first\nsecond\rthird\r\nforth");

		assertThat(file.positionOf(1, 6, 4)).isEmpty();
		assertThat(file.positionOf(2, 7, 4)).isEmpty();
		assertThat(file.positionOf(3, 6, 4)).isEmpty();
		assertThat(file.positionOf(3, 7, 4)).isEmpty();
	}

	@Test
	public void positionOfLineAndColumnBugfixOffset() throws IOException {
		VCSFile file = new VCSFileMock("ab\tcd\tef\tgh");

		assertThat(file.positionOf(1, 15, 4)).isEmpty();

		VCSFile.Position p1 = file.positionOf(1, 14, 4)
				.orElseThrow(AssertionError::new);
		assertThat(p1.getLine()).isEqualTo(1);
		assertThat(p1.getColumn()).isEqualTo(14);
		assertThat(p1.getOffset()).isEqualTo(10);
		assertThat(p1.getTabSize()).isEqualTo(4);
	}

	@Test
	public void positionOfLineAndColumnBugfixLength() throws IOException {
		VCSFile file = new VCSFileMock(
				"\t\t\t\t\t\tif (!method.getParameters().get(i).getType().equals(ctMethod.getParameters().get(i).getType())) {\n");

		VCSFile.Position position = file.positionOf(1, 118, 4)
				.orElseThrow(AssertionError::new);
		assertThat(position.getLine()).isEqualTo(1);
		assertThat(position.getColumn()).isEqualTo(118);
		assertThat(position.getOffset()).isEqualTo(99);
		assertThat(position.getTabSize()).isEqualTo(4);
	}
}
