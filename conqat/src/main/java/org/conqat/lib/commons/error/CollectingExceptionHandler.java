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

import java.util.ArrayList;
import java.util.List;

import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.collections.UnmodifiableList;

/**
 * An exception handler that collects exceptions for later use.
 * 
 * @author hummelb
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: AEE4ABF8AE9F5E3471A9192BB7676F6B
 */
public class CollectingExceptionHandler<X extends Exception> implements
		IExceptionHandler<X, NeverThrownRuntimeException> {

	/** The exceptions stored. */
	private final List<X> exceptions = new ArrayList<X>();

	/** {@inheritDoc} */
	@Override
	public void handleException(X exception) throws NeverThrownRuntimeException {
		exceptions.add(exception);
	}

	/** Returns the list of exception caught so far. */
	public UnmodifiableList<X> getExceptions() {
		return CollectionUtils.asUnmodifiable(exceptions);
	}

	/** Clears the list of exceptions. */
	public void clearExceptions() {
		exceptions.clear();
	}
}