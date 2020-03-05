package de.unibremen.informatik.st.libvcs4j.pmd;

import de.unibremen.informatik.st.libvcs4j.VCSFile;
import de.unibremen.informatik.st.libvcs4j.mapping.Mappable;
import lombok.NonNull;
import lombok.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * A readonly representation of a violation detected by PMD.
 */
@Value
public final class PMDViolation implements Mappable<String> {

	/**
	 * The range of this violation.
	 */
	@NonNull
	private final VCSFile.Range range;

	/**
	 * The PMD rule that triggered the detected violation.
	 */
	@NonNull
	private final String rule;

	/**
	 * The PMD rule set containing {@link #rule}.
	 */
	@NonNull
	private final String ruleSet;

	@Override
	public List<VCSFile.Range> getRanges() {
		final List<VCSFile.Range> ranges = new ArrayList<>();
		ranges.add(range);
		return ranges;
	}

	@Override
	public Optional<String> getMetadata() {
		return Optional.of(rule);
	}
}
