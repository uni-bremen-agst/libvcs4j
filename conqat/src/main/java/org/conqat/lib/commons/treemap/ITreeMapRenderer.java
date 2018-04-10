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

import java.awt.Graphics2D;

/**
 * Interface for code rendering treemaps into an image.
 * 
 * @author Benjamin Hummel
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 043ECB92AA81D91E68A48D0AB5CCDD46
 */
public interface ITreeMapRenderer {

	/**
	 * Renders the given tree into the graphics. The position is determined by
	 * the rectangle of the topmost node of the tree given.
	 * 
	 * @param tree
	 *            the tree to render. The tree must have been layouted and the
	 *            outermost rectangle should be completely included in the
	 *            provided graphics.
	 * @param graphics
	 *            the graphics used for drawing.
	 */
	public <T> void renderTreeMap(ITreeMapNode<T> tree, Graphics2D graphics);
}