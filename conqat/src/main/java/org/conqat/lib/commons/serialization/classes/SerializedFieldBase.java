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
 * A field of a {@link SerializedClass}.
 * 
 * @author $Author: poehlmann $
 * @version $Rev: 47519 $
 * @ConQAT.Rating GREEN Hash: 3ADED833D9EA345932A4759187BF6F52
 */
public abstract class SerializedFieldBase {

	/** The name of the field. */
	private String name;

	/** Constructor. */
	protected SerializedFieldBase(String name) {
		this.name = name;
	}

	/** Returns the name. */
	public String getName() {
		return name;
	}

	/** Sets the name. */
	public void setName(String name) {
		this.name = name;
	}

	/** Reads the value for a field from the given stream. */
	public abstract Object readValue(DataInputStream din,
			SerializedEntityParser parser) throws IOException;

	/**
	 * Writes the given field value to the output stream using the correct
	 * format for this field.
	 */
	public abstract void writeValue(Object value, SerializedEntityPool pool,
			DataOutputStream dos, SerializedEntitySerializer serializer)
			throws IOException;

	/**
	 * Serializes this field.
	 * 
	 * @param serializer
	 *            the serializer is used by sub classes.
	 */
	public void serialize(DataOutputStream dos,
			SerializedEntitySerializer serializer) throws IOException {
		dos.writeByte(getTypeCode());
		dos.writeUTF(name);
	}

	/** Returns the type code for the king of field. */
	protected abstract char getTypeCode();
}
