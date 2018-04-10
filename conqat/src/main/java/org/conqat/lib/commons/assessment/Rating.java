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
package org.conqat.lib.commons.assessment;

import java.security.MessageDigest;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.conqat.lib.commons.digest.Digester;
import org.conqat.lib.commons.enums.EnumUtils;
import org.conqat.lib.commons.string.StringUtils;

/**
 * A rating is essentially an {@link ETrafficLightColor} assigned to a file. The
 * rating can be stored in the file or externally. To protect the stored color,
 * it is protected by a hash sum over the file's content, i.e. if the file
 * changes this will be detected and result in a RED rating. There are some
 * exclusions when calculating the check sum to make it more stable with respect
 * to a versioning system or encoding problems.
 * <p>
 * While the typical application is to files (and their content), the actual
 * implementation has no notion of a file and works on arbitrary strings.
 * <p>
 * A note on compatibility: Over time there were different versions of the
 * rating algorithm.
 * <ul>
 * <li>The earliest version used no check sums, but rather compared to the
 * CVS/SVN revision stored in the file. This is no longer supported.</li>
 * <li>Another previous version used the tag '@levd.rating' and did not include
 * the color itself in the calculation of the hash (i.e. the color coud be
 * changed). Ratings created with this version can still be read.</li>
 * <li>The current version uses the tag '@ConQAT.Rating' and includes the color
 * in the hash code calculation. Ratings for this version can be read and
 * written. Migration to this version is supported.</li>
 * </ul>
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 544761BB145E28961876838662ADDEDD
 */
public class Rating {

	/** The rating tag used. */
	public static final String RATING_TAG = "@ConQAT.Rating";

	/** The old rating tag used for backwards compatibility. */
	private static final String OLD_RATING_TAG = "@levd.rating";

	/** Regular expression that matches the colors */
	/* package */static final String COLORS_REGEX = StringUtils.concat(
			ETrafficLightColor.values(), "|");

	/** Pattern used to extract the rating. */
	private static final Pattern RATING_PATTERN = Pattern.compile("("
			+ RATING_TAG + "|" + OLD_RATING_TAG + ") +(" + COLORS_REGEX
			+ ") +Hash:? *([a-fA-F0-9]*)", Pattern.CASE_INSENSITIVE);

	/**
	 * Pattern used to determine whether hash calculation should be restarted
	 * (see {@link #updateHash(String, MessageDigest)}). Currently this matches
	 * Java package declarations to exclude the file header.
	 */
	private static final Pattern RESET_PATTERN = Pattern
			.compile("^package .*;$");

	/** The rating stored in the file. */
	private ETrafficLightColor storedRating;

	/**
	 * Stores whether this uses the {@link #OLD_RATING_TAG}. If so, the color is
	 * not included in the hash calculation.
	 */
	private boolean oldStyleRating = false;

	/** The stored hash value. */
	private String storedHash;

	/** The expected hash value. */
	private String expectedHash;

	/** Constructor. */
	public Rating(String content) {
		this(content, null);
	}

	/**
	 * Constructor.
	 * 
	 * @param forcedRating
	 *            if this is non-null, the rating of the file is overwritten by
	 *            this color. This is useful to calculate the expected hash
	 *            value for a given (new) color.
	 */
	public Rating(String content, ETrafficLightColor forcedRating) {
		this(StringUtils.splitLinesAsList(content), forcedRating);
	}

	/** Constructor. */
	public Rating(List<String> lines) {
		this(lines, null);
	}

	/**
	 * Constructor.
	 * 
	 * @param forcedRating
	 *            if this is non-null, the rating of the file is overwritten by
	 *            this color. This is useful to calculate the expected hash
	 *            value for a given (new) color.
	 */
	public Rating(List<String> lines, ETrafficLightColor forcedRating) {
		MessageDigest md5 = Digester.getMD5();
		for (String line : lines) {
			// ignore line containing rating
			if (storedRating == null && checkRating(line)) {
				continue;
			}

			updateHash(line, md5);
		}

		if (forcedRating != null) {
			storedRating = forcedRating;
			oldStyleRating = false;
		}

		if (!oldStyleRating && storedRating != null) {
			md5.update(StringUtils.stringToBytes(storedRating.name()));
		}

		expectedHash = StringUtils.encodeAsHex(md5.digest());
	}

