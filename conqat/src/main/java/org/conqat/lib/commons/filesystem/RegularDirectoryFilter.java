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

/**
 * This file filter includes only <i>regular</i> directories. This is used to
 * exclude Subversion, CVS, hidden and similar directories.
 * 
 * @author deissenb
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 3C3D48AD518FF8717B44853A1C6ACE63
 */
public class RegularDirectoryFilter extends DirectoryOnlyFilter {

	/** See class comment for details. */
	@Override
	public boolean accept(File file) {
		return super.accept(file) && isRegular(file);
	}

	/** Checks if directory is hidden, name start with a dot or name is 'CVS'. */
	private boolean isRegular(File file) {
		if (file.isHidden()) {
			return false;
		}

		if (file.getName().equals("CVS")) {
			return false;
		}

		if (file.getName().startsWith(".")) {
			return false;
		}

		return true;
	}

}