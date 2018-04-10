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
package org.conqat.lib.commons.filesystem;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Utility methods which deal with the content of Zip files. (Do not support
 * caching.)
 * 
 * @author $Author: pfaller $
 * @version $Rev: 49259 $
 * @ConQAT.Rating YELLOW Hash: 220DD61F0E62AD1206A099F424F86B8A
 */
public abstract class ZipFileUtils {

	/**
	 * Reads the content of an entry in a ZipFile.
	 * 
	 * @throws IOException
	 *             if no entry with the given name exists or no size informtion
	 *             is stored for the entry.
	 */
	public static byte[] readZipEntryContent(ZipFile zipFile, String entryName)
			throws IOException {
		ZipEntry entry = zipFile.getEntry(entryName);
		if (entry == null) {
			throw new IOException("Entry " + entryName + " does not exist in "
					+ zipFile);
		}
		int size = (int) entry.getSize();
		if (size < 0) {
			throw new IOException("Size for entry " + entryName
					+ " not stored in ZIP file " + zipFile);
		}
		byte[] content = new byte[size];

		try (InputStream in = zipFile.getInputStream(entry)) {
			FileSystemUtils.safeRead(in, content);
			return content;
		}
	}

}
