package de.unibremen.informatik.st.libvcs4j;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

/**
 * An implementation of {@link VCSEngine} that throws an
 * {@link UnsupportedOperationException} for all non-default methods.
 */
class TestVCSEngine implements VCSEngine {
	@Override
	public Optional<RevisionRange> next() throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public byte[] readAllBytes(final VCSFile pVCSFile)
				throws NullPointerException, IllegalArgumentException,
				IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Optional<Revision> getRevision() {
		return Optional.empty();
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
	public List<LineChange> computeDiff(FileChange fileChange) throws
			NullPointerException {
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
	public Iterator<RevisionRange> iterator() {
		throw new UnsupportedOperationException();
	}
}
