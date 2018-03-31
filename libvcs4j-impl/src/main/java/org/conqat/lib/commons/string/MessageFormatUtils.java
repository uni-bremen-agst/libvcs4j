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
package org.conqat.lib.commons.string;

/**
 * This class provides all utility methods for dealing with
 * {@link java.text.MessageFormat}.
 * 
 * @author $Author: beller$
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 4E6DBDA0959DE0BE925BEEBDC4D2472D
 */
public class MessageFormatUtils {
	
	/**
	 * Escapes a string so that it can be safely passed to
	 * {@link java.text.MessageFormat}.
	 */
	public static String escapeString(String string) {
		if (StringUtils.isEmpty(string)) {
			return string;
		}

		string = string.replace("'", "''");
		string = string.replace("{", "'{'");
		string = string.replace("}", "'}'");

		return string;
	}
}