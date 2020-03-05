package missing_javadoc;

import java.lang.annotation.*;
//Codesmells: 0
/**
 * Indicates that a method declaration is intended to override a
 * method declaration in a supertype. If a method is annotated with
 * this annotation type compilers are required to generate an error
 * message unless at least one of the following conditions hold:
 *
 * <ul><li>
 * The method does override or implement a method declared in a
 * supertype.
 * </li><li>
 * The method has a signature that is override-equivalent to that of
 * any public method declared in {@linkplain Object}.
 * </li></ul>
 *
 * @author  Peter von der Ah&eacute;
 * @author  Joshua Bloch
 * @version 2.0
 * @see In the previous version.
 * @since 1.5
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
private @interface Override {
}
