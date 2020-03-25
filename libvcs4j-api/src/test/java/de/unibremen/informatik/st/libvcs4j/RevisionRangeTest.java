package de.unibremen.informatik.st.libvcs4j;

import org.junit.Test;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

public class RevisionRangeTest {

    private <T> List<T> singletonList(T t) {
        return new ArrayList<>(Collections.singletonList(t));
    }

    @Test
    public void testMergeAddRemove() {
        VCSFile file = mock(VCSFile.class);
        when(file.getRelativePath()).thenReturn("test/path");
        when(file.toRelativePath()).thenReturn(Paths.get("test/path"));

        FileChange add = mock(FileChange.class);
        when(add.getOldFile()).thenReturn(Optional.empty());
        when(add.getNewFile()).thenReturn(Optional.of(file));
        when(add.getType()).thenReturn(FileChange.Type.ADD);

        FileChange remove = mock(FileChange.class);
        when(remove.getOldFile()).thenReturn(Optional.of(file));
        when(remove.getNewFile()).thenReturn(Optional.empty());
        when(remove.getType()).thenReturn(FileChange.Type.REMOVE);

        Commit c1 = mock(Commit.class);
        when(c1.getFileChanges()).thenReturn(singletonList(add));

        Commit c2 = mock(Commit.class);
        when(c2.getFileChanges()).thenReturn(singletonList(remove));

        VCSModelFactory factory = new VCSModelFactory() {};
        VCSEngine engine = mock(VCSEngine.class);
        when(engine.getModelFactory()).thenReturn(factory);

        RevisionRange range = spy(RevisionRange.class);
        when(range.getCommits()).thenReturn(Arrays.asList(c1, c2));
        when(range.getVCSEngine()).thenReturn(engine);
        assertThat(range.getFileChanges()).isEmpty();
    }

    @Test
    public void testMergeAddRelocateRemove() {
        Revision rev1 = mock(Revision.class);
        when(rev1.getId()).thenReturn("1");

        Revision rev2 = mock(Revision.class);
        when(rev2.getId()).thenReturn("2");

        Revision rev3 = mock(Revision.class);
        when(rev2.getId()).thenReturn("3");

        VCSFile from = mock(VCSFile.class);
        when(from.getRelativePath()).thenReturn("from");
        when(from.toRelativePath()).thenReturn(Paths.get("from"));
        when(from.getRevision()).thenReturn(rev1);

        VCSFile to = mock(VCSFile.class);
        when(to.getRelativePath()).thenReturn("to");
        when(to.toRelativePath()).thenReturn(Paths.get("to"));
        when(to.getRevision()).thenReturn(rev2);

        FileChange add = mock(FileChange.class);
        when(add.getOldFile()).thenReturn(Optional.empty());
        when(add.getNewFile()).thenReturn(Optional.of(from));
        when(add.getType()).thenReturn(FileChange.Type.ADD);

        FileChange relocate = mock(FileChange.class);
        when(relocate.getOldFile()).thenReturn(Optional.of(from));
        when(relocate.getNewFile()).thenReturn(Optional.of(to));
        when(relocate.getType()).thenReturn(FileChange.Type.RELOCATE);

        FileChange remove = mock(FileChange.class);
        when(remove.getOldFile()).thenReturn(Optional.of(to));
        when(remove.getNewFile()).thenReturn(Optional.empty());
        when(remove.getType()).thenReturn(FileChange.Type.REMOVE);

        Commit c1 = mock(Commit.class);
        when(c1.getFileChanges()).thenReturn(singletonList(add));

        Commit c2 = mock(Commit.class);
        when(c2.getFileChanges()).thenReturn(singletonList(relocate));

        Commit c3 = mock(Commit.class);
        when(c3.getFileChanges()).thenReturn(singletonList(remove));

        VCSModelFactory factory = new VCSModelFactory() {};
        VCSEngine engine = mock(VCSEngine.class);
        when(engine.getModelFactory()).thenReturn(factory);

        RevisionRange range1 = spy(RevisionRange.class);
        when(range1.getCommits()).thenReturn(Arrays.asList(c1, c2));
        when(range1.getVCSEngine()).thenReturn(engine);
        when(range1.getRevision()).thenReturn(rev3);
        assertThat(range1.getFileChanges())
                .hasSize(1)
                .first()
                .matches(fc -> fc.getType() == FileChange.Type.ADD)
                .matches(fc -> fc.getNewFile()
                        .orElseThrow(AssertionError::new)
                        .toRelativePath()
                        .equals(to.toRelativePath()));

        RevisionRange range2 = spy(RevisionRange.class);
        when(range2.getCommits()).thenReturn(Arrays.asList(c1, c2, c3));
        when(range2.getVCSEngine()).thenReturn(engine);
        assertThat(range2.getFileChanges()).isEmpty();
    }

