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
package org.conqat.lib.scanner.highlighting;

import static org.conqat.lib.commons.html.ECSSProperty.COLOR;
import static org.conqat.lib.commons.html.ECSSProperty.FONT_FAMILY;
import static org.conqat.lib.commons.html.ECSSProperty.FONT_SIZE;
import static org.conqat.lib.commons.html.ECSSProperty.FONT_WEIGHT;

import java.awt.Color;
import java.awt.Font;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.conqat.lib.commons.cache4j.BasicCache;
import org.conqat.lib.commons.cache4j.ICache;
import org.conqat.lib.commons.cache4j.backend.ECachingStrategy;
import org.conqat.lib.commons.cache4j.backend.ICacheBackend;
import org.conqat.lib.commons.collections.ImmutablePair;
import org.conqat.lib.commons.collections.Pair;
import org.conqat.lib.commons.color.ColorUtils;
import org.conqat.lib.commons.error.NeverThrownRuntimeException;
import org.conqat.lib.commons.factory.IParameterizedFactory;
import org.conqat.lib.commons.html.CSSDeclarationBlock;
import org.conqat.lib.commons.html.ECSSProperty;
import org.conqat.lib.commons.html.EHTMLAttribute;
import org.conqat.lib.commons.html.EHTMLElement;
import org.conqat.lib.commons.html.HTMLWriter;
import org.conqat.lib.commons.region.Region;
import org.conqat.lib.commons.string.StringUtils;
import org.conqat.lib.scanner.ELanguage;
import org.conqat.lib.scanner.ETokenType;
import org.conqat.lib.scanner.IToken;
import org.conqat.lib.scanner.ScannerUtils;

/**
 * Helper class for formatting source code as HTML. This class is intended to be
 * customized by subclassing, although it can also be used as is.
 * 
 * @author $Author: goede $
 * @version $Rev: 44698 $
 * @ConQAT.Rating GREEN Hash: 14AF188C6BFED39BE1D6705854CD7818
 */
public class SourceCodeHtmlFormatter {

	/**
	 * Cache that converts from style information as provided by
	 * {@link SourceCodeStyle} to CSS. We use a cache not for performance
	 * reasons, but to keep the number of created CSS classes low. Thus, we use
	 * a cache that never loses information.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private final ICache<ImmutablePair<Color, Integer>, CSSDeclarationBlock, NeverThrownRuntimeException> cssCache = new BasicCache<ImmutablePair<Color, Integer>, CSSDeclarationBlock, NeverThrownRuntimeException>(
			null, new CSSStyleFactory(),
			(ICacheBackend) ECachingStrategy.UNLIMITED.getBackend(0));

	/** Base style for formatted code. */
	private CSSDeclarationBlock baseStyle;

	/** Style used for the line box of even lines. */
	private CSSDeclarationBlock evenLineStyle;

	/** Style used for the line box of odd lines. */
	private CSSDeclarationBlock oddLineStyle;

	/** Style used for the line number. */
	private CSSDeclarationBlock lineNumberStyle;

	/** Style used for disabled text. */
	private CSSDeclarationBlock disabledStyle;

	/** The writer for output. */
	protected HTMLWriter writer;

	/** The content. */
	private String content;

	/** Tokenized content. */
	private List<IToken> tokens;

	/** Disabled regions. */
	private Queue<Region> disabledRegions;

	/** The style used. */
	private SourceCodeStyle style;

	/** Global index into {@link #tokenIndex} during formatting. */
	private int tokenIndex;

	/** Global character position during formatting. */
	private int characterPosition;

	/**
	 * String to be used instead of a tab character. The default is the tab
	 * character (i.e. no replacement), but this can also be used to replace
	 * tabs with spaces.
	 */
	private String tabReplacement = "\t";

	/** Sets the string to be used instead of a tab character. */
	public void setTabReplacement(String tabReplacement) {
		this.tabReplacement = tabReplacement;
	}

	/** Formats the source code (given as token stream) using the writer. */
	public void formatSourceCode(String content, ELanguage language,
			HTMLWriter writer) {
		formatSourceCode(content, language, null, writer);
	}

	/**
	 * Formats the source code (given as token stream) using the writer.
	 * 
	 * @param disabledRegions
	 *            list of regions (begin and end offset) in the text that are
	 *            displayed as disabled. The regions may not overlap and must be
	 *            sorted by start position.
	 */
	public void formatSourceCode(String content, ELanguage language,
			List<Region> disabledRegions, HTMLWriter writer) {
		if (baseStyle == null) {
			init();
		}

		// we want to ensure single character line breaks to simplify offset
		// calculations later on (we only pass line arrays)
		this.content = StringUtils.replaceLineBreaks(content, "\n");
		tokens = ScannerUtils.getTokens(content, language);
		this.writer = writer;
		style = SourceCodeStyle.get(language);

		this.disabledRegions = new LinkedList<Region>();
		if (disabledRegions != null) {
			this.disabledRegions.addAll(disabledRegions);
		}

		tokenIndex = 0;
		characterPosition = 0;

		format();
	}

