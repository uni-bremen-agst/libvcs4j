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

import org.conqat.lib.commons.collections.PairList;
import org.conqat.lib.commons.error.IExceptionHandler;
import org.conqat.lib.commons.error.RethrowingExceptionHandler;
import org.conqat.lib.commons.visitor.IMeshWalker;
import org.conqat.lib.commons.visitor.ITreeWalker;
import org.conqat.lib.commons.visitor.IVisitor;
import org.conqat.lib.commons.visitor.VisitorUtils;

/**
 * A class for storing constraints in the context of classes for which the
 * constraint applies. Additionally it provides methods for checking all
 * matching constraints for a given class.
 * 
 * @author hummelb
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: EC66FED7EF1025607E7387F1B637DF11
 */
public class ConstraintValidator {

	/** Storage for constraints in conjunction with the class they apply to. */
	private final PairList<Class<?>, ILocalConstraint<?>> localConstraints = new PairList<Class<?>, ILocalConstraint<?>>();

	/** Adds a constraint for a class. */
	public <T> void addConstraint(Class<? extends T> clazz,
			ILocalConstraint<T> constraint) {
		localConstraints.add(clazz, constraint);
	}

	/**
	 * Checks all constraints to the given object which are applicable to it.
	 * 
	 * @throws ConstraintViolationException
	 *             if any constraint is violated
	 */
	public void checkConstaints(Object o) throws ConstraintViolationException {
		checkConstaints(o,
				RethrowingExceptionHandler
						.<ConstraintViolationException> getInstance());
	}

	/**
	 * Checks all constraints to the given object which are applicable to it. If
	 * a constraint is violated, the thrown exception is handled by the given
	 * provider.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <X extends Exception> void checkConstaints(Object o,
			IExceptionHandler<ConstraintViolationException, X> handler)
			throws X {
		Class<?> clazz = o.getClass();
		for (int i = 0; i < localConstraints.size(); ++i) {
			if (localConstraints.getFirst(i).isAssignableFrom(clazz)) {
				ILocalConstraint<?> constraint = localConstraints.getSecond(i);
				try {
					((ILocalConstraint) constraint).checkLocalConstraint(o);
				} catch (ConstraintViolationException e) {
					handler.handleException(e);
				}
			}
		}
	}

	/**
	 * Validates all nodes of a tree. The first violation found is propagated to
	 * the top using a {@link ConstraintViolationException}.
	 * 
	 * @param root
	 *            the root of the tree.
	 * @param walker
	 *            the walker used to navigate the tree.
	 * @throws ConstraintViolationException
	 *             if a constraint violation was found.
	 * @throws X_WALKER
	 *             if the walker throws an exception.
	 */
	public <T, X_WALKER extends Exception> void validateTree(T root,
			ITreeWalker<T, X_WALKER> walker)
			throws ConstraintViolationException, X_WALKER {

		validateTree(root, walker,
				RethrowingExceptionHandler
						.<ConstraintViolationException> getInstance());
	}

	/**
	 * Validates all nodes of a tree.
	 * 
	 * @param root
	 *            the root of the tree.
	 * @param walker
	 *            the walker used to navigate the tree.
	 * @param handler
	 *            the exception handler used for dealing with constraint
	 *            violations.
	 * @throws X
	 *             if the constraint violation handler throws it.
	 * @throws X_WALKER
	 *             if the walker throws an exception.
	 */
	public <T, X extends Exception, X_WALKER extends Exception> void validateTree(
			T root, ITreeWalker<T, X_WALKER> walker,
			IExceptionHandler<ConstraintViolationException, X> handler)
			throws X, X_WALKER {

		VisitorUtils.visitAllPreOrder(root, walker, new CheckVisitor<T, X>(
				handler));
	}

	/**
	 * Validates all reachable elements of a mesh. The first violation found is
	 * propagated to the top using a {@link ConstraintViolationException}.
	 * 
	 * @param start
	 *            the start element of the mesh.
	 * @param walker
	 *            the walker used to navigate the mesh.
	 * @throws ConstraintViolationException
	 *             if a constraint violation was found.
	 * @throws X_WALKER
	 *             if the walker throws an exception.
	 */
	public <T, X_WALKER extends Exception> void validateMesh(T start,
			IMeshWalker<T, X_WALKER> walker)
			throws ConstraintViolationException, X_WALKER {

		validateMesh(start, walker,
				RethrowingExceptionHandler
						.<ConstraintViolationException> getInstance());
	}

	/**
	 * Validates all reachable elements of a mesh.
	 * 
	 * @param start
	 *            the start element of the mesh.
	 * @param walker
	 *            the walker used to navigate the mesh.
	 * @param handler
	 *            the exception handler used for dealing with constraint
	 *            violations.
	 * @throws X
	 *             if the constraint violation handler throws it.
	 * @throws X_WALKER
	 *             if the walker throws an exception.
	 */
	public <T, X extends Exception, X_WALKER extends Exception> void validateMesh(
			T start, IMeshWalker<T, X_WALKER> walker,
			IExceptionHandler<ConstraintViolationException, X> handler)
			throws X, X_WALKER {

		VisitorUtils.visitAllDepthFirst(start, walker, new CheckVisitor<T, X>(
				handler));
	}

	/**
	 * A simple visitor checking each element with the
	 * {@link ConstraintValidator#checkConstaints(Object, IExceptionHandler)}
	 * method.
	 */
	private class CheckVisitor<T, X extends Exception> implements
			IVisitor<T, X> {

		/** The handler used. */
		private final IExceptionHandler<ConstraintViolationException, X> handler;

		/** Constructor. */
		public CheckVisitor(
				IExceptionHandler<ConstraintViolationException, X> handler) {
			this.handler = handler;
		}

		/** {@inheritDoc} */
		@Override
		public void visit(T element) throws X {
			checkConstaints(element, handler);
		}

	}
}