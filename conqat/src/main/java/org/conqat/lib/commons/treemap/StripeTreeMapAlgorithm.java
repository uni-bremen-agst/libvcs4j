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
import java.util.ArrayList;
import java.util.List;


/**
 * The strip layout algorithm adapted from Bederson, Shneiderman, Wattenberg:
 * "Ordered and Quantum Treemaps".
 * <p>
 * This is useful as it tries to minimize the aspect ratio of the generated
 * squares while maintaining the original order.
 * 
 * @author Benjamin Hummel
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 7F7689F0C1BEC3507CBC94A2028EB149
 */
public class StripeTreeMapAlgorithm implements ITreeMapLayoutAlgorithm {

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
		double scale = rect.getWidth() * rect.getHeight() / node.getArea();

		double layoutX = rect.getMinX();
		double lastX = 0;
		List<ITreeMapNode<T>> l = new ArrayList<ITreeMapNode<T>>();
		List<ITreeMapNode<T>> lastList = new ArrayList<ITreeMapNode<T>>();
		for (ITreeMapNode<T> child : node.getChildren()) {
			double oldAspect = calcAvgAspect(l, rect.getHeight(), scale);
			l.add(child);
			double newAspect = calcAvgAspect(l, rect.getHeight(), scale);

			if (oldAspect < newAspect) {
				l.remove(l.size() - 1);
				lastX = layoutX;
				lastList.clear();
				lastList.addAll(l);

				layoutX += layoutList(l, rect.getHeight(), layoutX, rect
						.getMinY(), scale);
				l.clear();
				l.add(child);
			}
		}

		// last list might be too small, so potentially merge with previous one
		lastList.addAll(l);
		if (calcAvgAspect(lastList, rect.getHeight(), scale) < calcAvgAspect(l,
				rect.getHeight(), scale)) {
			layoutList(lastList, rect.getHeight(), lastX, rect.getMinY(), scale);
		} else {
			layoutList(l, rect.getHeight(), layoutX, rect.getMinY(), scale);
		}
	}

	/**
	 * Calculates the average aspect ratio of the given list of nodes if the
	 * provided height may be used.
	 */
	private <T> double calcAvgAspect(List<ITreeMapNode<T>> l,
			double layoutHeight, double areaScale) {
		if (l.isEmpty()) {
			return 1e8;
		}
		double area = 0;
		for (ITreeMapNode<T> node : l) {
			area += node.getArea();
		}
		double layoutWidth = area * areaScale / layoutHeight;
		double aspectSum = 0;
		for (ITreeMapNode<T> node : l) {
			double localHeight = node.getArea() * areaScale / layoutWidth;
			double localAspect = Math.max(layoutWidth / localHeight,
					localHeight / layoutWidth);
			aspectSum += localAspect;
		}
		return aspectSum / l.size();
	}

	/**
	 * Layout the given list of nodes in one column.
	 * 
	 * @param l
	 *            the list of nodes.
	 * @param layoutHeight
	 *            the height of the column.
	 * @param layoutX
	 *            the x start value.
	 * @param layoutY
	 *            the y start value.
	 * @param areaScale
	 *            the scale factor used to convert from node area to layout
	 *            area.
	 * @return the layout width of the column.
	 */
	private <T> double layoutList(List<ITreeMapNode<T>> l, double layoutHeight,
			double layoutX, double layoutY, double areaScale) {
		double nodeArea = 0;
		for (ITreeMapNode<T> node : l) {
			nodeArea += node.getArea();
		}
		double layoutWidth = nodeArea * areaScale / layoutHeight;
		for (ITreeMapNode<T> node : l) {
			double nodeHeight = node.getArea() * areaScale / layoutWidth;
			node.setLayoutRectangle(new Rectangle2D.Double(layoutX, layoutY,
					layoutWidth, nodeHeight));
			layoutY += nodeHeight;
			layoutChildren(node);
		}
		return layoutWidth;
	}
}