	/** Performs the actual formatting. */
	protected void format() {
		writer.setSuppressLineBreaks(true);
		String[] lines = StringUtils.splitLines(content);
		for (int lineIndex = 0; lineIndex < lines.length; ++lineIndex) {
			formatLine(lines[lineIndex], lineIndex + 1);
		}
		writer.setSuppressLineBreaks(false);
	}

	/** Formats a single line including line numbers. */
	protected void formatLine(String line, int lineNumber) {
		if (lineNumber % 2 == 0) {
			writer.openElement(EHTMLElement.DIV, EHTMLAttribute.CLASS,
					evenLineStyle);
		} else {
			writer.openElement(EHTMLElement.DIV, EHTMLAttribute.CLASS,
					oddLineStyle);
		}

		beforeFormatLine(lineNumber);

		formatLineNumber(lineNumber);
		insertLineNumberSpacer(lineNumber);
		formatLineContent(line, lineNumber);

		writer.closeElement(EHTMLElement.DIV);
		writer.addNewLine();
		characterPosition += 1;

		clearPassedDisabledRegions();
	}

	/**
	 * Removes all disabled regions from the head of the queue that end before
	 * the current character offset. Returns <code>true</code> if at least one
	 * region has been removed, or <code>false</code> otherwise.
	 */
	private boolean clearPassedDisabledRegions() {
		boolean regionRemoved = false;
		while (!disabledRegions.isEmpty()
				&& characterPosition >= disabledRegions.peek().getEnd()) {
			disabledRegions.remove();
			regionRemoved = true;
		}
		return regionRemoved;
	}

	/** Allows deriving classes to add style attributes */
	protected void beforeFormatLine(@SuppressWarnings("unused") int lineNumber) {
		// empty default implementation
	}

	/**
	 * Template method for inserting HTML between the line number and the line
	 * content.
	 * 
	 * @param lineNumber
	 *            the number of this line, if required by subclasses.
	 */
	protected void insertLineNumberSpacer(int lineNumber) {
		// empty default implementation
	}

	/** Formats the line number. */
	private void formatLineNumber(int lineNumber) {
		writer.addClosedTextElement(EHTMLElement.SPAN,
				formatLineNumberString(lineNumber), EHTMLAttribute.CLASS,
				lineNumberStyleFor(lineNumber));
	}

	/**
	 * Allows deriving classes to adapt the line number style according to the
	 * line number
	 */
	protected CSSDeclarationBlock lineNumberStyleFor(
			@SuppressWarnings("unused") int lineNumber) {
		return lineNumberStyle;
	}

	/** Formats the given line number as a string. */
	protected String formatLineNumberString(int lineNumber) {
		return String.format("%5d: ", lineNumber);
	}

	/**
	 * Formats the content of the line.
	 * 
	 * @param lineNumber
	 *            the number of this line, if required by subclasses.
	 */
	protected void formatLineContent(String line, int lineNumber) {
		CSSDeclarationBlock currentCSS = null;
		IToken currentToken = null;

		boolean disabled = !disabledRegions.isEmpty()
				&& disabledRegions.peek().containsPosition(characterPosition);
		if (disabled) {
			currentCSS = disabledStyle;
			switchCSS(null, currentCSS);
		}

		for (int i = 0; i < line.length(); ++i) {

			if (disabled) {
				if (clearPassedDisabledRegions()) {
					disabled = false;
					// kill current token to restart search for token
					currentToken = null;
				}
			} else if (!disabledRegions.isEmpty()
					&& disabledRegions.peek().containsPosition(
							characterPosition)) {
				disabled = true;
				switchCSS(currentCSS, disabledStyle);
				currentCSS = disabledStyle;
			} else if (currentToken == null
					|| characterPosition > currentToken.getEndOffset()) {
				CSSDeclarationBlock expectedCSS = null;
				while (tokenIndex < tokens.size()
						&& tokens.get(tokenIndex).getEndOffset() < characterPosition) {
					tokenIndex += 1;
				}
				if (tokenIndex < tokens.size()
						&& characterPosition >= tokens.get(tokenIndex)
								.getOffset()) {
					currentToken = tokens.get(tokenIndex);
					expectedCSS = determineCSS(currentToken.getType());
				}
				switchCSS(currentCSS, expectedCSS);
				currentCSS = expectedCSS;
			}

			insertCharacter(line.charAt(i));
			characterPosition += 1;
		}
		switchCSS(currentCSS, null);
	}

