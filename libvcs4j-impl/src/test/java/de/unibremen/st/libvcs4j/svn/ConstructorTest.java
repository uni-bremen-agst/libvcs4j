package de.unibremen.st.libvcs4j.svn;

import de.unibremen.st.libvcs4j.VCSBaseTest;
import de.unibremen.st.libvcs4j.VCSEngineBuilder;
import de.unibremen.st.libvcs4j.exception.IllegalIntervalException;
import de.unibremen.st.libvcs4j.exception.IllegalRepositoryException;
import de.unibremen.st.libvcs4j.exception.IllegalTargetException;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import static de.unibremen.st.libvcs4j.svn.SVNEngine.MINIMUM_DATETIME;
import static org.junit.Assert.assertEquals;

@SuppressWarnings("deprecation")
public class ConstructorTest extends VCSBaseTest {

	@Override
	public String getTarGZFile() {
		return "roolie.tar.gz";
	}

	@Override
	public String getFolderInTarGZ() {
		return "roolie";
	}

	@Test
	public void createProviderWithBuilder() throws IOException {
		final SVNEngine engine =
				(SVNEngine) VCSEngineBuilder
						.of(input.toString())
						.withSVN()
						.build();
		assertEquals(MINIMUM_DATETIME, engine.getSince());
		assertEquals("", engine.getRoot());
		assertEquals(64, engine.listRevisions().size());
	}

	@Test
	public void unsupportedProtocol() {
		thrown.expect(IllegalRepositoryException.class);
		new SVNEngine(
				"unsupported://path",
				"",
				output,
				LocalDateTime.now().minusYears(1),
				LocalDateTime.now());
	}

	@Test
	public void notExistingRepository() {
		thrown.expect(IllegalRepositoryException.class);
		new SVNEngine(
				"file://" + "3hlkjf3l48@#%^&hwc8lv%&43pt2131",
				"",
				output,
				LocalDateTime.now().minusYears(1),
				LocalDateTime.now());
	}

	@Test
	public void regularFileRepository() throws IOException {
		final Path file = Files.createTempFile(null, null);
		file.toFile().deleteOnExit();

		thrown.expect(IllegalRepositoryException.class);
		new SVNEngine(
				"file://" + file.toString(),
				"",
				output,
				LocalDateTime.now().minusYears(1),
				LocalDateTime.now());
	}

	@Test
	public void notExistingRoot() throws IOException {
		final SVNEngine engine =
                (SVNEngine) VCSEngineBuilder
				.of(input.toString())
				.withSVN()
				.withRoot("yf928y298fy4f32f98fy39fy38943yf938y")
				.build();
		assertEquals(0, engine.listRevisions().size());
	}

	@Test
	public void existingTarget() throws IOException {
		final Path target = Files.createTempDirectory(null);
		target.toFile().deleteOnExit();

		thrown.expect(IllegalTargetException.class);
		new SVNEngine(
				"file://" + input.toString(),
				"",
				target,
				LocalDateTime.now().minusYears(1),
				LocalDateTime.now());

	}

	@Test
	public void untilBeforeSince() {
		final LocalDateTime now = LocalDateTime.now();
		thrown.expect(IllegalIntervalException.class);
		new SVNEngine(
				"file://" + input.toString(),
				"",
				output,
				now,
				now.minusSeconds(1));
	}

	@Test
	public void untilEqualsSince() throws IOException {
		final LocalDateTime now = LocalDateTime.now();
		final SVNEngine engine = new SVNEngine(
				"file://" + input.toString(),
				"",
				output,
				now,
				now);
		assertEquals(1, engine.listRevisions().size());
	}

	@Test
	public void toBeforeFrom() {
		thrown.expect(IllegalIntervalException.class);
		new SVNEngine(
				"file://" + input.toString(),
				"",
				output,
				"100",
				"1");
	}

	@Test
	public void toEqualsFrom() throws IOException {
		for (int i = 1; i <= 64; i++) {
			final String rev = String.valueOf(i);
			final SVNEngine provider = new SVNEngine(
					"file://" + input.toString(),
					"",
					output,
					rev,
					rev);
			assertEquals(1, provider.listRevisions().size());
			assertEquals(String.valueOf(i), provider.listRevisions().get(0));
		}
	}

	@Test
	public void invalidMinimumDate() throws IOException {
		final SVNEngine provider = new SVNEngine(
		        "file://" + input.toString(),
                "",
                output,
                LocalDateTime.of(1900, 1, 1, 0, 0),
                LocalDateTime.of(3000, 1, 1, 0, 0));
		assertEquals(MINIMUM_DATETIME, provider.getSince());
		assertEquals(64, provider.listRevisions().size());
	}

	@Test
	public void negativeFrom() throws IOException {
		final SVNEngine provider =
                (SVNEngine) VCSEngineBuilder
				.of(input.toString())
				.withSVN()
				.withFromRevision("-10")
				.withToRevision("64")
				.build();
		final List<String> revisions = provider.listRevisions();
		for (int i = 0; i < revisions.size(); i++) {
			assertEquals(String.valueOf(i+1), revisions.get(i));
		}
	}

	@Test
	public void moreThanMaximumTo() throws IOException {
		final SVNEngine provider =
                (SVNEngine) VCSEngineBuilder
				.of(input.toString())
				.withSVN()
				.withFromRevision("1")
				.withToRevision("100")
				.build();
		final List<String> revisions = provider.listRevisions();
		for (int i = 0; i < revisions.size(); i++) {
			assertEquals(String.valueOf(i+1), revisions.get(i));
		}
	}
}
