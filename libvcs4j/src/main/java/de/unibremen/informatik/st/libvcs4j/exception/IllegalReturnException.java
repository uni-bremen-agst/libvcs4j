package de.unibremen.informatik.st.libvcs4j.exception;

import java.util.Collection;

/**
 * A {@link RuntimeException} to indicate that a subclass or implementation
 * returned an invalid value. You can see this exception as the counterpart to
 * {@link IllegalArgumentException}.
 */
public class IllegalReturnException extends RuntimeException {

	private IllegalReturnException(final String pMessage) {
		super(pMessage);
	}

	public static void notNull(final Object pObject)
			throws IllegalReturnException {
		if (pObject == null) {
			throw new IllegalReturnException("Unexpected null value");
		}
	}

	public static void noNullElements(final Collection<?> pCollection)
			throws IllegalReturnException {
		notNull(pCollection);
		if (pCollection.contains(null)) {
			throw new IllegalReturnException(
					"Unexpected null element in collection");
		}
	}
}
