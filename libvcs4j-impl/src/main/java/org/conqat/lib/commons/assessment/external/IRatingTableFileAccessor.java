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
 * Interface used to access external rating information.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 76AE65BC72C2C79F04924B6F1DD119E6
 */
public interface IRatingTableFileAccessor {

	/**
	 * Provides the path corresponding to the rated content. This does not have
	 * to be an actual file's path, but the returned string has to be path-like,
	 * i.e. consist of parts separated by slash or back-slash.
	 */
	String getFilePath();

	/**
	 * Returns the content of a file.
	 * 
	 * @param relativePath
	 *            the path identifying the file, relative to
	 *            {@link #getFilePath()}. The path uses the usual ".." notation
	 *            for parent directories and uses a forward slash as a
	 *            separator.
	 * @return the content or null in case of errors.
	 */
	String getRelativeFileContent(String relativePath);

	/**
	 * Sets the content of a file. This is fail-silent, i.e. in case of an
	 * error, just nothing happens.
	 * 
	 * @param relativePath
	 *            the path identifying the file, relative to
	 *            {@link #getFilePath()}. The path uses the usual ".." notation
	 *            for parent directories and uses a forward slash as a
	 *            separator.
	 * @param content
	 *            the new content for the file.
	 */
	void setRelativeFileContent(String relativePath, String content);
}
