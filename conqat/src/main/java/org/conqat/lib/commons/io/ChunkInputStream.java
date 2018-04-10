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

import java.io.InputStream;
import java.util.List;

import org.conqat.lib.commons.assertion.CCSMPre;
import org.conqat.lib.commons.collections.CollectionUtils;

/**
 * A stream that provides functionality for reading from a list of byte arrays,
 * also called chunks.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 50463 $
 * @ConQAT.Rating GREEN Hash: E7283E6BCDF5B155AE6657AD85A12BFC
 */
public class ChunkInputStream extends InputStream {
	/** The internal chunks to read from. */
	private final List<byte[]> chunks;

	/** The chunk's index that is to be read after the current chunk. */
	private int nextChunkIndex;

	/** The remaining number of bytes to read. */
	private int remaining;

	/** The chunk from which is currently read. */
	private byte[] currentChunk;

	/** The offset in the current chunk to read from. */
	private int currentOffset;

	/**
	 * Creates a new ChunkInputStream with the given chunks.
	 */
	public ChunkInputStream(List<byte[]> chunks) {
		this(chunks, getLastChunkSize(chunks));
	}

	/**
	 * Retrieves the last chunk's size or zero if the given chunk list is empty.
	 * 
	 * @param chunks
	 *            the chunks
	 * @return the last chunk's size
	 */
	private static int getLastChunkSize(List<byte[]> chunks) {
		CCSMPre.isNotNull(chunks);
		if (chunks.isEmpty()) {
			return 0;
		}
		return CollectionUtils.getLast(chunks).length;
	}

	/**
	 * Creates a new ChunkInputStream with the given chunks. If the input chunks
	 * are the output of a ChunkOutputStream, the last chunk is probably smaller
	 * than its actual byte array size. Therefore the lastChunkSize can be
	 * specified additionally.
	 * 
	 * @param chunks
	 *            the chunks to read from, the list must not contain null values
	 *            or empty arrays
	 * @param lastChunkSize
	 *            the real size of the last chunk
	 */
	public ChunkInputStream(List<byte[]> chunks, int lastChunkSize) {
		CCSMPre.isNotNull(chunks);

		this.chunks = chunks;
		this.currentChunk = null;

		for (byte[] chunk : this.chunks) {
			remaining += chunk.length;
		}

		if (!this.chunks.isEmpty()) {
			this.currentChunk = this.chunks.get(0);
			byte[] lastChunk = CollectionUtils.getLast(this.chunks);
			CCSMPre.isTrue(lastChunkSize > 0
					&& lastChunkSize <= lastChunk.length,
					"lastChunkSize must be in range ]0,lastChunk.length]");

			remaining -= lastChunk.length - lastChunkSize;
		}

		nextChunkIndex = 1;
		currentOffset = 0;
	}

	/** {@inheritDoc} */
	@Override
	public int read() {
		updateChunk();
		if (remaining <= 0) {
			return -1;
		}

		remaining--;
		return ByteArrayUtils.unsignedByte(currentChunk[currentOffset++]);
	}

	/** {@inheritDoc} */
	@Override
	public int read(byte[] b, int offset, int length) {
		CCSMPre.isNotNull(b);
		CCSMPre.isFalse(offset < 0 || length < 0 || length > b.length - offset,
				"invalid offset/length");

		if (length == 0) {
			return 0;
		}

		if (remaining <= 0) {
			return -1;
		}

		int alreadyRead = 0;
		while (remaining > 0 && alreadyRead < length) {
			updateChunk();

			// calculate how many bytes must/can be read from the current chunk
			int readNow = Math.min(currentChunk.length - currentOffset, length
					- alreadyRead);

			System.arraycopy(currentChunk, currentOffset, b, offset
					+ alreadyRead, readNow);

			currentOffset += readNow;
			alreadyRead += readNow;
			remaining -= readNow;
		}
		return alreadyRead;
	}

	/** {@inheritDoc} */
	@Override
	public int available() {
		return remaining;
	}

	/** {@inheritDoc} */
	@Override
	public void close() {
		// do nothing
	}

	/**
	 * Updates the current chunk. If the current offset is at the end of the
	 * current chunk, switch to the next one. Sets the current chunk to null if
	 * there is no next chunk.
	 */
	private void updateChunk() {
		if (currentChunk != null && currentOffset == currentChunk.length) {
			if (nextChunkIndex < chunks.size()) {
				currentChunk = chunks.get(nextChunkIndex++);
				currentOffset = 0;
			} else {
				currentChunk = null;
			}
		}
	}
}
