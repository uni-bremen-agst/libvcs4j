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
import java.util.HashSet;
import java.util.Set;

import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.collections.ListMap;
import org.conqat.lib.commons.collections.UnmodifiableSet;

/**
 * Binary rating database. Rating is either 'like' or 'unrated'.
 * 
 * TODO (BH): Do not understand the comment. The terms 'rating' and 'unrated'
 * are not used once in the code. TODO(BH): I do not like the term "rating"
 * here, as in this library we mean by rating the ConQAT rating (RED, GREEN,
 * YELLOW). TODO (BH): The entire class seems to be just a thin wrapper around a
 * ListMap (maybe better a SetMap, as getter returns set?) with changed names
 * and a static calculation method. What is the benefit of using this class over
 * just returning a SetMap from the static method?
 * 
 * TODO(LH) The term rating is a fixed term in the area of recommendation
 * system. Therefore I would really like to keep it. I used the more specific
 * term 'recommender rating' to distinguish to LeVD rating.
 * 
 * TODO (LH) Also I'd like to keep this wrapper as the methods have speeking
 * names and thus improving comprehensibility IMHO.
 * 
 * @author $Author: heineman $
 * @version $Rev: 41808 $
 * @ConQAT.Rating YELLOW Hash: B23AEFBB99EA5778E94436C0A7DBB704
 */
public class RecommenderRatingDatabase<T> implements Serializable {

	/** Serial ID */
	private static final long serialVersionUID = 1L;

	/** The data */
	private final ListMap<IRecommenderUser, T> data = new ListMap<IRecommenderUser, T>();

	/** Returns all users contained in this database. */
	public UnmodifiableSet<IRecommenderUser> getUsers() {
		return CollectionUtils.asUnmodifiable(data.getKeys());
	}

	/** Adds the given users to this rating database. */
	public void add(IRecommenderUser user, Set<T> likedItems) {
		data.addAll(user, likedItems);
	}

	/** Removes the user completely */
	public void remove(IRecommenderUser user) {
		data.removeCollection(user);
	}

	/**
	 * Returns the items liked by the given user. If no entry for this user is
	 * found an IllegalArgumentException is thrown.
	 */
	public Set<T> getLikedItems(IRecommenderUser user) {
		if (!data.containsCollection(user)) {
			throw new IllegalArgumentException("No such user: " + user);
		}
		return new HashSet<T>(data.getCollection(user));
	}

	/**
	 * Constructs a new {@link RecommenderRatingDatabase} from the given
	 * shopping baskets
	 */
	public static <T> RecommenderRatingDatabase<T> fromShoppingBaskets(
			Set<Set<T>> shoppingBaskets) {
		RecommenderRatingDatabase<T> ratingDatabase = new RecommenderRatingDatabase<T>();
		for (Set<T> basket : shoppingBaskets) {
			ratingDatabase.add(new ShoppingBasketUser<T>(basket), basket);
		}
		return ratingDatabase;
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return data.toString();
	}

}
