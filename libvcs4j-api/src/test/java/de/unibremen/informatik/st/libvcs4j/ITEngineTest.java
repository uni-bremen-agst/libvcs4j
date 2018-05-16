package de.unibremen.informatik.st.libvcs4j;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

public class ITEngineTest {

	private Issue i1;
	private Issue i2;
	private Issue i3;
	private Issue i4;
	private ITEngine itEngine;

	@Before
	public void setUp() throws IOException {
		i1 = mock(Issue.class);
		when(i1.getId()).thenReturn("1");
		i2 = mock(Issue.class);
		when(i2.getId()).thenReturn("2");
		i3 = mock(Issue.class);
		when(i3.getId()).thenReturn("3");
		i4 = mock(Issue.class);
		when(i4.getId()).thenReturn("4");

		itEngine = spy(ITEngine.class);
		when(itEngine.getIssueById(i1.getId())).thenReturn(Optional.of(i1));
		when(itEngine.getIssueById(i2.getId())).thenReturn(Optional.of(i2));
		when(itEngine.getIssueById(i3.getId())).thenReturn(Optional.of(i3));
		when(itEngine.getIssueById(i4.getId())).thenReturn(Optional.of(i4));
	}

	@Test
	public void getIssuesForCommit() throws IOException {
		Commit commit = spy(Commit.class);
		when(commit.getMessage()).thenReturn("#1, #2foo#3 bar #4");

		assertThat(itEngine.getIssuesFor(commit))
				.containsExactly(i1, i2, i3, i4);
	}

	@Test
	public void getIssuesForCommitWithDuplicates() throws IOException {
		Commit commit = spy(Commit.class);
		when(commit.getMessage()).thenReturn("#1 #2 #1 #2 #4 #2");

		assertThat(itEngine.getIssuesFor(commit))
				.hasSize(3)
				.containsExactly(i1, i2, i4);
	}

	@Test
	public void getIssuesForRevisionRange() throws IOException {
		Commit c1 = spy(Commit.class);
		when(c1.getMessage()).thenReturn("#1");
		Commit c2 = spy(Commit.class);
		when(c2.getMessage()).thenReturn("#2");
		Commit c3 = spy(Commit.class);
		when(c3.getMessage()).thenReturn("#4");

		RevisionRange range = spy(RevisionRange.class);
		when(range.getCommits()).thenReturn(Arrays.asList(c1, c2, c3));

		assertThat(itEngine.getIssuesFor(range))
				.hasSize(3)
				.containsExactly(i1, i2, i4);
	}

	@Test
	public void getIssuesForRevisionRangeWithDuplicates() throws IOException {
		Commit c1 = spy(Commit.class);
		when(c1.getMessage()).thenReturn("#2");
		Commit c2 = spy(Commit.class);
		when(c2.getMessage()).thenReturn("#2");
		Commit c3 = spy(Commit.class);
		when(c3.getMessage()).thenReturn("#3");

		RevisionRange range = spy(RevisionRange.class);
		when(range.getCommits()).thenReturn(Arrays.asList(c1, c2, c3));

		assertThat(itEngine.getIssuesFor(range))
				.hasSize(2)
				.containsExactly(i2, i3);
	}
}
