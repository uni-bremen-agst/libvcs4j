package missing_javadoc;

import java.lang.annotation.*;

//Codesmell: 2, to short short-description and long-description.
/**
 * too short description.
 *
 * @author  Peter von der Ah&eacute;
 * @author  Joshua Bloch
 * @since 1.5
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
public @interface Override_shortDescription {
}
