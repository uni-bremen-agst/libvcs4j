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
package org.conqat.lib.commons.logging;

/**
 * This interface describes loggers. This interface does not provide methods for
 * logging message at FATAL level as such issues should be signaled with
 * exceptions.
 * 
 * @author Florian Deissenboeck
 * @author Elmar Juergens
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 46BA0822D615AAC91E2752CADD89EC49
 */
public interface ILogger {

	/**
	 * Log message with level DEBUG.
	 * 
	 * @param message
	 *            log message.
	 */
	public void debug(Object message);

	/**
	 * Log message with level DEBUG.
	 * 
	 * @param message
	 *            log message
	 * @param throwable
	 *            {@link Throwable} to be logged
	 */
	public void debug(Object message, Throwable throwable);

	/**
	 * Log message with level INFO.
	 * 
	 * @param message
	 *            log message.
	 */
	public void info(Object message);

	/**
	 * Log message with level INFO.
	 * 
	 * @param message
	 *            log message
	 * @param throwable
	 *            {@link Throwable} to be logged
	 */
	public void info(Object message, Throwable throwable);

	/**
	 * Log message with level WARN.
	 * 
	 * @param message
	 *            log message.
	 */
	public void warn(Object message);

	/**
	 * Log message with level WARN.
	 * 
	 * @param message
	 *            log message
	 * @param throwable
	 *            {@link Throwable} to be logged
	 */
	public void warn(Object message, Throwable throwable);

	/**
	 * Log message with level ERROR.
	 * 
	 * @param message
	 *            log message.
	 */
	public void error(Object message);

	/**
	 * Log message with level ERROR.
	 * 
	 * @param message
	 *            log message
	 * @param throwable
	 *            {@link Throwable} to be logged
	 */
	public void error(Object message, Throwable throwable);

}