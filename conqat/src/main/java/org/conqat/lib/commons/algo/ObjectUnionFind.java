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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import org.conqat.lib.commons.assertion.CCSMAssert;
import org.conqat.lib.commons.assertion.CCSMPre;

/**
 * Implementation of a simple union find data structure working on arbitrary
 * objects. It implements the "partial path compression" heuristic but does not
 * use "union by size" but instead uses randomization. Additional the size of
 * union clusters is managed.
 * 
 * @author hummelb
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: AB88F839284AF27A2D80E37184B6C2D1
 */
public class ObjectUnionFind<T> {

	/** The underlying union find. */
	private final UnionFindWithSize unionFind = new UnionFindWithSize();

	/** The lookup map for mapping objects to integers. */
	private final Map<T, Integer> lookup;

	/** Stores elements put into the union find struture. */
	private final List<T> elements = new ArrayList<T>();

	/** Constructor using a HashMap as underlying lookup storage. */
	public ObjectUnionFind() {
		this(new HashMap<T, Integer>());
	}

	/**
	 * Constructor through which the the underlying map can be set.
	 * 
	 * @param lookup
	 *            the map being used for lookup (e.g. for providing a
	 *            {@link IdentityHashMap}. This map should not be used outside
	 *            afterwards!
	 */
	public ObjectUnionFind(HashMap<T, Integer> lookup) {
		this.lookup = lookup;
		lookup.clear();
	}

	/** Finds and returns the representative for the given element. */
	public T find(T element) {
		Integer index = lookup.get(element);
		if (index == null) {
			return element;
		}
		return elements.get(unionFind.find(index));
	}

	/**
	 * Merges the classes in which element1 and element2 are, by giving them the
	 * same representative.
	 */
	public void union(T element1, T element2) {
		if (!containsElement(element1)) {
			addElement(element1);
		}
		if (!containsElement(element2)) {
			addElement(element2);
		}

		unionFind.union(lookup.get(element1), lookup.get(element2));
	}

	/**
	 * Adds a new element to this union find structure. Note that explicit
	 * adding is not required, as elements are dynamically added by
	 * {@link #union(Object, Object)} and all other method work correctly even
	 * for objects not yet added. However this method makes sure, that no object
	 * can be added a second time.
	 */
	public void addElement(T element) {
		CCSMPre.isFalse(containsElement(element), "May not add element twice.");
		int index = unionFind.addElement();
		CCSMAssert.isTrue(index == elements.size(),
				"Elements not managed consistently!");
		elements.add(element);
		lookup.put(element, index);
	}

	/**
	 * Returns whether an element has been added to this stucture either by
	 * {@link #addElement(Object)} or {@link #union(Object, Object)}. Note that
	 * all methods will also work for elements for which this method returns
	 * false,
	 */
	public boolean containsElement(T element) {
		return lookup.containsKey(element);
	}

	/** Returns the size of the union cluster containing the given element. */
	public int getClusterSize(T element) {
		Integer index = lookup.get(element);
		if (index == null) {
			return 1;
		}
		return unionFind.getClusterSize(index);
	}
}