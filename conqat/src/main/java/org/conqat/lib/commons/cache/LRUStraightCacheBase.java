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
 * This a special base class for last recently used (LRU) caches that work with
 * identifiers that are suitable as hash keys.
 * 
 * @author Florian Deissenboeck
 * @author $Author: kinnen $
 * 
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 807DE22106840D5E3EE3F5B216DB5405
 * 
 * @param <I>
 *            the index type of the cache
 * @param <E>
 *            the type stored in the cache
 * @param <X>
 *            the type of exception thrown by the {@link #obtainItem(Object)}
 *            method. Use the {@link NeverThrownRuntimeException} if no
 *            exception will be thrown.
 */
public abstract class LRUStraightCacheBase<I, E, X extends Exception> extends
		LRUCacheBase<I, I, E, X> {

	/** Constructor. */
	public LRUStraightCacheBase(int maxSize) {
		super(maxSize);
	}

	/**
	 * This method simply returns the identifier.
	 */
	@Override
	protected I getHashKey(I identifier) {
		return identifier;
	}

}