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

import java.util.Iterator;

/**
 * This is a wrapper for a {@link Iterator} prohibiting all calls which would
 * modify its owning container. All prohibited methods throw an
 * {@link UnsupportedOperationException}.
 * 
 * @author Benjamin Hummel
 * @author $Author: kinnen $
 * 
 * @version $Revision: 41751 $
 * @ConQAT.Rating GREEN Hash: 754617399E6F78CDAA8A3AE5522028DD
 */
public class UnmodifiableIterator<E> implements Iterator<E> {

	/** The underlying iterator. */
	private final Iterator<E> i;

	/**
	 * Creates a new unmodifiable iterator from another iterator. All
	 * modifications to the underlying iterator will directly be visible in this
	 * wrapper.
	 */
	public UnmodifiableIterator(Iterator<E> i) {
		if (i == null) {
			throw new IllegalArgumentException(
					"Underlying iterator may not be null!");
		}
		this.i = i;
	}

	/** {@inheritDoc} */
	@Override
	public boolean hasNext() {
		return i.hasNext();
	}

	/** {@inheritDoc} */
	@Override
	public E next() {
		return i.next();
	}

	/**
	 * Operation is not supported.
	 * 
	 * @throws UnsupportedOperationException
	 */
	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}
}