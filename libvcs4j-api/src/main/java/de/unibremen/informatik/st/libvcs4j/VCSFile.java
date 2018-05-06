package de.unibremen.informatik.st.libvcs4j;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

/**
 * Represents a file in a VCS at a certain point in time. For the sake of
 * convenience, we use the name "VCSFile" instead of "File" to avoid naming
 * collisions with {@link File}.
 */
@SuppressWarnings("unused")
public interface VCSFile {

	/**
	 * Returns the absolute path of this file as it was like when its
	 * corresponding revision was checked out by {@link VCSEngine#next()}.
	 *
	 * @return
	 * 		The absolute path of this file.
	 */
	String getPath();

	/**
	 * Returns the relative path of this file as it was like when its
	 * corresponding revision was checked out by {@link VCSEngine#next()}.
	 *
	 * The path is relative to {@link VCSEngine#getOutput()}.
	 *
	 * @return
	 * 		The relative path of this file.
	 */
	String getRelativePath();

	/**
	 * Returns the {@link Revision} of this file.
	 *
	 * @return
	 * 		The {@link Revision} of this file.
	 */
	Revision getRevision();

	/**
	 * Tries to guess the charset of this file.
	 *
	 * @return
	 * 		The guessed charset.
	 * @throws IOException
	 * 		If an error occurred while reading the contents of this file.
	 */
	Optional<Charset> guessCharset() throws IOException;

	/**
	 * Returns the contents of this file.
	 *
	 * @return
	 * 		The contents of this file.
	 * @throws IOException
	 * 		If an error occurred while reading the contents.
	 */
	byte[] readAllBytes() throws IOException;

	/**
	 * Returns the content of this file as String. The default implementation
	 * uses {@link #readAllBytes()} and {@link #guessCharset()} to create an
	 * appropriate String. If {@link #guessCharset()} returns an empty optional
	 * the system default charset is used as fallback.
	 *
	 * @return
	 * 		The content of this file as String.
	 * @throws IOException
	 * 		If an error occurred while reading the file content.
	 */
	default String readeContent() throws IOException {
		final Charset charset = guessCharset()
				.orElse(Charset.defaultCharset());
		return new String(readAllBytes(), charset);
	}

	/**
	 * Returns a {@link File} object (absolute path) representing this file.
	 *
	 * @return
	 * 		A {@link File} object (absolute path) representing this file.
	 */
	default File toFile() {
		return new File(getPath());
	}

	/**
	 * Returns a {@link File} object (relative path) representing this file.
	 *
	 * @return
	 * 		A {@link File} object (relative path) representing this file.
	 */
	default File toRelativeFile() {
		return new File(getRelativePath());
	}

	/**
	 * Returns the absolute path of this file as {@link Path}.
	 *
	 * @return
	 * 		The absolute path of this file as {@link Path}.
	 */
	default Path toPath() {
		return Paths.get(getPath());
	}

	/**
	 * Returns the relative path of this file as {@link Path}.
	 *
	 * @return
	 * 		The relative path of this file as {@link Path}.
	 */
	default Path toRelativePath() {
		return Paths.get(getRelativePath());
	}
}
