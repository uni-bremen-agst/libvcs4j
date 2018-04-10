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

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * This is a wrapper for a {@link List} prohibiting all calls which would modify
 * its contents. As the construction of this class is performed in constant time
 * it is preferred over copying the list (which takes linear time). Using this
 * class is also preferred to using the <code>unmodifiableX()</code> in class
 * {@link Collections} as they return the collection base type that does not
 * signal that the object is unmodifiable. Using the classes in this package
 * makes unmodifiability more explicit.
 * <p>
 * All prohibited methods throw an {@link UnsupportedOperationException}. The
 * class is nearly the same as the one returned by
 * {@link Collections#unmodifiableList(List)}, but by making it a public class
 * we can make the return value of some methods more explicit.
 * <p>
 * This list is serializable if the wrapped list is serializable.
 * 
 * @author Benjamin Hummel
 * @author $Author: goeb $
 * 
 * @version $Revision: 48949 $
 * @ConQAT.Rating GREEN Hash: BB18779C5194C8641B276FFFFE9C39BB
 */
public class UnmodifiableList<E> extends UnmodifiableCollection<E> implements
		List<E> {

	/** Version used for serialization. */
	private static final long serialVersionUID = 1;

	/** The underlying list. */
	private final List<E> l;

	/**
	 * Creates a new unmodifiable list from another list. All modifications to
	 * the underlying list will directly be visible in this wrapper.
	 */
	public UnmodifiableList(List<E> l) {
		super(l);
		this.l = l;
	}

	/** {@inheritDoc} */
	@Override
	public E get(int index) {
		return l.get(index);
	}

	/** {@inheritDoc} */
	@Override
	public int indexOf(Object o) {
		return l.indexOf(o);
	}

	/** {@inheritDoc} */
	@Override
	public int lastIndexOf(Object o) {
		return l.lastIndexOf(o);
	}

	/** {@inheritDoc} */
	@Override
	public UnmodifiableListIterator<E> listIterator() {
		return new UnmodifiableListIterator<E>(l.listIterator());
	}

	/** {@inheritDoc} */
	@Override
	public UnmodifiableListIterator<E> listIterator(int index) {
		return new UnmodifiableListIterator<E>(l.listIterator(index));
	}

	/** {@inheritDoc} */
	@Override
	public List<E> subList(int fromIndex, int toIndex) {
		return new UnmodifiableList<E>(l.subList(fromIndex, toIndex));
	}

	/**
	 * Operation is not supported.
	 * 
	 * @throws UnsupportedOperationException
	 */
	@Override
	public void add(int arg0, E arg1) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Operation is not supported.
	 * 
	 * @throws UnsupportedOperationException
	 */
	@Override
	public boolean addAll(int arg0, Collection<? extends E> arg1) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Operation is not supported.
	 * 
	 * @throws UnsupportedOperationException
	 */
	@Override
	public E remove(int arg0) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Operation is not supported.
	 * 
	 * @throws UnsupportedOperationException
	 */
	@Override
	public E set(int arg0, E arg1) {
		throw new UnsupportedOperationException();
	}
}