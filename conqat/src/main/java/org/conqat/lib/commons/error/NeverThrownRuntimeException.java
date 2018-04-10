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
 * A runtime exception that cannot be instantiated. Sometimes the typing
 * mechanism forces one to define an exception type although no exceptions are
 * actually thrown. This class may be used to clarify such circumstances.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 93F2AEA82117EA98FF215EFAEDEF8037
 */
public class NeverThrownRuntimeException extends RuntimeException {

	/** Version used for serialization. */
	private static final long serialVersionUID = 1;

	/** Prevents instantiation. */
	private NeverThrownRuntimeException() {
		// Prevents instantiation.
	}
}