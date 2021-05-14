package de.unibremen.informatik.st.libvcs4j;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Deque;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Scanner;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

/**
 * Represents a file in a VCS at a certain point in time. For the sake of
 * convenience, we use the name "VCSFile" instead of "File" to avoid naming
 * collisions with {@link File}.
 */
public interface VCSFile extends VCSModelElement {

	/**
	 * A position within a file. As a line of text does not include new line
	 * characters, a position can not point to new line delimiters, such as
	 * '\n', '\r', and '\r\n'. Use {@link VCSFile#positionOf(int, int, int)} or
	 * {@link VCSFile#positionOf(int, int)} to create instances of this class.
	 */
	class Position {

		/**
		 * Compares two positions using their {@link #offset}s.
		 */
		public static final Comparator<Position> OFFSET_COMPARATOR =
				Comparator.comparingInt(Position::getOffset);

		/**
		 * Tests if two positions are equal according to their {@link #offset}s
		 * (using {@link #OFFSET_COMPARATOR}) and their relative paths (using
		 * {@link VCSFile#toRelativePath()} and {@link Path#equals(Object)}).
		 * {@code null} matches {@code null}, but not a {@code non-null} value.
		 */
		public static final BiPredicate<Position, Position>
				RELATIVE_PATH_PREDICATE = (p1, p2) ->
				p1 == null && p2 == null || // null is equal to null
						p1 != null && p2 != null &&
						OFFSET_COMPARATOR.compare(p1, p2) == 0 &&
						p1.getFile().toRelativePath().equals(
								p2.getFile().toRelativePath());

		/**
		 * The referenced file.
		 */
		private final VCSFile file;

		/**
		 * The line of a position {@code >= 1}.
		 */
		private final int line;

		/**
		 * The column of a position {@code >= 1}.
		 */
		private final int column;

		/**
		 * Alternative position information for {@link #line} and
		 * {@link #column} {@code >= 0}).
		 */
		private final int offset;

		/**
		 * Alternative position information for {@link #column} {@code >= 0}.
		 */
		private final int lineOffset;

		/**
		 * The number of characters acquired by a tab (\t) {@code >= 1}.
		 */
		private final int tabSize;

		/**
		 * Creates a new size with given file, line, column, and tab size. To
		 * create new instances, call {@link VCSFile#positionOf(int, int)} or
		 * {@link VCSFile#positionOf(int, int, int)}.
		 *
		 * @param pFile
		 * 		The referenced file of the position to create.
		 * @param pLine
		 * 		The line of the position to create.
		 * @param pColumn
		 * 		The column of the position to create.
		 * @param pOffset
		 * 		The offset of the position to create.
		 * @param pTabSize
		 * 		The tab size of the position to create.
		 * @throws NullPointerException
		 * 		If {@code pFile} is {@code null}.
		 * @throws IllegalArgumentException
		 * 		If {@code pLine < 1}, {@code pColumn < 1}, {@code pOffset < 0},
		 * 		or {@code pTabSize < 1}.
		 */
		private Position(final VCSFile pFile, final int pLine,
				final int pColumn, final int pOffset, final int pLineOffset,
				final int pTabSize) throws NullPointerException,
				IllegalArgumentException {
			file = Validate.notNull(pFile);
			line = Validate.isPositive(pLine, "line < 1");
			column = Validate.isPositive(pColumn, "column < 1");
			offset = Validate.notNegative(pOffset, "offset < 0");
			lineOffset = Validate.notNegative(pLineOffset, "line offset < 0");
			tabSize = Validate.isPositive(pTabSize, "tab size < 1");
		}

