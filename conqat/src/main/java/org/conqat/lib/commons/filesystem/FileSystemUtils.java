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

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.EOFException;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.IllegalFormatException;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.conqat.lib.commons.assertion.CCSMAssert;
import org.conqat.lib.commons.assertion.CCSMPre;
import org.conqat.lib.commons.assertion.PreconditionException;
import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.logging.ILogger;
import org.conqat.lib.commons.string.StringUtils;

/**
 * File system utilities.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 51179 $
 * @ConQAT.Rating GREEN Hash: D52B6180F90139BCC8B5C1A7D90986B5
 */
public class FileSystemUtils {

	/** Encoding for UTF-8. */
	public static final String UTF8_ENCODING = "UTF-8";

	/**
	 * Charset for UTF-8. This can throw an exception, if the UTF-8 charset is
	 * not supported. This however is likely to indicate a corrupt JRE/JDK and
	 * thus getting an exception at loading this class seems ok.
	 */
	public static final Charset UTF8_CHARSET = Charset.forName(UTF8_ENCODING);

	/**
	 * Copy an input stream to an output stream. This does <em>not</em> close
	 * the streams.
	 * 
	 * @param input
	 *            input stream
	 * @param output
	 *            output stream
	 * @return number of bytes copied
	 * @throws IOException
	 *             if an IO exception occurs.
	 */
	public static int copy(InputStream input, OutputStream output)
			throws IOException {
		byte[] buffer = new byte[1024];
		int size = 0;
		int len;
		while ((len = input.read(buffer)) > 0) {
			output.write(buffer, 0, len);
			size += len;
		}
		return size;
	}

	/** Copy a file. This creates all necessary directories. */
	public static void copyFile(File sourceFile, File targetFile)
			throws IOException {

		if (sourceFile.getAbsoluteFile().equals(targetFile.getAbsoluteFile())) {
			throw new IOException("Can not copy file onto itself: "
					+ sourceFile);
		}

		ensureParentDirectoryExists(targetFile);

		FileChannel sourceChannel = null;
		FileChannel targetChannel = null;
		try {
			sourceChannel = new FileInputStream(sourceFile).getChannel();
			targetChannel = new FileOutputStream(targetFile).getChannel();
			sourceChannel.transferTo(0, sourceChannel.size(), targetChannel);
		} finally {
			close(sourceChannel);
			close(targetChannel);
		}
	}

	/** Copy a file. This creates all necessary directories. */
	public static void copyFile(String sourceFilename, String targetFilename)
			throws IOException {
		copyFile(new File(sourceFilename), new File(targetFilename));
	}

	/**
	 * Copy all files specified by a file filter from one directory to another.
	 * This automatically creates all necessary directories.
	 * 
	 * @param fileFilter
	 *            filter to specify file types. If all files should be copied,
	 *            use {@link FileOnlyFilter}.
	 * @return number of files copied
	 */
	public static int copyFiles(File sourceDirectory, File targetDirectory,
			FileFilter fileFilter) throws IOException {
		List<File> files = FileSystemUtils.listFilesRecursively(
				sourceDirectory, fileFilter);

		int fileCount = 0;
		for (File sourceFile : files) {
			if (sourceFile.isFile()) {
				String path = sourceFile.getAbsolutePath();
				int index = sourceDirectory.getAbsolutePath().length();
				String newPath = path.substring(index);
				File targetFile = new File(targetDirectory, newPath);
				copyFile(sourceFile, targetFile);
				fileCount++;
			}
		}
		return fileCount;
	}

	/**
	 * Create jar file from all files in a directory.
	 * 
	 * @param filter
	 *            filter to specify file types. If all files should be copied,
	 *            use {@link FileOnlyFilter}.
	 * @return number of files added to the jar file
	 */
	public static int createJARFile(File jarFile, File sourceDirectory,
			FileFilter filter) throws IOException {
		JarOutputStream out = null;
		int fileCount = 0;

		try {
			out = new JarOutputStream(new FileOutputStream(jarFile));

			for (File file : FileSystemUtils.listFilesRecursively(
					sourceDirectory, filter)) {
				if (!file.isFile()) {
					continue;
				}

				FileInputStream in = null;
				fileCount += 1;
				try {
					// works for forward slashes only
					String entryName = normalizeSeparators(file
							.getAbsolutePath()
							.substring(
									sourceDirectory.getAbsolutePath().length() + 1));
					out.putNextEntry(new ZipEntry(entryName));

					in = new FileInputStream(file);
					copy(in, out);
					out.closeEntry();
				} finally {
					close(in);
				}
			}
		} finally {
			close(out);
		}

		return fileCount;
	}

