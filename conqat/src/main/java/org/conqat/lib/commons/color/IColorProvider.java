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
package org.conqat.lib.commons.color;

import java.awt.Color;

/**
 * Generic color provider interface.
 * 
 * @author hummelb
 * @author $Author: kanis $
 * @version $Rev: 41978 $
 * @ConQAT.Rating YELLOW Hash: 300AF5710D798534AAC00ED4CD059609
 */
public interface IColorProvider<T> {

	/** Returns the color to be used for a given element. */
	Color getColor(T t);

}