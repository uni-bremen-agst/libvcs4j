package de.unibremen.informatik.st.libvcs4j.spoon;

/**
 * A managed exception indicating that an error occurred while building a
 * {@link spoon.reflect.CtModel}.
 */
public class BuildException extends Exception {

	public BuildException() {
	}

	public BuildException(String message) {
		super(message);
	}

	public BuildException(String message, Throwable cause) {
		super(message, cause);
	}

	public BuildException(Throwable cause) {
		super(cause);
	}

	public BuildException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