	/**
	 * Returns a string describing the relative path to the given directory. If
	 * there is no relative path, as the directories do not share a common
	 * parent, the absolute path is returned.
	 * 
	 * @param path
	 *            the path to convert to a relative path (must describe an
	 *            existing directory)
	 * @param relativeTo
	 *            the anchor (must describe an existing directory)
	 * @return a relative path
	 * @throws IOException
	 *             if creation of canonical pathes fails.
	 */
	public static String createRelativePath(File path, File relativeTo)
			throws IOException {
		CCSMAssert.isNotNull(path, "Path must not be null!");
		CCSMAssert.isNotNull(relativeTo, "relativeTo must not be null!");

		if (!path.isDirectory() || !relativeTo.isDirectory()) {
			throw new IllegalArgumentException(
					"Both arguments must be existing directories!");
		}
		path = path.getCanonicalFile();
		relativeTo = relativeTo.getCanonicalFile();

		Set<File> parents = new HashSet<File>();
		File f = path;
		while (f != null) {
			parents.add(f);
			f = f.getParentFile();
		}

		File root = relativeTo;
		while (root != null && !parents.contains(root)) {
			root = root.getParentFile();
		}

		if (root == null) {
			// no common root, so use full path
			return path.getAbsolutePath();
		}

		String result = "";
		while (!path.equals(root)) {
			result = path.getName() + "/" + result;
			path = path.getParentFile();
		}
		while (!relativeTo.equals(root)) {
			result = "../" + result;
			relativeTo = relativeTo.getParentFile();
		}

		return result;
	}

	/**
	 * Recursively delete directories and files. This method ignores the return
	 * value of delete(), i.e. if anything fails, some files might still exist.
	 */
	public static void deleteRecursively(File directory) {

		if (directory == null) {
			throw new IllegalArgumentException("Directory may not be null.");
		} else if (directory.listFiles() == null) {
			throw new IllegalArgumentException(directory.getAbsolutePath()
					+ " is not a valid directory.");
		}

		for (File entry : directory.listFiles()) {
			if (entry.isDirectory()) {
				deleteRecursively(entry);
			}
			entry.delete();
		}
		directory.delete();
	}

	/**
	 * Deletes the given file and throws an exception if this fails.
	 * 
	 * @see File#delete()
	 */
	public static void deleteFile(File file) throws IOException {
		if (file.exists() && !file.delete()) {
			throw new IOException("Could not delete " + file);
		}
	}

	/**
	 * Renames the given file and throws an exception if this fails.
	 * 
	 * @see File#renameTo(File)
	 */
	public static void renameFileTo(File file, File dest) throws IOException {
		if (!file.renameTo(dest)) {
			throw new IOException("Could not rename " + file + " to " + dest);
		}
	}

	/**
	 * Creates a directory and throws an exception if this fails.
	 * 
	 * @see File#mkdir()
	 */
	public static void mkdir(File dir) throws IOException {
		if (!dir.mkdir()) {
			throw new IOException("Could not create directory " + dir);
		}
	}

	/**
	 * Creates a directory and all required parent directories. Throws an
	 * exception if this fails.
	 * 
	 * @see File#mkdirs()
	 */
	public static void mkdirs(File dir) throws IOException {
		if (!dir.mkdirs()) {
			throw new IOException("Could not create directory " + dir);
		}
	}

	/**
	 * Checks if a directory exists. If not it creates the directory and all
	 * necessary parent directories.
	 * 
	 * @throws IOException
	 *             if directories couldn't be created.
	 */
	public static void ensureDirectoryExists(File directory) throws IOException {
		if (!directory.exists() && !directory.mkdirs()) {
			throw new IOException("Couldn't create directory: " + directory);
		}
	}

	/**
	 * Checks if the parent directory of a file exists. If not it creates the
	 * directory and all necessary parent directories.
	 * 
	 * @throws IOException
	 *             if directories couldn't be created.
	 */
	public static void ensureParentDirectoryExists(File file)
			throws IOException {
		ensureDirectoryExists(file.getCanonicalFile().getParentFile());
	}

	/**
	 * Returns a list of all files and directories contained in the given
	 * directory and all subdirectories. The given directory itself is not
	 * included in the result.
	 * <p>
	 * This method knows nothing about (symbolic and hard) links, so care should
	 * be taken when traversing directories containing recursive links.
	 * 
	 * @param directory
	 *            the directory to start the search from.
	 * @return the list of files found (the order is determined by the file
	 *         system).
	 */
	public static List<File> listFilesRecursively(File directory) {
		return listFilesRecursively(directory, null);
	}

