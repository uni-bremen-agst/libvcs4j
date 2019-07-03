package de.unibremen.informatik.st.libvcs4j;

import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

public class FileChangeTest {

	@Test
	public void getTypeOld() {
		VCSFile old = mock(VCSFile.class);

		FileChange change = mock(FileChange.class);
		when(change.getOldFile()).thenReturn(Optional.of(old));
		when(change.getNewFile()).thenReturn(Optional.empty());
		when(change.getType()).thenCallRealMethod();

		assertThat(change.getType()).isEqualTo(FileChange.Type.REMOVE);
	}

	@Test
	public void getTypeNew() {
		VCSFile nev = mock(VCSFile.class);

		FileChange change = mock(FileChange.class);
		when(change.getOldFile()).thenReturn(Optional.empty());
		when(change.getNewFile()).thenReturn(Optional.of(nev));
		when(change.getType()).thenCallRealMethod();

		assertThat(change.getType()).isEqualTo(FileChange.Type.ADD);
	}

	@Test
	public void getTypeModify() {
		VCSFile old = mock(VCSFile.class);
		when(old.getRelativePath()).thenReturn("src/main/A.java");

		VCSFile nev = mock(VCSFile.class);
		when(nev.getRelativePath()).thenReturn("src/main/A.java");

		FileChange change = mock(FileChange.class);
		when(change.getOldFile()).thenReturn(Optional.of(old));
		when(change.getNewFile()).thenReturn(Optional.of(nev));
		when(change.getType()).thenCallRealMethod();

		assertThat(change.getType()).isEqualTo(FileChange.Type.MODIFY);
	}

	@Test
	public void getTypeRelocate() {
		VCSFile old = mock(VCSFile.class);
		when(old.getRelativePath()).thenReturn("src/main/A.java");

		VCSFile nev = mock(VCSFile.class);
		when(nev.getRelativePath()).thenReturn("src/main/B.java");

		FileChange change = mock(FileChange.class);
		when(change.getOldFile()).thenReturn(Optional.of(old));
		when(change.getNewFile()).thenReturn(Optional.of(nev));
		when(change.getType()).thenCallRealMethod();

		assertThat(change.getType()).isEqualTo(FileChange.Type.RELOCATE);
	}

	@Test
	public void computeLineDeltaPositive() throws IOException {
		LineChange l1 = mock(LineChange.class);
		when(l1.getType()).thenReturn(LineChange.Type.INSERT);

		LineChange l2 = mock(LineChange.class);
		when(l2.getType()).thenReturn(LineChange.Type.INSERT);

		LineChange l3 = mock(LineChange.class);
		when(l3.getType()).thenReturn(LineChange.Type.INSERT);

		LineChange l4 = mock(LineChange.class);
		when(l4.getType()).thenReturn(LineChange.Type.DELETE);

		FileChange change = mock(FileChange.class);
		when(change.computeDiff()).thenReturn(Arrays.asList(l1, l2, l3, l4));
		when(change.computeLineDelta()).thenCallRealMethod();

		assertThat(change.computeLineDelta()).isEqualTo(2);
	}

	@Test
	public void computeLineDeltaNegative() throws IOException {
		LineChange l1 = mock(LineChange.class);
		when(l1.getType()).thenReturn(LineChange.Type.DELETE);

		LineChange l2 = mock(LineChange.class);
		when(l2.getType()).thenReturn(LineChange.Type.DELETE);

		LineChange l3 = mock(LineChange.class);
		when(l3.getType()).thenReturn(LineChange.Type.INSERT);

		LineChange l4 = mock(LineChange.class);
		when(l4.getType()).thenReturn(LineChange.Type.DELETE);

		FileChange change = mock(FileChange.class);
		when(change.computeDiff()).thenReturn(Arrays.asList(l1, l2, l3, l4));
		when(change.computeLineDelta()).thenCallRealMethod();

		assertThat(change.computeLineDelta()).isEqualTo(-2);
	}

	@Test
	public void computeDiffBinaryFileOld() throws IOException {
		VCSFile old = mock(VCSFile.class);
		when(old.isBinary()).thenReturn(true);

		VCSFile nev = mock(VCSFile.class);
		when(nev.isBinary()).thenReturn(false);

		FileChange change = mock(FileChange.class);
		when(change.getOldFile()).thenReturn(Optional.of(old));
		when(change.getNewFile()).thenReturn(Optional.of(nev));
		when(change.computeDiff()).thenCallRealMethod();

		assertThatExceptionOfType(BinaryFileException.class)
				.isThrownBy(change::computeDiff);
	}

	@Test
	public void computeDiffBinaryFileNew() throws IOException {
		VCSFile old = mock(VCSFile.class);
		when(old.isBinary()).thenReturn(false);

		VCSFile nev = mock(VCSFile.class);
		when(nev.isBinary()).thenReturn(true);

		FileChange change = mock(FileChange.class);
		when(change.getOldFile()).thenReturn(Optional.of(old));
		when(change.getNewFile()).thenReturn(Optional.of(nev));
		when(change.computeDiff()).thenCallRealMethod();

		assertThatExceptionOfType(BinaryFileException.class)
				.isThrownBy(change::computeDiff);
	}
}
