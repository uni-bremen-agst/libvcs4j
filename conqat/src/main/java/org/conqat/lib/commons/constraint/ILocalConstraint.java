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
package org.conqat.lib.commons.constraint;

/**
 * Interface of a constraint checking a local property, i.e. one that can be
 * verified by only seeing one object. A constraint usually should be stateless.
 * 
 * @param <T>
 *            the type of object being checked by the constraint.
 * 
 * @author hummelb
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 14C0AEFE3DB4D4034FA252A5271999FB
 */
public interface ILocalConstraint<T> {

	/**
	 * Checks this constraint and throws an exception only if the constraint is
	 * violated for the object.
	 * 
	 * @param object
	 *            the object to be checked by this constraint.
	 * 
	 * @throws ConstraintViolationException
	 *             if the constraint was violated.
	 */
	void checkLocalConstraint(T object) throws ConstraintViolationException;
}