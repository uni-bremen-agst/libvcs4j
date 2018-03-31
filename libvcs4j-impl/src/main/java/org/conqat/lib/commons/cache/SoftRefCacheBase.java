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
package org.conqat.lib.commons.cache;

import java.lang.ref.SoftReference;
import java.util.HashMap;

import org.conqat.lib.commons.error.NeverThrownRuntimeException;

/**
 * A base class for dynamic caches based on {@link SoftReference}s. If
 * identifiers itself are suitable hash keys, use class
 * {@link org.conqat.lib.commons.cache.SoftRefStraightCacheBase}.
 * <p>
 * The implementation is memory-sensitive, i.e. it dynamically removes entries
 * from the cache if the virtual machine is short on memory. However, this
 * dynamic is completely transparent to the user.
 * <p>
 * <b>Note:</b> To make this cache efficient the virtual machine must work in
 * server mode.
 * 
 * @author Florian Deissenboeck
 * @author Tilman Seifert
 * @author $Author: kinnen $
 * 
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: EBA83AEC6B34DC6DAE7D167CBEC88DDD
 * 
 * @param <I>
 *            the index type of the cache
 * @param <H>
 *            the hash map key type
 * @param <E>
 *            the type stored in the cache
 * @param <X>
 *            the type of exception thrown by the {@link #obtainItem(Object)}
 *            method. Use the {@link NeverThrownRuntimeException} if no
 *            exception will be thrown.
 */
public abstract class SoftRefCacheBase<I, H, E, X extends Exception> extends
		CacheBase<I, H, E, X> {

	/** Number of times the cache was accessed (for debugging purposes only) */
	private static int accessCounter = 0;

	/** Number of cache hits (for debugging purposes only) */
	private static int hitCounter = 0;

	/**
	 * Number of cache misses that were caused by accesses to items that were
	 * never cached (for debugging purposes only)
	 */
	private static int missBecauseNotInCacheCounter = 0;

	/**
	 * Number of cache misses that were caused by accesses to items that were
	 * removed from cache by garbage collection (for debugging purposes only)
	 */
	private static int missBecauseRemovedFromCacheCounter = 0;

	/** The actual cache. */
	protected final HashMap<H, SoftReference<E>> cache = new HashMap<H, SoftReference<E>>();

	/** {@inheritDoc} */
	@Override
	public E getItem(I identifier) throws X {
		accessCounter++;

		// check if item is cached
		SoftReference<E> ref = cache.get(getHashKey(identifier));
		if (ref == null) {
			return obtainUncachedItem(identifier);
		}

		// item was cached
		E item = ref.get();

		// check if item was garbage collected
		if (item == null) {
			return obtainGarbageCollectedItem(identifier);
		}

		// return item
		hitCounter++;
		return item;
	}

	/** Obtain (and cache) and item which was killed by the GC. */
	private E obtainGarbageCollectedItem(I identifier) throws X {
		missBecauseRemovedFromCacheCounter++;
		E item = obtainItem(identifier);

		// if null remove from cache, otherwise re-cache
		if (item == null) {
			cache.remove(getHashKey(identifier));
		} else {
			cache.put(getHashKey(identifier), new SoftReference<E>(item));
		}
		return item;
	}

	/** Obtain (and cache) an uncached item. */
	private E obtainUncachedItem(I identifier) throws X {
		E item = obtainItem(identifier);

		// only cache if not null
		if (item != null) {
			cache.put(getHashKey(identifier), new SoftReference<E>(item));
			missBecauseNotInCacheCounter++;
		}
		return item;
	}

	/**
	 * Returns a statistics string with information about cache hits and misses
	 * for debugging purposes. As this is implemented in a static way results
	 * are only valid if only a single instance of the specific cache class
	 * exists.
	 */
	public static String getStatistics() {
		StringBuilder stats = new StringBuilder();
		stats.append("#access: " + accessCounter);
		stats.append(" #hits: " + hitCounter);
		stats.append(" #miss (not cached): " + missBecauseNotInCacheCounter);
		stats.append(" #miss (removed): " + missBecauseRemovedFromCacheCounter);
		return stats.toString();
	}
}