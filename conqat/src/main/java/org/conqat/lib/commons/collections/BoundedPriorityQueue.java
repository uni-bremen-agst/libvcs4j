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

import java.util.Comparator;
import java.util.PriorityQueue;

import org.conqat.lib.commons.assertion.CCSMAssert;

/**
 * Priority queue that retains only the n last/largest elements. Order is
 * determined by the employed comparator.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 46DBEAE9DA6FAE7F210B2FE67EE671CE
 */
public class BoundedPriorityQueue<T> extends PriorityQueue<T> {

	/** Version used for serialization. */
	private static final long serialVersionUID = 1;

	/** Capacity of the collection */
	private int capacityBound;

	/**
	 * Constructor using natural order
	 * 
	 * @param capacityBound
	 *            maximal number of elements that are retained
	 */
	public BoundedPriorityQueue(int capacityBound) {
		super(capacityBound);
		setCapacityBound(capacityBound);
	}

	/** Assert that capacityBound is positive and store in field */
	private void setCapacityBound(int capacityBound) throws AssertionError {
		CCSMAssert.isTrue(capacityBound > 0,
				"Capacity bound must be positive but was: " + capacityBound);
		this.capacityBound = capacityBound;
	}

	/**
	 * Constructor.
	 * 
	 * @param capacityBound
	 *            maximal number of elements that are retained
	 * 
	 * @param comparator
	 *            Comparator used to compare elements
	 */
	public BoundedPriorityQueue(int capacityBound, Comparator<T> comparator) {
		super(capacityBound, comparator);
		setCapacityBound(capacityBound);
	}

	/** Add a single element */
	@Override
	public boolean offer(T element) {
		boolean result = super.offer(element);

		if (size() > capacityBound) {
			remove();
		}
		CCSMAssert.isTrue(size() <= capacityBound,
				"Size exceeds capacity bound");

		return result;
	}
}