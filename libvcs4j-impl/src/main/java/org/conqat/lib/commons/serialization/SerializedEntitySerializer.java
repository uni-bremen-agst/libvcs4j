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
package org.conqat.lib.commons.serialization;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectStreamConstants;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import org.conqat.lib.commons.assertion.CCSMAssert;
import org.conqat.lib.commons.serialization.objects.LongStringUtils;
import org.conqat.lib.commons.serialization.objects.SerializedObjectBase;
import org.conqat.lib.commons.serialization.objects.SerializedStringObject;

/**
 * Serializes serialized entities (objects + classes) back to a stream.
 * 
 * @author $Author: poehlmann $
 * @version $Rev: 48750 $
 * @ConQAT.Rating GREEN Hash: 031ACA2E0EAAC28C8C7111844607BB5D
 */
public class SerializedEntitySerializer {

	/** Maximal allowed size for short block data. */
	private static final int MAX_SHORT_BLOCK_DATA_SIZE = 255;

	/** The stream to write into. */
	private final DataOutputStream dos;

	/** Counter used to determine the next entity handle. */
	private int nextHandle = SerializedEntityPool.START_HANDLE;

	/** Entities already written together with their handles. */
	private final Map<SerializedEntityBase, Integer> entityPool = new IdentityHashMap<>();

	/**
	 * Strings already written together with their handles. This is kept
	 * separate from the {@link #entityPool} to allow exploitation of
	 * (accidental) equality in strings.
	 */
	private final Map<String, Integer> stringPool = new HashMap<>();

	/** Constructor. */
	private SerializedEntitySerializer(DataOutputStream dos) {
		this.dos = dos;
	}

	/** Performs the actual serialization. */
	private void serialize(List<SerializedEntityBase> entities)
			throws IOException {
		dos.writeShort(ObjectStreamConstants.STREAM_MAGIC);
		dos.writeShort(ObjectStreamConstants.STREAM_VERSION);

		for (SerializedEntityBase entity : entities) {
			if (entity == null) {
				dos.writeByte(ObjectStreamConstants.TC_NULL);
			} else {
				entity.serialize(dos, this);
			}
		}
	}

	/** Serializes a pool to a data output stream. */
	public static void serializeToStream(List<SerializedEntityBase> entities,
			DataOutputStream dos) throws IOException {
		new SerializedEntitySerializer(dos).serialize(entities);
	}

	/** Serializes a pool to an output stream. */
	public static void serializeToStream(List<SerializedEntityBase> entities,
			OutputStream out) throws IOException {
		DataOutputStream dos = new DataOutputStream(out);
		serializeToStream(entities, dos);
		dos.flush();
	}

	/** Serializes a pool to a raw array. */
	public static byte[] serializeToBytes(List<SerializedEntityBase> entities)
			throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		serializeToStream(entities, baos);
		baos.close();
		return baos.toByteArray();
	}

	/** Serializes a string as an object. */
	public void serializeStringObject(String value) throws IOException {
		Integer existingHandle = stringPool.get(value);
		if (existingHandle != null) {
			dos.writeByte(ObjectStreamConstants.TC_REFERENCE);
			dos.writeInt(existingHandle);
			return;
		}

		stringPool.put(value, obtainHandle());
		if (value.length() <= LongStringUtils.MAX_SHORT_STRING_LENGTH) {
			dos.writeByte(ObjectStreamConstants.TC_STRING);
			dos.writeUTF(value);
		} else {
			dos.writeByte(ObjectStreamConstants.TC_LONGSTRING);
			LongStringUtils.writeUTF(value, dos);
		}
	}

	/** Writes a raw block of data. */
	public void writeBlockData(byte[] data) throws IOException {
		if (data.length <= MAX_SHORT_BLOCK_DATA_SIZE) {
			dos.write(ObjectStreamConstants.TC_BLOCKDATA);
			dos.writeByte(data.length);
		} else {
			dos.write(ObjectStreamConstants.TC_BLOCKDATALONG);
			dos.writeInt(data.length);
		}
		dos.write(data);
	}

	/**
	 * Serializes an annotation list as found in <code>objectAnnotation</code>
	 * and <code>classAnnotation</code>. Note that this has nothing to do with
	 * Java annotations (the language feature), but rather is a list consisting
	 * of raw data (byte arrays) and handles (int).
	 */
	public void serializeAnnotationList(List<Object> rawDataList,
			SerializedEntityPool pool) throws IOException,
			SerializationConsistencyException {
		for (Object rawData : rawDataList) {
			if (rawData instanceof byte[]) {
				writeBlockData((byte[]) rawData);
			} else if (rawData instanceof Integer) {
				int handle = (Integer) rawData;
				serializeObject(handle, SerializedObjectBase.class, pool, dos,
						this);
			} else {
				throw new SerializationConsistencyException(
						"Unexpected type in class annotations!");
			}
		}
	}

	/**
	 * Serializes the object defined by the given handle. Also accepts the null
	 * handle.
	 */
	public static void serializeObject(int handle,
			Class<? extends SerializedEntityBase> expectedType,
			SerializedEntityPool pool, DataOutputStream dos,
			SerializedEntitySerializer serializer) throws IOException {
		if (handle == SerializedEntityPool.NULL_HANDLE) {
			dos.writeByte(ObjectStreamConstants.TC_NULL);
		} else {
			pool.getEntity(handle, expectedType).serialize(dos, serializer);
		}
	}

	/** Returns the next handle for a written object. */
	private int obtainHandle() {
		return nextHandle++;
	}

	/**
	 * Attempts to write a reference to this object if it has been written
	 * before. Returns whether a reference could be written. This may not be
	 * used for strings ({@link SerializedStringObject}).
	 */
	public boolean writeReference(SerializedEntityBase entity)
			throws IOException {
		CCSMAssert.isFalse(entity instanceof SerializedStringObject,
				"String are handled in a separate pool!");

		Integer existingHandle = entityPool.get(entity);
		if (existingHandle != null) {
			dos.writeByte(ObjectStreamConstants.TC_REFERENCE);
			dos.writeInt(existingHandle);
			return true;
		}
		return false;
	}

	/** Registers an entity for a handle to be referenceable later on. */
	public void registerHandle(SerializedEntityBase entity) throws IOException {
		if (entityPool.containsKey(entity)) {
			throw new IOException("Duplicate registration of entity " + entity);
		}

		entityPool.put(entity, obtainHandle());
	}

}
