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
package org.conqat.lib.commons.concurrent;

import org.conqat.lib.commons.assertion.CCSMAssert;

/**
 * A {@link Runnable} that provides a result that can be read asynchronously and
 * may also throw an exception.
 * 
 * @param <T>
 *            the return type.
 * @param <X>
 *            the exception type.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 9923E4315470FF21C089551360260E35
 */
public abstract class RunnableWithAsyncResultBase<T, X extends Throwable>
		implements Runnable {

	/** The result. */
	private T result;

	/** The caught exception. */
	private Throwable caughtException = null;

	/** Flag storing whether execution has finished (and the result is ready). */
	private boolean finished = false;

	/** {@inheritDoc} */
	@Override
	public final void run() {
		try {
			result = runWithResult();
		} catch (Throwable e) {
			caughtException = e;
		}
		finished = true;
	}

	/** Template method for calculating the result. */
	public abstract T runWithResult() throws X;

	/** Returns whether the calculation has been finished. */
	public boolean isFinished() {
		return finished;
	}

	/**
	 * Returns the result. If the execution threw an exception, this thrown
	 * here. This applies to checked exceptions of type X as well as to runtime
	 * exceptions and errors. If the {@link #run()} method did not terminate
	 * yet, an {@link AssertionError} is thrown.
	 */
	@SuppressWarnings("unchecked")
	public T getResult() throws X {
		CCSMAssert.isTrue(finished,
				"Can not query result before run() is finished!");

		if (caughtException instanceof RuntimeException) {
			throw (RuntimeException) caughtException;
		} else if (caughtException instanceof Error) {
			throw (Error) caughtException;
		} else if (caughtException != null) {
			// must be an X in this case
			throw (X) caughtException;
		}

		return result;
	}
}
