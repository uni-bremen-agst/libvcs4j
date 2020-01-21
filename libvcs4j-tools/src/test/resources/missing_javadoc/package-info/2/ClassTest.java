package missing_javadoc.classes;

//Codesmell: 1, missing javadoc
public class NoJavadoc {
    //Codesmell: 3, no description, missing @deprecated und @param <T> tag
    // @author and @version is no codesmell, because it's only required on the top level.
    /**
     *
     */
    @Deprecated
    private class EmptyDescription<T> {
        private void print() {System.out.println("The javadoc description of this class is empty.");}
    }
}

//Codesmell: 3, 2x short description, missing @Deprecated-Annotation.
/**
 * too short short-description.
 * too short long-description.
 * @author Michel Krause
 * @version 1.0
 * @deprecated this enum hasn't a @Deprecated-Annotation.
 */
protected class ShortDescription {
    private void print() {System.out.println("The javadoc description of this class is to short.");}
}

//Codesmell: 8, faulty tags
/**
 * This class has faulty tags.
 * The description of all tag's are to short. And @return is not allowed for enum's.
 * @author Krause
 * @version
 * @see too short
 * @deprecated is too short.
 * @since
 * @param <T> is too short.
 * @serial is too short.
 * @return is not allowed for class's.
 *
 */
@Deprecated
class FaultyTags<T> {
    private void print() {System.out.println("In this javadoc the tag's descriptions are to short.");}
}

//Codesmell: 0
/**
 * This javadoc throws no codesmell.
 * All tag's are allowed and sufficiently labeled too.
 * But their descriptions are not all meaningful.
 * @author Michel Krause
 * @version 1.1
 * @since 1.0
 * @see is only labeled, that no codesmell is thrown for too short description.
 * @param <T> is only labeled, that no codesmell is thrown for too short description.
 * @deprecated is only labeled, that no codesmell is thrown for too short description.
 * @serial is only labeled, that no codesmell is thrown for too short description.
 */
@Deprecated
public class JavadocOkay<T> {
    private void print() {System.out.println("The javadoc of this class is okay and doesn't throw any codesmells.");}
}