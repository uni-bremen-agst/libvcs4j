package de.unibremen.informatik.st.libvcs4j.data;

import de.unibremen.informatik.st.libvcs4j.Commit;
import de.unibremen.informatik.st.libvcs4j.Revision;
import de.unibremen.informatik.st.libvcs4j.RevisionRange;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.apache.commons.lang3.Validate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Pojo implementation of {@link RevisionRange}.
 */
public class RevisionRangeImpl extends VCSModelElementImpl
		implements RevisionRange {

	/**
	 * The ordinal of a revision range.
	 */
	@Getter
	private int ordinal = 1;

	/**
	 * The "from" revision of a revision range.
	 */
	@Getter
	@Setter
	@NonNull
	private Revision revision;

	/**
	 * The "to" revision of a revision range.
	 */
	private Revision predecessorRevision;

	/**
	 * The commits of a revision range.
	 */
	private List<Commit> commits;

	/**
	 * Sets the ordinal of this revision range.
	 *
	 * @param pOrdinal
	 * 		The ordinal to set.
	 * @throws IllegalArgumentException
	 * 		If {@code pOrdinal <= 0}.
	 */
	public void setOrdinal(final int pOrdinal) {
		Validate.isTrue(pOrdinal >= 1, "Ordinal (%d) < 1", pOrdinal);
		ordinal = pOrdinal;
	}

	@Override
	public Optional<Revision> getPredecessorRevision() {
		return Optional.ofNullable(predecessorRevision);
	}

	/**
	 * Sets the "to" revision of this revision range.
	 *
	 * @param pPredecessorRevision
	 * 		The "to" revision to set.
	 */
	public void setPredecessorRevision(final Revision pPredecessorRevision) {
		predecessorRevision = pPredecessorRevision;
	}

	@Override
	public List<Commit> getCommits() {
		return new ArrayList<>(commits);
	}

	/**
	 * Sets the commits of this revision range.
	 *
	 * @param pCommits
	 * 		The commits to set.
	 * @throws NullPointerException
	 * 		If {@code pCommits} is {@code null}.
	 * @throws IllegalArgumentException
	 * 		If {@code pCommits} contains {@code null} or is empty.
	 */
	public void setCommits(final List<Commit> pCommits) {
		Validate.noNullElements(pCommits);
		Validate.notEmpty(pCommits);
		commits = new ArrayList<>(pCommits);
	}
}
