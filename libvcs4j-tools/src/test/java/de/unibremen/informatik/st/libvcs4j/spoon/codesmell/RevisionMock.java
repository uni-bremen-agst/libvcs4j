package de.unibremen.informatik.st.libvcs4j.spoon.codesmell;

import de.unibremen.informatik.st.libvcs4j.FileChange;
import de.unibremen.informatik.st.libvcs4j.ITEngine;
import de.unibremen.informatik.st.libvcs4j.LineChange;
import de.unibremen.informatik.st.libvcs4j.LineInfo;
import de.unibremen.informatik.st.libvcs4j.Revision;
import de.unibremen.informatik.st.libvcs4j.RevisionRange;
import de.unibremen.informatik.st.libvcs4j.VCSEngine;
import de.unibremen.informatik.st.libvcs4j.VCSFile;
import de.unibremen.informatik.st.libvcs4j.VCSModelFactory;
import de.unibremen.informatik.st.libvcs4j.Validate;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Mocks a {@link Revision} for the use of testing code smell detectors.
 */
@RequiredArgsConstructor
public class RevisionMock implements Revision {

	@NonNull
	private final TemporaryFolder folder;

	@Getter
	private final List<VCSFile> files = new ArrayList<>();

	@Override
	public String getId() {
		return "1";
	}

	@Override
	public Path getOutput() {
		return folder.getRoot().toPath();
	}

	@Override
	public VCSEngine getVCSEngine() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Adds the file located at {@code path} (using
	 * {@link Class#getResourceAsStream(String)}).
	 *
	 * Example usage: {@code addFile(Paths.get("dir", "File.java"))}
	 *
	 * @param path
	 * 		Path to the file that is to be loaded from resources and added to
	 * 		this revision.
	 * @throws IOException
	 * 		If an error occurred while copying the file to {@link #folder}.
	 */
	public void addFile(@NonNull final Path path) throws IOException {
		files.add(new VCSFileMock(path));
	}

	/**
	 * Mocks a {@link VCSEngine} for the use of testing code smell detectors.
	 * The only operation supported is {@link #getModelFactory()}.
	 */
	public static class VCSEngineMock implements VCSEngine {
		private final VCSModelFactory factory = new VCSModelFactory() {};

		@Override
		public Optional<RevisionRange> next() {
			throw new UnsupportedOperationException();
		}

		@Override
		public byte[] readAllBytes(VCSFile file) {
			throw new UnsupportedOperationException();
		}

		@Override
		public List<LineInfo> readLineInfo(VCSFile file) {
			throw new UnsupportedOperationException();
		}

		@Override
		public Optional<Charset> guessCharset(VCSFile file) {
			throw new UnsupportedOperationException();
		}

		@Override
		public Optional<Revision> getRevision() {
			throw new UnsupportedOperationException();
		}

		@Override
		public String getRepository() {
			throw new UnsupportedOperationException();
		}

		@Override
		public String getRoot() {
			throw new UnsupportedOperationException();
		}

		@Override
		public Path getTarget() {
			throw new UnsupportedOperationException();
		}

		@Override
		public Path getOutput() {
			throw new UnsupportedOperationException();
		}

		@Override
		public List<LineChange> computeDiff(FileChange fileChange) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void setITEngine(ITEngine itEngine) {
			throw new UnsupportedOperationException();
		}

		@Override
		public Optional<ITEngine> getITEngine() {
			throw new UnsupportedOperationException();
		}

		@Override
		public VCSModelFactory getModelFactory() {
			return factory;
		}

		@Override
		public void setModelFactory(VCSModelFactory factory) {
			throw new UnsupportedOperationException();
		}

		@Override
		public Iterator<RevisionRange> iterator() {
			throw new UnsupportedOperationException();
		}
	}

	/**
	 * Mocks a {@link VCSFile} for the use of testing code smell detectors.
	 */
	public class VCSFileMock implements VCSFile {

		private final File file;
		private final Path relativePath;
		private final VCSEngine engine;

		public VCSFileMock(@NonNull final Path relPath) throws IOException {
			final Path path = folder.getRoot().toPath().resolve(relPath);
			Validate.isFalse(path.equals(relPath),
					"'%s' is not a relative path", relPath);
			Files.createDirectories(path.getParent());
			file = path.toFile();
			try (FileWriter fw = new FileWriter(file)) {
				IOUtils.copy(getClass().getResourceAsStream(
						"/" + relPath.toString()), fw, UTF_8);
			}
			relativePath = relPath;
			engine = new VCSEngineMock();
		}

		@Override
		public String getRelativePath() {
			return relativePath.toString();
		}

		@Override
		public Revision getRevision() {
			return RevisionMock.this;
		}

		@Override
		public byte[] readAllBytes() throws IOException {
			return Files.readAllBytes(file.toPath());
		}

		@Override
		public Optional<Charset> guessCharset() {
			return Optional.of(UTF_8);
		}

		@Override
		public VCSEngine getVCSEngine() {
			return engine;
		}
	}
}
