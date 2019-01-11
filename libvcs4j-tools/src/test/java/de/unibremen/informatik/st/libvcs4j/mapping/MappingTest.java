package de.unibremen.informatik.st.libvcs4j.mapping;

import de.unibremen.informatik.st.libvcs4j.*;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class MappingTest {

    private Mapping<String> mapping;

    private RevisionRange revisionRange;

    @Before
    public void setUp() {
        mapping = new Mapping<>();
        revisionRange = mock(RevisionRange.class);
        Revision revision = mock(Revision.class);
        when(revision.getId()).thenReturn("Bla");
        when(revisionRange.getRevision()).thenReturn(revision);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidRevisionsOfTo() throws IOException {
        VCSFile.Range range = mock(VCSFile.Range.class);
        VCSFile file = mock(VCSFile.class);
        Revision revision = mock(Revision.class);
        when(file.getRevision()).thenReturn(revision);
        when(range.getFile()).thenReturn(file);
        when(revision.getId()).thenReturn("B");
        List ranges = Collections.singletonList(range);
        Mappable mockMappable = mock(Mappable.class);
        when(mockMappable.getRanges()).thenReturn(ranges);
        List to = Collections.singletonList(mockMappable);
        mapping.map(new ArrayList<>(), to, revisionRange);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidRevisionsOfFrom() throws IOException {
        VCSFile.Range range = mock(VCSFile.Range.class);
        VCSFile file = mock(VCSFile.class);
        Revision revision = mock(Revision.class);
        when(file.getRevision()).thenReturn(revision);
        when(range.getFile()).thenReturn(file);
        when(revision.getId()).thenReturn("Bla");
        List ranges = Collections.singletonList(range);
        Mappable mockMappable = mock(Mappable.class);
        when(mockMappable.getRanges()).thenReturn(ranges);
        List to = Collections.singletonList(mockMappable);

        range = mock(VCSFile.Range.class);
        file = mock(VCSFile.class);
        revision = mock(Revision.class);
        when(file.getRevision()).thenReturn(revision);
        when(range.getFile()).thenReturn(file);
        when(revision.getId()).thenReturn("Bla");
        Revision predRevision = mock(Revision.class);
        when(predRevision.getId()).thenReturn("B");
        when(revisionRange.getPredecessorRevision()).thenReturn(Optional.of(predRevision));
        ranges = Collections.singletonList(range);
        mockMappable = mock(Mappable.class);
        when(mockMappable.getRanges()).thenReturn(ranges);
        List from = Collections.singletonList(mockMappable);

        mapping.map(from, to, revisionRange);
    }

    @Test
    public void testMappingWithSignatures() throws IOException {
        List to = createToMappables();
        List from = createFromMappables();

        Mapping.Result result = mapping.map(from, to, revisionRange);
        assertThat(result.getFrom()).isEqualTo(from);
        assertThat(result.getTo()).isEqualTo(to);
        assertThat(result.getWithMapping().isEmpty()).isFalse();
        assertThat(result.getWithoutMapping().isEmpty()).isTrue();
    }

    @Test
    public void testMappingWithSingleRange() throws IOException {
        VCSFile.Range range = mock(VCSFile.Range.class);
        when(range.apply(any(FileChange.class))).thenReturn(Optional.of(range));
        VCSFile.Position begin = mock(VCSFile.Position.class);
        when(begin.getOffset()).thenReturn(111);
        when(range.getBegin()).thenReturn(begin);
        VCSFile.Position end = mock(VCSFile.Position.class);
        when(end.getOffset()).thenReturn(222);
        when(range.getEnd()).thenReturn(end);
        VCSFile file = mock(VCSFile.class);
        when(file.getPath()).thenReturn("/path/to/temporaryField");
        Revision revision = mock(Revision.class);
        when(file.getRevision()).thenReturn(revision);
        when(range.getFile()).thenReturn(file);
        when(revision.getId()).thenReturn("Bla");
        Revision predRevision = mock(Revision.class);
        when(predRevision.getId()).thenReturn("Bla");
        when(revisionRange.getPredecessorRevision()).thenReturn(Optional.of(predRevision));
        List<Mappable<String>> from = new ArrayList<>();
        List<VCSFile.Range> ranges = Collections.singletonList(range);
        MockMappable mappable =
                new MockMappable(ranges, "", "TemporaryField");
        from.add(mappable);
        range = mock(VCSFile.Range.class);
        when(range.apply(any(FileChange.class))).thenReturn(Optional.of(range));
        begin = mock(VCSFile.Position.class);
        when(begin.getOffset()).thenReturn(123);
        when(range.getBegin()).thenReturn(begin);
        end = mock(VCSFile.Position.class);
        when(end.getOffset()).thenReturn(126);
        when(range.getEnd()).thenReturn(end);
        file = mock(VCSFile.class);
        when(file.getPath()).thenReturn("/path/to/dataClump");
        revision = mock(Revision.class);
        when(file.getRevision()).thenReturn(revision);
        when(range.getFile()).thenReturn(file);
        when(revision.getId()).thenReturn("Bla");
        predRevision = mock(Revision.class);
        when(predRevision.getId()).thenReturn("Bla");
        when(revisionRange.getPredecessorRevision()).thenReturn(Optional.of(predRevision));
        mappable = new MockMappable(ranges, "", "DataClump");
        from.add(mappable);
        range = mock(VCSFile.Range.class);
        when(range.apply(any(FileChange.class))).thenReturn(Optional.of(range));
        begin = mock(VCSFile.Position.class);
        when(begin.getOffset()).thenReturn(155);
        when(range.getBegin()).thenReturn(begin);
        end = mock(VCSFile.Position.class);
        when(end.getOffset()).thenReturn(255);
        when(range.getEnd()).thenReturn(end);
        file = mock(VCSFile.class);
        when(file.getPath()).thenReturn("/path/to/deadCode");
        revision = mock(Revision.class);
        when(file.getRevision()).thenReturn(revision);
        when(range.getFile()).thenReturn(file);
        when(revision.getId()).thenReturn("Bla");
        predRevision = mock(Revision.class);
        when(predRevision.getId()).thenReturn("Bla");
        when(revisionRange.getPredecessorRevision()).thenReturn(Optional.of(predRevision));
        mappable = new MockMappable(ranges, "", "DeadCode");
        from.add(mappable);

        List<Mappable<String>> to = new ArrayList<>();
        range = mock(VCSFile.Range.class);
        begin = mock(VCSFile.Position.class);
        when(begin.getOffset()).thenReturn(123);
        when(range.getBegin()).thenReturn(begin);
        end = mock(VCSFile.Position.class);
        when(end.getOffset()).thenReturn(126);
        when(range.getEnd()).thenReturn(end);
        file = mock(VCSFile.class);
        revision = mock(Revision.class);
        when(file.getRevision()).thenReturn(revision);
        when(range.getFile()).thenReturn(file);
        when(revision.getId()).thenReturn("Bla");
        ranges = Collections.singletonList(range);
        mappable = new MockMappable(ranges, "", "DataClump");
        to.add(mappable);
        range = mock(VCSFile.Range.class);
        begin = mock(VCSFile.Position.class);
        when(begin.getOffset()).thenReturn(111);
        when(range.getBegin()).thenReturn(begin);
        end = mock(VCSFile.Position.class);
        when(end.getOffset()).thenReturn(222);
        when(range.getEnd()).thenReturn(end);
        file = mock(VCSFile.class);
        revision = mock(Revision.class);
        when(file.getRevision()).thenReturn(revision);
        when(range.getFile()).thenReturn(file);
        when(revision.getId()).thenReturn("Bla");
        ranges = Collections.singletonList(range);
        mappable = new MockMappable(ranges, "", "TemporaryField");
        to.add(mappable);
        range = mock(VCSFile.Range.class);
        begin = mock(VCSFile.Position.class);
        when(begin.getOffset()).thenReturn(155);
        when(range.getBegin()).thenReturn(begin);
        end = mock(VCSFile.Position.class);
        when(end.getOffset()).thenReturn(255);
        when(range.getEnd()).thenReturn(end);
        file = mock(VCSFile.class);
        revision = mock(Revision.class);
        when(file.getRevision()).thenReturn(revision);
        when(range.getFile()).thenReturn(file);
        when(revision.getId()).thenReturn("Bla");
        ranges = Collections.singletonList(range);
        mappable = new MockMappable(ranges, "", "DeadCode");
        to.add(mappable);
        range = mock(VCSFile.Range.class);
        begin = mock(VCSFile.Position.class);
        when(begin.getOffset()).thenReturn(77);
        when(range.getBegin()).thenReturn(begin);
        end = mock(VCSFile.Position.class);
        when(end.getOffset()).thenReturn(88);
        when(range.getEnd()).thenReturn(end);
        file = mock(VCSFile.class);
        revision = mock(Revision.class);
        when(file.getRevision()).thenReturn(revision);
        when(range.getFile()).thenReturn(file);
        when(revision.getId()).thenReturn("Bla");
        ranges = Collections.singletonList(range);
        mappable = new MockMappable(ranges, "", "Unmapped");
        to.add(mappable);

        List<FileChange> fileChanges = new ArrayList<>();
        FileChange fileChange = mock(FileChange.class);
        VCSFile vcsFile = mock(VCSFile.class);
        when(vcsFile.getPath()).thenReturn("/path/to/dataClump");
        when(fileChange.getType()).thenReturn(FileChange.Type.MODIFY);
        when(fileChange.getOldFile()).thenReturn(Optional.of(vcsFile));
        fileChanges.add(fileChange);
        fileChange = mock(FileChange.class);
        vcsFile = mock(VCSFile.class);
        when(vcsFile.getPath()).thenReturn("/path/to/temporaryField");
        when(fileChange.getType()).thenReturn(FileChange.Type.MODIFY);
        when(fileChange.getOldFile()).thenReturn(Optional.of(vcsFile));
        fileChanges.add(fileChange);
        fileChange = mock(FileChange.class);
        vcsFile = mock(VCSFile.class);
        when(vcsFile.getPath()).thenReturn("/path/to/deadCode");
        when(fileChange.getType()).thenReturn(FileChange.Type.MODIFY);
        when(fileChange.getOldFile()).thenReturn(Optional.of(vcsFile));
        fileChanges.add(fileChange);
        when(revisionRange.getFileChanges()).thenReturn(fileChanges);

        Mapping.Result result = mapping.map(from, to, revisionRange);
        assertThat(result.getFrom()).isEqualTo(from);
        assertThat(result.getTo()).isEqualTo(to);
        assertThat(result.getWithMapping().isEmpty()).isFalse();
        assertThat(result.getUnmapped().contains(to.get(to.size() - 1))).isTrue();
        assertThat(result.getMapped().contains(to.get(to.size() - 1))).isFalse();
    }


    ////////////////////////////////////////////////////////////////////////////
    ///////////////////////// Helper methods ///////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////


    private List<Mappable<String>> createToMappables() {
        List<Mappable<String>> result = new ArrayList<>();
        VCSFile.Range range = mock(VCSFile.Range.class);
        VCSFile file = mock(VCSFile.class);
        Revision revision = mock(Revision.class);
        when(file.getRevision()).thenReturn(revision);
        when(range.getFile()).thenReturn(file);
        when(revision.getId()).thenReturn("Bla");
        List ranges = Collections.singletonList(range);
        MockMappable mappable =
                new MockMappable(ranges, "Class A", "");
        result.add(mappable);
        mappable = new MockMappable(ranges, "Class B", "");
        result.add(mappable);
        mappable = new MockMappable(ranges, "Class C", "");
        result.add(mappable);
        return result;
    }

    private List<Mappable<String>> createFromMappables() {
        VCSFile.Range range = mock(VCSFile.Range.class);
        VCSFile file = mock(VCSFile.class);
        Revision revision = mock(Revision.class);
        when(file.getRevision()).thenReturn(revision);
        when(range.getFile()).thenReturn(file);
        when(revision.getId()).thenReturn("Bla");
        Revision predRevision = mock(Revision.class);
        when(predRevision.getId()).thenReturn("Bla");
        when(revisionRange.getPredecessorRevision()).thenReturn(Optional.of(predRevision));
        List ranges = Collections.singletonList(range);
        List<Mappable<String>> result = new ArrayList<>();
        MockMappable mappable =
                new MockMappable(ranges, "Class C", "");
        result.add(mappable);
        mappable = new MockMappable(ranges, "Class A", "");
        result.add(mappable);
        mappable = new MockMappable(ranges, "Class B", "");
        result.add(mappable);
        return result;
    }

    ////////////////////////////////////////////////////////////////////////////
    ///////////////////////// Helper class /////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////

    private class MockMappable implements Mappable<String> {

        private List<VCSFile.Range> ranges;

        private String signature;

        private String metadata;

        MockMappable(final List<VCSFile.Range> ranges,
                     final String signature,
                     final String metadata) {
            this.ranges = ranges;
            this.signature = signature;
            this.metadata = metadata;
        }

        @Override
        public List<VCSFile.Range> getRanges() {
            return ranges;
        }

        @Override
        public Optional<String> getSignature() {
            return Optional.of(signature);
        }

        @Override
        public Optional<String> getMetadata() {
            return Optional.of(metadata);
        }
    }
}
