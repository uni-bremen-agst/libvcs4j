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
package org.conqat.lib.commons.datamining;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.conqat.lib.commons.assertion.CCSMAssert;
import org.conqat.lib.commons.collections.CollectionUtils;

/**
 * A-priori algorithm for mining frequent item sets from shopping baskets. See
 * http://en.wikipedia.org/wiki/Apriori_algorithm
 * 
 * @author $Author: heineman $
 * @version $Rev: 41779 $
 * @ConQAT.Rating YELLOW Hash: C20D99B23EB9F335E327D1EC7A0AFC55
 */
public class FrequentItemSetMiner<T> {

	/** Threshold for support */
	private final double supportThreshold;

	/**
	 * Constructs a new {@link FrequentItemSetMiner}.
	 * 
	 * @param supportThreshold
	 *            [0..1] denotes in what fraction of baskets an item set must
	 *            occur to be considered frequent.
	 */
	public FrequentItemSetMiner(double supportThreshold) {
		CCSMAssert.isTrue(supportThreshold >= 0 && supportThreshold <= 1,
				"supportThreshold must be in [0,1]");
		this.supportThreshold = supportThreshold;
	}

	/**
	 * Mines frequent item sets from the given shopping baskets. The support
	 * threshold is the fraction of baskets from which a frequent item set is a
	 * subset. The commodity factor is used to ignore items that are purchased
	 * extremely often. If an item occurs in the fraction commodityFactor of the
	 * baskets, it is ignored for identifying frequent item sets. Elements in
	 * baskets must be hashable.
	 * 
	 * @param baskets
	 *            the baskets to be analyzed.
	 */
	public Set<FrequentItemSet<T>> mineFrequentItemSets(Set<Set<T>> baskets) {

		Set<FrequentItemSet<T>> result = new HashSet<FrequentItemSet<T>>();

		// The choice of the names for the identifiers of the local variables
		// are intended and originate from the Wikipedia page (see class
		// comment).
		Map<Integer, Set<Set<T>>> L_ks = new HashMap<Integer, Set<Set<T>>>();

		// compute all item sets of size 1 with support >= supportThreshold
		HashSet<Set<T>> singletonItemSets = new HashSet<Set<T>>();
		L_ks.put(1, singletonItemSets);
		for (T item : CollectionUtils.unionSetAll(baskets)) {
			Set<T> singletonItemSet = Collections.singleton(item);
			double support = support(singletonItemSet, baskets);
			if (support >= supportThreshold) {
				singletonItemSets.add(singletonItemSet);
				result.add(new FrequentItemSet<T>(singletonItemSet, support));
			}
		}

		int k = 1;

		while (true) {
			// generate frequent item sets of size k+1 from frequent item sets
			// of size k

			Set<Set<T>> candidates = apriori_gen(L_ks.get(k), k + 1);

			Set<Set<T>> L_k = new HashSet<Set<T>>();
			L_ks.put(k + 1, L_k);

			for (Set<T> candidate : candidates) {
				double support = support(candidate, baskets);
				if (support >= supportThreshold) {
					L_k.add(candidate);
					result.add(new FrequentItemSet<T>(candidate, support));
				}
			}

			if (L_k.isEmpty()) {
				// We're done.
				break;
			}

			k++;

		}

		return result;
	}

	/** Generate candidate item sets */
	private Set<Set<T>> apriori_gen(Set<Set<T>> L_k_1, int k) {
		Set<Set<T>> C_k = new HashSet<Set<T>>();

		List<Set<T>> asList = new ArrayList<Set<T>>(L_k_1);
		for (int i = 0; i < asList.size(); i++) {
			inner: for (int j = i + 1; j < asList.size(); j++) {
				@SuppressWarnings("unchecked")
				Set<T> union = CollectionUtils.unionSet(asList.get(i),
						asList.get(j));
				if (union.size() == k) {
					for (T item : union) {
						Set<T> check = new HashSet<T>(union);
						check.remove(item);
						if (!L_k_1.contains(check)) {
							continue inner;
						}
					}
					C_k.add(union);
				}
			}
		}

		return C_k;
	}

	/** Returns the support of itemSet within the baskets. */
	private static <T> double support(Set<T> itemSet, Set<Set<T>> baskets) {
		int count = 0;

		for (Set<T> basket : baskets) {
			if (basket.containsAll(itemSet)) {
				count++;
			}
		}

		return (double) count / baskets.size();
	}

}