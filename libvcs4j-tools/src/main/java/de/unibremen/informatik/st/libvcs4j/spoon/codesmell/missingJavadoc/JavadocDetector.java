package de.unibremen.informatik.st.libvcs4j.spoon.codesmell.missingJavadoc;

import de.unibremen.informatik.st.libvcs4j.spoon.Environment;
import de.unibremen.informatik.st.libvcs4j.spoon.codesmell.CodeSmell;
import de.unibremen.informatik.st.libvcs4j.spoon.codesmell.CodeSmellDetector;
import de.unibremen.informatik.st.libvcs4j.spoon.codesmell.Thresholds;
import lombok.NonNull;
import spoon.reflect.code.*;
import spoon.reflect.cu.CompilationUnit;
import spoon.reflect.declaration.*;
import spoon.reflect.factory.TypeFactory;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.filter.TypeFilter;

import java.lang.annotation.Annotation;
import java.util.*;

/**
 * The JavadocDetector checks javadoc for its correctness and completeness.
 * It can check the javadoc for classes, enums, interfaces, methods, constructors, fields,
 * annotationTypes and packages.
 *
 * @author Michel Krause
 * @version 1.0
 */
public class JavadocDetector extends CodeSmellDetector {

    /**
     * This final array list contains all the predefined javadoc tags for packages.
     */
    private final ArrayList<CtJavaDocTag.TagType> PACKAGE_TAGS = new ArrayList<>(Arrays.asList(
            CtJavaDocTag.TagType.SEE, CtJavaDocTag.TagType.SINCE, CtJavaDocTag.TagType.SERIAL,
            CtJavaDocTag.TagType.AUTHOR, CtJavaDocTag.TagType.VERSION
    ));
    /**
     * This final array list contains all the predefined javadoc tags for classes and interfaces.
     * The tag @param is only allowed for the generic parameter.
     */
    private final ArrayList<CtJavaDocTag.TagType> TYPE_TAGS = new ArrayList<>(Arrays.asList(
            CtJavaDocTag.TagType.AUTHOR, CtJavaDocTag.TagType.VERSION, CtJavaDocTag.TagType.SEE,
            CtJavaDocTag.TagType.SINCE, CtJavaDocTag.TagType.DEPRECATED, CtJavaDocTag.TagType.SERIAL,
            CtJavaDocTag.TagType.PARAM
    ));
    /**
     * This final array list contains all the predefined javadoc tags for enums.
     * It differs from classes and interfaces in that the tag @param is not allowed.
     */
    private final ArrayList<CtJavaDocTag.TagType> ENUM_TAGS = new ArrayList<>(Arrays.asList(
            CtJavaDocTag.TagType.AUTHOR, CtJavaDocTag.TagType.VERSION, CtJavaDocTag.TagType.SEE,
            CtJavaDocTag.TagType.SINCE, CtJavaDocTag.TagType.DEPRECATED, CtJavaDocTag.TagType.SERIAL
    ));
    /**
     * This final array list contains all the predefined javadoc tags for methods.
     */
    private final ArrayList<CtJavaDocTag.TagType> METHOD_TAGS = new ArrayList<>(Arrays.asList(
            CtJavaDocTag.TagType.PARAM, CtJavaDocTag.TagType.RETURN, CtJavaDocTag.TagType.SEE,
            CtJavaDocTag.TagType.SINCE, CtJavaDocTag.TagType.DEPRECATED, CtJavaDocTag.TagType.THROWS,
            CtJavaDocTag.TagType.EXCEPTION, CtJavaDocTag.TagType.SERIAL_DATA
    ));
    /**
     * This final array list contains all the predefined javadoc tags for constructor.
     * It differs from method in that the tag @return is not allowed.
     */
    private final ArrayList<CtJavaDocTag.TagType> CONSTRUCTOR_TAGS = new ArrayList<>(Arrays.asList(
            CtJavaDocTag.TagType.PARAM, CtJavaDocTag.TagType.SEE, CtJavaDocTag.TagType.SINCE,
            CtJavaDocTag.TagType.DEPRECATED, CtJavaDocTag.TagType.THROWS, CtJavaDocTag.TagType.EXCEPTION
    ));
    /**
     * This final array list contains all the predefined javadoc tags for fields.
     */
    private final ArrayList<CtJavaDocTag.TagType> FIELD_TAGS = new ArrayList<>(Arrays.asList(
            CtJavaDocTag.TagType.SEE, CtJavaDocTag.TagType.SINCE, CtJavaDocTag.TagType.DEPRECATED,
            CtJavaDocTag.TagType.SERIAL, CtJavaDocTag.TagType.SERIAL_FIELD
    ));
    /**
     * This final array list contains all the predefined javadoc tags for annotationTypes.
     */
    private final ArrayList<CtJavaDocTag.TagType> ANNOTATIONTYPE_TAGS = new ArrayList<>(Arrays.asList(
            CtJavaDocTag.TagType.AUTHOR, CtJavaDocTag.TagType.SINCE, CtJavaDocTag.TagType.SEE,
            CtJavaDocTag.TagType.VERSION
    ));
    /**
     * This ArrayList contains all checked packages, so that no package can be double checked.
     */
    ArrayList<CtPackage> checkedPackages = new ArrayList<>();
    /**
     * This list contains all access modifier that are being checked.
     * Note that only javadocables with this access modifier be checked.
     */
    private LinkedHashSet<ModifierKind> accessModifierList = new LinkedHashSet<>();
    /**
     * This list contains all the javadoc-enabled types that are being checked.
     */
    private LinkedHashSet<Javadocable> javadocableList = new LinkedHashSet<>();
    /**
     * readByWord represents the setting for reading by word.
     * if it's true the detector reads by word, if it's false the detector reads by char.
     */
    private Boolean readByWord = true;
    /**
     * Is the setting for whether getters and setters need to be documented.
     */
    private Boolean fieldAccessMustBeDocumented = true;
    /**
     * This value represents the setting if a package needs a package-info.java
     */
    private Boolean packageInfoNeeded = false;
    /**
     * This value represents the setting if a declaring typeless package of the given used package needs an info.
     * It's for the case, that a package has no types. Then if a package has no types the checkPackageInfo - Method
     * can't be called because no types existing for this package.
     */
    private Boolean typelessPackageInfoNeeded = false;
    /**
     * This array is for the length of the classes.
     * The first value is for the short-description length, the second for the long-description and the last
     * for the total-description.
     */
    private int[] minClass = {0, 0, 0};
    /**
     * This array is for the length of the interfaces.
     * Look at the field "minClass" for the values occupancy.
     */
    private int[] minInterface = {0, 0, 0};
    /**
     * This array is for the length of the enums.
     * Look at the field "minClass" for the values occupancy.
     */
    private int[] minEnum = {0, 0, 0};
    /**
     * This array is for the length of the methods.
     * Look at the field "minClass" for the values occupancy.
     */
    private int[] minMethod = {0, 0, 0};
    /**
     * This array is for the length of the field access methods.
     * Look at the field "minClass" for the values occupancy.
     */
    private int[] minMethodFieldAccess = {0, 0, 0};
    /**
     * This array is for the length of the constructors.
     * Look at the field "minClass" for the values occupancy.
     */
    private int[] minConstructor = {0, 0, 0};
    /**
     * This array is for the length of the fields.
     * Look at the field "minClass" for the values occupancy.
     */
    private int[] minField = {0, 0, 0};
    /**
     * This array is for the length of the packages.
     * Look at the field "minClass" for the values occupancy.
     */
    private int[] minPackage = {0, 0, 0};
    /**
     * This array is for the length of the annotationTypes.
     * Look at the field "minClass" for the values occupancy.
     */
    private int[] minAnnotationType = {0, 0, 0};
    /**
     * Setting for the minimum length of the description for the @param tag.
     */
    private int tagParam = 0;
    /**
     * Setting for the minimum length of the description for the @return tag.
     */
    private int tagReturn = 0;
    /**
     * Setting for the minimum length of the description for the @throws and @exception tag.
     */
    private int tagThrows = 0;
    /**
     * Setting for the minimum length of the description for the @author tag.
     */
    private int tagAuthor = 0;
    /**
     * Setting for the minimum length of the description for the @version tag.
     */
    private int tagVersion = 0;
    /**
     * Setting for the minimum length of the description for the @since tag.
     */
    private int tagSince = 0;
    /**
     * Setting for the minimum length of the description for the @see tag.
     */
    private int tagSee = 0;
    /**
     * Setting for the minimum length of the description for the @serial tag.
     */
    private int tagSerial = 0;
    /**
     * Setting for the minimum length of the description for the @serialField tag.
     */
    private int tagSerialField = 0;
    /**
     * Setting for the minimum length of the description for the @serialData tag.
     */
    private int tagSerialData = 0;
    /**
     * Setting for the minimum length of the description for the @deprecated tag.
     */
    private int tagDeprecated = 0;

    /**
     * This is the constructor of the JavadocDetector class.
     * It only calls the constructor of the superclass.
     *
     * @param environment is a stand of the to checked project at a specific time.
     */
    public JavadocDetector(@NonNull Environment environment) {
        super(environment);
    }

