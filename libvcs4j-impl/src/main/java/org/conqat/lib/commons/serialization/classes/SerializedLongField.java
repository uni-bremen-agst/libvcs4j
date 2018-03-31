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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.conqat.lib.commons.serialization.SerializedEntityParser;
import org.conqat.lib.commons.serialization.SerializedEntityPool;
import org.conqat.lib.commons.serialization.SerializedEntitySerializer;

/**
 * Field for long.
 * 
 * @author $Author: poehlmann $
 * @version $Rev: 48750 $
 * @ConQAT.Rating GREEN Hash: C0398E8C4229F8C8DCA73FFD09CF8432
 */
public class SerializedLongField extends SerializedPrimitiveFieldBase {

	/** The type code. */
	public static final char TYPE_CODE = 'J';

	/** Constructor. */
	public SerializedLongField(String name) {
		super(name);
	}

	/** {@inheritDoc} */
	@Override
	public Object readValue(DataInputStream din, SerializedEntityParser parser)
			throws IOException {
		return din.readLong();
	}

	/** {@inheritDoc} */
	@Override
	public void writeValue(Object value, SerializedEntityPool pool,
			DataOutputStream dos, SerializedEntitySerializer serializer)
			throws IOException {
		dos.writeLong(ensureType(value, Long.class));
	}

	/** {@inheritDoc} */
	@Override
	protected char getTypeCode() {
		return TYPE_CODE;
	}
}
