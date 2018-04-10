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
package org.conqat.lib.commons.assessment;

import java.awt.Color;

import org.conqat.lib.commons.color.ECCSMColor;

/**
 * Utility methods for dealing with ratings.
 * 
 * @author $Author: heineman $
 * @version $Rev: 38629 $
 * @ConQAT.Rating GREEN Hash: 739153E0DB9869461F33802F2AF37D56
 */
public class AssessmentUtils {

	/** Returns the color used for visualizing a traffic light color. */
	public static Color getColor(ETrafficLightColor color) {
		switch (color) {
		case RED:
			return ECCSMColor.RED.getColor();
		case ORANGE:
			return Color.ORANGE;
		case YELLOW:
			return ECCSMColor.YELLOW.getColor();
		case GREEN:
			return ECCSMColor.GREEN.getColor();
		case BASELINE:
			return ECCSMColor.LIGHT_BLUE.getColor();

		case UNKNOWN:
		default:
			return ECCSMColor.DARK_GRAY.getColor();
		}
	}

}
