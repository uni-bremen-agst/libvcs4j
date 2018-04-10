/*-------------------------------------------------------------------------+
|                                                                          |
| Copyright 2005-2011 the ConQAT Project                                   |
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

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Set;

import org.conqat.lib.commons.assertion.CCSMAssert;
import org.conqat.lib.commons.enums.EnumUtils;
import org.conqat.lib.commons.xml.ElementEnumSaxHandler.IElementEnum;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * SAX XML Parser {@link DefaultHandler} implementation that matches XML
 * elements to enumeration values using the enumeration name. The enumeration
 * has to implement the interface {@link IElementEnum} and thus defines a state
 * graph of an XML element sequence for parsing the document.
 * <p>
 * The parser offers registration of {@link ElementHandler}s for each of the
 * enumeration elements that will be called for the start and end of the
 * element. If capturing the inner text of elements is desired a
 * {@link TextElementHandler} has to be registered.
 * <p>
 * Resolving element names to enum constants is performed using a
 * {@link JavaConstantResolver} but can be altered using
 * {@link #setElementResolver(IElementResolver)}.
 *
 * @author $Author: kinnen $
 * @version $Rev: 51722 $
 * @ConQAT.Rating RED Hash: 2B29E9702AD1F6C1E88D9BFFA2B103AD
 */
