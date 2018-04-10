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
package org.conqat.lib.commons.assessment.external;

/**
 * Interface for resolving a file name relative to another file.
 * 
 * The interface is quite generic and could be used for other purposes. However,
 * as this is just used for the rating tables, a rating table specific exception
 * is used. Change if required.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: F355C3C9B3BE5A3B46CE67E1E9D4CF0F
 */
public interface IFilePathResolver {

	/** Initializes the resolver with additional arguments. */
	void init(String... args) throws ExternalRatingTableException;

	/**
	 * Returns the path relative to a provided file path.
	 * 
	 * @param path
	 *            the path of the file. The caller of this method guarantees
	 *            that forward slashes are used as separator in this path.
	 * @return the relative path, using forward slashes.
	 */
	String getRelativeFilePath(String path);
}
