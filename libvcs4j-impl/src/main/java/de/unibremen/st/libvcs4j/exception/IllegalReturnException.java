package de.unibremen.st.libvcs4j.exception;

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

	public static void notNull(
			final Object pObject)
				throws IllegalReturnException {
		if (pObject == null) {
			throw new IllegalReturnException("Unexpected null value");
		}
	}

	public static void noNullElements(
			final Collection<?> pCollection)
				throws IllegalReturnException {
		notNull(pCollection);
		if (pCollection.contains(null)) {
			throw new IllegalReturnException(
					"Unexpected null element in collection");
		}
	}

	public static void equals(
			final Object pExpected,
			final Object pActual)
				throws IllegalReturnException {
		if (pExpected == null && pActual != null) {
			throw new IllegalReturnException(String.format(
					"Unexpected value: expected 'null', actual '%s'",
					pActual));
		} else if (pExpected != null && pActual == null) {
			throw new IllegalReturnException(String.format(
					"Unexpected value: expected '%s', actual 'null'",
					pExpected));
		} else if (pExpected != null /* && pActual != null > implicit */) {
			if (!pExpected.equals(pActual)) {
				throw new IllegalReturnException(String.format(
						"Unexpected value: expected '%s', actual '%s'",
						pExpected, pActual));
			}
		}
	}

	public static void isTrue(
			final boolean pCondition,
			final String pMessage,
			final Object... pValues)
				throws IllegalReturnException {
		if (!pCondition) {
			final String message = pMessage == null || pValues == null
					? "Condition did not hold"
					: String.format(pMessage, pValues);
			throw new IllegalReturnException(message);
		}
	}

	public static void isFalse(
			final boolean pCondition,
			final String pMessage,
			final Object... pValues)
				throws IllegalReturnException {
		isTrue(!pCondition, pMessage, pValues);
	}
}
