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

import java.util.HashMap;

import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.collections.UnmodifiableCollection;
import org.conqat.lib.commons.string.StringUtils;

/**
 * A simple node class that can be used with {@link TreeUtils}. See
 * {@link TreeUtilsTest} for an application of this class that uses strings as
 * keys.
 * 
 * @param <K>
 *            key used to identify children, e.g. String.
 * 
 * @author deissenb
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 1C91FA1356D5E77B50FFE3CAA85A7B7D
 */
public class SimpleTreeNode<K> {

	/** Maps from key to child. */
	private final HashMap<K, SimpleTreeNode<K>> children = new HashMap<K, SimpleTreeNode<K>>();

	/** Key of this node. */
	private final K key;

	/** Create new node with specified key. */
	public SimpleTreeNode(K key) {
		this.key = key;
	}

	/**
	 * Get child with specified key. This returns <code>null</code> if child
	 * with provided key does not exist.
	 */
	public SimpleTreeNode<K> getChild(K key) {
		return children.get(key);
	}

	/** Add child. This overwrites existing child with same key. */
	public void addChild(SimpleTreeNode<K> child) {
		children.put(child.getKey(), child);
	}

	/** Get key of this node. */
	public K getKey() {
		return key;
	}

	/** Get children of this node. */
	public UnmodifiableCollection<SimpleTreeNode<K>> getChildren() {
		return CollectionUtils.asUnmodifiable(children.values());
	}

	/**
	 * This returns a nicely indented representation of the whole tree below
	 * this node.
	 */
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append(key + StringUtils.CR);
		for (SimpleTreeNode<K> child : children.values()) {
			result.append(StringUtils.prefixLines(child.toString(),
					StringUtils.TWO_SPACES, true));
			result.append(StringUtils.CR);
		}
		return result.toString();
	}
}