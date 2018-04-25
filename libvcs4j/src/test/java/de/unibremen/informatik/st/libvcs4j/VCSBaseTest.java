package de.unibremen.informatik.st.libvcs4j;

import de.unibremen.informatik.st.libvcs4j.exception.IllegalIntervalException;
import de.unibremen.informatik.st.libvcs4j.exception.IllegalTargetException;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public abstract class VCSBaseTest {

	private Path input;

	private Path target;

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Before
	public void setUp() throws Exception {
		final Extractor extractor = new Extractor();
		input = extractor.extractTarGZResource(getTarGZFile());
		input = input.resolve(getFolderInTarGZ());
		// create a random path that does not exist
		target = Files.createTempDirectory(null);
		Files.delete(target);
	}

	protected Path getInput() {
		return input;
	}

	protected Path getTarget() {
		return target;
	}

	private VCSEngineBuilder createBuilder() {
		VCSEngineBuilder builder = VCSEngineBuilder.of(input.toString());
		setEngine(builder);
		return builder;
	}

	private List<String> readIds() throws IOException {
		InputStream is = getClass().getResourceAsStream("/" + getIdFile());
		String input = IOUtils.toString(is, StandardCharsets.UTF_8);
		String[] ids = input.split("\n");
		return Arrays.asList(ids);
	}

	@Test
	public void existingTargetDirectory() throws IOException {
		Path tmpDir = Files.createTempDirectory(null);
		VCSEngineBuilder builder = createBuilder();
		builder.withTarget(tmpDir);
		thrown.expect(IllegalTargetException.class);
		builder.build();
	}

	@Test
	public void existingTargetFile() throws IOException {
		Path tmpFile = Files.createTempFile(null, null);
		VCSEngineBuilder builder = createBuilder();
		builder.withTarget(tmpFile);
		thrown.expect(IllegalTargetException.class);
		builder.build();
	}

	@Test
	public void notExistingRoot() throws IOException {
		VCSEngine engine = createBuilder()
				.withRoot("yf928y298fy4f32f98fy39fy38943yf938y")
				.build();
		assertFalse(engine.next().isPresent());
	}

	@Test
	public void sinceAfterUntil() {
		VCSEngineBuilder builder = createBuilder();
		LocalDateTime dt = LocalDateTime.of(2010, 1, 1, 0, 0);
		builder.withSince(dt);
		builder.withUntil(dt.minusSeconds(1));
		thrown.expect(IllegalIntervalException.class);
		builder.build();
	}

	@Test
	public void sinceEqualsUntil() {
		VCSEngineBuilder builder = createBuilder();
		LocalDateTime dt = LocalDateTime.of(2010, 1, 1, 0, 0);
		builder.withSince(dt);
		builder.withUntil(dt);
		builder.build();
	}

	@Test
	public void sinceBeforeUntil() {
		VCSEngineBuilder builder = createBuilder();
		LocalDateTime dt = LocalDateTime.of(2010, 1, 1, 0, 0);
		builder.withSince(dt);
		builder.withUntil(dt.plusSeconds(1));
		builder.build();
	}

	@Test
	public void startNegative() {
		VCSEngineBuilder builder = createBuilder();
		builder.withStart(-1);
		thrown.expect(IllegalIntervalException.class);
		builder.build();
	}

	@Test
	public void startZero() {
		VCSEngineBuilder builder = createBuilder();
		builder.withStart(0);
		builder.build();
	}

	@Test
	public void startPositive() {
		VCSEngineBuilder builder = createBuilder();
		builder.withStart(10);
		builder.build();
	}

	@Test
	public void endNegative() {
		VCSEngineBuilder builder = createBuilder();
		builder.withEnd(-1);
		thrown.expect(IllegalIntervalException.class);
		builder.build();
	}

	@Test
	public void endZero() {
		VCSEngineBuilder builder = createBuilder();
		builder.withEnd(0);
		thrown.expect(IllegalIntervalException.class);
		builder.build();
	}

	@Test
	public void endPositive() {
		VCSEngineBuilder builder = createBuilder();
		builder.withEnd(10);
		builder.build();
	}

	@Test
	public void startLessEnd() {
		VCSEngineBuilder builder = createBuilder();
		builder.withStart(10);
		builder.withEnd(11);
		builder.build();
	}

	@Test
	public void startEqualsEnd() {
		VCSEngineBuilder builder = createBuilder();
		builder.withStart(4);
		builder.withEnd(4);
		thrown.expect(IllegalIntervalException.class);
		builder.build();
	}

	@Test
	public void startGreaterEnd() {
		VCSEngineBuilder builder = createBuilder();
		builder.withStart(7);
		builder.withEnd(6);
		thrown.expect(IllegalIntervalException.class);
		builder.build();
	}

	@Test
	public void processAll() throws IOException {
		List<String> commitIds = readIds();
		VCSEngine engine = createBuilder().build();
		List<Version> versions = new ArrayList<>();
		engine.forEach(versions::add);
		assertEquals(commitIds.size(), versions.size());
		for (int i = 0; i < versions.size(); i++) {
			assertEquals(commitIds.get(i),
					versions.get(i).getLatestCommit().getId());
		}
	}

	@Test
	public void rangeInterval0To3() throws IOException {
		VCSEngine engine = createBuilder()
				.withStart(0)
				.withEnd(3)
				.build();
		Optional<Version> version;

		version = engine.next();
		assertTrue(version.isPresent());
		assertTrue(version.get().getOrdinal() == 1);

		version = engine.next();
		assertTrue(version.isPresent());
		assertTrue(version.get().getOrdinal() == 2);

		version = engine.next();
		assertTrue(version.isPresent());
		assertTrue(version.get().getOrdinal() == 3);

		version = engine.next();
		assertFalse(version.isPresent());
	}

	@Test
	public void rangeInterval5To9() throws IOException {
		VCSEngine engine = createBuilder()
				.withStart(5)
				.withEnd(9)
				.build();
		Optional<Version> version;

		version = engine.next();
		assertTrue(version.isPresent());
		assertTrue(version.get().getOrdinal() == 1);

		version = engine.next();
		assertTrue(version.isPresent());
		assertTrue(version.get().getOrdinal() == 2);

		version = engine.next();
		assertTrue(version.isPresent());
		assertTrue(version.get().getOrdinal() == 3);

		version = engine.next();
		assertTrue(version.isPresent());
		assertTrue(version.get().getOrdinal() == 4);

		version = engine.next();
		assertFalse(version.isPresent());
	}

	@Test
	public void rangeIntervalTo2() throws IOException {
		VCSEngine engine = createBuilder()
				.withEnd(2)
				.build();
		Optional<Version> version;

		version = engine.next();
		assertTrue(version.isPresent());
		assertTrue(version.get().getOrdinal() == 1);

		version = engine.next();
		assertTrue(version.isPresent());
		assertTrue(version.get().getOrdinal() == 2);

		version = engine.next();
		assertFalse(version.isPresent());
	}

	@Test
	public void revisionIntervalIdx0To5() throws IOException {
		List<String> commitIds = readIds();
		String from = commitIds.get(0);
		String to = commitIds.get(5);
		VCSEngine engine = createBuilder()
				.withFrom(from)
				.withTo(to)
				.build();
		List<Version> versions = new ArrayList<>();
		engine.forEach(versions::add);
		assertEquals(6, versions.size());
		for (int i = 0; i < versions.size(); i++) {
			assertEquals(commitIds.get(i),
					versions.get(i).getLatestCommit().getId());
		}
	}

	@Test
	public void revisionIntervalIdx6To8() throws IOException {
		List<String> commitIds = readIds();
		String from = commitIds.get(6);
		String to = commitIds.get(8);
		VCSEngine engine = createBuilder()
				.withFrom(from)
				.withTo(to)
				.build();
		List<Version> versions = new ArrayList<>();
		engine.forEach(versions::add);
		assertEquals(3, versions.size());
		for (int i = 0; i < versions.size(); i++) {
			assertEquals(commitIds.get(i+6),
					versions.get(i).getLatestCommit().getId());
		}
	}

	protected abstract String getTarGZFile();
	protected abstract String getFolderInTarGZ();
	protected abstract void setEngine(VCSEngineBuilder builder);
	protected abstract String getIdFile();
}
