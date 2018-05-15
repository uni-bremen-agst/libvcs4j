package de.unibremen.informatik.st.libvcs4j.data;

import de.unibremen.informatik.st.libvcs4j.Issue;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.apache.commons.lang3.Validate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Pojo implementation of {@link Issue}.
 */
@Getter
@Setter
public class IssueImpl extends ITModelElementImpl implements Issue {

	/**
	 * The id of an issue.
	 */
	@NonNull
	private String id;

	/**
	 * The author of an issue.
	 */
	@NonNull
	private String author;

	/**
	 * The title of an issue.
	 */
	@NonNull
	private String title;

	/**
	 * The datetime of an issue.
	 */
	@NonNull
	private LocalDateTime dateTime;

	/**
	 * The comments of an issue.
	 */
	@NonNull
	private List<Comment> comments = Collections.emptyList();

	@Override
	public List<Comment> getComments() {
		return new ArrayList<>(comments);
	}

	/**
	 * Sets the comments of this issue.
	 *
	 * @param pComments
	 * 		The comments to set.
	 * @throws NullPointerException
	 * 		If {@code pComments} is {@code null}.
	 * @throws IllegalArgumentException
	 * 		If {@code pComments} contains {@code null}.
	 */
	public void setComments(final List<Comment> pComments) {
		Validate.noNullElements(pComments);
		comments = new ArrayList<>(pComments);
	}
}
