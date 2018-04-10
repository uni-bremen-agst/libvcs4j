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

import java.util.HashMap;
import java.util.Map;

/**
 * A map implementation of a map using string keys. This is based on a hybrid
 * map which uses an {@link ArrayBackedStringMap} while only a small number of
 * keys are present and switches to a {@link HashMap} after a certain size has
 * been reached.
 * 
 * @author hummelb
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 7CFCF942B47CAD069EED58337334D69E
 */
public class MemoryEfficientStringMap<V> extends HybridMapBase<String, V> {

	/** The maximal size of the map before switching is performed. */
	private static final int SWITCHING_SIZE = 16;

	/** Constructor. */
	public MemoryEfficientStringMap() {
		super(new ArrayBackedStringMap<V>(4));
	}

	/** Constructor. */
	public MemoryEfficientStringMap(Map<? extends String, ? extends V> map) {
		this();
		putAll(map);
	}

	/** {@inheritDoc} */
	@Override
	protected Map<String, V> obtainNewMap() {
		return new HashMap<String, V>();
	}

	/** {@inheritDoc} */
	@Override
	protected boolean shouldSwitch(Map<String, V> map) {
		return map.size() == SWITCHING_SIZE
				&& map instanceof ArrayBackedMap<?, ?>;
	}
}