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

import org.conqat.lib.commons.serialization.SerializedEntityParser;
import org.conqat.lib.commons.serialization.SerializedEntityPool;
import org.conqat.lib.commons.serialization.SerializedEntitySerializer;
import org.conqat.lib.commons.string.StringUtils;

/**
 * A serialized <a href=
 * "http://docs.oracle.com/javase/7/docs/api/java/lang/reflect/Proxy.html">proxy
 * class</a>.
 * 
 * @author $Author: poehlmann $
 * @version $Rev: 48729 $
 * @ConQAT.Rating GREEN Hash: 2A227B758ED673AFED143BEE7E4F39CE
 */
public class SerializedProxyClass extends SerializedClassBase {

	/** The names of interfaces implemented by the proxy class. */
	private List<String> proxyInterfaceNames;

	/** Constructor. */
	public SerializedProxyClass(DataInputStream din, SerializedEntityPool pool,
			SerializedEntityParser parser) throws IOException {
		super(din, pool, parser);
	}

	/** {@inheritDoc} */
	@Override
	protected void parseClass(DataInputStream din, SerializedEntityPool pool,
			SerializedEntityParser parser) throws IOException {
		int proxyClassCount = din.readInt();
		proxyInterfaceNames = new ArrayList<>();
		for (int i = 0; i < proxyClassCount; ++i) {
			proxyInterfaceNames.add(din.readUTF());
		}
	}

	/** {@inheritDoc} */
	@Override
	protected void serializeClass(DataOutputStream dos,
			SerializedEntitySerializer serializer) throws IOException {
		dos.writeByte(ObjectStreamConstants.TC_PROXYCLASSDESC);
		dos.writeInt(proxyInterfaceNames.size());
		for (String proxyInterfaceName : proxyInterfaceNames) {
			dos.writeUTF(proxyInterfaceName);
		}
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return "Proxy class for "
				+ StringUtils.concat(proxyInterfaceNames, "|");
	}
}
