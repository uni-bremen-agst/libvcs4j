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
import org.conqat.lib.commons.factory.IParameterizedFactory;

/**
 * Enumeration of strategies for supporting threads.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 6C09B6D2D01568E18AF3E1DDEA52F800
 */
public enum ECacheThreadSupport {

	/** No synchronization. */
	NONE,

	/** Java synchronization (i.e. single cache with synchronized access). */
	SYNCHRONIZED,

	/** Thread local implementation, i.e. each thread has a separate cache. */
	THREADLOCAL;

	/** Factory method for creating a cache from a synchronization strategy. */
	public <K, V, X extends Exception> ICache<K, V, X> createCache(String name,
			IParameterizedFactory<V, K, X> factory,
			ICacheBackend<K, V> backend) {
		switch (this) {
		case NONE:
			return new BasicCache<K, V, X>(name, factory, backend);
		case SYNCHRONIZED:
			return new SynchronizedCache<K, V, X>(name, factory, backend);
		case THREADLOCAL:
			return new ThreadLocalCache<K, V, X>(name, factory, backend);
		default:
			throw new AssertionError("Unknown enum value: " + this);
		}
	}
}