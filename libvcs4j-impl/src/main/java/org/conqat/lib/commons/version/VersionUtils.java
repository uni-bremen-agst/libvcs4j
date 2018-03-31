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
package org.conqat.lib.commons.version;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.swing.JOptionPane;

import org.conqat.lib.commons.error.FormatException;
import org.conqat.lib.commons.filesystem.FileSystemUtils;

/**
 * Utility code for dealing with versions.
 * 
 * @author hummelb
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 44F6803A130EA525E3206FA7A81DD03D
 */
public class VersionUtils {

	/**
	 * Checks whether a new version of an application is available. This
	 * retrieves the most recent version from an URL using
	 * {@link #getMostRecentVersion(String)}. If the version could be retrieved
	 * and is more recent than the running version, an informative message box
	 * appears (Swing).
	 */
	public static void checkForNewVersion(final String appName,
			String versionFileUrl, Version runningVersion) {
		final Version currentVersion;
		try {
			currentVersion = getMostRecentVersion(versionFileUrl);
		} catch (Exception e) {
			// just ignore
			return;
		}

		if (currentVersion.compareTo(runningVersion) > 0) {
			javax.swing.SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					JOptionPane
							.showMessageDialog(null, "The newer version "
									+ currentVersion + " of " + appName
									+ " is available for download!",
									"Update available",
									JOptionPane.INFORMATION_MESSAGE);
				}
			});
		}
	}

	/** Get the most recent version from a file available via an URL. */
	public static Version getMostRecentVersion(String versionFileUrl)
			throws IOException, FormatException {
		URL versionURL = new URL(versionFileUrl);
		InputStream in = versionURL.openStream();
		String versionString = FileSystemUtils.readStream(in);
		in.close();
		return Version.parseVersion(versionString);
	}
}