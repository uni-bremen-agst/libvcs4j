package missing_javadoc;

//Codesmell: 1, missing javadoc
public class FieldTest {
    //Codesmell: 1, missing javadoc
    public int fieldWithoutJavadoc = 4;

    //Codesmell: 1, missing description
    /**
     *
     */
    private String fieldWithoutDescription;

    //Codesmell: 1, description too short
    /**
     *  description is too short.
     */
    double fieldWithTooShortDescription;

    //Codesmell: 1, unallowed author tag
    /**
     * This field has an unallowed tag, so it thrown a codesmell.
     * @author Krause
     */
    public int fieldWithUnallowedTag;

    //Codesmell: 5, tags too short
    /**
     * In this javadoc are the tag - descriptions too short.
     * @see
     * @since
     * @deprecated is too short
     * @serial is too short.
     * @serialField is too short.
     */
    @Deprecated
    protected int fieldWithTooShortTagDescription;

    //Codesmell: 1, missing @Deprecated Annotation
    /**
     * The missing @Deprecated at the field is the thrown codesmell.
     * @deprecated this field is a deprecated field.
     */
    private char fieldWithMissingAnnotationDeprecated;

    //Codesmell: 1, missing javdoc @deprecated tag
    /**
     * In this javadoc is the @deprecated - tag missing. This is a codesmell.
     */
    @Deprecated
    private char fieldWithMissingTagDeprecated;

    //Codesmell: 0
    /**
     * This javadoc doesn't throw a codesmell.
     * @see this is a sentence, that no codesmell would be thrown for too short tag description.
     * @since 2019-06-18
     * @deprecated this is a sentence, that no codesmell would be thrown for too short tag description.
     * @serial this is a sentence, that no codesmell would be thrown for too short tag description.
     * @serialField this is a sentence, that no codesmell would be thrown for too short tag description.
     */
    @Deprecated
    public String fieldWithNoJavadocCodesmell;
}