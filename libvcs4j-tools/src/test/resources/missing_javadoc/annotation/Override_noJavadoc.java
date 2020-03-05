package missing_javadoc;

import java.lang.annotation.*;

//Codesmell: 1, missing javadoc
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
@interface Override_noJavadoc {
}
