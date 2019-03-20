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

import static org.assertj.core.api.Assertions.assertThat;
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
	public void testDateTime() throws IOException {
		VCSEngineBuilder builder = VCSEngineBuilder
				.of(folder.newFolder().getPath())
				.withSince(LocalDateTime.now())
				.withUntil(LocalDateTime.now());
		assertThat(builder.build()).isNotNull();
	}

	@Test
	public void testDate() throws IOException {
		VCSEngineBuilder builder = VCSEngineBuilder
				.of(folder.newFolder().getPath())
				.withSince(LocalDate.now())
				.withUntil(LocalDate.now());
		assertThat(builder.build()).isNotNull();
	}

	@Test
	public void testDateString() throws IOException {
		VCSEngineBuilder builder =  VCSEngineBuilder
				.of(folder.newFolder().getPath())
				.withSince("2000-01-01")
				.withUntil("2017-01-01");
		assertThat(builder.build()).isNotNull();
	}
}
