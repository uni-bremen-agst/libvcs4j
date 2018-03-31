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

import org.conqat.lib.commons.collections.ILookahead;

/**
 * Statement oracles detect statement boundaries. Statement oracles can be
 * obtained via {@link ELanguage#getStatementOracle()}.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: A0CE03E33527E417456C1ECAC2C9431D
 */
public interface IStatementOracle {

	/**
	 * Determines if the current token type ends a statement.
	 * 
	 * @param tokenType
	 *            the current token type
	 */
	public <X extends Exception> boolean isEndOfStatementTokenType(
			ETokenType tokenType, ILookahead<ETokenType, X> lookahead) throws X;
	
	/**
	 * Determines if the current token type ends a statement.
	 * 
	 * @param tokenType
	 *            the current token type
	 */
	public <X extends Exception> boolean isEndOfStatementToken(
			ETokenType tokenType, ILookahead<IToken, X> lookahead) throws X;

}