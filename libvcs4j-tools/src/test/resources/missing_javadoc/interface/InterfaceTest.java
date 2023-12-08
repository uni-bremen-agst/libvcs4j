package missing_javadoc;

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
public interface JavadocOkayInterface<T> {public int counter(String text);}