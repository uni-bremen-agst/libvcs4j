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
 * Predicate that combines other predicates with boolean "or". This supports
 * lazy evaluation. This is immutable and stateless if the inner predicates are
 * immutable and stateless.
 * 
 * @param <T>
 *            the element type the predicate works on.
 * 
 * @author $Author: heinemann $
 * @version $Rev: 46225 $
 * @ConQAT.Rating GREEN Hash: A988C04B22E240D0E8F94106F8EAEFE2
 */
public class OrPredicate<T> implements IPredicate<T> {

	/** The delegate predicates. */
	private final List<IPredicate<T>> innerPredicates;

	/** Constructor. */
	public OrPredicate(IPredicate<T>... innerPredicates) {
		this.innerPredicates = Arrays.asList(innerPredicates);
	}

	/** {@inheritDoc} */
	@Override
	public boolean isContained(T element) {
		for (IPredicate<T> inner : innerPredicates) {
			if (inner.isContained(element)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Factory method for creating an or-predicate. This is used to exploit
	 * generic type inference (i.e. syntactic reasons).
	 */
	public static <T> OrPredicate<T> create(IPredicate<T>... innerPredicates) {
		return new OrPredicate<T>(innerPredicates);
	}
}