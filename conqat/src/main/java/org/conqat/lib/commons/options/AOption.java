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

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for exposing methods as command line options. This should only be
 * used with methods taking zero or one parameters.
 * 
 * @author Benjamin Hummel
 * @author $Author: kinnen $
 * 
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: D2F97E602CBDEC38C8659DCF91C2AEA0
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AOption {
	/** The optional short (i.e. single character) name of the option. */
	char shortName() default 0;

	/** The optional long (i.e. multi-character) name of the option. */
	String longName() default "";

	/**
	 * If this is set to true, all non-option arguments following the annotated
	 * option are used for this option. This results in multiple calls to this
	 * options setter method.
	 */
	boolean greedy() default false;

	/** The description of this option used for usage messages. */
	String description();
}