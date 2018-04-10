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
package org.conqat.lib.commons.image;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.GeneralPath;

/**
 * Utility classes for graphics.
 * 
 * @author hummelb
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 8EC602064C634A82722581DA1EA1606E
 */
public class GraphicsUtils {

	/**
	 * Returns a path for the arrow at the end of an edge from p1 to p2.
	 * 
	 * @param arrowBarbSize
	 *            gives the size of the barb in pixels (i.e. the size of the
	 *            arrow tip)
	 * @param arrowPhi
	 *            gives the angle between the barbs and the center line, i.e.
	 *            this is half of the angle of the arrow tip.
	 */
	public static GeneralPath getArrowHead(Point p1, Point p2,
			int arrowBarbSize, double arrowPhi) {
		double theta = Math.atan2(p2.y - p1.y, p2.x - p1.x);

		GeneralPath path = new GeneralPath();

		// Add an arrow head at p2
		double x = p2.x + arrowBarbSize * Math.cos(theta + Math.PI - arrowPhi);
		double y = p2.y + arrowBarbSize * Math.sin(theta + Math.PI - arrowPhi);
		path.moveTo((float) x, (float) y);
		path.lineTo(p2.x, p2.y);
		x = p2.x + arrowBarbSize * Math.cos(theta + Math.PI + arrowPhi);
		y = p2.y + arrowBarbSize * Math.sin(theta + Math.PI + arrowPhi);
		path.lineTo((float) x, (float) y);

		return path;
	}

	/**
	 * The ChopboxAnchor's location is found by calculating the intersection of
	 * a line drawn from the center point of a box to a reference point and that
	 * box. Code borrowed from org.eclipse.draw2d.ChopboxAnchor.
	 */
	public static Point getChopboxAnchor(Rectangle box, Point referencePoint) {

		double baseX = box.getCenterX();
		double baseY = box.getCenterY();
		double refX = referencePoint.x;
		double refY = referencePoint.y;

		// This avoids divide-by-zero
		if (box.isEmpty() || (refX == baseX && refY == baseY)) {
			return new Point((int) refX, (int) refY);
		}

		double dx = refX - baseX;
		double dy = refY - baseY;

		// r.width, r.height, dx, and dy are guaranteed to be non-zero.
		double scale = 0.5 / Math.max(Math.abs(dx) / box.width, Math.abs(dy)
				/ box.height);
		baseX += dx * scale;
		baseY += dy * scale;
		return new Point((int) Math.round(baseX), (int) Math.round(baseY));
	}
}