package de.unibremen.informatik.st.libvcs4j.exception;

public class IllegalRevisionException extends IllegalArgumentException {

	private IllegalRevisionException(final String pMessage) {
		super(pMessage);
	}

	public static void isTrue(
			final boolean pCondition, final String pMessage,
			final Object... pValues) throws IllegalTargetException {
		if (!pCondition) {
			final String message = pMessage == null || pValues == null
					? "Illegal revision"
					: String.format(pMessage, pValues);
			throw new IllegalRevisionException(message);
		}
	}
}
