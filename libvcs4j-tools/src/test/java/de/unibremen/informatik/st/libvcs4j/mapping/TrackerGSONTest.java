package de.unibremen.informatik.st.libvcs4j.mapping;

import de.unibremen.informatik.st.libvcs4j.RevisionRange;
import de.unibremen.informatik.st.libvcs4j.VCSEngine;
import de.unibremen.informatik.st.libvcs4j.VCSEngineBuilder;
import de.unibremen.informatik.st.libvcs4j.VCSFile;
import de.unibremen.informatik.st.libvcs4j.testutils.ResourceExtractor;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;

public class TrackerGSONTest {

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();
	@Rule
	public TemporaryFolder trackerDir = new TemporaryFolder();
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
	public void noMoveInChangedFile() throws IOException {
		String suffix = "ProtoTypeAdapter.java";

		VCSEngine engine = VCSEngineBuilder
				.ofGit(repository.toString())
				.withTarget(target.toAbsolutePath())
				.withFrom("3bf1967c0a315d8a74a1975a9bbc6edbf1009549")
				.withTo("c744ccd51cdea2e92b3e06abc44336943281cddd")
				.build();

		Mapping<String> mapping = new Mapping<>();
		Tracker<String> tracker = new Tracker<>(
				Paths.get(trackerDir.getRoot().getAbsolutePath()),
				Objects::toString);

		RevisionRange fromRange = engine.next()
				.orElseThrow(AssertionError::new);
		List<VCSFile> fromFiles = fromRange.getRevision()
				.getFilesBySuffix(suffix);
		Mappable<String> fromMappable = new MappableMock(
				fromFiles.get(0), 1, 1, 200, 1);
		tracker.add(mapping.map(singletonList(fromMappable), fromRange));

		RevisionRange toRange = engine.next()
				.orElseThrow(AssertionError::new);
		List<VCSFile> toFiles = toRange.getRevision()
				.getFilesBySuffix(suffix);
		Mappable<String> toMappables = new MappableMock(
				toFiles.get(0), 1, 1, 200, 1);
		tracker.add(mapping.map(singletonList(toMappables), toRange));

		assertThat(tracker.getLifespans()).hasSize(1);
		assertThat(numEntries(tracker.getLifespans().get(0))).isEqualTo(2);
		assertThat(lastEntryChanged(tracker.getLifespans().get(0))).isFalse();
	}

	@Test
	public void noMoveInUnchangedFile() throws IOException {
		String suffix = "JsonPrimitive.java";

		VCSEngine engine = VCSEngineBuilder
				.ofGit(repository.toString())
				.withTarget(target.toAbsolutePath())
				.withFrom("3bf1967c0a315d8a74a1975a9bbc6edbf1009549")
				.withTo("c744ccd51cdea2e92b3e06abc44336943281cddd")
				.build();

		Mapping<String> mapping = new Mapping<>();
		Tracker<String> tracker = new Tracker<>(
				Paths.get(trackerDir.getRoot().getAbsolutePath()),
				Objects::toString);

		RevisionRange fromRange = engine.next()
				.orElseThrow(AssertionError::new);
		List<VCSFile> fromFiles = fromRange.getRevision()
				.getFilesBySuffix(suffix);
		Mappable<String> fromMappable = new MappableMock(
				fromFiles.get(0), 64, 1, 66, 1);
		tracker.add(mapping.map(singletonList(fromMappable), fromRange));

		RevisionRange toRange = engine.next()
				.orElseThrow(AssertionError::new);
		List<VCSFile> toFiles = toRange.getRevision()
				.getFilesBySuffix(suffix);
		Mappable<String> toMappables = new MappableMock(
				toFiles.get(0), 64, 1, 66, 1);
		tracker.add(mapping.map(singletonList(toMappables), toRange));

		assertThat(tracker.getLifespans()).hasSize(1);
		assertThat(numEntries(tracker.getLifespans().get(0))).isEqualTo(2);
		assertThat(lastEntryChanged(tracker.getLifespans().get(0))).isFalse();
	}

