package de.unibremen.st.libvcs4j;

import org.junit.Assert;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Some tests must be adjusted if new files are added or existing files are
 * removed from "src/main/java/de/unibremen/st/libvcs4j".
 */
public class VCSEngineTest {

	@Test
	public void testListFilesInOutput() throws IOException {
		String p = "src/main/java/de/unibremen/st/libvcs4j";
		VCSEngine vp = new TestVersionProvider() {
			@Override
			public Path getOutput() {
				return Paths.get(p).toAbsolutePath();
			}
		};
		Assert.assertEquals(11, vp.listFilesInOutput().size());
	}

	@Test
	public void testListFilesInOutputSubDirs() throws IOException {
		String p = "src/main/java/de/unibremen/st/";
		VCSEngine vp = new TestVersionProvider() {
			@Override
			public Path getOutput() {
				return Paths.get(p).toAbsolutePath();
			}
		};
		Assert.assertEquals(11, vp.listFilesInOutput().size());
	}

	@Test
	public void testListFilesInOutputSingleFile() throws IOException {
		String p = "src/main/java/de/unibremen/st/libvcs4j/VCSFile.java";
		VCSEngine vp = new TestVersionProvider() {
			@Override
			public Path getOutput() {
				return Paths.get(p).toAbsolutePath();
			}
		};
		Assert.assertEquals(1, vp.listFilesInOutput().size());
	}

	@Test(expected = FileNotFoundException.class)
	public void testNonExistingOutputDir() throws IOException {
		String p = "asdfhalf324hr789erher9c78vh3cr72ny48t784r7c8tycn87c3";
		new TestVersionProvider() {
			@Override
			public Path getOutput() {
				return Paths.get(p);
			}
		}.listFilesInOutput();
	}

	@Test
	public void testSimpleFileFilter() throws IOException {
		String p = "src/main/java/de/unibremen/st/libvcs4j";
		VCSEngine vp = new TestVersionProvider() {
			@Override
			public Path getOutput() {
				return Paths.get(p).toAbsolutePath();
			}

			@Override
			public FilenameFilter createVCSFileFilter() {
				return (dir, name) -> !name.startsWith("Version");
			}
		};
		Assert.assertEquals(10, vp.listFilesInOutput().size());
	}

	@Test
	public void testSimpleDirectoryFilter() throws IOException {
		String p = "src/main/java/de/unibremen/st/";
		VCSEngine vp = new TestVersionProvider() {
			@Override
			public Path getOutput() {
				return Paths.get(p).toAbsolutePath();
			}

			@Override
			public FilenameFilter createVCSFileFilter() {
				return (dir, name) -> !name.equals("libvcs4j");
			}
		};
		Assert.assertEquals(0, vp.listFilesInOutput().size());
	}
}
