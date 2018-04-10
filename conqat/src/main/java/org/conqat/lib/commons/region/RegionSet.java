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
package org.conqat.lib.commons.region;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.string.StringUtils;

/**
 * Set of {@link Region} objects. Allows tests for containment on the entire set
 * of regions.
 * 
 * @author $Author: heinemann $
 * @version $Revision: 44900 $
 * @ConQAT.Rating GREEN Hash: 9F62ED9AAC1E1B758EFA84DE1A288618
 */
public class RegionSet implements Set<Region> {

	/** Name that is used if {@link RegionSet} is created without name */
	public static final String ANONYMOUS = "Anonymous";

	/** Name of this RegionSet */
	private final String name;

	/**
	 * The size the map had when our internal structure was updated (or -1 to
	 * indicate a dirty state). We have to store the size rather than just a
	 * flag, as we do not catch remove calls in the iterator returned. But as
	 * you only can remove via an iterator we cannot be fooled by a sequence of
	 * add/remove calls, because we trap add in this class.
	 */
	private transient int cleanSize = -1;

	/** The inner set we are delegating to. */
	private final Set<Region> inner = new TreeSet<Region>(
			MemberComparator.INSTANCE);

	/** List of start points of the merged regions. */
	private transient final List<Integer> mergedStart = new ArrayList<Integer>();

	/** List of end points of the merged regions. */
	private transient final List<Integer> mergedEnd = new ArrayList<Integer>();

	/**
	 * Creates a named {@link RegionSet}.
	 * 
	 * @param name
	 *            Name of this region set.
	 */
	public RegionSet(String name) {
		this.name = name;
	}

	/** Creates an unnamed region set. */
	public RegionSet() {
		name = ANONYMOUS;
	}

	/** Returns the name. */
	public String getName() {
		return name;
	}

	/**
	 * Returns true if the position is contained in one of the {@link Region}s
	 * in the {@link RegionSet}
	 */
	public boolean contains(int position) {
		int k = getMergedIndex(position);
		return k >= 0 && position <= mergedEnd.get(k);
	}

	/**
	 * Tests whether all of the positions of the region are contained in the
	 * {@link RegionSet}
	 */
	public boolean contains(Region region) {
		int k = getMergedIndex(region.getStart());
		return k >= 0 && region.getEnd() <= mergedEnd.get(k);
	}

	/**
	 * Returns the index of the merged region whose start position is before or
	 * at the given position (or -1 if no such region exists).
	 */
	private int getMergedIndex(int position) {
		ensureMergedUpToDate();
		int k = Collections.binarySearch(mergedStart, position);

		// exact match, so return
		if (k >= 0) {
			return k;
		}

		// get insertion point (see the JavaDoc of binarySearch for an
		// explanation of the conversion)
		int insertionPoint = -(k + 1);

		// if it would be inserted at the beginning, there is no such index
		if (insertionPoint == 0) {
			return -1;
		}

		// otherwise, the index must be the one before the insertion point
		return insertionPoint - 1;
	}

	/**
	 * Tests whether any of the positions in the region are contained in the
	 * {@link RegionSet}.
	 */
	public boolean containsAny(Region region) {
		// if either start or end are in, it contains "any".
		if (contains(region.getStart()) || contains(region.getEnd())) {
			return true;
		}

		// now we know that start and end are not contained in an interval, so
		// to have any point contained, there must be an interval which is
		// completely contained in the given region. But this means that the
		// binary search has to give different results for start and end.
		int startIndex = Collections.binarySearch(mergedStart,
				region.getStart());
		int endIndex = Collections.binarySearch(mergedStart, region.getEnd());
		return startIndex != endIndex;
	}

	/** Makes sure the merged regions lists are up to date. */
	private void ensureMergedUpToDate() {
		if (inner.size() == cleanSize) {
			return;
		}

		mergedStart.clear();
		mergedEnd.clear();

		int start = -1;
		int end = -2;
		for (Region region : inner) {
			if (region.getStart() <= end + 1) {
				end = Math.max(end, region.getEnd());
			} else {
				if (start >= 0) {
					mergedStart.add(start);
					mergedEnd.add(end);
				}
				start = region.getStart();
				end = region.getEnd();
			}
		}

		if (start >= 0) {
			mergedStart.add(start);
			mergedEnd.add(end);
		}

		cleanSize = inner.size();
	}

	/**
	 * Gets the number of positions contained in the RegionSet. This corresponds
	 * to the (non-overlapping) sum of the length of the regions.
	 */
	public int getPositionCount() {
		ensureMergedUpToDate();
		int count = 0;

		int size = mergedStart.size();
		for (int i = 0; i < size; ++i) {
			count += mergedEnd.get(i) - mergedStart.get(i) + 1;
		}
		return count;
	}

	/**
	 * Returns last position in {@link RegionSet}.
	 * 
	 * @throws IllegalStateException
	 *             if {@link RegionSet} is empty
	 */
	public int getLastPosition() {
		if (isEmpty()) {
			throw new IllegalStateException("RegionSet is empty");
		}

		ensureMergedUpToDate();
		return CollectionUtils.getLast(mergedEnd);
	}

