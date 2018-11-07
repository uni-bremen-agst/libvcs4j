package de.unibremen.informatik.st.libvcs4j.spoon;

import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeInformation;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtTypeReference;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * This class servers as a threadsafe cache to speedup scanners that have to
 * resolve a large number of references, such as {@link CtTypeReference},
 * {@link CtFieldReference}, and {@link CtExecutableReference}. Resolving
 * references is synchronized. Thus, a single instance of this class may be
 * shared between several scanners.
 */
public class ReferenceCache {

	/**
	 * Qualified name ({@link CtTypeReference#getQualifiedName()}) ->
	 * {@link CtType}.
	 */
	private final Map<String, CtType> types = new HashMap<>();

	/**
	 * Qualified name ({@link CtFieldReference#getQualifiedName()}) ->
	 * {@link CtField}.
	 */
	private final Map<String, CtField> fields = new HashMap<>();

	/**
	 * Signature ({@link CtExecutableReference#getSignature()}) ->
	 * {@link CtExecutable}.
	 */
	private final Map<String, CtExecutable> executables = new HashMap<>();

	/**
	 * Returns the type referenced by {@code reference}. Returns an empty
	 * {@link Optional} if {@code reference} is {@code null}.
	 *
	 * @param reference
	 * 		The reference to lookup.
	 * @return
	 * 		The type referenced by {@code reference}.
	 */
	public Optional<CtType> getOrResolve(final CtTypeReference reference) {
		return lookup(reference, types,
				CtTypeInformation::getQualifiedName,
				CtTypeReference::getDeclaration);
	}

	/**
	 * Returns the field referenced by {@code reference}. Returns an empty
	 * {@link Optional} if {@code reference} is {@code null}.
	 *
	 * @param reference
	 * 		The reference to lookup.
	 * @return
	 * 		Tye field referenced by {@code reference}.
	 */
	public Optional<CtField> getOrResolve(final CtFieldReference reference) {
		return lookup(reference, fields,
				CtFieldReference::getQualifiedName,
				CtFieldReference::getDeclaration);
	}

	/**
	 * Returns the executable referenced by {@code reference}. Returns an empty
	 * {@link Optional} if {@code reference} is {@code null}.
	 *
	 * @param reference
	 * 		The reference to lookup.
	 * @return
	 * 		The executable referenced by {@code reference}.
	 */
	public Optional<CtExecutable> getOrResolve(
			final CtExecutableReference reference) {
		return lookup(reference, executables,
				CtExecutableReference::getSignature,
				CtExecutableReference::getDeclaration);
	}

	private <E, K, V> Optional<V> lookup(final E element, final Map<K, V> map,
			final Function<E, K> toKey, final Function<E, V> resolve) {
		return Optional.ofNullable(element)
				.map(toKey)
				.map(key -> {
					synchronized (map) {
						V value = map.get(key);
						// allow null mappings
						if (value == null && !map.containsKey(key)) {
							value = resolve.apply(element);
							map.put(key, value);
						}
						return value;
					}
				});
	}
}
