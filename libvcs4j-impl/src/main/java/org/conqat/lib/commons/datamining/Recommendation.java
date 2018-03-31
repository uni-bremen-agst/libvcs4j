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
package org.conqat.lib.commons.datamining;

import org.conqat.lib.commons.assertion.CCSMAssert;

/**
 * A recommendation produced by a recommender, encapsulating an item and a
 * confidence level.
 * 
 * @author $Author: heineman $
 * @version $Rev: 41796 $
 * @ConQAT.Rating YELLOW Hash: 11D10D78545E2B5332EBFE38A11E92C5
 */
public class Recommendation<T> {

	/** The recommended item */
	private T item;

	/** The confidence of this recommendation */
	private double confidence;

	/** Constructor */
	public Recommendation(T item, double confidence) {
		CCSMAssert.isNotNull(item);
		CCSMAssert.isTrue(confidence >= 0 && confidence <= 1,
				"Confidence must be in [0,1]");
		this.item = item;
		this.confidence = confidence;
	}

	/** Returns the item. */
	public T getItem() {
		return item;

	}

	/** Returns the confidence. */
	public double getConfidence() {
		return confidence;
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return item + " (" + confidence + ")";
	}
}
