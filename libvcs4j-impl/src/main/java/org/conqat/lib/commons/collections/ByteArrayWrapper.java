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
package org.conqat.lib.commons.collections;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;

import org.conqat.lib.commons.string.StringUtils;

/**
 * A wrapper class around a byte array that supports a clean implementation of
 * {@link #hashCode()} and {@link #equals(Object)}, so byte arrays can be used,
 * e.g., in a HashMap. The class is immutable. Custom (de)serialization is
 * provided to make this efficient to use in storage or RMI scenarios.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 973676B01AB2721FC15549F0A7258AD9
 */
public class ByteArrayWrapper implements Serializable,
		Comparable<ByteArrayWrapper> {

	/** Version used for serialization. */
	private static final long serialVersionUID = 1;

	/** The wrapped array. */
	protected byte[] array;

	/** Constructor. */
	public ByteArrayWrapper(byte[] array) {
		this.array = array.clone();
	}

	/** Returns a copy of the internal byte representation. */
	public byte[] getBytes() {
		return array.clone();
	}

	/** {@inheritDoc} */
	@Override
	public int hashCode() {
		return Arrays.hashCode(array);
	}

	/** {@inheritDoc} */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ByteArrayWrapper) {
			return Arrays.equals(array, ((ByteArrayWrapper) obj).array);
		}
		if (obj instanceof byte[]) {
			return Arrays.equals(array, ((byte[]) obj));
		}
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return StringUtils.encodeAsHex(array);
	}

	/** {@inheritDoc} */
	@Override
	public int compareTo(ByteArrayWrapper o) {
		if (o == null) {
			return -1;
		}

		int lengthDelta = array.length - o.array.length;
		if (lengthDelta != 0) {
			return lengthDelta;
		}

		for (int i = 0; i < array.length; ++i) {
			int delta = array[i] - o.array[i];
			if (delta != 0) {
				return delta;
			}
		}

		return 0;
	}

	/** Custom serialization. */
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(array.length);
		out.write(array);
	}

	/** Custom deserialization. */
	private void readObject(ObjectInputStream in) throws IOException {
		int size = in.readInt();
		array = new byte[size];
		int pos = 0;
		while (pos < size) {
			pos += in.read(array, pos, size - pos);
		}
	}

	/** Comparator for {@link ByteArrayWrapper}. */
	public static class Comparator implements
			java.util.Comparator<ByteArrayWrapper>, Serializable {

		/** Serial version ID. */
		private static final long serialVersionUID = 1;

		/** {@inheritDoc} */
		@Override
		public int compare(ByteArrayWrapper o1, ByteArrayWrapper o2) {
			return o1.compareTo(o2);
		}
	}
}