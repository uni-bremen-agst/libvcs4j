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
import java.util.regex.Pattern;

import org.conqat.lib.commons.enums.EnumUtils;

/**
 * Methods for handling colors.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 44051 $
 * @ConQAT.Rating GREEN Hash: 978452D4EC7A6BA28F0517962F63D745
 */
public class ColorUtils {

	/** Pattern used for finding */
	private static Pattern HTML_COLOR_PATTERN = Pattern.compile("#[0-9a-f]{6}",
			Pattern.CASE_INSENSITIVE);

	/** Converts a color to a HTML color in the format #RRGGBB. */
	public static String toHtmlString(Color color) {
		return String.format("#%06X", color.getRGB() & 0xffffff);
	}

	/**
	 * Returns a color from a string. If the string could not be decoded, null
	 * is returned. This methods supports the html color format (i.e. #RRGGBB)
	 * and some predefined names (e.g. red, green, etc.).
	 */
	public static Color fromString(String s) {

		if (HTML_COLOR_PATTERN.matcher(s).matches()) {
			return Color.decode("0x" + s.substring(1));
		}

		if (s.startsWith("ccsm-")) {
			ECCSMColor color = EnumUtils.valueOfIgnoreCase(ECCSMColor.class,
					s.substring(5));
			if (color != null) {
				return color.getColor();
			}
		}

		EAWTColors color = EnumUtils.valueOfIgnoreCase(EAWTColors.class, s);
		if (color != null) {
			return color.getColor();
		}

		return null;
	}

	/** List of colors defined in AWT used as a lookup table. */
	private static enum EAWTColors {

		/** Red */
		RED(Color.RED),

		/** Green */
		GREEN(Color.GREEN),

		/** Blue */
		BLUE(Color.BLUE),

		/** Yellow */
		YELLOW(Color.YELLOW),

		/** Orange */
		ORANGE(Color.ORANGE),

		/** White */
		WHITE(Color.WHITE),

		/** Black */
		BLACK(Color.BLACK),

		/** Gray */
		GRAY(Color.GRAY),

		/** Cyan */
		CYAN(Color.CYAN),

		/** Magenta */
		MAGENTA(Color.MAGENTA);

		/** The color actual color. */
		private final Color color;

		/** Constructor. */
		private EAWTColors(Color color) {
			this.color = color;
		}

		/** Returns the color for a enum constant. */
		public Color getColor() {
			return color;
		}
	}

	/**
	 * Blend together two colors, using the specified factor to indicate the
	 * weight given to the first color.
	 */
	public static Color blend(double factor, Color color1, Color color2) {
		int r = (int) Math.round(factor * color1.getRed() + (1.0 - factor)
				* color2.getRed());
		int g = (int) Math.round(factor * color1.getGreen() + (1.0 - factor)
				* color2.getGreen());
		int b = (int) Math.round(factor * color1.getBlue() + (1.0 - factor)
				* color2.getBlue());
		return new Color(r, g, b);
	}
}