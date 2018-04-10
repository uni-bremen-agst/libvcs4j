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

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.reflect.TypeConversionException;
import org.conqat.lib.commons.string.StringUtils;

/**
 * A class providing command line parsing and usage messages using GNU syntax.
 * <p>
 * The GNU syntax is implemented as follows. There are short (single character)
 * and long (multi character) options, just as provided by the AOption
 * annotation. Short options are introduced using a single minus (e.g. '-h')
 * while long options are introduced using a double minus (e.g. '--help'). The
 * parameter for an option is either the next argument, or--in case of long
 * options--possibly separated by an equals sign (e.g. '--file=test.txt'). Short
 * options may be chained (e.g. '-xvf abc' instead of '-x -v -f abc'). For
 * chained short options, only the last option may take a parameter. The
 * separator '--' may be used to switch off argument processing for the
 * remaining arguments, i.e. all other arguments are treated as left-overs.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: BF5952F977D4E9C9356448E46C3A6412
 */
public class CommandLine {

	/** Registry containing the options to be used by this instance */
	private final OptionRegistry registry;

	/**
	 * Constructor.
	 * 
	 * @param registry
	 *            Registry containing the options to be used by this instance.
	 */
	public CommandLine(OptionRegistry registry) {
		this.registry = registry;
	}

	/**
	 * Parses the given command line parameters and applies the options found.
	 * The arguments not treated as options or parameters are returned (often
	 * they are treated as file arguments). If the syntax does not conform to
	 * the options in the registry, an {@link OptionException} is thrown.
	 * 
	 * @param args
	 *            the command line arguments to be parsed.
	 * @return the remaining arguments.
	 * @throws OptionException
	 *             in case of syntax errors or invalid parameters.
	 */
	public String[] parse(String[] args) throws OptionException {
		return parse(new CommandLineTokenStream(args));
	}

	/**
	 * Parses the command line parameters implicitly given by the token stream
	 * and applies the options found. The arguments not treated as options or
	 * parameters are returned (often they are treated as file arguments). If
	 * the syntax does not conform to the options in the registry an
	 * IllegalArgumentException is thrown.
	 * 
	 * @param ts
	 *            Token stream containing the arguments.
	 * @return Remaining arguments.
	 * @throws OptionException
	 *             in case of syntax errors or invalid parameters.
	 */
	public String[] parse(CommandLineTokenStream ts) throws OptionException {
		List<String> remainingArgs = new ArrayList<String>();

		while (ts.hasNext()) {
			if (ts.nextIsSeparator()) {
				// discard separator '--'
				ts.next();
				// and swallow everything else
				while (ts.hasNext()) {
					remainingArgs.add(ts.next());
				}
			} else if (ts.nextIsLongOption()) {
				String name = ts.nextLongOption();
				OptionApplicator applicator = registry.getLongOption(name);
				applyOption(applicator, formatLongOption(name), ts);
			} else if (ts.nextIsShortOption()) {
				char name = ts.nextShortOption();
				OptionApplicator applicator = registry.getShortOption(name);
				applyOption(applicator, formatShortOption(name), ts);
			} else if (ts.nextIsFileArgument()) {
				remainingArgs.add(ts.next());
			} else {
				throw new OptionException("Unexpected command line argument: "
						+ ts.next());
			}
		}

		return CollectionUtils.toArray(remainingArgs, String.class);
	}

	/**
	 * Applies an option and tests for various errors.
	 * 
	 * @param applicator
	 *            the applicator for the option.
	 * @param optionName
	 *            the name of the option.
	 * @param ts
	 *            the token stream used to get additional parameters.
	 */
	private void applyOption(OptionApplicator applicator, String optionName,
			CommandLineTokenStream ts) throws OptionException {
		if (applicator == null) {
			throw new OptionException("Unknown option: " + optionName);
		}
		if (applicator.requiresParameter()) {
			if (!ts.nextIsParameter()) {
				throw new OptionException("Missing argument for option: "
						+ optionName);
			}

			do {
				String parameter = ts.next();
				try {
					applicator.applyOption(parameter);
				} catch (TypeConversionException e) {
					throw new OptionException("Parameter " + parameter
							+ " for option " + optionName
							+ " is not of required type!");
				}
			} while (applicator.isGreedy() && ts.hasNext()
					&& !(ts.nextIsLongOption() || ts.nextIsShortOption()));
		} else {
			applicator.applyOption();
		}
	}

	/**
	 * Print the list of all supported options using reasonable default values
	 * for widths.
	 * 
	 * @param pw
	 *            the writer used for output.
	 */
	public void printUsage(PrintWriter pw) {
		printUsage(pw, 20, 80);
	}

	/**
	 * Print the list of all supported options.
	 * 
	 * @param pw
	 *            the writer to print to.
	 * @param firstCol
	 *            the width of the first column containing the option name
	 *            (without the trailing space).
	 * @param width
	 *            the maximal width of a line (aka terminal width).
	 */
	public void printUsage(PrintWriter pw, int firstCol, int width) {
		List<AOption> sortedOptions = new ArrayList<AOption>(
				registry.getAllOptions());
		Collections.sort(sortedOptions, new AOptionComparator());

		for (AOption option : sortedOptions) {
			printOption(option, pw, firstCol, width);
		}
		pw.flush();
	}

	/**
	 * Print a single option.
	 * 
	 * @param option
	 *            the option to be printed.
	 * @param pw
	 *            the writer to print to.
	 * @param firstCol
	 *            the width of the first column containing the option name
	 *            (without the trailing space).
	 * @param width
	 *            the maximal width of a line (aka terminal width).
	 */
	private void printOption(AOption option, PrintWriter pw, int firstCol,
			int width) {
		String names = formatNames(option);
		pw.print(names);

		// start new line (if name too long for firstCol) or indent correctly
		int pos = names.length();
		if (pos > firstCol) {
			pos = width + 1;
		} else {
			pw.print(StringUtils.fillString(firstCol - pos, ' '));
		}

		// Format description using lines no longer than width
		String indent = StringUtils.fillString(firstCol, ' ');
		String[] words = option.description().split("\\s+");
		for (String word : words) {
			if (pos + 1 + word.length() > width) {
				pw.println();
				pw.print(indent);
				pos = firstCol;
			}
			pw.print(' ');
			pw.print(word);
			pos += 1 + word.length();
		}
		pw.println();
	}

	/**
	 * Format the names of an option for output.
	 * 
	 * @param option
	 *            the options to format.
	 * @return the formatted string.
	 */
	private String formatNames(AOption option) {
		String names = "  ";
		if (option.shortName() == 0) {
			names += StringUtils.fillString(
					2 + formatShortOption('x').length(), ' ');
		} else {
			names += formatShortOption(option.shortName());
			if (option.longName().length() > 0) {
				names += ", ";
			}
		}
		if (option.longName().length() > 0) {
			names += formatLongOption(option.longName());
		}
		return names;
	}

	/**
	 * Returns the user visible name for the given long option.
	 * 
	 * @param name
	 *            the name of the option to format.
	 * @return the user visible name for the given long option.
	 */
	private String formatLongOption(String name) {
		return "--" + name;
	}

	/**
	 * Returns the user visible name for the given short option.
	 * 
	 * @param name
	 *            the name of the option to format.
	 * @return the user visible name for the given short option.
	 */
	private String formatShortOption(char name) {
		return "-" + name;
	}
}