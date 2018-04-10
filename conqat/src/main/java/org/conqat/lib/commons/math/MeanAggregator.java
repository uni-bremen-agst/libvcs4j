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
 * Average aggregator.
 * 
 * @author deissenb
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 5CFA28B1717B9B890B6060392F5F577D
 */
public class MeanAggregator implements IAggregator {

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
		return MathUtils.sum(values) / values.size();
	}

	/** Returns {@link Double#NaN}. */
	@Override
	public double getNeutralElement() {
		return Double.NaN;
	}
}