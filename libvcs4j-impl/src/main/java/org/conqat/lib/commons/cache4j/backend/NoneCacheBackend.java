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
 * Backend that caches nothing.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: A3BD90B13F2E599E82B3018C0472679B
 */
/* package */class NoneCacheBackend<K, V> implements ICacheBackend<K, V> {

	/** {@inheritDoc} */
	@Override
	public void store(K key, V value) {
		// does nothing
	}

	/** {@inheritDoc} */
	@Override
	public V retrieve(K key) {
		return null;
	}

	/** {@inheritDoc} */
	@Override
	public NoneCacheBackend<K, V> newInstance() {
		return new NoneCacheBackend<K, V>();
	}
}