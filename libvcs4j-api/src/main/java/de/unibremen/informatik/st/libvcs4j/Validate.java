package de.unibremen.informatik.st.libvcs4j;

import java.util.Collection;

import static java.lang.String.format;

/**
 * Utility class to validate input parameters and the current state. In order
 * to distinguish {@code null} parameters and a parameters containing
 * {@code null} a {@link NullPointerException} and
 * {@link IllegalArgumentException} is thrown respectively.
 */
public class Validate {

	/**
	 * Validates that {@code t} is not {@code null}.
	 *
	 * @param t
	 * 		The value to validate.
	 * @param <T>
	 * 		The type of {@code t}.
	 * @return
	 * 		{@code t}, never {@code null}.
	 * @throws NullPointerException
	 * 		If {@code t} is {@code null}.
	 */
	public static <T> T notNull(final T t) throws NullPointerException {
		if (t == null) {
			throw new NullPointerException();
		}
		return t;
	}

	/**
	 * Validates that {@code t} is not {@code null}.
	 *
	 * @param t
	 * 		The value to validate.
	 * @param message
	 * 		The message of the thrown exception.
	 * @param args
	 * 		The arguments to pass to {@link String#format(String, Object...)}.
	 * @param <T>
	 * 		The type of {@code t}.
	 * @return
	 * 		{@code t}, never {@code null}.
	 * @throws NullPointerException
	 * 		If {@code t} is {@code null}.
	 */
	public static <T> T notNull(final T t, final String message,
			final Object... args) throws NullPointerException {
		if (t == null) {
			throw new NullPointerException(format(message, args));
		}
		return t;
	}

	/**
	 * Validates that {@code t} is not {@code null} and does not contain
	 * {@code null} values.
	 *
	 * @param t
	 * 		The value to validate.
	 * @param <T>
	 * 		The type of {@code t}.
	 * @return
	 * 		{@code t}, never {@code null}.
	 * @throws NullPointerException
	 * 		If {@code t} is {@code null}.
	 * @throws IllegalArgumentException
	 * 		If {@code t} contains {@code null}.
	 */
	public static <T extends Collection<?>> T noNullElements(final T t)
			throws NullPointerException, IllegalArgumentException {
		for (final Object o : notNull(t)) {
			if (o == null) {
				throw new IllegalArgumentException();
			}
		}
		return t;
	}

	/**
	 * Validates that {@code t} is not {@code null} and does not contain
	 * {@code null} values.
	 *
	 * @param t
	 * 		The value to validate.
	 * @param message
	 * 		The message of the thrown exception.
	 * @param args
	 * 		The arguments to pass to {@link String#format(String, Object...)}.
	 * @param <T>
	 * 		The type of {@code t}.
	 * @return
	 * 		{@code t}, never {@code null}.
	 * @throws NullPointerException
	 * 		If {@code t} is {@code null}.
	 * @throws IllegalArgumentException
	 * 		If {@code t} contains {@code null}.
	 */
	public static <T extends Collection<?>> T noNullElements(final T t,
			final String message, final Object... args)
			throws NullPointerException, IllegalArgumentException {
		for (final Object o : notNull(t)) {
			if (o == null) {
				throw new IllegalArgumentException(format(message, args));
			}
		}
		return t;
	}

	/**
	 * Validates that {@code t} is not {@code null}, does not contain
	 * {@code null} values, and is not empty.
	 *
	 * @param t
	 * 		The value to validate.
	 * @param <T>
	 * 		The type of {@code t}.
	 * @return
	 * 		{@code t}, never {@code null}.
	 * @throws NullPointerException
	 * 		If {@code t} is {@code null}.
	 * @throws IllegalArgumentException
	 * 		If {@code t} contains {@code null} or is empty.
	 */
	public static <T extends Collection<?>> T notEmpty(final T t)
			throws NullPointerException, IllegalArgumentException {
		if (noNullElements(t).isEmpty()) {
			throw new IllegalArgumentException();
		}
		return t;
	}

