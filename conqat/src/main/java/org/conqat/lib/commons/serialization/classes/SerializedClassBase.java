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

import org.conqat.lib.commons.serialization.SerializedEntityBase;
import org.conqat.lib.commons.serialization.SerializedEntityParser;
import org.conqat.lib.commons.serialization.SerializedEntityPool;
import org.conqat.lib.commons.serialization.SerializedEntitySerializer;

/**
 * Base class for all serialized classes.
 * 
 * @author $Author: heinemann $
 * @version $Rev: 50912 $
 * @ConQAT.Rating GREEN Hash: FDE1E10EB2E72743EFF3C95814E398B0
 */
public abstract class SerializedClassBase extends SerializedEntityBase {

	/** Handle to the super class (or null handle). */
	protected int superClassHandle = SerializedEntityPool.NULL_HANDLE;

	/**
	 * Class annotations. These are either byte arrays for block data, or
	 * integer values representing handles.
	 */
	private final List<Object> classAnnotations = new ArrayList<>();

	/** Constructor for parsing. */
	protected SerializedClassBase(DataInputStream din,
			SerializedEntityPool pool, SerializedEntityParser parser)
			throws IOException {
		super(pool);

		parseClass(din, pool, parser);

		readClassAnnotation(din, parser);
		readSuperClassDescription(parser);
	}

	/** Direct constructor. */
	protected SerializedClassBase(SerializedEntityPool pool) {
		super(pool);
	}

	/**
	 * Parses the class content. This is called from the constructor, so fields
	 * of sub classes might not be initialized.
	 */
	protected abstract void parseClass(DataInputStream din,
			SerializedEntityPool pool, SerializedEntityParser parser)
			throws IOException;

	/** Reads the description of the super class. */
	private void readSuperClassDescription(SerializedEntityParser parser)
			throws IOException {
		this.superClassHandle = parser.parseClassDesc();
	}

	/** Reads the <code>classAnnotation</code> part of a class description. */
	private void readClassAnnotation(DataInputStream din,
			SerializedEntityParser parser) throws IOException {
		while (true) {
			din.mark(1);

			byte next = din.readByte();
			switch (next) {
			case ObjectStreamConstants.TC_ENDBLOCKDATA:
				return;
			case ObjectStreamConstants.TC_BLOCKDATA:
				classAnnotations
						.add(readBlockData(din, din.readUnsignedByte()));
				break;
			case ObjectStreamConstants.TC_BLOCKDATALONG:
				classAnnotations.add(readBlockData(din, din.readInt()));
				break;
			default:
				din.reset();
				classAnnotations.add(parser.parseContent());
				break;
			}
		}
	}

	/** Reads block data. */
	private byte[] readBlockData(DataInputStream din, int length)
			throws IOException {
		byte[] result = new byte[length];
		din.readFully(result);
		return result;
	}

	/** {@inheritDoc} */
	@Override
	protected final void serializeContent(DataOutputStream dos,
			SerializedEntitySerializer serializer) throws IOException {
		serializer.registerHandle(this);

		serializeClass(dos, serializer);

		serializeClassAnnotation(dos, serializer);
		serializeSuperClass(dos, serializer);
	}

	/** Serializes the class part of this entity. */
	protected abstract void serializeClass(DataOutputStream dos,
			SerializedEntitySerializer serializer) throws IOException;

	/** Serializes the super class. */
	private void serializeSuperClass(DataOutputStream dos,
			SerializedEntitySerializer serializer) throws IOException {
		SerializedEntitySerializer.serializeObject(superClassHandle,
				SerializedClassBase.class, pool, dos, serializer);
	}

	/** Serializes the class annotation part. */
	private void serializeClassAnnotation(DataOutputStream dos,
			SerializedEntitySerializer serializer) throws IOException {
		serializer.serializeAnnotationList(classAnnotations, pool);

		// end of class annotation always signaled via endBlockData
		dos.writeByte(ObjectStreamConstants.TC_ENDBLOCKDATA);
	}

	/** Returns the handle of the super class. */
	public int getSuperClassHandle() {
		return superClassHandle;
	}
}
