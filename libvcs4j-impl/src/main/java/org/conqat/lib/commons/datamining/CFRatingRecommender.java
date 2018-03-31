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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.conqat.lib.commons.collections.Pair;

/**
 * A user rating-based recommender using collaborative filtering.
 * 
 * @author $Author: heineman $
 * @version $Rev: 41791 $
 * @ConQAT.Rating YELLOW Hash: 0F2B85C728C040FB685139CF23B1219A
 */
public class CFRatingRecommender<T> implements IRecommender<T> {

	/** The database containing the ratings. */
	private RecommenderRatingDatabase<T> ratingDatabase;
	/** Number of neighbors to consider for computing recommendations */
	private int numNeighbors;
	/** Maximum number of recommendations */
	private int maxRecommendations;

	/** Constructor */
	public CFRatingRecommender(RecommenderRatingDatabase<T> ratingDatabase,
			int numNeighbors, int maxRecommendations) {
		this.ratingDatabase = ratingDatabase;
		this.numNeighbors = numNeighbors;
		this.maxRecommendations = maxRecommendations;
	}

	/** {@inheritDoc} */
	@Override
	public Set<Recommendation<T>> recommend(IRecommenderUser queryUser) {
		Set<IRecommenderUser> users = ratingDatabase.getUsers();
		List<Pair<Double, IRecommenderUser>> neighbors = new ArrayList<Pair<Double, IRecommenderUser>>();
		for (IRecommenderUser user : users) {
			if (user.equals(queryUser)) {
				continue;
			}
			neighbors.add(new Pair<Double, IRecommenderUser>(user.similarity(queryUser),
					user));
		}

		Collections.sort(neighbors);
		Collections.reverse(neighbors);

		neighbors = neighbors.subList(0, numNeighbors);

		Set<Recommendation<T>> result = new HashSet<Recommendation<T>>();
		final Map<T, Double> recommendedItems = new HashMap<T, Double>();
		double sumSimilarity = 0;

		Set<T> userItems = ratingDatabase.getLikedItems(queryUser);
		for (Pair<Double, IRecommenderUser> neighbor : neighbors) {
			sumSimilarity += neighbor.getFirst();
			Set<T> neighborItems = ratingDatabase.getLikedItems(neighbor
					.getSecond());
			for (T item : neighborItems) {
				if (!userItems.contains(item)) {
					if (!recommendedItems.containsKey(item)) {
						recommendedItems.put(item, 0d);
					}
					recommendedItems.put(item, recommendedItems.get(item)
							+ neighbor.getFirst());
				}
			}
		}

		List<T> sortedItems = new ArrayList<T>(recommendedItems.keySet());
		Collections.sort(sortedItems, new Comparator<T>() {
			@Override
			public int compare(T item1, T item2) {
				return recommendedItems.get(item2).compareTo(
						recommendedItems.get(item1));
			}
		});

		while (result.size() < maxRecommendations && !sortedItems.isEmpty()) {
			T item = sortedItems.get(0);
			double confidence = 0;
			if (sumSimilarity > 0) {
				confidence = recommendedItems.get(item) / sumSimilarity;
			}
			result.add(new Recommendation<T>(item, confidence));
			sortedItems.remove(0);
		}

		return result;

	}

}
