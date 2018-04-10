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

import java.io.PrintStream;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.conqat.lib.commons.assertion.CCSMPre;
import org.conqat.lib.commons.collections.CounterSet;

/**
 * Collection of math utility methods.
 * 
 * @author $Author: heinemann $
 * @version $Rev: 46332 $
 * @ConQAT.Rating GREEN Hash: B2533E51719452CADD4C2588128D7F69
 */
public class MathUtils {

	/**
	 * Sum values.
	 * 
	 * @see SumAggregator
	 * @see EAggregationStrategy#SUM
	 */
	public static double sum(Collection<? extends Number> collection) {
		return aggregate(collection, EAggregationStrategy.SUM);
	}

	/**
	 * Find maximum.
	 * 
	 * @see MaxAggregator
	 * @see EAggregationStrategy#MAX
	 */
	public static double max(Collection<? extends Number> collection) {
		return aggregate(collection, EAggregationStrategy.MAX);
	}

	/**
	 * Find minimum.
	 * 
	 * @see MinAggregator
	 * @see EAggregationStrategy#MIN
	 */
	public static double min(Collection<? extends Number> collection) {
		return aggregate(collection, EAggregationStrategy.MIN);
	}

	/**
	 * Find mean.
	 * 
	 * @return {@link Double#NaN} for empty input collection
	 * 
	 * @see MeanAggregator
	 * @see EAggregationStrategy#MEAN
	 */
	public static double mean(Collection<? extends Number> collection) {
		return aggregate(collection, EAggregationStrategy.MEAN);
	}

	/**
	 * Find median.
	 * 
	 * @return {@link Double#NaN} for empty input collection
	 * 
	 * @see PercentileAggregator
	 * @see EAggregationStrategy#MEDIAN
	 */
	public static double median(Collection<? extends Number> collection) {
		return aggregate(collection, EAggregationStrategy.MEDIAN);
	}

	/**
	 * Find the 25-percentile.
	 * 
	 * @return {@link Double#NaN} for empty input collection
	 * 
	 * @see PercentileAggregator
	 * @see EAggregationStrategy#MEDIAN
	 */
	public static double percentile25(Collection<? extends Number> collection) {
		return aggregate(collection, EAggregationStrategy.PERCENTILE25);
	}

	/**
	 * Find the 75-percentile.
	 * 
	 * @return {@link Double#NaN} for empty input collection
	 * 
	 * @see PercentileAggregator
	 * @see EAggregationStrategy#MEDIAN
	 */
	public static double percentile75(Collection<? extends Number> collection) {
		return aggregate(collection, EAggregationStrategy.PERCENTILE75);
	}

	/**
	 * Calculate variance of the data set.
	 * 
	 * @param sample
	 *            if true sample variance is calculated, population variance
	 *            otherwise
	 */
	public static double variance(Collection<? extends Number> collection,
			boolean sample) {
		if (sample) {
			return aggregate(collection, EAggregationStrategy.SAMPLE_VARIANCE);
		}
		return aggregate(collection, EAggregationStrategy.POPULATION_VARIANCE);
	}

	/**
	 * Calculate standard deviation of the data set.
	 * 
	 * @param sample
	 *            if true sample standard deviation is calculated, population
	 *            variance otherwise
	 */
	public static double stdDev(Collection<? extends Number> collection,
			boolean sample) {
		if (sample) {
			return aggregate(collection, EAggregationStrategy.SAMPLE_STD_DEV);
		}
		return aggregate(collection, EAggregationStrategy.POPULATION_STD_DEV);
	}

	/**
	 * Aggregate collections of values with a given aggregation strategy.
	 * 
	 * @return certain aggregation strategies may return {@link Double#NaN} for
	 *         empty input collections
	 */
	public static double aggregate(Collection<? extends Number> values,
			EAggregationStrategy aggregation) {
		return aggregation.getAggregator().aggregate(values);
	}

	/**
	 * Computes the factorial of n. Errors are not handled. If n is negative, 1
	 * will be returned. If n to too large, wrong results will be produced due
	 * to numerical overflow.
	 */
	public static long factorial(int n) {
		long result = 1;
		for (int i = 2; i <= n; ++i) {
			result *= i;
		}
		return result;
	}

	/** Checks if the provided number is neither infinite nor NaN. */
	public static boolean isNormal(double number) {
		return !Double.isInfinite(number) && !Double.isNaN(number);
	}

	/**
	 * Calculates the number of choices for k from n elements, also known as
	 * binomial coefficients. The input parameters may not be too large to avoid
	 * overflows. Both parameters must be non-negative.
	 */
	public static int choose(long n, long k) {
		CCSMPre.isTrue(n >= 0 && k >= 0, "Parameters must be positive.");

		if (k == 0) {
			return 1;
		}
		if (n == 0) {
			return 0;
		}
		return choose(n - 1, k) + choose(n - 1, k - 1);
	}

	/**
	 * Computes a distribution of the given list of values and the given ranges.
	 * The result is a counter set of ranges, denoting how many values in the
	 * list are within a given range.
	 * <p>
	 * It is <B>not</B> checked whether the ranges are disjoint. Moreover, it is
	 * <B>not</B> checked whether every value can be mapped to a range. Thus,
	 * the resulting counter set may have an overall value that is less than or
	 * greater than the size of the list of given values.
	 * </p>
	 */
	public static CounterSet<Range> rangeDistribution(List<Double> values,
			Set<Range> ranges) {
		CounterSet<Range> result = new CounterSet<Range>();
		for (Double value : values) {
			for (Range range : ranges) {
				if (range.contains(value)) {
					result.inc(range);
				}
			}
		}
		return result;
	}

	/**
	 * Prints the min, max, mean, percentile25, median, and percentile75 of the
	 * given values to System.out.
	 */
	public static void printBasicDescriptiveStatistics(
			Collection<? extends Number> values) {
		printBasicDescriptiveStatistics(values, System.out);
	}

	/**
	 * Prints the min, max, sum, mean, percentile25, median, and percentile75 of
	 * the given values to the given {@link PrintStream}.
	 */
	public static void printBasicDescriptiveStatistics(
			Collection<? extends Number> values, PrintStream printStream) {
		printStream.println("Min          : " + MathUtils.min(values));
		printStream.println("Max          : " + MathUtils.max(values));
		printStream.println("Sum          : " + MathUtils.sum(values));
		printStream.println("Mean         : " + MathUtils.mean(values));
		printStream.println("Percentile25 : " + MathUtils.percentile25(values));
		printStream.println("Median       : " + MathUtils.median(values));
		printStream.println("Percentile75 : " + MathUtils.percentile75(values));
	}

	/**
	 * Constrains the given value, if it exceeds the given minimum or maximum.
	 * 
	 * @return the given value, if it is within the given minimum and maximum
	 *         (inclusive) or the minimum or maximum respectively, if it exceeds
	 *         these.
	 */
	public static int constrainValue(int value, int min, int max) {
		if (value < min) {
			return min;
		} else if (value > max) {
			return max;
		}
		return value;
	}
}