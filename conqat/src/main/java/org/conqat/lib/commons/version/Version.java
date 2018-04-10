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
package org.conqat.lib.commons.version;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.conqat.lib.commons.error.FormatException;

/**
 * A class to describe versions of software (or other) artifacts. A version has
 * a major and a minor version number. Version are ordered. This class is
 * immutable.
 * 
 * @author Florian Deissenboeck
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: AD1BC5C8034C812BFA04CA9596741295
 */
public class Version implements Comparable<Version>, Serializable {

	/** Version used for serialization. */
	private static final long serialVersionUID = 1;

	/** Major version. */
	private final int major;

	/** Minor version. */
	private final int minor;

	/**
	 * Create a new version.
	 * 
	 * @param major
	 *            major version number.
	 * @param minor
	 *            minor version number.
	 * @throws IllegalArgumentException
	 *             if one of the version numbers is less than 0.
	 */
	public Version(int major, int minor) {

		if (major < 0 || minor < 0) {
			throw new IllegalArgumentException(
					"Versions may not be less than 0.");
		}

		this.major = major;
		this.minor = minor;
	}

	/**
	 * Parses a version from a string. The format has to be "major.minor".
	 * 
	 * @throws FormatException
	 *             if the string does not follow the expected pattern.
	 */
	public static Version parseVersion(String s) throws FormatException {
		Matcher m = Pattern.compile("\\s*(\\d+)\\.(\\d+)\\s*").matcher(s);
		if (!m.matches()) {
			throw new FormatException(
					"The provided string did not match the pattern!");
		}
		return new Version(Integer.parseInt(m.group(1)), Integer.parseInt(m
				.group(2)));
	}

	/**
	 * Compares to version numbers by their major and minor numbers.
	 */
	@Override
	public int compareTo(Version other) {
		if (major > other.major) {
			return 1;
		}
		if (major < other.major) {
			return -1;
		}

		// major numbers are equal
		if (minor > other.minor) {
			return 1;
		}

		if (minor < other.minor) {
			return -1;
		}

		// both are equal
		return 0;
	}

	/**
	 * Two version are equal if their major and minor version numbers are equal.
	 */
	@Override
	public boolean equals(Object other) {
		if (other == this) {
			return true;
		}

		if (!(other instanceof Version)) {
			return false;
		}

		return compareTo((Version) other) == 0;

	}

	/** Get major version number. */
	public int getMajor() {
		return major;
	}

	/** Get minor version number. */
	public int getMinor() {
		return minor;
	}

	/**
	 * Hashcode is (major << 7) | minor;
	 */
	@Override
	public int hashCode() {
		return (major << 7) | minor;
	}

	/**
	 * This method is used to check version compatibility in dependency
	 * management.
	 * <p>
	 * Consider the following situation and artefact A (the depender) depends on
	 * another artefact B (the dependee). A claims that it requires B in version
	 * 1.3. B states that it has version 1.5 but is downward compatible to
	 * version 1.1.
	 * <p>
	 * Using this method one can find out if the version provided by B satisfies
	 * A's requirement. It is satisfied iff
	 * 
	 * <pre>
	 * requiredVersion &lt;= currentVersion &amp;&amp; requiredVersion &gt;= compatibleVersion
	 * </pre>
	 * 
	 * where <code>requiredVersion</code> is this instance and the other two
	 * are provided as method parameters.
	 * 
	 * @throws IllegalArgumentException
	 *             if <code>compatibleVersion</code> is greater than
	 *             <code>currentVersion</code>.
	 */
	public boolean isSatisfied(Version currentVersion, Version compatibleVersion) {

		if (compatibleVersion.compareTo(currentVersion) > 0) {
			throw new IllegalArgumentException(
					"Compatible version greater than current version.");
		}

		return this.compareTo(currentVersion) <= 0
				&& this.compareTo(compatibleVersion) >= 0;
	}

	/**
	 * String representation: major.minor
	 */
	@Override
	public String toString() {
		return major + "." + minor;
	}

}