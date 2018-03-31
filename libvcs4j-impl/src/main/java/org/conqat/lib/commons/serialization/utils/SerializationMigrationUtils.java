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
package org.conqat.lib.commons.serialization.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.conqat.lib.commons.assertion.CCSMPre;
import org.conqat.lib.commons.serialization.SerializedEntityBase;
import org.conqat.lib.commons.serialization.SerializedEntityPool;
import org.conqat.lib.commons.serialization.classes.SerializedClass;
import org.conqat.lib.commons.serialization.objects.SerializedClassValues;
import org.conqat.lib.commons.serialization.objects.SerializedObject;
import org.conqat.lib.commons.serialization.objects.SerializedStringObject;

/**
 * Utility methods for migrating serialized objects.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 48894 $
 * @ConQAT.Rating GREEN Hash: B3E29681CC8778BA0852C93DA19A0FE5
 */
public class SerializationMigrationUtils {

	/** Transforms all strings in a (de)serialized string set. */
	public static void transformStringsInHashSet(SerializedObject setObject,
			IStringTransformation transformation, SerializedEntityPool pool)
			throws IOException {

		SerializedClass setClass = findClassInHierarchy(setObject,
				HashSet.class);

		SerializedClassValues fieldSets = setObject.getFieldSet(setObject
				.getPlainClassHierarchy().indexOf(setClass));

		for (Object data : fieldSets.getPreFieldData()) {
			if (!(data instanceof Integer)) {
				continue;
			}

			SerializedEntityBase containedElement = pool.getEntity(
					(Integer) data, SerializedEntityBase.class);
			if (containedElement instanceof SerializedStringObject) {
				SerializedStringObject stringContent = (SerializedStringObject) containedElement;
				stringContent.setValue(transformation.transform(stringContent
						.getValue()));
			}
		}
	}

	/** Returns all objects in a (de)serialized {@link ArrayList}. */
	@SuppressWarnings("unchecked")
	public static <T extends SerializedEntityBase> List<T> getObjectsFromArrayList(
			SerializedObject setObject, SerializedEntityPool pool)
			throws IOException {

		SerializedClass setClass = findClassInHierarchy(setObject,
				ArrayList.class);

		SerializedClassValues fieldSets = setObject.getFieldSet(setObject
				.getPlainClassHierarchy().indexOf(setClass));
		List<T> objects = new ArrayList<>();
		for (Object data : fieldSets.getPostFieldData()) {
			if (!(data instanceof Integer)) {
				continue;
			}
			SerializedEntityBase containedElement = pool.getEntity(
					(Integer) data, SerializedEntityBase.class);
			objects.add((T) containedElement);
		}
		return objects;
	}

	/**
	 * Tries to find a given class in the {@link SerializedObject}'s
	 * classHierarchy
	 */
	public static SerializedClass findClassInHierarchy(
			SerializedObject setObject, Class<?> classToFind)
			throws IOException {
		for (SerializedClass classObject : setObject.getPlainClassHierarchy()) {
			if (classToFind.getName().equals(classObject.getName())) {
				return classObject;
			}
		}
		CCSMPre.fail("Only works on " + classToFind.getName() + " objects!");
		return null;
	}

	/** Interface for transforming strings. */
	public static interface IStringTransformation {

		/** Transforms a single string. */
		String transform(String input);
	}

}