    /**
     * visitCtMethod visits every CtMethod in the AST.
     * It examine if the method has javadoc and if it's a field acces.
     * It only throw a code smell if the javadocableList contains methods and if the access modifier of the method
     * is matched with the accessModifierList.
     * And depending on whether field access must be documented, if it's one.
     *
     * @param method is the method to be checked.
     * @param <T>    is a default of spoon. TODO
     */
    @Override
    public <T> void visitCtMethod(CtMethod<T> method) {
        //if ((method.getDocComment() == null || method.getDocComment().isEmpty()) &&
        if (hasNoJavadoc(method) &&
                javadocableList.contains(Javadocable.METHOD) && accessModifierList.contains(method.getVisibility())) {
            if (!isFieldAccess(method) || (isFieldAccess(method) && fieldAccessMustBeDocumented)) {
                addCodeSmell(method, Collections.emptyList(), createSignature(method).orElse(null),
                        "Method " + method.getSignature() + " has no javadoc."
                );
            }
        }
        super.visitCtMethod(method);
    }

    /**
     * Checks if the used package has a package-info.
     * This method only checks for the package-info, if packageInfoNeeded has the value true.
     * It also checks the declaring package of this package for a package-info, when declaringPackageInfoNeeded
     * has the value true.
     * Due to the checkedPackages list, packages can't be double checked.
     * It also filters out the empty package.
     *
     * @param ctPackage is the too checked package for a package-info.
     *                  Also it's the package from which the method get the declaring package.
     * @param owner     is the type where the package was found. addCodeSmell takes his position, because
     *                  a package without package-info has no valid position.
     */
    private void checkPackageForInfo(CtPackage ctPackage, CtType owner) {
        if (!ctPackage.hasPackageInfo() && !checkedPackages.contains(ctPackage)) {
            checkedPackages.add(ctPackage);
            addCodeSmell(owner, Collections.emptyList(), createSignature(ctPackage).orElse(null),
                    "Missing package-info for the package (" + ctPackage.getQualifiedName() + ")."
            );
        }
        if (typelessPackageInfoNeeded) {
            checkDeclaringPackage(ctPackage, owner);
        }
    }

    /**
     * Checks if the declaring package has a package-info.
     * It checks the declaring package of this package for a package-info, when declaringPackageInfoNeeded
     * has the value true.
     * Due to the checkedPackages list, packages can't be double checked.
     * It also filters out the empty package.
     * It throws only a code smell, if the package has no types, because if it has types it were found over
     * the method checkPackageForInfo.
     *
     * @param ctPackage It's the package from which the method get the declaring package.
     * @param element   is the element where the package was found. addCodeSmell takes his position, because
     *                  a package without package-info has no valid position.
     */
    private void checkDeclaringPackage(CtPackage ctPackage, CtElement element) {
        CtPackage declaringPackage = ctPackage.getDeclaringPackage();
        while (declaringPackage != null && !declaringPackage.getQualifiedName().equals("")) {
            if (!declaringPackage.hasPackageInfo() && !checkedPackages.contains(declaringPackage)
                    && declaringPackage.getTypes().isEmpty()) {
                checkedPackages.add(declaringPackage);
                addCodeSmell(element, Collections.emptyList(), createSignature(declaringPackage).orElse(null),
                        "Missing package-info for the typeless package (" + declaringPackage.getQualifiedName() + ")."
                );
            }
            declaringPackage = declaringPackage.getDeclaringPackage();
        }
    }

    /**
     * visitCtAnnotationType visits every CtAnnotationType in the AST.
     * It examine if the annotationType has javadoc.
     * It only throw a codesmell if the javadocableList contains annotationTypes
     * and if the access modifier of the annotationType is matched with the accessModifierList.
     *
     * @param annotationType is the annotationType to be checked.
     * @param <A>            is a default of spoon. TODO
     */
    @Override
    public <A extends Annotation> void visitCtAnnotationType(CtAnnotationType<A> annotationType) {
        //if ((annotationType.getDocComment() == null || annotationType.getDocComment().isEmpty()) &&
        if (hasNoJavadoc(annotationType) &&
                javadocableList.contains(Javadocable.ANNOTATIONTYPE) &&
                accessModifierList.contains(annotationType.getVisibility())) {
            addCodeSmell(annotationType, Collections.emptyList(), createSignature(annotationType).orElse(null),
                    "AnnotationType " + annotationType.getSimpleName() + " has no javadoc."
            );
        }
        if (packageInfoNeeded && javadocableList.contains(Javadocable.PACKAGE)) {
            CtPackage ctPackage = annotationType.getPackage();
            if (ctPackage != null) {
                checkPackageForInfo(ctPackage, annotationType);
            }
        }
        super.visitCtAnnotationType(annotationType);
    }

    /**
     * visitCtClass visits every CtClass in the AST.
     * It examine if the class has javadoc.
     * It only throw a codesmell if the javadocableList contains classes
     * and if the access modifier of the class is matched with the accessModifierList.
     *
     * @param ctClass is the class to be checked.
     * @param <T>     is a default of spoon. TODO
     */
    @Override
    public <T> void visitCtClass(CtClass<T> ctClass) {
        //if ((ctClass.getDocComment() == null || ctClass.getDocComment().isEmpty()) &&
        if (hasNoJavadoc(ctClass) &&
                javadocableList.contains(Javadocable.CLASS) && accessModifierList.contains(ctClass.getVisibility())) {
            addCodeSmell(ctClass, Collections.emptyList(), createSignature(ctClass).orElse(null),
                    "Class " + ctClass.getSimpleName() + " has no javadoc."
            );
        }
        if (packageInfoNeeded && javadocableList.contains(Javadocable.PACKAGE)) {
            CtPackage ctPackage = ctClass.getPackage();
            if (ctPackage != null) {
                checkPackageForInfo(ctPackage, ctClass);
            }
        }
        super.visitCtClass(ctClass);
    }

    /**
     * This method makes a simple signature from the given qualified name.
     * It is needed, because the spoon getSimpleName() - method of constructors only return <init>.
     * It is only needed to be able to say which constructor has no javadoc. And the qualified name is to long,
     * for this text.
     *
     * @param qualifiedName is the qualified name of the constructor.
     * @return the simple name of the constructor with his parameter.
     */
    private String getConstructorNameWithParameter(String qualifiedName) {
        if (qualifiedName != null && qualifiedName.split("\\.").length > 0) {
            String[] parts = qualifiedName.split("\\.");
            return parts[parts.length - 1];
        } else {
            return qualifiedName;
        }
    }

    /**
     * visitCtConstructor visits every CtConstructor in the AST.
     * It examine if the constructor has javadoc.
     * It only throw a codesmell if the javadocableList contains constructors
     * and if the access modifier of the constructor is matched with the accessModifierList.
     *
     * @param constructor is the constructor to be checked.
     * @param <T>         is a default of spoon. TODO
     */
    @Override
    public <T> void visitCtConstructor(CtConstructor<T> constructor) {
        //if ((constructor.getDocComment() == null || constructor.getDocComment().isEmpty()) &&
        if (hasNoJavadoc(constructor) &&
                javadocableList.contains(Javadocable.CONSTRUCTOR)
                && accessModifierList.contains(constructor.getVisibility())) {
            addCodeSmell(constructor, Collections.emptyList(), createSignature(constructor).orElse(null),
                    "Constructor " + getConstructorNameWithParameter(constructor.getSignature()) +
                            " has no javadoc."
            );
        }
        super.visitCtConstructor(constructor);
    }

    /**
     * visitCtEnum visits every CtEnum in the AST.
     * It examine if the enum has javadoc.
     * It only throw a codesmell if the javadocableList contains enums
     * and if the access modifier of the enum is matched with the accessModifierList.
     *
     * @param ctEnum is the enum to be checked.
     * @param <T>    is a default of spoon. TODO
     */
    @Override
    public <T extends Enum<?>> void visitCtEnum(CtEnum<T> ctEnum) {
        //if ((ctEnum.getDocComment() == null || ctEnum.getDocComment().isEmpty()) &&
        if (hasNoJavadoc(ctEnum) &&
                javadocableList.contains(Javadocable.ENUM) && accessModifierList.contains(ctEnum.getVisibility())) {
            addCodeSmell(ctEnum, Collections.emptyList(), createSignature(ctEnum).orElse(null),
                    "Enum " + ctEnum.getSimpleName() + " has no javadoc."
            );
        }
        if (packageInfoNeeded && javadocableList.contains(Javadocable.PACKAGE)) {
            CtPackage ctPackage = ctEnum.getPackage();
            if (ctPackage != null) {
                checkPackageForInfo(ctPackage, ctEnum);
            }
        }
        super.visitCtEnum(ctEnum);
    }

    /**
     * visitCtField visits every CtField in the AST.
     * It examine if the field has javadoc.
     * It only throw a codesmell if the javadocableList contains fields
     * and if the access modifier of the field is matched with the accessModifierList.
     *
     * @param field is the field to be checked.
     * @param <T>   is a default of spoon. TODO
     */
    @Override
    public <T> void visitCtField(CtField<T> field) {
        //if ((field.getDocComment() == null || field.getDocComment().isEmpty()) &&
        if (hasNoJavadoc(field) &&
                javadocableList.contains(Javadocable.FIELD) && accessModifierList.contains(field.getVisibility())) {
            addCodeSmell(field, Collections.emptyList(), createSignature(field).orElse(null),
                    "Field " + field.getSimpleName() + " has no javadoc."
            );
        }
        super.visitCtField(field);
    }

