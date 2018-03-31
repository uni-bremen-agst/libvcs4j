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
package org.conqat.lib.commons.options;

import java.util.Comparator;

/**
 * A comparator for ordering options in a way used for usage messages. Sorting
 * is performed on the short name and then on the long name (if present). If the
 * short name is missing, the long name is sorted into the short names.
 * <p>
 * Basically we just concatenate the short and long name and compare the
 * resulting string.
 * 
 * @author Benjamin Hummel
 * @author $Author: kinnen $
 * 
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 16BCBC91E579B23250B27FA38FECA39C
 */
public class AOptionComparator implements Comparator<AOption> {

	/** {@inheritDoc} */
	@Override
	public int compare(AOption o1, AOption o2) {
		return concatenatedName(o1).compareTo(concatenatedName(o2));
	}

	/**
	 * Returns the concatenation of the short and the long name, omitting those
	 * parts missing.
	 * 
	 * @param option
	 *            the option to take the names from.
	 * @return the concatenated names.
	 */
	private static String concatenatedName(AOption option) {
		if (option.shortName() == 0) {
			return option.longName();
		}
		return option.shortName() + option.longName();
	}
}