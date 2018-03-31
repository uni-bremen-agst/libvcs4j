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

import java.util.Comparator;

/**
 * This comparator orders formal parameters by their position within the formal
 * parameter list of the defining method. This comparator raises an exception if
 * the the compared parameters do not belong to the same method.
 * 
 * 
 * @author Florian Deissenboeck
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 550ECD8A6F9D0086A10B5C9CB46A1E76
 */
public class FormalParameterComparator implements Comparator<FormalParameter> {

    /**
     * Compae formal parameters by their position within the formal parameter
     * list of the defining method.
     * 
     * @throws IllegalArgumentException
     *             if parameters belong to different methods.
     */
    @Override
	public int compare(FormalParameter p1, FormalParameter p2)
            throws IllegalArgumentException {
        if (!p1.getMethod().equals(p2.getMethod())) {
            throw new IllegalArgumentException(
                    "Parameters must belong to same method");
        }
        return p1.getPosition() - p2.getPosition();
    }
}