package de.unibremen.informatik.st.libvcs4j.spoon.codesmell.missingJavadoc;

import java.util.Collection;

/**
 * Multiset is a google guava inspired collection that allows a set to contain duplicates.
 *
 * @param <V> the type of the values.
 */
public interface Multiset<V> extends Collection<V> {
    /**
     * Returns the count of a specific value in the set.
     *
     * @param value the value which occurrences have to be counted.
     */
    int count(V value);
}
