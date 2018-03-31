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
package org.conqat.lib.commons.equals;

import java.util.Objects;

/**
 * {@link IEquator} using the normal equals (but respecting null).
 * 
 * @author $Author: streitel $
 * @version $Rev: 51728 $
 * @ConQAT.Rating GREEN Hash: 74E655ECD7D84FEBF2998A383037F004
 */
public class DefaultEquator implements IEquator<Object> {

	/** Singleton instance. */
	public static final DefaultEquator INSTANCE = new DefaultEquator();

	/** Constructor */
	private DefaultEquator() {
		// enforce use of singleton instance
	}

	/** {@inheritDoc} */
	@Override
	public boolean equals(Object element1, Object element2) {
		return Objects.equals(element1, element2);
	}

}