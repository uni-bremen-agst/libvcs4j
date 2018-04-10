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

import org.conqat.lib.commons.error.NeverThrownRuntimeException;

/**
 * Common base class for caches. This class basically works like a map that maps
 * elements of type <code>I</code> to elements of type <code>E</code>. As this
 * class uses a hash map and elements of type <code>I</code> are not necessarily
 * suitable as hash map keys a special type (<code>H</code>) for the hash keys
 * must be defined. Concrete keys are determined by method
 * <code>getHashKey(I)</code>. Please note that making the hash type explicit as
 * generic parameter is not due to implementation reasons but to make design
 * more obvious.
 * 
 * @author hummelb
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 2A936A389FF1E3A2ED5197D0E72C3C45
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
public abstract class CacheBase<I, H, E, X extends Exception> {

	/**
	 * Obtain an item from the cache. If the item was not cached yet, it will be
	 * cached.
	 * 
	 * @param identifier
	 *            an object identifying the item to retrieve from the cache.
	 *            This class' implementation works with a hash map so
	 *            identifiers must adhere to the conventions for
	 *            <code>Object.hashcode()</code>.
	 * @return The item.
	 */
	public abstract E getItem(I identifier) throws X;

	/**
	 * Extenders of the cache class must implemented that method to define the
	 * item acquisition mechanism.
	 * 
	 * @param identifier
	 *            identifier unambiguously identifying the item.
	 * @return the item to cache.
	 */
	protected abstract E obtainItem(I identifier) throws X;

	/**
	 * Determine hash key for an identifier. If the identifier itself is a
	 * suitable hash key, simply return it or better, use one of the straight
	 * cache implementations.
	 * 
	 * 
	 * @param identifier
	 * @return an object that is suitable hash key
	 * @see Object#hashCode()
	 */
	protected abstract H getHashKey(I identifier);
}