	/**
	 * Returns a list of all files and directories contained in the given
	 * directory and all subdirectories matching the filter provided. The given
	 * directory itself is not included in the result.
	 * <p>
	 * The file filter may or may not exclude directories.
	 * <p>
	 * This method knows nothing about (symbolic and hard) links, so care should
	 * be taken when traversing directories containing recursive links.
	 * 
	 * @param directory
	 *            the directory to start the search from. If this is null or the
	 *            directory does not exists, an empty list is returned.
	 * @param filter
	 *            the filter used to determine whether the result should be
	 *            included. If the filter is null, all files and directories are
	 *            included.
	 * @return the list of files found (the order is determined by the file
	 *         system).
	 */
	public static List<File> listFilesRecursively(File directory,
			FileFilter filter) {
		if (directory == null || !directory.isDirectory()) {
			return CollectionUtils.emptyList();
		}
		List<File> result = new ArrayList<File>();
		listFilesRecursively(directory, result, filter);
		return result;
	}

	/**
	 * Lists the names of all simple files (i.e. no directories) next to an URL.
	 * For example for a file, this would return the names of all files in the
	 * same directory (including the file itself). Currently, this supports both
	 * file and jar URLs. The intended use-case is to list a set of files in a
	 * package via the class loader.
	 */
	public static List<String> listFilesInSameLocationForURL(URL baseUrl)
			throws IOException {

		String protocol = baseUrl.getProtocol();

		if ("file".equals(protocol)) {
			return listFilesInSameLocationForFileURL(baseUrl);
		}

		if ("jar".equals(protocol)) {
			return listFilesInSameLocationForJarURL(baseUrl);
		}

		throw new IOException("Unsupported protocol: " + protocol);
	}

	/** Lists the names of files for a JAR URL that share the same location. */
	private static List<String> listFilesInSameLocationForJarURL(URL baseUrl)
			throws IOException {
		JarFile jarFile = null;
		try {
			jarFile = new JarFile(
					FileSystemUtils.extractJarFileFromJarURL(baseUrl));

			// in JAR URLs we can rely on the separator being a slash
			String parentPath = StringUtils.getLastPart(baseUrl.getPath(), '!');
			parentPath = StringUtils.stripSuffix(parentPath,
					StringUtils.getLastPart(parentPath, '/'));
			parentPath = StringUtils.stripPrefix(parentPath, "/");

			List<String> names = new ArrayList<String>();
			Enumeration<JarEntry> entriesEnumerator = jarFile.entries();
			while (entriesEnumerator.hasMoreElements()) {
				JarEntry entry = entriesEnumerator.nextElement();
				String simpleName = StringUtils.getLastPart(entry.getName(),
						'/');
				String entryPath = StringUtils.stripSuffix(entry.getName(),
						simpleName);

				if (!entry.isDirectory() && entryPath.equals(parentPath)) {
					names.add(simpleName);
				}
			}
			return names;
		} finally {
			FileSystemUtils.close(jarFile);
		}
	}

	/** Lists the names of files in the same directory as the given file URL. */
	private static List<String> listFilesInSameLocationForFileURL(URL baseUrl)
			throws IOException {
		try {
			File directory = new File(baseUrl.toURI()).getParentFile();
			if (!directory.isDirectory()) {
				throw new IOException(
						"Parent directory does not exist or is not readable for "
								+ baseUrl);
			}

			List<String> names = new ArrayList<String>();
			for (File file : directory.listFiles()) {
				if (file.isFile()) {
					names.add(file.getName());
				}
			}
			return names;
		} catch (URISyntaxException e) {
			throw new IOException("Could not convert URL to valid file: "
					+ baseUrl, e);
		}
	}

	/**
	 * Extract all top-level classes in the given JAR and returns a list of
	 * their fully qualified class names. Inner classes are ignored.
	 */
	public static List<String> listTopLevelClassesInJarFile(File jarFile)
			throws IOException {
		List<String> result = new ArrayList<String>();
		PathBasedContentProviderBase provider = PathBasedContentProviderBase
				.createProvider(jarFile);
		Collection<String> paths = provider.getPaths();
		for (String path : paths) {
			if (path.endsWith(".class") && !path.contains("$")) {
				String fqn = StringUtils.removeLastPart(path, '.');
				fqn = fqn.replace('/', '.');
				result.add(fqn);
			}
		}
		return result;
	}

	/**
	 * Returns the extension of the file.
	 * 
	 * @return File extension, i.e. "java" for "FileSystemUtils.java", or
	 *         <code>null</code>, if the file has no extension (i.e. if a
	 *         filename contains no '.'), returns the empty string if the '.' is
	 *         the filename's last character.
	 */
	public static String getFileExtension(File file) {
		String name = file.getName();
		int posLastDot = name.lastIndexOf('.');
		if (posLastDot < 0) {
			return null;
		}
		return name.substring(posLastDot + 1);
	}

	/**
	 * Returns the name of the given file without extension. Example:
	 * '/home/joe/data.dat' -> 'data'.
	 */
	public static String getFilenameWithoutExtension(File file) {
		return StringUtils.removeLastPart(file.getName(), '.');
	}

