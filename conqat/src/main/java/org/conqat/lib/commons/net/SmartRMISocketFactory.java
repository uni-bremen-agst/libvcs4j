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
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.server.RMISocketFactory;

/**
 * A {@link RMISocketFactory} that adjusts flags on the sockets used. One is
 * that for the server socket the reuse flag is set, which allows fast
 * reopening. Second, an optional timeout can be set.
 * 
 * @author hummelb
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 01CB74A864069FBA8933C31356E5AB07
 */
public class SmartRMISocketFactory extends RMISocketFactory {

	/** Timeout in seconds. */
	private final int timeoutSeconds;

	/** Constructor. No timeout is set. */
	public SmartRMISocketFactory() {
		this(-1);
	}

	/** Constructor */
	public SmartRMISocketFactory(int timeoutSeconds) {
		this.timeoutSeconds = timeoutSeconds;
	}

	/** {@inheritDoc} */
	@Override
	public Socket createSocket(String host, int port) throws IOException {
		Socket socket = getDefaultSocketFactory().createSocket(host, port);
		if (timeoutSeconds > 0) {
			socket.setSoTimeout(timeoutSeconds * 1000);
		}
		return socket;
	}

	/** {@inheritDoc} */
	@Override
	public ServerSocket createServerSocket(int port) throws IOException {
		ServerSocket socket = getDefaultSocketFactory()
				.createServerSocket(port);
		socket.setReuseAddress(true);
		return socket;
	}
}