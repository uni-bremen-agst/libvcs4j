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
package org.conqat.lib.commons.options;

import java.util.LinkedList;
import java.util.Queue;

/**
 * This class preprocesses the command line arguments by splitting them into
 * several tokens. It supports the GNU style syntax as described in
 * {@link org.conqat.lib.commons.options.CommandLine}.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: F8268C791586F81F2BD6860CAD8CD6DB
 */
public class CommandLineTokenStream {

	/** Queue storing remaining short options (results from option chaining). */
	private final Queue<Character> shortOptionQueue = new LinkedList<Character>();

	/** Pending parameter (possibly remaining from the last long option read). */
	private String pendingParam = null;

	/** Queue storing all remaining arguments. */
	private final Queue<String> argQueue = new LinkedList<String>();

	/** Constructs a new CommandLineTokenStream on the given arguments. */
	public CommandLineTokenStream(String[] args) {
		for (String a : args) {
			argQueue.add(a);
		}
	}

	/** Returns whether a further token is available. */
	public boolean hasNext() {
		return !argQueue.isEmpty() || !shortOptionQueue.isEmpty()
				|| pendingParam != null;
	}

	/** Returns whether the next token is the argument separator "--". */
	public boolean nextIsSeparator() {
		if (!shortOptionQueue.isEmpty() || pendingParam != null
				|| argQueue.isEmpty()) {
			return false;
		}
		return argQueue.peek().equals("--");
	}

	/** Returns whether the next token is available and is a short option. */
	public boolean nextIsShortOption() {
		if (!shortOptionQueue.isEmpty()) {
			return true;
		}
		if (pendingParam != null || argQueue.isEmpty()) {
			return false;
		}
		String next = argQueue.peek();
		return next.length() >= 2 && next.charAt(0) == '-'
				&& next.charAt(1) != '-';
	}

	/** Returns whether the next token is available and is a long option. */
	public boolean nextIsLongOption() {
		if (!shortOptionQueue.isEmpty() || pendingParam != null
				|| argQueue.isEmpty()) {
			return false;
		}
		return argQueue.peek().startsWith("--") && argQueue.peek().length() > 2;
	}

	/**
	 * Returns whether the next token is available and can be used as a file
	 * argument.
	 */
	public boolean nextIsFileArgument() {
		if (!shortOptionQueue.isEmpty() || pendingParam != null
				|| argQueue.isEmpty()) {
			return false;
		}
		return !argQueue.peek().startsWith("-");
	}

	/**
	 * Returns whether the next token is available and can be used as a
	 * parameter to an option.
	 */
	public boolean nextIsParameter() {
		if (!shortOptionQueue.isEmpty()) {
			return false;
		}
		if (pendingParam != null) {
			return true;
		}
		return !argQueue.isEmpty();
	}

	/** Returns the next token as a plain string. */
	public String next() {
		if (!shortOptionQueue.isEmpty()) {
			return "-" + shortOptionQueue.poll();
		}
		if (pendingParam != null) {
			String result = pendingParam;
			pendingParam = null;
			return result;
		}
		return argQueue.poll();
	}

	/** Returns the next token as a short option. */
	public char nextShortOption() {
		if (!nextIsShortOption()) {
			throw new IllegalStateException("No short option available!");
		}
		if (shortOptionQueue.isEmpty()) {
			String arg = argQueue.poll();
			for (int i = 1; i < arg.length(); ++i) {
				shortOptionQueue.add(arg.charAt(i));
			}
		}
		return shortOptionQueue.poll();
	}

	/** Returns the next token as a long option. */
	public String nextLongOption() {
		if (!nextIsLongOption()) {
			throw new IllegalStateException("No long option available!");
		}
		String res = argQueue.poll().substring(2);
		if (res.contains("=")) {
			String[] parts = res.split("=", 2);
			res = parts[0];
			pendingParam = parts[1];
		}
		return res;
	}
}