    /**
     * visitCtInterface visits every CtInterface in the AST.
     * It examine if the interface has javadoc.
     * It only throw a codesmell if the javadocableList contains interfaces
     * and if the access modifier of the interface is matched with the accessModifierList.
     *
     * @param ctInterface is the interface to be checked.
     * @param <T>         is a default of spoon. TODO
     */
    @Override
    public <T> void visitCtInterface(CtInterface<T> ctInterface) {
        //if ((ctInterface.getDocComment() == null || ctInterface.getDocComment().isEmpty()) &&
        if (hasNoJavadoc(ctInterface) &&
                javadocableList.contains(Javadocable.INTERFACE)
                && accessModifierList.contains(ctInterface.getVisibility())) {
            addCodeSmell(ctInterface, Collections.emptyList(), createSignature(ctInterface).orElse(null),
                    "Interface " + ctInterface.getSimpleName() + " has no javadoc."
            );
        }
        if (packageInfoNeeded && javadocableList.contains(Javadocable.PACKAGE)) {
            CtPackage ctPackage = ctInterface.getPackage();
            if (ctPackage != null) {
                checkPackageForInfo(ctPackage, ctInterface);
            }
        }
        super.visitCtInterface(ctInterface);
    }

    /**
     * visitCtPackage visits every CtPackage in the AST.
     * It examine if the package has javadoc.
     * It only throw a codesmell if the javadocableList contains package.
     *
     * @param ctPackage is the package to be checked.
     */
    @Override
    public void visitCtPackage(CtPackage ctPackage) {
        //if ((ctPackage.getDocComment() == null || ctPackage.getDocComment().isEmpty()) &&
        if (hasNoJavadoc(ctPackage) &&
                javadocableList.contains(Javadocable.PACKAGE)) {
            addCodeSmell(ctPackage, Collections.emptyList(), createSignature(ctPackage).orElse(null),
                    "Package " + ctPackage.getSimpleName() + " has no javadoc."
            );
        }
        if (typelessPackageInfoNeeded && javadocableList.contains(Javadocable.PACKAGE)
                && ctPackage.getPosition().isValidPosition()) {
            checkDeclaringPackage(ctPackage, ctPackage);
        }
        super.visitCtPackage(ctPackage);
    }

    /**
     * Returns whether the given element has no Javadoc comment.
     * @param element the element to be checked.
     * @return if {@code element} has no Javadoc comment.
     */
    private boolean hasNoJavadoc(final CtElement element) {
        if (element == null) return true;
        final List<CtComment> comments = element.getComments();
        if (comments == null) return true;
        return comments.stream()
                .filter(Objects::nonNull)
                .noneMatch(c -> c instanceof CtJavaDoc);
    }

    /**
     * visitCtJavaDoc visits every CtJavaDoc in the AST.
     * A CtJavaDoc is a simple javadoc.
     * This method is a distribution method.
     * It only passes on if the javadocableList contains the owner of the javadoc.
     * For methods is again relevant, whether is it a field access and whether they should be examined.
     *
     * @param javadoc is the javadoc to be checked.
     */
    @Override
    public void visitCtJavaDoc(CtJavaDoc javadoc) {
        if (javadoc.getParent() instanceof CtMethod && javadocableList.contains(Javadocable.METHOD)) {
            CtMethod method = (CtMethod) javadoc.getParent();
            if (!isFieldAccess(method) || (isFieldAccess(method) && fieldAccessMustBeDocumented)) {
                executableJavadoc(javadoc, (CtMethod) javadoc.getParent(),
                        ((CtMethod) javadoc.getParent()).getVisibility()
                );
            }
        }
        if (javadoc.getParent() instanceof CtClass) {
            CtClass ctClass = (CtClass) javadoc.getParent();
            if ((ctClass.isClass() && javadocableList.contains(Javadocable.CLASS)) ||
                    (ctClass.isEnum() && javadocableList.contains((Javadocable.ENUM)))) {
                typeJavadoc(javadoc, ctClass);
            }
        }
        if (javadoc.getParent() instanceof CtInterface && javadocableList.contains(Javadocable.INTERFACE)) {
            typeJavadoc(javadoc, (CtInterface) javadoc.getParent());
        }
        if (javadoc.getParent() instanceof CtConstructor && javadocableList.contains(Javadocable.CONSTRUCTOR)) {
            executableJavadoc(javadoc, (CtConstructor) javadoc.getParent(),
                    ((CtConstructor) javadoc.getParent()).getVisibility()
            );
        }
        if (javadoc.getParent() instanceof CtField && javadocableList.contains(Javadocable.FIELD)) {
            fieldJavadoc(javadoc, (CtField) javadoc.getParent());
        }
        if (javadoc.getParent() instanceof CtPackage && javadocableList.contains(Javadocable.PACKAGE)) {
            packageJavadoc(javadoc, (CtPackage) javadoc.getParent());
        }
        if (javadoc.getParent() instanceof CtAnnotationType && javadocableList.contains(Javadocable.ANNOTATIONTYPE)) {
            annotationTypeJavadoc(javadoc, (CtAnnotationType) javadoc.getParent());
        }
        super.visitCtJavaDoc(javadoc);
    }

    /**
     * In this method, the javadoc of an executable is examined.
     * At first it checks if the access modifier of the executable is matched with the accessModifierList.
     * It checks if all the given javadoc - tags are allowed for this executable. (look at METHOD_TAGS and CONSTRUCTOR_TAGS)
     * Also it will be checked if everything is okay with the @return, @throws/@exception, @deprecated and with
     * the description length of all the given allowed javadoc tags.
     * Also @param and the lengths of the normal description are checked, depending on the type of executables. (Method or Constructor)
     *
     * @param javadoc    is the javadoc of the executable.
     * @param executable is the owner of the javadoc.
     * @param visibility is the access modifier of the executable.
     */
    private void executableJavadoc(CtJavaDoc javadoc, CtExecutable executable, ModifierKind visibility) {
        if (accessModifierList.contains(visibility)) {
            ArrayList<CtJavaDocTag.TagType> executableTags = new ArrayList<>();
            ArrayList<CtJavaDocTag> allowedDocumentedTags = checkAllowedTags(
                    javadoc,
                    executable instanceof CtMethod ? METHOD_TAGS : CONSTRUCTOR_TAGS
            );
            allowedDocumentedTags.forEach((t) -> executableTags.add(t.getType()));

            checkExceptions(executable, javadoc);
            checkDeprecated(executable, executableTags);
            checkTagLength(javadoc, allowedDocumentedTags);

            if (executable instanceof CtMethod) {
                checkReturn((CtMethod) executable, executableTags);
                checkParams((CtMethod) executable, javadoc);
                if (!isFieldAccess((CtMethod) executable)) {
                    checkDescriptionLength(javadoc, minMethod[0], minMethod[1], minMethod[2]);
                } else {
                    if (minMethodFieldAccess[0] == 0 && minMethodFieldAccess[1] == 0 && minMethodFieldAccess[2] == 0) {
                        checkDescriptionLength(javadoc, minMethod[0], minMethod[1], minMethod[2]);
                    } else {
                        checkDescriptionLength(
                                javadoc,
                                minMethodFieldAccess[0],
                                minMethodFieldAccess[1],
                                minMethodFieldAccess[2]
                        );
                    }
                }
            } else {
                checkParams((CtConstructor) executable, javadoc);
                checkDescriptionLength(javadoc, minConstructor[0], minConstructor[1], minConstructor[2]);
            }
        }
    }

    /**
     * In this method, the javadoc of an type (Class, Interface, or Enum) is examined.
     * At first it checks if the access modifier of the type is matched with the accessModifierList.
     * It checks if all the given javadoc - tags are allowed for this type. (look at TYPE_TAGS and ENUM_TAGS)
     * Also it will be checked if everything is okay with the @author, @version, @deprecated and with the
     * description length of all the given allowed javadoc tags.
     * The tag @param will only checked if the type is a class or an interface.
     * The lengths of the normal description are checked, depending of the type. (Class, Interface, Enum)
     *
     * @param javadoc is the javadoc of the type.
     * @param type    is the owner of the javadoc.
     */
    private void typeJavadoc(CtJavaDoc javadoc, CtType type) {
        if (accessModifierList.contains(type.getVisibility())) {
            ArrayList<CtJavaDocTag.TagType> typeTags = new ArrayList<>();
            ArrayList<CtJavaDocTag> allowedDocumentedTags = checkAllowedTags(
                    javadoc,
                    type instanceof CtEnum ? ENUM_TAGS : TYPE_TAGS
            );
            allowedDocumentedTags.forEach((t) -> typeTags.add(t.getType()));

            if (!typeTags.contains(CtJavaDocTag.TagType.AUTHOR) && tagAuthor > 0 && type.getParent(CtType.class) == null) {
                addCodeSmell(type, Collections.emptyList(), createSignature(type).orElse(null),
                        "Missing tag @author in the javadoc."
                );
            }
            if (!typeTags.contains(CtJavaDocTag.TagType.VERSION) && tagVersion > 0 && type.getParent(CtType.class) == null) {
                addCodeSmell(type, Collections.emptyList(), createSignature(type).orElse(null),
                        "Missing tag @version in the javadoc."
                );
            }

            if (!(type instanceof CtEnum)) {
                checkParams(type, javadoc);
            }
            checkDeprecated(type, typeTags);

            if (type instanceof CtClass && !(type instanceof CtEnum)) {
                checkDescriptionLength(javadoc, minClass[0], minClass[1], minClass[2]);
            } else if (type instanceof CtEnum) {
                checkDescriptionLength(javadoc, minEnum[0], minEnum[1], minEnum[2]);
            } else {
                checkDescriptionLength(javadoc, minInterface[0], minInterface[1], minInterface[2]);
            }
            checkTagLength(javadoc, allowedDocumentedTags);
        }
    }

