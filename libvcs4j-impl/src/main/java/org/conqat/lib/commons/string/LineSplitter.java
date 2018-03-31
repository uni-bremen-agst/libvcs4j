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
package org.conqat.lib.commons.string;

import java.util.Iterator;

/**
 * This class is used to split a string in lines using an {@link Iterator}. The
 * default setting is to not return trailing empty lines. Use
 * {@link #setIncludeTrailingEmptyLine(boolean)} to include them.
 * <p>
 * <b>Note:</b> According to tests I performed this is the fastest method to
 * split a string. It is about nine times faster than the regex-bases split
 * with:
 * 
 * <pre>
 * Pattern pattern = Pattern.compile(&quot;\r\n|\r|\n&quot;);
 * pattern.split(content);
 * </pre>
 * 
 * @author $Author: hummelb $
 * @version $Revision: 47169 $
 * @ConQAT.Rating GREEN Hash: 3F4DCF68808D37A2C247649FCE04FF91
 */
public class LineSplitter implements Iterator<String>, Iterable<String> {

	/** The string content to split. */
	private String content;

	/** Starting index. */
	private int startIndex;

	/** Flag for returning the trailing empty line. */
	private boolean includeTrailingEmptyLine = false;

	/**
	 * Constructor for empty content.
	 */
	public LineSplitter() {
		// Does nothing as content is empty.
	}

	/**
	 * Constructor which calls {@link #setContent(String)}.
	 */
	public LineSplitter(String content) {
		setContent(content);
	}

	/**
	 * Set the string to split and reset the iterator.
	 * 
	 * @param content
	 *            The string to split. If string is <code>null</code> or the
	 *            empty string, {@link #next()} will return <code>null</code>.
	 * 
	 */
	public void setContent(String content) {
		this.content = content;
		startIndex = 0;
	}

	/** {@inheritDoc} */
	@Override
	public boolean hasNext() {
		if (content == null) {
			return false;
		}

		if (includeTrailingEmptyLine && isTrailingEmptyLine()) {
			return true;
		}

		if (startIndex >= content.length()) {
			// delete reference to array to allow garbage collection
			content = null;
			return false;
		}

		return true;
	}

	/**
	 * Obtain next identified line.
	 * 
	 * @return <code>null</code> if all lines were returned. On returning the
	 *         last line all references to the input string are deleted. So it
	 *         is free for garbage collection.
	 */
	@Override
	public String next() {
		if (!hasNext()) {
			return null;
		}

		if (includeTrailingEmptyLine && isTrailingEmptyLine()) {
			startIndex++; // shift index, so it is beyond the content length
			return StringUtils.EMPTY_STRING;
		}

		// length to skip may vary due to the length of the line separator (\r,
		// \n or \r\n)
		int skip = 0;

		int endIndex = startIndex;

		while (skip == 0 && endIndex < content.length()) {
			char c = content.charAt(endIndex);

			endIndex++;

			// Skip newlines.
			if (c == '\n') {
				skip = 1;
			}

			// Skip newlines.
			if (c == '\r') {
				skip = 1;
				if (endIndex < content.length()
						&& content.charAt(endIndex) == '\n') {
					skip = 2;
					endIndex++;
				}
			}
		}

		String result = content.substring(startIndex, endIndex - skip);

		startIndex = endIndex;
		return result;
	}

	/**
	 * @return <code>true</code> if the iterator is at the end of the string
	 *         content and the content contains an empty trailing line.
	 */
	private boolean isTrailingEmptyLine() {
		if (startIndex > 0 && startIndex == content.length()) {
			char lastChar = content.charAt(startIndex - 1);
			return lastChar == '\n' || lastChar == '\r';
		}
		return false;
	}

	/**
	 * Enables returning of trailing empty lines during the iteration. Default
	 * is <code>false</code>
	 * <p>
	 * If <code>true</code> the string <code>Foo\nBar\n</code>will yield three
	 * items (Foo, Bar and the empty string), otherwise two items (Foo and Bar).
	 */
	public void setIncludeTrailingEmptyLine(boolean includeTrailingEmptyLine) {
		this.includeTrailingEmptyLine = includeTrailingEmptyLine;
	}

	/** {@inheritDoc} */
	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

	/** {@inheritDoc} */
	@Override
	public Iterator<String> iterator() {
		return this;
	}

}