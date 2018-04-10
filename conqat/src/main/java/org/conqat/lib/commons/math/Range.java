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

import java.io.Serializable;
import java.text.NumberFormat;

import org.conqat.lib.commons.assertion.CCSMPre;
import org.conqat.lib.commons.string.StringUtils;

/**
 * A class that represents ranges that may include or exclude the upper and
 * lower bounds. This class is immutable.
 * <p>
 * Note: If a range is constructed where the upper and lower bounds are equal
 * and one of them is exclusive, this range is considered empty, i.e. no number
 * can be contained in it.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 44540 $
 * @ConQAT.Rating GREEN Hash: 7675DD9C69A818D1BCB670923FF3D1F5
 */
public class Range implements Comparable<Range>, Serializable {

	/** Version for serialization. */
	private static final long serialVersionUID = 1;

	/** The lower bound. */
	private final double lower;

	/** The upper bound. */
	private final double upper;

	/** Flag that indicates if lower bound is inclusive or not. */
	private final boolean lowerIsInclusive;

	/** Flag that indicates if upper bound is inclusive or not. */
	private final boolean upperIsInclusive;

	/** Create range where both bounds are inclusive. */
	public Range(double lower, double upper) {
		this(lower, true, upper, true);
	}

	/**
	 * Create range.
	 * 
	 * @param lowerIsInclusive
	 *            flag that indicates if lower bound is inclusive or not
	 * @param upperIsInclusive
	 *            flag that indicates if upper bound is inclusive or not
	 */
	public Range(double lowerBound, boolean lowerIsInclusive,
			double upperBound, boolean upperIsInclusive) {
		CCSMPre.isFalse(Double.isNaN(lowerBound), "Lower bound must not be NaN");
		CCSMPre.isFalse(Double.isNaN(upperBound), "Upper bound must not be NaN");
		this.lower = lowerBound;
		this.lowerIsInclusive = lowerIsInclusive;
		this.upper = upperBound;
		this.upperIsInclusive = upperIsInclusive;
	}

	/** Checks is a number is contained in the range. */
	public boolean contains(double number) {
		if (lowerIsInclusive) {
			if (number < lower) {
				return false;
			}
		} else {
			if (number <= lower) {
				return false;
			}
		}

		if (upperIsInclusive) {
			if (number > upper) {
				return false;
			}
		} else {
			if (number >= upper) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Hash code includes bound and the flags that indicate if the bounds are
	 * inclusive or not.
	 */
	@Override
	public int hashCode() {
		if (isEmpty()) {
			return 0;
		}

		int result = hash(upper) + 37 * hash(lower);

		// indicate bounds by bit flips; which bit is used is not really
		// relevant
		if (lowerIsInclusive) {
			result ^= 0x100000;
		}
		if (upperIsInclusive) {
			result ^= 0x4000;
		}

		return result;
	}

	/** Code for hashing a double is copied from {@link Double#hashCode()}. */
	private int hash(double number) {
		long bits = Double.doubleToLongBits(number);
		return (int) (bits ^ (bits >>> 32));
	}

	/**
	 * Two ranges are equal if their bounds are equal and the flags that
	 * indicate if the bounds are inclusive or not are equal, too. Empty ranges
	 * are considered equal regardless for there specific bounds.
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof Range)) {
			return false;
		}

		Range other = (Range) obj;

		if (isEmpty() && other.isEmpty()) {
			return true;
		}

		return lowerIsInclusive == other.lowerIsInclusive
				&& upperIsInclusive == other.upperIsInclusive
				&& lower == other.lower && upper == other.upper;
	}

	/** Get lower bound. */
	public double getLower() {
		return lower;
	}

	/** Get upper bound. */
	public double getUpper() {
		return upper;
	}

	/** Get flag that indicates if the lower bound is inclusive. */
	public boolean isLowerInclusive() {
		return lowerIsInclusive;
	}

	/** Get flag that indicates if the upper bound is inclusive. */
	public boolean isUpperInclusive() {
		return upperIsInclusive;
	}

	/** Checks if a range is empty. */
	public boolean isEmpty() {
		if (lowerIsInclusive && upperIsInclusive) {
			return lower > upper;
		}
		return lower >= upper;
	}

	/**
	 * Returns whether this range is a singleton (i.e. contains of a single
	 * point/number).
	 */
	public boolean isSingleton() {
		return lower == upper && lowerIsInclusive && upperIsInclusive;
	}

	/**
	 * Returns the size of the range. In case of empty ranges this returns 0.
	 * Note that inclusiveness of bound is not relevant for this method.
	 */
	public double size() {
		return Math.max(0, upper - lower);
	}

	/** This forwards to <code>format(null);</code>. */
	@Override
	public String toString() {
		return format(null);
	}

	/**
	 * String representation contains the bounds and brackets that indicate if
	 * the bounds are inclusive or exclusive.
	 * 
	 * @param numberFormat
	 *            number format used for formatting the numbers. If this is
	 *            <code>null</code>, no special formatting is applied.
	 */
	public String format(NumberFormat numberFormat) {
		StringBuilder result = new StringBuilder();
		if (lowerIsInclusive) {
			result.append("[");
		} else {
			result.append("]");
		}
		result.append(StringUtils.format(lower, numberFormat) + ";"
				+ StringUtils.format(upper, numberFormat));
		if (upperIsInclusive) {
			result.append("]");
		} else {
			result.append("[");
		}
		return result.toString();
	}

	/**
	 * Returns whether this range has a non-empty intersection with another
	 * range.
	 */
	public boolean overlaps(Range other) {
		if (isEmpty() || other.isEmpty()) {
			return false;
		}

		if (lower == other.lower) {
			if (isSingleton()) {
				return other.lowerIsInclusive;
			}
			if (other.isSingleton()) {
				return lowerIsInclusive;
			}
			return true;
		}

		if (lower < other.lower) {
			if (upperIsInclusive && other.lowerIsInclusive) {
				return other.lower <= upper;
			}
			return other.lower < upper;
		}

		return other.overlaps(this);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Compares by lower values.
	 */
	@Override
	public int compareTo(Range other) {
		int result = Double.compare(lower, other.lower);
		if (result != 0) {
			return result;
		}

		if (lowerIsInclusive && !other.lowerIsInclusive) {
			return -1;
		}
		if (!lowerIsInclusive && other.lowerIsInclusive) {
			return 1;
		}
		return 0;
	}
}
