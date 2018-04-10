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
package org.conqat.lib.commons.collections;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * A counter set supporting ranged access to its values, i.e. it is possible to
 * query the sum of values for all keys in a given range. Inserting items works
 * in (amortized) constant time, but the range query is potentially expensive.
 * 
 * @author Florian Deissenboeck
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: C34D07E96CB9B856D0A2CB5B5BEEAC30
 */
public class SortedCounterSet<E extends Comparable<? super E>> extends
		CounterSet<E> {

	/**
	 * The comparator used to define the order of the elements. This may be
	 * <code>null</code>.
	 */
	private final Comparator<? super E> comparator;

	/**
	 * Create new counter array that orders its elements by the natural order.
	 */
	public SortedCounterSet() {
		comparator = null;
	}

	/**
	 * Create new counter array with specific comparator to define order.
	 */
	public SortedCounterSet(Comparator<? super E> comparator) {
		this.comparator = comparator;
	}

	/**
	 * Obtain the sum of all values in a certain range of elements. This
	 * operation runs in time <code>O(n*log(n))</code> where <code>n</code>
	 * is the size of this set, as the list of items is sorted each time.
	 * 
	 * @param firstElement
	 *            the first element to include. If the element is not present in
	 *            the list, the smallest element greater than this one is used.
	 * @param lastElement
	 *            the last element to include. If the element is not present in
	 *            the list, the largest element smaller than this one is used.
	 */
	public int getRangeSum(E firstElement, E lastElement) {

		List<E> elementList = new ArrayList<E>(getKeys());

		// if the list is empty the sum must be zero
		if (elementList.isEmpty()) {
			return 0;
		}

		// this uses natural ordering if compartor is null
		Collections.sort(elementList, comparator);

		// determine first index in the list
		int firstIndex;
		// this uses natural ordering if compartor is null
		firstIndex = Collections.binarySearch(elementList, firstElement,
				comparator);
		// see API documentation of Collections.binarySearch
		if (firstIndex < 0) {
			firstIndex = (firstIndex + 1) * (-1);
		}

		// determine last index in the lst
		int lastIndex;
		lastIndex = Collections.binarySearch(elementList, lastElement,
				comparator);

		// see API documentation of Collections.binarySearch
		if (lastIndex < 0) {
			lastIndex = (lastIndex + 2) * (-1);
		}

		int sum = 0;
		// iterate over the range and add values
		for (int i = firstIndex; i <= lastIndex; i++) {
			sum += getValue(elementList.get(i));
		}

		return sum;
	}
}