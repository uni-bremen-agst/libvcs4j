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
package org.conqat.lib.commons.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * A thread to drain an input stream. Storing the content is optional.
 * 
 * If an exception occurs during draining, the exceptions message is appended to
 * the content and the exception is also made available via
 * {@link #getException()}. So if the caller wants to ensure that the content is
 * really complete, he not only has to wait for the end of the thread via join,
 * but also check this method.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 6D25A11127AD1DC9553A1536BDC89BC0
 */
public class StreamReaderThread extends Thread {

	/** Stream the reader reads from. */
	private final InputStream input;

	/** Content read from the stream. */
	private final StringBuilder content = new StringBuilder();

	/** Whether to store content or not. */
	private final boolean storeContent;

	/** Exception that occurred (or null). */
	private IOException exception;

	/**
	 * Create a new reader that reads the content of this stream in its own
	 * thread. => This call is non-blocking.
	 * <p>
	 * This constructor causes the content to be stored.
	 * 
	 * @param input
	 *            Stream to read from. This stream is not automatically closed,
	 *            but must be closed by the caller (if this is intended).
	 * 
	 */
	public StreamReaderThread(InputStream input) {
		this(input, true);
	}

	/**
	 * Create a new reader that reads the content of this stream in its own
	 * thread. => This call is non-blocking
	 * 
	 * @param input
	 *            Stream to read from. This stream is not automatically closed,
	 *            but must be closed by the caller (if this is intended).
	 * 
	 */
	public StreamReaderThread(InputStream input, boolean storeContent) {
		this.input = input;
		this.storeContent = storeContent;
		start();
	}

	/**
	 * Reads content from the stream as long as the stream is not empty.
	 */
	@Override
	public synchronized void run() {
		BufferedReader reader = new BufferedReader(new InputStreamReader(input));
		char[] buffer = new char[1024];

		try {
			int read = 0;
			while ((read = reader.read(buffer)) != -1) {
				if (storeContent) {
					content.append(buffer, 0, read);
				}
			}
		} catch (IOException e) {
			exception = e;
			content.append(e);
		}
	}

	/** Returns the content read from the stream. */
	public synchronized String getContent() {
		return content.toString();
	}

	/**
	 * If everything went ok during reading from the stream, this returns null.
	 * Otherwise the exception can be found here.
	 */
	public IOException getException() {
		return exception;
	}
}