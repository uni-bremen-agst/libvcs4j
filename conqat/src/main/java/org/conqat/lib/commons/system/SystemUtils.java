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
package org.conqat.lib.commons.system;

/**
 * Utility class providing functionality regarding the current system.
 * 
 * @author $Author: heinemann $
 * @version $Rev: 47876 $
 * @ConQAT.Rating GREEN Hash: 1FEFDA1885FBA4056AB2B6F9C547BA31
 */
public class SystemUtils {

	/** Enumeration of operating systems. */
	public static enum EOperatingSystem {

		/** Identifies a Microsoft Windows operating system. */
		WINDOWS,

		/** Identifies a Linux operating system. */
		LINUX,

		/** Identifies an Apple Mac operating system. */
		MAC,

		/** Identifies an unknown operating system. */
		UNKNOWN;

		/** Returns the operating system the Java VM runs in. */
		private static EOperatingSystem getCurrent() {
			String osName = getOperatingSystemName().toUpperCase();
			for (EOperatingSystem system : values()) {
				if (osName.startsWith(system.name())) {
					return system;
				}
			}
			return UNKNOWN;
		}
	}

	/** Returns the operating system the Java VM runs in. */
	public static EOperatingSystem getOperatingSystem() {
		return EOperatingSystem.getCurrent();
	}

	/** Returns the operating system name the Java VM runs in. */
	public static String getOperatingSystemName() {
		return System.getProperty("os.name");
	}

	/** Returns true if the current operating system is Microsoft Windows. */
	public static boolean isWindows() {
		return SystemUtils.getOperatingSystem() == EOperatingSystem.WINDOWS;
	}

	/**
	 * Returns the architecture name of the Java VM. This is neither returns the
	 * architecture of the processor nor the architecture of the operating
	 * system, although the property is prefixed with 'os.' (@see <a href=
	 * "http://mark.koli.ch/javas-osarch-system-property-is-the-bitness-of-the-jre-not-the-operating-system"
	 * >this site</a> for more information). I.e. running a 32 bit JVM on a 64
	 * bit Windows operating system will return 'x86'.
	 */
	public static String getJVMArchitectureName() {
		return System.getProperty("os.arch");
	}

	/**
	 * Returns <code>true</code> if the current Java VM runs with a 64 bit
	 * architecture. E.g. will return <code>false</code> for a 32 bit JVM on a
	 * 64 bit operating system.
	 */
	public static boolean is64BitJVM() {
		return getJVMArchitectureName().contains("64");
	}
}
