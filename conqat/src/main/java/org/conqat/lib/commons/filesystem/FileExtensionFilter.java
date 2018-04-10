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

import org.conqat.lib.commons.string.StringUtils;

/**
 * This is a file filter finding only "normal" files having one of the specified
 * extensions.
 * 
 * @author Benjamin Hummel
 * @author Florian Deissenboeck
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 843F10AA8633075388757D62088DA63F
 */
public class FileExtensionFilter extends FileOnlyFilter {

	/** The extension to match. */
	private final String[] extensions;

	/**
	 * Creates a new file extension filter.
	 * 
	 * @param extensions
	 *            the extension required by the files (without the dot). Note
	 *            that using an empty string will not find all files without
	 *            extension, but rather those ending in a single dot.
	 */
	public FileExtensionFilter(String... extensions) {
		this.extensions = new String[extensions.length];
		for (int i = 0; i < extensions.length; i++) {
			this.extensions[i] = "." + extensions[i];
		}
	}

	/** {@inheritDoc} */
	@Override
	public boolean accept(File file) {
		return super.accept(file)
				&& StringUtils.endsWithOneOf(file.getName(), extensions);
	}
}