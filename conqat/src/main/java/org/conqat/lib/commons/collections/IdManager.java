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
package org.conqat.lib.commons.collections;

import java.util.HashMap;

/**
 * This class assigns unique ids to objects. The id creation is based on
 * <code>hashCode()/equals()</code>-semantics.
 * 
 * <p>
 * Note that obtaining a unique id from this class for an object prevents it
 * from being garbage collected.
 * 
 * 
 * @author Florian Deissenboeck
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: C0F76B1514F9C959FF5C0A7A212233F7
 */
public class IdManager<K> extends IdManagerBase<K> {

    /**
     * Create new id manager
     * 
     */
    public IdManager() {
        super(new HashMap<K, Integer>());
    }

}