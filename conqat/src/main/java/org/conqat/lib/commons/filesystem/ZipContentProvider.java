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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * {@link PathBasedContentProviderBase} implementation working on ZIP/JAR files.
 * 
 * @author hummelb
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: AFFBDAC0F888103FE59D4DC3BA81BCFD
 */
/* package */final class ZipContentProvider extends
		PathBasedContentProviderBase {

	/** The underlying ZIP file. */
	private final ZipFile zipFile;

	/** Constructor. */
	public ZipContentProvider(ZipFile zipFile) {
		this.zipFile = zipFile;
	}

	/** {@inheritDoc} */
	@Override
	public Collection<String> getPaths() {
		Collection<String> result = new ArrayList<String>();
		Enumeration<? extends ZipEntry> e = zipFile.entries();
		while (e.hasMoreElements()) {
			ZipEntry entry = e.nextElement();
			if (!entry.isDirectory()) {
				result.add(entry.getName());
			}
		}
		return result;
	}

	/** {@inheritDoc} */
	@Override
	public InputStream openStream(String relativePath) throws IOException {
		ZipEntry entry = zipFile.getEntry(relativePath);
		if (entry == null) {
			throw new IOException("Unknown path: " + relativePath);
		}
		return zipFile.getInputStream(entry);
	}

	/** {@inheritDoc} */
	@Override
	public void close() throws IOException {
		zipFile.close();
	}
}