	/**
	 * Validates that {@code t} is not {@code null}, does not contain
	 * {@code null} values, and is not empty.
	 *
	 * @param t
	 * 		The value to validate.
	 * @param message
	 * 		The message of the thrown exception.
	 * @param args
	 * 		The arguments to pass to {@link String#format(String, Object...)}.
	 * @param <T>
	 * 		The type of {@code t}.
	 * @return
	 * 		{@code t}, never {@code null}.
	 * @throws NullPointerException
	 * 		If {@code t} is {@code null}.
	 * @throws IllegalArgumentException
	 * 		If {@code t} contains {@code null} or is empty.
	 */
	public static <T extends Collection<?>> T notEmpty(final T t,
			final String message, final Object... args)
			throws NullPointerException, IllegalArgumentException {
		if (noNullElements(t).isEmpty()) {
			throw new IllegalArgumentException(format(message, args));
		}
		return t;
	}

	/**
	 * Validates that {@code s} is not {@code null} and not empty.
	 *
	 * @param s
	 * 		The string to validate.
	 * @return
	 * 		{@code s}, never {@code null}.
	 * @throws NullPointerException
	 * 		If {@code s} is {@code null}.
	 * @throws IllegalArgumentException
	 * 		If {@code s} is empty.
	 */
	public static String notEmpty(final String s)
			throws NullPointerException, IllegalArgumentException {
		if (notNull(s).isEmpty()) {
			throw new IllegalArgumentException();
		}
		return s;
	}

	/**
	 * Validates that {@code s} is not {@code null} and not empty.
	 *
	 * @param s
	 * 		The string to validate.
	 * @param message
	 * 		The message of the thrown exception.
	 * @param args
	 * 		The arguments to pass to {@link String#format(String, Object...)}.
	 * @return
	 * 		{@code s}, never {@code null}.
	 * @throws NullPointerException
	 * 		If {@code s} is {@code null}.
	 * @throws IllegalArgumentException
	 * 		If {@code s} is empty.
	 */
	public static String notEmpty(final String s, final String message,
			final Object... args) throws NullPointerException,
			IllegalArgumentException {
		if (notNull(s).isEmpty()) {
			throw new IllegalArgumentException(format(message, args));
		}
		return s;
	}

	/**
	 * Validates that {@code s} is not {@code null} and contains at least one
	 * non-space character.
	 *
	 * @param s
	 * 		The string to validate.
	 * @return
	 * 		{@code s}, never a value that is {@code null} or consists of spaces
	 * 		only.
	 * @throws NullPointerException
	 * 		If {@code s} is {@code null}.
	 * @throws IllegalArgumentException
	 * 		If {@code s} consists of spaces only.
	 */
	public static String notBlank(final String s) throws NullPointerException,
			IllegalArgumentException {
		if (notNull(s).trim().isEmpty()) {
			throw new IllegalArgumentException();
		}
		return s;
	}

	/**
	 * Validates that {@code s} is not {@code null} and contains at least one
	 * non-space character.
	 *
	 * @param s
	 * 		The string to validate.
	 * @return
	 * 		{@code s}, never a value that is {@code null} or consists of spaces
	 * 		only.
	 * @param message
	 * 		The message of the thrown exception.
	 * @param args
	 * 		The arguments to pass to {@link String#format(String, Object...)}.
	 * @throws NullPointerException
	 * 		If {@code s} is {@code null}.
	 * @throws IllegalArgumentException
	 * 		If {@code s} consists of spaces only.
	 */
	public static String notBlank(final String s, final String message,
			final Object... args) throws NullPointerException,
			IllegalArgumentException {
		if (notNull(s).trim().isEmpty()) {
			throw new IllegalArgumentException(format(message, args));
		}
		return s;
	}

	/**
	 * Validates that {@code s} matches {@code pattern} using
	 * {@link String#matches(String)}.
	 *
	 * @param s
	 * 		The string to validate.
	 * @param pattern
	 * 		The pattern to match.
	 * @return
	 * 		{@code s}, never a value that doesn't match {@code pattern}.
	 * @throws NullPointerException
	 * 		If any of the given arguments is {@code null}.
	 * @throws IllegalArgumentException
	 * 		If {@code s} does not match {@code pattern}.
	 */
	public static String hasPattern(final String s, final String pattern)
			throws NullPointerException, IllegalArgumentException {
		notNull(s);
		notNull(pattern);
		if (!s.matches(pattern)) {
			throw new IllegalArgumentException();
		}
		return s;
	}

