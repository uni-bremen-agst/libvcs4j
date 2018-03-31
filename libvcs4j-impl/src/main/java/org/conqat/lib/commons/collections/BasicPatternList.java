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
package org.conqat.lib.commons.collections;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

import org.conqat.lib.commons.clone.IDeepCloneable;

/**
 * A list of RegEx pattern. the reason for this name ("Basic") is that it
 * contains slightly less functions that the version found in the ConQAT engine.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: C3B1775B7FF3E25032BD004C56094D8F
 */
public class BasicPatternList extends ArrayList<Pattern> implements
		IDeepCloneable, Serializable {

	/** Version used for serialization. */
	private static final long serialVersionUID = 1;

	/** Creates an empty {@link BasicPatternList} */
	public BasicPatternList() {
		// do nothing
	}

	/** Creates a {@link BasicPatternList} with the collection of patterns */
	public BasicPatternList(Collection<? extends Pattern> patterns) {
		super(patterns);
	}

	/** Creates a pattern list for the specified patterns. */
	public BasicPatternList(Pattern... patterns) {
		this(Arrays.asList(patterns));
	}

	/**
	 * Returns true, if the pattern list is empty or {@link #matchesAny(String)}
	 * returns true.
	 */
	public boolean emptyOrMatchesAny(String s) {
		return isEmpty() || matchesAny(s);
	}

	/**
	 * Returns whether the given string matches at least one of the contained
	 * pattern. For this the <code>Matcher.matches()</code> method is used.
	 */
	public boolean matchesAny(String s) {
		for (Pattern p : this) {
			if (p.matcher(s).matches()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns whether in the given string at least one of the contained pattern
	 * is found. For this the <code>Matcher.find()</code> method is used.
	 */
	public boolean findsAnyIn(String s) {
		for (Pattern p : this) {
			if (p.matcher(s).find()) {
				return true;
			}
		}
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public BasicPatternList deepClone() {
		return new BasicPatternList(this);
	}

	/**
	 * Returns a list with the regular expressions from which the patters were
	 * compiled
	 */
	public List<String> asStringList() {
		List<String> patterns = new ArrayList<String>();
		for (Pattern pattern : this) {
			patterns.add(pattern.pattern());
		}
		return patterns;
	}
}