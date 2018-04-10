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
package org.conqat.lib.commons.cache4j.backend;

/**
 * Backend that stores at most one element.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 20B2D0FF6B2139A9DEF7AA7335E7BD33
 */
/* package */class SingleElementCacheBackend<K, V> implements
		ICacheBackend<K, V> {

	/** The stored key. */
	private K storedKey;

	/** The stored value. */
	private V storedValue;

	/** {@inheritDoc} */
	@Override
	public void store(K key, V value) {
		storedKey = key;
		storedValue = value;
	}

	/** {@inheritDoc} */
	@Override
	public V retrieve(K key) {
		if (storedKey != null && storedKey.equals(key)) {
			return storedValue;
		}
		return null;
	}

	/** {@inheritDoc} */
	@Override
	public SingleElementCacheBackend<K, V> newInstance() {
		return new SingleElementCacheBackend<K, V>();
	}
}