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

/**
 * A drawing pattern is a predicate that determines whether a given pixel
 * belongs to the foreground or background. This way image generating processors
 * can produce e.g. striped areas.
 * 
 * @author Benjamin Hummel
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 603C2EAFDA1A15C4DAE51109953411DA
 */
public interface IDrawingPattern {

	/**
	 * For the given x and y coordinates returns whether the pixel belongs to
	 * the foreground (otherwise it is background as we do not support
	 * transparency). The coordinates are in pixels using the standard
	 * coordinate system for 2D graphics where (0,0) is the top left corner.
	 */
	boolean isForeground(int x, int y);
}