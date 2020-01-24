package de.unibremen.informatik.st.libvcs4j.spoon.codesmell.missingJavadoc;

import java.util.*;
import java.util.function.Consumer;

/**
 * ArrayListMultiset implements {@link Multiset} by utilizing a {@link java.util.ArrayList}.
 *
 * @param <V> the type of the value.
 * @author Ruben Smidt
 */
public class ArrayListMultiset<V> implements Multiset<V> {
    private List<V> values;

    public ArrayListMultiset(final List<V> values) {
        this.values = values;
    }

    @Override
    public int size() {
        return values.size();
    }

    @Override
    public boolean isEmpty() {
        return values.isEmpty();
    }

    @Override
    public boolean contains(final Object o) {
        return values.contains(o);
    }

    @Override
    public Iterator<V> iterator() {
        return values.iterator();
    }

    @Override
    public Object[] toArray() {
        return values.toArray();
    }

    @Override
    public <T> T[] toArray(final T[] a) {
        return values.toArray(a);
    }

    @Override
    public boolean add(final V v) {
        return values.add(v);
    }

    @Override
    public boolean remove(final Object o) {
        return values.remove(o);
    }

    @Override
    public boolean containsAll(final Collection<?> c) {
        return values.containsAll(c);
    }

    @Override
    public boolean addAll(final Collection<? extends V> c) {
        return values.addAll(c);
    }

    @Override
    public boolean removeAll(final Collection<?> c) {
        return values.removeAll(c);
    }

    @Override
    public boolean retainAll(final Collection<?> c) {
        return values.retainAll(c);
    }

    @Override
    public void clear() {
        values.clear();
    }

    @Override
    public void forEach(final Consumer<? super V> action) {
        values.forEach(action);
    }

    @Override
    public Spliterator<V> spliterator() {
        return values.spliterator();
    }

    /**
     * Returns the count of a specific value in the set.
     *
     * @param value the value which occurrences have to be counted.
     */
    @Override
    public int count(final V value) {
        return (int) values.stream().filter(v -> v.equals(value)).count();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ArrayListMultiset<?> that = (ArrayListMultiset<?>) o;
        return Objects.equals(values, that.values);
    }

    @Override
    public int hashCode() {
        return Objects.hash(values);
    }
}