	/**
	 * Checks whether the line contains a rating and updates the fields
	 * accordingly.
	 * 
	 * @return true if a rating was found.
	 */
	private boolean checkRating(String line) {
		Matcher ratingMatcher = RATING_PATTERN.matcher(line);
		if (!ratingMatcher.find()) {
			return false;
		}

		oldStyleRating = ratingMatcher.group(1)
				.equalsIgnoreCase(OLD_RATING_TAG);
		storedRating = EnumUtils.valueOf(ETrafficLightColor.class,
				ratingMatcher.group(2));
		storedHash = ratingMatcher.group(3);
		return true;
	}

	/**
	 * Updates the hash using the following rules:
	 * 
	 * <ul>
	 * <li>Only characters in the US-ASCII range which are not control
	 * characters (including whitespace) are considered. Thus the hash value is
	 * invariant about changes in whitespace or the use of different encodings.</li>
	 * <li>Lines containing a rating tag (new or old) are ignored. This is for
	 * backwards compatibility, but also allows to be tolerant when managing
	 * multiple ratings in a file.</li>
	 * <li>Lines matching the {@link #RESET_PATTERN} cause the hash calculation
	 * to restart, effectively ignoring everything encountered so far.</li>
	 * <li>Everything between dollar sign is ignored, as these are often contain
	 * information updated by the version management system.</li>
	 * </ul>
	 * 
	 */
	private void updateHash(String line, MessageDigest md5) {
		if (line.contains(RATING_TAG) || line.contains(OLD_RATING_TAG)) {
			return;
		}

		if (RESET_PATTERN.matcher(line).matches()) {
			md5.reset();
			return;
		}

		boolean inDollar = false;
		for (int i = 0; i < line.length(); ++i) {
			int c = line.charAt(i);
			if (c == '$') {
				inDollar = !inDollar;
			} else if (!inDollar && c > 32 && c < 128) {
				// this cast is ok, as we checked before
				md5.update((byte) c);
			}
		}
	}

	/**
	 * Returns the rating stored in the file (which may be null if no old rating
	 * was found).
	 */
	public ETrafficLightColor getStoredRating() {
		return storedRating;
	}

	/**
	 * Returns the actual rating determined based on the stored value and a
	 * comparison of hash codes.
	 */
	public ETrafficLightColor getRating() {
		if (getStoredRating() == null || !isValidRating()) {
			return ETrafficLightColor.RED;
		}

		return getStoredRating();
	}

	/** Determines based on the attributes whether the rating is valid. */
	private boolean isValidRating() {
		return storedHash != null && storedHash.equalsIgnoreCase(expectedHash);
	}

	/** Returns the expected hash code as a string. */
	public String getExpectedHashString() {
		return expectedHash;
	}

	/**
	 * Updates the first found rating tag of a given string with the new color
	 * and returns the new content. If no rating tag is found, null is returned.
	 */
	public static String updateRating(String content,
			ETrafficLightColor newColor) {
		Matcher m = RATING_PATTERN.matcher(content);
		if (!m.find()) {
			return null;
		}

		String replacement = constructRatingTag(newColor, new Rating(content,
				newColor).getExpectedHashString());

		StringBuffer sb = new StringBuffer();
		m.appendReplacement(sb, replacement);
		m.appendTail(sb);
		return sb.toString();
	}

	/**
	 * Returns the rating tag starting with {@value #RATING_TAG} and containing
	 * the color and hash values.
	 */
	public static String constructRatingTag(ETrafficLightColor color,
			String hash) {
		return RATING_TAG + " " + color.name() + " Hash: " + hash;
	}
}