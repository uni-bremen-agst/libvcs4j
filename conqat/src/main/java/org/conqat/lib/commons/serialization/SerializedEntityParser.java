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
package org.conqat.lib.commons.serialization;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectStreamConstants;

import org.conqat.lib.commons.assertion.CCSMAssert;
import org.conqat.lib.commons.serialization.classes.SerializedClass;
import org.conqat.lib.commons.serialization.classes.SerializedProxyClass;
import org.conqat.lib.commons.serialization.objects.SerializedArrayObject;
import org.conqat.lib.commons.serialization.objects.SerializedClassObject;
import org.conqat.lib.commons.serialization.objects.SerializedEnumLiteral;
import org.conqat.lib.commons.serialization.objects.SerializedObject;
import org.conqat.lib.commons.serialization.objects.SerializedStringObject;

/**
 * Parses serialized entities (objects and classes).
 * 
 * @author $Author: poehlmann $
 * @version $Rev: 48713 $
 * @ConQAT.Rating GREEN Hash: 62D633892492DE70CC9B60D0449B5566
 */
public class SerializedEntityParser {

	/** The stream to read from. */
	private final DataInputStream din;

	/** The pool we use to store read objects into. */
	private final SerializedEntityPool pool = new SerializedEntityPool();

	/** Constructor. */
	private SerializedEntityParser(DataInputStream din) {
		CCSMAssert.isTrue(din.markSupported(),
				"Our parser requires an input stream that supports marking! "
						+ "However, all calling methods should ensure this.");

		this.din = din;
	}

	/** Performs the actual parsing and returns the pool of parsed objects. */
	private SerializedEntityPool parse() throws IOException {
		short magic = din.readShort();
		if (magic != ObjectStreamConstants.STREAM_MAGIC) {
			throw new IOException("Magic header not found!");
		}

		short version = din.readShort();
		if (version != ObjectStreamConstants.STREAM_VERSION) {
			throw new IOException("Unexpected stream version!");
		}

		try {
			while (true) {
				pool.registerRootHandle(parseContent());
			}
		} catch (EOFException e) {
			// this exception is the intended mechanism of communicating the end
			// of the stream. So all is ok.
		}

		return pool;
	}

	/**
	 * Parses the <code>content</code> part of the stream. Returns a handle for
	 * the created object.
	 */
	public int parseContent() throws IOException {
		byte next = din.readByte();
		switch (next) {
		// these are the options for "object"
		case ObjectStreamConstants.TC_OBJECT:
			return parseObject();
		case ObjectStreamConstants.TC_CLASS:
			return parseClass();
		case ObjectStreamConstants.TC_ARRAY:
			return parseArray();
		case ObjectStreamConstants.TC_STRING:
			return parseString(false);
		case ObjectStreamConstants.TC_LONGSTRING:
			return parseString(true);
		case ObjectStreamConstants.TC_ENUM:
			return parseEnum();
		case ObjectStreamConstants.TC_CLASSDESC:
			return parsePlainClassDesc();
		case ObjectStreamConstants.TC_PROXYCLASSDESC:
			return parseProxyClassDesc();
		case ObjectStreamConstants.TC_REFERENCE:
			return readHandle();
		case ObjectStreamConstants.TC_NULL:
			return SerializedEntityPool.NULL_HANDLE;
		case ObjectStreamConstants.TC_EXCEPTION:
			// not sure how to create them; maybe only relevant in RMI?
			throw new IOException("Top-level exceptions are not supported!");
		case ObjectStreamConstants.TC_RESET:
			pool.reset();
			return SerializedEntityPool.NULL_HANDLE;
		case ObjectStreamConstants.TC_BLOCKDATA:
		case ObjectStreamConstants.TC_BLOCKDATALONG:
			// although the grammar allows them at this point, we could find no
			// way block data can happen top-level
			throw new IOException("Unexpected block data at top-level!");
		default:
			throw new IOException("Unexpected value for next: " + next);
		}
	}

	/**
	 * Parses the <code>newClass</code> part of the stream. Returns the handle
	 * of the object.
	 */
	private int parseClass() throws IOException {
		return new SerializedClassObject(pool, parseClassDesc()).getHandle();
	}

	/**
	 * Parses the <code>newString</code> of the stream. Argument determines
	 * whether to parse a long string. Returns the handle of the object.
	 */
	private int parseString(boolean longString) throws IOException {
		return new SerializedStringObject(din, pool, longString).getHandle();
	}

	/**
	 * Parses the <code>newObject</code> part of the stream. Returns the handle
	 * of the object.
	 */
	private int parseObject() throws IOException {
		return new SerializedObject(din, pool, this, parseClassDesc())
				.getHandle();
	}

	/**
	 * Parses the <code>newArray</code> part of the stream. Returns the handle
	 * of the array object.
	 */
	private int parseArray() throws IOException {
		return new SerializedArrayObject(din, pool, this, parseClassDesc())
				.getHandle();
	}

	/**
	 * Parses the <code>newEnum</code> part of the stream. Returns the handle of
	 * the enum literal.
	 */
	private int parseEnum() throws IOException {
		return new SerializedEnumLiteral(pool, this, parseClassDesc())
				.getHandle();
	}

	/**
	 * Parses the <code>classDesc</code> part of the stream. Returns the handle
	 * of the class.
	 */
	public int parseClassDesc() throws IOException {
		byte next = din.readByte();
		switch (next) {
		case ObjectStreamConstants.TC_CLASSDESC:
			return parsePlainClassDesc();
		case ObjectStreamConstants.TC_PROXYCLASSDESC:
			return parseProxyClassDesc();
		case ObjectStreamConstants.TC_NULL:
			return SerializedEntityPool.NULL_HANDLE;
		case ObjectStreamConstants.TC_REFERENCE:
			return readHandle();
		default:
			throw new IOException("Unexpected value for next: " + next);
		}
	}

	/**
	 * Parses the "normal" part of a <code>newClassDesc</code> part of the
	 * stream. Returns the handle of the class.
	 */
	private int parsePlainClassDesc() throws IOException {
		return new SerializedClass(din, pool, this).getHandle();
	}

	/**
	 * Parses the proxy part of a <code>newClassDesc</code> part of the stream.
	 * Returns the handle of the class.
	 */
	private int parseProxyClassDesc() throws IOException {
		return new SerializedProxyClass(din, pool, this).getHandle();
	}

	/** Reads a handle to a previously read object from the stream. */
	private int readHandle() throws IOException {
		int handle = din.readInt();
		if (!pool.containsHandle(handle)) {
			throw new IOException("Handle to unknown object: " + handle);
		}
		return handle;
	}

	/**
	 * Parses an object and ensures that the returned object is a
	 * {@link SerializedStringObject}.
	 */
	public SerializedStringObject parseStringObject() throws IOException {
		return pool.getEntity(parseContent(), SerializedStringObject.class);
	}

	/** Parses entities from a given data input stream. */
	public static SerializedEntityPool parse(DataInputStream din)
			throws IOException {
		return new SerializedEntityParser(din).parse();
	}

	/** Parses entities from a given input stream. */
	public static SerializedEntityPool parse(InputStream in) throws IOException {
		if (!in.markSupported()) {
			in = new BufferedInputStream(in);
		}

		return parse(new DataInputStream(in));
	}

	/** Parses entities from a given byte array. */
	public static SerializedEntityPool parse(byte[] data) throws IOException {
		return parse(new ByteArrayInputStream(data));
	}
}
