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

import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.conqat.lib.commons.assertion.CCSMAssert;
import org.conqat.lib.commons.collections.BidirectionalMap;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Evaluator for XPath expression. This is preferable to using the normal
 * {@link XPath} class as it has built-in support for namespace-handling and the
 * evaluation-method does not define exceptions.
 * 
 * @author $Author:deissenb $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: E1F478AC5F0C3155279D8BB50354C75A
 */
public class XPathEvaluator {

	/** XPath object used to evaluate Bugzilla result document.s */
	private final XPath xPath = XPathFactory.newInstance().newXPath();

	/** The namespace context to use. */
	private final NSContext nsContext;

	/** Create new evaluator. */
	public XPathEvaluator() {
		nsContext = new NSContext();
		xPath.setNamespaceContext(nsContext);
	}

	/** Add a namespace. */
	public void addNamespace(String prefix, String uri) {
		nsContext.addNamespace(prefix, uri);
	}

	/**
	 * Evaluates an XPath expression on context element. This assumes that the
	 * XPath expression is valid and raises an {@link AssertionError} otherwise.
	 * 
	 * @param returnType
	 *            use {@link XPathConstants} to define return type.
	 */
	public Object select(String expr, Element context, QName returnType) {
		try {
			return selectUnsafe(expr, context, returnType);
		} catch (XPathExpressionException e) {
			CCSMAssert.fail(e.getMessage());
			return null;
		}
	}

	/**
	 * Evaluates an XPath expression on context element.
	 * 
	 * @param returnType
	 *            use {@link XPathConstants} to define return type.
	 * @throws XPathExpressionException
	 *             if the XPath expression is invalid.
	 */
	public Object selectUnsafe(String expr, Element context, QName returnType)
			throws XPathExpressionException {
		return xPath.evaluate(expr, context, returnType);
	}

	/**
	 * Evaluates an XPath expression on context element. This assumes that the
	 * XPath expression is valid and raises an {@link AssertionError} otherwise.
	 */
	public List<Element> selectList(String expr, Element context) {
		return XMLUtils.elementNodes(selectNodeList(expr, context));
	}

	/**
	 * Evaluates an XPath expression on context element. This assumes that the
	 * XPath expression is valid and raises an {@link AssertionError} otherwise.
	 */
	public NodeList selectNodeList(String expr, Element context) {
		return (NodeList) select(expr, context, XPathConstants.NODESET);
	}

	/**
	 * Evaluates an XPath expression on context element. This assumes that the
	 * XPath expression is valid and raises an {@link AssertionError} otherwise.
	 */
	public Element selectElement(String expr, Element context) {
		return (Element) select(expr, context, XPathConstants.NODE);
	}

	/**
	 * Evaluates an XPath expression on context element. This assumes that the
	 * XPath expression is valid and raises an {@link AssertionError} otherwise.
	 */
	public String selectString(String expr, Element context) {
		return (String) select(expr, context, XPathConstants.STRING);
	}

	/**
	 * Evaluates an XPath expression on context element. This assumes that the
	 * XPath expression is valid and raises an {@link AssertionError} otherwise.
	 * Due to the implementation of {@link XPath} this returns 0.0 if the
	 * element was not found.
	 */
	public double selectDouble(String expr, Element context) {
		return (Double) select(expr, context, XPathConstants.NUMBER);
	}

	/**
	 * Evaluates an XPath expression on context element. This assumes that the
	 * XPath expression is valid and raises an {@link AssertionError} otherwise.
	 * Due to the implementation of {@link XPath} this returns 0 if the element
	 * was not found.
	 */
	public int selectInt(String expr, Element context) {
		return ((Double) select(expr, context, XPathConstants.NUMBER))
				.intValue();
	}

	/**
	 * Evaluates an XPath expression on context element. This assumes that the
	 * XPath expression is valid and raises an {@link AssertionError} otherwise.
	 * Due to the implementation of {@link XPath} this returns
	 * <code>false</code> if the element was not found.
	 */
	public boolean selectBoolean(String expr, Element context) {
		return (Boolean) select(expr, context, XPathConstants.BOOLEAN);
	}

	/** Simple namespace context. */
	private static class NSContext implements NamespaceContext {

		/** Maps from prefix (first) to namespace URI (second). */
		private final BidirectionalMap<String, String> map = new BidirectionalMap<String, String>();

		/** Add new namespace to both maps. */
		private void addNamespace(String prefix, String uri) {
			map.put(prefix, uri);
		}

		/** {@inheritDoc} */
		@Override
		public String getNamespaceURI(String prefix) {
			return map.getSecond(prefix);
		}

		/** {@inheritDoc} */
		@Override
		public String getPrefix(String namespaceURI) {
			return map.getFirst(namespaceURI);
		}

		/** {@inheritDoc} */
		@Override
		@SuppressWarnings("rawtypes")
		public Iterator getPrefixes(String namespaceURI) {
			return map.getFirstSet().iterator();
		}
	}
}