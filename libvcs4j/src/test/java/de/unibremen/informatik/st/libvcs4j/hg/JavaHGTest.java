package de.unibremen.informatik.st.libvcs4j.hg;

import de.unibremen.informatik.st.libvcs4j.Commit;
import de.unibremen.informatik.st.libvcs4j.VCSBaseTest;
import de.unibremen.informatik.st.libvcs4j.VCSEngine;
import de.unibremen.informatik.st.libvcs4j.VCSEngineBuilder;
import de.unibremen.informatik.st.libvcs4j.Version;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
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
	protected String getIdFile() {
		return "javahg_master_ids.txt";
	}

	@Test
	public void commitaf30b413d2d2() throws IOException {
		VCSEngine engine = createBuilder()
				.withFrom("af30b413d2d2")
				.withTo("af30b413d2d2")
				.build();

		Optional<Version> version = engine.next();
		assertTrue(version.isPresent());

		Commit commit = version.get().getLatestCommit();
		assertEquals(
				"810",
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

		Optional<Version> version = engine.next();
		assertTrue(version.isPresent());

		Commit commit = version.get().getLatestCommit();assertEquals(
				"560",
				commit.getId());
		assertEquals(
				"Jan Sorensen",
				commit.getAuthor());
		assertEquals(
				"JavaHgTestMercurialExtensionTest: Fixed todo and test case for long messages written to stderr",
				commit.getMessage());
	}
}
