/*-------------------------------------------------------------------------+
|                                                                          |
| Copyright 2005-2011 The ConQAT Project                                   |
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
package org.conqat.lib.commons.net;

import java.io.IOException;
import java.io.Serializable;
import java.net.Socket;

/**
 * A socket factory that forces the host to use localhost.
 * 
 * @author hummelb
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: EB1EE85962B0FF70C586C0F3B54545CE
 */
public class LocalhostRMISocketFactory extends SmartRMISocketFactory implements
		Serializable {

	/** Version used for serialization. */
	private static final long serialVersionUID = 1;

	/** Constructor. No timeout is set. */
	public LocalhostRMISocketFactory() {
		// make call explicit here
		super();
	}

	/** Constructor */
	public LocalhostRMISocketFactory(int timeoutSeconds) {
		super(timeoutSeconds);
	}

	/** {@inheritDoc} */
	@Override
	public Socket createSocket(String host, int port) throws IOException {
		return super.createSocket("localhost", port);
	}
}