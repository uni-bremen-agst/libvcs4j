package de.unibremen.informatik.st.libvcs4j.spoon.codesmell.missingJavadoc;

import java.util.Collection;

public interface Multimap<K, V> {
    boolean put(K key, V value);

    Multiset<K> keys();

    Collection<V> get(K key);

    Collection<V> values();
}
