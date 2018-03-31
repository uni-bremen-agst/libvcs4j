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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

import org.conqat.lib.commons.color.MultiColor;

/**
 * A very simple tree map renderer just drawing "flat" rectangles.
 * 
 * @author $Author: goeb $
 * @version $Rev: 51579 $
 * @ConQAT.Rating GREEN Hash: 6BCB7F263E12BC8AF114CE5E95701B32
 */
public class FlatTreeMapRenderer implements ITreeMapRenderer {

	/** {@inheritDoc} */
	@Override
	public <T> void renderTreeMap(ITreeMapNode<T> node, Graphics2D graphics) {
		if (node.getChildren().isEmpty()) {
			Rectangle2D rect = node.getLayoutRectangle();
			if (rect == null) {
				return;
			}
			fillRect(graphics, rect, node.getColor());

			if (node.getDrawingPattern() != null) {
				drawPattern(graphics, rect, node);
			}

			graphics.setColor(Color.BLACK);
			graphics.draw(rect);
		} else {
			for (ITreeMapNode<T> child : node.getChildren()) {
				renderTreeMap(child, graphics);
			}
		}
	}

	/** Fills the given rect with the given color. */
	private void fillRect(Graphics2D graphics, Rectangle2D rect, Color color) {
		if (color instanceof MultiColor) {
			fillRectMultiColor(graphics, rect, (MultiColor) color);
		} else {
			graphics.setColor(color);
			graphics.fill(rect);
		}
	}

	/**
	 * Fills the given rect with the given {@link MultiColor}. The colors are
	 * arranged in a striped pattern, which is arranged horizontally or
	 * vertically depending on the aspect ratio of the rectangle.
	 */
	private void fillRectMultiColor(Graphics2D graphics, Rectangle2D rect,
			MultiColor multiColor) {
		double x = rect.getX();
		double y = rect.getY();
		double width = rect.getWidth();
		double height = rect.getHeight();

		double offset = 0;
		for (int i = 0; i < multiColor.size(); ++i) {
			Rectangle2D subrect;
			double relativeFrequency = multiColor.getRelativeFrequency(i);
			if (width > height) {
				subrect = new Rectangle2D.Double(x + offset * width, y, width
						* relativeFrequency, height);
			} else {
				subrect = new Rectangle2D.Double(x, y + offset * height, width,
						height * relativeFrequency);
			}

			fillRect(graphics, subrect, multiColor.getColor(i));
			offset += relativeFrequency;
		}
	}

	/** Draws the pattern. */
	private <T> void drawPattern(Graphics2D graphics, Rectangle2D rect,
			ITreeMapNode<T> node) {
		IDrawingPattern drawingPattern = node.getDrawingPattern();
		graphics.setColor(node.getPatternColor());
		for (int x = (int) rect.getMinX(); x <= rect.getMaxX(); ++x) {
			for (int y = (int) rect.getMinY(); y <= rect.getMaxY(); ++y) {
				if (drawingPattern.isForeground(x, y)) {
					graphics.fill(new Rectangle(x, y, 1, 1));
				}
			}
		}
	}

}