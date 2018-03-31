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
 * This exception is used to indicate format errors in provided strings.
 * 
 * @author hummelb
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: CF18C884E0ECF71C699BEA6D11B4E68E
 */
public class FormatException extends Exception {

	/** Version used for serialization. */
	private static final long serialVersionUID = 1;

	/** Constructor. */
	public FormatException(String message) {
		super(message);
	}

	/** Constructor. */
	public FormatException(String message, Throwable cause) {
		super(message, cause);
	}

}