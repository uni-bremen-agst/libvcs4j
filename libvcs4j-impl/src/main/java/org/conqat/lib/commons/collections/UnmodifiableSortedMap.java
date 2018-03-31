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

import java.util.Collections;
import java.util.Comparator;
import java.util.SortedMap;

/**
 * This is a wrapper for a {@link SortedMap} prohibiting all calls which would
 * modify its contents. As the construction of this class is performed in
 * constant time it is prefered over copying the map (which takes linear time).
 * Using this class is also preferred to using the <code>unmodifiableX()</code>
 * in class {@link Collections} as they return the collection base type that
 * does not signal, that the object ist unmodifiable. Using the classes in this
 * package makes unmodifiability more explicit.
 * <p>
 * All prohibited methods throw an {@link UnsupportedOperationException}. The
 * class is nearly the same as the one returned by
 * {@link Collections#unmodifiableSortedMap(SortedMap)}, but by making it a
 * public class we can make the return value of some methods more explicit.
 * 
 * @author Benjamin Hummel
 * @author $Author: kinnen $
 * 
 * @version $Revision: 41751 $
 * @ConQAT.Rating GREEN Hash: 22E47E591BAF8086AA89AA6F00E02B8B
 */
public class UnmodifiableSortedMap<K, V> extends UnmodifiableMap<K, V>
		implements SortedMap<K, V> {

	/** Version used for serialization. */
	private static final long serialVersionUID = 1;

	/** The underlying sorted map. */
	private final SortedMap<K, V> m;

	/**
	 * Creates a new unmodifiable sorted map from another sorted map. All
	 * modifications to the underlying map will directly be visible in this
	 * wrapper.
	 */
	public UnmodifiableSortedMap(SortedMap<K, V> m) {
		super(m);
		this.m = m;
	}

	/** {@inheritDoc} */
	@Override
	public Comparator<? super K> comparator() {
		return m.comparator();
	}

	/** {@inheritDoc} */
	@Override
	public K firstKey() {
		return m.firstKey();
	}

	/** {@inheritDoc} */
	@Override
	public UnmodifiableSortedMap<K, V> headMap(K toKey) {
		return new UnmodifiableSortedMap<K, V>(m.headMap(toKey));
	}

	/** {@inheritDoc} */
	@Override
	public K lastKey() {
		return m.lastKey();
	}

	/** {@inheritDoc} */
	@Override
	public UnmodifiableSortedMap<K, V> subMap(K fromKey, K toKey) {
		return new UnmodifiableSortedMap<K, V>(m.subMap(fromKey, toKey));
	}

	/** {@inheritDoc} */
	@Override
	public UnmodifiableSortedMap<K, V> tailMap(K fromKey) {
		return new UnmodifiableSortedMap<K, V>(m.tailMap(fromKey));
	}
}