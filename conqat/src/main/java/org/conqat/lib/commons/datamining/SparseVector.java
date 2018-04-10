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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.conqat.lib.commons.collections.CollectionUtils;

/**
 * A sparse vector in the n-dimensional space of a numerical type T. Basically a
 * mapping from component (represented as an Integer) to a double value. Unset
 * components correspond to 0, i.e. a newly created instance represents the
 * 0-vector.
 * 
 * @author $Author: heineman $
 * @version $Rev: 41796 $
 * @ConQAT.Rating YELLOW Hash: 93F49C08CA9CAE9644E44493F58AE412
 */
public class SparseVector {

	/** The data of this vector */
	private Map<Integer, Double> data = new HashMap<Integer, Double>();

	/**
	 * Sets the given value under the given key, value must not be
	 * <code>null</code>
	 */
	public void set(int key, double value) {
		data.put(key, value);
	}

	/**
	 * Computes the cosine distance between this and the given vector. See also
	 * http://en.wikipedia.org/wiki/Cosine_similarity
	 */
	public double cosineSimilarity(SparseVector other) {

		double dotProduct = 0.0d;

		@SuppressWarnings("unchecked")
		Collection<Integer> commonPositions = CollectionUtils.intersectionSet(
				data.keySet(), other.data.keySet());

		for (Integer commonPosition : commonPositions) {
			dotProduct += data.get(commonPosition)
					* other.data.get(commonPosition);
		}

		double divisor = l2norm() * other.l2norm();

		if (divisor == 0) {
			return 0;
		}

		return dotProduct / divisor;
	}

	/** Returns the L2 norm of this vector. */
	private double l2norm() {
		double tmp = 0.0d;
		for (Integer position : data.keySet()) {
			tmp += Math.pow(data.get(position), 2);
		}
		return Math.sqrt(tmp);
	}
}
