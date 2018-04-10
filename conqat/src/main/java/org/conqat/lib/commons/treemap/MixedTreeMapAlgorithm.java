/*-------------------------------------------------------------------------+
|                                                                          |
| Copyright 2005-2011 the ConQAT Project                                   |
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
package org.conqat.lib.commons.treemap;

import java.awt.geom.Rectangle2D;

/**
 * A hybrid layout algorithm that applies both the
 * {@link StripeTreeMapAlgorithm} and (near the leaves) the
 * {@link SquarifiedTreeMapAlgorithm} to obtain good sorting/locality while
 * getting rectangles that are "more square". The approach is described in
 * Vliegen, von Wijk, van der Linden:
 * "Visualizing Business Data with Generalized Treemaps".
 * 
 * @see "http://www.magnaview.nl/documents/Visualizing_Business_Data_with_Generalized_Treemaps.pdf"
 * 
 * @author $Author: heinemann $
 * @version $Rev: 45676 $
 * @ConQAT.Rating GREEN Hash: 2F6276BC09496786565DFDA9059B5CC2
 */
public class MixedTreeMapAlgorithm implements ITreeMapLayoutAlgorithm {

	/** {@inheritDoc} */
	@Override
	public <T> void layout(ITreeMapNode<T> rootNode, Rectangle2D targetArea) {
		new StripeTreeMapAlgorithm().layout(rootNode, targetArea);

		performLocalSquareLayout(rootNode);
	}

	/**
	 * Performs the square layout for nodes that are near the leaves.
	 * 
	 * @return the maximum distance to the leaves.
	 */
	private static <T> int performLocalSquareLayout(ITreeMapNode<T> node) {
		if (node.getChildren().isEmpty()) {
			return 0;
		}

		int distance = 0;
		for (ITreeMapNode<T> child : node.getChildren()) {
			distance = Math.max(distance, performLocalSquareLayout(child) + 1);
		}

		// layout last 3 levels
		if (distance == 3) {
			new SquarifiedTreeMapAlgorithm().layout(node,
					node.getLayoutRectangle());
		}

		return distance;
	}
}