	@Test
	public void moveWithoutChange() throws IOException {
		String suffix = "ProtoTypeAdapter.java";

		VCSEngine engine = VCSEngineBuilder
				.ofGit(repository.toString())
				.withTarget(target.toAbsolutePath())
				.withFrom("3bf1967c0a315d8a74a1975a9bbc6edbf1009549")
				.withTo("c744ccd51cdea2e92b3e06abc44336943281cddd")
				.build();

		Mapping<String> mapping = new Mapping<>();
		Tracker<String> tracker = new Tracker<>(
				Paths.get(trackerDir.getRoot().getAbsolutePath()),
				Objects::toString);

		RevisionRange fromRange = engine.next()
				.orElseThrow(AssertionError::new);
		List<VCSFile> fromFiles = fromRange.getRevision()
				.getFilesBySuffix(suffix);
		Mappable<String> fromMappable = new MappableMock(
				fromFiles.get(0), 1, 1, 200, 1);
		tracker.add(mapping.map(singletonList(fromMappable), fromRange));

		RevisionRange toRange = engine.next()
				.orElseThrow(AssertionError::new);
		List<VCSFile> toFiles = toRange.getRevision()
				.getFilesBySuffix(suffix);
		Mappable<String> toMappables = new MappableMock(
				toFiles.get(0), 3, 1, 200, 1);
		tracker.add(mapping.map(singletonList(toMappables), toRange));

		assertThat(tracker.getLifespans()).hasSize(2);
		assertThat(numEntries(tracker.getLifespans().get(0))).isEqualTo(1);
		assertThat(numEntries(tracker.getLifespans().get(1))).isEqualTo(1);
	}

	@Test
	public void moveWithChange() throws IOException {
		String suffix = "ProtoTypeAdapter.java";

		VCSEngine engine = VCSEngineBuilder
				.ofGit(repository.toString())
				.withTarget(target.toAbsolutePath())
				.withFrom("3bf1967c0a315d8a74a1975a9bbc6edbf1009549")
				.withTo("c744ccd51cdea2e92b3e06abc44336943281cddd")
				.build();

		Mapping<String> mapping = new Mapping<>();
		Tracker<String> tracker = new Tracker<>(
				Paths.get(trackerDir.getRoot().getAbsolutePath()),
				Objects::toString);

		RevisionRange fromRange = engine.next()
				.orElseThrow(AssertionError::new);
		List<VCSFile> fromFiles = fromRange.getRevision()
				.getFilesBySuffix(suffix);
		Mappable<String> fromMappable = new MappableMock(
				fromFiles.get(0), 250, 1, 325, 1);
		tracker.add(mapping.map(singletonList(fromMappable), fromRange));

		RevisionRange toRange = engine.next()
				.orElseThrow(AssertionError::new);
		List<VCSFile> toFiles = toRange.getRevision()
				.getFilesBySuffix(suffix);
		Mappable<String> toMappables = new MappableMock(
				toFiles.get(0), 250, 1, 327, 1);
		tracker.add(mapping.map(singletonList(toMappables), toRange));

		assertThat(tracker.getLifespans()).hasSize(1);
		assertThat(numEntries(tracker.getLifespans().get(0))).isEqualTo(2);
		assertThat(lastEntryChanged(tracker.getLifespans().get(0))).isTrue();
	}

	private int numEntries(final Lifespan lifespan) throws IOException {
		return Files.readAllLines(lifespan.getCsv(), Lifespan.CHARSET)
				.size() - 1;
	}

	private boolean lastEntryChanged(final Lifespan lifespan)
			throws IOException {
		List<String> lines = Files.readAllLines(
				lifespan.getCsv(), Lifespan.CHARSET);
		return lines.get(lines.size() - 1).split(Lifespan.DELIMITER)[2]
				.equals("\"1\"");
	}

	private class MappableMock implements Mappable<String> {
		private final VCSFile.Range range;

		MappableMock(VCSFile file, int beginLine, int beginColumn,
				int endLine, int endColumn) throws IOException {
			range = new VCSFile.Range(
					file.positionOf(beginLine, beginColumn, 4)
							.orElseThrow(IllegalArgumentException::new),
					file.positionOf(endLine, endColumn, 4)
							.orElseThrow(IllegalArgumentException::new));
		}

		@Override
		public List<VCSFile.Range> getRanges() {
			return singletonList(range);
		}

		@Override
		public Optional<String> getMetadata() {
			return Optional.of("mock");
		}
	}
}
