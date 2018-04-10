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
package org.conqat.lib.commons.algo;

import org.conqat.lib.commons.collections.ManagedIntArray;

/**
 * Implementation of a simple union find data structure. It implements the
 * "partial path compression" heuristic but does not use "union by rank" but
 * instead uses randomization.
 * 
 * @author hummelb
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 620EC9C85E5C8CE652DBF56B7766C366
 */
public class UnionFind extends ManagedIntArray {

	/** Flag that indicated whether random merge order should be used. */
	private final boolean noRandom;

	/** Constructor using random merge order. */
	public UnionFind() {
		this(false);
	}

	/**
	 * Constructor.
	 * 
	 * @param noRandom
	 *            if this is set to true, randomized merging is disabled. In
	 *            general, randomization should be used, as it prevents a linear
	 *            time worst case (for each union). However, if the order of
	 *            elements passed to the union method is already random (due to
	 *            the underlying data) or the size of the union size set is very
	 *            small, disabling randomization might cause a huge gain in
	 *            performance.
	 */
	public UnionFind(boolean noRandom) {
		this.noRandom = noRandom;
	}

	/** Finds and returns the representative for the given element. */
	public int find(int element) {
		if (element >= size) {
			throw new IllegalArgumentException("Unknown element!");
		}

		while (element != array[element]) {
			int next = array[element];
			array[element] = array[next];
			element = next;
		}

		return element;
	}

	/**
	 * Merges the classes in which element1 and element2 are, by giving them the
	 * same representative.
	 */
	public void union(int element1, int element2) {
		if (element1 >= size || element2 >= size) {
			throw new IllegalArgumentException("Unknown elements!");
		}

		// locate representatives
		element1 = find(element1);
		element2 = find(element2);

		if (element1 != element2) {
			if (noRandom || Math.random() > .5) {
				connectToRepresentative(element1, element2);
			} else {
				connectToRepresentative(element2, element1);
			}
		}
	}

	/**
	 * Connects the given element (must also be representative) to the given
	 * representative.
	 */
	protected void connectToRepresentative(int element, int representative) {
		array[element] = representative;
	}

	/** Adds a new element to this union find structure and returns its index. */
	public int addElement() {
		int index = size;
		addArrayElement();
		array[index] = index;
		return index;
	}
}