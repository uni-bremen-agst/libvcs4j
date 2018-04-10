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
package org.conqat.lib.commons.color;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import org.conqat.lib.commons.string.StringUtils;

/**
 * Colors of the CCSM CI.
 * 
 * @author Florian Deissenboeck
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 7C6571670B359E74A5A96B1768803B34
 */
public enum ECCSMColor implements IColor {

	/** Light blue */
	LIGHT_BLUE(205, 222, 239),

	/** Blue */
	BLUE(99, 156, 206),

	/** Purple */
	PURPLE(50, 50, 102),

	/** Dark Blue */
	DARK_BLUE(0, 117, 204),

	/** Green */
	GREEN(102, 204, 102),

	/** Yellow */
	YELLOW(255, 255, 51),

	/** Red */
	RED(255, 102, 51),

	/** Dark red */
	DARK_RED(204, 51, 51),

	/** Light Gray */
	LIGHT_GRAY(204, 204, 204),

	/** Dark Gray */
	DARK_GRAY(102, 102, 102);

	/** Red value. */
	private final int red;

	/** Green value. */
	private final int green;

	/** Blue value. */
	private final int blue;

	/** AWT color. */
	private Color color;

	/** Create new color. */
	private ECCSMColor(int red, int green, int blue) {
		check(red);
		check(green);
		check(blue);
		this.red = red;
		this.green = green;
		this.blue = blue;
	}

	/** Check if the color value is legal. */
	private void check(int colorValue) {
		if (colorValue < 0 || colorValue > 255) {
			throw new IllegalArgumentException(
					"Value must be between 0 and 255");
		}
	}

	/** {@inheritDoc} */
	@Override
	public String getHTMLColorCode() {
		return String.format("#%02X%02X%02X", red, green, blue);
	}

	/** {@inheritDoc} */
	@Override
	public Color getColor() {
		if (color == null) {
			color = new Color(red, green, blue);
		}
		return color;
	}

	/** Get string representation. */
	@Override
	public String toString() {
		return name() + "; " + getHTMLColorCode() + "; "
				+ String.format("%03d, %03d, %03d", red, green, blue);

	}

	/** Get table of all defined colors. */
	public static String getColorTable() {
		StringBuilder result = new StringBuilder();

		result.append("  HTML  |  r    g    b  | name");
		result.append(StringUtils.CR);

		for (ECCSMColor color : values()) {
			result.append(color.getHTMLColorCode());
			result.append(" | ");
			result.append(String.format("%03d, %03d, %03d", color.red,
					color.green, color.blue));
			result.append(" | ");
			result.append(color.name());
			result.append(StringUtils.CR);
		}

		return result.toString();
	}

	/**
	 * Get image showing all defined colors. If image height is sufficient,
	 * color information text is shown.
	 * 
	 * @param width
	 *            image width
	 * @param height
	 *            image height
	 */
	public static BufferedImage getColorChart(int width, int height) {

		BufferedImage image = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_RGB);

		Graphics2D graphics = image.createGraphics();

		float barHeight = (float) height / values().length;
		boolean drawName = graphics.getFontMetrics().getHeight() < barHeight;

		float y = 0;
		for (ECCSMColor color : values()) {
			graphics.setColor(color.getColor());

			Rectangle2D rect = new Rectangle2D.Float(0, y, width, barHeight);
			graphics.fill(rect);

			y += barHeight;

			if (drawName) {
				graphics.setColor(Color.black);
				graphics.drawString(color.toString(), 5f, y - 3);
			}
		}

		return image;
	}

}