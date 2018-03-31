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
package org.conqat.lib.commons.factory;

import org.conqat.lib.commons.error.NeverThrownRuntimeException;

/**
 * Generic factory interface.
 * 
 * @param <T>
 *            Type that gets created by the factory.
 * @param
 *            <P>
 *            Parameter that is used for creation.
 * @param <X>
 *            Exception that can get thrown during execution of the factory
 *            method. If no exception is thrown, use
 *            {@link NeverThrownRuntimeException}.
 * 
 * @author juergens
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: A053182D275EBD37CAFBD99D7EF2F77C
 */
public interface IParameterizedFactory<T, P, X extends Exception> {

	/** Factory method */
	T create(P parameter) throws X;

}