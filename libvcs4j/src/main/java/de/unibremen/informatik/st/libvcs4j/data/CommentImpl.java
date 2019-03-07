package de.unibremen.informatik.st.libvcs4j.data;

import de.unibremen.informatik.st.libvcs4j.Issue;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * Pojo implementation of {@link Issue.Comment}.
 */
@Getter
@Setter
@ToString(doNotUseGetters = true)
public class CommentImpl extends ITModelElementImpl implements Issue.Comment {

	/**
	 * The author of a comment.
	 */
	@NonNull
	private String author;

	/**
	 * The message of a comment
	 */
	@NonNull
	private String message;

	/**
	 * The datetime of a comment.
	 */
	@NonNull
	private LocalDateTime dateTime;
}
