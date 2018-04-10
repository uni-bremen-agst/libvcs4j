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

import java.util.HashSet;
import java.util.Set;

import org.conqat.lib.commons.assertion.CCSMAssert;

/**
 * User represented by a shopping basket, i.e. a set of purchased items. The
 * similarity measure used is cosine similarity (see
 * http://en.wikipedia.org/wiki/Cosine_similarity).
 * 
 * 
 * @author $Author: heineman $
 * @version $Rev: 41796 $
 * @ConQAT.Rating YELLOW Hash: 20A6DDE3BD3D1F49A232697408B4BC17
 */
public class ShoppingBasketUser<T> implements IRecommenderUser {

	/** The set of items */
	private Set<T> items;

	/** Constructor */
	public ShoppingBasketUser(Set<T> items) {
		CCSMAssert.isFalse(items.isEmpty(), "Items must not be empty");
		this.items = items;
	}

	/** Returns the items. */
	public Set<T> getItems() {
		return items;
	}

	/** {@inheritDoc} */
	@Override
	public double similarity(IRecommenderUser other) {
		if (!(other instanceof ShoppingBasketUser<?>)) {
			throw new IllegalArgumentException();
		}
		ShoppingBasketUser<?> user = (ShoppingBasketUser<?>) other;
		return similarity(items, user.items);
	}

	/** Computes the cosine similarity of two non-empty sets */
	private static double similarity(Set<?> set1, Set<?> set2) {
		CCSMAssert.isFalse(set1.isEmpty() || set2.isEmpty(),
				"Sets must not be empty");
		Set<?> intersection = new HashSet<Object>(set1);
		intersection.retainAll(set2);
		double numerator = intersection.size();
		double denominator = Math.sqrt(set1.size()) * Math.sqrt(set2.size());
		return numerator / denominator;
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return items.toString();
	}

}
