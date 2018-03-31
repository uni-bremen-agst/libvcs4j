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
package org.conqat.lib.commons.html;

/**
 * Enumeration of pseudo classes supported.
 * <p>
 * List taken from http://www.w3schools.com/css/css_pseudo_classes.asp.
 * 
 * @author hummelb
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: DE7D19EB3545BBCB4FBC1B258FEB22BA
 */
public enum ECSSPseudoClass {

	/** Used to indicate the lack of any class. */
	NONE(""),

	/** Adds special style to an activated element. */
	ACTIVE(":active"),

	/** Adds special style to an element while the element has focus. */
	FOCUS(":focus"),

	/** Adds special style to an element when you mouse over it. */
	HOVER(":hover"),

	/** Adds special style to an unvisited link. */
	LINK(":link"),

	/** Adds special style to a visited link. */
	VISITED(":visited"),

	/**
	 * Adds special style to an element that is the first child of some other
	 * element.
	 */
	FIRST_CHILD(":first-child");

	/** The name of the pseudo class including the colon. */
	private final String name;

	/** Constructor. */
	private ECSSPseudoClass(String name) {
		this.name = name;
	}

	/** Returns the name of the pseudo class including the leading colon. */
	public String getName() {
		return name;
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return name;
	}
}