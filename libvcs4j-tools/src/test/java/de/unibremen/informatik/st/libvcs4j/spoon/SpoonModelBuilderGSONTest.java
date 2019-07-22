package de.unibremen.informatik.st.libvcs4j.spoon;

import de.unibremen.informatik.st.libvcs4j.FileChange;
import de.unibremen.informatik.st.libvcs4j.Revision;
import de.unibremen.informatik.st.libvcs4j.RevisionRange;
import de.unibremen.informatik.st.libvcs4j.VCSEngine;
import de.unibremen.informatik.st.libvcs4j.VCSEngineBuilder;
import de.unibremen.informatik.st.libvcs4j.VCSFile;
import de.unibremen.informatik.st.libvcs4j.testutils.ResourceExtractor;
import lombok.AllArgsConstructor;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SpoonModelBuilderGSONTest {

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();
	private Path repository;
	private Path target;

	@Before
	public void setUp() throws IOException {
		ResourceExtractor extractor = new ResourceExtractor();
		repository = extractor
				.extractTarGZ("gson/gson.tar.gz")
				.resolve("gson");
		target = Paths.get(folder.getRoot().getAbsolutePath(), "target");
	}

	@Test
	public void nullPointerInUpdateFrom4To6() throws BuildException {
		VCSEngine engine = VCSEngineBuilder
				.ofGit(repository.toString())
				.withTarget(target.toAbsolutePath())
				.withStartIdx(4)
				.withEndIdx(6)
				.build();

		SpoonModelBuilder builder = new SpoonModelBuilder();
		builder.setIncremental(true);
		for (RevisionRange range : engine) {
			assertThat(builder.update(range)).isNotNull();
		}
	}

	@Test
	public void nullPointerInUpdateFrom565To567() throws Exception {
		VCSEngine engine = VCSEngineBuilder
				.ofGit(repository.toString())
				.withRoot("gson/src/main")
				.withTarget(target.toAbsolutePath())
				.withStartIdx(565)
				.withEndIdx(566)
				.build();

		SpoonModelBuilder builder = new SpoonModelBuilder();
		builder.setIncremental(true);

		// To raise the NullPointerException deterministically, we need to
		// delete the following java files one after another. Otherwise,
		// 'package-info.java' may not be deleted at last.

		Path output = engine.getOutput();
		Path basePath = Paths.get("java", "com", "google", "gson", "internal",
				"alpha");

		RevisionRange r1 = engine.next().orElseThrow(AssertionError::new);
		assertThat(builder.update(r1)).isNotNull();
		assertThat(builder.getEnvironment().orElseThrow(AssertionError::new)
				.getCtModel().getAllPackages().stream()
				.filter(pkg -> pkg.getQualifiedName().endsWith(
						"com.google.gson.internal.alpha"))
				.count()).isEqualTo(1);

		RevisionRange r2 = mock(RevisionRange.class);
		Path p2 = basePath.resolve("Intercept.java");
		assertThat(output.resolve(p2).toFile().delete()).isTrue();
		when(r2.getRevision()).thenReturn(r1.getRevision());
		when(r2.getFileChanges()).thenReturn(singletonList(new RemoveMock(
				r1.getRevision(), p2.toString())));
		when(r2.getRemovedFiles()).thenCallRealMethod();
		assertThat(builder.update(r2)).isNotNull();
		assertThat(builder.getEnvironment().orElseThrow(AssertionError::new)
				.getCtModel().getAllPackages().stream()
				.filter(pkg -> pkg.getQualifiedName().endsWith(
						"com.google.gson.internal.alpha"))
				.count()).isEqualTo(1);

		RevisionRange r3 = mock(RevisionRange.class);
		Path p3 = basePath.resolve("JsonPostDeserializer.java");
		assertThat(output.resolve(p3).toFile().delete()).isTrue();
		when(r3.getRevision()).thenReturn(r1.getRevision());
		when(r3.getFileChanges()).thenReturn(singletonList(new RemoveMock(
				r1.getRevision(), p3.toString())));
		when(r3.getRemovedFiles()).thenCallRealMethod();
		assertThat(builder.update(r3)).isNotNull();
		assertThat(builder.getEnvironment().orElseThrow(AssertionError::new)
				.getCtModel().getAllPackages().stream()
				.filter(pkg -> pkg.getQualifiedName().endsWith(
						"com.google.gson.internal.alpha"))
				.count()).isEqualTo(1);

		RevisionRange r4 = mock(RevisionRange.class);
		Path p4 = basePath.resolve("package-info.java");
		assertThat(output.resolve(p4).toFile().delete()).isTrue();
		when(r4.getRevision()).thenReturn(r1.getRevision());
		when(r4.getFileChanges()).thenReturn(singletonList(new RemoveMock(
				r1.getRevision(), p4.toString())));
		when(r4.getRemovedFiles()).thenCallRealMethod();
		assertThat(builder.update(r4)).isNotNull();
		assertThat(builder.getEnvironment().orElseThrow(AssertionError::new)
				.getCtModel().getAllPackages().stream()
				.filter(pkg -> pkg.getQualifiedName().endsWith(
						"com.google.gson.internal.alpha"))
				.count()).isZero();
	}

	@AllArgsConstructor
	class RemoveMock implements FileChange {
		private final Revision revision;
		private final String relPath;

		@Override
		public Optional<VCSFile> getOldFile() {
			return Optional.of(new VCSFile() {
				@Override
				public String getRelativePath() {
					return relPath;
				}

				@Override
				public Revision getRevision() {
					return revision;
				}

				@Override
				public Optional<Charset> guessCharset() {
					return Optional.of(StandardCharsets.UTF_8);
				}

				@Override
				public VCSEngine getVCSEngine() {
					return revision.getVCSEngine();
				}
			});
		}

		@Override
		public Optional<VCSFile> getNewFile() {
			return Optional.empty();
		}

		@Override
		public VCSEngine getVCSEngine() {
			return revision.getVCSEngine();
		}
	}
}
