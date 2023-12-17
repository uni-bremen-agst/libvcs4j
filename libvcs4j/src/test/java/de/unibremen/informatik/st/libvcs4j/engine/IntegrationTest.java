package de.unibremen.informatik.st.libvcs4j.engine;

import de.unibremen.informatik.st.libvcs4j.Commit;
import de.unibremen.informatik.st.libvcs4j.FileChange;
import de.unibremen.informatik.st.libvcs4j.Issue;
import de.unibremen.informatik.st.libvcs4j.LineInfo;
import de.unibremen.informatik.st.libvcs4j.RevisionRange;
import de.unibremen.informatik.st.libvcs4j.VCSEngine;
import de.unibremen.informatik.st.libvcs4j.VCSFile;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static de.unibremen.informatik.st.libvcs4j.FileChange.Type.*;
import static org.junit.Assert.*;

public class IntegrationTest {

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Before
	public void setUp() throws Exception {
		final File add = folder.newFolder("add");
		final File add1 = new File(add, "add1.java");
		assertTrue(add1.createNewFile());
		final File add2 = new File(add, "add2.java");
		assertTrue(add2.createNewFile());

		final File modify = folder.newFolder("modify");
		final File modify1 = new File(modify, "modify1.java");
		assertTrue(modify1.createNewFile());
		final File modify2 = new File(modify, "modify2.java");
		assertTrue(modify2.createNewFile());
		final File modify3 = new File(modify, "modify3.java");
		assertTrue(modify3.createNewFile());

		folder.newFile("relocate2.java");
	}

	@Test
	public void testFetchNextVersion() throws IOException {
		final VCSEngine vp = new TestClass(folder.getRoot().toPath());
		Optional<RevisionRange> optional;
		RevisionRange range;

		optional = vp.next();
		assertTrue(optional.isPresent());
		range = optional.get();
		assertEquals("1", range.getCurrent().getId());
		assertEquals(6, range.getFileChanges().size());
		assertTrue(range.isFirst());

		optional = vp.next();
		assertTrue(optional.isPresent());
		range = optional.get();
		assertEquals("2", range.getCurrent().getId());
		assertFalse(range.isFirst());
		assertEquals(7, range.getFileChanges().size());
		assertEquals(2, range.getFileChanges().stream()
			.filter(fileChange -> fileChange.getType() == ADD).count());
		assertEquals(1, range.getFileChanges().stream()
			.filter(fileChange -> fileChange.getType() == REMOVE).count());
		assertEquals(3, range.getFileChanges().stream()
			.filter(fileChange -> fileChange.getType() == MODIFY).count());
		assertEquals(1, range.getFileChanges().stream()
			.filter(fileChange -> fileChange.getType() == RELOCATE).count());
	}

	@Test
	public void testIterator() {
		final VCSEngine engine = new TestClass(folder.getRoot().toPath());

		for (final RevisionRange range : engine) {
			if (range.isFirst()) {
				assertEquals(6, range.getFileChanges().size());
			} else {
				assertEquals(7, range.getFileChanges().size());
			}
		}
		assertFalse(engine.iterator().hasNext());
	}

	@Test
	public void testListRevisions() throws IOException {
		final TestClass engine = new TestClass(folder.getRoot().toPath());
		final List<String> revisions = engine.listRevisions();
		assertEquals(3, revisions.size());
		assertEquals("1", revisions.get(0));
		assertEquals("2", revisions.get(1));
		assertEquals("3", revisions.get(2));
	}

	@Test
	public void testGetRevisionWithoutCheckout() {
		final VCSEngine vp = new TestClass(folder.getRoot().toPath());
		assertFalse(vp.getRevision().isPresent());
	}

	@Test
	public void testGetRevisionOfEmptyRevisionList() throws IOException {
		final VCSEngine vp = new EmptyRevisionListTestClass();
		assertFalse(vp.next().isPresent());
		assertFalse(vp.getRevision().isPresent());
	}

	@Test
	public void testTargetNull() {
		thrown.expect(NullPointerException.class);
		new TestClass(null);
	}

	@Test
	public void testGetTargetWithAbsolutePath() {
		final VCSEngine vp = new TestClass(folder.getRoot().toPath());
		assertEquals(folder.getRoot().toPath().toAbsolutePath(),
				vp.getTarget());
	}

