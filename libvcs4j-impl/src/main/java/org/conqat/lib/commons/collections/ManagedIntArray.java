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
 * A class containing an int array that is managed in the sense that it can grow
 * dynamically (using exponential growth). It is useful for cases where an
 * ArrayList<Integer> seems like overkill due to the high memory footprint of
 * the Integer objects and the amount of work performed for auto (un)boxing.
 * Note however, that a subclass has full access to the internals and thus might
 * cause chaos.
 * 
 * @author hummelb
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 001297A6A9CB6B5C5E53188ECCF69710
 */
public class ManagedIntArray {

	/** The current size of the array. */
	protected int size = 0;

	/** The actual array. */
	protected int[] array = new int[8];

	/** Add space for a single element to the end of the array. */
	protected void addArrayElement() {
		addArrayElements(1);
	}

	/** Add space for multiple elements to the end of the array. */
	protected void addArrayElements(int count) {
		if (size + count >= array.length) {
			int newSize = 2 * array.length;
			while (newSize <= size + count) {
				newSize *= 2;
			}

			int[] oldArray = array;
			array = new int[newSize];
			System.arraycopy(oldArray, 0, array, 0, size);
		}
		size += count;
	}
}