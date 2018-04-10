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

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Base class for anything that is part of a serialized stream (objects,
 * classes, etc.).
 * 
 * @author $Author: poehlmann $
 * @version $Rev: 48713 $
 * @ConQAT.Rating GREEN Hash: 6B40713F148DB2462CDC17CE6A2F26D4
 */
public abstract class SerializedEntityBase {

	/** The handle of this entity. */
	protected final int handle;

	/** The pool this entity is registered at. */
	protected final SerializedEntityPool pool;

	/**
	 * Constructor. This also registers the entity with the pool. The reason
	 * that this happens here is that adding to the pool also defines the handle
	 * of the entity (handle counter in the pool). Thus we must be sure that
	 * obtaining the handle happens before any other (child) entities are added.
	 */
	protected SerializedEntityBase(SerializedEntityPool pool) {
		this.handle = pool.addEntity(this);
		this.pool = pool;
	}

	/** Returns the handle for this entity. */
	public int getHandle() {
		return handle;
	}

	/** Serializes the entity on an output stream. */
	public void serialize(DataOutputStream dos,
			SerializedEntitySerializer serializer) throws IOException {
		if (serializer.writeReference(this)) {
			return;
		}

		serializeContent(dos, serializer);
	}

	/** Serializes the content of the entity (no reference) on an output stream. */
	protected abstract void serializeContent(DataOutputStream dos,
			SerializedEntitySerializer serializer) throws IOException;
}
