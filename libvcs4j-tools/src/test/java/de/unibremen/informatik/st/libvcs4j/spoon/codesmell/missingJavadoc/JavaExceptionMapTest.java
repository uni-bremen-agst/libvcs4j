package de.unibremen.informatik.st.libvcs4j.spoon.codesmell.missingJavadoc;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class JavaExceptionMapTest {
    @Test
    public void main() {
        assertEquals(
                "java.io.FileNotFoundException",
                JavaExceptionMap.getQualifiedName("FileNotFoundException")
        );
        assertEquals("", JavaExceptionMap.getQualifiedName("NotExistingException"));
    }
}
