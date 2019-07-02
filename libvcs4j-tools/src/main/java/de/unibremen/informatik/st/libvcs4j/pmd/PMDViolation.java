package de.unibremen.informatik.st.libvcs4j.pmd;

import de.unibremen.informatik.st.libvcs4j.VCSFile;
import lombok.NonNull;
import lombok.Value;

/**
 * A readonly representation of a violation detected by PMD.
 */
@Value
public final class PMDViolation {

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
}
