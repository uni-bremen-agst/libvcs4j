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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class defines a mapping from on item to a list of items.
 * 
 * @deprecated Use {@link ListMap} instead.
 * 
 * @author $Author: kinnen $
 * @version $Revision: 41751 $
 * @ConQAT.Rating GREEN Hash: F5A94C630E19D96F3AC24E786B8BB0FD
 */
@Deprecated
public class HashedListMap<K, I> {

	/** The actual map. */
	private final Map<K, List<I>> entries;

	/**
	 * Create new hashed list map.
	 */
	public HashedListMap() {
		entries = new HashMap<K, List<I>>();
	}

	/**
	 * Create new hashed list map with a specified map.
	 */
	public HashedListMap(Map<K, List<I>> map) {
		entries = map;
	}

	/** Copy constructor. */
	public HashedListMap(HashedListMap<K, I> other) {
		this();
		addAll(other);
	}

	/**
	 * Create an empty list for a key. This overrides a previosly mapped list.
	 */
	public List<I> createList(K key) {
		List<I> list = new ArrayList<I>();
		entries.put(key, list);
		return list;
	}

	/**
	 * Get list for key.
	 * 
	 * 
	 * @return the list or <code>null</code>
	 */
	public List<I> getList(K key) {
		return entries.get(key);
	}

	/**
	 * Add an item to the list identified by a key.
	 * 
	 */
	public void add(K key, I item) {
		ensureListExists(key).add(item);
	}

	/**
	 * Add all items to the list identified by a key.
	 * 
	 */
	public void addAll(K key, Collection<I> items) {
		ensureListExists(key).addAll(items);
	}

	/** Adds all elements from another hashed list map. */
	public void addAll(HashedListMap<K, I> other) {
		for (K key : other.getKeys()) {
			List<I> list = other.getList(key);
			if (list != null) {
				addAll(key, list);
			}
		}
	}

	/**
	 * Check if a list is present for a given key.
	 */
	public boolean containsList(K key) {
		return entries.containsKey(key);
	}

	/**
	 * Removes the list stored for a key.
	 */
	public void removeList(K key) {
		entries.remove(key);
	}

	/**
	 * Removes the lists stored for a collection of keys
	 */
	public void removeAllLists(Collection<K> keys) {
		for (K key : keys) {
			removeList(key);
		}
	}

	/** Get keys. */
	public Set<K> getKeys() {
		return entries.keySet();
	}

	/** Return all values from all lists. */
	public List<I> getValues() {
		List<I> result = new ArrayList<I>();
		for (List<I> values : entries.values()) {
			result.addAll(values);
		}
		return result;
	}

	/**
	 * Checks if all lists stored in the map are empty. This returns true if
	 * {@link #getValues()} returns an empty list.
	 */
	public boolean areAllListsEmpty() {
		for (K key : entries.keySet()) {
			List<I> list = entries.get(key);
			if (!list.isEmpty()) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Clear map. This removes all lists.
	 */
	public void clear() {
		entries.clear();
	}

	/**
	 * Ensures that a list exists for a given key.
	 */
	private List<I> ensureListExists(K item) {

		if (!entries.containsKey(item)) {
			return createList(item);
		}
		return entries.get(item);
	}

	/**
	 * Converts the {@link HashedListMap} to a map with arrays
	 * 
	 * @param type
	 *            Type of the target array
	 */
	public Map<K, I[]> listsToArrays(Class<I> type) {
		Map<K, I[]> map = new HashMap<K, I[]>();
		for (K key : getKeys()) {
			map.put(key, CollectionUtils.toArray(getList(key), type));
		}
		return map;
	}

	/** Returns the number of keys contained. */
	public int getNumKeys() {
		return entries.size();
	}

	/**
	 * Returns the number of values stored. This may be smaller than
	 * {@link #getNumKeys()} if there are empty lists.
	 */
	public int getNumValues() {
		int result = 0;
		for (List<I> values : entries.values()) {
			result += values.size();
		}
		return result;
	}
}