    @Test
    public void testRelocateRelocateRelocate() {
		VCSModelFactory factory = new VCSModelFactory() {};
        VCSEngine vcs = mock(VCSEngine.class);
		when(vcs.getModelFactory()).thenReturn(factory);

		Revision rev1 = mock(Revision.class);
		when(rev1.getId()).thenReturn("1");
        VCSFile a = mock(VCSFile.class);
        when(a.getRelativePath()).thenReturn("a");
        when(a.toRelativePath()).thenReturn(Paths.get("a"));
        when(a.getRevision()).thenReturn(rev1);

        Revision rev2 = mock(Revision.class);
        when(rev2.getId()).thenReturn("2");
        VCSFile b = mock(VCSFile.class);
        when(b.getRelativePath()).thenReturn("b");
        when(b.toRelativePath()).thenReturn(Paths.get("b"));
        when(b.getRevision()).thenReturn(rev2);

        Revision rev3 = mock(Revision.class);
        when(rev3.getId()).thenReturn("3");
        VCSFile c = mock(VCSFile.class);
        when(c.getRelativePath()).thenReturn("c");
        when(c.toRelativePath()).thenReturn(Paths.get("c"));
        when(c.getRevision()).thenReturn(rev3);

        Revision rev4 = mock(Revision.class);
        when(rev4.getId()).thenReturn("4");
        VCSFile d = mock(VCSFile.class);
        when(d.getRelativePath()).thenReturn("d");
        when(d.toRelativePath()).thenReturn(Paths.get("d"));
        when(d.getRevision()).thenReturn(rev4);

        FileChange r2 = mock(FileChange.class);
        when(r2.getVCSEngine()).thenReturn(vcs);
        when(r2.getOldFile()).thenReturn(Optional.of(a));
        when(r2.getNewFile()).thenReturn(Optional.of(b));
        when(r2.getType()).thenReturn(FileChange.Type.RELOCATE);

        FileChange r3 = mock(FileChange.class);
        when(r3.getVCSEngine()).thenReturn(vcs);
        when(r3.getOldFile()).thenReturn(Optional.of(b));
        when(r3.getNewFile()).thenReturn(Optional.of(c));
        when(r3.getType()).thenReturn(FileChange.Type.RELOCATE);

        FileChange r4 = mock(FileChange.class);
        when(r4.getVCSEngine()).thenReturn(vcs);
        when(r4.getOldFile()).thenReturn(Optional.of(c));
        when(r4.getNewFile()).thenReturn(Optional.of(d));
        when(r4.getType()).thenReturn(FileChange.Type.RELOCATE);

        Commit c2 = mock(Commit.class);
        when(c2.getFileChanges()).thenReturn(singletonList(r2));

        Commit c3 = mock(Commit.class);
        when(c3.getFileChanges()).thenReturn(singletonList(r3));

        Commit c4 = mock(Commit.class);
        when(c4.getFileChanges()).thenReturn(singletonList(r4));

        RevisionRange range1 = spy(RevisionRange.class);
        when(range1.getCommits()).thenReturn(Arrays.asList(c2, c3));
        when(range1.getVCSEngine()).thenReturn(vcs);
        when(range1.getPredecessorRevision()).thenReturn(Optional.of(rev1));
        when(range1.getRevision()).thenReturn(rev3);
        assertThat(range1.getFileChanges())
                .hasSize(1)
                .first()
                .matches(fc -> fc.getType() == FileChange.Type.RELOCATE)
                .matches(fc -> fc.getOldFile()
                        .orElseThrow(AssertionError::new)
                        .toRelativePath()
                        .equals(a.toRelativePath()))
                .matches(fc -> fc.getNewFile()
                        .orElseThrow(AssertionError::new)
                        .toRelativePath()
                        .equals(c.toRelativePath()))
                .matches(fc -> fc.getVCSEngine() == r2.getVCSEngine());

        RevisionRange range2 = spy(RevisionRange.class);
        when(range2.getCommits()).thenReturn(Arrays.asList(c2, c3, c4));
        when(range2.getVCSEngine()).thenReturn(vcs);
        when(range2.getPredecessorRevision()).thenReturn(Optional.of(rev1));
        when(range2.getRevision()).thenReturn(rev4);
        assertThat(range2.getFileChanges())
                .hasSize(1)
                .first()
                .matches(fc -> fc.getType() == FileChange.Type.RELOCATE)
                .matches(fc -> fc.getOldFile()
                        .orElseThrow(AssertionError::new)
                        .toRelativePath()
                        .equals(a.toRelativePath()))
                .matches(fc -> fc.getNewFile()
                        .orElseThrow(AssertionError::new)
                        .toRelativePath()
                        .equals(d.toRelativePath()))
                .matches(fc -> fc.getVCSEngine() == r2.getVCSEngine());
    }

