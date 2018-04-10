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
package org.conqat.lib.commons.visitor;

import java.util.Collection;

import org.conqat.lib.commons.error.NeverThrownRuntimeException;

/**
 * Interface for a mesh walker, i.e. a class which allows the traversal of a
 * general mesh of elements.
 * 
 * @param <T>
 *            the type of the elements of the mesh.
 * @param <X>
 *            the type of exception thrown. Use
 *            {@link NeverThrownRuntimeException} if no exception is thrown
 * 
 * @author hummelb
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: CF05D6B225551705E7C1E80B34B00B9A
 */
public interface IMeshWalker<T, X extends Exception> {

	/**
	 * Returns all elements which are directly reachable from a given element
	 * (and are part of the mesh).
	 */
	public Collection<T> getAdjacentElements(T element) throws X;

}