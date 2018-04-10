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

/**
 * Default implementation of {@link IXMLResolver}.
 * 
 * @author deissenb
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 36239AFAABED9E49B612414D7DBFD882
 */
public class XMLResolver<E extends Enum<E>, A extends Enum<A>> implements
		IXMLResolver<E, A> {

	/** Class object for attribute enum. */
	private final Class<A> attributeClass;

	/**
	 * Create new resolver.
	 * 
	 * @param attributeClass
	 *            class object for attribute enum.
	 */
	public XMLResolver(Class<A> attributeClass) {
		this.attributeClass = attributeClass;
	}

	/** {@inheritDoc} */
	@Override
	public Class<A> getAttributeClass() {
		return attributeClass;
	}

	/** Returns <code>&lt;enum-element&gt;.name()</code>. */
	@Override
	public String resolveAttributeName(A attribute) {
		return attribute.name();
	}

	/** Returns <code>&lt;enum-element&gt;.name()</code>. */
	@Override
	public String resolveElementName(E element) {
		return element.name();
	}

}