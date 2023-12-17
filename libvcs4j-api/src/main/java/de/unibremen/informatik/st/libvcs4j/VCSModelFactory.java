package de.unibremen.informatik.st.libvcs4j;

import java.io.IOException;
import java.lang.ref.SoftReference;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * This factory is responsible for instantiating model elements implementing
 * the {@link VCSModelElement} interface. The default methods provide a sane
 * default implementation.
 */
public interface VCSModelFactory {

	/**
	 * Creates a flat copy of {@code list}. Returns an empty list if
	 * {@code list} is {@code null}. {@code null} values are filtered out.
	 *
	 * @param list
	 * 		The list to copy.
	 * @param <T>
	 *     	The type of the values of {@code list}.
	 * @return
	 * 		The copied list.
	 */
	default <T> List<T> createCopy(final List<T> list) {
		return list == null
				? new ArrayList<>() : list.stream()
				.filter(Objects::nonNull)
				.collect(Collectors.toList());
	}

	/**
	 * Creates a new {@link Commit}. List arguments are flat copied. If any of
	 * the given lists is {@code null}, an empty list is used as fallback.
	 * {@code null} values are filtered out.
	 *
	 * @param id
	 * 		The id of the commit to create.
	 * @param author
	 * 		The author of the commit to create.
	 * @param message
	 * 		The message of the commit to create.
	 * @param dateTime
	 * 		The datetime of the commit to create.
	 * @param parentIds
	 * 		The parent ids of the commit to create.
	 * @param fileChanges
	 * 		The file changes of the commit to create.
	 * @param issues
	 * 		The issues of the commit to create.
	 * @param engine
	 * 		The engine of the commit to create.
	 * @return
	 * 		The created {@link Commit} instance.
	 * @throws NullPointerException
	 * 		If {@code id}, {@code author}, {@code message}, {@code dateTime},
	 * 		or {@code engine} is {@code null}.
	 * @throws IllegalArgumentException
	 * 		If {@code id} is empty.
	 */
	default Commit createCommit(final String id, final String author,
			final String message, final LocalDateTime dateTime,
			final List<String> parentIds, final List<FileChange> fileChanges,
			final List<Issue> issues, final VCSEngine engine)
			throws NullPointerException, IllegalArgumentException {
		Validate.notEmpty(id);
		Validate.notNull(author);
		Validate.notNull(message);
		Validate.notNull(dateTime);
		Validate.notNull(engine);
		final List<String> _parentIds = createCopy(parentIds);
		final List<FileChange> _fileChanges = createCopy(fileChanges);
		final List<Issue> _issues = createCopy(issues);
		return new Commit() {
			@Override
			public String getId() {
				return id;
			}

			@Override
			public String getAuthor() {
				return author;
			}

			@Override
			public String getMessage() {
				return message;
			}

			@Override
			public LocalDateTime getDateTime() {
				return dateTime;
			}

			@Override
			public List<String> getParentIds() {
				return new ArrayList<>(_parentIds);
			}

			@Override
			public List<FileChange> getFileChanges() {
				return new ArrayList<>(_fileChanges);
			}

			@Override
			public List<Issue> getIssues() {
				return new ArrayList<>(_issues);
			}

			@Override
			public VCSEngine getVCSEngine() {
				return engine;
			}

			@Override
			public String toString() {
				return String.format("Commit(id=%s, author=%s, message=%s, " +
								"dateTime=%s, parentIds=%s, fileChanges=%d, " +
								"issues=%d)", getId(), getAuthor(),
						getMessage(), getDateTime().toString(),
						Arrays.deepToString(getParentIds().toArray()),
						getFileChanges().size(), getIssues().size());
			}
		};
	}

