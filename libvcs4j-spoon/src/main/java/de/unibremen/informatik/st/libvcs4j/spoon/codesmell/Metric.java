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
	 * Indicates {@link #value} is decimal or integer.
	 */
	@Getter
	private final boolean isDecimal;

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
