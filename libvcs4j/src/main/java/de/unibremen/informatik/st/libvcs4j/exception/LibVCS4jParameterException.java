package de.unibremen.informatik.st.libvcs4j.exception;

abstract class LibVCS4jParameterException
		extends IllegalArgumentException {

	@SuppressWarnings("WeakerAccess")
	public LibVCS4jParameterException(final String pMessage) {
		super(pMessage);
	}

	static void isTrue(
			final Class<? extends LibVCS4jParameterException> clazz,
			final boolean pCondition, final String pMessage,
			final Object... pValues) {
		if (!pCondition) {
			final String message = pMessage == null || pValues == null
					? "Illegal repository" : String.format(pMessage, pValues);
			try {
				clazz.getConstructor(String.class).newInstance(message);
			} catch (final Exception e) {
				throw new IllegalStateException(
						"Unable to instantiate exception", e);
			}
		}
	}
}
