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

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.conqat.lib.commons.assessment.external.ExternalRatingPartitionTable;
import org.conqat.lib.commons.assessment.external.ExternalRatingTableException;
import org.conqat.lib.commons.assessment.external.IRatingTableFileAccessor;
import org.conqat.lib.commons.assessment.partition.IRatingPartitioner;
import org.conqat.lib.commons.assessment.partition.PartitioningException;
import org.conqat.lib.commons.collections.CollectionMap;
import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.collections.UnmodifiableList;
import org.conqat.lib.commons.error.NeverThrownRuntimeException;
import org.conqat.lib.commons.factory.IFactory;
import org.conqat.lib.commons.region.Region;
import org.conqat.lib.commons.string.StringUtils;

/**
 * A rating is essentially an {@link ETrafficLightColor} assigned to a piece of
 * code. The rating can be stored in the file or externally. To protect the
 * stored color, it is protected by a hash sum over piece of code, i.e. if the
 * code changes this will be detected and result in a RED rating. There are some
 * exclusions when calculating the check sum to make it more stable with respect
 * to a versioning system or encoding problems.
 * <p>
 * To persist this information, two kinds of tags are used. The first one is
 * called the rating tag and marks the file as using partitioned ratings. The
 * second one is called the partition tag and exists one for each partition
 * (piece of code). The rating tag should occur at most once; in case of
 * multiple rating tags, only the first one is respected.
 * <p>
 * The difference of this class to the {@link Rating} is that individual ratings
 * can be stored for partitions of the file.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 46705 $
 * @ConQAT.Rating GREEN Hash: 8FC518F18B7B2FC72305EC16441760B0
 */
public class PartitionedRating {

	/** The rating tag used. */
	public static final String PARTITIONED_RATING_TAG = "@ConQAT.PartitionedRating";

	/** Pattern used for parsing of the rating tag. */
	private static final Pattern PARTITIONED_RATING_PATTERN = Pattern.compile(
			"(.*)" + PARTITIONED_RATING_TAG + "[ _]+(\\S+) *",
			Pattern.CASE_INSENSITIVE);

	/** The class of the partitioner used. */
	private Class<? extends IRatingPartitioner> partitionerClass;

	/** The partitioner used. */
	private IRatingPartitioner partitioner;

	/** External rating table. */
	private final ExternalRatingPartitionTable externalTable;

	/**
	 * Contains the lines that make up the content of the entire file (and
	 * contain no tags)
	 */
	private final List<String> contentLines = new ArrayList<String>();

	/**
	 * List containing the (0-based) line numbers of those lines in the original
	 * content holding one of the rating or partition tags. The list is created
	 * and managed in a way to keep it sorted.
	 */
	private final List<Integer> tagLineNumbers = new ArrayList<Integer>();

	/**
	 * The partitions stored in the code as tags. We use a queue to ensure order
	 * within partitions with the same name.
	 */
	private final CollectionMap<String, RatingPartition, Queue<RatingPartition>> storedPartitions = new CollectionMap<String, RatingPartition, Queue<RatingPartition>>(
			new IFactory<Queue<RatingPartition>, NeverThrownRuntimeException>() {
				@Override
				public Queue<RatingPartition> create()
						throws NeverThrownRuntimeException {
					return new LinkedList<RatingPartition>();
				}
			});

	/**
	 * The string that came before the {@link #PARTITIONED_RATING_TAG}. This is
	 * typically a comment delimited and stored for reinsertion.
	 */
	private String tagPrefix = null;

	/** The actual partitions found in the code. */
	private final List<RatingPartition> partitions = new ArrayList<RatingPartition>();

	/** Constructor. */
	public PartitionedRating(String content) throws PartitioningException,
			ExternalRatingTableException {
		this(StringUtils.splitLinesAsList(content));
	}

	/** Constructor. */
	public PartitionedRating(String content, IRatingTableFileAccessor accessor)
			throws PartitioningException, ExternalRatingTableException {
		this(StringUtils.splitLinesAsList(content), null, accessor);
	}

	/**
	 * Constructor.
	 * 
	 * @param partitionerClass
	 *            if this is null, the partitioner will be read from the lines,
	 *            otherwise the given class will be used for partitioning.
	 */
	public PartitionedRating(String content,
			Class<? extends IRatingPartitioner> partitionerClass)
			throws PartitioningException, ExternalRatingTableException {
		this(StringUtils.splitLinesAsList(content), partitionerClass, null);
	}

	/** Constructor. */
	public PartitionedRating(List<String> lines) throws PartitioningException,
			ExternalRatingTableException {
		this(lines, null, null);
	}

	/**
	 * Constructor.
	 * 
	 * @param partitionerClass
	 *            if this is null, the partitioner will be read from the lines,
	 *            otherwise the given class will be used for partitioning.
	 * @param accessor
	 *            the accessor for external table file (may be null).
	 */
	public PartitionedRating(List<String> lines,
			Class<? extends IRatingPartitioner> partitionerClass,
			IRatingTableFileAccessor accessor) throws PartitioningException,
			ExternalRatingTableException {

		externalTable = new ExternalRatingPartitionTable(accessor);

		preprocessLines(lines);

		if (partitionerClass != null) {
			this.partitionerClass = partitionerClass;
		}

		if (this.partitionerClass == null) {
			throw new PartitioningException(
					"No partitioner description found in code!");
		}

		initPartitioner(partitionerClass);
		createPartitions();
	}

