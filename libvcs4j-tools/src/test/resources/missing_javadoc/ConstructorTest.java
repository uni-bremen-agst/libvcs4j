package missing_javadoc;

// Codesmell: 1, missing javadoc
public class ConstructorTest {
    /**
     * This field throws two codesmells.
     * The following tags @author und @version are unallowed for this javadocable, so it thrown codesmell's.
     * @author Krause
     * @version 1.0
     */
    public int fieldForTheJavadocableTest = 2;

    // Codesmell: 1, missing javadoc
    public ConstructorTest() {

    }

    // Codesmell: 5, missing description, undocumented deprecated, undocumented param text,
    // undocumented generic param <T> and undocumented IllegalArgumentException.
    /**
     *
     */
    @Deprecated
    private <T> ConstructorTest(String text) {
        if (text == null || text.isEmpty()) {
            throw new IllegalArgumentException("text is null or empty.");
        }
    }

    // Codesmell: 12, too short Short-Description, too short Long-Description,6x too short tag-description,
    // unallowed tag @return, missing Deprecated-Annotation and missing IllegalAccessException in Constructor.
    /**
     *  Too short Short - Description.
     *  Too short Long - Description.
     * @param number too short description.
     * @param <T> generic param T, doesn't exists in this constructor, so it throws a codesmell.
     * @deprecated too short description.
     * @exception IllegalArgumentException is too short.
     * @throws IllegalAccessException is too short.
     * @see too short.
     * @since
     * @return doesn't allowed.
     */
    ConstructorTest(int number) {
        if (number < 0) {
            throw new IllegalArgumentException("number must be bigger or equal than 0.");
        }
    }

    //Codesmells: 0

    /**
     * This is a constructer, which uses a method to check whether the given file is a txt file.
     * This is a sentence, that no codesmell is thrown for a too short long description.
     * @param textFilename is the file, where the extension is checked for txt.
     * @param number is the number of copy's that will be created.
     * @deprecated this is a sentence, that no codesmell would be thrown.
     * @throws IllegalArgumentException if the textFilename is faulty.
     * @exception IllegalStateException if the textFilname isn't a txt file.
     * @see this is a sentence, that no codesmell would be thrown.
     * @since 1.3
     */
    @Deprecated
    protected ConstructorTest(String textFilename, int number) {
        Boolean bool = checkFileExtensionForTxt(textFilename);
        if (bool) {
            // do something with the file.
        }
    }

    //Codesmell: 1, missing javadoc
    private Boolean checkFileExtensionForTxt(String textFilename) {
        if (textFilename == null || textFilename.isEmpty()) {
            throw new IllegalArgumentException("textFilename is faulty");
        }
        String[] parts = textFilename.split(".");

        if (parts[(parts.length - 1)].equals("txt")) {
            return true;
        } else {
            throw new IllegalStateException("the file is not a txt-file!");
        }
        return false;
    }
}