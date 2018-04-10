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
package org.conqat.lib.commons.datamining;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Set;

import org.conqat.lib.commons.collections.CollectionUtils;

/**
 * An association rule is of the form I -> j, where I is a set of items and j is
 * an item. The rule means that if all items of I appear in a shopping basket,
 * then j is "likely" to appear in that basket as well. Likely is defined by the
 * confidence which is computed as the ratio between the number of baskets
 * containing both I <b>and</b> j and the number of baskets that contain I.
 * 
 * @author $Author: heinemann $
 * @version $Rev: 47174 $
 * @ConQAT.Rating YELLOW Hash: 03667B06CAE75AECAD5BB0919068E622
 */
public class AssociationRule<T> implements Serializable {

	/** Version used for serialization. */
	private static final long serialVersionUID = 1;

	/** The item set (i.e. the I in I->j) */
	private Set<T> itemSet;

	/** The associated item (i.e. the j in I->j) */
	private T associatedItem;

	/** The confidence level [0..1] */
	private double confidence;

	/** Constructor */
	public AssociationRule(Set<T> itemSet, T associatedItem, double confidence) {
		this.itemSet = itemSet;
		this.associatedItem = associatedItem;
		this.confidence = confidence;
	}

	/** Returns the item set */
	public Set<T> getItemSet() {
		return itemSet;
	}

	/** Returns the associated item */
	public T getAssociatedItem() {
		return associatedItem;
	}

	/** Returns the confidence. */
	public double getConfidence() {
		return confidence;
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return CollectionUtils.sort(itemSet, new Comparator<T>() {
			@Override
			public int compare(T t1, T t2) {
				return t1.toString().compareTo(t2.toString());
			}
		}).toString() + " -> " + associatedItem + " (" + confidence + ")";
	}
}