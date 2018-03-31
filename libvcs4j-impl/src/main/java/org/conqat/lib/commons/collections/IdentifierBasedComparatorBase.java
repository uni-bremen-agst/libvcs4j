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

import java.util.Comparator;

/**
 * Base class for comparators that compare to objects by comparing a suitable
 * object identifier.
 * 
 * @author deissenb
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 6115B4C5E45367256718B4CE92A47428
 */
public abstract class IdentifierBasedComparatorBase<I extends Comparable<I>, T>
		implements Comparator<T> {

	/** Compare by identifier. */
	@Override
	public int compare(T object1, T object2) {
		return obtainIdentifier(object1).compareTo(obtainIdentifier(object2));
	}

	/** Get identifier for object. */
	abstract protected I obtainIdentifier(T object);
}