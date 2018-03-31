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
package org.conqat.lib.commons.predicate;

import java.util.Arrays;
import java.util.List;

/**
 * Predicate that combines other predicates with boolean "and". This supports
 * lazy evaluation. This is immutable and stateless if the inner predicates are
 * immutable and stateless.
 * 
 * @param <T>
 *            the element type the predicate works on.
 * 
 * @author $Author: heinemann $
 * @version $Rev: 46225 $
 * @ConQAT.Rating GREEN Hash: C3F6273FE258C67EC2E9E56ABDB8ADB2
 */
public class AndPredicate<T> implements IPredicate<T> {

	/** The delegate predicates. */
	private final List<IPredicate<T>> innerPredicates;

	/** Constructor. */
	public AndPredicate(IPredicate<T>... innerPredicates) {
		this.innerPredicates = Arrays.asList(innerPredicates);
	}

	/** {@inheritDoc} */
	@Override
	public boolean isContained(T element) {
		for (IPredicate<T> inner : innerPredicates) {
			if (!inner.isContained(element)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Factory method for creating an and-predicate. This is used to exploit
	 * generic type inference (i.e. syntactic reasons).
	 */
	public static <T> AndPredicate<T> create(IPredicate<T>... innerPredicates) {
		return new AndPredicate<T>(innerPredicates);
	}
}