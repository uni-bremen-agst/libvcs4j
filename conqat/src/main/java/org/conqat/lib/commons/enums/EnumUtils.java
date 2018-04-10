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
package org.conqat.lib.commons.enums;

import org.conqat.lib.commons.assertion.CCSMAssert;

/**
 * Utility class for enumerations.
 * 
 * @author Florian Deissenboeck
 * @author $Author: kinnen $
 * 
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 3204A6BC5C4A0FA4AAAA40B651AA201B
 */
public class EnumUtils {

	/**
	 * This works like {@link Enum#valueOf(java.lang.Class, java.lang.String)}
	 * but returns <code>null</code> if constant wasn't found instead of
	 * throwing an <code>IllegalArgumentException</code>.
	 * 
	 * @param enumType
	 *            Enumeration class
	 * @param constantName
	 *            name of the constant
	 * @return the matching constant or <code>null</code> if not found
	 */
	public static <T extends Enum<T>> T valueOf(Class<T> enumType,
			String constantName) {
		try {
			T constant = Enum.valueOf(enumType, constantName);
			return constant;
		} catch (IllegalArgumentException ex) {
			return null;
		}
	}

	/**
	 * Works like {@link #valueOf(Class, String)} but ignores case.
	 * 
	 * Worst case runtime is O('number of constants in enum').
	 */
	public static <T extends Enum<T>> T valueOfIgnoreCase(Class<T> enumType,
			String constantName) {
		T[] constants = enumType.getEnumConstants();
		CCSMAssert.isNotNull(constants);
		for (T constant : constants) {
			if (constant.name().equalsIgnoreCase(constantName)) {
				return constant;
			}
		}
		return null;
	}

	/**
	 * Returns an array containing the names of the enum element. Ordering is
	 * same as element ordering in enum.
	 */
	public static <T extends Enum<T>> String[] stringValues(Class<T> enumType) {
		T[] constants = enumType.getEnumConstants();
		CCSMAssert.isNotNull(constants);
		String[] result = new String[constants.length];
		for (int i = 0; i < constants.length; i++) {
			result[i] = constants[i].name();
		}
		return result;
	}
}