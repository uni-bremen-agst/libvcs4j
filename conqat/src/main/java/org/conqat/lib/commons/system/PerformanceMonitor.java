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
 * Combines a timer and a memory monitor in a simple utility class. Measures
 * both total memory and the delta between {@link PerformanceMonitor#start()}
 * and {@link PerformanceMonitor#stop()}.
 * <p>
 * In order to avoid programming mistakes, the calls to the methods
 * {@link PerformanceMonitor#start()} and {@link PerformanceMonitor#stop()} must
 * adhere to a simple protocol:<br>
 * The {@link PerformanceMonitor} can be in on of the three states NOT_RUN,
 * RUNNING, FINISHED.
 * <p>
 * {@link PerformanceMonitor#start()} May only be called in state NOT_RUN and
 * {@link PerformanceMonitor#stop()} may only be called in state RUNNING.
 * <p>
 * All other calls to {@link PerformanceMonitor#start()} and
 * {@link PerformanceMonitor#stop()} result in {@link IllegalArgumentException}s
 * 
 * @author Elmar Juergens
 * @author $Author: kinnen $
 * 
 * @version $Revision: 41751 $
 * @ConQAT.Rating GREEN Hash: 6649C4CE12076E80CBD776387502755A
 */
public class PerformanceMonitor {

	/** The state the {@link PerformanceMonitor} is currently in */
	private EPerformanceMonitorState state = EPerformanceMonitorState.NOT_RUN;

	/**
	 * Timestamp the call to the {@link PerformanceMonitor#start()} method
	 * occurred
	 */
	private long startTimeInMillis;

	/**
	 * Timestamp the call to the {@link PerformanceMonitor#stop()} method
	 * occurred
	 */
	private long stopTimeInMillis;

	/** {@link MemoryMonitor} used to measure total memory consumption */
	private MemoryMonitor memMonitor;

	/**
	 * Flag that determines whether a memory monitor (that uses its own thread)
	 * should be used to get a more exact measurement of maximum memory
	 * consumption.
	 */
	private final boolean useMemoryMonitor;

	/**
	 * Memory consumption when the call to {@link PerformanceMonitor#start()}
	 * occurred.
	 */
	private long startMemoryInBytes;

	/**
	 * Memory consumption when the call to {@link PerformanceMonitor#stop()}
	 * occurred.
	 */
	private long stopMemoryInBytes;

	/**
	 * Convenience factory method: Creates a new {@link PerformanceMonitor} and
	 * starts it.
	 * 
	 * @param useMemoryMonitor
	 *            detemines whether the PerformanceMonitor internally uses a
	 *            {@link MemoryMonitor} to measure memory consumption. This
	 *            requires more resources, since the {@link MemoryMonitor} runs
	 *            its own thread, but promises more precise measurement of
	 *            maximum memory consumption.
	 */
	public static PerformanceMonitor create(boolean useMemoryMonitor) {
		PerformanceMonitor monitor = new PerformanceMonitor(useMemoryMonitor);
		monitor.start();
		return monitor;
	}

	/**
	 * Convenience factory method: Creates a new {@link PerformanceMonitor} and
	 * starts it. PerformanceMonitor does not use a MemoryMonitor internally
	 */
	public static PerformanceMonitor create() {
		return create(false);
	}

	/**
	 * Constructor has package level to allow tests to access it, yet enforce
	 * use of factory methods for public use.
	 */
	/* package */PerformanceMonitor(boolean useMemoryMonitor) {
		this.useMemoryMonitor = useMemoryMonitor;
	}

	/**
	 * Starts the {@link PerformanceMonitor}. It will measure time and maximal
	 * memory consumption until the method {@link PerformanceMonitor#stop()} is
	 * called.
	 * <p>
	 * This method may only be called, if the {@link PerformanceMonitor} is in
	 * state NOT_RUN. (i.e., after it has been created).
	 * <p>
	 * All subsequent calls to this method will result in a
	 * {@link IllegalStateException}
	 */
	public void start() {
		if (state != EPerformanceMonitorState.NOT_RUN) {
			throw new IllegalStateException(
					"PerformanceMonitor is already running and cannot be restarted");
		}

		state = EPerformanceMonitorState.RUNNING;
		if (useMemoryMonitor) {
			memMonitor = new MemoryMonitor();
			memMonitor.start();
		}
		startMemoryInBytes = Runtime.getRuntime().totalMemory();
		startTimeInMillis = System.currentTimeMillis();
	}

	/**
	 * Stops the {@link PerformanceMonitor}.
	 * <p>
	 * This method may only be called, if the {@link PerformanceMonitor} is in
	 * state RUNNING. (i.e., after a call to {@link PerformanceMonitor#start()}).
	 * <p>
	 * If the {@link PerformanceMonitor} is in any other state, a call to this
	 * method results in an {@link IllegalStateException}
	 */
	public void stop() {
		if (state != EPerformanceMonitorState.RUNNING) {
			throw new IllegalStateException(
					"PerformanceMonitor can only be stopped if it is running");
		}

		stopTimeInMillis = System.currentTimeMillis();
		if (useMemoryMonitor) {
			memMonitor.stop();
		}
		stopMemoryInBytes = Runtime.getRuntime().totalMemory();
		state = EPerformanceMonitorState.FINISHED;
	}

	/** Gets the measured time in seconds. (Fractions of seconds are discarded) */
	public long getSeconds() {
		return getMilliseconds() / 1000;
	}

	/** Gets the measured time in milliseconds */
	public long getMilliseconds() {
		return stopTimeInMillis - startTimeInMillis;
	}

	/** Gets the maximal memory consumption in bytes */
	public long getMaxMemUsageInBytes() {
		if (useMemoryMonitor) {
			return memMonitor.getMaximumMemoryUsage();
		}
		return Math.max(stopMemoryInBytes, startMemoryInBytes);
	}

	/** Gets the maximal memory consumption in kilobytes */
	public long getMaxMemUsageInKBs() {
		return getMaxMemUsageInBytes() / 1024;
	}

	/** Gets the delta in memory consumption in bytes */
	public long getDeltaMemUsageInBytes() {
		return getMaxMemUsageInBytes() - startMemoryInBytes;
	}

	/** Gets the delta in memory consumption in kilobytes */
	public long getDeltaMemUsageInKBs() {
		return getDeltaMemUsageInBytes() / 1024;
	}

}