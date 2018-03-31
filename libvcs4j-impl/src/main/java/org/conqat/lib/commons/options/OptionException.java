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
package org.conqat.lib.commons.options;

/**
 * Base class for exceptions occurring while parsing options.
 * 
 * @author $Author: goeb $
 * @version $Rev: 48475 $
 * @ConQAT.Rating GREEN Hash: BE8E9A9AB05F3C6D707CDF3B6F9A8E22
 */
public class OptionException extends Exception {

	/** Version used for serialization. */
	private static final long serialVersionUID = 1;

	/**
	 * Create new exception.
	 */
	/* package */OptionException(String message) {
		super(message);
	}

	/**
	 * Create new exception.
	 */
	/* package */OptionException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Create new exception.
	 */
	/* package */OptionException(Throwable cause) {
		super(cause.getMessage(), cause);
	}
}