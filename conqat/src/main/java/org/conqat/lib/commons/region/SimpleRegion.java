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
package org.conqat.lib.commons.region;

import java.io.Serializable;

/**
 * A simple region with only start and end. If you also need a description
 * string (origin), use {@link Region}. Both start and end positions are
 * inclusive.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 47529 $
 * @ConQAT.Rating GREEN Hash: D6C8548362F69CC4F0ACB00A2E9FB236
 */
public class SimpleRegion implements Comparable<SimpleRegion>, Serializable {

	/** Version for serialization. */
	private static final long serialVersionUID = 1;

	/** Region start position (inclusive). */
	private final int start;

	/** Region end position (inclusive). */
	private final int end;

	/** Constructor. */
	public SimpleRegion(int start, int end) {
		this.start = start;
		this.end = end;
	}

	/** Checks if the region contains a position */
	public boolean containsPosition(int position) {
		return (start <= position && end >= position);
	}

	/** Checks if two regions are overlapping */
	public boolean overlaps(SimpleRegion r) {
		// Region with smaller start value performs overlap check
		if (r.start < start) {
			return r.overlaps(this);
		}

		return (start <= r.start && end >= r.start);
	}

	/** Checks if two regions are adjacent */
	public boolean adjacent(SimpleRegion r) {
		// Region with smaller start value performs adjacency check
		if (r.start < start) {
			return r.adjacent(this);
		}

		return (end + 1 == r.start);
	}

	/**
	 * Gets the end position of the region. This may be less than start for an
	 * empty region (see also {@link #isEmpty()}).
	 */
	public int getEnd() {
		return end;
	}

	/** Gets the start position of the region */
	public int getStart() {
		return start;
	}

	/**
	 * Gets the length of the region. Empty regions have a length of 0.
	 */
	public int getLength() {
		if (isEmpty()) {
			return 0;
		}
		return end - start + 1;
	}

	/** Returns whether this region is empty. */
	public boolean isEmpty() {
		return end < start;
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return "[" + start + "-" + end + "]";
	}

	/** Compares regions by their start position */
	@Override
	public int compareTo(SimpleRegion other) {
		return Integer.compare(start, other.start);
	}

	/**
	 * Returns whether start and end of the region is the same as for this
	 * region.
	 */
	public boolean equalsStartEnd(SimpleRegion other) {
		return start == other.start && end == other.end;
	}
}
