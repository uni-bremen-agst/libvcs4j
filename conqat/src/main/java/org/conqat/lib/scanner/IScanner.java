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
import java.io.Reader;

/**
 * Common interface for all scanners. Throws {@link ScannerException}s when
 * unrecognized characters are encountered in the input.
 * 
 * @author Florian Deissenboeck
 * @author $Author: kinnen $
 * @version $Revision: 41751 $
 * @ConQAT.Rating GREEN Hash: 3F03D537C9BA1916A8D6E5E3D597DF7F
 */
public interface IScanner {

	/**
	 * Returns the next token.
	 * 
	 * @return the next token. Returns {@link ETokenType#EOF} when current file
	 *         is entirely scanned.
	 * @throws ScannerException
	 *             Thrown upon scanner problems.
	 * @throws IOException
	 *             Thrown if the scanner encounters problem during file I/O.
	 */
	public IToken getNextToken() throws IOException, ScannerException;

	/**
	 * Reset the scanner.
	 * 
	 * @param reader
	 *            new input reader.
	 * @param originId
	 *            originId
	 */
	public void reset(Reader reader, String originId);

	/**
	 * Close scanner and any underlying readers.
	 * 
	 * @throws IOException
	 *             in case of an IO exception
	 */
	public void close() throws IOException;
}