    /**
     * In this method, the javadoc of an field is examined.
     * At first it checks if the access modifier of the field is matched with the accessModifierList.
     * It checks if all the given javadoc - tags are allowed for this field. (look at FIELD_TAGS)
     * Also it will be checked if everything is okay with the @deprecated and with the description length of
     * all the given allowed javadoc tags.
     * The lengths of the normal description are checked.
     *
     * @param javadoc is the javadoc of the field.
     * @param field   is the owner of the javadoc.
     */
    private void fieldJavadoc(CtJavaDoc javadoc, CtField field) {
        if (accessModifierList.contains(field.getVisibility())) {
            ArrayList<CtJavaDocTag.TagType> fieldTags = new ArrayList<>();
            ArrayList<CtJavaDocTag> allowedDocumentedTags = checkAllowedTags(javadoc, FIELD_TAGS);
            allowedDocumentedTags.forEach((t) -> fieldTags.add(t.getType()));

            checkDeprecated(field, fieldTags);
            checkDescriptionLength(javadoc, minField[0], minField[1], minField[2]);
            checkTagLength(javadoc, allowedDocumentedTags);
        }
    }

    /**
     * In this method, the javadoc of an package is examined.
     * It checks if all the given javadoc - tags are allowed for this package. (look at PACKAGE_TAGS)
     * Also it checks if everything is okay with the length of the tags and the normal description.
     *
     * @param javadoc   is the javadoc of the package.
     * @param ctPackage is the owner of the javadoc.
     */
    private void packageJavadoc(CtJavaDoc javadoc, CtPackage ctPackage) {
        ArrayList<CtJavaDocTag> allowDocumentedTags = checkAllowedTags(javadoc, PACKAGE_TAGS);
        checkDescriptionLength(javadoc, minPackage[0], minPackage[1], minPackage[2]);
        checkTagLength(javadoc, allowDocumentedTags);
    }

    /**
     * In this method, the javadoc of an package is examined.
     * At first it checks if the access modifier of the annotation is matched with the accessModifierList.
     * It checks if all the given javadoc - tags are allowed for this annotation. (look at ANNOTATION_TAGS)
     * Also it checks if everything is okay with the length of the tags and the normal description.
     *
     * @param javadoc    is the javadoc of the annotation.
     * @param annotation is the owner of the javadoc.
     */
    private void annotationTypeJavadoc(CtJavaDoc javadoc, CtAnnotationType annotation) {
        if (accessModifierList.contains(annotation.getVisibility())) {
            ArrayList<CtJavaDocTag> allowedDocumentedTags = checkAllowedTags(javadoc, ANNOTATIONTYPE_TAGS);
            checkDescriptionLength(javadoc, minAnnotationType[0], minAnnotationType[1], minAnnotationType[2]);
            checkTagLength(javadoc, allowedDocumentedTags);
        }
    }

    /**
     * In this method the given javadoc will be checked if every tag in it are allowed.
     * If the inspection of a tag don't get a match with the allowedJavadocableTags-List it throws a codesmell, that
     * this tag is not allowed for this javadocable.
     * Otherwise the tag will be saved as a allowed tag.
     *
     * @param javadoc                is the javadoc with the javadoc tag that will be checked.
     * @param allowedJavadocableTags is the list in that say's which tag is allowed for this javadocable.
     * @return all the allowed tags for this javadocable of this javadoc.
     */
    private ArrayList<CtJavaDocTag> checkAllowedTags(
            CtJavaDoc javadoc,
            ArrayList<CtJavaDocTag.TagType> allowedJavadocableTags
    ) {
        ArrayList<CtJavaDocTag> allowedDocumentedTags = new ArrayList<>();
        for (CtJavaDocTag tag : javadoc.getTags()) {
            if (!allowedJavadocableTags.contains(tag.getType())) {
                addCodeSmell(javadoc.getParent(), Collections.emptyList(),
                        createSignature(javadoc.getParent()).orElse(null),
                        "Tag " + tag.getType() + " is not allowed for this javadocable."
                );
            } else {
                allowedDocumentedTags.add(tag);
            }
        }
        return allowedDocumentedTags;
    }

    /**
     * This method checks the deprecated requirement.
     * It only checks deprecated if the expected minimum length has been set.
     * Also it checks if the element has the annotation @Deprecated, if the javadoc contains the matching
     * javadoc tag @deprecated.
     * If the javadoc contains the tag @deprecated it will check if the element has the matching annotation @Deprecated.
     * It throws a code smell if one of them has deprecated and the other one not.
     *
     * @param element is the owner of the javadoc.
     * @param tagList contains all the javadoc tags of the javadoc.
     */
    private void checkDeprecated(CtElement element, ArrayList<CtJavaDocTag.TagType> tagList) {
        if (tagDeprecated > 0) {
            if (tagList.contains(CtJavaDocTag.TagType.DEPRECATED) && (element.getAnnotation(Deprecated.class) == null)) {
                addCodeSmell(element, Collections.emptyList(), createSignature(element).orElse(null),
                        "Javadoc contains @deprecated, but the annotation @Deprecated at the javadocable is missing."
                );
            }
            if (!tagList.contains(CtJavaDocTag.TagType.DEPRECATED) && element.getAnnotation(Deprecated.class) != null) {
                addCodeSmell(element, Collections.emptyList(), createSignature(element).orElse(null),
                        "Missing tag @deprecated in the javadoc."
                );
            }
        }
    }

    /**
     * This method checks the return requirement.
     * checkReturn checks only if the expected minimum length of return has been set.
     * If the javadoc contains the tag @return, it will be checked if the method has a return value.
     * And vice versa.
     * If one of them not be passed it throws a code smell.
     *
     * @param method  is the method where the return is checked.
     * @param tagList contains all the javadoc tags of the javadoc.
     */
    private void checkReturn(CtMethod method, ArrayList<CtJavaDocTag.TagType> tagList) {
        if (tagReturn > 0) {
            if (!tagList.contains(CtJavaDocTag.TagType.RETURN) && !method.getType().getSimpleName().equals("void")) {
                addCodeSmell(method, Collections.emptyList(), createSignature(method).orElse(null),
                        "Missing tag @return in the javadoc."
                );
            }
            if (tagList.contains(CtJavaDocTag.TagType.RETURN) && method.getType().getSimpleName().equals("void")) {
                addCodeSmell(method, Collections.emptyList(), createSignature(method).orElse(null),
                        "Javadoc contains @return, but the method has no return value."
                );
            }
        }
    }

    /**
     * This method checks the param requirement.
     * It checks if every occurring parameter are described in the javadoc.
     * Also it will be checked that no not occurring parameter is described.
     * It throws a code smell if any parameter is not described or an not existing parameter is described.
     * But for this the minimum length of the tag param must be set.
     * It also works for generic parameters.
     *
     * @param element is the javadocable in which the parameters are checked.
     * @param javadoc is the javadoc where the parameters are described.
     */
    private void checkParams(CtFormalTypeDeclarer element, CtJavaDoc javadoc) {
        if (tagParam > 0) {
            ArrayList<String> elementParams = new ArrayList<>();
            if (element instanceof CtExecutable) {
                List<CtParameter<?>> params = ((CtExecutable) element).getParameters();
                for (CtParameter p : params) {
                    elementParams.add(p.getSimpleName());
                }
            }

            List<CtTypeParameter> typeParams = element.getFormalCtTypeParameters();
            for (CtTypeParameter param : typeParams) {
                elementParams.add("<" + param.getSimpleName() + ">");
            }

            ArrayList<String> executableDocumentedParams = new ArrayList<>();
            for (CtJavaDocTag tag : javadoc.getTags()) {
                if (tag.getType().equals(CtJavaDocTag.TagType.PARAM)) {
                    executableDocumentedParams.add(tag.getParam());
                }
            }

            for (String s : elementParams) {
                if (!executableDocumentedParams.contains(s)) {
                    addCodeSmell(element, Collections.emptyList(),
                            createSignature(element).orElse(null),
                            "Missing tag @param " + s + " in the javadoc."
                    );
                }
            }

            for (String s : executableDocumentedParams) {
                if (!elementParams.contains(s)) {
                    addCodeSmell(element, Collections.emptyList(),
                            createSignature(element).orElse(null),
                            "Javadoc contains @param " + s + ", but this parameter does not exists."
                    );
                }
            }
        }
    }

    /**
     * This method cuts the qualified package name from a import.
     * For example ctImport is: "import static java.util.*;"
     * Then the method returns "java.util"
     *
     * @param ctImport is an import with CtImportKind unequal CtImportKind.TYPE
     * @return the extracted package of this import.
     */
    private String extractImportPackage(CtImport ctImport) {
        String extract = ctImport.toString();
        extract = extract.substring(extract.lastIndexOf(" ") + 1).replace("*", " ").replace(";", " ").trim();
        if (extract.charAt(extract.length() - 1) == '.') {
            extract = extract.substring(0, extract.length() - 1);
        }
        return extract;
    }

    /**
     * The task of this method is to get the imports of the type.
     *
     * @param type is the type where the imports are declared.
     * @return a list with all declared imports.
     */
    private List<CtImport> getImports(CtType type) {
        CompilationUnit cu = type.getFactory().CompilationUnit().getOrCreate(type);
        return cu.getImports();
    }

