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
package org.conqat.lib.commons.assessment.external;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.conqat.lib.commons.assessment.partition.IRatingPartitioner;
import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.filesystem.FileSystemUtils;
import org.conqat.lib.commons.string.StringUtils;

/**
 * This class manages an external rating partition table, i.e. a table for a
 * partioned rating that is not kept as comments within the code file but in an
 * external file.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 600E235964B7E748F1B0C6B1D19EB209
 */
public class ExternalRatingPartitionTable {

	/** The tag used to mark individual partitions. */
	public static final String EXTERNAL_TABLE_TAG = "@ConQAT.ExternalRatingTable";

	/** Pattern used for parsing of the partition tags. */
	private static final Pattern EXTERNAL_TABLE_PATTERN = Pattern.compile(".*"
			+ EXTERNAL_TABLE_TAG + "[ _]+(\\S+) *", Pattern.CASE_INSENSITIVE);

	/** The accessor used (may be null). */
	private final IRatingTableFileAccessor accessor;

	/** The argument used to construct the resolver. */
	private String resolverArguments;

	/**
	 * The currently used resolver (may be null). This is initialized during the
	 * parsing process.
	 */
	private IFilePathResolver resolver;

	/** Constructor. */
	public ExternalRatingPartitionTable(IRatingTableFileAccessor accessor) {
		this.accessor = accessor;
	}

	/**
	 * Attempts to extract the rating tag from a line. Returns true if a tag was
	 * found.
	 */
	public boolean processTag(String line) throws ExternalRatingTableException {
		// process at most one tag
		if (resolverArguments != null) {
			return false;
		}

		Matcher matcher = EXTERNAL_TABLE_PATTERN.matcher(line);
		if (!matcher.matches()) {
			return false;
		}

		resolverArguments = matcher.group(1);
		createResolver();

		return true;
	}

	/** Creates the {@link #resolver} based on the {@link #resolverArguments}. */
	private void createResolver() throws ExternalRatingTableException {
		String[] parts = resolverArguments.split(":");
		String className = parts[0];
		String[] args = new String[parts.length - 1];
		System.arraycopy(parts, 1, args, 0, args.length);

		createResolver(className, args);
	}

	/** Creates the {@link #resolver} based on class name and arguments list. */
	private void createResolver(String className, String[] args)
			throws ExternalRatingTableException {
		try {
			resolver = determineResolverClass(className).newInstance();
			resolver.init(args);
		} catch (InstantiationException e) {
			handleResolverCreationException(e);
		} catch (IllegalAccessException e) {
			handleResolverCreationException(e);
		} catch (ExternalRatingTableException e) {
			resolver = null;
			throw e;
		}
	}

	/** Helper method for handling exception consistently. */
	private void handleResolverCreationException(Exception e)
			throws ExternalRatingTableException {
		resolver = null;
		throw new ExternalRatingTableException(
				"Could not instantiate resolver: " + e.getMessage());
	}

	/** Determines the class used for the resolver. */
	@SuppressWarnings("unchecked")
	private Class<? extends IFilePathResolver> determineResolverClass(
			String className) throws ExternalRatingTableException {
		Class<? extends IFilePathResolver> resolverClass;
		try {
			resolverClass = (Class<? extends IFilePathResolver>) Class
					.forName(className, true, Thread.currentThread()
							.getContextClassLoader());
		} catch (ClassNotFoundException e) {
			throw new ExternalRatingTableException(
					"Could not find resolver class " + className);
		}
		if (!IFilePathResolver.class.isAssignableFrom(resolverClass)) {
			throw new ExternalRatingTableException("Given resolver class "
					+ className + " does not implement "
					+ IRatingPartitioner.class.getSimpleName());
		}
		return resolverClass;
	}

	/**
	 * Returns the lines contained in the currently configured external rating
	 * table. If this is not possible, an empty list is returned.
	 */
	public List<String> getLines() {
		String relativePath = determineRelativePath();
		if (relativePath == null) {
			return CollectionUtils.emptyList();
		}

		String content = accessor.getRelativeFileContent(relativePath);
		if (content == null) {
			return CollectionUtils.emptyList();
		}

		return StringUtils.splitLinesAsList(content);
	}

	/** Returns the relative path to the table file (or null). */
	private String determineRelativePath() {
		if (resolver == null || accessor == null) {
			return null;
		}
		return resolver.getRelativeFilePath(FileSystemUtils
				.normalizeSeparators(accessor.getFilePath()));
	}

	/** Returns whether this external table is actually used. */
	public boolean isUsed() {
		return resolverArguments != null;
	}

	/** Returns the tag used to mark the file for using an external table. */
	public String getTag() {
		return EXTERNAL_TABLE_TAG + "_" + resolverArguments;
	}

	/** Updates the table with the given tags. */
	public void updateTable(List<String> partitionTags) {
		String relativePath = determineRelativePath();
		if (relativePath == null) {
			return;
		}

		String content = StringUtils.concat(partitionTags, StringUtils.CR);
		accessor.setRelativeFileContent(relativePath, content);
	}
}
