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
 * This class provides simple methods to check preconditions. Please see refer
 * to the {@linkplain org.conqat.lib.commons.assertion package documentation} for a
 * discussion of assertions vs preconditions.
 * 
 * @author deissenb
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 9B72BE23F4BDB68EB3A4CFD61D38FE06
 */
public class CCSMPre {

	/**
	 * Checks if a condition is <code>true</code>.
	 * 
	 * @param condition
	 *            condition to check
	 * @param message
	 *            exception message
	 * @throws PreconditionException
	 *             if the condition is <code>false</code>
	 */
	public static void isTrue(boolean condition, String message)
			throws PreconditionException {
		if (!condition) {
			throw new PreconditionException(message);
		}
	}

	/**
	 * Checks if a condition is <code>false</code>.
	 * 
	 * @param condition
	 *            condition to check
	 * @param message
	 *            exception message
	 * @throws PreconditionException
	 *             if the condition is <code>true</code>
	 */
	public static void isFalse(boolean condition, String message)
			throws PreconditionException {
		if (condition) {
			throw new PreconditionException(message);
		}
	}

	/** Checks that the object is a instance of the class */
	public static void isInstanceOf(Object object, Class<?> clazz) {
		isTrue(clazz.isInstance(object), object + " must be instance of "
				+ clazz);
	}

	/**
	 * Throws a {@link PreconditionException} with the provided message.
	 */
	public static void fail(String message) throws PreconditionException {
		throw new PreconditionException(message);
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