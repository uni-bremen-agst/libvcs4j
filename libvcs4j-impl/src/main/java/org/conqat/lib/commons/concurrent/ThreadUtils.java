/*-------------------------------------------------------------------------+
|                                                                          |
| Copyright 2005-2011 the ConQAT Project                                   |
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
package org.conqat.lib.commons.concurrent;

/**
 * Utility methods for dealing with threads.
 * 
 * @author $Author: heinemann $
 * @version $Rev: 44280 $
 * @ConQAT.Rating GREEN Hash: 89C4A63305D4782C6C2D632B75F32CCA
 */
public class ThreadUtils {

	/**
	 * Causes the current thread to sleep the given number of milliseconds. The
	 * sleep phase can also exit earlier if an {@link InterruptedException} is
	 * thrown. Any {@link InterruptedException} is silently discarded.
	 */
	public static void sleep(long milliseconds) {
		try {
			Thread.sleep(milliseconds);
		} catch (InterruptedException e) {
			// ignore exception
		}
	}
}
