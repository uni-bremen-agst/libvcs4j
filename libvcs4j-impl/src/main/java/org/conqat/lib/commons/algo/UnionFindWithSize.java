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
package org.conqat.lib.commons.algo;

import org.conqat.lib.commons.collections.IntList;

/**
 * Implementation of a simple union find data structure. It implements the
 * "partial path compression" heuristic but does not use "union by size" but
 * instead uses randomization. Additionally the size of union clusters is
 * managed.
 * 
 * @author hummelb
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 14C39E519E9D9A022B4ED97C86539CCC
 */
public class UnionFindWithSize extends UnionFind {

	/** The sizes for the individual clusters. */
	private IntList unionSizes = new IntList();

	/** {@inheritDoc} */
	@Override
	public int addElement() {
		unionSizes.add(1);
		return super.addElement();
	}

	/** {@inheritDoc} */
	@Override
	protected void connectToRepresentative(int element, int representative) {
		super.connectToRepresentative(element, representative);
		int connectedSize = unionSizes.get(representative)
				+ unionSizes.get(element);
		unionSizes.set(representative, connectedSize);
	}

	/** Returns the size of the union cluster containing the given element. */
	public int getClusterSize(int element) {
		return unionSizes.get(find(element));
	}
}