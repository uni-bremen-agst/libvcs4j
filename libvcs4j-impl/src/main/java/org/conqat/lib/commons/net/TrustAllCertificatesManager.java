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

import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;

/**
 * A simple implementation of {@link X509TrustManager} that simple trusts every
 * certificate.
 * 
 * @author deissenb
 * @author $Author:deissenb $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 406FEF389473035D9FC3BB98435AA04E
 */
public class TrustAllCertificatesManager implements X509TrustManager {
	/** Returns <code>null</code>. */
	@Override
	public X509Certificate[] getAcceptedIssuers() {
		return null;
	}

	/** Does nothing. */
	@Override
	public void checkServerTrusted(X509Certificate[] certs, String authType) {
		// Nothing to do
	}

	/** Does nothing. */
	@Override
	public void checkClientTrusted(X509Certificate[] certs, String authType) {
		// Nothing to do
	}

}