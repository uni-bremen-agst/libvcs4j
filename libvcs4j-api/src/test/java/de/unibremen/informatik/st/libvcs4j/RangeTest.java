package de.unibremen.informatik.st.libvcs4j;

import org.junit.Test;

import java.nio.file.Paths;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

public class RangeTest {

	@Test
	public void mergeWithGap() {
		Revision revision = mock(Revision.class);
		when(revision.getId()).thenReturn("1");
		VCSFile file = mock(VCSFile.class);
		when(file.getRevision()).thenReturn(revision);
		when(file.getRelativePath()).thenReturn("A.java");

		VCSFile.Position begin1 = mock(VCSFile.Position.class);
		when(begin1.getFile()).thenReturn(file);
		when(begin1.getOffset()).thenReturn(10);
		VCSFile.Position end1 = mock(VCSFile.Position.class);
		when(end1.getFile()).thenReturn(file);
		when(end1.getOffset()).thenReturn(20);

		VCSFile.Position begin2 = mock(VCSFile.Position.class);
		when(begin2.getFile()).thenReturn(file);
		when(begin2.getOffset()).thenReturn(22);
		VCSFile.Position end2 = mock(VCSFile.Position.class);
		when(end2.getFile()).thenReturn(file);
		when(end2.getOffset()).thenReturn(30);

		VCSFile.Range range1 = new VCSFile.Range(begin1, end1);
		VCSFile.Range range2 = new VCSFile.Range(begin2, end2);
		assertThat(range1.merge(range2)).isEmpty();
	}

	@Test
	public void mergeSuccessive() {
		Revision revision = mock(Revision.class);
		when(revision.getId()).thenReturn("1");
		VCSFile file = mock(VCSFile.class);
		when(file.getRevision()).thenReturn(revision);
		when(file.getRelativePath()).thenReturn("A.java");

		VCSFile.Position begin1 = mock(VCSFile.Position.class);
		when(begin1.getFile()).thenReturn(file);
		when(begin1.getOffset()).thenReturn(10);
		VCSFile.Position end1 = mock(VCSFile.Position.class);
		when(end1.getFile()).thenReturn(file);
		when(end1.getOffset()).thenReturn(20);

		VCSFile.Position begin2 = mock(VCSFile.Position.class);
		when(begin2.getFile()).thenReturn(file);
		when(begin2.getOffset()).thenReturn(21);
		VCSFile.Position end2 = mock(VCSFile.Position.class);
		when(end2.getFile()).thenReturn(file);
		when(end2.getOffset()).thenReturn(30);

		VCSFile.Range range1 = new VCSFile.Range(begin1, end1);
		VCSFile.Range range2 = new VCSFile.Range(begin2, end2);
		VCSFile.Range merge = range1.merge(range2)
				.orElseThrow(AssertionError::new);
		assertThat(merge.getBegin().getOffset()).isEqualTo(begin1.getOffset());
		assertThat(merge.getEnd().getOffset()).isEqualTo(end2.getOffset());
	}

	@Test
	public void mergeOverlapping() {
		Revision revision = mock(Revision.class);
		when(revision.getId()).thenReturn("1");
		VCSFile file = mock(VCSFile.class);
		when(file.getRevision()).thenReturn(revision);
		when(file.getRelativePath()).thenReturn("A.java");

		VCSFile.Position begin1 = mock(VCSFile.Position.class);
		when(begin1.getFile()).thenReturn(file);
		when(begin1.getOffset()).thenReturn(10);
		VCSFile.Position end1 = mock(VCSFile.Position.class);
		when(end1.getFile()).thenReturn(file);
		when(end1.getOffset()).thenReturn(20);

		VCSFile.Position begin2 = mock(VCSFile.Position.class);
		when(begin2.getFile()).thenReturn(file);
		when(begin2.getOffset()).thenReturn(15);
		VCSFile.Position end2 = mock(VCSFile.Position.class);
		when(end2.getFile()).thenReturn(file);
		when(end2.getOffset()).thenReturn(25);

		VCSFile.Range range1 = new VCSFile.Range(begin1, end1);
		VCSFile.Range range2 = new VCSFile.Range(begin2, end2);
		VCSFile.Range merge = range1.merge(range2)
				.orElseThrow(AssertionError::new);
		assertThat(merge.getBegin().getOffset()).isEqualTo(begin1.getOffset());
		assertThat(merge.getEnd().getOffset()).isEqualTo(end2.getOffset());
	}

	@Test
	public void mergeSubsuming() {
		Revision revision = mock(Revision.class);
		when(revision.getId()).thenReturn("1");
		VCSFile file = mock(VCSFile.class);
		when(file.getRevision()).thenReturn(revision);
		when(file.getRelativePath()).thenReturn("A.java");

		VCSFile.Position begin1 = mock(VCSFile.Position.class);
		when(begin1.getFile()).thenReturn(file);
		when(begin1.getOffset()).thenReturn(10);
		VCSFile.Position end1 = mock(VCSFile.Position.class);
		when(end1.getFile()).thenReturn(file);
		when(end1.getOffset()).thenReturn(20);

		VCSFile.Position begin2 = mock(VCSFile.Position.class);
		when(begin2.getFile()).thenReturn(file);
		when(begin2.getOffset()).thenReturn(13);
		VCSFile.Position end2 = mock(VCSFile.Position.class);
		when(end2.getFile()).thenReturn(file);
		when(end2.getOffset()).thenReturn(18);

		VCSFile.Range range1 = new VCSFile.Range(begin1, end1);
		VCSFile.Range range2 = new VCSFile.Range(begin2, end2);
		VCSFile.Range merge = range1.merge(range2)
				.orElseThrow(AssertionError::new);
		assertThat(merge.getBegin().getOffset()).isEqualTo(begin1.getOffset());
		assertThat(merge.getEnd().getOffset()).isEqualTo(end1.getOffset());
	}

