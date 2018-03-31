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

/**
 * Object of type {@link IToken} are returned by the scanners. {@link IToken}s
 * are immutable.
 * 
 * @author $Author: hummelb $
 * @version $Revision: 49563 $
 * @ConQAT.Rating GREEN Hash: 713DC79689003AFD723AC06A226C593D
 */
public interface IToken {

	/**
	 * Obtain the original input text for a token (copied verbatim from the
	 * source).
	 */
	public String getText();

	/**
	 * Get the number of characters before this token in the text. The offset is
	 * 0-based and inclusive.
	 */
	public int getOffset();

	/**
	 * Get the number of characters before the end of this token in the text
	 * (i.e. the 0-based index of the last character, inclusive).
	 */
	public int getEndOffset();

	/**
	 * Obtain number of line this token was found at. Counting starts at 0.
	 */
	public int getLineNumber();

	/**
	 * Get string that identifies the origin of this token. This can, e.g., be a
	 * uniform path to the resource. Its actual content depends on how the token
	 * gets constructed.
	 */
	public String getOriginId();

	/**
	 * Obtain type of token.
	 */
	public ETokenType getType();

	/**
	 * Obtain language.
	 */
	public ELanguage getLanguage();

	/**
	 * Create new token. Can be used to clone or create modified copies of
	 * tokens.
	 * 
	 * @param type
	 *            Token type of new token
	 * @param offset
	 *            Offset of new token
	 * @param lineNumber
	 *            LineNumber of new token
	 * @param text
	 *            Text of new token
	 * @param originId
	 *            Origin id of new token
	 * @return New token with set values of the same java type as the token on
	 *         which the method was called.
	 */
	public IToken newToken(ETokenType type, int offset, int lineNumber,
			String text, String originId);
}