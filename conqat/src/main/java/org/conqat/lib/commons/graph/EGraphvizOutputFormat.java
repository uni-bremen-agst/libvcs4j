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
package org.conqat.lib.commons.graph;

/**
 * Enumeration for the different output formats supported by Graphviz.
 * 
 * @author Florian Deissenboeck
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 28D555175F56F26A546460EC75123014
 */
public enum EGraphvizOutputFormat {

	/** canon */
	CANON("Prettyprint input; no layout is done.", "dot"),

	/** dot */
	DOT("Attributed DOT. Prints input with layout information attached.", "dot"),

	/** gif */
	GIF("GIF output.", "gif"),

	/** jpeg */
	JPG("JPEG output.", "jpg"),

	/** ps2 */
	PS2("PostScript (EPSF) output with PDF annotations.", "ps"),

	/** png */
	PNG("PNG (Portable Network Graphics) output.", "png"),
	
	/** svg */
	SVG("SVG (Scalable Vector Graphics) output.", "svg");

	/** Format description. */
	private final String description;

	/** File extension typically used for this format. */
	private final String fileExtension;

	/**
	 * Create enum constant.
	 */
	private EGraphvizOutputFormat(String description, String fileExtension) {
		this.description = description;
		this.fileExtension = fileExtension;
	}

	/**
	 * Get format description.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Get file extension typically used for this format.
	 */
	public String getFileExtension() {
		return fileExtension;
	}

}