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
package org.conqat.lib.commons.cache4j;

/**
 * Exception thrown in case of problems during rule parsing.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 09F741342A7CDF30C1E1DE66D8C07556
 */
public class CacheRuleParsingException extends Exception {

	/** Version used for serialization. */
	private static final long serialVersionUID = 1;

	/** Line number this exception refers to. */
	private int line = -1;

	/** Constructor. */
	public CacheRuleParsingException(String message) {
		super(message);
	}

	/** Constructor. */
	public CacheRuleParsingException(String message, Throwable cause) {
		super(message, cause);
	}

	/** Sets the line number this exception originates from. */
	public void setLine(int line) {
		this.line = line;
	}

	/** {@inheritDoc} */
	@Override
	public String getMessage() {
		if (line < 0) {
			return super.getMessage() + " (unknown line)";
		}

		return super.getMessage() + " (line: " + line + ")";
	}
}