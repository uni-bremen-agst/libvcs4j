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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.conqat.lib.commons.collections.IdentityHashSet;

/**
 * Mines association rules from a set of shopping baskets. Uses Apriori
 * algorithm. See http://en.wikipedia.org/wiki/Apriori_algorithm.
 * 
 * @param <T>
 *            the item type; must support hashing.
 * 
 * @author $Author: heineman $
 * @version $Rev: 41779 $
 * @ConQAT.Rating YELLOW Hash: DC2B0583F12C65BDC8525FC71EEA7DEF
 */
public class AssociationRuleMiner<T> {

	/** Threshold for confidence */
	private final float confidenceThreshold;

	/** Miner for frequent item sets */
	private final FrequentItemSetMiner<T> itemSetMiner;

	/**
	 * Constructor.
	 * 
	 * @param supportThreshold
	 *            the support threshold [0..1], i.e. the fraction of the baskets
	 *            in which a frequent item set must be present in order to be
	 *            considered.
	 * @param confidenceThreshold
	 *            the minimal confidence of the mined rules [0..1].
	 */
	public AssociationRuleMiner(float supportThreshold,
			float confidenceThreshold) {
		this.confidenceThreshold = confidenceThreshold;
		itemSetMiner = new FrequentItemSetMiner<T>(supportThreshold);
	}

	/** Mines frequent item sets from the given shopping baskets. */
	public Set<AssociationRule<T>> mineAssociationRules(Set<Set<T>> baskets) {

		Set<AssociationRule<T>> result = new IdentityHashSet<AssociationRule<T>>();

		Set<FrequentItemSet<T>> frequentItemSets = itemSetMiner
				.mineFrequentItemSets(baskets);

		Map<Set<T>, Double> supportMap = new HashMap<Set<T>, Double>();

		for (FrequentItemSet<T> frequentItemset : frequentItemSets) {
			supportMap.put(frequentItemset.getItems(),
					frequentItemset.getSupport());
		}

		for (FrequentItemSet<T> frequentItemSet : frequentItemSets) {
			Set<T> items = frequentItemSet.getItems();
			if (items.size() > 1) {
				for (T item : items) {
					Set<T> reducedItemSet = new HashSet<T>(items);
					reducedItemSet.remove(item);

					double confidence = frequentItemSet.getSupport()
							/ supportMap.get(reducedItemSet);
					if (confidence >= confidenceThreshold) {
						result.add(new AssociationRule<T>(reducedItemSet, item,
								confidence));
					}
				}
			}
		}
		return result;
	}

}