/*-------------------------------------------------------------------------+
|                                                                          |
| Copyright 2005-2011 the ConQAT Project                                   |
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

import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Special implementation of a {@link Set} of {@link String}s, which allows
 * storing strings in a case-insensitive manner. Two strings which only differ
 * in case are handled as equal and can thus occur just once in the set (the
 * first string added to the set is retained).
 * <p>
 * It also allows accessing the case-sensitive representation of a string that
 * was stored utilizing the {@link #get(String)} method. I.e. storing string "A"
 * in the set and calling set.get("a") will return the original representation
 * "A".
 * <p>
 * This class does not inherit from {@link AbstractSet} or
 * {@link AbstractCollection} as most provided operations have higher
 * performance directly using the hash map instead of the iterator
 * implementations. Moreover, comparison in {@link AbstractCollection} is
 * performed with equals and the {@link CaseInsensitiveStringSet} violates this
 * contract as it requires comparison after normalization with
 * {@link #getMappingKey(String)}.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 49877 $
 * @ConQAT.Rating GREEN Hash: 9116DA861831E5AD8017AAEFF2C77091
 */
public class CaseInsensitiveStringSet implements Set<String> {

	/**
	 * Mapping from case-insensitive lower-cased strings to case-sensitive ones.
	 */
	private Map<String, String> caseInsensitiveMapping = new HashMap<String, String>();

	/** Constructor. */
	public CaseInsensitiveStringSet() {
		// default constructor.
	}

	/** Constructor. */
	public CaseInsensitiveStringSet(Collection<String> strings) {
		addAll(strings);
	}

	/**
	 * Returns the case-sensitive representation of the given string, as it was
	 * stored with the call to {@link #add(String)} or <code>null</code> if the
	 * string is not stored in the {@link Set}.
	 */
	public String get(String string) {
		return caseInsensitiveMapping.get(getMappingKey(string));
	}

	/**
	 * Converts the string to a case-insensitive mapping key. This is
	 * lower-cased string representation.
	 */
	private String getMappingKey(String string) {
		if (string == null) {
			return null;
		}
		return string.toLowerCase();
	}

	/** {@inheritDoc} */
	@Override
	public int size() {
		return caseInsensitiveMapping.size();
	}

	/** {@inheritDoc} */
	@Override
	public boolean isEmpty() {
		return caseInsensitiveMapping.isEmpty();
	}

	/** {@inheritDoc} */
	@Override
	public boolean contains(Object o) {
		if (o instanceof String) {
			String mappingKey = getMappingKey((String) o);
			return caseInsensitiveMapping.containsKey(mappingKey);
		}
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public Iterator<String> iterator() {
		return caseInsensitiveMapping.values().iterator();
	}

	/** {@inheritDoc} */
	@Override
	public Object[] toArray() {
		return caseInsensitiveMapping.values().toArray();
	}

	/** {@inheritDoc} */
	@Override
	public <T> T[] toArray(T[] a) {
		return caseInsensitiveMapping.values().toArray(a);
	}

	/** {@inheritDoc} */
	@Override
	public boolean add(String e) {
		if (!contains(e)) {
			caseInsensitiveMapping.put(getMappingKey(e), e);
			return true;
		}
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public boolean remove(Object o) {
		if (o instanceof String) {
			return caseInsensitiveMapping.remove(getMappingKey((String) o)) != null;
		}
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public boolean containsAll(Collection<?> c) {
		for (Object o : c) {
			if (!contains(o)) {
				return false;
			}
		}
		return true;
	}

	/** {@inheritDoc} */
	@Override
	public boolean addAll(Collection<? extends String> c) {
		boolean setChanged = false;
		for (String s : c) {
			setChanged |= add(s);
		}
		return setChanged;
	}

	/** {@inheritDoc} */
	@Override
	public boolean retainAll(Collection<?> c) {
		boolean setChanged = false;

		CaseInsensitiveStringSet retain = new CaseInsensitiveStringSet();
		for (Object toRetain : c) {
			if (toRetain instanceof String) {
				retain.add((String) toRetain);
			}
		}

		List<String> keys = new ArrayList<String>(
				caseInsensitiveMapping.keySet());

		for (String string : keys) {
			if (!retain.contains(string)) {
				setChanged |= remove(string);
			}
		}

		return setChanged;
	}

	/** {@inheritDoc} */
	@Override
	public boolean removeAll(Collection<?> c) {
		boolean setChanged = false;
		for (Object o : c) {
			setChanged |= remove(o);
		}

		return setChanged;
	}

	/** {@inheritDoc} */
	@Override
	public void clear() {
		caseInsensitiveMapping.clear();
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return caseInsensitiveMapping.values().toString();
	}

}
