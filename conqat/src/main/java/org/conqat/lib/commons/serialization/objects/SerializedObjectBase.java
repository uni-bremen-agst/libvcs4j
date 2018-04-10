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

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.conqat.lib.commons.serialization.SerializedEntityBase;
import org.conqat.lib.commons.serialization.SerializedEntityPool;
import org.conqat.lib.commons.serialization.SerializedEntitySerializer;
import org.conqat.lib.commons.serialization.classes.SerializedClass;
import org.conqat.lib.commons.serialization.classes.SerializedClassBase;

/**
 * Base class for all serialized objects.
 * 
 * @author $Author: poehlmann $
 * @version $Rev: 48715 $
 * @ConQAT.Rating GREEN Hash: 86899F83BE69C623B68923F878E1E4AE
 */
public abstract class SerializedObjectBase extends SerializedEntityBase {

	/** The handle of the class this object instantiates. */
	protected final int classHandle;

	/** Constructor. */
	protected SerializedObjectBase(SerializedEntityPool pool, int classHandle) {
		super(pool);
		this.classHandle = classHandle;
	}

	/**
	 * Returns the class hierarchy from most super class (nearest to
	 * {@link Object}) to sub class including only plain classes (no proxies).
	 */
	public List<SerializedClass> getPlainClassHierarchy() throws IOException {
		int currentClassHandle = this.classHandle;
		List<SerializedClass> hierarchy = new ArrayList<>();
		while (currentClassHandle != SerializedEntityPool.NULL_HANDLE) {
			SerializedClassBase classEntity = pool.getEntity(
					currentClassHandle, SerializedClassBase.class);
			if (classEntity instanceof SerializedClass) {
				// Omits other serialized class implementations like proxies
				hierarchy.add((SerializedClass) classEntity);
			}
			currentClassHandle = classEntity.getSuperClassHandle();
		}

		Collections.reverse(hierarchy);
		return hierarchy;
	}

	/** Returns the handle of the class for this object. */
	public int getClassHandle() {
		return classHandle;
	}

	/** {@inheritDoc} */
	@Override
	protected void serializeContent(DataOutputStream dos,
			SerializedEntitySerializer serializer) throws IOException {
		dos.writeByte(getObjectTagConstant());
		pool.getEntity(classHandle, SerializedClassBase.class).serialize(dos,
				serializer);

		serializer.registerHandle(this);

		serializeObjectContent(dos, serializer);
	}

	/** Returns the tag code for the object type. */
	protected abstract byte getObjectTagConstant();

	/** Serializes the object's content. */
	protected abstract void serializeObjectContent(DataOutputStream dos,
			SerializedEntitySerializer serializer) throws IOException;
}
