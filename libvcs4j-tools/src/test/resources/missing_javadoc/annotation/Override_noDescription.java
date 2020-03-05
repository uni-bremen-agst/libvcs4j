package missing_javadoc;

import java.lang.annotation.*;

//Codesmell: 1, to short description.
/**
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
protected @interface Override_noDescription {
}
