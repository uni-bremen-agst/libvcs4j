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
package org.conqat.lib.commons.system;

import org.conqat.lib.commons.string.StringUtils;

/**
 * A utility class for system diagnostics.
 * 
 * @author Florian Deissenboeck
 * @author $Author: kinnen $
 * 
 * @version $Revision: 41751 $
 * @ConQAT.Rating GREEN Hash: 7330B367562E2508BCFDB39C59978F76
 */
public class DiagnosticUtils {
    /**
     * Returns a string describing the current memory status (max, free and used
     * memory).
     */
    public static String getMemoryStatusDescription() {
        StringBuilder status = new StringBuilder();
        Runtime runtime = Runtime.getRuntime();
        status.append("Memory status [kB]: ");
        status.append("  Max: "
                + StringUtils.format(runtime.maxMemory() / 1024));
        status.append("  Used: "
                + StringUtils.format(runtime.totalMemory() / 1024));
        status.append("  Free: "
                + StringUtils.format((runtime.maxMemory() - runtime
                        .totalMemory()) / 1024));
        return status.toString();
    }
}