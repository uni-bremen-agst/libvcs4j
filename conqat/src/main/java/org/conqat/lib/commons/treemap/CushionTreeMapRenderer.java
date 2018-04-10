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
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import org.conqat.lib.commons.color.MultiColor;

/**
 * A tree map renderer using "cushions" as described in J. van Wijk, H. van de
 * Wetering: "Cushion Treemaps: Visualization of Hierarchical Information".
 * 
 * @author Benjamin Hummel
 * @author $Author: goeb $
 * @version $Rev: 51604 $
 * @ConQAT.Rating GREEN Hash: DBB743F4FAB8614ECDF3D9E944599C83
 */
public class CushionTreeMapRenderer implements ITreeMapRenderer {

	/** The height parameter for the cushions. */
	private final double h;

	/** The height scale factor. */
	private final double f;

	/**
	 * Constructor.
	 * 
	 * @param h
	 *            the height parameter giving the heigt of the cushions relative
	 *            to their size. 0.5 seems to be a reasonable value.
	 * @param f
	 *            the scale factor used to reduce the heights of nested
	 *            cushions. The value should be between 0 and 1, where smaller
	 *            values will reduce the cushion effect.
	 */
	public CushionTreeMapRenderer(double h, double f) {
		this.h = h;
		this.f = f;
	}

	/** {@inheritDoc} */
	@Override
	public <T> void renderTreeMap(ITreeMapNode<T> node, Graphics2D graphics) {
		// use loop here, to avoid adding cushion to top level node
		for (ITreeMapNode<T> child : node.getChildren()) {
			render(child, graphics, h, new double[4]);
		}
	}

	/**
	 * Renders the given node.
	 * 
	 * @param node
	 *            the node to render.
	 * @param g
	 *            the graphics to render into.
	 * @param height
	 *            the current height (already scaled for this level).
	 * @param coefs
	 *            the coefficients of the local parabola. The indices 0 and 1
	 *            give the coefficients for x^2 and x, while 2 and 3 are for y^2
	 *            and y. The constant part is not needed.
	 */
	private <T> void render(ITreeMapNode<T> node, Graphics2D g, double height,
			double[] coefs) {
		Rectangle2D rect = node.getLayoutRectangle();
		if (rect == null) {
			return;
		}

		double[] myCoefs = addLocalParabola(height, coefs, rect);
		if (node.getChildren().isEmpty()) {
			renderCushion(rect, myCoefs, g, node.getColor(),
					node.getPatternColor(), node.getDrawingPattern());
		} else if (node.getChildren().size() == 1) {
			// do not scale height or add cushion
			render(node.getChildren().get(0), g, height, coefs);
		} else {
			for (ITreeMapNode<T> child : node.getChildren()) {
				render(child, g, height * f, myCoefs);
			}
		}
	}

	/** Adds the local parabola to the given coefs and returns the result. */
	private double[] addLocalParabola(double height, double[] coefs,
			Rectangle2D rect) {
		double[] myCoefs = new double[4];
		double x1 = rect.getMinX();
		double x2 = rect.getMaxX();
		double y1 = rect.getMinY();
		double y2 = rect.getMaxY();
		myCoefs[0] = coefs[0] - 4 * height / (x2 - x1);
		myCoefs[1] = coefs[1] + 4 * height * (x1 + x2) / (x2 - x1);
		myCoefs[2] = coefs[2] - 4 * height / (y2 - y1);
		myCoefs[3] = coefs[3] + 4 * height * (y1 + y2) / (y2 - y1);
		return myCoefs;
	}

	/** Renders the given cushion. */
	private void renderCushion(Rectangle2D rect, double[] coefs, Graphics2D g,
			Color baseColor, Color patternColor, IDrawingPattern drawingPattern) {

		// light normal taken from the cited paper.
		final double lx = 0.09759;
		final double ly = 0.19518;
		final double lz = 0.9759;

		int minX = (int) (rect.getMinX() + .5);
		int minY = (int) (rect.getMinY() + .5);
		int maxX = (int) (rect.getMaxX() + .5);
		int maxY = (int) (rect.getMaxY() + .5);

		for (int x = minX; x < maxX; ++x) {
			for (int y = minY; y < maxY; ++y) {
				double nx = -(2 * coefs[0] * (x + .5) + coefs[1]);
				double ny = -(2 * coefs[2] * (y + .5) + coefs[3]);
				double norm = Math.sqrt(nx * nx + ny * ny + 1);
				double cosa = (nx * lx + ny * ly + lz) / norm;

				Color color = determineBaseColor(x, y, baseColor, patternColor,
						drawingPattern, rect);

				g.setColor(shadeColor(color, .2 + .8 * Math.max(0, cosa)));
				g.drawLine(x, y, x, y);
			}
		}
	}

	/** Determines the base color to be used for a given pixel. */
	private Color determineBaseColor(int x, int y, Color baseColor,
			Color patternColor, IDrawingPattern drawingPattern, Rectangle2D rect) {
		if (drawingPattern != null && drawingPattern.isForeground(x, y)) {
			return resolveMultiColor(x, y, rect, patternColor);
		}
		return resolveMultiColor(x, y, rect, baseColor);
	}

	/**
	 * Resolves the pixel color with special handling for multi color. The
	 * colors are arranged in a striped pattern, which is arranged horizontally
	 * or vertically depending on the aspect ratio of the rectangle.
	 */
	private Color resolveMultiColor(int x, int y, Rectangle2D rect, Color color) {
		if (!(color instanceof MultiColor)) {
			return color;
		}

		MultiColor multiColor = (MultiColor) color;
		double relative;
		if (rect.getWidth() > rect.getHeight()) {
			relative = (x - rect.getX()) / rect.getWidth();
		} else {
			relative = (y - rect.getY()) / rect.getHeight();
		}

		double current = 0;
		for (int i = 0; i < multiColor.size(); ++i) {
			current += multiColor.getRelativeFrequency(i);
			if (current > relative) {
				return multiColor.getColor(i);
			}
		}

		// can only be reached in case of rounding errors
		return multiColor.getColor(multiColor.size() - 1);
	}

	/**
	 * Calculate the shaded color.
	 * 
	 * @param color
	 *            the base color.
	 * @param luminance
	 *            a parameter between 0 and 1, where 0 corresponds to black and
	 *            1 to white.
	 */
	private Color shadeColor(Color color, double luminance) {
		int base = 0;
		luminance *= 2;
		if (luminance > 1) {
			luminance = 2 - luminance;
			base = (int) (255 * (1 - luminance));
		}

		return new Color((int) (color.getRed() * luminance) + base,
				(int) (color.getGreen() * luminance) + base,
				(int) (color.getBlue() * luminance) + base);
	}
}