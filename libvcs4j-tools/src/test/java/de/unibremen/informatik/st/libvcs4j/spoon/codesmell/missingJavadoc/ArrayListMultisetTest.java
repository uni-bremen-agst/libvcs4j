package de.unibremen.informatik.st.libvcs4j.spoon.codesmell.missingJavadoc;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class ArrayListMultisetTest {

    @Test
    public void count() {
        final var given = new ArrayListMultiset<>(List.of("item1", "item1", "item2", "item3"));

        assertEquals(2, given.count("item1"));
        assertEquals(1, given.count("item2"));
        assertEquals(1, given.count("item3"));
    }
}
