package de.unibremen.informatik.st.libvcs4j.exception;

public class IllegalIntervalException extends LibVCS4jParameterException {

	private IllegalIntervalException(final String pMessage) {
		super(pMessage);
	}

	public static void isTrue(
			final boolean pCondition, final String pMessage,
			final Object... pValues) throws IllegalIntervalException {
		LibVCS4jParameterException.isTrue(IllegalIntervalException.class,
				pCondition, pMessage, pValues);
	}
}
