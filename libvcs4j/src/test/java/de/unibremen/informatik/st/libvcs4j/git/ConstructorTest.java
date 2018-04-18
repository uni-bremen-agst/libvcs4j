package de.unibremen.informatik.st.libvcs4j.git;

import de.unibremen.informatik.st.libvcs4j.exception.IllegalTargetException;
import de.unibremen.informatik.st.libvcs4j.VCSBaseTest;
import de.unibremen.informatik.st.libvcs4j.VCSEngineBuilder;
import de.unibremen.informatik.st.libvcs4j.exception.IllegalIntervalException;
import de.unibremen.informatik.st.libvcs4j.exception.IllegalRepositoryException;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;

@SuppressWarnings("deprecation")
public class ConstructorTest extends VCSBaseTest {

	@Override
	public String getTarGZFile() {
		return "javacpp.tar.gz";
	}

	@Override
	public String getFolderInTarGZ() {
		return "javacpp";
	}

	@Test
	public void testUnsupportedProtocol() {
		thrown.expect(IllegalRepositoryException.class);
		new GitEngine(
				"unsupported://path",
				"",
				output,
				"master",
				LocalDateTime.now().minusYears(1),
				LocalDateTime.now());
	}

	@Test
	public void notExistingRepository() {
		thrown.expect(IllegalRepositoryException.class);
		new GitEngine(
				"file://" + "3hlkjf3l48@#%^&hwc8lv%&43pt2131",
				"",
				output,
				"master",
				LocalDateTime.now().minusYears(1),
				LocalDateTime.now());
	}

	@Test
	public void regularFileRepository() throws IOException {
		final Path file = Files.createTempFile(null, null);
		file.toFile().deleteOnExit();
		thrown.expect(IllegalRepositoryException.class);
		new GitEngine(
				"file://" + file.toString(),
				"",
				output,
				"master",
				LocalDateTime.now().minusYears(1),
				LocalDateTime.now());
	}

	@Test
	public void testExistingTarget() throws IOException {
		final Path target = Files.createTempDirectory(null);
		target.toFile().deleteOnExit();
		thrown.expect(IllegalTargetException.class);
		new GitEngine(
				"file://" + input.toString(),
				"",
				target,
				"master",
				LocalDateTime.now().minusYears(1),
				LocalDateTime.now());

	}

	@Test
	public void testUntilBeforeSince() {
		final LocalDateTime now = LocalDateTime.now();
		thrown.expect(IllegalIntervalException.class);
		new GitEngine(
				"file://" + input.toString(),
				"",
				output,
				"master",
				now,
				now.minusSeconds(1));
	}

	@Test
	public void testUntilEqualToSince() throws IOException {
		final LocalDateTime now = LocalDateTime.now();
		final GitEngine engine = new GitEngine(
				"file://" + input.toString(),
				"",
				output,
				"master",
				now,
				now);
		assertEquals(0, engine.listRevisions().size());
	}

	@Test
	public void notExistingRoot() throws IOException {
		final GitEngine engine =
				(GitEngine) VCSEngineBuilder
						.of("file://" + input.toString())
						.withGit()
						.withRoot("yf928y298fy4f32f98fy39fy38943yf938y")
						.build();
		assertEquals(0, engine.listRevisions().size());
	}
}
