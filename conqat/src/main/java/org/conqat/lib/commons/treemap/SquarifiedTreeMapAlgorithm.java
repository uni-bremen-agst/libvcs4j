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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.conqat.lib.commons.collections.CollectionUtils;

/**
 * A layout algorithm using the squarified layout approach described in Mark
 * Bruls, Kees Huizing, and Jarke J. van Wijk: "Squarified Treemaps". This
 * algorithm will maintain nearly perfect squares but does not preserve order.
 * 
 * @see "http://www.win.tue.nl/~vanwijk/stm.pdf"
 * 
 * @author $Author: streitel $
 * @version $Rev: 51556 $
 * @ConQAT.Rating GREEN Hash: 49A675A5C5F1886326ED2C1BFEE02019
 */
public class SquarifiedTreeMapAlgorithm implements ITreeMapLayoutAlgorithm {

	/** {@inheritDoc} */
	@Override
	public <T> void layout(ITreeMapNode<T> rootNode, Rectangle2D targetArea) {
		rootNode.setLayoutRectangle(targetArea);
		layoutChildren(rootNode);
	}

	/** Layouts the children of the given node (if it has any). */
	private <T> void layoutChildren(ITreeMapNode<T> node) {
		if (node.getChildren().isEmpty()) {
			return;
		}

		Rectangle2D rect = node.getLayoutRectangle();
		double areaScale = rect.getWidth() * rect.getHeight() / node.getArea();

		// sort larger nodes to the front
		List<ITreeMapNode<T>> sortedNodes = new ArrayList<ITreeMapNode<T>>(
				node.getChildren());
		Collections.sort(sortedNodes, new Comparator<ITreeMapNode<T>>() {
			@Override
			public int compare(ITreeMapNode<T> node1, ITreeMapNode<T> node2) {
				return Double.compare(node2.getArea(), node1.getArea());
			}
		});

		while (!sortedNodes.isEmpty()
				&& CollectionUtils.getLast(sortedNodes).getArea() <= 0) {
			sortedNodes.remove(sortedNodes.size() - 1);
		}

		int start = 0;
		double shorterSide = Math.min(rect.getWidth(), rect.getHeight());
		for (int end = 1; end <= sortedNodes.size();) {
			if (end < sortedNodes.size()
					&& worstAspectRatio(sortedNodes.subList(start, end),
							shorterSide, areaScale) > worstAspectRatio(
							sortedNodes.subList(start, end + 1), shorterSide,
							areaScale)) {
				end += 1;
			} else {
				rect = layoutRow(sortedNodes.subList(start, end), rect,
						areaScale);
				shorterSide = Math.min(rect.getWidth(), rect.getHeight());
				start = end;
				end = start + 1;
			}
		}

		for (ITreeMapNode<T> child : sortedNodes) {
			layoutChildren(child);
		}
	}

	/**
	 * Layouts the given nodes as a row along the shorter side of the rectangle.
	 */
	private static <T> Rectangle2D layoutRow(List<ITreeMapNode<T>> nodes,
			Rectangle2D rect, double areaScale) {
		double overallArea = getArea(nodes);
		if (rect.getWidth() < rect.getHeight()) {
			double height = overallArea * areaScale / rect.getWidth();
			double x = rect.getX();
			for (ITreeMapNode<T> node : nodes) {
				double nodeWidth = node.getArea() * areaScale / height;
				node.setLayoutRectangle(new Rectangle2D.Double(x, rect.getY(),
						nodeWidth, height));
				x += nodeWidth;
			}
			return new Rectangle2D.Double(rect.getX(), rect.getY() + height,
					rect.getWidth(), rect.getHeight() - height);
		}

		double width = overallArea * areaScale / rect.getHeight();
		double y = rect.getY();
		for (ITreeMapNode<T> node : nodes) {
			double nodeHeight = node.getArea() * areaScale / width;
			node.setLayoutRectangle(new Rectangle2D.Double(rect.getX(), y,
					width, nodeHeight));
			y += nodeHeight;
		}
		return new Rectangle2D.Double(rect.getX() + width, rect.getY(),
				rect.getWidth() - width, rect.getHeight());
	}

	/**
	 * Returns the worst aspect ratio is the given nodes were layouted in a
	 * rectangle with this side length.
	 */
	private static <T> double worstAspectRatio(List<ITreeMapNode<T>> nodes,
			double minSide, double areaScale) {
		double overallArea = getArea(nodes) * areaScale;
		double side = overallArea / minSide;
		double worst = 1;
		for (ITreeMapNode<T> node : nodes) {
			double aspect = node.getArea() * areaScale / side / side;
			worst = Math.max(worst, Math.max(aspect, 1 / aspect));
		}
		return worst;
	}

	/** Returns the accumulated area for a list of nodes. */
	private static <T> double getArea(List<ITreeMapNode<T>> nodes) {
		double area = 0;
		for (ITreeMapNode<T> node : nodes) {
			area += node.getArea();
		}
		return area;
	}
}
