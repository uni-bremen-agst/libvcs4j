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
 * Compares strings by their length. Using this comparator on a list of string
 * makes shorter strings appear first.
 * 
 * 
 * @author Florian Deissenboeck
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 355D8277FF2A840755FB60F4363B713C
 */
public class StringLengthComparator implements Comparator<String> {

    /**
     * Compare strings by their length. Using this comparator on a list of
     * string makes shorter strings appear first. {@inheritDoc}
     */
    @Override
	public int compare(String string1, String string2) {
        int length1;
        int length2;

        if (string1 == null) {
            length1 = 0;
        } else {
            length1 = string1.length();
        }

        if (string2 == null) {
            length2 = 0;
        } else {
            length2 = string2.length();
        }

        return length1 - length2;
    }

}