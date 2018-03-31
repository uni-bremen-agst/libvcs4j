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
 * Interface for resolution of element and attribute names.
 * 
 * @author deissenb
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 8498CAA4E6F015E5BC7BCBCD53D92347
 */
public interface IXMLResolver<E extends Enum<E>, A extends Enum<A>> {
	/** Returns for an attribute the name which should be used in the XML file. */
	public String resolveAttributeName(A attribute);

	/** Returns for an element the name which should be used in the XML file. */
	public abstract String resolveElementName(E element);

	/** Returns the class of the attribute enumeration. */
	public abstract Class<A> getAttributeClass();
}