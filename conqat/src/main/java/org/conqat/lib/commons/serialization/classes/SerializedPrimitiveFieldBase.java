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

import org.conqat.lib.commons.serialization.SerializationConsistencyException;

/**
 * Base class for fields of primitive type.
 * 
 * @author $Author: poehlmann $
 * @version $Rev: 48750 $
 * @ConQAT.Rating GREEN Hash: EBF03224A6BB3DE0637EFBCA7291D181
 */
public abstract class SerializedPrimitiveFieldBase extends SerializedFieldBase {

	/** Constructor. */
	protected SerializedPrimitiveFieldBase(String name) {
		super(name);
	}

	/**
	 * Ensures that the given value object is of the given type and not null.
	 * Returns the casted type.
	 */
	@SuppressWarnings("unchecked")
	protected <T> T ensureType(Object value, Class<T> type)
			throws SerializationConsistencyException {
		if (!type.isInstance(value)) {
			String actualType = null;
			if (value != null) {
				actualType = value.getClass().getName();
			}
			throw new SerializationConsistencyException(
					"Would have expected type " + type + " for field "
							+ getName() + " but was " + actualType);
		}
		return (T) value;
	}

	/** Factory method for creating a primitive field from its type code. */
	public static SerializedPrimitiveFieldBase fromTypeCode(char typeCode,
			String fieldName) throws IOException {
		switch (typeCode) {
		case SerializedByteField.TYPE_CODE:
			return new SerializedByteField(fieldName);
		case SerializedCharField.TYPE_CODE:
			return new SerializedCharField(fieldName);
		case SerializedDoubleField.TYPE_CODE:
			return new SerializedDoubleField(fieldName);
		case SerializedFloatField.TYPE_CODE:
			return new SerializedFloatField(fieldName);
		case SerializedIntField.TYPE_CODE:
			return new SerializedIntField(fieldName);
		case SerializedLongField.TYPE_CODE:
			return new SerializedLongField(fieldName);
		case SerializedShortField.TYPE_CODE:
			return new SerializedShortField(fieldName);
		case SerializedBooleanField.TYPE_CODE:
			return new SerializedBooleanField(fieldName);
		default:
			throw new SerializationConsistencyException(
					"Unrecognized type code: " + typeCode);
		}
	}
}
