package de.unibremen.informatik.st.libvcs4j.spoon.codesmell.missingJavadoc;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.function.Consumer;

public class ListMultiset<V> implements Multiset<V> {
    private List<V> values;

    public ListMultiset(final List<V> values) {
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

    @Override
    public int count(final V value) {
        return (int) values.stream().filter(v -> v.equals(value)).count();
    }
}