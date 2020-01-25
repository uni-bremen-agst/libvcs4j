package de.unibremen.informatik.st.libvcs4j.spoon.codesmell.missingJavadoc;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * ArrayListMultimap is a google guava inspired multimap implementation that utilizes
 * {@link ArrayList} to store the values for a given key.
 *
 * @param <K> the type of the keys.
 * @param <V> the type of the values.
 * @author Ruben Smidt
 */
public class ArrayListMultimap<K, V> implements Multimap<K, V> {
    private Map<K, ArrayList<V>> map;

    private ArrayListMultimap() {
        map = new HashMap<>();
    }

    /**
     * Constructs an instance of ArrayListMultimap with a default configuration.
     *
     * @param <K> the type of the key.
     * @param <V> the type of the value.
     * @return the newly created instance.
     */
    public static <K, V> ArrayListMultimap<K, V> create() {
        return new ArrayListMultimap<>();
    }

    /**
     * Tries to put a value into the sink associated with a given key.
     *
     * @param key   the key of the sink.
     * @param value the value to insert.
     * @return {@code true} if the the element was inserted, otherwise {@code false}.
     */
    @Override
    public boolean put(final K key, final V value) {
        return map.computeIfAbsent(key, k -> new ArrayList<>()).add(value);
    }

    /**
     * Retrieves a collection for a given key.
     *
     * @param key the key that is associated with the collection.
     * @return the collection if already associated with a key otherwise an empty collection.
     */
    @Override
    public Collection<V> get(final K key) {
        return map.get(key);
    }

    /**
     * Consolidates and returns all collections associated by a key.
     */
    @Override
    public Collection<V> values() {
        return map.values().stream().flatMap(Collection::stream).collect(Collectors.toList());
    }

    /**
     * Returns a {@link Multiset} containing each key in the same amount of values a key has.
     */
    @Override
    public Multiset<K> keys() {
        return new ArrayListMultiset<>(map.entrySet()
                .stream()
                .flatMap(this::toKeys)
                .collect(Collectors.toList()));
    }

    private Stream<K> toKeys(Map.Entry<K, ArrayList<V>> entry) {
        return entry.getValue().stream().map(e -> entry.getKey());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ArrayListMultimap<?, ?> that = (ArrayListMultimap<?, ?>) o;
        return Objects.equals(map, that.map);
    }

    @Override
    public int hashCode() {
        return map.hashCode();
    }
}
