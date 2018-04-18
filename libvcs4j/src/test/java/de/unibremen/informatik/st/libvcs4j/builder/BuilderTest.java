package de.unibremen.informatik.st.libvcs4j.builder;

import de.unibremen.informatik.st.libvcs4j.VCSEngine;
import de.unibremen.informatik.st.libvcs4j.VCSEngineBuilder;
import de.unibremen.informatik.st.libvcs4j.filesystem.SingleEngine;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;

/**
 * A {@link VCSEngineBuilder} test using the {@link SingleEngine}.
 */
public class BuilderTest {

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	@Test
	public void testRepository() {
		final String path = folder.getRoot().getAbsolutePath();
		final VCSEngine provider = VCSEngineBuilder
				.of(path)
				.build();
		assertEquals(path, provider.getRepository());
	}

	@Test
	public void testRoot() throws IOException {
		final String path = folder.newFolder("sub").getAbsolutePath();
		final VCSEngine vp = VCSEngineBuilder
				.of(folder.getRoot().getAbsolutePath())
				.withRoot("sub")
				.build();
		assertEquals(path, vp.getRepository());
	}

	@Test
	public void testDateTime() {
		VCSEngineBuilder.of("")
				.withSinceDate(LocalDateTime.now())
				.withUntilDate(LocalDateTime.now())
				.build();
	}

	@Test
	public void testDate() {
		VCSEngineBuilder.of("")
				.withSinceDate(LocalDate.now())
				.withUntilDate(LocalDate.now())
				.build();
	}

	@Test
	public void testDateString() {
		VCSEngineBuilder.of("")
				.withSinceDate("2000-01-01")
				.withUntilDate("2017-01-01")
				.build();
	}
}
