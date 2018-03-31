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
package org.conqat.lib.commons.filesystem;

import java.io.File;
import java.util.Comparator;

/**
 * Compares files based on the lexical order of their fully qualified names.
 * Files must not be null.
 * 
 * @author juergens
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 74A6BBC075239749B65542BC2EEE7DDE
 */
public class FilenameComparator implements Comparator<File> {

	/** {@inheritDoc} */
	@Override
	public int compare(File file1, File file2) {
		if (file1 == null || file2 == null) {
			throw new IllegalArgumentException("Files must not be null");
		}

		return file1.getAbsolutePath().compareTo(file2.getAbsolutePath());
	}

}