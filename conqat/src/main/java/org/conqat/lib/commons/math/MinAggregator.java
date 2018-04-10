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

/**
 * Minimum aggregator.
 * 
 * @author deissenb
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 6375F31ED3865876F17D11926ACE1D6D
 */
public class MinAggregator extends PairwiseAggregatorBase {

	/** Returns {@link Double#POSITIVE_INFINITY}. */
	@Override
	public double getNeutralElement() {
		return Double.POSITIVE_INFINITY;
	}

	/** {@inheritDoc} */
	@Override
	protected double aggregate(double aggregate, double value) {
		return Math.min(aggregate, value);
	}
}