package de.unibremen.informatik.st.libvcs4j;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Scanner;

/**
 * Represents a file in a VCS at a certain point in time. For the sake of
 * convenience, we use the name "VCSFile" instead of "File" to avoid naming
 * collisions with {@link File}.
 */
public interface VCSFile extends VCSModelElement {

	/**
	 * Represents a position within a file.
	 */
	class Position {

		/**
		 * The line of a position {@code >= 1}.
		 */
		private final int line;

		/**
		 * The column of a position {@code >= 1}.
		 */
		private final int column;

		/**
		 * The number of characters acquired by a tab (\t) {@code >= 1}.
		 */
		private final int tabSize;

		/**
		 * Creates a new size with given line, column, and tab size.
		 *
		 * @param line
		 * 		The line of the position to create.
		 * @param column
		 * 		The column of the position to create.
		 * @param tabSize
		 * 		The tab size of the position to create.
		 * @throws IllegalArgumentException
		 * 		If any of the given arguments is less than 1.
		 */
		public Position(final int line, final int column, final int tabSize) {
			if (line < 1) {
				throw new IllegalArgumentException("line < 1");
			} else if (column < 1) {
				throw new IllegalArgumentException("column < 1");
			} else if (tabSize < 1) {
				throw new IllegalArgumentException("tab size < 1");
			}
			this.line = line;
			this.column = column;
			this.tabSize = tabSize;
		}

		/**
		 * Returns the line of this position {@code >= 1}.
		 *
		 * @return
		 * 		The line of this position {@code >= 1}.
		 */
		public int getLine() {
			return line;
		}

		/**
		 * Returns the column of this position {@code >= 1}.
		 *
		 * @return
		 * 		The column of this position {@code >= 1}.
		 */
		public int getColumn() {
			return column;
		}

		/**
		 * Returns the tab size of this position {@code >= 1}.
		 *
		 * @return
		 * 		The tab size of this position {@code >= 1}.
		 */
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

	/**
	 * Returns the content of this file as a list of strings including EOL
	 * characters. The following EOLs are supported: '\n', '\r\n', '\r'.
	 *
	 * @return
	 * 		The content of this file as a list of strings including EOLs.
	 * @throws IOException
	 * 		If an error occurred while reading the file content.
	 */
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
	 * Reads the line information of this file.
	 *
	 * @return
	 * 		The line information of this file.
	 * @throws IOException
	 * 		If an error occurred while reading the the information.
	 */
	default List<LineInfo> readLineInfo() throws IOException {
		return getVCSEngine().readLineInfo(this);
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

	/**
	 * Maps the given offset to a position using the given tab size.
	 *
	 * @param offset
	 * 		The offset to map. An offset describes the number of characters to
	 * 		move to reach a position. The origin is 0.
	 * @param tabSize
	 * 		The number of characters acquired by a tab (\t).
	 * @return
	 * 		The corresponding position.
	 * @throws IllegalArgumentException
	 * 		If {@code offset < 0} or {@code tabSize < 1}.
	 * @throws IndexOutOfBoundsException
	 * 		If there is no position for {@code offset}.
	 * @throws IOException
	 * 		If an error occurred while reading the file content.
	 */
	default Position mapOffset(final int offset, final int tabSize)
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
			// Find number of tabs in line.
			int tabs = 0;
			for (int i = 0; i < lineStr.length(); i++) {
				if (lineStr.charAt(i) == '\t') {
					tabs++;
				}
			}
			// Calculate line length based on the number of tabs found.
			final int length = lineStr.length() + (tabs * (tabSize - 1));
			if (length <= offs) {
				line++;
				offs -= length;
			} else {
				final String offsStr = lineStr.substring(0, offs + 1);
				// Find number of tabs in sub-line.
				tabs = 0;
				for (int i = 0; i < offsStr.length(); i++) {
					if (lineStr.charAt(i) == '\t') {
						tabs++;
					}
				}
				// Calculate column based on the number of tabs found.
				final int column = offsStr.length() + (tabs * (tabSize - 1));
				return new Position(line, column, tabSize);
			}
		}
		throw new IndexOutOfBoundsException("offset: " + offset);
	}

	/**
	 * Maps the given position to an offset.
	 *
	 * @param position
	 * 		The position to map.
	 * @return
	 * 		The corresponding offset. An offset describes the number of
	 * 		characters to move to reach a position. The origin is 0.
	 * @throws NullPointerException
	 * 		If {@code position} is {@code null}.
	 * @throws IllegalArgumentException
	 * 		If the line or column of {@code position} does not exist.
	 * @throws IOException
	 * 		If an error occurred while reading the file content.
	 */
	default int mapPosition(final Position position) throws IOException {
		Objects.requireNonNull(position);

		////// Error handling line.
		final List<String> lines = readLinesWithEOL();
		if (position.getLine() > lines.size()) {
			throw new IllegalArgumentException(
					String.format("line: %d, lines: %d",
							position.getLine(), lines.size()));
		}

		final int lineIdx = position.getLine() - 1;
		final String lineStr = lines.get(lineIdx);

		////// Error handling column.
		// Find number of tabs in line.
		int tabs = 0;
		for (int i = 0; i < lineStr.length(); i++) {
			if (lineStr.charAt(i) == '\t') {
				tabs++;
			}
		}
		// Calculate line length based on the number of tabs found.
		final int lineLen =
				// Use scanner to remove EOL
				new Scanner(lineStr).nextLine().length() +
				(tabs * (position.getTabSize() - 1));
		if (position.getColumn() > lineLen) {
			throw new IllegalArgumentException(
					String.format("column: %d, columns: %d",
							position.getColumn(), lineStr.length()));
		}

		////// Offset calculation
		int offset = lines.subList(0, lineIdx).stream()
				.map(String::length)
				.mapToInt(Integer::intValue)
				.sum();
		for (int i = 0, column = 1;
			 column < position.getColumn();
			 i++, column++) {
			if (lineStr.charAt(i) == '\t') {
				column += position.getTabSize() - 2;
			}
			offset++;
		}
		return offset;
	}
}
