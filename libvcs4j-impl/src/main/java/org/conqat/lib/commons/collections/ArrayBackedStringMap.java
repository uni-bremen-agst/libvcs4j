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
 * A specialization of the array backed map for string keys. This uses string
 * interning and reference comparison.
 * 
 * @author hummelb
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: F8548BE48C0D58DD8BE8F321ED4BD5A0
 */
public class ArrayBackedStringMap<V> extends ArrayBackedMap<String, V> {

	/** Constructor. */
	public ArrayBackedStringMap() {
		super();
	}

	/** Constructor. */
	public ArrayBackedStringMap(int initialCapacity) {
		super(initialCapacity);
	}

	/** {@inheritDoc} */
	@Override
	protected String internKey(Object key) throws ClassCastException {
		if (key == null) {
			return null;
		}
		return ((String) key).intern();
	}

	/** {@inheritDoc} */
	@Override
	protected boolean areEqual(String key1, String key2) {
		return key1 == key2;
	}
}