	/**
	 * Validates that {@code s} matches {@code pattern} using
	 * {@link String#matches(String)}.
	 *
	 * @param s
	 * 		The string to validate.
	 * @param pattern
	 * 		The pattern to match.
	 * @param message
	 * 		The message of the thrown exception.
	 * @param args
	 * 		The arguments to pass to {@link String#format(String, Object...)}.
	 * @return
	 * 		{@code s}, never a value that doesn't match {@code pattern}.
	 * @throws NullPointerException
	 * 		If any of the given arguments is {@code null}.
	 * @throws IllegalArgumentException
	 * 		If {@code s} does not match {@code pattern}.
	 */
	public static String hasPattern(final String s, final String pattern,
			final String message, final Object... args)
			throws NullPointerException, IllegalArgumentException {
		notNull(s);
		notNull(pattern);
		if (!s.matches(pattern)) {
			throw new IllegalArgumentException(format(message, args));
		}
		return s;
	}

	/**
	 * Validates that {@code i} is not negative.
	 *
	 * @param i
	 * 		The integer to validate.
	 * @return
	 * 		{@code i}, never a negative value.
	 * @throws IllegalArgumentException
	 * 		If {@code i < 0}.
	 */
	public static int notNegative(final int i)
			throws IllegalArgumentException {
		if (i < 0) {
			throw new IllegalArgumentException();
		}
		return i;
	}

	/**
	 * Validates that {@code i} is not negative.
	 *
	 * @param i
	 * 		The integer to validate.
	 * @param message
	 * 		The message of the thrown exception.
	 * @param args
	 * 		The arguments to pass to {@link String#format(String, Object...)}.
	 * @return
	 * 		{@code i}, never a negative value.
	 * @throws IllegalArgumentException
	 * 		If {@code i < 0}.
	 */
	public static int notNegative(final int i, final String message,
			final Object... args) throws IllegalArgumentException {
		if (i < 0) {
			throw new IllegalArgumentException(format(message, args));
		}
		return i;
	}

	/**
	 * Validates that {@code d} is not negative.
	 *
	 * @param d
	 * 		The double to validate.
	 * @return
	 * 		{@code d}, never a negative value.
	 * @throws IllegalArgumentException
	 * 		If {@code d < 0}.
	 */
	public static double notNegative(final double d)
			throws IllegalArgumentException {
		if (d < 0) {
			throw new IllegalArgumentException();
		}
		return d;
	}

	/**
	 * Validates that {@code d} is not negative.
	 *
	 * @param d
	 * 		The double to validate.
	 * @param message
	 * 		The message of the thrown exception.
	 * @param args
	 * 		The arguments to pass to {@link String#format(String, Object...)}.
	 * @return
	 * 		{@code d}, never a negative value.
	 * @throws IllegalArgumentException
	 * 		If {@code d < 0}.
	 */
	public static double notNegative(final double d, final String message,
			final Object... args) throws IllegalArgumentException {
		if (d < 0) {
			throw new IllegalArgumentException(format(message, args));
		}
		return d;
	}

	/**
	 * Validates that {@code i} is negative.
	 *
	 * @param i
	 * 		The integer to validate.
	 * @return
	 * 		{@code i}, never a value {@code >= 0}.
	 * @throws IllegalArgumentException
	 * 		If {@code i >= 0}.
	 */
	public static int isNegative(final int i) throws IllegalArgumentException {
		if (i >= 0) {
			throw new IllegalArgumentException();
		}
		return i;
	}

	/**
	 * Validates that {@code i} is negative.
	 *
	 * @param i
	 * 		The integer to validate.
	 * @param message
	 * 		The message of the thrown exception.
	 * @param args
	 * 		The arguments to pass to {@link String#format(String, Object...)}.
	 * @return
	 * 		{@code i}, never a value {@code >= 0}.
	 * @throws IllegalArgumentException
	 * 		If {@code i >= 0}.
	 */
	public static int isNegative(final int i, final String message,
			final Object... args) throws IllegalArgumentException {
		if (i >= 0) {
			throw new IllegalArgumentException(format(message, args));
		}
		return i;
	}

	/**
	 * Validates that {@code d} is negative.
	 *
	 * @param d
	 * 		The double to validate.
	 * @return
	 * 		{@code d}, never a value {@code >= 0}.
	 * @throws IllegalArgumentException
	 * 		If {@code d >= 0}.
	 */
	public static double isNegative(final double d)
			throws IllegalArgumentException {
		if (d >= 0) {
			throw new IllegalArgumentException();
		}
		return d;
	}

