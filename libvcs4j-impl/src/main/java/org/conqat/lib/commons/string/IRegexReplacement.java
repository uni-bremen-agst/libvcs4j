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

import java.util.regex.Pattern;

/**
 * This interface describe a replacement to be performed on a string. Syntax for
 * patterns and replacements is specified in the API documentation of
 * {@link java.util.regex.Pattern} and {@link java.util.regex.Matcher}.
 * 
 * @author Florian Deissenboeck
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 3A0FA31AF246E1446886667B549332CB
 */
public interface IRegexReplacement {
    /** The pattern to match. */
    public Pattern getPattern();

    /**
     * The replacement for the pattern. The empty string may be used to define a
     * deletion.
     */
    public String getReplacement();
}