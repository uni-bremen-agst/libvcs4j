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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.conqat.lib.commons.assertion.CCSMAssert;
import org.conqat.lib.commons.assertion.CCSMPre;
import org.conqat.lib.commons.filesystem.FileSystemUtils;
import org.conqat.lib.commons.string.StringUtils;

/**
 * Utility methods for dealing with raw byte arrays. This is located in the I/O
 * package, as the typical application for these methods is binary I/O on byte
 * array level.
 * 
 * @author $Author: heinemann $
 * @version $Rev: 50901 $
 * @ConQAT.Rating GREEN Hash: E331857282CDE9B426A4D841B1A03420
 */
public class ByteArrayUtils {
	/** The number of bytes used to encode an integer as a byte array. */
	public static final int INT_BYTE_ARRAY_LENGTH = 4;

	/** The number of bytes used to encode a double as a byte array. */
	public static final int DOUBLE_BYTE_ARRAY_LENGTH = 8;

	/** The number of bytes used to encode a long as a byte array. */
	public static final int LONG_BYTE_ARRAY_LENGTH = 8;

	/** Converts an integer value to a byte array. */
	public static byte[] intToByteArray(int value) {
		byte[] bytes = new byte[INT_BYTE_ARRAY_LENGTH];
		bytes[0] = (byte) (value >> 24);
		bytes[1] = (byte) (value >> 16);
		bytes[2] = (byte) (value >> 8);
		bytes[3] = (byte) (value);
		return bytes;
	}

