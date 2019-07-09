package de.unibremen.informatik.st.libvcs4j;

import de.unibremen.informatik.st.libvcs4j.engine.AbstractVSCEngine;
import lombok.NonNull;
import lombok.Value;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.apache.commons.io.IOUtils.toByteArray;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RangeTest {

	@Value
	private static final class VCSFileMock implements VCSFile {
		@NonNull
		private final String relativePath;
		@NonNull
		private final Revision revision;
		@NonNull
		private final VCSEngine VCSEngine;
		@NonNull
		private final byte[] bytes;
		@NonNull
		private final Charset charset;

		@Override
		public byte[] readAllBytes() {
			return bytes;
		}

		@Override
		public Optional<Charset> guessCharset() {
			return Optional.of(charset);
		}
	}

	private final VCSModelFactory modelFactory = new VCSModelFactory() {};

	private FileChange createFileChangeFromResource(String oldFile,
			String newFile) throws IOException {
		VCSEngine engine = mock(AbstractVSCEngine.class);
		when(engine.getModelFactory()).thenReturn(modelFactory);
		when(engine.computeDiff(any())).thenCallRealMethod();
		Revision revision1 = mock(Revision.class);
		when(revision1.getId()).thenReturn("1");
		when(revision1.getOutput()).thenReturn(Paths.get("/tmp"));
		Revision revision2 = mock(Revision.class);
		when(revision2.getId()).thenReturn("2");
		when(revision2.getOutput()).thenReturn(Paths.get("/tmp"));

		VCSFile file1 = new VCSFileMock(oldFile, revision1, engine,
				toByteArray(getClass().getResourceAsStream(oldFile)),
				StandardCharsets.UTF_8);

		VCSFile file2 = new VCSFileMock(newFile, revision2, engine,
				toByteArray(getClass().getResourceAsStream(newFile)),
				StandardCharsets.UTF_8);

		return modelFactory.createFileChange(file1, file2, engine);
	}

	@Test
	public void applyDefaultTypeAdapters_1_2() throws IOException {
		FileChange change = createFileChangeFromResource(
				"/diff/DefaultTypeAdapters.java.1",
				"/diff/DefaultTypeAdapters.java.2");

		VCSFile.Position begin = change.getOldFile()
				.orElseThrow(AssertionError::new)
				.positionOf(400, 7, 2)
				.orElseThrow(AssertionError::new);
		VCSFile.Position end = change.getOldFile()
				.orElseThrow(AssertionError::new)
				.positionOf(400, 32, 2)
				.orElseThrow(AssertionError::new);
		VCSFile.Range range = new VCSFile.Range(begin, end);
		VCSFile.Range updated = range.apply(change)
				.orElseThrow(AssertionError::new);

		assertThat(updated.getBegin().getLine()).isEqualTo(464);
		assertThat(updated.getBegin().getColumn()).isEqualTo(7);
		assertThat(updated.getEnd().getLine()).isEqualTo(464);
		assertThat(updated.getEnd().getColumn()).isEqualTo(32);
		assertThat(updated.readContent()).isEqualTo(
				"return new Boolean(false);");
	}

	@Test
	public void applyDefaultTypeAdapters_47_48() throws IOException {
		FileChange change = createFileChangeFromResource(
				"/diff/DefaultTypeAdapters.java.47",
				"/diff/DefaultTypeAdapters.java.48");

		VCSFile.Position begin = change.getOldFile()
				.orElseThrow(AssertionError::new)
				.positionOf(463, 14, 2)
				.orElseThrow(AssertionError::new);
		VCSFile.Position end = change.getOldFile()
				.orElseThrow(AssertionError::new)
				.positionOf(463, 32, 2)
				.orElseThrow(AssertionError::new);
		VCSFile.Range range = new VCSFile.Range(begin, end);
		VCSFile.Range updated = range.apply(change)
				.orElseThrow(AssertionError::new);

		assertThat(updated.getBegin().getLine()).isEqualTo(506);
		assertThat(updated.getBegin().getColumn()).isEqualTo(14);
		assertThat(updated.getEnd().getLine()).isEqualTo(506);
		assertThat(updated.getEnd().getColumn()).isEqualTo(32);
		assertThat(updated.readContent()).isEqualTo(
				"new BigInteger(\"0\")");
	}

	@Test
	public void applyPrimitiveTest_88_89() throws IOException {
		FileChange change = createFileChangeFromResource(
				"/diff/PrimitiveTest.java.88",
				"/diff/PrimitiveTest.java.89");

		VCSFile.Position begin = change.getOldFile()
				.orElseThrow(AssertionError::new)
				.positionOf(400, 7, 2)
				.orElseThrow(AssertionError::new);
		VCSFile.Position end = change.getOldFile()
				.orElseThrow(AssertionError::new)
				.positionOf(401, 5, 2)
				.orElseThrow(AssertionError::new);
		VCSFile.Range range = new VCSFile.Range(begin, end);
		VCSFile.Range updated = range.apply(change)
				.orElseThrow(AssertionError::new);

		assertThat(updated.getBegin().getLine()).isEqualTo(393);
		assertThat(updated.getBegin().getColumn()).isEqualTo(7);
		assertThat(updated.getEnd().getLine()).isEqualTo(394);
		assertThat(updated.getEnd().getColumn()).isEqualTo(5);
		assertThat(updated.readContent()).isEqualTo(
				"catch (JsonParseException expected) {      \n    }");
	}

	@Test
	public void applyJsonReaderTest_973_974() throws IOException {
		FileChange change = createFileChangeFromResource(
				"/diff/JsonReaderTest.java.973",
				"/diff/JsonReaderTest.java.974");

		VCSFile.Position begin = change.getOldFile()
				.orElseThrow(AssertionError::new)
				.positionOf(1251, 7, 2)
				.orElseThrow(AssertionError::new);
		VCSFile.Position end = change.getOldFile()
				.orElseThrow(AssertionError::new)
				.positionOf(1252, 5, 2)
				.orElseThrow(AssertionError::new);
		VCSFile.Range range = new VCSFile.Range(begin, end);
		assertThat(range.apply(change)).isEmpty();
	}
}
