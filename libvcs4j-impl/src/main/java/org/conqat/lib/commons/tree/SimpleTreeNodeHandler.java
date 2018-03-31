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
 * Handler for {@link SimpleTreeNode}.
 * 
 * @author deissenb
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 016D448C72EE88BB78410BDDF3DE7565
 */
public class SimpleTreeNodeHandler<K> implements
		ITreeNodeHandler<SimpleTreeNode<K>, K> {

	/** The key used for the root. */
	private final K rootKey;

	/**
	 * Create new handler.
	 * 
	 * @param rootKey
	 *            the key used for the root node.
	 */
	public SimpleTreeNodeHandler(K rootKey) {
		this.rootKey = rootKey;
	}

	/** {@inheritDoc} */
	@Override
	public SimpleTreeNode<K> createRoot() {
		return new SimpleTreeNode<K>(rootKey);
	}

	/** {@inheritDoc} */
	@Override
	public SimpleTreeNode<K> getOrCreateChild(SimpleTreeNode<K> node, K key) {
		SimpleTreeNode<K> child = node.getChild(key);
		if (child != null) {
			return child;
		}

		child = new SimpleTreeNode<K>(key);
		node.addChild(child);
		return child;
	}

}