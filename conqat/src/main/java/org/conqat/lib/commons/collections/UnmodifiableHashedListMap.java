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

import java.util.List;

/**
 * This is a wrapper for a {@link HashedListMap} prohibiting all calls that
 * would modify its contents. As the construction of this class is performed in
 * constant time it is prefered over copying the collection (which takes linear
 * time). All prohibited methods throw an {@link UnsupportedOperationException}.
 * 
 * @deprecated This class does not work correctly and it is not easy to fix it
 *             (in a maintainable way) without introducing an interface for it.
 *             Additionally, it should be replaced by {@link ListMap}. The
 *             solution is to not expose the {@link HashedListMap} at the
 *             (public) interface of your class. Also see CR#3394.
 * 
 * @author $Author: kinnen $
 * @version $Revision: 41751 $
 * @ConQAT.Rating GREEN Hash: 801F5E2C63166EA5D385C3177284D3EF
 */
@Deprecated
public class UnmodifiableHashedListMap<K, I> extends HashedListMap<K, I> {

	/** The wrapped list. */
	private final HashedListMap<K, I> map;

	/**
	 * Create new unmodifiable hashed list map.
	 * 
	 * @param map
	 *            the map to wrap
	 * @throws IllegalArgumentException
	 *             if map is <code>null</code>
	 */
	public UnmodifiableHashedListMap(HashedListMap<K, I> map) {
		if (map == null) {
			throw new IllegalArgumentException(
					"Underlying map may not be null!");
		}
		this.map = map;
	}

	/**
	 * Operation is not supported.
	 * 
	 * @throws UnsupportedOperationException
	 */
	@Override
	public List<I> createList(K key) {
		throw new UnsupportedOperationException();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UnmodifiableList<I> getList(K key) {
		List<I> list = map.getList(key);
		if (list == null) {
			return null;
		}
		return CollectionUtils.asUnmodifiable(list);
	}

	/**
	 * Operation is not supported.
	 * 
	 * @throws UnsupportedOperationException
	 */
	@Override
	public void add(K key, I item) {
		throw new UnsupportedOperationException();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean containsList(K key) {
		return map.containsList(key);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UnmodifiableSet<K> getKeys() {
		return CollectionUtils.asUnmodifiable(map.getKeys());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UnmodifiableList<I> getValues() {
		return CollectionUtils.asUnmodifiable(map.getValues());
	}

}