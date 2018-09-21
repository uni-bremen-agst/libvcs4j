package de.unibremen.informatik.st.libvcs4j;

import org.junit.Test;

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
}
