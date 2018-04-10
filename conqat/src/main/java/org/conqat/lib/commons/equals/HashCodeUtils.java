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
package org.conqat.lib.commons.equals;

import java.util.Objects;

/**
 * This class provides utility methods on hash codes, usually used when
 * overriding {@link Object#hashCode()}.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 51771 $
 * @ConQAT.Rating RED Hash: 25E27C3089E2D79530406273072C4693
 */
public abstract class HashCodeUtils {

	/** The prime number used when calculating hash codes. */
	private static final int PRIME = 31;

	/**
	 * Generates a hash code for part of an array. The hash code is calculated
	 * from the hash codes of the given objects. Only objects that lie within
	 * the given start and end index in the array are considered.
	 * 
	 * This method offers a better performance than creating a copy of the array
	 * that only contains the desired elements and then calling
	 * {@link Objects#hash(Object...)}.
	 */
	public static int hashArrayPart(Object[] objects, int startInclusive,
			int endExclusive) {
		int hashCode = 1;
		for (int i = startInclusive; i < endExclusive; i++) {
			// TODO (BH): As far as I understand the JavaDoc, the Objects.hash()
			// should not be used for single objects. I will work, but creates
			// unnecessary objects. Why not directly call objects[i].hashCode()?
			// Has to handle the null case of course.
			hashCode = hashCode * PRIME + Objects.hash(objects[i]);
		}
		return hashCode;
	}

}
