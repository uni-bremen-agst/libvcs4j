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
 * Cache implementation that is synchronized to support multi-threaded
 * environments.
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
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 39FB9C78EB375EF4B9480697BE29FC19
 */
public class SynchronizedCache<K, V, X extends Exception> extends
		BasicCache<K, V, X> {

	/** Constructor. */
	public SynchronizedCache(String name,
			IParameterizedFactory<V, K, X> factory, ICacheBackend<K, V> backend) {
		super(name, factory, backend);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Overridden to provide synchronization.
	 */
	@Override
	public synchronized V obtain(K key) throws X {
		return super.obtain(key);
	}
}