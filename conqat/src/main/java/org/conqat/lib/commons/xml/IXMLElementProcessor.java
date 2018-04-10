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
 * This interface defines processors that process elements from an XML file.
 * 
 * @param <E>
 *            the enumeration describing the XML elements
 * @param <X>
 *            the exception type this processor might throw
 * @author Florian Deissenboeck
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 1CFEC723DABE1DA2E4FD8B270F6B1D10
 */
public interface IXMLElementProcessor<E extends Enum<E>, X extends Exception> {

	/** The element type this processor is meant to process. */
	public E getTargetElement();

	/** Process element. */
	public void process() throws X;
}