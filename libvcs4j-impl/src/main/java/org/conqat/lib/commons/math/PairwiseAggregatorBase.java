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
 * This is a base class for aggregators that have a defined neutral element and
 * operate in pairwise manner.
 * 
 * @author Florian Deissenboeck
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 9FF1FC5F2A41B029A6ED4AEE11616313
 */
public abstract class PairwiseAggregatorBase implements IAggregator {

	/** {@inheritDoc} */
	@Override
	public double aggregate(Collection<? extends Number> values) {
		double result = getNeutralElement();
		for (Number value : values) {
			result = aggregate(result, value.doubleValue());
		}
		return result;
	}

	/**
	 * Calculate aggregate of two values.
	 * 
	 * @param aggregate
	 *            the value that has been aggregated by previous pairwise
	 *            aggregations
	 * @param value
	 *            the new value
	 */
	protected abstract double aggregate(double aggregate, double value);
}