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
package org.conqat.lib.commons.string;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.PatternSyntaxException;

/**
 * This class allows the application of multiplex {@link IRegexReplacement}s to
 * a string.
 * 
 * 
 * @author Florian Deissenboeck
 * @author $Author: hummelb $
 * @version $Rev: 47169 $
 * @ConQAT.Rating GREEN Hash: FBFB6034BF6946EA1D9A1B835094E4DD
 */
public class RegexReplacementProcessor {

	/** The list of replacements. */
	private final List<IRegexReplacement> expressions;

	/** Create a new replacement processor. */
	public RegexReplacementProcessor(List<IRegexReplacement> expressions) {
		this.expressions = expressions;
	}

	/**
	 * Apply replacements to a string.
	 * 
	 * @return the input string after the application of all replacements or the
	 *         input string if the list of replacements is empty.
	 * @throws PatternSyntaxException
	 *             unfortunately method
	 *             {@link Matcher#replaceAll(java.lang.String)} throws an
	 *             {@link IndexOutOfBoundsException} if a non-existent capturing
	 *             group is referenced. This method converts this exception to a
	 *             {@link PatternSyntaxException}.
	 */
	public String process(String text) throws PatternSyntaxException {
		String result = text;
		for (IRegexReplacement expr : expressions) {
			Matcher matcher = expr.getPattern().matcher(result);
			String replacement = expr.getReplacement();

			try {
				result = matcher.replaceAll(replacement);
			} catch (IndexOutOfBoundsException ex) {
				throw new PatternSyntaxException(ex.getMessage(), replacement,
						-1);
			}
		}

		return result;
	}
}