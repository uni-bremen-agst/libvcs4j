package de.unibremen.informatik.st.libvcs4j;

import java.io.IOException;

/**
 * Is thrown by {@link VCSFile#readContent()} if the corresponding file is
 * binary (see {@link VCSFile#isBinary()}).
 */
public class BinaryFileException extends IOException {

	/**
	 * Creates a new instance with given detail message.
	 *
	 * @param message
	 * 		The detail message (which is saved for later retrieval
	 * 		by the {@link #getMessage()} method)
	 */
	public BinaryFileException(final String message) {
		super(message);
	}
}
