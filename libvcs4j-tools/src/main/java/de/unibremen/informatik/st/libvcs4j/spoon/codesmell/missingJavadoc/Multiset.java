package de.unibremen.informatik.st.libvcs4j.spoon.codesmell.missingJavadoc;

import java.util.Collection;

public interface Multiset<V> extends Collection<V> {
    int count(V value);
}
