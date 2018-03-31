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
 * Interface for a exception handling routine.
 * 
 * @param <X_IN>
 *            the type of exception being handled.
 * @param <X_OUT>
 *            the type of exception being (potentially) thrown. Use
 *            {@link NeverThrownRuntimeException} if no exception will be thrown
 *            by this handler.
 * 
 * @author hummelb
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: B0609193EEF6B48F5D352C06F2DAE508
 */
public interface IExceptionHandler<X_IN extends Exception, X_OUT extends Exception> {

	/**
	 * Handle the provided exception.
	 * 
	 * @param exception
	 *            the exception being handled.
	 */
	void handleException(X_IN exception) throws X_OUT;
}