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
package org.conqat.lib.commons.datamining;

/**
 * A user of a recommendation system.
 * 
 * @author $Author: heineman $
 * @version $Rev: 41782 $
 * @ConQAT.Rating YELLOW Hash: 840B672CC8BB8E65ACCA3FEFEE1FBDE0
 */
public interface IRecommenderUser {

	/** Returns the similarity [0..1] of this user to the given user. */
	double similarity(IRecommenderUser other);

}
