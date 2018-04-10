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

import static org.conqat.lib.commons.string.StringUtils.obtainStackTrace;

import java.io.OutputStream;
import java.io.PrintWriter;

/**
 * Simple logger that writes all messages to a stream or print writer.
 * 
 * @author deissenb
 * @author $Author: poehlmann $
 * @version $Rev: 50320 $
 * @ConQAT.Rating YELLOW Hash: 0BF149E3D8DEC0B1BC4FA25E6B98D682
 */
public class SimpleLogger implements ILogger {

	/** The writer used for output. */
	private final PrintWriter writer;

	/** Create logger that logs to {@link System#out}. */
	public SimpleLogger() {
		this(System.out);
	}

	/** Create logger that logs to a stream. */
	public SimpleLogger(OutputStream stream) {
		writer = new PrintWriter(stream, true);
	}

	/** Create logger that logs to a writer. */
	public SimpleLogger(PrintWriter writer) {
		this.writer = writer;
	}

	/** Logs a message to the print writer. */
	protected void log(String prefix, Object message, Throwable throwable) {
		writer.print(prefix + ": " + message);
		if (throwable != null) {
			writer.print(": " + obtainStackTrace(throwable));
		}
		writer.println();
	}

	/** {@inheritDoc} */
	@Override
	public void debug(Object message) {
		log("DEBUG", message, null);
	}

	/** {@inheritDoc} */
	@Override
	public void debug(Object message, Throwable throwable) {
		log("DEBUG", message, throwable);
	}

	/** {@inheritDoc} */
	@Override
	public void error(Object message) {
		log("ERROR", message, null);
	}

	/** {@inheritDoc} */
	@Override
	public void error(Object message, Throwable throwable) {
		log("ERROR", message, throwable);
	}

	/** {@inheritDoc} */
	@Override
	public void info(Object message) {
		log("INFO", message, null);
	}

	/** {@inheritDoc} */
	@Override
	public void info(Object message, Throwable throwable) {
		log("INFO", message, throwable);
	}

	/** {@inheritDoc} */
	@Override
	public void warn(Object message) {
		log("WARN", message, null);
	}

	/** {@inheritDoc} */
	@Override
	public void warn(Object message, Throwable throwable) {
		log("WARN", message, throwable);
	}

}