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
package org.conqat.lib.commons.reflect;

import static org.conqat.lib.commons.string.StringUtils.CR;

import java.util.ArrayList;
import java.util.List;

import org.conqat.lib.commons.assertion.CCSMPre;
import org.conqat.lib.commons.collections.IdentityHashSet;
import org.conqat.lib.commons.collections.UniqueIdManager;
import org.conqat.lib.commons.color.ECCSMColor;
import org.conqat.lib.commons.string.StringUtils;

/**
 * Create a DOT graph that contains the provided object, their defining classes,
 * the classes' class loaders and the parent class loaders up to the bootstrap
 * class loader. This is very useful for debugging class loader-related bugs.
 * 
 * @author deissenb
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 19EA526EC2A664B041D2DFAB4C80BB3D
 */
public class ClassLoaderGraphCreator {

	/** The header for DOT files. */
	public final static String HEADER = "digraph G {" + CR
			+ "  edge [  fontname = \"Helvetica\"," + CR
			+ "          color = \"#639CCE\", fontsize = 8 ];" + CR
			+ "  node [  shape = polygon," + CR + "          sides = 4," + CR
			+ "          color = \"#639CCE\"," + CR
			+ "          fontname = \"Helvetica\"," + CR
			+ "          fontsize    = 9," + CR + "          height=0.25];"
			+ CR;

	/** Manager to create unique node ids. */
	private final UniqueIdManager<Object> idManager = new UniqueIdManager<Object>();

	/** Objects included in the graph. */
	private final IdentityHashSet<Object> objects = new IdentityHashSet<Object>();

	/** Classes included in the graph. */
	private final IdentityHashSet<Class<?>> classes = new IdentityHashSet<Class<?>>();

	/** Class loaders. */
	private final IdentityHashSet<ClassLoader> classLoaders = new IdentityHashSet<ClassLoader>();

	/**
	 * Create new graph creator.
	 * 
	 * @param objects
	 *            the objects provided may be arbitrary objects or {@link Class}
	 *            -objects.
	 */
	public ClassLoaderGraphCreator(Object... objects) {
		for (Object object : objects) {
			if (object instanceof Class<?>) {
				addClass((Class<?>) object);
			} else {
				addObject(object);
			}
		}
	}

	/** Add a object. */
	@SuppressWarnings("null")
	public void addObject(Object object) {
		CCSMPre.isFalse(object == null, "Object may not be null.");
		objects.add(object);
		addClass(object.getClass());
	}

	/** Add a class. */
	public void addClass(Class<?> clazz) {
		CCSMPre.isFalse(clazz == null, "Class may not be null.");
		classes.add(clazz);
		classLoaders.addAll(getClassLoaders(clazz));
	}

	/**
	 * Determine class loader hierarchy of a class. This includes the
	 * <code>null</code> value to describe the bootstrap loader.
	 */
	private static List<ClassLoader> getClassLoaders(Class<?> clazz) {
		ArrayList<ClassLoader> loaders = new ArrayList<ClassLoader>();

		ClassLoader loader = clazz.getClassLoader();

		while (loader != null) {
			loaders.add(loader);
			loader = loader.getParent();
		}

		loaders.add(null);

		return loaders;
	}

	/** Create graph. */
	public String createClassLoaderGraph() {
		StringBuilder builder = new StringBuilder();
		builder.append(HEADER + CR);
		builder.append(createVertices());
		builder.append(createEdges());
		builder.append("}" + CR);

		return builder.toString();
	}

	/** Create edge. */
	private String createEdges() {
		StringBuilder result = new StringBuilder();

		for (Object object : objects) {
			appendEdge(result, object, object.getClass());
		}

		for (Class<?> clazz : classes) {
			appendEdge(result, clazz, clazz.getClassLoader());
		}

		for (ClassLoader loader : classLoaders) {
			if (loader != null) {
				appendEdge(result, loader, loader.getParent());
			}
		}

		return result.toString();
	}

	/** Append edge if it was not added before. */
	private void appendEdge(StringBuilder builder, Object start, Object end) {
		String edge = makeId(start) + " -> " + makeId(end) + ";" + CR;
		builder.append(edge);
	}

	/** Creates vertices. */
	private String createVertices() {
		StringBuilder result = new StringBuilder();

		for (Object object : objects) {
			result.append(createVertex(object, ECCSMColor.GREEN));
		}

		for (Class<?> clazz : classes) {
			result.append(createVertex(clazz, ECCSMColor.RED));
		}

		for (ClassLoader loader : classLoaders) {
			result.append(createVertex(loader, ECCSMColor.BLUE));
		}

		return result.toString();
	}

	/** Creates vertex. */
	private String createVertex(Object node, ECCSMColor color) {
		StringBuilder result = new StringBuilder();
		result.append(makeId(node));
		result.append(" [label=\"" + makeLabel(node) + "\", ");
		result.append(" color=\"" + color.getHTMLColorCode() + "\"");
		result.append("];" + CR);
		return result.toString();
	}

	/** Creates a label for a node. */
	private String makeLabel(Object object) {
		String result;
		if (object == null) {
			result = "Bootstrap Loader";
		} else if (object instanceof Class<?>) {
			// A class object
			result = ((Class<?>) object).getName();
		} else if (object instanceof ClassLoader) {
			// A class loader object
			result = object.toString();
		} else {
			// A "normal" object
			String toString = object.toString();

			// Deal with empty toStrings
			if (StringUtils.isEmpty(toString)) {
				toString = "instance of " + object.getClass().getName();
			}
			result = toString;
		}

		result = StringUtils.replaceLineBreaks(result, " ");
		result = result.replace('"', '\'');

		return result;
	}

	/** Determines unique id for node. */
	private String makeId(Object object) {
		return "id" + idManager.obtainId(object);
	}
}