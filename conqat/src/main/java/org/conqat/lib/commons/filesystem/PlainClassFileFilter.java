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
 * This is a file filter finding only plain class files (i.e. no inner or
 * anonymous classes).
 * 
 * @author Benjamin Hummel
 * @author Florian Deissenboeck
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 59A1770E8F11BC3015EFE1E26819C7CD
 */
public class PlainClassFileFilter extends FileExtensionFilter {

	/** Creates a new filter. */
	public PlainClassFileFilter() {
		super("class");
	}

	/** {@inheritDoc} */
	@Override
	public boolean accept(File file) {
		return super.accept(file) && !file.getName().contains("$");
	}
}