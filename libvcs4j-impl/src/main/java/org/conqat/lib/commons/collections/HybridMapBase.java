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
import java.util.Map;
import java.util.Set;

import org.conqat.lib.commons.assertion.CCSMPre;

/**
 * A hybrid map is a map which starts with one map implementation, but switches
 * to another one after a certain size is reached.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: ECD2053719EED14FE88CA922DE21856F
 */
public abstract class HybridMapBase<K, V> implements Map<K, V> {

	/** The inner map. */
	private Map<K, V> map;

	/** Constructor. */
	protected HybridMapBase(Map<K, V> initialMap) {
		CCSMPre.isNotNull(initialMap);
		map = initialMap;
	}

	/**
	 * Template method for deciding that a switch of map implementation should
	 * be performed before the next insertion. This will be called right before
	 * each put operation. If this returns true, the new map implementation is
	 * obtained via {@link #obtainNewMap()} and all values are copied.
	 * 
	 * @param map
	 *            the currently used map.
	 * */
	protected abstract boolean shouldSwitch(Map<K, V> map);

	/**
	 * Template method for obtaining a new map implementation after
	 * {@link #shouldSwitch(Map)} returned true.
	 */
	protected abstract Map<K, V> obtainNewMap();

	/** {@inheritDoc} */
	@Override
	public void clear() {
		map.clear();
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
	public Set<java.util.Map.Entry<K, V>> entrySet() {
		return map.entrySet();
	}

	/** {@inheritDoc} */
	@Override
	public V get(Object key) {
		return map.get(key);
	}

	/** {@inheritDoc} */
	@Override
	public boolean isEmpty() {
		return map.isEmpty();
	}

	/** {@inheritDoc} */
	@Override
	public Set<K> keySet() {
		return map.keySet();
	}

	/** {@inheritDoc} */
	@Override
	public V put(K key, V value) {
		if (shouldSwitch(map)) {
			Map<K, V> oldMap = map;
			map = obtainNewMap();
			map.putAll(oldMap);
		}

		return map.put(key, value);
	}

	/** {@inheritDoc} */
	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		map.putAll(m);
	}

	/** {@inheritDoc} */
	@Override
	public V remove(Object key) {
		return map.remove(key);
	}

	/** {@inheritDoc} */
	@Override
	public int size() {
		return map.size();
	}

	/** {@inheritDoc} */
	@Override
	public Collection<V> values() {
		return map.values();
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return map.toString();
	}
}