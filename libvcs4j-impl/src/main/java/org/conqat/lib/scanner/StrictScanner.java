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

import org.conqat.lib.scanner.ETokenType.ETokenClass;

/**
 * Wrapps an {@link ILenientScanner} and converts tokens of
 * {@link ETokenClass#ERROR} into {@link ScannerException}s.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 35769 $
 * @ConQAT.Rating GREEN Hash: 6FAB77B981943CF6DD36184D6C902773
 */
public class StrictScanner implements IScanner {

	/** Underlying lenient scanner */
	private final ILenientScanner scanner;

	/**
	 * Constructor
	 * 
	 * @param scanner
	 *            wrapped {@link ILenientScanner}
	 */
	public StrictScanner(ILenientScanner scanner) {
		this.scanner = scanner;
	}

	/** {@inheritDoc} */
	@Override
	public IToken getNextToken() throws IOException, ScannerException {
		IToken token = scanner.getNextToken();

		if (token != null && token.getType().isError()) {
			throw new ScannerException(token.getType(), token.getText(), token
					.getLineNumber());
		}

		return token;
	}

	/** {@inheritDoc} */
	@Override
	public void close() throws IOException {
		scanner.close();
	}

	/** {@inheritDoc} */
	@Override
	public void reset(Reader reader, String originId) {
		scanner.reset(reader, originId);
	}

}