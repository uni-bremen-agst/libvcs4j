package de.unibremen.informatik.st.libvcs4j.spoon.codesmell.missingJavadoc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ArrayListMultimap<K, V> implements Multimap<K, V> {
    private Map<K, ArrayList<V>> map;

    public ArrayListMultimap() {
        map = new HashMap<>();
    }

    public static <K, V> ArrayListMultimap<K, V> create() {
        return new ArrayListMultimap<>();
    }

    @Override
    public boolean put(final K key, final V value) {
        var coll = map.get(key);

        if (coll == null) {
            coll = new ArrayList<>();
            if (coll.add(value)) {
                map.put(key, coll);
                return true;
            } else {
                return false;
            }
        }

        return coll.add(value);
    }

    @Override
    public Collection<V> get(final K key) {
        return map.get(key);
    }

    @Override
    public Collection<V> values() {
        return map.values().stream().flatMap(Collection::stream).collect(Collectors.toList());
    }

    @Override
    public Multiset<K> keys() {
        return new ListMultiset<>(map.entrySet()
                .stream()
                .flatMap(this::toKeys)
                .collect(Collectors.toList()));
    }

    private Stream<K> toKeys(Map.Entry<K, ArrayList<V>> entry) {
        return entry.getValue().stream().map(e -> entry.getKey());
    }
}
