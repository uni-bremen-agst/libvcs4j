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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.conqat.lib.commons.assertion.CCSMPre;
import org.conqat.lib.commons.collections.BasicPatternList;
import org.conqat.lib.commons.string.StringUtils;

/**
 * * This class performs directory scanning, i.e. returns all files residing
 * within a certain directory. The list of files returned can be narrowed using
 * include/exclude pattern, which use the same syntax as the pattern known from
 * ANT (see http://ant.apache.org/manual/dirtasks.html#patterns).
 * <p>
 * This class is meant to be a faster and more memory efficient replacement for
 * ANT's DirectoryScanner.
 * <p>
 * Internally this works entirely with '/' as path separator. However, this is
 * not visible from the outside.
 * <p>
 * Internally, the implementation uses Java's RegEx engine by translating ANT
 * patterns to regular expressions.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 44967 $
 * @ConQAT.Rating GREEN Hash: B5D5D79289326EC0D5456DA25EB9339E
 */
public class AntPatternDirectoryScanner {

	/** The base directory. */
	private final File baseDir;

	/** Stores whether we are case sensitive. */
	private final boolean caseSensitive;

	/** The list of files found (result of scanning). */
	private final List<String> filesFound = new ArrayList<String>();

	/** Include pattern used with files. */
	private final BasicPatternList fileIncludes = new BasicPatternList();

	/** Exclude pattern used with files. */
	private final BasicPatternList fileExcludes = new BasicPatternList();

	/**
	 * Exclude patterns that are greedy, i.e. that end in '**'. These are
	 * interesting, because as soon as they match, every extension of the
	 * matched string will match as well. So these pattern can be used to skip
	 * entire directories. This list contains a subset of the pattern from
	 * {@link #fileExcludes}.
	 */
	private final BasicPatternList greedyExcludes = new BasicPatternList();

	/**
	 * List of required prefixes. This can be used if no include pattern starts
	 * with a '*'. In this case, directories not starting with the prefix may be
	 * skipped. Otherwise this attribute is null. This is an array so we can use
	 * it with the {@link StringUtils#startsWithOneOf(String, String...)}
	 * method.
	 */
	private final String[] requiredPrefixes;

	/** Constructor. */
	private AntPatternDirectoryScanner(File baseDir, boolean caseSensitive,
			String[] includePatterns, String[] excludePatterns)
			throws PatternSyntaxException {
		CCSMPre.isTrue(baseDir.isDirectory(), "Can only scan in directories: "
				+ baseDir);
		this.baseDir = baseDir;
		this.caseSensitive = caseSensitive;

		boolean hadStarPrefix = false;
		List<String> prefixes = new ArrayList<String>();
		for (String include : includePatterns) {
			fileIncludes.add(AntPatternUtils.convertPattern(include,
					caseSensitive));
			if (include.startsWith("*")) {
				hadStarPrefix = true;
			} else {
				// extract the plain prefix, i.e. the prefix of the pattern
				// without wildcard characters or directory separators; this
				// prefix can be used to speed up scanning, as only directories
				// starting with one of the prefixes are relevant at all.
				String prefix = include.replaceFirst(
						"([\\*/\\?]|" + Pattern.quote(File.separator) + ").*$",
						"");
				if (!caseSensitive) {
					prefix = prefix.toLowerCase();
				}
				prefixes.add(prefix);
			}
		}
		if (hadStarPrefix || prefixes.isEmpty()) {
			requiredPrefixes = null;
		} else {
			requiredPrefixes = prefixes.toArray(new String[prefixes.size()]);
		}

		for (String exclude : excludePatterns) {
			Pattern pattern = AntPatternUtils.convertPattern(exclude,
					caseSensitive);
			fileExcludes.add(pattern);
			if (exclude.endsWith("**")) {
				greedyExcludes.add(pattern);
			}
		}
	}

	/** Performs scanning starting from the given file. */
	private String[] scan() throws IOException {
		for (String path : listChildren(baseDir)) {
			String testPath = path;
			if (!caseSensitive) {
				testPath = testPath.toLowerCase();
			}

			if (requiredPrefixes != null
					&& !StringUtils.startsWithOneOf(testPath, requiredPrefixes)) {
				continue;
			}

			doScan(path);
		}
		return filesFound.toArray(new String[filesFound.size()]);
	}

	/**
	 * Performs scanning in the directory denoted by the given relative path
	 * name.
	 */
	private void doScan(String relativePath) throws IOException {
		File file = new File(baseDir, relativePath);

		if (file.isDirectory()) {
			if (!skipDirectory(relativePath)) {
				for (String name : listChildren(file)) {
					doScan(relativePath + "/" + name);
				}
			}
		} else if (isIncluded(relativePath) && !isExcluded(relativePath)) {
			String foundFile = relativePath.replace('/', File.separatorChar);
			filesFound.add(foundFile);
		}
	}

	/**
	 * Lists the children of a directory. If this fails, a {@link IOException}
	 * is thrown (avoid NPE!).
	 */
	private Set<String> listChildren(File dir) throws IOException {
		String[] list = dir.list();
		if (list == null) {
			throw new IOException("Cannot scan in directory " + dir
					+ "! Maybe read permissions are missing?");
		}

		// although occurring rarely, it happens that the build machine returns
		// duplicate entries in java.io.File.list(), hence remove these here via
		// a set. See also CR#4916.
		return new HashSet<String>(Arrays.asList(list));
	}

	/** Heuristic used to skip entire directories. */
	private boolean skipDirectory(String relativePath) {
		return greedyExcludes.matchesAny(relativePath);
	}

	/** Returns whether a relative path is included. */
	private boolean isIncluded(String relativePath) {
		return fileIncludes.isEmpty() || fileIncludes.matchesAny(relativePath);
	}

	/** Returns whether a relative path is excluded. */
	private boolean isExcluded(String relativePath) {
		return fileExcludes.matchesAny(relativePath);
	}

	/**
	 * Performs directory scanning.
	 * 
	 * @param baseDir
	 *            the directory to start scanning in. All file names returned
	 *            will be relative to this file.
	 * @param caseSensitive
	 *            whether pattern should be applied case sensitive or not.
	 * @param includePatterns
	 *            the include pattern (use ANT's pattern syntax)
	 * @param excludePatterns
	 *            the exclude pattern (use ANT's pattern syntax)
	 * @throws IOException
	 *             in case of invalid pattern provided.
	 */
	public static String[] scan(String baseDir, boolean caseSensitive,
			String[] includePatterns, String[] excludePatterns)
			throws IOException {
		if (includePatterns == null) {
			includePatterns = new String[0];
		}

		if (excludePatterns == null) {
			excludePatterns = new String[0];
		}

		return new AntPatternDirectoryScanner(new File(baseDir), caseSensitive,
				includePatterns, excludePatterns).scan();
	}
}