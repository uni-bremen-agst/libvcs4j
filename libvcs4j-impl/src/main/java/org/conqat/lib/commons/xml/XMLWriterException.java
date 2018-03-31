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
package org.conqat.lib.commons.xml;

/**
 * XMLWriterExceptions are runtime exceptions thrown by the {@link XMLWriter}.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 667FA9577F33C563028FA4C59815719F
 */
public class XMLWriterException extends RuntimeException {

	/** Version used for serialization. */
	private static final long serialVersionUID = 1;

	/** Exception type. */
	private final EXMLWriterExceptionType type;

	/** Constructor. */
	/* package */XMLWriterException(String message, EXMLWriterExceptionType type) {
		super(message);
		this.type = type;
	}

	/** Get exception type. */
	public EXMLWriterExceptionType getType() {
		return type;
	}
}