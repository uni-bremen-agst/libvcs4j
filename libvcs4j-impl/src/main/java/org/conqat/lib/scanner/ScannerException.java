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

import org.conqat.lib.scanner.ETokenType.ETokenClass;

/**
 * Generated scanners throw exceptions of this description to signal scanning
 * problems. This class is immutable.
 * 
 * @author $Author: hummelb $
 * @version $Revision: 50869 $
 * @ConQAT.Rating GREEN Hash: 45B0C663E3D486D9365B4A30B7F311AA
 */
public class ScannerException extends Exception {

	/** Version used for serialization. */
	private static final long serialVersionUID = 1;

	/** Exception description. */
	private final ETokenType type;

	/** A verbatim copy of the scanned text. */
	private final String yyText;

	/** The line number where the problem occurred. */
	private final int position;

	/**
	 * Create a new <code>ScannerException</code>.
	 * 
	 * @param type
	 *            the token type representing the scanner error. Expected to
	 *            have token class {@link ETokenClass#ERROR}.
	 * @param yyText
	 *            A verbatim copy of the scanned text.
	 * @param position
	 *            The line number where the problem occurred.
	 */
	public ScannerException(ETokenType type, String yyText, int position) {
		this.type = type;
		this.yyText = yyText;
		this.position = position;
	}

	/**
	 * Get an error message.
	 * 
	 * @return An error message, including line number and scanned text.
	 */
	@Override
	public String getMessage() {
		return type.name().toLowerCase().replace('_', ' ') + " at line "
				+ position + " [" + yyText + "]";
	}

	/** Returns the line number where the proplem ocurred. */
	public int getLineNumber() {
		return position;
	}
}