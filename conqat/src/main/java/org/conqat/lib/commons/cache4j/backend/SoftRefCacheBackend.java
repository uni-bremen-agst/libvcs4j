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

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Backend implementing a memory sensitive cache using {@link SoftReference}s.
 * This implementation has two drawbacks. First, keys are not freed
 * automatically and second there are situations with large heaps, where the VM
 * exits with GC problems although sufficient memory is available. To deal with
 * the first issue, we implement regular cleaning intervals that sweep entries
 * with stale values. For the second problem, you can adjust the VM parameters
 * as described in the ConQAT book.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: B01F987515EA5D3C8A6A6B47F08B24EF
 */
/* package */class SoftRefCacheBackend<K, V> implements ICacheBackend<K, V> {

	/**
	 * Number of minimum calls to {@link #store(Object, Object)} after which
	 * {@link #cleanUp()} is called.
	 */
	private static final int CLEANUP_INTERVAL = 1000;

	/** Counts the number of calls to {@link #store(Object, Object)}. */
	private int storeCalls = 0;

	/** The actual cache. */
	private final Map<K, SoftReference<V>> cache = new HashMap<K, SoftReference<V>>();

	/** {@inheritDoc} */
	@Override
	public void store(K key, V value) {
		cache.put(key, new SoftReference<V>(value));
		storeCalls += 1;

		// we perform a cleanup only every CLEANUP_INTERVAL steps. To avoid bad
		// performance for large caches, we also use the cache size as a lower
		// limit. This way we run in amortized constant time.
		if (storeCalls >= Math.max(CLEANUP_INTERVAL, cache.size())) {
			storeCalls = 0;
			cleanUp();
		}
	}

	/**
	 * Performs a clean up to discard unused keys. This is called regularly, as
	 * otherwise the keys (which are not garbage collected) can accumulate and
	 * waste memory.
	 */
	private void cleanUp() {
		List<K> staleKeys = new ArrayList<K>();
		for (Entry<K, SoftReference<V>> entry : cache.entrySet()) {
			if (entry.getValue() == null || entry.getValue().get() == null) {
				staleKeys.add(entry.getKey());
			}
		}

		for (K key : staleKeys) {
			cache.remove(key);
		}
	}

	/** {@inheritDoc} */
	@Override
	public V retrieve(K key) {
		SoftReference<V> softReference = cache.get(key);
		if (softReference == null) {
			return null;
		}
		return softReference.get();
	}

	/** {@inheritDoc} */
	@Override
	public SoftRefCacheBackend<K, V> newInstance() {
		return new SoftRefCacheBackend<K, V>();
	}
}