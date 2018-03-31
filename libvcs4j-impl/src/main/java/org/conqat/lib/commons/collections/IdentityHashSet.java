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

import java.util.AbstractSet;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Iterator;

/**
 * This class implements a set based on referential equality similar to JDK
 * class {@link IdentityHashMap}. This class can be e.g. used to implement
 * listener lists that should not rely on the listeners <code>equals()</code>-methods.
 * <p>
 * The implementation is based on class {@link java.util.HashSet} that also uses
 * an underlying hash map.
 * 
 * 
 * @author Florian Deissenboeck
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 756A207AA32D7703098B979F694BC679
 */
public class IdentityHashSet<E> extends AbstractSet<E> {

	/** Dummy object for the map. */
	private static final Object PRESENT = new Object();

	/** The map that actually stores the values. */
	private final IdentityHashMap<E, Object> map;

	/** Create new identity hash set. */
	public IdentityHashSet() {
		map = new IdentityHashMap<E, Object>();
	}

	/** Create new identity hash set from an existing collection. */
	public IdentityHashSet(Collection<? extends E> collection) {
		this(collection.size());

		for (E e : collection) {
			add(e);
		}
	}

	/** Create new identity hash set with an expected maximum size. */
	public IdentityHashSet(int expectedMaxSize) {
		map = new IdentityHashMap<E, Object>(expectedMaxSize);
	}

	/**
	 * Adds the specified element to this set if it is not already present.
	 * 
	 * @param o
	 *            element to be added to this set.
	 * @return <tt>true</tt> if the set did not already contain the specified
	 *         element.
	 */
	@Override
	public boolean add(E o) {
		return map.put(o, PRESENT) == null;
	}

	/**
	 * Removes all of the elements from this set.
	 */
	@Override
	public void clear() {
		map.clear();
	}

	/**
	 * Returns a shallow copy of this <tt>IdentityHashSet</tt> instance: the
	 * elements themselves are not cloned.
	 * 
	 * @return a shallow copy of this set.
	 */
	@Override
	public IdentityHashSet<E> clone() {
		return new IdentityHashSet<E>(this);
	}

	/**
	 * Returns <tt>true</tt> if this set contains the specified element.
	 * 
	 * @param o
	 *            element whose presence in this set is to be tested.
	 * @return <tt>true</tt> if this set contains the specified element.
	 */
	@Override
	public boolean contains(Object o) {
		return map.containsKey(o);
	}

	/**
	 * Returns <tt>true</tt> if this set contains no elements.
	 * 
	 * @return <tt>true</tt> if this set contains no elements.
	 */
	@Override
	public boolean isEmpty() {
		return map.isEmpty();
	}

	/** Return iterator over the set. */
	@Override
	public Iterator<E> iterator() {
		return map.keySet().iterator();
	}

	/**
	 * Removes the specified element from this set if it is present.
	 * 
	 * @param o
	 *            object to be removed from this set, if present.
	 * @return <tt>true</tt> if the set contained the specified element.
	 */
	@Override
	public boolean remove(Object o) {
		return map.remove(o) == PRESENT;
	}

	/**
	 * Get set size.
	 */
	@Override
	public int size() {
		return map.size();
	}

}