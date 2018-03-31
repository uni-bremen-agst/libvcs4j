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
package org.conqat.lib.commons.concurrent;

import java.util.TimerTask;

/**
 * A TimerTask that interrupts the specified thread when run.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 74BE68F2DFCB0409FB592FDD03C18E64
 */
public class InterruptTimerTask extends TimerTask {

	/** The thread being interrupted. */
	private final Thread thread;

	/** Constructor. */
	public InterruptTimerTask(Thread t) {
		this.thread = t;
	}

	/** {@inheritDoc} */
	@Override
	public void run() {
		thread.interrupt();
	}
}