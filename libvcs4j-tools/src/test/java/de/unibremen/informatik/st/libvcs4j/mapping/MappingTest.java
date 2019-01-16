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
        when(revision.getId()).thenReturn("2");
        when(revisionRange.getRevision()).thenReturn(revision);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidRevisionsOfTo() throws IOException {
        VCSFile.Range range = mock(VCSFile.Range.class);
        VCSFile file = mock(VCSFile.class);
        Revision revision = mock(Revision.class);
        when(file.getRevision()).thenReturn(revision);
        when(range.getFile()).thenReturn(file);
        when(revision.getId()).thenReturn("1");
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
        when(revision.getId()).thenReturn("2");
        List ranges = Collections.singletonList(range);
        Mappable mockMappable = mock(Mappable.class);
        when(mockMappable.getRanges()).thenReturn(ranges);
        List to = Collections.singletonList(mockMappable);

        range = mock(VCSFile.Range.class);
        file = mock(VCSFile.class);
        revision = mock(Revision.class);
        when(file.getRevision()).thenReturn(revision);
        when(range.getFile()).thenReturn(file);
        when(revision.getId()).thenReturn("2");
        Revision predRevision = mock(Revision.class);
        when(predRevision.getId()).thenReturn("1");
        when(revisionRange.getPredecessorRevision()).thenReturn(Optional.of(predRevision));
        ranges = Collections.singletonList(range);
        mockMappable = mock(Mappable.class);
        when(mockMappable.getRanges()).thenReturn(ranges);
        List from = Collections.singletonList(mockMappable);

        mapping.map(from, to, revisionRange);
    }

    @Test
    public void testMappingWithSignatures() throws IOException {
        VCSFile.Range range = createMockRange(11,
                22,
                "/path/to/file/",
                true);
        MockMappable predTemporaryField =
                new MockMappable(Collections.singletonList(range), "Class A", "");
        MockMappable predDataClump =
                new MockMappable(Collections.singletonList(range), "Class B", "");
        MockMappable predDeadCode =
                new MockMappable(Collections.singletonList(range), "Class C", "");
        List<Mappable<String>> from = Arrays.asList(
                predTemporaryField,
                predDataClump,
                predDeadCode);

        range = createMockRange(
                123,
                126,
                "/path/to/file/",
                false);
        MockMappable succDataClump =
                new MockMappable(Collections.singletonList(range), "Class B", "");
        MockMappable succTemporaryField =
                new MockMappable(Collections.singletonList(range), "Class A", "");
        MockMappable succDeadCode =
                new MockMappable(Collections.singletonList(range), "Class C", "");
        List<Mappable<String>> to = Arrays.asList(
                succDataClump,
                succDeadCode,
                succTemporaryField);

        Mapping.Result<String> result = mapping.map(from, to, revisionRange);
        from.forEach(mappable -> assertThat(result.getFrom().contains(mappable)).isTrue());
        to.forEach(mappable -> assertThat(result.getTo().contains(mappable)).isTrue());
        assertThat(result.getWithMapping().isEmpty()).isFalse();
        assertThat(result.getWithoutMapping().isEmpty()).isTrue();
        assertThat(result.getPredecessor(succDataClump))
                .isEqualTo(Optional.of(predDataClump));
        assertThat(result.getPredecessor(succDeadCode))
                .isEqualTo(Optional.of(predDeadCode));
        assertThat(result.getPredecessor(succTemporaryField))
                .isEqualTo(Optional.of(predTemporaryField));
    }

    @Test
    public void testMappingWithSingleRange() throws IOException {
        VCSFile.Range range = createMockRange(111,
                222,
                "/path/to/file/with/temporaryField",
                true);
        MockMappable predTemporaryField =
                new MockMappable(Collections.singletonList(range), "", "TemporaryField");
        range = createMockRange(123,
                126,
                "/path/to/file/with/dataClump",
                true);
        MockMappable predDataClump =
                new MockMappable(Collections.singletonList(range), "", "DataClump");
        range = createMockRange(155,
                255,
                "/path/to/file/with/deadCode",
                true);
        MockMappable predDeadCode =
                new MockMappable(Collections.singletonList(range), "", "DeadCode");
        List<Mappable<String>> from = Arrays.asList(
                predTemporaryField,
                predDataClump,
                predDeadCode);

        range = createMockRange(
                123,
                126,
                "/path/to/file/with/dataClump",
                false);
        MockMappable succDataClump =
                new MockMappable(Collections.singletonList(range), "", "DataClump");
        range = createMockRange(
                111,
                222,
                "/path/to/file/with/temporaryField",
                false);
        MockMappable succTemporaryField =
                new MockMappable(Collections.singletonList(range), "", "TemporaryField");
        range = createMockRange(
                155,
                255,
                "/path/to/file/with/deadCode",
                false);
        MockMappable succDeadCode =
                new MockMappable(Collections.singletonList(range), "", "DeadCode");
        range = createMockRange(
                77,
                88,
                "/path/to/file/with/unmapped",
                false);
        MockMappable succUnmapped =
                new MockMappable(Collections.singletonList(range), "", "Unmapped");
        List<Mappable<String>> to = Arrays.asList(
                succDataClump,
                succDeadCode,
                succTemporaryField,
                succUnmapped);

        List<FileChange> fileChanges = new ArrayList<>();
        FileChange fileChange = createMockFileChange("/path/to/file/with/dataClump");
        fileChanges.add(fileChange);
        fileChange = createMockFileChange("/path/to/file/with/temporaryField");
        fileChanges.add(fileChange);
        fileChange = createMockFileChange("/path/to/file/with/deadCode");
        fileChanges.add(fileChange);
        when(revisionRange.getFileChanges()).thenReturn(fileChanges);

        Mapping.Result<String> result = mapping.map(from, to, revisionRange);
        from.forEach(mappable -> assertThat(result.getFrom().contains(mappable)).isTrue());
        to.forEach(mappable -> assertThat(result.getTo().contains(mappable)).isTrue());
        assertThat(result.getWithMapping().isEmpty()).isFalse();
        assertThat(result.getUnmapped().contains(succUnmapped)).isTrue();
        assertThat(result.getMapped().contains(succUnmapped)).isFalse();
        assertThat(result.getPredecessor(succDataClump))
                .isEqualTo(Optional.of(predDataClump));
        assertThat(result.getPredecessor(succDeadCode))
                .isEqualTo(Optional.of(predDeadCode));
        assertThat(result.getPredecessor(succTemporaryField))
                .isEqualTo(Optional.of(predTemporaryField));
        assertThat(result.getPredecessor(succUnmapped).isPresent()).isFalse();
    }

    @Test
    public void testMappingWithMultipleRanges() throws IOException {
        //From mappables
        VCSFile.Range firstRange = createMockRange(111,
                222,
                "/path/to/file/with/temporaryField",
                true);
        VCSFile.Range secondRange = createMockRange(10,
                15,
                "/path/to/file/with/temporaryField",
                true);
        VCSFile.Range thirdRange = createMockRange(55,
                66,
                "/path/to/file/with/temporaryField",
                true);
        List<VCSFile.Range> ranges = Arrays.asList(firstRange, secondRange, thirdRange);
        MockMappable predTemporaryField =
                new MockMappable(ranges, "", "TemporaryField");
        firstRange = createMockRange(123,
                126,
                "/path/to/file/with/dataClump",
                true);
        secondRange = createMockRange(77,
                99,
                "/path/to/file/with/dataClump",
                true);
        thirdRange = createMockRange(25,
                50,
                "/path/to/file/with/dataClump",
                true);
        ranges = Arrays.asList(firstRange, secondRange, thirdRange);
        MockMappable predDataClump =
                new MockMappable(ranges, "", "DataClump");
        firstRange = createMockRange(155,
                255,
                "/path/to/file/with/deadCode",
                true);
        secondRange = createMockRange(99,
                103,
                "/path/to/file/with/deadCode",
                true);
        thirdRange = createMockRange(324,
                456,
                "/path/to/file/with/deadCode",
                true);
        ranges = Arrays.asList(firstRange, secondRange, thirdRange);
        MockMappable predDeadCode =
                new MockMappable(ranges, "", "DeadCode");
        List<Mappable<String>> from = Arrays.asList(
                predTemporaryField,
                predDataClump,
                predDeadCode);

        //To mappables
        firstRange = createMockRange(123,
                126,
                "/path/to/file/with/dataClump",
                false);
        secondRange = createMockRange(77,
                99,
                "/path/to/file/with/dataClump",
                false);
        thirdRange = createMockRange(25,
                50,
                "/path/to/file/with/dataClump",
                false);
        ranges = Arrays.asList(firstRange, secondRange, thirdRange);
        MockMappable succDataClump =
                new MockMappable(ranges, "", "DataClump");
        firstRange = createMockRange(111,
                222,
                "/path/to/file/with/temporaryField",
                false);
        secondRange = createMockRange(10,
                15,
                "/path/to/file/with/temporaryField",
                false);
        thirdRange = createMockRange(55,
                66,
                "/path/to/file/with/temporaryField",
                false);
        ranges = Arrays.asList(firstRange, secondRange, thirdRange);
        MockMappable succTemporaryField =
                new MockMappable(ranges, "", "TemporaryField");
        firstRange = createMockRange(155,
                255,
                "/path/to/file/with/deadCode",
                false);
        secondRange = createMockRange(99,
                103,
                "/path/to/file/with/deadCode",
                false);
        thirdRange = createMockRange(324,
                456,
                "/path/to/file/with/deadCode",
                false);
        ranges = Arrays.asList(firstRange, secondRange, thirdRange);
        MockMappable succDeadCode =
                new MockMappable(ranges, "", "DeadCode");
        firstRange = createMockRange(155,
                255,
                "/path/to/file/with/unmapped",
                false);
        secondRange = createMockRange(55,
                66,
                "/path/to/file/with/unmapped",
                false);
        thirdRange = createMockRange(25,
                50,
                "/path/to/file/with/unmapped",
                false);
        ranges = Arrays.asList(firstRange, secondRange, thirdRange);
        MockMappable succUnmapped =
                new MockMappable(ranges, "", "Unmapped");
        List<Mappable<String>> to = Arrays.asList(
                succDataClump,
                succDeadCode,
                succTemporaryField,
                succUnmapped);

        List<FileChange> fileChanges = new ArrayList<>();
        FileChange fileChange = createMockFileChange("/path/to/file/with/dataClump");
        fileChanges.add(fileChange);
        fileChange = createMockFileChange("/path/to/file/with/temporaryField");
        fileChanges.add(fileChange);
        fileChange = createMockFileChange("/path/to/file/with/deadCode");
        fileChanges.add(fileChange);
        when(revisionRange.getFileChanges()).thenReturn(fileChanges);

        Mapping.Result<String> result = mapping.map(from, to, revisionRange);
        from.forEach(mappable -> assertThat(result.getFrom().contains(mappable)).isTrue());
        to.forEach(mappable -> assertThat(result.getTo().contains(mappable)).isTrue());
        assertThat(result.getWithMapping().isEmpty()).isFalse();
        assertThat(result.getUnmapped().contains(succUnmapped)).isTrue();
        assertThat(result.getMapped().contains(succUnmapped)).isFalse();
        assertThat(result.getPredecessor(succDataClump))
                .isEqualTo(Optional.of(predDataClump));
        assertThat(result.getPredecessor(succDeadCode))
                .isEqualTo(Optional.of(predDeadCode));
        assertThat(result.getPredecessor(succTemporaryField))
                .isEqualTo(Optional.of(predTemporaryField));
        assertThat(result.getPredecessor(succUnmapped).isPresent()).isFalse();
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
        when(revision.getId()).thenReturn("2");
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
        when(revision.getId()).thenReturn("1");
        Revision predRevision = mock(Revision.class);
        when(predRevision.getId()).thenReturn("1");
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

    private VCSFile.Range createMockRange(final int withBeginPosition,
                                          final int withEndPosition,
                                          final String withPathToFile,
                                          final boolean isFromRange)
            throws IOException {
        VCSFile.Range range = mock(VCSFile.Range.class);
        when(range.apply(any(FileChange.class))).thenReturn(Optional.of(range));
        VCSFile.Position begin = mock(VCSFile.Position.class);
        when(begin.getOffset()).thenReturn(withBeginPosition);
        when(range.getBegin()).thenReturn(begin);
        VCSFile.Position end = mock(VCSFile.Position.class);
        when(end.getOffset()).thenReturn(withEndPosition);
        when(range.getEnd()).thenReturn(end);
        VCSFile file = mock(VCSFile.class);
        when(file.getPath()).thenReturn(withPathToFile);
        Revision revision = mock(Revision.class);
        when(file.getRevision()).thenReturn(revision);
        when(range.getFile()).thenReturn(file);
        if (isFromRange) {
            when(revision.getId()).thenReturn("1");
            Revision predRevision = mock(Revision.class);
            when(predRevision.getId()).thenReturn("1");
            when(revisionRange.getPredecessorRevision())
                    .thenReturn(Optional.of(predRevision));
        } else {
            when(revision.getId()).thenReturn("2");
        }
        return range;
    }

    private FileChange createMockFileChange(final String withPathToFile) {
        FileChange fileChange = mock(FileChange.class);
        VCSFile vcsFile = mock(VCSFile.class);
        when(vcsFile.getPath()).thenReturn(withPathToFile);
        when(fileChange.getType()).thenReturn(FileChange.Type.MODIFY);
        when(fileChange.getOldFile()).thenReturn(Optional.of(vcsFile));
        return fileChange;
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
