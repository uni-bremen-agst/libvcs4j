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
 * Field for arrays.
 * 
 * @author $Author: poehlmann $
 * @version $Rev: 48728 $
 * @ConQAT.Rating GREEN Hash: CF67C633D03943FC4A1A919CF31A3E15
 */
public class SerializedArrayField extends SerializedComplexFieldBase {

	/** The type code for this kind of field. */
	public static final char TYPE_CODE = '[';

	/** Constructor. */
	public SerializedArrayField(String fieldName, SerializedEntityParser parser)
			throws IOException {
		super(fieldName, parser);
	}

	/** {@inheritDoc} */
	@Override
	protected char getTypeCode() {
		return TYPE_CODE;
	}
}
