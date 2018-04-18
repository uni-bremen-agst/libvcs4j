package de.unibremen.informatik.st.libvcs4j.data;

import de.unibremen.informatik.st.libvcs4j.Issue;
import org.apache.commons.lang3.Validate;

import java.time.LocalDateTime;

public class CommentImpl implements Issue.Comment {

	private String author;
	private String message;
	private LocalDateTime dateTime;

	@Override
	public String getAuthor() {
		return author;
	}

	public void setAuthor(final String pAuthor) {
		author = Validate.notNull(pAuthor);
	}

	@Override
	public String getMessage() {
		return message;
	}

	public void setMessage(final String pMessage) {
		message = Validate.notNull(pMessage);
	}

	@Override
	public LocalDateTime getDateTime() {
		return dateTime;
	}

	public void setDateTime(final LocalDateTime pDateTime) {
		dateTime = Validate.notNull(pDateTime);
	}
}
