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
 * Base class for implementations of the token interface. This class is
 * immutable.
 * 
 * @author $Author: kinnen $
 * @version $Revision: 41751 $
 * @ConQAT.Rating GREEN Hash: 6F237C5B996E5883B0D05A94FCB31D96
 */
public abstract class Token implements IToken {

	/** Token type. */
	protected final ETokenType type;

	/** Number of characters before token in the resource it originates from */
	protected final int offset;

	/** Line number token was found at. */
	protected final int lineNumber;

	/** Verbatim copy of the text found. */
	protected final String text;

	/** Identifier of the resource this token originates from */
	protected final String originId;

	/** Constructor. */
	protected Token(ETokenType type, int offset, int lineNumber, String text,
			String originId) {
		this.type = type;
		this.offset = offset;
		this.lineNumber = lineNumber;
		this.text = text;
		this.originId = originId;
	}

	/**
	 * Obtain string representation of this token. This is meant for debugging
	 * purposes.
	 */
	@Override
	public String toString() {
		String result = "TOKEN (" + type + ") Text: >>" + text + "<<";

		result += " Origin: '" + originId + "' line#: " + lineNumber;
		return result;
	}

	/** {@inheritDoc} */
	@Override
	public String getText() {
		return text;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getOffset() {
		return offset;
	}

	/** {@inheritDoc} */
	@Override
	public int getEndOffset() {
		return offset + text.length() - 1;
	}

	/** {@inheritDoc} */
	@Override
	public int getLineNumber() {
		return lineNumber;
	}

	/** {@inheritDoc} */
	@Override
	public String getOriginId() {
		return originId;
	}

	/** {@inheritDoc} */
	@Override
	public ETokenType getType() {
		return type;
	}

}