    @Test
    public void testAddAdd() {
        VCSModelFactory factory = new VCSModelFactory() {};
        VCSEngine vcs = mock(VCSEngine.class);
        when(vcs.getModelFactory()).thenReturn(factory);

        Revision rev1 = mock(Revision.class);
        when(rev1.getId()).thenReturn("1");
        VCSFile a = mock(VCSFile.class);
        when(a.getRelativePath()).thenReturn("a");
        when(a.toRelativePath()).thenReturn(Paths.get("a"));
        when(a.getRevision()).thenReturn(rev1);

        Revision rev2 = mock(Revision.class);
        when(rev2.getId()).thenReturn("2");
        VCSFile b = mock(VCSFile.class);
        when(b.getRelativePath()).thenReturn("b");
        when(b.toRelativePath()).thenReturn(Paths.get("b"));
        when(b.getRevision()).thenReturn(rev2);

        FileChange ch1 = mock(FileChange.class);
        when(ch1.getVCSEngine()).thenReturn(vcs);
        when(ch1.getNewFile()).thenReturn(Optional.of(a));
        when(ch1.getType()).thenCallRealMethod();

        FileChange ch2 = mock(FileChange.class);
        when(ch2.getVCSEngine()).thenReturn(vcs);
        when(ch2.getNewFile()).thenReturn(Optional.of(b));
        when(ch2.getType()).thenCallRealMethod();

        Commit c1 = mock(Commit.class);
        when(c1.getFileChanges()).thenReturn(singletonList(ch1));

        Commit c2 = mock(Commit.class);
        when(c2.getFileChanges()).thenReturn(singletonList(ch2));

        RevisionRange range = spy(RevisionRange.class);
        when(range.getCommits()).thenReturn(Arrays.asList(c1, c2));
        when(range.getVCSEngine()).thenReturn(vcs);
        when(range.getPredecessorRevision()).thenReturn(Optional.empty());
        when(range.getRevision()).thenReturn(rev2);
        assertThat(range.getFileChanges()).hasSize(2);

        assertThat(range.getFileChanges().get(0).getType())
                .isEqualTo(FileChange.Type.ADD);
        assertThat(range.getFileChanges().get(0)
                .getNewFile().orElseThrow(AssertionError::new)
                .getRevision().getId()).isEqualTo("2");

        assertThat(range.getFileChanges().get(1).getType())
                .isEqualTo(FileChange.Type.ADD);
        assertThat(range.getFileChanges().get(1)
                .getNewFile().orElseThrow(AssertionError::new)
                .getRevision().getId()).isEqualTo("2");
    }

