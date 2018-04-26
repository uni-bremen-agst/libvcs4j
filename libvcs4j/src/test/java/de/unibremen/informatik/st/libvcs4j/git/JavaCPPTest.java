package de.unibremen.informatik.st.libvcs4j.git;

import de.unibremen.informatik.st.libvcs4j.Commit;
import de.unibremen.informatik.st.libvcs4j.FileChange;
import de.unibremen.informatik.st.libvcs4j.VCSBaseTest;
import de.unibremen.informatik.st.libvcs4j.VCSEngine;
import de.unibremen.informatik.st.libvcs4j.VCSEngineBuilder;
import de.unibremen.informatik.st.libvcs4j.VCSFile;
import de.unibremen.informatik.st.libvcs4j.Version;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class JavaCPPTest extends VCSBaseTest {

	@Override
	protected String getTarGZFile() {
		return "javacpp.tar.gz";
	}

	@Override
	protected String getFolderInTarGZ() {
		return "javacpp";
	}

	@Override
	protected void setEngine(VCSEngineBuilder builder) {
		builder.withGit();
	}

	@Override
	protected String getIdFile() {
		return "javacpp_master_ids.txt";
	}

	@Test
	public void commit29208f7369e95219830b60fb37e90bb1e5bd9448()
			throws IOException {
		VCSEngine engine = createBuilder()
				.withFrom("29208f7369e95219830b60fb37e90bb1e5bd9448")
				.withTo("29208f7369e95219830b60fb37e90bb1e5bd9448")
				.build();

		Optional<Version> version = engine.next();
		assertTrue(version.isPresent());

		Commit commit = version.get().getLatestCommit();
		assertEquals(
				"29208f7369e95219830b60fb37e90bb1e5bd9448",
				commit.getId());
		assertEquals(
				"Samuel Audet",
				commit.getAuthor());
		assertEquals(
				"Fix potential compilation error with VectorAdapter",
				commit.getMessage());
		assertEquals(
				LocalDateTime.of(2016, 4, 7, 13, 31, 20),
				commit.getDateTime());
		assertEquals(
				Arrays.asList("24abf3a5d2e54d815ef35b85ffd786023784d073"),
				commit.getParentIds());
	}

	@Test
	public void commit3934c45285d7de75c7c827b3a104cbe89658d6aa()
			throws IOException {
		VCSEngine engine = createBuilder()
				.withFrom("3934c45285d7de75c7c827b3a104cbe89658d6aa")
				.withTo("3934c45285d7de75c7c827b3a104cbe89658d6aa")
				.build();

		Optional<Version> version = engine.next();
		assertTrue(version.isPresent());

		Commit commit = version.get().getLatestCommit();
		assertEquals(
				"3934c45285d7de75c7c827b3a104cbe89658d6aa",
				commit.getId());
		assertEquals(
				"Samuel Audet",
				commit.getAuthor());
		assertEquals(
				" * Arguments of `Pointer` type now get handled as `char*` in cases when the `position` can be used for arithmetic\n" +
				" * Worked around bug of `InputStream.available()` always returning 0 with the `http` protocol in `Loader.extractResource(URL)`",
				commit.getMessage());
		assertEquals(
				LocalDateTime.of(2013, 3, 24, 10, 33, 42),
				commit.getDateTime());
		assertEquals(
				Arrays.asList("28832078f3c748b54b18cd120cfe84bb34762a78"),
				commit.getParentIds());
	}

	@Test
	public void changesb6cf70463402133a4c62b9bc0b8ba224e0b05d0f()
			throws IOException {
		VCSEngine engine = createBuilder()
				.withFrom("7c53985d8e9d5311b478e903e80c7ff7c5fde277")
				.withTo("b6cf70463402133a4c62b9bc0b8ba224e0b05d0f")
				.build();

		engine.next();
		Optional<Version> version = engine.next();
		assertTrue(version.isPresent());

		List<FileChange> fileChanges = version.get().getFileChanges();
		assertEquals(3, fileChanges.size());

		Path path1 = Paths.get(
				"src/main/java/org/bytedeco/javacpp/tools/BuildMojo.java");
		Path path2 = Paths.get(
				"src/main/java/org/bytedeco/javacpp/tools/Builder.java");
		Path path3 = Paths.get(
				"src/main/java/org/bytedeco/javacpp/tools/Parser.java");

		assertEquals(FileChange.Type.MODIFY, fileChanges.get(0).getType());
		Optional<VCSFile> fileChange1 = fileChanges.get(0).getNewFile();
		assertTrue(fileChange1.isPresent());
		Path pathChange1 = fileChange1.get().toRelativePath();
		assertTrue(pathChange1.equals(path1)
				|| pathChange1.equals(path2)
				|| pathChange1.equals(path3));

		assertEquals(FileChange.Type.MODIFY, fileChanges.get(1).getType());
		Optional<VCSFile> fileChange2 = fileChanges.get(1).getNewFile();
		assertTrue(fileChange2.isPresent());
		Path pathChange2 = fileChange2.get().toRelativePath();
		assertTrue(pathChange2.equals(path1)
				|| pathChange2.equals(path2)
				|| pathChange2.equals(path3));

		assertEquals(FileChange.Type.MODIFY, fileChanges.get(2).getType());
		Optional<VCSFile> fileChange3 = fileChanges.get(2).getNewFile();
		assertTrue(fileChange3.isPresent());
		Path pathChange3 = fileChange3.get().toRelativePath();
		assertTrue(pathChange3.equals(path1)
				|| pathChange3.equals(path2)
				|| pathChange3.equals(path3));
	}
}
