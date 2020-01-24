package de.unibremen.informatik.st.libvcs4j.spoon.codesmell.missingJavadoc;

import de.unibremen.informatik.st.libvcs4j.spoon.codesmell.CodeSmell;
import spoon.reflect.CtModel;
import spoon.reflect.declaration.*;
import spoon.reflect.visitor.filter.TypeFilter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;

import static java.util.Collections.reverseOrder;

public class JavadocEvaluator {
    private static final String PATH_TO_SAVEDIR = "C:/analysis/csv/";
    private static ArrayList<CodeSmell> notEvaluatedCodeSmells = new ArrayList<>();
    private static DecimalFormat df = new DecimalFormat("####0.00");

    /**
     * Initalisiert die gegebene Map mit den Javadoc Code Smells und weist ihnen den Wert 0 zu.
     *
     * @param map die initalisiert werden soll.
     */
    private static void initializeMap(HashMap<String, Integer> map) {
        map.put("Method has no javadoc", 0);
        map.put("Constructor has no javadoc", 0);
        map.put("Class has no javadoc", 0);
        map.put("Interface has no javadoc", 0);
        map.put("Enum has no javadoc", 0);
        map.put("Field has no javadoc", 0);
        map.put("AnnotationType has no javadoc", 0);
        map.put("Package has no javadoc", 0);
        map.put("Tag is not allowed", 0);
        map.put("Missing package-info for package", 0);
        map.put("Missing package-info for typeless package", 0);
        map.put("Missing tag @author", 0);
        map.put("Missing tag @version", 0);
        map.put("Missing tag @deprecated", 0);
        map.put("Missing tag @return", 0);
        map.put("Missing tag @param", 0);
        map.put("Missing tag @throws", 0);
        map.put("Javadoc contains @deprecated", 0);
        map.put("Javadoc contains @return", 0);
        map.put("Javadoc contains @param", 0);
        map.put("Javadoc contains @throws", 0);
        map.put("No description in javadoc", 0);
        map.put("Short-description too short", 0);
        map.put("Long-description too short", 0);
        map.put("Total-description too short", 0);
        map.put("Too short tag @deprecated", 0);
        map.put("Too short tag @serialData", 0);
        map.put("Too short tag @serialField", 0);
        map.put("Too short tag @serial", 0);
        map.put("Too short tag @see", 0);
        map.put("Too short tag @since", 0);
        map.put("Too short tag @version", 0);
        map.put("Too short tag @author", 0);
        map.put("Too short tag @return", 0);
        map.put("Too short tag @param", 0);
        map.put("Too short tag @exception", 0);
        map.put("Too short tag @throws", 0);
    }

    /**
     * Zählt die Vorkommnisse jedes Javadoc Code Smells
     *
     * @param codeSmells die von dem Javadoc-Detektor gefundenen Code Smells.
     * @return eine Map, die die Code Smell Bezeichnung sowie deren gefundenen Anzahl enthält.
     */
    private static HashMap<String, Integer> countCodeSmellsTypes(List<CodeSmell> codeSmells) {
        HashMap<String, Integer> countCodeSmellKind = new HashMap<>();
        initializeMap(countCodeSmellKind);
        for (CodeSmell smell : codeSmells) {
            if (smell.getSummary().get().startsWith("Method") && smell.getSummary().get().contains("has no javadoc.")) {
                countCodeSmellKind.put("Method has no javadoc", countCodeSmellKind.get("Method has no javadoc") + 1);
            } else if (smell.getSummary().get().startsWith("Constructor") && smell.getSummary()
                    .get()
                    .contains("has no javadoc.")) {
                countCodeSmellKind.put(
                        "Constructor has no javadoc",
                        countCodeSmellKind.get("Constructor has no javadoc") + 1
                );
            } else if (smell.getSummary().get().startsWith("Class") && smell.getSummary()
                    .get()
                    .contains("has no javadoc.")) {
                countCodeSmellKind.put("Class has no javadoc", countCodeSmellKind.get("Class has no javadoc") + 1);
            } else if (smell.getSummary().get().startsWith("Interface") && smell.getSummary()
                    .get()
                    .contains("has no javadoc.")) {
                countCodeSmellKind.put(
                        "Interface has no javadoc",
                        countCodeSmellKind.get("Interface has no javadoc") + 1
                );
            } else if (smell.getSummary().get().startsWith("Enum") && smell.getSummary()
                    .get()
                    .contains("has no javadoc.")) {
                countCodeSmellKind.put("Enum has no javadoc", countCodeSmellKind.get("Enum has no javadoc") + 1);
            } else if (smell.getSummary().get().startsWith("Field") && smell.getSummary()
                    .get()
                    .contains("has no javadoc.")) {
                countCodeSmellKind.put("Field has no javadoc", countCodeSmellKind.get("Field has no javadoc") + 1);
            } else if (smell.getSummary().get().startsWith("AnnotationType") && smell.getSummary()
                    .get()
                    .contains("has no javadoc.")) {
                countCodeSmellKind.put(
                        "AnnotationType has no javadoc",
                        countCodeSmellKind.get("AnnotationType has no javadoc") + 1
                );
            } else if (smell.getSummary().get().startsWith("Package") && smell.getSummary()
                    .get()
                    .contains("has no javadoc.")) {
                countCodeSmellKind.put("Package has no javadoc", countCodeSmellKind.get("Package has no javadoc") + 1);
            } else if (smell.getSummary().get().startsWith("Tag") && smell.getSummary()
                    .get()
                    .contains("is not allowed for this javadocable.")) {
                countCodeSmellKind.put("Tag is not allowed", countCodeSmellKind.get("Tag is not allowed") + 1);
            } else if (smell.getSummary().get().startsWith("Missing package-info for the package (")) {
                countCodeSmellKind.put(
                        "Missing package-info for package",
                        countCodeSmellKind.get("Missing package-info for package") + 1
                );
            } else if (smell.getSummary().get().startsWith("Missing package-info for the typeless package (")) {
                countCodeSmellKind.put(
                        "Missing package-info for typeless package",
                        countCodeSmellKind.get("Missing package-info for typeless package") + 1
                );
            } else if (smell.getSummary().get().startsWith("Missing tag @author in the javadoc.")) {
                countCodeSmellKind.put("Missing tag @author", countCodeSmellKind.get("Missing tag @author") + 1);
            } else if (smell.getSummary().get().startsWith("Missing tag @version in the javadoc.")) {
                countCodeSmellKind.put("Missing tag @version", countCodeSmellKind.get("Missing tag @version") + 1);
            } else if (smell.getSummary().get().startsWith("Missing tag @deprecated in the javadoc.")) {
                countCodeSmellKind.put(
                        "Missing tag @deprecated",
                        countCodeSmellKind.get("Missing tag @deprecated") + 1
                );
            } else if (smell.getSummary().get().startsWith("Missing tag @return in the javadoc.")) {
                countCodeSmellKind.put("Missing tag @return", countCodeSmellKind.get("Missing tag @return") + 1);
            } else if (smell.getSummary().get().startsWith("Missing tag @param") && smell.getSummary()
                    .get()
                    .contains("in the javadoc.")) {
                countCodeSmellKind.put("Missing tag @param", countCodeSmellKind.get("Missing tag @param") + 1);
            } else if (smell.getSummary().get().startsWith("Missing tag @throws") && smell.getSummary()
                    .get()
                    .contains("in the javadoc.")) {
                countCodeSmellKind.put("Missing tag @throws", countCodeSmellKind.get("Missing tag @throws") + 1);
            } else if (smell.getSummary()
                    .get()
                    .startsWith(
                            "Javadoc contains @deprecated, but the annotation @Deprecated at the javadocable is missing.")) {
                countCodeSmellKind.put(
                        "Javadoc contains @deprecated",
                        countCodeSmellKind.get("Javadoc contains @deprecated") + 1
                );
            } else if (smell.getSummary()
                    .get()
                    .startsWith("Javadoc contains @return, but the method has no return value.")) {
                countCodeSmellKind.put(
                        "Javadoc contains @return",
                        countCodeSmellKind.get("Javadoc contains @return") + 1
                );
            } else if (smell.getSummary().get().startsWith("Javadoc contains @param ") && smell.getSummary()
                    .get()
                    .contains(", but this parameter does not exists.")) {
                countCodeSmellKind.put(
                        "Javadoc contains @param",
                        countCodeSmellKind.get("Javadoc contains @param") + 1
                );
            } else if (smell.getSummary().get().startsWith("Javadoc contains @throws/@exception ") && smell.getSummary()
                    .get()
                    .contains(", but this does not thrown by this or a called executable.")) {
                countCodeSmellKind.put(
                        "Javadoc contains @throws",
                        countCodeSmellKind.get("Javadoc contains @throws") + 1
                );
            } else if (smell.getSummary().get().startsWith("No description existing in this javadoc.")) {
                countCodeSmellKind.put(
                        "No description in javadoc",
                        countCodeSmellKind.get("No description in javadoc") + 1
                );
            } else if (smell.getSummary().get().startsWith("Short-description of this javadoc is too short.")) {
                countCodeSmellKind.put(
                        "Short-description too short",
                        countCodeSmellKind.get("Short-description too short") + 1
                );
            } else if (smell.getSummary().get().startsWith("Long-description of this javadoc is too short.")) {
                countCodeSmellKind.put(
                        "Long-description too short",
                        countCodeSmellKind.get("Long-description too short") + 1
                );
            } else if (smell.getSummary().get().startsWith("Total-description of this javadoc is too short.")) {
                countCodeSmellKind.put(
                        "Total-description too short",
                        countCodeSmellKind.get("Total-description too short") + 1
                );
            } else if (smell.getSummary().get().startsWith("Description of the @deprecated tag is too short.")) {
                countCodeSmellKind.put(
                        "Too short tag @deprecated",
                        countCodeSmellKind.get("Too short tag @deprecated") + 1
                );
            } else if (smell.getSummary().get().startsWith("Description of the @serialData tag is too short.")) {
                countCodeSmellKind.put(
                        "Too short tag @serialData",
                        countCodeSmellKind.get("Too short tag @serialData") + 1
                );
            } else if (smell.getSummary().get().startsWith("Description of the @serialField tag is too short.")) {
                countCodeSmellKind.put(
                        "Too short tag @serialField",
                        countCodeSmellKind.get("Too short tag @serialField") + 1
                );
            } else if (smell.getSummary().get().startsWith("Description of the @serial tag is too short.")) {
                countCodeSmellKind.put("Too short tag @serial", countCodeSmellKind.get("Too short tag @serial") + 1);
            } else if (smell.getSummary().get().startsWith("Description of the @see tag is too short.")) {
                countCodeSmellKind.put("Too short tag @see", countCodeSmellKind.get("Too short tag @see") + 1);
            } else if (smell.getSummary().get().startsWith("Description of the @since tag is too short.")) {
                countCodeSmellKind.put("Too short tag @since", countCodeSmellKind.get("Too short tag @since") + 1);
            } else if (smell.getSummary().get().startsWith("Description of the @author tag is too short.")) {
                countCodeSmellKind.put("Too short tag @author", countCodeSmellKind.get("Too short tag @author") + 1);
            } else if (smell.getSummary().get().startsWith("Description of the @version tag is too short.")) {
                countCodeSmellKind.put("Too short tag @version", countCodeSmellKind.get("Too short tag @version") + 1);
            } else if (smell.getSummary().get().startsWith("Description of the @return tag is too short.")) {
                countCodeSmellKind.put("Too short tag @return", countCodeSmellKind.get("Too short tag @return") + 1);
            } else if (smell.getSummary().get().startsWith("Description of the @throws") && smell.getSummary()
                    .get()
                    .contains("is too short.")) {
                countCodeSmellKind.put("Too short tag @throws", countCodeSmellKind.get("Too short tag @throws") + 1);
            } else if (smell.getSummary().get().startsWith("Description of the @exception") && smell.getSummary()
                    .get()
                    .contains("is too short.")) {
                countCodeSmellKind.put(
                        "Too short tag @exception",
                        countCodeSmellKind.get("Too short tag @exception") + 1
                );
            } else if (smell.getSummary().get().startsWith("Description of the @param") && smell.getSummary()
                    .get()
                    .contains("is too short.")) {
                countCodeSmellKind.put("Too short tag @param", countCodeSmellKind.get("Too short tag @param") + 1);
            } else {
                System.out.println(smell.getSummary().get() + " von " + smell.getSignature());
            }
        }
        return countCodeSmellKind;
    }

