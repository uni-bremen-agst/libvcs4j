package de.unibremen.st.libvcs4j.data;

import de.unibremen.st.libvcs4j.Commit;
import de.unibremen.st.libvcs4j.Revision;
import de.unibremen.st.libvcs4j.Version;
import org.apache.commons.lang3.Validate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementation for {@link Version}.
 */
public class VersionImpl implements Version {

	private int ordinal = 0;
	private Revision revision;
	private Revision predecessorRevision;
	private List<Commit> commits;

	@Override
	public int getOrdinal() {
		return ordinal;
	}

	public void setOrdinal(final int pOrdinal) {
		Validate.isTrue(pOrdinal >= 0, "%d < 0", pOrdinal);
		ordinal = pOrdinal;
	}

	@Override
	public Revision getRevision() {
		return revision;
	}

	public void setRevision(final Revision pRevision) {
		revision = Validate.notNull(pRevision);
	}

	@Override
	public Optional<Revision> getPredecessorRevision() {
		return Optional.ofNullable(predecessorRevision);
	}

	public void setPredecessorRevision(final Revision pPredecessorRevision) {
		predecessorRevision = pPredecessorRevision;
	}

	@Override
	public List<Commit> getCommits() {
		return new ArrayList<>(commits);
	}

	public void setCommits(final List<Commit> pCommits) {
		Validate.noNullElements(pCommits);
		Validate.notEmpty(pCommits);
		commits = new ArrayList<>(pCommits);
	}
}
