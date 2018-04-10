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

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Based on a set of association rules, this recommender can recommend items for
 * a given basket.
 * 
 * @author $Author: heineman $
 * @version $Rev: 41791 $
 * @ConQAT.Rating YELLOW Hash: 6CAA4CD7ABBED7FEFA3E3D46C32F2C64
 */
public class AssociationRuleRecommender<T> implements IRecommender<T>,
		Serializable {

	/** Serial ID */
	private static final long serialVersionUID = 1L;
	/** The database containing the ratings. */
	private final RecommenderRatingDatabase<T> ratingDatabase;
	/** The mined association rules */
	private final Set<AssociationRule<T>> associationRules;

	/** Constructor. */
	public AssociationRuleRecommender(RecommenderRatingDatabase<T> ratingDatabase,
			float supportThreshold, float confidenceThreshold) {
		this.ratingDatabase = ratingDatabase;
		Set<Set<T>> baskets = new HashSet<Set<T>>();
		for (IRecommenderUser user : ratingDatabase.getUsers()) {
			baskets.add(ratingDatabase.getLikedItems(user));
		}
		associationRules = new AssociationRuleMiner<T>(supportThreshold,
				confidenceThreshold).mineAssociationRules(baskets);
	}

	/**
	 * Recommends items for the given user. The returned set may be empty, if no
	 * recommendations could be made.
	 */
	@Override
	public Set<Recommendation<T>> recommend(IRecommenderUser user) {

		Map<T, Recommendation<T>> itemToRecommendationMap = new HashMap<T, Recommendation<T>>();
		Set<T> items = ratingDatabase.getLikedItems(user);

		for (AssociationRule<T> rule : associationRules) {
			if (items.containsAll(rule.getItemSet())
					&& !items.contains(rule.getAssociatedItem())) {
				T item = rule.getAssociatedItem();
				double confidence = rule.getConfidence();

				Recommendation<T> existingRecommendation = itemToRecommendationMap
						.get(item);

				if (existingRecommendation == null
						|| confidence > existingRecommendation.getConfidence()) {
					itemToRecommendationMap.put(item, new Recommendation<T>(
							item, confidence));
				}
			}
		}
		return new HashSet<Recommendation<T>>(itemToRecommendationMap.values());
	}

}
