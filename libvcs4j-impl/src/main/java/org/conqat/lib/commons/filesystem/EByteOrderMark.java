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
package org.conqat.lib.commons.filesystem;

import java.util.Arrays;

import org.conqat.lib.commons.assertion.CCSMAssert;
import org.conqat.lib.commons.io.ByteArrayUtils;

/**
 * Enumeration of the UTF byte order marks (BOM). The actual values are taken
 * from http://unicode.org/faq/utf_bom.html
 * <p>
 * The order of the values in this enum is chosen such that BOMs that are a
 * prefix of other BOMs are at the end, i.e. UTF-32 is before UTF-16. This way
 * we can check the BOM prefix in the order of the enum values' appearance.
 * 
 * @author hummelb
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating YELLOW Hash: 5D59A6685FA2EDE86DD381C2E7F6324E
 */
public enum EByteOrderMark {

	/** UTF-32 with big endian encoding. */
	UTF_32BE("UTF-32BE", new byte[] { 0x00, 0x00, (byte) 0xFE, (byte) 0xFF }),

	/** UTF-32 with little endian encoding. */
	UTF_32LE("UTF-32LE", new byte[] { (byte) 0xFF, (byte) 0xFE, 0x00, 0x00 }),

	/** UTF-16 with big endian encoding. */
	UTF_16BE("UTF-16BE", new byte[] { (byte) 0xFE, (byte) 0xFF }),

	/** UTF-16 with little endian encoding. */
	UTF_16LE("UTF-16LE", new byte[] { (byte) 0xFF, (byte) 0xFE }),

	/**
	 * UTF-8. Note that for UTF-8 the endianess is not relevant and that the BOM
	 * is optional.
	 */
	UTF_8_BOM("UTF-8", new byte[] { (byte) 0xEF, (byte) 0xBB, (byte) 0xBF });

	/** The maximal length of a BOM. */
	public static final int MAX_BOM_LENGTH = 4;

	/** The name of the encoding */
	private final String encoding;

	/** The byte order mark. */
	private final byte[] bom;

	/** Constructor. */
	private EByteOrderMark(String encoding, byte[] bom) {
		this.encoding = encoding;
		CCSMAssert.isTrue(bom.length <= MAX_BOM_LENGTH,
				"Inconsistent max BOM length!");
		this.bom = bom;
	}

	/** Returns the encoding. */
	public String getEncoding() {
		return encoding;
	}

	/**
	 * Returns the byte order mark. This returns a copy, so the array may be
	 * modified.
	 */
	public byte[] getBOM() {
		return Arrays.copyOf(bom, bom.length);
	}

	/** Returns the size of the BOM in bytes. */
	public int getBOMLength() {
		return bom.length;
	}

	/**
	 * This method checks the start of the provided data array to find a BOM. If
	 * a BOM is found, the corresponding enum value is returned. Otherwise,
	 * <code>null</code> is returned. If possible, the provided data should at
	 * least be of size {@value #MAX_BOM_LENGTH}. Otherwise the encoding might
	 * not be detected correctly. However, the method also works with shorter
	 * arrays (e.g. if a file consists of only 3 bytes).
	 */
	public static EByteOrderMark determineBOM(byte[] data) {
		for (EByteOrderMark bom : values()) {
			if (ByteArrayUtils.isPrefix(bom.bom, data)) {
				return bom;
			}
		}
		return null;
	}
}