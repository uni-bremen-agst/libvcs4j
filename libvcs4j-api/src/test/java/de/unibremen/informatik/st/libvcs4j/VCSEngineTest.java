package de.unibremen.informatik.st.libvcs4j;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

public class VCSEngineTest {

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	@Before
	public void setup() throws IOException {
		/*
		 * The following layout will be generated:
		 *
		 * a (file)
		 * b (file)
		 * c (dir)
		 * 		c1 (file)
		 * 		c2 (file)
		 * 		c3 (file)
		 * d (file)
		 * e (file)
		 * f (dir)
		 * 		f1 (file)
		 * 		ff (dir)
		 * 			ff1 (file)
		 * 			ff2 (file)
		 */
		folder.newFile("a");
		folder.newFile("b");
		File c = folder.newFolder("c");
		Files.createFile(c.toPath().resolve("c1"));
		Files.createFile(c.toPath().resolve("c2"));
		Files.createFile(c.toPath().resolve("c3"));
		folder.newFile("d");
		folder.newFile("e");
		File f = folder.newFolder("f");
		Files.createFile(f.toPath().resolve("f1"));
		Path ff = Files.createDirectory(f.toPath().resolve("ff"));
		Files.createFile(ff.resolve("ff1"));
		Files.createFile(ff.resolve("ff2"));
	}

	@Test
	public void testListFilesInOutput() throws IOException {
		VCSEngine engine = spy(VCSEngine.class);
		when(engine.getOutput()).thenReturn(folder.getRoot().toPath());

		assertThat(engine.listFilesInOutput()).hasSize(10);
	}

	@Test
	public void testListFilesInOutputSingleFile() throws IOException {
		VCSEngine engine = spy(VCSEngine.class);
		when(engine.getOutput()).thenReturn(
				folder.getRoot().toPath().resolve("c").resolve("c1"));

		assertThat(engine.listFilesInOutput()).hasSize(1);
	}

	@Test
	public void testNonExistingOutputDir() throws IOException {
		String path = "asdfhalf324hr789erher9c78vh3cr72ny48t784r7c8tycn87c3";
		VCSEngine engine = spy(VCSEngine.class);
		when(engine.getOutput()).thenReturn(Paths.get(path));

		assertThatExceptionOfType(FileNotFoundException.class)
				.isThrownBy(engine::listFilesInOutput);
	}

	@Test
	public void testSimpleFileFilter() throws IOException {
		VCSEngine engine = spy(VCSEngine.class);
		when(engine.getOutput()).thenReturn(folder.getRoot().toPath());
		when(engine.createVCSFileFilter()).thenReturn(
				(dir, name) -> !name.startsWith("f"));

		assertThat(engine.listFilesInOutput()).hasSize(7);
	}

	@Test
	public void testSimpleDirectoryFilter() throws IOException {
		VCSEngine engine = spy(VCSEngine.class);
		when(engine.getOutput()).thenReturn(folder.getRoot().toPath());
		when(engine.createVCSFileFilter()).thenReturn(
				(dir, name) -> !dir.getName().equals("c"));

		assertThat(engine.listFilesInOutput()).hasSize(7);
	}
}
