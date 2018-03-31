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
import java.util.Collection;
import java.util.Collections;

/**
 * This is a wrapper for a {@link Collection} prohibiting all calls which would
 * modify its contents. As the construction of this class is performed in
 * constant time it is preferred over copying the collection (which takes linear
 * time). Using this class is also preferred to using the
 * <code>unmodifiableX()</code> in class {@link Collections} as they return the
 * collection base type that does not signal, that the object is unmodifiable.
 * Using the classes in this package makes unmodifiability more explicit.
 * <p>
 * All prohibited methods throw an {@link UnsupportedOperationException}. The
 * class is nearly the same as the one returned by
 * {@link Collections#unmodifiableCollection(Collection)}, but by making it a
 * public class we can make the return value of some methods more explicit.
 * <p>
 * This collection is serializable if the wrapped collection is serializable.
 * 
 * @author $Author: heinemann $
 * @version $Revision: 42189 $
 * @ConQAT.Rating GREEN Hash: 9E9DB2D2CA31AD249EE8F3C793491D4B
 */
public class UnmodifiableCollection<E> implements Collection<E>, Serializable {

	/** Version used for serialization. */
	private static final long serialVersionUID = 1;

	/** The underlying collection. */
	private final Collection<E> c;

	/**
	 * Creates a new unmodifiable collection from another collection. All
	 * modifications to the underlying collection will directly be visible in
	 * this wrapper.
	 */
	public UnmodifiableCollection(Collection<E> c) {
		if (c == null) {
			throw new IllegalArgumentException(
					"Underlying collection may not be null!");
		}
		this.c = c;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isEmpty() {
		return c.isEmpty();
	}

	/** {@inheritDoc} */
	@Override
	public int size() {
		return c.size();
	}

	/** {@inheritDoc} */
	@Override
	public boolean contains(Object o) {
		return c.contains(o);
	}

	/** {@inheritDoc} */
	@Override
	public boolean containsAll(Collection<?> other) {
		return c.containsAll(other);
	}

	/** {@inheritDoc} */
	@Override
	public UnmodifiableIterator<E> iterator() {
		return new UnmodifiableIterator<E>(c.iterator());
	}

	/** {@inheritDoc} */
	@Override
	public Object[] toArray() {
		return c.toArray();
	}

	/** {@inheritDoc} */
	@Override
	public <T> T[] toArray(T[] a) {
		return c.toArray(a);
	}

	/**
	 * Operation is not supported.
	 * 
	 * @throws UnsupportedOperationException
	 */
	@Override
	public boolean add(E arg0) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Operation is not supported.
	 * 
	 * @throws UnsupportedOperationException
	 */
	@Override
	public boolean addAll(Collection<? extends E> arg0) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Operation is not supported.
	 * 
	 * @throws UnsupportedOperationException
	 */
	@Override
	public void clear() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Operation is not supported.
	 * 
	 * @throws UnsupportedOperationException
	 */
	@Override
	public boolean remove(Object arg0) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Operation is not supported.
	 * 
	 * @throws UnsupportedOperationException
	 */
	@Override
	public boolean removeAll(Collection<?> arg0) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Operation is not supported.
	 * 
	 * @throws UnsupportedOperationException
	 */
	@Override
	public boolean retainAll(Collection<?> arg0) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Returns a string representation of this collection.
	 */
	@Override
	public String toString() {
		return c.toString();
	}

	/** {@inheritDoc} */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof UnmodifiableCollection<?>) {
			return c.equals(((UnmodifiableCollection<?>) obj).c);
		}
		return c.equals(obj);
	}

	/** {@inheritDoc} */
	@Override
	public int hashCode() {
		return c.hashCode();
	}
}