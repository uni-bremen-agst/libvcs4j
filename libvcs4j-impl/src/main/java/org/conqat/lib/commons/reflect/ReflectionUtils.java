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

import java.awt.Color;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.conqat.lib.commons.color.ColorUtils;
import org.conqat.lib.commons.enums.EnumUtils;
import org.conqat.lib.commons.string.StringUtils;
import org.conqat.lib.commons.version.Version;

/**
 * This class provides utility methods for reflection purposes. In particular it
 * provides access to {@link FormalParameter}.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 49404 $
 * @ConQAT.Rating GREEN Hash: C0E2C8DADCD4C02EC20CC003A1BD1296
 */
public class ReflectionUtils {

	/** To compare formal parameters. */
	private static FormalParameterComparator comparator = new FormalParameterComparator();

	/**
	 * Convert a String to an Object of the provided type. It supports
	 * conversion to primitive types and simple tests (char: use first character
	 * of string, boolean: test for values "true", "on", "1", "yes"). Enums are
	 * handled by the {@link EnumUtils#valueOfIgnoreCase(Class, String)} method.
	 * Otherwise it is checked if the target type has a constructor that takes a
	 * single string and it is invoked. For all other cases an exception is
	 * thrown, as no conversion is possible.
	 * 
	 * <i>Maintainer note</i>: Make sure this method is changed in accordance
	 * with method {@link #isConvertibleFromString(Class)}
	 * 
	 * @see #convertString(String, Class)
	 * 
	 * @param value
	 *            the string to be converted.
	 * @param targetType
	 *            the type of the resulting object.
	 * @return the converted object.
	 * @throws TypeConversionException
	 *             in the case that no conversion could be performed.
	 * @see #isConvertibleFromString(Class)
	 * 
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T> T convertString(String value, Class<T> targetType)
			throws TypeConversionException {

		// value must be provided
		if (value == null) {
			if (String.class.equals(targetType)) {
				return (T) StringUtils.EMPTY_STRING;
			}
			throw new TypeConversionException(
					"Null value can't be converted to type '"
							+ targetType.getName() + "'.");
		}

		if (targetType.equals(Object.class) || targetType.equals(String.class)) {
			return (T) value;
		}
		if (targetType.isPrimitive()
				|| EJavaPrimitive.isWrapperType(targetType)) {
			return convertPrimitive(value, targetType);
		}
		if (targetType.isEnum()) {
			// we checked manually before
			Object result = EnumUtils.valueOfIgnoreCase((Class) targetType,
					value);
			if (result == null) {
				throw new TypeConversionException("'" + value
						+ "' is no valid value for enum "
						+ targetType.getName());
			}
			return (T) result;

		}
		if (targetType.equals(Color.class)) {
			Color result = ColorUtils.fromString(value);
			if (result == null) {
				throw new TypeConversionException("'" + value
						+ "' is not a valid color!");
			}
			return (T) result;
		}

		// Check if the target type has a constructor taking a single string.
		try {
			Constructor<T> c = targetType.getConstructor(String.class);
			return c.newInstance(value);
		} catch (Exception e) {
			throw new TypeConversionException(
					"No constructor taking one String argument found for type '"
							+ targetType + "' (" + e.getMessage() + ")", e);
		}

	}

	/** Convert String to object of specified type */
	public static Object convertTo(String valueString, String typeName)
			throws TypeConversionException, ClassNotFoundException {
		Class<?> clazz = resolveType(typeName);
		return convertString(valueString, clazz);
	}

