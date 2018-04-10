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
package org.conqat.lib.commons.cache4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.conqat.lib.commons.cache4j.backend.ICacheBackend;
import org.conqat.lib.commons.error.NeverThrownRuntimeException;
import org.conqat.lib.commons.factory.IParameterizedFactory;

/**
 * Implementation of a cache that manages a separate backend for each thread,
 * thus isolating threads from each other. Note that accessing this cache from
 * many temporary threads can lead to memory leaks (as the cached data is still
 * stored) for certain backends.
 * 
 * @param <K>
 *            the key type. This must have both {@link Object#equals(Object)}
 *            and {@link Object#hashCode()} correctly implemented.
 * @param <V>
 *            the value type. It is generally recommended to use an immutable
 *            type here, but this is not required.
 * @param <X>
 *            the type of exception thrown. If no exception will be throws, use
 *            {@link NeverThrownRuntimeException}.
 * 
 * @author $Author: heinemann $
 * @version $Rev: 43065 $
 * @ConQAT.Rating GREEN Hash: D31944D6F064DDD12384430E3168CB42
 */
public class ThreadLocalCache<K, V, X extends Exception> implements
		ICache<K, V, X> {

	/** The name of this cache (used for reporting). */
	private final String name;

	/** The factory used to obtain elements. */
	private final IParameterizedFactory<V, K, X> factory;

	/** The backend used. */
	private final ICacheBackend<K, V> backend;

	/** Stores all sub caches for reporting. */
	private final List<BasicCache<K, V, X>> subCaches = Collections
			.synchronizedList(new ArrayList<BasicCache<K, V, X>>());

	/** Manages the individual caches for each thread. */
	private final ThreadLocal<BasicCache<K, V, X>> localCaches = new ThreadLocal<BasicCache<K, V, X>>() {
		/** {@inheritDoc} */
		@Override
		protected BasicCache<K, V, X> initialValue() {
			BasicCache<K, V, X> cache = new BasicCache<K, V, X>(name + ":"
					+ Thread.currentThread().getId(), factory,
					backend.newInstance());
			subCaches.add(cache);
			return cache;
		}
	};

	/**
	 * Constructor.
	 * 
	 * @param factory
	 *            the factory has to either support multi-threaded access or to
	 *            be synchronized!
	 */
	public ThreadLocalCache(String name,
			IParameterizedFactory<V, K, X> factory, ICacheBackend<K, V> backend) {
		this.name = name;
		this.factory = factory;
		this.backend = backend;
	}

	/** {@inheritDoc} */
	@Override
	public V obtain(K parameter) throws X {
		return localCaches.get().obtain(parameter);
	}

	/** {@inheritDoc} */
	@Override
	public void clear(boolean allThreads) {
		if (allThreads) {
			for (BasicCache<K, V, X> cache : subCaches) {
				cache.clear(false);
			}
		} else {
			localCaches.get().clear(false);
		}
	}

	/** {@inheritDoc} */
	@Override
	public String getName() {
		return name;
	}

	/** {@inheritDoc} */
	@Override
	public int getHits() {
		int result = 0;
		for (BasicCache<K, V, X> cache : subCaches) {
			result += cache.getHits();
		}
		return result;
	}

	/** {@inheritDoc} */
	@Override
	public int getMisses() {
		int result = 0;
		for (BasicCache<K, V, X> cache : subCaches) {
			result += cache.getMisses();
		}
		return result;
	}

	/** {@inheritDoc} */
	@Override
	public long getMissCostMillis() {
		long result = 0;
		for (BasicCache<K, V, X> cache : subCaches) {
			result += cache.getMissCostMillis();
		}
		return result;
	}
}