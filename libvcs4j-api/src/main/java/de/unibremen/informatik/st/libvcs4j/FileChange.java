package de.unibremen.informatik.st.libvcs4j;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Represents a change of a file.
 */
public interface FileChange extends VCSModelElement {

	/**
	 * The type of a {@link FileChange}.
	 */
	enum Type {
		/**
		 * The {@link FileChange} is an addition.
		 */
		ADD,

		/**
		 * The {@link FileChange} is a removal.
		 */
		REMOVE,

		/**
		 * The {@link FileChange} is a modify.
		 */
		MODIFY,

		/**
		 * The {@link FileChange} is a relocation.
		 */
		RELOCATE
	}

	/**
	 * Returns the file as it was like when its corresponding revision was
	 * checked out by {@link VCSEngine#next()}.
	 *
	 * @return
	 * 		The old state of the file or an empty {@link Optional} if this
	 * 		{@link FileChange} is an addition ({@link Type#ADD}).
	 */
	Optional<VCSFile> getOldFile();

	/**
	 * Returns the file as it was like when its corresponding revision was
	 * checked out by {@link VCSEngine#next()}.
	 *
	 * @return
	 * 		The new state of the file or an empty {@link Optional} if this
	 * 		{@link FileChange} is a removal ({@link Type#REMOVE}).
	 */
	Optional<VCSFile> getNewFile();

	/**
	 * Returns the {@link Type} of this file change. The default implementation
	 * computes the type based on the presence/absence of {@link #getOldFile()}
	 * and {@link #getNewFile()}.
	 *
	 * @return
	 * 		The {@link Type} of this file change.
	 */
	default Type getType() {
		final Optional<VCSFile> old = getOldFile();
		final Optional<VCSFile> nev = getNewFile();

		Validate.validateState(old.isPresent() || nev.isPresent(),
				"Neither the old nor the new file is available");

		if (!old.isPresent()) {
			return Type.ADD;
		}
		if (!nev.isPresent()) {
			return Type.REMOVE;
		}
		return old.get().getRelativePath()
					.equals(nev.get().getRelativePath())
				? Type.MODIFY : Type.RELOCATE;
	}

	/**
	 * @see VCSEngine#computeDiff(FileChange)
	 *
	 * @return
	 * 		A sequence of {@link LineChange} objects.
	 * @throws BinaryFileException
	 * 		If the old or the new file is binary (see
	 * 		{@link VCSFile#isBinary()}).
	 * @throws IOException
	 *      If an error occurred while reading the content of the old or new
	 *      file (see {@link VCSFile#readContent()}).
	 */
	default List<LineChange> computeDiff() throws IOException {
		final Optional<VCSFile> old = getOldFile();
		if (old.isPresent() && old.get().isBinary()) {
			throw new BinaryFileException(String.format(
					"Old file (%s) is binary", old.get().getPath()));
		}
		final Optional<VCSFile> nev = getNewFile();
		if (nev.isPresent() && nev.get().isBinary()) {
			throw new BinaryFileException(String.format(
					"New file (%s) is binary", nev.get().getPath()));
		}
		return getVCSEngine().computeDiff(this);
	}

	/**
	 * Returns the delta of the changed lines. A positive value indicates that
	 * more lines have been inserted than deleted, whereas a negative value
	 * indicates that more lines have been deleted than inserted. 0 indicates
	 * that an equal amount of lines have been inserted and deleted.
	 *
	 * @return
	 * 		The delta of the changed lines.
	 * @throws BinaryFileException
	 * 		If the old or the new file is binary (see
	 * 		{@link VCSFile#isBinary()}).
	 * @throws IOException
	 * 		If an error occurred while computing the diff (see
	 * 		{@link #computeDiff()}).
	 */
	default int computeLineDelta() throws IOException {
		final List<LineChange> changes = computeDiff();
		int delta = 0;
		for (final LineChange c : changes) {
			delta = c.getType() == LineChange.Type.INSERT
					? delta + 1 : delta - 1;
		}
		return delta;
	}
}
