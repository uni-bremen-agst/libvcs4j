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

import java.util.LinkedHashMap;

import org.conqat.lib.commons.assertion.CCSMPre;
import org.conqat.lib.commons.error.NeverThrownRuntimeException;

/**
 * A cache with a fixed size using a last recently used (LRU) strategy. If
 * identifiers itself are suitable hash keys, use class
 * {@link org.conqat.lib.commons.cache.LRUStraightCacheBase}.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: AFC0C53F122A665F50EF1D003593A416
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
public abstract class LRUCacheBase<I, H, E, X extends Exception> extends
		CacheBase<I, H, E, X> {

	/** The actual cache. */
	private final LinkedHashMap<H, E> cache;

	/** Constructor. */
	@SuppressWarnings("serial")
	public LRUCacheBase(final int maxSize) {
		CCSMPre.isTrue(maxSize > 0, "Maximal size must be positive!");

		cache = new LinkedHashMap<H, E>(2 * maxSize, .6f, true) {
			@Override
			protected boolean removeEldestEntry(java.util.Map.Entry<H, E> eldest) {
				return size() > maxSize;
			}
		};
	}

	/** {@inheritDoc} */
	@Override
	public E getItem(I identifier) throws X {
		H key = getHashKey(identifier);
		E value = cache.get(key);
		if (value == null) {
			value = obtainItem(identifier);
			cache.put(key, value);
		}
		return value;
	}
}