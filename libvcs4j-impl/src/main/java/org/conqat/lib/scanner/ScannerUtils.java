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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class offers utility methods for scanners.
 * 
 * @author Florian Deissenboeck
 * @author Elmar Juergens
 * @author $Author: hummelb $
 * @version $Rev: 47108 $
 * @ConQAT.Rating GREEN Hash: 7653C46FCA092A85783E4D6D1B975488
 */
public class ScannerUtils {

	/**
	 * Read tokens from a strict scanner and store them in a list. This expects
	 * the scanner to return a token with type {@link ETokenType#EOF} at the end
	 * of the file. This last token will not be added to the list of tokens.
	 * 
	 * @param scanner
	 *            the scanner to read from
	 * @param tokens
	 *            the list to store tokens
	 * @param exceptions
	 *            another list to store possible exceptions that occurred during
	 *            scanning.
	 * @throws IOException
	 *             thrown if scanner throws an IO exception
	 */
	public static void readTokens(IScanner scanner, List<IToken> tokens,
			List<ScannerException> exceptions) throws IOException {

		IToken token = null;

		do {
			try {
				token = scanner.getNextToken();
				if (token.getType() != ETokenType.EOF) {
					tokens.add(token);
				}
			} catch (ScannerException e) {
				exceptions.add(e);
				continue;
			}
		} while (token != null && token.getType() != ETokenType.EOF);

	}

	/**
	 * Read tokens from a lenient scanner and store them in a list. This expects
	 * the scanner to return a token with type {@link ETokenType#EOF} at the end
	 * of the file. This last token will not be added to the list of tokens.
	 * 
	 * @param scanner
	 *            the scanner to read from
	 * 
	 * @throws IOException
	 *             thrown if scanner throws an IO exception
	 */
	public static List<IToken> readTokens(ILenientScanner scanner)
			throws IOException {

		List<IToken> tokens = new ArrayList<IToken>();

		IToken token = null;
		while ((token = scanner.getNextToken()).getType() != ETokenType.EOF) {
			tokens.add(token);
		}

		return tokens;

	}

	/**
	 * Returns the results of scanning a string. The origin of the tokens is set
	 * to some arbitrary value.
	 */
	public static List<IToken> getTokens(String content, ELanguage language) {
		return getTokens(content, language, "origin");
	}

	/**
	 * Returns the results of scanning a string. The origin of the tokens is set
	 * to some arbitrary value.
	 */
	public static List<IToken> getTokens(String content, ELanguage language,
			String origin) {
		ILenientScanner scanner = ScannerFactory.newLenientScanner(language,
				content, origin);
		try {
			return readTokens(scanner);
		} catch (IOException e) {
			throw new AssertionError(
					"can not happen as the scanner is lenient and we are working from memory");
		}
	}
}