    /**
     * The function of this method is to check the exceptions.
     * It only checks if the minimum length of the exception has been set.
     * It checks if the unhandle exceptions are described in the javadoc, and that
     * all described exceptions from the javadoc can be thrown from the executable.
     *
     * @param executable is the element where the exceptions to be thrown.
     * @param javadoc    is the javadoc where the exceptions are described.
     */
    private void checkExceptions(CtExecutable executable, CtJavaDoc javadoc) {
        if (tagThrows > 0) {
            List<CtImport> imports = getImports(executable.getParent(CtType.class));
            List<CtThrow> executableThrows = executable.getElements(new TypeFilter<>(CtThrow.class));

            LinkedHashSet<CtTypeReference> executableExceptions = new LinkedHashSet();
            if (executableThrows != null && !executableThrows.isEmpty()) {
                for (CtThrow exception : executableThrows) {
                    if (exception.getThrownExpression().getType() != null) {
                        executableExceptions.add(exception.getThrownExpression().getType());
                    }
                }
            }
            executableExceptions.addAll(executable.getThrownTypes());

            ArrayList<String> executableDocumentedExceptions = new ArrayList<>();
            for (CtJavaDocTag tag : javadoc.getTags()) {
                if (tag.getType().equals(CtJavaDocTag.TagType.THROWS) ||
                        tag.getType().equals(CtJavaDocTag.TagType.EXCEPTION)) {
                    executableDocumentedExceptions.add(tag.getParam());
                }
            }

            ArrayList<CtExecutable> visitedInvocations = new ArrayList<>();
            visitedInvocations.add(executable);
            ArrayList<CtTypeReference> catches = new ArrayList<>();
            executable.getElements(new TypeFilter<>(CtCatch.class)).forEach(
                    c -> catches.add(c.getParameter().getType()));
            executableExceptions.addAll(
                    checkHandleException(catches, checkCalledExceptions(executable, visitedInvocations)));
            for (CtTypeReference ref : executableExceptions) {
                if (ref != null) {
                    if (!executableDocumentedExceptions.contains(ref.getSimpleName())) {
                        if (!isSuperExceptionClass(executableDocumentedExceptions, ref, imports)) {
                            addCodeSmell(executable, Collections.emptyList(), createSignature(executable).orElse(null),
                                    "Missing tag @throws " + ref.getSimpleName() + " in the javadoc."
                            );
                        }
                    }
                }
            }

            ArrayList<String> exceptionNames = new ArrayList<>();
            for (CtTypeReference ref : executableExceptions) {
                if (ref != null) {
                    exceptionNames.add(ref.getSimpleName());
                }
            }
            for (String s : executableDocumentedExceptions) {
                if (!exceptionNames.contains(s)) {
                    if (!isSubExceptionClass(s, executableExceptions, imports)) {
                        addCodeSmell(executable, Collections.emptyList(), createSignature(executable).orElse(null),
                                "Javadoc contains @throws/@exception " + s + ", but this does not thrown by " +
                                        "this or a called executable."
                        );
                    }
                }
            }
        }
    }

    /**
     * This method checks if a exception was handled.
     * It also check for the inherit exceptions that was handled by a try - catch - block.
     *
     * @param catches             contains all exceptions that this executable handles.
     * @param invoctionExceptions contains all exception that can be thrown by call other executables.
     * @return a list with all unhandled exceptions that can be thrown by the executable.
     */
    private ArrayList<CtTypeReference> checkHandleException(
            ArrayList<CtTypeReference> catches,
            ArrayList<CtTypeReference> invoctionExceptions
    ) {
        ArrayList<CtTypeReference> invocWithoutHandleException = new ArrayList<>(invoctionExceptions);
        for (CtTypeReference exception : invoctionExceptions) {
            for (CtTypeReference cat : catches) {
                if (cat != null && exception != null) {
                    if (exception.equals(cat) || exception.isSubtypeOf(cat)) {
                        invocWithoutHandleException.remove(exception);
                    }
                }
            }
        }
        return invocWithoutHandleException;
    }

    /**
     * This method gets all the called executables by the element.
     * And get the thrown unhandled exceptions from them. Also it get the declare exception in the executable signature.
     *
     * @param element            is the actual handled element, that will be checked for recursive exceptions.
     * @param visitedInvocations is a list with all the visited executables, to prevent a loop.
     * @return
     */
    private ArrayList<CtTypeReference> checkCalledExceptions(
            CtElement element,
            ArrayList<CtExecutable> visitedInvocations
    ) {
        ArrayList<CtInvocation> calledInvocations = (ArrayList<CtInvocation>) element.getElements(new TypeFilter<>(
                CtInvocation.class));
        ArrayList<CtTypeReference> calledException = new ArrayList<>();

        for (CtInvocation invocation : calledInvocations) {
            if (invocation.getExecutable().getDeclaration() != null) {
                if (!visitedInvocations.contains(invocation.getExecutable().getDeclaration())) {
                    visitedInvocations.add(invocation.getExecutable().getDeclaration());
                    ArrayList<CtThrow> calledExceptions = (ArrayList<CtThrow>) invocation.getExecutable()
                            .getDeclaration()
                            .getElements(new TypeFilter<>(
                                    CtThrow.class));
                    for (CtThrow exception : calledExceptions) {
                        calledException.add(exception.getThrownExpression().getType());
                    }
                    calledException.addAll(invocation.getExecutable().getDeclaration().getThrownTypes());
                    ArrayList<CtTypeReference> catches = new ArrayList<>();
                    invocation.getElements(new TypeFilter<>(CtCatch.class))
                            .forEach(c -> catches.add(c.getParameter().getType()));
                    calledException.addAll(checkHandleException(
                            catches,
                            checkCalledExceptions(invocation.getExecutable().getDeclaration(), visitedInvocations)
                            )
                    );
                }
            }
        }
        return calledException;
    }

    /**
     * This method checks if any documented exceptions is a sub-exception of the given exception,
     * so that this described the exception.
     * At first it try to find the qualified name of the documented exception over the imports.
     * After that it try's to find the qualified name in the package of the given exception.
     * And at least it will be try to find the qualified name with the JavaExceptionMap.
     *
     * @param documentedExceptions contains all described exceptions in the javadoc.
     * @param exception            is the exception, where no direct match with a description by a simple name can be found.
     * @param imports              are the imports from the parent type of the given executable.
     * @return true if a relationship was found, false if not.
     */
    private Boolean isSuperExceptionClass(
            List<String> documentedExceptions,
            CtTypeReference exception,
            List<CtImport> imports
    ) {
        TypeFactory typeFactory = new TypeFactory();
        for (String s : documentedExceptions) {
            for (CtImport i : imports) {
                if (i.getImportKind().equals(CtImportKind.TYPE)) {
                    if (s.equals(i.getReference().getSimpleName())) {
                        try {
                            if (typeFactory.get(Class.forName(i.getReference().toString())).isSubtypeOf(exception)) {
                                System.out.println("INFO: Missing description of " + exception +
                                        " was described by " + s + ".");
                                return true;
                            }
                        } catch (Exception e0) {
                        }
                    }
                } else {
                    try {
                        if (typeFactory.get(Class.forName(extractImportPackage(i) + "." + s)).isSubtypeOf(exception)) {
                            System.out.println("INFO: Missing description of " + exception +
                                    " was described by " + s + ".");
                            return true;
                        }
                    } catch (Exception e0) {
                    }
                }
            }
            try {
                if (typeFactory.get(Class.forName(exception.getPackage().getQualifiedName() + "." + s))
                        .isSubtypeOf(exception)) {
                    System.out.println("INFO: Missing description of " + exception + " was described by " + s + ".");
                    return true;
                }
            } catch (Exception e1) {
            }
            try {
                String qualifiedName = JavaExceptionMap.getQualifiedName(s);
                if (typeFactory.get(Class.forName(qualifiedName)).isSubtypeOf(exception)) {
                    System.out.println("INFO: Missing description of " + exception + " was described by " + s + ".");
                    return true;
                }
            } catch (Exception e2) {
            }
        }
        return false;
    }

    /**
     * This method checks if any exception is the superclass-exception of the searched documented exception.
     * At first it try to find the qualified name of the documented exception over the imports.
     * After that it try's to find the qualified name in the package of the given exception.
     * And at least it will be try to find the qualified name with the JavaExceptioptionMap
     *
     * @param documentedException is the described exception, where no direct match can be found by simple name.
     * @param exceptions          are all exceptions that can be thrown by the given executable
     * @param imports             are the imports from the parent type of the given executable.
     * @return true if a relationship was found, false if not.
     */
    private Boolean isSubExceptionClass(
            String documentedException,
            LinkedHashSet<CtTypeReference> exceptions,
            List<CtImport> imports
    ) {
        TypeFactory typeFactory = new TypeFactory();

        for (CtTypeReference exception : exceptions) {
            for (CtImport i : imports) {
                if (i.getImportKind().equals(CtImportKind.TYPE)) {
                    if (documentedException.equals(i.getReference().getSimpleName())) {
                        try {
                            if (typeFactory.get(Class.forName(i.getReference().toString())).isSubtypeOf(exception)) {
                                System.out.println("INFO: " + documentedException + " describes " + exception + ", found the relation over the imports");
                                return true;
                            }
                        } catch (Exception e0) {
                        }
                    }
                } else {
                    try {
                        if (typeFactory.get(Class.forName(extractImportPackage(i) + "." + documentedException))
                                .isSubtypeOf(exception)) {
                            System.out.println("INFO: " + documentedException + " describes " + exception + ", found the relation over the imports");
                            return true;
                        }
                    } catch (Exception e0) {
                    }
                }
            }
            try {
                if (typeFactory.get(Class.forName(exception.getPackage()
                        .getQualifiedName() + "." + documentedException)).isSubtypeOf(exception)) {
                    System.out.println("INFO: " + documentedException + " describes " + exception + ", found the relation over the same package.");
                    return true;
                }
            } catch (Exception e) {
            }
            try {
                String qualifiedName = JavaExceptionMap.getQualifiedName(documentedException);
                if (typeFactory.get(Class.forName(qualifiedName)).isSubtypeOf(exception)) {
                    System.out.println("INFO: " + documentedException + " describes " + exception + ", found the relation with JavaExceptionMap.");
                    return true;
                }
            } catch (Exception e2) {
            }
        }
        return false;
    }

