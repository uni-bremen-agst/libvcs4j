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
import java.io.ObjectStreamConstants;
import java.util.ArrayList;
import java.util.List;

import org.conqat.lib.commons.assertion.CCSMPre;
import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.collections.UnmodifiableList;
import org.conqat.lib.commons.serialization.SerializedEntityParser;
import org.conqat.lib.commons.serialization.SerializedEntityPool;
import org.conqat.lib.commons.serialization.SerializedEntitySerializer;

/**
 * A serialized class.
 * 
 * @author $Author: heinemann $
 * @version $Rev: 50911 $
 * @ConQAT.Rating GREEN Hash: 3A1C5B0B47A769D17ABFE3A94D2FE459
 */
public class SerializedClass extends SerializedClassBase {

	/** The class description flags used for enums (found by experimentation). */
	private static final byte ENUM_CLASS_DESCRIPTION_FLAGS = 18;

	/** Fully qualified class name. */
	private String name;

	/** The serial version uid. */
	private long serialVersionUid;

	/** The class description flags. */
	private byte classDescriptionFlags;

	/** The fields of the class. */
	private List<SerializedFieldBase> fields;

	/** Constructor. */
	public SerializedClass(DataInputStream din, SerializedEntityPool pool,
			SerializedEntityParser parser) throws IOException {
		super(din, pool, parser);
	}

	/** Direct constructor. */
	private SerializedClass(String name, int serialVersionUid,
			byte classDescriptionFlags, SerializedEntityPool pool) {
		super(pool);
		this.name = name;
		this.serialVersionUid = serialVersionUid;
		this.classDescriptionFlags = classDescriptionFlags;
		this.fields = new ArrayList<>();
	}

	/** {@inheritDoc} */
	@Override
	protected void parseClass(DataInputStream din, SerializedEntityPool pool,
			SerializedEntityParser parser) throws IOException {
		this.name = din.readUTF();
		this.serialVersionUid = din.readLong();
		this.classDescriptionFlags = din.readByte();

		short fieldCount = din.readShort();
		fields = new ArrayList<>();
		for (int i = 0; i < fieldCount; ++i) {
			fields.add(readFieldDescription(din, parser));
		}
	}

	/** Reads the <code>fieldDesc</code> part of the stream. */
	private SerializedFieldBase readFieldDescription(DataInputStream din,
			SerializedEntityParser parser) throws IOException {
		byte next = din.readByte();
		String fieldName = din.readUTF();

		switch (next) {
		case SerializedArrayField.TYPE_CODE:
			return new SerializedArrayField(fieldName, parser);
		case SerializedObjectField.TYPE_CODE:
			return new SerializedObjectField(fieldName, parser);
		default:
			return SerializedPrimitiveFieldBase.fromTypeCode((char) next,
					fieldName);
		}
	}

	/** {@inheritDoc} */
	@Override
	protected void serializeClass(DataOutputStream dos,
			SerializedEntitySerializer serializer) throws IOException {
		dos.writeByte(ObjectStreamConstants.TC_CLASSDESC);

		dos.writeUTF(name);
		dos.writeLong(serialVersionUid);
		dos.writeByte(classDescriptionFlags);

		dos.writeShort(fields.size());

		// during serialization, the primitive types are required to come first,
		// as otherwise we get an exception
		for (SerializedFieldBase field : fields) {
			if (field instanceof SerializedPrimitiveFieldBase) {
				field.serialize(dos, serializer);
			}
		}
		for (SerializedFieldBase field : fields) {
			if (!(field instanceof SerializedPrimitiveFieldBase)) {
				field.serialize(dos, serializer);
			}
		}
	}

	/** Returns the name. */
	public String getName() {
		return name;
	}

	/** Sets the name. */
	public void setName(String name) {
		this.name = name;
	}

	/** Returns the serial version. */
	public long getSerialVersionUid() {
		return serialVersionUid;
	}

	/** Sets the serial version uid. */
	public void setSerialVersionUid(long serialVersionUid) {
		this.serialVersionUid = serialVersionUid;
	}

	/** Returns the class description flags. */
	public byte getClassDescriptionFlags() {
		return classDescriptionFlags;
	}

	/** Returns the fields of this class. */
	public UnmodifiableList<SerializedFieldBase> getFields() {
		return CollectionUtils.asUnmodifiable(fields);
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return "Plain class " + name;
	}

	/** Returns the field by name (or null if not found). */
	public SerializedFieldBase getField(String name) {
		// we do not use a map to speed up lookup as field names may be changed
		for (SerializedFieldBase field : fields) {
			if (field.getName().equals(name)) {
				return field;
			}
		}
		return null;
	}

	/**
	 * Adds a field to this class. Note that the instances of this class have to
	 * be adjusted accordingly, otherwise you will get exceptions during
	 * serialization.
	 */
	public void addField(SerializedFieldBase newField) {
		CCSMPre.isTrue(getField(newField.getName()) == null,
				"Field with this name already exists!");
		fields.add(newField);
	}

	/** Returns whether this is an externalizable class. */
	public boolean isExternalizable() {
		return (classDescriptionFlags & ObjectStreamConstants.SC_EXTERNALIZABLE) != 0;
	}

	/** Returns whether this is a serializable class. */
	public boolean isSerializable() {
		return (classDescriptionFlags & ObjectStreamConstants.SC_SERIALIZABLE) != 0;
	}

	/**
	 * Returns whether this class has a custom write method (only relevant for
	 * serializable).
	 */
	public boolean hasWriteMethod() {
		return (classDescriptionFlags & ObjectStreamConstants.SC_WRITE_METHOD) != 0;
	}

	/**
	 * Returns whether this class stores block data (only relevant for
	 * externalizable).
	 */
	public boolean hasBlockData() {
		return (classDescriptionFlags & ObjectStreamConstants.SC_BLOCK_DATA) != 0;
	}

	/** Factory method for creating a new enum class. */
	public static SerializedClass createSimpleEnum(SerializedEntityPool pool,
			String enumName) {
		SerializedClass resultClass = new SerializedClass(enumName, 0,
				ENUM_CLASS_DESCRIPTION_FLAGS, pool);

		SerializedClass enumClass = pool.findClass(Enum.class.getName());
		if (enumClass == null) {
			enumClass = new SerializedClass(Enum.class.getName(), 0,
					ENUM_CLASS_DESCRIPTION_FLAGS, pool);
		}
		resultClass.superClassHandle = enumClass.getHandle();

		return resultClass;
	}

}
