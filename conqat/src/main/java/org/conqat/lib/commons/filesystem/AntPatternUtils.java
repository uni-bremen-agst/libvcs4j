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

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.conqat.lib.commons.string.StringUtils;

/**
 * Utility methods for dealing with Ant pattern as defined at
 * http://ant.apache.org/manual/dirtasks.html#patterns
 * 
 * Testing is currently performed implicitly in the context of the
 * AntPatternDirectoryScanner class.
 * 
 * @author $Author: streitel $
 * @version $Rev: 50585 $
 * @ConQAT.Rating GREEN Hash: F4FF1595E0CF285B7DB45AD9E45A4D81
 */
public class AntPatternUtils {

	/** Converts an ANT pattern to a regex pattern. */
	public static Pattern convertPattern(String antPattern,
			boolean caseSensitive) throws PatternSyntaxException {

		// normalize separator
		antPattern = FileSystemUtils.normalizeSeparators(antPattern);

		// ant pattern syntax: if a pattern ends with /, then ** is
		// appended
		if (antPattern.endsWith("/")) {
			antPattern += "**";
		}

		StringBuilder patternBuilder = new StringBuilder();

		// ant specialty: trailing /** is optional
		// for example **/e*/** will also match foo/entry
		boolean addTrailAll = false;
		if (antPattern.endsWith("/**")) {
			addTrailAll = true;
			antPattern = StringUtils.stripSuffix(antPattern, "/**");
		}

		convertPlainPattern(antPattern, patternBuilder);

		if (addTrailAll) {
			// the tail pattern is optional (i.e. we do not require the '/'),
			// but the "**" is only in effect if the '/' occurs
			patternBuilder.append("(/.*)?");
		}

		// Use DOTALL flag, as on Unix the file names can contain line breaks
		int flags = Pattern.DOTALL;
		if (!caseSensitive) {
			flags |= Pattern.CASE_INSENSITIVE;
		}

		try {
			return Pattern.compile(patternBuilder.toString(), flags);
		} catch (PatternSyntaxException e) {
			// make pattern syntax exception more understandable
			throw new PatternSyntaxException("Error compiling ANT pattern '"
					+ antPattern + "' to regular expression. "
					+ e.getDescription(), e.getPattern(), e.getIndex());
		}
	}

	/**
	 * Converts a plain ANT pattern to a regular expression, by replacing
	 * special characters, such as '?', '*', and '**'. The created pattern is
	 * appended to the given {@link StringBuilder}. The pattern must be plain,
	 * i.e. all ANT specialties, such as trailing double stars have to be dealt
	 * with beforehand.
	 */
	private static void convertPlainPattern(String antPattern,
			StringBuilder patternBuilder) {
		for (int i = 0; i < antPattern.length(); ++i) {
			char c = antPattern.charAt(i);
			if (c == '?') {
				patternBuilder.append("[^/]");
			} else {
				if (c != '*') {
					patternBuilder.append(Pattern.quote(Character.toString(c)));
				} else {
					boolean doubleStar = lookAhead(antPattern, i + 1, '*');
					if (doubleStar) {
						// if the double star is followed by a slash, the entire
						// group becomes optional, as we want "**/foo" to also
						// match a top-level "foo"
						if (lookAhead(antPattern, i + 2, '/')) {
							patternBuilder.append("(.*/)?");
							i += 2;
						} else {
							patternBuilder.append(".*");
							i += 1;
						}
					} else {
						patternBuilder.append("[^/]*");
					}
				}
			}
		}
	}

	/**
	 * Returns whether the given position exists in the string and equals the
	 * given character.
	 */
	private static boolean lookAhead(String s, int position, char character) {
		return position < s.length() && s.charAt(position) == character;
	}
}