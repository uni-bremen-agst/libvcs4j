package de.unibremen.informatik.st.libvcs4j.pmd;

import de.unibremen.informatik.st.libvcs4j.VCSFile;
import de.unibremen.informatik.st.libvcs4j.Validate;

/**
 * A readonly representation of a violation detected by PMD.
 */
public final class PMDViolation {

	/**
	 * The range of this violation.
	 */
	private final VCSFile.Range range;

	/**
	 * The PMD rule that triggered the detected violation.
	 */
	private final String rule;

	/**
	 * The PMD rule set containing {@link #rule}.
	 */
	private final String ruleSet;

	/**
	 * Creates a new violation with given values.
	 *
	 * @param pRange
	 * 		The range of the violation to create.
	 * @param pRule
	 * 		The rule that triggered the detected violation.
	 * @param pRuleSet
	 * 		The rule set containing {@code pRule}.
	 * @throws NullPointerException
	 * 		If any of the given arguments is {@code null}.
	 */
	public PMDViolation(final VCSFile.Range pRange, final String pRule,
			final String pRuleSet) throws NullPointerException,
			IllegalArgumentException {
		range = Validate.notNull(pRange);
		rule = Validate.notNull(pRule);
		ruleSet = Validate.notNull(pRuleSet);
	}

	/**
	 * Copy constructor.
	 *
	 * @param pOther
	 * 		The violation to copy.
	 * @throws NullPointerException
	 * 		If {@code pOther} is {@code null}.
	 */
	public PMDViolation(final PMDViolation pOther)
			throws NullPointerException {
		this(Validate.notNull(pOther).getRange(),
				pOther.getRule(), pOther.getRuleSet());
	}

	/**
	 * Returns the range of this violation.
	 *
	 * @return
	 * 		The range of this violation.
	 */
	public VCSFile.Range getRange() {
		return range;
	}

	/**
	 * Returns the rule that triggered this violation.
	 *
	 * @return
	 * 		The rule that triggered this violation.
	 */
	public String getRule() {
		return rule;
	}

	/**
	 * Returns rule set containing {@link #getRule()}.
	 *
	 * @return
	 * 		The rule set containing {@link #getRule()},
	 */
	public String getRuleSet() {
		return ruleSet;
	}
}