	@Test
	public void testGetTargetWithRelativePath() {
		final Path rPath = Paths.get("");
		final VCSEngine vp = new TestClass(rPath);
		assertEquals(rPath.toAbsolutePath(), vp.getTarget());
	}

	@Test
	public void testGetOutputValidSubPath() {
		final VCSEngine vp =
				new ValidOutputTestClass(folder.getRoot().toPath());
		final Path expected = folder.getRoot().toPath().resolve("src");
		assertEquals(expected, vp.getOutput());
	}

	@Test
	public void testFilesInOutputFlat() throws IOException {
		final VCSEngine vp = new TestClass(folder.getRoot().toPath());
		vp.next();
		assertEquals(6, vp.listFilesInOutput().size());
		assertTrue(vp.listFilesInOutput().contains(
				Paths.get(folder.getRoot().getAbsolutePath(),
						"relocate2.java")));
		assertTrue(vp.listFilesInOutput().contains(
				Paths.get(folder.getRoot().getAbsolutePath(),
						"add", "add1.java")));
	}

	private static class TestClass extends AbstractIntervalVCSEngine {
		private TestClass(final Path target) {
			super("", "", target, LocalDateTime.now(), LocalDateTime.now());
		}

		@Override
		protected LocalDateTime validateMapDateTime(LocalDateTime dt) {
			return dt;
		}

		@Override
		protected String validateMapIntervalRevision(String revision) {
			return revision;
		}

		@Override
		protected String validateMapRepository(String repository) {
			return repository;
		}

		@Override
		protected String validateMapRoot(String root) {
			return root;
		}

		@Override
		protected Path validateMapTarget(Path target) {
			return target;
		}

		@Override
		protected List<String> validateMapRevisions(List<String> revisions) {
			return revisions;
		}

		@Override
		protected void checkoutImpl(String revision) {}

		@Override
		protected Changes createChangesImpl(
				final String fromRev, final String toRev) {
		    final Changes changes = new Changes();
			changes.getAdded().add(getTarget() + "/add/add1.java");
            changes.getAdded().add(getTarget() + "/add/add2.java");
			changes.getRemoved().add(getTarget() +  "/remove/remove.java");
			changes.getModified().add(getTarget() + "/modify/modify1.java");
            changes.getModified().add(getTarget() + "/modify/modify2.java");
            changes.getModified().add(getTarget() + "/modify/modify3.java");
			changes.getRelocated().add(new AbstractMap.SimpleImmutableEntry<>(
					getTarget() + "/relocate.java",
					getTarget() + "/relocate2.java"
			));
			return changes;
		}

		@Override
		public byte[] readAllBytesImpl(String s, String s1) {
			return new byte[0];
		}

		@Override
		protected List<LineInfo> readLineInfoImpl(VCSFile file) {
			return Collections.emptyList();
		}

		@Override
		protected Commit createCommitImpl(final String pRevision,
				final List<FileChange> pFileChanges, final List<Issue> pIssues)
				throws IllegalArgumentException {
			return getModelFactory().createCommit(pRevision, "test author",
					"test message", LocalDateTime.of(2000, 1, 2, 3, 4),
					Collections.emptyList(), pFileChanges, pIssues, this);
		}

		@Override
		protected Optional<String> getLatestRevision() throws IOException {
			final List<String> revs = listRevisionsImpl();
			return Optional.of(revs.get(revs.size() - 1));
		}

		@Override
		protected List<String> listRevisionsImpl(
				final LocalDateTime pSince,
				final LocalDateTime pUntil) {
			return Arrays.asList("1", "2", "3");
		}

		@Override
		protected List<String> listRevisionsImpl(
				final String pFrom,
				final String pTo) {
			return Arrays.asList("1", "2", "3");
		}

		@Override
		public Path getOutput() {
			return getTarget();
		}
	}

	private static class EmptyRevisionListTestClass extends TestClass {
		private EmptyRevisionListTestClass() {
			super(Paths.get(""));
		}

		@Override
		protected List<String> listRevisionsImpl(
				final LocalDateTime pSince,
				final LocalDateTime pUntil) {
			return Collections.emptyList();
		}

		@Override
		protected List<String> listRevisionsImpl(
				final String pFrom,
				final String pTo) {
			return Collections.emptyList();
		}
	}

	private static class ValidOutputTestClass extends TestClass {
		private ValidOutputTestClass(final Path target) {
			super(target);
		}

		@Override
		public Path getOutput() {
			return getTarget().resolve("src");
		}
	}
}
