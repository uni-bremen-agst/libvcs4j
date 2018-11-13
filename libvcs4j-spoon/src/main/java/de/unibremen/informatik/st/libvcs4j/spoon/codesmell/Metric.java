package de.unibremen.informatik.st.libvcs4j.spoon.codesmell;

import de.unibremen.informatik.st.libvcs4j.Validate;

import java.math.BigDecimal;

/**
 * An immutable class to represent arbitrary metrics.
 */
public class Metric {

	/**
	 * The name of a metric.
	 */
	private final String name;

	/**
	 * The value of a metric.
	 */
	private final BigDecimal value;

	/**
	 * Indicates {@link #value} is decimal or integer.
	 */
	private final boolean isDecimal;

	/**
	 * Creates a metric with given name and value.
	 *
	 * @param name
	 * 		The name of the metric to create.
	 * @param value
	 * 		The value of the metric to create.
	 * @param isDecimal
	 * 		Indicates whether the metric is decimal or integer.
	 * @throws NullPointerException
	 * 		If any of the given arguments is {@code null}.
	 * @throws IllegalArgumentException
	 * 		If {@code name} is empty (including blanks).
	 */
	public Metric(final String name, final BigDecimal value,
			final boolean isDecimal) throws NullPointerException,
			IllegalArgumentException {
		this.name = Validate.notBlank(name);
		this.value = Validate.notNull(value);
		this.isDecimal = isDecimal;
	}

	/**
	 * Returns the name of this metric.
	 *
	 * @return
	 * 		The name of this metric.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the value of this metric.
	 *
	 * @return
	 * 		The value of this metric.
	 */
	public BigDecimal getValue() {
		return value;
	}

	/**
	 * Returns whether this metric is decimal or integer.
	 *
	 * @return
	 * 		{@code true} if this metric is decimal, {@code false} otherwise.
	 */
	public boolean isDecimal() {
		return isDecimal;
	}

	/**
	 * Returns the value of this metric as int.
	 *
	 * @return
	 * 		The value of this metric as int.
	 */
	public int getIntValue() {
		return value.intValue();
	}

	/**
	 * Returns the value of this metric as double.
	 *
	 * @return
	 * 		The value of this metric as double.
	 */
	public double getDoubleValue() {
		return value.doubleValue();
	}
}
