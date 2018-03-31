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
package org.conqat.lib.commons.digest;

import java.security.MessageDigest;

import org.conqat.lib.commons.assertion.CCSMPre;
import org.conqat.lib.commons.collections.ByteArrayWrapper;

/**
 * An MD5 digest. This is just a thin wrapper around a byte array with some
 * convenience methods. This class is used instead of plain strings to save both
 * memory and (some) execution time.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 1D8556FFB7A1CFD64495333C61B393FB
 */
public final class MD5Digest extends ByteArrayWrapper {

	/** Version used for serialization. */
	private static final long serialVersionUID = 1;

	/** Number of bytes in an MD5 sum. */
	public static final int MD5_BYTES = 16;

	/**
	 * Constructor. This calls {@link MessageDigest#digest()}, so the digest
	 * will be reset afterwards.
	 */
	public MD5Digest(MessageDigest digest) {
		this(digest.digest());
	}

	/** Constructor. */
	public MD5Digest(byte[] digest) {
		super(digest);
		CCSMPre.isTrue(digest.length == MD5_BYTES,
				"Invalid size of MD5 digest!");
	}

	/**
	 * Inserts the digest data into the given MD. This method is used to rehash
	 * multiple hashes.
	 * <p>
	 * This method is provided instead of a getter, to keep this immutable.
	 */
	public void insertIntoDigest(MessageDigest digest) {
		digest.update(array);
	}

	/**
	 * Calculates and returns a hashcode that only depends on the first 3 bytes.
	 */
	public int partialHashCode() {
		return array[0] | (array[1] << 8) | (array[2] << 16);
	}
}