package missing_javadoc;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

//Codesmell: 3
/**
 * too short short-description.
 * This javadoc throws three codesmell's for: too short short-description, missing annotation @Deprecated and missing generic
 * param <T> at the class ReadByCharTest.
 * @deprecated in this class is only to show that no codesmell is thrown, if the tag description long enough.
 * @param <T> don't exists in this class.
 */
public class ReadByCharTest {
    //Codesmell: 2
    /**
     * too short short-description.
     * In this javadoc is the @deprecated - tag missing, this is a codesmell. Also is the short-description too short.
     */
    @Deprecated
    private char fieldWithMissingTagDeprecated;

    //Codesmell 4: 2x to short description and missing @Deprecated-Annotation, also is the tag @deprecated too short.
    /**
     * too short short-description.
     * too short long-description.
     * @author Michel Krause
     * @version 1.0
     * @deprecated too short
     */
    protected enum DescriptionEnum {
        SHORT,LONG,DESCRIPTION
    }

//Codesmell: 5
    /**
     * too short short-description.
     * Codesmell: 5, too short short-description, undocumented deprecated, undocumented param text,
     * undocumented generic param <T> and undocumented IllegalArgumentException.
     */
    @Deprecated
    private <T> ReadByCharTest(String text) {
        if (text == null || text.isEmpty()) {
            throw new IllegalArgumentException("text is null or empty.");
        }
    }

    //Codesmell: 3, short short-description, missing @param file and missing annotation @Deprecated
    /**
     * too short short-description.
     * This javadoc throws 2 codesmell's. One for missing annotation @Deprecated and for too short short-description.
     * The relation is found over the import: java.io.IOException
     * @throws FileNotFoundException this is no codesmell, because FileNotFoundException is a subclass of IOException.
     * @deprecated in this method is only to show that no codesmell is thrown, if the tag description long enough.
     */
    public void fileReaderTest(String file) throws IOException {
        FileReader fr = new FileReader(file);
        int i;
        while((i=fr.read())!=-1)
            System.out.print((char)i);
        fr.close();
    }

    //Codesmell: 4, missing tag @deprecated, too short long-description, 2x exception.
    /**
     * In this javadoc is the long-description too short, missing tag @deprecated and it throws two codesmell's for missing FileNotFoundException in javadoc and wrong IOException.
     * long-description is also too short.
     * @param file is not too short.
     * @throws IOException can't describe the exception FileNotFoundException, because IOException is the superclass of FileNotFoundException and that's not enough.
     */
    @Deprecated
    public void fileReaderTest2(String file) throws FileNotFoundException {
        FileReader fr = new FileReader(file);
        int i;
        while((i=fr.read())!=-1)
            System.out.print((char)i);
        fr.close();
    }

    /**
     * In this javadoc is no codesmell, nothing is too short.
     * And the FileNotFoundException described the Exception, because is the subclass of the subclass (IOException).
     * The relation is found over the import: java.io.FileNotFoundException.
     * @param file is not too short.
     * @throws FileNotFoundException this is no codesmell, because ClassNotFoundException is a subclass of Exception.
     */
    public void fileReaderTest3(String file) throws Exception {
        FileReader fr = new FileReader(file);
        int i;
        while((i=fr.read())!=-1)
            System.out.print((char)i);
        fr.close();
    }

    /**
     * In this javadoc is no codesmell, nothing is too short.
     * And the ClassNotFoundException described the Exception, because is the subclass of the subclass (ReflectiveOperationException).
     * The relation is found via the same package. ClassNotFoundException is in the package java.lang
     * @param qualifiedName is the qualified name of the class you are searching.
     * @return the class if it's founded.
     * @throws ClassNotFoundException this is no codesmell, because ClassNotFoundException is a subclass of Exception.
     * @throws IllegalArgumentException from the constructor ReadByTest.
     */
    public Class classFinderTest(String qualifiedName) throws Exception {
        ReadByCharTest rbc = new ReadByCharTest(qualifiedName);
        return Class.forName(qualifiedName);
    }

    /**
     * In this javadoc is no codesmell, nothing is too short.
     * And the IllegalFormatException described the IllegalArgumentException, because is the subclass of it.
     * The relationship is found via JavaExceptionMap.
     * @param text the text which is being tested.
     * @throws IllegalFormatException this is no codesmell, because IllegalFormatException is a subclass of IllegalArgumentException.
     */
    public void testDocumentedExceptionDescribtSuperException(String text) {
        if (text == null) {
            throw new IllegalArgumentException("text is null");
        }
    }

    /**
     * This method is a test for handle exception.
     * It don't describe the FileNotFoundException from the method fileReaderTest2, because the method handle this exception.
     * @param filepath is the where the file can be found.
     * @throws IllegalArgumentException if the text is null or empty.
     */
    public void handleException(String filepath) {
        if (filepath == null || filepath.isEmpty()) {
            throw new IllegalArgumentException("text can't be a filepath.");
        }
        try {
            fileReaderTest2(filepath);
        } catch (FileNotFoundException e) {
            System.out.println("the file can't be found.");
        }
    }

    /**
     * This method is a test for handle exception.
     * It don't describe the FileNotFoundException from the method fileReaderTest2, because the method handle this exception.
     * @param filepath is the where the file can be found.
     * @throws IllegalArgumentException if the text is null or empty.
     * @throws UnknownFormatConversionException test for the java.util.* test, it should be found over the import.
     */
    public void handleException2(String filepath) {
        if (filepath == null || filepath.isEmpty()) {
            throw new IllegalArgumentException("text can't be a filepath.");
        }
        try {
            fileReaderTest2(filepath);
        } catch (Exception e) {
            System.out.println("the file can't be found.");
        }
    }
}