	/**
	 * This method checks if the provided type can be converted from a string.
	 * With respect to {@link #convertString(String, Class)} the semantics are
	 * the following: If this method returns <code>true</code> a particular
	 * string <i>may</i> be convertible to the target type. If this method
	 * returns <code>false</code>, a call to
	 * {@link #convertString(String, Class)} is guaranteed to result in a
	 * {@link TypeConversionException}. If a call to
	 * {@link #convertString(String, Class)} does not result in an exception, a
	 * call to this method is guaranteed to return <code>true</code>.
	 * <p>
	 * <i>Maintainer note</i>: Make sure this method is change in accordance
	 * with method {@link #convertString(String, Class)}
	 * 
	 * @see #convertString(String, Class)
	 */
	public static boolean isConvertibleFromString(Class<?> targetType) {

		if (targetType.equals(Object.class) || targetType.equals(String.class)) {
			return true;
		}
		if (targetType.isPrimitive()
				|| EJavaPrimitive.isWrapperType(targetType)) {
			return true;
		}
		if (targetType.isEnum()) {
			return true;

		}
		if (targetType.equals(Color.class)) {
			return true;
		}

		try {
			targetType.getConstructor(String.class);
			return true;
		} catch (SecurityException e) {
			// case is handled at method end
		} catch (NoSuchMethodException e) {
			// case is handled at method end
		}

		return false;
	}

	/**
	 * Obtain array of formal parameters for a method.
	 * 
	 * @see FormalParameter
	 */
	public static FormalParameter[] getFormalParameters(Method method) {

		int parameterCount = method.getParameterTypes().length;

		FormalParameter[] parameters = new FormalParameter[parameterCount];

		for (int i = 0; i < parameterCount; i++) {
			parameters[i] = new FormalParameter(method, i);
		}

		return parameters;
	}

	/**
	 * Get super class list of a class.
	 * 
	 * @param clazz
	 *            the class to start traversal from
	 * @return a list of super class where the direct super class of the
	 *         provided class is the first member of the list. <br>
	 *         For {@link Object}, primitives and interfaces this returns an
	 *         empty list. <br>
	 *         For arrays this returns a list containing only {@link Object}. <br>
	 *         For enums this returns a list containing {@link Enum} and
	 *         {@link Object}
	 */
	public static List<Class<?>> getSuperClasses(Class<?> clazz) {
		ArrayList<Class<?>> superClasses = new ArrayList<Class<?>>();
		findSuperClasses(clazz, superClasses);
		return superClasses;
	}

	/**
	 * Invoke a method with parameters.
	 * 
	 * @param method
	 *            the method to invoke
	 * @param object
	 *            the object the underlying method is invoked from
	 * @param parameterMap
	 *            this maps from the formal parameter of the method to the
	 *            parameter value
	 * @return the result of dispatching the method
	 * @throws IllegalArgumentException
	 *             if the method is an instance method and the specified object
	 *             argument is not an instance of the class or interface
	 *             declaring the underlying method (or of a subclass or
	 *             implementor thereof); if the number of actual and formal
	 *             parameters differ; if an unwrapping conversion for primitive
	 *             arguments fails; or if, after possible unwrapping, a
	 *             parameter value cannot be converted to the corresponding
	 *             formal parameter type by a method invocation conversion; if
	 *             formal parameters belong to different methods.
	 * 
	 * @throws IllegalAccessException
	 *             if this Method object enforces Java language access control
	 *             and the underlying method is inaccessible.
	 * @throws InvocationTargetException
	 *             if the underlying method throws an exception.
	 * @throws NullPointerException
	 *             if the specified object is null and the method is an instance
	 *             method.
	 * @throws ExceptionInInitializerError
	 *             if the initialization provoked by this method fails.
	 */
	public static Object invoke(Method method, Object object,
			Map<FormalParameter, Object> parameterMap)
			throws IllegalArgumentException, IllegalAccessException,
			InvocationTargetException {

		for (FormalParameter formalParameter : parameterMap.keySet()) {
			if (!formalParameter.getMethod().equals(method)) {
				throw new IllegalArgumentException(
						"Parameters must belong to method.");
			}
		}

		Object[] parameters = obtainParameters(parameterMap);

		return method.invoke(object, parameters);

	}