	@Test
	public void mergeSubsumingWithEqualEndOffset() {
		Revision revision = mock(Revision.class);
		when(revision.getId()).thenReturn("1");
		VCSFile file = mock(VCSFile.class);
		when(file.getRevision()).thenReturn(revision);
		when(file.getRelativePath()).thenReturn("A.java");

		VCSFile.Position begin1 = mock(VCSFile.Position.class);
		when(begin1.getFile()).thenReturn(file);
		when(begin1.getOffset()).thenReturn(10);
		VCSFile.Position end1 = mock(VCSFile.Position.class);
		when(end1.getFile()).thenReturn(file);
		when(end1.getOffset()).thenReturn(20);

		VCSFile.Position begin2 = mock(VCSFile.Position.class);
		when(begin2.getFile()).thenReturn(file);
		when(begin2.getOffset()).thenReturn(14);
		VCSFile.Position end2 = mock(VCSFile.Position.class);
		when(end2.getFile()).thenReturn(file);
		when(end2.getOffset()).thenReturn(20);

		VCSFile.Range range1 = new VCSFile.Range(begin1, end1);
		VCSFile.Range range2 = new VCSFile.Range(begin2, end2);
		VCSFile.Range merge = range1.merge(range2)
				.orElseThrow(AssertionError::new);
		assertThat(merge.getBegin().getOffset()).isEqualTo(begin1.getOffset());
		assertThat(merge.getEnd().getOffset()).isEqualTo(end1.getOffset());
	}

	@Test
	public void relativePathPredicate() {
		VCSFile file = mock(VCSFile.class);
		when(file.toRelativePath()).thenReturn(
				Paths.get("src", "main", "File.java"));

		VCSFile.Position r1b = mock(VCSFile.Position.class);
		when(r1b.getFile()).thenReturn(file);
		when(r1b.getOffset()).thenReturn(500);
		VCSFile.Position r1e = mock(VCSFile.Position.class);
		when(r1e.getFile()).thenReturn(file);
		when(r1e.getOffset()).thenReturn(800);
		VCSFile.Range r1 = mock(VCSFile.Range.class);
		when(r1.getBegin()).thenReturn(r1b);
		when(r1.getEnd()).thenReturn(r1e);

		VCSFile.Position r2b = mock(VCSFile.Position.class);
		when(r2b.getFile()).thenReturn(file);
		when(r2b.getOffset()).thenReturn(500);
		VCSFile.Position r2e = mock(VCSFile.Position.class);
		when(r2e.getFile()).thenReturn(file);
		when(r2e.getOffset()).thenReturn(800, 801, 799);
		VCSFile.Range r2 = mock(VCSFile.Range.class);
		when(r2.getBegin()).thenReturn(r2b);
		when(r2.getEnd()).thenReturn(r2e);

		// Only the first call matches.
		assertThat(VCSFile.Range.RELATIVE_PATH_PREDICATE
				.test(r1, r2)).isTrue();
		assertThat(VCSFile.Range.RELATIVE_PATH_PREDICATE
				.test(r1, r2)).isFalse();
		assertThat(VCSFile.Range.RELATIVE_PATH_PREDICATE
				.test(r1, r2)).isFalse();
	}

	@Test
	public void relativePathPredicateNullWithNull() {
		assertThat(VCSFile.Range.RELATIVE_PATH_PREDICATE.test(null, null))
				.isTrue();
	}

	@Test
	public void relativePathPredicateNullWithNonNull() {
		assertThat(VCSFile.Range.RELATIVE_PATH_PREDICATE.test(null,
				mock(VCSFile.Range.class))).isFalse();
	}

	@Test
	public void lengthOfOverlapping() {
		Revision revision = mock(Revision.class);
		when(revision.getId()).thenReturn("1");

		VCSFile file = mock(VCSFile.class);
		when(file.getRevision()).thenReturn(revision);
		when(file.getRelativePath()).thenReturn("LengthOf.java");

		VCSFile.Position range1Begin = mock(VCSFile.Position.class);
		when(range1Begin.getFile()).thenReturn(file);
		when(range1Begin.getOffset()).thenReturn(10);
		VCSFile.Position range1End = mock(VCSFile.Position.class);
		when(range1End.getFile()).thenReturn(file);
		when(range1End.getOffset()).thenReturn(50);
		VCSFile.Range range1 = mock(VCSFile.Range.class);
		when(range1.getFile()).thenReturn(file);
		when(range1.getBegin()).thenReturn(range1Begin);
		when(range1.getEnd()).thenReturn(range1End);
		when(range1.merge(any())).thenCallRealMethod();

		VCSFile.Position range2Begin = mock(VCSFile.Position.class);
		when(range2Begin.getFile()).thenReturn(file);
		when(range2Begin.getOffset()).thenReturn(30);
		VCSFile.Position range2End = mock(VCSFile.Position.class);
		when(range2End.getFile()).thenReturn(file);
		when(range2End.getOffset()).thenReturn(70);
		VCSFile.Range range2 = mock(VCSFile.Range.class);
		when(range2.getFile()).thenReturn(file);
		when(range2.getBegin()).thenReturn(range2Begin);
		when(range2.getEnd()).thenReturn(range2End);
		when(range2.merge(any())).thenCallRealMethod();

		int length = VCSFile.Range.lengthOf(Arrays.asList(range1, range2));
		assertThat(length).isEqualTo(61);
	}
}