    /**
     * The procedure of this method depends on the selection of readByWord.
     * If word-wise reading was selected, then it split the text by whitespaces and count a word if it's
     * consisting of a-Z or 0-9.
     * If character-wise reading was selected, the method returns the length of the text.
     *
     * @param text is the text that should be counted.
     * @return the counted words / character.
     */
    private int counter(String text) {
        int count = 0;
        if (text == null || text.isEmpty()) {
            return 0;
        }
        if (readByWord) {
            text = text.replace("-", " ");
            String[] words = text.split("\\s+");
            for (String word : words) {
                if (word.matches(".*[a-zA-Z0-9].*")) {
                    count++;
                }
            }
        } else {
            count = text.length();
        }
        return count;
    }

    /**
     * Checks the length from the short description, long description and total description.
     * If the length do not match the specifications it will be throw a code smell.
     *
     * @param javadoc                is the too checked javadoc.
     * @param shortDescriptionLength is the length of the short description.
     * @param longDescriptionLength  is the length of the long description.
     * @param totalDescriptionLength is the length of the total description.
     */
    private void checkDescriptionLength(
            CtJavaDoc javadoc,
            int shortDescriptionLength,
            int longDescriptionLength,
            int totalDescriptionLength
    ) {
        if (javadoc.getContent()
                .isEmpty() && (shortDescriptionLength > 0 || longDescriptionLength > 0 || totalDescriptionLength > 0)) {
            addCodeSmell(javadoc.getParent(), Collections.emptyList(),
                    createSignature(javadoc.getParent()).orElse(null),
                    "No description existing in this javadoc."
            );
        }

        if (!javadoc.getContent().isEmpty() && counter(javadoc.getShortDescription()) < shortDescriptionLength) {
            addCodeSmell(javadoc.getParent(), Collections.emptyList(),
                    createSignature(javadoc.getParent()).orElse(null),
                    "Short-description of this javadoc is too short."
            );
        }

        if (!javadoc.getContent().isEmpty() && counter(javadoc.getLongDescription()) < longDescriptionLength) {
            addCodeSmell(javadoc.getParent(), Collections.emptyList(),
                    createSignature(javadoc.getParent()).orElse(null),
                    "Long-description of this javadoc is too short."
            );
        }

        if (!javadoc.getContent().isEmpty() && counter(javadoc.getContent()) < totalDescriptionLength) {
            addCodeSmell(javadoc.getParent(), Collections.emptyList(),
                    createSignature(javadoc.getParent()).orElse(null),
                    "Total-description of this javadoc is too short."
            );
        }
    }

    /**
     * Checks the length from all allowed javadoc tags.
     * If the length do not match the specifications it will be throw a code smell.
     *
     * @param javadoc             is the too checked javadoc, that contains the tags.
     * @param allowDocumentedTags are the allowed tags from the javadoc.
     */
    private void checkTagLength(CtJavaDoc javadoc, ArrayList<CtJavaDocTag> allowDocumentedTags) {
        for (CtJavaDocTag tag : allowDocumentedTags) {
            switch (tag.getType()) {
                case DEPRECATED:
                    if (counter(tag.getContent()) < tagDeprecated) {
                        addCodeSmell(javadoc.getParent(), Collections.emptyList(),
                                createSignature(javadoc.getParent()).orElse(null),
                                "Description of the @deprecated tag is too short."
                        );
                    }
                    break;
                case SERIAL_DATA:
                    if (counter(tag.getContent()) < tagSerialData) {
                        addCodeSmell(javadoc.getParent(), Collections.emptyList(),
                                createSignature(javadoc.getParent()).orElse(null),
                                "Description of the @serialData tag is too short."
                        );
                    }
                    break;
                case SERIAL_FIELD:
                    if (counter(tag.getContent()) < tagSerialField) {
                        addCodeSmell(javadoc.getParent(), Collections.emptyList(),
                                createSignature(javadoc.getParent()).orElse(null),
                                "Description of the @serialField tag is too short."
                        );
                    }
                    break;
                case SERIAL:
                    if (counter(tag.getContent()) < tagSerial) {
                        addCodeSmell(javadoc.getParent(), Collections.emptyList(),
                                createSignature(javadoc.getParent()).orElse(null),
                                "Description of the @serial tag is too short."
                        );
                    }
                    break;
                case SEE:
                    if (counter(tag.getContent()) < tagSee) {
                        addCodeSmell(javadoc.getParent(), Collections.emptyList(),
                                createSignature(javadoc.getParent()).orElse(null),
                                "Description of the @see tag is too short."
                        );
                    }
                    break;
                case SINCE:
                    if (counter(tag.getContent()) < tagSince) {
                        addCodeSmell(javadoc.getParent(), Collections.emptyList(),
                                createSignature(javadoc.getParent()).orElse(null),
                                "Description of the @since tag is too short."
                        );
                    }
                    break;
                case VERSION:
                    if (counter(tag.getContent()) < tagVersion) {
                        addCodeSmell(javadoc.getParent(), Collections.emptyList(),
                                createSignature(javadoc.getParent()).orElse(null),
                                "Description of the @version tag is too short."
                        );
                    }
                    break;
                case AUTHOR:
                    if (counter(tag.getContent()) < tagAuthor) {
                        addCodeSmell(javadoc.getParent(), Collections.emptyList(),
                                createSignature(javadoc.getParent()).orElse(null),
                                "Description of the @author tag is too short."
                        );
                    }
                    break;
                case RETURN:
                    if (counter(tag.getContent()) < tagReturn) {
                        addCodeSmell(javadoc.getParent(), Collections.emptyList(),
                                createSignature(javadoc.getParent()).orElse(null),
                                "Description of the @return tag is too short."
                        );
                    }
                    break;
                case PARAM:
                    if (counter(tag.getContent()) < tagParam) {
                        addCodeSmell(javadoc.getParent(), Collections.emptyList(),
                                createSignature(javadoc.getParent()).orElse(null),
                                "Description of the @param " + tag.getParam() + " is too short."
                        );
                    }
                    break;
                case EXCEPTION:
                    if (counter(tag.getContent()) < tagThrows) {
                        addCodeSmell(javadoc.getParent(), Collections.emptyList(),
                                createSignature(javadoc.getParent()).orElse(null),
                                "Description of the @exception " + tag.getParam() + " is too short."
                        );
                    }
                    break;
                case THROWS:
                    if (counter(tag.getContent()) < tagThrows) {
                        addCodeSmell(javadoc.getParent(), Collections.emptyList(),
                                createSignature(javadoc.getParent()).orElse(null),
                                "Description of the @throws " + tag.getParam() + " is too short."
                        );
                    }
                    break;
            }
        }
    }

    //----------------- Getter / Setter
    @Override
    public CodeSmell.Definition getDefinition() {
        return new CodeSmell.Definition("Javadoc", new Thresholds());
    }

    public void withAllAccessModifier() {
        withAccessModifier(new AccessModifier[]{AccessModifier.PUBLIC, AccessModifier.PRIVATE, AccessModifier.PROTECTED,
                AccessModifier.NULL});
    }

    public void withAccessModifier(@NonNull AccessModifier[] list) {
        for (AccessModifier modifier : list) {
            withAccessModifier(modifier);
        }
    }

    /**
     * This method add's an access modifier to the accessModifierList.
     * It will be converted to the class ModifierKind.
     * ModifierKind is the type that an element returns when getVisibility() is called.
     *
     * @param modifier the access modifier to be added.
     */
    public void withAccessModifier(@NonNull AccessModifier modifier) {
        switch (modifier) {
            case PUBLIC:
                accessModifierList.add(ModifierKind.PUBLIC);
                break;
            case PRIVATE:
                accessModifierList.add(ModifierKind.PRIVATE);
                break;
            case PROTECTED:
                accessModifierList.add(ModifierKind.PROTECTED);
                break;
            case NULL:
                accessModifierList.add(null);
                break;
        }
    }

    /**
     * This method remove an access modifier from the accessModifierList.
     * It will be converted to the class ModifierKind.
     * ModifierKind is the type that an element returns when getVisibility() is called.
     *
     * @param modifier the access modifier to be removed.
     */
    public void withoutAccessModifier(@NonNull AccessModifier modifier) {
        switch (modifier) {
            case PUBLIC:
                accessModifierList.remove(ModifierKind.PUBLIC);
                break;
            case PRIVATE:
                accessModifierList.remove(ModifierKind.PRIVATE);
                break;
            case PROTECTED:
                accessModifierList.remove(ModifierKind.PROTECTED);
                break;
            case NULL:
                accessModifierList.remove(null);
                break;
        }
    }

    public void withoutAccessModifier(@NonNull AccessModifier[] modifiers) {
        for (AccessModifier modifier : modifiers) {
            withoutAccessModifier(modifier);
        }
    }

