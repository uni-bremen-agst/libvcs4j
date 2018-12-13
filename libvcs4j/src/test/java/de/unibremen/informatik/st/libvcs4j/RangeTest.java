package de.unibremen.informatik.st.libvcs4j;

import de.unibremen.informatik.st.libvcs4j.data.FileChangeImpl;
import de.unibremen.informatik.st.libvcs4j.data.VCSFileImpl;
import de.unibremen.informatik.st.libvcs4j.engine.AbstractVSCEngine;
import org.junit.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.apache.commons.io.IOUtils.toByteArray;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RangeTest {

	private FileChange createFileChangeFromResource(String oldFile,
			String newFile) throws IOException {
		VCSEngine engine = mock(AbstractVSCEngine.class);
		when(engine.computeDiff(any())).thenCallRealMethod();
		Revision revision1 = mock(Revision.class);
		when(revision1.getId()).thenReturn("1");
		Revision revision2 = mock(Revision.class);
		when(revision2.getId()).thenReturn("2");

		VCSFileImpl file1 = new VCSFileImpl() {
			@Override
			public byte[] readAllBytes() throws IOException {
				return toByteArray(getClass().getResourceAsStream(oldFile));
			}
		};
		file1.setRelativePath(oldFile);
		file1.setRevision(revision1);

		VCSFileImpl file2 = new VCSFileImpl(){
			@Override
			public byte[] readAllBytes() throws IOException {
				return toByteArray(getClass().getResourceAsStream(newFile));
			}
		};
		file2.setRelativePath(newFile);
		file2.setRevision(revision2);

		FileChangeImpl change = new FileChangeImpl();
		change.setOldFile(file1);
		change.setNewFile(file2);
		change.setVCSEngine(engine);
		return change;
	}

	@Test
	public void applyDefaultTypeAdapters_1_2() throws IOException {
		FileChange change = createFileChangeFromResource(
				"/diff/DefaultTypeAdapters.java.1",
				"/diff/DefaultTypeAdapters.java.2");

		VCSFile.Range range = new VCSFile.Range(
				change.getOldFile().orElseThrow(AssertionError::new),
				400, 7, 400, 32, 2);
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

		VCSFile.Range range = new VCSFile.Range(
				change.getOldFile().orElseThrow(AssertionError::new),
				463, 14, 463, 32, 2);
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

		VCSFile.Range range = new VCSFile.Range(
				change.getOldFile().orElseThrow(AssertionError::new),
				400, 7, 401, 5, 2);
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

		VCSFile.Range range = new VCSFile.Range(
				change.getOldFile().orElseThrow(AssertionError::new),
				1251, 7, 1252, 5, 2);
		assertThat(range.apply(change)).isEmpty();
	}
}