	/**
	 * Constructs a file from a base file by appending several path elements.
	 * Insertion of separators is performed automatically as needed. This is
	 * similar to the constructor {@link File#File(File, String)} but allows to
	 * define multiple child levels.
	 * 
	 * @param pathElements
	 *            list of elements. If this is empty, the parent is returned.
	 * @return the new file.
	 */
	public static File newFile(File parentFile, String... pathElements) {
		if (pathElements.length == 0) {
			return parentFile;
		}

		File child = new File(parentFile, pathElements[0]);

		String[] remainingElements = new String[pathElements.length - 1];
		System.arraycopy(pathElements, 1, remainingElements, 0,
				pathElements.length - 1);
		return newFile(child, remainingElements);
	}

	/**
	 * Read file content into a string using the default encoding for the
	 * platform. If the file starts with a UTF byte order mark (BOM), the
	 * encoding is ignored and the correct encoding based on this BOM is used
	 * for reading the file.
	 * 
	 * @see EByteOrderMark
	 */
	public static String readFile(File file) throws IOException {
		return readFile(file, Charset.defaultCharset().name());
	}

	/**
	 * Read file content into a string using UTF-8 encoding. If the file starts
	 * with a UTF byte order mark (BOM), the encoding is ignored and the correct
	 * encoding based on this BOM is used for reading the file.
	 * 
	 * @see EByteOrderMark
	 */
	public static String readFileUTF8(File file) throws IOException {
		return readFile(file, UTF8_ENCODING);
	}

	/**
	 * Read file content into a string using the given encoding. If the file
	 * starts with a UTF byte order mark (BOM), the encoding is ignored and the
	 * correct encoding based on this BOM is used for reading the file.
	 * 
	 * @see EByteOrderMark
	 */
	public static String readFile(File file, String encoding)
			throws IOException, UnsupportedEncodingException {
		byte[] buffer = readFileBinary(file);

		EByteOrderMark bom = EByteOrderMark.determineBOM(buffer);
		if (bom != null) {
			return new String(buffer, bom.getBOMLength(), buffer.length
					- bom.getBOMLength(), bom.getEncoding());
		}

		return new String(buffer, encoding);
	}

	/**
	 * Read file content into a list of lines (strings) using the given
	 * encoding. This uses automatic BOM handling, just as
	 * {@link #readFile(File)}.
	 */
	public static List<String> readLines(File file, String encoding)
			throws UnsupportedEncodingException, IOException {
		return StringUtils.splitLinesAsList(readFile(file, encoding));
	}

	/**
	 * Read file content into a list of lines (strings) using UTF-8 encoding.
	 * This uses automatic BOM handling, just as {@link #readFile(File)}.
	 */
	public static List<String> readLinesUTF8(String filePath)
			throws UnsupportedEncodingException, IOException {
		return readLinesUTF8(new File(filePath));
	}

	/**
	 * Read file content into a list of lines (strings) using UTF-8 encoding.
	 * This uses automatic BOM handling, just as {@link #readFile(File)}.
	 */
	public static List<String> readLinesUTF8(File file)
			throws UnsupportedEncodingException, IOException {
		return readLines(file, UTF8_ENCODING);
	}

	/** Read file content into a byte array. */
	public static byte[] readFileBinary(String filePath) throws IOException {
		return readFileBinary(new File(filePath));
	}

	/** Read file content into a byte array. */
	@SuppressWarnings("resource")
	public static byte[] readFileBinary(File file)
			throws FileNotFoundException, IOException {
		FileInputStream in = new FileInputStream(file);

		byte[] buffer = new byte[(int) file.length()];
		ByteBuffer byteBuffer = ByteBuffer.wrap(buffer);

		FileChannel channel = in.getChannel();
		try {
			int readSum = 0;
			while (readSum < buffer.length) {
				int read = channel.read(byteBuffer);
				if (read < 0) {
					throw new IOException(
							"Reached EOF before entire file could be read!");
				}
				readSum += read;
			}
		} finally {
			close(channel);
			close(in);
		}

		return buffer;
	}

	/** Extract a JAR file to a directory. */
	public static void unjar(File jarFile, File targetDirectory)
			throws IOException {
		unzip(jarFile, targetDirectory);
	}

	/**
	 * Extract a ZIP file to a directory.
	 */
	public static void unzip(File zipFile, File targetDirectory)
			throws IOException {
		unzip(zipFile, targetDirectory, null, null);
	}