    @Test
    public void testUnrelated() {
        Revision rev1 = mock(Revision.class);
        when(rev1.getId()).thenReturn("1");

        Revision rev2 = mock(Revision.class);
        when(rev2.getId()).thenReturn("2");

        Revision rev3 = mock(Revision.class);
        when(rev3.getId()).thenReturn("3");

        VCSFile a = mock(VCSFile.class);
        when(a.getRelativePath()).thenReturn("a");
        when(a.toRelativePath()).thenReturn(Paths.get("a"));
        when(a.getRevision()).thenReturn(rev1);

        VCSFile b = mock(VCSFile.class);
        when(b.getRelativePath()).thenReturn("b");
        when(b.toRelativePath()).thenReturn(Paths.get("b"));
        when(b.getRevision()).thenReturn(rev2);

        VCSFile c = mock(VCSFile.class);
        when(c.getRelativePath()).thenReturn("c");
        when(c.toRelativePath()).thenReturn(Paths.get("c"));
        when(c.getRevision()).thenReturn(rev3);

        FileChange r1 = mock(FileChange.class);
        when(r1.getOldFile()).thenReturn(Optional.empty());
        when(r1.getNewFile()).thenReturn(Optional.of(a));
        when(r1.getType()).thenReturn(FileChange.Type.ADD);

        FileChange r2 = mock(FileChange.class);
        when(r2.getOldFile()).thenReturn(Optional.empty());
        when(r2.getNewFile()).thenReturn(Optional.of(b));
        when(r2.getType()).thenReturn(FileChange.Type.ADD);

        FileChange r3 = mock(FileChange.class);
        when(r3.getOldFile()).thenReturn(Optional.empty());
        when(r3.getNewFile()).thenReturn(Optional.of(c));
        when(r3.getType()).thenReturn(FileChange.Type.ADD);

        Commit c1 = mock(Commit.class);
        when(c1.getFileChanges()).thenReturn(singletonList(r1));

        Commit c2 = mock(Commit.class);
        when(c2.getFileChanges()).thenReturn(singletonList(r2));

        Commit c3 = mock(Commit.class);
        when(c3.getFileChanges()).thenReturn(singletonList(r3));

        VCSModelFactory factory = new VCSModelFactory() {};
        VCSEngine engine = mock(VCSEngine.class);
        when(engine.getModelFactory()).thenReturn(factory);

        RevisionRange range = spy(RevisionRange.class);
        when(range.getCommits()).thenReturn(Arrays.asList(c1, c2, c3));
        when(range.getVCSEngine()).thenReturn(engine);
        when(range.getRevision()).thenReturn(rev3);
        assertThat(range.getFileChanges().get(0).getType())
                .isEqualTo(r1.getType());
        assertThat(range.getFileChanges().get(1).getType())
                .isEqualTo(r2.getType());
        assertThat(range.getFileChanges().get(2).getType())
                .isEqualTo(r3.getType());
    }

    @Test
    public void singleCommit() {
        VCSFile file = mock(VCSFile.class);
        when(file.getRelativePath()).thenReturn("single");
        when(file.toRelativePath()).thenReturn(Paths.get("single"));

        FileChange add = mock(FileChange.class);
        when(add.getOldFile()).thenReturn(Optional.empty());
        when(add.getNewFile()).thenReturn(Optional.of(file));
        when(add.getType()).thenReturn(FileChange.Type.ADD);

        Commit commit = mock(Commit.class);
        when(commit.getFileChanges()).thenReturn(singletonList(add));

        RevisionRange range = spy(RevisionRange.class);
        when(range.getCommits()).thenReturn(singletonList(commit));
        assertThat(range.getFileChanges()).containsExactly(add);
    }

    @Test
    public void getLatestCommit() {
        Commit c1 = mock(Commit.class);
        Commit c2 = mock(Commit.class);
        Commit c3 = mock(Commit.class);

        RevisionRange range = mock(RevisionRange.class);
        when(range.getCommits()).thenReturn(Arrays.asList(c1, c2, c3));
        when(range.getLatestCommit()).thenCallRealMethod();
        assertThat(range.getLatestCommit()).isSameAs(c3);
    }

    @Test
    public void getLatestCommitWithoutCommit() {
        RevisionRange range = mock(RevisionRange.class);
        when(range.getCommits()).thenReturn(Collections.emptyList());
        when(range.getLatestCommit()).thenCallRealMethod();
        assertThatIllegalStateException()
                .isThrownBy(range::getLatestCommit);
    }

    @Test
    public void isFirstWithPredecessor() {
        Revision rev = mock(Revision.class);

        RevisionRange range = mock(RevisionRange.class);
        when(range.getPredecessorRevision()).thenReturn(Optional.of(rev));
        when(range.isFirst()).thenCallRealMethod();
        assertThat(range.isFirst()).isFalse();
    }

    @Test
    public void isFirstWithoutPredecessor() {
        RevisionRange range = mock(RevisionRange.class);
        when(range.getPredecessorRevision()).thenReturn(Optional.empty());
        when(range.isFirst()).thenCallRealMethod();
        assertThat(range.isFirst()).isTrue();
    }
}
