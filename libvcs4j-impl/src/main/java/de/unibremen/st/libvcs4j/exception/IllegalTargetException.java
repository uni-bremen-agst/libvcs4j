package de.unibremen.st.libvcs4j.exception;

public class IllegalTargetException extends IllegalArgumentException {

	private IllegalTargetException(final String pMessage) {
		super(pMessage);
	}

	public static void isTrue(
			final boolean pCondition, final String pMessage,
			final Object... pValues) throws IllegalTargetException {
		if (!pCondition) {
			final String message = pMessage == null || pValues == null
					? "Illegal target"
					: String.format(pMessage, pValues);
			throw new IllegalTargetException(message);
		}
	}
}
