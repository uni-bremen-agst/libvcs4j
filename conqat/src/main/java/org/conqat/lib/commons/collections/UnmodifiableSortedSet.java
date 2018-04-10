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

import java.util.Collections;
import java.util.Comparator;
import java.util.SortedSet;

/**
 * This is a wrapper for a {@link SortedSet} prohibiting all calls which would
 * modify its contents. As the construction of this class is performed in
 * constant time it is prefered over copying the set (which takes linear time).
 * Using this class is also preferred to using the <code>unmodifiableX()</code>
 * in class {@link Collections} as they return the collection base type that
 * does not signal, that the object ist unmodifiable. Using the classes in this
 * package makes unmodifiability more explicit.
 * <p>
 * All prohibited methods throw an {@link UnsupportedOperationException}. The
 * class is nearly the same as the one returned by
 * {@link Collections#unmodifiableSortedSet(java.util.SortedSet)}, but by
 * making it a public class we can make the return value of some methods more
 * explicit.
 * 
 * @author Benjamin Hummel
 * @author $Author: kinnen $
 * 
 * @version $Revision: 41751 $
 * @ConQAT.Rating GREEN Hash: C61E1EF11ED7B60417548E1586849DE9
 */
public class UnmodifiableSortedSet<E> extends UnmodifiableSet<E> implements
		SortedSet<E> {

	/** Version used for serialization. */
	private static final long serialVersionUID = 1;

	/** The underlying sorted set. */
	private final SortedSet<E> s;

	/**
	 * Creates a new unmodifiable sorted set from another sorted set. All
	 * modifications to the underlying set will directly be visible in this
	 * wrapper.
	 */
	public UnmodifiableSortedSet(SortedSet<E> s) {
		super(s);
		this.s = s;
	}

	/** {@inheritDoc} */
	@Override
	public Comparator<? super E> comparator() {
		return s.comparator();
	}

	/** {@inheritDoc} */
	@Override
	public E first() {
		return s.first();
	}

	/** {@inheritDoc} */
	@Override
	public UnmodifiableSortedSet<E> headSet(E toElement) {
		return new UnmodifiableSortedSet<E>(s.headSet(toElement));
	}

	/** {@inheritDoc} */
	@Override
	public E last() {
		return s.last();
	}

	/** {@inheritDoc} */
	@Override
	public UnmodifiableSortedSet<E> subSet(E fromElement, E toElement) {
		return new UnmodifiableSortedSet<E>(s.subSet(fromElement, toElement));
	}

	/** {@inheritDoc} */
	@Override
	public UnmodifiableSortedSet<E> tailSet(E fromElement) {
		return new UnmodifiableSortedSet<E>(s.tailSet(fromElement));
	}
}