	/**
	 * Validates that {@code d} is negative.
	 *
	 * @param d
	 * 		The double to validate.
	 * @param message
	 * 		The message of the thrown exception.
	 * @param args
	 * 		The arguments to pass to {@link String#format(String, Object...)}.
	 * @return
	 * 		{@code d}, never a value {@code >= 0}.
	 * @throws IllegalArgumentException
	 * 		If {@code d >= 0}.
	 */
	public static double isNegative(final double d, final String message,
			final Object... args) throws IllegalArgumentException {
		if (d >= 0) {
			throw new IllegalArgumentException(format(message, args));
		}
		return d;
	}

	/**
	 * Validates that {@code i} is positive ({@code > 0}).
	 *
	 * @param i
	 * 		The integer to validate.
	 * @return
	 * 		{@code i}, never a value {@code <= 0}.
	 * @throws IllegalArgumentException
	 * 		If {@code i <= 0}.
	 */
	public static int isPositive(final int i) throws IllegalArgumentException {
		if (i < 1) {
			throw new IllegalArgumentException();
		}
		return i;
	}

	/**
	 * Validates that {@code i} is positive ({@code > 0}).
	 *
	 * @param i
	 * 		The integer to validate.
	 * @param message
	 * 		The message of the thrown exception.
	 * @param args
	 * 		The arguments to pass to {@link String#format(String, Object...)}.
	 * @return
	 * 		{@code i}, never a value {@code <= 0}.
	 * @throws IllegalArgumentException
	 * 		If {@code i <= 0}.
	 */
	public static int isPositive(final int i, final String message,
			final Object... args) throws IllegalArgumentException {
		if (i < 1) {
			throw new IllegalArgumentException(format(message, args));
		}
		return i;
	}

	/**
	 * Validates that {@code d} is positive ({@code > 0}).
	 *
	 * @param d
	 * 		The double to validate.
	 * @return
	 * 		{@code d}, never a value {@code <= 0}.
	 * @throws IllegalArgumentException
	 * 		If {@code d <= 0}.
	 */
	public static double isPositive(final double d)
			throws IllegalArgumentException {
		if (d < 1) {
			throw new IllegalArgumentException();
		}
		return d;
	}
	/**
	 * Validates that {@code d} is positive ({@code > 0}).
	 *
	 * @param d
	 * 		The double to validate.
	 * @param message
	 * 		The message of the thrown exception.
	 * @param args
	 * 		The arguments to pass to {@link String#format(String, Object...)}.
	 * @return
	 * 		{@code d}, never a value {@code <= 0}.
	 * @throws IllegalArgumentException
	 * 		If {@code d <= 0}.
	 */
	public static double isPositive(final double d, final String message,
			final Object... args) throws IllegalArgumentException {
		if (d < 1) {
			throw new IllegalArgumentException(format(message, args));
		}
		return d;
	}

	/**
	 * Validates that {@code from <= t <= to}.
	 *
	 * @param t
	 * 		The value to validate.
	 * @param from
	 * 		The from value.
	 * @param to
	 * 		The to value.
	 * @param <T>
	 *     	The type of {@code t}, {@code from}, and {@code to}.
	 * @return
	 * 		{@code t}, never a value {@code < from} or {@code > to}.
	 * @throws NullPointerException
	 * 		If any of the given arguments is {@code null}.
	 * @throws IllegalArgumentException
	 * 		If {@code t < from} or {@code t > to}.
	 */
	public static <T extends Comparable<T>> T hasRange(final T t, final T from,
			final T to) throws NullPointerException,
			IllegalArgumentException {
		notNull(from);
		notNull(to);
		notNull(t);
		if (from.compareTo(t) > 0) {
			throw new IllegalArgumentException();
		} else if (to.compareTo(t) < 0) {
			throw new IllegalArgumentException();
		}
		return t;
	}

