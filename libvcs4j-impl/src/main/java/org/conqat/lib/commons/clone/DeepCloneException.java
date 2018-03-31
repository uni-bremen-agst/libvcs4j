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
package org.conqat.lib.commons.clone;

/**
 * Exception to be thrown when deep cloning fails.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: F6D0AFF5BC41B6C0DAE52B46E00F3F98
 * 
 * @see IDeepCloneable
 */
public class DeepCloneException extends Exception {

	/** Version used for serialization. */
	private static final long serialVersionUID = 1;

	/** Create exception. */
	public DeepCloneException(String message) {
		super(message);
	}

	/** Create exception. */
	public DeepCloneException(String message, Throwable cause) {
		super(message, cause);
	}

	/** Create exception. */
	public DeepCloneException(Throwable cause) {
		super(cause);
	}
}