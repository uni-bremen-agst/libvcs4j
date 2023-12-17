package de.unibremen.informatik.st.libvcs4j.hg;

import de.unibremen.informatik.st.libvcs4j.*;
import org.junit.Test;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class JavaHGTest extends VCSBaseTest {

	@Override
	protected String getTarGZFile() {
		return "javahg.tar.gz";
	}

	@Override
	protected String getFolderInTarGZ() {
		return "javahg";
	}

	@Override
	protected void setEngine(VCSEngineBuilder builder) {
		builder.withHG();
	}

	@Override
	protected String getRootCommitIdFile() {
		return "javahg_master_ids.txt";
	}

	@Override
	protected String getSubDir() {
		return "src/main/java/com/aragost/javahg/commands";
	}

	@Override
	protected String getSubDirCommitIdFile() {
		return "javahg_master_commands_ids.txt";
	}

	@Test
	public void commitaf30b413d2d2() throws IOException {
		VCSEngine engine = createBuilder()
				.withFrom("af30b413d2d2")
				.withTo("af30b413d2d2")
				.build();

		Optional<RevisionRange> range = engine.next();
		assertTrue(range.isPresent());

		Commit commit = range.get().getLatestCommit();
		assertEquals(
				"af30b413d2d26edfaacfe5732c2e56d6197acb04",
				commit.getId());
		assertEquals(
				"Amenel Voglozin",
				commit.getAuthor());
		assertEquals(
				"README.text edited online with Bitbucket",
				commit.getMessage());
		// We can not assert the datetime because
		// JavaHG does not provide a proper zone id.
//		assertEquals(
//				LocalDateTime.of(2016, 12, 21, 1, 26, 3),
//				commit.getDateTime());
		assertEquals(
				Arrays.asList("809"),
				commit.getParentIds());
	}

	@Test
	public void commit30467fac2239() throws IOException {
		VCSEngine engine = createBuilder()
				.withFrom("30467fac2239")
				.withTo("30467fac2239")
				.build();

		Optional<RevisionRange> range = engine.next();
		assertTrue(range.isPresent());

		Commit commit = range.get().getLatestCommit();assertEquals(
				"30467fac22392fe643e2f0ea6a9e88db40e43f12",
				commit.getId());
		assertEquals(
				"Jan Sorensen",
				commit.getAuthor());
		assertEquals(
				"JavaHgTestMercurialExtensionTest: Fixed todo and test case for long messages written to stderr",
				commit.getMessage());
	}

	@Test
	public void branch_1485179654809() throws IOException {
		VCSEngine engine = createBuilder()
				.withBranch("g-pechorin/readmetext-edited-online-with-bitbucket-1485179654809")
				.build();

		List<RevisionRange> ranges = new ArrayList<>();
		engine.forEach(ranges::add);
		assertEquals(1, ranges.size());
		assertEquals("544c5168e4d3d314a996699692d8099ffa6419b2",
				ranges.get(0).getCurrent().getId());
	}

	@Test
	public void branch_1485179654809_withRevisionInterval() throws IOException {
		VCSEngine engine = createBuilder()
				.withBranch("g-pechorin/readmetext-edited-online-with-bitbucket-1485179654809")
				.withFrom("820")
				.withTo("820")
				.build();

		List<RevisionRange> ranges = new ArrayList<>();
		engine.forEach(ranges::add);
		assertEquals(1, ranges.size());
		assertEquals("544c5168e4d3d314a996699692d8099ffa6419b2",
				ranges.get(0).getCurrent().getId());
	}

	@Test
	public void lineInfoAddCommandTest30b413d2d2() throws IOException {
		VCSEngine engine = createBuilder()
				.withFrom("af30b413d2d2")
				.withTo("af30b413d2d2")
				.build();

		Optional<RevisionRange> range = engine.next();
		assertTrue(range.isPresent());

		Revision revision = range.get().getCurrent();
		VCSFile addCommandTest = revision
				.getFilesBySuffix("AddCommandTest.java").get(0);
		List<LineInfo> lineInfo = addCommandTest.readLineInfo();

		assertEquals(
				"65364bf9dfb302952541d0380601dbacddf39107",
				lineInfo.get(38).getId());
		assertEquals(
				"Jan Sorensen",
				lineInfo.get(38).getAuthor());
		assertEquals(
				"AddCommand: Return lsit of files added",
				lineInfo.get(38).getMessage());
		assertEquals(
				39,
				lineInfo.get(38).getLine());
		assertEquals(
				"",
				lineInfo.get(38).getContent());
		assertEquals(
				addCommandTest,
				lineInfo.get(38).getFile());
	}

	@Test
	public void lineInfoAddCommandTest1da1b235e27cSubDir() throws IOException {
		VCSEngine engine = createBuilder()
				.withRoot("src")
				.withFrom("1da1b235e27c")
				.build();

		Optional<RevisionRange> range = engine.next();
		assertTrue(range.isPresent());

		Revision revision = range.get().getCurrent();
		VCSFile addCommandTest = revision
				.getFilesBySuffix("AddCommandTest.java").get(0);
		List<LineInfo> lineInfo = addCommandTest.readLineInfo();

		assertEquals(
				"65364bf9dfb302952541d0380601dbacddf39107",
				lineInfo.get(38).getId());
		assertEquals(
				"Jan Sorensen",
				lineInfo.get(38).getAuthor());
		assertEquals(
				"AddCommand: Return lsit of files added",
				lineInfo.get(38).getMessage());
		assertEquals(
				39,
				lineInfo.get(38).getLine());
		assertEquals(
				"",
				lineInfo.get(38).getContent());
		assertEquals(
				addCommandTest,
				lineInfo.get(38).getFile());
	}
}
