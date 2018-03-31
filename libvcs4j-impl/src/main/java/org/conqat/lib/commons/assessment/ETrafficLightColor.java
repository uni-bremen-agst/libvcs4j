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
package org.conqat.lib.commons.assessment;

/**
 * Enum for traffic light colors.
 * 
 * Note that the order is relevant: The first Color (RED) is considered the most
 * dominant color (see {@link Assessment#getDominantColor()}).
 * 
 * The mapping to actual colors is defined in
 * {@link AssessmentUtils#getColor(ETrafficLightColor)}, so typically this
 * method should be adjusted when a new constant is introduced here.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: F273535EA6F2DE4667D44121804CDE65
 */
public enum ETrafficLightColor {

	/** Red signals errors or incompleteness. */
	RED,

	/**
	 * Orange is an intermediate state between {@link #RED} and {@link #YELLOW}.
	 */
	ORANGE,

	/** Yellow signals warning or lack of control. */
	YELLOW,

	/** Green signals the absence of errors or correctness. */
	GREEN,

	/**
	 * Baseline indicates a baseline entry, i.e. there has been no change
	 * compared to a given baseline and thus no color applies.
	 */
	BASELINE,

	/** This is used if no information is available. */
	UNKNOWN;

	/**
	 * Returns the more dominant color, which is the enum literal with smaller
	 * index (as they are sorted by dominance).
	 */
	public static ETrafficLightColor getDominantColor(
			ETrafficLightColor color1, ETrafficLightColor color2) {
		if (color2.ordinal() < color1.ordinal()) {
			return color2;
		}
		return color1;
	}
}