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
package org.conqat.lib.commons.assessment;

import org.conqat.lib.commons.assessment.external.ExternalRatingTableException;
import org.conqat.lib.commons.assessment.external.IRatingTableFileAccessor;
import org.conqat.lib.commons.assessment.partition.PartitioningException;

/**
 * Utility methods used for dealing with ratings.
 * 
 * @author $Author: deissenb $
 * @version $Rev: 36783 $
 * @ConQAT.Rating GREEN Hash: 1C6347EA40F892BF35E93A3BB7A199DA
 */
public class RatingUtils {

	/**
	 * Calculates the rating for the given content. This respects both the old
	 * full-file {@link Rating} and the new {@link PartitionedRating}. If both
	 * are found in a file, the full-file rating is used. If no valid rating is
	 * found at all, the file is rated RED.
	 */
	public static ETrafficLightColor calculateRating(String content) {
		return calculateRating(content, null);
	}

	/**
	 * Calculates the rating for the given content. This respects both the old
	 * full-file {@link Rating} and the new {@link PartitionedRating}. If both
	 * are found in a file, the full-file rating is used. If no valid rating is
	 * found at all, the file is rated RED.
	 */
	public static ETrafficLightColor calculateRating(String content,
			IRatingTableFileAccessor accessor) {
		Rating rating = new Rating(content);
		if (rating.getStoredRating() != null) {
			return rating.getRating();
		}

		// no old rating found, so maybe this a partitioned rating
		try {
			PartitionedRating partitionedRating = new PartitionedRating(
					content, accessor);
			ETrafficLightColor result = ETrafficLightColor.UNKNOWN;
			for (RatingPartition partition : partitionedRating.getPartitions()) {
				result = ETrafficLightColor.getDominantColor(result,
						partition.getRating());
			}
			return result;
		} catch (PartitioningException e) {
			// no rating means RED
			return ETrafficLightColor.RED;
		} catch (ExternalRatingTableException e) {
			// problems accessing external table means RED
			return ETrafficLightColor.RED;
		}
	}
}
