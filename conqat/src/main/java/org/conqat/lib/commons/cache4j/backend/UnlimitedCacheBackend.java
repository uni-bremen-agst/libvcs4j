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

import java.util.HashMap;
import java.util.Map;

/**
 * Backend implementing unlimited caching.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 2E53527D034C9FD60DBBEE91EE0C4797
 */
/* package */class UnlimitedCacheBackend<K, V> implements
		ICacheBackend<K, V> {

	/** The actual cache. */
	private final Map<K, V> cache = new HashMap<K, V>();

	/** {@inheritDoc} */
	@Override
	public void store(K key, V value) {
		cache.put(key, value);
	}

	/** {@inheritDoc} */
	@Override
	public V retrieve(K key) {
		return cache.get(key);
	}

	/** {@inheritDoc} */
	@Override
	public UnlimitedCacheBackend<K, V> newInstance() {
		return new UnlimitedCacheBackend<K, V>();
	}
}