	/**
	 * Check whether an Object of the source type can be used instead of an
	 * Object of the target type. This method is required, as the
	 * {@link Class#isAssignableFrom(java.lang.Class)} does not handle primitive
	 * types.
	 * 
	 * @param source
	 *            type of the source object
	 * @param target
	 *            type of the target object
	 * @return whether an assignment would be possible.
	 */
	public static boolean isAssignable(Class<?> source, Class<?> target) {
		return resolvePrimitiveClass(target).isAssignableFrom(
				resolvePrimitiveClass(source));
	}

	/**
	 * Returns the wrapper class type for a primitive type (e.g.
	 * <code>Integer</code> for an <code>int</code>). If the given class is not
	 * a primitive, the class itself is returned.
	 * 
	 * @param clazz
	 *            the class.
	 * @return the corresponding class type.
	 */
	public static Class<?> resolvePrimitiveClass(Class<?> clazz) {
		if (!clazz.isPrimitive()) {
			return clazz;
		}

		EJavaPrimitive primitive = EJavaPrimitive.getForPrimitiveClass(clazz);
		if (primitive == null) {
			throw new IllegalStateException("Did Java get a new primitive? "
					+ clazz.getName());
		}
		return primitive.getWrapperClass();
	}

	/**
	 * Convert a String to an Object of the provided type. This only works for
	 * primitive types and wrapper types.
	 * 
	 * @param value
	 *            the string to be converted.
	 * @param targetType
	 *            the type of the resulting object.
	 * @return the converted object.
	 * @throws TypeConversionException
	 *             in the case that no conversion could be performed.
	 */
	@SuppressWarnings("unchecked")
	/* package */static <T> T convertPrimitive(String value, Class<T> targetType)
			throws TypeConversionException {

		EJavaPrimitive primitive = EJavaPrimitive
				.getForPrimitiveOrWrapperClass(targetType);
		if (primitive == null) {
			throw new IllegalArgumentException("Type '" + targetType.getName()
					+ "' is not a primitive type!");
		}

		try {

			switch (primitive) {
			case BOOLEAN:
				boolean b = "1".equalsIgnoreCase(value)
						|| "true".equalsIgnoreCase(value)
						|| "on".equalsIgnoreCase(value)
						|| "yes".equalsIgnoreCase(value);
				return (T) Boolean.valueOf(b);

			case CHAR:
				return (T) Character.valueOf(value.charAt(0));

			case BYTE:
				return (T) Byte.valueOf(value);

			case SHORT:
				return (T) Short.valueOf(value);

			case INT:
				return (T) Integer.valueOf(value);

			case LONG:
				return (T) Long.valueOf(value);

			case FLOAT:
				return (T) Float.valueOf(value);

			case DOUBLE:
				return (T) Double.valueOf(value);

			default:
				throw new TypeConversionException("No conversion possible for "
						+ primitive);
			}

		} catch (NumberFormatException e) {
			throw new TypeConversionException("Value'" + value
					+ "' can't be converted to type '" + targetType.getName()
					+ "' (" + e.getMessage() + ").", e);
		}
	}

	/**
	 * Resolves the class object for a type name. Type name can be a primitive.
	 * For resolution, {@link Class#forName(String)} is used, that uses the
	 * caller's class loader.
	 * <p>
	 * While method <code>Class.forName(...)</code> resolves fully qualified
	 * names, it does not resolve primitives, e.g. "java.lang.Boolean" can be
	 * resolved but "boolean" cannot.
	 * 
	 * @param typeName
	 *            name of the type. For primitives case is ignored.
	 * 
	 * @throws ClassNotFoundException
	 *             if the typeName neither resolves to a primitive, nor to a
	 *             known class.
	 */
	public static Class<?> resolveType(String typeName)
			throws ClassNotFoundException {
		return resolveType(typeName, null);
	}

