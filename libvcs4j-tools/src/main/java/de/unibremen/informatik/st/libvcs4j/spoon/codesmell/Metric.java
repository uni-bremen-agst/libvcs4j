package de.unibremen.informatik.st.libvcs4j.spoon.codesmell;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * An immutable class to represent arbitrary metrics.
 */
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class Metric {

	/**
	 * The name of a metric.
	 */
	@Getter
	@NonNull
	private final String name;

	/**
	 * The value of a metric.
	 */
	@Getter
	@NonNull
	private final BigDecimal value;

	/**
	 * Indicates whether {@link #value} is decimal or integer.
	 */
	@Getter
	private final boolean isDecimal;

	/**
	 * Creates a new decimal metric.
	 *
	 * @param name
	 * 		The name of the metric.
	 * @param value
	 * 		The decimal value of the metric.
	 * @throws NullPointerException
	 * 		If {@code name} is {@code null}.
	 */
	public Metric(@NonNull final String name, final double value)
			throws NullPointerException {
		this.name = name;
		this.value = new BigDecimal(value);
		this.isDecimal = true;
	}

	/**
	 * Creates a new integer metric.
	 *
	 * @param name
	 * 		The name of the metric.
	 * @param value
	 * 		The integer value of the metric.
	 * @throws NullPointerException
	 * 		If {@code name} is {@code null}.
	 */
	public Metric(@NonNull final String name, final int value)
			throws NullPointerException {
		this.name = name;
		this.value = new BigDecimal(value);
		this.isDecimal = false;
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
