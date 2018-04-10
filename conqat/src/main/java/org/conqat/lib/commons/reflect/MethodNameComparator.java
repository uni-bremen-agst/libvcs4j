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
package org.conqat.lib.commons.reflect;

import java.lang.reflect.Method;

import org.conqat.lib.commons.collections.IdentifierBasedComparatorBase;

/**
 * This comparator compares methods by their name.
 * 
 * @author deissenb
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 8DF9CB9C7B920A659AF44ADA9FAE87ED
 */
public class MethodNameComparator extends
		IdentifierBasedComparatorBase<String, Method> {

	/** Instance of this comparator. */
	public static final MethodNameComparator INSTANCE = new MethodNameComparator();

	/** Returns method name. */
	@Override
	protected String obtainIdentifier(Method method) {
		return method.getName();
	}

}