	/**
	 * Validates that {@code from <= t <= to}.
	 *
	 * @param t
	 * 		The value to validate.
	 * @param from
	 * 		The from value.
	 * @param to
	 * 		The to value.
	 * @param message
	 * 		The message of the thrown exception.
	 * @param args
	 * 		The arguments to pass to {@link String#format(String, Object...)}.
	 * @param <T>
	 *     	The type of {@code t}, {@code from}, and {@code to}.
	 * @return
	 * 		{@code t}, never a value {@code < from} or {@code > to}.
	 * @throws NullPointerException
	 * 		If any of the given arguments is {@code null}.
	 * @throws IllegalArgumentException
	 * 		If {@code t < from} or {@code t > to}.
	 */
	public static <T extends Comparable<T>> T hasRange(final T t, final T from,
			final T to, final String message, final Object... args)
			throws NullPointerException, IllegalArgumentException {
		notNull(from);
		notNull(to);
		notNull(t);
		if (from.compareTo(t) > 0) {
			throw new IllegalArgumentException(format(message, args));
		} else if (to.compareTo(t) < 0) {
			throw new IllegalArgumentException(format(message, args));
		}
		return t;
	}

	/**
	 * Validates that {@code t > compare}.
	 *
	 * @param t
	 * 		The value to validate.
	 * @param compare
	 * 		The value to compare to {@code t}.
	 * @param <T>
	 *     	The type of {@code t} and {@code compare}.
	 * @return
	 * 		{@code t}, never a value {@code <= compare}
	 * @throws NullPointerException
	 * 		If any of the given arguments is {@code null}.
	 * @throws IllegalArgumentException
	 * 		If {@code t <= compare}.
	 */
	public static <T extends Comparable<T>> T isGreaterThan(final T t,
			final T compare) throws NullPointerException,
			IllegalArgumentException {
		isGreaterThanOrEquals(t, compare);
		return notEquals(t, compare);
	}

	/**
	 * Validates that {@code t > compare}.
	 *
	 * @param t
	 * 		The value to validate.
	 * @param compare
	 * 		The value to compare to {@code t}.
	 * @param message
	 * 		The message of the thrown exception.
	 * @param args
	 * 		The arguments to pass to {@link String#format(String, Object...)}.
	 * @param <T>
	 *     	The type of {@code t} and {@code compare}.
	 * @return
	 * 		{@code t}, never a value {@code <= compare}
	 * @throws NullPointerException
	 * 		If any of the given arguments is {@code null}.
	 * @throws IllegalArgumentException
	 * 		If {@code t <= compare}.
	 */
	public static <T extends Comparable<T>> T isGreaterThan(final T t,
			final T compare, final String message, final Object... args)
			throws NullPointerException, IllegalArgumentException {
		isGreaterThanOrEquals(t, compare, message, args);
		return notEquals(t, compare, message, args);
	}

	/**
	 * Validates that {@code t >= compare}.
	 *
	 * @param t
	 * 		The value to validate.
	 * @param compare
	 * 		The value to compare to {@code t}.
	 * @param <T>
	 *     	The type of {@code t} and {@code compare}.
	 * @return
	 * 		{@code t}, never a value {@code < compare}.
	 * @throws NullPointerException
	 * 		If any of the given arguments is {@code null}.
	 * @throws IllegalArgumentException
	 * 		If {@code t < compare}.
	 */
	public static <T extends Comparable<T>> T isGreaterThanOrEquals(final T t,
			final T compare) throws NullPointerException,
			IllegalArgumentException {
		notNull(t);
		notNull(compare);
		if (compare.compareTo(t) > 0) {
			throw new IllegalArgumentException();
		}
		return t;
	}

	/**
	 * Validates that {@code t >= compare}.
	 *
	 * @param t
	 * 		The value to validate.
	 * @param compare
	 * 		The value to compare to {@code t}.
	 * @param message
	 * 		The message of the thrown exception.
	 * @param args
	 * 		The arguments to pass to {@link String#format(String, Object...)}.
	 * @param <T>
	 *     	The type of {@code t} and {@code compare}.
	 * @return
	 * 		{@code t}, never a value {@code < compare}.
	 * @throws NullPointerException
	 * 		If any of the given arguments is {@code null}.
	 * @throws IllegalArgumentException
	 * 		If {@code t < compare}.
	 */
	public static <T extends Comparable<T>> T isGreaterThanOrEquals(final T t,
			final T compare, final String message, final Object... args)
			throws NullPointerException, IllegalArgumentException {
		notNull(t);
		notNull(compare);
		if (compare.compareTo(t) > 0) {
			throw new IllegalArgumentException(format(message, args));
		}
		return t;
	}

