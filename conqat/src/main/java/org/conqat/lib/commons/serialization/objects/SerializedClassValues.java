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
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamConstants;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.conqat.lib.commons.serialization.SerializedEntityParser;
import org.conqat.lib.commons.serialization.SerializedEntityPool;
import org.conqat.lib.commons.serialization.SerializedEntitySerializer;
import org.conqat.lib.commons.serialization.classes.SerializedClass;
import org.conqat.lib.commons.serialization.classes.SerializedFieldBase;
import org.conqat.lib.commons.serialization.classes.SerializedPrimitiveFieldBase;

/**
 * The values of an object corresponding to a single class, i.e. each object has
 * multiple class values, one for each class in its hierarchy. This can store
 * values for all declared (non-transient) fields of a class as well as
 * additional data. The presence of both the fields and the additional data are
 * optional depending on the serialization mechanism used (default
 * {@link Serializable}, serializable with custom read/write methods,
 * {@link Externalizable}).
 * 
 * The old externalization protocol from Java versions before 1.2 is not
 * supported by this class.
 * 
 * @author $Author: heinemann $
 * @version $Rev: 50916 $
 * @ConQAT.Rating GREEN Hash: EB3D51D5F55BB461AC00695BF8F1149E
 */
public class SerializedClassValues {

	/**
	 * Chunks of raw data (byte arrays) found before the field values. These are
	 * written by custom serialization or externalizable classes. If no fields
	 * are written, data is only put into {@link #preFieldData}. This list may
	 * be null to indicate no data.
	 */
	private List<Object> preFieldData = null;

	/**
	 * The values for the fields of the corresponding class. This may be null if
	 * the fields are not stored (e.g. custom read/write or
	 * {@link Externalizable}).
	 */
	private List<Object> fieldValues = null;

	/**
	 * Chunks of raw data (byte arrays) found after the field values. These are
	 * written by custom serialization or externalizable classes. If no fields
	 * are written, data is only put into {@link #preFieldData}. This list may
	 * be null to indicate no data.
	 */
	private List<Object> postFieldData = null;

	/** Constructor. */
	SerializedClassValues(SerializedClass serializedClass, DataInputStream din,
			SerializedEntityParser parser) throws IOException {
		if (serializedClass.isSerializable()) {
			if (serializedClass.hasWriteMethod()) {
				parseWithData(serializedClass, din, parser, true);
			} else {
				parseFieldValues(serializedClass, din, parser);
			}
		} else if (serializedClass.isExternalizable()) {
			if (!serializedClass.hasBlockData()) {
				throw new IOException(
						"Externalizable with externalContents (old serialization protocol) not supported!");
			}
			parseWithData(serializedClass, din, parser, false);
		} else {
			throw new IOException(
					"Invalid class encountered: neither serializable nor externalizable: "
							+ serializedClass.getName());
		}
	}

	/**
	 * Parses raw data and field values. There seems to be a gap in the
	 * specification for {@link Serializable} classes with custom read/write
	 * methods. If the writeObject() methods calls
	 * {@link ObjectOutputStream#defaultWriteObject()} it seems we have no way
	 * of deciding whether a data block or field values start. The
	 * implementation probably copes with this by calling the readObject()
	 * method and expecting it to work just right. As calling a method is not an
	 * option for us, we use the following heuristic. If the next byte is one of
	 * the block data, string, or object constants, we interpret it as such,
	 * otherwise we assume field data. This heuristic can make errors as the
	 * field data might actually start with on of the mentioned constants. This
	 * is unlikely but possible.
	 * 
	 * @param mayContainFields
	 *            if this is false, only raw data (and no field values) are
	 *            expected.
	 */
	private void parseWithData(SerializedClass serializedClass,
			DataInputStream din, SerializedEntityParser parser,
			boolean mayContainFields) throws IOException {
		boolean isPreFields = true;

		while (true) {
			din.mark(1);
			byte next = din.readByte();

			switch (next) {
			case ObjectStreamConstants.TC_BLOCKDATA:
				int shortBlockLength = din.readUnsignedByte();
				readBlockData(din, shortBlockLength, isPreFields);
				break;
			case ObjectStreamConstants.TC_BLOCKDATALONG:
				int longBlockLength = din.readInt();
				readBlockData(din, longBlockLength, isPreFields);
				break;
			case ObjectStreamConstants.TC_ENDBLOCKDATA:
				return;
			case ObjectStreamConstants.TC_STRING:
			case ObjectStreamConstants.TC_OBJECT:
				din.reset();
				appendData(parser.parseContent(), isPreFields);
				break;
			default:
				// assume fields
				if (!mayContainFields) {
					throw new IOException(
							"No more fields expected at this time!");
				}
				mayContainFields = false;
				isPreFields = false;

				din.reset();
				parseFieldValues(serializedClass, din, parser);
			}
		}
	}

