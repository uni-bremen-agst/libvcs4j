package de.unibremen.informatik.st.libvcs4j;

import org.junit.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CommitTest {

	@Test
	public void isMergeWithToParents() {
		Commit commit = mock(Commit.class);
		when(commit.getParentIds()).thenReturn(Arrays.asList("1", "2"));
		when(commit.isMerge()).thenCallRealMethod();
		assertThat(commit.isMerge()).isTrue();
	}

	@Test
	public void isMergeWithOneParent() {
		Commit commit = mock(Commit.class);
		when(commit.getParentIds()).thenReturn(Arrays.asList("1"));
		when(commit.isMerge()).thenCallRealMethod();
		assertThat(commit.isMerge()).isFalse();
	}
}