    /**
     * Ordnet den gefundenen Code Smells die Javadocable-Art ihres Inhabers zu.
     *
     * @param codeSmells die gefundenen Code Smells des Javadoc-Detektors
     * @param map        ist die Map, die die Signatur und die Javadocable-Art jedes im Model gefundenen Javadocables enthält.
     * @return eine Map, die zu jeden Code Smell die Javadocable-Art ihres Inhabers besitzt.
     */
    private static Multimap<String, String> smellTypesWithDataClass(
            List<CodeSmell> codeSmells,
            HashMap<Optional<String>, String> map
    ) {
        Multimap<String, String> countCodeSmellKind = ArrayListMultimap.create();

        for (CodeSmell smell : codeSmells) {
            if (!smell.getSignature().equals(Optional.empty()) && map.get(smell.getSignature()) != null) {
                if (smell.getSummary().get().startsWith("Method") && smell.getSummary()
                        .get()
                        .contains("has no javadoc.")) {
                    countCodeSmellKind.put("Method has no javadoc", map.get(smell.getSignature()));
                } else if (smell.getSummary().get().startsWith("Constructor") && smell.getSummary()
                        .get()
                        .contains("has no javadoc.")) {
                    countCodeSmellKind.put("Constructor has no javadoc", map.get(smell.getSignature()));
                } else if (smell.getSummary().get().startsWith("Class") && smell.getSummary()
                        .get()
                        .contains("has no javadoc.")) {
                    countCodeSmellKind.put("Class has no javadoc", map.get(smell.getSignature()));
                } else if (smell.getSummary().get().startsWith("Interface") && smell.getSummary()
                        .get()
                        .contains("has no javadoc.")) {
                    countCodeSmellKind.put("Interface has no javadoc", map.get(smell.getSignature()));
                } else if (smell.getSummary().get().startsWith("Enum") && smell.getSummary()
                        .get()
                        .contains("has no javadoc.")) {
                    countCodeSmellKind.put("Enum has no javadoc", map.get(smell.getSignature()));
                } else if (smell.getSummary().get().startsWith("Field") && smell.getSummary()
                        .get()
                        .contains("has no javadoc.")) {
                    countCodeSmellKind.put("Field has no javadoc", map.get(smell.getSignature()));
                } else if (smell.getSummary().get().startsWith("AnnotationType") && smell.getSummary()
                        .get()
                        .contains("has no javadoc.")) {
                    countCodeSmellKind.put("AnnotationType has no javadoc", map.get(smell.getSignature()));
                } else if (smell.getSummary().get().startsWith("Package") && smell.getSummary()
                        .get()
                        .contains("has no javadoc.")) {
                    countCodeSmellKind.put("Package has no javadoc", map.get(smell.getSignature()));
                } else if (smell.getSummary().get().startsWith("Tag") && smell.getSummary()
                        .get()
                        .contains("is not allowed for this javadocable.")) {
                    countCodeSmellKind.put("Tag is not allowed", map.get(smell.getSignature()));
                } else if (smell.getSummary().get().startsWith("Missing package-info for the package (")) {
                    countCodeSmellKind.put("Missing package-info for package", map.get(smell.getSignature()));
                } else if (smell.getSummary().get().startsWith("Missing package-info for the typeless package (")) {
                    countCodeSmellKind.put("Missing package-info for typeless package", map.get(smell.getSignature()));
                } else if (smell.getSummary().get().startsWith("Missing tag @author in the javadoc.")) {
                    countCodeSmellKind.put("Missing tag @author", map.get(smell.getSignature()));
                } else if (smell.getSummary().get().startsWith("Missing tag @version in the javadoc.")) {
                    countCodeSmellKind.put("Missing tag @version", map.get(smell.getSignature()));
                } else if (smell.getSummary().get().startsWith("Missing tag @deprecated in the javadoc.")) {
                    countCodeSmellKind.put("Missing tag @deprecated", map.get(smell.getSignature()));
                } else if (smell.getSummary().get().startsWith("Missing tag @return in the javadoc.")) {
                    countCodeSmellKind.put("Missing tag @return", map.get(smell.getSignature()));
                } else if (smell.getSummary().get().startsWith("Missing tag @param") && smell.getSummary()
                        .get()
                        .contains("in the javadoc.")) {
                    countCodeSmellKind.put("Missing tag @param", map.get(smell.getSignature()));
                } else if (smell.getSummary().get().startsWith("Missing tag @throws") && smell.getSummary()
                        .get()
                        .contains("in the javadoc.")) {
                    countCodeSmellKind.put("Missing tag @throws", map.get(smell.getSignature()));
                } else if (smell.getSummary()
                        .get()
                        .startsWith(
                                "Javadoc contains @deprecated, but the annotation @Deprecated at the javadocable is missing.")) {
                    countCodeSmellKind.put("Javadoc contains @deprecated", map.get(smell.getSignature()));
                } else if (smell.getSummary()
                        .get()
                        .startsWith("Javadoc contains @return, but the method has no return value.")) {
                    countCodeSmellKind.put("Javadoc contains @return", map.get(smell.getSignature()));
                } else if (smell.getSummary().get().startsWith("Javadoc contains @param ") && smell.getSummary()
                        .get()
                        .contains(", but this parameter does not exists.")) {
                    countCodeSmellKind.put("Javadoc contains @param", map.get(smell.getSignature()));
                } else if (smell.getSummary()
                        .get()
                        .startsWith("Javadoc contains @throws/@exception ") && smell.getSummary()
                        .get()
                        .contains(", but this does not thrown by this or a called executable.")) {
                    countCodeSmellKind.put("Javadoc contains @throws", map.get(smell.getSignature()));
                } else if (smell.getSummary().get().startsWith("No description existing in this javadoc.")) {
                    countCodeSmellKind.put("No description in javadoc", map.get(smell.getSignature()));
                } else if (smell.getSummary().get().startsWith("Short-description of this javadoc is too short.")) {
                    countCodeSmellKind.put("Short-description too short", map.get(smell.getSignature()));
                } else if (smell.getSummary().get().startsWith("Long-description of this javadoc is too short.")) {
                    countCodeSmellKind.put("Long-description too short", map.get(smell.getSignature()));
                } else if (smell.getSummary().get().startsWith("Total-description of this javadoc is too short.")) {
                    countCodeSmellKind.put("Total-description too short", map.get(smell.getSignature()));
                } else if (smell.getSummary().get().startsWith("Description of the @deprecated tag is too short.")) {
                    countCodeSmellKind.put("Too short tag @deprecated", map.get(smell.getSignature()));
                } else if (smell.getSummary().get().startsWith("Description of the @serialData tag is too short.")) {
                    countCodeSmellKind.put("Too short tag @serialData", map.get(smell.getSignature()));
                } else if (smell.getSummary().get().startsWith("Description of the @serialField tag is too short.")) {
                    countCodeSmellKind.put("Too short tag @serialField", map.get(smell.getSignature()));
                } else if (smell.getSummary().get().startsWith("Description of the @serial tag is too short.")) {
                    countCodeSmellKind.put("Too short tag @serial", map.get(smell.getSignature()));
                } else if (smell.getSummary().get().startsWith("Description of the @see tag is too short.")) {
                    countCodeSmellKind.put("Too short tag @see", map.get(smell.getSignature()));
                } else if (smell.getSummary().get().startsWith("Description of the @since tag is too short.")) {
                    countCodeSmellKind.put("Too short tag @since", map.get(smell.getSignature()));
                } else if (smell.getSummary().get().startsWith("Description of the @author tag is too short.")) {
                    countCodeSmellKind.put("Too short tag @author", map.get(smell.getSignature()));
                } else if (smell.getSummary().get().startsWith("Description of the @version tag is too short.")) {
                    countCodeSmellKind.put("Too short tag @version", map.get(smell.getSignature()));
                } else if (smell.getSummary().get().startsWith("Description of the @return tag is too short.")) {
                    countCodeSmellKind.put("Too short tag @return", map.get(smell.getSignature()));
                } else if (smell.getSummary().get().startsWith("Description of the @throws") && smell.getSummary()
                        .get()
                        .contains("is too short.")) {
                    countCodeSmellKind.put("Too short tag @throws", map.get(smell.getSignature()));
                } else if (smell.getSummary().get().startsWith("Description of the @exception") && smell.getSummary()
                        .get()
                        .contains("is too short.")) {
                    countCodeSmellKind.put("Too short tag @exception", map.get(smell.getSignature()));
                } else if (smell.getSummary().get().startsWith("Description of the @param") && smell.getSummary()
                        .get()
                        .contains("is too short.")) {
                    countCodeSmellKind.put("Too short tag @param", map.get(smell.getSignature()));
                }
            } else {
                notEvaluatedCodeSmells.add(smell);
            }
        }
        return countCodeSmellKind;
    }