	/**
	 * Preprocesses the content's lines by filtering out all tags and preparing
	 * the {@link #contentLines}, {@link #tagLineNumbers} and
	 * {@link #storedPartitions}.
	 */
	private void preprocessLines(List<String> lines)
			throws PartitioningException, ExternalRatingTableException {
		int lineNumber = 0;
		for (String line : lines) {
			if (processRatingTag(line) || processPartitionTag(line)) {
				tagLineNumbers.add(lineNumber);
			} else if (externalTable.processTag(line)) {
				tagLineNumbers.add(lineNumber);
				for (String externalLine : externalTable.getLines()) {
					processPartitionTag(externalLine);
				}
			} else {
				contentLines.add(line);
			}
			lineNumber += 1;
		}
	}

	/**
	 * Processes the rating tag. Returns true if the tag was found and
	 * processed.
	 */
	@SuppressWarnings("unchecked")
	private boolean processRatingTag(String line) throws PartitioningException {
		if (tagPrefix != null) {
			// already had the tag, so ignore the rest
			return false;
		}

		Matcher ratingMatcher = PARTITIONED_RATING_PATTERN.matcher(line);
		if (!ratingMatcher.matches()) {
			return false;
		}
		tagPrefix = ratingMatcher.group(1);
		String className = ratingMatcher.group(2);

		try {
			partitionerClass = (Class<? extends IRatingPartitioner>) Class
					.forName(className, true, Thread.currentThread()
							.getContextClassLoader());
		} catch (ClassNotFoundException e) {
			throw new PartitioningException("Could not find partitioner class "
					+ className, e);
		}
		if (!IRatingPartitioner.class.isAssignableFrom(partitionerClass)) {
			throw new PartitioningException("Given partitioner class "
					+ className + " does not implement "
					+ IRatingPartitioner.class.getSimpleName());
		}
		return true;
	}

	/**
	 * Processes a partition tag. Returns true if the tag was found and
	 * processed.
	 */
	private boolean processPartitionTag(String line) {
		RatingPartition partition = RatingPartition.processPartitionTag(line);
		if (partition != null) {
			storedPartitions.add(partition.getName(), partition);
			return true;
		}
		return false;
	}

	/** Initializes the {@link #partitioner}. */
	private void initPartitioner(
			Class<? extends IRatingPartitioner> partitionerClass)
			throws PartitioningException {
		try {
			partitioner = this.partitionerClass.newInstance();
		} catch (InstantiationException e) {
			throw new PartitioningException(
					"Could not create instance of partitioner: "
							+ partitionerClass, e);
		} catch (IllegalAccessException e) {
			throw new PartitioningException(
					"Could not create instance of partitioner: "
							+ partitionerClass, e);
		}
	}

	/** Partitions the code and fills {@link #partitions}. */
	private void createPartitions() throws PartitioningException {
		for (Region region : splitIntoRegions()) {
			Queue<RatingPartition> queue = storedPartitions
					.getCollection(region.getOrigin());
			RatingPartition partition;
			if (queue != null && !queue.isEmpty()) {
				partition = queue.poll();
			} else {
				partition = new RatingPartition(null, StringUtils.EMPTY_STRING,
						region.getOrigin());
			}

			partition.setLinesAndContent(getOriginalLine(region.getStart()),
					getOriginalLine(region.getEnd()), contentLines.subList(
							region.getStart(), region.getEnd() + 1));
			partitions.add(partition);
		}
	}

	/**
	 * Splits the {@link #contentLines} into regions using the
	 * {@link #partitioner}.
	 */
	private List<Region> splitIntoRegions() throws PartitioningException {
		List<Region> regions = partitioner.partition(CollectionUtils.toArray(
				contentLines, String.class));
		Collections.sort(regions);
		for (int i = 1; i < regions.size(); ++i) {
			if (regions.get(i - 1).overlaps(regions.get(i))) {
				throw new PartitioningException(
						"Partitioner seems to be broken as overlapping regions were returned!");
			}
		}
		return regions;
	}

	/**
	 * Determine the original (zero based) line number from an index into the
	 * {@link #contentLines}.
	 */
	private int getOriginalLine(int line) {
		for (int skippedLine : tagLineNumbers) {
			if (skippedLine <= line) {
				line += 1;
			} else {
				break;
			}
		}

		return line;
	}

	/**
	 * Returns the list of partitions. While the list itself may not be changed,
	 * the rating color of the partitions may be changed.
	 */
	public UnmodifiableList<RatingPartition> getPartitions() {
		return CollectionUtils.asUnmodifiable(partitions);
	}

	/**
	 * Returns the content but with the regions updated to reflect the new
	 * rating colors. If there was no old rating found for replacement, null is
	 * returned to indicate this problem. This also updates the external table
	 * if used.
	 */
	public String getUpdatedContent() {
		if (tagLineNumbers.isEmpty()) {
			return null;
		}

		List<String> output = new ArrayList<String>();

		boolean hadTags = false;
		for (String line : contentLines) {
			// write out tags at the position of the first rating or partition
			// tag
			if (output.size() == tagLineNumbers.get(0)) {
				insertTags(output);
				hadTags = true;
			}
			output.add(line);
		}

		// tags might be at the very end
		if (!hadTags) {
			insertTags(output);
		}

		return StringUtils.concat(output, StringUtils.CR);
	}

	/**
	 * Inserts the partition rating table and updates the external table if
	 * used.
	 */
	private void insertTags(List<String> output) {
		output.add(tagPrefix + createRatingTag(partitionerClass.getName()));

		List<String> partitionTags = new ArrayList<String>();
		for (RatingPartition partition : partitions) {
			partitionTags.add(tagPrefix + partition.getTag());
		}

		if (externalTable.isUsed()) {
			output.add(tagPrefix + externalTable.getTag());
			externalTable.updateTable(partitionTags);
		} else {
			output.addAll(partitionTags);
		}
	}

	/** Constructs the tag used to mark a partition rated file. */
	/* package */static String createRatingTag(String partitionerName) {
		return PARTITIONED_RATING_TAG + "_" + partitionerName;
	}
}