// TODO (FS) as discussed about the enums: we want parent-child relationship and
// we can
// simplify the enums by using constructors. also: throw errors when the XML
// does not fit the state graph
public class ElementEnumSaxHandler<ELEMENT extends Enum<ELEMENT> & IElementEnum<ELEMENT>>
		extends DefaultHandler {

	/** The currently parsed element, i.e. the parser state. */
	// TODO (FS) not exactly true. it's not always the currently parsed element,
	// especially when hasReachedStartElement = false
	private ELEMENT element;

	/** Flag that indicates if the start element has been reached yet. */
	private boolean hasReachedStartElement = false;

	/**
	 * The map of registered handler callbacks.
	 */
	// TODO (FS) please use Map here, not HashMap
	private final HashMap<ELEMENT, ElementHandler<ELEMENT>> handlers = new HashMap<>();

	/**
	 * Stack of opened (and handled) elements. Will be popped from the stack on
	 * close.
	 */
	// TODO (FS) please use Stack, not ArrayDeque. It is confusing
	// TODO (FS) you don't need this stack. please remove (see #endElement() for
	// an explanation)
	private final Deque<ELEMENT> openedElements = new ArrayDeque<>();

	/**
	 * Stack of opened elements a {@link TextElementHandler} is registered for.
	 * Text is always captured for the top element of the stack.
	 */
	// TODO (FS) please use Stack, not ArrayDeque. It is confusing
	private final Deque<StringBuffer> textBuffers = new ArrayDeque<>();

	/**
	 * Resolver from XML element names to element enum names. <code>null</code>
	 * if no resolver is used. The default resolver is a
	 * {@link JavaConstantResolver}.
	 */
	private IElementResolver resolver = new JavaConstantResolver();

	/** Constructor. */
	public ElementEnumSaxHandler(ELEMENT initialElement) {
		this.element = initialElement;
	}

	/** Sets (or overrides) the element handler for a given element. */
	public void setElementHandler(ELEMENT element,
			ElementHandler<ELEMENT> handler) {
		handlers.put(element, handler);
	}

	// TODO (FS) I don't think this will ever be necessary. Either a parser
	// handles an element or not. please consider
	// removing
	/** Removes the element handler for a given element. */
	public void removeElementHandler(ELEMENT element) {
		handlers.remove(element);
	}

	/** @see #resolver */
	public void setElementResolver(IElementResolver resolver) {
		this.resolver = resolver;
	}

	/** {@inheritDoc} */
	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {

		ELEMENT nextElement = getEnumForElement(localName);
		if (nextElement == null) {
			return;
		}

		if (!hasReachedStartElement) {
			if (element != nextElement) {
				return;
			}

			hasReachedStartElement = true;

		} else if (!element.nextElements().contains(nextElement)) {
			return;
		}

		element = nextElement;
		openedElements.push(element);

		// TODO (FS) move inside if. getting it and then checking if the
		// collection contains it is a weird order of operations^^
		ElementHandler<ELEMENT> handler = handlers.get(element);
		if (handlers.containsKey(element)) {
			handler.onStartElement(element, attributes);

			if (handler instanceof TextElementHandler) {
				textBuffers.push(new StringBuffer());
			}
		}
	}

	/** {@inheritDoc} */
	@Override
	public void characters(char[] ch, int start, int length) {
		// TODO (FS) if I have XML like this:
		// <a>foo<b>bar</b></a> and the handler for <a> wants to receive onText,
		// I think it will receive "foobar" instead of "foo" as I would have
		// expected, because this method will be called for <b> and the text is
		// then appended to the topmost buffer, i.e. a
		// TODO (FS) I would also be happy with an implementation that does not
		// buffer the text but simply forwards the characters() call to onText()
		// and lets the handler itself concatenate multiple calls in a buffer,
		// if that can happen in the respective XML format
		StringBuffer buffer = textBuffers.peek();
		if (buffer != null) {
			buffer.append(ch, start, length);
		}
	}

	/** {@inheritDoc} */
	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		ELEMENT endedElement = getEnumForElement(localName);

		if (endedElement == null || endedElement != openedElements.peek()) {
			return;
		}
		openedElements.pop();

		ElementHandler<ELEMENT> handler = handlers.get(endedElement);
		if (handler == null) {
			return;
		}

		handler.onEndElement(endedElement);

		if (handler instanceof TextElementHandler) {
			TextElementHandler<ELEMENT> textHandler = (TextElementHandler<ELEMENT>) handler;

			StringBuffer buffer = textBuffers.pop();
			textHandler.onText(endedElement, buffer.toString());
		}
	}

	/**
	 * Returns the enum value for a given element name or <code>null</code> if
	 * no element with this name exists. Respects normalization rules provided
	 * by {@link #resolver}.
	 */
	@SuppressWarnings("unchecked")
	// TODO (FS) please document the error
	private ELEMENT getEnumForElement(String elementName) throws AssertionError {
		if (resolver != null) {
			elementName = resolver.resolve(elementName);
		}

		Class<ELEMENT> enumClass = null;
		if (element.getClass().isEnum()) {
			// Enum is a top-level enumeration
			enumClass = (Class<ELEMENT>) element.getClass();
		} else if (element.getClass().getSuperclass().isEnum()) {
			// Enum is a nested in a class
			// TODO (FS) "nested in a class" is not accurate. that sounds like
			// an enum that's declared inside a class. it's an enum
			// _constant_ that overrides methods defined in the enum class
			enumClass = (Class<ELEMENT>) element.getClass().getSuperclass();
		} else {
			// TODO (FS) can never happen since we require that ELEMENT extends
			// Enum<ELEMENT>. you can safely remove this case and make the
			// else-if an else. plus: don't use Assert, use CCSMAssert
			CCSMAssert.fail(element.name()
					+ " seems not to be a valid enumeration.");
		}

		return EnumUtils.valueOf(enumClass, elementName);
	}

	/**
	 * Interface that defines methods for getting the next elements for parsing
	 * the document.
	 */
	public static interface IElementEnum<E extends Enum<E>> {
		/** Set of elements that are expected to be parsed after this element. */
		public Set<E> nextElements();
	}

	/** Handler for start and element callbacks. */
	@SuppressWarnings("unused")
	public static class ElementHandler<ELEMENT> {

		/**
		 * Being called each time the handled element is opened.
		 */
		public void onStartElement(ELEMENT element, Attributes attributes)
				throws SAXException {
			// stub
		}

		/**
		 * Being called each time the handled element is closed.
		 */
		public void onEndElement(ELEMENT element) throws SAXException {
			// stub
		}
	}

	/**
	 * Handler for text callbacks in addition to the callbacks defined in
	 * {@link org.conqat.lib.commons.xml.ElementEnumSaxHandler.ElementHandler}.
	 * <p>
	 * As capturing text may reduce performance, please consider using
	 * {@link org.conqat.lib.commons.xml.ElementEnumSaxHandler.ElementHandler}
	 * if not interested in text.
	 */
	@SuppressWarnings("unused")
	public static class TextElementHandler<ELEMENT> extends
			ElementHandler<ELEMENT> {

		/**
		 * Will be called exactly once for an element with all the text that has
		 * occurred in the XML between the start element and end element
		 * callback. Text of descendant elements is also captured unless a
		 * {@link org.conqat.lib.commons.xml.ElementEnumSaxHandler.TextElementHandler}
		 * for these elements is registered.
		 */
		// TODO (FS) I find this behaviour a bit strange. I think there might be
		// parser that don't want this. It especially forces you to have an enum
		// constant for the child elements, even if you're not at all interested
		// in them, just to get rid of their text content. I think it would be
		// better if we did not buffer the text but let the ElementHandlers do
		// that themselves if they require it. this way, you could also merge
		// this handler with the ElementHandler
		public void onText(ELEMENT element, String text) throws SAXException {
			// stub
		}
	}

	/** Resolver from XML element names to enumeration names. */
	public static interface IElementResolver {
		/**
		 * Takes the name of an XML element and resolves it to the name of a
		 * possible enumeration value.
		 */
		public String resolve(String elementName);
	}

	/**
	 * Resolver that normalizes XML element names to valid Java identifiers.
	 *
	 * @see #toIdentifier(String)
	 */
	public static class JavaConstantResolver implements IElementResolver {

		/** {@inheritDoc} */
		@Override
		public String resolve(String elementName) {
			return toIdentifier(elementName);
		}

		/**
		 * Converts the given string to a valid Java identifier by replacing all
		 * non word characters by underscores and converting the string to
		 * uppercase.
		 */
		// TODO (FS) why is this a separate method? please inline and merge the
		// comments
		// TODO (FS) please change the comment to read
		// "consecutive non word characters". currently it reads like "foo--bar"
		// would be replaced by "foo__bar". alternatively: provide an example
		private String toIdentifier(String elementName) {
			return elementName.toUpperCase().replaceAll("\\W+", "_");
		}

	}
}