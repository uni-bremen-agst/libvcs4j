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
package org.conqat.lib.commons.reflect;

/**
 * This exceptions signals type conversion problems.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 3884C59BB8874CF80AE8DBAF92BACAC2
 */
public class TypeConversionException extends Exception {

	/** Version used for serialization. */
	private static final long serialVersionUID = 1;

	/** Create new exception. */
	public TypeConversionException(String message) {
		super(message);
	}

	/** Create new exception. */
	public TypeConversionException(String message, Throwable cause) {
		super(message, cause);
	}
}