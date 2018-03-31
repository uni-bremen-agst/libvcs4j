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

import org.conqat.lib.commons.cache4j.backend.ICacheBackend;
import org.conqat.lib.commons.error.NeverThrownRuntimeException;
import org.conqat.lib.commons.factory.IParameterizedFactory;

/**
 * Basic implementation of a cache.
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
 * @ConQAT.Rating GREEN Hash: 4937D8B7639A13AD408998A2498F0F19
 */
public class BasicCache<K, V, X extends Exception> implements ICache<K, V, X> {

	/** The name of this cache (e.g. used for statistics). */
	private final String name;

	/** The cache backend. */
	private ICacheBackend<K, V> backend;

	/** The factory used to create new elements. */
	private final IParameterizedFactory<V, K, X> factory;

	/**
	 * Counts the number of cache hits. This variable is not synchronized in any
	 * way, so multi-threaded usage of this class may lead to incorrect results.
	 * However, the amount of error is small enough to be tolerated, while the
	 * performance impact of synchronization is not.
	 */
	private int hits = 0;

	/**
	 * Counts the number of cache misses. This variable is not synchronized in
	 * any way, so multi-threaded usage of this class may lead to incorrect
	 * results. However, the amount of error is small enough to be tolerated,
	 * while the performance impact of synchronization is not.
	 */
	private int misses = 0;

	/** Accumulated cost of cache misses in milliseconds. */
	private long missCostMillis = 0;

	/** Constructor. */
	public BasicCache(String name, IParameterizedFactory<V, K, X> factory,
			ICacheBackend<K, V> backend) {
		this.name = name;
		this.factory = factory;
		this.backend = backend;
	}

	/** {@inheritDoc} */
	@Override
	public String getName() {
		return name;
	}

	/** {@inheritDoc} */
	@Override
	public void clear(boolean allThreads) {
		backend = backend.newInstance();
	}
	
	/** {@inheritDoc} */
	@Override
	public V obtain(K key) throws X {
		V value = backend.retrieve(key);

		if (value != null) {
			hits++;
			return value;
		}

		misses++;

		long start = System.currentTimeMillis();

		value = factory.create(key);
		backend.store(key, value);

		missCostMillis += System.currentTimeMillis() - start;

		return value;
	}

	/** {@inheritDoc} */
	@Override
	public int getHits() {
		return hits;
	}

	/** {@inheritDoc} */
	@Override
	public int getMisses() {
		return misses;
	}

	/** {@inheritDoc} */
	@Override
	public long getMissCostMillis() {
		return missCostMillis;
	}
}