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
import java.io.ObjectStreamConstants;

import org.conqat.lib.commons.serialization.SerializedEntityPool;
import org.conqat.lib.commons.serialization.SerializedEntitySerializer;
import org.conqat.lib.commons.serialization.classes.SerializedClass;

/**
 * A class object. This is different from the {@link SerializedClass} as this is
 * the actual object, while the former is only a description.
 * 
 * @author $Author: poehlmann $
 * @version $Rev: 48715 $
 * @ConQAT.Rating GREEN Hash: 1862DF2C81CC97065D7B2B277D477E24
 */
public class SerializedClassObject extends SerializedObjectBase {

	/** Constructor. */
	public SerializedClassObject(SerializedEntityPool pool, int parseClassDesc) {
		super(pool, parseClassDesc);
	}

	/** {@inheritDoc} */
	@Override
	protected byte getObjectTagConstant() {
		return ObjectStreamConstants.TC_CLASS;
	}

	/** {@inheritDoc} */
	@Override
	protected void serializeObjectContent(DataOutputStream dos,
			SerializedEntitySerializer serializer) {
		// nothing to do
	}

}
