package missing_javadoc;

//Codesmell: 3 (+1 for missing javadoc at method), 2x short description, missing @Deprecated-Annotation.
/**
 * too short short-description.
 * too short long-description.
 * @author Michel Krause
 * @version 1.0
 * @deprecated this enum hasn't a @Deprecated-Annotation.
 */
protected interface ShortDescriptionInterface {public int counter(String text);}