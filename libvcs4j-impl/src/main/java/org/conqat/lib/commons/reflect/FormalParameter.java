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

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * This class models formal method parameters to allow convenient reflective
 * access as the Java Reflection API does not model them explicitly.
 * 
 * Instances of this class can be obtained via
 * {@link ReflectionUtils#getFormalParameters(Method)}.
 * 
 * @author Florian Deissenboeck
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 83C459B9DD16515BE34B2F02411F81A0
 * 
 * @see ReflectionUtils#getFormalParameters(Method)
 * @see ReflectionUtils#invoke(Method, Object, java.util.Map)
 */
public final class FormalParameter {

	/** The method the parameter belongs to. */
	private final Method method;

	/** The position of the formal parameter within the methods parameter list. */
	private final int position;

	/**
	 * Create new formal parameter. This is called from
	 * {@link ReflectionUtils#getFormalParameters(Method)}.
	 */
	/* package */FormalParameter(Method method, int position) {
		this.method = method;
		this.position = position;
	}

	/** Get the method that declares this formal parameter. */
	public Method getMethod() {
		return method;
	}

	/**
	 * Get parameter type.
	 */
	public Class<?> getType() {
		return method.getParameterTypes()[position];
	}

	/**
	 * Get generic parameter type.
	 */
	public Type getGenericType() {
		return method.getGenericParameterTypes()[position];
	}

	/**
	 * Get parameter annotations.
	 */
	public Annotation[] getAnnotations() {
		Annotation[][] annotations = method.getParameterAnnotations();
		return annotations[position];
	}

	/**
	 * Get the position of the formal parameter within the methods parameter
	 * list.
	 */
	public int getPosition() {
		return position;
	}

	/**
	 * The hashcode is computed as the exclusive-or of the method's hashcode and
	 * (position+1).
	 */
	@Override
	public int hashCode() {
		return method.hashCode() ^ (position + 1);
	}

	/**
	 * Returns this element's annotation for the specified type if such an
	 * annotation is present, else <code>null</code>.
	 */
	@SuppressWarnings("unchecked")
	public <A extends Annotation> A getAnnotation(Class<A> annotationClass) {
		if (annotationClass == null) {
			throw new NullPointerException();
		}

		for (Annotation annotation : getAnnotations()) {
			if (annotation.annotationType().equals(annotationClass)) {
				return (A) annotation;
			}
		}

		return null;
	}

	/**
	 * Returns <code>true</code> if an annotation of the specified type is
	 * defined for this formal parameter.
	 */
	public boolean isAnnotationPresent(
			Class<? extends Annotation> annotationClass) {
		if (annotationClass == null) {
			throw new NullPointerException();
		}

		return getAnnotation(annotationClass) != null;
	}

	/**
	 * Two formal parameters are equal if their declaring methods and their
	 * position within the formal parameter list are equal.
	 */
	@Override
	public boolean equals(Object object) {
		if (object == null || !(object instanceof FormalParameter)) {
			return false;
		}

		if (object == this) {
			return true;
		}

		FormalParameter otherFormalParameter = (FormalParameter) object;

		return method.equals(otherFormalParameter.method)
				&& (position == otherFormalParameter.position);
	}

	/**
	 * Returns method name, position and type.
	 */
	@Override
	public String toString() {
		return "Formal parameter #" + position + " of method '"
				+ method.getName() + "' (type: '" + getType().getName() + "')";
	}
}