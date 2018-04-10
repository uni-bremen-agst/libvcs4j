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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;

import org.conqat.lib.commons.assertion.CCSMPre;
import org.conqat.lib.commons.string.StringUtils;

/**
 * {@link PathBasedContentProviderBase} implementation working on directories.
 * 
 * @author $Author: streitel $
 * @version $Rev: 50585 $
 * @ConQAT.Rating GREEN Hash: D7FECC6C25221A1A7FD6D13BB6EFE28B
 */
/* package */final class DirectoryContentProvider extends
		PathBasedContentProviderBase {

	/** The base directory. */
	private final File baseDir;

	/** Constructor. */
	public DirectoryContentProvider(File baseDir) {
		CCSMPre
				.isTrue(baseDir.isDirectory(),
						"Input file must be a directory!");
		this.baseDir = baseDir;
	}

	/** {@inheritDoc} */
	@Override
	public Collection<String> getPaths() {
		Collection<String> result = new ArrayList<String>();
		for (File file : FileSystemUtils.listFilesRecursively(baseDir)) {
			if (file.isFile()) {
				String relative = StringUtils.stripPrefix(file.getPath(),
						baseDir.getPath());
				result.add(StringUtils.stripPrefix(FileSystemUtils
						.normalizeSeparators(relative), "/"));
			}
		}
		return result;
	}

	/** {@inheritDoc} */
	@Override
	public InputStream openStream(String relativePath) throws IOException {
		return new FileInputStream(new File(baseDir, relativePath));
	}

	/** {@inheritDoc} */
	@Override
	public void close() {
		// does nothing
	}
}