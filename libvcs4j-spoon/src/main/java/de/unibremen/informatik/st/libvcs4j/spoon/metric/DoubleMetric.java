package de.unibremen.informatik.st.libvcs4j.spoon.metric;

import de.unibremen.informatik.st.libvcs4j.Validate;

public class DoubleMetric extends Metric<Double> {

	@Override
	protected Double sum(final Double a, final Double b)
			throws NullPointerException {
		return Validate.notNull(a) + Validate.notNull(b);
	}

	/**
	 * Increments the metric of the top element of {@link #stack} by 1.0.
	 */
	void inc() {
		inc(1.0);
	}
}
