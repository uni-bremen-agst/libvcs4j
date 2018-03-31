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
import java.io.IOException;
import java.io.ObjectStreamConstants;
import java.util.ArrayList;
import java.util.List;

import org.conqat.lib.commons.collections.Pair;
import org.conqat.lib.commons.serialization.SerializedEntityParser;
import org.conqat.lib.commons.serialization.SerializedEntityPool;
import org.conqat.lib.commons.serialization.SerializedEntitySerializer;
import org.conqat.lib.commons.serialization.classes.SerializedClass;
import org.conqat.lib.commons.serialization.classes.SerializedClassBase;
import org.conqat.lib.commons.serialization.classes.SerializedFieldBase;
import org.conqat.lib.commons.string.StringUtils;

/**
 * A serialized object.
 * 
 * @author $Author: heinemann $
 * @version $Rev: 50916 $
 * @ConQAT.Rating GREEN Hash: C82138F1747595C4FDA7EB7F8D536B94
 */
public class SerializedObject extends SerializedObjectBase {

	/**
	 * The field values for this class organized as field sets. Each field set
	 * corresponds to one class in the class hierarchy (from sub class to super
	 * class).
	 */
	private final List<SerializedClassValues> fieldSets = new ArrayList<>();

	/** Constructor. */
	public SerializedObject(DataInputStream din, SerializedEntityPool pool,
			SerializedEntityParser parser, int classHandle) throws IOException {
		super(pool, classHandle);

		for (SerializedClass serializedClass : getPlainClassHierarchy()) {
			fieldSets.add(new SerializedClassValues(serializedClass, din,
					parser));
		}
	}

	/** {@inheritDoc} */
	@Override
	protected byte getObjectTagConstant() {
		return ObjectStreamConstants.TC_OBJECT;
	}

	/** {@inheritDoc} */
	@Override
	protected void serializeObjectContent(DataOutputStream dos,
			SerializedEntitySerializer serializer) throws IOException {
		int index = 0;
		for (SerializedClass serializedClass : getPlainClassHierarchy()) {
			SerializedClassValues fieldSet = fieldSets.get(index++);
			fieldSet.serialize(serializedClass, pool, dos, serializer);
		}
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		String className = "<unknown>";
		try {
			className = pool.getEntity(classHandle, SerializedClassBase.class)
					.toString();
			builder.append("Object " + getHandle() + ": " + className
					+ StringUtils.CR);

			int index = 0;
			for (SerializedClass serializedClass : getPlainClassHierarchy()) {
				appendFieldSet(fieldSets.get(index++), serializedClass, builder);
			}
		} catch (IOException e) {
			builder.append("Could not resolve hierarchy for class " + className
					+ ": " + e.getMessage());
		}
		return builder.toString();
	}

	/** Appends the information on a field set to the given builder. */
	private void appendFieldSet(SerializedClassValues fieldSet,
			SerializedClass serializedClass, StringBuilder builder) {
		if (fieldSet.hasFieldValues()) {
			int fieldIndex = 0;
			for (SerializedFieldBase field : serializedClass.getFields()) {
				builder.append("  " + serializedClass.getName() + "#"
						+ field.getName() + ": "
						+ fieldSet.getValue(fieldIndex++) + StringUtils.CR);
			}
		} else {
			builder.append("  " + serializedClass.getName() + ": <no data>");
		}
	}

	/**
	 * Return the value object for the field with the given name.
	 * 
	 * @return The fields value or null if no field with the name was found
	 */
	public Object getFieldValue(String name) throws IOException {
		Pair<SerializedClassValues, Integer> fieldSetAndValue = getFieldSetAndIndex(name);
		if (fieldSetAndValue == null) {
			return null;
		}
		return fieldSetAndValue.getFirst().getValue(
				fieldSetAndValue.getSecond());
	}

	/** Sets the value object for the field with the given name. */
	public void setFieldValue(String name, Object value) throws IOException {
		Pair<SerializedClassValues, Integer> fieldSetAndValue = getFieldSetAndIndex(name);
		if (fieldSetAndValue != null) {
			fieldSetAndValue.getFirst().setValue(fieldSetAndValue.getSecond(),
					value);
		}
	}

	/**
	 * Returns the field set and index within this fieldset for a field with
	 * given name. Returns null if none is found.
	 */
	private Pair<SerializedClassValues, Integer> getFieldSetAndIndex(
			String fieldName) throws IOException {
		int classIndex = 0;
		for (SerializedClass serializedClass : getPlainClassHierarchy()) {
			SerializedFieldBase field = serializedClass.getField(fieldName);
			if (field != null) {
				int index = serializedClass.getFields().indexOf(field);
				SerializedClassValues fieldSet = fieldSets.get(classIndex);
				return new Pair<>(fieldSet, index);
			}
			classIndex++;
		}
		return null;
	}

	/** Returns number of field sets. */
	public int getFieldSetCount() {
		return fieldSets.size();
	}

	/** Returns field sets of given index. */
	public SerializedClassValues getFieldSet(int index) {
		return fieldSets.get(index);
	}
}
