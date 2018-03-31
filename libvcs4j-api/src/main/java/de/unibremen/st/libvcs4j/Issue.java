package de.unibremen.st.libvcs4j;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Represents an issue from an issue tracker.
 */
@SuppressWarnings("unused")
public interface Issue {

	/**
	 * Represents a comment attached to an {@link Issue}.
	 */
	interface Comment {

		/**
		 * Returns the author of this comment.
		 *
		 * @return
		 *      The author of this comment.
		 */
		String getAuthor();

		/**
		 * Returns the message of this comment.
		 *
		 * @return
		 *      The message of this comment.
		 */
		String getMessage();

		/**
		 * Returns the datetime of this comment.
		 *
		 * @return
		 *      The datetime of this comment.
		 */
		LocalDateTime getDateTime();
	}

	/**
	 * Returns the id of this issue.
	 *
	 * @return
	 *      The id of this issue.
	 */
	String getId();

	/**
	 * Returns the user who created this issue.
	 *
	 * @return
	 *      The user who created this issue.
	 */
	String getAuthor();

	/**
	 * Returns the title of this issue.
	 *
	 * @return
	 *      The title of this issue.
	 */
	String getTitle();

	/**
	 * Returns the datetime of this issue.
	 *
	 * @return
	 *      The datetime of this issue.
	 */
	LocalDateTime getDateTime();

	/**
	 * Returns the comments of this issue.
	 *
	 * @return
	 *      The comments of this issue.
	 */
	List<Comment> getComments();
}
