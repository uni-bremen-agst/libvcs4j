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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A map implementation based on unsorted arrays. This is by far more memory
 * efficient than the usual map implementations and has reasonable performance
 * for small maps. Note that this map violates the map interface by just
 * returning copies for the set accessor methods ({@link #entrySet()},
 * {@link #values()}, {@link #keySet()}), i.e. they are not backed by the map.
 * <p>
 * Implementation hints:
 * <ul>
 * <li>Nearly all operations require a full traversal of the array (resp.
 * PairList).</li>
 * <li>Iteration is performed backwards, to avoid frequent calls to size()
 * method. This also gives more efficient access to recently added elements.</li>
 * <li>This class is prepared to support subclasses with more specific keys with
 * more efficient key handling. Thus all keys inserted are preprocessed and
 * comparison of keys can be overwritten.</li>
 * </ul>
 * 
 * @author $Author: hummelb $
 * @version $Rev: 51643 $
 * @ConQAT.Rating GREEN Hash: FAE5EA56F412D00C09788B13AF72A28A
 */
public class ArrayBackedMap<K, V> implements Map<K, V> {

	/** The underlying list used for storing the entries. */
	private final PairList<K, V> list;

	/** Constructs a new map with an initial capacity of 4. */
	public ArrayBackedMap() {
		this(4);
	}

	/** Constructor. */
	public ArrayBackedMap(int initialCapacity) {
		list = new PairList<K, V>(initialCapacity);
	}

	/** Copy constructor. */
	public ArrayBackedMap(ArrayBackedMap<K, V> other) {
		list = new PairList<K, V>(other.list);
	}

	/** {@inheritDoc} */
	@Override
	public void clear() {
		list.clear();
	}

	/** {@inheritDoc} */
	@Override
	public boolean containsKey(Object key) {
		try {
			K cleanKey = internKey(key);
			for (int i = list.size() - 1; i >= 0; --i) {
				if (areEqual(cleanKey, list.getFirst(i))) {
					return true;
				}
			}
			return false;
		} catch (ClassCastException e) {
			return false;
		}
	}

	/**
	 * Template method for calculating an internal key representation. The
	 * default implementation just performs a cast. This method may throw a
	 * class cast exception if the provided key is not an instance of the key
	 * type.
	 * 
	 * @throws ClassCastException
	 *             if the provided key is not of a suitable class.
	 */
	@SuppressWarnings("unchecked")
	protected K internKey(Object key) throws ClassCastException {
		return (K) key;
	}

	/** Template method for comparing two keys for equality. */
	protected boolean areEqual(K key1, K key2) {
		if (key1 == null) {
			return key2 == null;
		}
		return key1.equals(key2);
	}

	/** {@inheritDoc} */
	@Override
	public V get(Object key) {
		try {
			K cleanKey = internKey(key);
			for (int i = list.size() - 1; i >= 0; --i) {
				if (areEqual(cleanKey, list.getFirst(i))) {
					return list.getSecond(i);
				}
			}
			return null;
		} catch (ClassCastException e) {
			return null;
		}
	}

	/** {@inheritDoc} */
	@Override
	public V put(K key, V value) {
		// no catch clause here, as key must be of correct type (interface
		// contract)
		K cleanKey = internKey(key);

		for (int i = list.size() - 1; i >= 0; --i) {
			if (areEqual(cleanKey, list.getFirst(i))) {
				V oldValue = list.getSecond(i);
				list.setSecond(i, value);
				return oldValue;
			}
		}

		list.add(cleanKey, value);
		return null;
	}

	/** {@inheritDoc} */
	@Override
	public V remove(Object key) {
		try {
			K cleanKey = internKey(key);
			for (int i = list.size() - 1; i >= 0; --i) {
				if (areEqual(cleanKey, list.getFirst(i))) {
					V oldValue = list.getSecond(i);
					int last = list.size() - 1;
					if (i != last) {
						list.setFirst(i, list.getFirst(last));
						list.setSecond(i, list.getSecond(last));
					}
					list.removeLast();
					return oldValue;
				}
			}
			return null;
		} catch (ClassCastException e) {
			return null;
		}
	}

	/** {@inheritDoc} */
	@Override
	public boolean containsValue(Object value) {
		for (int i = list.size() - 1; i >= 0; --i) {

			// can not use areEqual(), as we work on values and not keys here.
			if (value == null) {
				if (list.getSecond(i) == null) {
					return true;
				}
			} else {
				if (value.equals(list.getSecond(i))) {
					return true;
				}
			}
		}
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public Set<java.util.Map.Entry<K, V>> entrySet() {
		Map<K, V> map = new HashMap<K, V>();
		for (int i = list.size() - 1; i >= 0; --i) {
			map.put(list.getFirst(i), list.getSecond(i));
		}
		return map.entrySet();
	}

	/** {@inheritDoc} */
	@Override
	public boolean isEmpty() {
		return list.isEmpty();
	}

	/** {@inheritDoc} */
	@Override
	public Set<K> keySet() {
		return new HashSet<K>(list.extractFirstList());
	}

	/** {@inheritDoc} */
	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		for (Entry<? extends K, ? extends V> e : m.entrySet()) {
			put(e.getKey(), e.getValue());
		}
	}

	/** {@inheritDoc} */
	@Override
	public int size() {
		return list.size();
	}

	/** {@inheritDoc} */
	@Override
	public Collection<V> values() {
		return list.extractSecondList();
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return list.toString();
	}

	/** {@inheritDoc} */
	@Override
	public int hashCode() {
		return list.hashCode();
	}

	/** {@inheritDoc} */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof ArrayBackedMap)) {
			return false;
		}
		return list.equals(((ArrayBackedMap<?, ?>) obj).list);
	}

}