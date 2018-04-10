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
import java.io.IOException;
import java.net.URL;
import java.util.LinkedHashSet;

import org.conqat.lib.commons.string.StringUtils;

/**
 * Utility class for dealing with class paths.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 44650 $
 * @ConQAT.Rating GREEN Hash: 73EBAE8CCA0A75CFC8E285417DF29ECB
 */
public class ClassPathUtils {

	/** Suffix for class files. */
	public static final String CLASS_FILE_SUFFIX = ".class";

	/**
	 * This method calls {@link #createClassPath(IURLResolver, Class)} for each
	 * class and concatenates them with {@link File#pathSeparator}.
	 */
	public static String createClassPathAsString(IURLResolver resolver,
			Class<?>... anchorClasses) throws IOException {
		return StringUtils.concat(
				createClassPathAsSet(resolver, anchorClasses),
				File.pathSeparator);
	}

	/**
	 * This method calls {@link #createClassPath(IURLResolver, Class)} for each
	 * class and returns the classpath as String array.
	 */
	public static String[] createClassPathAsArray(IURLResolver resolver,
			Class<?>... anchorClasses) throws IOException {
		return createClassPathAsSet(resolver, anchorClasses).toArray(
				new String[0]);
	}

	/**
	 * This method calls {@link #createClassPath(IURLResolver, Class)} for each
	 * class and returns the classpath as ordered set.
	 * 
	 * @param resolver
	 *            an optional resolver (may be null) that can be used to map
	 *            special URLs (for example in the Eclipse context).
	 */
	public static LinkedHashSet<String> createClassPathAsSet(
			IURLResolver resolver, Class<?>... anchorClasses)
			throws IOException {
		LinkedHashSet<String> classPath = new LinkedHashSet<String>();
		for (Class<?> clazz : anchorClasses) {
			classPath.add(createClassPath(resolver, clazz));
		}
		return classPath;
	}

	/**
	 * Create class path for an anchor class. This method checks were the anchor
	 * class resides, i.e. in a jar file or directory and returns it.
	 * 
	 * @param resolver
	 *            an optional resolver (may be null) that can be used to map
	 *            special URLs (for example in the Eclipse context).
	 */
	public static String createClassPath(IURLResolver resolver,
			Class<?> anchorClass) throws IOException {
		// this should be a save way to obtain the location
		URL url = obtainClassFileURL(anchorClass);

		if (url == null) {
			throw new AssertionError("Internal assumption violated.");
		}

		// resolve URL if resolver was provided
		if (resolver != null) {
			url = resolver.resolve(url);
		}

		String protocol = url.getProtocol();

		if ("file".equals(protocol)) {
			return createFileClasspath(url, anchorClass);
		}

		if ("jar".equals(protocol)) {
			return FileSystemUtils.extractJarFileFromJarURL(url)
					.getCanonicalPath();
		}

		// If this resides somewhere on the net, we have a problem.
		throw new IOException("Unsupported protocol: " + protocol);
	}

	/**
	 * Returns the URL for the .class-file of a given class. For inner classes,
	 * the URL of the surrounding class file is returned.
	 */
	public static URL obtainClassFileURL(Class<?> clazz) {
		String name = clazz.getSimpleName();

		// deal with inner classes
		while (clazz.getEnclosingClass() != null) {
			clazz = clazz.getEnclosingClass();
			name = clazz.getSimpleName() + "$" + name;
		}

		return clazz.getResource(name + ".class");
	}

	/** Converts file URL to normal file location. */
	private static String createFileClasspath(URL url, Class<?> clazz)
			throws IOException {
		String path = url.getPath();

		// strip class name and '.class'
		path = path.substring(0, path.length() - 6 - clazz.getName().length());
		return new File(path).getCanonicalPath();
	}

	/** Interface for performing URL resolving. */
	public static interface IURLResolver {

		/** Resolves the URL and returns the result. */
		URL resolve(URL url) throws IOException;
	}
}