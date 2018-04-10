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
import java.io.FileFilter;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * This is a file filter that accepts only filenames out of a list of filenames
 * 
 * @author Elmar Juergens
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 2922C3A6048E9361B02575D07C651238
 */
public class FilenameFilter implements FileFilter {

	/** List of file names */
	private final Set<String> filenames;

	/** Constructor */
	public FilenameFilter(Collection<String> filenames) {
		this.filenames = new HashSet<String>(filenames);
	}

	/** {@inheritDoc} */
	@Override
	public boolean accept(File file) {
		return filenames.contains(file.getName());
	}

}