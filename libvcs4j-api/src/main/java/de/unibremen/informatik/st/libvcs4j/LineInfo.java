package de.unibremen.informatik.st.libvcs4j;

import java.time.LocalDateTime;

/**
 * Stores the commit information of a single line of a {@link VCSFile}.
 */
public interface LineInfo extends VCSModelElement {

	/**
	 * Returns the commit id of this line.
	 *
	 * @see Commit#getId()
	 *
	 * @return
	 * 		The commit id of this line.
	 */
	String getId();

	/**
	 * Returns the author of this line.
	 *
	 * @see Commit#getAuthor()
	 *
	 * @return
	 * 		The author of this line.
	 */
	String getAuthor();

	/**
	 * Returns the commit message of this line.
	 *
	 * @see Commit#getMessage()
	 *
	 * @return
	 * 		The commit message of this line.
	 */
	String getMessage();

	/**
	 * Returns the datetime of this line.
	 *
	 * @see Commit#getDateTime()
	 *
	 * @return
	 * 		The datetime of this line.
	 */
	LocalDateTime getDateTime();

	/**
	 * Returns the line number (1 origin).
	 *
	 * @return
	 * 		The line number (1 origin).
	 */
	int getLine();

	/**
	 * Returns the content of this line.
	 *
	 * @return
	 * 		The content of this line.
	 */
	String getContent();

	/**
	 * Returns the file this line belongs to.
	 *
	 * @return
	 * 		The file this line change belongs to.
	 */
	VCSFile getFile();
}
