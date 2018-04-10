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
package org.conqat.lib.commons.datamining;

import java.util.Set;

/**
 * A frequent item set consists of a set of items and an associated support
 * value between 0..1 indicating in what fraction of all baskets the frequent
 * item set occurs.
 * 
 * @author $Author: heineman $
 * @version $Rev: 41812 $
 * @ConQAT.Rating YELLOW Hash: 05707348BC7859552BB6D2B09C2DA0AA
 */
public class FrequentItemSet<T> {

	/** The items */
	private final Set<T> items;

	/** The confidence */
	private final double support;

	/** Constructor */
	public FrequentItemSet(Set<T> items, double support) {
		this.items = items;
		this.support = support;
	}

	/** Returns the items. */
	public Set<T> getItems() {
		return items;
	}

	/** Returns the support [0..1]. */
	public double getSupport() {
		return support;
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return items.toString() + "(" + support + ")";
	}
}
