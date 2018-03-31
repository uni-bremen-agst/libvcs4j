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
package org.conqat.lib.scanner;

import java.util.List;

import org.conqat.lib.commons.assertion.CCSMPre;
import org.conqat.lib.commons.collections.ILookahead;
import org.conqat.lib.commons.error.NeverThrownRuntimeException;

/**
 * {@link ILookahead} on Lists of ITokens
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: CEF161CE61440CD28040D2A78A557104
 */
public class TokenListLookahead implements
		ILookahead<ETokenType, NeverThrownRuntimeException> {

	/** Underlying list */
	private List<IToken> list;

	/** Current position in list */
	private int index;

	/** Set list and position */
	public TokenListLookahead(List<IToken> tokens, int index) {
		CCSMPre.isFalse(index < 0, "Index must not be negative but was: "
				+ index);

		this.list = tokens;
		this.index = index;
	}

	/** {@inheritDoc} */
	@Override
	public ETokenType lookahead(int lookahead) {
		CCSMPre.isFalse(lookahead < 0,
				"Lookahead must not be negative but was: " + lookahead);

		int local = index + lookahead;
		if (local >= list.size()) {
			return null;
		}
		return list.get(local).getType();
	}
}
