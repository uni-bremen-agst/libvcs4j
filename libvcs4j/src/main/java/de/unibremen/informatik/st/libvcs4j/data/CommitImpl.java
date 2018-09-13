package de.unibremen.informatik.st.libvcs4j.data;

import de.unibremen.informatik.st.libvcs4j.Commit;
import de.unibremen.informatik.st.libvcs4j.FileChange;
import de.unibremen.informatik.st.libvcs4j.Issue;
import de.unibremen.informatik.st.libvcs4j.Validate;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Pojo Implementation of {@link Commit}.
 */
@Getter
@Setter
public class CommitImpl extends VCSModelElementImpl implements Commit {

	/**
	 * The id of a commit.
	 */
	@NonNull
	private String id;

	/**
	 * The author of a commit.
	 */
	@NonNull
	private String author;

	/**
	 * The message of a commit.
	 */
	@NonNull
	private String message;

	/**
	 * The datetime of a commit.
	 */
	@NonNull
	private LocalDateTime dateTime;

	/**
	 * The parents of a commit.
	 */
	@NonNull
	private List<String> parentIds = Collections.emptyList();

	/**
	 * The file changes of a commit.
	 */
	@NonNull
	private List<FileChange> fileChanges = Collections.emptyList();

	/**
	 * The issues of a commit.
	 */
	@NonNull
	private List<Issue> issues = Collections.emptyList();

	@Override
	public List<String> getParentIds() {
		return new ArrayList<>(parentIds);
	}

	/**
	 * Sets the parent ids of this commit.
	 *
	 * @param pParentIds
	 * 		The ids to set.
	 * @throws NullPointerException
	 * 		If {@code pParentIds} is {@code null}.
	 * @throws IllegalArgumentException
	 * 		If {@code pParents} contains {@code null}.
	 */
	public void setParentIds(final List<String> pParentIds) {
		Validate.noNullElements(pParentIds);
		parentIds = new ArrayList<>(pParentIds);
	}

	@Override
	public List<FileChange> getFileChanges() {
		return new ArrayList<>(fileChanges);
	}

	/**
	 * Sets the file changes of this commit.
	 *
	 * @param pFileChanges
	 * 		The file changes to set.
	 * @throws NullPointerException
	 * 		If {@code pFileChanges} is {@code null}.
	 * @throws IllegalArgumentException
	 * 		If {@code pFileChanges} contains {@code null}.
	 */
	public void setFileChanges(final List<FileChange> pFileChanges) {
		Validate.noNullElements(pFileChanges);
		fileChanges = new ArrayList<>(pFileChanges);
	}

	@Override
	public List<Issue> getIssues() {
		return new ArrayList<>(issues);
	}

	/**
	 * Sets the issues of this commit.
	 *
	 * @param pIssues
	 * 		The issues to set.
	 * @throws NullPointerException
	 * 		If {@code pIssues} is {@code null}.
	 * @throws IllegalArgumentException
	 * 		If {@code pIssues} contains {@code null}.
	 */
	public void setIssues(final List<Issue> pIssues) {
		Validate.noNullElements(pIssues);
		issues = new ArrayList<>(pIssues);
	}
}
