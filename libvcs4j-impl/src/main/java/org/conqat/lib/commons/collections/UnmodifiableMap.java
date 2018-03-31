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

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;

/**
 * This is a wrapper for a {@link Map} prohibiting all calls which would modify
 * its contents. As the construction of this class is performed in constant time
 * it is prefered over copying the map (which takes linear time). Using this
 * class is also preferred to using the <code>unmodifiableX()</code> in class
 * {@link Collections} as they return the collection base type that does not
 * signal, that the object ist unmodifiable. Using the classes in this package
 * makes unmodifiability more explicit.
 * <p>
 * All prohibited methods throw an {@link UnsupportedOperationException}. The
 * class is nearly the same as the one returned by
 * {@link Collections#unmodifiableMap(Map)}, but by making it a public class we
 * can make the return value of some methods more explicit.
 * <p>
 * This map is serializable if the wrapped map is serializable.
 * 
 * @author $Author: hummelb $
 * @version $Revision: 51464 $
 * @ConQAT.Rating GREEN Hash: A1BAE763AF66D945A48E4D1E30AD9C7B
 */
public class UnmodifiableMap<K, V> implements Map<K, V>, Serializable {

	/** Version used for serialization. */
	private static final long serialVersionUID = 1;

	/** The underlying map. */
	private final Map<K, V> map;

	/**
	 * Creates a new unmodifiable map from another map. All modifications to the
	 * underlying map will directly be visible in this wrapper.
	 */
	public UnmodifiableMap(Map<K, V> m) {
		if (m == null) {
			throw new IllegalArgumentException(
					"Underlying map may not be null!");
		}
		this.map = m;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isEmpty() {
		return map.isEmpty();
	}

	/** {@inheritDoc} */
	@Override
	public int size() {
		return map.size();
	}

	/** {@inheritDoc} */
	@Override
	public boolean containsKey(Object key) {
		return map.containsKey(key);
	}

	/** {@inheritDoc} */
	@Override
	public boolean containsValue(Object value) {
		return map.containsValue(value);
	}

	/** {@inheritDoc} */
	@Override
	public V get(Object key) {
		return map.get(key);
	}

	/** {@inheritDoc} */
	@Override
	public UnmodifiableSet<K> keySet() {
		return new UnmodifiableSet<K>(map.keySet());
	}

	/** {@inheritDoc} */
	@Override
	public UnmodifiableCollection<V> values() {
		return new UnmodifiableCollection<V>(map.values());
	}

	/** {@inheritDoc} */
	@Override
	public UnmodifiableSet<java.util.Map.Entry<K, V>> entrySet() {
		return new UnmodifiableSet<java.util.Map.Entry<K, V>>(map.entrySet());
	}

	/** Operation is not supported.  */
	@Override
	public void clear() {
		throw new UnsupportedOperationException();
	}

	/** Operation is not supported.  */
	@Override
	public V put(K arg0, V arg1) {
		throw new UnsupportedOperationException();
	}

	/** Operation is not supported.  */
	@Override
	public void putAll(Map<? extends K, ? extends V> arg0) {
		throw new UnsupportedOperationException();
	}

	/** Operation is not supported.  */
	@Override
	public V remove(Object arg0) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Returns a string representation of this map.
	 */
	@Override
	public String toString() {
		return map.toString();
	}

	/** {@inheritDoc} */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof UnmodifiableMap<?, ?>) {
			return map.equals(((UnmodifiableMap<?, ?>) obj).map);
		}
		return map.equals(obj);
	}

	/** {@inheritDoc} */
	@Override
	public int hashCode() {
		return map.hashCode();
	}
}