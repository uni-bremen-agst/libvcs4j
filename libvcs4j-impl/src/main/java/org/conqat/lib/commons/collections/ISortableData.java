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

/**
 * Abstraction for sortable/comparable data. Implementations of this interface
 * can be used with {@link SortableDataUtils} to supports basic algorithms, such
 * as sorting and binary search on any data which can be mapped to a random
 * access list. The main benefit of this interface is that the type of data is
 * operated on must not be known (or be a concrete type), thus it can also be
 * used to sort data spread over multiple lists or arrays.
 * 
 * @author hummelb
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 8EEF3668086F65A8F9A0A4F3A2C9530D
 */
public interface ISortableData {

	/** Returns the number of elements. */
	int size();

	/**
	 * Returns whether the element stored at index <code>i</code> is smaller
	 * than the one stored at <code>j</code>.
	 */
	boolean isLess(int i, int j);

	/** Swaps the elements at the given indices. */
	void swap(int i, int j);
}