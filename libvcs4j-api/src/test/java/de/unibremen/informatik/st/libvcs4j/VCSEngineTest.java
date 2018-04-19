package de.unibremen.informatik.st.libvcs4j;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;

/**
 * Some tests must be adjusted if new files are added or existing files are
 * removed from "src/main/java/de/unibremen/st/libvcs4j".
 */
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
		final File c = folder.newFolder("c");
		Files.createFile(c.toPath().resolve("c1"));
		Files.createFile(c.toPath().resolve("c2"));
		Files.createFile(c.toPath().resolve("c3"));
		folder.newFile("d");
		folder.newFile("e");
		final File f = folder.newFolder("f");
		Files.createFile(f.toPath().resolve("f1"));
		final Path ff = Files.createDirectory(f.toPath().resolve("ff"));
		Files.createFile(ff.resolve("ff1"));
		Files.createFile(ff.resolve("ff2"));
	}

	@Test
	public void testListFilesInOutput() throws IOException {
		VCSEngine vp = new TestVCSEngine() {
			@Override
			public Path getOutput() {
				return folder.getRoot().toPath();
			}
		};
		assertEquals(10, vp.listFilesInOutput().size());
	}

	@Test
	public void testListFilesInOutputSingleFile() throws IOException {

		VCSEngine vp = new TestVCSEngine() {
			@Override
			public Path getOutput() {
				return folder.getRoot().toPath().resolve("c").resolve("c1");
			}
		};
		assertEquals(1, vp.listFilesInOutput().size());
	}

	@Test(expected = FileNotFoundException.class)
	public void testNonExistingOutputDir() throws IOException {
		String p = "asdfhalf324hr789erher9c78vh3cr72ny48t784r7c8tycn87c3";
		new TestVCSEngine() {
			@Override
			public Path getOutput() {
				return Paths.get(p);
			}
		}.listFilesInOutput();
	}

	@Test
	public void testSimpleFileFilter() throws IOException {
		VCSEngine vp = new TestVCSEngine() {
			@Override
			public Path getOutput() {
				return folder.getRoot().toPath();
			}

			@Override
			public FilenameFilter createVCSFileFilter() {
				return (dir, name) -> !name.startsWith("f");
			}
		};
		assertEquals(7, vp.listFilesInOutput().size());
	}

	@Test
	public void testSimpleDirectoryFilter() throws IOException {
		VCSEngine vp = new TestVCSEngine() {
			@Override
			public Path getOutput() {
				return folder.getRoot().toPath();
			}

			@Override
			public FilenameFilter createVCSFileFilter() {
				return (dir, name) -> !dir.getName().equals("c");
			}
		};
		assertEquals(7, vp.listFilesInOutput().size());
	}
}
