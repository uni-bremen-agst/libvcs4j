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

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.conqat.lib.commons.assertion.CCSMPre;
import org.conqat.lib.commons.assertion.PreconditionException;
import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.collections.UnmodifiableList;
import org.conqat.lib.commons.string.StringUtils;

/**
 * This class enables multiplexing of output streams. It can be e.g. used to
 * output content to multiple files.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 8B46FEFFEEEE3178787821E0FAC2A755
 */
public class MultiplexedOutputStream extends OutputStream {

	/** The underlying output streams. */
	private final OutputStream[] streams;

	/**
	 * Create new multiplexed output streams.
	 * 
	 * @param streams
	 *            any number of output streams.
	 */
	public MultiplexedOutputStream(OutputStream... streams) {
		this.streams = streams;
	}

	/**
	 * Forwards close operation to all underlying output streams.
	 * 
	 * @throws MultiIOException
	 *             if one or more of the underlying streams raised an exception
	 */
	@Override
	public void close() throws MultiIOException {
		List<IOException> exceptions = new ArrayList<IOException>();
		for (OutputStream stream : streams) {
			try {
				stream.close();
			} catch (IOException e) {
				exceptions.add(e);
			}
		}
		checkExceptions(exceptions);
	}

	/**
	 * Forwards flush operation to all underlying output streams.
	 * 
	 * @throws MultiIOException
	 *             if one or more of the underlying streams raised an exception
	 */
	@Override
	public void flush() throws MultiIOException {
		List<IOException> exceptions = new ArrayList<IOException>();
		for (OutputStream stream : streams) {
			try {
				stream.flush();
			} catch (IOException e) {
				exceptions.add(e);
			}
		}
		checkExceptions(exceptions);
	}

	/**
	 * Forwards write operation to all underlying output streams.
	 * 
	 * @throws MultiIOException
	 *             if one or more of the underlying streams raised an exception
	 */
	@Override
	public void write(int b) throws MultiIOException {
		List<IOException> exceptions = new ArrayList<IOException>();
		for (OutputStream stream : streams) {
			try {
				stream.write(b);
			} catch (IOException e) {
				exceptions.add(e);
			}
		}
		checkExceptions(exceptions);
	}

	/**
	 * Raises an {@link MultiIOException} if the provided collection is not
	 * empty.
	 */
	private void checkExceptions(Collection<IOException> exceptions)
			throws MultiIOException {
		if (!exceptions.isEmpty()) {
			throw new MultiIOException(exceptions);
		}
	}

	/** Exception class that encapsulates multiple {@link IOException}s. */
	public static class MultiIOException extends IOException {

		/** Serial version UID. */
		private static final long serialVersionUID = 1;

		/** The exceptions. */
		private final List<IOException> exceptions = new ArrayList<IOException>();

		/**
		 * Create exception
		 * 
		 * @throws PreconditionException
		 *             if provided collection is empty.
		 */
		public MultiIOException(Collection<? extends IOException> exceptions) {
			CCSMPre.isFalse(exceptions.isEmpty(),
					"Must have at least one exception.");
			this.exceptions.addAll(exceptions);
		}

		/** Returns messages of all exceptions. */
		@Override
		public String getMessage() {
			StringBuilder result = new StringBuilder();
			for (IOException ex : exceptions) {
				result.append(ex.getMessage());
				result.append(StringUtils.CR);
			}
			return result.toString();
		}

		/** Get exceptions. */
		public UnmodifiableList<IOException> getExceptions() {
			return CollectionUtils.asUnmodifiable(exceptions);
		}
	}
}