package de.unibremen.informatik.st.libvcs4j.exception;

public class IllegalIntervalException extends IllegalArgumentException {

	private IllegalIntervalException(final String pMessage) {
		super(pMessage);
	}

	public static void isTrue(
			final boolean pCondition, final String pMessage,
			final Object... pValues) throws IllegalIntervalException {
		if (!pCondition) {
			final String message = pMessage == null || pValues == null
					? "Illegal interval"
					: String.format(pMessage, pValues);
			throw new IllegalIntervalException(message);
		}
	}
}