    /**
     * Ordnet den gefundenen Javadocables aus dem Model ihre jeweilige Javadocable-Art zu.
     *
     * @param javadocDetector wird nur für die Signaturerstellung benötigt.
     * @param model           ist das Model, aus dem die Javadocables gesucht werden.
     * @return eine Map mit der Signatur des gefundenen Javadocables mit der dazugehörigen Javadocable-Art.
     */
    private static HashMap<Optional<String>, String> modelMapSignatureElementclass(
            JavadocDetector javadocDetector,
            CtModel model
    ) {
        HashMap<Optional<String>, String> modelMap = new HashMap<>();
        List<CtClass> classes = model.getElements(new TypeFilter<>(CtClass.class));
        for (CtClass c : classes) {
            modelMap.put(javadocDetector.createSignature(c), "CtClass");
        }
        List<CtInterface> interfaces = model.getElements(new TypeFilter<>(CtInterface.class));
        for (CtInterface i : interfaces) {
            modelMap.put(javadocDetector.createSignature(i), "CtInterface");
        }
        List<CtEnum> enums = model.getElements(new TypeFilter<>(CtEnum.class));
        for (CtEnum e : enums) {
            modelMap.put(javadocDetector.createSignature(e), "CtEnum");
        }
        List<CtMethod> methods = model.getElements(new TypeFilter<>(CtMethod.class));
        for (CtMethod m : methods) {
            modelMap.put(javadocDetector.createSignature(m), "CtMethod");
        }
        List<CtConstructor> constructors = model.getElements(new TypeFilter<>(CtConstructor.class));
        for (CtConstructor c : constructors) {
            modelMap.put(javadocDetector.createSignature(c), "CtConstructor");
        }
        List<CtField> fields = model.getElements(new TypeFilter<>(CtField.class));
        for (CtField f : fields) {
            modelMap.put(javadocDetector.createSignature(f), "CtField");
        }
        List<CtAnnotationType> annotations = model.getElements(new TypeFilter<>(CtAnnotationType.class));
        for (CtAnnotationType a : annotations) {
            modelMap.put(javadocDetector.createSignature(a), "CtAnnotationType");
        }
        List<CtPackage> packages = model.getElements(new TypeFilter<>(CtPackage.class));
        for (CtPackage p : packages) {
            if (!p.getQualifiedName().equals(""))
                modelMap.put(javadocDetector.createSignature(p), "CtPackage");
        }
        return modelMap;
    }

    private static Multimap<Optional<String>, CodeSmell> javadocableSignatureCodeSmell(List<CodeSmell> codeSmells) {
        Multimap<Optional<String>, CodeSmell> codeSmellMap = ArrayListMultimap.create();
        for (CodeSmell smell : codeSmells) {
            codeSmellMap.put(smell.getSignature(), smell);
        }
        return codeSmellMap;
    }

    /**
     * Bereitet einen String vor, der alle Vorkommnisse der Code Smells konkatiniert.
     *
     * @param map enthält alle gefundenen Code Smells und die Anzahl der Vorkommnisse der einzelnen Code Smells eines Projektes.
     * @return einen String der die Vorkommnisse der Code Smells im CSV-Format vorbereitet.
     */
    private static String q2(HashMap<String, Integer> map) {
        String q2 = map.get("Method has no javadoc") + ";" +
                map.get("Constructor has no javadoc") + ";" +
                map.get("Class has no javadoc") + ";" +
                map.get("Interface has no javadoc") + ";" +
                map.get("Enum has no javadoc") + ";" +
                map.get("Field has no javadoc") + ";" +
                map.get("AnnotationType has no javadoc") + ";" +
                map.get("Package has no javadoc") + ";" +
                map.get("Tag is not allowed") + ";" +
                map.get("Missing package-info for package") + ";" +
                map.get("Missing package-info for typeless package") + ";" +
                map.get("Missing tag @author") + ";" +
                map.get("Missing tag @version") + ";" +
                map.get("Missing tag @deprecated") + ";" +
                map.get("Missing tag @return") + ";" +
                map.get("Missing tag @param") + ";" +
                map.get("Missing tag @throws") + ";" +
                map.get("Javadoc contains @deprecated") + ";" +
                map.get("Javadoc contains @return") + ";" +
                map.get("Javadoc contains @param") + ";" +
                map.get("Javadoc contains @throws") + ";" +
                map.get("No description in javadoc") + ";" +
                map.get("Short-description too short") + ";" +
                map.get("Long-description too short") + ";" +
                map.get("Total-description too short") + ";" +
                map.get("Too short tag @deprecated") + ";" +
                map.get("Too short tag @serialData") + ";" +
                map.get("Too short tag @serialField") + ";" +
                map.get("Too short tag @serial") + ";" +
                map.get("Too short tag @see") + ";" +
                map.get("Too short tag @since") + ";" +
                map.get("Too short tag @version") + ";" +
                map.get("Too short tag @author") + ";" +
                map.get("Too short tag @return") + ";" +
                map.get("Too short tag @param") + ";" +
                map.get("Too short tag @exception") + ";" +
                map.get("Too short tag @throws");
        return q2;
    }

    /**
     * Bereitet die Ausgabe der Analyse der Tags für die zweite Forschungsfrage vor.
     *
     * @param map enthält die Code Smells für die verschiedenen Javadocables und somit deren Anzahl.
     * @return einen String im CSV-Format, der die Anzahl des analysierten Tags für die verschiedenen Javadocables beinhaltet.
     */
    private static String q2tags(HashMap<String, Integer> map) {
        return map.get("CtClass") + ";" +
                map.get("CtInterface") + ";" +
                map.get("CtEnum") + ";" +
                map.get("CtAnnotationType") + ";" +
                map.get("CtMethod") + ";" +
                map.get("CtConstructor") + ";" +
                map.get("CtPackage") + ";" +
                map.get("CtField");
    }

    /**
     * Zählt die Vorkommnisse eines Wertes in der Map
     *
     * @param map   die den Wert mehrfach enthält
     * @param value ist der Wert der gezählt werden soll.
     * @return die Anzahl der Häufigkeit des Wertes.
     */
    private static int countValuesCorrespondence(HashMap<Optional<String>, String> map, String value) {
        int i = 0;
        for (Optional<String> key : map.keySet()) {
            if (map.get(key).equals(value)) {
                i++;
            }
        }
        return i;
    }

