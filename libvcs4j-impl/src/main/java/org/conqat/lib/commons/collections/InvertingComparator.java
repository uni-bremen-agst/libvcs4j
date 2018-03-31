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

import java.util.Comparator;

/**
 * This works on another compartor and inverts it.
 * 
 * 
 * @author Florian Deissenboeck
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 5F819AC5DE229463D83C6336178752C7
 */
public class InvertingComparator<E> implements Comparator<E> {

    /** The underlying comparator */
    private final Comparator<? super E> comparator;

    /**
     * Create a new <code>InvertingComparator</code>.
     * 
     * @param comparator
     *            the comparator to invert.
     */
    public InvertingComparator(Comparator<? super E> comparator) {
        this.comparator = comparator;
    }

    /**
     * Compares to objects with the orginal comparator and inverts the result. *
     */
    @Override
	public int compare(E object1, E object2) {
        return -1 * comparator.compare(object1, object2);
    }

}