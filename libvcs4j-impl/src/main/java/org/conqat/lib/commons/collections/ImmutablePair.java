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
package org.conqat.lib.commons.collections;

import java.io.Serializable;

import org.conqat.lib.commons.clone.CloneUtils;
import org.conqat.lib.commons.clone.DeepCloneException;
import org.conqat.lib.commons.clone.IDeepCloneable;

/**
 * Simple readonly pair class.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 46B153FAFB516B8827C4D1950BC8D934
 */
public class ImmutablePair<S, T> implements Cloneable, IDeepCloneable,
		Comparable<ImmutablePair<S, T>>, Serializable {

	/** Version used for serialization. */
	private static final long serialVersionUID = 1;

	/** The first element. */
	protected S first;

	/** The second element. */
	protected T second;

	/** Constructor. */
	public ImmutablePair(S first, T second) {
		this.first = first;
		this.second = second;
	}

	/** Copy constructor. */
	public ImmutablePair(ImmutablePair<S, T> p) {
		this.first = p.first;
		this.second = p.second;
	}

	/** Returns the first element of the pair. */
	public S getFirst() {
		return first;
	}

	/** Returns the second element of the pair. */
	public T getSecond() {
		return second;
	}

	/** {@inheritDoc} */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ImmutablePair<?, ?>)) {
			return false;
		}
		ImmutablePair<?, ?> p = (ImmutablePair<?, ?>) obj;
		return areEqual(first, p.first) && areEqual(second, p.second);
	}

	/** Returns true if either both are <code>null</code> or they are equal. */
	private boolean areEqual(Object o1, Object o2) {
		if (o1 == null) {
			return o2 == null;
		}
		return o1.equals(o2);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * The hash code is based on the hash code of the first and second members.
	 */
	@Override
	public int hashCode() {
		int firstCode = 1;
		if (first != null) {
			firstCode = first.hashCode();
		}

		int secondCode = 1;
		if (second != null) {
			secondCode = second.hashCode();
		}

		return firstCode + 1013 * secondCode;
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return "(" + first + "," + second + ")";
	}

	/** {@inheritDoc} */
	@Override
	protected ImmutablePair<S, T> clone() {
		return new ImmutablePair<S, T>(this);
	}

	/** {@inheritDoc} */
	@Override
	@SuppressWarnings("unchecked")
	public ImmutablePair<S, T> deepClone() throws DeepCloneException {
		S newFirst = (S) CloneUtils.cloneAsDeepAsPossible(first);
		T newSecond = (T) CloneUtils.cloneAsDeepAsPossible(second);
		return new ImmutablePair<S, T>(newFirst, newSecond);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Compare based on first element. Use second element only if first elements
	 * are equal. Null entries are sorted to the top.
	 */
	@Override
	public int compareTo(ImmutablePair<S, T> pair) {
		int cmp = objCompare(first, pair.first);
		if (cmp != 0) {
			return cmp;
		}
		return objCompare(second, pair.second);
	}

	/**
	 * Performs comparison on two arbitrary objects of the same type.
	 */
	@SuppressWarnings("unchecked")
	private <O> int objCompare(O o1, O o2) {
		if (o1 == null) {
			if (o2 == null) {
				return 0;
			}
			return -1;
		} else if (o2 == null) {
			return 1;
		}

		if ((o1 instanceof Comparable) && (o2 instanceof Comparable)) {
			try {
				return ((Comparable<Object>) o1).compareTo(o2);
			} catch (ClassCastException e) {
				// somehow failed, so continue and treat as if not comparable
			}
		}

		// compare using hash code, so we get at least an approximation of an
		// ordering
		int h1 = o1.hashCode();
		int h2 = o2.hashCode();

		if (h1 == h2) {
			return 0;
		}
		if (h1 < h2) {
			return -1;
		}
		return 1;
	}
}