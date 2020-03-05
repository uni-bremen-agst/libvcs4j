package de.unibremen.informatik.st.libvcs4j.mapping;

import de.unibremen.informatik.st.libvcs4j.FileChange;
import de.unibremen.informatik.st.libvcs4j.Revision;
import de.unibremen.informatik.st.libvcs4j.RevisionRange;
import de.unibremen.informatik.st.libvcs4j.VCSFile;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class MappingTest {

    private Mapping<String> mapping;

    private RevisionRange revisionRange;

    @Rule
    public ExpectedException expected = ExpectedException.none();

    @Before
    public void setUp() {
        mapping = new Mapping<>();
        revisionRange = mock(RevisionRange.class);
        Revision revision = mock(Revision.class);
        when(revision.getId()).thenReturn("2");
        when(revisionRange.getRevision()).thenReturn(revision);
    }

    @Test
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
        
        expected.expect(IllegalArgumentException.class);
        mapping.map(new ArrayList<>(), to, revisionRange);
    }

    @Test
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

        expected.expect(IllegalArgumentException.class);
        mapping.map(from, to, revisionRange);
    }

    @Test
    public void testMappingWithSignatures() throws IOException {
        //From mappables
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

        //To mappables
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
        assertThat(result.getWithSuccessor().isEmpty()).isFalse();
        assertThat(result.getWithoutSuccessor().isEmpty()).isTrue();
        assertThat(result.getPredecessor(succDataClump))
                .isEqualTo(Optional.of(predDataClump));
        assertThat(result.getPredecessor(succDeadCode))
                .isEqualTo(Optional.of(predDeadCode));
        assertThat(result.getPredecessor(succTemporaryField))
                .isEqualTo(Optional.of(predTemporaryField));
    }

    @Test
    public void testMappingWithSingleRange() throws IOException {
        //From mappables
        VCSFile.Range range = createMockRange(111, 222,
                "/path/to/file/with/temporaryField", true);
        MockMappable predTemporaryField = new MockMappable(
                singletonList(range), null, "TemporaryField");
        range = createMockRange(123, 126,
                "/path/to/file/with/dataClump", true);
        MockMappable predDataClump = new MockMappable(
                singletonList(range), null, "DataClump");
        range = createMockRange(155, 255,
                "/path/to/file/with/deadCode", true);
        MockMappable predDeadCode = new MockMappable(
                singletonList(range), null, "DeadCode");
        List<Mappable<String>> from = Arrays.asList(
                null,
                predTemporaryField,
                predDataClump,
                predDeadCode,
                null);

        //To mappables
        range = createMockRange(123, 126,
                "/path/to/file/with/dataClump", false);
        MockMappable succDataClump = new MockMappable(
                singletonList(range), null, "DataClump");
        range = createMockRange(111, 222,
                "/path/to/file/with/temporaryField", false);
        MockMappable succTemporaryField = new MockMappable(
                Collections.singletonList(range), null, "TemporaryField");
        range = createMockRange(155, 255,
                "/path/to/file/with/deadCode", false);
        MockMappable succDeadCode = new MockMappable(
                singletonList(range), null, "DeadCode");
        range = createMockRange(77, 88,
                "/path/to/file/with/unmapped", false);
        MockMappable succUnmapped = new MockMappable(
                Collections.singletonList(range), null, "Unmapped");
        List<Mappable<String>> to = Arrays.asList(
                succDataClump,
                succDeadCode,
                null,
                succTemporaryField,
                succUnmapped,
                null);

        List<FileChange> fileChanges = new ArrayList<>();
        FileChange fileChange = createMockFileChange(
                "/path/to/file/with/dataClump");
        fileChanges.add(fileChange);
        fileChange = createMockFileChange(
                "/path/to/file/with/temporaryField");
        fileChanges.add(fileChange);
        fileChange = createMockFileChange(
                "/path/to/file/with/deadCode");
        fileChanges.add(fileChange);
        when(revisionRange.getFileChanges()).thenReturn(fileChanges);

        Mapping.Result<String> result = mapping.map(from, to, revisionRange);
        from.stream()
                .filter(Objects::nonNull)
                .forEach(m -> assertThat(result.getFrom()).contains(m));
        to.stream()
                .filter(Objects::nonNull)
                .forEach(m -> assertThat(result.getTo()).contains(m));
        assertThat(result.getWithSuccessor())
                .isNotEmpty();
        assertThat(result.getWithoutPredecessor())
                .contains(succUnmapped);
        assertThat(result.getWithPredecessor())
                .doesNotContain(succUnmapped);
        assertThat(result.getPredecessor(succDataClump))
                .hasValue(predDataClump);
        assertThat(result.getPredecessor(succDeadCode))
                .hasValue(predDeadCode);
        assertThat(result.getPredecessor(succTemporaryField))
                .hasValue(predTemporaryField);
        assertThat(result.getPredecessor(succUnmapped))
                .isEmpty();
    }

    @Test
    public void testMappingWithMultipleRanges() throws IOException {
        //From mappables
        VCSFile.Range firstRange = createMockRange(111, 222,
                "/path/to/file/with/temporaryField", true);
        VCSFile.Range secondRange = createMockRange(10, 15,
                "/path/to/file/with/temporaryField", true);
        VCSFile.Range thirdRange = createMockRange(55, 66,
                "/path/to/file/with/temporaryField", true);
        List<VCSFile.Range> ranges = Arrays.asList(
                firstRange, secondRange, thirdRange);
        MockMappable predTemporaryField = new MockMappable(
                ranges, null, "TemporaryField");

        firstRange = createMockRange(123, 126,
                "/path/to/file/with/dataClump", true);
        secondRange = createMockRange(77, 99,
                "/path/to/file/with/dataClump", true);
        thirdRange = createMockRange(25, 50,
                "/path/to/file/with/dataClump", true);
        ranges = Arrays.asList(firstRange, secondRange, thirdRange);
        MockMappable predDataClump = new MockMappable(
                ranges, null, "DataClump");

        firstRange = createMockRange(155, 255,
                "/path/to/file/with/deadCode", true);
        secondRange = createMockRange(99, 103,
                "/path/to/file/with/deadCode", true);
        thirdRange = createMockRange(324, 456,
                "/path/to/file/with/deadCode", true);
        ranges = Arrays.asList(firstRange, secondRange, thirdRange);
        MockMappable predDeadCode = new MockMappable(
                ranges, null, "DeadCode");

        List<Mappable<String>> from = Arrays.asList(
                predTemporaryField,
                predDataClump,
                predDeadCode);

        //To mappables
        firstRange = createMockRange(123, 126,
                "/path/to/file/with/dataClump", false);
        secondRange = createMockRange(77, 99,
                "/path/to/file/with/dataClump", false);
        thirdRange = createMockRange(25, 50,
                "/path/to/file/with/dataClump", false);
        ranges = Arrays.asList(firstRange, secondRange, thirdRange);
        MockMappable succDataClump = new MockMappable(
                ranges, null, "DataClump");

        firstRange = createMockRange(111, 222,
                "/path/to/file/with/temporaryField", false);
        secondRange = createMockRange(10, 15,
                "/path/to/file/with/temporaryField", false);
        thirdRange = createMockRange(55, 66,
                "/path/to/file/with/temporaryField", false);
        ranges = Arrays.asList(firstRange, secondRange, thirdRange);
        MockMappable succTemporaryField = new MockMappable(
                ranges, null, "TemporaryField");

        firstRange = createMockRange(155, 255,
                "/path/to/file/with/deadCode", false);
        secondRange = createMockRange(99, 103,
                "/path/to/file/with/deadCode", false);
        thirdRange = createMockRange(324, 456,
                "/path/to/file/with/deadCode", false);
        ranges = Arrays.asList(firstRange, secondRange, thirdRange);
        MockMappable succDeadCode = new MockMappable(
                ranges, null, "DeadCode");

        firstRange = createMockRange(155, 255,
                "/path/to/file/with/unmapped", false);
        secondRange = createMockRange(55,
                66, "/path/to/file/with/unmapped", false);
        thirdRange = createMockRange(25, 50,
                "/path/to/file/with/unmapped", false);
        ranges = Arrays.asList(firstRange, secondRange, thirdRange);
        MockMappable succUnmapped = new MockMappable(
                ranges, null, "Unmapped");

        List<Mappable<String>> to = Arrays.asList(
                succDataClump,
                succDeadCode,
                succTemporaryField,
                succUnmapped);

        List<FileChange> fileChanges = new ArrayList<>();
        FileChange fileChange = createMockFileChange(
                "/path/to/file/with/dataClump");
        fileChanges.add(fileChange);
        fileChange = createMockFileChange(
                "/path/to/file/with/temporaryField");
        fileChanges.add(fileChange);
        fileChange = createMockFileChange(
                "/path/to/file/with/deadCode");
        fileChanges.add(fileChange);
        when(revisionRange.getFileChanges()).thenReturn(fileChanges);

        Mapping.Result<String> result = mapping.map(from, to, revisionRange);
        from.forEach(m -> assertThat(result.getFrom()).contains(m));
        to.forEach(m -> assertThat(result.getTo()).contains(m));
        assertThat(result.getWithSuccessor())
                .isNotEmpty();
        assertThat(result.getWithoutPredecessor())
                .contains(succUnmapped);
        assertThat(result.getWithPredecessor())
                .containsExactlyInAnyOrder(
                        succDataClump, succDeadCode, succTemporaryField);
        assertThat(result.getPredecessor(succDataClump))
                .hasValue(predDataClump);
        assertThat(result.getPredecessor(succDeadCode))
                .hasValue(predDeadCode);
        assertThat(result.getPredecessor(succTemporaryField))
                .hasValue(predTemporaryField);
        assertThat(result.getPredecessor(succUnmapped)).isEmpty();
    }

    @Test
    public void testNoMappingFound() throws IOException {
        //From mappables
        VCSFile.Range range = createMockRange(111, 222,
                "/path/to/file/with/temporaryField", true);
        MockMappable predTemporaryField = new MockMappable(
                singletonList(range), null, "TemporaryField");
        range = createMockRange(123, 126,
                "/path/to/file/with/dataClump", true);
        MockMappable predDataClump = new MockMappable(
                singletonList(range), null, "DataClump");
        range = createMockRange(155, 255,
                "/path/to/file/with/deadCode", true);
        MockMappable predDeadCode = new MockMappable(
                singletonList(range), null, "DeadCode");
        List<Mappable<String>> from = Arrays.asList(
                predTemporaryField,
                predDataClump,
                predDeadCode);

        //To mappables
        range = createMockRange(123, 324,
                "/path/to/file/with/dataClump", false);
        MockMappable succDataClump = new MockMappable(
                singletonList(range), null, "DataClump");
        range = createMockRange(44, 55,
                "/path/to/file/with/temporaryField", false);
        MockMappable succTemporaryField = new MockMappable(
                singletonList(range), null, "TemporaryField");
        range = createMockRange(99, 345,
                "/path/to/file/with/deadCode", false);
        MockMappable succDeadCode = new MockMappable(
                singletonList(range), null, "DeadCode");
        List<Mappable<String>> to = Arrays.asList(
                succDataClump,
                succDeadCode,
                succTemporaryField);

        List<FileChange> fileChanges = new ArrayList<>();
        FileChange fileChange = createMockFileChange(
                "/path/to/file/with/dataClump");
        fileChanges.add(fileChange);
        fileChange = createMockFileChange(
                "/path/to/file/with/temporaryField");
        fileChanges.add(fileChange);
        fileChange = createMockFileChange(
                "/path/to/file/with/deadCode");
        fileChanges.add(fileChange);
        when(revisionRange.getFileChanges()).thenReturn(fileChanges);

        Mapping.Result<String> result = mapping.map(from, to, revisionRange);
        from.forEach(m -> assertThat(result.getFrom()).contains(m));
        to.forEach(m -> assertThat(result.getTo()).contains(m));
        assertThat(result.getWithoutPredecessor()).isNotEmpty();
        assertThat(result.getWithoutSuccessor()).isNotEmpty();
        assertThat(result.getWithSuccessor()).isEmpty();
        assertThat(result.getPredecessor(succDataClump)).isEmpty();
        assertThat(result.getPredecessor(succDeadCode)).isEmpty();
        assertThat(result.getPredecessor(succTemporaryField)).isEmpty();
    }


    ////////////////////////////////////////////////////////////////////////////
    ///////////////////////// Helper methods ///////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////

    private VCSFile.Range createMockRange(final int withBeginPosition,
            final int withEndPosition, final String withPathToFile,
            final boolean isFromRange) throws IOException {
        VCSFile.Range range = mock(VCSFile.Range.class);
        when(range.apply(any(FileChange.class))).thenReturn(Optional.of(range));
        VCSFile.Position begin = mock(VCSFile.Position.class);
        when(begin.getOffset()).thenReturn(withBeginPosition);
        when(range.getBegin()).thenReturn(begin);
        VCSFile.Position end = mock(VCSFile.Position.class);
        when(end.getOffset()).thenReturn(withEndPosition);
        when(range.getEnd()).thenReturn(end);
        VCSFile file = mock(VCSFile.class);
        when(file.getRelativePath()).thenReturn(withPathToFile);
        when(file.toRelativePath()).thenCallRealMethod();
        when(begin.getFile()).thenReturn(file);
        when(end.getFile()).thenReturn(file);
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
        when(vcsFile.getRelativePath()).thenReturn(withPathToFile);
        when(vcsFile.toRelativePath()).thenCallRealMethod();
        when(fileChange.getType()).thenReturn(FileChange.Type.MODIFY);
        when(fileChange.getOldFile()).thenReturn(Optional.of(vcsFile));
        return fileChange;
    }

    ////////////////////////////////////////////////////////////////////////////
    ///////////////////////// Helper class /////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////

    @AllArgsConstructor
    private class MockMappable implements Mappable<String> {

        @Getter
        @NonNull
        private List<VCSFile.Range> ranges;
        private String signature;
        private String metadata;

        @Override
        public Optional<String> getSignature() {
            return Optional.ofNullable(signature);
        }

        @Override
        public Optional<String> getMetadata() {
            return Optional.ofNullable(metadata);
        }
    }
}
