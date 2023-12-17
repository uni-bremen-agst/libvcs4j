package de.unibremen.informatik.st.libvcs4j;

import de.unibremen.informatik.st.libvcs4j.exception.IllegalIntervalException;
import de.unibremen.informatik.st.libvcs4j.exception.IllegalTargetException;
import de.unibremen.informatik.st.libvcs4j.testutils.ResourceExtractor;
import junit.framework.AssertionFailedError;
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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * This class defines several basic tests for {@link VCSEngine}
 * implementations. Each engine should extend this test class and implement the
 * required methods.
 */
@SuppressWarnings({"Duplicates", "ConstantConditions"})
public abstract class VCSBaseTest {

	private Path input;

	private Path target;

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Before
	public void setUp() throws Exception {
		final ResourceExtractor extractor = new ResourceExtractor();
		input = extractor.extractTarGZ(getTarGZFile());
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

	protected VCSEngineBuilder createBuilder() {
		VCSEngineBuilder builder = VCSEngineBuilder.of(input.toString());
		setEngine(builder);
		return builder;
	}

	private List<String> readIds(String idFile) throws IOException {
		InputStream is = getClass().getResourceAsStream("/" + idFile);
		return IOUtils.readLines(is, StandardCharsets.UTF_8);
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
		assertThat(builder.build()).isNotNull();
	}

	@Test
	public void sinceBeforeUntil() {
		VCSEngineBuilder builder = createBuilder();
		LocalDateTime dt = LocalDateTime.of(2010, 1, 1, 0, 0);
		builder.withSince(dt);
		builder.withUntil(dt.plusSeconds(1));
		assertThat(builder.build()).isNotNull();
	}

	@Test
	public void startNegative() {
		VCSEngineBuilder builder = createBuilder();
		builder.withStartIdx(-1);
		thrown.expect(IllegalIntervalException.class);
		builder.build();
	}

	@Test
	public void startZero() {
		VCSEngineBuilder builder = createBuilder();
		builder.withStartIdx(0);
		assertThat(builder.build()).isNotNull();
	}

	@Test
	public void startPositive() {
		VCSEngineBuilder builder = createBuilder();
		builder.withStartIdx(10);
		assertThat(builder.build()).isNotNull();
	}

	@Test
	public void endNegative() {
		VCSEngineBuilder builder = createBuilder();
		builder.withEndIdx(-1);
		thrown.expect(IllegalIntervalException.class);
		builder.build();
	}

	@Test
	public void endZero() {
		VCSEngineBuilder builder = createBuilder();
		builder.withEndIdx(0);
		thrown.expect(IllegalIntervalException.class);
		builder.build();
	}

	@Test
	public void endPositive() {
		VCSEngineBuilder builder = createBuilder();
		builder.withEndIdx(10);
		assertThat(builder.build()).isNotNull();
	}

	@Test
	public void startLessEnd() {
		VCSEngineBuilder builder = createBuilder();
		builder.withStartIdx(10);
		builder.withEndIdx(11);
		assertThat(builder.build()).isNotNull();
	}

	@Test
	public void startEqualsEnd() {
		VCSEngineBuilder builder = createBuilder();
		builder.withStartIdx(4);
		builder.withEndIdx(4);
		thrown.expect(IllegalIntervalException.class);
		builder.build();
	}

	@Test
	public void startGreaterEnd() {
		VCSEngineBuilder builder = createBuilder();
		builder.withStartIdx(7);
		builder.withEndIdx(6);
		thrown.expect(IllegalIntervalException.class);
		builder.build();
	}

	@Test
	public void processAll() throws IOException {
		List<String> commitIds = readIds(getRootCommitIdFile());
		List<String> revisionIds = readIds(getRootRevisionIdFile());

		assertEquals(commitIds.size(), revisionIds.size());
		assertTrue(commitIds.size() >= 20);

		VCSEngine engine = createBuilder().build();
		List<RevisionRange> ranges = new ArrayList<>();
		for (RevisionRange range : engine) {
			ranges.add(range);
			assertThat(range.getFileChanges())
					.allSatisfy(fc -> {
						if (fc.getType() == FileChange.Type.ADD) {
							assertThat(fc.getNewFile().get().toPath())
									.isRegularFile();
						} else if (fc.getType() == FileChange.Type.REMOVE) {
							assertThat(fc.getOldFile().get().toPath())
									.doesNotExist();
						} else if (fc.getType() == FileChange.Type.MODIFY) {
							assertThat(fc.getOldFile().get().toPath())
									.isRegularFile();
							assertThat(fc.getNewFile().get().toPath())
									.isRegularFile();
							assertThat(fc.getOldFile().get().toPath())
									.isEqualTo(fc.getNewFile()
											.get().toPath());
						} else if (fc.getType() == FileChange.Type.RELOCATE) {
							assertThat(fc.getOldFile().get().toPath())
									.doesNotExist();
							assertThat(fc.getNewFile().get().toPath())
									.isRegularFile();
							assertThat(fc.getOldFile().get().toPath())
									.isNotEqualTo(fc.getNewFile()
											.get().toPath());
						}
					});
		}

		assertThat(ranges).hasSize(commitIds.size());
		for (int i = 0; i < ranges.size(); i++) {
			RevisionRange r = ranges.get(i);
			assertThat(r.getOrdinal())
					.isEqualTo(i + 1);
			assertThat(r.getLatestCommit().getId())
					.isEqualTo(commitIds.get(i));
			assertThat(r.getCurrent().getId())
					.isEqualTo(revisionIds.get(i));
		}
	}

	@Test
	public void processSubDir() throws IOException {
		List<String> commitIds = readIds(getSubDirCommitIdFile());
		List<String> revisionIds = readIds(getSubDirRevisionIdFile());
		assertEquals(commitIds.size(), revisionIds.size());
		assertFalse(commitIds.isEmpty());
		assertTrue(commitIds.size() < readIds(getRootCommitIdFile()).size());
		VCSEngine engine = createBuilder().withRoot(getSubDir()).build();
		List<RevisionRange> ranges = new ArrayList<>();
		engine.forEach(ranges::add);
		assertEquals(commitIds.size(), ranges.size());
		for (int i = 0; i < ranges.size(); i++) {
			RevisionRange r = ranges.get(i);
			assertEquals(i + 1, r.getOrdinal());
			assertEquals(commitIds.get(i), r.getLatestCommit().getId());
			assertEquals(revisionIds.get(i), r.getCurrent().getId());
		}
	}

	@Test
	public void rangeInterval0To3() throws IOException {
		List<String> commitIds = readIds(getRootCommitIdFile());
		List<String> revisionIds = readIds(getRootRevisionIdFile());
		assertEquals(commitIds.size(), revisionIds.size());
		VCSEngine engine = createBuilder()
				.withStartIdx(0)
				.withEndIdx(3)
				.build();

		List<RevisionRange> ranges = new ArrayList<>();
		engine.forEach(ranges::add);
		assertEquals(3, ranges.size());
		for (int i = 3; i < ranges.size(); i++) {
			RevisionRange r = ranges.get(i);
			assertEquals(i + 1, r.getOrdinal());
			assertEquals(commitIds.get(i), r.getLatestCommit().getId());
			assertEquals(revisionIds.get(i), r.getCurrent().getId());
		}
	}

	@Test
	public void rangeInterval5To9() throws IOException {
		List<String> commitIds = readIds(getRootCommitIdFile());
		List<String> revisionIds = readIds(getRootRevisionIdFile());
		assertEquals(commitIds.size(), revisionIds.size());
		VCSEngine engine = createBuilder()
				.withStartIdx(5)
				.withEndIdx(9)
				.build();

		List<RevisionRange> ranges = new ArrayList<>();
		engine.forEach(ranges::add);
		assertEquals(4, ranges.size());
		for (int i = 0; i < ranges.size(); i++) {
			RevisionRange r = ranges.get(i);
			assertEquals(i + 1, r.getOrdinal());
			assertEquals(commitIds.get(i + 5), r.getLatestCommit().getId());
			assertEquals(revisionIds.get(i + 5), r.getCurrent().getId());
		}
	}

	@Test
	public void rangeIntervalTo2() throws IOException {
		List<String> commitIds = readIds(getRootCommitIdFile());
		List<String> revisionIds = readIds(getRootRevisionIdFile());
		assertEquals(commitIds.size(), revisionIds.size());
		VCSEngine engine = createBuilder()
				.withEndIdx(2)
				.build();

		List<RevisionRange> ranges = new ArrayList<>();
		engine.forEach(ranges::add);
		assertEquals(2, ranges.size());
		for (int i = 0; i < ranges.size(); i++) {
			RevisionRange r = ranges.get(i);
			assertEquals(i + 1, r.getOrdinal());
			assertEquals(commitIds.get(i), r.getLatestCommit().getId());
			assertEquals(revisionIds.get(i), r.getCurrent().getId());
		}
	}

	@Test
	public void rangeIntervalLast3() throws IOException {
		List<String> commitIds = readIds(getRootCommitIdFile());
		List<String> revisionIds = readIds(getRootRevisionIdFile());
		assertEquals(commitIds.size(), revisionIds.size());
		int start = commitIds.size() - 3;
		VCSEngine engine = createBuilder()
				.withStartIdx(start)
				.build();

		List<RevisionRange> ranges = new ArrayList<>();
		engine.forEach(ranges::add);
		assertEquals(3, ranges.size());
		for (int i = 0; i < ranges.size(); i++) {
			RevisionRange r = ranges.get(i);
			assertEquals(i + 1, r.getOrdinal());
			assertEquals(commitIds.get(i + start),
					r.getLatestCommit().getId());
			assertEquals(revisionIds.get(i + start),
					r.getCurrent().getId());
		}
	}

	@Test
	public void revisionIntervalIdx0To5() throws IOException {
		List<String> commitIds = readIds(getRootCommitIdFile());
		List<String> revisionIds = readIds(getRootRevisionIdFile());
		assertEquals(commitIds.size(), revisionIds.size());
		String from = commitIds.get(0);
		String to = commitIds.get(5);
		VCSEngine engine = createBuilder()
				.withFrom(from)
				.withTo(to)
				.build();
		List<RevisionRange> ranges = new ArrayList<>();
		engine.forEach(ranges::add);
		assertEquals(6, ranges.size());
		for (int i = 0; i < ranges.size(); i++) {
			RevisionRange r = ranges.get(i);
			assertEquals(commitIds.get(i), r.getLatestCommit().getId());
			assertEquals(revisionIds.get(i), r.getCurrent().getId());
		}
	}

	@Test
	public void revisionIntervalIdx6To8() throws IOException {
		List<String> commitIds = readIds(getRootCommitIdFile());
		List<String> revisionIds = readIds(getRootRevisionIdFile());
		assertEquals(commitIds.size(), revisionIds.size());
		String from = commitIds.get(6);
		String to = commitIds.get(8);
		VCSEngine engine = createBuilder()
				.withFrom(from)
				.withTo(to)
				.build();
		List<RevisionRange> ranges = new ArrayList<>();
		engine.forEach(ranges::add);
		assertEquals(3, ranges.size());
		for (int i = 0; i < ranges.size(); i++) {
			RevisionRange r = ranges.get(i);
			assertEquals(commitIds.get(i + 6), r.getLatestCommit().getId());
			assertEquals(revisionIds.get(i + 6), r.getCurrent().getId());
		}
	}

	@Test
	public void revisionIntervalIdxTo3() throws IOException {
		List<String> commitIds = readIds(getRootCommitIdFile());
		List<String> revisionIds = readIds(getRootRevisionIdFile());
		assertEquals(commitIds.size(), revisionIds.size());
		String to = commitIds.get(3);
		VCSEngine engine = createBuilder()
				.withTo(to)
				.build();
		List<RevisionRange> ranges = new ArrayList<>();
		engine.forEach(ranges::add);
		assertEquals(4, ranges.size());
		for (int i = 0; i < ranges.size(); i++) {
			RevisionRange r = ranges.get(i);
			assertEquals(commitIds.get(i), r.getLatestCommit().getId());
			assertEquals(revisionIds.get(i), r.getCurrent().getId());
		}
	}

	@Test
	public void revisionIntervalLast4() throws IOException {
		List<String> commitIds = readIds(getRootCommitIdFile());
		List<String> revisionIds = readIds(getRootRevisionIdFile());
		assertEquals(commitIds.size(), revisionIds.size());
		int start = commitIds.size() - 4;
		VCSEngine engine = createBuilder()
				.withFrom(commitIds.get(start))
				.build();

		List<RevisionRange> ranges = new ArrayList<>();
		engine.forEach(ranges::add);
		assertEquals(4, ranges.size());
		for (int i = 0; i < ranges.size(); i++) {
			RevisionRange r = ranges.get(i);
			assertEquals(i + 1, r.getOrdinal());
			assertEquals(commitIds.get(i + start),
					r.getLatestCommit().getId());
			assertEquals(revisionIds.get(i + start),
					r.getCurrent().getId());
		}
	}

	@Test
	public void iterateFirst7() throws IOException {
		List<String> commitIds = readIds(getRootCommitIdFile());
		List<String> revisionIds = readIds(getRootRevisionIdFile());
		assertEquals(commitIds.size(), revisionIds.size());
		VCSEngine engine = createBuilder()
				.withTo(commitIds.get(6))
				.build();

		int i = 0;
		for (RevisionRange range : engine) {
			assertEquals(i + 1, range.getOrdinal());
			assertEquals(commitIds.get(i),
					range.getLatestCommit().getId());
			assertEquals(revisionIds.get(i),
					range.getCurrent().getId());
			i++;
		}
	}

	@Test
	public void readAllBytesOfOldRevision() throws IOException {
		VCSEngine engine = createBuilder()
				.withEndIdx(5)
				.build();

		Revision lastRevision = null;
		for (RevisionRange range : engine) {
			if (lastRevision != null) {
				for (VCSFile file : lastRevision.getFiles()) {
					file.readAllBytes();
				}
			}
			lastRevision = range.getCurrent();
		}
		assertThat(lastRevision).isNotNull();
	}

	@Test
	public void readLineInfoOfOldRevision() throws IOException {
		VCSEngine engine = createBuilder()
				.withStartIdx(3)
				.withEndIdx(7)
				.build();

		Revision lastRevision = null;
		for (RevisionRange range : engine) {
			if (lastRevision != null) {
				for (VCSFile file : lastRevision.getFiles()) {
					List<String> lines = file.readLinesWithEOL();
					List<LineInfo> lineInfo = file.readLineInfo();
					assertThat(lineInfo.size()).isEqualTo(lines.size());
					for (int i = 0; i < lineInfo.size(); i++) {
						LineInfo info = lineInfo.get(i);
						assertThat(info.getLine()).isEqualTo(i + 1);
						assertThat(lines.get(i)).startsWith(info.getContent());
					}
				}
			}
			lastRevision = range.getCurrent();
		}
	}

	@Test
	public void computeDiffOfOldRange() throws IOException {
		VCSEngine engine = createBuilder()
				.withStartIdx(5)
				.withEndIdx(9)
				.build();

		RevisionRange lastRange = null;
		for (RevisionRange range : engine) {
			if (lastRange != null) {
				for (FileChange fChange : lastRange.getFileChanges()) {
					List<LineChange> lChange = fChange.computeDiff();
					assertThat(lChange).isNotEmpty();
				}
			}
			lastRange = range;
		}
	}

	@Test
	public void latestRevision() throws IOException {
		List<String> revisionIds = readIds(getRootRevisionIdFile());
		VCSEngine engine = createBuilder()
				.withLatestRevision()
				.build();

		RevisionRange range = engine.next()
				.orElseThrow(AssertionFailedError::new);
		assertThat(range.getCurrent().getId())
				.isEqualTo(revisionIds.get(revisionIds.size() - 1));
	}

	@Test
	public void latestRevisionWithNotExistingRoot() throws IOException {
		VCSEngine engine = createBuilder()
				.withLatestRevision()
				.withRoot("yf928y298fy4f32f98fy39fy38943yf938y")
				.build();
		assertFalse(engine.next().isPresent());
	}

	/**
	 * Returns the path of the archive to extract, i.e. 'javacpp.tar.gz'.
	 *
	 * @return
	 * 		The path of the archive to extract.
	 */
	protected abstract String getTarGZFile();

	/**
	 * Returns the path within {@link #getTarGZFile()} that contains the VCS to
	 * process.
	 *
	 * @return
	 * 		The path within {@link #getTarGZFile()} that contains the VCS to
	 * 		process.
	 */
	protected abstract String getFolderInTarGZ();

	/**
	 * Returns the path of the file containing the commit ids of the root
	 * directory of the VCS to process, i.e. 'javacpp_ids.txt'. It is assumed
	 * that the file is UTF-8 encoded, contains at least 20 ids, and stores the
	 * ids in ascending order.
	 *
	 * @return
	 * 		The path of the file containing the commit ids of the root
	 * 		directory of the VCS to process.
	 */
	protected abstract String getRootCommitIdFile();

	/**
	 * Same as {@link #getRootCommitIdFile()}, but for revision ids. The
	 * default implementation returns {@link #getRootCommitIdFile()} as most
	 * VCS do not differ between commit and revision ids.
	 *
	 * @return
	 * 		Same as {@link #getRootCommitIdFile()}, but for revision ids.
	 */
	protected String getRootRevisionIdFile() {
		return getRootCommitIdFile();
	}

	/**
	 * Returns the path of the subdirectory corresponding to
	 * {@link #getSubDirCommitIdFile()}. The path will be passed to
	 * {@link VCSEngineBuilder#withRoot(String)}.
	 *
	 * @return
	 * 		The path of the subdirectory corresponding to
	 * 		{@link #getSubDirCommitIdFile()}.
	 */
	protected abstract String getSubDir();

	/**
	 * Returns the path of the file containing the commit ids of an arbitrary
	 * subdirectory of the VCS to process, i.e. 'javacpp_subdir_ids.txt'. It is
	 * assumed that the file is UTF-8 encoded, contains less ids than the file
	 * returned by {@link #getRootCommitIdFile()} (but still more than 0), and
	 * stores the ids in ascending order.
	 *
	 * @return
	 * 		The path of the file containing the commit ids of an arbitrary
	 * 		subdirectory of the VCS to process.
	 */
	protected abstract String getSubDirCommitIdFile();

	/**
	 * Same as {@link #getSubDirCommitIdFile()}, but for revision ids. The
	 * default implementation returns {@link #getSubDirCommitIdFile()} as most
	 * VCS do not differ between commit and revision ids.
	 *
	 * @return
	 * 		Same as {@link #getSubDirCommitIdFile()}, but for revision ids.
	 */
	protected String getSubDirRevisionIdFile() {
		return getSubDirCommitIdFile();
	}

	/**
	 * Sets the engine used to process the VCS.
	 *
	 * @param builder
	 * 		The builder whose engine is set.
	 */
	protected abstract void setEngine(VCSEngineBuilder builder);
}
