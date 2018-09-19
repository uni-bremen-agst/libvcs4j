package de.unibremen.informatik.st.libvcs4j;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * Represents a file in a VCS at a certain point in time. For the sake of
 * convenience, we use the name "VCSFile" instead of "File" to avoid naming
 * collisions with {@link File}.
 */
public interface VCSFile extends VCSModelElement {

	/**
	 * A position within a file.
	 */
	class Position {

		/**
		 * Compares two positions using their offsets.
		 */
		public static Comparator<Position> OFFSET_COMPARATOR =
				Comparator.comparingInt(Position::getOffset);

		/**
		 * The line of a position {@code >= 1}.
		 */
		private final int line;

		/**
		 * The column of a position {@code >= 1}.
		 */
		private final int column;

		/**
		 * The number of characters to move to reach a position {@code >= 0}.
		 */
		private final int offset;

		/**
		 * The number of characters acquired by a tab (\t) {@code >= 1}.
		 */
		private final int tabSize;

		/**
		 * Creates a new size with given line, column, and tab size.
		 *
		 * @param pLine
		 * 		The line of the position to create.
		 * @param pColumn
		 * 		The column of the position to create.
		 * @param pOffset
		 * 		The offset of the position to create.
		 * @param pTabSize
		 * 		The tab size of the position to create.
		 * @throws IllegalArgumentException
		 * 		If {@code pLine < 1}, {@code pColumn < 1}, {@code pOffset < 0},
		 * 		or {@code pTabSize < 1}.
		 */
		public Position(final int pLine, final int pColumn, final int pOffset,
				final int pTabSize) throws IllegalArgumentException {
			line = Validate.isPositive(pLine, "line < 1");
			column = Validate.isPositive(pColumn, "column < 1");
			offset = Validate.notNegative(pOffset, "offset < 0");
			tabSize = Validate.isPositive(pTabSize, "tab size < 1");
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
		 * Returns the offset of this position {@code >= 0}.
		 *
		 * @return
		 * 		The offset of this position {@code >= 0}.
		 */
		public int getOffset() {
			return offset;
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

		/**
		 * Applies the diff of {@code fileChange}
		 * ({@link FileChange#computeDiff()}) and computes the resulting
		 * position. Returns an empty Optional if {@code fileChange} is of type
		 * {@link FileChange.Type#REMOVE} or if the line of this position was
		 * deleted without a corresponding insertion. If the line of this
		 * position was changed, the resulting column is set to 1.
		 *
		 * @param fileChange
		 * 		The file change to apply.
		 * @return
		 * 		The updated position.
		 * @throws NullPointerException
		 * 		If {@code fileChange} is {@code null}.
		 * @throws IOException
		 * 		If computing the line diff ({@link FileChange#computeDiff()})
		 * 		fails.
		 */
		public Optional<Position> apply(final FileChange fileChange)
				throws NullPointerException, IOException {
			Validate.notNull(fileChange);

			// Ignore removed files.
			if (fileChange.getType() == FileChange.Type.REMOVE) {
				return Optional.empty();
			}

			// Ignore changes applied after this position.
			final List<LineChange> changes = fileChange
					.computeDiff().stream()
					.filter(lc -> lc.getLine() <= getLine())
					.collect(Collectors.toList());

			// Has this position been deleted (its corresponding line was
			// deleted without being inserted)?
			final boolean lineDeleted = changes.stream()
					.anyMatch(lc -> lc.getLine() == getLine() &&
							lc.getType() == LineChange.Type.DELETE);
			final boolean lineInserted = changes.stream()
					.anyMatch(lc -> lc.getLine() == getLine() &&
							lc.getType() == LineChange.Type.INSERT);
			if (lineDeleted && !lineInserted) {
				// This position was deleted.
				return Optional.empty();
			}

			final int line = getLine() -
					// Remove deleted lines.
					(int) changes.stream()
						.filter(fc -> fc.getType() == LineChange.Type.DELETE)
						.count() +
					// Add inserted lines.
					(int) changes.stream()
						.filter(fc -> fc.getType() == LineChange.Type.INSERT)
						.count();
			final int column = lineDeleted
					? 1 // lineDeleted? => lineInserted => line change
					    // We can't determine the column of a changed line, use
					    // 1 as fallback.
					: getColumn();
			return Optional.of(fileChange.getNewFile()
					.orElseThrow(IllegalStateException::new)
					.positionOf(line, column, getTabSize()));
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
	 * Returns the content of this file as a list of strings excluding EOL
	 * characters.
	 *
	 * @return
	 * 		The content of this file as a list of strings excluding EOLs.
	 * @throws IOException
	 * 		If an error occurred while reading the file content.
	 */
	default List<String> readLines() throws IOException {
		final List<String> lines = new ArrayList<>();
		final Scanner scanner = new Scanner(readeContent());
		while (scanner.hasNextLine()) {
			lines.add(scanner.nextLine());
		}
		return lines;
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
	 * Tries to guess whether this file is a binary file. The default
	 * implementation uses {@link Files#probeContentType(Path)} to check
	 * whether the detected file type (if any) matches one of the predefined
	 * values. If {@link Files#probeContentType(Path)} is unable to detect the
	 * file type, the number of ASCII and non-ASCII chars is counted and
	 * evaluated.
	 *
	 * @return
	 * 		{@code true} if this file is a binary file, {@code false}
	 * 		otherwise.
	 * @throws IOException
	 * 		If an error occurred while reading the file contents.
	 */
	default boolean isBinary() throws IOException {
		// Some detectors parse the file extension to guess the file type.
		// Thus, use the file name as suffix for the temporarily created file.
		final Path tmp = Files.createTempFile(null,
				toPath().getFileName().toString());
		try {
			final byte[] bytes = readAllBytes();
			Files.write(tmp, bytes);
			final String type = Files.probeContentType(tmp);
			if (type != null) {
				return !(
						type.startsWith("text") ||
						// Bash
						type.equals("application/x-sh") ||
						// C-Shell
						type.equals("application/x-csh") ||
						// JavaScript
						type.equals("application/javascript") ||
						// JSF
						type.equals("application/xhtml+xml") ||
						// JSON
						type.equals("application/json") ||
						// Latex
						type.equals("application/x-latex") ||
						// PHP
						type.equals("application/x-httpd-php") ||
						// RTF
						type.equals("application/rtf") ||
						// Tex
						type.equals("application/x-tex") ||
						// Texinfo
						type.equals("application/x-texinfo") ||
						// Typescript
						type.equals("application/typescript") ||
						// XML
						type.equals("application/xml"));
			}
			// Apply heuristic.
			int numASCII = 0;
			int numNonASCII = 0;
			for (final byte b : bytes) {
				if (b < 0x09) { // less than \t
					return true;
				} else if (b == 0x09 || // \t
						b == 0x0A ||    // \n
						b == 0x0C ||    // \f
						b == 0x0D) {    // \r
					numASCII++;
				} else if (b >= 0x20 && b <= 0x7E) { // regular char
					numASCII++;
				} else { // something else
					numNonASCII++;
				}
			}
			return numNonASCII != 0 &&
					100 * numNonASCII / (numASCII + numNonASCII) > 95;
		} finally {
			try {
				Files.delete(tmp);
			} catch (final Exception e) {
				/* ignored */
			}
		}
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
	 * Creates a position from the the given offset tab size.
	 *
	 * @param offset
	 * 		The number of characters to move to reach a position.
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
	default Position positionOf(final int offset, final int tabSize)
			throws IllegalArgumentException, IndexOutOfBoundsException,
			IOException {
		Validate.notNegative(offset);
		Validate.isPositive(tabSize);

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
				return new Position(line, column, offset, tabSize);
			}
		}
		throw new IndexOutOfBoundsException("offset: " + offset);
	}

	/**
	 * Creates a position from the given line, column, and tab size.
	 *
	 * @param line
	 * 		The line of the position to create.
	 * @param column
	 * 		The column of the position to create.
	 * @param tabSize
	 * 		The number of characters acquired by a tab (\t).
	 * @return
	 * 		The corresponding position.
	 * @throws IllegalArgumentException
	 * 		If {@code line < 1}, {@code column < 1}, or {@code tabSize < 1}.
	 * @throws IndexOutOfBoundsException
	 * 		If there is no position for {@code line} and {@code column} with
	 * 		respect to {@code tabSize}.
	 * @throws IOException
	 * 		If an error occurred while reading the file content.
	 */
	default Position positionOf(final int line, final int column,
			final int tabSize) throws IllegalArgumentException,
			IndexOutOfBoundsException, IOException {
		////// Error handling tab size.
		Validate.isPositive(tabSize);

		////// Error handling line.
		Validate.isPositive(line);
		final List<String> lines = readLinesWithEOL();
		if (line > lines.size()) {
			throw new IndexOutOfBoundsException(String.format(
					"line: %d, lines: %d", line, lines.size()));
		}

		final int lineIdx = line - 1;
		final String lineStr = lines.get(lineIdx);

		////// Error handling column.
		// Find number of tabs in line.
		Validate.isPositive(column);
		int numTabs = 0;
		for (int i = 0; i < lineStr.length(); i++) {
			if (lineStr.charAt(i) == '\t') {
				numTabs++;
			}
		}
		// Calculate line length based on the number of tabs found.
		final int lineLen =
				// Use scanner to remove EOL
				new Scanner(lineStr).nextLine().length() +
						(numTabs * (tabSize - 1));
		if (column > lineLen) {
			throw new IndexOutOfBoundsException(String.format(
					"column: %d, columns: %d", column, lineLen));
		}

		////// Offset calculation
		int offset = lines.subList(0, lineIdx).stream()
				.map(String::length)
				.mapToInt(Integer::intValue)
				.sum();
		for (int i = 0, c = 1; c < column; i++, c++) {
			if (lineStr.charAt(i) == '\t') {
				c += tabSize - 2;
			}
			offset++;
		}

		////// Result
		return new Position(line, column, offset, tabSize);
	}
}
