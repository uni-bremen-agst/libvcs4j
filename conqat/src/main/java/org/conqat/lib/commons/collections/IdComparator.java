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

/**
 * Comparator that compares objects based on the identifiers provided by a
 * {@link IIdProvider}.
 * 
 * @author deissenb
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: EF0D00094977248987F024804890C2A8
 */
public class IdComparator<I extends Comparable<I>, T> extends
		IdentifierBasedComparatorBase<I, T> {

	/** ID provider used for comparing. */
	private final IIdProvider<I, T> idProvider;

	/** Create new comparator. */
	public IdComparator(IIdProvider<I, T> idProvider) {
		this.idProvider = idProvider;
	}

	/**
	 * Obtain identifier from identifier provider.
	 * 
	 * @throws NullPointerException
	 *             if the id provider returns <code>null</code>.
	 */
	@Override
	protected I obtainIdentifier(T t) {
		I id = idProvider.obtainId(t);
		if (id == null) {
			throw new NullPointerException("Id for " + t + " is null.");
		}
		return id;
	}
}