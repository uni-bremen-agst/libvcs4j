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
package org.conqat.lib.commons.factory;

import org.conqat.lib.commons.assertion.CCSMPre;
import org.conqat.lib.commons.cache.LRUCacheBase;
import org.conqat.lib.commons.cache.LRUStraightCacheBase;
import org.conqat.lib.commons.error.NeverThrownRuntimeException;

/**
 * Caching factory which can reuse created objects. Creation is delegated to an
 * inner factory, while for caching a {@link LRUCacheBase} is used.
 * 
 * @param <T>
 *            Type that gets created by the factory.
 * @param <P>
 *            Parameter that is used for creation.
 * @param <X>
 *            Exception that can get thrown during execution of the factory
 *            method. If no exception is thrown, use
 *            {@link NeverThrownRuntimeException}.
 * 
 * @author hummelb
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: A3F0C901C48C63F56F9292AACA978E17
 */
public class LRUCachingParameterizedFactory<T, P, X extends Exception>
		extends LRUStraightCacheBase<P, T, X> implements
		IParameterizedFactory<T, P, X> {

	/** The wrapped factory we delegate to. */
	private final IParameterizedFactory<T, P, X> inner;

	/** Constructor. */
	public LRUCachingParameterizedFactory(
			IParameterizedFactory<T, P, X> inner, int maxSize) {
		super(maxSize);
		CCSMPre.isNotNull(inner, "Delegate factory may not be null!");
		this.inner = inner;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Delegates to the inner factory.
	 */
	@Override
	protected T obtainItem(P identifier) throws X {
		return inner.create(identifier);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Forwards to the {@link #getItem(Object)} method.
	 */
	@Override
	public T create(P parameter) throws X {
		return getItem(parameter);
	}

}