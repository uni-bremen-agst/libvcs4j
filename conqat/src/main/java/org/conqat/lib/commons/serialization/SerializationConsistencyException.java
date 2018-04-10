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
package org.conqat.lib.commons.serialization;

import java.io.IOException;

/**
 * Exception used for signaling inconsistencies in the object hierarchy to be
 * serialized.
 * 
 * @author $Author: poehlmann $
 * @version $Rev: 47519 $
 * @ConQAT.Rating GREEN Hash: 6AB391AA5EAE3308AA2895904614299F
 */
public class SerializationConsistencyException extends IOException {

	/** Version for serialization. */
	private static final long serialVersionUID = 1L;

	/** Constructor. */
	public SerializationConsistencyException(String message) {
		super(message);
	}

}
