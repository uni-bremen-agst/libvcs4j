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

import static org.conqat.lib.commons.html.ECSSProperty.BORDER_BOTTOM_COLOR;
import static org.conqat.lib.commons.html.ECSSProperty.BORDER_BOTTOM_STYLE;
import static org.conqat.lib.commons.html.ECSSProperty.BORDER_BOTTOM_WIDTH;
import static org.conqat.lib.commons.html.ECSSProperty.BORDER_LEFT_COLOR;
import static org.conqat.lib.commons.html.ECSSProperty.BORDER_LEFT_STYLE;
import static org.conqat.lib.commons.html.ECSSProperty.BORDER_LEFT_WIDTH;
import static org.conqat.lib.commons.html.ECSSProperty.BORDER_RIGHT_COLOR;
import static org.conqat.lib.commons.html.ECSSProperty.BORDER_RIGHT_STYLE;
import static org.conqat.lib.commons.html.ECSSProperty.BORDER_RIGHT_WIDTH;
import static org.conqat.lib.commons.html.ECSSProperty.BORDER_TOP_COLOR;
import static org.conqat.lib.commons.html.ECSSProperty.BORDER_TOP_STYLE;
import static org.conqat.lib.commons.html.ECSSProperty.BORDER_TOP_WIDTH;
import static org.conqat.lib.commons.html.ECSSProperty.MARGIN_BOTTOM;
import static org.conqat.lib.commons.html.ECSSProperty.MARGIN_LEFT;
import static org.conqat.lib.commons.html.ECSSProperty.MARGIN_RIGHT;
import static org.conqat.lib.commons.html.ECSSProperty.MARGIN_TOP;
import static org.conqat.lib.commons.html.ECSSProperty.PADDING_BOTTOM;
import static org.conqat.lib.commons.html.ECSSProperty.PADDING_LEFT;
import static org.conqat.lib.commons.html.ECSSProperty.PADDING_RIGHT;
import static org.conqat.lib.commons.html.ECSSProperty.PADDING_TOP;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * This class describes a set of CSS declarations (property value pairs).
 * Additionally it allows for simple multiple inheritance, where the properties
 * of all inherited blocks are merged (including the block itself). The classes
 * coming later in the inheritance list and the block itself will overwrite any
 * properties defined multiple times.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 45280 $
 * @ConQAT.Rating GREEN Hash: 3428CA5EDC6EB4820141A098E021DF66
 */
public class CSSDeclarationBlock {

	/** The properties and the values */
	private final Map<ECSSProperty, String> properties = new EnumMap<ECSSProperty, String>(
			ECSSProperty.class);

	/** The list of blocks we inherit from. */
	private final List<CSSDeclarationBlock> inheritsFrom = new ArrayList<CSSDeclarationBlock>();

	/**
	 * Create new declaration block.
	 * 
	 * @param values
	 *            the property value pairs to add (so the number must be even).
	 */
	public CSSDeclarationBlock(Object... values) {
		if (values.length % 2 != 0) {
			throw new IllegalArgumentException(
					"Expected even number of arguments");
		}
		for (int i = 0; i < values.length; i += 2) {
			if (!(values[i] instanceof ECSSProperty)) {
				throw new IllegalArgumentException(
						"Expected CSS property as parameter " + i
								+ " instead of " + values[i].getClass());
			}
			if (!(values[i + 1] instanceof String)) {
				throw new IllegalArgumentException(
						"Expected property value (String) as parameter "
								+ (i + 1) + " instead of "
								+ values[i + 1].getClass());
			}

			setProperty((ECSSProperty) values[i], (String) values[i + 1]);
		}
	}

	/**
	 * Create new declaration block.
	 * 
	 * @param superBlock
	 *            the block to inherit from.
	 * @param values
	 *            the property value pairs to add (so the number must be even).
	 */
	public CSSDeclarationBlock(CSSDeclarationBlock superBlock, Object... values) {
		this(values);
		inheritFrom(superBlock);
	}

	/**
	 * Adds a property to this block. Is a property with this name exists, it
	 * will be overwritten.
	 * 
	 * @return this
	 */
	public CSSDeclarationBlock setProperty(ECSSProperty property, String value) {
		properties.put(property, value);
		return this;
	}

