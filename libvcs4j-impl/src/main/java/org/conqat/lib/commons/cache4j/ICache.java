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

import org.conqat.lib.commons.error.NeverThrownRuntimeException;
import org.conqat.lib.commons.factory.IParameterizedFactory;

/**
 * Basic interface for a cache, which is basically a
 * {@link IParameterizedFactory} with additional hit/miss statistics.
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
 * @ConQAT.Rating GREEN Hash: F3B07DA8C687D337C4E9790731F89FB2
 */
public interface ICache<K, V, X extends Exception> {

	/**
	 * Obtain the value for a key. This returns the cached element is possible.
	 * Otherwise the value is calculated (and stored in the cache for later
	 * use).
	 */
	V obtain(K key) throws X;

	/**
	 * Clears the cache, i.e. removes all cached data.
	 * 
	 * @param allThreads
	 *            if this is true, data will be discarded for all threads,
	 *            otherwise only data for the current thread will be cleaned (if
	 *            possible).
	 */
	void clear(boolean allThreads);

	/** Returns the name (used for reporting). */
	String getName();

	/** Returns the number of cache hits so far. */
	int getHits();

	/** Returns the number of cache misses so far. */
	int getMisses();

	/**
	 * Returns an estimation of the overhead caused by cache misses in
	 * milliseconds.
	 */
	long getMissCostMillis();
}