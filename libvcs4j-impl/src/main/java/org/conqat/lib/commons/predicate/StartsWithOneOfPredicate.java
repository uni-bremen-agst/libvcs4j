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
package org.conqat.lib.commons.predicate;

import org.conqat.lib.commons.string.StringUtils;

/**
 * A predicate that selects strings that start with one of the given prefixes.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 50C248869B66FC7FF10E98558A99793E
 */
public class StartsWithOneOfPredicate implements IPredicate<String> {

	/** The prefixes we check for. */
	private final String[] prefixes;

	/** Constructor. */
	public StartsWithOneOfPredicate(String... prefixes) {
		this.prefixes = prefixes.clone();
	}

	/** {@inheritDoc} */
	@Override
	public boolean isContained(String element) {
		return StringUtils.startsWithOneOf(element, prefixes);
	}

}