	/**
	 * Extract the entries of ZIP file to a directory.
	 * 
	 * @param prefix
	 *            Sets a prefix for the the entry names (paths) which should be
	 *            extracted. Only entries which starts with the given prefix are
	 *            extracted. If prefix is <code>null</code> or empty all entries
	 *            are extracted. The prefix will be stripped form the extracted
	 *            entries.
	 * @param charset
	 *            defines the {@link Charset} of the ZIP file. If
	 *            <code>null</code>, the standard of {@link ZipFile} is used
	 *            (which is UTF-8).
	 * @return list of the extracted paths
	 */
	public static List<String> unzip(File zipFile, File targetDirectory,
			String prefix, Charset charset) throws IOException {
		ZipFile zip = null;
		try {
			if (charset == null) {
				zip = new ZipFile(zipFile);
			} else {
				zip = new ZipFile(zipFile, charset);
			}
			Enumeration<? extends ZipEntry> entries = zip.entries();
			List<String> extractedPahts = new ArrayList<String>();

			while (entries.hasMoreElements()) {
				ZipEntry entry = entries.nextElement();
				if (entry.isDirectory()) {
					continue;
				}
				String fileName = entry.getName();
				if (!StringUtils.isEmpty(prefix)) {
					if (!fileName.startsWith(prefix)) {
						continue;
					}
					fileName = StringUtils.stripPrefix(fileName, prefix);
				}

				InputStream entryStream = zip.getInputStream(entry);
				File file = new File(targetDirectory, fileName);
				FileSystemUtils.ensureParentDirectoryExists(file);

				FileOutputStream outputStream = null;
				try {
					outputStream = new FileOutputStream(file);
					FileSystemUtils.copy(entryStream, outputStream);
				} finally {
					FileSystemUtils.close(outputStream);
				}

				entryStream.close();
				extractedPahts.add(fileName);
			}
			return extractedPahts;
		} finally {
			FileSystemUtils.close(zip);
		}
	}

	/**
	 * Write string to a file with the default encoding. This ensures all
	 * directories exist.
	 */
	public static void writeFile(File file, String content) throws IOException {
		writeFile(file, content, Charset.defaultCharset().name());
	}

	/**
	 * Writes the given collection of String as lines into the specified file.
	 * This method uses \n as a line separator.
	 */
	public static void writeLines(File file, Collection<String> lines)
			throws IOException {
		writeFile(file, StringUtils.concat(lines, "\n"));
	}

	/**
	 * Write string to a file with UTF8 encoding. This ensures all directories
	 * exist.
	 */
	public static void writeFileUTF8(File file, String content)
			throws IOException {
		writeFile(file, content, UTF8_ENCODING);
	}

	/** Write string to a file. This ensures all directories exist. */
	public static void writeFile(File file, String content, String encoding)
			throws IOException {
		ensureParentDirectoryExists(file);
		OutputStreamWriter writer = null;
		try {
			writer = new OutputStreamWriter(new FileOutputStream(file),
					encoding);
			writer.write(content);
		} finally {
			FileSystemUtils.close(writer);
		}
	}

	/**
	 * Write string to a file using a UTF encoding. The file will be prefixed
	 * with a byte-order mark. This ensures all directories exist.
	 */
	public static void writeFileWithBOM(File file, String content,
			EByteOrderMark bom) throws IOException {
		ensureParentDirectoryExists(file);
		FileOutputStream out = null;
		OutputStreamWriter writer = null;
		try {
			out = new FileOutputStream(file);
			out.write(bom.getBOM());

			writer = new OutputStreamWriter(out, bom.getEncoding());
			writer.write(content);
			writer.flush();
		} finally {
			FileSystemUtils.close(out);
			FileSystemUtils.close(writer);
		}
	}

