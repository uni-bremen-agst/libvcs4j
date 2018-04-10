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

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.conqat.lib.commons.assertion.CCSMPre;

/**
 * A collection which implements a bidirectional mapping.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: F89E31E9987D516CD3C8B53A5A66C02F
 */
public class BidirectionalMap<S, T> {

	/** Mapping from s to t. */
	private final Map<S, T> stMap;

	/** Mapping from t to s. */
	private final Map<T, S> tsMap;

	/** Creates new bidirectional map based on hash maps. */
	public BidirectionalMap() {
		stMap = new HashMap<S, T>();
		tsMap = new HashMap<T, S>();
	}

	/** Creates new bidirectional map based given maps. */
	@SuppressWarnings("null")
	public BidirectionalMap(Map<S, T> stMap, Map<T, S> tsMap) {
		CCSMPre.isTrue(stMap != null && tsMap != null, "Maps may not be null!");
		CCSMPre.isTrue(stMap != tsMap, "Maps may not be equal!");
		CCSMPre.isTrue(stMap.isEmpty() && tsMap.isEmpty(),
				"Maps may not be used (filled)!");

		this.stMap = stMap;
		this.tsMap = tsMap;
	}

	/** Get first element. */
	public S getFirst(T t) {
		return tsMap.get(t);
	}

	/** Get second element. */
	public T getSecond(S s) {
		return stMap.get(s);
	}

	/** Returns whether this map is empty. */
	public boolean isEmpty() {
		return stMap.isEmpty();
	}

	/** Returns the size. */
	public int size() {
		return stMap.size();
	}

	/** Clears the map. */
	public void clear() {
		stMap.clear();
		tsMap.clear();
	}

	/**
	 * Returns whether the given element is in the first set (the domain of the
	 * bijection).
	 */
	public boolean containsFirst(S s) {
		return stMap.containsKey(s);
	}

	/**
	 * Returns whether the given element is in the second set (the range of the
	 * bijection).
	 */
	public boolean containsSecond(T t) {
		return tsMap.containsKey(t);
	}

	/** Returns the first set (the domain). */
	public UnmodifiableSet<S> getFirstSet() {
		return CollectionUtils.asUnmodifiable(stMap.keySet());
	}

	/** Returns the second set (the range). */
	public UnmodifiableSet<T> getSecondSet() {
		return CollectionUtils.asUnmodifiable(tsMap.keySet());
	}

	/** Returns the entries. */
	public UnmodifiableSet<Entry<S, T>> getEntrySet() {
		return CollectionUtils.asUnmodifiable(stMap.entrySet());
	}

	/** Returns the inverted entries. */
	public UnmodifiableSet<Entry<T, S>> getInvertedEntrySet() {
		return CollectionUtils.asUnmodifiable(tsMap.entrySet());
	}

	/**
	 * Inserts the given pair into the bijection. Any mapping associated with
	 * those values is removed before. This map does not support null values.
	 */
	public void put(S s, T t) {
		CCSMPre.isTrue(s != null && t != null, "null values not supported.");

		removeFirst(s);
		removeSecond(t);

		stMap.put(s, t);
		tsMap.put(t, s);
	}

	/** Removes the first object */
	public void removeFirst(S s) {
		T t = stMap.get(s);
		if (t != null) {
			stMap.remove(s);
			tsMap.remove(t);
		}
	}

	/** Removes the second object */
	public void removeSecond(T t) {
		S s = tsMap.get(t);
		if (s != null) {
			stMap.remove(s);
			tsMap.remove(t);
		}
	}
	
	/** {@inheritDoc} */
	@Override
	public String toString() {
		return stMap.toString();
	}

}