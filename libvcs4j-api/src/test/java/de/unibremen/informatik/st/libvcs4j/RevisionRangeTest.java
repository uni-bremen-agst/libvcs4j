package de.unibremen.informatik.st.libvcs4j;

import org.junit.Test;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
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
        when(file.toPath()).thenReturn(Paths.get("test/path"));

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

        RevisionRange range = spy(RevisionRange.class);
        when(range.getCommits()).thenReturn(Arrays.asList(c1, c2));
        assertThat(range.getFileChanges()).isEmpty();
    }

    @Test
    public void testMergeAddRelocateRemove() {
        VCSFile from = mock(VCSFile.class);
        when(from.toPath()).thenReturn(Paths.get("from"));

        VCSFile to = mock(VCSFile.class);
        when(to.toPath()).thenReturn(Paths.get("to"));

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

        RevisionRange range1 = spy(RevisionRange.class);
        when(range1.getCommits()).thenReturn(Arrays.asList(c1, c2));
        assertThat(range1.getFileChanges())
                .hasSize(1)
                .first()
                .matches(fc -> fc.getType() == FileChange.Type.ADD)
                .matches(fc -> fc.getNewFile()
                        .orElseThrow(AssertionError::new)
                        .toPath()
                        .equals(to.toPath()));

        RevisionRange range2 = spy(RevisionRange.class);
        when(range2.getCommits()).thenReturn(Arrays.asList(c1, c2, c3));
        assertThat(range2.getFileChanges()).isEmpty();
    }

    @Test
    public void testRelocateRelocateRelocate() {
        VCSFile a = mock(VCSFile.class);
        when(a.getPath()).thenReturn("a");
        when(a.toPath()).thenReturn(Paths.get("a"));

        VCSFile b = mock(VCSFile.class);
        when(b.getPath()).thenReturn("b");
        when(b.toPath()).thenReturn(Paths.get("b"));

        VCSFile c = mock(VCSFile.class);
        when(c.getPath()).thenReturn("c");
        when(c.toPath()).thenReturn(Paths.get("c"));

        VCSFile d = mock(VCSFile.class);
        when(d.getPath()).thenReturn("d");
        when(d.toPath()).thenReturn(Paths.get("d"));

        FileChange r1 = mock(FileChange.class);
        when(r1.getOldFile()).thenReturn(Optional.of(a));
        when(r1.getNewFile()).thenReturn(Optional.of(b));
        when(r1.getType()).thenReturn(FileChange.Type.RELOCATE);

        FileChange r2 = mock(FileChange.class);
        when(r2.getOldFile()).thenReturn(Optional.of(b));
        when(r2.getNewFile()).thenReturn(Optional.of(c));
        when(r2.getType()).thenReturn(FileChange.Type.RELOCATE);

        FileChange r3 = mock(FileChange.class);
        when(r3.getOldFile()).thenReturn(Optional.of(c));
        when(r3.getNewFile()).thenReturn(Optional.of(d));
        when(r3.getType()).thenReturn(FileChange.Type.REMOVE);

        Commit c1 = mock(Commit.class);
        when(c1.getFileChanges()).thenReturn(singletonList(r1));

        Commit c2 = mock(Commit.class);
        when(c2.getFileChanges()).thenReturn(singletonList(r2));

        Commit c3 = mock(Commit.class);
        when(c3.getFileChanges()).thenReturn(singletonList(r3));

        RevisionRange range1 = spy(RevisionRange.class);
        when(range1.getCommits()).thenReturn(Arrays.asList(c1, c2));
        assertThat(range1.getFileChanges())
                .hasSize(1)
                .first()
                .matches(fc -> fc.getType() == FileChange.Type.RELOCATE)
                .matches(fc -> fc.getOldFile()
                        .orElseThrow(AssertionError::new)
                        .toPath()
                        .equals(a.toPath()))
                .matches(fc -> fc.getNewFile()
                        .orElseThrow(AssertionError::new)
                        .toPath()
                        .equals(c.toPath()));

        RevisionRange range2 = spy(RevisionRange.class);
        when(range2.getCommits()).thenReturn(Arrays.asList(c1, c2, c3));
        assertThat(range2.getFileChanges())
                .hasSize(1)
                .first()
                .matches(fc -> fc.getType() == FileChange.Type.RELOCATE)
                .matches(fc -> fc.getOldFile()
                        .orElseThrow(AssertionError::new)
                        .toPath()
                        .equals(a.toPath()))
                .matches(fc -> fc.getNewFile()
                        .orElseThrow(AssertionError::new)
                        .toPath()
                        .equals(d.toPath()));
    }

    @Test
    public void testUnrelated() {
        VCSFile a = mock(VCSFile.class);
        when(a.getPath()).thenReturn("a");
        when(a.toPath()).thenReturn(Paths.get("a"));

        VCSFile b = mock(VCSFile.class);
        when(b.getPath()).thenReturn("b");
        when(b.toPath()).thenReturn(Paths.get("b"));

        VCSFile c = mock(VCSFile.class);
        when(c.getPath()).thenReturn("c");
        when(c.toPath()).thenReturn(Paths.get("c"));

        VCSFile d = mock(VCSFile.class);
        when(d.getPath()).thenReturn("d");
        when(d.toPath()).thenReturn(Paths.get("d"));

        FileChange r1 = mock(FileChange.class);
        when(r1.getOldFile()).thenReturn(Optional.empty());
        when(r1.getNewFile()).thenReturn(Optional.of(a));
        when(r1.getType()).thenReturn(FileChange.Type.ADD);

        FileChange r2 = mock(FileChange.class);
        when(r2.getOldFile()).thenReturn(Optional.of(b));
        when(r2.getNewFile()).thenReturn(Optional.empty());
        when(r2.getType()).thenReturn(FileChange.Type.REMOVE);

        FileChange r3 = mock(FileChange.class);
        when(r3.getOldFile()).thenReturn(Optional.of(c));
        when(r3.getNewFile()).thenReturn(Optional.of(d));
        when(r3.getType()).thenReturn(FileChange.Type.RELOCATE);

        Commit c1 = mock(Commit.class);
        when(c1.getFileChanges()).thenReturn(singletonList(r1));

        Commit c2 = mock(Commit.class);
        when(c2.getFileChanges()).thenReturn(singletonList(r2));

        Commit c3 = mock(Commit.class);
        when(c3.getFileChanges()).thenReturn(singletonList(r3));

        RevisionRange range = spy(RevisionRange.class);
        when(range.getCommits()).thenReturn(Arrays.asList(c1, c2, c3));
        assertThat(range.getFileChanges()).containsExactly(r1, r2, r3);
    }
}
