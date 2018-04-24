package de.unibremen.informatik.st.libvcs4j.exception;

public class IllegalRepositoryException extends LibVCS4jParameterException {

	private IllegalRepositoryException(final String pMessage) {
		super(pMessage);
	}

	public static void isTrue(
			final boolean pCondition, final String pMessage,
			final Object... pValues) throws IllegalIntervalException {
		LibVCS4jParameterException.isTrue(IllegalRepositoryException.class,
				pCondition, pMessage, pValues);
	}
}