	/**
	 * Resolves the class object for a type name. Type name can be a primitive.
	 * For resolution, the given class loader is used.
	 * <p>
	 * While method <code>Class.forName(...)</code> resolves fully qualified
	 * names, it does not resolve primitives, e.g. "java.lang.Boolean" can be
	 * resolved but "boolean" cannot.
	 * 
	 * @param typeName
	 *            name of the type. For primitives case is ignored.
	 * 
	 * @param classLoader
	 *            the class loader used for loading the class. If this is null,
	 *            the caller class loader is used.
	 * 
	 * @throws ClassNotFoundException
	 *             if the typeName neither resolves to a primitive, nor to a
	 *             known class.
	 */
	public static Class<?> resolveType(String typeName, ClassLoader classLoader)
			throws ClassNotFoundException {

		EJavaPrimitive primitive = EJavaPrimitive
				.getPrimitiveIgnoreCase(typeName);

		if (primitive != null) {
			return primitive.getClassObject();
		}

		if (classLoader == null) {
			return Class.forName(typeName);
		}
		return Class.forName(typeName, true, classLoader);
	}

	/**
	 * Recursively add super classes to a list.
	 * 
	 * @param clazz
	 *            class to start from
	 * @param superClasses
	 *            list to store super classes.
	 */
	private static void findSuperClasses(Class<?> clazz,
			List<Class<?>> superClasses) {
		Class<?> superClass = clazz.getSuperclass();
		if (superClass == null) {
			return;
		}
		superClasses.add(superClass);
		findSuperClasses(superClass, superClasses);
	}

	/**
	 * Obtain parameter array from parameter map.
	 */
	private static Object[] obtainParameters(
			Map<FormalParameter, Object> parameterMap) {

		ArrayList<FormalParameter> formalParameters = new ArrayList<FormalParameter>(
				parameterMap.keySet());

		Collections.sort(formalParameters, comparator);

		Object[] result = new Object[formalParameters.size()];

		for (int i = 0; i < formalParameters.size(); i++) {
			result[i] = parameterMap.get(formalParameters.get(i));
		}

		return result;
	}

	/**
	 * Obtain the return type of method. This method deals with bridge methods
	 * introduced by generics. This works for methods without parameters only.
	 * 
	 * @param clazz
	 *            the class
	 * @param methodName
	 *            the name of the method.
	 * @return the return type
	 * @throws NoSuchMethodException
	 *             if the class doesn't contain the desired method
	 */
	public static Class<?> obtainMethodReturnType(Class<?> clazz,
			String methodName) throws NoSuchMethodException {

		// due to the potential presense of bridge methods we can't use
		// Clazz.getMethod() and have to iterate over all methods.
		for (Method method : clazz.getMethods()) {
			if (isValid(method, methodName)) {
				return method.getReturnType();
			}
		}
		// method not found
		throw new NoSuchMethodException("Class " + clazz.getName()
				+ " doesn't have parameterless method named " + methodName);
	}

	/**
	 * Obtain the generic return type of method. This method deals with the gory
	 * details of bridge methods and generics. This works for methods without
	 * parameters only. This doesn't work for interfaces, arrays and enums.
	 * 
	 * @param clazz
	 *            the class
	 * @param methodName
	 *            the name of the method.
	 * @return the return type
	 * @throws NoSuchMethodException
	 *             if the class doesn't contain the desired method
	 */
	public static Class<?> obtainGenericMethodReturnType(Class<?> clazz,
			String methodName) throws NoSuchMethodException {

		if (clazz.isArray() || clazz.isEnum()) {
			throw new IllegalArgumentException(
					"Doesn't work for arrays and enums.");
		}
		if (clazz.getTypeParameters().length != 0) {
			throw new IllegalArgumentException(
					"Doesn't work for generic classes.");
		}

		for (Method method : clazz.getMethods()) {
			if (isValid(method, methodName)) {
				return new GenericTypeResolver(clazz).resolveGenericType(method
						.getGenericReturnType());
			}
		}

		// method not found
		throw new NoSuchMethodException("Class " + clazz.getName()
				+ " doesn't have parameterless method named " + methodName);
	}

