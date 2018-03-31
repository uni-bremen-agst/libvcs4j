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

import java.util.HashSet;
import java.util.Set;

import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.collections.UnmodifiableSet;

/**
 * This stores the full type of a class, i.e., base type plus required
 * interfaces. Primitive types are internally mapped to their corresponding
 * class, so for example <code>int</code> and <code>Integer</code> are the same
 * class type. This class is immutable.
 * <p>
 * The list of additional interfaces is kept normalized, such that no interface
 * is in the list if it already implicitly given (either implemented by the base
 * type or by another interface of the list).
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: CB0791D3D8A7212BBFD304C22FC8AC00
 */
public final class ClassType {

	/** The base class of the class type. */
	private Class<?> baseClass = Object.class;

	/**
	 * All interfaces to be implemented by this type (normalized as described
	 * above).
	 */
	private Set<Class<?>> interfaces = new HashSet<Class<?>>();

	/**
	 * Creates the most general class type, that is of type Object without any
	 * additional interfaces.
	 */
	public ClassType() {
		// nothing to do
	}

	/** Copy constructor. */
	private ClassType(ClassType c) {
		baseClass = c.baseClass;
		interfaces.addAll(c.interfaces);
	}

	/**
	 * Creates a new class type from a "normal" class.
	 */
	public ClassType(Class<?> clazz) {
		if (clazz.isInterface()) {
			interfaces.add(clazz);
			normalizeInterfaces();
		} else {
			baseClass = ReflectionUtils.resolvePrimitiveClass(clazz);
		}
	}

	/**
	 * Creates a new class type from a list of "normal" class by either using it
	 * as the base class or adding it to the interface list.
	 * 
	 * @throws TypesNotMergableException
	 *             if the provided classes can not be joined.
	 */
	public ClassType(Class<?>... classes) throws TypesNotMergableException {
		for (Class<?> c : classes) {
			mergeInClass(ReflectionUtils.resolvePrimitiveClass(c));
		}
		normalizeInterfaces();
	}

	/**
	 * Normalizes this class type by removing all interfaces implemented by the
	 * base class or already handled by other interfaces.
	 */
	private void normalizeInterfaces() {
		Set<Class<?>> oldInterfaces = interfaces;
		interfaces = new HashSet<Class<?>>();

		for (Class<?> iface : oldInterfaces) {
			if (iface.isAssignableFrom(baseClass)) {
				// base class can be assigned to interface (i.e. implements it),
				// so there is no need to carry it around
				continue;
			}

			boolean isCovered = false;
			for (Class<?> otherIface : oldInterfaces) {
				if ((iface != otherIface) && iface.isAssignableFrom(otherIface)) {
					// more specific interface already in set, so iface is
					// "covered" already
					isCovered = true;
					break;
				}
			}
			if (!isCovered) {
				interfaces.add(iface);
			}
		}
	}

	/**
	 * Merges the given class type with this type. This class and the merged in
	 * class is not modified in this process, and a newly created instance of
	 * the correct type is returned.
	 * <p>
	 * Merging corresponds to "and" types, i.e. the resulting type will be
	 * assignable <b>to</b> each of the input types.
	 * 
	 * @throws TypesNotMergableException
	 *             if the new type could not be merged in.
	 */
	public ClassType merge(ClassType classType)
			throws TypesNotMergableException {
		ClassType result = new ClassType(this);
		result.interfaces.addAll(classType.interfaces);

		result.mergeInClass(classType.baseClass);
		result.normalizeInterfaces();

		return result;
	}

	/**
	 * Merges the given class or interface with this type. The type is modified
	 * to also be compatible with the given class. This does not include
	 * normalization!
	 * 
	 * @throws TypesNotMergableException
	 *             if the new class could not be merged in.
	 */
	private void mergeInClass(Class<?> c) throws TypesNotMergableException {
		if (c.isInterface()) {
			interfaces.add(c);
		} else if (baseClass.equals(c)
				|| ReflectionUtils.isAssignable(baseClass, c)) {
			// nothing to do, as base class is more specific than given class
		} else if (ReflectionUtils.isAssignable(c, baseClass)) {
			// c specializes the base class
			baseClass = c;
		} else {
			throw new TypesNotMergableException("Types " + c + " and "
					+ baseClass + " could not be merged!");
		}
	}

