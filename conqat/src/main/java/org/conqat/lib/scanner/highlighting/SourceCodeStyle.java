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
package org.conqat.lib.scanner.highlighting;

import java.awt.Color;
import java.awt.Font;

import org.conqat.lib.commons.collections.ImmutablePair;
import org.conqat.lib.scanner.ELanguage;
import org.conqat.lib.scanner.ETokenType;
import org.conqat.lib.scanner.IToken;
import org.conqat.lib.scanner.ETokenType.ETokenClass;

/**
 * Class that determines the style used when displaying source code.
 * 
 * @author $Author: juergens $
 * @version $Rev: 35756 $
 * @ConQAT.Rating GREEN Hash: F633DE0DE8690FCF1D782339E87D860B
 */
public class SourceCodeStyle {

	/** Color used for comments. */
	private static final Color JAVA_COMMENT_COLOR = new Color(63, 127, 95);

	/** Color used for JavaDoc comments. */
	private static final Color JAVA_DOCCOMMENT_COLOR = new Color(63, 95, 191);

	/** Color used for Java keywords. */
	private static final Color JAVA_KEYWORD_COLOR = new Color(127, 0, 85);

	/** Color used for Java literals. */
	private static final Color JAVA_LITERAL_COLOR = new Color(42, 0, 255);

	/** Color used for C# keywords. */
	private static final Color CS_KEYWORD_COLOR = new Color(0, 0, 255);

	/** Color used for C# comments. */
	private static final Color CS_COMMENT_COLOR = new Color(0, 128, 0);

	/** Color used for C# literals. */
	private static final Color CS_LITERAL_COLOR = new Color(163, 21, 21);

	/** Color used for C# specials. */
	private static final Color CS_SPECIAL_COLOR = new Color(200, 200, 200);

	/** Color used for C# doc comments. */
	private static final Color CS_DOCCOMMENT_COLOR = new Color(128, 128, 128);

	/** The default style used. */
	private static SourceCodeStyle DEFAULT_STYLE = new SourceCodeStyle();

	/**
	 * The style used for Java. As the default style if already Java-like, we
	 * only have to handle JavaDoc comments here.
	 */
	private static SourceCodeStyle JAVA_STYLE = new SourceCodeStyle() {
		@Override
		public ImmutablePair<Color, Integer> getStyle(ETokenType tokenType) {
			if (tokenType == ETokenType.DOCUMENTATION_COMMENT) {
				return style(JAVA_DOCCOMMENT_COLOR);
			}

			return super.getStyle(tokenType);
		}
	};

	/**
	 * The style used for PL/SQL. The only difference is that we highlight
	 * operators the same as keywords.
	 */
	private static SourceCodeStyle PLSQL_STYLE = new SourceCodeStyle() {
		@Override
		public ImmutablePair<Color, Integer> getStyle(ETokenType tokenType) {
			if (tokenType.getTokenClass() == ETokenClass.OPERATOR) {
				// we use "if" but any keyword will do
				return super.getStyle(ETokenType.IF);
			}
			return super.getStyle(tokenType);
		}
	};

	/** The style used for C#. */
	private static SourceCodeStyle CS_STYLE = new SourceCodeStyle() {
		@Override
		public ImmutablePair<Color, Integer> getStyle(ETokenType tokenType) {
			if (tokenType == ETokenType.DOCUMENTATION_COMMENT) {
				return style(CS_DOCCOMMENT_COLOR);
			}

			switch (tokenType.getTokenClass()) {
			case KEYWORD:
				return style(CS_KEYWORD_COLOR);
			case LITERAL:
				return style(CS_LITERAL_COLOR);
			case COMMENT:
				return style(CS_COMMENT_COLOR);
			case SPECIAL:
				return style(CS_SPECIAL_COLOR);
			default:
				return super.getStyle(tokenType);
			}
		}
	};

	/** The style used for plain text. */
	private static SourceCodeStyle PLAIN_TEXT_STYLE = new SourceCodeStyle() {
		@Override
		public ImmutablePair<Color, Integer> getStyle(ETokenType tokenType) {
			return style(Color.BLACK);
		}
	};

	/** Constructor. */
	private SourceCodeStyle() {
		// empty
	}

	/**
	 * Returns the color/font style pair for a given token type. The default
	 * implementation provides a generic highlighting roughly following the one
	 * of java.
	 */
	public ImmutablePair<Color, Integer> getStyle(ETokenType tokenType) {
		switch (tokenType.getTokenClass()) {
		case KEYWORD:
			return style(JAVA_KEYWORD_COLOR, Font.BOLD);
		case LITERAL:
			return style(JAVA_LITERAL_COLOR);
		case SYNTHETIC:
		case ERROR:
			return style(Color.RED, Font.BOLD);
		case COMMENT:
			return style(JAVA_COMMENT_COLOR);
		default:
			return style(Color.BLACK);
		}
	}

	/** Returns the color/font style pair for a given token. */
	public ImmutablePair<Color, Integer> getStyle(IToken token) {
		return getStyle(token.getType());
	}

	/** Factory method to simplify creation of style pairs. */
	private static ImmutablePair<Color, Integer> style(Color color) {
		return style(color, Font.PLAIN);
	}

	/** Factory method to simplify creation of style pairs. */
	private static ImmutablePair<Color, Integer> style(Color color, int style) {
		return new ImmutablePair<Color, Integer>(color, style);
	}

	/** Returns the style for a given language. */
	public static SourceCodeStyle get(ELanguage language) {
		switch (language) {
		case TEXT:
			return PLAIN_TEXT_STYLE;
		case JAVA:
			return JAVA_STYLE;
		case CS:
		case VB:
			return CS_STYLE;
		case PLSQL:
			return PLSQL_STYLE;
		default:
			return DEFAULT_STYLE;
		}
	}
}
