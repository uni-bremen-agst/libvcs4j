package de.unibremen.informatik.st.libvcs4j.exception;

public class IllegalTargetException extends LibVCS4jParameterException {

	private IllegalTargetException(final String pMessage) {
		super(pMessage);
	}

	public static void isTrue(
			final boolean pCondition, final String pMessage,
			final Object... pValues) throws IllegalIntervalException {
		LibVCS4jParameterException.isTrue(IllegalTargetException.class,
				pCondition, pMessage, pValues);
	}
}
