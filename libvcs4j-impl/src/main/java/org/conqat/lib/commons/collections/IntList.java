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
 * A really simple list for storing ints. This exists as it is both more
 * efficient and uses way less memory than a List of Integers.
 * 
 * @author hummelb
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 19BFED21825867CF4C57A1E9FAF4DFC6
 */
public class IntList extends ManagedIntArray {

	/** Returns the size of the list. */
	public int getSize() {
		return size;
	}

	/**
	 * Returns the element at the given index. No range checking is performed,
	 * thus you might sometimes get an {@link ArrayIndexOutOfBoundsException},
	 * and sometimes just 0 returned, depending on if you reached existing
	 * memory by chance.
	 */
	public int get(int index) {
		return array[index];
	}

	/**
	 * Set the element at the given index. No range checking is performed, thus
	 * you might sometimes get an {@link ArrayIndexOutOfBoundsException}, and
	 * sometimes not for illegal indexes, depending on whether you hit memory
	 * allocated by the exponential growth strategy by chance.
	 */
	public void set(int index, int value) {
		array[index] = value;
	}

	/** Adds an element to the end of the list. */
	public void add(int value) {
		int index = size;
		addArrayElement();
		array[index] = value;
	}
}