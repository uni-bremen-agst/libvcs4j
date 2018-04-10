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

import java.util.ListIterator;

/**
 * This is a wrapper for a {@link ListIterator} prohibiting all calls which
 * would modify its owning container. All prohibited methods throw an
 * {@link UnsupportedOperationException}.
 * 
 * @author Benjamin Hummel
 * @author $Author: kinnen $
 * 
 * @version $Revision: 41751 $
 * @ConQAT.Rating GREEN Hash: 484FE5630C414B525B8BB0800C52894E
 */
public class UnmodifiableListIterator<T> extends UnmodifiableIterator<T>
		implements ListIterator<T> {

	/** The underlying iterator. */
	private final ListIterator<T> i;

	/**
	 * Creates a new unmodifiable list iterator from another list iterator. All
	 * modifications to the underlying iterator will directly be visible in this
	 * wrapper.
	 */
	public UnmodifiableListIterator(ListIterator<T> i) {
		super(i);
		this.i = i;
	}

	/** {@inheritDoc} */
	@Override
	public boolean hasPrevious() {
		return i.hasPrevious();
	}

	/** {@inheritDoc} */
	@Override
	public int nextIndex() {
		return i.nextIndex();
	}

	/** {@inheritDoc} */
	@Override
	public T previous() {
		return i.previous();
	}

	/** {@inheritDoc} */
	@Override
	public int previousIndex() {
		return i.previousIndex();
	}

	/**
	 * Operation is not supported.
	 * 
	 * @throws UnsupportedOperationException
	 */
	@Override
	public void add(T o) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Operation is not supported.
	 * 
	 * @throws UnsupportedOperationException
	 */
	@Override
	public void set(T o) {
		throw new UnsupportedOperationException();
	}

}