	/** Converts a double value to a byte array. */
	public static byte[] doubleToByteArray(double value) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(bos);
		try {
			dos.writeDouble(value);
			dos.close();
		} catch (IOException e) {
			throw new AssertionError("Can not happen as we work in memory: "
					+ e.getMessage());
		}
		return bos.toByteArray();
	}

	/** Converts a long value to a byte array. */
	public static byte[] longToByteArray(long value) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(bos);
		try {
			dos.writeLong(value);
			dos.close();
		} catch (IOException e) {
			throw new AssertionError("Can not happen as we work in memory: "
					+ e.getMessage());
		}
		return bos.toByteArray();
	}

	/** Converts a byte array to an integer value. */
	public static int byteArrayToInt(byte[] bytes) {
		CCSMPre.isTrue(bytes.length == INT_BYTE_ARRAY_LENGTH,
				"bytes.length must be 4");
		int value = 0;
		value |= unsignedByte(bytes[0]) << 24;
		value |= unsignedByte(bytes[1]) << 16;
		value |= unsignedByte(bytes[2]) << 8;
		value |= unsignedByte(bytes[3]);
		return value;
	}

	/**
	 * Converts a byte array to a double value.
	 * 
	 * @throws IOException
	 *             if the array is too short (less than
	 *             {@value #DOUBLE_BYTE_ARRAY_LENGTH} bytes) or the bytes can
	 *             not be converted to a double. Overall, this method is only
	 *             guaranteed to work if the input array was created by
	 *             {@link #doubleToByteArray(double)}.
	 */
	public static double byteArrayToDouble(byte[] value) throws IOException {
		ByteArrayInputStream bis = new ByteArrayInputStream(value);
		DataInputStream dis = new DataInputStream(bis);
		try {
			return dis.readDouble();
		} finally {
			FileSystemUtils.close(dis);
		}
	}

	/**
	 * Converts a byte array to a long value.
	 * 
	 * @throws IOException
	 *             if the array is too short (less than
	 *             {@value #LONG_BYTE_ARRAY_LENGTH} bytes) or the bytes can not
	 *             be converted to a long. Overall, this method is only
	 *             guaranteed to work if the input array was created by
	 *             {@link #longToByteArray(long)}.
	 */
	public static long byteArrayToLong(byte[] value) throws IOException {
		ByteArrayInputStream bis = new ByteArrayInputStream(value);
		DataInputStream dis = new DataInputStream(bis);
		try {
			return dis.readLong();
		} finally {
			FileSystemUtils.close(dis);
		}
	}

	/**
	 * Decompresses a single byte[] using GZIP. A null input array will cause
	 * this method to return null.
	 * 
	 * @throws IOException
	 *             if the input array is not valid GZIP compressed data (as
	 *             created by {@link #compress(byte[])}).
	 */
	public static byte[] decompress(byte[] value) throws IOException {
		if (value == null) {
			return null;
		}

		ByteArrayOutputStream bos = new ByteArrayOutputStream(value.length);
		ByteArrayInputStream bis = new ByteArrayInputStream(value);
		GZIPInputStream gzis = new GZIPInputStream(bis);

		FileSystemUtils.copy(gzis, bos);

		// it does not matter if we close in case of exceptions, as these are
		// in-memory resources
		gzis.close();
		bos.close();

		return bos.toByteArray();
	}

	/**
	 * Compresses a single byte[] using GZIP. A null input array will cause this
	 * method to return null.
	 */
	public static byte[] compress(byte[] value) {
		if (value == null) {
			return null;
		}

		ByteArrayOutputStream bos = new ByteArrayOutputStream(value.length);
		try {
			GZIPOutputStream gzos = new GZIPOutputStream(bos);
			gzos.write(value);

			// it does not matter if we close in case of exceptions, as this is
			// an in-memory resource
			gzos.close();
		} catch (IOException e) {
			throw new AssertionError("Can not happen as we work in memory: "
					+ e.getMessage());
		}

		return bos.toByteArray();
	}

	/** Returns whether the prefix is a prefix of the given key. */
	public static boolean isPrefix(byte[] prefix, byte[] key) {
		if (key.length < prefix.length) {
			return false;
		}
		for (int i = 0; i < prefix.length; ++i) {
			if (prefix[i] != key[i]) {
				return false;
			}
		}
		return true;
	}

	/** Returns true if a1 is (lexicographically) less than a2. */
	public static boolean isLess(byte[] a1, byte[] a2, boolean resultIfEqual) {
		int limit = Math.min(a1.length, a2.length);
		for (int i = 0; i < limit; ++i) {
			if (unsignedByte(a1[i]) < unsignedByte(a2[i])) {
				return true;
			}
			if (unsignedByte(a1[i]) > unsignedByte(a2[i])) {
				return false;
			}
		}

		if (a1.length < a2.length) {
			return true;
		}
		if (a1.length > a2.length) {
			return false;
		}

		return resultIfEqual;
	}

	/** Returns the unsigned byte interpretation of the parameter. */
	public static int unsignedByte(byte b) {
		return b & 0xff;
	}

	/** Returns the concatenation of the given arrays. */
	public static byte[] concat(byte[]... arrays) {
		return concat(Arrays.asList(arrays));
	}

	/** Returns the concatenation of the given arrays. */
	public static byte[] concat(Iterable<byte[]> arrays) {
		int length = 0;
		for (byte[] array : arrays) {
			length += array.length;
		}

		byte[] result = new byte[length];
		int start = 0;
		for (byte[] array : arrays) {
			System.arraycopy(array, 0, result, start, array.length);
			start += array.length;
		}
		return result;
	}

	/**
	 * Creates a hex dump of the provided bytes. This is similar to output from
	 * hexdump tools and primarily used for debugging. The output string will
	 * contain in each line 16 bytes of data first printed as hex numbers and
	 * then as a string interpretation. Each line is also prefixed with an
	 * offset.
	 */
	public static String hexDump(byte[] data) {
		return hexDump(data, 16);
	}

	/**
	 * Creates a hex dump of the provided bytes. This is similar to output from
	 * hexdump tools and primarily used for debugging. The output string will
	 * contain in each line <code>width</code> bytes of data first printed as
	 * hex numbers and then as a string interpretation. Each line is also
	 * prefixed with an offset.
	 */
	public static String hexDump(byte[] data, int width) {
		CCSMAssert.isTrue(width >= 1, "Width must be positive!");

		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < data.length; i += width) {
			hexDumpAppendLine(data, i, Math.min(data.length, i + width), width,
					builder);
		}
		return builder.toString();
	}

	/**
	 * Appends a single line to the hex dump for {@link #hexDump(byte[], int)}.
	 * The start is inclusive, the end is exclusive.
	 */
	private static void hexDumpAppendLine(byte[] data, int startOffset,
			int endOffset, int width, StringBuilder builder) {
		builder.append(String.format("%06d: ", startOffset));
		for (int i = startOffset; i < endOffset; ++i) {
			builder.append(String.format("%02x ", data[i]));
		}

		if (endOffset - startOffset < width) {
			builder.append(StringUtils.fillString(
					(width - (endOffset - startOffset)) * 3,
					StringUtils.SPACE_CHAR));
		}

		builder.append(StringUtils.SPACE_CHAR);
		for (int i = startOffset; i < endOffset; ++i) {
			boolean isInPrintableAsciiRange = (33 <= data[i] && data[i] <= 126);
			if (isInPrintableAsciiRange) {
				builder.append((char) data[i]);
			} else {
				builder.append('.');
			}
		}

		builder.append(StringUtils.CR);
	}

	/**
	 * Returns whether the given bytes start with the <a
	 * href="http://en.wikipedia.org/wiki/Zip_%28file_format%29#File_headers"
	 * >magic bytes</a> that mark a ZIP file.
	 */
	public static boolean startsWithZipMagicBytes(byte[] data) {
		return isPrefix(new byte[] { 0x50, 0x4b, 0x03, 0x04 }, data);
	}
}
