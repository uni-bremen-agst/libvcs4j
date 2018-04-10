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
import java.awt.geom.Rectangle2D;
import java.util.List;

/**
 * Interface for nodes used for building the tree map node hierarchy which is
 * then rendered as a tree map.
 * 
 * @author Benjamin Hummel
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: EAB834AEB241541183617E9AD55B5547
 * 
 * @param <T>
 *            the type the user data has.
 */
public interface ITreeMapNode<T> {

	/** Returns the text of the tree map node */
	public String getText();

	/**
	 * Returns the list of children of this node. This usually is a readonly
	 * list.
	 */
	public List<ITreeMapNode<T>> getChildren();

	/** Returns the area of this node including all subnodes. */
	public double getArea();

	/** Returns the base color used for drawing this node. */
	public Color getColor();

	/** Returns the color used for drawing the pattern (if any) of this node. */
	public Color getPatternColor();

	/**
	 * Returns the pattern used for drawing the node (may be <code>null</code>
	 * to use no pattern).
	 */
	public IDrawingPattern getDrawingPattern();

	/** Returns some user defined data which can be useful for some callbacks. */
	public T getUserDatum();

	/**
	 * Returns the rectangle this node was layouted into. If the tree was not
	 * yet layouted, this may be null, otherwise it should be the value set by
	 * {@link #setLayoutRectangle(Rectangle2D)}.
	 */
	public Rectangle2D getLayoutRectangle();

	/** Sets the rectangle this node should be layouted into. */
	public void setLayoutRectangle(Rectangle2D rect);

	/** Get displayable name of the node. */
	public String getTooltipId();

	/** Returns keys for structured displayable data. */
	public List<String> getTooltipKeys();

	/** Returns the value to be displayed for a single key. */
	public Object getTooltipValue(String key);

}