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
package org.conqat.lib.commons.xml;

import java.util.Iterator;

import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPath;

/**
 * A minimalistic implementation of {@link NamespaceContext} to be used with
 * {@link XPath}. Method {@link #getNamespaceURI(String)} always returns the
 * string provided to the constructor. All other methods throw
 * {@link UnsupportedOperationException}s. These methods are not needed for
 * XPath resolution.
 * <p>
 * Implementation is inspired by snippet on <a
 * href="http://www.ibm.com/developerworks/library/x-javaxpathapi.html"
 * >http://www.ibm.com/developerworks/library/x-javaxpathapi.html</a>.
 * 
 * @author deissenb
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: D54D68196AC51B2B9E53A1D05B518F9D
 */
public class ConstantNamespaceContext implements NamespaceContext {

	/** The URI */
	private final String namesspaceURI;

	/** Create new context. */
	public ConstantNamespaceContext(String namesspaceURI) {
		this.namesspaceURI = namesspaceURI;
	}

	/**
	 * Always returns the string provided to the constructor.
	 */
	@Override
	public String getNamespaceURI(String prefix) {
		return namesspaceURI;
	}

	/**
	 * Throws {@link UnsupportedOperationException}. This method isn't necessary
	 * for XPath processing.
	 */
	@Override
	public String getPrefix(String uri) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Throws {@link UnsupportedOperationException}. This method isn't necessary
	 * for XPath processing.
	 */
	@Override
	@SuppressWarnings("rawtypes")
	public Iterator getPrefixes(String uri) {
		throw new UnsupportedOperationException();
	}

}