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
package org.conqat.lib.commons.assessment.partition;

import java.util.ArrayList;
import java.util.List;

import org.conqat.lib.commons.region.Region;
import org.conqat.lib.commons.string.StringUtils;

/**
 * A partitioner that separates at empty lines.
 * 
 * @author $Author: juergens $
 * @version $Rev: 35765 $
 * @ConQAT.Rating GREEN Hash: D4E16C1FCF60D7AA02F52BF19D886ACB
 */
public class EmptyLinePartitioner implements IRatingPartitioner {

	/** {@inheritDoc} */
	@Override
	public List<Region> partition(String[] lines) {
		List<Region> regions = new ArrayList<Region>();
		int start = -1;
		String name = null;
		for (int i = 0; i < lines.length; ++i) {
			if (StringUtils.isEmpty(lines[i])) {
				if (start >= 0) {
					regions.add(new Region(start, i - 1, name));
					start = -1;
				}
			} else if (start < 0) {
				start = i;
				name = lines[i].replaceAll("\\s+", StringUtils.EMPTY_STRING);
			}
		}

		if (start >= 0) {
			regions.add(new Region(start, lines.length - 1, name));
		}

		return regions;
	}
}
