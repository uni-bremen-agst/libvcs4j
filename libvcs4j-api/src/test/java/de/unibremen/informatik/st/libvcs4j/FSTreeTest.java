package de.unibremen.informatik.st.libvcs4j;

import org.junit.Test;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FSTreeTest {

	@Test
	public void emptyTree() {
		FSTree<Void> tree = FSTree.of(
				Collections.emptyList(),
				f -> null,
				(f1, f2) -> null);

		assertThat(tree.getPath()).isEqualTo(FSTree.EMPTY_DIRECTORY);
	}

	@Test
	public void directoryWithThreeFiles() {
		VCSFile a = mock(VCSFile.class);
		when(a.toRelativePath()).thenReturn(Paths.get("src", "A.java"));
		VCSFile b = mock(VCSFile.class);
		when(b.toRelativePath()).thenReturn(Paths.get("src", "B.java"));
		VCSFile c = mock(VCSFile.class);
		when(c.toRelativePath()).thenReturn(Paths.get("src", "C.java"));

		FSTree<Integer> tree = FSTree.of(
				Arrays.asList(a, b, c),
				f -> null,
				Integer::sum);

		assertThat(tree.getPath()).isEqualTo("src");
		assertThat(tree.getNodes())
				.extracting(FSTree::getFile)
				.extracting(Optional::get)
				.contains(a, b, c);
	}

	@Test
	public void mapAggregate() {
		VCSFile a = mock(VCSFile.class);
		when(a.toRelativePath()).thenReturn(Paths.get("dir1", "A.java"));
		VCSFile b = mock(VCSFile.class);
		when(b.toRelativePath()).thenReturn(Paths.get("dir1", "B.java"));
		VCSFile c = mock(VCSFile.class);
		when(c.toRelativePath()).thenReturn(Paths.get("dir2", "C.java"));

		FSTree<Integer> tree = FSTree.of(
				Arrays.asList(a, b, c),
				f -> 1,
				Integer::sum);

		assertThat(tree.getValue()).hasValue(3);
		assertThat(tree.navigateTo("dir1"))
				.flatMap(FSTree::getValue)
				.hasValue(2);
		assertThat(tree.navigateTo("dir2"))
				.flatMap(FSTree::getValue)
				.hasValue(1);
	}
}
