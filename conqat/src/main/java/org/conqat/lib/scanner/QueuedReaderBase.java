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
package org.conqat.lib.scanner;

import java.io.FilterReader;
import java.io.IOException;
import java.io.Reader;
import java.util.LinkedList;
import java.util.Queue;

/**
 * For some reader implementation it is desirable to return multiple characters
 * for a single call to {@link #read()}. This base class makes it easy to
 * implement such behavior. This is achieved by pushing the characters to a
 * queue. A call to {@link #read()} first returns characters from the queue
 * until it's empty before it forwards to the actual reading method
 * {@link #internalRead()}.
 * 
 * @author Florian Deissenboeck
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 58EAC1D6F73F2994B6AFCE2D3262DAD2
 */
/* package */abstract class QueuedReaderBase extends FilterReader {

	/** The queue to store characters. */
	private final Queue<Integer> queue = new LinkedList<Integer>();

	/** Create new reader. */
	protected QueuedReaderBase(Reader in) {
		super(in);
	}

	/**
	 * If queue is non-empty return its head, otherwise forward to
	 * {@link #internalRead()}.
	 */
	@Override
	public int read() throws IOException {
		if (!queue.isEmpty()) {
			return queue.poll();
		}
		return internalRead();
	}

	/** Put a character to the queue. */
	protected void pushCharacter(int character) {
		queue.add(character);
	}

	/**
	 * This method performs the actual reading.
	 */
	protected abstract int internalRead() throws IOException;

}