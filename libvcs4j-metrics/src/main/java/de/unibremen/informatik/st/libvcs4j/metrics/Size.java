package de.unibremen.informatik.st.libvcs4j.metrics;

import de.unibremen.informatik.st.libvcs4j.Validate;

/**
 * Stores different size metrics.
 */
public class Size {

	/**
	 * An empty (default) size.
	 */
	public static final Size EMPTY_SIZE = new Size(0, 0, 0, 0, 0, 0);

	/**
	 * Lines of code including comments and whitespaces.
	 */
	private final int LOC;

	/**
	 * Lines of code excluding comments and whitespaces.
	 */
	private final int SLOC;

	/**
	 * Lines of comments.
	 */
	private final int CLOC;

	/**
	 * Number of tokens including comments.
	 */
	private final int NOT;

	/**
	 * Number of tokens excluding comments.
	 */
	private final int SNOT;

	/**
	 * Number of comment tokens.
	 */
	private final int CNOT;

	/**
	 * Creates a new size with given values.
	 *
	 * @param pLOC
	 * 		Lines of code including comments and whitespaces.
	 * @param pSLOC
	 * 		Lines of code excluding comments and whitespaces.
	 * @param pCLOC
	 * 		Lines of comments.
	 * @param pNOT
	 * 		Number of tokens including comments.
	 * @param pSNOT
	 * 		Number of tokens excluding comments.
	 * @param pCNOT
	 * 		Number of comment tokens.
	 * @throws IllegalArgumentException
	 * 		If any of the given values is negative.
	 */
	public Size(final int pLOC, final int pSLOC, final int pCLOC,
				final int pNOT, final int pSNOT, final int pCNOT)
			throws IllegalArgumentException {
		LOC =  Validate.notNegative(pLOC,  "LOC < 0");
		SLOC = Validate.notNegative(pSLOC, "SLOC < 0");
		CLOC = Validate.notNegative(pCLOC, "CLOC < 0");
		NOT =  Validate.notNegative(pNOT,  "NOT < 0");
		SNOT = Validate.notNegative(pSNOT, "SNOT < 0");
		CNOT = Validate.notNegative(pCNOT, "CNOT < 0");
	}

	/**
	 * Copy constructor.
	 *
	 * @param pOther
	 * 		The size to copy.
	 * @throws NullPointerException
	 * 		If {@code pOther} is {@code null}.
	 */
	public Size(final Size pOther) throws NullPointerException {
		this(pOther.LOC, pOther.SLOC, pOther.CLOC,
				pOther.NOT, pOther.SNOT, pOther.CNOT);
	}

	/**
	 * Returns the lines of code including comments and whitespaces
	 *
	 * @return
	 *      Lines of code including comments and whitespaces
	 */
	public int getLOC() {
		return LOC;
	}

	/**
	 * Returns the lines of code excluding comments and whitespaces.
	 *
	 * @return
	 *      Lines of code excluding comments and whitespaces.
	 */
	public int getSLOC() {
		return SLOC;
	}

	/**
	 * Returns the lines of comments.
	 *
	 * @return
	 *      Lines of comments.
	 */
	public int getCLOC() {
		return CLOC;
	}

	/**
	 * Returns the number of tokens including comments.
	 *
	 * @return
	 *      Number of tokens including comments.
	 */
	public int getNOT() {
		return NOT;
	}

	/**
	 * Returns the number of tokens excluding comments.
	 *
	 * @return
	 *      Number of tokens excluding tokens.
	 */
	public int getSNOT() {
		return SNOT;
	}

	/**
	 * Returns the number of comment tokens.
	 *
	 * @return
	 *      Number of comment tokens.
	 */
	public int getCNOT() {
		return CNOT;
	}

	/**
	 * Returns the sum of this and the given size.
	 *
	 * @param size
	 * 		The other size.
	 * @return
	 * 		A new instance containing the sum of this and the given size.
	 */
	public Size add(final Size size) {
		return new Size(
				LOC  + size.LOC,
				SLOC + size.SLOC,
				CLOC + size.CLOC,
				NOT  + size.NOT,
				SNOT + size.SNOT,
				CNOT + size.CNOT);
	}
}
