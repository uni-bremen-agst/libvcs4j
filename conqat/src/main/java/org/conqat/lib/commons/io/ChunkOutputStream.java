/*-------------------------------------------------------------------------+
|                                                                          |
| Copyright 2005-2011 the ConQAT Project                                   |
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

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.conqat.lib.commons.assertion.CCSMPre;

/**
 * A stream that provides functionality for writing to a list of byte arrays,
 * also called chunks.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 50463 $
 * @ConQAT.Rating GREEN Hash: 97D3AF1848B61508FF26FD53CB1BCEC3
 */
public class ChunkOutputStream extends OutputStream {

	/** The chunk size to use. */
	private int chunkSize;

	/** The list of written chunks. */
	private List<byte[]> chunks;

	/** The chunk to which is currently written. */
	private byte[] currentChunk;

	/** The offset in the current chunk to write to. */
	private int currentOffset;

	/**
	 * Creates a new ChunkOutputStream with a chunk size of pow(2,20).
	 */
	public ChunkOutputStream() {
		this(1 << 20);
	}

	/**
	 * Creates a new ChunkOutputStream with the given chunk size.
	 * 
	 * @param chunkSize
	 *            the chunk size
	 */
	public ChunkOutputStream(int chunkSize) {
		CCSMPre.isTrue(chunkSize >= 1, "chunkSize must be >= 1, is "
				+ chunkSize);
		this.chunkSize = chunkSize;
		this.currentOffset = 0;

		chunks = new ArrayList<>();
	}

	/** Returns chunk size. */
	public int getChunkSize() {
		return chunkSize;
	}

	/** {@inheritDoc} */
	@Override
	public void write(int b) {
		updateChunk();
		currentChunk[currentOffset++] = (byte) b;
	}

	/** {@inheritDoc} */
	@Override
	public void write(byte[] b, int offset, int length) {
		int alreadyWritten = 0;
		while (alreadyWritten < length) {
			updateChunk();

			int writeNow = Math.min(length - alreadyWritten, chunkSize
					- currentOffset);
			System.arraycopy(b, offset + alreadyWritten, currentChunk,
					currentOffset, writeNow);

			currentOffset += writeNow;
			alreadyWritten += writeNow;
		}
	}

	/**
	 * Updates the current chunk. If the current offset is at the end of the
	 * current chunk, switch to the next one.
	 */
	private void updateChunk() {
		if (currentOffset == chunkSize || chunks.isEmpty()) {
			currentChunk = new byte[chunkSize];
			chunks.add(currentChunk);
			currentOffset = 0;
		}
	}

	/** Returns chunks. */
	public List<byte[]> getChunks() {
		return chunks;
	}

	/** Returns the size of the last chunk. */
	public int getLastChunkSize() {
		return currentOffset;
	}

	/** {@inheritDoc} */
	@Override
	public void close() {
		// do nothing
	}
}
