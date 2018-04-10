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
 * Exception types to detail {@link XMLWriterException}s.
 * 
 * @author Florian Deissenboeck
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 5849A4A67D1E279654D57AFFA4C36FF4
 */
public enum EXMLWriterExceptionType {

	/** On attempt to close wrong element. */
	UNCLOSED_ELEMENT,

	/** On attempt to create an attribute outside an element head. */
	ATTRIBUTE_OUTSIDE_ELEMENT,

	/** On attempt to create a duplicate attribute. */
	DUPLICATE_ATTRIBUTE,

	/** On attempt to add an XML header in the middle of a document. */
	HEADER_WITHIN_DOCUMENT,

	/** The number of arguments provided is odd. */
	ODD_NUMBER_OF_ARGUMENTS,

	/** The attributes provided are of the wrong type. */
	ILLEGAL_ATTRIBUTE_TYPE,

	/** Text added to a CDATA section contains CDATA closing tag <code>]]></code> */
	CDATA_CONTAINS_CDATA_CLOSING_TAG
}