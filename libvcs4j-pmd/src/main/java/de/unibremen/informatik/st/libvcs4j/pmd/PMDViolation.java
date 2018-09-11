package de.unibremen.informatik.st.libvcs4j.pmd;

import de.unibremen.informatik.st.libvcs4j.VCSFile;
import de.unibremen.informatik.st.libvcs4j.VCSFile.Position;
import org.apache.commons.lang3.Validate;

/**
 * A readonly representation of a violation detected by PMD.
 */
public final class PMDViolation {

	/**
	 * The file containing the detected violation.
	 */
	private VCSFile file;

	/**
	 * The begin position.
	 */
	private Position begin;

	/**
	 * The end position.
	 */
	private Position end;

	/**
	 * The rule that triggered the detected violation.
	 */
	private String rule;

	/**
	 * The rule set containing {@link #rule}.
	 */
	private String ruleSet;

	/**
	 * Creates a new violation with given values.
	 *
	 * @param pFile
	 * 		The file containing the violation.
	 * @param pBegin
	 * 		The begin position.
	 * @param pEnd
	 * 		The end position.
	 * @param pRule
	 * 		The rule that triggered the detected violation.
	 * @param pRuleSet
	 * 		The rule set containing {@code pRule}.
	 * @throws NullPointerException
	 * 		If any of the given arguments is {@code null}.
	 * @throws IllegalArgumentException
	 * 		If begin line is greater than end line or if begin line is equal to
	 * 		end line and begin column is greater than end column.
	 */
	public PMDViolation(final VCSFile pFile, final Position pBegin,
			final Position pEnd, final String pRule, final String pRuleSet)
			throws NullPointerException, IllegalArgumentException {
		file = Validate.notNull(pFile);
		begin = Validate.notNull(pBegin);
		end = Validate.notNull(pEnd);
		rule = Validate.notNull(pRule);
		ruleSet = Validate.notNull(pRuleSet);
		if (pBegin.getLine() > pEnd.getLine()) {
			throw new IllegalArgumentException(String.format(
					"Begin line (%s) > end line (%s)",
					pBegin.getLine(), pEnd.getLine()));
		} else if (pBegin.getLine() == pEnd.getLine() &&
				pBegin.getColumn() > pEnd.getColumn()) {
			throw new IllegalArgumentException(String.format(
					"Begin column (%s) > end column (%s)",
					pBegin.getColumn(), pEnd.getColumn()));
		}
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
		this(Validate.notNull(pOther).file, pOther.begin,
				pOther.end, pOther.rule, pOther.ruleSet);
	}

	/**
	 * Returns the file containing this violation.
	 *
	 * @return
	 * 		The file containing this violation.
	 */
	public VCSFile getFile() {
		return file;
	}

	/**
	 * Returns a deep copy of the begin position of this violation.
	 *
	 * @return
	 * 		A deep copy of the begin position of this violation.
	 */
	public Position getBegin() {
		return begin;
	}

	/**
	 * Returns a deep copy of the end position of this violation.
	 *
	 * @return
	 * 		A deep copy of the end position of this violation.
	 */
	public Position getEnd() {
		return end;
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
