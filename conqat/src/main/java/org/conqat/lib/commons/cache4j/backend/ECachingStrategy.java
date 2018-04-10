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

/**
 * Enumeration of supported caching strategies.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: CF60595D7C9C81484810FF46847AFDCB
 */
public enum ECachingStrategy {

	/** Performs no caching at all. */
	OFF,

	/**
	 * Caches exactly one element (basically the same as an LRU cache with size
	 * 1.
	 */
	SINGLE,

	/**
	 * Last recently used caching strategy. The parameter denotes the maximal
	 * number of elements stored.
	 */
	LRU,

	/**
	 * Memory sensitive caching using {@link SoftReference}s. This allows the
	 * cache to keep elements as long as memory is available, but free memory
	 * when requested by the garbage collector.
	 */
	MEMORY,

	/**
	 * Unlimited caching, i.e. the cache never discard data. This can lead to
	 * situations where the memory is exceeded.
	 */
	UNLIMITED;

	/**
	 * Returns the cache backend for the strategy (factory method).
	 * 
	 * @param parameter
	 *            the parameter used for the caching strategy. This is ignored
	 *            by those strategies that are not parameterizable.
	 */
	public <K, V> ICacheBackend<K, V> getBackend(int parameter) {
		switch (this) {
		case OFF:
			return new NoneCacheBackend<K, V>();
		case SINGLE:
			return new SingleElementCacheBackend<K, V>();
		case LRU:
			if (parameter < 1) {
				parameter = 10;
			}
			return new LRUCacheBackend<K, V>(parameter);
		case MEMORY:
			return new SoftRefCacheBackend<K, V>();
		case UNLIMITED:
			return new UnlimitedCacheBackend<K, V>();
		default:
			throw new AssertionError("Unknown enum value: " + this);
		}
	}
}