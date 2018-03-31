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

/**
 * A partitioner that expects explicit partition marks in the file.
 * 
 * @author $Author: juergens $
 * @version $Rev: 35765 $
 * @ConQAT.Rating GREEN Hash: 57C5C3060E07750874A50383B3B505A4
 */
public class ExplicitPartitioner implements IRatingPartitioner {

	/** The marker used for partitioning. */
	private static final String PARTITION_MARK = "@ConQAT.PartitionMark";

	/** {@inheritDoc} */
	@Override
	public List<Region> partition(String[] lines) {
		List<Region> regions = new ArrayList<Region>();

		int start = 0;
		String name = "INITIAL";
		for (int i = 0; i < lines.length; ++i) {
			int markPosition = lines[i].indexOf(PARTITION_MARK);
			if (markPosition < 0) {
				continue;
			}

			insertRegion(start, i - 1, name, regions);

			start = i + 1;
			name = lines[i].substring(
					markPosition + PARTITION_MARK.length() + 1).trim();
		}

		insertRegion(start, lines.length - 1, name, regions);

		return regions;
	}

	/** Inserts a region if it is not empty. */
	private void insertRegion(int start, int end, String name,
			List<Region> regions) {
		if (end < start) {
			return;
		}
		regions.add(new Region(start, end, name));
	}
}