	/**
	 * Reads block data of given length.
	 * 
	 * @param isPreFields
	 *            determines whether to store into {@link #preFieldData} or
	 *            {@link #postFieldData}.
	 */
	private void readBlockData(DataInputStream din, int length,
			boolean isPreFields) throws IOException {
		byte[] data = new byte[length];
		din.readFully(data);
		appendData(data, isPreFields);
	}

	/** Appends data to either {@link #preFieldData} or {@link #postFieldData}. */
	private void appendData(Object data, boolean isPreFields) {
		if (isPreFields) {
			if (preFieldData == null) {
				preFieldData = new ArrayList<>();
			}
			preFieldData.add(data);
		} else {
			if (postFieldData == null) {
				postFieldData = new ArrayList<>();
			}
			postFieldData.add(data);
		}
	}

	/** Reads the values for the fields of the class into {@link #fieldValues}. */
	private void parseFieldValues(SerializedClass serializedClass,
			DataInputStream din, SerializedEntityParser parser)
			throws IOException {
		fieldValues = new ArrayList<>();
		for (SerializedFieldBase field : serializedClass.getFields()) {
			fieldValues.add(field.readValue(din, parser));
		}
	}

	/** Returns the given value. */
	public Object getValue(int index) {
		return fieldValues.get(index);
	}

	/** Sets the given value. */
	public void setValue(int index, Object value) {
		while (index >= fieldValues.size()) {
			fieldValues.add(null);
		}
		fieldValues.set(index, value);
	}

	/** Serializes the class values. */
	public void serialize(SerializedClass serializedClass,
			SerializedEntityPool pool, DataOutputStream dos,
			SerializedEntitySerializer serializer) throws IOException {
		writeRawData(preFieldData, pool, serializer);

		if (fieldValues != null) {
			serializeFieldValues(serializedClass, pool, dos, serializer);
		}

		writeRawData(postFieldData, pool, serializer);

		if (!serializedClass.isSerializable()
				|| serializedClass.hasWriteMethod()) {
			dos.writeByte(ObjectStreamConstants.TC_ENDBLOCKDATA);
		}
	}

	/** Serializes the field values. */
	private void serializeFieldValues(SerializedClass serializedClass,
			SerializedEntityPool pool, DataOutputStream dos,
			SerializedEntitySerializer serializer) throws IOException {
		// during serialization, the primitive types are required to come
		// first, as otherwise we get an exception
		int index = 0;
		for (SerializedFieldBase field : serializedClass.getFields()) {
			if (field instanceof SerializedPrimitiveFieldBase) {
				Object value = fieldValues.get(index);
				field.writeValue(value, pool, dos, serializer);
			}
			index += 1;
		}

		index = 0;
		for (SerializedFieldBase field : serializedClass.getFields()) {
			if (!(field instanceof SerializedPrimitiveFieldBase)) {
				Object value = fieldValues.get(index);
				field.writeValue(value, pool, dos, serializer);
			}
			index += 1;
		}
	}

	/**
	 * Writes the raw data to the stream, which consists of both byte arrays and
	 * handles. The provided list may be null.
	 */
	private static void writeRawData(List<Object> blockData,
			SerializedEntityPool pool, SerializedEntitySerializer serializer)
			throws IOException {
		if (blockData == null) {
			return;
		}

		serializer.serializeAnnotationList(blockData, pool);
	}

	/** Returns whether field data is present. */
	public boolean hasFieldValues() {
		return fieldValues != null;
	}

	/** Returns the pre field data. */
	public List<Object> getPreFieldData() {
		return preFieldData;
	}

	/** Returns the post field data. */
	public List<Object> getPostFieldData() {
		return postFieldData;
	}

}
