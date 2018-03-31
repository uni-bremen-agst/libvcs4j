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
package org.conqat.lib.commons.region;

/**
 * Regions represent intervals. Both the start and the end position are
 * considered to be part of the region. Regions can i.e. be used to represent
 * fragments of files.
 * <p>
 * This class is immutable.
 * 
 * @author $Author: kinnen $
 * @version $Revision: 47537 $
 * @ConQAT.Rating GREEN Hash: 403EB230B1F3742FECDFACF57ED84D4A
 */
public final class Region extends SimpleRegion {

	/** Version for serialization. */
	private static final long serialVersionUID = 1;

	/** Name that is used if region is created without name */
	public static final String UNKNOWN_ORIGIN = "Unknown origin";

	/**
	 * Origin of the region. Can be used to store information about who created
	 * the region.
	 */
	private final String origin;

	/**
	 * Creates a region with an origin. An empty region can be denoted with and
	 * end position smaller than start.
	 * 
	 * @param start
	 *            Start position of the region
	 * @param end
	 *            End position of the region
	 * @param origin
	 *            Region origin. (i.e. region producer)
	 */
	public Region(int start, int end, String origin) {
		super(start, end);
		this.origin = origin;
	}

	/**
	 * Creates a region with an unknown origin
	 * 
	 * @param start
	 *            Start position of the region
	 * @param end
	 *            End position of the region
	 */
	public Region(int start, int end) {
		this(start, end, UNKNOWN_ORIGIN);
	}

	/** Get origin. */
	public String getOrigin() {
		return origin;
	}
}