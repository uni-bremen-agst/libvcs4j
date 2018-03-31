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
package org.conqat.lib.commons.string;

/**
 * Enumeration of newline types. This deals only with the most common subset of
 * new line types. For more details see http://en.wikipedia.org/wiki/Newline.
 * 
 * @author $Author: goede $
 * @version $Rev: 40695 $
 * @ConQAT.Rating GREEN Hash: AD84BA55982439F6D716BC4B25B72A6E
 */
public enum ENewline {

	/** Unix (and Linux) are using LF. */
	UNIX("\n"),

	/** Windows uses CR+LF. */
	WINDOWS("\r\n"),

	/** MAC up to version 9 uses CR. MacOS X uses {@link #UNIX} newlines. */
	MAC("\r");

	/** The character(s) used to represent newline. */
	private final String newline;

	/** Constructor. */
	private ENewline(String newline) {
		this.newline = newline;
	}

	/** Returns character(s) used to represent newline. */
	public String getNewline() {
		return newline;
	}

	/**
	 * Converts the input string to using the specified line breaks and returns
	 * the result.
	 */
	public String convertNewlines(String input) {
		return StringUtils.replaceLineBreaks(input, newline);
	}

	/**
	 * Attempts to guess the newline style from a string. This performs a simple
	 * majority guess, where CR+lF are simply assumed to be next to each other.
	 * If no line breaks are found, the newline for {@link StringUtils#CR} is
	 * returned.
	 */
	public static ENewline guessNewline(String string) {
		int crCount = 0;
		int lfCount = 0;
		int crlfCount = 0;
		boolean previousWasCR = false;
		for (char c : string.toCharArray()) {
			if (c == '\r') {
				previousWasCR = true;
				crCount += 1;
			} else if (c == '\n') {
				if (previousWasCR) {
					crlfCount += 1;
					crCount -= 1;
				} else {
					lfCount += 1;
				}
				previousWasCR = false;
			} else {
				previousWasCR = false;
			}
		}

		// in case of equals, we chose arbitrarily, so using < is ok
		if (crlfCount > lfCount && crlfCount > crCount) {
			return WINDOWS;
		}
		if (lfCount > crCount) {
			return UNIX;
		}

		if (crCount == 0) {
			// in this case, all are 0
			return guessNewline(StringUtils.CR);
		}

		return MAC;
	}
}
