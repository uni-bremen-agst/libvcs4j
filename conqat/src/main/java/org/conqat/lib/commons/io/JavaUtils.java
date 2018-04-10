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
package org.conqat.lib.commons.io;

import static java.io.File.separatorChar;

import java.io.File;

/**
 * This class provides utilities to access a Java runtime execution environment.
 * 
 * @author juergens
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 76FAD1CBCE2EE89D6318E9297AF40480
 */
public class JavaUtils {

	/** JAVA_HOME environment variable. */
	private static final String JAVA_HOME = System.getProperty("java.home");

	/** List of candidate java executable names. */
	private static final String[] CANDIDATE_JAVA_EXECUTABLES = { "java",
			"java.exe", "javaw", "javaw.exe", "j9w", "j9w.exe", "j9", "j9.exe" };

	/**
	 * The list of locations in which to look for the java executable in
	 * candidate VM install locations, relative to the VM install location.
	 */
	private static final String[] CANDIDATE_JAVA_LOCATIONS = {
			"bin" + separatorChar,
			"jre" + separatorChar + "bin" + separatorChar };

	/**
	 * Starting in the specified VM install location, attempt to find the 'java'
	 * executable file. If found, return the corresponding <code>File</code>
	 * object, otherwise return <code>null</code>.
	 * 
	 * This is copied from
	 * <code>org.eclipse.jdt.internal.launching.StandardVMType</code>.
	 */
	public static File findJavaExecutable(File vmInstallLocation) {
		// Try each candidate in order. The first one found wins. Thus, the
		// order of CANDIDATE_JAVA_EXECUTABLES and CANDIDATE_JAVA_LOCATIONS is
		// significant.
		for (int i = 0; i < CANDIDATE_JAVA_EXECUTABLES.length; i++) {
			for (int j = 0; j < CANDIDATE_JAVA_LOCATIONS.length; j++) {
				File javaFile = new File(vmInstallLocation,
						CANDIDATE_JAVA_LOCATIONS[j]
								+ CANDIDATE_JAVA_EXECUTABLES[i]);
				if (javaFile.isFile()) {
					return javaFile;
				}
			}
		}
		return null;
	}

	/**
	 * Use {@link #findJavaExecutable(File)} to search in the directory
	 * specified by environment variable <code>JAVA_HOME</code> for the Java
	 * executable.
	 */
	public static File obtainJavaExecutable() {
		return findJavaExecutable(new File(JAVA_HOME));
	}

	/**
	 * Use {@link #obtainJavaExecutable()} to determine the Java executable via
	 * environment variable <code>JAVA_HOME</code>. If this fails, a command
	 * that expects the Java executable to be on the path is returned.
	 */
	public static String obtainJavaExecutionCommand() {
		File executable = obtainJavaExecutable();
		if (executable != null) {
			return executable.getAbsolutePath();
		}
		return CANDIDATE_JAVA_EXECUTABLES[0];
	}

}