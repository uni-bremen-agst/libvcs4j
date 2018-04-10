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

import java.util.LinkedHashMap;

import org.conqat.lib.commons.assertion.CCSMPre;

/**
 * Backend implementing LRU strategy.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 913240E8A931BB7F5D43FC3930D9A206
 */
/* package */class LRUCacheBackend<K, V> implements ICacheBackend<K, V> {

	/** The actual cache. */
	private final LinkedHashMap<K, V> cache;

	/** Max size of the cache. */
	private final int maxCacheSize;

	/** Constructor. */
	@SuppressWarnings("serial")
	public LRUCacheBackend(int maxSize) {
		CCSMPre.isTrue(maxSize > 0, "Maximal size must be positive!");

		maxCacheSize = maxSize;

		cache = new LinkedHashMap<K, V>(2 * maxSize, .6f, true) {
			@Override
			protected boolean removeEldestEntry(java.util.Map.Entry<K, V> eldest) {
				return size() > maxCacheSize;
			}
		};
	}

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
	public LRUCacheBackend<K, V> newInstance() {
		return new LRUCacheBackend<K, V>(maxCacheSize);
	}
}