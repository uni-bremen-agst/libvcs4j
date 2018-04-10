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

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.HashMap;
import java.util.Map;

import org.conqat.lib.commons.assertion.CCSMAssert;

/**
 * This is a class for helping with the resolution of generic types in the
 * context of reflection. Unfortunately there is no easy way to get the actual
 * return type or parameter type of a method if some generic class is within the
 * class hierarchy. This class handles the messy aspects of this.
 * <p>
 * The instances of this class are bound to single classes, for which they are
 * constructed. They only work correctly when querying parameters originating
 * from this class or one of its methods. Furthermore this class does not work,
 * if the class the lookup is performed for is generic itself.
 * <p>
 * The error handling of this class is rather crude. If any of the assumptions
 * (either specified above or we learned from playing with reflection) is not
 * met, an exception is thrown (currently {@link IllegalStateException}).
 * 
 * @author $Author: hummelb $
 * @version $Rev: 48551 $
 * @ConQAT.Rating GREEN Hash: 0A8EA31CB99E34F84AD60FAEB70F1007
 */
public class GenericTypeResolver {

	/** The map for looking up generic parameters. */
	private final Map<TypeVariable<?>, Class<?>> parameterLookup = new HashMap<TypeVariable<?>, Class<?>>();

	/**
	 * Creates a new generic type resolver for the given class.
	 * 
	 * @throws IllegalArgumentException
	 *             if called for generic class.
	 */
	public GenericTypeResolver(Class<?> clazz) {
		if (clazz.getTypeParameters().length != 0) {
			throw new IllegalArgumentException(
					"This only works for non-generic classes!");
		}
		fillParamMap(clazz);
	}

	/**
	 * Creates a new generic type resolver for the given field and resolver of
	 * the field's (non-generic) class or child class.
	 */
	public GenericTypeResolver(Field field, GenericTypeResolver parentResolver) {
		parameterLookup.putAll(parentResolver.parameterLookup);

		fillInParameters(field.getType(), field.getGenericType());
		fillParamMap(field.getType());
	}

	/**
	 * Initializes the generic parameter lookup table by comparing for each
	 * super class and interface the type parameters with the actual type
	 * arguments. This process then is repeated recursively. It is important to
	 * fill from the current class before going to the super class, as the super
	 * class may reference parameters used in this class.
	 */
	private void fillParamMap(Class<?> clazz) {
		Class<?> superClass = clazz.getSuperclass();
		if (superClass != null) {
			Type superType = clazz.getGenericSuperclass();
			fillInParameters(superClass, superType);
			fillParamMap(superClass);
		}

		Class<?>[] interfaces = clazz.getInterfaces();
		Type[] genericInterfaces = clazz.getGenericInterfaces();
		check(interfaces.length == genericInterfaces.length,
				"Interface lists should be equally long!");
		for (int i = 0; i < interfaces.length; ++i) {
			fillInParameters(interfaces[i], genericInterfaces[i]);
			fillParamMap(interfaces[i]);
		}
	}

	/**
	 * Fill the generic parameter lookup map from an explicit (class, type) pair
	 * by comparing the type parameters with the actual type parameters (if the
	 * class is generic at all).
	 * 
	 * @param clazz
	 *            the class (potentially) containing type parameters.
	 * @param type
	 *            the corresponding (potentially) generic type.
	 */
	private void fillInParameters(Class<?> clazz, Type type) {
		if (type instanceof ParameterizedType) {
			Type[] actualTypeArguments = ((ParameterizedType) type)
					.getActualTypeArguments();
			TypeVariable<?>[] typeParameters = clazz.getTypeParameters();
			check(actualTypeArguments.length == typeParameters.length,
					"Type parameters and actual arguments should be equally long!");

			for (int i = 0; i < typeParameters.length; ++i) {
				parameterLookup.put(typeParameters[i],
						resolveGenericType(actualTypeArguments[i]));
			}
		}
	}

	/**
	 * Returns the actual type from a (potentially) generic type. If the
	 * argument is a plain class, it is returned, otherwise a lookup in the
	 * internal generic parameter map is performed. For parameterized types
	 * (e.g. <code>List&lt;String&gt;</code> the raw type (here: List) is
	 * returned.
	 * <p>
	 * Note that this only works for return values and parameters of methods
	 * belonging to the class for which this instance was constructed for.
	 * Otherwise the behavior is undefined (either returning nonsense or
	 * throwing an exception).
	 * 
	 * @param genericType
	 *            a type such as returned from
	 *            {@link Method#getGenericReturnType()} or
	 *            {@link Method#getGenericParameterTypes()}.
	 */
	public Class<?> resolveGenericType(Type genericType) {
		if (genericType instanceof Class<?>) {
			return (Class<?>) genericType;
		}
		if (genericType instanceof TypeVariable<?>) {
			check(parameterLookup.containsKey(genericType),
					"All generic parameters should be bound.");
			return parameterLookup.get(genericType);
		}
		if (genericType instanceof ParameterizedType) {
			ParameterizedType pt = (ParameterizedType) genericType;
			return (Class<?>) pt.getRawType();
		}
		if (genericType instanceof GenericArrayType) {
			GenericArrayType gat = (GenericArrayType) genericType;
			return Array.newInstance(
					resolveGenericType(gat.getGenericComponentType()), 0)
					.getClass();
		}

		check(false,
				"Generic types should be either concrete classes, type variables, or parametrized types: "
						+ genericType.getClass());
		return null; // this line is never reached
	}

	/**
	 * This is an assertion method to simplify changing the type of error
	 * handling. Many of the assertions rather document our assumptions about
	 * the JVM, than really checking errors.
	 */
	private void check(boolean assumedCondition, String errorMessage) {
		CCSMAssert.isTrue(assumedCondition, errorMessage);
	}
}