	/**
	 * Tests if a method has the correct name, no parameters and is no bridge
	 * method.
	 */
	private static boolean isValid(Method method, String methodName) {
		return method.getName().equals(methodName)
				&& method.getParameterTypes().length == 0 && !method.isBridge();
	}

	/**
	 * Returns the value from the map, whose key is the best match for the given
	 * class. The best match is defined by the first match occurring in a breath
	 * first search of the inheritance tree, where the base class is always
	 * visited before the implemented interfaces. Interfaces are traversed in
	 * the order they are defined in the source file. The only exception is
	 * {@link Object}, which is considered only as the very last option.
	 * <p>
	 * As this lookup can be expensive (reflective iteration over the entire
	 * inheritance tree) the results should be cached if multiple lookups for
	 * the same class are expected.
	 * 
	 * 
	 * @param clazz
	 *            the class being looked up.
	 * @param classMap
	 *            the map to perform the lookup in.
	 * @return the best match found or <code>null</code> if no matching entry
	 *         was found. Note that <code>null</code> will also be returned if
	 *         the entry for the best matching class was <code>null</code>.
	 */
	public static <T> T performNearestClassLookup(Class<?> clazz,
			Map<Class<?>, T> classMap) {
		Queue<Class<?>> q = new LinkedList<Class<?>>();
		q.add(clazz);

		while (!q.isEmpty()) {
			Class<?> current = q.poll();
			if (classMap.containsKey(current)) {
				return classMap.get(current);
			}

			Class<?> superClass = current.getSuperclass();
			if (superClass != null && superClass != Object.class) {
				q.add(superClass);
			}

			for (Class<?> iface : current.getInterfaces()) {
				q.add(iface);
			}
		}
		return classMap.get(Object.class);
	}

