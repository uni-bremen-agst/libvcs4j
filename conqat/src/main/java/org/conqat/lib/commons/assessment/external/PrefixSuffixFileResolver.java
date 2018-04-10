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

/**
 * A file resolver that works by adding prefixes and suffixes to the base name.
 * Optionally, it can also map paths into a parallel directory tree. This tree
 * is determined by an "anchor directory" (such as "src") that is searched in
 * the path to find the point of the parallel tree, and the replacement
 * directory (e.g. "ratings") where the parallel tree resides. See the test-case
 * for examples.
 * 
 * Note that the fully qualified name of this class is used in the rating
 * comments of many code files. Thus, rename or move this class only if it can
 * not be avoided.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: DE1CBE957CDD47F3119835302DD25E24
 */
public class PrefixSuffixFileResolver implements IFilePathResolver {

	/** The prefix. */
	private String filenamePrefix;

	/** The suffix. */
	private String filenameSuffix;

	/** The name of the "anchor" directory. */
	private String anchorDirectoryName;

	/**
	 * The name of the directory replacing {@link #anchorDirectoryName} in the
	 * parallel tree.
	 */
	private String replacementDirectoryName;

	/**
	 * {@inheritDoc}.
	 * 
	 * @param args
	 *            the arguments taken are (in order) the prefix, the suffix, the
	 *            anchor directory and the replacement directory. It is ok to
	 *            provide only one or two parameters (prefix or prefix and
	 *            suffix).
	 */
	@Override
	public void init(String... args) throws ExternalRatingTableException {
		if (args.length == 0 || args.length == 3 || args.length > 4) {
			throw new ExternalRatingTableException(
					"Expecting 1, 2, or 4 arguments!");
		}

		filenamePrefix = pickArg(args, 0, "");
		filenameSuffix = pickArg(args, 1, "");
		anchorDirectoryName = pickArg(args, 2, null);
		replacementDirectoryName = pickArg(args, 3, null);
	}

	/** Returns the i-th argument if it exists (otherwise the provided default). */
	private String pickArg(String[] args, int i, String defaultValue) {
		if (i >= args.length) {
			return defaultValue;
		}
		return args[i];
	}

	/** {@inheritDoc} */
	@Override
	public String getRelativeFilePath(String path) {
		String[] parts = path.split("/");

		String result = filenamePrefix + parts[parts.length - 1]
				+ filenameSuffix;
		if (anchorDirectoryName == null) {
			return result;
		}

		String dotsPart = "";
		for (int i = parts.length - 2; i >= 0; --i) {
			dotsPart += "../";
			if (parts[i].equalsIgnoreCase(anchorDirectoryName)) {
				return dotsPart + replacementDirectoryName + "/" + result;
			}
			result = parts[i] + "/" + result;
		}

		// No anchor found
		return null;
	}
}
