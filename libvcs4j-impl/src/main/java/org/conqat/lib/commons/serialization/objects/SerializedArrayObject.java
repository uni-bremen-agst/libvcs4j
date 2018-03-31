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
package org.conqat.lib.commons.serialization.objects;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectStreamConstants;
import java.util.ArrayList;
import java.util.List;

import org.conqat.lib.commons.serialization.SerializedEntityParser;
import org.conqat.lib.commons.serialization.SerializedEntityPool;
import org.conqat.lib.commons.serialization.SerializedEntitySerializer;
import org.conqat.lib.commons.serialization.classes.SerializedArrayField;
import org.conqat.lib.commons.serialization.classes.SerializedClass;
import org.conqat.lib.commons.serialization.classes.SerializedFieldBase;
import org.conqat.lib.commons.serialization.classes.SerializedObjectField;
import org.conqat.lib.commons.serialization.classes.SerializedPrimitiveFieldBase;

/**
 * An array in a serialized stream.
 * 
 * @author $Author: poehlmann $
 * @version $Rev: 48715 $
 * @ConQAT.Rating GREEN Hash: DC85D62E18B39AB3D1A64A7A148D8380
 */
public class SerializedArrayObject extends SerializedObjectBase {

	/** The values in the array. */
	private final List<Object> values = new ArrayList<>();

	/** The type of the elements use for reading/writing them. */
	private final SerializedFieldBase elementType;

	/** Constructor. */
	public SerializedArrayObject(DataInputStream din,
			SerializedEntityPool pool, SerializedEntityParser parser,
			int classHandle) throws IOException {
		super(pool, classHandle);

		String className = pool.getEntity(classHandle, SerializedClass.class)
				.getName();
		if (className.length() == 2
				&& className.charAt(0) == SerializedArrayField.TYPE_CODE) {
			elementType = SerializedPrimitiveFieldBase.fromTypeCode(
					className.charAt(1), null);
		} else {
			elementType = new SerializedObjectField();
		}

		int size = din.readInt();
		for (int i = 0; i < size; ++i) {
			values.add(elementType.readValue(din, parser));
		}
	}

	/** {@inheritDoc} */
	@Override
	protected byte getObjectTagConstant() {
		return ObjectStreamConstants.TC_ARRAY;
	}

	/** {@inheritDoc} */
	@Override
	protected void serializeObjectContent(DataOutputStream dos,
			SerializedEntitySerializer serializer) throws IOException {
		dos.writeInt(values.size());
		for (Object value : values) {
			elementType.writeValue(value, pool, dos, serializer);
		}
	}
}
