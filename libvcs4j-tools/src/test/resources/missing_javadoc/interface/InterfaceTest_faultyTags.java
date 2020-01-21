package missing_javadoc;

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
interface FaultyTagsInterface<T> {public int counter(String text);}