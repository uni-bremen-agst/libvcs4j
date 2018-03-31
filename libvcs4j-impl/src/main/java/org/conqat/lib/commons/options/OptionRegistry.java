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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class supports collecting options from several objects whose methods
 * have been annotated with {@link AOption}.
 * 
 * @author Benjamin Hummel
 * @author $Author: kinnen $
 * 
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 0175B3D2CF3E5E2EE4F7EF712622A17D
 */
public class OptionRegistry {

	/** Mapping from short option names to actual option applicator. */
	private final Map<Character, OptionApplicator> shortOptions = new HashMap<Character, OptionApplicator>();

	/** Mapping from long option names to actual option applicator. */
	private final Map<String, OptionApplicator> longOptions = new HashMap<String, OptionApplicator>();

	/** List of all options (including description). */
	private final List<AOption> allOptions = new ArrayList<AOption>();

	/**
	 * Default constructor. Does nothing but we want to allow this too.
	 */
	public OptionRegistry() {
		// nothing to do
	}

	/**
	 * Construct a new option registry and register the given option handler.
	 * 
	 * @param optionHandler
	 *            the option handler to register.
	 */
	public OptionRegistry(Object optionHandler) {
		registerOptionHandler(optionHandler);
	}

	/**
	 * Adds all options provided by the given object to this registry. Options
	 * are public methods annotated with AOption.
	 * 
	 * @param optionHandler
	 *            The object to extract the options from.
	 */
	public void registerOptionHandler(Object optionHandler) {
		for (Method m : optionHandler.getClass().getMethods()) {
			AOption optionDescriptor = m.getAnnotation(AOption.class);
			if (optionDescriptor != null) {
				allOptions.add(optionDescriptor);
				OptionApplicator applicator = new OptionApplicator(
						optionHandler, m, optionDescriptor.greedy());
				if (optionDescriptor.shortName() != 0) {
					registerApplicator(applicator, shortOptions,
							optionDescriptor.shortName());
				}
				if (optionDescriptor.longName().length() > 0) {
					registerApplicator(applicator, longOptions,
							optionDescriptor.longName());
				}
			}
		}
	}

	/**
	 * Registers an applicator under the given name in a map. Throws an
	 * exception if the name was already taken.
	 * 
	 * @param <T>
	 *            type of the option name used in the map.
	 * @param applicator
	 *            the option applicator to register.
	 * @param map
	 *            the map to add the applicator to.
	 * @param optionName
	 *            the name of the option.
	 */
	private <T> void registerApplicator(OptionApplicator applicator,
			Map<T, OptionApplicator> map, T optionName) {
		if (map.containsKey(optionName)) {
			throw new IllegalArgumentException("An option of the name "
					+ optionName + " already exists!");
		}
		map.put(optionName, applicator);
	}

	/**
	 * Returns the OptionApplicator for the given short option name or null if
	 * no such options exists.
	 * 
	 * @param name
	 *            the name of the requested option.
	 * @return the OptionApplicator for the given short option name or null if
	 *         no such options exists.
	 */
	public OptionApplicator getShortOption(char name) {
		return shortOptions.get(name);
	}

	/**
	 * Returns the OptionApplicator for the given short option name or null if
	 * no such options exists.
	 * 
	 * @param name
	 *            the name of the requested option.
	 * @return the OptionApplicator for the given short option name or null if
	 *         no such options exists.
	 */
	public OptionApplicator getLongOption(String name) {
		return longOptions.get(name);
	}

	/**
	 * Returns a list containing all supported options.
	 * 
	 * @return a list containing all supported options.
	 */
	public List<AOption> getAllOptions() {
		return allOptions;
	}
}