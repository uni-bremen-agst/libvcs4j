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
import java.awt.geom.Rectangle2D;
import java.util.regex.Pattern;

/**
 * A simple renderer that draws tree map node texts into the tree map.
 * 
 * @author juergens
 * @author $Author: goeb $
 * @version $Rev: 51604 $
 * @ConQAT.Rating GREEN Hash: DCC426B493D944260A220D7FC6709BBA
 */
public class NodeTextRenderer implements ITreeMapRenderer {

	/** Padding between a node's text label and its rectangle border */
	private static final int TEXT_PADDING = 5;

	/** Color in which text is drawn */
	private final Color textColor;

	/** Separation pattern used to isolate local name */
	private final Pattern separationPattern;

	/** Constructor */
	public NodeTextRenderer(Color textColor, Pattern separationPattern) {
		this.textColor = textColor;
		this.separationPattern = separationPattern;
	}

	/** {@inheritDoc} */
	@Override
	public <T> void renderTreeMap(ITreeMapNode<T> node, Graphics2D graphics) {
		if (node.getChildren().isEmpty()) {
			Rectangle2D nodeArea = node.getLayoutRectangle();
			if (nodeArea == null) {
				return;
			}
			if (enoughSpace(nodeArea)) {
				drawText(node.getText(), nodeArea, graphics);
			}
		} else {
			for (ITreeMapNode<T> child : node.getChildren()) {
				renderTreeMap(child, graphics);
			}
		}
	}

	/** Determines if node area is large enough */
	private boolean enoughSpace(Rectangle2D nodeArea) {
		return nodeArea.getWidth() > 3 * TEXT_PADDING
				&& nodeArea.getHeight() > 3 * TEXT_PADDING;
	}

	/** Draws the node text */
	private <T> void drawText(String text, Rectangle2D availableSpace,
			Graphics2D graphics) {

		// cut text to size
		String fittedText = clipTextToWidth(text, availableSpace.getWidth(),
				graphics);
		if (fittedText.length() < text.length()) {
			fittedText += "...";
		}

		// compute text position
		int x = (int) availableSpace.getCenterX()
				- (actualWidth(fittedText, graphics) / 2);
		int y = (int) availableSpace.getCenterY()
				+ (actualHeight(fittedText, graphics) / 2);

		// draw label
		graphics.setColor(textColor);
		graphics.drawString(fittedText, x, y);
	}

	/** Clips a string to a certain width */
	private String clipTextToWidth(String text, double width,
			Graphics2D graphics) {
		double availableWidth = width - 2 * TEXT_PADDING;

		// try to prune to last name part
		if (separationPattern != null
				&& actualWidth(text, graphics) > availableWidth) {
			String[] parts = separationPattern.split(text);
			if (parts.length > 0) {
				text = parts[parts.length - 1];
			}
		}

		// reserve space for trailing "..."
		if (actualWidth(text, graphics) > availableWidth) {
			availableWidth -= actualWidth("...", graphics);
		}

		// clip until small enough
		while (text.length() > 0
				&& actualWidth(text, graphics) > availableWidth) {
			text = text.substring(0, text.length() - 1);
		}
		return text;
	}

	/** Determines the width a string requires in the current font */
	private int actualWidth(String label, Graphics2D graphics) {
		return (int) actualBounds(label, graphics).getWidth();
	}

	/** Determines the height a string requires in the current font */
	private int actualHeight(String label, Graphics2D graphics) {
		return (int) actualBounds(label, graphics).getHeight();
	}

	/** Computes the bounds of a string in the current font */
	private Rectangle2D actualBounds(String label, Graphics2D graphics) {
		return graphics.getFont().getStringBounds(label,
				graphics.getFontRenderContext());
	}

}