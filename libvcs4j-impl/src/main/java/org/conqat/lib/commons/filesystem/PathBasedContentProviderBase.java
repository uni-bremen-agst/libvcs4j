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

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.zip.ZipFile;

/**
 * Base class for objects that provide access to the content of various elements
 * using a path for access. This basically abstracts the differences between
 * plain directories and ZIP files (containing multiple entries).
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 839A951DC41FDE1A8BDD52462EDD7BDE
 */
public abstract class PathBasedContentProviderBase implements Closeable {

	/**
	 * Returns the relative paths to all available elements that carry content.
	 * These correspond to file names or names of ZIP file entries. The returned
	 * paths are guaranteed to use a forward slash as the path separator.
	 */
	public abstract Collection<String> getPaths() throws IOException;

	/**
	 * {@inheritDoc}
	 * <p>
	 * This closes the content provider itself, i.e. after closing, no new
	 * streams may be opened. Whether existing streams are closed depends on the
	 * implementation, but it is recommended to close any streams returned from
	 * {@link #openStream(String)} yourself and to not use any of these streams
	 * after calling this method.
	 */
	@Override
	public abstract void close() throws IOException;

	/**
	 * Opens a stream to the element identified by the given path. This should
	 * be one of the paths returned by the {@link #getPaths()} method. The
	 * returned stream must be closed by the caller.
	 */
	public abstract InputStream openStream(String relativePath)
			throws IOException;

	/** Returns all paths starting with a given prefix. */
	public Collection<String> getPathsWithPrefix(String prefix)
			throws IOException {
		Collection<String> result = new ArrayList<String>();
		for (String path : getPaths()) {
			if (path.startsWith(prefix)) {
				result.add(path);
			}
		}
		return result;
	}

	/**
	 * Creates a suitable provider for a file. This works for directories as
	 * well as ZIP/JAR files. The returned provider has to be closed at the end.
	 * 
	 * @throws IOException
	 *             if accessing the file fails or the file is not of suitable
	 *             format (i.e. no directory or ZIP file).
	 */
	public static PathBasedContentProviderBase createProvider(File file)
			throws IOException {
		if (file.isDirectory()) {
			return new DirectoryContentProvider(file);
		}

		return new ZipContentProvider(new ZipFile(file));
	}
}