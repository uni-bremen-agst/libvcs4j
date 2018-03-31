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
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URI;

/**
 * This class represents files that are guaranteed to be canonical. For this
 * class methods <code>getPath()</code>, <code>getAbsolutePath()</code> and
 * <code>getCanonicalPath()</code> all return the same (canonical) path.
 * 
 * @author deissenb
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: B15B306186A1D9535965747AB1F6E045
 */
public class CanonicalFile extends File {

	/** Version used for serialization. */
	private static final long serialVersionUID = 1;

	/**
	 * Create new canonical file from existing file.
	 */
	public CanonicalFile(File file) throws IOException {
		super(file.getCanonicalPath());
	}

	/**
	 * Create new canonical file.
	 * 
	 * @see File#File(String)
	 */
	public CanonicalFile(String pathname) throws IOException {
		super(new File(pathname).getCanonicalPath());
	}

	/**
	 * Create new canonical file.
	 * 
	 * @see File#File(File, String)
	 */
	public CanonicalFile(File parent, String child) throws IOException {
		this(new File(parent, child));
	}

	/**
	 * Create new canonical file.
	 * 
	 * @see File#File(String, String)
	 */
	public CanonicalFile(String parent, String child) throws IOException {
		this(new File(parent, child));
	}

	/**
	 * Create new canonical file.
	 * 
	 * @see File#File(URI)
	 */
	public CanonicalFile(URI uri) throws IOException {
		super(new File(uri).getCanonicalPath());
	}

	/**
	 * Returns the canonical file itself. Use {@link #getCanonicalFile()} for
	 * consistency reasons.
	 */
	@Deprecated
	@Override
	public CanonicalFile getAbsoluteFile() {
		return this;
	}

	/** Returns the canonical file itself. */
	@Override
	public CanonicalFile getCanonicalFile() {
		return this;
	}

	/**
	 * Same as {@link File#listFiles()} but returns canonical files. If for some
	 * strange reason the files below a canonical file cannot be canonized, this
	 * may throw an {@link AssertionError}.
	 */
	@Override
	public CanonicalFile[] listFiles() {
		return canonize(super.listFiles());
	}

	/**
	 * Same as {@link File#listFiles(FileFilter)} but returns canonical files.
	 * If for some strange reason the files below a canonical file cannot be
	 * canonized, this may throw an {@link AssertionError}.
	 */
	@Override
	public CanonicalFile[] listFiles(FileFilter filter) {
		File[] files = super.listFiles(filter);
		CanonicalFile[] result = new CanonicalFile[files.length];
		for (int i = 0; i < result.length; i++) {
			result[i] = canonize(files[i].getAbsoluteFile());
		}
		return result;
	}

	/**
	 * Same as {@link File#listFiles(FilenameFilter)} but returns canonical
	 * files. If for some strange reason the files below a canonical file cannot
	 * be canonized, this may throw an {@link AssertionError}.
	 */
	@Override
	public CanonicalFile[] listFiles(FilenameFilter filter) {
		return canonize(super.listFiles(filter));
	}

	/**
	 * Same as {@link File#getParentFile()} but returns a canonical file. If for
	 * some strange reason the parent file of a canonical file cannot be
	 * canonized, this may throw an {@link AssertionError}.
	 */
	@Override
	public CanonicalFile getParentFile() {
		File parent = super.getParentFile();
		if (parent == null) {
			return null;
		}
		return canonize(parent);
	}

	/** Checks if this <em>file</em> is a file an can be read. */
	public boolean isReadableFile() {
		return isFile() && canRead();
	}

	/**
	 * This method is overridden to save effort for call to
	 * {@link File#getCanonicalPath()}.
	 */
	@Override
	public String getCanonicalPath() {
		return super.getPath();
	}

	/**
	 * This method is overridden to save effort for call to
	 * {@link File#getAbsolutePath()}. Use {@link #getCanonicalPath()} for
	 * consistency reasons.
	 */
	@Deprecated
	@Override
	public String getAbsolutePath() {
		return super.getPath();
	}

	/**
	 * Use {@link #getCanonicalPath()} for consistency reasons.
	 */
	@Deprecated
	@Override
	public String getPath() {
		return super.getPath();
	}

	/**
	 * Returns the extension of the file.
	 * 
	 * @return File extension, i.e. "java" for "FileSystemUtils.java", or
	 *         <code>null</code>, if the file has no extension (i.e. if a
	 *         filename contains no '.'), returns the empty string if the '.' is
	 *         the filename's last character.
	 */
	public String getExtension() {
		return FileSystemUtils.getFileExtension(this);
	}

	/**
	 * Canonizes list of filenames. If a file could not be canonized, this
	 * throws an {@link AssertionError}.
	 */
	private CanonicalFile[] canonize(File[] files) {
		CanonicalFile[] result = new CanonicalFile[files.length];
		for (int i = 0; i < result.length; i++) {
			result[i] = canonize(files[i]);
		}
		return result;

	}

	/**
	 * Canonizes filename. If a file could not be canonized, this throws an
	 * {@link AssertionError}.
	 */
	private CanonicalFile canonize(File file) {
		try {
			return new CanonicalFile(file);
		} catch (IOException e) {
			throw new AssertionError("Problems creating canonical path for "
					+ file + ": " + e.getMessage());
		}
	}

}