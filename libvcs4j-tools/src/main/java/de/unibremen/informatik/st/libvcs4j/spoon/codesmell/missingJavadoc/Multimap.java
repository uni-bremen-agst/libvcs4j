package de.unibremen.informatik.st.libvcs4j.spoon.codesmell.missingJavadoc;

import java.util.Collection;

/**
 * Multimap is a google guava inspired map-like collection type that allows to
 * associate a key with multiple values instead of just one.
 *
 * @param <K> the type of the keys.
 * @param <V> the type of the values.
 * @author Ruben Smidt
 */
public interface Multimap<K, V> {
    /**
     * Tries to put a value into the sink associated with a given key.
     *
     * @param key   the key of the sink.
     * @param value the value to insert.
     * @return {@code true} if the the element was inserted, otherwise {@code false}.
     */
    boolean put(K key, V value);

    /**
     * Returns a {@link Multiset} containing each key in the same amount of values a key has.
     */
    Multiset<K> keys();

    /**
     * Retrieves a collection for a given key.
     *
     * @param key the key that is associated with the collection.
     * @return the collection if already associated with a key otherwise an empty collection.
     */
    Collection<V> get(K key);

    /**
     * Consolidates and returns all collections associated by a key.
     */
    Collection<V> values();
}
