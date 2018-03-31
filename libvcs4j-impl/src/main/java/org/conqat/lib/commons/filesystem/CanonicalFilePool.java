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

import java.util.HashMap;
import java.util.Map;

/**
 * Pool for {@link CanonicalFile}s.
 * 
 * @author juergens
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 6251C0B39E28B135B2FF3047DF399FED
 */
public class CanonicalFilePool {

	/** Set that manages pooled files */
	private final Map<CanonicalFile, CanonicalFile> internedFiles = new HashMap<CanonicalFile, CanonicalFile>();

	/** Get file from pool */
	public CanonicalFile intern(CanonicalFile file) {
		if (!internedFiles.containsKey(file)) {
			internedFiles.put(file, file);
		}

		return internedFiles.get(file);
	}
}