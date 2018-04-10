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
 * Calculates the standard deviation. This calculation is based on
 * {@link VarianceAggregator}.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: C5182F028500200DE4C280F01F8B8F8F
 */
public class StdDevAggregator implements IAggregator {

	/** The variance aggregator used to calculate the standard deviation. */
	private final VarianceAggregator varianceAggregator;

	/**
	 * Constructor.
	 * 
	 * @param sample
	 *            if true sample standard deviation is calculated, population
	 *            standard deviation otherwise.
	 */
	public StdDevAggregator(boolean sample) {
		varianceAggregator = new VarianceAggregator(sample);
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
		double variance = varianceAggregator.aggregate(values);
		return Math.sqrt(variance);
	}

	/** Returns {@link Double#NaN}. */
	@Override
	public double getNeutralElement() {
		return Double.NaN;
	}
}