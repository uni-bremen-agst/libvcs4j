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
package org.conqat.lib.commons.treemap;

import java.awt.geom.Rectangle2D;


/**
 * A very simple tree map layouter just dividing the given rectangle along the
 * longer side.
 * 
 * @author Benjamin Hummel
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: B30FE7C414DF304C088C9B5541B3F06E
 */
public class SimpleTreeMapAlgorithm implements ITreeMapLayoutAlgorithm {

	/** {@inheritDoc} */
	@Override
	public <T> void layout(ITreeMapNode<T> tree, Rectangle2D target) {
		tree.setLayoutRectangle(target);
		layoutChildren(tree);
	}

	/** Layouts the children of the given node (if it has any). */
	private <T> void layoutChildren(ITreeMapNode<T> node) {
		if (node.getChildren().isEmpty()) {
			return;
		}

		Rectangle2D rect = node.getLayoutRectangle();
		double sum = node.getArea();
		double x = rect.getMinX();
		double y = rect.getMinY();
		if (rect.getWidth() > rect.getHeight()) {
			for (ITreeMapNode<T> child : node.getChildren()) {
				double width = rect.getWidth() * child.getArea() / sum;
				child.setLayoutRectangle(new Rectangle2D.Double(x, y, width,
						rect.getHeight()));
				layoutChildren(child);
				x += width;
			}
		} else {
			for (ITreeMapNode<T> child : node.getChildren()) {
				double height = rect.getHeight() * child.getArea() / sum;
				child.setLayoutRectangle(new Rectangle2D.Double(x, y, rect
						.getWidth(), height));
				layoutChildren(child);
				y += height;
			}
		}
	}
}