	/**
	 * Validates that {@code t.compareTo(compare) == 0}.
	 *
	 * @param t
	 * 		The value to validate.
	 * @param compare
	 * 		The value to compare to {@code t}.
	 * @param <T>
	 *     	The type of {@code t} and {@code compare}.
	 * @return
	 * 		{@code t}, never a value {@code != compare}.
	 * @throws NullPointerException
	 * 		If any of the given arguments is {@code null}.
	 * @throws IllegalArgumentException
	 * 		If {@code t.compareTo(compare) != 0}.
	 */
	public static <T extends Comparable<T>> T isEqualTo(final T t,
			final T compare) throws NullPointerException,
			IllegalArgumentException {
		notNull(t);
		notNull(compare);
		if (t.compareTo(compare) != 0) {
			throw new IllegalArgumentException();
		}
		return t;
	}

	/**
	 * Validates that {@code t.compareTo(compare) == 0}.
	 *
	 * @param t
	 * 		The value to validate.
	 * @param compare
	 * 		The value to compare to {@code t}.
	 * @param message
	 * 		The message of the thrown exception.
	 * @param args
	 * 		The arguments to pass to {@link String#format(String, Object...)}.
	 * @param <T>
	 *     	The type of {@code t} and {@code compare}.
	 * @return
	 * 		{@code t}, never a value {@code != compare}.
	 * @throws NullPointerException
	 * 		If any of the given arguments is {@code null}.
	 * @throws IllegalArgumentException
	 * 		If {@code t.compareTo(compare) != 0}.
	 */
	public static <T extends Comparable<T>> T isEqualTo(final T t,
			final T compare, final String message, final Object... args)
			throws NullPointerException, IllegalArgumentException {
		notNull(t);
		notNull(compare);
		if (t.compareTo(compare) != 0) {
			throw new IllegalArgumentException(format(message, args));
		}
		return t;
	}

	/**
	 * Validates that {@code t.compareTo(compare) != 0}.
	 *
	 * @param t
	 * 		The value to validate.
	 * @param compare
	 * 		The value to compare to {@code t}.
	 * @param <T>
	 *     	The type of {@code t} and {@code compare}.
	 * @return
	 * 		{@code t}, never a value {@code == compare}.
	 * @throws NullPointerException
	 * 		If any of the given arguments is {@code null}.
	 * @throws IllegalArgumentException
	 * 		If {@code t.compareTo(compare) == 0}.
	 */
	public static <T extends Comparable<T>> T notEquals(final T t,
			final T compare) throws NullPointerException,
			IllegalArgumentException {
		notNull(t);
		notNull(compare);
		if (t.compareTo(compare) == 0) {
			throw new IllegalArgumentException();
		}
		return t;
	}

	/**
	 * Validates that {@code t.compareTo(compare) != 0}.
	 *
	 * @param t
	 * 		The value to validate.
	 * @param compare
	 * 		The value to compare to {@code t}.
	 * @param message
	 * 		The message of the thrown exception.
	 * @param args
	 * 		The arguments to pass to {@link String#format(String, Object...)}.
	 * @param <T>
	 *     	The type of {@code t} and {@code compare}.
	 * @return
	 * 		{@code t}, never a value {@code == compare}.
	 * @throws NullPointerException
	 * 		If any of the given arguments is {@code null}.
	 * @throws IllegalArgumentException
	 * 		If {@code t.compareTo(compare) == 0}.
	 */
	public static <T extends Comparable<T>> T notEquals(final T t,
			final T compare, final String message, final Object... args)
			throws NullPointerException, IllegalArgumentException {
		notNull(t);
		notNull(compare);
		if (t.compareTo(compare) == 0) {
			throw new IllegalArgumentException(format(message, args));
		}
		return t;
	}

	/**
	 * Validates that {@code t.compareTo(compare) <= 0}.
	 *
	 * @param t
	 * 		The value to validate.
	 * @param compare
	 * 		The value to compare to {@code t}.
	 * @param <T>
	 *     	The type of {@code t} and {@code compare}.
	 * @return
	 * 		{@code t}, never a value {@code > compare}.
	 * @throws NullPointerException
	 * 		If any of the given arguments is {@code null}.
	 * @throws IllegalArgumentException
	 * 		If {@code t.compareTo(compare) > 0}.
	 */
	public static <T extends Comparable<T>> T isLessThanOrEquals(final T t,
			final T compare) throws NullPointerException,
			IllegalArgumentException {
		isGreaterThanOrEquals(compare, t);
		return t;
	}

