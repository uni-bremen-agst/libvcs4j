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

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.conqat.lib.commons.assertion.CCSMPre;
import org.conqat.lib.commons.error.NeverThrownRuntimeException;
import org.conqat.lib.commons.factory.IFactory;

/**
 * A collection map deals with a map of collections, i.e. each key can store
 * multiple elements. Depending on the collection implementation chosen, this
 * can also be interpreted as a multi map.
 * <p>
 * If you deal with the basic case of {@link Map}s and {@link List}s, use the
 * {@link ListMap}, which is much easier to apply.
 * 
 * @param <K>
 *            the key type.
 * @param <V>
 *            the value type (i.e. the values stored in the collections).
 * @param <C>
 *            the collection type, which is made explicit at the interface.
 * 
 * @author $Author: poehlmann $
 * @version $Rev: 48895 $
 * @ConQAT.Rating GREEN Hash: 5C57C2BBBB9DAE63ADEA6328619EC308
 */
public class CollectionMap<K, V, C extends Collection<V>> {

	/** The underlying map. */
	private final Map<K, C> map;

	/** The factory used for creating collections. */
	private final IFactory<C, NeverThrownRuntimeException> collectionFactory;

	/** Constructor using a {@link HashMap} as underlying map. */
	public CollectionMap(
			IFactory<C, NeverThrownRuntimeException> collectionFactory) {
		this(new HashMap<K, C>(), collectionFactory);
	}

	/** Constructor. */
	public CollectionMap(Map<K, C> map,
			IFactory<C, NeverThrownRuntimeException> collectionFactory) {
		CCSMPre.isNotNull(map);
		CCSMPre.isNotNull(collectionFactory);

		this.map = map;
		this.collectionFactory = collectionFactory;
	}

	/**
	 * Returns the collection stored under the given key (or null). Modifying
	 * the collection will directly affect this object.
	 */
	public C getCollection(K key) {
		return map.get(key);
	}

	/**
	 * Adds a value to the collection associated with the given key.
	 * 
	 * @return <code>true</code> if the collection associated with the given key
	 *         changed as a result of the call.
	 */
	public boolean add(K key, V value) {
		return getOrCreateCollection(key).add(value);
	}

	/**
	 * Adds all values to the collection associated with the given key.
	 * 
	 * @return <code>true</code> if the collection associated with the given key
	 *         changed as a result of the call.
	 */
	public boolean addAll(K key, Collection<? extends V> values) {
		return getOrCreateCollection(key).addAll(values);
	}

	/**
	 * Returns the collection stored under the given key (or creates a new one).
	 */
	/* package */C getOrCreateCollection(K key) {
		C collection = map.get(key);
		if (collection == null) {
			collection = collectionFactory.create();
			map.put(key, collection);
		}
		return collection;
	}

	/** Adds all elements from another collection map. */
	public void addAll(CollectionMap<K, V, C> other) {
		for (K key : other.getKeys()) {
			C collection = other.getCollection(key);
			if (collection != null) {
				addAll(key, collection);
			}
		}
	}

	/** Returns whether an element is contained. */
	public boolean contains(K key, V value) {
		C collection = map.get(key);
		if (collection == null) {
			return false;
		}
		return collection.contains(value);
	}

	/**
	 * Removes an element.
	 * 
	 * @return true if an element was removed as a result of this call.
	 */
	public boolean remove(K key, V value) {
		C collection = map.get(key);
		if (collection == null) {
			return false;
		}
		return collection.remove(value);
	}

	/**
	 * Check if a (possibly empty) collection is present for a given key.
	 */
	public boolean containsCollection(K key) {
		return map.containsKey(key);
	}

	/**
	 * Removes the collection stored for a key.
	 * 
	 * @return true if a collection was removed as a result of this call.
	 */
	public boolean removeCollection(K key) {
		return map.remove(key) != null;
	}

	/** Get the keys. */
	public UnmodifiableSet<K> getKeys() {
		return CollectionUtils.asUnmodifiable(map.keySet());
	}

	/** Return all values from all collections. */
	public C getValues() {
		C result = collectionFactory.create();
		for (C values : map.values()) {
			result.addAll(values);
		}
		return result;
	}

	/**
	 * Returns the number of key-value mappings in this map.
	 */
	public int size() {
		return map.size();
	}

	/** Return the total count of values over all collections. */
	public int getValueCount() {
		int result = 0;
		for (C values : map.values()) {
			result += values.size();
		}
		return result;
	}

	/** Clears the underlying map and thus all contents. */
	public void clear() {
		map.clear();
	}

	/**
	 * Converts the {@link CollectionMap} to a map with arrays
	 * 
	 * @param type
	 *            Type of the target array
	 */
	public Map<K, V[]> collectionsToArrays(Class<V> type) {
		Map<K, V[]> map = new HashMap<K, V[]>();
		for (K key : getKeys()) {
			map.put(key, CollectionUtils.toArray(getCollection(key), type));
		}
		return map;
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return map.toString();
	}
}