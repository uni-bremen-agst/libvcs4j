/*-------------------------------------------------------------------------+
|                                                                          |
| Copyright 2005-2011 the ConQAT Project                                   |
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.conqat.lib.commons.assertion.CCSMAssert;

/**
 * Calculates a given percentile. E.g. PercentileAggregator(50) calculates the
 * median
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 343DD5C2EE6BDE09E458F1C4DE1D63D2
 */
public class PercentileAggregator implements IAggregator {

	/** The percentile being calculated */
	private final double percentile;

	/** Constructor. */
	public PercentileAggregator(double percentile) {
		CCSMAssert.isTrue(percentile > 0 && percentile <= 100,
				"Percentile must be in the range ]0, 100].");
		this.percentile = percentile;
	}

	/**
	 * Aggregates by finding the median.
	 * 
	 * @return {@link Double#NaN} for empty input collection
	 */
	@Override
	public double aggregate(Collection<? extends Number> values) {
		if (values.isEmpty()) {
			return Double.NaN;
		}

		// convert to doubles, as Number is not comparable
		ArrayList<Double> doubleValues = new ArrayList<Double>();
		for (Number value : values) {
			doubleValues.add(value.doubleValue());
		}
		Collections.sort(doubleValues);

		// this value is in ]0, size()]
		double index = doubleValues.size() * percentile / 100.;
		return doubleValues.get((int) Math.ceil(index) - 1);
	}

	/** Returns {@link Double#NaN}. */
	@Override
	public double getNeutralElement() {
		return Double.NaN;
	}
}
