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

/**
 * Utility methods used in conjunction with sockets.
 * 
 * @author hummelb
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: F429597D39F372E8715BB1485541289B
 */
public class SocketUtils {

	/** Checks whether the given TCP port is available. */
	public static boolean isFreePort(int portNumber) {
		try {
			ServerSocket socket = new ServerSocket(portNumber);
			socket.setReuseAddress(true);
			socket.close();
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	/** Returns a new free TCP port number */
	public static int getFreePort() throws IOException {
		ServerSocket socket = new ServerSocket(0);
		socket.setReuseAddress(true);
		int result = socket.getLocalPort();
		socket.close();
		return result;
	}

}