package missing_javadoc;

//Codesmell: 1, missing javadoc
public enum NoJavadocEnum {
    NO,JAVA,DOC
}

//Codesmell: 4, missing description, @deprecated, @author and @version in javadoc.
/**
 *
 */
@Deprecated
private enum EmptyDescriptionEnum {
    EMPTY, DESCRIPTION
}

//Codesmell 3: 2x to short description and missing @Deprecated-Annotation.
/**
 * too short short-description.
 * too short long-description.
 * @author Michel Krause
 * @version 1.0
 * @deprecated this enum hasn't a @Deprecated-Annotation.
 */
protected enum ShortDescriptionEnum {
    SHORT,LONG,DESCRIPTION
}

//Codesmell: 8, faulty tags
/**
 * This enum has faulty tags.
 * The description of all tag's are to short. And @return is not allowed for enum's.
 * @author Krause
 * @version
 * @see too short
 * @deprecated is too short.
 * @since
 * @serial is too short.
 * @return is not allowed for enum's.
 * @param this tag is not allowed for enum's.
 *
 */
@Deprecated
enum FaultyTagsEnum {
    AUTHOR,VERSION,SEE,DEPRECADED,SINCE,PARAM,SERIAL
}

//Codesmell: 0
/**
 * This javadoc throws no codesmell.
 * All tag's are allowed and sufficiently labeled too.
 * But their descriptions are not all meaningful.
 * @author Michel Krause
 * @version 1.4
 * @since 1.0
 * @see is only labeled, that no codesmell is thrown for too short description.
 * @deprecated is only labeled, that no codesmell is thrown for too short description.
 * @serial is only labeled, that no codesmell is thrown for too short description.
 */
@Deprecated
public enum WeekEnum {
    MONDAY,TUESDAY,WEDNESDAY,THURSDAY,FRIDAY,SATURDAY,SUNDAY
}