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

import java.util.List;

import org.conqat.lib.commons.assessment.PartitionedRating;
import org.conqat.lib.commons.region.Region;

/**
 * This interface describes code used to partition code into segments that can
 * be rated individually for the {@link PartitionedRating}.
 * 
 * @author $Author: deissenb $
 * @version $Rev: 35901 $
 * @ConQAT.Rating GREEN Hash: B525619B13B678FEAF2EE5E49DCA6882
 */
public interface IRatingPartitioner {

	/**
	 * Partitions the given lines into regions.
	 * 
	 * @param lines
	 *            the lines forming the source code being partitioned. The lines
	 *            may not contain line breaks or newlines.
	 * 
	 * @return a list of regions. The regions are line regions, i.e. start and
	 *         end are (both inclusive) indices into the lines array (thus also
	 *         0-based). The regions must not overlap. It is not necessary to
	 *         use named regions, but it is preferred as it helps both the user
	 *         and the algorithm used for matching up regions if unique names
	 *         can be generated.
	 */
	List<Region> partition(String[] lines) throws PartitioningException;

}
