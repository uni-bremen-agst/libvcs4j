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
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.List;

import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.error.EnvironmentError;
import org.conqat.lib.commons.string.StringUtils;

/**
 * Utility functions for creation of digests.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 51573 $
 * @ConQAT.Rating GREEN Hash: 1CE6F4C846AE50D0BCBC49F9DCF98D09
 */
public class Digester {

	/**
	 * MD5 Digesters used organized by thread. This is used to avoid recreation
	 * of digesters, while keeping the code thread safe (i.e. each thread has
	 * its own instance).
	 */
	private static ThreadLocal<MessageDigest> md5Digesters = new ThreadLocal<MessageDigest>() {
		/** {@inheritDoc} */
		@Override
		protected MessageDigest initialValue() {
			return getMD5();
		}
	};

	/**
	 * SHA-1 Digesters used organized by thread. This is used to avoid
	 * recreation of digesters, while keeping the code thread safe (i.e. each
	 * thread has its own instance).
	 */
	private static ThreadLocal<MessageDigest> sha1Digesters = new ThreadLocal<MessageDigest>() {
		/** {@inheritDoc} */
		@Override
		protected MessageDigest initialValue() {
			return getSHA1();
		}
	};

	/**
	 * Computes an MD5 hash for a string. The hash is always 32 characters long
	 * and only uses characters from [0-9A-F].
	 */
	public static String createMD5Digest(String base) {
		return createMD5Digest(base.getBytes());
	}

	/**
	 * Computes an MD5 hash for a byte array. The hash is always 32 characters
	 * long and only uses characters from [0-9A-F].
	 */
	public static String createMD5Digest(byte[] data) {
		MessageDigest digester = md5Digesters.get();
		digester.reset();
		return StringUtils.encodeAsHex(digester.digest(data));
	}

	/**
	 * Computes an MD5 hash for a collection of strings. The strings are sorted
	 * before MD5 computation, so that the resulting MD5 hash is independent of
	 * the order of the strings in the collection.
	 */
	public static String createMD5Digest(Collection<String> bases) {
		List<String> sortedBases = CollectionUtils.sort(bases);
		return createMD5Digest(StringUtils.concat(sortedBases,
				StringUtils.EMPTY_STRING));
	}

	/**
	 * Computes an SHA-1 hash for a byte array and returns the binary hash (i.e.
	 * no string conversion).
	 */
	public static byte[] createBinarySHA1Digest(byte[] data) {
		MessageDigest digester = sha1Digesters.get();
		digester.reset();
		return digester.digest(data);
	}

	/**
	 * Returns MD5 digester or throws an AssertionError if the digester could
	 * not be located.
	 */
	public static MessageDigest getMD5() {
		try {
			return MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new EnvironmentError(
					"No MD5 algorithm found. Please check your JRE installation",
					e);
		}
	}

	/**
	 * Returns SHA-1 digester or throws an AssertionError if the digester could
	 * not be located.
	 */
	public static MessageDigest getSHA1() {
		try {
			return MessageDigest.getInstance("SHA-1");
		} catch (NoSuchAlgorithmException e) {
			throw new EnvironmentError(
					"No SHA-1 algorithm found. Please check your JRE installation",
					e);
		}
	}

}