	/**
	 * Inserts a character. This performs escaping directly, instead of calling
	 * {@link HTMLWriter#addText(String)}, as this is significantly faster for
	 * single characters (addText uses regular expressions for escaping).
	 */
	private void insertCharacter(char c) {
		switch (c) {
		case '&':
			writer.addRawString("&amp;");
			break;
		case '<':
			writer.addRawString("&lt;");
			break;
		case '>':
			writer.addRawString("&gt;");
			break;
		case '\"':
			writer.addRawString("&quot;");
			break;
		case '\t':
			writer.addRawString(tabReplacement);
			break;
		default:
			writer.addRawString(Character.toString(c));
		}
	}

	/** Inserts code to switch between CSS classes (in required). */
	protected void switchCSS(CSSDeclarationBlock current,
			CSSDeclarationBlock next) {

		// Comparison with == is valid, as CSSDeclarationBlock has no proper
		// equals() implementation and the CSSManagerBase works with an
		// IdentityHashMap internally. Furthermore, the cache helps.
		if (current == next) {
			return;
		}

		if (current != null) {
			writer.closeElement(EHTMLElement.SPAN);
		}
		if (next != null) {
			writer.openElement(EHTMLElement.SPAN, EHTMLAttribute.CLASS, next);
		}
	}

	/** Returns the CSS class to be used for a token (or null). */
	protected CSSDeclarationBlock determineCSS(ETokenType type) {
		return cssCache.obtain(style.getStyle(type));
	}

	/**
	 * Lazy initialization. We do not initialize styles in the constructor, as
	 * this might lead to cases where this constructor calls overridden methods.
	 */
	protected void init() {
		baseStyle = createBaseStyle();
		evenLineStyle = createEvenLineStyle(baseStyle);
		oddLineStyle = createOddLineStyle(baseStyle);
		lineNumberStyle = createLineNumberStyle(baseStyle);
		disabledStyle = cssCache.obtain(getDisabledStyle());
	}

	/** Returns the style pair to be used for disabled code. */
	protected ImmutablePair<Color, Integer> getDisabledStyle() {
		return new Pair<Color, Integer>(new Color(0xaa, 0xaa, 0xaa), 0);
	}

	/** Creates the base style all other styles inherit from. */
	protected CSSDeclarationBlock createBaseStyle() {
		return new CSSDeclarationBlock(FONT_SIZE, "13px",
				ECSSProperty.WHITE_SPACE, "pre", FONT_FAMILY, "Monospace");
	}

	/** Creates the style used for the div element enclosing the line. */
	protected CSSDeclarationBlock createLineStyle(CSSDeclarationBlock baseStyle) {
		return new CSSDeclarationBlock(baseStyle, ECSSProperty.MARGIN_BOTTOM,
				"0");
	}

	/**
	 * Creates the style used for the div element enclosing even lines. Default
	 * implementation uses {@link #createLineStyle(CSSDeclarationBlock)}.
	 */
	protected CSSDeclarationBlock createEvenLineStyle(
			CSSDeclarationBlock baseStyle) {
		return createLineStyle(baseStyle);
	}

	/**
	 * Creates the style used for the div element enclosing odd lines. Default
	 * implementation uses {@link #createLineStyle(CSSDeclarationBlock)}.
	 */
	protected CSSDeclarationBlock createOddLineStyle(
			CSSDeclarationBlock baseStyle) {
		return createLineStyle(baseStyle);
	}

	/** Creates the style used for the line number. */
	protected CSSDeclarationBlock createLineNumberStyle(
			CSSDeclarationBlock baseStyle) {
		return new CSSDeclarationBlock(baseStyle, COLOR, "#aaaaaa");
	}

	/** Factory for converting AWT based styles to CSS. */
	private final class CSSStyleFactory
			implements
			IParameterizedFactory<CSSDeclarationBlock, ImmutablePair<Color, Integer>, NeverThrownRuntimeException> {

		/**
		 * Content of style parameter is defined in {@link SourceCodeStyle}
		 * class.
		 */
		@Override
		public CSSDeclarationBlock create(ImmutablePair<Color, Integer> style) {
			CSSDeclarationBlock css = new CSSDeclarationBlock(baseStyle);
			css.setProperty(COLOR, ColorUtils.toHtmlString(style.getFirst()));
			if ((style.getSecond() & Font.BOLD) != 0) {
				css.setProperty(FONT_WEIGHT, "bold");
			}
			if ((style.getSecond() & Font.ITALIC) != 0) {
				css.setProperty(ECSSProperty.FONT_STYLE, "italic");
			}
			return css;
		}
	}
}