	/**
	 * Creates a new {@link FileChange}.
	 *
	 * @param oldFile
	 * 		The old file of the file change to create.
	 * @param newFile
	 * 		The new file of the file change to create.
	 * @param engine
	 * 		The engine of the file change to create.
	 * @return
	 * 		The created {@link FileChange} instance.
	 * @throws NullPointerException
	 * 		If {@code engine} is {@code null}.
	 * @throws IllegalArgumentException
	 * 		If {@code oldFile} as well as {newFile} is {@code null}.
	 */
	default FileChange createFileChange(final VCSFile oldFile,
			final VCSFile newFile, final VCSEngine engine)
			throws NullPointerException, IllegalArgumentException {
		Validate.isFalse(oldFile == null && newFile == null,
				"At least one of the given files must not be null");
		Validate.notNull(engine);
		return new FileChange() {
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
				return engine;
			}

			@Override
			public String toString() {
				return String.format("FileChange(oldFile=%s, newFile=%s)",
						getOldFile().map(VCSFile::toString).orElse(null),
						getNewFile().map(VCSFile::toString).orElse(null));
			}
		};
	}

	/**
	 * Creates a new {@link LineChange}.
	 *
	 * @param type
	 * 		The type of the line change to create.
	 * @param line
	 * 		The line number of the line change to create.
	 * @param content
	 * 		The content of the line change to create.
	 * @param file
	 * 		The file of the line change to create.
	 * @param engine
	 * 		The engine of the line change to create.
	 * @return
	 * 		The created {@link LineChange} instance.
	 * @throws NullPointerException
	 * 		If any of the given arguments is {@code null}.
	 * @throws IllegalArgumentException
	 * 		If {@code line < 1}.
	 */
	default LineChange createLineChange(final LineChange.Type type,
			final int line, final String content, final VCSFile file,
			final VCSEngine engine) throws NullPointerException,
			IllegalArgumentException {
		Validate.notNull(type);
		Validate.isPositive(line, "Line (%d) < 1", line);
		Validate.notNull(content);
		Validate.notNull(file);
		Validate.notNull(engine);
		return new LineChange() {
			@Override
			public Type getType() {
				return type;
			}

			@Override
			public int getLine() {
				return line;
			}

			@Override
			public String getContent() {
				return content;
			}

			@Override
			public VCSFile getFile() {
				return file;
			}

			@Override
			public VCSEngine getVCSEngine() {
				return engine;
			}

			@Override
			public String toString() {
				return String.format("LineChange(type=%s, line=%d, " +
								"content=%s, file=%s)", getType().toString(),
						getLine(), getContent(), getFile().toString());
			}
		};
	}

	/**
	 * Creates a new {@link LineInfo}.
	 *
	 * @param id
	 * 		The id of the line info to create.
	 * @param author
	 * 		The author of the line info to create.
	 * @param message
	 * 		The message of the line info to create.
	 * @param dateTime
	 * 		The datetime of the line info to create.
	 * @param line
	 * 		The line number of the line info to create.
	 * @param content
	 * 		The content of the line info to create.
	 * @param file
	 * 		The file of the line info to create.
	 * @param engine
	 * 		The engine of the line info to create.
	 * @return
	 * 		The created {@link LineInfo} instance.
	 * @throws NullPointerException
	 * 		If any of the given arguments is {@code null}.
	 * @throws IllegalArgumentException
	 * 		If {@code line < 1}.
	 */
	default LineInfo createLineInfo(final String id, final String author,
			final String message, final LocalDateTime dateTime, final int line,
			final String content, final VCSFile file, final VCSEngine engine)
			throws NullPointerException, IllegalArgumentException {
		Validate.notEmpty(id);
		Validate.notNull(author);
		Validate.notNull(message);
		Validate.notNull(dateTime);
		Validate.isPositive(line, "Line (%d) < 1");
		Validate.notNull(content);
		Validate.notNull(file);
		Validate.notNull(engine);
		return new LineInfo() {
			@Override
			public String getId() {
				return id;
			}

			@Override
			public String getAuthor() {
				return author;
			}

			@Override
			public String getMessage() {
				return message;
			}

			@Override
			public LocalDateTime getDateTime() {
				return dateTime;
			}

			@Override
			public int getLine() {
				return line;
			}

			@Override
			public String getContent() {
				return content;
			}

			@Override
			public VCSFile getFile() {
				return file;
			}

			@Override
			public VCSEngine getVCSEngine() {
				return engine;
			}

			@Override
			public String toString() {
				return String.format("LineInfo(id=%s, author=%s, " +
						"message=%s, dateTime=%s, line=%d, content=%s, " +
						"file=%s)", getId(), getAuthor(), getMessage(),
						getDateTime().toString(), getLine(), getContent(),
						getFile().toString());
			}
		};
	}

	/**
	 * Creates a new {@link Revision}. List arguments are flat copied. If any
	 * of the given lists is {@code null}, an empty list is used as fallback.
	 * {@code null} values are filtered out.
	 *
	 * @param id
	 * 		The id of the revision to create.
	 * @param files
	 * 		The files (relative paths) of the revision to create.
	 * @param engine
	 * 		The engine of the revision to create.
	 * @return
	 * 		The created {@link Revision} instance.
	 * @throws NullPointerException
	 * 		If {@code id} or {@code engine} is {@code null}.
	 * @throws IllegalArgumentException
	 * 		If {@code id} is empty.
	 */
	default Revision createRevision(final String id, final List<String> files,
			final VCSEngine engine) throws NullPointerException,
			IllegalArgumentException {
		Validate.notEmpty(id);
		Validate.notNull(engine);
		final List<VCSFile> _files = new ArrayList<>();
		final Revision revision = new Revision() {
			@Override
			public String getId() {
				return id;
			}

			@Override
			public List<VCSFile> getFiles() {
				return new ArrayList<>(_files);
			}

			@Override
			public VCSEngine getVCSEngine() {
				return engine;
			}

			@Override
			public String toString() {
				return String.format("Revision(id=%s, output=%s, files=%d)",
						getId(), getOutput().toString(), getFiles().size());
			}
		};
		createCopy(files).stream()
				.map(f -> createVCSFile(f, revision, engine))
				.forEach(_files::add);
		return revision;
	}

	/**
	 * Creates a new {@link RevisionRange}. List arguments are flat copied. If
	 * any of the given lists is {@code null}, an empty list is used as
	 * fallback. {@code null} values are filtered out.
	 *
	 * @param ordinal
	 * 		The ordinal of the range to create.
	 * @param current
	 * 		The current revision of the range to create.
	 * @param previous
	 * 		The previous revision of the range to create.
	 * @param commits
	 * 		The commits of the range to create.
	 * @param engine
	 * 		The engine of the range to create.
	 * @return
	 * 		The created {@link RevisionRange} instance.
	 * @throws NullPointerException
	 * 		If {@code current} or {@code engine} is {@code null}.
	 * @throws IllegalArgumentException
	 * 		If {@code ordinal < 1} or if {@code commits} is empty.
	 */
	default RevisionRange createRevisionRange(final int ordinal,
			final Revision current, final Revision previous,
			final List<Commit> commits, final VCSEngine engine)
			throws NullPointerException, IllegalArgumentException {
		Validate.isPositive(ordinal, "Ordinal (%d) < 1");
		Validate.notNull(current);
		Validate.notNull(engine);
		final List<Commit> _commits = createCopy(commits);
		Validate.notEmpty(_commits, "There must be at least one commit");
		return new RevisionRange() {
			@Override
			public int getOrdinal() {
				return ordinal;
			}

			@Override
			public Revision getCurrent() {
				return current;
			}

			@Override
			public Optional<Revision> getPrevious() {
				return Optional.ofNullable(previous);
			}

			@Override
			public List<Commit> getCommits() {
				return new ArrayList<>(_commits);
			}

			@Override
			public VCSEngine getVCSEngine() {
				return engine;
			}

			@Override
			public String toString() {
				return String.format("RevisionRange(ordinal=%d, " +
						"current=%s, previous=%s, commits=%d, " +
						"first=%b)", getOrdinal(), getCurrent().getId(),
						getPrevious().map(Revision::getId).orElse(null),
						getCommits().size(), isFirst());
			}
		};
	}

	/**
	 * Creates a new {@link VCSFile}.
	 *
	 * @param relativePath
	 * 		The relative path of the file to create.
	 * @param revision
	 * 		The revision of the file to create.
	 * @param engine
	 * 		The engine of the file to create.
	 * @return
	 * 		The created {@link VCSFile} instance.
	 * @throws NullPointerException
	 * 		If any of the given arguments is {@code null}.
	 */
	default VCSFile createVCSFile(final String relativePath,
			final Revision revision, final VCSEngine engine)
			throws NullPointerException, IllegalArgumentException {
		Validate.notNull(relativePath);
		Validate.notNull(revision);
		Validate.notNull(engine);
		return new VCSFile() {

			/**
			 * Caches the contents of this file (see {@link #readAllBytes()}).
			 * Use a {@link SoftReference} to avoid an {@link OutOfMemoryError}
			 * due to hundrets of thousands of cached file contents.
			 */
			private SoftReference<byte[]> contentsCache =
					new SoftReference<>(null);

			/**
			 * Caches the charset of this file (see {@link #guessCharset()}).
			 */
			private AtomicReference<Charset> charsetCache = null;

			/**
			 * Caches the information whether this file is binary.
			 */
			private AtomicBoolean binary = null;

			@Override
			public String getRelativePath() {
				return relativePath;
			}

			@Override
			public Revision getRevision() {
				return revision;
			}

			@Override
			public VCSEngine getVCSEngine() {
				return engine;
			}

			@Override
			public byte[] readAllBytes() throws IOException {
				byte[] bytes = contentsCache.get();
				if (bytes == null) {
					bytes = VCSFile.super.readAllBytes();
					contentsCache = new SoftReference<>(bytes);
				}
				return bytes;
			}

			@Override
			public Optional<Charset> guessCharset() throws IOException {
				if (charsetCache == null) {
					charsetCache = new AtomicReference<>(
							VCSFile.super.guessCharset().orElse(null));
				}
				return Optional.ofNullable(charsetCache.get());
			}

			@Override
			public boolean isBinary() throws IOException {
				if (binary == null) {
					binary = new AtomicBoolean(VCSFile.super.isBinary());
				}
				return binary.get();
			}

			@Override
			public String toString() {
				return String.format("VCSFile(relativePath=%s, revision=%s)",
						getRelativePath(), getRevision().getId());
			}
		};
	}

	/**
	 * Creates a new {@link VCSFile.Position}.
	 *
	 * @param file
	 * 		The referenced file of the position to create.
	 * @param line
	 * 		The line of the position to create.
	 * @param column
	 * 		The column of the position to create.
	 * @param offset
	 * 		The offset of the position to create.
	 * @param lineOffset
	 * 		The line offset of the position to create.
	 * @param tabSize
	 * 		The tab size of the position to create.
	 * @param engine
	 * 		The engine of the position to create.
	 * @throws NullPointerException
	 * 		If {@code file} or {@code engine} is {@code null}.
	 * @throws IllegalArgumentException
	 * 		If {@code line < 1}, {@code column < 1}, {@code offset < 0},
	 * 		{@code lineOffset < 0}, or {@code tabSize < 1}.
	 */
	default VCSFile.Position createPosition(final VCSFile file,
			final int line, final int column, final int offset,
			final int lineOffset, final int tabSize, final VCSEngine engine)
			throws NullPointerException, IllegalArgumentException {
		Validate.notNull(file);
		Validate.notNull(engine);
		Validate.isPositive(line, "line < 1");
		Validate.isPositive(column, "column < 1");
		Validate.notNegative(offset, "offset < 0");
		Validate.notNegative(lineOffset, "line offset < 0");
		Validate.isPositive(tabSize, "tab size < 1");

		return new VCSFile.Position() {
			@Override
			public VCSFile getFile() {
				return file;
			}

			@Override
			public int getLine() {
				return line;
			}

			@Override
			public int getColumn() {
				return column;
			}

			@Override
			public int getOffset() {
				return offset;
			}

			@Override
			public int getLineOffset() {
				return lineOffset;
			}

			@Override
			public int getTabSize() {
				return tabSize;
			}

			@Override
			public VCSEngine getVCSEngine() {
				return engine;
			}

			@Override
			public String toString() {
				return String.format("Position(file=%s, line=%d, column=%d, " +
						"offset=%d, lineOffset=%d, tabSize=%d)",
						getFile().toString(), getLine(), getColumn(), getOffset(),
						getLineOffset(), getTabSize());
			}
		};
	}

	/**
	 * Creates a new {@link VCSFile.Range}.
	 *
	 * @param begin
	 * 		The begin position of the range to create.
	 * @param end
	 * 		The end position of the range to create.
	 * @param engine
	 * 		The engine of the position to create.
	 * @throws NullPointerException
	 * 		If any of the given arguments is {@code null}.
	 * @throws IllegalArgumentException
	 * 		If {@code begin} and {@code end} reference different files,
	 * 		or if {@code begin} is after {@code end}.
	 */
	default VCSFile.Range createRange(final VCSFile.Position begin,
		  	final VCSFile.Position end, final VCSEngine engine)
			throws NullPointerException, IllegalArgumentException {
		Validate.notNull(begin);
		Validate.notNull(end);
		Validate.notNull(engine);
		Validate.isEqualTo(begin.getFile(), end.getFile(),
				"Begin and end position reference different files.");
		Validate.isTrue(begin.getOffset() <= end.getOffset(),
				"Begin must not be after end.");

		return new VCSFile.Range() {
			@Override
			public VCSFile.Position getBegin() {
				return begin;
			}

			@Override
			public VCSFile.Position getEnd() {
				return end;
			}

			@Override
			public VCSFile getFile() {
				return begin.getFile();
			}

			@Override
			public VCSEngine getVCSEngine() {
				return engine;
			}

			@Override
			public String toString() {
				return String.format("Range(begin=%s, end=%s)",
						getBegin().toString(), getEnd().toString());
			}
		};
	}
}
