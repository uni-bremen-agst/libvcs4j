package de.unibremen.informatik.st.libvcs4j;

import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PositionTest {

	@Test
	public void applyFileChange() throws IOException {
		Revision oldRevision = mock(Revision.class);
		when(oldRevision.getId()).thenReturn("1");

		VCSFile oldFile = mock(VCSFile.class);
		when(oldFile.getRevision()).thenReturn(oldRevision);
		when(oldFile.getRelativePath()).thenReturn("A.java");
		when(oldFile.readLinesWithEOL()).thenReturn(
				Arrays.asList("first line\n", "second line\n", "third line"));
		when(oldFile.positionOf(2, 8, 4)).thenCallRealMethod();

		Revision newRevision = mock(Revision.class);
		when(newRevision.getId()).thenReturn("2");

		VCSFile newFile = mock(VCSFile.class);
		when(newFile.getRevision()).thenReturn(newRevision);
		when(newFile.getRelativePath()).thenReturn("A.java");
		when(newFile.readLinesWithEOL()).thenReturn(
				Arrays.asList("first line\n", "new line\n", "second line"));
		when(newFile.positionOf(anyInt(), anyInt(), anyInt()))
				.thenCallRealMethod();

		LineChange l1 = mock(LineChange.class);
		when(l1.getType()).thenReturn(LineChange.Type.INSERT);
		when(l1.getLine()).thenReturn(2);

		LineChange l2 = mock(LineChange.class);
		when(l2.getType()).thenReturn(LineChange.Type.DELETE);
		when(l2.getLine()).thenReturn(3);

		FileChange fileChange = mock(FileChange.class);
		when(fileChange.getOldFile()).thenReturn(Optional.of(oldFile));
		when(fileChange.getNewFile()).thenReturn(Optional.of(newFile));
		when(fileChange.computeDiff()).thenReturn(Arrays.asList(l1, l2));

		VCSFile.Position oldPosition = oldFile.positionOf(2, 8, 4);
		VCSFile.Position newPosition = oldPosition.apply(fileChange)
				.orElseThrow(AssertionError::new);

		assertThat(newPosition.getFile()).isSameAs(newFile);
		assertThat(newPosition.getLine()).isEqualTo(3);
		assertThat(newPosition.getColumn()).isEqualTo(8);
		assertThat(newPosition.getOffset()).isEqualTo(27);
		assertThat(newPosition.getTabSize()).isEqualTo(4);
	}
}
