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

import java.util.Arrays;

import org.conqat.lib.commons.assertion.CCSMPre;
import org.conqat.lib.commons.assertion.PreconditionException;
import org.conqat.lib.commons.collections.ManagedIntArray;

/**
 * A class that helps to convert between line numbers and character offsets. The
 * character offset is zero based while the line number is one based.
 * <p>
 * This class works for strings with arbitrary line terminators.
 * <p>
 * The implementation works by storing all character offsets of the newline
 * characters. For multi-character line endings (i.e. CR+LF on windows) the last
 * offset is stored. For space/performance reasons, these offsets are stored in
 * a bare array, which is managed by the base class.
 * <p>
 * <i>Note:</i> This class extends {@link ManagedIntArray} although delegation
 * would be more elegant. The problem, however, is, that {@link ManagedIntArray}
 * does not support delegation. The idea of this class was to provide protected
 * access to its internals, which only works via inheritance. Actually, this is
 * not interface inheritance ({@link ManagedIntArray} has no public method,
 * btw.) but implementation inheritance.
 * 
 * @author $Author: heinemann $
 * @version $Rev: 48259 $
 * @ConQAT.Rating GREEN Hash: 457F2E1C19BECCB45527491C47777171
 */
public class LineOffsetConverter extends ManagedIntArray {

	/** Constructor */
	public LineOffsetConverter(String s) {
		char[] chars = s.toCharArray();
		for (int i = 0; i < chars.length; ++i) {
			if (chars[i] == '\n') {
				addArrayElement();
				array[size - 1] = i;
			} else if (chars[i] == '\r') {
				if (i + 1 < chars.length && chars[i + 1] == '\n') {
					// for \r\n just store the position of the \n
					continue;
				}
				addArrayElement();
				array[size - 1] = i;
			}
		}

		// append implicit '\n' at the end to allow querying start of last line
		// (to determine size of last line)
		if (chars.length == 0 || chars[chars.length - 1] != '\n') {
			addArrayElement();
			array[size - 1] = chars.length;
		}
	}

	/** Returns the number of lines of the input string. */
	public int getLineCount() {
		return size;
	}

	/**
	 * Returns the (zero based) offset of the first character of the given line
	 * (starting at 1).
	 * 
	 * @throws PreconditionException
	 *             if the line is not valid for the string.
	 */
	public int getOffset(int line) {
		CCSMPre.isTrue(isValidLine(line), "This is not a valid line: " + line);

		if (line == 1) {
			return 0;
		}

		// first character of line is directly behind newline character
		return array[line - 2] + 1;
	}

	/** Returns whether the given line is valid for the converter. */
	public boolean isValidLine(int line) {
		return 1 <= line && line <= size + 1;
	}

	/**
	 * Returns the (one based) line for the character at the given (zero based)
	 * offset. The newline at the end of a line is counted as a part of the line
	 * (i.e. for the very first newline, we would return 1). If the offset is
	 * larger than the length of the string, the index of the last line is
	 * returned.
	 */
	public int getLine(int offset) {
		CCSMPre.isTrue(offset >= 0, "Negative offsets not supported!");

		int index = Arrays.binarySearch(array, 0, size, offset);
		if (index >= 0) {
			return index + 1;
		}

		return Math.min(-index, size);
	}
}