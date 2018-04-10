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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.conqat.lib.commons.reflect.ReflectionUtils;
import org.conqat.lib.commons.reflect.TypeConversionException;

/**
 * A class for storing details on the method which is called to apply the
 * specific option.
 * <p>
 * As this is only used internally, is has package visibility.
 * 
 * @author Benjamin Hummel
 * @author $Author: kinnen $
 * 
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 3DFBCF47778292149B5734C1B233D75B
 */
/* package */class OptionApplicator {

	/** Makes the method receiving an option accessible */
	private final Object handler;

	/** Method that receives the option value */
	private final Method method;

	/** Whether this applicator is based on a "greedy" annotation. */
	private final boolean greedy;

	/**
	 * Constructor.
	 * 
	 * @param optionHandler
	 *            the object responsible for handling the option.
	 * @param method
	 *            the method to be called to apply this option.
	 * @param greedy
	 *            whether this applicator is based on a "greedy" annotation.
	 */
	/* package */OptionApplicator(Object optionHandler, Method method,
			boolean greedy) {
		if (method.getParameterTypes().length > 1) {
			throw new IllegalArgumentException(
					"Method "
							+ method.getName()
							+ " has more than 1 parameter and thus cannot be used as an option!");
		}
		this.handler = optionHandler;
		this.method = method;
		this.greedy = greedy;
	}

	/**
	 * Returns whether this option requires a parameter.
	 * 
	 * @return whether this option requires a parameter.
	 */
	public boolean requiresParameter() {
		return method.getParameterTypes().length > 0;
	}

	/**
	 * Applies this (parameterless) option.
	 * 
	 * @throws OptionException
	 */
	public void applyOption() throws OptionException {
		if (requiresParameter()) {
			throw new IllegalStateException("This option requires a parameter!");
		}
		try {
			method.invoke(handler, new Object[0]);
		} catch (IllegalAccessException e) {
			throw new IllegalStateException(
					"Unexpectedly could not invoke given method " + method
							+ "!", e);
		} catch (InvocationTargetException e) {
			throw new OptionException(e.getCause());
		}
	}

	/**
	 * Applies this (parametrized) option.
	 * 
	 * @param parameter
	 *            the parameter used.
	 * @throws TypeConversionException
	 *             if the parameter could not be transformed to the required
	 *             type.
	 * @throws OptionException
	 */
	public void applyOption(String parameter) throws TypeConversionException,
			OptionException {
		if (!requiresParameter()) {
			throw new IllegalStateException(
					"This option does not require a parameter!");
		}
		Object methodParameter = ReflectionUtils.convertString(parameter,
				method.getParameterTypes()[0]);
		try {
			method.invoke(handler, new Object[] { methodParameter });
		} catch (IllegalAccessException e) {
			throw new IllegalStateException(
					"Unexpectedly could not invoke given method " + method
							+ "!", e);
		} catch (InvocationTargetException e) {
			throw new OptionException(e.getCause());
		}
	}

	/** Returns whether this applicator is based on a "greedy" annotation. */
	public boolean isGreedy() {
		return greedy;
	}
}