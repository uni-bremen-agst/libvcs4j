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

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

/**
 * This as base class for scanners that makes it quite easy to return multiple
 * tokens from a single rule match. This is achieved by pushing the tokens to a
 * queue. A call to {@link #getNextToken()} first returns tokens from the queue
 * until its empty before it forwards to the actual scanning method
 * {@link #internalScan()}.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 47387 $
 * @ConQAT.Rating GREEN Hash: 55ADE57694322D518D7EFD0AE6B8710D
 */
/* package */abstract class QueuedScannerBase<E extends IToken> implements
		ILenientScanner {

	/** The queue to store tokens. */
	private final Queue<E> queue = new LinkedList<E>();

	/** See class comment for details. */
	@Override
	public E getNextToken() throws IOException {
		if (!queue.isEmpty()) {
			return queue.poll();
		}
		return internalScan();
	}

	/** Put a token in the queue. */
	protected void pushToken(E token) {
		queue.add(token);
	}

	/** Put multiple tokens in the queue. */
	@SuppressWarnings("unchecked")
	protected void pushTokens(E... tokens) {
		queue.addAll(Arrays.asList(tokens));
	}

	/** Clear queue. */
	protected void clearQueue() {
		queue.clear();
	}

	/**
	 * This method performs the actual scanning and should be implemented by the
	 * generated scanner.
	 */
	protected abstract E internalScan() throws IOException;

}