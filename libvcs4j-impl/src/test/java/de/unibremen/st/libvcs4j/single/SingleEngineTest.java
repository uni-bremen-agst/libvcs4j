package de.unibremen.st.libvcs4j.single;

import de.unibremen.st.libvcs4j.FileChange;
import de.unibremen.st.libvcs4j.VCSEngine;
import de.unibremen.st.libvcs4j.Version;
import de.unibremen.st.libvcs4j.VCSEngineBuilder;
import de.unibremen.st.libvcs4j.filesystem.SingleEngine;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@SuppressWarnings({"deprecation", "ConstantConditions"})
public class SingleEngineTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	@Test
	public void testSingleFile() throws IOException {
		final VCSEngine engine = new SingleEngine(
				folder.newFile("single.txt").toPath());
		final String pathOfFile = Paths.get(
				folder.getRoot().toString(), "single.txt").toString();

		assertEquals(1, engine.listFilesInOutput().size());
		assertEquals(pathOfFile, engine.listFilesInOutput().get(0));

		final Optional<Version> optional = engine.next();
		Assert.assertTrue(optional.isPresent());
		final Version version = optional.get();
		assertEquals(1, version.getFileChanges().size());

		final FileChange fileChange = version.getFileChanges().get(0);
		assertEquals(FileChange.Type.ADD, fileChange.getType());
		assertEquals(pathOfFile, fileChange.getNewFile().get().getPath());
		// there is no relative path for a single file
		assertEquals("", fileChange.getNewFile().get().getRelativePath());
	}

	@Test
	public void testFolderWithSingleFile() throws IOException {
		final String path = folder.newFile("file.java").getAbsolutePath();
		final VCSEngine engine = new SingleEngine(folder.getRoot().toPath());

		assertEquals(1, engine.listFilesInOutput().size());
		assertEquals(path, engine.listFilesInOutput().get(0));

		final Optional<Version> optional = engine.next();
		Assert.assertTrue(optional.isPresent());
		final Version version = optional.get();
		assertEquals(1, version.getFileChanges().size());

		final FileChange fileChange = version.getFileChanges().get(0);
		assertEquals(FileChange.Type.ADD, fileChange.getType());
		assertEquals(path, fileChange.getNewFile().get().getPath());
		assertEquals("file.java",
				fileChange.getNewFile().get().getRelativePath());
	}

	@Test
	public void testFolderWithMultipleFiles() throws IOException {
		folder.newFile("first.java");
		final File sub = folder.newFolder("sub");
		Files.createFile(Paths.get(sub.getAbsolutePath(), "second.java"));
		final String first = Paths.get(folder.getRoot().getAbsolutePath(),
				"first.java").toString();
		final String second = Paths.get(sub.getAbsolutePath(),
				"second.java").toString();

		final VCSEngine engine = new SingleEngine(folder.getRoot().toPath());
		assertEquals(2, engine.listFilesInOutput().size());
		assertTrue(engine.listFilesInOutput().contains(first));
		assertTrue(engine.listFilesInOutput().contains(second));

		final Optional<Version> optional = engine.next();
		Assert.assertTrue(optional.isPresent());
		final Version version = optional.get();
		assertEquals(2, version.getFileChanges().size());

		final FileChange fc1 = version.getFileChanges().get(0);
		final FileChange fc2 = version.getFileChanges().get(1);
		assertEquals(FileChange.Type.ADD, fc1.getType());
		assertEquals(FileChange.Type.ADD, fc2.getType());
	}

	@Test
	public void testConstructorWithNull() {
		thrown.expect(NullPointerException.class);
		new SingleEngine(null);
	}

	@Test
	public void testNonExistingInput() {
		thrown.expect(IllegalArgumentException.class);
		new SingleEngine(Paths.get("hf98p34ycp4csfny9p2841!$@#$jrw8fd9"));
	}

	@Test
	public void testExistingInput() {
		final VCSEngine engine = new SingleEngine(folder.getRoot().toPath());
		assertEquals(Paths.get(engine.getRepository()), engine.getTarget());
	}

	@Test
	public void testBuilderTarget() {
		final VCSEngine engine = VCSEngineBuilder
				.of(folder.getRoot().getAbsolutePath())
				.withTarget("asdf")
				.build();
		assertEquals(Paths.get(engine.getRepository()), engine.getTarget());
	}
}