	/**
	 * Intersects the given class type with this type. This class and the
	 * intersected class are not modified in this process, and a newly created
	 * instance of the correct type is returned.
	 * <p>
	 * Intersection corresponds to "or" types, i.e. the resulting type will be
	 * assignable <b>from</b> each of the input types.
	 */
	public ClassType intersect(ClassType classType) {
		ClassType result = new ClassType(ReflectionUtils.determineCommonBase(
				getBaseClass(), classType.getBaseClass()));

		Set<Class<?>> commonInterfaces = getAllInterfaces();
		commonInterfaces.retainAll(classType.getAllInterfaces());
		result.interfaces.addAll(commonInterfaces);
		result.normalizeInterfaces();

		return result;
	}

	/** Returns transitive set of all interfaces. */
	private Set<Class<?>> getAllInterfaces() {
		Set<Class<?>> result = ReflectionUtils.getAllInterfaces(baseClass);
		for (Class<?> iface : interfaces) {
			if (!result.contains(iface)) {
				result.addAll(ReflectionUtils.getAllInterfaces(iface));
			}
		}
		return result;
	}

	/** Returns the base class for this ClassType. */
	public Class<?> getBaseClass() {
		return baseClass;
	}

	/**
	 * Returns whether this ClassType requires the implementation of interfaces
	 * in addition to the base class.
	 */
	public boolean hasInterfaces() {
		return !interfaces.isEmpty();
	}

	/**
	 * Returns all interfaces implemented by this class type in addition to the
	 * base class. The collection is normalized such that no interface is
	 * explicitly given if it is implemented by the base class or a super
	 * interface of another interface of the list.
	 */
	public UnmodifiableSet<Class<?>> getInterfaces() {
		return CollectionUtils.asUnmodifiable(interfaces);
	}

	/**
	 * Checks if an object of class type <code>classType</code> could be
	 * assigned to an object of this class type.
	 */
	public boolean isAssignableFrom(ClassType classType) {
		// we do not need the isAssignable from the ReflectionUtils here, as we
		// store primitive types as they class right at construction time.
		if (!baseClass.isAssignableFrom(classType.baseClass)) {
			return false;
		}

		// all interfaces of "this" must also be fulfilled by classType
		for (Class<?> iface : interfaces) {
			if (!classType.implementsInterface(iface)) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Returns whether the provided interface is implemented by this class type.
	 */
	public boolean implementsInterface(Class<?> requiredInterface) {
		if (requiredInterface.isAssignableFrom(baseClass)) {
			return true;
		}
		for (Class<?> iface : interfaces) {
			if (requiredInterface.isAssignableFrom(iface)) {
				return true;
			}
		}
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		// shortcut for plain interfaces
		if (interfaces.size() == 1 && baseClass.equals(Object.class)) {
			return interfaces.iterator().next().getName();
		}

		StringBuilder sb = new StringBuilder();
		sb.append(baseClass.getName());
		if (!interfaces.isEmpty()) {
			String sep = " ";
			sb.append(" implements");
			for (Class<?> iface : interfaces) {
				sb.append(sep);
				sep = ", ";
				sb.append(iface.getName());
			}
		}
		return sb.toString();
	}

	/** {@inheritDoc} */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ClassType)) {
			return false;
		}
		ClassType ct = (ClassType) obj;
		if (!ct.baseClass.equals(baseClass)) {
			return false;
		}
		if (ct.interfaces.size() != interfaces.size()) {
			return false;
		}
		for (Class<?> iface : ct.interfaces) {
			if (!interfaces.contains(iface)) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Returns a hash code for this instance based on the base class and the
	 * additional (normalized) interfaces.
	 */
	@Override
	public int hashCode() {
		int result = 1;
		for (Class<?> iface : interfaces) {
			result *= iface.hashCode();
		}
		return 13 * result * baseClass.hashCode();
	}
}