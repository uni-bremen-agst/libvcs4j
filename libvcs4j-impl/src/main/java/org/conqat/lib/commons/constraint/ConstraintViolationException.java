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
package org.conqat.lib.commons.constraint;

/**
 * Exception to be thrown in case of constraint violations.
 * 
 * @author hummelb
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 2C5CF8102A74D904B0897F95EF044626
 */
public class ConstraintViolationException extends Exception {

	/** Version used for serialization. */
	private static final long serialVersionUID = 1;

	/** The object that violated the constraint. */
	private final Object violator;

	/** Constructor. */
	public ConstraintViolationException(String message, Object violator) {
		super(message);
		this.violator = violator;
	}

	/** Constructor. */
	public ConstraintViolationException(String message, Object violator,
			Throwable t) {
		super(message, t);
		this.violator = violator;
	}

	/** Get object that violated the constraint. */
	public Object getViolator() {
		return violator;
	}
}