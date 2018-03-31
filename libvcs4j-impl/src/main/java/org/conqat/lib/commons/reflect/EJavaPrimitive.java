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
package org.conqat.lib.commons.reflect;

import org.conqat.lib.commons.assertion.CCSMPre;
import org.conqat.lib.commons.enums.EnumUtils;

/**
 * Enumeration of Java primitives.
 * 
 * @author deissenb
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: B6F3E3BF553614F037D41D71E5295463
 */
public enum EJavaPrimitive {

	/** void */
	VOID(void.class, Void.class),

	/** byte */
	BYTE(byte.class, Byte.class),

	/** char */
	CHAR(char.class, Character.class),

	/** double */
	DOUBLE(double.class, Double.class),

	/** float */
	FLOAT(float.class, Float.class),

	/** int */
	INT(int.class, Integer.class),

	/** long */
	LONG(long.class, Long.class),

	/** short */
	SHORT(short.class, Short.class),

	/** boolean */
	BOOLEAN(boolean.class, Boolean.class);

	/** Class object of the primitive. */
	private final Class<?> primitiveClass;

	/** Class object of the wrapper type primitive. */
	private final Class<?> wrapperClass;

	/** Create new primitive. */
	private EJavaPrimitive(Class<?> primitiveClass, Class<?> wrapperClass) {
		CCSMPre.isTrue(primitiveClass.isPrimitive(),
				"Clazz object must be a primitive.");
		this.primitiveClass = primitiveClass;
		this.wrapperClass = wrapperClass;
	}

	/** Get the class object of the primitive. */
	public Class<?> getClassObject() {
		return primitiveClass;
	}

	/** Returns the wrapper class for the primitive. */
	public Class<?> getWrapperClass() {
		return wrapperClass;
	}

	/**
	 * Get primitive by name.
	 * 
	 * @return primitive or <code>null</code> if unknown primitive was
	 *         requested
	 */
	public static EJavaPrimitive getPrimitive(String name) {
		return EnumUtils.valueOf(EJavaPrimitive.class, name);
	}

	/**
	 * Get primitive by name ignoring case.
	 * 
	 * @return primitive or <code>null</code> if unknown primitive was
	 *         requested
	 */
	public static EJavaPrimitive getPrimitiveIgnoreCase(String name) {
		return EnumUtils.valueOfIgnoreCase(EJavaPrimitive.class, name);
	}

	/**
	 * Returns the enum literal belonging to the given primitive class (or
	 * null).
	 */
	public static EJavaPrimitive getForPrimitiveClass(Class<?> clazz) {
		for (EJavaPrimitive javaPrimitive : values()) {
			if (javaPrimitive.primitiveClass.equals(clazz)) {
				return javaPrimitive;
			}
		}
		return null;
	}

	/** Returns the enum literal belonging to the given wrapper class (or null). */
	public static EJavaPrimitive getForWrapperClass(Class<?> clazz) {
		for (EJavaPrimitive javaPrimitive : values()) {
			if (javaPrimitive.wrapperClass.equals(clazz)) {
				return javaPrimitive;
			}
		}
		return null;
	}

	/**
	 * Returns the enum literal belonging to the given primitive or wrapper
	 * class (or null).
	 */
	public static EJavaPrimitive getForPrimitiveOrWrapperClass(Class<?> clazz) {
		for (EJavaPrimitive javaPrimitive : values()) {
			if (javaPrimitive.primitiveClass.equals(clazz)
					|| javaPrimitive.wrapperClass.equals(clazz)) {
				return javaPrimitive;
			}
		}
		return null;
	}

	/** Returns whether the given class is a wrapper type for a primitive. */
	public static boolean isWrapperType(Class<?> clazz) {
		return getForWrapperClass(clazz) != null;
	}
}