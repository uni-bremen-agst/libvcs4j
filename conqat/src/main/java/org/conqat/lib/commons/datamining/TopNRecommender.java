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
import java.util.List;
import java.util.Set;

import org.conqat.lib.commons.assertion.CCSMAssert;
import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.collections.CounterSet;
import org.conqat.lib.commons.collections.UnmodifiableSet;

/**
 * Trivial recommender that always returns the top n used items from the
 * training data, independent of the query. The confidence is set to a fixed
 * value of .5 for all recommendations.
 * 
 * @author $Author: heineman $
 * @version $Rev: 41791 $
 * @ConQAT.Rating YELLOW Hash: DE84E46C3E55F690A1D0FE2492A47946
 */
public class TopNRecommender<T> implements IRecommender<T> {

	/** The fixed set of recommendations */
	private final Set<Recommendation<T>> recommendations = new HashSet<Recommendation<T>>();

	/**
	 * Constructs a new {@link TopNRecommender} using the given rating data
	 * base. There have to be at least numRecommendations entries in the data
	 * base.
	 */
	public TopNRecommender(RecommenderRatingDatabase<T> ratingDatabase,
			int numRecommendations) {

		CounterSet<T> occurences = new CounterSet<T>();
		for (IRecommenderUser user : ratingDatabase.getUsers()) {
			occurences.incAll(ratingDatabase.getLikedItems(user));
		}

		CCSMAssert.isTrue(occurences.getKeys().size() >= numRecommendations,
				"There have to be at least numRecommendation distinct items");

		List<T> topItems = occurences.getKeysByValueDescending();

		for (int i = 0; i < numRecommendations; i++) {
			// We give each recommendation a fixed 'dummy' confidence of .5
			recommendations.add(new Recommendation<T>(topItems.get(i), .5d));
		}
	}

	/** {@inheritDoc} */
	@Override
	public UnmodifiableSet<Recommendation<T>> recommend(IRecommenderUser user) {
		return CollectionUtils.asUnmodifiable(recommendations);
	}
}
