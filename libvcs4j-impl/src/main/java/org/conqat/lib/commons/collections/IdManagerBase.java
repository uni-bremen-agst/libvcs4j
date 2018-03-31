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

import java.util.Map;

/**
 * Base class for id managers.
 * 
 * 
 * @author Florian Deissenboeck
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: FBE28F52C2DC5F85748DE4AD50B45D4F
 */
public class IdManagerBase<K> {
    /** Maps from object to ids. */
    private final Map<K, Integer> ids;

    /** the next id to bes used */
    private int currentId = 0;

    /**
     * Create new id manager
     * 
     * @param map
     *            maps from object to ids
     */
    protected IdManagerBase(Map<K, Integer> map) {
        this.ids = map;
    }

    /**
     * Obtain a unique id for an object. Note that obtaining a id for an object
     * prevents it from being garbage collected.
     */
    public int obtainId(K k) {

        // is already stored
        if (ids.containsKey(k)) {
            return ids.get(k);
        }

        ids.put(k, currentId);

        // return id and increase it afterwards
        return currentId++;
    }

    /**
     * Clear the manager. Adding an object to the manager, clearing the manager
     * and re-adding the object will not result in the same ids.
     */
    public void clear() {
        ids.clear();
        currentId = 0;
    }
}