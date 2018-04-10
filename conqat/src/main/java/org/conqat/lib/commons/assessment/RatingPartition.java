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
package org.conqat.lib.commons.assessment;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.conqat.lib.commons.enums.EnumUtils;
import org.conqat.lib.commons.string.StringUtils;

/**
 * A single partition as used in the {@link PartitionedRating}.
 * 
 * @author $Author: deissenb $
 * @version $Rev: 35954 $
 * @ConQAT.Rating GREEN Hash: EA58E8C34203468BF00A8F3BF8FD826C
 */
public class RatingPartition {

	/** The tag used to mark individual partitions. */
	public static final String PARTITION_TAG = "@ConQAT.Partition";

	/** Pattern used for parsing of the partition tags. */
	private static final Pattern PARTITION_PATTERN = Pattern.compile(".*"
			+ PARTITION_TAG + " +(" + Rating.COLORS_REGEX
			+ ") +Hash: *([a-fA-F0-9]*)( .*)?", Pattern.CASE_INSENSITIVE);

	/** The stored rating color. */
	private ETrafficLightColor storedRating;

	/** The hash including both the partition and the hash. */
	private String hash;

	/** The name of the region. */
	private final String name;

	/** The start line in the file. */
	private int startLine;

	/** The end line in the file. */
	private int endLine;

	/**
	 * The content of this partition (used mostly for calculation of hash
	 * values).
	 */
	private List<String> content;

	/** Constructor. */
	/* package */RatingPartition(ETrafficLightColor rating, String hash,
			String name) {
		this.storedRating = rating;
		this.hash = hash;
		this.name = name;
	}

	/** Sets the remaining information stored in this partition. */
	/* package */void setLinesAndContent(int startLine, int endLine,
			List<String> content) {
		this.startLine = startLine;
		this.endLine = endLine;
		this.content = content;
	}

	/** Returns name. */
	public String getName() {
		return name;
	}

	/** Returns 0-based start line (inclusive). */
	public int getStartLine() {
		return startLine;
	}

	/** Returns 0-based end line (inclusive). */
	public int getEndLine() {
		return endLine;
	}

	/**
	 * Returns the rating stored in the partition. This may be null if no stored
	 * rating exists.
	 */
	public ETrafficLightColor getStoredRating() {
		return storedRating;
	}

	/**
	 * Returns the actual rating determined based on the stored value and a
	 * comparison of hash codes.
	 */
	public ETrafficLightColor getRating() {
		if (storedRating == null || !hash.equalsIgnoreCase(getExpectedHash())) {
			return ETrafficLightColor.RED;
		}

		return getStoredRating();
	}

	/** Calculates and returns the expected hash. */
	private String getExpectedHash() {
		return new Rating(content, storedRating).getExpectedHashString();
	}

	/** Sets the rating and updates the hash value accordingly. */
	public void setStoredRatingAndUpdateHash(ETrafficLightColor color) {
		this.storedRating = color;
		this.hash = getExpectedHash();
	}

	/**
	 * Processes a partition tag and returns the rating partition. Returns null
	 * if no tag was found and processed.
	 */
	/* package */static RatingPartition processPartitionTag(String line) {
		Matcher matcher = PARTITION_PATTERN.matcher(line);
		if (!matcher.matches()) {
			return null;
		}

		ETrafficLightColor color = EnumUtils.valueOfIgnoreCase(
				ETrafficLightColor.class, matcher.group(1));

		String partitionName = matcher.group(3);
		if (StringUtils.isEmpty(partitionName)) {
			partitionName = StringUtils.EMPTY_STRING;
		}

		return new RatingPartition(color, matcher.group(2),
				partitionName.trim());
	}

	/** Returns the tag representing this partition. */
	/* package */String getTag() {
		ETrafficLightColor rating = storedRating;
		if (rating == null) {
			rating = ETrafficLightColor.RED;
		}

		return PARTITION_TAG + " " + rating.name() + " Hash: " + hash + " "
				+ getName();
	}
}