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

import java.util.Collections;
import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * An executor service that executes everything within the caller's thread.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 32078A9B2C4107CAC2FEB3D1FC7DC8AD
 */
public class InThreadExecutorService extends AbstractExecutorService {

	/** Flags for shutdown. */
	private boolean shutdown = false;

	/** {@inheritDoc} */
	@Override
	public boolean awaitTermination(long timeout, TimeUnit unit) {
		// always terminated (single thread)
		return true;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isShutdown() {
		return shutdown;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isTerminated() {
		return true;
	}

	/** {@inheritDoc} */
	@Override
	public void shutdown() {
		shutdown = true;
	}

	/** {@inheritDoc} */
	@SuppressWarnings("unchecked")
	@Override
	public List<Runnable> shutdownNow() {
		shutdown = true;
		return Collections.EMPTY_LIST;
	}

	/** {@inheritDoc} */
	@Override
	public void execute(Runnable command) {
		command.run();
	}

}