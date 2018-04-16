package de.unibremen.st.libvcs4j.exception;

public class IllegalRepositoryException extends IllegalArgumentException {

	private IllegalRepositoryException(final String pMessage) {
		super(pMessage);
	}

	public static void isTrue(
			final boolean pCondition, final String pMessage,
			final Object... pValues) throws IllegalRepositoryException {
		if (!pCondition) {
			final String message = pMessage == null || pValues == null
					? "Illegal repository"
					: String.format(pMessage, pValues);
			throw new IllegalRepositoryException(message);
		}
	}
}
