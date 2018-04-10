/*-------------------------------------------------------------------------+
|                                                                          |
| Copyright 2005-2011 The ConQAT Project                                   |
|                                                                          |
| Licensed under the Apache License, Version 2.0 (the "License");          |
| you may not use this file except in compliance with the License.         |
| You may obtain a copy of the License at                                  |
|                                                                          |
|    http://www.apache.org/licenses/LICENSE-2.0                            |
|                                                                          |
| Unless required by applicable law or agreed to in writing, software      |
| distributed under the License is distributed on an "AS IS" BASIS,        |
| WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. |
| See the License for the specific language governing permissions and      |
| limitations under the License.                                           |
+-------------------------------------------------------------------------*/
package org.conqat.lib.commons.math;

import java.util.Collection;

/**
 * Calculates the variance with a two-pass algorithm. Implementation is highly
 * similar to the one used in Apache Commons.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 44616 $
 * @ConQAT.Rating GREEN Hash: 9FB777B4A2A74246D97CB478E8E9CCF1
 */
public class VarianceAggregator implements IAggregator {

	/** If true, sample variance is calculated, population variance otherwise. */
	private final boolean sample;

	/**
	 * Constructor.
	 * 
	 * @param sample
	 *            if true sample variance is calculated, population variance
	 *            otherwise.
	 */
	public VarianceAggregator(boolean sample) {
		this.sample = sample;
	}

	/**
	 * Aggregates by finding the average value
	 * 
	 * @return {@link Double#NaN} for empty input collection
	 */
	@Override
	public double aggregate(Collection<? extends Number> values) {
		if (values.isEmpty()) {
			return Double.NaN;
		}
		return calcVariance(values, MathUtils.mean(values));
	}

	/**
	 * Calculate variance. The implementation is based on the algorithm
	 * described at
	 * http://en.wikipedia.org/wiki/Algorithms_for_calculating_variance
	 */
	private double calcVariance(Collection<? extends Number> values, double mean) {

		if (values.size() == 1) {
			return 0;
		}

		double totalDeviationSquared = 0.0;
		double totalDeviation = 0.0;

		for (Number value : values) {
			double deviation = value.doubleValue() - mean;
			totalDeviationSquared += deviation * deviation;
			totalDeviation += deviation;
		}

		double x = totalDeviationSquared
				- (totalDeviation * totalDeviation / values.size());
		if (sample) {
			return x / (values.size() - 1);
		}

		return x / values.size();
	}

	/** Returns {@link Double#NaN}. */
	@Override
	public double getNeutralElement() {
		return Double.NaN;
	}
}