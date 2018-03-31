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
package org.conqat.lib.commons.xml;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Base class for SAX parser handlers. This class has a default implementation
 * of {@link #resolveEntity(String, String)} that works around a bug in the Java
 * SAX parser implementation:
 * 
 * If {@link #resolveEntity(String, String)} returns <code>null</code> for a
 * given DTD (the default behavior of {@link DefaultHandler}), the Java SAX
 * parser will try to download that DTD. This will fail on machines that have no
 * Internet connection or require a proxy to access the Internet or for DTDs
 * where the URI is not a valid URL. This class' default implementation instead
 * returns an empty {@link InputSource}, which effectively causes the parser to
 * skip DTD validation.
 * 
 * Please note that downloading the DTD from the Internet is against the DTD
 * specification (as the DTD URI is not required to be a valid URL) and is
 * highly error-prone, resulting in a hard to interpret error message. It is
 * thus recommended you use this base class and never return <code>null</code>
 * from {@link #resolveEntity(String, String)}.
 * 
 * @author $Author: goeb $
 * @version $Rev: 50982 $
 * @ConQAT.Rating GREEN Hash: 99AF4DE1DF6A85A538B2BCBD1BEA41AC
 */
public class OfflineSAXHandlerBase extends DefaultHandler {

	/**
	 * {@inheritDoc}
	 * 
	 * Default implementation that can be overwritten by subclasses. This
	 * implementation returns an empty document for any entity that should be
	 * resolved. This effectively causes the parser to skip DTD validation.
	 * 
	 * @throws IOException
	 *             may be thrown by subclasses.
	 * @throws SAXException
	 *             may be thrown by subclasses.
	 */
	@Override
	public InputSource resolveEntity(String publicId, String systemId)
			throws IOException, SAXException {
		return new InputSource(new ByteArrayInputStream(new byte[0]));
	}

}
