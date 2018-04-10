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
 * Interface for a tree walker, i.e. a class which allows the traversal of a
 * tree. The implementor has to make sure, that this really is a tree, i.e. for
 * two different node, the children returned must be disjunctive, and traversing
 * the tree may not result in loops.
 * 
 * @param <T>
 *            the type used for the nodes of the tree.
 * @param <X>
 *            the type of exception thrown. Use
 *            {@link NeverThrownRuntimeException} if no exception is thrown
 * 
 * @author hummelb
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 21997CE50A873F6D3FC32D0B5B5DEF54
 */
public interface ITreeWalker<T, X extends Exception> {

	/**
	 * Returns the children of the given tree node. The returned collection may
	 * not contain duplicate entries.
	 */
	public Collection<T> getChildren(T node) throws X;
}