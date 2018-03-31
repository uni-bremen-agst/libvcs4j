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
package org.conqat.lib.scanner;

/**
 * Constants for dealing with languages. These constants cannot be declared in
 * {@link ELanguage}, since the literals have to be the first statements in an
 * enumeration.
 * 
 * @author herrmama
 * @author $Author: heinemann $
 * @version $Rev: 49751 $
 * @ConQAT.Rating GREEN Hash: B3DBDCE24FE637FDC83354966F1DE5D3
 */
/* package */class ELanguageConstants {

	/** Regex for trimming C-like comments. */
	/* package */static final String CLIKE_COMMENT_REGEX = "(^ *(/[*]+|//+|[*]+(?!/)))|([*]+/$)";
}