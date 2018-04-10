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
import java.util.Set;

import org.conqat.lib.commons.reflect.ReflectionUtils;

/**
 * This class provides a mapping from classes to values. The speciality of this
 * class lies in its awareness of the class hierarchy: If no value was found
 * this class tries to retrieve values stored for the super classes of the
 * provided class. If values are stored for multiple super classes of the class
 * the one value that maps to the super class closest to the provided class will
 * be returned.
 * <p>
 * 
 * @param <T>
 *            This type parameter allows to specify a lower bound of the classes
 *            used as keys. If this is unnecessary, use {@link Object}.
 * @author Florian Deissenboeck
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 7E176191D7D5CF6F43825F60535527AE
 */
public class ClassHierarchyMap<T, V> {
	/** Underlying map. */
	private final HashMap<Class<? extends T>, V> map = new HashMap<Class<? extends T>, V>();

	/** @see java.util.Map#clear() */
	public void clear() {
		map.clear();
	}

	/**
	 * Get value stored for a class. If no value was found this method tries to
	 * retrieve values stored for the super classes of the provided class. If
	 * values are stored for multiple super classes of the class the one value
	 * that maps to the super class closest to the provided class will be
	 * returned.
	 * <p>
	 * If a key is stored for the provided class the performance of this method
	 * equals the performance of {@link HashMap#get(Object)}. Otherwise its
	 * worst case performance is O(DIT(key)).
	 * 
	 * @param key
	 *            the key
	 * @return the value stored for the provided key or one if its super classes
	 *         or <code>null</code> if no value was found.
	 */
	public V get(Class<?> key) {
		V value = map.get(key);
		if (value != null) {
			return value;
		}

		List<Class<?>> superClasses = ReflectionUtils.getSuperClasses(key);

		for (Class<?> clazz : superClasses) {
			value = map.get(clazz);
			if (value != null) {
				return value;
			}
		}
		return null;
	}

	/**
	 * Get value stored for the declaring class of the provided element.
	 * 
	 * @see #get(Class)
	 */
	public V get(T element) {
		return get(element.getClass());
	}

	/**
	 * Retrieve a list of values stored for the provided class and its super
	 * classes. List starts with the value stored for the provided class (if
	 * present).
	 * 
	 * @return the list of values or an empty list of no value was found.
	 */
	public List<V> getAll(Class<?> key) {
		ArrayList<V> list = new ArrayList<V>();

		List<Class<?>> classes = ReflectionUtils.getSuperClasses(key);

		classes.add(0, key);

		for (Class<?> clazz : classes) {
			V value = map.get(clazz);

			if (value != null) {
				list.add(value);
			}
		}

		return list;
	}

	/**
	 * Get value list for the declaring class of the provided element.
	 * 
	 * @see #getAll(Class)
	 */
	public List<V> getAll(T element) {
		return getAll(element.getClass());
	}

	/**
	 * Get value stored for this class. Unlike {@link #get(Class)} this does not
	 * retrieve values stored for super classes.
	 */
	public V getDeclared(Class<?> key) {
		return map.get(key);
	}

	/**
	 * Get value stored for the declaring class of the provided element. Unlike
	 * {@link #get(Object)} this does not retrieve values stored for super
	 * classes.
	 */
	public V getDeclared(T element) {
		return getDeclared(element.getClass());
	}

	/**
	 * @see java.util.Map#isEmpty()
	 */
	public boolean isEmpty() {
		return map.isEmpty();
	}

	/**
	 * @see java.util.Map#keySet()
	 */
	public Set<Class<? extends T>> keySet() {
		return map.keySet();
	}

	/**
	 * Store a key-value-pair.
	 * 
	 * @see java.util.Map#put(Object, Object)
	 */
	public V put(Class<? extends T> key, V value) {
		return map.put(key, value);
	}

	/**
	 * @see java.util.Map#remove(Object)
	 */
	public V remove(Class<?> key) {
		return map.remove(key);
	}

	/**
	 * @see java.util.Map#size()
	 */
	public int size() {
		return map.size();
	}

	/**
	 * @see java.util.Map#values()
	 */
	public Collection<V> values() {
		return map.values();
	}
}