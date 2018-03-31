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
package org.conqat.lib.commons.error;

/**
 * An exception handler simply rethrowing the exception caught.
 * 
 * @author hummelb
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: CAD92A72E09168631A076C2EB31BF6F2
 */
public class RethrowingExceptionHandler<X extends Exception> implements
		IExceptionHandler<X, X> {

	/** Singleton instance. */
	private static RethrowingExceptionHandler<Exception> INSTANCE = new RethrowingExceptionHandler<Exception>();

	/** Returns the singleton instance of this class. */
	@SuppressWarnings("unchecked")
	public static <X extends Exception> RethrowingExceptionHandler<X> getInstance() {
		return (RethrowingExceptionHandler<X>) INSTANCE;
	}

	/** {@inheritDoc} */
	@Override
	public void handleException(X exception) throws X {
		throw exception;
	}
}