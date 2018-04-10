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
 * A 2-dimensional hash map. Allows storage of items identified by two different
 * keys. This can be used to store the following data structure:
 * 
 * <ul>
 * <li>Project A
 * <ul>
 * <li>Dan &mdash; <b>Testing </b></li>
 * <li>Flo &mdash; <b>Documentation </b></li>
 * </ul>
 * </li>
 * <li>Project B
 * <ul>
 * <li>Flo &mdash; <b>Design </b></li>
 * <li>Dan &mdash; <b>QA </b></li>
 * <li>Markus &mdash; <b>CM </b></li>
 * <li>Jorge &mdash; <b>Testing </b></li>
 * </ul>
 * </li>
 * </ul>
 * 
 * @author $Author: streitel $
 * @version $Revision: 51656 $
 * @ConQAT.Rating GREEN Hash: 9506E5B988EF07887307633F67FBBE92
 */
public class TwoDimHashMap<K1, K2, V> {

	/** The first level map. */
	private final Map<K1, Map<K2, V>> data;

	/** Create a new doubly hashed map. */
	public TwoDimHashMap() {
		data = new HashMap<K1, Map<K2, V>>();
	}

	/** Create a new two dimensional map using the data in the given map. */
	public TwoDimHashMap(Map<K1, Map<K2, V>> map) {
		data = map;
	}

	/** Put all values of another {@link TwoDimHashMap} into this map. */
	public void putAll(TwoDimHashMap<K1, K2, V> otherMap) {
		for (K1 key1 : otherMap.getFirstKeys()) {
			for (K2 key2 : otherMap.getSecondKeys(key1)) {
				V value = otherMap.getValue(key1, key2);
				putValue(key1, key2, value);
			}
		}
	}

	/**
	 * Puts the given value into this map under the given keys. Any potentially
	 * existing value will be overwritten.
	 * 
	 * @param key1
	 *            first level key
	 * @param key2
	 *            second level key
	 * @param value
	 *            the value
	 */
	public void putValue(K1 key1, K2 key2, V value) {
		Map<K2, V> map = data.get(key1);
		if (map == null) {
			map = new HashMap<K2, V>();
			data.put(key1, map);
		}
		map.put(key2, value);
	}

	/**
	 * Get a value by specifying first and second level key.
	 * 
	 * @param firstKey
	 *            first level key
	 * @param secondKey
	 *            second level key
	 * @return the value. Is <code>null</code> if first or second level key does
	 *         not exist or if <code>null</code> was explicitly stored.
	 */
	public V getValue(K1 firstKey, K2 secondKey) {
		Map<K2, V> map = data.get(firstKey);
		if (map == null) {
			return null;
		}
		return map.get(secondKey);
	}

	/**
	 * Returns whether the given key combination is available in the map.
	 * 
	 * @param firstKey
	 *            first level key
	 * @param secondKey
	 *            second level key
	 */
	public boolean containsKey(K1 firstKey, K2 secondKey) {
		Map<K2, V> map = data.get(firstKey);
		if (map == null) {
			return false;
		}
		return map.containsKey(secondKey);
	}

	/**
	 * Get all values referenced by a first level key.
	 * 
	 * @param firstKey
	 *            the first level key
	 * @return a list of values referenced by the specified first level key
	 */
	public Collection<V> getValuesByFirstKey(K1 firstKey) {
		Map<K2, V> map = data.get(firstKey);
		if (map == null) {
			return null;
		}
		return map.values();

	}

	/**
	 * Get all first level keys.
	 */
	public Set<K1> getFirstKeys() {
		return data.keySet();
	}

	/**
	 * Get all the second level keys stored under the given first level key.
	 * 
	 * @param firstKey
	 *            the first level key.
	 * @return all second level keys for a first level key.
	 */
	public Set<K2> getSecondKeys(K1 firstKey) {
		Map<K2, V> map = data.get(firstKey);
		if (map == null) {
			return CollectionUtils.emptySet();
		}
		return map.keySet();
	}

	/**
	 * Get all values referenced by a second level key.
	 * 
	 * <b>Note: </b> This method's complexity is linear in the number of first
	 * level keys.
	 * 
	 * @param secondKey
	 *            the second level key
	 * @return a new list of values referenced by the specified second level key
	 */
	public List<V> getValuesBySecondKey(K2 secondKey) {
		ArrayList<V> result = new ArrayList<V>();

		for (Map<K2, V> map : data.values()) {
			if (map.containsKey(secondKey)) {
				result.add(map.get(secondKey));
			}
		}

		return result;
	}

	/**
	 * Get all values stored in the map.
	 * 
	 * @return a new list of all values.
	 */
	public List<V> getValues() {
		ArrayList<V> result = new ArrayList<V>();

		for (Map<K2, V> map : data.values()) {
			result.addAll(map.values());
		}

		return result;
	}

	/**
	 * Get size of the map.
	 * 
	 * @return the number of values stored in this map.
	 */
	public int getSize() {
		int size = 0;
		for (Map<K2, V> map : data.values()) {
			size += map.size();
		}
		return size;
	}

	/**
	 * Check if the map is empty, i.e. no values are stored in it.
	 */
	public boolean isEmpty() {
		return getSize() == 0;
	}

	/**
	 * Get the size of the (second) map stored for a first key.
	 * 
	 * @return the size or 0 if the first level key wasn't found.
	 */
	public int getSecondSize(K1 key1) {
		Map<K2, V> map = data.get(key1);
		if (map == null) {
			return 0;
		}
		return map.size();
	}

	/**
	 * Clear the whole map.
	 */
	public void clear() {
		data.clear();
	}

	/**
	 * Removes the value associated with the given key combination.
	 * 
	 * @return previous value associated with specified keys, or
	 *         <code>null</code> if there was no mapping for those keys. A
	 *         <code>null</code> return can also indicate that the map
	 *         previously associated <code>null</code> with the specified keys.
	 */
	public V remove(K1 key1, K2 key2) {
		Map<K2, V> map = data.get(key1);
		if (map == null) {
			return null;
		}

		if (!map.containsKey(key2)) {
			return null;
		}

		V result = map.remove(key2);

		if (map.isEmpty()) {
			data.remove(key1);
		}

		return result;
	}

	/**
	 * Remove all values stored under the given first level key.
	 * 
	 * @param key
	 *            first level key
	 * @return <code>true</code> if the given key was present in the map,
	 *         <code>false</code> otherwise.
	 */
	public boolean remove(K1 key) {
		Map<K2, V> result = data.remove(key);
		return result != null;
	}

	/**
	 * Returns the data stored under the given first-level key as an
	 * unmodifiable map or <code>null</code> if nothing is stored under that
	 * key.
	 */
	public UnmodifiableMap<K2, V> getSecondMap(K1 key) {
		Map<K2, V> secondMap = data.get(key);
		if (secondMap == null) {
			return null;
		}
		return CollectionUtils.asUnmodifiable(secondMap);
	}
}