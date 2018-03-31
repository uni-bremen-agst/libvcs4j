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
package org.conqat.lib.commons.serialization.classes;

import java.io.IOException;

import org.conqat.lib.commons.serialization.SerializedEntityParser;

/**
 * Field for arbitrary object.
 * 
 * @author $Author: heinemann $
 * @version $Rev: 50916 $
 * @ConQAT.Rating GREEN Hash: DAE97EB8F8ED0A4C0CDAFDC2B7283ED9
 */
public class SerializedObjectField extends SerializedComplexFieldBase {

	/** The type code for this kind of field. */
	public static final char TYPE_CODE = 'L';

	/**
	 * Constructor for creating a non-functional field that is only used for
	 * read/write.
	 */
	public SerializedObjectField() throws IOException {
		super(null, (SerializedEntityParser) null);
	}

	/** Constructor. */
	public SerializedObjectField(String fieldName, SerializedEntityParser parser)
			throws IOException {
		super(fieldName, parser);
	}

	/**
	 * Constructor.
	 * 
	 * @param jvmTypeName
	 *            the fully qualified type in JVM internal notation.
	 */
	public SerializedObjectField(String fieldName, String jvmTypeName) {
		super(fieldName, jvmTypeName);
	}

	/**
	 * Converts a plain class name (dot separated) to JVM notation, such as
	 * "Ljava/lang/String;".
	 */
	public static String createJvmNotationFromPlainClassName(
			String fullyQualifiedType) {
		return TYPE_CODE + fullyQualifiedType.replace('.', '/') + ";";
	}

	/** {@inheritDoc} */
	@Override
	protected char getTypeCode() {
		return TYPE_CODE;
	}
}
