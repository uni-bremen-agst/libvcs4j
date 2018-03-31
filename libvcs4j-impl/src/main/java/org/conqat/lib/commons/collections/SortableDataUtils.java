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

/**
 * Abstraction for sortable/comparable data. This class supports basic
 * algorithms, such as sorting and binary search on any data which can be mapped
 * to a random access list. The main benefit of this class, is that the type of
 * data is operated on must not be known (or be a concrete type), thus it can
 * also be used to sort data spread over multiple lists or arrays.
 * 
 * @author hummelb
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 105638EE7A30AE7BD57365B60DD5B32F
 */
public class SortableDataUtils {

	/**
	 * Performs a binary search for the element at the given index. The returned
	 * index will be the first index at which the element could be inserted
	 * without violating the sorting order. If the underlying data is not
	 * sorted, the result is undefined. The result will be between 0 and
	 * {@link ISortableData#size()} inclusive. The given element index may be
	 * larger than {@link ISortableData#size()}, i.e. the element does not have
	 * to be in the list.
	 */
	public static int binarySearch(ISortableData data, int elementIndex) {
		int lower = 0;
		int upper = data.size();
		while (lower < upper) {
			// For next line see
			// http://googleresearch.blogspot.com/2006/06/extra-extra-read-all-about-it-nearly.html
			int mid = lower + upper >>> 1;
			if (data.isLess(mid, elementIndex)) {
				lower = mid + 1;
			} else {
				upper = mid;
			}
		}
		return lower;
	}

	/** Sorts the data in place using a randomized quick sort algorithm. */
	public static void sort(ISortableData data) {
		sort(data, 0, data.size());
	}

	/**
	 * Sorts the data between <code>begin</code> (inclusive) and
	 * <code>end</code> (exclusive).
	 */
	private static void sort(ISortableData data, int begin, int end) {
		if (end - begin < 5) {
			bubbleSort(data, begin, end);
			return;
		}

		int pivot = begin + (int) (Math.random() * (end - begin));
		int lower = begin;
		int upper = end - 1;
		while (lower <= upper) {
			if (data.isLess(lower, pivot)) {
				++lower;
			} else {
				pivot = swapFixPivot(data, lower, upper, pivot);
				--upper;
			}
		}

		// make pivot the central element
		if (lower != pivot) {
			data.swap(lower, pivot);
		}

		sort(data, begin, lower);
		sort(data, lower + 1, end);
	}

	/**
	 * Performs a swap but preserves the index of the pivot element. So, if the
	 * swap affects the pivot element, the new pivot index is returned.
	 */
	private static int swapFixPivot(ISortableData data, int i, int j, int pivot) {
		data.swap(i, j);
		if (i == pivot) {
			return j;
		}
		if (j == pivot) {
			return i;
		}
		return pivot;
	}

	/**
	 * Performs bubble sort on the given sub range. This is used for the base
	 * case in the quick sort algorithm.
	 */
	// package visible for testing
	/* package */static void bubbleSort(ISortableData data, int begin, int end) {
		for (int i = end - 1; i > begin; --i) {
			for (int j = begin; j < i; ++j) {
				if (data.isLess(j + 1, j)) {
					data.swap(j, j + 1);
				}
			}
		}
	}
}