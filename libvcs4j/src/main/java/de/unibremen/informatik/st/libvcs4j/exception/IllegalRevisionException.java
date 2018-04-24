package de.unibremen.informatik.st.libvcs4j.exception;

public class IllegalRevisionException extends LibVCS4jParameterException {

	public IllegalRevisionException(final String pMessage) {
		super(pMessage);
	}

	public static void isTrue(
			final boolean pCondition, final String pMessage,
			final Object... pValues) throws IllegalIntervalException {
		LibVCS4jParameterException.isTrue(IllegalRevisionException.class,
				pCondition, pMessage, pValues);
	}
}
