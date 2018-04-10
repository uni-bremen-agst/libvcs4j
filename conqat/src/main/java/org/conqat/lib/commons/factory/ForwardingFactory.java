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
package org.conqat.lib.commons.factory;

import org.conqat.lib.commons.error.NeverThrownRuntimeException;

/**
 * A factory that forwards creation to its parameter.
 * 
 * @param <T>
 *            Type that gets created by the factory.
 * @param <X>
 *            Exception that can get thrown during execution of the factory
 *            method. If no exception is thrown, use
 *            {@link NeverThrownRuntimeException}.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 6799E40C67E737544CE5870734528FC3
 */
public class ForwardingFactory<T, X extends Exception> implements
		IParameterizedFactory<T, IFactory<T, X>, X> {

	/** Singleton instance. */
	@SuppressWarnings("rawtypes")
	public static final ForwardingFactory INSTANCE = new ForwardingFactory();

	/** {@inheritDoc} */
	@Override
	public T create(IFactory<T, X> factory) throws X {
		return factory.create();
	}
}