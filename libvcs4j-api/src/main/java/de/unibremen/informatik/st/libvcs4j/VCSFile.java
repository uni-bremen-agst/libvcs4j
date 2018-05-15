package de.unibremen.informatik.st.libvcs4j;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Represents a file in a VCS at a certain point in time. For the sake of
 * convenience, we use the name "VCSFile" instead of "File" to avoid naming
 * collisions with {@link File}.
 */
@SuppressWarnings("unused")
public interface VCSFile extends VCSModelElement {

	class Position {

		private final int line;
		private final int column;
		private final int tabSize;

		public Position(final int line, final int column, final int tabSize) {
			this.line = line;
			this.column = column;
			this.tabSize = tabSize;
		}

		public int getLine() {
			return line;
		}

		public int getColumn() {
			return column;
		}

		public int getTabSize() {
			return tabSize;
		}
	}

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
	 * Returns the absolute path of this file as it was like when its
	 * corresponding revision was checked out by {@link VCSEngine#next()}.
	 *
	 * @return
	 * 		The absolute path of this file.
	 */
	default String getPath() {
		return getVCSEngine().getOutput()
				.resolve(getRelativePath()).toString();
	}

	/**
	 * Returns the contents of this file.
	 *
	 * @return
	 * 		The contents of this file.
	 * @throws IOException
	 * 		If an error occurred while reading the contents.
	 */
	default byte[] readAllBytes() throws IOException {
		return getVCSEngine().readAllBytes(this);
	}

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

	default List<String> readLinesWithEOL() throws IOException {
		final StringReader reader = new StringReader(readeContent());
		final List<String> lines = new ArrayList<>();
		final StringBuilder builder = new StringBuilder();
		int code;
		while ((code = reader.read()) != -1) {
			char ch = (char) code;
			builder.append(ch);

			if (ch == '\n') { // Unix EOL
				lines.add(builder.toString());
				builder.setLength(0);
			} else if ( ch == '\r') {
				reader.mark(1);
				code = reader.read();
				ch = (char) code;

				if (ch == '\n') { // Windows EOL
					builder.append(ch);
				} else if (code == -1) { // old Mac EOL followed by EOF
					break;
				} else { // old Mac EOL followed by regular char
					reader.reset();
				}
				lines.add(builder.toString());
				builder.setLength(0);
			}
		}
		if (builder.length() > 0) { // skip empty lines
			lines.add(builder.toString());
		}
		return lines;
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

	default Position getPosition(final int offset, final int tabSize)
			throws IOException {
		if (offset < 0) {
			throw new IllegalArgumentException("offset < 0");
		} else if (tabSize < 1) {
			throw new IllegalArgumentException("tab size < 1");
		}

		final List<String> lines = readLinesWithEOL();

		int line = 1;
		int offs = offset;
		while (line <= lines.size()) {
			final String lineStr = lines.get(line - 1);
			// find number of tabs in line
			int tabs = 0;
			for (int i = 0; i < lineStr.length(); i++) {
				if (lineStr.charAt(i) == '\t') {
					tabs++;
				}
			}
			// calculate line length based on the number of tabs
			final int length = lineStr.length() + (tabs * (tabSize - 1));
			if (length <= offs) {
				line++;
				offs -= length;
			} else {
				final String offsStr = lineStr.substring(0, offs + 1);
				// find number of tabs in sub-line
				tabs = 0;
				for (int i = 0; i < offsStr.length(); i++) {
					if (lineStr.charAt(i) == '\t') {
						tabs++;
					}
				}
				// calculate column based on the number of tabs
				final int column = offsStr.length() + (tabs * (tabSize - 1));
				return new Position(line, column, tabSize);
			}
		}
		throw new IndexOutOfBoundsException("offset: " + offset);
	}
}