	/**
	 * Validates that {@code t.compareTo(compare) <= 0}.
	 *
	 * @param t
	 * 		The value to validate.
	 * @param compare
	 * 		The value to compare to {@code t}.
	 * @param message
	 * 		The message of the thrown exception.
	 * @param args
	 * 		The arguments to pass to {@link String#format(String, Object...)}.
	 * @param <T>
	 *     	The type of {@code t} and {@code compare}.
	 * @return
	 * 		{@code t}, never a value {@code > compare}.
	 * @throws NullPointerException
	 * 		If any of the given arguments is {@code null}.
	 * @throws IllegalArgumentException
	 * 		If {@code t.compareTo(compare) > 0}.
	 */
	public static <T extends Comparable<T>> T isLessThanOrEquals(final T t,
			final T compare, final String message, final String args)
			throws NullPointerException, IllegalArgumentException {
		isGreaterThanOrEquals(compare, t, message, args);
		return t;
	}

	/**
	 * Validates that {@code t.compareTo(compare) < 0}.
	 *
	 * @param t
	 * 		The value to validate.
	 * @param compare
	 * 		The value to compare to {@code t}.
	 * @param <T>
	 *     	The type of {@code t} and {@code compare}.
	 * @return
	 * 		{@code t}, never a value {@code >= compare}.
	 * @throws NullPointerException
	 * 		If any of the given arguments is {@code null}.
	 * @throws IllegalArgumentException
	 * 		If {@code t.compareTo(compare) >= 0}.
	 */
	public static <T extends Comparable<T>> T isLessThan(final T t,
			final T compare) throws NullPointerException,
			IllegalArgumentException {
		notEquals(compare, t);
		isGreaterThan(compare, t);
		return t;
	}

	/**
	 * Validates that {@code t.compareTo(compare) < 0}.
	 *
	 * @param t
	 * 		The value to validate.
	 * @param compare
	 * 		The value to compare to {@code t}.
	 * @param message
	 * 		The message of the thrown exception.
	 * @param args
	 * 		The arguments to pass to {@link String#format(String, Object...)}.
	 * @param <T>
	 *     	The type of {@code t} and {@code compare}.
	 * @return
	 * 		{@code t}, never a value {@code >= compare}.
	 * @throws NullPointerException
	 * 		If any of the given arguments is {@code null}.
	 * @throws IllegalArgumentException
	 * 		If {@code t.compareTo(compare) >= 0}.
	 */
	public static <T extends Comparable<T>> T isLessThan(final T t,
			final T compare, final String message, final Object... args)
			throws NullPointerException, IllegalArgumentException {
		notEquals(compare, t, message, args);
		isGreaterThan(compare, t, message, args);
		return t;
	}

	/**
	 * Validates that {@code b} is {@code true}.
	 *
	 * @param b
	 * 		The boolean to validate.
	 * @throws IllegalArgumentException
	 * 		If {@code b} is {@code false}.
	 */
	public static void isTrue(final boolean b)
			throws IllegalArgumentException {
		if (!b) {
			throw new IllegalArgumentException();
		}
	}

	/**
	 * Validates that {@code b} is {@code true}.
	 *
	 * @param b
	 * 		The boolean to validate.
	 * @param message
	 * 		The message of the thrown exception.
	 * @param args
	 * 		The arguments to pass to {@link String#format(String, Object...)}.
	 * @throws IllegalArgumentException
	 * 		If {@code b} is {@code false}.
	 */
	public static void isTrue(final boolean b, final String message,
			final Object... args) throws IllegalArgumentException {
		if (!b) {
			throw new IllegalArgumentException(format(message, args));
		}
	}

	/**
	 * Validates that {@code b} is {@code false}.
	 *
	 * @param b
	 * 		The boolean to validate.
	 * @throws IllegalArgumentException
	 * 		If {@code b} is {@code true}.
	 */
	public static void isFalse(final boolean b)
			throws IllegalArgumentException {
		isTrue(!b);
	}