	/**
	 * Writes the given bytes to the given file. Directories are created as
	 * needed. The file is closed after writing.
	 */
	public static void writeFileBinary(File file, byte[] bytes)
			throws IOException {
		ensureParentDirectoryExists(file);
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(file);
			out.write(bytes);
		} finally {
			FileSystemUtils.close(out);
		}
	}

	/**
	 * Finds all files and directories contained in the given directory and all
	 * subdirectories matching the filter provided and put them into the result
	 * collection. The given directory itself is not included in the result.
	 * <p>
	 * This method knows nothing about (symbolic and hard) links, so care should
	 * be taken when traversing directories containing recursive links.
	 * 
	 * @param directory
	 *            the directory to start the search from.
	 * @param result
	 *            the collection to add to all files found.
	 * @param filter
	 *            the filter used to determine whether the result should be
	 *            included. If the filter is null, all files and directories are
	 *            included.
	 */
	private static void listFilesRecursively(File directory,
			Collection<File> result, FileFilter filter) {
		for (File file : directory.listFiles()) {
			if (file.isDirectory()) {
				listFilesRecursively(file, result, filter);
			}
			if (filter == null || filter.accept(file)) {
				result.add(file);
			}
		}
	}

	/**
	 * Loads template file with a <a href=
	 * "http://java.sun.com/javase/6/docs/api/java/util/Formatter.html#syntax"
	 * >Format string</a>, formats it and writes result to file.
	 * 
	 * @param templateFile
	 *            the template file with the format string
	 * @param outFile
	 *            the target file, parent directories are created automatically.
	 * @param arguments
	 *            the formatting arguments.
	 * @throws IOException
	 *             if an IO exception occurs or the template file defines an
	 *             illegal format.
	 */
	public static void mergeTemplate(File templateFile, File outFile,
			Object... arguments) throws IOException {
		String template = readFile(templateFile);
		String output;
		try {
			output = String.format(template, arguments);
		} catch (IllegalFormatException e) {
			// We do not pass the cause to the constructor as the required
			// constructor is only defined in 1.6
			throw new IOException("Illegal format: " + e.getMessage());
		}
		writeFile(outFile, output);
	}

	/**
	 * Loads template file with a <a href=
	 * "http://java.sun.com/javase/6/docs/api/java/util/Formatter.html#syntax"
	 * >Format string</a>, formats it and provides result as stream. No streams
	 * are closed by this method.
	 * 
	 * @param inStream
	 *            stream that provides the template format string
	 * @param arguments
	 *            the formatting arguments.
	 * @throws IOException
	 *             if an IOException occurs or the template file defines an
	 *             illegal format.
	 */
	public static InputStream mergeTemplate(InputStream inStream,
			Object... arguments) throws IOException {
		String template = readStream(inStream);
		String output;
		try {
			output = String.format(template, arguments);
		} catch (IllegalFormatException e) {
			// We do not pass the cause to the constructor as the required
			// constructor is only defined in 1.6
			throw new IOException("Illegal format: " + e.getMessage());
		}
		return new ByteArrayInputStream(output.getBytes());
	}

	/** Read input stream into string. */
	public static String readStream(InputStream input) throws IOException {
		return readStream(input, Charset.defaultCharset().name());
	}

	/** Read input stream into string. */
	public static String readStreamUTF8(InputStream input) throws IOException {
		return readStream(input, UTF8_ENCODING);
	}

	/**
	 * Read input stream into string. This method is BOM aware, i.e. deals with
	 * the UTF-BOM.
	 */
	public static String readStream(InputStream input, String encoding)
			throws IOException {
		StringBuilder out = new StringBuilder();
		Reader r = streamReader(input, encoding);
		char[] b = new char[4096];

		int n;
		while ((n = r.read(b)) != -1) {
			out.append(b, 0, n);
		}
		return out.toString();
	}

	/** Read input stream into raw byte array. */
	public static byte[] readStreamBinary(InputStream input) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		copy(input, out);
		return out.toByteArray();
	}

	/**
	 * Returns a reader that wraps the given input stream. This method handles
	 * the BOM transparently. As the normal reader constructors can not deal
	 * with this, direct construction of readers is discouraged.
	 */
	public static Reader streamReader(InputStream in, String encoding)
			throws IOException {
		// we need marking to read the BOM mark
		if (!in.markSupported()) {
			in = new BufferedInputStream(in);
		}

		in.mark(EByteOrderMark.MAX_BOM_LENGTH);
		byte[] prefix = new byte[EByteOrderMark.MAX_BOM_LENGTH];

		EByteOrderMark bom = null;
		try {
			safeRead(in, prefix);
			bom = EByteOrderMark.determineBOM(prefix);
		} catch (IOException e) {
			// just use provided encoding; keep BOM as null
		}

		in.reset();

		if (bom != null) {
			encoding = bom.getEncoding();

			// consume BOM
			for (int i = 0; i < bom.getBOMLength(); ++i) {
				in.read();
			}
		}

		return new InputStreamReader(in, encoding);
	}

	/** Reads properties from a properties file. */
	public static Properties readPropertiesFile(File propertiesFile)
			throws IOException {
		Properties properties = new Properties();
		InputStream inputStream = new FileInputStream(propertiesFile);
		try {
			properties.load(inputStream);
		} finally {
			inputStream.close();
		}
		return properties;
	}

	/**
	 * Determines the root directory from a collection of files. The root
	 * directory is the lowest common ancestor directory of the files in the
	 * directory tree.
	 * <p>
	 * This method does not require the input files to exist.
	 * 
	 * @param files
	 *            Collection of files for which root directory gets determined.
	 *            This collection is required to contain at least 2 files. If it
	 *            does not, an AssertionError is thrown.
	 * 
	 * @throws PreconditionException
	 *             If less than two different files are provided whereas fully
	 *             qualified canonical names are used for comparison.
	 * 
	 * @throws IOException
	 *             Since canonical paths are used for determination of the
	 *             common root, and {@link File#getCanonicalPath()} can throw
	 *             {@link IOException}s.
	 * 
	 * @return Root directory, or null, if the files do not have a common root
	 *         directory.
	 */
	public static File commonRoot(Iterable<? extends File> files)
			throws IOException {
		// determine longest common prefix on canonical absolute paths
		Set<String> absolutePaths = new HashSet<String>();
		for (File file : files) {
			absolutePaths.add(file.getCanonicalPath());
		}

		CCSMPre.isTrue(absolutePaths.size() >= 2,
				"Expected are at least 2 files");

		String longestCommonPrefix = StringUtils
				.longestCommonPrefix(absolutePaths);

		// trim to name of root directory (remove possible equal filename
		// prefixes.)
		int lastSeparator = longestCommonPrefix.lastIndexOf(File.separator);
		if (lastSeparator > -1) {
			longestCommonPrefix = longestCommonPrefix.substring(0,
					lastSeparator);
		}

		if (StringUtils.isEmpty(longestCommonPrefix)) {
			return null;
		}

		return new File(longestCommonPrefix);
	}

	/**
	 * Transparently creates a stream for decompression if the provided stream
	 * is compressed. Otherwise the stream is just handed through. Currently the
	 * following compression methods are supported:
	 * <ul>
	 * <li>GZIP via {@link GZIPInputStream}</li>
	 * </ul>
	 */
	public static InputStream autoDecompressStream(InputStream in)
			throws IOException {
		if (!in.markSupported()) {
			in = new BufferedInputStream(in);
		}
		in.mark(2);
		// check first two bytes for GZIP header
		boolean isGZIP = (in.read() & 0xff | (in.read() & 0xff) << 8) == GZIPInputStream.GZIP_MAGIC;
		in.reset();
		if (isGZIP) {
			return new GZIPInputStream(in);
		}
		return in;
	}

	/**
	 * Closes the given ZIP file quietly, i.e. ignoring a potential IOException.
	 * Additionally it is <code>null</code> safe.
	 */
	public static void close(ZipFile zipFile) {
		if (zipFile == null) {
			return;
		}
		try {
			zipFile.close();
		} catch (IOException e) {
			// ignore
		}
	}

	/**
	 * Convenience method for calling {@link #close(Closeable, ILogger)} with a
	 * <code>null</code>-logger.
	 */
	public static void close(Closeable closeable) {
		close(closeable, null);
	}

	/**
	 * This method can be used to simplify the typical <code>finally</code>
	 * -block of code dealing with streams and readers/writers. It checks if the
	 * provided closeable is <code>null</code>. If not it closes it. An
	 * exception thrown during the close operation is logged with the provided
	 * logger with level <i>warn</i>. If the provided logger is
	 * <code>null</code>, no logging is performed. If no logging is required,
	 * method {@link #close(Closeable)} may also be used.
	 */
	public static void close(Closeable closeable, ILogger logger) {
		if (closeable == null) {
			return;
		}

		try {
			closeable.close();
		} catch (IOException e) {
			if (logger != null) {
				logger.warn("Trouble closing: " + e.getMessage());
			}
		}
	}

	/**
	 * Compares files based on the lexical order of their fully qualified names.
	 * Files must not null.
	 */
	public static void sort(List<File> files) {
		Collections.sort(files, new FilenameComparator());
	}

	/**
	 * Replace platform dependent separator char with forward slashes to create
	 * system-independent paths.
	 */
	public static String normalizeSeparators(String path) {
		return path.replace(File.separatorChar, '/');
	}

	/**
	 * Returns the JAR file for an URL with protocol 'jar'. If the protocol is
	 * not 'jar' an assertion error will be caused! An assertion error is also
	 * thrown if URL does not point to a file.
	 */
	public static File extractJarFileFromJarURL(URL url) {
		CCSMPre.isTrue("jar".equals(url.getProtocol()),
				"May only be used with 'jar' URLs!");

		String path = url.getPath();
		CCSMPre.isTrue(path.startsWith("file:"),
				"May only be used for URLs pointing to files");

		// the exclamation mark is the separator between jar file and path
		// within the file
		int index = path.indexOf('!');
		CCSMAssert.isTrue(index >= 0, "Unknown format for jar URLs");
		path = path.substring(0, index);

		return fromURL(path);
	}

	/**
	 * Often file URLs are created the wrong way, i.e. without proper escaping
	 * characters invalid in URLs. Unfortunately, the URL class allows this and
	 * the Eclipse framework does it. See
	 * http://weblogs.java.net/blog/2007/04/25/how-convert-javaneturl-javaiofile
	 * for details.
	 * 
	 * This method attempts to fix this problem and create a file from it.
	 * 
	 * @throws AssertionError
	 *             if cleaning up fails.
	 */
	private static File fromURL(String url) {

		// We cannot simply encode the URL this also encodes slashes and other
		// stuff. As a result, the file constructor throws an exception. As a
		// simple heuristic, we only fix the spaces.
		// The other route to go would be manually stripping of "file:" and
		// simply creating a file. However, this does not work if the URL was
		// created properly and contains URL escapes.

		url = url.replace(StringUtils.SPACE, "%20");
		try {
			return new File(new URI(url));
		} catch (URISyntaxException e) {
			throw new AssertionError(
					"The assumption is that this method is capable of "
							+ "working with non-standard-compliant URLs, too. "
							+ "Apparently it is not. Invalid URL: " + url
							+ ". Ex: " + e.getMessage());
		}
	}

	/**
	 * Returns whether a filename represents an absolute path.
	 * 
	 * This method returns the same result, independent on which operating
	 * system it gets executed. In contrast, the behavior of
	 * {@link File#isAbsolute()} is operating system specific.
	 */
	public static boolean isAbsolutePath(String filename) {
		// Unix and MacOS: absolute path starts with slash or user home
		if (filename.startsWith("/") || filename.startsWith("~")) {
			return true;
		}
		// Windows and OS/2: absolute path start with letter and colon
		if (filename.length() > 2 && Character.isLetter(filename.charAt(0))
				&& filename.charAt(1) == ':') {
			return true;
		}
		// UNC paths (aka network shares): start with double backslash
		if (filename.startsWith("\\\\")) {
			return true;
		}

		return false;
	}

	/**
	 * Reads bytes of data from the input stream into an array of bytes until
	 * the array is full. This method blocks until input data is available, end
	 * of file is detected, or an exception is thrown.
	 * 
	 * The reason for this method is that {@link InputStream#read(byte[])} may
	 * read less than the requested number of bytes, while this method ensures
	 * the data is complete.
	 * 
	 * @param in
	 *            the stream to read from.
	 * @param data
	 *            the stream to read from.
	 * @throws IOException
	 *             if reading the underlying stream causes an exception.
	 * @throws EOFException
	 *             if the end of file was reached before the requested data was
	 *             read.
	 */
	public static void safeRead(InputStream in, byte[] data)
			throws IOException, EOFException {
		safeRead(in, data, 0, data.length);
	}

	/**
	 * Reads <code>length</code> bytes of data from the input stream into an
	 * array of bytes and stores it at position <code>offset</code>. This method
	 * blocks until input data is available, end of file is detected, or an
	 * exception is thrown.
	 * 
	 * The reason for this method is that
	 * {@link InputStream#read(byte[], int, int)} may read less than the
	 * requested number of bytes, while this method ensures the data is
	 * complete.
	 * 
	 * @param in
	 *            the stream to read from.
	 * @param data
	 *            the stream to read from.
	 * @param offset
	 *            the offset in the array where the first read byte is stored.
	 * @param length
	 *            the length of data read.
	 * @throws IOException
	 *             if reading the underlying stream causes an exception.
	 * @throws EOFException
	 *             if the end of file was reached before the requested data was
	 *             read.
	 */
	public static void safeRead(InputStream in, byte[] data, int offset,
			int length) throws IOException, EOFException {
		while (length > 0) {
			int read = in.read(data, offset, length);
			if (read < 0) {
				throw new EOFException(
						"Reached end of file before completing read.");
			}
			offset += read;
			length -= read;
		}
	}

	/** Obtains the system's temporary directory */
	public static File getTmpDir() {
		return new File(System.getProperty("java.io.tmpdir"));
	}

	/** Obtains the current user's home directory */
	public static File getUserHomeDir() {
		return new File(System.getProperty("user.home"));
	}

	/** Checks whether two files have the same content. */
	public static boolean contentsEqual(File file1, File file2)
			throws IOException {
		byte[] content1 = readFileBinary(file1);
		byte[] content2 = readFileBinary(file2);
		return Arrays.equals(content1, content2);
	}

	/**
	 * Opens an {@link InputStream} for the entry with the given name in the
	 * given JAR file
	 */
	public static InputStream openJarFileEntry(JarFile jarFile, String entryName)
			throws IOException {
		JarEntry entry = jarFile.getJarEntry(entryName);
		if (entry == null) {
			throw new IOException("No entry '" + entryName
					+ "' found in JAR file '" + jarFile + "'");
		}
		return jarFile.getInputStream(entry);
	}

	/**
	 * Returns whether the given file is non-null, a plain file and is readable.
	 */
	public static boolean isReadableFile(File file) {
		return file != null && file.isFile() && file.canRead();
	}

	/**
	 * Concatenates all path parts into a single path with normalized
	 * separators.
	 */
	public static String concatenatePaths(String firstParent, String... paths) {
		return FileSystemUtils.normalizeSeparators(Paths
				.get(firstParent, paths).toString());
	}
}