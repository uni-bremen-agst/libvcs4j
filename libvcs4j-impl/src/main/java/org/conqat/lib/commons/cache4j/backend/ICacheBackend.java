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
package org.conqat.lib.commons.cache4j.backend;

/**
 * Interface for cache backends. This is a basic key-based get/set interface.
 * However, we do not use the method names from the map interface, as we do not
 * have map semantics (i.e. information can get lost).
 * <p>
 * Implementations are not required to deal with synchronization. This has to be
 * handled by the caller.
 * 
 * @param <K>
 *            the key type. This must have both {@link Object#equals(Object)}
 *            and {@link Object#hashCode()} correctly implemented.
 * 
 * @param <V>
 *            the value type. It is generally recommended to use an immutable
 *            type here, but this is not required.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 7171F0C9FF707A7D5A2D1B691C8F0265
 */
public interface ICacheBackend<K, V> {

	/** Stores an element (value) using the given key. */
	void store(K key, V value);

	/**
	 * Returns the value stored at a given key or null. The cache guarantees,
	 * that only values inserted via {@link #store(Object, Object)} can be
	 * returned. However, there is no guarantee that a non-null value is
	 * returned, i.e. the cache may forget information.
	 */
	V retrieve(K key);

	/**
	 * Returns a new instance of this backend. This is required as certain cache
	 * implementations require multiple backends, and cloning is not an option
	 * as we do not want to duplicate the cache contents.
	 */
	ICacheBackend<K, V> newInstance();
}