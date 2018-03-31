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

import static org.conqat.lib.commons.html.EHTMLAttribute.SRC;
import static org.conqat.lib.commons.html.EHTMLAttribute.TYPE;
import static org.conqat.lib.commons.html.EHTMLElement.SCRIPT;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import org.conqat.lib.commons.filesystem.FileSystemUtils;
import org.conqat.lib.commons.string.StringUtils;
import org.conqat.lib.commons.xml.IXMLResolver;
import org.conqat.lib.commons.xml.XMLWriter;

/**
 * This class is used for writing HTML.
 * 
 * @author $Author: poehlmann $
 * @version $Rev: 47486 $
 * @ConQAT.Rating YELLOW Hash: 6DDBBF970B3981AA0143B74FB7D206CD
 */
public class HTMLWriter extends XMLWriter<EHTMLElement, EHTMLAttribute> {

	/** The CSS manager class used. */
	private final CSSManagerBase cssManager;

	/**
	 * Creates a new writer for HTML documents.
	 * 
	 * @param file
	 *            the file to write to.
	 * @param cssManager
	 *            the CSS manager used. If this is null, the class attributes
	 *            may be filled with simple strings.
	 */
	public HTMLWriter(File file, CSSManagerBase cssManager) throws IOException {
		this(new PrintStream(file, FileSystemUtils.UTF8_ENCODING), cssManager);
	}

	/**
	 * Creates a new writer for HTML documents.
	 * 
	 * @param stream
	 *            the stream to print to.
	 * @param cssManager
	 *            the CSS manager used. If this is null, the class attributes
	 *            may be filled with simple strings.
	 */
	public HTMLWriter(OutputStream stream, CSSManagerBase cssManager) {
		super(new PrintWriter(wrapStream(stream)), new HTMLResolver());
		this.cssManager = cssManager;
	}

	/**
	 * Helper method for {@link #HTMLWriter(OutputStream, CSSManagerBase)} to
	 * deal with the exception.
	 */
	private static OutputStreamWriter wrapStream(OutputStream stream) {
		try {
			return new OutputStreamWriter(stream, FileSystemUtils.UTF8_ENCODING);
		} catch (UnsupportedEncodingException e) {
			throw new AssertionError("UTF-8 should be supported!");
		}
	}

	/**
	 * Creates a new writer for HTML documents.
	 * 
	 * @param writer
	 *            the writer to print to.
	 */
	public HTMLWriter(PrintWriter writer, CSSManagerBase cssManager) {
		super(writer, new HTMLResolver());
		this.cssManager = cssManager;
	}

	/**
	 * This adds a default header for HTML files consisting of the XML header
	 * and a DOCTYPE of the xhtml frameset DTD.
	 * <p>
	 * XML version is set to "1.0", encoding provided by a parameter, and doc
	 * type definition to XHTML 1.0 Frameset.
	 */
	public void addStdHeader(String encoding) {
		addHeader("1.0", encoding);
		addPublicDocTypeDefintion(EHTMLElement.HTML,
				"-//W3C//DTD XHTML 1.0 Frameset//EN",
				"http://www.w3.org/TR/xhtml1/DTD/xhtml1-frameset.dtd");
	}

	/**
	 * This adds a default header for HTML files consisting of the XML header
	 * and a DOCTYPE of the xhtml frameset DTD.
	 * <p>
	 * XML version is set to "1.0", encoding to "UTF-8", and doc type definition
	 * to XHTML 1.0 Frameset.
	 */
	public void addStdHeader() {
		addStdHeader(FileSystemUtils.UTF8_ENCODING);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * Made this public here.
	 */
	@Override
	public void addRawString(String html) {
		super.addRawString(html);
	}

	/**
	 * Adds a line separator with closing and open tag (see
	 * {@link #addNewLine()}.
	 */
	public void addRawNewLine() {
		addRawString(StringUtils.CR);
	}

	/** Inserts a JavaScript block. */
	public void insertJavaScript(String javaScript) {
		openElement(SCRIPT, TYPE, "text/javascript");
		addRawNewLine();
		addRawString(javaScript);
		addRawNewLine();
		closeElement(SCRIPT);
	}

	/** Inserts a script tag that loads JavaScript from a separate file. */
	public void addExternalJavaScript(String scriptFilePath) {
		// this is required, as some browsers choke on a directly closed
		// script element
		insertEmptyElement(SCRIPT, SRC, scriptFilePath, TYPE, "text/javascript");
	}

	/**
	 * Inserts an empty element but ensures that it is not closed using the
	 * shorthand syntax (e.g. <code>&lt;div /&gt;</code>).
	 * 
	 * This is useful since some browsers choke on some shorthand elements, e.g.
	 * divs or JavaScript tags.
	 * 
	 * @see #openElement(EHTMLElement, Object...)
	 */
	public void insertEmptyElement(EHTMLElement element, Object... attributes) {
		openElement(element, attributes);
		addText(StringUtils.EMPTY_STRING);
		closeElement(element);
	}

	/** Inserts a non-breaking space. */
	public void addNonBreakingSpace() {
		addRawString("&nbsp;");
	}

	/**
	 * Adds an attribute to the currently open element but checks in addition if
	 * the attribute may be added at all.
	 * 
	 * @throws HTMLWriterException
	 *             if the attribute is not allowed for the current element.
	 */
	@Override
	public void addAttribute(EHTMLAttribute attribute, Object value) {
		if (!getCurrentElement().allowsAttribute(attribute)) {
			throw new HTMLWriterException("Attribute " + attribute
					+ " not allowed for element " + getCurrentElement());
		}

		if (attribute == EHTMLAttribute.STYLE) {
			assertCssDeclarationBlock(value);
			value = ((CSSDeclarationBlock) value).toInlineStyle();
		} else if (cssManager != null && attribute == EHTMLAttribute.CLASS) {
			assertCssDeclarationBlock(value);
			value = cssManager.getCSSClassName((CSSDeclarationBlock) value);
		}

		super.addAttribute(attribute, value);
	}

	/**
	 * Asserts that the given value is a {@link CSSDeclarationBlock} and throws
	 * an exception otherwise.
	 */
	private void assertCssDeclarationBlock(Object value) {
		if (!(value instanceof CSSDeclarationBlock)) {
			throw new HTMLWriterException(
					"The argument for STYLE and CLASS attributes must be a "
							+ CSSDeclarationBlock.class.getSimpleName() + "!");
		}
	}

	/** The resolver used for the {@link HTMLWriter}. */
	public static class HTMLResolver implements
			IXMLResolver<EHTMLElement, EHTMLAttribute> {

		/** {@inheritDoc} */
		@Override
		public String resolveAttributeName(EHTMLAttribute attribute) {
			return attribute.toString();
		}

		/** {@inheritDoc} */
		@Override
		public String resolveElementName(EHTMLElement element) {
			return element.toString();
		}

		/** {@inheritDoc} */
		@Override
		public Class<EHTMLAttribute> getAttributeClass() {
			return EHTMLAttribute.class;
		}
	}
}