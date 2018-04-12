package de.unibremen.st.libvcs4j;

/**
 * Represents different size metrics.
 */
@SuppressWarnings("unused")
public interface Size {

	/**
	 * Returns the lines of code including comments and whitespaces
	 *
	 * @return
	 *      Lines of code including comments and whitespaces
	 */
	int getLOC();

	/**
	 * Returns the lines of code excluding comments and whitespaces.
	 *
	 * @return
	 *      Lines of code excluding comments and whitespaces.
	 */
	int getSLOC();

	/**
	 * Returns the lines of comments.
	 *
	 * @return
	 *      Lines of comments.
	 */
	int getCLOC();

	/**
	 * Returns the number of tokens including comments.
	 *
	 * @return
	 *      Number of tokens including comments.
	 */
	int getNOT();

	/**
	 * Returns the number of tokens excluding comments.
	 *
	 * @return
	 *      Number of tokens excluding tokens.
	 */
	int getSNOT();

	/**
	 * Returns the number of comment tokens.
	 *
	 * @return
	 *      Number of comment tokens.
	 */
	int getCNOT();
}
