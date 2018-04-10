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
package org.conqat.lib.commons.collections;

import java.util.Comparator;

/**
 * A comparator for strings that sorts numbers (represented as strings)
 * "correctly".
 * 
 * @author $Author: goede $
 * @version $Rev: 39887 $
 * @ConQAT.Rating GREEN Hash: A7CB6C9EC6381FBBEA3F4F6CE2117BCD
 */
public class SmartStringComparator implements Comparator<String> {

	/** {@inheritDoc} */
	@Override
	public int compare(String s1, String s2) {
		try {
			double d1 = Double.parseDouble(s1);
			double d2 = Double.parseDouble(s2);
			return Double.compare(d1, d2);
		} catch (NumberFormatException e) {
			// compare based on strings
			return s1.compareTo(s2);
		}
	}
}
