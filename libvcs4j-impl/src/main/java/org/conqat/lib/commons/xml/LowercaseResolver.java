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
 * XML resolver which transforms the enum names by making them lower case and
 * replacing underscores by dashes.
 * 
 * @author hummelb
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: E05E554D1CDBE511344E92EEA6E1C645
 */
public class LowercaseResolver<E extends Enum<E>, A extends Enum<A>> implements
		IXMLResolver<E, A> {

	/** Class object for attribute enum. */
	private final Class<A> attributeClass;

	/**
	 * Create new resolver.
	 * 
	 * @param attributeClass
	 *            class object for attribute enum.
	 */
	public LowercaseResolver(Class<A> attributeClass) {
		this.attributeClass = attributeClass;
	}

	/** {@inheritDoc} */
	@Override
	public Class<A> getAttributeClass() {
		return attributeClass;
	}

	/** {@inheritDoc} */
	@Override
	public String resolveAttributeName(A attribute) {
		return transform(attribute.name());
	}

	/** {@inheritDoc} */
	@Override
	public String resolveElementName(E element) {
		return transform(element.name());
	}

	/** Performs the name transformation. */
	private String transform(String name) {
		return name.toLowerCase().replace('_', '-');
	}

}