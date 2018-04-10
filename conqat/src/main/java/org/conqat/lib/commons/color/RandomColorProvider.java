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
import java.util.HashMap;
import java.util.Map;

/**
 * Color provider returning a random color.
 * 
 * @author hummelb
 * @author $Author: kanis $
 * @version $Rev: 41977 $
 * @ConQAT.Rating YELLOW Hash: 1F88BBB059856006C1518F8C4AB22318
 */
public class RandomColorProvider<T> implements IColorProvider<T> {

	/** Map of generated colors. */
	private final Map<Object, Color> cache = new HashMap<Object, Color>();

	/** {@inheritDoc} */
	@Override
	public Color getColor(T t) {
		Object key = getKey(t);

		Color c = cache.get(key);
		if (c == null) {
			c = generateColor();
			cache.put(key, c);
		}

		return c;
	}

	/**
	 * Generates a new color. This is based on the hue/saturation/brightness
	 * model (http://en.wikipedia.org/wiki/HSL_and_HSV). The overall idea is to
	 * always use full saturation (to get nice colors) and two levels of
	 * brightness. For each brightness level we determine the hue value (360
	 * degree color wheel) by first using 3 different values, next using the 3
	 * between each of these, then the 6 between the previous 6 (3+3), then 12
	 * between those, and so on. For brightness we alternate as follows: 3
	 * initial color bright, 3 initial color non-bright, 3 second step colors
	 * bright, 3 second step colors non-bright, 6 third step colors bright, etc.
	 */
	protected Color generateColor() {
		int i = cache.size();
		int fractions = 3;
		boolean bright = true;

		// Hue in [0;1)
		float hue = 0;

		// use original 3 colors
		if (i < 2 * fractions) {
			hue = i % fractions / (float) fractions;
			if (i >= fractions) {
				bright = false;
			}
		} else {
			i -= 6;

			// this loop determines the brightness level, the number of
			// fractions to divide the "color wheel" into, and the relative
			// index into the fractions (i)
			while (i >= fractions) {
				i -= fractions;
				bright = false;

				if (i >= fractions) {
					bright = true;
					fractions *= 2;
				}
			}

			hue = .5f / fractions * (2 * i + 1);
		}

		if (bright) {
			return new Color(Color.HSBtoRGB(hue, 1, 1));
		}
		return new Color(Color.HSBtoRGB(hue, 1, .7f));
	}

	/**
	 * Returns the key which is used for color generation. This key must support
	 * {@link Object#equals(Object)} and {@link Object#hashCode()} as this key
	 * is used for color caching as well. The default implementation just uses
	 * the element from {@link #getColor(Object)} (i.e. identity).
	 */
	protected Object getKey(T t) {
		return t;
	}

}