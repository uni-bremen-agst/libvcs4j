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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.Serializable;

import org.conqat.lib.commons.assertion.CCSMAssert;
import org.conqat.lib.commons.filesystem.FileSystemUtils;

/**
 * Utility methods for serialization.
 * 
 * @author hummelb
 * @author $Author: heinemann $
 * @version $Rev: 43503 $
 * @ConQAT.Rating GREEN Hash: 5AEB9CE739E9E30378AACF24C3FB340E
 */
public class SerializationUtils {

	/** Serializes an object to byte array */
	public static byte[] serializeToByteArray(Serializable object)
			throws IOException {
		ByteArrayOutputStream outBuffer = new ByteArrayOutputStream();
		ObjectOutputStream out = new ObjectOutputStream(outBuffer);
		out.writeObject(object);
		// no need to put this in finally, as we do not block file handles.
		// Let the GC do the work
		out.close();
		return outBuffer.toByteArray();
	}

	/**
	 * Deserializes an object from byte array using the default class loader
	 * used in {@link ObjectInputStream}, i.e. if there is a method on the
	 * current call stack whose class was loaded via a custom class loader, this
	 * class loader is used. Otherwise the default class loader is used. Also
	 * see the documentation of {@link ObjectInputStream}s resolveClass()
	 * method. Passes null on, i.e. a null byte array leads to a null result.
	 */
	public static Serializable deserializeFromByteArray(byte[] bytes)
			throws IOException, ClassNotFoundException {
		return deserializeFromByteArray(bytes, null);
	}

	/**
	 * Deserializes an object from byte array using a custom/given class loader.
	 * Passes null on, i.e. a null byte array leads to a null result.
	 * 
	 * @param classLoader
	 *            the class loader used. If this is null, the default behavior
	 *            of {@link ObjectInputStream} is used, i.e. if there is a
	 *            method on the current call stack whose class was loaded via a
	 *            custom class loader, this class loader is used. Otherwise the
	 *            default class loader is used. Also see the documentation of
	 *            {@link ObjectInputStream}s resolveClass() method.
	 */
	public static Serializable deserializeFromByteArray(byte[] bytes,
			final ClassLoader classLoader) throws IOException,
			ClassNotFoundException {

		if (bytes == null) {
			return null;
		}

		ObjectInputStream in;
		if (classLoader == null) {
			in = new ObjectInputStream(new ByteArrayInputStream(bytes));
		} else {
			// it seems that the only solution to use a custom class loader is
			// to override a method in the ObjectInputStream class. This is
			// confirmed by
			// http://blogs.sun.com/adventures/entry/desrializing_objects_custom_class_loaders
			// and seems plausible as the corresponding method is protected.

			in = new ObjectInputStream(new ByteArrayInputStream(bytes)) {

				/** {@inheritDoc} */
				@Override
				protected Class<?> resolveClass(ObjectStreamClass desc)
						throws IOException, ClassNotFoundException {
					try {
						return Class
								.forName(desc.getName(), false, classLoader);
					} catch (ClassNotFoundException e) {
						// as a fallback we pass this to the super method, as
						// for example primitive values are treated there.
						return super.resolveClass(desc);
					}
				}
			};
		}

		try {
			return (Serializable) in.readObject();
		} finally {
			FileSystemUtils.close(in);
		}
	}

	/**
	 * Returns a copy of the given object obtained by serialization and
	 * deserialization in memory.
	 * 
	 * @param classLoader
	 *            the class loader used. If this is null, the default behavior
	 *            of {@link ObjectInputStream} is used, i.e. if there is a
	 *            method on the current call stack whose class was loaded via a
	 *            custom class loader, this class loader is used. Otherwise the
	 *            default class loader is used. Also see the documentation of
	 *            {@link ObjectInputStream}s resolveClass() method.
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Serializable> T cloneBySerialization(T t,
			ClassLoader classLoader) {
		try {
			return (T) deserializeFromByteArray(serializeToByteArray(t),
					classLoader);
		} catch (IOException e) {
			CCSMAssert
					.fail("This should be impossible as we are working in memory!");
			return null;
		} catch (ClassNotFoundException e) {
			CCSMAssert
					.fail("This should be impossible as we just had the object available!");
			return null;
		}
	}

	/**
	 * Inserts an int value to the given position in the byte array. The storage
	 * will require 4 bytes in big endian byte order.
	 */
	public static void insertInt(int i, byte[] bytes, int position) {
		bytes[position++] = (byte) (i >> 24 & 0xff);
		bytes[position++] = (byte) (i >> 16 & 0xff);
		bytes[position++] = (byte) (i >> 8 & 0xff);
		bytes[position] = (byte) (i & 0xff);
	}

	/**
	 * Extracts an int value from the given array position (4 bytes in big
	 * endian). This is the counter part to {@link #insertInt(int, byte[], int)}
	 * .
	 */
	public static int extractInt(byte[] bytes, int position) {
		int result = bytes[position++] & 0xff;
		result <<= 8;
		result |= bytes[position++] & 0xff;
		result <<= 8;
		result |= bytes[position++] & 0xff;
		result <<= 8;
		result |= bytes[position++] & 0xff;
		return result;
	}

}