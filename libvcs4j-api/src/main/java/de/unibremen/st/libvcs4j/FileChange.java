package de.unibremen.st.libvcs4j;

import bmsi.util.Diff;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Represents a change of a single file.
 */
@SuppressWarnings("unused")
public interface FileChange {

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
	 * 		The old version of the file or an empty {@link Optional} if this
	 * 		{@link FileChange} is an addition ({@link Type#ADD}).
	 */
	Optional<VCSFile> getOldFile();

	/**
	 * Returns the file as it was like when its corresponding revision was
	 * checked out by {@link VCSEngine#next()}.
	 *
	 * @return
	 * 		The new version of the file or an empty {@link Optional} if this
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

		if (!old.isPresent() && !nev.isPresent()) {
			throw new IllegalStateException(
					"Neither the old nor the new file is available");
		}
		if (!old.isPresent()) {
			return Type.ADD;
		}
		//noinspection OptionalIsPresent
		if (!nev.isPresent()) {
			return Type.REMOVE;
		}

		return old.get().getPath().equals(nev.get().getPath()) ?
				Type.MODIFY : Type.RELOCATE;
	}

    /**
     * Computes the changed lines.
     *
     * @return
     *      A sequence of {@link LineChange} objects.
     * @throws IOException
     *      If an error occurred while reading the content of the old or new
     *      file (see {@link VCSFile#readeContent()}).
     */
	default List<LineChange> computeDiff() throws IOException {
		final String LINE_SEPARATOR = "\\r?\\n";
		final String[] old = getOldFile().isPresent()
				? getOldFile().get().readeContent().split(LINE_SEPARATOR)
				: new String[0];
		final String[] nev = getNewFile().isPresent()
				? getNewFile().get().readeContent().split(LINE_SEPARATOR)
				: new String[0];

		final Diff diff = new Diff(old, nev);
		Diff.change change = diff.diff_2(false);
		final List<LineChange> lineChanges = new ArrayList<>();
		while (change != null) {
			final Diff.change c = change;
			for (int i = 0; i < change.deleted; i++) {
				final int j = i;
				lineChanges.add(new LineChange() {
					@Override
					public Type getType() {
						return Type.DELETE;
					}

					@Override
					public int getLine() {
						return c.line0 + j + 1;
					}

					@Override
					public String getContent() {
						return old[getLine() - 1];
					}

					@Override
					public VCSFile getFile() {
						return getOldFile()
								.orElseThrow(IllegalStateException::new);
					}
				});
			}
			for (int i = 0; i < change.inserted; i++) {
				final int j = i;
				lineChanges.add(new LineChange() {
					@Override
					public Type getType() {
						return Type.INSERT;
					}

					@Override
					public int getLine() {
						return c.line1 + j + 1;
					}

					@Override
					public String getContent() {
						return nev[getLine() - 1];
					}

					@Override
					public VCSFile getFile() {
						return getNewFile()
								.orElseThrow(IllegalStateException::new);
					}
				});
			}
			change = change.link;
		}

		return lineChanges;
	}
}
