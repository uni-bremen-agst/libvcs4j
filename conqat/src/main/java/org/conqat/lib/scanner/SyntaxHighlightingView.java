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
package org.conqat.lib.scanner;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;

import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.PlainView;
import javax.swing.text.Utilities;

import org.conqat.lib.scanner.ETokenType.ETokenClass;

/**
 * View to be used with CCSM scanners to implement syntax highlighting in Swing
 * text components.
 * 
 * @author deissenb
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 80A1190CB988103EC57233521112CF40
 * @see SyntaxHighlightingViewFactory
 */
/* package */class SyntaxHighlightingView extends PlainView {

	/** Scanner to be used. */
	private final IScanner scanner;

	/** Maps from token class to color. */
	private final HashMap<ETokenClass, Color> colorMap;

	/**
	 * Maps from token class to font style. Style integers are defined by class
	 * {@link Font}.
	 */
	private final HashMap<ETokenClass, Integer> styleMap;

	/**
	 * Create new view factory.
	 * 
	 * @param scanner
	 *            scanner to be used.
	 * @param colorMap
	 *            maps from token class to color.
	 * @param styleMap
	 *            maps from token class to font style. Style integers are
	 *            defined by class {@link Font}.
	 */
	public SyntaxHighlightingView(Element element, IScanner scanner,
			HashMap<ETokenClass, Color> colorMap,
			HashMap<ETokenClass, Integer> styleMap) {
		super(element);
		this.scanner = scanner;
		this.colorMap = colorMap;
		this.styleMap = styleMap;
	}

	/**
	 * Selected text is drawn like unselected text (
	 * {@link #drawUnselectedText(Graphics, int, int, int, int)}.
	 */
	@Override
	protected int drawSelectedText(Graphics g, int x, int y, int p0, int p1)
			throws BadLocationException {
		return drawUnselectedText(g, x, y, p0, p1);
	}

	/** {@inheritDoc} */
	@Override
	protected int drawUnselectedText(Graphics graphics, int x, int y,
			int startIndex, int endIndex) throws BadLocationException {
		String text = getDocument().getText(startIndex, endIndex - startIndex);
		scanner.reset(new StringReader(text), null);

		IToken token;
		IToken lastToken = null;

		try {
			while ((token = scanner.getNextToken()).getType() != ETokenType.EOF) {
				x = handleWhitespace(graphics, x, y, startIndex, token,
						lastToken);

				x = drawText(startIndex + token.getOffset(), token.getText()
						.length(), graphics, x, y, token);

				lastToken = token;
			}
		} catch (ScannerException e) {
			// this is handled by the code below
		} catch (IOException e) {
			// this is handled by the code below
		}

		if (lastToken != null) {
			int index = startIndex + lastToken.getOffset()
					+ lastToken.getText().length();
			return drawTextPlain(index, endIndex - index, graphics, x, y);
		}

		// apparently there was an exception on the very first token, don't
		// highlight at all
		return super.drawSelectedText(graphics, x, y, startIndex, endIndex);
	}

	/**
	 * Draw text.
	 * 
	 * @param offset
	 *            offset of the text within the document.
	 * @param length
	 *            length of the text
	 * @param graphics
	 *            graphics object
	 * @param x
	 *            x coordinate
	 * @param y
	 *            y coordinate
	 * @param color
	 *            color
	 * @param style
	 *            style, defined by the style integers in class {@link Font}.
	 * @return the X position at the end of the rendered text.
	 */
	private int drawText(int offset, int length, Graphics graphics, int x,
			int y, Color color, int style) throws BadLocationException {
		graphics.setColor(color);
		graphics.setFont(graphics.getFont().deriveFont(style));
		getDocument().getText(offset, length, getLineBuffer());
		return Utilities.drawTabbedText(getLineBuffer(), x, y, graphics, this,
				offset);
	}

	/**
	 * Draw highlighted text. Color and style a determined by the token class.
	 * For other parameters see
	 * {@link #drawText(int, int, Graphics, int, int, Color, int)}.
	 */
	private int drawText(int offset, int length, Graphics graphics, int x,
			int y, IToken token) throws BadLocationException {
		return drawText(offset, length, graphics, x, y, getColor(token),
				getStyle(token));
	}

	/**
	 * Draw text without highlighting. See
	 * {@link #drawText(int, int, Graphics, int, int, Color, int)} for parameter
	 * description.
	 */
	private int drawTextPlain(int offset, int length, Graphics graphics, int x,
			int y) throws BadLocationException {
		return drawText(offset, length, graphics, x, y, Color.black, Font.PLAIN);
	}

	/** Get color for token. */
	private Color getColor(IToken token) {
		Color color = colorMap.get(token.getType().getTokenClass());
		if (color != null) {
			return color;
		}
		return Color.BLACK;
	}

	/** Get style for token. */
	private int getStyle(IToken token) {
		Integer style = styleMap.get(token.getType().getTokenClass());
		if (style != null) {
			return style;
		}
		return Font.PLAIN;
	}

	/**
	 * The scanners do not return white space. Hence, we need this method to
	 * handle white space.
	 */
	private int handleWhitespace(Graphics g, int x, int y, int startIndex,
			IToken token, IToken lastToken) throws BadLocationException {

		// start of line
		if (lastToken == null) {
			int whitespace = token.getOffset();

			// line starts with whitespace
			if (whitespace > 0) {
				return drawTextPlain(startIndex, whitespace, g, x, y);
			}
			return x;
		}

		int whitespace = token.getOffset()
				- (lastToken.getOffset() + lastToken.getText().length());
		if (whitespace > 0) {
			return drawTextPlain(startIndex + lastToken.getOffset()
					+ lastToken.getText().length(), whitespace, g, x, y);
		}

		return x;
	}

}