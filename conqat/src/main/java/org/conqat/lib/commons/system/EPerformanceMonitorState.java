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

/**
 * Enumeration of the states of the {@link PerformanceMonitor}.
 * <p>
 * Since this enumeration is only meant for internal use by the
 * {@link PerformanceMonitor}, it has package visibility.
 * 
 * @author Elmar Juergens
 * @author $Author: kinnen $
 * 
 * @version $Revision: 41751 $
 * @ConQAT.Rating GREEN Hash: 1B40FCDE445CA49841319E621F84C2B9
 */
/* package */enum EPerformanceMonitorState {

	/** Performance monitor has not yet been started */
	NOT_RUN,

	/** Performance monitor is running */
	RUNNING,

	/** Performance monitor has been stopped */
	FINISHED
}