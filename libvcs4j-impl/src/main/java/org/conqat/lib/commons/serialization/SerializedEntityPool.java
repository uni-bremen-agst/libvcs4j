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

import java.io.IOException;
import java.io.ObjectStreamConstants;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.conqat.lib.commons.serialization.classes.SerializedClass;

/**
 * A class that manages a set of serialized entities (objects and classes) as
 * well as secondary entities referenced by them.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 48837 $
 * @ConQAT.Rating GREEN Hash: 35D7EC1854BE26450471F27E7FDC4595
 */
public class SerializedEntityPool {

	/** The handle representing the <code>null</code> value. */
	public static final int NULL_HANDLE = 0;

	/**
	 * Start value for handles as defined in the <a href=
	 * "http://docs.oracle.com/javase/6/docs/platform/serialization/spec/protocol.html"
	 * >spec</a>.
	 */
	/* package */static final int START_HANDLE = ObjectStreamConstants.baseWireHandle;

	/** Counter used to determine the next entity handle. */
	private int nextHandle = START_HANDLE;

	/** The existing entities in this pool (both root and referenced). */
	private final Map<Integer, SerializedEntityBase> entities = new HashMap<>();

	/**
	 * The handles of root entities, i.e. entities that are actual part of a
	 * serialization stream and not only indirectly referenced.
	 */
	private final List<Integer> rootHandles = new ArrayList<>();

	/**
	 * The classes encountered. They are additionally kept in this list to allow
	 * for faster lookup. We do not use a map (by name) as the name of a class
	 * may be changed programmatically.
	 */
	private final List<SerializedClass> classEntities = new ArrayList<>();

	/** Resets the pool to an empty state. */
	public void reset() {
		nextHandle = START_HANDLE;
		entities.clear();
		rootHandles.clear();
		classEntities.clear();
	}

	/** Adds an entity to this pool and returns the new handle assigned to it. */
	/* package */int addEntity(SerializedEntityBase entity) {
		if (entity instanceof SerializedClass) {
			classEntities.add((SerializedClass) entity);
		}

		int handle = nextHandle++;
		entities.put(handle, entity);
		return handle;
	}

	/** Returns whether the given handle is known to this pool. */
	public boolean containsHandle(int handle) {
		return entities.containsKey(handle);
	}

	/**
	 * Returns an entity by handle. Throws an exception if the handle is not
	 * known.
	 */
	private SerializedEntityBase getEntity(int handle) throws IOException {
		if (handle == NULL_HANDLE) {
			return null;
		}

		SerializedEntityBase entity = entities.get(handle);
		if (entity == null) {
			throw new IOException("No entity registered for handle " + handle);
		}
		return entity;
	}

	/**
	 * Returns an entity of specific expected type. Throws an exception if the
	 * handle is not known or of different type.
	 */
	@SuppressWarnings("unchecked")
	public <T extends SerializedEntityBase> T getEntity(int handle,
			Class<T> expectedType) throws IOException {
		SerializedEntityBase entity = getEntity(handle);
		if (!expectedType.isInstance(entity)) {
			throw new IOException("Expected type " + expectedType
					+ " for handle " + handle + " but was " + entity.getClass());
		}
		return (T) entity;
	}

	/** Return all entities of the given type. */
	@SuppressWarnings("unchecked")
	public <T extends SerializedEntityBase> List<T> getEntities(
			Class<T> expectedType) throws IOException {
		List<T> returnEntities = new ArrayList<>();
		List<SerializedEntityBase> rootEntities = getRootEntities();
		for (SerializedEntityBase entity : rootEntities) {
			if (expectedType.isInstance(entity)) {
				returnEntities.add((T) entity);
			}
		}
		return returnEntities;
	}

	/** Returns the root entities in this pool. */
	public List<SerializedEntityBase> getRootEntities() throws IOException {
		List<SerializedEntityBase> rootEntities = new ArrayList<>();
		for (int handle : rootHandles) {
			rootEntities.add(getEntity(handle));
		}
		return rootEntities;
	}

	/** Adds a handle as root. */
	public void registerRootHandle(int handle) throws IOException {
		if (handle != NULL_HANDLE && !containsHandle(handle)) {
			throw new IOException("Can not register unknown handle " + handle
					+ " as root!");
		}
		rootHandles.add(handle);
	}

	/** Attempts to find a class by its name. Returns null if not found. */
	public SerializedClass findClass(String name) {
		for (SerializedClass classEntity : classEntities) {
			if (classEntity.getName().equals(name)) {
				return classEntity;
			}
		}
		return null;
	}
}
