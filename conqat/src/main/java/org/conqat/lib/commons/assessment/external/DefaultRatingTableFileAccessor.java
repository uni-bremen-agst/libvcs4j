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

import java.io.File;
import java.io.IOException;

import org.conqat.lib.commons.filesystem.FileSystemUtils;

/**
 * An {@link IRatingTableFileAccessor} working on {@link File}s.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 5BE8B77DF70559568EB67B37DFE44DF1
 */
public class DefaultRatingTableFileAccessor implements IRatingTableFileAccessor {

	/** The underlying file. */
	private final File file;

	/** Constructor. */
	public DefaultRatingTableFileAccessor(File file) {
		this.file = file;
	}

	/** {@inheritDoc} */
	@Override
	public String getFilePath() {
		return file.getAbsolutePath();
	}

	/** {@inheritDoc} */
	@Override
	public String getRelativeFileContent(String relativePath) {
		try {
			return FileSystemUtils.readFileUTF8(new File(file.getParentFile(),
					relativePath));
		} catch (IOException e) {
			return null;
		}
	}

	/** {@inheritDoc} */
	@Override
	public void setRelativeFileContent(String relativePath, String content) {
		try {
			FileSystemUtils
					.writeFileUTF8(new File(file, relativePath), content);
		} catch (IOException e) {
			// discarded as required from the interface contract
		}
	}
}
