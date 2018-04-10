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

import java.util.EnumSet;
import java.util.Set;

import org.conqat.lib.commons.collections.ILookahead;

/**
 * Statement oracle class that is based on a simple set of {@link ETokenType}s
 * that describe statement boundaries. The {@link ETokenType#SENTINEL} is always
 * treated as statement boundary.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 608F2D0998E168A0E1CF7224C27F8214
 */
/* package */class StatementOracle implements IStatementOracle {

	/** Set of statement delimiters. */
	private final EnumSet<ETokenType> statementDelimiters = EnumSet
			.noneOf(ETokenType.class);

	/** Create new statement oracle. */
	public StatementOracle(Set<ETokenType> statementDelimiters) {
		this.statementDelimiters.add(ETokenType.SENTINEL);
		this.statementDelimiters.addAll(statementDelimiters);
	}

	/** {@inheritDoc} */
	@Override
	public <X extends Exception> boolean isEndOfStatementTokenType(
			ETokenType tokenType, ILookahead<ETokenType, X> lookahead) {
		return statementDelimiters.contains(tokenType);
	}

	/** {@inheritDoc} */
	@Override
	public <X extends Exception> boolean isEndOfStatementToken(
			ETokenType tokenType, ILookahead<IToken, X> lookahead) {
		return statementDelimiters.contains(tokenType);
	}

}