	/**
	 * Sets all given properties to the same value.
	 * 
	 * @return this
	 */
	private CSSDeclarationBlock setProperties(String value,
			ECSSProperty... properties) {
		for (ECSSProperty p : properties) {
			setProperty(p, value);
		}
		return this;
	}

	/**
	 * Sets the margin to the given value.
	 * 
	 * @return this
	 */
	public CSSDeclarationBlock setMargin(String value) {
		return setProperties(value, MARGIN_BOTTOM, MARGIN_LEFT, MARGIN_RIGHT,
				MARGIN_TOP);
	}

	/**
	 * Sets the padding to the given value.
	 * 
	 * @return this
	 */
	public CSSDeclarationBlock setPadding(String value) {
		return setProperties(value, PADDING_BOTTOM, PADDING_LEFT,
				PADDING_RIGHT, PADDING_TOP);
	}

	/**
	 * Sets the border to the given values.
	 * 
	 * @return this
	 */
	public CSSDeclarationBlock setBorder(String width, String style,
			String color) {
		setBorderWidth(width);
		setBorderStyle(style);
		setBorderColor(color);
		return this;
	}

	/**
	 * Sets the border width to the given value.
	 * 
	 * @return this
	 */
	public CSSDeclarationBlock setBorderWidth(String width) {
		return setProperties(width, BORDER_BOTTOM_WIDTH, BORDER_LEFT_WIDTH,
				BORDER_RIGHT_WIDTH, BORDER_TOP_WIDTH);
	}

	/**
	 * Sets the border style to the given value.
	 * 
	 * @return this
	 */
	public CSSDeclarationBlock setBorderStyle(String style) {
		return setProperties(style, BORDER_BOTTOM_STYLE, BORDER_LEFT_STYLE,
				BORDER_RIGHT_STYLE, BORDER_TOP_STYLE);
	}

	/**
	 * Sets the border color to the given value.
	 * 
	 * @return this
	 */
	public CSSDeclarationBlock setBorderColor(String color) {
		return setProperties(color, BORDER_BOTTOM_COLOR, BORDER_LEFT_COLOR,
				BORDER_RIGHT_COLOR, BORDER_TOP_COLOR);
	}

	/**
	 * Removes the property from this block (whether it exists or not).
	 * 
	 * @return this
	 */
	public CSSDeclarationBlock removeProperty(ECSSProperty property) {
		properties.remove(property);
		return this;
	}

	/**
	 * Returns the value of the property (or null if it is not defined for this
	 * block).
	 */
	public String getProperty(ECSSProperty property) {
		return properties.get(property);
	}

	/**
	 * Adds another block to inherit from.
	 * 
	 * @return this
	 */
	public CSSDeclarationBlock inheritFrom(CSSDeclarationBlock css) {
		inheritsFrom.add(css);
		return this;
	}

	/**
	 * Adds all properties (including those inherited) to the given map. Calling
	 * this with an empty map will result in a map containing the actual
	 * properties of this block.
	 */
	private void fillFullPropertyMap(Map<ECSSProperty, String> map) {
		for (CSSDeclarationBlock block : inheritsFrom) {
			block.fillFullPropertyMap(map);
		}
		map.putAll(properties);
	}

	/**
	 * Writes the full (including inherited) properties into the given stream
	 * using the format for CSS files, i.e. one property in each line followed
	 * by a colon, the value, and a semicolon.
	 */
	public void writeOut(PrintStream ps, String indent) {
		Map<ECSSProperty, String> result = new EnumMap<ECSSProperty, String>(
				ECSSProperty.class);
		fillFullPropertyMap(result);

		for (ECSSProperty property : result.keySet()) {
			ps.println(indent + property.getName() + ": "
					+ result.get(property) + ";");
		}
	}

	/**
	 * Returns the full (including inherited) properties as a single line string
	 * using the format suitable for inline styles as used in HTML.
	 */
	public String toInlineStyle() {
		StringBuilder sb = new StringBuilder();
		Map<ECSSProperty, String> result = new EnumMap<ECSSProperty, String>(
				ECSSProperty.class);
		fillFullPropertyMap(result);

		for (ECSSProperty property : result.keySet()) {
			sb.append(property.getName() + ": " + result.get(property) + "; ");
		}
		return sb.toString();
	}
}