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

import java.awt.geom.Rectangle2D;

/**
 * Interface for tree map layout algorithms.
 * 
 * @author Benjamin Hummel
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 93E0E385945E4FC8A049696F239C869F
 */
public interface ITreeMapLayoutAlgorithm {

	/**
	 * Modifies the given tree by adding layout information. The topmost
	 * rectangle will be the given target rectangle. All other nodes will be
	 * assigned rectangles which sum exactly to the the rectangle of they target
	 * node and have areas proportional to their size. 
	 */
	public <T> void layout(ITreeMapNode<T> tree, Rectangle2D target);
}