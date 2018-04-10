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
package org.conqat.lib.commons.collections;

import java.text.Collator;
import java.util.Comparator;

/**
 * This comparator compares objects by their toString() representation.
 * 
 * @author deissenb
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 1F03FF5C9279BFB09B062B5040CB5B98
 */
public class ToStringComparator implements Comparator<Object> {

	/** Instance of this comparator. */
	public static final ToStringComparator INSTANCE = new ToStringComparator();

	/** Collator used for comparing. */
	private final Collator collator;

	/** Create new comparator based on the default locale's collator. */
	public ToStringComparator() {
		collator = Collator.getInstance();
	}

	/** Create new comparator with specific collator. */
	public ToStringComparator(Collator collator) {
		this.collator = collator;
	}

	/** Compare by toString() representation. */
	@Override
	public int compare(Object o1, Object o2) {
		return collator.compare(o1.toString(), o2.toString());
	}

}