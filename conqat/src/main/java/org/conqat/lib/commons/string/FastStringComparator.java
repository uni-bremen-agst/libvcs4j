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

import java.util.Comparator;

/**
 * This is a more efficient implementation of a String comparator. While the
 * comparison order is stable, there is no guarantee that is is lexicographic.
 * The additional speed is gained by using the hash code as primary comparison
 * attribute. As the hash code is cached by the string object, its access is
 * very cheap even for long strings.
 * 
 * @author hummelb
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: EDFCF2E6A9B8FDC3B4A6EFEFA49F4B10
 */
public class FastStringComparator implements Comparator<String> {

	/** Singleton instance. */
	public static final FastStringComparator INSTANCE = new FastStringComparator();

	/** {@inheritDoc} */
	@Override
	public int compare(String s0, String s1) {
		if (s0 == s1) {
			return 0;
		}
		int hash0 = s0.hashCode();
		int hash1 = s1.hashCode();
		if (hash0 < hash1) {
			return -1;
		}
		if (hash0 > hash1) {
			return 1;
		}

		return s0.compareTo(s1);
	}
}