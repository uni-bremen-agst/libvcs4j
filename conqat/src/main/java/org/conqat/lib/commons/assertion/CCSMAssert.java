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
package org.conqat.lib.commons.assertion;

/**
 * This class provides simple methods to implement assertions. Please refer to
 * the {@linkplain org.conqat.lib.commons.assertion package documentation} for a
 * discussion of assertions vs preconditions.
 * 
 * @author deissenb
 * @author $Author: goede $
 * @version $Rev: 41976 $
 * @ConQAT.Rating GREEN Hash: 9B0A6CCD640EADB6E9D5A27532D5C414
 */
public class CCSMAssert {

	/**
	 * Checks if a condition is <code>true</code>.
	 * 
	 * @param condition
	 *            condition to check
	 * @param message
	 *            exception message
	 * @throws AssertionError
	 *             if the condition if <code>false</code>
	 */
	public static void isTrue(boolean condition, String message)
			throws AssertionError {
		if (!condition) {
			throw new AssertionError(message);
		}
	}

	/**
	 * Checks if a condition is <code>false</code>.
	 * 
	 * @param condition
	 *            condition to check
	 * @param message
	 *            exception message
	 * @throws AssertionError
	 *             if the condition if <code>true</code>
	 */
	public static void isFalse(boolean condition, String message)
			throws AssertionError {
		if (condition) {
			throw new AssertionError(message);
		}
	}

	/** Checks that the object is a instance of the class */
	public static void isInstanceOf(Object object, Class<?> clazz) {
		CCSMAssert.isTrue(clazz.isInstance(object), object
				+ " must be instance of " + clazz);
	}

	/**
	 * This calls {@link #isInstanceOf(Object, Class)} and, if this doesn't fail
	 * returns the casted object.
	 */
	@SuppressWarnings("unchecked")
	public static <T> T checkedCast(Object object, Class<T> clazz) {
		isInstanceOf(object, clazz);
		return (T) object;
	}

	/**
	 * @throws AssertionError
	 *             with message
	 */
	public static void fail(String message) throws AssertionError {
		throw new AssertionError(message);
	}

	/**
	 * Checks whether a reference is <code>null</code>.
	 * 
	 * @param reference
	 *            reference to check
	 * @throws AssertionError
	 *             if the reference is <code>null</code>
	 */
	public static void isNotNull(Object reference) throws AssertionError {
		isNotNull(reference, "Reference must not be null");
	}

	/**
	 * Checks whether a reference is <code>null</code>.
	 * 
	 * @param reference
	 *            reference to check
	 * @param message
	 *            exception message
	 * @throws AssertionError
	 *             if the reference is <code>null</code>
	 */
	public static void isNotNull(Object reference, String message)
			throws AssertionError {
		if (reference == null) {
			throw new AssertionError(message);
		}
	}

}