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
import java.util.List;
import java.util.Map;

import org.conqat.lib.commons.error.NeverThrownRuntimeException;
import org.conqat.lib.commons.factory.IFactory;

/**
 * Manages a map of lists, i.e. each key can store multiple elements.
 * 
 * @param <K>
 *            the key type.
 * @param <V>
 *            the value type (i.e. the values stored in the collections).
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 731DC32B96A79814844C8B0702E8B19F
 */
public class ListMap<K, V> extends CollectionMap<K, V, List<V>> {

	/**
	 * The factory used for list creation. We have to make this static, so is
	 * can be used in the calls to super(), but this also means we can not use
	 * the generic parameters of this class. While we could use an anonymous
	 * class in the constructor to circumvent this, we would create an instance
	 * of this factory for each instance for {@link ListMap}. To avoid this, we
	 * use the static but untyped variant here.
	 */
	private static final IFactory<List<?>, NeverThrownRuntimeException> factory = new IFactory<List<?>, NeverThrownRuntimeException>() {
		@Override
		public List<?> create() {
			return new ArrayList<Object>();
		}
	};

	/** Create new hashed list map. */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public ListMap() {
		// This cast is not nice, but it works
		super((IFactory) factory);
	}

	/** Create new hashed list map with a specified map. */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public ListMap(Map<K, List<V>> map) {
		// This cast is not nice, but it works
		super(map, (IFactory) factory);
	}

	/** Copy constructor. */
	public ListMap(ListMap<K, V> other) {
		this();
		addAll(other);
	}

}