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

import org.conqat.lib.commons.serialization.SerializationConsistencyException;
import org.conqat.lib.commons.serialization.SerializedEntityParser;
import org.conqat.lib.commons.serialization.SerializedEntityPool;
import org.conqat.lib.commons.serialization.SerializedEntitySerializer;
import org.conqat.lib.commons.serialization.objects.SerializedObjectBase;

/**
 * Base class for field of complex type.
 * 
 * @author $Author: heinemann $
 * @version $Rev: 50916 $
 * @ConQAT.Rating GREEN Hash: 3C6987E3654227F3520B8DE301F28D63
 */
public abstract class SerializedComplexFieldBase extends SerializedFieldBase {

	/** The detailed type contains the fully qualified type name. */
	private final String detailedType;

	/**
	 * Constructor.
	 * 
	 * @param parser
	 *            the parser used for reading the {@link #detailedType}. If this
	 *            is null (which can be used to construct artificial fields not
	 *            resulting from a serialized stream), the {@link #detailedType}
	 *            will also be null.
	 */
	protected SerializedComplexFieldBase(String fieldName,
			SerializedEntityParser parser) throws IOException {
		super(fieldName);
		if (parser == null) {
			this.detailedType = null;
		} else {
			this.detailedType = parser.parseStringObject().getValue();
		}
	}

	/** Constructor with explicit type construction. */
	protected SerializedComplexFieldBase(String fieldName, String detailedType) {
		super(fieldName);
		this.detailedType = detailedType;
	}

	/** Returns the detailed type (may be null). */
	public String getDetailedType() {
		return detailedType;
	}

	/** {@inheritDoc} */
	@Override
	public void serialize(DataOutputStream dos,
			SerializedEntitySerializer serializer) throws IOException {
		super.serialize(dos, serializer);
		serializer.serializeStringObject(detailedType);
	}

	/** {@inheritDoc} */
	@Override
	public Object readValue(DataInputStream din, SerializedEntityParser parser)
			throws IOException {
		return parser.parseContent();
	}

	/** {@inheritDoc} */
	@Override
	public void writeValue(Object value, SerializedEntityPool pool,
			DataOutputStream dos, SerializedEntitySerializer serializer)
			throws IOException {
		if (!(value instanceof Integer)) {
			throw new SerializationConsistencyException(
					"Expected a handle as value for field " + getName());
		}

		int handle = (Integer) value;
		SerializedEntitySerializer.serializeObject(handle,
				SerializedObjectBase.class, pool, dos, serializer);
	}
}
