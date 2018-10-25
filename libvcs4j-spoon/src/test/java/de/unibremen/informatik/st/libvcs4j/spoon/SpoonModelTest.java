package de.unibremen.informatik.st.libvcs4j.spoon;

import de.unibremen.informatik.st.libvcs4j.*;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import spoon.reflect.CtModel;
import spoon.reflect.code.CtAssignment;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.support.SerializationModelStreamer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SpoonModelTest {

	private static final String ORIGINAL_FILES =
			"/incremental/original-files/";

	private static final String CHANGED_FILES =
			"/incremental/changed-files/";

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	/**
	 * The first range
	 */
	private RevisionRange firstRange;

	/**
	 * The test subject.
	 */
	private SpoonModel spoonModel;

	@Before
	public void init() throws IOException {
		// Copy all files from {@link #ORIGINAL_FILES} to {@link #folder}.
		Files.write(folder.newFile("A.java").toPath(),
				IOUtils.toByteArray(getClass().getResourceAsStream(
						ORIGINAL_FILES + "A.java")));
		Files.write(folder.newFile("B.java").toPath(),
				IOUtils.toByteArray(getClass().getResourceAsStream(
						ORIGINAL_FILES + "B.java")));
		Files.write(folder.newFile("C.java").toPath(),
				IOUtils.toByteArray(getClass().getResourceAsStream(
						ORIGINAL_FILES + "C.java")));
		Files.write(folder.newFile("D.java").toPath(),
				IOUtils.toByteArray(getClass().getResourceAsStream(
						ORIGINAL_FILES + "D.java")));

		// Mock firstRange.
		Revision revision = mock(Revision.class);
		when(revision.getOutput()).thenReturn(folder.getRoot().toPath());
		firstRange = mock(RevisionRange.class);
		when(firstRange.getRevision()).thenReturn(revision);

		// Setup spoonModel.
		spoonModel = new SpoonModel();
	}

	@Test
	public void updateD() throws IOException {
		byte[] original = save(spoonModel.update(firstRange)
				.orElseThrow(IllegalStateException::new));

		updateFile("D.java");
		VCSFile dFile = new VCSFileMock("D.java");
		FileChange dChange = new FileChangeMock(dFile, dFile);
		RevisionRange second = new RevisionRangeMock(Arrays.asList(dChange));
		byte[] update = save(spoonModel.update(second)
				.orElseThrow(IllegalStateException::new));

		CtModel originalModel = load(original);
		CtModel updatedModel = load(update);
		assertThat(originalModel).isNotEqualTo(updatedModel);

		Collection<CtType<?>> originalTypes = originalModel.getAllTypes();
		Collection<CtType<?>> updatedTypes = updatedModel.getAllTypes();
		assertThat(originalTypes).hasSize(4);
		assertThat(updatedTypes).hasSize(4);
		assertThat(originalTypes).isNotEqualTo(updatedTypes);

		CtType<?> a1 = getTypeByName(originalTypes, "A");
		CtType<?> b1 = getTypeByName(originalTypes, "B");
		CtType<?> c1 = getTypeByName(originalTypes, "C");
		CtType<?> d1 = getTypeByName(originalTypes, "D");
		CtType<?> a2 = getTypeByName(updatedTypes, "A");
		CtType<?> b2 = getTypeByName(updatedTypes, "B");
		CtType<?> c2 = getTypeByName(updatedTypes, "C");
		CtType<?> d2 = getTypeByName(updatedTypes, "D");
		assertThat(a1).isEqualTo(a2);
		assertThat(b1).isEqualTo(b2);
		assertThat(c1).isEqualTo(c2);
		assertThat(d1).isNotEqualTo(d2);
		assertThat(d1.getDeclaredFields()).isEmpty();
		assertThat(d2.getDeclaredFields()).hasSize(2);
		assertThat(d1.getMethods()).isEmpty();
		assertThat(d2.getMethods()).hasSize(1);
	}

	@Test
	public void removeCAndAddD() throws IOException {
		deleteFile("D.java");
		byte[] original = save(spoonModel.update(firstRange)
				.orElseThrow(IllegalStateException::new));

		deleteFile("C.java");
		addFile("D.java");
		VCSFile cFile = new VCSFileMock("C.java");
		FileChange cChange = new FileChangeMock(cFile, null);
		VCSFile dFile = new VCSFileMock("D.java");
		FileChange dChange = new FileChangeMock(null, dFile);
		RevisionRange second = new RevisionRangeMock(
				Arrays.asList(cChange, dChange));
		byte[] update = save(spoonModel.update(second)
				.orElseThrow(IllegalStateException::new));

		CtModel originalModel = load(original);
		CtModel updatedModel = load(update);
		assertThat(originalModel).isNotEqualTo(updatedModel);

		Collection<CtType<?>> originalTypes = originalModel.getAllTypes();
		Collection<CtType<?>> updatedTypes = updatedModel.getAllTypes();
		assertThat(originalTypes).hasSize(3);
		assertThat(updatedTypes).hasSize(3);
		assertThat(originalTypes).isNotEqualTo(updatedTypes);

		CtType<?> a1 = getTypeByName(originalTypes, "A");
		CtType<?> b1 = getTypeByName(originalTypes, "B");
		CtType<?> c1 = getTypeByName(originalTypes, "C");
		CtType<?> a2 = getTypeByName(updatedTypes, "A");
		CtType<?> b2 = getTypeByName(updatedTypes, "B");
		CtType<?> d2 = getTypeByName(updatedTypes, "D");
		assertThat(a1).isEqualTo(a2);
		assertThat(b1).isEqualTo(b2);
		assertThat(c1).isNotEqualTo(d2);
		assertThat(d2.getDeclaredFields()).isEmpty();
		assertThat(d2.getMethods()).isEmpty();
	}

	@Test
	@Ignore
	public void updateTypeOfC() throws IOException {
		deleteFile("D.java");
		byte[] original = save(spoonModel.update(firstRange)
				.orElseThrow(IllegalStateException::new));

		updateFile("C.java");
		VCSFile cFile = new VCSFileMock("C.java");
		FileChange cChange = new FileChangeMock(cFile, cFile);
		RevisionRange second = new RevisionRangeMock(Arrays.asList(cChange));
		byte[] update = save(spoonModel.update(second)
				.orElseThrow(IllegalStateException::new));

		CtModel originalModel = load(original);
		CtModel updatedModel = load(update);
		assertThat(originalModel).isNotEqualTo(updatedModel);

		Collection<CtType<?>> originalTypes = originalModel.getAllTypes();
		Collection<CtType<?>> updatedTypes = updatedModel.getAllTypes();
		assertThat(originalTypes).hasSize(3);
		assertThat(updatedTypes).hasSize(3);
		assertThat(originalTypes).isNotEqualTo(updatedTypes);

		CtType<?> c1 = getTypeByName(originalModel.getAllTypes(), "C");
		assertThat(c1.getField("val").getType()
				.getSimpleName()).isEqualTo("int");
		CtType<?> b1 = getTypeByName(originalModel.getAllTypes(), "B");
		CtMethod<?> method1 = b1.getMethodsByName("func").get(0);
		CtStatement stmt1 = method1.getBody().getStatement(0);
		CtAssignment<?, ?> assignment1 = (CtAssignment<?, ?>) stmt1;
		CtExpression<?> lhs1 = assignment1.getAssigned();
		assertThat(assignment1.getType().getSimpleName()).isEqualTo("int");
		assertThat(lhs1.getType().getSimpleName()).isEqualTo("int");

		CtType<?> c2 = getTypeByName(updatedModel.getAllTypes(), "C");
		assertThat(c2.getField("val").getType()
				.getSimpleName()).isEqualTo("float");
		CtType<?> b2 = getTypeByName(updatedModel.getAllTypes(), "B");
		CtMethod<?> method2 = b2.getMethodsByName("func").get(0);
		CtStatement stmt2 = method2.getBody().getStatement(0);
		CtAssignment<?, ?> assignment2 = (CtAssignment<?, ?>) stmt2;
		CtExpression<?> lhs2 = assignment2.getAssigned();
		assertThat(assignment2.getType().getSimpleName()).isEqualTo("float");
		assertThat(lhs2.getType().getSimpleName()).isEqualTo("float");
	}

	@Test
	@Ignore
	public void relocateCToD() throws IOException {
		deleteFile("D.java");
		byte[] original = save(spoonModel.update(firstRange)
				.orElseThrow(IllegalStateException::new));

		deleteFile("C.java");
		addFile("D.java");
		updateFile("D.java");
		VCSFile cFile = new VCSFileMock("C.java");
		VCSFile dFile = new VCSFileMock("D.java");
		FileChange change = new FileChangeMock(cFile, dFile);
		RevisionRange second = new RevisionRangeMock(Arrays.asList(change));
		byte[] update = save(spoonModel.update(second)
				.orElseThrow(IllegalStateException::new));

		CtModel originalModel = load(original);
		CtModel updatedModel = load(update);
		assertThat(originalModel).isNotEqualTo(updatedModel);

		Collection<CtType<?>> originalTypes = originalModel.getAllTypes();
		Collection<CtType<?>> updatedTypes = updatedModel.getAllTypes();
		assertThat(originalTypes).hasSize(3);
		assertThat(updatedTypes).hasSize(3);
		assertThat(originalTypes).isNotEqualTo(updatedTypes);

		CtType<?> a1 = getTypeByName(originalTypes, "A");
		CtType<?> b1 = getTypeByName(originalTypes, "B");
		CtType<?> c1 = getTypeByName(originalTypes, "C");
		CtType<?> a2 = getTypeByName(updatedTypes, "A");
		CtType<?> b2 = getTypeByName(updatedTypes, "B");
		CtType<?> d2 = getTypeByName(updatedTypes, "D");
		assertThat(a1).isEqualTo(a2);
		assertThat(b1).isEqualTo(b2);
		assertThat(c1).isEqualTo(d2);
		assertThat(d2.getDeclaredFields()).hasSize(2);
		assertThat(d2.getMethods()).hasSize(1);

		assertThat(b1.getMethods().iterator().next().getParameters()
				.get(0).getType().getDeclaration()).isNull();
	}

	@Test
	@Ignore
	public void canonicalPathD() throws IOException {
		deleteFile("D.java");
		byte[] original = save(spoonModel.update(firstRange)
				.orElseThrow(IllegalStateException::new));

		addFile("D.java");
		String folderName = folder.getRoot().toPath().getName(
				folder.getRoot().toPath().getNameCount() - 1).toString();
		String dPath = Paths.get("..", folderName, "D.java").toString();
		VCSFile dFile = new VCSFileMock(dPath);
		FileChange dChange = new FileChangeMock(null, dFile);
		RevisionRange second = new RevisionRangeMock(Arrays.asList(dChange));
		byte[] update = save(spoonModel.update(second)
				.orElseThrow(IllegalStateException::new));

		CtModel originalModel = load(original);
		CtModel updatedModel = load(update);
		assertThat(originalModel).isNotEqualTo(updatedModel);

		Collection<CtType<?>> originalTypes = originalModel.getAllTypes();
		Collection<CtType<?>> updatedTypes = updatedModel.getAllTypes();
		assertThat(originalTypes).hasSize(3);
		assertThat(updatedTypes).hasSize(4);
		assertThat(originalTypes).isNotEqualTo(updatedTypes);

		CtType<?> d2 = getTypeByName(updatedTypes, "D");
		assertThat(d2.getDeclaredFields()).isEmpty();
		assertThat(d2.getMethods()).isEmpty();
	}


	///////////////////////////////////////////////////////////////////////////

	private void addFile(String file) throws IOException {
		Files.write(folder.newFile(file).toPath(),
				IOUtils.toByteArray(getClass().getResourceAsStream(
						ORIGINAL_FILES + file)));
	}

	private void updateFile(String file) throws IOException {
		Files.write(folder.getRoot().toPath().resolve(file),
				IOUtils.toByteArray(getClass().getResourceAsStream(
						CHANGED_FILES + file)));
	}

	private void deleteFile(String file) {
		assertThat(folder.getRoot().toPath()
				.resolve(file).toFile().delete()).isTrue();
	}

	private byte[] save(CtModel model) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		SerializationModelStreamer streamer = new SerializationModelStreamer();
		streamer.save(model.getRootPackage().getFactory(), baos);
		return baos.toByteArray();
	}

	private CtModel load(byte[] bytes) throws IOException {
		SerializationModelStreamer streamer = new SerializationModelStreamer();
		return streamer.load(new ByteArrayInputStream(bytes)).getModel();
	}

	private CtType<?> getTypeByName(Collection<CtType<?>> types, String name) {
		return types.stream().filter(t -> t.getSimpleName().equals(name))
				.findFirst().orElseThrow(AssertionError::new);
	}

	private class VCSFileMock implements VCSFile {

		private final String file;

		public VCSFileMock(String file) {
			this.file = file;
		}

		@Override
		public String getRelativePath() {
			return file;
		}

		@Override
		public String getPath() {
			return folder.getRoot().toPath().resolve(file).toString();
		}

		@Override
		public Optional<Charset> guessCharset() throws IOException {
			return Optional.empty();
		}

		@Override
		public Revision getRevision() {
			throw new UnsupportedOperationException();
		}

		@Override
		public VCSEngine getVCSEngine() {
			throw new UnsupportedOperationException();
		}
	}

	private class FileChangeMock implements FileChange {

		private final VCSFile oldFile;
		private final VCSFile newFile;

		public FileChangeMock(VCSFile oldFile, VCSFile newFile) {
			this.oldFile = oldFile;
			this.newFile = newFile;
		}

		@Override
		public Optional<VCSFile> getOldFile() {
			return Optional.ofNullable(oldFile);
		}

		@Override
		public Optional<VCSFile> getNewFile() {
			return Optional.ofNullable(newFile);
		}

		@Override
		public VCSEngine getVCSEngine() {
			throw new UnsupportedOperationException();
		}
	}

	private class RevisionRangeMock implements RevisionRange {

		private final List<FileChange> fileChanges;

		public RevisionRangeMock(List<FileChange> fileChanges) {
			this.fileChanges = fileChanges;
		}

		@Override
		public List<FileChange> getFileChanges() {
			return fileChanges;
		}

		@Override
		public int getOrdinal() {
			throw new UnsupportedOperationException();
		}

		@Override
		public Revision getRevision() {
			throw new UnsupportedOperationException();
		}

		@Override
		public Optional<Revision> getPredecessorRevision() {
			throw new UnsupportedOperationException();
		}

		@Override
		public List<Commit> getCommits() {
			throw new UnsupportedOperationException();
		}

		@Override
		public VCSEngine getVCSEngine() {
			throw new UnsupportedOperationException();
		}
	}
}
