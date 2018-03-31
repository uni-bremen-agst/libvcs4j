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

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.conqat.lib.commons.error.NeverThrownRuntimeException;
import org.conqat.lib.commons.factory.IFactory;

/**
 * Manages a map of sets, i.e. each key can store multiple elements.
 * 
 * @param <K>
 *            the key type.
 * @param <V>
 *            the value type (i.e. the values stored in the collections).
 * 
 * @author $Author: hummelb $
 * @version $Rev: 38839 $
 * @ConQAT.Rating GREEN Hash: 7A16EF2527D04C6B207035DCDF095337
 */
public class SetMap<K, V> extends CollectionMap<K, V, Set<V>> {

	/**
	 * The factory used for set creation. We have to make this static, so it can
	 * be used in the calls to super(), but this also means we can not use the
	 * generic parameters of this class. While we could use an anonymous class
	 * in the constructor to circumvent this, we would create an instance of
	 * this factory for each instance for {@link SetMap}. To avoid this, we use
	 * the static but untyped variant here.
	 */
	private static final IFactory<Set<?>, NeverThrownRuntimeException> factory = new IFactory<Set<?>, NeverThrownRuntimeException>() {
		@Override
		public Set<?> create() {
			return new HashSet<Object>();
		}
	};

	/** Create new hashed list map. */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public SetMap() {
		// This cast is not nice, but it works
		super((IFactory) factory);
	}

	/** Create new hashed list map with a specified map. */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public SetMap(Map<K, Set<V>> map) {
		// This cast is not nice, but it works
		super(map, (IFactory) factory);
	}

	/** Copy constructor. */
	public SetMap(SetMap<K, V> other) {
		this();
		addAll(other);
	}
}