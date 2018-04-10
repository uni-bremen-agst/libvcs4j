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
package org.conqat.lib.commons.clone;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Collection of utility methods to simplify cloning.
 * 
 * @author Benjamin Hummel
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 9D0C34DF50C347672400D484438C68B8
 * 
 */
public class CloneUtils {

	/**
	 * Duplicate all entries of the source map to the target map and create
	 * clones of all contained objects.
	 * 
	 * @param source
	 *            the source for the key value pairs.
	 * @param target
	 *            the map to insert the created clones into.
	 */
	public static void cloneMapEntries(Map<String, Object> source,
			Map<String, Object> target) throws DeepCloneException {

		for (String key : source.keySet()) {
			target.put(key, cloneAsDeepAsPossible(source.get(key)));
		}
	}

	/**
	 * Clone the provided object if supported. If the depth of the structure is
	 * deeper than 3 an exception is thrown. For some of the special cases
	 * handled see {@link #cloneAsDeepAsPossible(Object, int)}.
	 * 
	 * @param o
	 *            the object to be cloned.
	 * @return the cloned (or original) object.
	 * @throws DeepCloneException
	 *             if the depth of the cloned struture is too high or the
	 *             underlying clone methods failed.
	 */
	public static Object cloneAsDeepAsPossible(Object o)
			throws DeepCloneException {
		return cloneAsDeepAsPossible(o, 3);
	}

	/**
	 * Clone the provided object if supported. The following cases are
	 * explicitly handled:
	 * <ul>
	 * <li>For {@link IDeepCloneable}s the {@link IDeepCloneable#deepClone()}
	 * method is used, ignoring the maximal depth.</li>
	 * <li>For {@link Map}s the contents (keys and values) are cloned and put
	 * into a {@link HashMap}. The order is not preserved.</li>
	 * <li>Arrays are cloned as expected.</li>
	 * <li>All {@link Collection}s are cloned as an {@link ArrayList}</li>
	 * <li>Is nothing matched so far, and the object implements
	 * {@link Cloneable}, and has a public clone method, the
	 * {@link Object#clone()} method is used.</li>
	 * <li>In any other case the object itself is returned (uncloned).</li>
	 * </ul>
	 * 
	 * @param o
	 *            the object to be cloned.
	 * @param maxDepth
	 *            the maximal depth of the structure before an exception is
	 *            thrown. A depth of 0 indicates that no recursive calls are
	 *            allowed, depth 1 allows 1 recursive call, and so on.
	 * @return the cloned (or original) object.
	 * @throws DeepCloneException
	 *             if the depth of the cloned struture is too high or the
	 *             underlying clone methods failed.
	 */
	public static Object cloneAsDeepAsPossible(Object o, int maxDepth)
			throws DeepCloneException {
		if (maxDepth < 0) {
			throw new DeepCloneException(
					"Reached maximal allowed cloning depth.");
		}
		// decrement for recursive calls
		maxDepth -= 1;

		try {
			if (o == null) {
				return null;
			}

			if (o instanceof IDeepCloneable) {
				return ((IDeepCloneable) o).deepClone();
			}

			if (o instanceof Map<?, ?>) {
				@SuppressWarnings("unchecked")
				Map<Object, Object> result = createNewInstance((Map<Object, Object>) o);
				for (Object child : ((Map<?, ?>) o).entrySet()) {
					@SuppressWarnings({ "unchecked", "rawtypes" })
					Entry<Object, Object> entry = (Entry) child;
					result.put(cloneAsDeepAsPossible(entry.getKey(), maxDepth),
							cloneAsDeepAsPossible(entry.getValue(), maxDepth));
				}
				return result;
			}

			if (o instanceof Object[]) {
				Object[] result = ((Object[]) o).clone();
				for (int i = 0; i < result.length; ++i) {
					result[i] = cloneAsDeepAsPossible(result[i], maxDepth);
				}
				return result;
			}

			if (o instanceof Collection<?>) {
				@SuppressWarnings("unchecked")
				Collection<Object> result = createNewInstance((Collection<Object>) o);
				for (Object child : (Collection<?>) o) {
					result.add(cloneAsDeepAsPossible(child, maxDepth));
				}
				return result;
			}

			if (o instanceof Cloneable) {
				Method clone = o.getClass().getMethod("clone");
				if (Modifier.isPublic(clone.getModifiers())) {
					return clone.invoke(o);
				}
			}

			// nothing else worked, so return uncloned
			return o;

		} catch (DeepCloneException ex) {
			throw ex;
		} catch (Exception ex) {
			throw new DeepCloneException(ex);
		}
	}

	/**
	 * Returns a new instance of the same class as the input object. This
	 * contains special handling for some types.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static <T> T createNewInstance(T object) throws DeepCloneException {
		if (object instanceof TreeSet<?>) {
			return (T) new TreeSet(((TreeSet) object).comparator());
		}

		if (object instanceof TreeMap<?, ?>) {
			return (T) new TreeMap(((TreeMap) object).comparator());
		}

		try {
			return (T) object.getClass().getConstructor().newInstance();
		} catch (Exception e) {
			// as the list of possible exceptions is very long here, we deal
			// with them in one group
			throw new DeepCloneException("Could not duplicate object of type "
					+ object.getClass(), e);
		}
	}
}
