package de.unibremen.informatik.st.libvcs4j.spoon.codesmell.missingJavadoc;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class ArrayListMultimapTest {

    @Test
    public void create() {
        final var actual = ArrayListMultimap.create();

        assertNotNull(actual);
    }

    @Test
    public void put() {
        final var given = ArrayListMultimap.create();

        assertTrue(given.put("key1", "item1"));
        assertTrue(given.put("key1", "item1"));
        assertTrue(given.put("key1", "item2"));
    }

    @Test
    public void get() {
        final var given = ArrayListMultimap.create();

        given.put("key1", "item1");
        given.put("key1", "item1");
        given.put("key2", "item2");

        final var expected = new ArrayList<>();
        expected.add("item1");
        expected.add("item1");

        final var actual = given.get("key1");

        assertEquals(expected, actual);
    }

    @Test
    public void values() {
        final var given = ArrayListMultimap.create();

        given.put("key1", "item1");
        given.put("key1", "item1");
        given.put("key2", "item2");

        final var expected = new ArrayList<>();
        expected.add("item1");
        expected.add("item1");
        expected.add("item2");

        final var actual = given.values();

        assertEquals(expected, actual);
    }

    @Test
    public void keys() {
        final var given = ArrayListMultimap.create();

        given.put("key1", "item1");
        given.put("key1", "item1");
        given.put("key2", "item2");

        final var values = List.of("key1", "key1", "key2");
        final var expected = new ArrayListMultiset<>(values);

        final var actual = given.keys();

        assertEquals(expected, actual);
    }
}