	/**
	 * Returns whether the given object is an instance of at least one of the
	 * given classes.
	 */
	public static boolean isInstanceOfAny(Object o, Class<?>... classes) {
		for (Class<?> c : classes) {
			if (c.isInstance(o)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns whether the given object is an instance of all of the given
	 * classes.
	 */
	public static boolean isInstanceOfAll(Object o, Class<?>... classes) {
		for (Class<?> c : classes) {
			if (!c.isInstance(o)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Returns the first object in the given collection which is an instance of
	 * the given class (or null otherwise).
	 */
	@SuppressWarnings("unchecked")
	public static <T> T pickInstanceOf(Class<T> clazz, Collection<?> objects) {
		for (Object o : objects) {
			if (clazz.isInstance(o)) {
				return (T) o;
			}
		}
		return null;
	}

	/**
	 * Obtains the version of a Java class file.
	 * 
	 * Class file major versions (from
	 * http://en.wikipedia.org/wiki/Class_(file_format)):
	 * 
	 * <pre>
	 * J2SE 7 = 51 
	 * J2SE 6.0 = 50
	 * J2SE 5.0 = 49
	 * JDK 1.4 = 48
	 * JDK 1.3 = 47
	 * JDK 1.2 = 46
	 * JDK 1.1 = 45
	 * </pre>
	 * 
	 * @param inputStream
	 *            stream to read class file from.
	 * @return the class file version or <code>null</code> if stream does not
	 *         contain a class file.
	 * @throws IOException
	 *             if an IO problem occurs.
	 */
	public static Version obtainClassFileVersion(InputStream inputStream)
			throws IOException {
		DataInputStream classfile = new DataInputStream(inputStream);
		int magic = classfile.readInt();
		if (magic != 0xcafebabe) {
			return null;
		}
		int minorVersion = classfile.readUnsignedShort();
		int majorVersion = classfile.readUnsignedShort();

		return new Version(majorVersion, minorVersion);
	}

	/**
	 * This method extracts the class file version from each class file in the
	 * provided jar.
	 * 
	 * @return the result maps from the class file to its version.
	 */
	public static HashMap<String, Version> getClassFileVersions(File jarFile)
			throws IOException {

		HashMap<String, Version> result = new HashMap<String, Version>();

		JarFile jar = new JarFile(jarFile);
		Enumeration<JarEntry> entries = jar.entries();

		while (entries.hasMoreElements()) {
			JarEntry entry = entries.nextElement();
			if (!entry.isDirectory() && entry.getName().endsWith(".class")) {
				InputStream entryStream = jar.getInputStream(entry);
				Version version = obtainClassFileVersion(entryStream);
				result.put(entry.getName(), version);
				entryStream.close();
			}
		}

		jar.close();

		return result;
	}

	/**
	 * Creates a list that contains only the types that are instances of a
	 * specified type from the objects of an input list. The input list is not
	 * modified.
	 * 
	 * @param objects
	 *            List of objects that gets filtered
	 * 
	 * @param type
	 *            target type whose instances are returned
	 */
	@SuppressWarnings("unchecked")
	public static <T> List<T> listInstances(List<?> objects, Class<T> type) {
		List<T> filtered = new ArrayList<T>();

		for (Object object : objects) {
			if (type.isInstance(object)) {
				filtered.add((T) object);
			}
		}

		return filtered;
	}

	/**
	 * Determines the most specific common base class. If one of the types is
	 * primitive or an interface, null is returned.
	 */
	public static Class<?> determineCommonBase(Class<?> class1, Class<?> class2) {
		List<Class<?>> hierarchy1 = getBaseHierarchy(class1);
		List<Class<?>> hierarchy2 = getBaseHierarchy(class2);

		if (hierarchy1.isEmpty() || hierarchy2.isEmpty()
				|| !hierarchy1.get(0).equals(hierarchy2.get(0))) {
			return null;
		}

		int index = 0;
		while (index < hierarchy1.size() && index < hierarchy2.size()
				&& hierarchy1.get(index).equals(hierarchy2.get(index))) {
			index += 1;
		}
		if (index <= hierarchy1.size()) {
			index -= 1;
		}

		return hierarchy1.get(index);
	}

	/**
	 * Returns the hierarchy of base classes starting with {@link Object} up to
	 * the class itself.
	 */
	public static List<Class<?>> getBaseHierarchy(Class<?> baseClass) {
		List<Class<?>> hierarchy = ReflectionUtils.getSuperClasses(baseClass);
		Collections.reverse(hierarchy);
		hierarchy.add(baseClass);
		return hierarchy;
	}

	/**
	 * Returns the set of all interfaces implemented by the given class. This
	 * includes also interfaces that are indirectly implemented as they are
	 * extended by an interfaces that is implemented by the given class. If the
	 * given class is an interface, it is included itself.
	 */
	public static Set<Class<?>> getAllInterfaces(Class<?> baseClass) {
		Queue<Class<?>> q = new LinkedList<Class<?>>();
		q.add(baseClass);

		Set<Class<?>> result = new HashSet<Class<?>>();
		if (baseClass.isInterface()) {
			result.add(baseClass);
		}

		while (!q.isEmpty()) {
			for (Class<?> iface : q.poll().getInterfaces()) {
				if (result.add(iface)) {
					q.add(iface);
				}
			}
		}

		return result;
	}

	/**
	 * Returns all fields declared for a class (all visibilities and also
	 * inherited from super classes). Note that multiple fields with the same
	 * name may exist due to redeclaration/shadowing in sub classes.
	 */
	public static List<Field> getAllFields(Class<?> type) {
		List<Field> fields = new ArrayList<>();
		while (type != null) {
			fields.addAll(Arrays.asList(type.getDeclaredFields()));
			type = type.getSuperclass();
		}
		return fields;
	}

	/**
	 * Returns all methods declared for a class (all visibilities and also
	 * inherited from super classes). Note that multiple methods with the same
	 * name may exist due to redeclaration/shadowing in sub classes.
	 */
	public static List<Method> getAllMethods(Class<?> type) {
		List<Method> methods = new ArrayList<>();
		while (type != null) {
			methods.addAll(Arrays.asList(type.getDeclaredMethods()));
			type = type.getSuperclass();
		}
		return methods;
	}
}
