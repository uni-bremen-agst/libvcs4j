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

import org.conqat.lib.scanner.ETokenType.ETokenClass;

/**
 * Lenient scanner that, in contrast to {@link IScanner}, does not throw
 * {@link ScannerException}s for unrecognized characters, but returns tokens of
 * token class {@link ETokenClass#ERROR} and resumes scanning.
 * 
 * @author $Author: hummelb $
 * @version $Revision: 35769 $
 * @ConQAT.Rating GREEN Hash: BAC60D7F84AB5E43F458FDA11184E88C
 */
public interface ILenientScanner extends IScanner {

	/**
	 * Returns the next token.
	 * 
	 * @return the next token. Returns {@link ETokenType#EOF} when current file
	 *         is entirely scanned.
	 * @throws IOException
	 *             Thrown if the scanner encounters problem during file I/O.
	 */
	@Override
	public IToken getNextToken() throws IOException;

}