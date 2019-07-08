package de.unibremen.informatik.st.libvcs4j;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * This factory is responsible for instantiating model elements implementing
 * the {@link ITModelElement} interface. The default methods provide a sane
 * default implementation.
 */
public interface ItModelFactory {

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
	 * Creates a new {@link Issue.Comment}.
	 *
	 * @param author
	 * 		The author of the comment to create.
	 * @param message
	 * 		The message of the comment to create.
	 * @param dateTime
	 * 		The datetime of the comment to create.
	 * @param engine
	 * 		The engine of the comment to create.
	 * @return
	 * 		The created {@link Issue.Comment} instance.
	 * @throws NullPointerException
	 * 		If any of the given arguments is {@code null}.
	 */
	default Issue.Comment createComment(final String author,
			final String message, final LocalDateTime dateTime,
			final ITEngine engine) throws NullPointerException {
		Validate.notNull(author);
		Validate.notNull(message);
		Validate.notNull(dateTime);
		Validate.notNull(engine);
		return new Issue.Comment() {
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
			public ITEngine getITEngine() {
				return engine;
			}
		};
	}

	/**
	 * Creates a new {@link Issue}. List arguments are flat copied. If any of
	 * the given lists is {@code null}, an empty list is used as fallback.
	 * {@code null} values are filtered out.
	 *
	 * @param id
	 * 		The id of the issue to create.
	 * @param author
	 * 		The author of the issue to create.
	 * @param title
	 * 		The title of the issue to create.
	 * @param dateTime
	 * 		The datetime of the issue to create.
	 * @param comments
	 * 		The commits of the issue to create.
	 * @param engine
	 * 		The engine of the issue to create.
	 * @return
	 * 		The created {@link Issue} instance.
	 * @throws NullPointerException
	 * 		If {@code id}, {@code author}, {@code title}, {@code dateTime}, or
	 * 		{@code engine} is {@code null}.
	 */
	default Issue createIssue(final String id, final String author,
			final String title, final LocalDateTime dateTime,
			final List<Issue.Comment> comments, final ITEngine engine)
			throws NullPointerException {
		Validate.notNull(id);
		Validate.notNull(author);
		Validate.notNull(title);
		Validate.notNull(dateTime);
		Validate.notNull(engine);
		final List<Issue.Comment> _comments = createCopy(comments);
		return new Issue() {
			@Override
			public String getId() {
				return id;
			}

			@Override
			public String getAuthor() {
				return author;
			}

			@Override
			public String getTitle() {
				return title;
			}

			@Override
			public LocalDateTime getDateTime() {
				return dateTime;
			}

			@Override
			public List<Comment> getComments() {
				return _comments;
			}

			@Override
			public ITEngine getITEngine() {
				return engine;
			}
		};
	}
}
