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

import java.awt.Color;
import java.awt.Font;
import java.util.HashMap;

import javax.swing.JEditorPane;
import javax.swing.text.Element;
import javax.swing.text.ViewFactory;

import org.conqat.lib.scanner.ETokenType.ETokenClass;

/**
 * View factory to be used with CCSM scanners. To use this implement an editor
 * kit like the one below and set it on your text component via
 * {@link JEditorPane#setEditorKit(javax.swing.text.EditorKit)}.
 * 
 * <pre>
 * public class ExampleEditorKit extends StyledEditorKit {
 * 
 * 	private final ViewFactory viewFactory;
 * 
 * 	public ExampleEditorKit() {
 * 		ILenientScanner scanner = ScannerFactory.newLenientScanner(
 * 				ELanguage.JAVA, &quot;&quot;, null);
 * 		HashMap&lt;ETokenClass, Color&gt; colorMap = new HashMap&lt;ETokenClass, Color&gt;();
 * 		colorMap.put(ETokenClass.COMMENT, new Color(51, 102, 102));
 * 		colorMap.put(ETokenClass.KEYWORD, new Color(102, 0, 102));
 * 		colorMap.put(ETokenClass.LITERAL, new Color(51, 0, 255));
 * 		colorMap.put(ETokenClass.ERROR, Color.red);
 * 
 * 		HashMap&lt;ETokenClass, Integer&gt; styleMap = new HashMap&lt;ETokenClass, Integer&gt;();
 * 		styleMap.put(ETokenClass.COMMENT, Font.ITALIC);
 * 		styleMap.put(ETokenClass.KEYWORD, Font.BOLD);
 * 
 * 		viewFactory = new SyntaxHighlightingViewFactory(scanner, colorMap,
 * 				styleMap);
 * 	}
 * 
 * 	&#064;Override
 * 	public ViewFactory getViewFactory() {
 * 		return viewFactory;
 * 	}
 * }
 * </pre>
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 3F30853C579AD7AF3B75B5B87975B86A
 */
public class SyntaxHighlightingViewFactory implements ViewFactory {

	/** Scanner to be used. */
	private final IScanner scanner;

	/** Maps from token class to color. */
	private final HashMap<ETokenClass, Color> colorMap;

	/**
	 * Maps from token class to font style. Style integers are defined by class
	 * {@link Font}.
	 */
	private final HashMap<ETokenClass, Integer> styleMap;

	/**
	 * Create new view factory.
	 * 
	 * @param scanner
	 *            scanner to be used.
	 * @param colorMap
	 *            maps from token class to color.
	 * @param styleMap
	 *            maps from token class to font style. Style integers are
	 *            defined by class {@link Font}.
	 */
	public SyntaxHighlightingViewFactory(IScanner scanner,
			HashMap<ETokenClass, Color> colorMap,
			HashMap<ETokenClass, Integer> styleMap) {
		this.scanner = scanner;
		this.colorMap = colorMap;
		this.styleMap = styleMap;
	}

	/** Create view. */
	@Override
	public SyntaxHighlightingView create(Element element) {
		return new SyntaxHighlightingView(element, scanner, colorMap, styleMap);
	}
}