		/**
		 * Returns the referenced file.
		 *
		 * @return
		 * 		The referenced file.
		 */
		public VCSFile getFile() {
			return file;
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
		 * Returns the line offset of this position {@code >= 0}.
		 *
		 * @return
		 * 		The line offset of this position {@code >= 0}.
		 */
		public int getLineOffset() {
			return lineOffset;
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
		 * Applies the diff of {@code fileChange} (see
		 * {@link FileChange#computeDiff()}) and computes the resulting
		 * position. Returns an empty Optional if {@code fileChange} is of type
		 * {@link FileChange.Type#REMOVE}, or if the line of this position was
		 * deleted without a corresponding insertion. If the line of this
		 * position was changed to a non-empty string, the resulting column is
		 * set to 1. If the line of this position was changed to an empty
		 * string, an empty {@link Optional} is returned.
		 *
		 * @param fileChange
		 * 		The file change to apply.
		 * @return
		 * 		The updated position.
		 * @throws NullPointerException
		 * 		If {@code fileChange} is {@code null}.
		 * @throws IllegalArgumentException
		 * 		If the file referenced by {@code fileChange} differs from the
		 * 		file referenced by this position.
		 * @throws IOException
		 * 		If computing the line diff (see
		 * 		{@link FileChange#computeDiff()}) fails.
		 */
		public Optional<Position> apply(final FileChange fileChange)
				throws NullPointerException, IllegalArgumentException,
				IOException {
			Validate.notNull(fileChange);
			final VCSFile oldFile = fileChange.getOldFile().orElseThrow(
					() -> new IllegalArgumentException(
							"The given file change has no old file."));
			Validate.isEqualTo(oldFile, getFile(),
					"The given file change references an invalid file.");

			// Ignore removed files.
			if (fileChange.getType() == FileChange.Type.REMOVE) {
				return Optional.empty();
			}
			// getType() != REMOVE => new file must exist.
			final VCSFile newFile = fileChange.getNewFile()
					.orElseThrow(IllegalStateException::new);

			// Find all deletions without insertions and insertions without
			// deletions.
			final List<LineChange> changes = fileChange.computeDiff();
			final List<LineChange> dels = changes.stream()
					.filter(lc -> lc.getType() == LineChange.Type.DELETE)
					.sorted(Comparator.comparingInt(LineChange::getLine))
					.collect(Collectors.toList());
			final List<LineChange> ins = changes.stream()
					.filter(lc -> lc.getType() == LineChange.Type.INSERT)
					.sorted(Comparator.comparingInt(LineChange::getLine))
					.collect(Collectors.toList());
			final List<LineChange> delsWithoutIns = new ArrayList<>();
			final List<LineChange> insWithoutDels = new ArrayList<>();
			int delsIdx = 0; // Index of the currently processed deletion.
			int insIdx = 0;  // Index of currently processed insertion.
			while (delsIdx < dels.size() && insIdx < ins.size()) {
				final LineChange del = dels.get(delsIdx);
				final LineChange in = ins.get(insIdx);
				final int delLine = del.getLine() + insWithoutDels.size();
				final int inLine = in.getLine() + delsWithoutIns.size();
				if (delLine == inLine) {
					delsIdx++;
					insIdx++;
				} else if (delLine < inLine) {
					delsWithoutIns.add(del);
					delsIdx++;
				} else {
					insWithoutDels.add(in);
					insIdx++;
				}
			}
			for(; delsIdx < dels.size(); delsIdx++) {
				delsWithoutIns.add(dels.get(delsIdx));
			}
			for (; insIdx < ins.size(); insIdx++) {
				insWithoutDels.add(ins.get(insIdx));
			}

			// Handle special case: Line was deleted entirely.
			if (delsWithoutIns.stream().anyMatch(
					lc -> lc.getLine() == getLine())) {
				return Optional.empty();
			}

			// Find all deletions and insertions applied up to this position.
			final List<LineChange> layoutChanges = new ArrayList<>();
			layoutChanges.addAll(delsWithoutIns);
			layoutChanges.addAll(insWithoutDels);
			layoutChanges.sort(Comparator.comparingInt(LineChange::getLine));
			final List<LineChange> relevantDels = new ArrayList<>();
			final List<LineChange> relevantIns = new ArrayList<>();
			for (LineChange lc : layoutChanges) {
				if (lc.getType() == LineChange.Type.DELETE &&
						lc.getLine() <= getLine()) {
					relevantDels.add(lc);
				} else if (lc.getType() == LineChange.Type.INSERT) {
					final int inLine = lc.getLine()
							+ relevantDels.size()
							- relevantIns.size();
					if (inLine <= getLine()) {
						relevantIns.add(lc);
					}
				}
			}
			final List<LineChange> relevantChanges = new ArrayList<>();
			relevantChanges.addAll(relevantDels);
			relevantChanges.addAll(relevantIns);

			// Map position.
			final int mappedLine = getLine() -
					// Remove deleted lines.
					(int) relevantChanges.stream()
						.filter(fc -> fc.getType() == LineChange.Type.DELETE)
						.count() +
					// Add inserted lines.
					(int) relevantChanges.stream()
						.filter(fc -> fc.getType() == LineChange.Type.INSERT)
						.count();
			final String oldLineStr = oldFile.readLines().get(getLine() - 1);
			final String newLineStr = newFile.readLines().get(mappedLine - 1);
			if (newLineStr.isEmpty()) {
				// We can't create a position for an empty line.
				return Optional.empty();
			}
			final int mappedColumn = !oldLineStr.equals(newLineStr)
					? 1 // We can't determine the column of a changed line, use
					    // 1 as fallback.
					: getColumn();
			return Optional.of(fileChange.getNewFile()
					.orElseThrow(IllegalStateException::new)
					.positionOf(mappedLine, mappedColumn, getTabSize())
					// Validate implementation.
					.orElseThrow(IllegalStateException::new));
		}

		/**
		 * Returns the position located at the first column of the next line.
		 * If this position is located at the last line, or if the next line is
		 * empty, an empty {@link Optional} is returned.
		 *
		 * @return
		 * 		The position located at the first column of the next line.
		 * @throws IOException
		 * 		If an error occurred while reading the file content.
		 */
		public Optional<Position> nextLine() throws IOException {
			final List<String> lines = getFile().readLines();
			Validate.validateState(lines.size() >= getLine());
			if (lines.size() == getLine()) {
				return Optional.empty();
			}
			return getFile().positionOf(getLine() + 1, 1, getTabSize());
		}

		/**
		 * Returns the position located at the first column of the previous
		 * line. If this position is located at the first line, or if the
		 * previous line is empty, an empty {@link Optional} is returned.
		 *
		 * @return
		 * 		The position located at the first column of the previous line.
		 * @throws IOException
		 * 		If an error occurred while reading the file content.
		 */
		public Optional<Position> previousLine() throws IOException {
			final List<String> lines = getFile().readLines();
			Validate.validateState(lines.size() >= getLine());
			if (getLine() == 1) {
				return Optional.empty();
			}
			return getFile().positionOf(getLine() - 1, 1, getTabSize());
		}

		/**
		 * Returns the position located at the first column of the current
		 * line.
		 *
		 * @return
		 * 		The position located at the first column of the current line.
		 * @throws IOException
		 * 		If an error occurred while reading the file content.
		 */
		public Position beginOfLine() throws IOException {
			return getFile().positionOf(getLine(), 1, getTabSize())
					.orElseThrow(IllegalStateException::new);
		}

		/**
		 * Returns the position located at the last column of the current line.
		 *
		 * @return
		 * 		The position located at the last column of the current line.
		 * @throws IOException
		 * 		If an error occurred while reading the file content.
		 */
		public Position endOfLine() throws IOException {
			final List<String> lines = getFile().readLines();
			Validate.validateState(lines.size() >= getLine());
			final String currentLine = lines.get(getLine()-1);
			int column = 1;
			for(char c : currentLine.toCharArray()){
				column = c == '\t'
						? ( (column-1)/getTabSize() + 1 ) * getTabSize() + 1
						: column + 1;
			}
			return getFile().positionOf(getLine(), column, getTabSize())
					.orElseThrow(IllegalStateException::new);
		}

		/**
		 * Returns a position with same line, column, offset, line offset, and
		 * tab size, but located in {@code file}. Returns an empty optional, if
		 * this position does not exist in {@code file}.
		 *
		 * @param file
		 * 		The file to map this position to.
		 * @return
		 * 		A position with same line, column, offset, line offset, and tab
		 * 		size, but located in {@code file}.
		 * @throws NullPointerException
		 * 		If {@code file} is {@code null}.
		 * @throws IOException
		 * 		If an error occurred while reading the file content.
		 */
		public Optional<Position> mapTo(final VCSFile file)
				throws IOException {
			Validate.notNull(file);
			return file.positionOf(getLine(), getColumn(), getTabSize());
		}

		/**
		 * Reads the character this position points to.
		 *
		 * @return
		 * 		The character this position points to.
		 * @throws IOException
		 * 		If an error occurred while reading the file content.
		 */
		public char readChar() throws IOException {
			return file.readContent().charAt(offset);
		}

		@Override
		public String toString() {
			return String.format("Position(file=%s, line=%d, column=%d, " +
					"offset=%d, lineOffset=%d, tabSize=%d)",
					getFile().toString(), getLine(), getColumn(), getOffset(),
					getLineOffset(), getTabSize());
		}
	}

	/**
	 * A range within a file. Can't be empty or negative.
	 */
	class Range {

		/**
		 * Compares two ranges using their {@link #begin} positions and
		 * {@link Position#OFFSET_COMPARATOR}.
		 */
		public static final Comparator<Range> BEGIN_COMPARATOR =
				(r1, r2) -> Position.OFFSET_COMPARATOR.compare(
						r1.getBegin(), r2.getBegin());

		/**
		 * Tests if two ranges are equal according to their {@link #begin} and
		 * {@link #end} positions (by matching the {@link #begin} and
		 * {@link #end} positions with {@link Position#RELATIVE_PATH_PREDICATE}.
		 * {@code null} matches {@code null}, but not a {@code non-null} value.
		 */
		public static final BiPredicate<Range, Range>
				RELATIVE_PATH_PREDICATE = (r1, r2) ->
				r1 == null && r2 == null || // null is equal to null
						r1 != null && r2 != null &&
						Position.RELATIVE_PATH_PREDICATE.test(
								r1.getBegin(), r2.getBegin()) &&
						Position.RELATIVE_PATH_PREDICATE.test(
								r1.getEnd(), r2.getEnd());

		/**
		 * The begin position.
		 */
		private final Position begin;

		/**
		 * The end position (inclusive).
		 */
		private final Position end;

		/**
		 * Creates a new range with given begin and end position.
		 *
		 * @param pBegin
		 * 		The begin position of the range to create.
		 * @param pEnd
		 * 		The end position of the range to create.
		 * @throws NullPointerException
		 * 		If any of the given arguments is {@code null}.
		 * @throws IllegalArgumentException
		 * 		If {@code pBegin} and {@code pEnd} reference different files,
		 * 		or if {@code pBegin} is after {@code pEnd}.
		 */
		public Range(final Position pBegin, final Position pEnd)
				throws NullPointerException, IllegalArgumentException {
			begin = Validate.notNull(pBegin);
			end = Validate.notNull(pEnd);
			Validate.isEqualTo(begin.getFile(), end.getFile(),
					"Begin and end position reference different files.");
			Validate.isTrue(begin.getOffset() <= end.getOffset(),
					"Begin must not be after end.");
		}

		/**
		 * Calculates the sum of the lengths of the given ranges. Overlapping
		 * parts are handled accordingly.
		 *
		 * @param ranges
		 * 		The collection of ranges to calculate the sum of the lengths
		 * 		from. If {@code null}, {@code 0} is returned. The collection
		 * 		may contain {@code null} values.
		 * @return
		 * 		The sum of the lengths of the given ranges.
		 * @throws IllegalArgumentException
		 * 		If the ranges reference different files.
		 */
		public static int lengthOf(final Collection<Range> ranges) {
			if (ranges == null || ranges.isEmpty()) {
				return 0;
			}
			final Deque<Range> queue = ranges.stream()
					.filter(Objects::nonNull)
					.sorted((r1, r2) -> Position.OFFSET_COMPARATOR
							.compare(r1.getBegin(), r2.getBegin()))
					.collect(Collectors.toCollection(ArrayDeque::new));
			final List<Range> parts = new ArrayList<>();
			while (queue.size() >= 2) {
				final Range head = queue.poll();
				final Range next = queue.poll();
				// Throws IllegalArgumentException if necessary.
				final Optional<Range> merge = head.merge(next);
				if (merge.isPresent()) {
					queue.addFirst(merge.get());
				} else {
					parts.add(head);
					queue.addFirst(next);
				}
			}
			if (!queue.isEmpty()) {
				parts.add(queue.poll());
			}
			return parts.stream()
					.map(Range::length)
					.mapToInt(Integer::intValue)
					.sum();
		}

		/**
		 * Returns the begin position of this range.
		 *
		 * @return
		 * 		The begin position of this range.
		 */
		public Position getBegin() {
			return begin;
		}

		/**
		 * Returns the end position of this range.
		 *
		 * @return
		 * 		The end position of this range.
		 */
		public Position getEnd() {
			return end;
		}

		/**
		 * Returns the referenced file.
		 *
		 * @return
		 * 		The referenced. file.
		 */
		public VCSFile getFile() {
			return getBegin().getFile();
		}

		/**
		 * Returns the length of this range.
		 *
		 * @return
		 * 		The length of this range.
		 */
		public int length() {
			return (getEnd().getOffset() + 1) - getBegin().getOffset();
		}

		/**
		 * Reads the content of this range.
		 *
		 * @return
		 * 		The content of this range.
		 * @throws IOException
		 * 		If an error occurred while reading the file content.
		 */
		public String readContent() throws IOException {
			return getFile().readContent().substring(
					getBegin().getOffset(), getEnd().getOffset() + 1);
		}

		/**
		 * Creates a new range that merges the positions of this and the given
		 * range. Returns an empty {@link Optional} if their positions do not
		 * overlap.
		 *
		 * @param range
		 * 		The range to merge.
		 * @return
		 * 		A range that merges the overlapping positions of this and the
		 * 		given range. An empty {@link Optional} if their positions do
		 * 		not overlap.
		 * @throws NullPointerException
		 * 		If {@code range} is {@code null}.
		 * @throws IllegalArgumentException
		 * 		If {@code range} references a different file.
		 */
		public Optional<Range> merge(final Range range)
				throws NullPointerException, IllegalArgumentException {
			Validate.notNull(range);
			Validate.isEqualTo(getFile(), range.getFile());

			final Range upper = BEGIN_COMPARATOR.compare(this, range) < 0
					? this : range;
			final Range lower = this == upper ? range : this;

			// Unable to merge ranges with a gap.
			if (upper.getEnd().getOffset() + 1 < // make end offset exclusive
					lower.getBegin().getOffset()) {
				return Optional.empty();
			}
			// Upper subsumes lower.
			if (upper.getEnd().getOffset() >= lower.getEnd().getOffset()) {
				return Optional.of(new Range(
						upper.getBegin(), upper.getEnd()));
			}
			// Merge upper and lower
			Validate.validateState( // just to be sure
					upper.getEnd().getOffset() + 1 // make end offset exclusive
							>= lower.getBegin().getOffset());
			Validate.validateState( // just to be sure
					upper.getBegin().getOffset()
							<= lower.getBegin().getOffset());
			return Optional.of(new Range(upper.getBegin(), lower.getEnd()));
		}

		/**
		 * Delegates {@code fileChange} to {@link #getBegin()} and
		 * {@link #getEnd()} (see {@link Position#apply(FileChange)}) and
		 * computes the resulting range. Returns an empty Optional if
		 * {@code fileChange} is of type {@link FileChange.Type#REMOVE}, or if
		 * {@link #getBegin()} or {@link #getEnd()} returns an empty
		 * {@link Optional}.
		 *
		 * @param fileChange
		 * 		The file change to apply.
		 * @return
		 * 		The updated range.
		 * @throws NullPointerException
		 * 		If {@code fileChange} is {@code null}.
		 * @throws IllegalArgumentException
		 * 		If the file referenced by {@code fileChange} differs from the
		 * 		file referenced by this range.
		 * @throws IOException
		 * 		If computing the line diff (see
		 * 		{@link FileChange#computeDiff()}) fails.
		 */
		public Optional<Range> apply(final FileChange fileChange)
				throws NullPointerException, IOException {
			Validate.notNull(fileChange);
			final Optional<Position> newBegin = getBegin().apply(fileChange);
			final Optional<Position> newEnd = getEnd().apply(fileChange);
			return newBegin.isPresent() && newEnd.isPresent()
					? Optional.of(new Range(newBegin.get(), newEnd.get()))
					: Optional.empty();
		}

		/**
		 * Returns a range with same begin and end, but located in
		 * {@code file}. Returns an empty optional, if this range does not
		 * exist in {@code file}.
		 *
		 * @param file
		 * 		The file to map this range to.
		 * @return
		 * 		A range with same begin and end, but located in {@code file}.
		 * @throws NullPointerException
		 * 		If {@code file} is {@code null}.
		 * @throws IOException
		 * 		If an error occurred while reading the file content.
		 */
		public Optional<Range> mapTo(final VCSFile file) throws IOException {
			Validate.notNull(file);
			final Optional<Position> newBegin = getBegin().mapTo(file);
			final Optional<Position> newEnd = getEnd().mapTo(file);
			return newBegin.isPresent() && newEnd.isPresent()
					? Optional.of(new Range(newBegin.get(), newEnd.get()))
					: Optional.empty();
		}

		@Override
		public String toString() {
			return String.format("Range(begin=%s, end=%s)",
					getBegin().toString(), getEnd().toString());
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
	default Optional<Charset> guessCharset() throws IOException {
		return getVCSEngine().guessCharset(this);
	}

	/**
	 * Returns the absolute path of this file as it was like when its
	 * corresponding revision was checked out by {@link VCSEngine#next()}.
	 *
	 * @return
	 * 		The absolute path of this file.
	 */
	default String getPath() {
		return getRevision().getOutput()
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
	 * Returns the content of this file as a String. The default implementation
	 * uses {@link #readAllBytes()} and {@link #guessCharset()} to create an
	 * appropriate String. If {@link #guessCharset()} returns an empty optional
	 * the system default charset is used as fallback.
	 *
	 * @return
	 * 		The content of this file as a String.
	 * @throws BinaryFileException
	 * 		If this file is binary (see {@link #isBinary()}).
	 * @throws IOException
	 * 		If an error occurred while reading the file content.
	 */
	default String readContent() throws IOException {
		if (isBinary()) {
			throw new BinaryFileException(String.format(
					"'%s' is a binary file", getPath()));
		}
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
	 * @throws BinaryFileException
	 * 		If this file is binary (see {@link #isBinary()}).
	 * @throws IOException
	 * 		If an error occurred while reading the file content.
	 */
	default List<String> readLines() throws IOException {
		final List<String> lines = new ArrayList<>();
		try (final Scanner scanner = new Scanner(readContent())) {
			while (scanner.hasNextLine()) {
				lines.add(scanner.nextLine());
			}
			return lines;
		}
	}

	/**
	 * Returns the content of this file as a list of strings including EOL
	 * characters. The following EOLs are supported: '\n', '\r\n', '\r'.
	 *
	 * @return
	 * 		The content of this file as a list of strings including EOLs.
	 * @throws BinaryFileException
	 * 		If this file is binary (see {@link #isBinary()}).
	 * @throws IOException
	 * 		If an error occurred while reading the file content.
	 */
	default List<String> readLinesWithEOL() throws IOException {
		final StringReader reader = new StringReader(readContent());
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
	 * @throws BinaryFileException
	 * 		If this file is binary (see {@link #isBinary()}).
	 * @throws IOException
	 * 		If an error occurred while reading the the information.
	 */
	default List<LineInfo> readLineInfo() throws IOException {
		if (isBinary()) {
			throw new BinaryFileException(String.format(
					"'%s' is a binary file", getPath()));
		}
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
		final byte[] bytes = readAllBytes();
		final String fileName = toPath().getFileName().toString();

		////////////// Files#probeContentType(Path)
		// Some detectors parse the extension of a file to guess its type.
		// Thus, use the file name as suffix for the temporarily created file.
		final Path tmp = Files.createTempFile(null, fileName);
		try {
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
		} finally {
			try { Files.delete(tmp); }
			catch (final Exception e) { /* ignored */ }
		}

		////////////// Heuristic
		int numASCII = 0;
		int numNonASCII = 0;
		for (final byte b : bytes) {
			if (b == 0x09 ||     // \t
					b == 0x0A || // \n
					b == 0x0C || // \f
					b == 0x0D) { // \r
				numASCII++;
			} else if (b >= 0x20 && b <= 0x7E) { // regular char
				numASCII++;
			} else { // something else
				numNonASCII++;
			}
		}
		final double nonASCIIRation =
				(double)numNonASCII /
						(numASCII + numNonASCII);
		if (
				// C
				fileName.endsWith(".c") ||
				fileName.endsWith(".h") ||
				// C++
				fileName.endsWith(".cc")  ||
				fileName.endsWith(".hh")  ||
				fileName.endsWith(".cpp") ||
				fileName.endsWith(".hpp") ||
				fileName.endsWith(".cxx") ||
				fileName.endsWith(".hxx") ||
				// CSS
				fileName.endsWith(".css") ||
				// C#
				fileName.endsWith(".cs") ||
				// Groovy
				fileName.endsWith(".groovy") ||
				// HTML
				fileName.endsWith(".html") ||
				// Java
				fileName.endsWith(".java") ||
				// Javascript
				fileName.endsWith(".js") ||
				// JSF
				fileName.endsWith(".xhtml") ||
				// Kotlin
				fileName.endsWith(".kt") ||
				// Markdown
				fileName.endsWith(".md") ||
				// PHP
				fileName.endsWith(".php") ||
				// Python
				fileName.endsWith(".py") ||
				// Scala
				fileName.endsWith(".scala") ||
				// Tex
				fileName.endsWith(".tex") ||
				// Typescript
				fileName.endsWith(".ts")) {
			return nonASCIIRation > 0.3;
		} else {
			return nonASCIIRation > 0.95;
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
	 * Creates a position from the the given offset and tab size. Returns an
	 * empty {@link Optional} if there is no position for {@code offset}, or if
	 * {@code offset} points to a new line delimiter.
	 *
	 * @param offset
	 * 		The number of characters to move to reach a position.
	 * @param tabSize
	 * 		The number of characters acquired by a tab (\t).
	 * @return
	 * 		The corresponding position.
	 * @throws IllegalArgumentException
	 * 		If {@code offset < 0} or {@code tabSize < 1}.
	 * @throws BinaryFileException
	 * 		If this file is binary (see {@link #isBinary()}).
	 * @throws IOException
	 * 		If an error occurred while reading the file content.
	 */
	default Optional<Position> positionOf(final int offset, final int tabSize)
			throws IllegalArgumentException, IOException {
		Validate.notNegative(offset);
		Validate.isPositive(tabSize);

		final List<String> lines = readLinesWithEOL();

		int line = 1;
		int offsetInLine = offset;
		while (line <= lines.size()) {
			final String lineStr = lines.get(line - 1);
			final int lineLen = lineStr.length();
			if (lineLen <= offsetInLine) {
				line++;
				offsetInLine -= lineLen;
			} else {
				String offsetStr = lineStr.substring(0, offsetInLine + 1);
				if (offsetStr.endsWith("\n") || offsetStr.endsWith("\r")) {
					return Optional.empty();
				}
				offsetStr = offsetStr.substring(0, offsetStr.length() - 1);
				int column = 1;
				for (char c : offsetStr.toCharArray()) {
					column = c == '\t'
							? ( (column-1)/tabSize + 1 ) * tabSize + 1
							: column + 1;
				}
				return Optional.of(new Position(this, line, column, offset,
						offsetInLine, tabSize));
			}
		}
		return Optional.empty();
	}

	/**
	 * Creates a position from the given line, column, and tab size. Returns an
	 * empty {@link Optional} if there is no position for {@code line} and
	 * {@code column} with respect to {@code tabSize}, or if {@code line} and
	 * {@code column} with respect to {@code tabSize} point to a new line
	 * delimiter.
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
	 * @throws BinaryFileException
	 * 		If this file is binary (see {@link #isBinary()}).
	 * @throws IOException
	 * 		If an error occurred while reading the file content.
	 */
	default Optional<Position> positionOf(final int line, final int column,
			final int tabSize) throws IllegalArgumentException,
			IndexOutOfBoundsException, IOException {
		Validate.isPositive(line);
		Validate.isPositive(column);
		Validate.isPositive(tabSize);

		// We need the lines with EOL to compute the corresponding offset.
		final List<String> lines = readLinesWithEOL();
		if (line > lines.size()) {
			return Optional.empty();
		}

		final int lineIdx = line - 1;
		final String lineStr = lines.get(lineIdx);
		int col = 1;
		for (int offsetInLine = 0; offsetInLine < lineStr.length();
				offsetInLine++) {
			final char c = lineStr.charAt(offsetInLine);
			if (c == '\n' || c == '\r' || col > column) {
				return Optional.empty();
			} else if (col == column) {
				final int offset = offsetInLine +
						lines.subList(0, lineIdx).stream()
								.map(String::length)
								.mapToInt(Integer::intValue)
								.sum();
				return Optional.of(new Position(this, line, column, offset,
						offsetInLine, tabSize));
			}
			col = c == '\t'
					? ( (col-1)/tabSize + 1 ) * tabSize + 1
					: col + 1;
		}
		return Optional.empty();
	}
}
