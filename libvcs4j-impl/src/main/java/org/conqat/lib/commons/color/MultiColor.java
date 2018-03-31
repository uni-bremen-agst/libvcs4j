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
package org.conqat.lib.commons.color;

import java.awt.Color;

import org.conqat.lib.commons.assertion.CCSMPre;
import org.conqat.lib.commons.collections.PairList;

/**
 * A multicolor manages an ordered color distribution, i.e. provides a list of
 * colors with a count for these colors. Colors may be contained more than once.
 * To be compatible with code that expects "plain" colors, we inherit from color
 * and require a primary/dominant color to be determined for such code.
 * <p>
 * This class is designed to be immutable.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 5126EE0514C744F09212CA5443DA5B6D
 */
public class MultiColor extends Color {

	/** Serial version UID. */
	private static final long serialVersionUID = 1;

	/**
	 * The underlying color distribution.
	 */
	private final PairList<Color, Integer> colorDistribution;

	/**
	 * Contains the sum of colors, i.e. the sum of the second components of
	 * {@link #colorDistribution}.
	 */
	private final int sum;

	/**
	 * Constructor.
	 * 
	 * @param primaryColor
	 *            the primary color passed to code that does not support multi
	 *            colors.
	 * @param colorDistribution
	 *            the color distribution. All entries must have a positive
	 *            integer and the list may not be empty.
	 */
	public MultiColor(Color primaryColor,
			PairList<Color, Integer> colorDistribution) {
		super(primaryColor.getRed(), primaryColor.getGreen(), primaryColor
				.getBlue(), primaryColor.getAlpha());
		this.sum = checkAndAddDistribution(colorDistribution);
		this.colorDistribution = new PairList<Color, Integer>(colorDistribution);
	}

	/**
	 * Checks constraints for the color distribution and adds the sum of the
	 * color frequencies.
	 */
	private static int checkAndAddDistribution(
			PairList<Color, Integer> colorDistribution) throws AssertionError {
		CCSMPre.isTrue(!colorDistribution.isEmpty(),
				"Distribution may not be empty.");
		int sum = 0;
		for (int i = 0; i < colorDistribution.size(); ++i) {
			CCSMPre.isNotNull(colorDistribution.getFirst(i));
			CCSMPre.isTrue(colorDistribution.getSecond(i) > 0,
					"Color entry must be positive!");
			sum += colorDistribution.getSecond(i);
		}
		return sum;
	}

	/**
	 * Returns the number of colors in the distribution. This corresponds to the
	 * number of (color, int) pairs and does not necessarily count distinct
	 * colors.
	 */
	public int size() {
		return colorDistribution.size();
	}

	/** Returns the color at the given index. */
	public Color getColor(int index) {
		return colorDistribution.getFirst(index);
	}

	/** Returns the absolute frequency of the color at the given index. */
	public int getFrequency(int index) {
		return colorDistribution.getSecond(index);
	}

	/** Returns the relative frequency of the color at the given index. */
	public double getRelativeFrequency(int index) {
		return colorDistribution.getSecond(index) / (double) sum;
	}
}
