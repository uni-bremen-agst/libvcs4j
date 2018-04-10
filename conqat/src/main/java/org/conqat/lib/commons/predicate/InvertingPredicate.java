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

/**
 * Predicate that inverts another predicate.
 * 
 * @param <T>
 *            the element type the predicate works on.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 45908 $
 * @ConQAT.Rating YELLOW Hash: 7B3290EB7D3334DFAA644873563AFE7B
 */
public class InvertingPredicate<T> implements IPredicate<T> {

	/** The delegate predicate. */
	private final IPredicate<T> inner;

	/** Constructor. */
	public InvertingPredicate(IPredicate<T> inner) {
		this.inner = inner;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isContained(T element) {
		return !inner.isContained(element);
	}

	/**
	 * Factory method for creating an inverting predicate. This is used to
	 * exploit generic type inference (i.e. syntactic reasons).
	 */
	public static <T> InvertingPredicate<T> create(IPredicate<T> inner) {
		return new InvertingPredicate<T>(inner);
	}
}