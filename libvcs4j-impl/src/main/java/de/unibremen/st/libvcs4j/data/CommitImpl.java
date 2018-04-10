package de.unibremen.st.libvcs4j.data;

import de.unibremen.st.libvcs4j.Commit;
import de.unibremen.st.libvcs4j.FileChange;
import de.unibremen.st.libvcs4j.Issue;
import org.apache.commons.lang3.Validate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Implementation for {@link Commit}.
 */
public class CommitImpl implements Commit {

	private String id;
	private String author;
	private String message;
	private LocalDateTime dateTime;
	private List<String> parentIds = Collections.emptyList();
	private List<FileChange> fileChanges = Collections.emptyList();
	private List<Issue> issues = Collections.emptyList();

	@Override
	public String getId() {
		return id;
	}

	public void setId(final String pId) {
		id = Validate.notNull(pId);
	}

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

	@Override
	public List<String> getParentIds() {
		return new ArrayList<>(parentIds);
	}

	public void setParentIds(List<String> pParentIds) {
		Validate.noNullElements(pParentIds);
		parentIds = new ArrayList<>(pParentIds);
	}

	@Override
	public List<FileChange> getFileChanges() {
		return new ArrayList<>(fileChanges);
	}

	public void setFileChanges(final List<FileChange> pFileChanges) {
		Validate.noNullElements(pFileChanges);
		fileChanges = new ArrayList<>(pFileChanges);
	}

	@Override
	public List<Issue> getIssues() {
		return new ArrayList<>(issues);
	}

	public void setIssues(final List<Issue> pIssues) {
		Validate.noNullElements(pIssues);
		issues = new ArrayList<>(pIssues);
	}
}