	/**
	 * Validates that {@code b} is {@code false}.
	 *
	 * @param b
	 * 		The boolean to validate.
	 * @param message
	 * 		The message of the thrown exception.
	 * @param args
	 * 		The arguments to pass to {@link String#format(String, Object...)}.
	 * @throws IllegalArgumentException
	 * 		If {@code b} is {@code true}.
	 */
	public static void isFalse(final boolean b, final String message,
			final Object... args) throws IllegalArgumentException {
		isTrue(!b, message, args);
	}

	/**
	 * Validates that {@code file} is equal to {@code compare}. Two files are
	 * equal if the ids of their revisions (see {@link Revision#getId()}) and
	 * their relative paths (see {@link VCSFile#getRelativePath()}) are equal.
	 *
	 * @param file
	 * 		The file to validate.
	 * @param compare
	 * 		The file to compare to {@code file}.
	 * @return
	 * 		{@code file}, never a value {@code != compare}.
	 * @throws NullPointerException
	 * 		If any of the given arguments is {@code null}.
	 * @throws IllegalArgumentException
	 * 		If {@code file} and {@code compare} are not equals.
	 */
	public static VCSFile isEqualTo(final VCSFile file, final VCSFile compare)
			throws NullPointerException, IllegalArgumentException {
		Validate.notNull(file);
		Validate.notNull(compare);
		if (!file.getRevision().getId().equals(
				compare.getRevision().getId())) {
			throw new IllegalArgumentException();
		}
		if (!file.getRelativePath().equals(
				compare.getRelativePath())) {
			throw new IllegalArgumentException();
		}
		return file;
	}

	/**
	 * Validates that {@code file} is equal to {@code compare}. Two files are
	 * equal if the ids of their revisions (see {@link Revision#getId()}) and
	 * their relative paths (see {@link VCSFile#getRelativePath()}) are equal.
	 *
	 * @param file
	 * 		The file to validate.
	 * @param compare
	 * 		The file to compare to {@code file}.
	 * @param message
	 * 		The message of the thrown exception.
	 * @param args
	 * 		The arguments to pass to {@link String#format(String, Object...)}.
	 * @return
	 * 		{@code file}, never a value {@code != compare}.
	 * @throws NullPointerException
	 * 		If any of the given arguments is {@code null}.
	 * @throws IllegalArgumentException
	 * 		If {@code file} and {@code compare} are not equals.
	 */
	public static VCSFile isEqualTo(final VCSFile file, final VCSFile compare,
			final String message, final Object... args)
			throws NullPointerException, IllegalArgumentException {
		Validate.notNull(file);
		Validate.notNull(compare);
		if (!file.getRevision().getId().equals(
				compare.getRevision().getId())) {
			throw new IllegalArgumentException(format(message, args));
		}
		if (!file.getRelativePath().equals(
				compare.getRelativePath())) {
			throw new IllegalArgumentException(format(message, args));
		}
		return file;
	}

	/**
	 * Validates that {@code expression} is {@code true}.
	 *
	 * @param expression
	 * 		The expression to validate.
	 * @throws IllegalStateException
	 *		If the expression evaluates to {@code false}.
	 */
	public static void validateState(final boolean expression)
			throws IllegalStateException {
		if (!expression) {
			throw new IllegalStateException();
		}
	}

	/**
	 * Validates that {@code expression} is {@code true}.
	 *
	 * @param expression
	 * 		The expression to validate.
	 * @param message
	 * 		The message of the thrown exception.
	 * @param args
	 * 		The arguments to pass to {@link String#format(String, Object...)}.
	 * @throws IllegalStateException
	 *		If the expression evaluates to {@code false}.
	 */
	public static void validateState(final boolean expression,
			final String message, final Object... args)
			throws IllegalStateException {
		if (!expression) {
			throw new IllegalStateException(format(message, args));
		}
	}

	/**
	 * Throws a {@link IllegalStateException}.
	 *
	 * @throws IllegalStateException
	 * 		Thrown by this method.
	 */
	public static void fail() throws IllegalStateException {
		validateState(false);
	}

	/**
	 * Throws a {@link IllegalStateException}.
	 *
	 * @param message
	 * 		The message of the thrown exception.
	 * @param args
	 * 		The arguments to pass to {@link String#format(String, Object...)}.
	 * @throws IllegalStateException
	 * 		Thrown by this method.
	 */
	public static void fail(final String message, final Object... args) {
		validateState(false, message, args);
	}
}
