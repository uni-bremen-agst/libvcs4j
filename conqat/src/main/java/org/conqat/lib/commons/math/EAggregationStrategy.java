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
 * Enumeration for different aggregation strategies.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 231D829AFF1A5796A658B56E12F45370
 */
public enum EAggregationStrategy {
	/** Sum aggregation, see {@link SumAggregator}. */
	SUM(new SumAggregator()),

	/** Maximum aggregation, see {@link MaxAggregator}. */
	MAX(new MaxAggregator()),

	/** Minimum aggregation, see {@link MinAggregator}. */
	MIN(new MinAggregator()),

	/** Mean aggregation, see {@link MeanAggregator}. */
	MEAN(new MeanAggregator()),

	/** Median aggregation, see {@link PercentileAggregator}. */
	MEDIAN(new PercentileAggregator(50)),

	/** Percentile-25 aggregation, see {@link PercentileAggregator}. */
	PERCENTILE25(new PercentileAggregator(25)),

	/** Percentile-75 aggregation, see {@link PercentileAggregator}. */
	PERCENTILE75(new PercentileAggregator(75)),

	/** Population variance */
	POPULATION_VARIANCE(new VarianceAggregator(false)),

	/** Sample variance */
	SAMPLE_VARIANCE(new VarianceAggregator(true)),

	/** Population standard deviation */
	POPULATION_STD_DEV(new StdDevAggregator(false)),

	/** Sample standard deviation */
	SAMPLE_STD_DEV(new StdDevAggregator(true));

	/** The aggregator used for this strategy. */
	private final IAggregator aggregator;

	/** Create strategy. */
	private EAggregationStrategy(IAggregator aggregator) {
		this.aggregator = aggregator;
	}

	/** Get aggregator. */
	public IAggregator getAggregator() {
		return aggregator;
	}
}