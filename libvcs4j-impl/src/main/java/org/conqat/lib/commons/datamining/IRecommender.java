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

import java.util.Set;

/**
 * Recommender that can produce recommendations for a user of the recommendation
 * system. A concrete recommender uses different aspects of the user to
 * determine the recommendations.
 * 
 * @param <T>
 *            the item type to be recommended
 * 
 * @author $Author: heineman $
 * @version $Rev: 41782 $
 * @ConQAT.Rating YELLOW Hash: BDDED7E42FD4DC2AD123476455D247E6
 */
public interface IRecommender<T> {

	/** Computes a set of recommendations for the given user */
	Set<Recommendation<T>> recommend(IRecommenderUser user);

}
