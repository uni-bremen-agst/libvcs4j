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

import org.conqat.lib.commons.serialization.SerializedEntityPool;
import org.conqat.lib.commons.serialization.SerializedEntitySerializer;

/**
 * A serialized object of type string.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 48814 $
 * @ConQAT.Rating GREEN Hash: CDD489F07445C1E6381A0B3FA921B60A
 */
public class SerializedStringObject extends SerializedObjectBase {

	/** The string value of this object. */
	private String value;

	/**
	 * Constructor reading the string from an input stream.
	 * 
	 * @param longString
	 *            if this is true, the string is a long string.
	 */
	public SerializedStringObject(DataInputStream din,
			SerializedEntityPool pool, boolean longString) throws IOException {
		// strings are serialized with an explicit tag code and hence have no
		// class (at least not in the serialization stream); we use null here.
		super(pool, SerializedEntityPool.NULL_HANDLE);
		if (longString) {
			this.value = LongStringUtils.readLongString(din);
		} else {
			this.value = din.readUTF();
		}
	}

	/** Returns the string value. */
	public String getValue() {
		return value;
	}

	/** Sets the string value */
	public void setValue(String value) {
		this.value = value;
	}

	/** {@inheritDoc} */
	@Override
	public void serialize(DataOutputStream dos,
			SerializedEntitySerializer serializer) throws IOException {
		// we never want stings to use references here, as references are
		// handled in the serializer itself
		serializeContent(dos, serializer);
	}

	/** {@inheritDoc} */
	@Override
	protected void serializeContent(DataOutputStream dos,
			SerializedEntitySerializer serializer) throws IOException {
		serializer.serializeStringObject(value);
	}

	/** {@inheritDoc} */
	@Override
	protected byte getObjectTagConstant() {
		throw new AssertionError("Should not be called!");
	}

	/** {@inheritDoc} */
	@Override
	protected void serializeObjectContent(DataOutputStream dos,
			SerializedEntitySerializer serializer) {
		throw new AssertionError("Should not be called!");
	}
}