    public void withoutAllAccessModifier() {
        accessModifierList.clear();
    }

    public LinkedHashSet<ModifierKind> getAccessModifier() {
        return accessModifierList;
    }

    public void withAllJavadocables() {
        withJavadocable(new Javadocable[]{Javadocable.CLASS, Javadocable.INTERFACE, Javadocable.METHOD,
                Javadocable.ENUM, Javadocable.CONSTRUCTOR, Javadocable.FIELD, Javadocable.PACKAGE, Javadocable.ANNOTATIONTYPE});
    }

    public void withJavadocable(@NonNull Javadocable[] list) {
        for (Javadocable element : list) {
            javadocableList.add(element);
        }
    }

    public void withJavadocable(@NonNull Javadocable element) {
        javadocableList.add(element);
    }

    public void withoutJavadocable(@NonNull Javadocable element) {
        javadocableList.remove(element);
    }

    public void withoutJavadocable(@NonNull Javadocable[] elements) {
        for (Javadocable element : elements) {
            javadocableList.remove(element);
        }
    }

    public void withoutAllJavadocable() {
        javadocableList.clear();
    }

    public LinkedHashSet<Javadocable> getJavadocable() {
        return javadocableList;
    }

    public void withFieldAccessMustBeDocumented() {
        this.fieldAccessMustBeDocumented = true;
    }

    public void withoutFieldAccessMustBeDocumented() {
        this.fieldAccessMustBeDocumented = false;
    }

    public void withReadByWord() {
        this.readByWord = true;
    }

    public void withoutReadByWord() {
        this.readByWord = false;
    }

    public void withPackageInfoNeeded() {
        this.packageInfoNeeded = true;
    }

    public void withoutPackageInfoNeeded() {
        this.packageInfoNeeded = false;
    }

    public void withTypelessPackageInfoNeeded() {
        this.typelessPackageInfoNeeded = true;
    }

    public void withoutTypelessPackageInfoNeeded() {
        this.typelessPackageInfoNeeded = false;
    }

    public void withParam(int tagParam) {
        this.tagParam = tagParam;
    }

    public void withoutParam() {
        this.tagParam = 0;
    }

    public void withReturn(int tagReturn) {
        this.tagReturn = tagReturn;
    }

    public void withoutReturn() {
        this.tagReturn = 0;
    }

    public void withThrows(int tagThrows) {
        this.tagThrows = tagThrows;
    }

    public void withoutThrows() {
        this.tagThrows = 0;
    }

    public void withException(int tagThrows) {
        this.tagThrows = tagThrows;
    }

    public void withoutException() {
        this.tagThrows = 0;
    }

    public void withAuthor(int tagAuthor) {
        this.tagAuthor = tagAuthor;
    }

    public void withoutAuthor() {
        this.tagAuthor = 0;
    }

    public void withVersion(int tagVersion) {
        this.tagVersion = tagVersion;
    }

    public void withoutVersion() {
        this.tagVersion = 0;
    }

    public void withSince(int tagSince) {
        this.tagSince = tagSince;
    }

    public void withoutSince() {
        this.tagSince = 0;
    }

    public void withSee(int tagSee) {
        this.tagSee = tagSee;
    }

    public void withoutSee() {
        this.tagSee = 0;
    }

    public void withSerial(int tagSerial) {
        this.tagSerial = tagSerial;
    }

    public void withoutSerial() {
        this.tagSerial = 0;
    }

    public void withSerialField(int tagSerialField) {
        this.tagSerialField = tagSerialField;
    }

    public void withoutSerialField() {
        this.tagSerialField = 0;
    }

    public void withSerialData(int tagSerialData) {
        this.tagSerialData = tagSerialData;
    }

    public void withoutSerialData() {
        this.tagSerialData = 0;
    }

    public void withDeprecated(int tagDeprecated) {
        this.tagDeprecated = tagDeprecated;
    }

    public void withoutDeprecated() {
        this.tagDeprecated = 0;
    }

    public void withAll(int minShort, int minLong) {
        withType(minShort, minLong);
        withExecutable(minShort, minLong);
        withField(minShort, minLong);
        withPackage(minShort, minLong);
        withAnnotationType(minShort, minLong);
        withAllJavadocables();
    }

    public void withAll(int minTotal) {
        withType(minTotal);
        withExecutable(minTotal);
        withField(minTotal);
        withPackage(minTotal);
        withAnnotationType(minTotal);
        withAllJavadocables();
    }

    public void withoutAll() {
        withoutType();
        withoutExecutable();
        withoutField();
        withoutPackage();
        withoutAnnotationType();
        withoutJavadocable(new Javadocable[]{Javadocable.CLASS, Javadocable.INTERFACE, Javadocable.METHOD,
                Javadocable.ENUM, Javadocable.CONSTRUCTOR, Javadocable.FIELD, Javadocable.PACKAGE, Javadocable.ANNOTATIONTYPE});
    }

    public void withAllShort(int minShort) {
        withTypeShort(minShort);
        withExecutableShort(minShort);
        withFieldShort(minShort);
        withPackageShort(minShort);
        withAnnotationTypeShort(minShort);
    }

    public void withoutAllShort() {
        withoutTypeShort();
        withoutExecutableShort();
        withoutFieldShort();
        withoutPackageShort();
        withoutAnnotationTypeShort();
    }

    public void withAllLong(int minLong) {
        withTypeLong(minLong);
        withExecutableLong(minLong);
        withFieldLong(minLong);
        withPackageLong(minLong);
        withAnnotationTypeLong(minLong);
    }

    public void withoutAllLong() {
        withoutTypeLong();
        withoutExecutableLong();
        withoutFieldLong();
        withoutPackageLong();
        withoutAnnotationTypeLong();
    }

    public void withAllTotal(int minTotal) {
        withTypeTotal(minTotal);
        withExecutableTotal(minTotal);
        withFieldTotal(minTotal);
        withPackageTotal(minTotal);
        withAnnotationTypeTotal(minTotal);
    }

    public void withoutAllTotal() {
        withoutTypeTotal();
        withoutExecutableTotal();
        withoutFieldTotal();
        withoutPackageTotal();
        withoutAnnotationTypeTotal();
    }

    public void withType(int minShort, int minLong) {
        withClass(minShort, minLong);
        withInterface(minShort, minLong);
        withEnum(minShort, minLong);
        withJavadocable(new Javadocable[]{Javadocable.CLASS, Javadocable.INTERFACE, Javadocable.ENUM});

    }

    public void withType(int minTotal) {
        withClass(minTotal);
        withInterface(minTotal);
        withEnum(minTotal);
        withJavadocable(new Javadocable[]{Javadocable.CLASS, Javadocable.INTERFACE, Javadocable.ENUM});
    }

    public void withoutType() {
        withoutClass();
        withoutInterface();
        withoutEnum();
        withoutJavadocable(new Javadocable[]{Javadocable.CLASS, Javadocable.INTERFACE, Javadocable.ENUM});

    }

    public void withTypeShort(int shortDescription) {
        withClassShort(shortDescription);
        withInterfaceShort(shortDescription);
        withEnumShort(shortDescription);
    }

    public void withoutTypeShort() {
        withoutClassShort();
        withoutInterfaceShort();
        withoutEnumShort();
    }

    public void withTypeLong(int longDescription) {
        withClassLong(longDescription);
        withInterfaceLong(longDescription);
        withEnumLong(longDescription);
    }

    public void withoutTypeLong() {
        withoutClassLong();
        withoutInterfaceLong();
        withoutEnumLong();
    }

    public void withTypeTotal(int totalDescription) {
        withClassTotal(totalDescription);
        withInterfaceTotal(totalDescription);
        withEnumTotal(totalDescription);
    }

    public void withoutTypeTotal() {
        withoutClassTotal();
        withoutInterfaceTotal();
        withoutEnumTotal();
    }

    public void withExecutable(int minShort, int minLong) {
        withMethod(minShort, minLong);
        withConstructor(minShort, minLong);
        withJavadocable(new Javadocable[]{Javadocable.METHOD, Javadocable.CONSTRUCTOR});

    }

    public void withExecutable(int minTotal) {
        withMethod(minTotal);
        withConstructor(minTotal);
        withJavadocable(new Javadocable[]{Javadocable.METHOD, Javadocable.CONSTRUCTOR});
    }

    public void withoutExecutable() {
        withoutMethod();
        withoutConstructor();
        withoutJavadocable(new Javadocable[]{Javadocable.METHOD, Javadocable.CONSTRUCTOR});
    }

    public void withExecutableShort(int shortDescription) {
        withMethodShort(shortDescription);
        withConstructorShort(shortDescription);
    }

    public void withoutExecutableShort() {
        withoutMethodShort();
        withoutConstructorShort();
    }

    public void withExecutableLong(int longDescription) {
        withMethodLong(longDescription);
        withConstructorLong(longDescription);
    }

    public void withoutExecutableLong() {
        withoutMethodLong();
        withoutConstructorLong();
    }

    public void withExecutableTotal(int totalDescription) {
        withMethodTotal(totalDescription);
        withConstructorTotal(totalDescription);
    }

    public void withoutExecutableTotal() {
        withoutMethodTotal();
        withoutConstructorTotal();
    }

    public void withField(int minShort, int minLong) {
        withFieldShort(minShort);
        withFieldLong(minLong);
        withJavadocable(Javadocable.FIELD);
    }

    public void withField(int minTotal) {
        withFieldTotal(minTotal);
        withJavadocable(Javadocable.FIELD);
    }

