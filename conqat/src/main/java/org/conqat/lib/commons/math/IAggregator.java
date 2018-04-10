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
package org.conqat.lib.commons.math;

import java.util.Collection;

/**
 * Aggregtor interface.
 * 
 * @author deissenb
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 9338245431192F82012C421C12296D6D
 */
public interface IAggregator {

	/**
	 * Aggregate collection of values to one value. Implementing classes may
	 * return non-normal numbers, e.g. {@link Double#NaN} for the median of an
	 * empty collections.
	 */
	public double aggregate(Collection<? extends Number> values);

	/**
	 * Get the neutral element of this aggregation operator. This may return
	 * non-normal numbers, e.g. {@link Double#NaN} or
	 * {@link Double#POSITIVE_INFINITY} .
	 */
	public double getNeutralElement();

}