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

import static org.conqat.lib.commons.string.StringUtils.CR;
import static org.conqat.lib.commons.string.StringUtils.SPACE;

import java.io.Closeable;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.EmptyStackException;
import java.util.HashSet;
import java.util.Stack;

import org.conqat.lib.commons.filesystem.FileSystemUtils;
import org.conqat.lib.commons.string.StringUtils;

/**
 * Utility class for creating XML documents. Please consult test case
 * {@link XMLWriterTest} to see how this class is intended to be used.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 51018 $
 * @ConQAT.Rating GREEN Hash: C5F1AB663987FC9EEC292D53E218B9CE
 */
public class XMLWriter<E extends Enum<E>, A extends Enum<A>> implements
		Closeable {

	/** This enunmeration describes the states of the writer. */
	private enum EState {

		/** Indicates that we are at the beginning of the document. */
		DOCUMENT_START,

		/** Started a tag but did not close it yet. */
		INSIDE_TAG,

		/** Inside a text element. */
		INSIDE_TEXT,

		/** Indicates that we are between to tags but not within a text element. */
		OUTSIDE_TAG
	}

	/** XML comment end symbol. */
	private final static String COMMENT_END = " -->";

	/** XML comment start symbol. */
	private final static String COMMENT_START = "<!-- ";

	/** Right angle bracket */
	private final static String GT = ">";

	/** Left angle bracket */
	private final static String LT = "<";

	/** Resolver used by the writer. */
	protected final IXMLResolver<E, A> xmlResolver;

	/**
	 * This set maintains the attributes added to an element to detect duplicate
	 * attributes.
	 * <p>
	 * We can not use {@link java.util.EnumSet} here as we would need a
	 * reference to the defining class.
	 */
	private final HashSet<A> currentAttributes = new HashSet<A>();

	/** The current nesting depth is used to calculate the ident. */
	private int currentNestingDepth = -1;

	/**
	 * This stack maintains the elements in order of creation. It is used to
	 * check if elements are closed in the correct order.
	 */
	private final Stack<E> elementStack = new Stack<E>();

	/** The current state of the writer. */
	private EState state = EState.DOCUMENT_START;

	/** The writer to write to. */
	private final PrintWriter writer;

	/** This flag indicates if line breaks should be generated or not. */
	private boolean suppressLineBreaks = false;

	/**
	 * Create a new writer.
	 * 
	 * @param stream
	 *            the stream to write to.
	 * @param xmlResolver
	 *            resolvers used by this writer
	 */
	public XMLWriter(OutputStream stream, IXMLResolver<E, A> xmlResolver) {
		try {
			this.writer = new PrintWriter(new OutputStreamWriter(stream,
					FileSystemUtils.UTF8_ENCODING));
		} catch (UnsupportedEncodingException e) {
			throw new AssertionError("UTF-8 should always be supported!");
		}
		this.xmlResolver = xmlResolver;
	}

	/**
	 * Create a new writer.
	 * 
	 * @param writer
	 *            the writer to write to.
	 * @param xmlResolver
	 *            resolvers used by this writer
	 */
	public XMLWriter(PrintWriter writer, IXMLResolver<E, A> xmlResolver) {
		this.writer = writer;
		this.xmlResolver = xmlResolver;
	}

	/**
	 * Toogle line break behavior. If set to <code>true</code> the writer does
	 * not write line breaks. If set to <code>false</code> (default) line breaks
	 * are written.
	 * <p>
	 * This can, for example, be used for HTML where line breaks sometimes
	 * change the layout.
	 */
	public void setSuppressLineBreaks(boolean supressLineBreaks) {
		this.suppressLineBreaks = supressLineBreaks;
	}

	/**
	 * Add an XML header.
	 * 
	 * @param version
	 *            version string
	 * @param encoding
	 *            encoding definition
	 */
	public void addHeader(String version, String encoding) {
		if (state != EState.DOCUMENT_START) {
			throw new XMLWriterException(
					"Can be called at the beginning of a document only.",
					EXMLWriterExceptionType.HEADER_WITHIN_DOCUMENT);
		}
		print(LT);
		print("?xml version=\"");
		print(version);
		print("\" encoding=\"");
		print(encoding);
		print("\"?");
		print(GT);

		state = EState.OUTSIDE_TAG;
	}

	/**
	 * Add public document type definiton
	 * 
	 * @param rootElement
	 *            root element
	 * @param publicId
	 *            public id
	 * @param systemId
	 *            sytem id
	 */
	public void addPublicDocTypeDefintion(E rootElement, String publicId,
			String systemId) {
		print(LT);
		print("!DOCTYPE ");
		print(xmlResolver.resolveElementName(rootElement));
		print(" PUBLIC \"");
		print(publicId);
		print("\" \"");
		print(systemId);
		print("\"");
		print(GT);

		state = EState.OUTSIDE_TAG;
	}

	/**
	 * Add the HTML5 doctype, which is different from normal XML/HTML doctypes
	 * in that it is much shorter and does not contain any public or system IDs.
	 */
	public void addHTML5Doctype() {
		print(LT);
		print("!DOCTYPE html");
		print(GT);

		state = EState.OUTSIDE_TAG;
	}

	/**
	 * Start a new element
	 * 
	 * @param element
	 *            the element to start.
	 */
	public void openElement(E element) {
		if (state == EState.INSIDE_TAG) {
			println(GT);
		} else if (state == EState.OUTSIDE_TAG) {
			println();
		}

		currentNestingDepth++;
		if (state != EState.INSIDE_TEXT) {
			printIndent();
		}
		print(LT);
		print(xmlResolver.resolveElementName(element));

		state = EState.INSIDE_TAG;
		elementStack.push(element);
		currentAttributes.clear();
	}

	/**
	 * Add a attribute. This only works if a element was started but no other
	 * elements were added yet.
	 * 
	 * @param attribute
	 *            the attribute to create
	 * @param value
	 *            its value
	 * @throws XMLWriterException
	 *             if there's no element to add attributes to (
	 *             {@link EXMLWriterExceptionType#ATTRIBUTE_OUTSIDE_ELEMENT}) or
	 *             if an attribute is added twice (
	 *             {@link EXMLWriterExceptionType#DUPLICATE_ATTRIBUTE}).
	 */
	public void addAttribute(A attribute, Object value) {
		if (currentAttributes.contains(attribute)) {
			throw new XMLWriterException("Duplicate attribute.",
					EXMLWriterExceptionType.DUPLICATE_ATTRIBUTE);
		}

		addExternalAttribute(xmlResolver.resolveAttributeName(attribute), value);
		currentAttributes.add(attribute);
	}

	/**
	 * Adds an external attribute, i.e. an attribute that was not defined in the
	 * attribute enumeration.
	 */
	public void addExternalAttribute(String attributeName, Object value) {
		if (state != EState.INSIDE_TAG) {
			throw new XMLWriterException("Must be called for an open element.",
					EXMLWriterExceptionType.ATTRIBUTE_OUTSIDE_ELEMENT);
		}

		print(SPACE);
		print(attributeName);
		print("=");
		print("\"");
		print(escape(value.toString()));
		print("\"");
	}

	/**
	 * Convenience method for adding an element together with (some of) its
	 * attributes.
	 * 
	 * @param element
	 *            The element to be opened (using {@link #openElement(Enum)}).
	 * @param attributes
	 *            the attributes to be added. The number of arguments must be
	 *            even, where the first, third, etc. argument is an attribute
	 *            enum.
	 */
	public void openElement(E element, Object... attributes) {
		if (attributes.length % 2 != 0) {
			throw new XMLWriterException(
					"Expected an even number of arguments!",
					EXMLWriterExceptionType.ODD_NUMBER_OF_ARGUMENTS);
		}
		for (int i = 0; i < attributes.length; i += 2) {
			if (!xmlResolver.getAttributeClass().isAssignableFrom(
					attributes[i].getClass())) {
				throw new XMLWriterException("Attribute name (index " + i
						+ ") must be of type "
						+ xmlResolver.getAttributeClass().getName(),
						EXMLWriterExceptionType.ILLEGAL_ATTRIBUTE_TYPE);
			}
		}
		openElement(element);
		for (int i = 0; i < attributes.length; i += 2) {
			// this is ok as we checked it above
			@SuppressWarnings("unchecked")
			A a = (A) attributes[i];
			addAttribute(a, attributes[i + 1]);
		}
	}

	/**
	 * Convenience method for adding an element together with (some of) its
	 * attributes. This is the same as {@link #openElement(Enum, Object[])}, but
	 * also closes the element.
	 */
	public void addClosedElement(E element, Object... attributes) {
		openElement(element, attributes);
		closeElement(element);
	}

	/**
	 * Convenience method for adding an element together with (some of) its
	 * attributes and text inbetween. This is the same as
	 * {@link #openElement(Enum, Object[])}, but then adds the provided text and
	 * closes the element.
	 */
	public void addClosedTextElement(E element, String text,
			Object... attributes) {
		openElement(element, attributes);
		addText(text);
		closeElement(element);
	}

	/**
	 * Close an element.
	 * 
	 * @param element
	 *            the element to close.
	 * @throws XMLWriterException
	 *             on attempt to close the wrong element (
	 *             {@link EXMLWriterExceptionType#UNCLOSED_ELEMENT}).
	 */
	public void closeElement(E element) {
		if (element != elementStack.peek()) {
			throw new XMLWriterException("Must close element "
					+ elementStack.peek() + " first.",
					EXMLWriterExceptionType.UNCLOSED_ELEMENT);
		}

		if (state == EState.INSIDE_TAG) {
			// if inside a tag, just close the tag and done
			print(" /");
			print(GT);

		} else {
			// we're not inside a tag

			if (state != EState.INSIDE_TEXT) {
				// if not inside a text element, create new line and indent
				println();
				printIndent();
			}

			// create closing tag
			print(LT);
			print("/");
			print(xmlResolver.resolveElementName(element));
			print(GT);
		}

		// we're done with this element
		elementStack.pop();
		currentNestingDepth--;
		state = EState.OUTSIDE_TAG;
	}

	/**
	 * Add a text element to an element.
	 * 
	 * @param text
	 *            the text to add.
	 */
	public void addText(String text) {
		if (state == EState.INSIDE_TAG) {
			print(GT);
		}
		print(escape(text));

		state = EState.INSIDE_TEXT;
	}

	/**
	 * Add CDATA section. Added text is not escaped.
	 * 
	 * @throws XMLWriterException
	 *             If the added text contains the CDATA closing tag
	 *             <code>]]></code>. This is not automatically escaped as some
	 *             parsers do not automatically unescape it when reading.
	 */
	public void addCDataSection(String cdata) {
		if (state == EState.INSIDE_TAG) {
			print(GT);
		}

		if (cdata.contains("]]>")) {
			throw new XMLWriterException("CDATA contains ']]>'",
					EXMLWriterExceptionType.CDATA_CONTAINS_CDATA_CLOSING_TAG);
		}

		print("<![CDATA[");
		print(cdata);
		print("]]>");
		state = EState.INSIDE_TEXT;
	}

	/**
	 * Add an XML comment.
	 * 
	 * @param text
	 *            comment text.
	 */
	public void addComment(String text) {
		ensureOutsideTag();

		currentNestingDepth++;
		printIndent();
		print(COMMENT_START);
		print(escape(text));
		print(COMMENT_END);
		currentNestingDepth--;

		state = EState.OUTSIDE_TAG;
	}

	/** Add new line. */
	public void addNewLine() {
		ensureOutsideTag();
	}

	/**
	 * Close the writer.
	 * 
	 * @throws XMLWriterException
	 *             if there is a remaining open element.
	 */
	@Override
	public void close() {
		if (!elementStack.isEmpty()) {
			throw new XMLWriterException("Need to close element <"
					+ xmlResolver.resolveElementName(elementStack.peek())
					+ "> before closing writer.",
					EXMLWriterExceptionType.UNCLOSED_ELEMENT);
		}
		writer.close();
	}

	/** Flushes the underlying writer. */
	public void flush() {
		writer.flush();
	}

	/**
	 * Adds the given text unprocessed to the writer. This is useful for adding
	 * chunks of generated XML to avoid having the brackets escaped.
	 */
	protected void addRawString(String text) {
		if (state == EState.INSIDE_TAG) {
			print(GT);
		}
		print(text);
		state = EState.INSIDE_TEXT;
	}

	/** Get writer this writer writes to. */
	protected PrintWriter getWriter() {
		return writer;
	}

	/**
	 * Returns the element we are currently in.
	 * 
	 * @throws EmptyStackException
	 *             if there is no unclosed element.
	 */
	protected E getCurrentElement() {
		return elementStack.peek();
	}

	/** Make sure the current tag is closed. */
	private void ensureOutsideTag() {
		if (state == EState.INSIDE_TAG) {
			println(GT);
			state = EState.OUTSIDE_TAG;
		} else if (state == EState.OUTSIDE_TAG) {
			println();
		}
	}

	/**
	 * Escape text for XML. Creates empty string for <code>null</code> value.
	 */
	public static String escape(String text) {
		if (text == null) {
			return StringUtils.EMPTY_STRING;
		}

		text = text.replaceAll("&", "&amp;");
		text = text.replaceAll(LT, "&lt;");
		text = text.replaceAll(GT, "&gt;");
		text = text.replaceAll("\"", "&quot;");

		// normalize line breaks
		text = StringUtils.replaceLineBreaks(text, CR);
		return text;
	}

	/** Write to writer. */
	private void print(String message) {
		writer.print(message);
	}

	/** Write indent to writer. */
	private void printIndent() {
		if (!suppressLineBreaks) {
			writer.print(StringUtils.fillString(currentNestingDepth * 2,
					StringUtils.SPACE_CHAR));
		}
	}

	/** Write to writer. */
	private void println() {
		if (!suppressLineBreaks) {
			print(CR);
		}
	}

	/** Write to writer. */
	private void println(String text) {
		print(text);
		println();
	}
}