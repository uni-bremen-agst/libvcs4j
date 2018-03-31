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
package org.conqat.lib.commons.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.conqat.lib.commons.assertion.CCSMPre;
import org.conqat.lib.commons.assertion.PreconditionException;
import org.conqat.lib.commons.enums.EnumUtils;
import org.conqat.lib.commons.filesystem.FileSystemUtils;
import org.conqat.lib.commons.string.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Utility class for reading XML documents. The XML document can be validated by
 * an optional schema that is passed via {@link #setSchema(URL)}. Please consult
 * test case {@link XMLReaderTest} to see how this class is intended to be used.
 * 
 * @author $Author: goeb $
 * @version $Rev: 51157 $
 * @ConQAT.Rating GREEN Hash: D345C8E3210D00F6B93FD20D5415BDE3
 */
public abstract class XMLReader<E extends Enum<E>, A extends Enum<A>, X extends Exception> {

	/** The current DOM element. */
	private Element currentDOMElement;

	/** The schema URL to use or <code>null</code> if no schema is used. */
	private URL schemaURL;

	/** Resolver used by the writer. */
	private final IXMLResolver<E, A> xmlResolver;

	/** Reader that accesses content. */
	private final Reader reader;

	/**
	 * Create new reader.
	 * 
	 * @param file
	 *            the file to be read
	 * @param xmlResolver
	 *            resolvers used by this reader
	 */
	public XMLReader(File file, IXMLResolver<E, A> xmlResolver)
			throws IOException {
		this(file, null, xmlResolver);
	}

	/**
	 * Create reader.
	 * 
	 * @param file
	 *            the file to be read
	 * @param encoding
	 *            XML encoding of the file. No encoding is set if
	 *            <code>null</code>.
	 * @param xmlResolver
	 *            resolvers used by this reader
	 */
	public XMLReader(File file, String encoding, IXMLResolver<E, A> xmlResolver)
			throws IOException {
		this(FileSystemUtils.streamReader(new FileInputStream(file),
				ensureNotNullEncoding(encoding)), xmlResolver);
	}

	/**
	 * Create reader.
	 * 
	 * @param content
	 *            XML string that gets parsed validation will be performed if
	 *            <code>null</code>.
	 * @param xmlResolver
	 *            resolvers used by this reader
	 */
	public XMLReader(String content, IXMLResolver<E, A> xmlResolver) {
		this(new StringReader(content), xmlResolver);
	}

	/**
	 * Create reader.
	 * 
	 * @param reader
	 *            the reader used to access the XML document.
	 * @param xmlResolver
	 *            resolvers used by this reader
	 */
	public XMLReader(Reader reader, IXMLResolver<E, A> xmlResolver) {
		CCSMPre.isFalse(reader == null, "Reader may not be null.");
		CCSMPre.isFalse(xmlResolver == null, "XML resolver may not be null.");
		this.reader = reader;
		this.xmlResolver = xmlResolver;
	}

	/** Replaces a null value with the name of the default encoding. */
	private static String ensureNotNullEncoding(String encoding) {
		if (encoding == null) {
			return Charset.defaultCharset().name();
		}
		return encoding;
	}

	/** Sets the URL pointing to the schema that is used for validation. */
	protected void setSchema(URL schemaURL) {
		this.schemaURL = schemaURL;
	}

	/**
	 * Get <code>boolean</code> value of an attribute.
	 * 
	 * @return the boolean value, semantics for non-translatable or empty values
	 *         is defined by {@link Boolean#parseBoolean(String)}.
	 */
	protected boolean getBooleanAttribute(A attribute) {
		String value = getStringAttribute(attribute);
		return Boolean.parseBoolean(value);
	}

	/**
	 * Get the text content of a child element of the current element.
	 * 
	 * @param childElement
	 *            the child element
	 * @return the text or <code>null</code> if the current element doesn't have
	 *         the requested child element
	 */
	protected String getChildText(E childElement) {

		String elementName = xmlResolver.resolveElementName(childElement);
		Element domElement = XMLUtils.getNamedChild(currentDOMElement,
				elementName);
		if (domElement == null) {
			return null;
		}

		return domElement.getTextContent();
	}

	/**
	 * Translate attribute value to an enumeration element.
	 * 
	 * @param attribute
	 *            the attribute
	 * @param enumClass
	 *            the enumeration class
	 * 
	 * @return the enum value, semantics for non-translatable or empty values is
	 *         defined by {@link Enum#valueOf(Class, String)}.
	 */
	protected <T extends Enum<T>> T getEnumAttribute(A attribute,
			Class<T> enumClass) {
		String value = getStringAttribute(attribute);
		return Enum.valueOf(enumClass, value);
	}

	/**
	 * Translate attribute value to an enumeration element.
	 * 
	 * @param attribute
	 *            the attribute
	 * @param enumClass
	 *            the enumeration class
	 * @param defaultValue
	 *            the default value to return in case the attribute is not
	 *            specified or the enumeration does not contain the specified
	 *            value.
	 * 
	 * @return The enum value, semantics for non-translatable or empty values is
	 *         defined by {@link EnumUtils#valueOfIgnoreCase(Class, String)}.
	 */
	protected <T extends Enum<T>> T getEnumAttributeIgnoreCase(A attribute,
			Class<T> enumClass, T defaultValue) {
		String value = getStringAttribute(attribute);
		if (StringUtils.isEmpty(value)) {
			return defaultValue;
		}
		T result = EnumUtils.valueOfIgnoreCase(enumClass, value);
		if (result == null) {
			return defaultValue;
		}
		return result;
	}

	/**
	 * Get <code>int</code> value of an attribute.
	 * 
	 * @return the int value, semantics for non-translatable or empty values is
	 *         defined by {@link Integer#parseInt(String)}.
	 */
	protected int getIntAttribute(A attribute) {
		String value = getStringAttribute(attribute);
		return Integer.parseInt(value);
	}

	/**
	 * Get <code>long</code> value of an attribute.
	 * 
	 * @return the long value, semantics for non-translatable or empty values is
	 *         defined by {@link Long#parseLong(String)}.
	 */
	protected long getLongAttribute(A attribute) {
		String value = getStringAttribute(attribute);
		return Long.parseLong(value);
	}

	/**
	 * Get attribute value.
	 * 
	 * 
	 * @return the attribute value or the empty string if attribute is
	 *         undefined.
	 */
	protected String getStringAttribute(A attribute) {
		return currentDOMElement.getAttribute(xmlResolver
				.resolveAttributeName(attribute));
	}

	/** Returns true if the current element has a given attribute. */
	protected boolean hasAttribute(A attribute) {
		return currentDOMElement.hasAttribute(xmlResolver
				.resolveAttributeName(attribute));
	}

	/**
	 * Get text content of current node.
	 */
	protected String getText() {
		return currentDOMElement.getTextContent();
	}

	/**
	 * Parse file. This sets the current element focus to the document root
	 * element. If schema URL was set the document is validated against the
	 * schema.
	 * <p>
	 * Sub classes should typically wrap this method with a proper error
	 * handling mechanism.
	 * 
	 * @throws SAXException
	 *             if a parsing exceptions occurs
	 * @throws IOException
	 *             if an IO exception occurs.
	 */
	protected void parseFile() throws SAXException, IOException {
		try {
			InputSource input = new InputSource(reader);
			Document document;
			if (schemaURL == null) {
				document = XMLUtils.parse(input);
			} else {
				document = XMLUtils.parse(input, schemaURL);
			}
			currentDOMElement = document.getDocumentElement();
		} finally {
			reader.close();
		}
	}

	/**
	 * Process the child elements of the current element with a given processor.
	 * Target elements are specified by
	 * {@link IXMLElementProcessor#getTargetElement()}.
	 * 
	 * @param processor
	 *            the processor used to process the elements
	 * @throws X
	 *             if the processor throws an exception
	 */
	protected void processChildElements(IXMLElementProcessor<E, X> processor)
			throws X {
		String targetElementName = xmlResolver.resolveElementName(processor
				.getTargetElement());
		processElementList(processor,
				XMLUtils.getNamedChildren(currentDOMElement, targetElementName));
	}

	/**
	 * Process all descendant elements of the current element with a given
	 * processor. In contrast to
	 * {@link #processChildElements(IXMLElementProcessor)}, not only direct
	 * child elements are processed. Descendant elements are processed in the
	 * sequence they are found during a top-down, left-right traversal of the
	 * XML document.
	 * <p>
	 * Target elements are specified by
	 * {@link IXMLElementProcessor#getTargetElement()}.
	 * 
	 * @param processor
	 *            the processor used to process the elements
	 * @throws X
	 *             if the processor throws an exception
	 */
	protected void processDecendantElements(IXMLElementProcessor<E, X> processor)
			throws X {
		String targetElementName = xmlResolver.resolveElementName(processor
				.getTargetElement());

		NodeList descendantNodes = currentDOMElement
				.getElementsByTagName(targetElementName);

		processElementList(processor, XMLUtils.elementNodes(descendantNodes));
	}

	/**
	 * Processes the elements in the list with the given processor
	 * 
	 * @param processor
	 *            the processor used to process the elements
	 * @param elements
	 *            list of elements that get processed
	 * @throws X
	 *             if the processor throws an exception
	 */
	private void processElementList(IXMLElementProcessor<E, X> processor,
			List<Element> elements) throws X {
		Element oldElement = currentDOMElement;

		for (Element child : elements) {
			currentDOMElement = child;
			processor.process();
		}

		currentDOMElement = oldElement;
	}

	/**
	 * This works similar to the template mechanism known from XSLT. It
	 * traverses the DOM tree starting from the current DOM element in
	 * depth-first fashion. For each element it checks if one of the provided
	 * processors has the current element as target element. If a matching
	 * processor is found, it is executed.
	 * 
	 * @throws PreconditionException
	 *             if multiple processors apply to the same target element
	 */
	@SuppressWarnings("unchecked")
	protected void apply(IXMLElementProcessor<E, X>... processors) throws X {
		Map<String, IXMLElementProcessor<E, X>> processorMap = new HashMap<String, IXMLElementProcessor<E, X>>();

		for (IXMLElementProcessor<E, X> processor : processors) {
			String targetElementName = xmlResolver.resolveElementName(processor
					.getTargetElement());
			CCSMPre.isFalse(processorMap.containsKey(targetElementName),
					"Multiple processors found for element: "
							+ targetElementName);
			processorMap.put(targetElementName, processor);
		}

		Element oldElement = currentDOMElement;
		traverse(processorMap);
		currentDOMElement = oldElement;
	}

	/**
	 * Traverse element tree in depth-first fashion and execute the processors
	 * provided by the processor map.
	 */
	private void traverse(Map<String, IXMLElementProcessor<E, X>> processorMap)
			throws X {
		IXMLElementProcessor<E, X> processor = processorMap
				.get(currentDOMElement.getTagName());
		if (processor != null) {
			processor.process();
		}

		NodeList nodeList = currentDOMElement.getChildNodes();

		for (Element element : XMLUtils.elementNodes(nodeList)) {
			currentDOMElement = element;
			traverse(processorMap);
		}
	}

}