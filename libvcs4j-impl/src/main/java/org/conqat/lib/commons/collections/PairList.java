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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import org.conqat.lib.commons.assertion.CCSMPre;
import org.conqat.lib.commons.equals.HashCodeUtils;

/**
 * A list for storing pairs in a specific order.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 51770 $
 * @ConQAT.Rating GREEN Hash: 7459D6D0F59028B37DD23DD091BDCEEA
 */
public class PairList<S, T> implements Serializable {

	/** Version used for serialization. */
	private static final long serialVersionUID = 1;

	/** The current size. */
	private int size = 0;

	/** The array used for storing the S. */
	private Object[] firstElements;

	/** The array used for storing the T. */
	private Object[] secondElements;

	/** Constructor. */
	public PairList() {
		this(16);
	}

	/** Constructor. */
	public PairList(int initialCapacity) {
		if (initialCapacity < 1) {
			initialCapacity = 1;
		}
		firstElements = new Object[initialCapacity];
		secondElements = new Object[initialCapacity];
	}

	/** Copy constructor. */
	public PairList(PairList<S, T> other) {
		this(other.size);
		addAll(other);
	}

	/**
	 * Constructor to convert a map into a pair list.
	 */
	public PairList(Map<S, T> map) {
		this(map.size());
		for (Entry<S, T> entry : map.entrySet()) {
			add(entry.getKey(), entry.getValue());
		}
	}

	/** Returns whether the list is empty. */
	public boolean isEmpty() {
		return size == 0;
	}

	/** Returns the size of the list. */
	public int size() {
		return size;
	}

	/** Add the given pair to the list. */
	public void add(S first, T second) {
		ensureSpace(size + 1);
		firstElements[size] = first;
		secondElements[size] = second;
		++size;
	}

	/** Adds all pairs from another list. */
	public void addAll(PairList<S, T> other) {
		// we have to store this in a local var, as other.size may change if
		// other == this
		int otherSize = other.size;

		ensureSpace(size + otherSize);
		for (int i = 0; i < otherSize; ++i) {
			firstElements[size] = other.firstElements[i];
			secondElements[size] = other.secondElements[i];
			++size;
		}
	}

	/** Make sure there is space for at least the given amount of elements. */
	protected void ensureSpace(int space) {
		if (space <= firstElements.length) {
			return;
		}

		Object[] oldFirst = firstElements;
		Object[] oldSecond = secondElements;
		int newSize = firstElements.length * 2;
		while (newSize < space) {
			newSize *= 2;
		}

		firstElements = new Object[newSize];
		secondElements = new Object[newSize];
		System.arraycopy(oldFirst, 0, firstElements, 0, size);
		System.arraycopy(oldSecond, 0, secondElements, 0, size);
	}

	/** Returns the first element at given index. */
	@SuppressWarnings("unchecked")
	public S getFirst(int i) {
		checkWithinBounds(i);
		return (S) firstElements[i];
	}

	/**
	 * Checks whether the given <code>i</code> is within the bounds. Throws an
	 * exception otherwise.
	 */
	private void checkWithinBounds(int i) {
		if (i < 0 || i >= size) {
			throw new IndexOutOfBoundsException("Out of bounds: " + i);
		}
	}

	/** Sets the first element at given index. */
	public void setFirst(int i, S value) {
		checkWithinBounds(i);
		firstElements[i] = value;
	}

	/** Returns the second element at given index. */
	@SuppressWarnings("unchecked")
	public T getSecond(int i) {
		checkWithinBounds(i);
		return (T) secondElements[i];
	}

	/** Sets the first element at given index. */
	public void setSecond(int i, T value) {
		checkWithinBounds(i);
		secondElements[i] = value;
	}

	/** Creates a new list containing all first elements. */
	@SuppressWarnings("unchecked")
	public List<S> extractFirstList() {
		List<S> result = new ArrayList<S>(size + 1);
		for (int i = 0; i < size; ++i) {
			result.add((S) firstElements[i]);
		}
		return result;
	}

	/** Creates a new list containing all second elements. */
	@SuppressWarnings("unchecked")
	public List<T> extractSecondList() {
		List<T> result = new ArrayList<T>(size + 1);
		for (int i = 0; i < size; ++i) {
			result.add((T) secondElements[i]);
		}
		return result;
	}

	/**
	 * Swaps the pairs of this list. Is S and T are different types, this will
	 * be extremely dangerous.
	 */
	public void swapPairs() {
		Object[] temp = firstElements;
		firstElements = secondElements;
		secondElements = temp;
	}

	/** Swaps the entries located at indexes i and j. */
	public void swapEntries(int i, int j) {
		S tmp1 = getFirst(i);
		T tmp2 = getSecond(i);
		setFirst(i, getFirst(j));
		setSecond(i, getSecond(j));
		setFirst(j, tmp1);
		setSecond(j, tmp2);
	}

	/** Clears this list. */
	public void clear() {
		size = 0;
	}

	/** Removes the last element of the list. */
	public void removeLast() {
		CCSMPre.isTrue(size > 0, "Size must be positive!");
		size -= 1;
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		StringBuffer result = new StringBuffer();
		result.append('[');
		for (int i = 0; i < size; i++) {
			if (i != 0) {
				result.append(',');
			}
			result.append('(');
			result.append(String.valueOf(firstElements[i]));
			result.append(',');
			result.append(String.valueOf(secondElements[i]));
			result.append(')');
		}
		result.append(']');
		return result.toString();
	}

	/** {@inheritDoc} */
	@Override
	public int hashCode() {
		int prime = 31;
		int hash = size;
		hash = prime * hash
				+ HashCodeUtils.hashArrayPart(firstElements, 0, size);
		return prime * hash
				+ HashCodeUtils.hashArrayPart(secondElements, 0, size);
	}

	/** {@inheritDoc} */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof PairList)) {
			return false;
		}

		PairList<?, ?> other = (PairList<?, ?>) obj;
		if (size != other.size) {
			return false;
		}
		for (int i = 0; i < size; i++) {
			if (!Objects.equals(firstElements[i], other.firstElements[i])
					|| !Objects.equals(secondElements[i], secondElements[i])) {
				return false;
			}
		}
		return true;
	}
}