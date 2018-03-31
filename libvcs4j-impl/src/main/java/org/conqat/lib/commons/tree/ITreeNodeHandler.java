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
package org.conqat.lib.commons.tree;

/**
 * This interface is used by {@link TreeUtils} to create tree structures. We use
 * this factory-based approach as this allows us to create trees based on model
 * elements that do not have to implement any specific interfaces.
 * 
 * @param <T>
 *            the type of nodes this handler handles
 * @param <K>
 *            the key used by the nodes to identify children
 * @author deissenb
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: D0FEC0BC74E53061979D8555C8260115
 */
public interface ITreeNodeHandler<T, K> {
	/**
	 * Get the nodes child identified by the provided key. If the node has no
	 * child with the specified key, one should be created.
	 */
	public T getOrCreateChild(T node, K key);

	/** Create root of node of the tree. */
	public T createRoot();
}