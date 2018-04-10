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

import java.io.PrintStream;
import java.util.IdentityHashMap;
import java.util.Map;

import org.conqat.lib.commons.collections.TwoDimHashMap;

/**
 * This class is used for managing cascading style sheets. It keeps track of all
 * declaration blocks and selectors used.
 * 
 * @author hummelb
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 62DF81A26871978562E17F98CB989E5F
 */
public abstract class CSSManagerBase {

	/** Default declarations for elements (i.e. without class). */
	private final TwoDimHashMap<EHTMLElement, ECSSPseudoClass, CSSDeclarationBlock> defaultDeclarations = new TwoDimHashMap<EHTMLElement, ECSSPseudoClass, CSSDeclarationBlock>();

	/** The names the declaration blocks are registered with. */
	private final Map<CSSDeclarationBlock, String> classNames = new IdentityHashMap<CSSDeclarationBlock, String>();

	/** Counter used for generating unique CSS class names. */
	private static int classCounter = 0;

	/** Returns whether there is a default declaration for the given element. */
	public boolean hasDefaultDeclaration(EHTMLElement element) {
		return hasDefaultDeclaration(element, ECSSPseudoClass.NONE);
	}

	/** Returns whether there is a default declaration for the given element. */
	public boolean hasDefaultDeclaration(EHTMLElement element,
			ECSSPseudoClass pseudoClass) {
		return defaultDeclarations.containsKey(element, pseudoClass);
	}

	/** Adds a single selector and its block to this manager. */
	public final void addDefaultDeclaration(EHTMLElement element,
			CSSDeclarationBlock block) {
		addDefaultDeclaration(element, ECSSPseudoClass.NONE, block);
	}

	/** Adds a single selector and its block to this manager. */
	public void addDefaultDeclaration(EHTMLElement element,
			ECSSPseudoClass pseudoClass, CSSDeclarationBlock block) {
		if (defaultDeclarations.containsKey(element, pseudoClass)) {
			throw new IllegalStateException("May not add element " + element
					+ " twice.");
		}
		if (!element.allowsAttribute(EHTMLAttribute.STYLE)) {
			throw new IllegalArgumentException("The given element "
					+ element.getName() + " does not support styles!");
		}
		defaultDeclarations.putValue(element, pseudoClass, block);
	}

	/**
	 * Returns the name of the CSS class used for this block. If the block is
	 * not yet known, it is registered with this manager.
	 */
	public String getCSSClassName(CSSDeclarationBlock block) {
		String name = classNames.get(block);
		if (name == null) {
			name = generateCSSClassName();
			classNames.put(block, name);
		}
		return name;
	}

	/**
	 * Generates a suitable name for a CSS class. This may be overridden by
	 * subclasses. However it must be made sure, that the class names returned
	 * are unique and do not overlap with HTML element names.
	 */
	protected String generateCSSClassName() {
		return "CSSCLASS" + ++classCounter;
	}

	/**
	 * Write all selectors with their blocks to the given stream. The format is
	 * the one usually used in CSS files. This merely calls
	 * {@link #writeOutDefaultDeclarations(PrintStream)} and
	 * {@link #writeOutDeclarations(PrintStream)}.
	 */
	protected void writeOut(PrintStream ps) {

		writeOutDefaultDeclarations(ps);
		writeOutDeclarations(ps);
	}

	/** Write out default declarations for element (i.e. without specific class). */
	protected void writeOutDefaultDeclarations(PrintStream ps) {
		for (EHTMLElement element : defaultDeclarations.getFirstKeys()) {
			for (ECSSPseudoClass pseudocssClass : defaultDeclarations
					.getSecondKeys(element)) {
				String selector = element.getName() + pseudocssClass.getName();
				writeBlock(defaultDeclarations
						.getValue(element, pseudocssClass), selector, ps);
			}
		}
	}

	/** Write out declarations. */
	protected void writeOutDeclarations(PrintStream ps) {
		for (Map.Entry<CSSDeclarationBlock, String> entry : classNames
				.entrySet()) {
			writeBlock(entry.getKey(), "." + entry.getValue(), ps);
		}
	}

	/** Writes a single block/selector pair to the given stream. */
	private void writeBlock(CSSDeclarationBlock block, String selector,
			PrintStream ps) {
		ps.print(selector);
		ps.println(" {");
		block.writeOut(ps, "  ");
		ps.println("}");
		ps.println();
	}
}