    public void withoutField() {
        withoutFieldShort();
        withoutFieldLong();
        withoutFieldTotal();
        withoutJavadocable(Javadocable.FIELD);
    }

    public void withFieldShort(int shortDescription) {
        minField[0] = shortDescription;
    }

    public void withoutFieldShort() {
        minField[0] = 0;
    }

    public void withFieldLong(int longDescription) {
        minField[1] = longDescription;
    }

    public void withoutFieldLong() {
        minField[1] = 0;
    }

    public void withFieldTotal(int totalDescription) {
        minField[2] = totalDescription;
    }

    public void withoutFieldTotal() {
        minField[2] = 0;
    }

    public void withClass(int minShort, int minLong) {
        withClassShort(minShort);
        withClassLong(minLong);
        withJavadocable(Javadocable.CLASS);
    }

    public void withClass(int minTotal) {
        withClassTotal(minTotal);
        withJavadocable(Javadocable.CLASS);
    }

    public void withoutClass() {
        withoutClassShort();
        withoutClassLong();
        withoutClassTotal();
        withoutJavadocable(Javadocable.CLASS);
    }

    public void withClassShort(int shortDescription) {
        minClass[0] = shortDescription;
    }

    public void withoutClassShort() {
        minClass[0] = 0;
    }

    public void withClassLong(int longDescription) {
        minClass[1] = longDescription;
    }

    public void withoutClassLong() {
        minClass[1] = 0;
    }

    public void withClassTotal(int totalDescription) {
        minClass[2] = totalDescription;
    }

    public void withoutClassTotal() {
        minClass[2] = 0;
    }

    public void withInterface(int minShort, int minLong) {
        withInterfaceShort(minShort);
        withInterfaceLong(minLong);
        withJavadocable(Javadocable.INTERFACE);
    }

    public void withInterface(int minTotal) {
        withInterfaceTotal(minTotal);
        withJavadocable(Javadocable.INTERFACE);
    }

    public void withoutInterface() {
        withoutInterfaceShort();
        withoutInterfaceLong();
        withoutInterfaceTotal();
        withoutJavadocable(Javadocable.INTERFACE);
    }

    public void withInterfaceShort(int shortDescription) {
        minInterface[0] = shortDescription;
    }

    public void withoutInterfaceShort() {
        minInterface[0] = 0;
    }

    public void withInterfaceLong(int longDescription) {
        minInterface[1] = longDescription;
    }

    public void withoutInterfaceLong() {
        minInterface[1] = 0;
    }

    public void withInterfaceTotal(int totalDescription) {
        minInterface[2] = totalDescription;
    }

    public void withoutInterfaceTotal() {
        minInterface[2] = 0;
    }

    public void withEnum(int minShort, int minLong) {
        withEnumShort(minShort);
        withEnumLong(minLong);
        withJavadocable(Javadocable.ENUM);
    }

    public void withEnum(int minTotal) {
        withEnumTotal(minTotal);
        withJavadocable(Javadocable.ENUM);
    }

    public void withoutEnum() {
        withoutEnumShort();
        withoutEnumLong();
        withoutEnumTotal();
        withoutJavadocable(Javadocable.ENUM);
    }

    public void withEnumShort(int shortDescription) {
        minEnum[0] = shortDescription;
    }

    public void withoutEnumShort() {
        minEnum[0] = 0;
    }

    public void withEnumLong(int longDescription) {
        minEnum[1] = longDescription;
    }

    public void withoutEnumLong() {
        minEnum[1] = 0;
    }

    public void withEnumTotal(int totalDescription) {
        minEnum[2] = totalDescription;
    }

    public void withoutEnumTotal() {
        minEnum[2] = 0;
    }

    public void withMethod(int minShort, int minLong) {
        withMethodShort(minShort);
        withMethodLong(minLong);
        withJavadocable(Javadocable.METHOD);
    }

    public void withMethod(int minTotal) {
        withMethodTotal(minTotal);
        withJavadocable(Javadocable.METHOD);
    }

    public void withoutMethod() {
        withoutMethodShort();
        withoutMethodLong();
        withoutMethodTotal();
        withoutJavadocable(Javadocable.METHOD);
    }

    public void withMethodShort(int shortDescription) {
        minMethod[0] = shortDescription;
    }

    public void withoutMethodShort() {
        minMethod[0] = 0;
    }

    public void withMethodLong(int longDescription) {
        minMethod[1] = longDescription;
    }

    public void withoutMethodLong() {
        minMethod[1] = 0;
    }

    public void withMethodTotal(int totalDescription) {
        minMethod[2] = totalDescription;
    }

    public void withoutMethodTotal() {
        minMethod[2] = 0;
    }

    public void withMethodFieldAccess(int minShort, int minLong) {
        minMethodFieldAccess[0] = minShort;
        minMethodFieldAccess[1] = minLong;
        withJavadocable(Javadocable.METHOD);
        withFieldAccessMustBeDocumented();
    }

    public void withMethodFieldAccess(int minTotal) {
        minMethodFieldAccess[2] = minTotal;
        withJavadocable(Javadocable.METHOD);
        withFieldAccessMustBeDocumented();
    }

    public void withoutMethodFieldAccess() {
        minMethodFieldAccess[0] = 0;
        minMethodFieldAccess[1] = 0;
        minMethodFieldAccess[2] = 0;
        if (minMethod[0] == 0 && minMethod[1] == 0 && minMethod[2] == 0) {
            withoutJavadocable(Javadocable.METHOD);
        }
        withoutFieldAccessMustBeDocumented();
    }

    public void withConstructor(int minShort, int minLong) {
        withConstructorShort(minShort);
        withConstructorLong(minLong);
        withJavadocable(Javadocable.CONSTRUCTOR);
    }

    public void withConstructor(int minTotal) {
        withConstructorTotal(minTotal);
        withJavadocable(Javadocable.CONSTRUCTOR);
    }

    public void withoutConstructor() {
        withoutConstructorShort();
        withoutConstructorLong();
        withoutConstructorTotal();
        withoutJavadocable(Javadocable.CONSTRUCTOR);
    }

    public void withConstructorShort(int shortDescription) {
        minConstructor[0] = shortDescription;
    }

    public void withoutConstructorShort() {
        minConstructor[0] = 0;
    }

    public void withConstructorLong(int longDescription) {
        minConstructor[1] = longDescription;
    }

    public void withoutConstructorLong() {
        minConstructor[1] = 0;
    }

    public void withConstructorTotal(int totalDescription) {
        minConstructor[2] = totalDescription;
    }

    public void withoutConstructorTotal() {
        minConstructor[2] = 0;
    }

    public void withPackage(int minShort, int minLong) {
        withPackageShort(minShort);
        withPackageLong(minLong);
        withJavadocable(Javadocable.PACKAGE);
        withPackageInfoNeeded();
    }

    public void withPackage(int minTotal) {
        withPackageTotal(minTotal);
        withJavadocable(Javadocable.PACKAGE);
        withPackageInfoNeeded();
    }

    public void withoutPackage() {
        withoutPackageShort();
        withoutPackageLong();
        withoutPackageTotal();
        withoutJavadocable(Javadocable.PACKAGE);
        withoutPackageInfoNeeded();
    }

    public void withPackageShort(int shortDescription) {
        minPackage[0] = shortDescription;
    }

    public void withoutPackageShort() {
        minPackage[0] = 0;
    }

    public void withPackageLong(int longDescription) {
        minPackage[1] = longDescription;
    }

    public void withoutPackageLong() {
        minPackage[1] = 0;
    }

    public void withPackageTotal(int totalDescription) {
        minPackage[2] = totalDescription;
    }

    public void withoutPackageTotal() {
        minPackage[2] = 0;
    }

    public void withAnnotationType(int minShort, int minLong) {
        withAnnotationTypeShort(minShort);
        withAnnotationTypeLong(minLong);
        withJavadocable(Javadocable.ANNOTATIONTYPE);
    }

    public void withAnnotationType(int minTotal) {
        withAnnotationTypeTotal(minTotal);
        withJavadocable(Javadocable.ANNOTATIONTYPE);
    }

    public void withoutAnnotationType() {
        withoutAnnotationTypeShort();
        withoutAnnotationTypeLong();
        withoutAnnotationTypeTotal();
        withoutJavadocable(Javadocable.ANNOTATIONTYPE);
    }

    public void withAnnotationTypeShort(int shortDescription) {
        minAnnotationType[0] = shortDescription;
    }

    public void withoutAnnotationTypeShort() {
        minAnnotationType[0] = 0;
    }

    public void withAnnotationTypeLong(int longDescription) {
        minAnnotationType[1] = longDescription;
    }

    public void withoutAnnotationTypeLong() {
        minAnnotationType[1] = 0;
    }

    public void withAnnotationTypeTotal(int totalDescription) {
        minAnnotationType[2] = totalDescription;
    }

    public void withoutAnnotationTypeTotal() {
        minAnnotationType[2] = 0;
    }

    /**
     * The enum AccessModifier represents the different access modifier for all javadocables.
     * null denotes the default access modifier.
     */
    public enum AccessModifier {
        PUBLIC, PRIVATE, PROTECTED, NULL
    }

    /**
     * The enum Javadocable represents the different javadoc-enabled types.
     */
    public enum Javadocable {
        CLASS, INTERFACE, METHOD, ENUM, CONSTRUCTOR, FIELD, PACKAGE, ANNOTATIONTYPE
    }
}
