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

import java.util.Timer;
import java.util.TimerTask;

/**
 * A simple class for monitoring memory usage of an application. A second thread
 * is started which periodically polls the memory status. <br/><br/> <b>Note:
 * </b> Due to performance reasons the method
 * <code>getMaximumMemoryUsage()</code> and <code>reset()</code> are <b>not
 * </b> synchronized, so calling these methods while the memory monitor is still
 * running might lead to undesired results. Therefore it is recommended stop the
 * memory befor calling <code>getMaximumMemoryUsage()</code> or
 * <code>reset()</code>.
 * 
 * @author Florian Deissenboeck
 * @author $Author: kinnen $
 * 
 * @version $Revision: 41751 $
 * @ConQAT.Rating GREEN Hash: 44868D9B40859A1D4A23B06D58702882
 */
public class MemoryMonitor {
    /** Default polling period [ms]. */
    private static final int DEFAULT_PERIOD = 50;

    /** The timer for controlling the periodical call. */
    private Timer timer;

    /** Period between polls [ms]. */
    private final int period;

    /** The task gathering the memory information. */
    private final MemoryMonitorTask monitorTask;

    /**
     * Construct a new <code>MemoryMonitor</code> with the default monitoring
     * interval.
     * 
     */
    public MemoryMonitor() {
        this(DEFAULT_PERIOD);
    }

    /**
     * Construct a new <code>MemoryMonitor</code>.
     * 
     * @param period
     *            time between subsequent polls to obtain memory status
     * @see #start()
     */
    public MemoryMonitor(int period) {
        monitorTask = new MemoryMonitorTask();
        this.period = period;

        // start as daemon thread
        timer = new Timer(true);
    }

    /**
     * Start the memory monitor.
     * 
     * @see #stop()
     */
    public void start() {
        timer.schedule(monitorTask, 0, period);
    }

    /**
     * Stop the memory monitor. Memory monitor can be restarted safely after
     * stopping without loosing the current maximum value.
     * 
     * @see #start()
     * @see #reset()
     */
    public void stop() {
        timer.cancel();
        timer = new Timer(true);
    }

    /**
     * Reset the maximum memory usage value. Use this method only when monitor
     * is stopped.
     * 
     * @see #stop()
     */
    public void reset() {
        monitorTask.reset();
    }

    /**
     * Obtain maximum amount of memory used since the monitor was started or
     * reset.Use this method only when monitor is stopped.
     * 
     * @see #stop()
     * 
     * @return maximum memory usage [byte]
     */
    public long getMaximumMemoryUsage() {
        return monitorTask.getMaximumMemoryUsage();
    }

    /**
     * A simple timer task for monitor memory status. <br/><br/><b>Note: </b>
     * Due to performance reasons the method
     * <code>getMaximumMemoryUsage()</code> and <code>reset()</code> are
     * <b>not </b> synchronized, so calling these methods while the memory
     * monitor is still running might lead to undesired results.
     */
    private static class MemoryMonitorTask extends TimerTask {

        /** Runtime object for accessing the VM's memory status. */
        private final Runtime runtime;

        /** Maximum amount of memory used . */
        private long maxMemory = 0;

        /**
         * Set up a new monitor task.
         * 
         */
        public MemoryMonitorTask() {
            runtime = Runtime.getRuntime();
        }

        /**
         * Retrieve currently used memory from runtime object and update maximum
         * memory usage if necessary.
         */
        @Override
		public void run() {
            long usedMemory = runtime.totalMemory();
            if (usedMemory > maxMemory) {
                maxMemory = usedMemory;
            }
        }

        /**
         * Obtain maximum amount of memory used since the monitor was started or
         * reset.Use this method only when monitor is stopped.
         * 
         * @return maximum memory usage [byte]
         */
        public long getMaximumMemoryUsage() {
            return maxMemory;
        }

        /**
         * Reset the maximum memory usage value. Use this method only when
         * monitor is stopped.
         * 
         */
        public void reset() {
            maxMemory = 0;
        }

    }
}