	/**
	 * Returns first position in {@link RegionSet}.
	 * 
	 * @throws IllegalStateException
	 *             if {@link RegionSet} is empty
	 */
	public int getFirstPosition() {
		if (isEmpty()) {
			throw new IllegalStateException("RegionSet is empty");
		}

		ensureMergedUpToDate();
		return mergedStart.get(0);
	}

	/**
	 * Creates a new {@link RegionSet} whose regions are a compactified version
	 * of this region set, i.e. the returned set is the minimal set that creates
	 * the same answers for all possible {@link #contains(int)} queries.
	 */
	public RegionSet createCompact() {
		ensureMergedUpToDate();

		RegionSet compacted = new RegionSet(name);
		for (int i = 0; i < mergedStart.size(); ++i) {
			Region region = new Region(mergedStart.get(i), mergedEnd.get(i),
					name);
			compacted.add(region);
		}
		return compacted;
	}

	/**
	 * Creates a new {@link RegionSet} whose regions are a complement to this
	 * {@link RegionSet}.
	 * 
	 * Inversion is relative to the interval [0, last position]
	 */
	public RegionSet createInverted(String name, int lastPosition) {
		ensureMergedUpToDate();

		RegionSet inverted = new RegionSet(name);
		int lastPos = 0;
		int size = mergedStart.size();
		for (int i = 0; i < size; ++i) {
			if (mergedStart.get(i) > lastPos) {
				inverted.add(new Region(lastPos, mergedStart.get(i) - 1, name));
			}
			lastPos = mergedEnd.get(i) + 1;
		}

		if (lastPos <= lastPosition) {
			inverted.add(new Region(lastPos, lastPosition, name));
		}

		return inverted;
	}

	/**
	 * Returns true if both {@link RegionSet}s contain the same positions and
	 * gaps.
	 */
	public boolean positionsEqual(RegionSet other) {
		if (other == null) {
			return false;
		}

		ensureMergedUpToDate();
		other.ensureMergedUpToDate();

		return mergedStart.equals(other.mergedStart)
				&& mergedEnd.equals(other.mergedEnd);
	}

	/**
	 * Comparator used for sorting the members of this set. Sorts ascending by
	 * start, then by end, then by name.
	 */
	private static class MemberComparator implements Comparator<Region> {

		/** Unique instance of this comparator. */
		private final static MemberComparator INSTANCE = new MemberComparator();

		/** {@inheritDoc} */
		@Override
		public int compare(Region region1, Region region2) {
			int startDiff = region1.getStart() - region2.getStart();
			if (startDiff != 0) {
				return startDiff;
			}

			int lengthDiff = region1.getLength() - region2.getLength();
			if (lengthDiff != 0) {
				return lengthDiff;
			}

			return region1.getOrigin().compareTo(region2.getOrigin());
		}
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return "{" + StringUtils.concat(inner, ",") + "}";
	}

	/** {@inheritDoc} */
	@Override
	public boolean add(Region o) {
		cleanSize = -1;
		return inner.add(o);
	}

	/** {@inheritDoc} */
	@Override
	public boolean addAll(Collection<? extends Region> c) {
		cleanSize = -1;
		return inner.addAll(c);
	}

	/** {@inheritDoc} */
	@Override
	public void clear() {
		cleanSize = -1;
		inner.clear();
	}

	/** {@inheritDoc} */
	@Override
	public boolean contains(Object o) {
		return inner.contains(o);
	}

	/** {@inheritDoc} */
	@Override
	public boolean containsAll(Collection<?> c) {
		return inner.containsAll(c);
	}

	/** {@inheritDoc} */
	@Override
	public boolean equals(Object o) {
		return inner.equals(o);
	}

	/** {@inheritDoc} */
	@Override
	public int hashCode() {
		return inner.hashCode();
	}

	/** {@inheritDoc} */
	@Override
	public boolean isEmpty() {
		return inner.isEmpty();
	}

	/** {@inheritDoc} */
	@Override
	public Iterator<Region> iterator() {
		return inner.iterator();
	}

	/** {@inheritDoc} */
	@Override
	public boolean remove(Object o) {
		cleanSize = -1;
		return inner.remove(o);
	}

	/** {@inheritDoc} */
	@Override
	public boolean removeAll(Collection<?> c) {
		cleanSize = -1;
		return inner.removeAll(c);
	}

	/** {@inheritDoc} */
	@Override
	public boolean retainAll(Collection<?> c) {
		cleanSize = -1;
		return inner.retainAll(c);
	}

	/** {@inheritDoc} */
	@Override
	public int size() {
		return inner.size();
	}

	/** {@inheritDoc} */
	@Override
	public Object[] toArray() {
		return inner.toArray();
	}

	/** {@inheritDoc} */
	@Override
	public <T> T[] toArray(T[] a) {
		return inner.toArray(a);
	}
}