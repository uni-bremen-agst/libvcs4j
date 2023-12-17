package de.unibremen.informatik.st.libvcs4j.git;

import de.unibremen.informatik.st.libvcs4j.*;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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
	protected String getRootCommitIdFile() {
		return "javacpp_master_ids.txt";
	}

	@Override
	protected String getSubDir() {
		return "src/main/java/org/bytedeco/javacpp/tools";
	}

	@Override
	protected String getSubDirCommitIdFile() {
		return "javacpp_master_tools_ids.txt";
	}

	@Test
	public void commit29208() throws IOException {
		VCSEngine engine = createBuilder()
				.withFrom("29208")
				.withTo("29208")
				.build();

		Optional<RevisionRange> range = engine.next();
		assertTrue(range.isPresent());

		Commit commit = range.get().getLatestCommit();
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
	public void commit3934c45285d() throws IOException {
		VCSEngine engine = createBuilder()
				.withFrom("3934c45285d")
				.withTo("3934c45285d")
				.build();

		Optional<RevisionRange> range = engine.next();
		assertTrue(range.isPresent());

		Commit commit = range.get().getLatestCommit();
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
	public void changesb6cf7() throws IOException {
		VCSEngine engine = createBuilder()
				.withFrom("7c539")
				.withTo("b6cf7")
				.build();

		engine.next();
		Optional<RevisionRange> range = engine.next();
		assertTrue(range.isPresent());

		List<FileChange> fileChanges = range.get().getFileChanges();
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

	@Test
	public void includeGitIgnoreInFile() throws IOException {
		VCSEngine engine = createBuilder()
				.withFrom("1380e19f51dd12b7083356e3601f6b5fc763da35")
				.withTo("1380e19f51dd12b7083356e3601f6b5fc763da35")
				.build();

		Optional<RevisionRange> range = engine.next();
		assertTrue(range.isPresent());

		List<String> files = range.get()
				.getCurrent()
				.getFiles()
				.stream()
				.map(VCSFile::toRelativePath)
				.map(Path::getFileName)
				.map(Path::toString)
				.collect(Collectors.toList());
		assertTrue(files.contains(".gitignore"));
	}

	@Test
	public void doesNotIncludeGitDir() throws IOException {
		VCSEngine engine = createBuilder()
				.withEndIdx(10)
				.build();

		List<RevisionRange> ranges = new ArrayList<>();
		engine.forEach(ranges::add);
		assertEquals(10, ranges.size());

		ranges.stream()
				.map(RevisionRange::getCurrent)
				.map(Revision::getFiles)
				.flatMap(Collection::stream)
				.map(VCSFile::toRelativePath)
				.map(Path::toString)
				.forEach(f -> assertFalse(f.startsWith(".git")));
	}

	@Test
	public void branch_gh_pages() throws IOException {
		VCSEngine engine = createBuilder()
				.withBranch("gh-pages")
				.build();

		List<RevisionRange> ranges = new ArrayList<>();
		engine.forEach(ranges::add);
		assertEquals(9, ranges.size());

		assertEquals("9b2c67502aaf168b1dbfee640a38a897cd02a6ec",
				ranges.get(0).getCurrent().getId());
		assertEquals("369203faee219272bc658333c71ffc7dc9117efb",
				ranges.get(1).getCurrent().getId());
		assertEquals("6b95f2bc0b443299e6dbfbf9774fd807c8e8b2c4",
				ranges.get(2).getCurrent().getId());
		assertEquals("320baec0f14f99c2284bb69e0dc6df52677f1474",
				ranges.get(3).getCurrent().getId());
		assertEquals("fbdff9f6014d31f6bd7a5424f510ebd77d0b7c16",
				ranges.get(4).getCurrent().getId());
		assertEquals("4e6011ac12f6e3f7ed9464814cbd7d0a09065273",
				ranges.get(5).getCurrent().getId());
		assertEquals("1106d44879310a9aa658ac73120ea4aaa67d0ab0",
				ranges.get(6).getCurrent().getId());
		assertEquals("1c08928b9b4e0f6529760cf7dbc607383afa7fa5",
				ranges.get(7).getCurrent().getId());
		assertEquals("32510a922ab069d52c312b3fb8668fb9dfda5e5f",
				ranges.get(8).getCurrent().getId());
	}

	@Test
	public void lineInfoBuildMojo3934c45285d() throws IOException {
		VCSEngine engine = createBuilder()
				.withFrom("3934c45285d")
				.withTo("3934c45285d")
				.build();

		Optional<RevisionRange> range = engine.next();
		assertTrue(range.isPresent());

		Revision revision = range.get().getCurrent();
		VCSFile buildMojo = revision.getFilesBySuffix("BuildMojo.java").get(0);
		List<LineInfo> lineInfo = buildMojo.readLineInfo();

		assertEquals(
				"a41cb1dcc6513133e9466331c1f73143dca5e422",
				lineInfo.get(1).getId());
		assertEquals(
				"Samuel Audet",
				lineInfo.get(1).getAuthor());
		assertEquals(
				" * Fixed callbacks not working on Android anymore (issue #30)\n * Added some Javadoc to most of the code",
				lineInfo.get(1).getMessage());
		assertEquals(
				LocalDateTime.of(2013, 2, 24, 23, 38, 11),
				lineInfo.get(1).getDateTime());
		assertEquals(
				2,
				lineInfo.get(1).getLine());
		assertEquals(
				" * Copyright (C) 2012,2013 Arnaud Nauwynck, Samuel Audet",
				lineInfo.get(1).getContent());
		assertEquals(
				buildMojo,
				lineInfo.get(1).getFile());
	}

	@Test
	public void lineInfoBuildMojo29208dSubDir() throws IOException {
		VCSEngine engine = createBuilder()
				.withRoot(getSubDir())
				.withFrom("29208")
				.withTo("29208")
				.build();

		Optional<RevisionRange> range = engine.next();
		assertTrue(range.isPresent());

		Revision revision = range.get().getCurrent();
		VCSFile buildMojo = revision.getFilesBySuffix("BuildMojo.java").get(0);
		List<LineInfo> lineInfo = buildMojo.readLineInfo();

		assertEquals(
				"1604a0f4880f698572a241de49262556a9377138",
				lineInfo.get(1).getId());
		assertEquals(
				"Samuel Audet",
				lineInfo.get(1).getAuthor());
		assertEquals(
				" * Prepend \"javacpp.\" to all properties associated with Maven in `BuildMojo` to avoid name clashes",
				lineInfo.get(1).getMessage());
		assertEquals(
				LocalDateTime.of(2016, 2, 7, 23, 11, 32),
				lineInfo.get(1).getDateTime());
		assertEquals(
				2,
				lineInfo.get(1).getLine());
		assertEquals(
				" * Copyright (C) 2012-2016 Arnaud Nauwynck, Samuel Audet",
				lineInfo.get(1).getContent());
		assertEquals(
				buildMojo,
				lineInfo.get(1).getFile());
	}
}