    /**
     * Initalisiert die gegebene Map, um die Vorkommnisse der einzelnen Javadocables zu analysieren.
     *
     * @param map die Map die initialisiert werden soll.
     */
    private static void initCodeSmellOccurrences(HashMap<String, Integer> map) {
        map.put("CtClass", 0);
        map.put("CtInterface", 0);
        map.put("CtEnum", 0);
        map.put("CtAnnotationType", 0);
        map.put("CtMethod", 0);
        map.put("CtConstructor", 0);
        map.put("CtPackage", 0);
        map.put("CtField", 0);
    }

    /**
     * Führt die Auswertungen für die erste Forschungsfrage durch.
     *
     * @param javadocDetector ist der für die Analyse verwendete Javadoc-Detektor
     * @param model           ist das für die Analyse verwendete Model
     * @param projectName     ist der Name des Projektes, wird zum abspeichern der Daten benötigt.
     * @param revisionOrdinal ist die Nummer der Revision, die aktuell untersucht wird.
     */
    public static void beginQ1(
            JavadocDetector javadocDetector,
            CtModel model,
            String projectName,
            int revisionOrdinal
    ) {
        HashMap<Optional<String>, String> modelMap = modelMapSignatureElementclass(javadocDetector, model);
        HashMap<String, Integer> smellsTypes = countCodeSmellsTypes(javadocDetector.getCodeSmells());
        int codeSmellSize = javadocDetector.getCodeSmells().size();
        final String PATH = PATH_TO_SAVEDIR + "q1/";

        StringBuilder sbQ1 = new StringBuilder(revisionOrdinal + ";" + modelMap.size() + ";" + codeSmellSize + ";");

        double smellsTooShortTag = smellsTypes.get("Too short tag @deprecated") + smellsTypes.get(
                "Too short tag @serialData") +
                smellsTypes.get("Too short tag @serialField") + smellsTypes.get("Too short tag @serial") +
                smellsTypes.get("Too short tag @see") + smellsTypes.get("Too short tag @since") +
                smellsTypes.get("Too short tag @version") + smellsTypes.get("Too short tag @author") +
                smellsTypes.get("Too short tag @return") + smellsTypes.get("Too short tag @param") +
                smellsTypes.get("Too short tag @exception") + smellsTypes.get("Too short tag @throws");
        System.out.println((int) smellsTooShortTag + " Code Smells entstehen durch zu kurze Tag - Beschreibung. Das entspricht " + df
                .format(smellsTooShortTag / codeSmellSize * 100) + "% der gesamten Code Smells.");

        double smellsTooShortDescription = smellsTypes.get("No description in javadoc") + smellsTypes.get(
                "Short-description too short") +
                smellsTypes.get("Long-description too short") + smellsTypes.get("Total-description too short");
        System.out.println((int) smellsTooShortDescription + " Code Smells entstehen durch zu kurze Short-/Long-/Total-Description. Das entspricht " + df
                .format(smellsTooShortDescription / codeSmellSize * 100) + "% der gesamten Code Smells.");
        System.out.println("=> " + (int) (smellsTooShortDescription + smellsTooShortTag) + " Code Smells entstehen durch zu kurze Beschreibung im Allgemeinen. Das entspricht " + df
                .format((smellsTooShortDescription + smellsTooShortTag) / codeSmellSize * 100) + "% der gesamten Code Smells.");

        sbQ1.append((int) (smellsTooShortDescription + smellsTooShortTag) + ";");

        double smellsNoJavadoc = smellsTypes.get("Method has no javadoc") + smellsTypes.get("Constructor has no javadoc") +
                smellsTypes.get("Class has no javadoc") + smellsTypes.get("Interface has no javadoc") +
                smellsTypes.get("Enum has no javadoc") + smellsTypes.get("Field has no javadoc") +
                smellsTypes.get("AnnotationType has no javadoc") + smellsTypes.get("Package has no javadoc");
        System.out.println((int) smellsNoJavadoc + " Code Smells entstehen durch nicht vorhandener Javadoc. Das entspricht " + df
                .format(smellsNoJavadoc / codeSmellSize * 100) + "% der gesamten Code Smells.");

        sbQ1.append((int) smellsNoJavadoc + ";");

        double smellMissingTag = smellsTypes.get("Missing tag @author") + smellsTypes.get("Missing tag @version") +
                smellsTypes.get("Missing tag @deprecated") + smellsTypes.get("Missing tag @return") +
                smellsTypes.get("Missing tag @param") + smellsTypes.get("Missing tag @throws");
        System.out.println((int) smellMissingTag + " Code Smells entstehen durch nicht fehlende Tags. Das entspricht " + df
                .format(smellMissingTag / codeSmellSize * 100) + "% der gesamten Code Smells.");

        sbQ1.append((int) smellMissingTag + ";");

        double smellMissingRequirement = smellsTypes.get("Javadoc contains @deprecated") +
                smellsTypes.get("Javadoc contains @return") +
                smellsTypes.get("Javadoc contains @param") +
                smellsTypes.get("Javadoc contains @throws");
        System.out.println((int) smellMissingRequirement + " Code Smells entstehen durch nicht Erfüllung, der Tag - Voraussetzung. Das entspricht " + df
                .format(smellMissingRequirement / codeSmellSize * 100) + "% der gesamten Code Smells.");
        System.out.println(smellsTypes.get("Tag is not allowed") + " Code Smells entstehen durch nicht erlaubte Javadoc - Tags in dem Javadocable. Das entspricht " + df
                .format(smellsTypes.get("Tag is not allowed")
                        .doubleValue() / codeSmellSize * 100) + "% der gesamten Code Smells.");

        sbQ1.append((int) smellMissingRequirement + ";");
        sbQ1.append((int) smellsTypes.get("Tag is not allowed") + ";");

        double smellMissingPackageInfo = smellsTypes.get("Missing package-info for package") +
                smellsTypes.get("Missing package-info for typeless package");
        System.out.println((int) smellMissingPackageInfo + " Code Smells entstehen durch fehlende Package-Infos. Das entspricht " + df
                .format(smellMissingPackageInfo / codeSmellSize * 100) + "% der gesamten Code Smells.");

        sbQ1.append((int) smellMissingPackageInfo);
        sbQ1.append("\n");

        File dir = new File(PATH);
        if (!dir.exists()) {
            dir.mkdir();
        }

        File csvFile = new File(PATH + "q1_" + projectName + ".csv");
        StringBuilder sbQ1ToFile = new StringBuilder();
        final String header = "Revisions_Ordinal;Javadocables_in_project;Javadoc_Code_Smells;" +
                "Too_short_descriptions_(tag_&_normal);Element_has_no_javadoc;Missing_tags;Missing_tag_requirement;" +
                "Tag_is_not_allowed;Missing_package-info";
        if (!csvFile.exists()) {
            sbQ1ToFile.append(header);
            sbQ1ToFile.append("\n");
        }

        sbQ1ToFile.append(sbQ1);

        try (FileWriter writer = new FileWriter(csvFile, true)) {
            writer.write(sbQ1ToFile.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

        StringBuilder sbQ1All = new StringBuilder();
        File q1AllFile = new File("C:/analysis/csv/q1/q1_all.csv");
        if (!q1AllFile.exists()) {
            sbQ1All.append("Projects;");
            sbQ1All.append(header);
            sbQ1All.append("\n");
        }

        StringBuilder sbQ1WithProjectName = new StringBuilder(projectName + ";");
        sbQ1WithProjectName.append(sbQ1);

        sbQ1All.append(sbQ1WithProjectName);
        try (FileWriter writer = new FileWriter(q1AllFile, true)) {
            writer.write(sbQ1All.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Führt die Auswertung für die zweite und dritte Forschungsfrage durch.
     *
     * @param javadocDetector ist der für die Analyse verwendete Javadoc-Detektor.
     * @param model           ist das für die Analyse verwendete Model.
     * @param projectName     ist der Name des analysierten Projektes, wird zum abspeichern der Daten benötigt.
     */
    public static void begin(JavadocDetector javadocDetector, CtModel model, String projectName) {
        beginEvaluation(javadocDetector, model, projectName);
    }

    /**
     * Führt die Auswertung für die zweite und dritte Forschungsfrage durch.
     *
     * @param javadocDetector ist der für die Analyse verwendete Javadoc-Detektor.
     * @param model           ist das für die Analyse verwendete Model.
     * @param projectName     ist der Name des analysierten Projektes, wird zum abspeichern der Daten benötigt.
     */
    private static void beginEvaluation(JavadocDetector javadocDetector, CtModel model, String projectName) {
        HashMap<Optional<String>, String> modelMap = modelMapSignatureElementclass(javadocDetector, model);
        List<CodeSmell> codeSmells = javadocDetector.getCodeSmells();
        HashMap<String, Integer> smellsTypes = countCodeSmellsTypes(codeSmells);
        int codeSmellSize = codeSmells.size();

        Multimap<Optional<String>, CodeSmell> javadocableSignatureCS = javadocableSignatureCodeSmell(codeSmells);

        HashMap<Optional<String>, String> smellFree = new HashMap<>();
        smellFree.putAll(modelMap);


        for (Optional<String> str : javadocableSignatureCS.keys()) {
            smellFree.remove(str);
        }

        HashMap<Optional<String>, String> smellInfected = new HashMap<>();
        LinkedHashSet<Optional<String>> csSignature = new LinkedHashSet<>(javadocableSignatureCS.keys());
        for (Optional<String> signature : csSignature) {
            if (modelMap.get(signature) != null)
                smellInfected.put(signature, modelMap.get(signature));
        }

        System.out.println("--------------------------------------------");
        evaluateQ3(projectName, modelMap, smellInfected, smellFree);
        System.out.println("--------------------------------------------");
        evaluateQ2(projectName, codeSmellSize, codeSmells, smellsTypes);
        System.out.println("--------------------------------------------");
        double smellsTooShortTag = smellsTypes.get("Too short tag @deprecated") + smellsTypes.get(
                "Too short tag @serialData") +
                smellsTypes.get("Too short tag @serialField") + smellsTypes.get("Too short tag @serial") +
                smellsTypes.get("Too short tag @see") + smellsTypes.get("Too short tag @since") +
                smellsTypes.get("Too short tag @version") + smellsTypes.get("Too short tag @author") +
                smellsTypes.get("Too short tag @return") + smellsTypes.get("Too short tag @param") +
                smellsTypes.get("Too short tag @exception") + smellsTypes.get("Too short tag @throws");
        System.out.println((int) smellsTooShortTag + " Code Smells entstehen durch zu kurze Tag - Beschreibung. Das entspricht " + df
                .format(smellsTooShortTag / codeSmellSize * 100) + "% der gesamten Code Smells.");

        double smellsTooShortDescription = smellsTypes.get("No description in javadoc") + smellsTypes.get(
                "Short-description too short") +
                smellsTypes.get("Long-description too short") + smellsTypes.get("Total-description too short");
        System.out.println((int) smellsTooShortDescription + " Code Smells entstehen durch zu kurze Short-/Long-/Total-Description. Das entspricht " + df
                .format(smellsTooShortDescription / codeSmellSize * 100) + "% der gesamten Code Smells.");
        System.out.println("=> " + (int) (smellsTooShortDescription + smellsTooShortTag) + " Code Smells entstehen durch zu kurze Beschreibung im Allgemeinen. Das entspricht " + df
                .format((smellsTooShortDescription + smellsTooShortTag) / codeSmellSize * 100) + "% der gesamten Code Smells.");

        double smellsNoJavadoc = smellsTypes.get("Method has no javadoc") + smellsTypes.get("Constructor has no javadoc") +
                smellsTypes.get("Class has no javadoc") + smellsTypes.get("Interface has no javadoc") +
                smellsTypes.get("Enum has no javadoc") + smellsTypes.get("Field has no javadoc") +
                smellsTypes.get("AnnotationType has no javadoc") + smellsTypes.get("Package has no javadoc");
        System.out.println((int) smellsNoJavadoc + " Code Smells entstehen durch nicht vorhandener Javadoc. Das entspricht " + df
                .format(smellsNoJavadoc / codeSmellSize * 100) + "% der gesamten Code Smells.");

        double smellMissingTag = smellsTypes.get("Missing tag @author") + smellsTypes.get("Missing tag @version") +
                smellsTypes.get("Missing tag @deprecated") + smellsTypes.get("Missing tag @return") +
                smellsTypes.get("Missing tag @param") + smellsTypes.get("Missing tag @throws");
        System.out.println((int) smellMissingTag + " Code Smells entstehen durch nicht fehlende Tags. Das entspricht " + df
                .format(smellMissingTag / codeSmellSize * 100) + "% der gesamten Code Smells.");

        double smellMissingRequirement = smellsTypes.get("Javadoc contains @deprecated") +
                smellsTypes.get("Javadoc contains @return") +
                smellsTypes.get("Javadoc contains @param") +
                smellsTypes.get("Javadoc contains @throws");
        System.out.println((int) smellMissingRequirement + " Code Smells entstehen durch nicht Erfüllung, der Tag - Voraussetzung. Das entspricht " + df
                .format(smellMissingRequirement / codeSmellSize * 100) + "% der gesamten Code Smells.");
        System.out.println(smellsTypes.get("Tag is not allowed") + " Code Smells entstehen durch nicht erlaubte Javadoc - Tags in dem Javadocable. Das entspricht " + df
                .format(smellsTypes.get("Tag is not allowed")
                        .doubleValue() / codeSmellSize * 100) + "% der gesamten Code Smells.");

        double smellMissingPackageInfo = smellsTypes.get("Missing package-info for package") +
                smellsTypes.get("Missing package-info for typeless package");
        System.out.println((int) smellMissingPackageInfo + " Code Smells entstehen durch fehlende Package-Infos. Das entspricht " + df
                .format(smellMissingPackageInfo / codeSmellSize * 100) + "% der gesamten Code Smells.");

        System.out.println("--------------------------------------------");

        double smellsTagAuthor = smellsTypes.get("Missing tag @author") + smellsTypes.get("Too short tag @author");
        System.out.println((int) smellsTagAuthor + " Code Smells wurden durch den Javadoc-Tag @author gefunden. Das entspricht " + df
                .format(smellsTagAuthor / codeSmellSize * 100) + "% der gesamten Code Smells.");

        double smellsTagVersion = smellsTypes.get("Missing tag @version") + smellsTypes.get("Too short tag @version");
        System.out.println((int) smellsTagVersion + " Code Smells wurden durch den Javadoc - Tag @version gefunden. Das entspricht " + df
                .format(smellsTagVersion / codeSmellSize * 100) + "% der gesamten Code Smells.");

        double smellsDeprecated = smellsTypes.get("Missing tag @deprecated") + smellsTypes.get(
                "Too short tag @deprecated") + smellsTypes.get("Javadoc contains @deprecated");
        System.out.println((int) smellsDeprecated + " Code Smells wurden durch Deprecated - Verstöße gefunden. Das entspricht " + df
                .format(smellsDeprecated / codeSmellSize * 100) + "% der gesamten Code Smells.");

        double smellsReturn = smellsTypes.get("Missing tag @return") + smellsTypes.get("Too short tag @return") + smellsTypes
                .get("Javadoc contains @return");
        System.out.println((int) smellsReturn + " Code Smells wurden durch Return - Verstöße gefunden. Das entspricht " + df
                .format(smellsReturn / codeSmellSize * 100) + "% der gesamten Code Smells.");

        double smellsParam = smellsTypes.get("Missing tag @param") + smellsTypes.get("Too short tag @param") + smellsTypes
                .get("Javadoc contains @param");
        System.out.println((int) smellsParam + " Code Smells wurden durch Parameter - Verstöße gefunden. Das entspricht " + df
                .format(smellsParam / codeSmellSize * 100) + "% der gesamten Code Smells.");

        double smellsException = smellsTypes.get("Missing tag @throws") + smellsTypes.get("Too short tag @throws") +
                smellsTypes.get("Javadoc contains @throws") + smellsTypes.get("Too short tag @exception");
        System.out.println((int) smellsException + " Code Smells wurden durch Exception - Verstöße gefunden. Das entspricht " + df
                .format(smellsException / codeSmellSize * 100) + "% der gesamten Code Smells.");

        int smells = 0;
        for (Integer i : smellsTypes.values()) smells += i;
        System.out.print("Map enthält alle CodeSmells vom Detektor: ");
        System.out.println(smells == codeSmellSize);
        if (smells != codeSmellSize) {
            System.out.println("Map enthält: " + smells + " Code Smells und der JavadocDetector: " + codeSmellSize);
        }

        System.out.println("--------------------------------------------");

        Multimap<String, String> smellTypeWithClass = smellTypesWithDataClass(codeSmells, smellInfected);
        LinkedHashSet<String> setWithoutDuplicates = new LinkedHashSet<>(smellTypeWithClass.keys());
        Multimap<String, String> inversSmellTypeWithClass = ArrayListMultimap.create();

        evaluateQ2Tags(projectName, setWithoutDuplicates, smellTypeWithClass, inversSmellTypeWithClass);
        System.out.println("--------------------------------------------");
        LinkedHashSet<String> inversSetWithoutDuplicates = new LinkedHashSet<>(inversSmellTypeWithClass.keys());
        evaluateQ2Javadocable(projectName, inversSetWithoutDuplicates, inversSmellTypeWithClass);
        System.out.println("--------------------------------------------");
        System.out.println("Anzahl der nicht ausgewerteten Code Smells: " + notEvaluatedCodeSmells.size());
        notEvaluatedCodeSmells.forEach(n -> System.out.println(n.getSignature() + " " + n.getSummary()));
        notEvaluatedCodeSmells.clear();
    }

    /**
     * Wertet die Daten für die zweite Forschungsfrage aus. Dabei geht es um die insgesamt gefundenen Smells
     *
     * @param projectName   des analysierten Projektes
     * @param codeSmellSize enthält die Gesamtanzahl der gefundenen Code Smells
     * @param codeSmells    enthält die gefundenen Code Smells
     * @param smellsTypes   enthält eine Map, mit den verschiedenen Code Smells und ihrer gefundenen Häufigkeit.
     */
    private static void evaluateQ2(
            String projectName, int codeSmellSize,
            List<CodeSmell> codeSmells, HashMap<String, Integer> smellsTypes
    ) {
        System.out.println("Insgesamt wurden " + codeSmells.size() + " Code Smell - Verstöße in dem Projekt gefunden.");
        System.out.println("Von den " + codeSmells.size() + " sind: ");
        smellsTypes.entrySet().stream().sorted(reverseOrder(Map.Entry.comparingByValue())).forEach(entry -> {
            if (entry.getValue() > 0)
                System.out.println(entry.getValue() + "x " + entry.getKey() + " (" + df.format(entry.getValue()
                        .doubleValue() / codeSmellSize * 100) + "%)");
        });

        StringBuilder sbQ2 = new StringBuilder(projectName + ";" + codeSmellSize + ";");
        sbQ2.append(q2(smellsTypes));
        sbQ2.append("\n");

        File q2File = new File(PATH_TO_SAVEDIR + "q2.csv");
        StringBuilder sbQ2ToFile = new StringBuilder();
        if (!q2File.exists()) {
            String header = "Project;Javadoc_Code_Smells;Method_has_no_javadoc;Constructor_has_no_javadoc;" +
                    "Class_has_no_javadoc;Interface_has_no_javadoc;Enum_has_no_javadoc;Field_has_no_javadoc;" +
                    "AnnotationType_has_no_javadoc;Package_has_no_javadoc;Tag_is_not_allowed;" +
                    "Missing_package-info_for_package;Missing_package-info_for_typeless_package;Missing_tag_@author;" +
                    "Missing_tag_@version;Missing_tag_@deprecated;Missing_tag_@return;Missing_tag_@param;" +
                    "Missing_tag_@throws;Javadoc_contains_@deprecated;Javadoc_contains_@return;Javadoc_contains_@param;" +
                    "Javadoc_contains_@throws;No_description_in_javadoc;Short-description_too_short;Long-description_too_short;" +
                    "Total-description_too_short;Too_short_tag_@deprecated;Too_short_tag_@serialData;Too_short_tag_@serialField;" +
                    "Too_short_tag_@serial;Too_short_tag_@see;Too_short_tag_@since;Too_short_tag_@version;" +
                    "Too_short_tag_@author;Too_short_tag_@return;Too_short_tag_@param;Too_short_tag_@exception;" +
                    "Too_short_tag_@throws";
            sbQ2ToFile.append(header);
            sbQ2ToFile.append("\n");
        }
        sbQ2ToFile.append(sbQ2);

        try (FileWriter writer = new FileWriter(q2File, true)) {
            writer.write(sbQ2ToFile.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Wertet die untersuchten Daten aus der Sicht der Javadocables aus.
     *
     * @param projectName                des analysierten Projektes.
     * @param inversSetWithoutDuplicates sind die gefundenen Javadocable-Arten der Projekte ohne Duplikate.
     * @param inversSmellTypeWithClass   Map die die Javadocable-Art und dazu als Wert den Code Smell enthält.
     */
    private static void evaluateQ2Javadocable(
            String projectName, LinkedHashSet<String> inversSetWithoutDuplicates,
            Multimap<String, String> inversSmellTypeWithClass
    ) {
        for (String key : inversSetWithoutDuplicates) {
            HashMap<String, Integer> countedSmellTypes = new HashMap<>();
            initializeMap(countedSmellTypes);
            for (String value : inversSmellTypeWithClass.get(key)) {
                countedSmellTypes.put(value, countedSmellTypes.get(value) + 1);
            }
            System.out.println();
            System.out.println("Es wurden " + inversSmellTypeWithClass.keys()
                    .count(key) + " Code Smells in " + key + " gefunden.");
            countedSmellTypes.entrySet().stream().sorted(reverseOrder(Map.Entry.comparingByValue())).forEach(entry -> {
                if (entry.getValue() > 0)
                    System.out.println(entry.getValue() + "x der Code Smell Type \"" + entry.getKey() + "\".");
            });

            String header = "Project;Javadoc_Code_Smells;Method_has_no_javadoc;Constructor_has_no_javadoc;" +
                    "Class_has_no_javadoc;Interface_has_no_javadoc;Enum_has_no_javadoc;Field_has_no_javadoc;" +
                    "AnnotationType_has_no_javadoc;Package_has_no_javadoc;Tag_is_not_allowed;" +
                    "Missing_package-info_for_package;Missing_package-info_for_typeless_package;Missing_tag_@author;" +
                    "Missing_tag_@version;Missing_tag_@deprecated;Missing_tag_@return;Missing_tag_@param;" +
                    "Missing_tag_@throws;Javadoc_contains_@deprecated;Javadoc_contains_@return;Javadoc_contains_@param;" +
                    "Javadoc_contains_@throws;No_description_in_javadoc;Short-description_too_short;Long-description_too_short;" +
                    "Total-description_too_short;Too_short_tag_@deprecated;Too_short_tag_@serialData;Too_short_tag_@serialField;" +
                    "Too_short_tag_@serial;Too_short_tag_@see;Too_short_tag_@since;Too_short_tag_@version;" +
                    "Too_short_tag_@author;Too_short_tag_@return;Too_short_tag_@param;Too_short_tag_@exception;" +
                    "Too_short_tag_@throws";

            if (key.equals("CtClass")) {
                StringBuilder sbQ2Class = new StringBuilder(projectName + ";" + inversSmellTypeWithClass.keys()
                        .count(key) + ";");
                sbQ2Class.append(q2(countedSmellTypes));
                sbQ2Class.append("\n");

                File q2File = new File(PATH_TO_SAVEDIR + "q2_class.csv");
                StringBuilder sbQ3ToFile = new StringBuilder();
                if (!q2File.exists()) {
                    sbQ3ToFile.append(header);
                    sbQ3ToFile.append("\n");
                }
                sbQ3ToFile.append(sbQ2Class);

                try (FileWriter writer = new FileWriter(q2File, true)) {
                    writer.write(sbQ3ToFile.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (key.equals("CtInterface")) {
                StringBuilder sbQ2Interface = new StringBuilder(projectName + ";" + inversSmellTypeWithClass.keys()
                        .count(key) + ";");
                sbQ2Interface.append(q2(countedSmellTypes));
                sbQ2Interface.append("\n");

                File q2File = new File(PATH_TO_SAVEDIR + "q2_interface.csv");
                StringBuilder sbQ2ToFile = new StringBuilder();
                if (!q2File.exists()) {
                    sbQ2ToFile.append(header);
                    sbQ2ToFile.append("\n");
                }
                sbQ2ToFile.append(sbQ2Interface);

                try (FileWriter writer = new FileWriter(q2File, true)) {
                    writer.write(sbQ2ToFile.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else if (key.equals("CtEnum")) {
                StringBuilder sbQ2Enum = new StringBuilder(projectName + ";" + inversSmellTypeWithClass.keys()
                        .count(key) + ";");
                sbQ2Enum.append(q2(countedSmellTypes));
                sbQ2Enum.append("\n");

                File q2File = new File(PATH_TO_SAVEDIR + "q2_enum.csv");
                StringBuilder sbQ2ToFile = new StringBuilder();
                if (!q2File.exists()) {
                    sbQ2ToFile.append(header);
                    sbQ2ToFile.append("\n");
                }
                sbQ2ToFile.append(sbQ2Enum);

                try (FileWriter writer = new FileWriter(q2File, true)) {
                    writer.write(sbQ2ToFile.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else if (key.equals("CtMethod")) {
                StringBuilder sbQ2Method = new StringBuilder(projectName + ";" + inversSmellTypeWithClass.keys()
                        .count(key) + ";");
                sbQ2Method.append(q2(countedSmellTypes));
                sbQ2Method.append("\n");

                File q2File = new File(PATH_TO_SAVEDIR + "q2_method.csv");
                StringBuilder sbQ2ToFile = new StringBuilder();
                if (!q2File.exists()) {
                    sbQ2ToFile.append(header);
                    sbQ2ToFile.append("\n");
                }
                sbQ2ToFile.append(sbQ2Method);

                try (FileWriter writer = new FileWriter(q2File, true)) {
                    writer.write(sbQ2ToFile.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else if (key.equals("CtConstructor")) {
                StringBuilder sbQ2Constructor = new StringBuilder(projectName + ";" + inversSmellTypeWithClass.keys()
                        .count(key) + ";");
                sbQ2Constructor.append(q2(countedSmellTypes));
                sbQ2Constructor.append("\n");

                File q2File = new File(PATH_TO_SAVEDIR + "q2_constructor.csv");
                StringBuilder sbQ2ToFile = new StringBuilder();
                if (!q2File.exists()) {
                    sbQ2ToFile.append(header);
                    sbQ2ToFile.append("\n");
                }
                sbQ2ToFile.append(sbQ2Constructor);

                try (FileWriter writer = new FileWriter(q2File, true)) {
                    writer.write(sbQ2ToFile.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else if (key.equals("CtField")) {
                StringBuilder sbQ2Field = new StringBuilder(projectName + ";" + inversSmellTypeWithClass.keys()
                        .count(key) + ";");
                sbQ2Field.append(q2(countedSmellTypes));
                sbQ2Field.append("\n");

                File q2File = new File(PATH_TO_SAVEDIR + "q2_field.csv");
                StringBuilder sbQ2ToFile = new StringBuilder();
                if (!q2File.exists()) {
                    sbQ2ToFile.append(header);
                    sbQ2ToFile.append("\n");
                }
                sbQ2ToFile.append(sbQ2Field);

                try (FileWriter writer = new FileWriter(q2File, true)) {
                    writer.write(sbQ2ToFile.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else if (key.equals("CtAnnotationType")) {
                StringBuilder sbQ2Annotation = new StringBuilder(projectName + ";" + inversSmellTypeWithClass.keys()
                        .count(key) + ";");
                sbQ2Annotation.append(q2(countedSmellTypes));
                sbQ2Annotation.append("\n");

                File q2File = new File(PATH_TO_SAVEDIR + "q2_annotationType.csv");
                StringBuilder sbQ2ToFile = new StringBuilder();
                if (!q2File.exists()) {
                    sbQ2ToFile.append(header);
                    sbQ2ToFile.append("\n");
                }
                sbQ2ToFile.append(sbQ2Annotation);

                try (FileWriter writer = new FileWriter(q2File, true)) {
                    writer.write(sbQ2ToFile.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else if (key.equals("CtPackage")) {
                StringBuilder sbQ2Package = new StringBuilder(projectName + ";" + inversSmellTypeWithClass.keys()
                        .count(key) + ";");
                sbQ2Package.append(q2(countedSmellTypes));
                sbQ2Package.append("\n");

                File q2File = new File(PATH_TO_SAVEDIR + "q2_package.csv");
                StringBuilder sbQ2ToFile = new StringBuilder();
                if (!q2File.exists()) {
                    sbQ2ToFile.append(header);
                    sbQ2ToFile.append("\n");
                }
                sbQ2ToFile.append(sbQ2Package);

                try (FileWriter writer = new FileWriter(q2File, true)) {
                    writer.write(sbQ2ToFile.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    /**
     * Wertet die Daten aus der Sicht der Javadoc-Tags aus.
     *
     * @param projectName              das analysierte Projekt
     * @param setWithoutDuplicates     sind die gefundenen Javadoc-Tags in dem Projekt, ohne Duplikate.
     * @param smellTypeWithClass       ist eine Map, die die gefundenen Javadoc Code Smell Arten zu der Javadocable-Art ihres Inhabers zuordnen.
     * @param inversSmellTypeWithClass leere Multimap.
     */
    private static void evaluateQ2Tags(
            String projectName, LinkedHashSet<String> setWithoutDuplicates,
            Multimap<String, String> smellTypeWithClass,
            Multimap<String, String> inversSmellTypeWithClass
    ) {
        for (String key : setWithoutDuplicates) {
            HashMap<String, Integer> smellOccurrences = new HashMap<>();
            initCodeSmellOccurrences(smellOccurrences);
            for (String value : smellTypeWithClass.get(key)) {
                if (value != null && smellOccurrences.get(value) != null) {
                    smellOccurrences.put(value, smellOccurrences.get(value) + 1);
                    inversSmellTypeWithClass.put(value, key);
                }
            }
            System.out.println();
            System.out.println("Der Code Smell Typ \"" + key + "\" wurde " + smellTypeWithClass.keys()
                    .count(key) + "-Mal in den Javadocables gefunden.");
            smellOccurrences.entrySet().stream().sorted(reverseOrder(Map.Entry.comparingByValue())).forEach(entry -> {
                if (entry.getValue() > 0)
                    System.out.println(entry.getValue() + "x in " + entry.getKey() + ", dass entspricht " + df.format(
                            entry.getValue().doubleValue() / smellTypeWithClass.keys()
                                    .count(key) * 100) + "% der ganzen \"" + key + "\" Vorkommen.");
            });


            StringBuilder sbQ2tag = new StringBuilder(projectName + ";" + smellTypeWithClass.keys().count(key) + ";");
            sbQ2tag.append(q2tags(smellOccurrences));
            sbQ2tag.append("\n");

            File dir = new File(PATH_TO_SAVEDIR + "q2_tags");
            if (!dir.exists()) {
                dir.mkdir();
            }

            String keyName = key.replace(" ", "_").toLowerCase();
            File q2tagsFile = new File(PATH_TO_SAVEDIR + "q2_tags/q2_" + keyName + ".csv");

            StringBuilder sbQ2tagToFile = new StringBuilder();
            if (!q2tagsFile.exists()) {
                final String header = "Project;" + keyName + "_in_project;Class;Interface;Enum;AnnotationType;Method;" +
                        "Constructor;Package;Field";
                sbQ2tagToFile.append(header);
                sbQ2tagToFile.append("\n");
            }

            sbQ2tagToFile.append(sbQ2tag);

            try (FileWriter writer = new FileWriter(q2tagsFile, true)) {
                writer.write(sbQ2tagToFile.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * Wertet die Daten für die dritte Forschungsfrage aus.
     *
     * @param projectName   des analysierten Projektes
     * @param modelMap      die Map, die die Sigantur und die Javadocable-Art beinhaltet.
     * @param smellInfected beinhaltet die Signatur und die Javadocable-Art aller befallenen Javadocables.
     * @param smellFree     beihaltet die Signatur und die Javadocable-Art aller unbefallenen Javadocables.
     */
    private static void evaluateQ3(
            String projectName, HashMap<Optional<String>, String> modelMap,
            HashMap<Optional<String>, String> smellInfected, HashMap<Optional<String>, String> smellFree
    ) {
        StringBuilder sbQ3 = new StringBuilder(projectName + ";");

        System.out.println("Insgesamt wurden " + modelMap.size() + " Javadocables im Projekt gefunden.");
        System.out.println("Davon enthalten " + smellInfected.size() + " jeweils Code Smells (" + df.format((double) smellInfected
                .size() / modelMap.size() * 100) + "%).");
        System.out.println("Dahingegen sind " + smellFree.size() + " Javadocables frei von Code Smells (" + df.format((double) smellFree
                .size() / modelMap.size() * 100) + "%).");
        sbQ3.append(modelMap.size() + ";" + smellInfected.size() + ";" + smellFree.size() + ";");
        System.out.println();
        System.out.println("Insgesamt wurden " + countValuesCorrespondence(
                modelMap,
                "CtMethod"
        ) + " Methoden im Projekt gefunden.");
        System.out.println("Davon enthalten " + countValuesCorrespondence(
                smellInfected,
                "CtMethod"
        ) + " jeweils Code Smells (" + df.format((double) countValuesCorrespondence(
                smellInfected,
                "CtMethod"
        ) / countValuesCorrespondence(
                modelMap,
                "CtMethod"
        ) * 100) + "%). Das entspricht " + df.format((double) countValuesCorrespondence(
                smellInfected,
                "CtMethod"
        ) / smellInfected.size() * 100) + "% aller gefundenen Code Smells.");
        System.out.println("Dahingegen sind " + countValuesCorrespondence(
                smellFree,
                "CtMethod"
        ) + " Methoden frei von Code Smells (" + df.format((double) countValuesCorrespondence(
                smellFree,
                "CtMethod"
        ) / countValuesCorrespondence(modelMap, "CtMethod") * 100) + "%).");
        sbQ3.append(countValuesCorrespondence(modelMap, "CtMethod") + ";" + countValuesCorrespondence(
                smellInfected,
                "CtMethod"
        ) + ";" + countValuesCorrespondence(smellFree, "CtMethod") + ";");
        System.out.println();
        System.out.println("Insgesamt wurden " + countValuesCorrespondence(
                modelMap,
                "CtConstructor"
        ) + " Konstruktoren im Projekt gefunden.");
        System.out.println("Davon enthalten " + countValuesCorrespondence(
                smellInfected,
                "CtConstructor"
        ) + " jeweils Code Smells (" + df.format((double) countValuesCorrespondence(
                smellInfected,
                "CtConstructor"
        ) / countValuesCorrespondence(
                modelMap,
                "CtConstructor"
        ) * 100) + "%). Das entspricht " + df.format((double) countValuesCorrespondence(
                smellInfected,
                "CtConstructor"
        ) / smellInfected.size() * 100) + "% aller gefundenen Code Smells.");
        System.out.println("Dahingegen sind " + countValuesCorrespondence(
                smellFree,
                "CtConstructor"
        ) + " Konstruktoren frei von Code Smells (" + df.format((double) countValuesCorrespondence(
                smellFree,
                "CtConstructor"
        ) / countValuesCorrespondence(modelMap, "CtConstructor") * 100) + "%).");
        sbQ3.append(countValuesCorrespondence(
                modelMap,
                "CtConstructor"
        ) + ";" + countValuesCorrespondence(smellInfected, "CtConstructor") + ";" + countValuesCorrespondence(
                smellFree,
                "CtConstructor"
        ) + ";");
        System.out.println();
        System.out.println("Insgesamt wurden " + countValuesCorrespondence(
                modelMap,
                "CtField"
        ) + " Felder im Projekt gefunden.");
        System.out.println("Davon enthalten " + countValuesCorrespondence(
                smellInfected,
                "CtField"
        ) + " jeweils Code Smells (" + df.format((double) countValuesCorrespondence(
                smellInfected,
                "CtField"
        ) / countValuesCorrespondence(
                modelMap,
                "CtField"
        ) * 100) + "%). Das entspricht " + df.format((double) countValuesCorrespondence(
                smellInfected,
                "CtField"
        ) / smellInfected.size() * 100) + "% aller gefundenen Code Smells.");
        System.out.println("Dahingegen sind " + countValuesCorrespondence(
                smellFree,
                "CtField"
        ) + " Felder frei von Code Smells (" + df.format((double) countValuesCorrespondence(
                smellFree,
                "CtField"
        ) / countValuesCorrespondence(modelMap, "CtField") * 100) + "%).");
        sbQ3.append(countValuesCorrespondence(modelMap, "CtField") + ";" + countValuesCorrespondence(
                smellInfected,
                "CtField"
        ) + ";" + countValuesCorrespondence(smellFree, "CtField") + ";");
        System.out.println();
        System.out.println("Insgesamt wurden " + countValuesCorrespondence(
                modelMap,
                "CtClass"
        ) + " Klassen im Projekt gefunden.");
        System.out.println("Davon enthalten " + countValuesCorrespondence(
                smellInfected,
                "CtClass"
        ) + " jeweils Code Smells (" + df.format((double) countValuesCorrespondence(
                smellInfected,
                "CtClass"
        ) / countValuesCorrespondence(
                modelMap,
                "CtClass"
        ) * 100) + "%). Das entspricht " + df.format((double) countValuesCorrespondence(
                smellInfected,
                "CtClass"
        ) / smellInfected.size() * 100) + "% aller gefundenen Code Smells.");
        System.out.println("Dahingegen sind " + countValuesCorrespondence(
                smellFree,
                "CtClass"
        ) + " Klassen frei von Code Smells (" + df.format((double) countValuesCorrespondence(
                smellFree,
                "CtClass"
        ) / countValuesCorrespondence(modelMap, "CtClass") * 100) + "%).");
        sbQ3.append(countValuesCorrespondence(modelMap, "CtClass") + ";" + countValuesCorrespondence(
                smellInfected,
                "CtClass"
        ) + ";" + countValuesCorrespondence(smellFree, "CtClass") + ";");
        System.out.println();
        System.out.println("Insgesamt wurden " + countValuesCorrespondence(
                modelMap,
                "CtEnum"
        ) + " Enum im Projekt gefunden.");
        System.out.println("Davon enthalten " + countValuesCorrespondence(
                smellInfected,
                "CtEnum"
        ) + " jeweils Code Smells (" + df.format((double) countValuesCorrespondence(
                smellInfected,
                "CtEnum"
        ) / countValuesCorrespondence(
                modelMap,
                "CtEnum"
        ) * 100) + "%). Das entspricht " + df.format((double) countValuesCorrespondence(
                smellInfected,
                "CtEnum"
        ) / smellInfected.size() * 100) + "% aller gefundenen Code Smells.");
        System.out.println("Dahingegen sind " + countValuesCorrespondence(
                smellFree,
                "CtEnum"
        ) + " Enum frei von Code Smells (" + df.format((double) countValuesCorrespondence(
                smellFree,
                "CtEnum"
        ) / countValuesCorrespondence(modelMap, "CtEnum") * 100) + "%).");
        sbQ3.append(countValuesCorrespondence(modelMap, "CtEnum") + ";" + countValuesCorrespondence(
                smellInfected,
                "CtEnum"
        ) + ";" + countValuesCorrespondence(smellFree, "CtEnum") + ";");
        System.out.println();
        System.out.println("Insgesamt wurden " + countValuesCorrespondence(
                modelMap,
                "CtInterface"
        ) + " Interfaces im Projekt gefunden.");
        System.out.println("Davon enthalten " + countValuesCorrespondence(
                smellInfected,
                "CtInterface"
        ) + " jeweils Code Smells (" + df.format((double) countValuesCorrespondence(
                smellInfected,
                "CtInterface"
        ) / countValuesCorrespondence(
                modelMap,
                "CtInterface"
        ) * 100) + "%). Das entspricht " + df.format((double) countValuesCorrespondence(
                smellInfected,
                "CtInterface"
        ) / smellInfected.size() * 100) + "% aller gefundenen Code Smells.");
        System.out.println("Dahingegen sind " + countValuesCorrespondence(
                smellFree,
                "CtInterface"
        ) + " Interfaces frei von Code Smells (" + df.format((double) countValuesCorrespondence(
                smellFree,
                "CtInterface"
        ) / countValuesCorrespondence(modelMap, "CtInterface") * 100) + "%).");
        sbQ3.append(countValuesCorrespondence(modelMap, "CtInterface") + ";" + countValuesCorrespondence(
                smellInfected,
                "CtInterface"
        ) + ";" + countValuesCorrespondence(smellFree, "CtInterface") + ";");
        System.out.println();
        System.out.println("Insgesamt wurden " + countValuesCorrespondence(
                modelMap,
                "CtAnnotationType"
        ) + " AnnotationTypes im Projekt gefunden.");
        System.out.println("Davon enthalten " + countValuesCorrespondence(
                smellInfected,
                "CtAnnotationType"
        ) + " jeweils Code Smells (" + df.format((double) countValuesCorrespondence(
                smellInfected,
                "CtAnnotationType"
        ) / countValuesCorrespondence(
                modelMap,
                "CtAnnotationType"
        ) * 100) + "%). Das entspricht " + df.format((double) countValuesCorrespondence(
                smellInfected,
                "CtAnnotationType"
        ) / smellInfected.size() * 100) + "% aller gefundenen Code Smells.");
        System.out.println("Dahingegen sind " + countValuesCorrespondence(
                smellFree,
                "CtAnnotationType"
        ) + " AnnotationTypes frei von Code Smells (" + df.format((double) countValuesCorrespondence(
                smellFree,
                "CtAnnotationType"
        ) / countValuesCorrespondence(modelMap, "CtAnnotationType") * 100) + "%).");
        sbQ3.append(countValuesCorrespondence(modelMap, "CtAnnotationType") + ";" + countValuesCorrespondence(
                smellInfected,
                "CtAnnotationType"
        ) + ";" + countValuesCorrespondence(smellFree, "CtAnnotationType") + ";");
        System.out.println();
        System.out.println("Insgesamt wurden " + countValuesCorrespondence(
                modelMap,
                "CtPackage"
        ) + " Packages im Projekt gefunden.");
        System.out.println("Davon enthalten " + countValuesCorrespondence(
                smellInfected,
                "CtPackage"
        ) + " jeweils Code Smells (" + df.format((double) countValuesCorrespondence(
                smellInfected,
                "CtPackage"
        ) / countValuesCorrespondence(
                modelMap,
                "CtPackage"
        ) * 100) + "%). Das entspricht " + df.format((double) countValuesCorrespondence(
                smellInfected,
                "CtPackage"
        ) / smellInfected.size() * 100) + "% aller gefundenen Code Smells.");
        System.out.println("Dahingegen sind " + countValuesCorrespondence(
                smellFree,
                "CtPackage"
        ) + " Packages frei von Code Smells (" + df.format((double) countValuesCorrespondence(
                smellFree,
                "CtPackage"
        ) / countValuesCorrespondence(modelMap, "CtPackage") * 100) + "%).");
        sbQ3.append(countValuesCorrespondence(modelMap, "CtPackage") + ";" + countValuesCorrespondence(
                smellInfected,
                "CtPackage"
        ) + ";" + countValuesCorrespondence(smellFree, "CtPackage") + ";");
        sbQ3.append("\n");

        File q3File = new File(PATH_TO_SAVEDIR + "q3.csv");
        StringBuilder sbQ3ToFile = new StringBuilder();
        if (!q3File.exists()) {
            String header = "Project;Javadocables_in_project;Javadocables_with_Code_Smells;Javadocables_without_Code_Smells;" +
                    "Methods_in_project;Methods_with_Code_Smells;Methods_without_Code_Smells;" +
                    "Constructors_in_project;Constructors_with_Code_Smells;Constructors_without_Code_Smells;" +
                    "Fields_in_project;Fields_with_Code_Smells;Fields_without_Code_Smells;" +
                    "Classes_in_project;Class_with_Code_Smells;Class_without_Code_Smells;" +
                    "Enums_in_project;Enums_with_Code_Smells;Enums_without_Code_Smells;" +
                    "Interfaces_in_project;Interfaces_with_Code_Smells;Interfaces_without_Code_Smells;" +
                    "AnnotationTypes_in_project;AnnotationTypes_with_Code_Smells;AnnotationTypes_without_Code_Smells;" +
                    "Packages_in_project;Packages_with_Code_Smells;Packages_without_Code_Smells";
            sbQ3ToFile.append(header);
            sbQ3ToFile.append("\n");
        }
        sbQ3ToFile.append(sbQ3);

        try (FileWriter writer = new FileWriter(q3File, true)) {
            writer.write(sbQ3ToFile.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
