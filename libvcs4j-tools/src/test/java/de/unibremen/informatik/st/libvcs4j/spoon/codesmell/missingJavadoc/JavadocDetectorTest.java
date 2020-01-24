package de.unibremen.informatik.st.libvcs4j.spoon.codesmell.missingJavadoc;

import de.unibremen.informatik.st.libvcs4j.RevisionRange;
import de.unibremen.informatik.st.libvcs4j.spoon.Environment;
import de.unibremen.informatik.st.libvcs4j.spoon.codesmell.CodeSmell;
import de.unibremen.informatik.st.libvcs4j.spoon.codesmell.RevisionMock;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import spoon.Launcher;
import spoon.reflect.CtModel;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class JavadocDetectorTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    /**
     * TODO
     *
     * @param codeSmells
     * @return
     */
    private Multimap<Optional<String>, CodeSmell> mappedCodeSmell(List<CodeSmell> codeSmells) {
        Multimap<Optional<String>, CodeSmell> codeSmellMap = ArrayListMultimap.create();
        for (CodeSmell smell : codeSmells) {
            codeSmellMap.put(smell.getSignature(), smell);
        }
        assertTrue(codeSmellMap.values().containsAll(codeSmells));
        return codeSmellMap;
    }

    private void setMethodTags(JavadocDetector javadocDetector) {
        javadocDetector.withParam(5);
        javadocDetector.withReturn(3);
        javadocDetector.withThrows(5);
        javadocDetector.withDeprecated(5);
        javadocDetector.withSee(3);
        javadocDetector.withSerialData(5);
        javadocDetector.withSince(1);
    }

    @Test
    public void testMethodJavadoc() throws IOException {
        RevisionMock revision = new RevisionMock(folder);
        revision.addFile(Paths.get("missing_javadoc", "MethodTest.java"));

        Launcher launcher = new Launcher();
        launcher.getEnvironment().setAutoImports(true);
        launcher.addInputResource(folder.getRoot().getAbsolutePath());
        CtModel model = launcher.buildModel();

        RevisionRange revisionRange = mock(RevisionRange.class);
        when(revisionRange.getRevision()).thenReturn(revision);

        final var env = new Environment(model, revisionRange);

        JavadocDetector javadocDetector = new JavadocDetector(env);
        javadocDetector.withMethod(5, 15);
        javadocDetector.withMethodFieldAccess(5);
        javadocDetector.withAllAccessModifier();
        setMethodTags(javadocDetector);
        javadocDetector.scan(model);
        assertEquals(33, javadocDetector.getCodeSmells().size());

        Multimap smellMap = mappedCodeSmell(javadocDetector.getCodeSmells());

        assertEquals(1, smellMap.keys().count(Optional.of("missing_javadoc.MethodTest#getFieldAccessTest()")));
        assertEquals(
                "Method getFieldAccessTest() has no javadoc.",
                ((List<CodeSmell>) smellMap.get(Optional.of("missing_javadoc.MethodTest#getFieldAccessTest()")))
                        .get(0).getSummary().get()
        );

        assertEquals(1, smellMap.keys().count(Optional.of("missing_javadoc.MethodTest#setFieldAccessTest(int)")));
        assertEquals(
                "Method setFieldAccessTest(int) has no javadoc.",
                ((List<CodeSmell>) smellMap.get(Optional.of("missing_javadoc.MethodTest#setFieldAccessTest(int)")))
                        .get(0).getSummary().get()
        );

        assertEquals(
                11,
                smellMap.keys().count(Optional.of("missing_javadoc.MethodTest#visitCtMethod(missing_javadoc.CtMethod)"))
        );
        List<CodeSmell> codeSmellsVCM = (List<CodeSmell>) smellMap.get(Optional.of(
                "missing_javadoc.MethodTest#visitCtMethod(missing_javadoc.CtMethod)"));
        assertEquals("Tag @author is not allowed for this javadocable.", codeSmellsVCM.get(0).getSummary().get());
        assertEquals("Tag @version is not allowed for this javadocable.", codeSmellsVCM.get(1).getSummary().get());
        assertEquals("Tag @unknown is not allowed for this javadocable.", codeSmellsVCM.get(2).getSummary().get());
        assertEquals(
                "Javadoc contains @throws/@exception IllegalAccessException, but this does not thrown by this or a called executable.",
                codeSmellsVCM.get(3).getSummary().get()
        );
        assertEquals(
                "Javadoc contains @throws/@exception IllegalArgumentException, but this does not thrown by this or a called executable.",
                codeSmellsVCM.get(4).getSummary().get()
        );
        assertEquals(
                "Javadoc contains @deprecated, but the annotation @Deprecated at the javadocable is missing.",
                codeSmellsVCM.get(5).getSummary().get()
        );
        assertEquals(
                "Javadoc contains @return, but the method has no return value.",
                codeSmellsVCM.get(6).getSummary().get()
        );
        assertEquals("Missing tag @param method in the javadoc.", codeSmellsVCM.get(7).getSummary().get());
        assertEquals("Missing tag @param <E> in the javadoc.", codeSmellsVCM.get(8).getSummary().get());
        assertEquals(
                "Javadoc contains @param dingdong, but this parameter does not exists.",
                codeSmellsVCM.get(9).getSummary().get()
        );
        assertEquals(
                "Javadoc contains @param <T>, but this parameter does not exists.",
                codeSmellsVCM.get(10).getSummary().get()
        );

        assertEquals(3, smellMap.keys().count(Optional.of("missing_javadoc.MethodTest#numberBetweenOneAndFour(int)")));
        List<CodeSmell> codeSmellsNBOAF = (List<CodeSmell>) smellMap.get(Optional.of(
                "missing_javadoc.MethodTest#numberBetweenOneAndFour(int)"));
        assertEquals(
                "Missing tag @throws IllegalArgumentException in the javadoc.",
                codeSmellsNBOAF.get(0).getSummary().get()
        );
        assertEquals("Missing tag @deprecated in the javadoc.", codeSmellsNBOAF.get(1).getSummary().get());
        assertEquals("Missing tag @return in the javadoc.", codeSmellsNBOAF.get(2).getSummary().get());


        assertEquals(11, smellMap.keys().count(Optional.of("missing_javadoc.MethodTest#charRepeater(char,int)")));
        List<CodeSmell> codeSmellsCR = (List<CodeSmell>) smellMap.get(Optional.of(
                "missing_javadoc.MethodTest#charRepeater(char,int)"));
        assertEquals("Description of the @param c is too short.", codeSmellsCR.get(0).getSummary().get());
        assertEquals("Description of the @param repeatNumber is too short.", codeSmellsCR.get(1).getSummary().get());
        assertEquals("Description of the @return tag is too short.", codeSmellsCR.get(2).getSummary().get());
        assertEquals(
                "Description of the @throws IllegalArgumentException is too short.",
                codeSmellsCR.get(3).getSummary().get()
        );
        assertEquals(
                "Description of the @exception IllegalStateException is too short.",
                codeSmellsCR.get(4).getSummary().get()
        );
        assertEquals("Description of the @see tag is too short.", codeSmellsCR.get(5).getSummary().get());
        assertEquals("Description of the @since tag is too short.", codeSmellsCR.get(6).getSummary().get());
        assertEquals("Description of the @deprecated tag is too short.", codeSmellsCR.get(7).getSummary().get());
        assertEquals("Description of the @serialData tag is too short.", codeSmellsCR.get(8).getSummary().get());
        assertEquals("Short-description of this javadoc is too short.", codeSmellsCR.get(9).getSummary().get());
        assertEquals("Long-description of this javadoc is too short.", codeSmellsCR.get(10).getSummary().get());

        assertEquals(
                3,
                smellMap.keys().count(Optional.of("missing_javadoc.MethodTest#noDescription(java.lang.String)"))
        );
        List<CodeSmell> codeSmellsND = (List<CodeSmell>) smellMap.get(Optional.of(
                "missing_javadoc.MethodTest#noDescription(java.lang.String)"));
        assertEquals("Missing tag @return in the javadoc.", codeSmellsND.get(0).getSummary().get());
        assertEquals("Missing tag @param text in the javadoc.", codeSmellsND.get(1).getSummary().get());
        assertEquals("No description existing in this javadoc.", codeSmellsND.get(2).getSummary().get());

        assertEquals(
                1,
                smellMap.keys()
                        .count(Optional.of("missing_javadoc.MethodTest#writeToFile(java.lang.String,java.lang.String)"))
        );
        assertEquals(
                "Method writeToFile(java.lang.String,java.lang.String) has no javadoc.",
                ((List<CodeSmell>) smellMap.get(Optional.of(
                        "missing_javadoc.MethodTest#writeToFile(java.lang.String,java.lang.String)")))
                        .get(0).getSummary().get()
        );

        assertEquals(
                2,
                smellMap.keys()
                        .count(Optional.of(
                                "missing_javadoc.MethodTest#readAndWriteJSON(java.lang.String,java.lang.String)"))
        );
        List<CodeSmell> codeSmellsRWJSON = (List<CodeSmell>) smellMap.get(Optional.of(
                "missing_javadoc.MethodTest#readAndWriteJSON(java.lang.String,java.lang.String)"));
        assertEquals(
                "Missing tag @throws IllegalFormatException in the javadoc.",
                codeSmellsRWJSON.get(0).getSummary().get()
        );
        assertEquals("Missing tag @throws ParseException in the javadoc.", codeSmellsRWJSON.get(1).getSummary().get());

        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.MethodTest#readJSON(java.lang.String)")));
        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.MethodTest#negativNumber(int)")));
        assertEquals(
                0,
                smellMap.keys()
                        .count(Optional.of("missing_javadoc.MethodTest#getFieldAccessTestForDescriptionLength()"))
        );
    }

    @Test
    public void testMethodWithoutFieldAccess() throws IOException {
        RevisionMock revision = new RevisionMock(folder);
        revision.addFile(Paths.get("missing_javadoc", "MethodTest.java"));

        Launcher launcher = new Launcher();
        launcher.getEnvironment().setAutoImports(true);
        launcher.addInputResource(folder.getRoot().getAbsolutePath());
        CtModel model = launcher.buildModel();

        RevisionRange revisionRange = mock(RevisionRange.class);
        when(revisionRange.getRevision()).thenReturn(revision);

        final var env = new Environment(model, revisionRange);

        JavadocDetector javadocDetectorWithoutFieldAccess = new JavadocDetector(env);
        javadocDetectorWithoutFieldAccess.withMethod(20);
        javadocDetectorWithoutFieldAccess.withAllAccessModifier();
        setMethodTags(javadocDetectorWithoutFieldAccess);
        javadocDetectorWithoutFieldAccess.withoutFieldAccessMustBeDocumented();
        javadocDetectorWithoutFieldAccess.scan(model);
        assertEquals(30, javadocDetectorWithoutFieldAccess.getCodeSmells().size());
        Multimap smellMap = mappedCodeSmell(javadocDetectorWithoutFieldAccess.getCodeSmells());

        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.MethodTest#getFieldAccessTest()")));
        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.MethodTest#setFieldAccessTest(int)")));

        assertEquals(
                11,
                smellMap.keys().count(Optional.of("missing_javadoc.MethodTest#visitCtMethod(missing_javadoc.CtMethod)"))
        );
        List<CodeSmell> codeSmellsVCM = (List<CodeSmell>) smellMap.get(Optional.of(
                "missing_javadoc.MethodTest#visitCtMethod(missing_javadoc.CtMethod)"));
        assertEquals("Tag @author is not allowed for this javadocable.", codeSmellsVCM.get(0).getSummary().get());
        assertEquals("Tag @version is not allowed for this javadocable.", codeSmellsVCM.get(1).getSummary().get());
        assertEquals("Tag @unknown is not allowed for this javadocable.", codeSmellsVCM.get(2).getSummary().get());
        assertEquals(
                "Javadoc contains @throws/@exception IllegalAccessException, but this does not thrown by this or a called executable.",
                codeSmellsVCM.get(3).getSummary().get()
        );
        assertEquals(
                "Javadoc contains @throws/@exception IllegalArgumentException, but this does not thrown by this or a called executable.",
                codeSmellsVCM.get(4).getSummary().get()
        );
        assertEquals(
                "Javadoc contains @deprecated, but the annotation @Deprecated at the javadocable is missing.",
                codeSmellsVCM.get(5).getSummary().get()
        );
        assertEquals(
                "Javadoc contains @return, but the method has no return value.",
                codeSmellsVCM.get(6).getSummary().get()
        );
        assertEquals("Missing tag @param method in the javadoc.", codeSmellsVCM.get(7).getSummary().get());
        assertEquals("Missing tag @param <E> in the javadoc.", codeSmellsVCM.get(8).getSummary().get());
        assertEquals(
                "Javadoc contains @param dingdong, but this parameter does not exists.",
                codeSmellsVCM.get(9).getSummary().get()
        );
        assertEquals(
                "Javadoc contains @param <T>, but this parameter does not exists.",
                codeSmellsVCM.get(10).getSummary().get()
        );


        assertEquals(3, smellMap.keys().count(Optional.of("missing_javadoc.MethodTest#numberBetweenOneAndFour(int)")));
        List<CodeSmell> codeSmellsNBOAF = (List<CodeSmell>) smellMap.get(Optional.of(
                "missing_javadoc.MethodTest#numberBetweenOneAndFour(int)"));
        assertEquals(
                "Missing tag @throws IllegalArgumentException in the javadoc.",
                codeSmellsNBOAF.get(0).getSummary().get()
        );
        assertEquals("Missing tag @deprecated in the javadoc.", codeSmellsNBOAF.get(1).getSummary().get());
        assertEquals("Missing tag @return in the javadoc.", codeSmellsNBOAF.get(2).getSummary().get());


        assertEquals(10, smellMap.keys().count(Optional.of("missing_javadoc.MethodTest#charRepeater(char,int)")));
        List<CodeSmell> codeSmellsCR = (List<CodeSmell>) smellMap.get(Optional.of(
                "missing_javadoc.MethodTest#charRepeater(char,int)"));
        assertEquals("Description of the @param c is too short.", codeSmellsCR.get(0).getSummary().get());
        assertEquals("Description of the @param repeatNumber is too short.", codeSmellsCR.get(1).getSummary().get());
        assertEquals("Description of the @return tag is too short.", codeSmellsCR.get(2).getSummary().get());
        assertEquals(
                "Description of the @throws IllegalArgumentException is too short.",
                codeSmellsCR.get(3).getSummary().get()
        );
        assertEquals(
                "Description of the @exception IllegalStateException is too short.",
                codeSmellsCR.get(4).getSummary().get()
        );
        assertEquals("Description of the @see tag is too short.", codeSmellsCR.get(5).getSummary().get());
        assertEquals("Description of the @since tag is too short.", codeSmellsCR.get(6).getSummary().get());
        assertEquals("Description of the @deprecated tag is too short.", codeSmellsCR.get(7).getSummary().get());
        assertEquals("Description of the @serialData tag is too short.", codeSmellsCR.get(8).getSummary().get());
        assertEquals("Total-description of this javadoc is too short.", codeSmellsCR.get(9).getSummary().get());

        assertEquals(
                3,
                smellMap.keys().count(Optional.of("missing_javadoc.MethodTest#noDescription(java.lang.String)"))
        );
        List<CodeSmell> codeSmellsND = (List<CodeSmell>) smellMap.get(Optional.of(
                "missing_javadoc.MethodTest#noDescription(java.lang.String)"));
        assertEquals("Missing tag @return in the javadoc.", codeSmellsND.get(0).getSummary().get());
        assertEquals("Missing tag @param text in the javadoc.", codeSmellsND.get(1).getSummary().get());
        assertEquals("No description existing in this javadoc.", codeSmellsND.get(2).getSummary().get());

        assertEquals(
                1,
                smellMap.keys()
                        .count(Optional.of("missing_javadoc.MethodTest#writeToFile(java.lang.String,java.lang.String)"))
        );
        assertEquals(
                "Method writeToFile(java.lang.String,java.lang.String) has no javadoc.",
                ((List<CodeSmell>) smellMap.get(Optional.of(
                        "missing_javadoc.MethodTest#writeToFile(java.lang.String,java.lang.String)")))
                        .get(0).getSummary().get()
        );

        assertEquals(
                2,
                smellMap.keys()
                        .count(Optional.of(
                                "missing_javadoc.MethodTest#readAndWriteJSON(java.lang.String,java.lang.String)"))
        );
        List<CodeSmell> codeSmellsRWJSON = (List<CodeSmell>) smellMap.get(Optional.of(
                "missing_javadoc.MethodTest#readAndWriteJSON(java.lang.String,java.lang.String)"));
        assertEquals(
                "Missing tag @throws IllegalFormatException in the javadoc.",
                codeSmellsRWJSON.get(0).getSummary().get()
        );
        assertEquals("Missing tag @throws ParseException in the javadoc.", codeSmellsRWJSON.get(1).getSummary().get());

        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.MethodTest#readJSON(java.lang.String)")));
        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.MethodTest#negativNumber(int)")));
        assertEquals(
                0,
                smellMap.keys()
                        .count(Optional.of("missing_javadoc.MethodTest#getFieldAccessTestForDescriptionLength()"))
        );

    }

    @Test
    public void testMethodWithoutReturn() throws IOException {
        RevisionMock revision = new RevisionMock(folder);
        revision.addFile(Paths.get("missing_javadoc", "MethodTest.java"));

        Launcher launcher = new Launcher();
        launcher.getEnvironment().setAutoImports(true);
        launcher.addInputResource(folder.getRoot().getAbsolutePath());
        CtModel model = launcher.buildModel();

        RevisionRange revisionRange = mock(RevisionRange.class);
        when(revisionRange.getRevision()).thenReturn(revision);

        final var env = new Environment(model, revisionRange);

        JavadocDetector javadocDetectorWithoutReturn = new JavadocDetector(env);
        javadocDetectorWithoutReturn.withMethod(5, 15);
        javadocDetectorWithoutReturn.withMethodFieldAccess(10);
        javadocDetectorWithoutReturn.withAllAccessModifier();
        setMethodTags(javadocDetectorWithoutReturn);
        javadocDetectorWithoutReturn.withoutReturn();
        javadocDetectorWithoutReturn.scan(model);
        assertEquals(30, javadocDetectorWithoutReturn.getCodeSmells().size());
        Multimap smellMap = mappedCodeSmell(javadocDetectorWithoutReturn.getCodeSmells());

        assertEquals(1, smellMap.keys().count(Optional.of("missing_javadoc.MethodTest#getFieldAccessTest()")));
        assertEquals(
                "Method getFieldAccessTest() has no javadoc.",
                ((List<CodeSmell>) smellMap.get(Optional.of("missing_javadoc.MethodTest#getFieldAccessTest()")))
                        .get(0).getSummary().get()
        );

        assertEquals(1, smellMap.keys().count(Optional.of("missing_javadoc.MethodTest#setFieldAccessTest(int)")));
        assertEquals(
                "Method setFieldAccessTest(int) has no javadoc.",
                ((List<CodeSmell>) smellMap.get(Optional.of("missing_javadoc.MethodTest#setFieldAccessTest(int)")))
                        .get(0).getSummary().get()
        );

        assertEquals(
                10,
                smellMap.keys().count(Optional.of("missing_javadoc.MethodTest#visitCtMethod(missing_javadoc.CtMethod)"))
        );
        List<CodeSmell> codeSmellsVCM = (List<CodeSmell>) smellMap.get(Optional.of(
                "missing_javadoc.MethodTest#visitCtMethod(missing_javadoc.CtMethod)"));
        assertEquals("Tag @author is not allowed for this javadocable.", codeSmellsVCM.get(0).getSummary().get());
        assertEquals("Tag @version is not allowed for this javadocable.", codeSmellsVCM.get(1).getSummary().get());
        assertEquals("Tag @unknown is not allowed for this javadocable.", codeSmellsVCM.get(2).getSummary().get());
        assertEquals(
                "Javadoc contains @throws/@exception IllegalAccessException, but this does not thrown by this or a called executable.",
                codeSmellsVCM.get(3).getSummary().get()
        );
        assertEquals(
                "Javadoc contains @throws/@exception IllegalArgumentException, but this does not thrown by this or a called executable.",
                codeSmellsVCM.get(4).getSummary().get()
        );
        assertEquals(
                "Javadoc contains @deprecated, but the annotation @Deprecated at the javadocable is missing.",
                codeSmellsVCM.get(5).getSummary().get()
        );
        assertEquals("Missing tag @param method in the javadoc.", codeSmellsVCM.get(6).getSummary().get());
        assertEquals("Missing tag @param <E> in the javadoc.", codeSmellsVCM.get(7).getSummary().get());
        assertEquals(
                "Javadoc contains @param dingdong, but this parameter does not exists.",
                codeSmellsVCM.get(8).getSummary().get()
        );
        assertEquals(
                "Javadoc contains @param <T>, but this parameter does not exists.",
                codeSmellsVCM.get(9).getSummary().get()
        );


        assertEquals(2, smellMap.keys().count(Optional.of("missing_javadoc.MethodTest#numberBetweenOneAndFour(int)")));
        List<CodeSmell> codeSmellsNBOAF = (List<CodeSmell>) smellMap.get(Optional.of(
                "missing_javadoc.MethodTest#numberBetweenOneAndFour(int)"));
        assertEquals(
                "Missing tag @throws IllegalArgumentException in the javadoc.",
                codeSmellsNBOAF.get(0).getSummary().get()
        );
        assertEquals("Missing tag @deprecated in the javadoc.", codeSmellsNBOAF.get(1).getSummary().get());

        assertEquals(10, smellMap.keys().count(Optional.of("missing_javadoc.MethodTest#charRepeater(char,int)")));
        List<CodeSmell> codeSmellsCR = (List<CodeSmell>) smellMap.get(Optional.of(
                "missing_javadoc.MethodTest#charRepeater(char,int)"));
        assertEquals("Description of the @param c is too short.", codeSmellsCR.get(0).getSummary().get());
        assertEquals("Description of the @param repeatNumber is too short.", codeSmellsCR.get(1).getSummary().get());
        assertEquals(
                "Description of the @throws IllegalArgumentException is too short.",
                codeSmellsCR.get(2).getSummary().get()
        );
        assertEquals(
                "Description of the @exception IllegalStateException is too short.",
                codeSmellsCR.get(3).getSummary().get()
        );
        assertEquals("Description of the @see tag is too short.", codeSmellsCR.get(4).getSummary().get());
        assertEquals("Description of the @since tag is too short.", codeSmellsCR.get(5).getSummary().get());
        assertEquals("Description of the @deprecated tag is too short.", codeSmellsCR.get(6).getSummary().get());
        assertEquals("Description of the @serialData tag is too short.", codeSmellsCR.get(7).getSummary().get());
        assertEquals("Short-description of this javadoc is too short.", codeSmellsCR.get(8).getSummary().get());
        assertEquals("Long-description of this javadoc is too short.", codeSmellsCR.get(9).getSummary().get());

        assertEquals(
                2,
                smellMap.keys().count(Optional.of("missing_javadoc.MethodTest#noDescription(java.lang.String)"))
        );
        List<CodeSmell> codeSmellsND = (List<CodeSmell>) smellMap.get(Optional.of(
                "missing_javadoc.MethodTest#noDescription(java.lang.String)"));
        assertEquals("Missing tag @param text in the javadoc.", codeSmellsND.get(0).getSummary().get());
        assertEquals("No description existing in this javadoc.", codeSmellsND.get(1).getSummary().get());

        assertEquals(
                1,
                smellMap.keys()
                        .count(Optional.of("missing_javadoc.MethodTest#writeToFile(java.lang.String,java.lang.String)"))
        );
        assertEquals(
                "Method writeToFile(java.lang.String,java.lang.String) has no javadoc.",
                ((List<CodeSmell>) smellMap.get(Optional.of(
                        "missing_javadoc.MethodTest#writeToFile(java.lang.String,java.lang.String)")))
                        .get(0).getSummary().get()
        );

        assertEquals(
                2,
                smellMap.keys()
                        .count(Optional.of(
                                "missing_javadoc.MethodTest#readAndWriteJSON(java.lang.String,java.lang.String)"))
        );
        List<CodeSmell> codeSmellsRWJSON = (List<CodeSmell>) smellMap.get(Optional.of(
                "missing_javadoc.MethodTest#readAndWriteJSON(java.lang.String,java.lang.String)"));
        assertEquals(
                "Missing tag @throws IllegalFormatException in the javadoc.",
                codeSmellsRWJSON.get(0).getSummary().get()
        );
        assertEquals("Missing tag @throws ParseException in the javadoc.", codeSmellsRWJSON.get(1).getSummary().get());

        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.MethodTest#readJSON(java.lang.String)")));
        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.MethodTest#negativNumber(int)")));

        assertEquals(
                1,
                smellMap.keys()
                        .count(Optional.of("missing_javadoc.MethodTest#getFieldAccessTestForDescriptionLength()"))
        );
        assertEquals(
                "Total-description of this javadoc is too short.",
                ((List<CodeSmell>) smellMap.get(Optional.of(
                        "missing_javadoc.MethodTest#getFieldAccessTestForDescriptionLength()")))
                        .get(0).getSummary().get()
        );
    }

    @Test
    public void testMethodWithoutParam() throws IOException {
        RevisionMock revision = new RevisionMock(folder);
        revision.addFile(Paths.get("missing_javadoc", "MethodTest.java"));

        Launcher launcher = new Launcher();
        launcher.getEnvironment().setAutoImports(true);
        launcher.addInputResource(folder.getRoot().getAbsolutePath());
        CtModel model = launcher.buildModel();

        RevisionRange revisionRange = mock(RevisionRange.class);
        when(revisionRange.getRevision()).thenReturn(revision);

        final var env = new Environment(model, revisionRange);

        JavadocDetector javadocDetector = new JavadocDetector(env);

        JavadocDetector javadocDetectorWithoutParam = new JavadocDetector(env);
        javadocDetectorWithoutParam.withMethod(5, 15);
        javadocDetectorWithoutParam.withMethodFieldAccess(5);
        javadocDetectorWithoutParam.withAllAccessModifier();
        setMethodTags(javadocDetectorWithoutParam);
        javadocDetectorWithoutParam.withoutParam();
        javadocDetectorWithoutParam.scan(model);
        assertEquals(26, javadocDetectorWithoutParam.getCodeSmells().size());
        Multimap smellMap = mappedCodeSmell(javadocDetectorWithoutParam.getCodeSmells());

        assertEquals(1, smellMap.keys().count(Optional.of("missing_javadoc.MethodTest#getFieldAccessTest()")));
        assertEquals(
                "Method getFieldAccessTest() has no javadoc.",
                ((List<CodeSmell>) smellMap.get(Optional.of("missing_javadoc.MethodTest#getFieldAccessTest()")))
                        .get(0).getSummary().get()
        );

        assertEquals(1, smellMap.keys().count(Optional.of("missing_javadoc.MethodTest#setFieldAccessTest(int)")));
        assertEquals(
                "Method setFieldAccessTest(int) has no javadoc.",
                ((List<CodeSmell>) smellMap.get(Optional.of("missing_javadoc.MethodTest#setFieldAccessTest(int)")))
                        .get(0).getSummary().get()
        );

        assertEquals(
                7,
                smellMap.keys().count(Optional.of("missing_javadoc.MethodTest#visitCtMethod(missing_javadoc.CtMethod)"))
        );
        List<CodeSmell> codeSmellsVCM = (List<CodeSmell>) smellMap.get(Optional.of(
                "missing_javadoc.MethodTest#visitCtMethod(missing_javadoc.CtMethod)"));
        assertEquals("Tag @author is not allowed for this javadocable.", codeSmellsVCM.get(0).getSummary().get());
        assertEquals("Tag @version is not allowed for this javadocable.", codeSmellsVCM.get(1).getSummary().get());
        assertEquals("Tag @unknown is not allowed for this javadocable.", codeSmellsVCM.get(2).getSummary().get());
        assertEquals(
                "Javadoc contains @throws/@exception IllegalAccessException, but this does not thrown by this or a called executable.",
                codeSmellsVCM.get(3).getSummary().get()
        );
        assertEquals(
                "Javadoc contains @throws/@exception IllegalArgumentException, but this does not thrown by this or a called executable.",
                codeSmellsVCM.get(4).getSummary().get()
        );
        assertEquals(
                "Javadoc contains @deprecated, but the annotation @Deprecated at the javadocable is missing.",
                codeSmellsVCM.get(5).getSummary().get()
        );
        assertEquals(
                "Javadoc contains @return, but the method has no return value.",
                codeSmellsVCM.get(6).getSummary().get()
        );


        assertEquals(3, smellMap.keys().count(Optional.of("missing_javadoc.MethodTest#numberBetweenOneAndFour(int)")));
        List<CodeSmell> codeSmellsNBOAF = (List<CodeSmell>) smellMap.get(Optional.of(
                "missing_javadoc.MethodTest#numberBetweenOneAndFour(int)"));
        assertEquals(
                "Missing tag @throws IllegalArgumentException in the javadoc.",
                codeSmellsNBOAF.get(0).getSummary().get()
        );
        assertEquals("Missing tag @deprecated in the javadoc.", codeSmellsNBOAF.get(1).getSummary().get());
        assertEquals("Missing tag @return in the javadoc.", codeSmellsNBOAF.get(2).getSummary().get());


        assertEquals(9, smellMap.keys().count(Optional.of("missing_javadoc.MethodTest#charRepeater(char,int)")));
        List<CodeSmell> codeSmellsCR = (List<CodeSmell>) smellMap.get(Optional.of(
                "missing_javadoc.MethodTest#charRepeater(char,int)"));
        assertEquals("Description of the @return tag is too short.", codeSmellsCR.get(0).getSummary().get());
        assertEquals(
                "Description of the @throws IllegalArgumentException is too short.",
                codeSmellsCR.get(1).getSummary().get()
        );
        assertEquals(
                "Description of the @exception IllegalStateException is too short.",
                codeSmellsCR.get(2).getSummary().get()
        );
        assertEquals("Description of the @see tag is too short.", codeSmellsCR.get(3).getSummary().get());
        assertEquals("Description of the @since tag is too short.", codeSmellsCR.get(4).getSummary().get());
        assertEquals("Description of the @deprecated tag is too short.", codeSmellsCR.get(5).getSummary().get());
        assertEquals("Description of the @serialData tag is too short.", codeSmellsCR.get(6).getSummary().get());
        assertEquals("Short-description of this javadoc is too short.", codeSmellsCR.get(7).getSummary().get());
        assertEquals("Long-description of this javadoc is too short.", codeSmellsCR.get(8).getSummary().get());

        assertEquals(
                2,
                smellMap.keys().count(Optional.of("missing_javadoc.MethodTest#noDescription(java.lang.String)"))
        );
        List<CodeSmell> codeSmellsND = (List<CodeSmell>) smellMap.get(Optional.of(
                "missing_javadoc.MethodTest#noDescription(java.lang.String)"));
        assertEquals("Missing tag @return in the javadoc.", codeSmellsND.get(0).getSummary().get());
        assertEquals("No description existing in this javadoc.", codeSmellsND.get(1).getSummary().get());

        assertEquals(
                1,
                smellMap.keys()
                        .count(Optional.of("missing_javadoc.MethodTest#writeToFile(java.lang.String,java.lang.String)"))
        );
        assertEquals(
                "Method writeToFile(java.lang.String,java.lang.String) has no javadoc.",
                ((List<CodeSmell>) smellMap.get(Optional.of(
                        "missing_javadoc.MethodTest#writeToFile(java.lang.String,java.lang.String)")))
                        .get(0).getSummary().get()
        );

        assertEquals(
                2,
                smellMap.keys()
                        .count(Optional.of(
                                "missing_javadoc.MethodTest#readAndWriteJSON(java.lang.String,java.lang.String)"))
        );
        List<CodeSmell> codeSmellsRWJSON = (List<CodeSmell>) smellMap.get(Optional.of(
                "missing_javadoc.MethodTest#readAndWriteJSON(java.lang.String,java.lang.String)"));
        assertEquals(
                "Missing tag @throws IllegalFormatException in the javadoc.",
                codeSmellsRWJSON.get(0).getSummary().get()
        );
        assertEquals("Missing tag @throws ParseException in the javadoc.", codeSmellsRWJSON.get(1).getSummary().get());

        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.MethodTest#readJSON(java.lang.String)")));
        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.MethodTest#negativNumber(int)")));
        assertEquals(
                0,
                smellMap.keys()
                        .count(Optional.of("missing_javadoc.MethodTest#getFieldAccessTestForDescriptionLength()"))
        );
    }

    @Test
    public void testMethodWithoutException() throws IOException {
        RevisionMock revision = new RevisionMock(folder);
        revision.addFile(Paths.get("missing_javadoc", "MethodTest.java"));

        Launcher launcher = new Launcher();
        launcher.getEnvironment().setAutoImports(true);
        launcher.addInputResource(folder.getRoot().getAbsolutePath());
        CtModel model = launcher.buildModel();
        RevisionRange revisionRange = mock(RevisionRange.class);
        when(revisionRange.getRevision()).thenReturn(revision);

        final var env = new Environment(model, revisionRange);

        JavadocDetector javadocDetectorWithoutException = new JavadocDetector(env);
        javadocDetectorWithoutException.withMethod(5, 15);
        javadocDetectorWithoutException.withMethodFieldAccess(5);
        javadocDetectorWithoutException.withAllAccessModifier();
        setMethodTags(javadocDetectorWithoutException);
        javadocDetectorWithoutException.withoutException();
        javadocDetectorWithoutException.scan(model);
        assertEquals(26, javadocDetectorWithoutException.getCodeSmells().size());
        Multimap smellMap = mappedCodeSmell(javadocDetectorWithoutException.getCodeSmells());

        assertEquals(1, smellMap.keys().count(Optional.of("missing_javadoc.MethodTest#getFieldAccessTest()")));
        assertEquals(
                "Method getFieldAccessTest() has no javadoc.",
                ((List<CodeSmell>) smellMap.get(Optional.of("missing_javadoc.MethodTest#getFieldAccessTest()")))
                        .get(0).getSummary().get()
        );

        assertEquals(1, smellMap.keys().count(Optional.of("missing_javadoc.MethodTest#setFieldAccessTest(int)")));
        assertEquals(
                "Method setFieldAccessTest(int) has no javadoc.",
                ((List<CodeSmell>) smellMap.get(Optional.of("missing_javadoc.MethodTest#setFieldAccessTest(int)")))
                        .get(0).getSummary().get()
        );

        assertEquals(
                9,
                smellMap.keys().count(Optional.of("missing_javadoc.MethodTest#visitCtMethod(missing_javadoc.CtMethod)"))
        );
        List<CodeSmell> codeSmellsVCM = (List<CodeSmell>) smellMap.get(Optional.of(
                "missing_javadoc.MethodTest#visitCtMethod(missing_javadoc.CtMethod)"));
        assertEquals("Tag @author is not allowed for this javadocable.", codeSmellsVCM.get(0).getSummary().get());
        assertEquals("Tag @version is not allowed for this javadocable.", codeSmellsVCM.get(1).getSummary().get());
        assertEquals("Tag @unknown is not allowed for this javadocable.", codeSmellsVCM.get(2).getSummary().get());
        assertEquals(
                "Javadoc contains @deprecated, but the annotation @Deprecated at the javadocable is missing.",
                codeSmellsVCM.get(3).getSummary().get()
        );
        assertEquals(
                "Javadoc contains @return, but the method has no return value.",
                codeSmellsVCM.get(4).getSummary().get()
        );
        assertEquals("Missing tag @param method in the javadoc.", codeSmellsVCM.get(5).getSummary().get());
        assertEquals("Missing tag @param <E> in the javadoc.", codeSmellsVCM.get(6).getSummary().get());
        assertEquals(
                "Javadoc contains @param dingdong, but this parameter does not exists.",
                codeSmellsVCM.get(7).getSummary().get()
        );
        assertEquals(
                "Javadoc contains @param <T>, but this parameter does not exists.",
                codeSmellsVCM.get(8).getSummary().get()
        );


        assertEquals(2, smellMap.keys().count(Optional.of("missing_javadoc.MethodTest#numberBetweenOneAndFour(int)")));
        List<CodeSmell> codeSmellsNBOAF = (List<CodeSmell>) smellMap.get(Optional.of(
                "missing_javadoc.MethodTest#numberBetweenOneAndFour(int)"));
        assertEquals("Missing tag @deprecated in the javadoc.", codeSmellsNBOAF.get(0).getSummary().get());
        assertEquals("Missing tag @return in the javadoc.", codeSmellsNBOAF.get(1).getSummary().get());


        assertEquals(9, smellMap.keys().count(Optional.of("missing_javadoc.MethodTest#charRepeater(char,int)")));
        List<CodeSmell> codeSmellsCR = (List<CodeSmell>) smellMap.get(Optional.of(
                "missing_javadoc.MethodTest#charRepeater(char,int)"));
        assertEquals("Description of the @param c is too short.", codeSmellsCR.get(0).getSummary().get());
        assertEquals("Description of the @param repeatNumber is too short.", codeSmellsCR.get(1).getSummary().get());
        assertEquals("Description of the @return tag is too short.", codeSmellsCR.get(2).getSummary().get());
        assertEquals("Description of the @see tag is too short.", codeSmellsCR.get(3).getSummary().get());
        assertEquals("Description of the @since tag is too short.", codeSmellsCR.get(4).getSummary().get());
        assertEquals("Description of the @deprecated tag is too short.", codeSmellsCR.get(5).getSummary().get());
        assertEquals("Description of the @serialData tag is too short.", codeSmellsCR.get(6).getSummary().get());
        assertEquals("Short-description of this javadoc is too short.", codeSmellsCR.get(7).getSummary().get());
        assertEquals("Long-description of this javadoc is too short.", codeSmellsCR.get(8).getSummary().get());

        assertEquals(
                3,
                smellMap.keys().count(Optional.of("missing_javadoc.MethodTest#noDescription(java.lang.String)"))
        );
        List<CodeSmell> codeSmellsND = (List<CodeSmell>) smellMap.get(Optional.of(
                "missing_javadoc.MethodTest#noDescription(java.lang.String)"));
        assertEquals("Missing tag @return in the javadoc.", codeSmellsND.get(0).getSummary().get());
        assertEquals("Missing tag @param text in the javadoc.", codeSmellsND.get(1).getSummary().get());
        assertEquals("No description existing in this javadoc.", codeSmellsND.get(2).getSummary().get());

        assertEquals(
                1,
                smellMap.keys()
                        .count(Optional.of("missing_javadoc.MethodTest#writeToFile(java.lang.String,java.lang.String)"))
        );
        assertEquals(
                "Method writeToFile(java.lang.String,java.lang.String) has no javadoc.",
                ((List<CodeSmell>) smellMap.get(Optional.of(
                        "missing_javadoc.MethodTest#writeToFile(java.lang.String,java.lang.String)")))
                        .get(0).getSummary().get()
        );

        assertEquals(
                0,
                smellMap.keys()
                        .count(Optional.of(
                                "missing_javadoc.MethodTest#readAndWriteJSON(java.lang.String,java.lang.String)"))
        );
        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.MethodTest#readJSON(java.lang.String)")));
        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.MethodTest#negativNumber(int)")));
        assertEquals(
                0,
                smellMap.keys()
                        .count(Optional.of("missing_javadoc.MethodTest#getFieldAccessTestForDescriptionLength()"))
        );
    }

    @Test
    public void testMethodWithoutDeprecated() throws IOException {
        RevisionMock revision = new RevisionMock(folder);
        revision.addFile(Paths.get("missing_javadoc", "MethodTest.java"));

        Launcher launcher = new Launcher();
        launcher.getEnvironment().setAutoImports(true);
        launcher.addInputResource(folder.getRoot().getAbsolutePath());
        CtModel model = launcher.buildModel();

        RevisionRange revisionRange = mock(RevisionRange.class);
        when(revisionRange.getRevision()).thenReturn(revision);

        final var env = new Environment(model, revisionRange);
        JavadocDetector javadocDetectorWithoutDeprecated = new JavadocDetector(env);
        javadocDetectorWithoutDeprecated.withMethod(5, 15);
        javadocDetectorWithoutDeprecated.withMethodFieldAccess(5);
        javadocDetectorWithoutDeprecated.withAllAccessModifier();
        setMethodTags(javadocDetectorWithoutDeprecated);
        javadocDetectorWithoutDeprecated.withoutDeprecated();
        javadocDetectorWithoutDeprecated.scan(model);
        assertEquals(30, javadocDetectorWithoutDeprecated.getCodeSmells().size());
        Multimap smellMap = mappedCodeSmell(javadocDetectorWithoutDeprecated.getCodeSmells());

        assertEquals(1, smellMap.keys().count(Optional.of("missing_javadoc.MethodTest#getFieldAccessTest()")));
        assertEquals(
                "Method getFieldAccessTest() has no javadoc.",
                ((List<CodeSmell>) smellMap.get(Optional.of("missing_javadoc.MethodTest#getFieldAccessTest()")))
                        .get(0).getSummary().get()
        );

        assertEquals(1, smellMap.keys().count(Optional.of("missing_javadoc.MethodTest#setFieldAccessTest(int)")));
        assertEquals(
                "Method setFieldAccessTest(int) has no javadoc.",
                ((List<CodeSmell>) smellMap.get(Optional.of("missing_javadoc.MethodTest#setFieldAccessTest(int)")))
                        .get(0).getSummary().get()
        );

        assertEquals(
                10,
                smellMap.keys().count(Optional.of("missing_javadoc.MethodTest#visitCtMethod(missing_javadoc.CtMethod)"))
        );
        List<CodeSmell> codeSmellsVCM = (List<CodeSmell>) smellMap.get(Optional.of(
                "missing_javadoc.MethodTest#visitCtMethod(missing_javadoc.CtMethod)"));
        assertEquals("Tag @author is not allowed for this javadocable.", codeSmellsVCM.get(0).getSummary().get());
        assertEquals("Tag @version is not allowed for this javadocable.", codeSmellsVCM.get(1).getSummary().get());
        assertEquals("Tag @unknown is not allowed for this javadocable.", codeSmellsVCM.get(2).getSummary().get());
        assertEquals(
                "Javadoc contains @throws/@exception IllegalAccessException, but this does not thrown by this or a called executable.",
                codeSmellsVCM.get(3).getSummary().get()
        );
        assertEquals(
                "Javadoc contains @throws/@exception IllegalArgumentException, but this does not thrown by this or a called executable.",
                codeSmellsVCM.get(4).getSummary().get()
        );
        assertEquals(
                "Javadoc contains @return, but the method has no return value.",
                codeSmellsVCM.get(5).getSummary().get()
        );
        assertEquals("Missing tag @param method in the javadoc.", codeSmellsVCM.get(6).getSummary().get());
        assertEquals("Missing tag @param <E> in the javadoc.", codeSmellsVCM.get(7).getSummary().get());
        assertEquals(
                "Javadoc contains @param dingdong, but this parameter does not exists.",
                codeSmellsVCM.get(8).getSummary().get()
        );
        assertEquals(
                "Javadoc contains @param <T>, but this parameter does not exists.",
                codeSmellsVCM.get(9).getSummary().get()
        );


        assertEquals(2, smellMap.keys().count(Optional.of("missing_javadoc.MethodTest#numberBetweenOneAndFour(int)")));
        List<CodeSmell> codeSmellsNBOAF = (List<CodeSmell>) smellMap.get(Optional.of(
                "missing_javadoc.MethodTest#numberBetweenOneAndFour(int)"));
        assertEquals(
                "Missing tag @throws IllegalArgumentException in the javadoc.",
                codeSmellsNBOAF.get(0).getSummary().get()
        );
        assertEquals("Missing tag @return in the javadoc.", codeSmellsNBOAF.get(1).getSummary().get());


        assertEquals(10, smellMap.keys().count(Optional.of("missing_javadoc.MethodTest#charRepeater(char,int)")));
        List<CodeSmell> codeSmellsCR = (List<CodeSmell>) smellMap.get(Optional.of(
                "missing_javadoc.MethodTest#charRepeater(char,int)"));
        assertEquals("Description of the @param c is too short.", codeSmellsCR.get(0).getSummary().get());
        assertEquals("Description of the @param repeatNumber is too short.", codeSmellsCR.get(1).getSummary().get());
        assertEquals("Description of the @return tag is too short.", codeSmellsCR.get(2).getSummary().get());
        assertEquals(
                "Description of the @throws IllegalArgumentException is too short.",
                codeSmellsCR.get(3).getSummary().get()
        );
        assertEquals(
                "Description of the @exception IllegalStateException is too short.",
                codeSmellsCR.get(4).getSummary().get()
        );
        assertEquals("Description of the @see tag is too short.", codeSmellsCR.get(5).getSummary().get());
        assertEquals("Description of the @since tag is too short.", codeSmellsCR.get(6).getSummary().get());
        assertEquals("Description of the @serialData tag is too short.", codeSmellsCR.get(7).getSummary().get());
        assertEquals("Short-description of this javadoc is too short.", codeSmellsCR.get(8).getSummary().get());
        assertEquals("Long-description of this javadoc is too short.", codeSmellsCR.get(9).getSummary().get());

        assertEquals(
                3,
                smellMap.keys().count(Optional.of("missing_javadoc.MethodTest#noDescription(java.lang.String)"))
        );
        List<CodeSmell> codeSmellsND = (List<CodeSmell>) smellMap.get(Optional.of(
                "missing_javadoc.MethodTest#noDescription(java.lang.String)"));
        assertEquals("Missing tag @return in the javadoc.", codeSmellsND.get(0).getSummary().get());
        assertEquals("Missing tag @param text in the javadoc.", codeSmellsND.get(1).getSummary().get());
        assertEquals("No description existing in this javadoc.", codeSmellsND.get(2).getSummary().get());

        assertEquals(
                1,
                smellMap.keys()
                        .count(Optional.of("missing_javadoc.MethodTest#writeToFile(java.lang.String,java.lang.String)"))
        );
        assertEquals(
                "Method writeToFile(java.lang.String,java.lang.String) has no javadoc.",
                ((List<CodeSmell>) smellMap.get(Optional.of(
                        "missing_javadoc.MethodTest#writeToFile(java.lang.String,java.lang.String)")))
                        .get(0).getSummary().get()
        );

        assertEquals(
                2,
                smellMap.keys()
                        .count(Optional.of(
                                "missing_javadoc.MethodTest#readAndWriteJSON(java.lang.String,java.lang.String)"))
        );
        List<CodeSmell> codeSmellsRWJSON = (List<CodeSmell>) smellMap.get(Optional.of(
                "missing_javadoc.MethodTest#readAndWriteJSON(java.lang.String,java.lang.String)"));
        assertEquals(
                "Missing tag @throws IllegalFormatException in the javadoc.",
                codeSmellsRWJSON.get(0).getSummary().get()
        );
        assertEquals("Missing tag @throws ParseException in the javadoc.", codeSmellsRWJSON.get(1).getSummary().get());

        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.MethodTest#readJSON(java.lang.String)")));
        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.MethodTest#negativNumber(int)")));
        assertEquals(
                0,
                smellMap.keys()
                        .count(Optional.of("missing_javadoc.MethodTest#getFieldAccessTestForDescriptionLength()"))
        );
    }

    @Test
    public void testMethodJavadocable() throws IOException {
        RevisionMock revision = new RevisionMock(folder);
        revision.addFile(Paths.get("missing_javadoc", "MethodTest.java"));

        Launcher launcher = new Launcher();
        launcher.getEnvironment().setAutoImports(true);
        launcher.addInputResource(folder.getRoot().getAbsolutePath());
        CtModel model = launcher.buildModel();

        RevisionRange revisionRange = mock(RevisionRange.class);
        when(revisionRange.getRevision()).thenReturn(revision);

        final var env = new Environment(model, revisionRange);
        JavadocDetector javadocDetector = new JavadocDetector(env);
        javadocDetector.withoutMethod();
        javadocDetector.withClass(5, 15);
        javadocDetector.withAllAccessModifier();
        setMethodTags(javadocDetector);
        javadocDetector.scan(model);
        assertEquals(1, javadocDetector.getCodeSmells().size());
        Multimap smellMap = mappedCodeSmell(javadocDetector.getCodeSmells());
        assertEquals(1, smellMap.keys().count(Optional.of("missing_javadoc.MethodTest")));
        assertEquals(
                "Long-description of this javadoc is too short.",
                ((List<CodeSmell>) smellMap.get(Optional.of("missing_javadoc.MethodTest"))).get(0).getSummary().get()
        );
    }

    @Test
    public void testMethodAMPublic() throws IOException {
        RevisionMock revision = new RevisionMock(folder);
        revision.addFile(Paths.get("missing_javadoc", "MethodTest.java"));

        Launcher launcher = new Launcher();
        launcher.getEnvironment().setAutoImports(true);
        launcher.addInputResource(folder.getRoot().getAbsolutePath());
        CtModel model = launcher.buildModel();
        RevisionRange revisionRange = mock(RevisionRange.class);
        when(revisionRange.getRevision()).thenReturn(revision);

        final var env = new Environment(model, revisionRange);

        JavadocDetector javadocDetector = new JavadocDetector(env);
        javadocDetector.withMethod(5, 15);
        javadocDetector.withMethodFieldAccess(5);
        setMethodTags(javadocDetector);
        javadocDetector.withAccessModifier(JavadocDetector.AccessModifier.PUBLIC);
        javadocDetector.scan(model);
        assertEquals(13, javadocDetector.getCodeSmells().size());
        Multimap smellMap = mappedCodeSmell(javadocDetector.getCodeSmells());

        assertEquals(1, smellMap.keys().count(Optional.of("missing_javadoc.MethodTest#getFieldAccessTest()")));
        assertEquals(
                "Method getFieldAccessTest() has no javadoc.",
                ((List<CodeSmell>) smellMap.get(Optional.of("missing_javadoc.MethodTest#getFieldAccessTest()")))
                        .get(0).getSummary().get()
        );

        assertEquals(1, smellMap.keys().count(Optional.of("missing_javadoc.MethodTest#setFieldAccessTest(int)")));
        assertEquals(
                "Method setFieldAccessTest(int) has no javadoc.",
                ((List<CodeSmell>) smellMap.get(Optional.of("missing_javadoc.MethodTest#setFieldAccessTest(int)")))
                        .get(0).getSummary().get()
        );

        assertEquals(
                0,
                smellMap.keys().count(Optional.of("missing_javadoc.MethodTest#visitCtMethod(missing_javadoc.CtMethod)"))
        );
        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.MethodTest#numberBetweenOneAndFour(int)")));

        assertEquals(11, smellMap.keys().count(Optional.of("missing_javadoc.MethodTest#charRepeater(char,int)")));
        List<CodeSmell> codeSmellsCR = (List<CodeSmell>) smellMap.get(Optional.of(
                "missing_javadoc.MethodTest#charRepeater(char,int)"));
        assertEquals("Description of the @param c is too short.", codeSmellsCR.get(0).getSummary().get());
        assertEquals("Description of the @param repeatNumber is too short.", codeSmellsCR.get(1).getSummary().get());
        assertEquals("Description of the @return tag is too short.", codeSmellsCR.get(2).getSummary().get());
        assertEquals(
                "Description of the @throws IllegalArgumentException is too short.",
                codeSmellsCR.get(3).getSummary().get()
        );
        assertEquals(
                "Description of the @exception IllegalStateException is too short.",
                codeSmellsCR.get(4).getSummary().get()
        );
        assertEquals("Description of the @see tag is too short.", codeSmellsCR.get(5).getSummary().get());
        assertEquals("Description of the @since tag is too short.", codeSmellsCR.get(6).getSummary().get());
        assertEquals("Description of the @deprecated tag is too short.", codeSmellsCR.get(7).getSummary().get());
        assertEquals("Description of the @serialData tag is too short.", codeSmellsCR.get(8).getSummary().get());
        assertEquals("Short-description of this javadoc is too short.", codeSmellsCR.get(9).getSummary().get());
        assertEquals("Long-description of this javadoc is too short.", codeSmellsCR.get(10).getSummary().get());

        assertEquals(
                0,
                smellMap.keys().count(Optional.of("missing_javadoc.MethodTest#noDescription(java.lang.String)"))
        );
        assertEquals(
                0,
                smellMap.keys()
                        .count(Optional.of("missing_javadoc.MethodTest#writeToFile(java.lang.String,java.lang.String)"))
        );
        assertEquals(
                0,
                smellMap.keys()
                        .count(Optional.of(
                                "missing_javadoc.MethodTest#readAndWriteJSON(java.lang.String,java.lang.String)"))
        );
        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.MethodTest#readJSON(java.lang.String)")));
        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.MethodTest#negativNumber(int)")));
        assertEquals(
                0,
                smellMap.keys()
                        .count(Optional.of("missing_javadoc.MethodTest#getFieldAccessTestForDescriptionLength()"))
        );
    }

    @Test
    public void testMethodAMPrivate() throws IOException {
        RevisionMock revision = new RevisionMock(folder);
        revision.addFile(Paths.get("missing_javadoc", "MethodTest.java"));

        Launcher launcher = new Launcher();
        launcher.getEnvironment().setAutoImports(true);
        launcher.addInputResource(folder.getRoot().getAbsolutePath());
        CtModel model = launcher.buildModel();

        RevisionRange revisionRange = mock(RevisionRange.class);
        when(revisionRange.getRevision()).thenReturn(revision);

        final var env = new Environment(model, revisionRange);

        JavadocDetector javadocDetector = new JavadocDetector(env);
        javadocDetector.withMethod(5, 15);
        setMethodTags(javadocDetector);
        javadocDetector.withAccessModifier(JavadocDetector.AccessModifier.PRIVATE);
        javadocDetector.scan(model);
        assertEquals(6, javadocDetector.getCodeSmells().size());
        Multimap smellMap = mappedCodeSmell(javadocDetector.getCodeSmells());

        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.MethodTest#getFieldAccessTest()")));
        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.MethodTest#setFieldAccessTest(int)")));
        assertEquals(
                0,
                smellMap.keys().count(Optional.of("missing_javadoc.MethodTest#visitCtMethod(missing_javadoc.CtMethod)"))
        );

        assertEquals(3, smellMap.keys().count(Optional.of("missing_javadoc.MethodTest#numberBetweenOneAndFour(int)")));
        List<CodeSmell> codeSmellsNBOAF = (List<CodeSmell>) smellMap.get(Optional.of(
                "missing_javadoc.MethodTest#numberBetweenOneAndFour(int)"));
        assertEquals(
                "Missing tag @throws IllegalArgumentException in the javadoc.",
                codeSmellsNBOAF.get(0).getSummary().get()
        );
        assertEquals("Missing tag @deprecated in the javadoc.", codeSmellsNBOAF.get(1).getSummary().get());
        assertEquals("Missing tag @return in the javadoc.", codeSmellsNBOAF.get(2).getSummary().get());


        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.MethodTest#charRepeater(char,int)")));

        assertEquals(
                3,
                smellMap.keys().count(Optional.of("missing_javadoc.MethodTest#noDescription(java.lang.String)"))
        );
        List<CodeSmell> codeSmellsND = (List<CodeSmell>) smellMap.get(Optional.of(
                "missing_javadoc.MethodTest#noDescription(java.lang.String)"));
        assertEquals("Missing tag @return in the javadoc.", codeSmellsND.get(0).getSummary().get());
        assertEquals("Missing tag @param text in the javadoc.", codeSmellsND.get(1).getSummary().get());
        assertEquals("No description existing in this javadoc.", codeSmellsND.get(2).getSummary().get());

        assertEquals(
                0,
                smellMap.keys()
                        .count(Optional.of("missing_javadoc.MethodTest#writeToFile(java.lang.String,java.lang.String)"))
        );
        assertEquals(
                0,
                smellMap.keys()
                        .count(Optional.of(
                                "missing_javadoc.MethodTest#readAndWriteJSON(java.lang.String,java.lang.String)"))
        );
        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.MethodTest#readJSON(java.lang.String)")));
        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.MethodTest#negativNumber(int)")));
        assertEquals(
                0,
                smellMap.keys()
                        .count(Optional.of("missing_javadoc.MethodTest#getFieldAccessTestForDescriptionLength()"))
        );
    }

    @Test
    public void testMethodAMProtected() throws IOException {
        RevisionMock revision = new RevisionMock(folder);
        revision.addFile(Paths.get("missing_javadoc", "MethodTest.java"));

        Launcher launcher = new Launcher();
        launcher.getEnvironment().setAutoImports(true);
        launcher.addInputResource(folder.getRoot().getAbsolutePath());
        CtModel model = launcher.buildModel();

        RevisionRange revisionRange = mock(RevisionRange.class);
        when(revisionRange.getRevision()).thenReturn(revision);

        final var env = new Environment(model, revisionRange);

        JavadocDetector javadocDetector = new JavadocDetector(env);
        javadocDetector.withMethod(5, 15);
        setMethodTags(javadocDetector);
        javadocDetector.withAccessModifier(JavadocDetector.AccessModifier.PROTECTED);
        javadocDetector.scan(model);
        assertEquals(13, javadocDetector.getCodeSmells().size());
        Multimap smellMap = mappedCodeSmell(javadocDetector.getCodeSmells());

        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.MethodTest#getFieldAccessTest()")));
        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.MethodTest#setFieldAccessTest(int)")));

        assertEquals(
                11,
                smellMap.keys().count(Optional.of("missing_javadoc.MethodTest#visitCtMethod(missing_javadoc.CtMethod)"))
        );
        List<CodeSmell> codeSmellsVCM = (List<CodeSmell>) smellMap.get(Optional.of(
                "missing_javadoc.MethodTest#visitCtMethod(missing_javadoc.CtMethod)"));
        assertEquals("Tag @author is not allowed for this javadocable.", codeSmellsVCM.get(0).getSummary().get());
        assertEquals("Tag @version is not allowed for this javadocable.", codeSmellsVCM.get(1).getSummary().get());
        assertEquals("Tag @unknown is not allowed for this javadocable.", codeSmellsVCM.get(2).getSummary().get());
        assertEquals(
                "Javadoc contains @throws/@exception IllegalAccessException, but this does not thrown by this or a called executable.",
                codeSmellsVCM.get(3).getSummary().get()
        );
        assertEquals(
                "Javadoc contains @throws/@exception IllegalArgumentException, but this does not thrown by this or a called executable.",
                codeSmellsVCM.get(4).getSummary().get()
        );
        assertEquals(
                "Javadoc contains @deprecated, but the annotation @Deprecated at the javadocable is missing.",
                codeSmellsVCM.get(5).getSummary().get()
        );
        assertEquals(
                "Javadoc contains @return, but the method has no return value.",
                codeSmellsVCM.get(6).getSummary().get()
        );
        assertEquals("Missing tag @param method in the javadoc.", codeSmellsVCM.get(7).getSummary().get());
        assertEquals("Missing tag @param <E> in the javadoc.", codeSmellsVCM.get(8).getSummary().get());
        assertEquals(
                "Javadoc contains @param dingdong, but this parameter does not exists.",
                codeSmellsVCM.get(9).getSummary().get()
        );
        assertEquals(
                "Javadoc contains @param <T>, but this parameter does not exists.",
                codeSmellsVCM.get(10).getSummary().get()
        );

        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.MethodTest#numberBetweenOneAndFour(int)")));
        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.MethodTest#charRepeater(char,int)")));
        assertEquals(
                0,
                smellMap.keys().count(Optional.of("missing_javadoc.MethodTest#noDescription(java.lang.String)"))
        );
        assertEquals(
                0,
                smellMap.keys()
                        .count(Optional.of("missing_javadoc.MethodTest#writeToFile(java.lang.String,java.lang.String)"))
        );

        assertEquals(
                2,
                smellMap.keys()
                        .count(Optional.of(
                                "missing_javadoc.MethodTest#readAndWriteJSON(java.lang.String,java.lang.String)"))
        );
        List<CodeSmell> codeSmellsRWJSON = (List<CodeSmell>) smellMap.get(Optional.of(
                "missing_javadoc.MethodTest#readAndWriteJSON(java.lang.String,java.lang.String)"));
        assertEquals(
                "Missing tag @throws IllegalFormatException in the javadoc.",
                codeSmellsRWJSON.get(0).getSummary().get()
        );
        assertEquals("Missing tag @throws ParseException in the javadoc.", codeSmellsRWJSON.get(1).getSummary().get());

        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.MethodTest#readJSON(java.lang.String)")));
        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.MethodTest#negativNumber(int)")));
        assertEquals(
                0,
                smellMap.keys()
                        .count(Optional.of("missing_javadoc.MethodTest#getFieldAccessTestForDescriptionLength()"))
        );
    }

    @Test
    public void testMethodAMNull() throws IOException {
        RevisionMock revision = new RevisionMock(folder);
        revision.addFile(Paths.get("missing_javadoc", "MethodTest.java"));

        Launcher launcher = new Launcher();
        launcher.getEnvironment().setAutoImports(true);
        launcher.addInputResource(folder.getRoot().getAbsolutePath());
        CtModel model = launcher.buildModel();

        RevisionRange revisionRange = mock(RevisionRange.class);
        when(revisionRange.getRevision()).thenReturn(revision);

        final var env = new Environment(model, revisionRange);
        JavadocDetector javadocDetector = new JavadocDetector(env);
        javadocDetector.withMethod(5, 15);
        setMethodTags(javadocDetector);
        javadocDetector.withAccessModifier(JavadocDetector.AccessModifier.NULL);
        javadocDetector.scan(model);
        assertEquals(1, javadocDetector.getCodeSmells().size());
        Multimap smellMap = mappedCodeSmell(javadocDetector.getCodeSmells());

        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.MethodTest#getFieldAccessTest()")));
        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.MethodTest#setFieldAccessTest(int)")));
        assertEquals(
                0,
                smellMap.keys().count(Optional.of("missing_javadoc.MethodTest#visitCtMethod(missing_javadoc.CtMethod)"))
        );
        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.MethodTest#numberBetweenOneAndFour(int)")));
        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.MethodTest#charRepeater(char,int)")));
        assertEquals(
                0,
                smellMap.keys().count(Optional.of("missing_javadoc.MethodTest#noDescription(java.lang.String)"))
        );

        assertEquals(
                1,
                smellMap.keys()
                        .count(Optional.of("missing_javadoc.MethodTest#writeToFile(java.lang.String,java.lang.String)"))
        );
        assertEquals(
                "Method writeToFile(java.lang.String,java.lang.String) has no javadoc.",
                ((List<CodeSmell>) smellMap.get(Optional.of(
                        "missing_javadoc.MethodTest#writeToFile(java.lang.String,java.lang.String)")))
                        .get(0).getSummary().get()
        );

        assertEquals(
                0,
                smellMap.keys()
                        .count(Optional.of(
                                "missing_javadoc.MethodTest#readAndWriteJSON(java.lang.String,java.lang.String)"))
        );
        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.MethodTest#readJSON(java.lang.String)")));
        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.MethodTest#negativNumber(int)")));
        assertEquals(
                0,
                smellMap.keys()
                        .count(Optional.of("missing_javadoc.MethodTest#getFieldAccessTestForDescriptionLength()"))
        );
    }

    @Test
    public void testMethodAMMixed() throws IOException {
        RevisionMock revision = new RevisionMock(folder);
        revision.addFile(Paths.get("missing_javadoc", "MethodTest.java"));

        Launcher launcher = new Launcher();
        launcher.getEnvironment().setAutoImports(true);
        launcher.addInputResource(folder.getRoot().getAbsolutePath());
        CtModel model = launcher.buildModel();

        RevisionRange revisionRange = mock(RevisionRange.class);
        when(revisionRange.getRevision()).thenReturn(revision);

        final var env = new Environment(model, revisionRange);
        JavadocDetector javadocDetector = new JavadocDetector(env);
        javadocDetector.withMethod(5, 15);
        javadocDetector.withMethodFieldAccess(5);
        setMethodTags(javadocDetector);
        javadocDetector.withAccessModifier(new JavadocDetector.AccessModifier[]{
                JavadocDetector.AccessModifier.PUBLIC, JavadocDetector.AccessModifier.PROTECTED});
        javadocDetector.scan(model);
        assertEquals(26, javadocDetector.getCodeSmells().size());
        Multimap smellMap = mappedCodeSmell(javadocDetector.getCodeSmells());

        assertEquals(1, smellMap.keys().count(Optional.of("missing_javadoc.MethodTest#getFieldAccessTest()")));
        assertEquals(
                "Method getFieldAccessTest() has no javadoc.",
                ((List<CodeSmell>) smellMap.get(Optional.of("missing_javadoc.MethodTest#getFieldAccessTest()")))
                        .get(0).getSummary().get()
        );

        assertEquals(1, smellMap.keys().count(Optional.of("missing_javadoc.MethodTest#setFieldAccessTest(int)")));
        assertEquals(
                "Method setFieldAccessTest(int) has no javadoc.",
                ((List<CodeSmell>) smellMap.get(Optional.of("missing_javadoc.MethodTest#setFieldAccessTest(int)")))
                        .get(0).getSummary().get()
        );

        assertEquals(
                11,
                smellMap.keys().count(Optional.of("missing_javadoc.MethodTest#visitCtMethod(missing_javadoc.CtMethod)"))
        );
        List<CodeSmell> codeSmellsVCM = (List<CodeSmell>) smellMap.get(Optional.of(
                "missing_javadoc.MethodTest#visitCtMethod(missing_javadoc.CtMethod)"));
        assertEquals("Tag @author is not allowed for this javadocable.", codeSmellsVCM.get(0).getSummary().get());
        assertEquals("Tag @version is not allowed for this javadocable.", codeSmellsVCM.get(1).getSummary().get());
        assertEquals("Tag @unknown is not allowed for this javadocable.", codeSmellsVCM.get(2).getSummary().get());
        assertEquals(
                "Javadoc contains @throws/@exception IllegalAccessException, but this does not thrown by this or a called executable.",
                codeSmellsVCM.get(3).getSummary().get()
        );
        assertEquals(
                "Javadoc contains @throws/@exception IllegalArgumentException, but this does not thrown by this or a called executable.",
                codeSmellsVCM.get(4).getSummary().get()
        );
        assertEquals(
                "Javadoc contains @deprecated, but the annotation @Deprecated at the javadocable is missing.",
                codeSmellsVCM.get(5).getSummary().get()
        );
        assertEquals(
                "Javadoc contains @return, but the method has no return value.",
                codeSmellsVCM.get(6).getSummary().get()
        );
        assertEquals("Missing tag @param method in the javadoc.", codeSmellsVCM.get(7).getSummary().get());
        assertEquals("Missing tag @param <E> in the javadoc.", codeSmellsVCM.get(8).getSummary().get());
        assertEquals(
                "Javadoc contains @param dingdong, but this parameter does not exists.",
                codeSmellsVCM.get(9).getSummary().get()
        );
        assertEquals(
                "Javadoc contains @param <T>, but this parameter does not exists.",
                codeSmellsVCM.get(10).getSummary().get()
        );

        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.MethodTest#numberBetweenOneAndFour(int)")));

        assertEquals(11, smellMap.keys().count(Optional.of("missing_javadoc.MethodTest#charRepeater(char,int)")));
        List<CodeSmell> codeSmellsCR = (List<CodeSmell>) smellMap.get(Optional.of(
                "missing_javadoc.MethodTest#charRepeater(char,int)"));
        assertEquals("Description of the @param c is too short.", codeSmellsCR.get(0).getSummary().get());
        assertEquals("Description of the @param repeatNumber is too short.", codeSmellsCR.get(1).getSummary().get());
        assertEquals("Description of the @return tag is too short.", codeSmellsCR.get(2).getSummary().get());
        assertEquals(
                "Description of the @throws IllegalArgumentException is too short.",
                codeSmellsCR.get(3).getSummary().get()
        );
        assertEquals(
                "Description of the @exception IllegalStateException is too short.",
                codeSmellsCR.get(4).getSummary().get()
        );
        assertEquals("Description of the @see tag is too short.", codeSmellsCR.get(5).getSummary().get());
        assertEquals("Description of the @since tag is too short.", codeSmellsCR.get(6).getSummary().get());
        assertEquals("Description of the @deprecated tag is too short.", codeSmellsCR.get(7).getSummary().get());
        assertEquals("Description of the @serialData tag is too short.", codeSmellsCR.get(8).getSummary().get());
        assertEquals("Short-description of this javadoc is too short.", codeSmellsCR.get(9).getSummary().get());
        assertEquals("Long-description of this javadoc is too short.", codeSmellsCR.get(10).getSummary().get());

        assertEquals(
                0,
                smellMap.keys().count(Optional.of("missing_javadoc.MethodTest#noDescription(java.lang.String)"))
        );
        assertEquals(
                0,
                smellMap.keys()
                        .count(Optional.of("missing_javadoc.MethodTest#writeToFile(java.lang.String,java.lang.String)"))
        );

        assertEquals(
                2,
                smellMap.keys()
                        .count(Optional.of(
                                "missing_javadoc.MethodTest#readAndWriteJSON(java.lang.String,java.lang.String)"))
        );
        List<CodeSmell> codeSmellsRWJSON = (List<CodeSmell>) smellMap.get(Optional.of(
                "missing_javadoc.MethodTest#readAndWriteJSON(java.lang.String,java.lang.String)"));
        assertEquals(
                "Missing tag @throws IllegalFormatException in the javadoc.",
                codeSmellsRWJSON.get(0).getSummary().get()
        );
        assertEquals("Missing tag @throws ParseException in the javadoc.", codeSmellsRWJSON.get(1).getSummary().get());

        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.MethodTest#readJSON(java.lang.String)")));
        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.MethodTest#negativNumber(int)")));
        assertEquals(
                0,
                smellMap.keys()
                        .count(Optional.of("missing_javadoc.MethodTest#getFieldAccessTestForDescriptionLength()"))
        );
    }

    private void setPackageTags(JavadocDetector javadocDetector) {
        javadocDetector.withAuthor(2);
        javadocDetector.withVersion(1);
        javadocDetector.withSee(5);
        javadocDetector.withSince(1);
        javadocDetector.withSerial(5);
    }

    @Test
    public void testPackage() throws IOException {
        RevisionMock revision = new RevisionMock(folder);
        revision.addFile(Paths.get("missing_javadoc/package/empty", "package-info.java"));
        revision.addFile(Paths.get("missing_javadoc/package/no_javadoc", "package-info.java"));
        revision.addFile(Paths.get("missing_javadoc/package/short_description", "package-info.java"));
        revision.addFile(Paths.get("missing_javadoc/package/long_description", "package-info.java"));
        revision.addFile(Paths.get("missing_javadoc/package/tags", "package-info.java"));
        revision.addFile(Paths.get("missing_javadoc/package", "package-info.java"));

        Launcher launcher = new Launcher();
        launcher.getEnvironment().setAutoImports(true);
        launcher.addInputResource(folder.getRoot().getAbsolutePath());
        CtModel model = launcher.buildModel();

        RevisionRange revisionRange = mock(RevisionRange.class);
        when(revisionRange.getRevision()).thenReturn(revision);

        final var env = new Environment(model, revisionRange);
        JavadocDetector javadocDetector = new JavadocDetector(env);
        javadocDetector.withPackage(5, 15);
        setPackageTags(javadocDetector);
        javadocDetector.scan(model);
        assertEquals(10, javadocDetector.getCodeSmells().size());
        Multimap smellMap = mappedCodeSmell(javadocDetector.getCodeSmells());

        assertEquals(1, smellMap.keys().count(Optional.of("spoon.emptyPackage")));
        assertEquals(
                "No description existing in this javadoc.",
                ((List<CodeSmell>) smellMap.get(Optional.of("spoon.emptyPackage")))
                        .get(0).getSummary().get()
        );

        assertEquals(1, smellMap.keys().count(Optional.of("spoon.noJavadocPackage")));
        assertEquals(
                "Package noJavadocPackage has no javadoc.",
                ((List<CodeSmell>) smellMap.get(Optional.of("spoon.noJavadocPackage")))
                        .get(0).getSummary().get()
        );

        assertEquals(1, smellMap.keys().count(Optional.of("spoon.tooShortShortDescriptionPackage")));
        assertEquals(
                "Short-description of this javadoc is too short.",
                ((List<CodeSmell>) smellMap.get(Optional.of("spoon.tooShortShortDescriptionPackage")))
                        .get(0).getSummary().get()
        );

        assertEquals(1, smellMap.keys().count(Optional.of("spoon.tooShortLongDescriptionPackage")));
        assertEquals(
                "Long-description of this javadoc is too short.",
                ((List<CodeSmell>) smellMap.get(Optional.of("spoon.tooShortLongDescriptionPackage")))
                        .get(0).getSummary().get()
        );

        assertEquals(6, smellMap.keys().count(Optional.of("spoon.tooShortTagsPackage")));
        List<CodeSmell> codeSmellsSTP = (List<CodeSmell>) smellMap.get(Optional.of("spoon.tooShortTagsPackage"));
        assertEquals("Tag @param is not allowed for this javadocable.", codeSmellsSTP.get(0).getSummary().get());
        assertEquals("Description of the @author tag is too short.", codeSmellsSTP.get(1).getSummary().get());
        assertEquals("Description of the @version tag is too short.", codeSmellsSTP.get(2).getSummary().get());
        assertEquals("Description of the @see tag is too short.", codeSmellsSTP.get(3).getSummary().get());
        assertEquals("Description of the @since tag is too short.", codeSmellsSTP.get(4).getSummary().get());
        assertEquals("Description of the @serial tag is too short.", codeSmellsSTP.get(5).getSummary().get());

        assertEquals(0, smellMap.keys().count(Optional.of("spoon.testing")));
    }

    @Test
    public void testPackageJavadocable() throws IOException {
        RevisionMock revision = new RevisionMock(folder);
        revision.addFile(Paths.get("missing_javadoc/package/empty", "package-info.java"));
        revision.addFile(Paths.get("missing_javadoc/package/no_javadoc", "package-info.java"));
        revision.addFile(Paths.get("missing_javadoc/package/short_description", "package-info.java"));
        revision.addFile(Paths.get("missing_javadoc/package/long_description", "package-info.java"));
        revision.addFile(Paths.get("missing_javadoc/package/tags", "package-info.java"));
        revision.addFile(Paths.get("missing_javadoc/package", "package-info.java"));

        Launcher launcher = new Launcher();
        launcher.getEnvironment().setAutoImports(true);
        launcher.addInputResource(folder.getRoot().getAbsolutePath());
        CtModel model = launcher.buildModel();

        RevisionRange revisionRange = mock(RevisionRange.class);
        when(revisionRange.getRevision()).thenReturn(revision);

        final var env = new Environment(model, revisionRange);

        JavadocDetector detectorWithoutPackage = new JavadocDetector(env);
        detectorWithoutPackage.withoutPackage();
        setPackageTags(detectorWithoutPackage);
        detectorWithoutPackage.scan(model);
        assertEquals(0, detectorWithoutPackage.getCodeSmells().size());
    }

    @Test
    public void testPackageInfo() throws IOException {
        RevisionMock revision = new RevisionMock(folder);
        revision.addFile(Paths.get("missing_javadoc/package-info", "package-info.java"));
        revision.addFile(Paths.get("missing_javadoc/package-info", "ClassTest.java"));
        revision.addFile(Paths.get("missing_javadoc/package-info", "EnumTest.java"));
        revision.addFile(Paths.get("missing_javadoc/package-info", "Override.java"));
        revision.addFile(Paths.get("missing_javadoc/package-info", "InterfaceTest.java"));

        Launcher launcher = new Launcher();
        launcher.getEnvironment().setAutoImports(true);
        launcher.addInputResource(folder.getRoot().getAbsolutePath());
        CtModel model = launcher.buildModel();

        RevisionRange revisionRange = mock(RevisionRange.class);
        when(revisionRange.getRevision()).thenReturn(revision);

        final var env = new Environment(model, revisionRange);

        JavadocDetector javadocDetector = new JavadocDetector(env);
        javadocDetector.withPackage(5, 15);
        javadocDetector.withAllJavadocables();
        javadocDetector.withoutAllAccessModifier();
        javadocDetector.withPackageInfoNeeded();
        javadocDetector.withTypelessPackageInfoNeeded();
        javadocDetector.scan(model);
        assertEquals(5, javadocDetector.getCodeSmells().size());
        Multimap smellMap = mappedCodeSmell(javadocDetector.getCodeSmells());

        assertEquals(1, smellMap.keys().count(Optional.of("missing_javadoc.classes")));
        assertEquals(
                "Missing package-info for the package (missing_javadoc.classes).",
                ((List<CodeSmell>) smellMap.get(Optional.of("missing_javadoc.classes")))
                        .get(0).getSummary().get()
        );

        assertEquals(1, smellMap.keys().count(Optional.of("missing_javadoc.enums")));
        assertEquals(
                "Missing package-info for the package (missing_javadoc.enums).",
                ((List<CodeSmell>) smellMap.get(Optional.of("missing_javadoc.enums")))
                        .get(0).getSummary().get()
        );

        assertEquals(1, smellMap.keys().count(Optional.of("missing_javadoc.interfaces")));
        assertEquals(
                "Missing package-info for the package (missing_javadoc.interfaces).",
                ((List<CodeSmell>) smellMap.get(Optional.of("missing_javadoc.interfaces")))
                        .get(0).getSummary().get()
        );

        assertEquals(1, smellMap.keys().count(Optional.of("missing_javadoc")));
        assertEquals(
                "Missing package-info for the package (missing_javadoc).",
                ((List<CodeSmell>) smellMap.get(Optional.of("missing_javadoc")))
                        .get(0).getSummary().get()
        );

        assertEquals(1, smellMap.keys().count(Optional.of("spoon")));
        assertEquals(
                "Missing package-info for the typeless package (spoon).",
                ((List<CodeSmell>) smellMap.get(Optional.of("spoon")))
                        .get(0).getSummary().get()
        );
    }

    @Test
    public void testPackageInfoDeclaringPackageMissing_javadoc() throws IOException {
        RevisionMock revision = new RevisionMock(folder);
        revision.addFile(Paths.get("missing_javadoc/package-info", "package-info.java"));
        revision.addFile(Paths.get("missing_javadoc/package-info", "ClassTest.java"));
        revision.addFile(Paths.get("missing_javadoc/package-info", "EnumTest.java"));
        revision.addFile(Paths.get("missing_javadoc/package-info", "InterfaceTest.java"));

        Launcher launcher = new Launcher();
        launcher.getEnvironment().setAutoImports(true);
        launcher.addInputResource(folder.getRoot().getAbsolutePath());
        CtModel model = launcher.buildModel();

        RevisionRange revisionRange = mock(RevisionRange.class);
        when(revisionRange.getRevision()).thenReturn(revision);

        final var env = new Environment(model, revisionRange);

        JavadocDetector javadocDetector = new JavadocDetector(env);
        javadocDetector.withPackage(5, 15);
        javadocDetector.withAllJavadocables();
        javadocDetector.withoutAllAccessModifier();
        javadocDetector.withPackageInfoNeeded();
        javadocDetector.withTypelessPackageInfoNeeded();
        javadocDetector.scan(model);
        assertEquals(5, javadocDetector.getCodeSmells().size());
        Multimap smellMap = mappedCodeSmell(javadocDetector.getCodeSmells());

        assertEquals(1, smellMap.keys().count(Optional.of("missing_javadoc.classes")));
        assertEquals(
                "Missing package-info for the package (missing_javadoc.classes).",
                ((List<CodeSmell>) smellMap.get(Optional.of("missing_javadoc.classes")))
                        .get(0).getSummary().get()
        );

        assertEquals(1, smellMap.keys().count(Optional.of("missing_javadoc.enums")));
        assertEquals(
                "Missing package-info for the package (missing_javadoc.enums).",
                ((List<CodeSmell>) smellMap.get(Optional.of("missing_javadoc.enums")))
                        .get(0).getSummary().get()
        );

        assertEquals(1, smellMap.keys().count(Optional.of("missing_javadoc.interfaces")));
        assertEquals(
                "Missing package-info for the package (missing_javadoc.interfaces).",
                ((List<CodeSmell>) smellMap.get(Optional.of("missing_javadoc.interfaces")))
                        .get(0).getSummary().get()
        );

        assertEquals(1, smellMap.keys().count(Optional.of("missing_javadoc")));
        assertEquals(
                "Missing package-info for the typeless package (missing_javadoc).",
                ((List<CodeSmell>) smellMap.get(Optional.of("missing_javadoc")))
                        .get(0).getSummary().get()
        );

        assertEquals(1, smellMap.keys().count(Optional.of("spoon")));
        assertEquals(
                "Missing package-info for the typeless package (spoon).",
                ((List<CodeSmell>) smellMap.get(Optional.of("spoon")))
                        .get(0).getSummary().get()
        );
    }

    @Test
    public void testPackageInfo2() throws IOException {
        RevisionMock revision = new RevisionMock(folder);
        revision.addFile(Paths.get("missing_javadoc/package-info/2", "package-info.java"));
        revision.addFile(Paths.get("missing_javadoc/package-info/2", "ClassTest.java"));
        revision.addFile(Paths.get("missing_javadoc/package-info/2", "EnumTest.java"));
        revision.addFile(Paths.get("missing_javadoc/package-info/2", "Override.java"));
        revision.addFile(Paths.get("missing_javadoc/package-info/2", "InterfaceTest.java"));

        Launcher launcher = new Launcher();
        launcher.getEnvironment().setAutoImports(true);
        launcher.addInputResource(folder.getRoot().getAbsolutePath());
        CtModel model = launcher.buildModel();

        RevisionRange revisionRange = mock(RevisionRange.class);
        when(revisionRange.getRevision()).thenReturn(revision);

        final var env = new Environment(model, revisionRange);

        JavadocDetector javadocDetector = new JavadocDetector(env);
        javadocDetector.withPackage(3);
        javadocDetector.withAllJavadocables();
        javadocDetector.withoutAllAccessModifier();
        javadocDetector.withPackageInfoNeeded();
        javadocDetector.withTypelessPackageInfoNeeded();
        javadocDetector.scan(model);
        javadocDetector.getCodeSmells().forEach(n -> System.out.println(n.getSignature() + " " + n.getSummary()));
        assertEquals(3, javadocDetector.getCodeSmells().size());
        Multimap smellMap = mappedCodeSmell(javadocDetector.getCodeSmells());

        assertEquals(1, smellMap.keys().count(Optional.of("missing_javadoc.classes")));
        assertEquals(
                "Missing package-info for the package (missing_javadoc.classes).",
                ((List<CodeSmell>) smellMap.get(Optional.of("missing_javadoc.classes")))
                        .get(0).getSummary().get()
        );

        assertEquals(1, smellMap.keys().count(Optional.of("missing_javadoc.enums")));
        assertEquals(
                "Missing package-info for the package (missing_javadoc.enums).",
                ((List<CodeSmell>) smellMap.get(Optional.of("missing_javadoc.enums")))
                        .get(0).getSummary().get()
        );

        assertEquals(1, smellMap.keys().count(Optional.of("missing_javadoc.interfaces")));
        assertEquals(
                "Missing package-info for the package (missing_javadoc.interfaces).",
                ((List<CodeSmell>) smellMap.get(Optional.of("missing_javadoc.interfaces")))
                        .get(0).getSummary().get()
        );

        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc")));
    }


    private void setFieldTags(JavadocDetector javadocDetector) {
        javadocDetector.withField(5);
        javadocDetector.withAllAccessModifier();
        javadocDetector.withSince(1);
        javadocDetector.withSee(1);
        javadocDetector.withDeprecated(5);
        javadocDetector.withSerial(5);
        javadocDetector.withSerialField(5);
    }

    @Test
    public void testField() throws IOException {
        RevisionMock revision = new RevisionMock(folder);
        revision.addFile(Paths.get("missing_javadoc", "FieldTest.java"));

        Launcher launcher = new Launcher();
        launcher.getEnvironment().setAutoImports(true);
        launcher.addInputResource(folder.getRoot().getAbsolutePath());
        CtModel model = launcher.buildModel();

        RevisionRange revisionRange = mock(RevisionRange.class);
        when(revisionRange.getRevision()).thenReturn(revision);

        final var env = new Environment(model, revisionRange);

        JavadocDetector javadocDetector = new JavadocDetector(env);
        setFieldTags(javadocDetector);
        javadocDetector.scan(model);
        assertEquals(11, javadocDetector.getCodeSmells().size());
        Multimap smellMap = mappedCodeSmell(javadocDetector.getCodeSmells());

        assertEquals(1, smellMap.keys().count(Optional.of("missing_javadoc.FieldTest#fieldWithoutJavadoc")));
        assertEquals(
                "Field fieldWithoutJavadoc has no javadoc.",
                ((List<CodeSmell>) smellMap.get(Optional.of("missing_javadoc.FieldTest#fieldWithoutJavadoc")))
                        .get(0).getSummary().get()
        );

        assertEquals(1, smellMap.keys().count(Optional.of("missing_javadoc.FieldTest#fieldWithoutDescription")));
        assertEquals(
                "No description existing in this javadoc.",
                ((List<CodeSmell>) smellMap.get(Optional.of("missing_javadoc.FieldTest#fieldWithoutDescription")))
                        .get(0).getSummary().get()
        );

        assertEquals(1, smellMap.keys().count(Optional.of("missing_javadoc.FieldTest#fieldWithTooShortDescription")));
        assertEquals(
                "Total-description of this javadoc is too short.",
                ((List<CodeSmell>) smellMap.get(Optional.of("missing_javadoc.FieldTest#fieldWithTooShortDescription")))
                        .get(0).getSummary().get()
        );

        assertEquals(1, smellMap.keys().count(Optional.of("missing_javadoc.FieldTest#fieldWithUnallowedTag")));
        assertEquals(
                "Tag @author is not allowed for this javadocable.",
                ((List<CodeSmell>) smellMap.get(Optional.of("missing_javadoc.FieldTest#fieldWithUnallowedTag")))
                        .get(0).getSummary().get()
        );

        assertEquals(
                5,
                smellMap.keys().count(Optional.of("missing_javadoc.FieldTest#fieldWithTooShortTagDescription"))
        );
        List<CodeSmell> codeSmellsFTSTD = (List<CodeSmell>) smellMap.get(Optional.of(
                "missing_javadoc.FieldTest#fieldWithTooShortTagDescription"));
        assertEquals("Description of the @see tag is too short.", codeSmellsFTSTD.get(0).getSummary().get());
        assertEquals("Description of the @since tag is too short.", codeSmellsFTSTD.get(1).getSummary().get());
        assertEquals("Description of the @deprecated tag is too short.", codeSmellsFTSTD.get(2).getSummary().get());
        assertEquals("Description of the @serial tag is too short.", codeSmellsFTSTD.get(3).getSummary().get());
        assertEquals("Description of the @serialField tag is too short.", codeSmellsFTSTD.get(4).getSummary().get());

        assertEquals(
                1,
                smellMap.keys().count(Optional.of("missing_javadoc.FieldTest#fieldWithMissingAnnotationDeprecated"))
        );
        assertEquals(
                "Javadoc contains @deprecated, but the annotation @Deprecated at the javadocable is missing.",
                ((List<CodeSmell>) smellMap.get(Optional.of(
                        "missing_javadoc.FieldTest#fieldWithMissingAnnotationDeprecated")))
                        .get(0).getSummary().get()
        );

        assertEquals(1, smellMap.keys().count(Optional.of("missing_javadoc.FieldTest#fieldWithMissingTagDeprecated")));
        assertEquals(
                "Missing tag @deprecated in the javadoc.",
                ((List<CodeSmell>) smellMap.get(Optional.of("missing_javadoc.FieldTest#fieldWithMissingTagDeprecated")))
                        .get(0).getSummary().get()
        );

        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.FieldTest#fieldWithNoJavadocCodesmell")));
    }

    @Test
    public void testFieldAMPublic() throws IOException {
        RevisionMock revision = new RevisionMock(folder);
        revision.addFile(Paths.get("missing_javadoc", "FieldTest.java"));

        Launcher launcher = new Launcher();
        launcher.getEnvironment().setAutoImports(true);
        launcher.addInputResource(folder.getRoot().getAbsolutePath());
        CtModel model = launcher.buildModel();

        RevisionRange revisionRange = mock(RevisionRange.class);
        when(revisionRange.getRevision()).thenReturn(revision);

        final var env = new Environment(model, revisionRange);

        JavadocDetector javadocDetector = new JavadocDetector(env);
        setFieldTags(javadocDetector);
        javadocDetector.withoutAccessModifier(new JavadocDetector.AccessModifier[]{
                JavadocDetector.AccessModifier.NULL, JavadocDetector.AccessModifier.PRIVATE, JavadocDetector.AccessModifier.PROTECTED});
        javadocDetector.scan(model);
        assertEquals(2, javadocDetector.getCodeSmells().size());
        Multimap smellMap = mappedCodeSmell(javadocDetector.getCodeSmells());

        assertEquals(1, smellMap.keys().count(Optional.of("missing_javadoc.FieldTest#fieldWithoutJavadoc")));
        assertEquals(
                "Field fieldWithoutJavadoc has no javadoc.",
                ((List<CodeSmell>) smellMap.get(Optional.of("missing_javadoc.FieldTest#fieldWithoutJavadoc")))
                        .get(0).getSummary().get()
        );

        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.FieldTest#fieldWithoutDescription")));
        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.FieldTest#fieldWithTooShortDescription")));

        assertEquals(1, smellMap.keys().count(Optional.of("missing_javadoc.FieldTest#fieldWithUnallowedTag")));
        assertEquals(
                "Tag @author is not allowed for this javadocable.",
                ((List<CodeSmell>) smellMap.get(Optional.of("missing_javadoc.FieldTest#fieldWithUnallowedTag")))
                        .get(0).getSummary().get()
        );

        assertEquals(
                0,
                smellMap.keys().count(Optional.of("missing_javadoc.FieldTest#fieldWithTooShortTagDescription"))
        );
        assertEquals(
                0,
                smellMap.keys().count(Optional.of("missing_javadoc.FieldTest#fieldWithMissingAnnotationDeprecated"))
        );
        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.FieldTest#fieldWithMissingTagDeprecated")));
        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.FieldTest#fieldWithNoJavadocCodesmell")));
    }

    @Test
    public void testFieldAMPrivate() throws IOException {
        RevisionMock revision = new RevisionMock(folder);
        revision.addFile(Paths.get("missing_javadoc", "FieldTest.java"));

        Launcher launcher = new Launcher();
        launcher.getEnvironment().setAutoImports(true);
        launcher.addInputResource(folder.getRoot().getAbsolutePath());
        CtModel model = launcher.buildModel();

        RevisionRange revisionRange = mock(RevisionRange.class);
        when(revisionRange.getRevision()).thenReturn(revision);

        final var env = new Environment(model, revisionRange);

        JavadocDetector javadocDetector = new JavadocDetector(env);
        setFieldTags(javadocDetector);
        javadocDetector.withoutAccessModifier(new JavadocDetector.AccessModifier[]{
                JavadocDetector.AccessModifier.NULL, JavadocDetector.AccessModifier.PUBLIC, JavadocDetector.AccessModifier.PROTECTED});
        javadocDetector.scan(model);
        assertEquals(3, javadocDetector.getCodeSmells().size());
        Multimap smellMap = mappedCodeSmell(javadocDetector.getCodeSmells());

        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.FieldTest#fieldWithoutJavadoc")));

        assertEquals(1, smellMap.keys().count(Optional.of("missing_javadoc.FieldTest#fieldWithoutDescription")));
        assertEquals(
                "No description existing in this javadoc.",
                ((List<CodeSmell>) smellMap.get(Optional.of("missing_javadoc.FieldTest#fieldWithoutDescription")))
                        .get(0).getSummary().get()
        );

        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.FieldTest#fieldWithTooShortDescription")));
        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.FieldTest#fieldWithUnallowedTag")));

        assertEquals(
                0,
                smellMap.keys().count(Optional.of("missing_javadoc.FieldTest#fieldWithTooShortTagDescription"))
        );

        assertEquals(
                1,
                smellMap.keys().count(Optional.of("missing_javadoc.FieldTest#fieldWithMissingAnnotationDeprecated"))
        );
        assertEquals(
                "Javadoc contains @deprecated, but the annotation @Deprecated at the javadocable is missing.",
                ((List<CodeSmell>) smellMap.get(Optional.of(
                        "missing_javadoc.FieldTest#fieldWithMissingAnnotationDeprecated")))
                        .get(0).getSummary().get()
        );

        assertEquals(1, smellMap.keys().count(Optional.of("missing_javadoc.FieldTest#fieldWithMissingTagDeprecated")));
        assertEquals(
                "Missing tag @deprecated in the javadoc.",
                ((List<CodeSmell>) smellMap.get(Optional.of("missing_javadoc.FieldTest#fieldWithMissingTagDeprecated")))
                        .get(0).getSummary().get()
        );

        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.FieldTest#fieldWithNoJavadocCodesmell")));
    }

    @Test
    public void testFieldAMProtected() throws IOException {
        RevisionMock revision = new RevisionMock(folder);
        revision.addFile(Paths.get("missing_javadoc", "FieldTest.java"));

        Launcher launcher = new Launcher();
        launcher.getEnvironment().setAutoImports(true);
        launcher.addInputResource(folder.getRoot().getAbsolutePath());
        CtModel model = launcher.buildModel();

        RevisionRange revisionRange = mock(RevisionRange.class);
        when(revisionRange.getRevision()).thenReturn(revision);

        final var env = new Environment(model, revisionRange);

        JavadocDetector javadocDetector = new JavadocDetector(env);
        setFieldTags(javadocDetector);
        javadocDetector.withoutAccessModifier(new JavadocDetector.AccessModifier[]{
                JavadocDetector.AccessModifier.NULL, JavadocDetector.AccessModifier.PRIVATE, JavadocDetector.AccessModifier.PUBLIC});
        javadocDetector.scan(model);
        assertEquals(5, javadocDetector.getCodeSmells().size());
        Multimap smellMap = mappedCodeSmell(javadocDetector.getCodeSmells());
        assertEquals(
                5,
                smellMap.keys().count(Optional.of("missing_javadoc.FieldTest#fieldWithTooShortTagDescription"))
        );

        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.FieldTest#fieldWithoutJavadoc")));
        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.FieldTest#fieldWithoutDescription")));
        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.FieldTest#fieldWithTooShortDescription")));
        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.FieldTest#fieldWithUnallowedTag")));

        assertEquals(
                5,
                smellMap.keys().count(Optional.of("missing_javadoc.FieldTest#fieldWithTooShortTagDescription"))
        );
        List<CodeSmell> codeSmellsFTSTD = (List<CodeSmell>) smellMap.get(Optional.of(
                "missing_javadoc.FieldTest#fieldWithTooShortTagDescription"));
        assertEquals("Description of the @see tag is too short.", codeSmellsFTSTD.get(0).getSummary().get());
        assertEquals("Description of the @since tag is too short.", codeSmellsFTSTD.get(1).getSummary().get());
        assertEquals("Description of the @deprecated tag is too short.", codeSmellsFTSTD.get(2).getSummary().get());
        assertEquals("Description of the @serial tag is too short.", codeSmellsFTSTD.get(3).getSummary().get());
        assertEquals("Description of the @serialField tag is too short.", codeSmellsFTSTD.get(4).getSummary().get());

        assertEquals(
                0,
                smellMap.keys().count(Optional.of("missing_javadoc.FieldTest#fieldWithMissingAnnotationDeprecated"))
        );
        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.FieldTest#fieldWithMissingTagDeprecated")));
        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.FieldTest#fieldWithNoJavadocCodesmell")));
    }

    @Test
    public void testFieldAMNull() throws IOException {
        RevisionMock revision = new RevisionMock(folder);
        revision.addFile(Paths.get("missing_javadoc", "FieldTest.java"));

        Launcher launcher = new Launcher();
        launcher.getEnvironment().setAutoImports(true);
        launcher.addInputResource(folder.getRoot().getAbsolutePath());
        CtModel model = launcher.buildModel();

        RevisionRange revisionRange = mock(RevisionRange.class);
        when(revisionRange.getRevision()).thenReturn(revision);

        final var env = new Environment(model, revisionRange);

        JavadocDetector javadocDetector = new JavadocDetector(env);
        setFieldTags(javadocDetector);
        javadocDetector.withoutAccessModifier(new JavadocDetector.AccessModifier[]{
                JavadocDetector.AccessModifier.PUBLIC, JavadocDetector.AccessModifier.PRIVATE, JavadocDetector.AccessModifier.PROTECTED});
        javadocDetector.scan(model);
        assertEquals(1, javadocDetector.getCodeSmells().size());
        Multimap smellMap = mappedCodeSmell(javadocDetector.getCodeSmells());

        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.FieldTest#fieldWithoutJavadoc")));
        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.FieldTest#fieldWithoutDescription")));

        assertEquals(1, smellMap.keys().count(Optional.of("missing_javadoc.FieldTest#fieldWithTooShortDescription")));
        assertEquals(
                "Total-description of this javadoc is too short.",
                ((List<CodeSmell>) smellMap.get(Optional.of("missing_javadoc.FieldTest#fieldWithTooShortDescription")))
                        .get(0).getSummary().get()
        );

        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.FieldTest#fieldWithUnallowedTag")));
        assertEquals(
                0,
                smellMap.keys().count(Optional.of("missing_javadoc.FieldTest#fieldWithTooShortTagDescription"))
        );
        assertEquals(
                0,
                smellMap.keys().count(Optional.of("missing_javadoc.FieldTest#fieldWithMissingAnnotationDeprecated"))
        );
        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.FieldTest#fieldWithMissingTagDeprecated")));
        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.FieldTest#fieldWithNoJavadocCodesmell")));
    }

    @Test
    public void testFieldAMMixed() throws IOException {
        RevisionMock revision = new RevisionMock(folder);
        revision.addFile(Paths.get("missing_javadoc", "FieldTest.java"));

        Launcher launcher = new Launcher();
        launcher.getEnvironment().setAutoImports(true);
        launcher.addInputResource(folder.getRoot().getAbsolutePath());
        CtModel model = launcher.buildModel();

        RevisionRange revisionRange = mock(RevisionRange.class);
        when(revisionRange.getRevision()).thenReturn(revision);

        final var env = new Environment(model, revisionRange);

        JavadocDetector javadocDetector = new JavadocDetector(env);
        setFieldTags(javadocDetector);
        javadocDetector.withoutAccessModifier(new JavadocDetector.AccessModifier[]{
                JavadocDetector.AccessModifier.NULL, JavadocDetector.AccessModifier.PUBLIC});
        javadocDetector.scan(model);
        assertEquals(8, javadocDetector.getCodeSmells().size());
        Multimap smellMap = mappedCodeSmell(javadocDetector.getCodeSmells());

        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.FieldTest#fieldWithoutJavadoc")));

        assertEquals(1, smellMap.keys().count(Optional.of("missing_javadoc.FieldTest#fieldWithoutDescription")));
        assertEquals(
                "No description existing in this javadoc.",
                ((List<CodeSmell>) smellMap.get(Optional.of("missing_javadoc.FieldTest#fieldWithoutDescription")))
                        .get(0).getSummary().get()
        );

        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.FieldTest#fieldWithTooShortDescription")));
        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.FieldTest#fieldWithUnallowedTag")));

        assertEquals(
                5,
                smellMap.keys().count(Optional.of("missing_javadoc.FieldTest#fieldWithTooShortTagDescription"))
        );
        List<CodeSmell> codeSmellsFTSTD = (List<CodeSmell>) smellMap.get(Optional.of(
                "missing_javadoc.FieldTest#fieldWithTooShortTagDescription"));
        assertEquals("Description of the @see tag is too short.", codeSmellsFTSTD.get(0).getSummary().get());
        assertEquals("Description of the @since tag is too short.", codeSmellsFTSTD.get(1).getSummary().get());
        assertEquals("Description of the @deprecated tag is too short.", codeSmellsFTSTD.get(2).getSummary().get());
        assertEquals("Description of the @serial tag is too short.", codeSmellsFTSTD.get(3).getSummary().get());
        assertEquals("Description of the @serialField tag is too short.", codeSmellsFTSTD.get(4).getSummary().get());

        assertEquals(
                1,
                smellMap.keys().count(Optional.of("missing_javadoc.FieldTest#fieldWithMissingAnnotationDeprecated"))
        );
        assertEquals(
                "Javadoc contains @deprecated, but the annotation @Deprecated at the javadocable is missing.",
                ((List<CodeSmell>) smellMap.get(Optional.of(
                        "missing_javadoc.FieldTest#fieldWithMissingAnnotationDeprecated")))
                        .get(0).getSummary().get()
        );

        assertEquals(1, smellMap.keys().count(Optional.of("missing_javadoc.FieldTest#fieldWithMissingTagDeprecated")));
        assertEquals(
                "Missing tag @deprecated in the javadoc.",
                ((List<CodeSmell>) smellMap.get(Optional.of("missing_javadoc.FieldTest#fieldWithMissingTagDeprecated")))
                        .get(0).getSummary().get()
        );

        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.FieldTest#fieldWithNoJavadocCodesmell")));
    }

    @Test
    public void testFieldJavadocable() throws IOException {
        RevisionMock revision = new RevisionMock(folder);
        revision.addFile(Paths.get("missing_javadoc", "FieldTest.java"));

        Launcher launcher = new Launcher();
        launcher.getEnvironment().setAutoImports(true);
        launcher.addInputResource(folder.getRoot().getAbsolutePath());
        CtModel model = launcher.buildModel();

        RevisionRange revisionRange = mock(RevisionRange.class);
        when(revisionRange.getRevision()).thenReturn(revision);

        final var env = new Environment(model, revisionRange);

        JavadocDetector javadocDetector = new JavadocDetector(env);
        setFieldTags(javadocDetector);
        javadocDetector.withoutField();
        javadocDetector.withClass(5, 15);
        javadocDetector.scan(model);
        assertEquals(1, javadocDetector.getCodeSmells().size());
        Multimap smellMap = mappedCodeSmell(javadocDetector.getCodeSmells());
        assertEquals(1, smellMap.keys().count(Optional.of("missing_javadoc.FieldTest")));

        assertEquals(
                "Class FieldTest has no javadoc.",
                ((List<CodeSmell>) smellMap.get(Optional.of("missing_javadoc.FieldTest"))).get(0).getSummary().get()
        );
    }

    @Test
    public void testConstructor() throws IOException {
        RevisionMock revision = new RevisionMock(folder);
        revision.addFile(Paths.get("missing_javadoc", "ConstructorTest.java"));

        Launcher launcher = new Launcher();
        launcher.getEnvironment().setAutoImports(true);
        launcher.addInputResource(folder.getRoot().getAbsolutePath());
        CtModel model = launcher.buildModel();

        RevisionRange revisionRange = mock(RevisionRange.class);
        when(revisionRange.getRevision()).thenReturn(revision);

        final var env = new Environment(model, revisionRange);

        JavadocDetector javadocDetector = new JavadocDetector(env);
        javadocDetector.withConstructor(5, 15);
        javadocDetector.withAllAccessModifier();
        setMethodTags(javadocDetector);
        javadocDetector.withoutMethod();
        javadocDetector.scan(model);
        assertEquals(18, javadocDetector.getCodeSmells().size());

        Multimap smellMap = mappedCodeSmell(javadocDetector.getCodeSmells());
        assertEquals(1, smellMap.keys().count(Optional.of("missing_javadoc.ConstructorTest()")));
        assertEquals(
                "Constructor ConstructorTest() has no javadoc.",
                ((List<CodeSmell>) smellMap.get(Optional.of("missing_javadoc.ConstructorTest()")))
                        .get(0).getSummary().get()
        );

        assertEquals(5, smellMap.keys().count(Optional.of("missing_javadoc.ConstructorTest(java.lang.String)")));
        List<CodeSmell> codeSmellsCTS = (List<CodeSmell>) smellMap.get(Optional.of(
                "missing_javadoc.ConstructorTest(java.lang.String)"));
        assertEquals(
                "Missing tag @throws IllegalArgumentException in the javadoc.",
                codeSmellsCTS.get(0).getSummary().get()
        );
        assertEquals("Missing tag @deprecated in the javadoc.", codeSmellsCTS.get(1).getSummary().get());
        assertEquals("Missing tag @param text in the javadoc.", codeSmellsCTS.get(2).getSummary().get());
        assertEquals("Missing tag @param <T> in the javadoc.", codeSmellsCTS.get(3).getSummary().get());
        assertEquals("No description existing in this javadoc.", codeSmellsCTS.get(4).getSummary().get());

        assertEquals(12, smellMap.keys().count(Optional.of("missing_javadoc.ConstructorTest(int)")));
        List<CodeSmell> codeSmellsCTI = (List<CodeSmell>) smellMap.get(Optional.of(
                "missing_javadoc.ConstructorTest(int)"));
        assertEquals("Tag @return is not allowed for this javadocable.", codeSmellsCTI.get(0).getSummary().get());
        assertEquals(
                "Javadoc contains @throws/@exception IllegalAccessException, but this does not thrown by this or a called executable.",
                codeSmellsCTI.get(1).getSummary().get()
        );
        assertEquals(
                "Javadoc contains @deprecated, but the annotation @Deprecated at the javadocable is missing.",
                codeSmellsCTI.get(2).getSummary().get()
        );
        assertEquals("Description of the @param number is too short.", codeSmellsCTI.get(3).getSummary().get());
        assertEquals("Description of the @deprecated tag is too short.", codeSmellsCTI.get(4).getSummary().get());
        assertEquals(
                "Description of the @exception IllegalArgumentException is too short.",
                codeSmellsCTI.get(5).getSummary().get()
        );
        assertEquals(
                "Description of the @throws IllegalAccessException is too short.",
                codeSmellsCTI.get(6).getSummary().get()
        );
        assertEquals("Description of the @see tag is too short.", codeSmellsCTI.get(7).getSummary().get());
        assertEquals("Description of the @since tag is too short.", codeSmellsCTI.get(8).getSummary().get());
        assertEquals(
                "Javadoc contains @param <T>, but this parameter does not exists.",
                codeSmellsCTI.get(9).getSummary().get()
        );
        assertEquals("Short-description of this javadoc is too short.", codeSmellsCTI.get(10).getSummary().get());
        assertEquals("Long-description of this javadoc is too short.", codeSmellsCTI.get(11).getSummary().get());

        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.ConstructorTest(java.lang.String,int")));
    }

    @Test
    public void testConstructorAMPublic() throws IOException {
        RevisionMock revision = new RevisionMock(folder);
        revision.addFile(Paths.get("missing_javadoc", "ConstructorTest.java"));

        Launcher launcher = new Launcher();
        launcher.getEnvironment().setAutoImports(true);
        launcher.addInputResource(folder.getRoot().getAbsolutePath());
        CtModel model = launcher.buildModel();

        RevisionRange revisionRange = mock(RevisionRange.class);
        when(revisionRange.getRevision()).thenReturn(revision);

        final var env = new Environment(model, revisionRange);

        JavadocDetector javadocDetector = new JavadocDetector(env);
        javadocDetector.withConstructor(5, 15);
        javadocDetector.withAccessModifier(JavadocDetector.AccessModifier.PUBLIC);
        setMethodTags(javadocDetector);
        javadocDetector.withoutMethod();
        javadocDetector.scan(model);
        assertEquals(1, javadocDetector.getCodeSmells().size());
        Multimap smellMap = mappedCodeSmell(javadocDetector.getCodeSmells());

        assertEquals(1, smellMap.keys().count(Optional.of("missing_javadoc.ConstructorTest()")));
        assertEquals(
                "Constructor ConstructorTest() has no javadoc.",
                ((List<CodeSmell>) smellMap.get(Optional.of("missing_javadoc.ConstructorTest()")))
                        .get(0).getSummary().get()
        );

        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.ConstructorTest(java.lang.String)")));
        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.ConstructorTest(int)")));
        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.ConstructorTest(java.lang.String,int")));
    }

    @Test
    public void testConstructorAMPrivate() throws IOException {
        RevisionMock revision = new RevisionMock(folder);
        revision.addFile(Paths.get("missing_javadoc", "ConstructorTest.java"));

        Launcher launcher = new Launcher();
        launcher.getEnvironment().setAutoImports(true);
        launcher.addInputResource(folder.getRoot().getAbsolutePath());
        CtModel model = launcher.buildModel();

        RevisionRange revisionRange = mock(RevisionRange.class);
        when(revisionRange.getRevision()).thenReturn(revision);

        final var env = new Environment(model, revisionRange);

        JavadocDetector javadocDetector = new JavadocDetector(env);
        javadocDetector.withConstructor(5, 15);
        javadocDetector.withAccessModifier(JavadocDetector.AccessModifier.PRIVATE);
        setMethodTags(javadocDetector);
        javadocDetector.withoutMethod();
        javadocDetector.scan(model);
        assertEquals(5, javadocDetector.getCodeSmells().size());
        Multimap smellMap = mappedCodeSmell(javadocDetector.getCodeSmells());

        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.ConstructorTest()")));

        assertEquals(5, smellMap.keys().count(Optional.of("missing_javadoc.ConstructorTest(java.lang.String)")));
        List<CodeSmell> codeSmellsCTS = (List<CodeSmell>) smellMap.get(Optional.of(
                "missing_javadoc.ConstructorTest(java.lang.String)"));
        assertEquals(
                "Missing tag @throws IllegalArgumentException in the javadoc.",
                codeSmellsCTS.get(0).getSummary().get()
        );
        assertEquals("Missing tag @deprecated in the javadoc.", codeSmellsCTS.get(1).getSummary().get());
        assertEquals("Missing tag @param text in the javadoc.", codeSmellsCTS.get(2).getSummary().get());
        assertEquals("Missing tag @param <T> in the javadoc.", codeSmellsCTS.get(3).getSummary().get());
        assertEquals("No description existing in this javadoc.", codeSmellsCTS.get(4).getSummary().get());

        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.ConstructorTest(int)")));
        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.ConstructorTest(java.lang.String,int")));
    }

    @Test
    public void testConstructorAMProtected() throws IOException {
        RevisionMock revision = new RevisionMock(folder);
        revision.addFile(Paths.get("missing_javadoc", "ConstructorTest.java"));

        Launcher launcher = new Launcher();
        launcher.getEnvironment().setAutoImports(true);
        launcher.addInputResource(folder.getRoot().getAbsolutePath());
        CtModel model = launcher.buildModel();

        RevisionRange revisionRange = mock(RevisionRange.class);
        when(revisionRange.getRevision()).thenReturn(revision);

        final var env = new Environment(model, revisionRange);

        JavadocDetector javadocDetector = new JavadocDetector(env);
        javadocDetector.withConstructor(5, 15);
        javadocDetector.withAccessModifier(JavadocDetector.AccessModifier.PROTECTED);
        setMethodTags(javadocDetector);
        javadocDetector.withoutMethod();
        javadocDetector.scan(model);
        assertEquals(0, javadocDetector.getCodeSmells().size());
    }

    @Test
    public void testConstructorAMNull() throws IOException {
        RevisionMock revision = new RevisionMock(folder);
        revision.addFile(Paths.get("missing_javadoc", "ConstructorTest.java"));

        Launcher launcher = new Launcher();
        launcher.getEnvironment().setAutoImports(true);
        launcher.addInputResource(folder.getRoot().getAbsolutePath());
        CtModel model = launcher.buildModel();

        RevisionRange revisionRange = mock(RevisionRange.class);
        when(revisionRange.getRevision()).thenReturn(revision);

        final var env = new Environment(model, revisionRange);

        JavadocDetector javadocDetector = new JavadocDetector(env);
        javadocDetector.withConstructor(5, 15);
        javadocDetector.withAccessModifier(JavadocDetector.AccessModifier.NULL);
        setMethodTags(javadocDetector);
        javadocDetector.withoutMethod();
        javadocDetector.scan(model);
        assertEquals(12, javadocDetector.getCodeSmells().size());
        Multimap smellMap = mappedCodeSmell(javadocDetector.getCodeSmells());
        assertEquals(12, smellMap.keys().count(Optional.of("missing_javadoc.ConstructorTest(int)")));

        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.ConstructorTest()")));
        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.ConstructorTest(java.lang.String)")));

        assertEquals(12, smellMap.keys().count(Optional.of("missing_javadoc.ConstructorTest(int)")));
        List<CodeSmell> codeSmellsCTI = (List<CodeSmell>) smellMap.get(Optional.of(
                "missing_javadoc.ConstructorTest(int)"));
        assertEquals("Tag @return is not allowed for this javadocable.", codeSmellsCTI.get(0).getSummary().get());
        assertEquals(
                "Javadoc contains @throws/@exception IllegalAccessException, but this does not thrown by this or a called executable.",
                codeSmellsCTI.get(1).getSummary().get()
        );
        assertEquals(
                "Javadoc contains @deprecated, but the annotation @Deprecated at the javadocable is missing.",
                codeSmellsCTI.get(2).getSummary().get()
        );
        assertEquals("Description of the @param number is too short.", codeSmellsCTI.get(3).getSummary().get());
        assertEquals("Description of the @deprecated tag is too short.", codeSmellsCTI.get(4).getSummary().get());
        assertEquals(
                "Description of the @exception IllegalArgumentException is too short.",
                codeSmellsCTI.get(5).getSummary().get()
        );
        assertEquals(
                "Description of the @throws IllegalAccessException is too short.",
                codeSmellsCTI.get(6).getSummary().get()
        );
        assertEquals("Description of the @see tag is too short.", codeSmellsCTI.get(7).getSummary().get());
        assertEquals("Description of the @since tag is too short.", codeSmellsCTI.get(8).getSummary().get());
        assertEquals(
                "Javadoc contains @param <T>, but this parameter does not exists.",
                codeSmellsCTI.get(9).getSummary().get()
        );
        assertEquals("Short-description of this javadoc is too short.", codeSmellsCTI.get(10).getSummary().get());
        assertEquals("Long-description of this javadoc is too short.", codeSmellsCTI.get(11).getSummary().get());

        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.ConstructorTest(java.lang.String,int")));
    }

    @Test
    public void testConstructorAMMixed() throws IOException {
        RevisionMock revision = new RevisionMock(folder);
        revision.addFile(Paths.get("missing_javadoc", "ConstructorTest.java"));

        Launcher launcher = new Launcher();
        launcher.getEnvironment().setAutoImports(true);
        launcher.addInputResource(folder.getRoot().getAbsolutePath());
        CtModel model = launcher.buildModel();

        RevisionRange revisionRange = mock(RevisionRange.class);
        when(revisionRange.getRevision()).thenReturn(revision);

        final var env = new Environment(model, revisionRange);

        JavadocDetector javadocDetector = new JavadocDetector(env);
        javadocDetector.withConstructor(5, 15);
        javadocDetector.withAccessModifier(new JavadocDetector.AccessModifier[]
                {JavadocDetector.AccessModifier.PROTECTED, JavadocDetector.AccessModifier.NULL, JavadocDetector.AccessModifier.PUBLIC});
        setMethodTags(javadocDetector);
        javadocDetector.withoutMethod();
        javadocDetector.scan(model);
        assertEquals(13, javadocDetector.getCodeSmells().size());
        Multimap smellMap = mappedCodeSmell(javadocDetector.getCodeSmells());
        assertEquals(1, smellMap.keys().count(Optional.of("missing_javadoc.ConstructorTest()")));
        assertEquals(12, smellMap.keys().count(Optional.of("missing_javadoc.ConstructorTest(int)")));

        assertEquals(1, smellMap.keys().count(Optional.of("missing_javadoc.ConstructorTest()")));
        assertEquals(
                "Constructor ConstructorTest() has no javadoc.",
                ((List<CodeSmell>) smellMap.get(Optional.of("missing_javadoc.ConstructorTest()")))
                        .get(0).getSummary().get()
        );

        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.ConstructorTest(java.lang.String)")));

        assertEquals(12, smellMap.keys().count(Optional.of("missing_javadoc.ConstructorTest(int)")));
        List<CodeSmell> codeSmellsCTI = (List<CodeSmell>) smellMap.get(Optional.of(
                "missing_javadoc.ConstructorTest(int)"));
        assertEquals("Tag @return is not allowed for this javadocable.", codeSmellsCTI.get(0).getSummary().get());
        assertEquals(
                "Javadoc contains @throws/@exception IllegalAccessException, but this does not thrown by this or a called executable.",
                codeSmellsCTI.get(1).getSummary().get()
        );
        assertEquals(
                "Javadoc contains @deprecated, but the annotation @Deprecated at the javadocable is missing.",
                codeSmellsCTI.get(2).getSummary().get()
        );
        assertEquals("Description of the @param number is too short.", codeSmellsCTI.get(3).getSummary().get());
        assertEquals("Description of the @deprecated tag is too short.", codeSmellsCTI.get(4).getSummary().get());
        assertEquals(
                "Description of the @exception IllegalArgumentException is too short.",
                codeSmellsCTI.get(5).getSummary().get()
        );
        assertEquals(
                "Description of the @throws IllegalAccessException is too short.",
                codeSmellsCTI.get(6).getSummary().get()
        );
        assertEquals("Description of the @see tag is too short.", codeSmellsCTI.get(7).getSummary().get());
        assertEquals("Description of the @since tag is too short.", codeSmellsCTI.get(8).getSummary().get());
        assertEquals(
                "Javadoc contains @param <T>, but this parameter does not exists.",
                codeSmellsCTI.get(9).getSummary().get()
        );
        assertEquals("Short-description of this javadoc is too short.", codeSmellsCTI.get(10).getSummary().get());
        assertEquals("Long-description of this javadoc is too short.", codeSmellsCTI.get(11).getSummary().get());

        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.ConstructorTest(java.lang.String,int")));
    }

    @Test
    public void testConstructorJavadocable() throws IOException {
        RevisionMock revision = new RevisionMock(folder);
        revision.addFile(Paths.get("missing_javadoc", "ConstructorTest.java"));

        Launcher launcher = new Launcher();
        launcher.getEnvironment().setAutoImports(true);
        launcher.addInputResource(folder.getRoot().getAbsolutePath());
        CtModel model = launcher.buildModel();

        RevisionRange revisionRange = mock(RevisionRange.class);
        when(revisionRange.getRevision()).thenReturn(revision);

        final var env = new Environment(model, revisionRange);

        JavadocDetector javadocDetector = new JavadocDetector(env);
        javadocDetector.withField(5, 15);
        javadocDetector.withMethod(5, 15);
        javadocDetector.withAllAccessModifier();
        setMethodTags(javadocDetector);
        javadocDetector.scan(model);
        assertEquals(3, javadocDetector.getCodeSmells().size());
        Multimap smellMap = mappedCodeSmell(javadocDetector.getCodeSmells());

        assertEquals(
                2,
                smellMap.keys().count(Optional.of("missing_javadoc.ConstructorTest#fieldForTheJavadocableTest"))
        );
        List<CodeSmell> codeSmellsFFTJT = (List<CodeSmell>) smellMap.get(Optional.of(
                "missing_javadoc.ConstructorTest#fieldForTheJavadocableTest"));
        assertEquals("Tag @author is not allowed for this javadocable.", codeSmellsFFTJT.get(0).getSummary().get());
        assertEquals("Tag @version is not allowed for this javadocable.", codeSmellsFFTJT.get(1).getSummary().get());

        assertEquals(
                1,
                smellMap.keys()
                        .count(Optional.of("missing_javadoc.ConstructorTest#checkFileExtensionForTxt(java.lang.String)"))
        );
        assertEquals(
                "Method checkFileExtensionForTxt(java.lang.String) has no javadoc.",
                ((List<CodeSmell>) smellMap.get(Optional.of(
                        "missing_javadoc.ConstructorTest#checkFileExtensionForTxt(java.lang.String)")))
                        .get(0).getSummary().get()
        );
    }


    private void setAnnotationTags(JavadocDetector javadocDetector) {
        javadocDetector.withAnnotationType(5, 15);
        javadocDetector.withAllAccessModifier();
        javadocDetector.withAuthor(2);
        javadocDetector.withVersion(1);
        javadocDetector.withSince(1);
        javadocDetector.withSee(3);
    }

    @Test
    public void testAnnotation() throws IOException {
        RevisionMock revision = new RevisionMock(folder);
        revision.addFile(Paths.get("missing_javadoc/annotation", "Override.java"));
        revision.addFile(Paths.get("missing_javadoc/annotation", "Override_faultyTags.java"));
        revision.addFile(Paths.get("missing_javadoc/annotation", "Override_noDescription.java"));
        revision.addFile(Paths.get("missing_javadoc/annotation", "Override_noJavadoc.java"));
        revision.addFile(Paths.get("missing_javadoc/annotation", "Override_shortDescription.java"));

        Launcher launcher = new Launcher();
        launcher.getEnvironment().setAutoImports(true);
        launcher.addInputResource(folder.getRoot().getAbsolutePath());
        CtModel model = launcher.buildModel();

        RevisionRange revisionRange = mock(RevisionRange.class);
        when(revisionRange.getRevision()).thenReturn(revision);

        final var env = new Environment(model, revisionRange);

        JavadocDetector javadocDetector = new JavadocDetector(env);
        setAnnotationTags(javadocDetector);
        javadocDetector.scan(model);
        assertEquals(9, javadocDetector.getCodeSmells().size());
        Multimap smellMap = mappedCodeSmell(javadocDetector.getCodeSmells());

        assertEquals(5, smellMap.keys().count(Optional.of("missing_javadoc.Override_faultyTags")));
        List<CodeSmell> codeSmellsOFT = (List<CodeSmell>) smellMap.get(Optional.of("missing_javadoc.Override_faultyTags"));
        assertEquals("Tag @unknown is not allowed for this javadocable.", codeSmellsOFT.get(0).getSummary().get());
        assertEquals("Description of the @author tag is too short.", codeSmellsOFT.get(1).getSummary().get());
        assertEquals("Description of the @version tag is too short.", codeSmellsOFT.get(2).getSummary().get());
        assertEquals("Description of the @since tag is too short.", codeSmellsOFT.get(3).getSummary().get());
        assertEquals("Description of the @see tag is too short.", codeSmellsOFT.get(4).getSummary().get());

        assertEquals(1, smellMap.keys().count(Optional.of("missing_javadoc.Override_noDescription")));
        assertEquals(
                "No description existing in this javadoc.",
                ((List<CodeSmell>) smellMap.get(Optional.of("missing_javadoc.Override_noDescription")))
                        .get(0).getSummary().get()
        );

        assertEquals(1, smellMap.keys().count(Optional.of("missing_javadoc.Override_noJavadoc")));
        assertEquals(
                "AnnotationType Override_noJavadoc has no javadoc.",
                ((List<CodeSmell>) smellMap.get(Optional.of("missing_javadoc.Override_noJavadoc")))
                        .get(0).getSummary().get()
        );

        assertEquals(2, smellMap.keys().count(Optional.of("missing_javadoc.Override_shortDescription")));
        List<CodeSmell> codeSmellsOSD = (List<CodeSmell>) smellMap.get(Optional.of(
                "missing_javadoc.Override_shortDescription"));
        assertEquals("Short-description of this javadoc is too short.", codeSmellsOSD.get(0).getSummary().get());
        assertEquals("Long-description of this javadoc is too short.", codeSmellsOSD.get(1).getSummary().get());

        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.Override")));
    }

    @Test
    public void testAnnotationAMPublic() throws IOException {
        RevisionMock revision = new RevisionMock(folder);
        revision.addFile(Paths.get("missing_javadoc/annotation", "Override.java"));
        revision.addFile(Paths.get("missing_javadoc/annotation", "Override_faultyTags.java"));
        revision.addFile(Paths.get("missing_javadoc/annotation", "Override_noDescription.java"));
        revision.addFile(Paths.get("missing_javadoc/annotation", "Override_noJavadoc.java"));
        revision.addFile(Paths.get("missing_javadoc/annotation", "Override_shortDescription.java"));

        Launcher launcher = new Launcher();
        launcher.getEnvironment().setAutoImports(true);
        launcher.addInputResource(folder.getRoot().getAbsolutePath());
        CtModel model = launcher.buildModel();

        RevisionRange revisionRange = mock(RevisionRange.class);
        when(revisionRange.getRevision()).thenReturn(revision);

        final var env = new Environment(model, revisionRange);

        JavadocDetector javadocDetector = new JavadocDetector(env);
        setAnnotationTags(javadocDetector);
        javadocDetector.withoutAccessModifier(new JavadocDetector.AccessModifier[]{
                JavadocDetector.AccessModifier.NULL, JavadocDetector.AccessModifier.PRIVATE, JavadocDetector.AccessModifier.PROTECTED});
        javadocDetector.scan(model);
        assertEquals(2, javadocDetector.getCodeSmells().size());
        Multimap smellMap = mappedCodeSmell(javadocDetector.getCodeSmells());

        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.Override_faultyTags")));
        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.Override_noDescription")));
        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.Override_noJavadoc")));

        assertEquals(2, smellMap.keys().count(Optional.of("missing_javadoc.Override_shortDescription")));
        List<CodeSmell> codeSmellsOSD = (List<CodeSmell>) smellMap.get(Optional.of(
                "missing_javadoc.Override_shortDescription"));
        assertEquals("Short-description of this javadoc is too short.", codeSmellsOSD.get(0).getSummary().get());
        assertEquals("Long-description of this javadoc is too short.", codeSmellsOSD.get(1).getSummary().get());

        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.Override")));
    }

    @Test
    public void testAnnotationAMPrivate() throws IOException {
        RevisionMock revision = new RevisionMock(folder);
        revision.addFile(Paths.get("missing_javadoc/annotation", "Override.java"));
        revision.addFile(Paths.get("missing_javadoc/annotation", "Override_faultyTags.java"));
        revision.addFile(Paths.get("missing_javadoc/annotation", "Override_noDescription.java"));
        revision.addFile(Paths.get("missing_javadoc/annotation", "Override_noJavadoc.java"));
        revision.addFile(Paths.get("missing_javadoc/annotation", "Override_shortDescription.java"));

        Launcher launcher = new Launcher();
        launcher.getEnvironment().setAutoImports(true);
        launcher.addInputResource(folder.getRoot().getAbsolutePath());
        CtModel model = launcher.buildModel();

        RevisionRange revisionRange = mock(RevisionRange.class);
        when(revisionRange.getRevision()).thenReturn(revision);

        final var env = new Environment(model, revisionRange);

        JavadocDetector javadocDetector = new JavadocDetector(env);
        setAnnotationTags(javadocDetector);
        javadocDetector.withoutAccessModifier(new JavadocDetector.AccessModifier[]{
                JavadocDetector.AccessModifier.NULL, JavadocDetector.AccessModifier.PUBLIC, JavadocDetector.AccessModifier.PROTECTED});
        javadocDetector.scan(model);
        assertEquals(5, javadocDetector.getCodeSmells().size());
        Multimap smellMap = mappedCodeSmell(javadocDetector.getCodeSmells());

        assertEquals(5, smellMap.keys().count(Optional.of("missing_javadoc.Override_faultyTags")));
        List<CodeSmell> codeSmellsOFT = (List<CodeSmell>) smellMap.get(Optional.of("missing_javadoc.Override_faultyTags"));
        assertEquals("Tag @unknown is not allowed for this javadocable.", codeSmellsOFT.get(0).getSummary().get());
        assertEquals("Description of the @author tag is too short.", codeSmellsOFT.get(1).getSummary().get());
        assertEquals("Description of the @version tag is too short.", codeSmellsOFT.get(2).getSummary().get());
        assertEquals("Description of the @since tag is too short.", codeSmellsOFT.get(3).getSummary().get());
        assertEquals("Description of the @see tag is too short.", codeSmellsOFT.get(4).getSummary().get());

        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.Override_noDescription")));
        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.Override_noJavadoc")));
        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.Override_shortDescription")));
        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.Override")));
    }

    @Test
    public void testAnnotationAMProtected() throws IOException {
        RevisionMock revision = new RevisionMock(folder);
        revision.addFile(Paths.get("missing_javadoc/annotation", "Override.java"));
        revision.addFile(Paths.get("missing_javadoc/annotation", "Override_faultyTags.java"));
        revision.addFile(Paths.get("missing_javadoc/annotation", "Override_noDescription.java"));
        revision.addFile(Paths.get("missing_javadoc/annotation", "Override_noJavadoc.java"));
        revision.addFile(Paths.get("missing_javadoc/annotation", "Override_shortDescription.java"));

        Launcher launcher = new Launcher();
        launcher.getEnvironment().setAutoImports(true);
        launcher.addInputResource(folder.getRoot().getAbsolutePath());
        CtModel model = launcher.buildModel();

        RevisionRange revisionRange = mock(RevisionRange.class);
        when(revisionRange.getRevision()).thenReturn(revision);

        final var env = new Environment(model, revisionRange);

        JavadocDetector javadocDetector = new JavadocDetector(env);
        setAnnotationTags(javadocDetector);
        javadocDetector.withoutAccessModifier(new JavadocDetector.AccessModifier[]{
                JavadocDetector.AccessModifier.NULL, JavadocDetector.AccessModifier.PRIVATE, JavadocDetector.AccessModifier.PUBLIC});
        javadocDetector.scan(model);
        assertEquals(1, javadocDetector.getCodeSmells().size());
        Multimap smellMap = mappedCodeSmell(javadocDetector.getCodeSmells());

        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.Override_faultyTags")));
        assertEquals(1, smellMap.keys().count(Optional.of("missing_javadoc.Override_noDescription")));
        assertEquals(
                "No description existing in this javadoc.",
                ((List<CodeSmell>) smellMap.get(Optional.of("missing_javadoc.Override_noDescription")))
                        .get(0).getSummary().get()
        );
        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.Override_noJavadoc")));
        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.Override_shortDescription")));
        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.Override")));
    }

    @Test
    public void testAnnotationAMNull() throws IOException {
        RevisionMock revision = new RevisionMock(folder);
        revision.addFile(Paths.get("missing_javadoc/annotation", "Override.java"));
        revision.addFile(Paths.get("missing_javadoc/annotation", "Override_faultyTags.java"));
        revision.addFile(Paths.get("missing_javadoc/annotation", "Override_noDescription.java"));
        revision.addFile(Paths.get("missing_javadoc/annotation", "Override_noJavadoc.java"));
        revision.addFile(Paths.get("missing_javadoc/annotation", "Override_shortDescription.java"));

        Launcher launcher = new Launcher();
        launcher.getEnvironment().setAutoImports(true);
        launcher.addInputResource(folder.getRoot().getAbsolutePath());
        CtModel model = launcher.buildModel();

        RevisionRange revisionRange = mock(RevisionRange.class);
        when(revisionRange.getRevision()).thenReturn(revision);

        final var env = new Environment(model, revisionRange);

        JavadocDetector javadocDetector = new JavadocDetector(env);
        setAnnotationTags(javadocDetector);
        javadocDetector.withoutAccessModifier(new JavadocDetector.AccessModifier[]{
                JavadocDetector.AccessModifier.PUBLIC, JavadocDetector.AccessModifier.PRIVATE, JavadocDetector.AccessModifier.PROTECTED});
        javadocDetector.scan(model);
        assertEquals(1, javadocDetector.getCodeSmells().size());
        Multimap smellMap = mappedCodeSmell(javadocDetector.getCodeSmells());

        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.Override_faultyTags")));
        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.Override_noDescription")));
        assertEquals(1, smellMap.keys().count(Optional.of("missing_javadoc.Override_noJavadoc")));
        assertEquals(
                "AnnotationType Override_noJavadoc has no javadoc.",
                ((List<CodeSmell>) smellMap.get(Optional.of("missing_javadoc.Override_noJavadoc")))
                        .get(0).getSummary().get()
        );
        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.Override_shortDescription")));
        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.Override")));
    }

    @Test
    public void testAnnotationAMMixed() throws IOException {
        RevisionMock revision = new RevisionMock(folder);
        revision.addFile(Paths.get("missing_javadoc/annotation", "Override.java"));
        revision.addFile(Paths.get("missing_javadoc/annotation", "Override_faultyTags.java"));
        revision.addFile(Paths.get("missing_javadoc/annotation", "Override_noDescription.java"));
        revision.addFile(Paths.get("missing_javadoc/annotation", "Override_noJavadoc.java"));
        revision.addFile(Paths.get("missing_javadoc/annotation", "Override_shortDescription.java"));

        Launcher launcher = new Launcher();
        launcher.getEnvironment().setAutoImports(true);
        launcher.addInputResource(folder.getRoot().getAbsolutePath());
        CtModel model = launcher.buildModel();

        RevisionRange revisionRange = mock(RevisionRange.class);
        when(revisionRange.getRevision()).thenReturn(revision);

        final var env = new Environment(model, revisionRange);

        JavadocDetector javadocDetector = new JavadocDetector(env);
        setAnnotationTags(javadocDetector);
        javadocDetector.withoutAccessModifier(new JavadocDetector.AccessModifier[]{
                JavadocDetector.AccessModifier.NULL, JavadocDetector.AccessModifier.PUBLIC});
        javadocDetector.scan(model);
        assertEquals(6, javadocDetector.getCodeSmells().size());
        Multimap smellMap = mappedCodeSmell(javadocDetector.getCodeSmells());
        assertEquals(5, smellMap.keys().count(Optional.of("missing_javadoc.Override_faultyTags")));
        assertEquals(1, smellMap.keys().count(Optional.of("missing_javadoc.Override_noDescription")));

        assertEquals(5, smellMap.keys().count(Optional.of("missing_javadoc.Override_faultyTags")));
        List<CodeSmell> codeSmellsOFT = (List<CodeSmell>) smellMap.get(Optional.of("missing_javadoc.Override_faultyTags"));
        assertEquals("Tag @unknown is not allowed for this javadocable.", codeSmellsOFT.get(0).getSummary().get());
        assertEquals("Description of the @author tag is too short.", codeSmellsOFT.get(1).getSummary().get());
        assertEquals("Description of the @version tag is too short.", codeSmellsOFT.get(2).getSummary().get());
        assertEquals("Description of the @since tag is too short.", codeSmellsOFT.get(3).getSummary().get());
        assertEquals("Description of the @see tag is too short.", codeSmellsOFT.get(4).getSummary().get());

        assertEquals(1, smellMap.keys().count(Optional.of("missing_javadoc.Override_noDescription")));
        assertEquals(
                "No description existing in this javadoc.",
                ((List<CodeSmell>) smellMap.get(Optional.of("missing_javadoc.Override_noDescription")))
                        .get(0).getSummary().get()
        );

        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.Override_noJavadoc")));
        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.Override_shortDescription")));
        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.Override")));
    }

    @Test
    public void testAnnotationJavadocable() throws IOException {
        RevisionMock revision = new RevisionMock(folder);
        revision.addFile(Paths.get("missing_javadoc/annotation", "Override.java"));
        revision.addFile(Paths.get("missing_javadoc/annotation", "Override_faultyTags.java"));
        revision.addFile(Paths.get("missing_javadoc/annotation", "Override_noDescription.java"));
        revision.addFile(Paths.get("missing_javadoc/annotation", "Override_noJavadoc.java"));
        revision.addFile(Paths.get("missing_javadoc/annotation", "Override_shortDescription.java"));

        Launcher launcher = new Launcher();
        launcher.getEnvironment().setAutoImports(true);
        launcher.addInputResource(folder.getRoot().getAbsolutePath());
        CtModel model = launcher.buildModel();

        RevisionRange revisionRange = mock(RevisionRange.class);
        when(revisionRange.getRevision()).thenReturn(revision);

        final var env = new Environment(model, revisionRange);

        JavadocDetector javadocDetector = new JavadocDetector(env);
        setAnnotationTags(javadocDetector);
        javadocDetector.withoutAnnotationType();
        javadocDetector.withType(5, 15);
        javadocDetector.scan(model);
        assertEquals(0, javadocDetector.getCodeSmells().size());
        Multimap smellMap = mappedCodeSmell(javadocDetector.getCodeSmells());

        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.Override_faultyTags")));
        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.Override_noDescription")));
        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.Override_noJavadoc")));
        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.Override_shortDescription")));
        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.Override")));
    }

    private void setTypeTags(JavadocDetector javadocDetector) {
        javadocDetector.withAllAccessModifier();
        javadocDetector.withAuthor(2);
        javadocDetector.withVersion(1);
        javadocDetector.withParam(5);
        javadocDetector.withDeprecated(5);
        javadocDetector.withSee(3);
        javadocDetector.withSince(1);
        javadocDetector.withSerial(5);
    }

    @Test
    public void testEnum() throws IOException {
        RevisionMock revision = new RevisionMock(folder);
        revision.addFile(Paths.get("missing_javadoc", "EnumTest.java"));

        Launcher launcher = new Launcher();
        launcher.getEnvironment().setAutoImports(true);
        launcher.addInputResource(folder.getRoot().getAbsolutePath());
        CtModel model = launcher.buildModel();

        RevisionRange revisionRange = mock(RevisionRange.class);
        when(revisionRange.getRevision()).thenReturn(revision);

        final var env = new Environment(model, revisionRange);

        JavadocDetector javadocDetector = new JavadocDetector(env);
        javadocDetector.withEnum(5, 15);
        setTypeTags(javadocDetector);
        javadocDetector.scan(model);
        assertEquals(16, javadocDetector.getCodeSmells().size());
        Multimap smellMap = mappedCodeSmell(javadocDetector.getCodeSmells());

        assertEquals(4, smellMap.keys().count(Optional.of("missing_javadoc.EmptyDescriptionEnum")));
        List<CodeSmell> codeSmellsEDE = (List<CodeSmell>) smellMap.get(Optional.of(
                "missing_javadoc.EmptyDescriptionEnum"));
        assertEquals("Missing tag @author in the javadoc.", codeSmellsEDE.get(0).getSummary().get());
        assertEquals("Missing tag @version in the javadoc.", codeSmellsEDE.get(1).getSummary().get());
        assertEquals("Missing tag @deprecated in the javadoc.", codeSmellsEDE.get(2).getSummary().get());
        assertEquals("No description existing in this javadoc.", codeSmellsEDE.get(3).getSummary().get());

        assertEquals(8, smellMap.keys().count(Optional.of("missing_javadoc.FaultyTagsEnum")));
        List<CodeSmell> codeSmellsFTE = (List<CodeSmell>) smellMap.get(Optional.of("missing_javadoc.FaultyTagsEnum"));
        assertEquals("Tag @return is not allowed for this javadocable.", codeSmellsFTE.get(0).getSummary().get());
        assertEquals("Tag @param is not allowed for this javadocable.", codeSmellsFTE.get(1).getSummary().get());
        assertEquals("Description of the @author tag is too short.", codeSmellsFTE.get(2).getSummary().get());
        assertEquals("Description of the @version tag is too short.", codeSmellsFTE.get(3).getSummary().get());
        assertEquals("Description of the @see tag is too short.", codeSmellsFTE.get(4).getSummary().get());
        assertEquals("Description of the @deprecated tag is too short.", codeSmellsFTE.get(5).getSummary().get());
        assertEquals("Description of the @since tag is too short.", codeSmellsFTE.get(6).getSummary().get());
        assertEquals("Description of the @serial tag is too short.", codeSmellsFTE.get(7).getSummary().get());

        assertEquals(1, smellMap.keys().count(Optional.of("missing_javadoc.NoJavadocEnum")));
        assertEquals(
                "Enum NoJavadocEnum has no javadoc.",
                ((List<CodeSmell>) smellMap.get(Optional.of("missing_javadoc.NoJavadocEnum")))
                        .get(0).getSummary().get()
        );

        assertEquals(3, smellMap.keys().count(Optional.of("missing_javadoc.ShortDescriptionEnum")));
        List<CodeSmell> codeSmellsSDE = (List<CodeSmell>) smellMap.get(Optional.of(
                "missing_javadoc.ShortDescriptionEnum"));
        assertEquals(
                "Javadoc contains @deprecated, but the annotation @Deprecated at the javadocable is missing.",
                codeSmellsSDE.get(0).getSummary().get()
        );
        assertEquals("Short-description of this javadoc is too short.", codeSmellsSDE.get(1).getSummary().get());
        assertEquals("Long-description of this javadoc is too short.", codeSmellsSDE.get(2).getSummary().get());

        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.WeekEnum")));
    }

    @Test
    public void testEnumAMPublic() throws IOException {
        RevisionMock revision = new RevisionMock(folder);
        revision.addFile(Paths.get("missing_javadoc", "EnumTest.java"));

        Launcher launcher = new Launcher();
        launcher.getEnvironment().setAutoImports(true);
        launcher.addInputResource(folder.getRoot().getAbsolutePath());
        CtModel model = launcher.buildModel();

        RevisionRange revisionRange = mock(RevisionRange.class);
        when(revisionRange.getRevision()).thenReturn(revision);

        final var env = new Environment(model, revisionRange);

        JavadocDetector javadocDetector = new JavadocDetector(env);
        javadocDetector.withEnum(5, 15);
        setTypeTags(javadocDetector);
        javadocDetector.withoutAccessModifier(new JavadocDetector.AccessModifier[]{
                JavadocDetector.AccessModifier.NULL, JavadocDetector.AccessModifier.PRIVATE, JavadocDetector.AccessModifier.PROTECTED});
        javadocDetector.scan(model);
        assertEquals(1, javadocDetector.getCodeSmells().size());
        Multimap smellMap = mappedCodeSmell(javadocDetector.getCodeSmells());

        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.EmptyDescriptionEnum")));
        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.FaultyTagsEnum")));

        assertEquals(1, smellMap.keys().count(Optional.of("missing_javadoc.NoJavadocEnum")));
        assertEquals(
                "Enum NoJavadocEnum has no javadoc.",
                ((List<CodeSmell>) smellMap.get(Optional.of("missing_javadoc.NoJavadocEnum")))
                        .get(0).getSummary().get()
        );

        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.ShortDescriptionEnum")));
        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.WeekEnum")));
    }

    @Test
    public void testEnumAMPrivate() throws IOException {
        RevisionMock revision = new RevisionMock(folder);
        revision.addFile(Paths.get("missing_javadoc", "EnumTest.java"));

        Launcher launcher = new Launcher();
        launcher.getEnvironment().setAutoImports(true);
        launcher.addInputResource(folder.getRoot().getAbsolutePath());
        CtModel model = launcher.buildModel();

        RevisionRange revisionRange = mock(RevisionRange.class);
        when(revisionRange.getRevision()).thenReturn(revision);

        final var env = new Environment(model, revisionRange);

        JavadocDetector javadocDetector = new JavadocDetector(env);
        javadocDetector.withEnum(5, 15);
        setTypeTags(javadocDetector);
        javadocDetector.withoutAccessModifier(new JavadocDetector.AccessModifier[]{
                JavadocDetector.AccessModifier.NULL, JavadocDetector.AccessModifier.PUBLIC, JavadocDetector.AccessModifier.PROTECTED});
        javadocDetector.scan(model);
        assertEquals(4, javadocDetector.getCodeSmells().size());
        Multimap smellMap = mappedCodeSmell(javadocDetector.getCodeSmells());

        assertEquals(4, smellMap.keys().count(Optional.of("missing_javadoc.EmptyDescriptionEnum")));
        List<CodeSmell> codeSmellsEDE = (List<CodeSmell>) smellMap.get(Optional.of(
                "missing_javadoc.EmptyDescriptionEnum"));
        assertEquals("Missing tag @author in the javadoc.", codeSmellsEDE.get(0).getSummary().get());
        assertEquals("Missing tag @version in the javadoc.", codeSmellsEDE.get(1).getSummary().get());
        assertEquals("Missing tag @deprecated in the javadoc.", codeSmellsEDE.get(2).getSummary().get());
        assertEquals("No description existing in this javadoc.", codeSmellsEDE.get(3).getSummary().get());

        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.FaultyTagsEnum")));
        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.NoJavadocEnum")));
        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.ShortDescriptionEnum")));
        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.WeekEnum")));
    }

    @Test
    public void testEnumAMProtected() throws IOException {
        RevisionMock revision = new RevisionMock(folder);
        revision.addFile(Paths.get("missing_javadoc", "EnumTest.java"));

        Launcher launcher = new Launcher();
        launcher.getEnvironment().setAutoImports(true);
        launcher.addInputResource(folder.getRoot().getAbsolutePath());
        CtModel model = launcher.buildModel();

        RevisionRange revisionRange = mock(RevisionRange.class);
        when(revisionRange.getRevision()).thenReturn(revision);

        final var env = new Environment(model, revisionRange);

        JavadocDetector javadocDetector = new JavadocDetector(env);
        javadocDetector.withEnum(5, 15);
        setTypeTags(javadocDetector);
        javadocDetector.withoutAccessModifier(new JavadocDetector.AccessModifier[]{
                JavadocDetector.AccessModifier.NULL, JavadocDetector.AccessModifier.PRIVATE, JavadocDetector.AccessModifier.PUBLIC});
        javadocDetector.scan(model);
        assertEquals(3, javadocDetector.getCodeSmells().size());
        Multimap smellMap = mappedCodeSmell(javadocDetector.getCodeSmells());

        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.EmptyDescriptionEnum")));
        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.FaultyTagsEnum")));
        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.NoJavadocEnum")));

        assertEquals(3, smellMap.keys().count(Optional.of("missing_javadoc.ShortDescriptionEnum")));
        List<CodeSmell> codeSmellsSDE = (List<CodeSmell>) smellMap.get(Optional.of(
                "missing_javadoc.ShortDescriptionEnum"));
        assertEquals(
                "Javadoc contains @deprecated, but the annotation @Deprecated at the javadocable is missing.",
                codeSmellsSDE.get(0).getSummary().get()
        );
        assertEquals("Short-description of this javadoc is too short.", codeSmellsSDE.get(1).getSummary().get());
        assertEquals("Long-description of this javadoc is too short.", codeSmellsSDE.get(2).getSummary().get());

        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.WeekEnum")));
    }

    @Test
    public void testEnumAMNull() throws IOException {
        RevisionMock revision = new RevisionMock(folder);
        revision.addFile(Paths.get("missing_javadoc", "EnumTest.java"));

        Launcher launcher = new Launcher();
        launcher.getEnvironment().setAutoImports(true);
        launcher.addInputResource(folder.getRoot().getAbsolutePath());
        CtModel model = launcher.buildModel();

        RevisionRange revisionRange = mock(RevisionRange.class);
        when(revisionRange.getRevision()).thenReturn(revision);

        final var env = new Environment(model, revisionRange);

        JavadocDetector javadocDetector = new JavadocDetector(env);
        javadocDetector.withEnum(5, 15);
        setTypeTags(javadocDetector);
        javadocDetector.withoutAccessModifier(new JavadocDetector.AccessModifier[]{
                JavadocDetector.AccessModifier.PUBLIC, JavadocDetector.AccessModifier.PRIVATE, JavadocDetector.AccessModifier.PROTECTED});
        javadocDetector.scan(model);
        assertEquals(8, javadocDetector.getCodeSmells().size());
        Multimap smellMap = mappedCodeSmell(javadocDetector.getCodeSmells());

        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.EmptyDescriptionEnum")));

        assertEquals(8, smellMap.keys().count(Optional.of("missing_javadoc.FaultyTagsEnum")));
        List<CodeSmell> codeSmellsFTE = (List<CodeSmell>) smellMap.get(Optional.of("missing_javadoc.FaultyTagsEnum"));
        assertEquals("Tag @return is not allowed for this javadocable.", codeSmellsFTE.get(0).getSummary().get());
        assertEquals("Tag @param is not allowed for this javadocable.", codeSmellsFTE.get(1).getSummary().get());
        assertEquals("Description of the @author tag is too short.", codeSmellsFTE.get(2).getSummary().get());
        assertEquals("Description of the @version tag is too short.", codeSmellsFTE.get(3).getSummary().get());
        assertEquals("Description of the @see tag is too short.", codeSmellsFTE.get(4).getSummary().get());
        assertEquals("Description of the @deprecated tag is too short.", codeSmellsFTE.get(5).getSummary().get());
        assertEquals("Description of the @since tag is too short.", codeSmellsFTE.get(6).getSummary().get());
        assertEquals("Description of the @serial tag is too short.", codeSmellsFTE.get(7).getSummary().get());

        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.NoJavadocEnum")));
        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.ShortDescriptionEnum")));
        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.WeekEnum")));
    }

    @Test
    public void testEnumAMMixed() throws IOException {
        RevisionMock revision = new RevisionMock(folder);
        revision.addFile(Paths.get("missing_javadoc", "EnumTest.java"));

        Launcher launcher = new Launcher();
        launcher.getEnvironment().setAutoImports(true);
        launcher.addInputResource(folder.getRoot().getAbsolutePath());
        CtModel model = launcher.buildModel();

        RevisionRange revisionRange = mock(RevisionRange.class);
        when(revisionRange.getRevision()).thenReturn(revision);

        final var env = new Environment(model, revisionRange);

        JavadocDetector javadocDetector = new JavadocDetector(env);
        javadocDetector.withEnum(5, 15);
        setTypeTags(javadocDetector);
        javadocDetector.withoutAccessModifier(new JavadocDetector.AccessModifier[]{
                JavadocDetector.AccessModifier.NULL, JavadocDetector.AccessModifier.PUBLIC});
        javadocDetector.scan(model);
        assertEquals(7, javadocDetector.getCodeSmells().size());
        Multimap smellMap = mappedCodeSmell(javadocDetector.getCodeSmells());

        assertEquals(4, smellMap.keys().count(Optional.of("missing_javadoc.EmptyDescriptionEnum")));
        List<CodeSmell> codeSmellsEDE = (List<CodeSmell>) smellMap.get(Optional.of(
                "missing_javadoc.EmptyDescriptionEnum"));
        assertEquals("Missing tag @author in the javadoc.", codeSmellsEDE.get(0).getSummary().get());
        assertEquals("Missing tag @version in the javadoc.", codeSmellsEDE.get(1).getSummary().get());
        assertEquals("Missing tag @deprecated in the javadoc.", codeSmellsEDE.get(2).getSummary().get());
        assertEquals("No description existing in this javadoc.", codeSmellsEDE.get(3).getSummary().get());

        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.FaultyTagsEnum")));
        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.NoJavadocEnum")));

        assertEquals(3, smellMap.keys().count(Optional.of("missing_javadoc.ShortDescriptionEnum")));
        List<CodeSmell> codeSmellsSDE = (List<CodeSmell>) smellMap.get(Optional.of(
                "missing_javadoc.ShortDescriptionEnum"));
        assertEquals(
                "Javadoc contains @deprecated, but the annotation @Deprecated at the javadocable is missing.",
                codeSmellsSDE.get(0).getSummary().get()
        );
        assertEquals("Short-description of this javadoc is too short.", codeSmellsSDE.get(1).getSummary().get());
        assertEquals("Long-description of this javadoc is too short.", codeSmellsSDE.get(2).getSummary().get());

        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.WeekEnum")));
    }

    @Test
    public void testEnumJavadocable() throws IOException {
        RevisionMock revision = new RevisionMock(folder);
        revision.addFile(Paths.get("missing_javadoc", "EnumTest.java"));

        Launcher launcher = new Launcher();
        launcher.getEnvironment().setAutoImports(true);
        launcher.addInputResource(folder.getRoot().getAbsolutePath());
        CtModel model = launcher.buildModel();

        RevisionRange revisionRange = mock(RevisionRange.class);
        when(revisionRange.getRevision()).thenReturn(revision);

        final var env = new Environment(model, revisionRange);

        JavadocDetector javadocDetector = new JavadocDetector(env);
        setTypeTags(javadocDetector);
        javadocDetector.withClass(5, 15);
        javadocDetector.scan(model);
        assertEquals(0, javadocDetector.getCodeSmells().size());
        Multimap smellMap = mappedCodeSmell(javadocDetector.getCodeSmells());

        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.EmptyDescriptionEnum")));
        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.FaultyTagsEnum")));
        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.NoJavadocEnum")));
        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.ShortDescriptionEnum")));
        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.WeekEnum")));
    }

    @Test
    public void testClass() throws IOException {
        RevisionMock revision = new RevisionMock(folder);
        revision.addFile(Paths.get("missing_javadoc", "ClassTest.java"));

        Launcher launcher = new Launcher();
        launcher.getEnvironment().setAutoImports(true);
        launcher.addInputResource(folder.getRoot().getAbsolutePath());
        CtModel model = launcher.buildModel();

        RevisionRange revisionRange = mock(RevisionRange.class);
        when(revisionRange.getRevision()).thenReturn(revision);

        final var env = new Environment(model, revisionRange);

        JavadocDetector javadocDetector = new JavadocDetector(env);
        javadocDetector.withClass(5, 15);
        setTypeTags(javadocDetector);
        javadocDetector.scan(model);
        assertEquals(15, javadocDetector.getCodeSmells().size());

        Multimap smellMap = mappedCodeSmell(javadocDetector.getCodeSmells());

        assertEquals(8, smellMap.keys().count(Optional.of("missing_javadoc.FaultyTags")));
        List<CodeSmell> codeSmellsFT = (List<CodeSmell>) smellMap.get(Optional.of("missing_javadoc.FaultyTags"));
        assertEquals("Tag @return is not allowed for this javadocable.", codeSmellsFT.get(0).getSummary().get());
        assertEquals("Description of the @author tag is too short.", codeSmellsFT.get(1).getSummary().get());
        assertEquals("Description of the @version tag is too short.", codeSmellsFT.get(2).getSummary().get());
        assertEquals("Description of the @see tag is too short.", codeSmellsFT.get(3).getSummary().get());
        assertEquals("Description of the @deprecated tag is too short.", codeSmellsFT.get(4).getSummary().get());
        assertEquals("Description of the @since tag is too short.", codeSmellsFT.get(5).getSummary().get());
        assertEquals("Description of the @param <T> is too short.", codeSmellsFT.get(6).getSummary().get());
        assertEquals("Description of the @serial tag is too short.", codeSmellsFT.get(7).getSummary().get());

        assertEquals(1, smellMap.keys().count(Optional.of("missing_javadoc.NoJavadoc")));
        assertEquals(
                "Class NoJavadoc has no javadoc.",
                ((List<CodeSmell>) smellMap.get(Optional.of("missing_javadoc.NoJavadoc")))
                        .get(0).getSummary().get()
        );

        assertEquals(3, smellMap.keys().count(Optional.of("missing_javadoc.NoJavadoc$EmptyDescription")));
        List<CodeSmell> codeSmellsED = (List<CodeSmell>) smellMap.get(Optional.of(
                "missing_javadoc.NoJavadoc$EmptyDescription"));
        assertEquals("Missing tag @param <T> in the javadoc.", codeSmellsED.get(0).getSummary().get());
        assertEquals("Missing tag @deprecated in the javadoc.", codeSmellsED.get(1).getSummary().get());
        assertEquals("No description existing in this javadoc.", codeSmellsED.get(2).getSummary().get());

        assertEquals(3, smellMap.keys().count(Optional.of("missing_javadoc.ShortDescription")));
        List<CodeSmell> codeSmellsSD = (List<CodeSmell>) smellMap.get(Optional.of("missing_javadoc.ShortDescription"));
        assertEquals(
                "Javadoc contains @deprecated, but the annotation @Deprecated at the javadocable is missing.",
                codeSmellsSD.get(0).getSummary().get()
        );
        assertEquals("Short-description of this javadoc is too short.", codeSmellsSD.get(1).getSummary().get());
        assertEquals("Long-description of this javadoc is too short.", codeSmellsSD.get(2).getSummary().get());

        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.JavadocOkay")));
    }

    @Test
    public void testClassAMPublic() throws IOException {
        RevisionMock revision = new RevisionMock(folder);
        revision.addFile(Paths.get("missing_javadoc", "ClassTest.java"));

        Launcher launcher = new Launcher();
        launcher.getEnvironment().setAutoImports(true);
        launcher.addInputResource(folder.getRoot().getAbsolutePath());
        CtModel model = launcher.buildModel();

        RevisionRange revisionRange = mock(RevisionRange.class);
        when(revisionRange.getRevision()).thenReturn(revision);

        final var env = new Environment(model, revisionRange);

        JavadocDetector javadocDetector = new JavadocDetector(env);
        javadocDetector.withClass(5, 15);
        setTypeTags(javadocDetector);
        javadocDetector.withoutAccessModifier(new JavadocDetector.AccessModifier[]{
                JavadocDetector.AccessModifier.NULL, JavadocDetector.AccessModifier.PRIVATE, JavadocDetector.AccessModifier.PROTECTED});
        javadocDetector.scan(model);
        assertEquals(1, javadocDetector.getCodeSmells().size());
        Multimap smellMap = mappedCodeSmell(javadocDetector.getCodeSmells());

        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.FaultyTags")));

        assertEquals(1, smellMap.keys().count(Optional.of("missing_javadoc.NoJavadoc")));
        assertEquals(
                "Class NoJavadoc has no javadoc.",
                ((List<CodeSmell>) smellMap.get(Optional.of("missing_javadoc.NoJavadoc")))
                        .get(0).getSummary().get()
        );

        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.NoJavadoc$EmptyDescription")));
        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.ShortDescription")));
        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.JavadocOkay")));
    }

    @Test
    public void testClassAMPrivate() throws IOException {
        RevisionMock revision = new RevisionMock(folder);
        revision.addFile(Paths.get("missing_javadoc", "ClassTest.java"));

        Launcher launcher = new Launcher();
        launcher.getEnvironment().setAutoImports(true);
        launcher.addInputResource(folder.getRoot().getAbsolutePath());
        CtModel model = launcher.buildModel();

        RevisionRange revisionRange = mock(RevisionRange.class);
        when(revisionRange.getRevision()).thenReturn(revision);

        final var env = new Environment(model, revisionRange);

        JavadocDetector javadocDetector = new JavadocDetector(env);
        javadocDetector.withClass(5, 15);
        setTypeTags(javadocDetector);
        javadocDetector.withoutAccessModifier(new JavadocDetector.AccessModifier[]{
                JavadocDetector.AccessModifier.NULL, JavadocDetector.AccessModifier.PUBLIC, JavadocDetector.AccessModifier.PROTECTED});
        javadocDetector.scan(model);
        assertEquals(3, javadocDetector.getCodeSmells().size());
        Multimap smellMap = mappedCodeSmell(javadocDetector.getCodeSmells());

        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.FaultyTags")));
        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.NoJavadoc")));

        assertEquals(3, smellMap.keys().count(Optional.of("missing_javadoc.NoJavadoc$EmptyDescription")));
        List<CodeSmell> codeSmellsED = (List<CodeSmell>) smellMap.get(Optional.of(
                "missing_javadoc.NoJavadoc$EmptyDescription"));
        assertEquals("Missing tag @param <T> in the javadoc.", codeSmellsED.get(0).getSummary().get());
        assertEquals("Missing tag @deprecated in the javadoc.", codeSmellsED.get(1).getSummary().get());
        assertEquals("No description existing in this javadoc.", codeSmellsED.get(2).getSummary().get());

        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.ShortDescription")));
        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.JavadocOkay")));
    }

    @Test
    public void testClassAMProtected() throws IOException {
        RevisionMock revision = new RevisionMock(folder);
        revision.addFile(Paths.get("missing_javadoc", "ClassTest.java"));

        Launcher launcher = new Launcher();
        launcher.getEnvironment().setAutoImports(true);
        launcher.addInputResource(folder.getRoot().getAbsolutePath());
        CtModel model = launcher.buildModel();

        RevisionRange revisionRange = mock(RevisionRange.class);
        when(revisionRange.getRevision()).thenReturn(revision);

        final var env = new Environment(model, revisionRange);

        JavadocDetector javadocDetector = new JavadocDetector(env);
        javadocDetector.withClass(5, 15);
        setTypeTags(javadocDetector);
        javadocDetector.withoutAccessModifier(new JavadocDetector.AccessModifier[]{
                JavadocDetector.AccessModifier.NULL, JavadocDetector.AccessModifier.PRIVATE, JavadocDetector.AccessModifier.PUBLIC});
        javadocDetector.scan(model);
        assertEquals(3, javadocDetector.getCodeSmells().size());
        Multimap smellMap = mappedCodeSmell(javadocDetector.getCodeSmells());

        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.FaultyTags")));
        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.NoJavadoc")));
        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.NoJavadoc$EmptyDescription")));

        assertEquals(3, smellMap.keys().count(Optional.of("missing_javadoc.ShortDescription")));
        List<CodeSmell> codeSmellsSD = (List<CodeSmell>) smellMap.get(Optional.of("missing_javadoc.ShortDescription"));
        assertEquals(
                "Javadoc contains @deprecated, but the annotation @Deprecated at the javadocable is missing.",
                codeSmellsSD.get(0).getSummary().get()
        );
        assertEquals("Short-description of this javadoc is too short.", codeSmellsSD.get(1).getSummary().get());
        assertEquals("Long-description of this javadoc is too short.", codeSmellsSD.get(2).getSummary().get());

        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.JavadocOkay")));
    }

    @Test
    public void testClassAMNull() throws IOException {
        RevisionMock revision = new RevisionMock(folder);
        revision.addFile(Paths.get("missing_javadoc", "ClassTest.java"));

        Launcher launcher = new Launcher();
        launcher.getEnvironment().setAutoImports(true);
        launcher.addInputResource(folder.getRoot().getAbsolutePath());
        CtModel model = launcher.buildModel();

        RevisionRange revisionRange = mock(RevisionRange.class);
        when(revisionRange.getRevision()).thenReturn(revision);

        final var env = new Environment(model, revisionRange);

        JavadocDetector javadocDetector = new JavadocDetector(env);
        javadocDetector.withClass(5, 15);
        setTypeTags(javadocDetector);
        javadocDetector.withoutAccessModifier(new JavadocDetector.AccessModifier[]{
                JavadocDetector.AccessModifier.PUBLIC, JavadocDetector.AccessModifier.PRIVATE, JavadocDetector.AccessModifier.PROTECTED});
        javadocDetector.scan(model);
        assertEquals(8, javadocDetector.getCodeSmells().size());
        Multimap smellMap = mappedCodeSmell(javadocDetector.getCodeSmells());
        assertEquals(8, smellMap.keys().count(Optional.of("missing_javadoc.FaultyTags")));

        assertEquals(8, smellMap.keys().count(Optional.of("missing_javadoc.FaultyTags")));
        List<CodeSmell> codeSmellsFT = (List<CodeSmell>) smellMap.get(Optional.of("missing_javadoc.FaultyTags"));
        assertEquals("Tag @return is not allowed for this javadocable.", codeSmellsFT.get(0).getSummary().get());
        assertEquals("Description of the @author tag is too short.", codeSmellsFT.get(1).getSummary().get());
        assertEquals("Description of the @version tag is too short.", codeSmellsFT.get(2).getSummary().get());
        assertEquals("Description of the @see tag is too short.", codeSmellsFT.get(3).getSummary().get());
        assertEquals("Description of the @deprecated tag is too short.", codeSmellsFT.get(4).getSummary().get());
        assertEquals("Description of the @since tag is too short.", codeSmellsFT.get(5).getSummary().get());
        assertEquals("Description of the @param <T> is too short.", codeSmellsFT.get(6).getSummary().get());
        assertEquals("Description of the @serial tag is too short.", codeSmellsFT.get(7).getSummary().get());

        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.NoJavadoc")));
        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.NoJavadoc$EmptyDescription")));
        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.ShortDescription")));
        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.JavadocOkay")));
    }

    @Test
    public void testClassAMMixed() throws IOException {
        RevisionMock revision = new RevisionMock(folder);
        revision.addFile(Paths.get("missing_javadoc", "ClassTest.java"));

        Launcher launcher = new Launcher();
        launcher.getEnvironment().setAutoImports(true);
        launcher.addInputResource(folder.getRoot().getAbsolutePath());
        CtModel model = launcher.buildModel();

        RevisionRange revisionRange = mock(RevisionRange.class);
        when(revisionRange.getRevision()).thenReturn(revision);

        final var env = new Environment(model, revisionRange);

        JavadocDetector javadocDetector = new JavadocDetector(env);
        javadocDetector.withClass(5, 15);
        setTypeTags(javadocDetector);
        javadocDetector.withoutAccessModifier(new JavadocDetector.AccessModifier[]{
                JavadocDetector.AccessModifier.NULL, JavadocDetector.AccessModifier.PUBLIC});
        javadocDetector.scan(model);
        assertEquals(6, javadocDetector.getCodeSmells().size());
        Multimap smellMap = mappedCodeSmell(javadocDetector.getCodeSmells());

        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.FaultyTags")));
        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.NoJavadoc")));

        assertEquals(3, smellMap.keys().count(Optional.of("missing_javadoc.NoJavadoc$EmptyDescription")));
        List<CodeSmell> codeSmellsED = (List<CodeSmell>) smellMap.get(Optional.of(
                "missing_javadoc.NoJavadoc$EmptyDescription"));
        assertEquals("Missing tag @param <T> in the javadoc.", codeSmellsED.get(0).getSummary().get());
        assertEquals("Missing tag @deprecated in the javadoc.", codeSmellsED.get(1).getSummary().get());
        assertEquals("No description existing in this javadoc.", codeSmellsED.get(2).getSummary().get());

        assertEquals(3, smellMap.keys().count(Optional.of("missing_javadoc.ShortDescription")));
        List<CodeSmell> codeSmellsSD = (List<CodeSmell>) smellMap.get(Optional.of("missing_javadoc.ShortDescription"));
        assertEquals(
                "Javadoc contains @deprecated, but the annotation @Deprecated at the javadocable is missing.",
                codeSmellsSD.get(0).getSummary().get()
        );
        assertEquals("Short-description of this javadoc is too short.", codeSmellsSD.get(1).getSummary().get());
        assertEquals("Long-description of this javadoc is too short.", codeSmellsSD.get(2).getSummary().get());

        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.JavadocOkay")));
    }

    @Test
    public void testClassJavadocable() throws IOException {
        RevisionMock revision = new RevisionMock(folder);
        revision.addFile(Paths.get("missing_javadoc", "ClassTest.java"));

        Launcher launcher = new Launcher();
        launcher.getEnvironment().setAutoImports(true);
        launcher.addInputResource(folder.getRoot().getAbsolutePath());
        CtModel model = launcher.buildModel();

        RevisionRange revisionRange = mock(RevisionRange.class);
        when(revisionRange.getRevision()).thenReturn(revision);

        final var env = new Environment(model, revisionRange);

        JavadocDetector javadocDetector = new JavadocDetector(env);
        setTypeTags(javadocDetector);
        javadocDetector.withMethod(5, 15);
        javadocDetector.scan(model);
        assertEquals(4, javadocDetector.getCodeSmells().size());
        Multimap smellMap = mappedCodeSmell(javadocDetector.getCodeSmells());
        assertEquals(1, smellMap.keys().count(Optional.of("missing_javadoc.FaultyTags#print()")));
        assertEquals(
                "Method print() has no javadoc.",
                ((List<CodeSmell>) smellMap.get(Optional.of("missing_javadoc.FaultyTags#print()")))
                        .get(0).getSummary().get()
        );
        assertEquals(1, smellMap.keys().count(Optional.of("missing_javadoc.JavadocOkay#print()")));
        assertEquals(
                "Method print() has no javadoc.",
                ((List<CodeSmell>) smellMap.get(Optional.of("missing_javadoc.JavadocOkay#print()")))
                        .get(0).getSummary().get()
        );
        assertEquals(1, smellMap.keys().count(Optional.of("missing_javadoc.NoJavadoc$EmptyDescription#print()")));
        assertEquals(
                "Method print() has no javadoc.",
                ((List<CodeSmell>) smellMap.get(Optional.of("missing_javadoc.NoJavadoc$EmptyDescription#print()")))
                        .get(0).getSummary().get()
        );
        assertEquals(1, smellMap.keys().count(Optional.of("missing_javadoc.ShortDescription#print()")));
        assertEquals(
                "Method print() has no javadoc.",
                ((List<CodeSmell>) smellMap.get(Optional.of("missing_javadoc.ShortDescription#print()")))
                        .get(0).getSummary().get()
        );

        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.FaultyTags")));
        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.NoJavadoc")));
        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.NoJavadoc$EmptyDescription")));
        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.ShortDescription")));
        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.JavadocOkay")));
    }

    @Test
    public void testInterface() throws IOException {
        RevisionMock revision = new RevisionMock(folder);
        revision.addFile(Paths.get("missing_javadoc/interface", "InterfaceTest.java"));
        revision.addFile(Paths.get("missing_javadoc/interface", "InterfaceTest_noJavadoc.java"));
        revision.addFile(Paths.get("missing_javadoc/interface", "InterfaceTest_emptyDescription.java"));
        revision.addFile(Paths.get("missing_javadoc/interface", "InterfaceTest_shortDescription.java"));
        revision.addFile(Paths.get("missing_javadoc/interface", "InterfaceTest_faultyTags.java"));


        Launcher launcher = new Launcher();
        launcher.getEnvironment().setAutoImports(true);
        launcher.addInputResource(folder.getRoot().getAbsolutePath());
        CtModel model = launcher.buildModel();

        RevisionRange revisionRange = mock(RevisionRange.class);
        when(revisionRange.getRevision()).thenReturn(revision);

        final var env = new Environment(model, revisionRange);

        JavadocDetector javadocDetector = new JavadocDetector(env);
        javadocDetector.withInterface(5, 15);
        setTypeTags(javadocDetector);
        javadocDetector.scan(model);
        assertEquals(17, javadocDetector.getCodeSmells().size());
        Multimap smellMap = mappedCodeSmell(javadocDetector.getCodeSmells());

        assertEquals(5, smellMap.keys().count(Optional.of("missing_javadoc.EmptyJavadocInterface")));
        List<CodeSmell> codeSmellsEJI = (List<CodeSmell>) smellMap.get(Optional.of(
                "missing_javadoc.EmptyJavadocInterface"));
        assertEquals("Missing tag @author in the javadoc.", codeSmellsEJI.get(0).getSummary().get());
        assertEquals("Missing tag @version in the javadoc.", codeSmellsEJI.get(1).getSummary().get());
        assertEquals("Missing tag @param <T> in the javadoc.", codeSmellsEJI.get(2).getSummary().get());
        assertEquals("Missing tag @deprecated in the javadoc.", codeSmellsEJI.get(3).getSummary().get());
        assertEquals("No description existing in this javadoc.", codeSmellsEJI.get(4).getSummary().get());

        assertEquals(8, smellMap.keys().count(Optional.of("missing_javadoc.FaultyTagsInterface")));
        List<CodeSmell> codeSmellsFTI = (List<CodeSmell>) smellMap.get(Optional.of("missing_javadoc.FaultyTagsInterface"));
        assertEquals("Tag @return is not allowed for this javadocable.", codeSmellsFTI.get(0).getSummary().get());
        assertEquals("Description of the @author tag is too short.", codeSmellsFTI.get(1).getSummary().get());
        assertEquals("Description of the @version tag is too short.", codeSmellsFTI.get(2).getSummary().get());
        assertEquals("Description of the @see tag is too short.", codeSmellsFTI.get(3).getSummary().get());
        assertEquals("Description of the @deprecated tag is too short.", codeSmellsFTI.get(4).getSummary().get());
        assertEquals("Description of the @since tag is too short.", codeSmellsFTI.get(5).getSummary().get());
        assertEquals("Description of the @param <T> is too short.", codeSmellsFTI.get(6).getSummary().get());
        assertEquals("Description of the @serial tag is too short.", codeSmellsFTI.get(7).getSummary().get());

        assertEquals(1, smellMap.keys().count(Optional.of("missing_javadoc.NoJavadocInterface")));
        assertEquals(
                "Interface NoJavadocInterface has no javadoc.",
                ((List<CodeSmell>) smellMap.get(Optional.of("missing_javadoc.NoJavadocInterface")))
                        .get(0).getSummary().get()
        );

        assertEquals(3, smellMap.keys().count(Optional.of("missing_javadoc.ShortDescriptionInterface")));
        List<CodeSmell> codeSmellsSDI = (List<CodeSmell>) smellMap.get(Optional.of(
                "missing_javadoc.ShortDescriptionInterface"));
        assertEquals(
                "Javadoc contains @deprecated, but the annotation @Deprecated at the javadocable is missing.",
                codeSmellsSDI.get(0).getSummary().get()
        );
        assertEquals("Short-description of this javadoc is too short.", codeSmellsSDI.get(1).getSummary().get());
        assertEquals("Long-description of this javadoc is too short.", codeSmellsSDI.get(2).getSummary().get());

        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.JavadocOkayInterface")));
    }

    @Test
    public void testInterfaceAMPublic() throws IOException {
        RevisionMock revision = new RevisionMock(folder);
        revision.addFile(Paths.get("missing_javadoc/interface", "InterfaceTest.java"));
        revision.addFile(Paths.get("missing_javadoc/interface", "InterfaceTest_noJavadoc.java"));
        revision.addFile(Paths.get("missing_javadoc/interface", "InterfaceTest_emptyDescription.java"));
        revision.addFile(Paths.get("missing_javadoc/interface", "InterfaceTest_shortDescription.java"));
        revision.addFile(Paths.get("missing_javadoc/interface", "InterfaceTest_faultyTags.java"));

        Launcher launcher = new Launcher();
        launcher.getEnvironment().setAutoImports(true);
        launcher.addInputResource(folder.getRoot().getAbsolutePath());
        CtModel model = launcher.buildModel();

        RevisionRange revisionRange = mock(RevisionRange.class);
        when(revisionRange.getRevision()).thenReturn(revision);

        final var env = new Environment(model, revisionRange);

        JavadocDetector javadocDetector = new JavadocDetector(env);
        javadocDetector.withInterface(5, 15);
        setTypeTags(javadocDetector);
        javadocDetector.withoutAccessModifier(new JavadocDetector.AccessModifier[]{
                JavadocDetector.AccessModifier.NULL, JavadocDetector.AccessModifier.PRIVATE, JavadocDetector.AccessModifier.PROTECTED});
        javadocDetector.scan(model);
        assertEquals(1, javadocDetector.getCodeSmells().size());
        Multimap smellMap = mappedCodeSmell(javadocDetector.getCodeSmells());

        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.EmptyJavadocInterface")));
        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.FaultyTagsInterface")));

        assertEquals(1, smellMap.keys().count(Optional.of("missing_javadoc.NoJavadocInterface")));
        assertEquals(
                "Interface NoJavadocInterface has no javadoc.",
                ((List<CodeSmell>) smellMap.get(Optional.of("missing_javadoc.NoJavadocInterface")))
                        .get(0).getSummary().get()
        );

        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.ShortDescriptionInterface")));
        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.JavadocOkayInterface")));
    }

    @Test
    public void testInterfaceAMPrivate() throws IOException {
        RevisionMock revision = new RevisionMock(folder);
        revision.addFile(Paths.get("missing_javadoc/interface", "InterfaceTest.java"));
        revision.addFile(Paths.get("missing_javadoc/interface", "InterfaceTest_noJavadoc.java"));
        revision.addFile(Paths.get("missing_javadoc/interface", "InterfaceTest_emptyDescription.java"));
        revision.addFile(Paths.get("missing_javadoc/interface", "InterfaceTest_shortDescription.java"));
        revision.addFile(Paths.get("missing_javadoc/interface", "InterfaceTest_faultyTags.java"));

        Launcher launcher = new Launcher();
        launcher.getEnvironment().setAutoImports(true);
        launcher.addInputResource(folder.getRoot().getAbsolutePath());
        CtModel model = launcher.buildModel();

        RevisionRange revisionRange = mock(RevisionRange.class);
        when(revisionRange.getRevision()).thenReturn(revision);

        final var env = new Environment(model, revisionRange);

        JavadocDetector javadocDetector = new JavadocDetector(env);
        javadocDetector.withInterface(5, 15);
        setTypeTags(javadocDetector);
        javadocDetector.withoutAccessModifier(new JavadocDetector.AccessModifier[]{
                JavadocDetector.AccessModifier.NULL, JavadocDetector.AccessModifier.PUBLIC, JavadocDetector.AccessModifier.PROTECTED});
        javadocDetector.scan(model);
        assertEquals(5, javadocDetector.getCodeSmells().size());
        Multimap smellMap = mappedCodeSmell(javadocDetector.getCodeSmells());

        assertEquals(5, smellMap.keys().count(Optional.of("missing_javadoc.EmptyJavadocInterface")));
        List<CodeSmell> codeSmellsEJI = (List<CodeSmell>) smellMap.get(Optional.of(
                "missing_javadoc.EmptyJavadocInterface"));
        assertEquals("Missing tag @author in the javadoc.", codeSmellsEJI.get(0).getSummary().get());
        assertEquals("Missing tag @version in the javadoc.", codeSmellsEJI.get(1).getSummary().get());
        assertEquals("Missing tag @param <T> in the javadoc.", codeSmellsEJI.get(2).getSummary().get());
        assertEquals("Missing tag @deprecated in the javadoc.", codeSmellsEJI.get(3).getSummary().get());
        assertEquals("No description existing in this javadoc.", codeSmellsEJI.get(4).getSummary().get());

        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.FaultyTagsInterface")));
        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.NoJavadocInterface")));
        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.ShortDescriptionInterface")));
        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.JavadocOkayInterface")));
    }

    @Test
    public void testInterfaceAMProtected() throws IOException {
        RevisionMock revision = new RevisionMock(folder);
        revision.addFile(Paths.get("missing_javadoc/interface", "InterfaceTest.java"));
        revision.addFile(Paths.get("missing_javadoc/interface", "InterfaceTest_noJavadoc.java"));
        revision.addFile(Paths.get("missing_javadoc/interface", "InterfaceTest_emptyDescription.java"));
        revision.addFile(Paths.get("missing_javadoc/interface", "InterfaceTest_shortDescription.java"));
        revision.addFile(Paths.get("missing_javadoc/interface", "InterfaceTest_faultyTags.java"));

        Launcher launcher = new Launcher();
        launcher.getEnvironment().setAutoImports(true);
        launcher.addInputResource(folder.getRoot().getAbsolutePath());
        CtModel model = launcher.buildModel();

        RevisionRange revisionRange = mock(RevisionRange.class);
        when(revisionRange.getRevision()).thenReturn(revision);

        final var env = new Environment(model, revisionRange);

        JavadocDetector javadocDetector = new JavadocDetector(env);
        javadocDetector.withInterface(5, 15);
        setTypeTags(javadocDetector);
        javadocDetector.withoutAccessModifier(new JavadocDetector.AccessModifier[]{
                JavadocDetector.AccessModifier.NULL, JavadocDetector.AccessModifier.PRIVATE, JavadocDetector.AccessModifier.PUBLIC});
        javadocDetector.scan(model);
        assertEquals(3, javadocDetector.getCodeSmells().size());
        Multimap smellMap = mappedCodeSmell(javadocDetector.getCodeSmells());

        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.EmptyJavadocInterface")));
        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.FaultyTagsInterface")));
        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.NoJavadocInterface")));

        assertEquals(3, smellMap.keys().count(Optional.of("missing_javadoc.ShortDescriptionInterface")));
        List<CodeSmell> codeSmellsSDI = (List<CodeSmell>) smellMap.get(Optional.of(
                "missing_javadoc.ShortDescriptionInterface"));
        assertEquals(
                "Javadoc contains @deprecated, but the annotation @Deprecated at the javadocable is missing.",
                codeSmellsSDI.get(0).getSummary().get()
        );
        assertEquals("Short-description of this javadoc is too short.", codeSmellsSDI.get(1).getSummary().get());
        assertEquals("Long-description of this javadoc is too short.", codeSmellsSDI.get(2).getSummary().get());

        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.JavadocOkayInterface")));
    }

    @Test
    public void testInterfaceAMNull() throws IOException {
        RevisionMock revision = new RevisionMock(folder);
        revision.addFile(Paths.get("missing_javadoc/interface", "InterfaceTest.java"));
        revision.addFile(Paths.get("missing_javadoc/interface", "InterfaceTest_noJavadoc.java"));
        revision.addFile(Paths.get("missing_javadoc/interface", "InterfaceTest_emptyDescription.java"));
        revision.addFile(Paths.get("missing_javadoc/interface", "InterfaceTest_shortDescription.java"));
        revision.addFile(Paths.get("missing_javadoc/interface", "InterfaceTest_faultyTags.java"));

        Launcher launcher = new Launcher();
        launcher.getEnvironment().setAutoImports(true);
        launcher.addInputResource(folder.getRoot().getAbsolutePath());
        CtModel model = launcher.buildModel();

        RevisionRange revisionRange = mock(RevisionRange.class);
        when(revisionRange.getRevision()).thenReturn(revision);

        final var env = new Environment(model, revisionRange);

        JavadocDetector javadocDetector = new JavadocDetector(env);
        javadocDetector.withInterface(5, 15);
        setTypeTags(javadocDetector);
        javadocDetector.withoutAccessModifier(new JavadocDetector.AccessModifier[]{
                JavadocDetector.AccessModifier.PUBLIC, JavadocDetector.AccessModifier.PRIVATE, JavadocDetector.AccessModifier.PROTECTED});
        javadocDetector.scan(model);
        assertEquals(8, javadocDetector.getCodeSmells().size());
        Multimap smellMap = mappedCodeSmell(javadocDetector.getCodeSmells());

        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.EmptyJavadocInterface")));

        assertEquals(8, smellMap.keys().count(Optional.of("missing_javadoc.FaultyTagsInterface")));
        List<CodeSmell> codeSmellsFTI = (List<CodeSmell>) smellMap.get(Optional.of("missing_javadoc.FaultyTagsInterface"));
        assertEquals("Tag @return is not allowed for this javadocable.", codeSmellsFTI.get(0).getSummary().get());
        assertEquals("Description of the @author tag is too short.", codeSmellsFTI.get(1).getSummary().get());
        assertEquals("Description of the @version tag is too short.", codeSmellsFTI.get(2).getSummary().get());
        assertEquals("Description of the @see tag is too short.", codeSmellsFTI.get(3).getSummary().get());
        assertEquals("Description of the @deprecated tag is too short.", codeSmellsFTI.get(4).getSummary().get());
        assertEquals("Description of the @since tag is too short.", codeSmellsFTI.get(5).getSummary().get());
        assertEquals("Description of the @param <T> is too short.", codeSmellsFTI.get(6).getSummary().get());
        assertEquals("Description of the @serial tag is too short.", codeSmellsFTI.get(7).getSummary().get());

        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.NoJavadocInterface")));
        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.ShortDescriptionInterface")));
        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.JavadocOkayInterface")));
    }

    @Test
    public void testInterfaceAMMixed() throws IOException {
        RevisionMock revision = new RevisionMock(folder);
        revision.addFile(Paths.get("missing_javadoc/interface", "InterfaceTest.java"));
        revision.addFile(Paths.get("missing_javadoc/interface", "InterfaceTest_noJavadoc.java"));
        revision.addFile(Paths.get("missing_javadoc/interface", "InterfaceTest_emptyDescription.java"));
        revision.addFile(Paths.get("missing_javadoc/interface", "InterfaceTest_shortDescription.java"));
        revision.addFile(Paths.get("missing_javadoc/interface", "InterfaceTest_faultyTags.java"));

        Launcher launcher = new Launcher();
        launcher.getEnvironment().setAutoImports(true);
        launcher.addInputResource(folder.getRoot().getAbsolutePath());
        CtModel model = launcher.buildModel();

        RevisionRange revisionRange = mock(RevisionRange.class);
        when(revisionRange.getRevision()).thenReturn(revision);

        final var env = new Environment(model, revisionRange);

        JavadocDetector javadocDetector = new JavadocDetector(env);
        javadocDetector.withInterface(5, 15);
        setTypeTags(javadocDetector);
        javadocDetector.withoutAccessModifier(new JavadocDetector.AccessModifier[]{
                JavadocDetector.AccessModifier.NULL, JavadocDetector.AccessModifier.PUBLIC});
        javadocDetector.scan(model);
        assertEquals(8, javadocDetector.getCodeSmells().size());
        Multimap smellMap = mappedCodeSmell(javadocDetector.getCodeSmells());

        assertEquals(5, smellMap.keys().count(Optional.of("missing_javadoc.EmptyJavadocInterface")));
        List<CodeSmell> codeSmellsEJI = (List<CodeSmell>) smellMap.get(Optional.of(
                "missing_javadoc.EmptyJavadocInterface"));
        assertEquals("Missing tag @author in the javadoc.", codeSmellsEJI.get(0).getSummary().get());
        assertEquals("Missing tag @version in the javadoc.", codeSmellsEJI.get(1).getSummary().get());
        assertEquals("Missing tag @param <T> in the javadoc.", codeSmellsEJI.get(2).getSummary().get());
        assertEquals("Missing tag @deprecated in the javadoc.", codeSmellsEJI.get(3).getSummary().get());
        assertEquals("No description existing in this javadoc.", codeSmellsEJI.get(4).getSummary().get());

        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.FaultyTagsInterface")));
        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.NoJavadocInterface")));

        assertEquals(3, smellMap.keys().count(Optional.of("missing_javadoc.ShortDescriptionInterface")));
        List<CodeSmell> codeSmellsSDI = (List<CodeSmell>) smellMap.get(Optional.of(
                "missing_javadoc.ShortDescriptionInterface"));
        assertEquals(
                "Javadoc contains @deprecated, but the annotation @Deprecated at the javadocable is missing.",
                codeSmellsSDI.get(0).getSummary().get()
        );
        assertEquals("Short-description of this javadoc is too short.", codeSmellsSDI.get(1).getSummary().get());
        assertEquals("Long-description of this javadoc is too short.", codeSmellsSDI.get(2).getSummary().get());

        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.JavadocOkayInterface")));
    }

    @Test
    public void testInterfaceJavadocable() throws IOException {
        RevisionMock revision = new RevisionMock(folder);
        revision.addFile(Paths.get("missing_javadoc/interface", "InterfaceTest.java"));
        revision.addFile(Paths.get("missing_javadoc/interface", "InterfaceTest_noJavadoc.java"));
        revision.addFile(Paths.get("missing_javadoc/interface", "InterfaceTest_emptyDescription.java"));
        revision.addFile(Paths.get("missing_javadoc/interface", "InterfaceTest_shortDescription.java"));
        revision.addFile(Paths.get("missing_javadoc/interface", "InterfaceTest_faultyTags.java"));

        Launcher launcher = new Launcher();
        launcher.getEnvironment().setAutoImports(true);
        launcher.addInputResource(folder.getRoot().getAbsolutePath());
        CtModel model = launcher.buildModel();

        RevisionRange revisionRange = mock(RevisionRange.class);
        when(revisionRange.getRevision()).thenReturn(revision);

        final var env = new Environment(model, revisionRange);

        JavadocDetector javadocDetector = new JavadocDetector(env);
        setTypeTags(javadocDetector);
        javadocDetector.withMethod(5, 15);
        javadocDetector.scan(model);
        assertEquals(5, javadocDetector.getCodeSmells().size());
        Multimap smellMap = mappedCodeSmell(javadocDetector.getCodeSmells());

        assertEquals(
                1,
                smellMap.keys().count(Optional.of("missing_javadoc.EmptyJavadocInterface#counter(java.lang.String)"))
        );
        assertEquals(
                "Method counter(java.lang.String) has no javadoc.",
                ((List<CodeSmell>) smellMap.get(Optional.of(
                        "missing_javadoc.EmptyJavadocInterface#counter(java.lang.String)")))
                        .get(0).getSummary().get()
        );

        assertEquals(
                1,
                smellMap.keys().count(Optional.of("missing_javadoc.FaultyTagsInterface#counter(java.lang.String)"))
        );
        assertEquals(
                "Method counter(java.lang.String) has no javadoc.",
                ((List<CodeSmell>) smellMap.get(Optional.of(
                        "missing_javadoc.FaultyTagsInterface#counter(java.lang.String)")))
                        .get(0).getSummary().get()
        );

        assertEquals(
                1,
                smellMap.keys().count(Optional.of("missing_javadoc.JavadocOkayInterface#counter(java.lang.String)"))
        );
        assertEquals(
                "Method counter(java.lang.String) has no javadoc.",
                ((List<CodeSmell>) smellMap.get(Optional.of(
                        "missing_javadoc.JavadocOkayInterface#counter(java.lang.String)")))
                        .get(0).getSummary().get()
        );

        assertEquals(
                1,
                smellMap.keys().count(Optional.of("missing_javadoc.NoJavadocInterface#counter(java.lang.String)"))
        );
        assertEquals(
                "Method counter(java.lang.String) has no javadoc.",
                ((List<CodeSmell>) smellMap.get(Optional.of(
                        "missing_javadoc.NoJavadocInterface#counter(java.lang.String)")))
                        .get(0).getSummary().get()
        );

        assertEquals(
                1,
                smellMap.keys()
                        .count(Optional.of("missing_javadoc.ShortDescriptionInterface#counter(java.lang.String)"))
        );
        assertEquals(
                "Method counter(java.lang.String) has no javadoc.",
                ((List<CodeSmell>) smellMap.get(Optional.of(
                        "missing_javadoc.ShortDescriptionInterface#counter(java.lang.String)")))
                        .get(0).getSummary().get()
        );

        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.EmptyJavadocInterface")));
        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.FaultyTagsInterface")));
        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.NoJavadocInterface")));
        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.ShortDescriptionInterface")));
        assertEquals(0, smellMap.keys().count(Optional.of("missing_javadoc.JavadocOkayInterface")));
    }

    @Test
    public void testReadByChar() throws IOException {
        RevisionMock revision = new RevisionMock(folder);
        revision.addFile(Paths.get("missing_javadoc", "ReadByCharTest.java"));
        revision.addFile(Paths.get("missing_javadoc/annotation", "Override_shortDescription.java"));
        revision.addFile(Paths.get("missing_javadoc/interface", "InterfaceTest_shortDescription.java"));
        revision.addFile(Paths.get("missing_javadoc/package/short_description", "package-info.java"));

        Launcher launcher = new Launcher();
        launcher.getEnvironment().setAutoImports(true);
        launcher.addInputResource(folder.getRoot().getAbsolutePath());
        CtModel model = launcher.buildModel();

        RevisionRange revisionRange = mock(RevisionRange.class);
        when(revisionRange.getRevision()).thenReturn(revision);

        final var env = new Environment(model, revisionRange);

        JavadocDetector javadocDetector = new JavadocDetector(env);
        javadocDetector.withAll(35, 50);
        javadocDetector.withAllAccessModifier();
        javadocDetector.withoutReadByWord();
        javadocDetector.withParam(10);
        javadocDetector.withException(10);
        javadocDetector.withDeprecated(10);
        javadocDetector.scan(model);
        assertEquals(29, javadocDetector.getCodeSmells().size());
        Multimap smellMap = mappedCodeSmell(javadocDetector.getCodeSmells());

        assertEquals(1, smellMap.keys().count(Optional.of("missing_javadoc")));
        assertEquals(
                "Missing package-info for the package (missing_javadoc).",
                ((List<CodeSmell>) smellMap.get(Optional.of("missing_javadoc")))
                        .get(0).getSummary().get()
        );

        assertEquals(2, smellMap.keys().count(Optional.of("missing_javadoc.Override_shortDescription")));
        List<CodeSmell> codeSmellsOSD = (List<CodeSmell>) smellMap.get(Optional.of(
                "missing_javadoc.Override_shortDescription"));
        assertEquals("Short-description of this javadoc is too short.", codeSmellsOSD.get(0).getSummary().get());
        assertEquals("Long-description of this javadoc is too short.", codeSmellsOSD.get(1).getSummary().get());

        assertEquals(
                2,
                smellMap.keys().count(Optional.of("missing_javadoc.ReadByCharTest#fieldWithMissingTagDeprecated"))
        );
        List<CodeSmell> codeSmellsFMD = (List<CodeSmell>) smellMap.get(Optional.of(
                "missing_javadoc.ReadByCharTest#fieldWithMissingTagDeprecated"));
        assertEquals("Missing tag @deprecated in the javadoc.", codeSmellsFMD.get(0).getSummary().get());
        assertEquals("Short-description of this javadoc is too short.", codeSmellsFMD.get(1).getSummary().get());

        assertEquals(4, smellMap.keys().count(Optional.of("missing_javadoc.ReadByCharTest$DescriptionEnum")));
        List<CodeSmell> codeSmellsED = (List<CodeSmell>) smellMap.get(Optional.of(
                "missing_javadoc.ReadByCharTest$DescriptionEnum"));
        assertEquals(
                "Javadoc contains @deprecated, but the annotation @Deprecated at the javadocable is missing.",
                codeSmellsED.get(0).getSummary().get()
        );
        assertEquals("Short-description of this javadoc is too short.", codeSmellsED.get(1).getSummary().get());
        assertEquals("Long-description of this javadoc is too short.", codeSmellsED.get(2).getSummary().get());
        assertEquals("Description of the @deprecated tag is too short.", codeSmellsED.get(3).getSummary().get());

        assertEquals(5, smellMap.keys().count(Optional.of("missing_javadoc.ReadByCharTest(java.lang.String)")));
        List<CodeSmell> codeSmellsCS = (List<CodeSmell>) smellMap.get(Optional.of(
                "missing_javadoc.ReadByCharTest(java.lang.String)"));
        assertEquals(
                "Missing tag @throws IllegalArgumentException in the javadoc.",
                codeSmellsCS.get(0).getSummary().get()
        );
        assertEquals("Missing tag @deprecated in the javadoc.", codeSmellsCS.get(1).getSummary().get());
        assertEquals("Missing tag @param text in the javadoc.", codeSmellsCS.get(2).getSummary().get());
        assertEquals("Missing tag @param <T> in the javadoc.", codeSmellsCS.get(3).getSummary().get());
        assertEquals("Short-description of this javadoc is too short.", codeSmellsCS.get(4).getSummary().get());

        assertEquals(
                3,
                smellMap.keys().count(Optional.of("missing_javadoc.ReadByCharTest#fileReaderTest(java.lang.String)"))
        );
        List<CodeSmell> codeSmellsMFR = (List<CodeSmell>) smellMap.get(Optional.of(
                "missing_javadoc.ReadByCharTest#fileReaderTest(java.lang.String)"));
        assertEquals(
                "Javadoc contains @deprecated, but the annotation @Deprecated at the javadocable is missing.",
                codeSmellsMFR.get(0).getSummary().get()
        );
        assertEquals("Missing tag @param file in the javadoc.", codeSmellsMFR.get(1).getSummary().get());
        assertEquals("Short-description of this javadoc is too short.", codeSmellsMFR.get(2).getSummary().get());

        assertEquals(
                4,
                smellMap.keys().count(Optional.of("missing_javadoc.ReadByCharTest#fileReaderTest2(java.lang.String)"))
        );
        List<CodeSmell> codeSmellsMFR2 = (List<CodeSmell>) smellMap.get(Optional.of(
                "missing_javadoc.ReadByCharTest#fileReaderTest2(java.lang.String)"));
        assertEquals(
                "Missing tag @throws FileNotFoundException in the javadoc.",
                codeSmellsMFR2.get(0).getSummary().get()
        );
        assertEquals(
                "Javadoc contains @throws/@exception IOException, but this does not thrown by this or a called executable.",
                codeSmellsMFR2.get(1).getSummary().get()
        );
        assertEquals("Missing tag @deprecated in the javadoc.", codeSmellsMFR2.get(2).getSummary().get());
        assertEquals("Long-description of this javadoc is too short.", codeSmellsMFR2.get(3).getSummary().get());

        assertEquals(3, smellMap.keys().count(Optional.of("missing_javadoc.ReadByCharTest")));
        List<CodeSmell> codeSmellsCR = (List<CodeSmell>) smellMap.get(Optional.of("missing_javadoc.ReadByCharTest"));
        assertEquals(
                "Javadoc contains @param <T>, but this parameter does not exists.",
                codeSmellsCR.get(0).getSummary().get()
        );
        assertEquals(
                "Javadoc contains @deprecated, but the annotation @Deprecated at the javadocable is missing.",
                codeSmellsCR.get(1).getSummary().get()
        );
        assertEquals("Short-description of this javadoc is too short.", codeSmellsCR.get(2).getSummary().get());

        assertEquals(
                1,
                smellMap.keys()
                        .count(Optional.of("missing_javadoc.ShortDescriptionInterface#counter(java.lang.String)"))
        );
        assertEquals(
                "Method counter(java.lang.String) has no javadoc.",
                ((List<CodeSmell>) smellMap.get(Optional.of(
                        "missing_javadoc.ShortDescriptionInterface#counter(java.lang.String)")))
                        .get(0).getSummary().get()
        );

        assertEquals(3, smellMap.keys().count(Optional.of("missing_javadoc.ShortDescriptionInterface")));
        List<CodeSmell> codeSmellsISD = (List<CodeSmell>) smellMap.get(Optional.of(
                "missing_javadoc.ShortDescriptionInterface"));
        assertEquals(
                "Javadoc contains @deprecated, but the annotation @Deprecated at the javadocable is missing.",
                codeSmellsISD.get(0).getSummary().get()
        );
        assertEquals("Short-description of this javadoc is too short.", codeSmellsISD.get(1).getSummary().get());
        assertEquals("Long-description of this javadoc is too short.", codeSmellsISD.get(2).getSummary().get());

        assertEquals(1, smellMap.keys().count(Optional.of("spoon.tooShortShortDescriptionPackage")));
        assertEquals(
                "Short-description of this javadoc is too short.",
                ((List<CodeSmell>) smellMap.get(Optional.of("spoon.tooShortShortDescriptionPackage")))
                        .get(0).getSummary().get()
        );
    }

    @Test
    public void testReadByCharWithoutParam() throws IOException {
        RevisionMock revision = new RevisionMock(folder);
        revision.addFile(Paths.get("missing_javadoc", "ReadByCharTest.java"));
        revision.addFile(Paths.get("missing_javadoc/annotation", "Override_shortDescription.java"));
        revision.addFile(Paths.get("missing_javadoc/interface", "InterfaceTest_shortDescription.java"));
        revision.addFile(Paths.get("missing_javadoc/package/short_description", "package-info.java"));

        Launcher launcher = new Launcher();
        launcher.getEnvironment().setAutoImports(true);
        launcher.addInputResource(folder.getRoot().getAbsolutePath());
        CtModel model = launcher.buildModel();

        RevisionRange revisionRange = mock(RevisionRange.class);
        when(revisionRange.getRevision()).thenReturn(revision);

        final var env = new Environment(model, revisionRange);

        JavadocDetector javadocDetector = new JavadocDetector(env);
        javadocDetector.withAll(35, 50);
        javadocDetector.withAllAccessModifier();
        javadocDetector.withoutReadByWord();
        javadocDetector.withException(10);
        javadocDetector.withDeprecated(10);
        javadocDetector.scan(model);
        assertEquals(25, javadocDetector.getCodeSmells().size());
        Multimap smellMap = mappedCodeSmell(javadocDetector.getCodeSmells());

        assertEquals(1, smellMap.keys().count(Optional.of("missing_javadoc")));
        assertEquals(
                "Missing package-info for the package (missing_javadoc).",
                ((List<CodeSmell>) smellMap.get(Optional.of("missing_javadoc")))
                        .get(0).getSummary().get()
        );

        assertEquals(2, smellMap.keys().count(Optional.of("missing_javadoc.Override_shortDescription")));
        List<CodeSmell> codeSmellsOSD = (List<CodeSmell>) smellMap.get(Optional.of(
                "missing_javadoc.Override_shortDescription"));
        assertEquals("Short-description of this javadoc is too short.", codeSmellsOSD.get(0).getSummary().get());
        assertEquals("Long-description of this javadoc is too short.", codeSmellsOSD.get(1).getSummary().get());

        assertEquals(
                2,
                smellMap.keys().count(Optional.of("missing_javadoc.ReadByCharTest#fieldWithMissingTagDeprecated"))
        );
        List<CodeSmell> codeSmellsFMD = (List<CodeSmell>) smellMap.get(Optional.of(
                "missing_javadoc.ReadByCharTest#fieldWithMissingTagDeprecated"));
        assertEquals("Missing tag @deprecated in the javadoc.", codeSmellsFMD.get(0).getSummary().get());
        assertEquals("Short-description of this javadoc is too short.", codeSmellsFMD.get(1).getSummary().get());

        assertEquals(4, smellMap.keys().count(Optional.of("missing_javadoc.ReadByCharTest$DescriptionEnum")));
        List<CodeSmell> codeSmellsED = (List<CodeSmell>) smellMap.get(Optional.of(
                "missing_javadoc.ReadByCharTest$DescriptionEnum"));
        assertEquals(
                "Javadoc contains @deprecated, but the annotation @Deprecated at the javadocable is missing.",
                codeSmellsED.get(0).getSummary().get()
        );
        assertEquals("Short-description of this javadoc is too short.", codeSmellsED.get(1).getSummary().get());
        assertEquals("Long-description of this javadoc is too short.", codeSmellsED.get(2).getSummary().get());
        assertEquals("Description of the @deprecated tag is too short.", codeSmellsED.get(3).getSummary().get());

        assertEquals(3, smellMap.keys().count(Optional.of("missing_javadoc.ReadByCharTest(java.lang.String)")));
        List<CodeSmell> codeSmellsCS = (List<CodeSmell>) smellMap.get(Optional.of(
                "missing_javadoc.ReadByCharTest(java.lang.String)"));
        assertEquals(
                "Missing tag @throws IllegalArgumentException in the javadoc.",
                codeSmellsCS.get(0).getSummary().get()
        );
        assertEquals("Missing tag @deprecated in the javadoc.", codeSmellsCS.get(1).getSummary().get());
        assertEquals("Short-description of this javadoc is too short.", codeSmellsCS.get(2).getSummary().get());

        assertEquals(
                2,
                smellMap.keys().count(Optional.of("missing_javadoc.ReadByCharTest#fileReaderTest(java.lang.String)"))
        );
        List<CodeSmell> codeSmellsMFR = (List<CodeSmell>) smellMap.get(Optional.of(
                "missing_javadoc.ReadByCharTest#fileReaderTest(java.lang.String)"));
        assertEquals(
                "Javadoc contains @deprecated, but the annotation @Deprecated at the javadocable is missing.",
                codeSmellsMFR.get(0).getSummary().get()
        );
        assertEquals("Short-description of this javadoc is too short.", codeSmellsMFR.get(1).getSummary().get());

        assertEquals(
                4,
                smellMap.keys().count(Optional.of("missing_javadoc.ReadByCharTest#fileReaderTest2(java.lang.String)"))
        );
        List<CodeSmell> codeSmellsMFR2 = (List<CodeSmell>) smellMap.get(Optional.of(
                "missing_javadoc.ReadByCharTest#fileReaderTest2(java.lang.String)"));
        assertEquals(
                "Missing tag @throws FileNotFoundException in the javadoc.",
                codeSmellsMFR2.get(0).getSummary().get()
        );
        assertEquals(
                "Javadoc contains @throws/@exception IOException, but this does not thrown by this or a called executable.",
                codeSmellsMFR2.get(1).getSummary().get()
        );
        assertEquals("Missing tag @deprecated in the javadoc.", codeSmellsMFR2.get(2).getSummary().get());
        assertEquals("Long-description of this javadoc is too short.", codeSmellsMFR2.get(3).getSummary().get());

        assertEquals(2, smellMap.keys().count(Optional.of("missing_javadoc.ReadByCharTest")));
        List<CodeSmell> codeSmellsCR = (List<CodeSmell>) smellMap.get(Optional.of("missing_javadoc.ReadByCharTest"));
        assertEquals(
                "Javadoc contains @deprecated, but the annotation @Deprecated at the javadocable is missing.",
                codeSmellsCR.get(0).getSummary().get()
        );
        assertEquals("Short-description of this javadoc is too short.", codeSmellsCR.get(1).getSummary().get());

        assertEquals(
                1,
                smellMap.keys()
                        .count(Optional.of("missing_javadoc.ShortDescriptionInterface#counter(java.lang.String)"))
        );
        assertEquals(
                "Method counter(java.lang.String) has no javadoc.",
                ((List<CodeSmell>) smellMap.get(Optional.of(
                        "missing_javadoc.ShortDescriptionInterface#counter(java.lang.String)")))
                        .get(0).getSummary().get()
        );

        assertEquals(3, smellMap.keys().count(Optional.of("missing_javadoc.ShortDescriptionInterface")));
        List<CodeSmell> codeSmellsISD = (List<CodeSmell>) smellMap.get(Optional.of(
                "missing_javadoc.ShortDescriptionInterface"));
        assertEquals(
                "Javadoc contains @deprecated, but the annotation @Deprecated at the javadocable is missing.",
                codeSmellsISD.get(0).getSummary().get()
        );
        assertEquals("Short-description of this javadoc is too short.", codeSmellsISD.get(1).getSummary().get());
        assertEquals("Long-description of this javadoc is too short.", codeSmellsISD.get(2).getSummary().get());

        assertEquals(1, smellMap.keys().count(Optional.of("spoon.tooShortShortDescriptionPackage")));
        assertEquals(
                "Short-description of this javadoc is too short.",
                ((List<CodeSmell>) smellMap.get(Optional.of("spoon.tooShortShortDescriptionPackage")))
                        .get(0).getSummary().get()
        );
    }

    @Test
    public void testReadByCharWithoutDeprecated() throws IOException {
        RevisionMock revision = new RevisionMock(folder);
        revision.addFile(Paths.get("missing_javadoc", "ReadByCharTest.java"));
        revision.addFile(Paths.get("missing_javadoc/annotation", "Override_shortDescription.java"));
        revision.addFile(Paths.get("missing_javadoc/interface", "InterfaceTest_shortDescription.java"));
        revision.addFile(Paths.get("missing_javadoc/package/short_description", "package-info.java"));

        Launcher launcher = new Launcher();
        launcher.getEnvironment().setAutoImports(true);
        launcher.addInputResource(folder.getRoot().getAbsolutePath());
        CtModel model = launcher.buildModel();

        RevisionRange revisionRange = mock(RevisionRange.class);
        when(revisionRange.getRevision()).thenReturn(revision);

        final var env = new Environment(model, revisionRange);

        JavadocDetector javadocDetector = new JavadocDetector(env);
        javadocDetector.withAll(35, 50);
        javadocDetector.withAllAccessModifier();
        javadocDetector.withoutReadByWord();
        javadocDetector.withParam(10);
        javadocDetector.withException(10);
        javadocDetector.scan(model);
        assertEquals(21, javadocDetector.getCodeSmells().size());
        Multimap smellMap = mappedCodeSmell(javadocDetector.getCodeSmells());

        assertEquals(1, smellMap.keys().count(Optional.of("missing_javadoc")));
        assertEquals(
                "Missing package-info for the package (missing_javadoc).",
                ((List<CodeSmell>) smellMap.get(Optional.of("missing_javadoc")))
                        .get(0).getSummary().get()
        );

        assertEquals(2, smellMap.keys().count(Optional.of("missing_javadoc.Override_shortDescription")));
        List<CodeSmell> codeSmellsOSD = (List<CodeSmell>) smellMap.get(Optional.of(
                "missing_javadoc.Override_shortDescription"));
        assertEquals("Short-description of this javadoc is too short.", codeSmellsOSD.get(0).getSummary().get());
        assertEquals("Long-description of this javadoc is too short.", codeSmellsOSD.get(1).getSummary().get());

        assertEquals(
                1,
                smellMap.keys().count(Optional.of("missing_javadoc.ReadByCharTest#fieldWithMissingTagDeprecated"))
        );
        List<CodeSmell> codeSmellsFMD = (List<CodeSmell>) smellMap.get(Optional.of(
                "missing_javadoc.ReadByCharTest#fieldWithMissingTagDeprecated"));
        assertEquals("Short-description of this javadoc is too short.", codeSmellsFMD.get(0).getSummary().get());

        assertEquals(2, smellMap.keys().count(Optional.of("missing_javadoc.ReadByCharTest$DescriptionEnum")));
        List<CodeSmell> codeSmellsED = (List<CodeSmell>) smellMap.get(Optional.of(
                "missing_javadoc.ReadByCharTest$DescriptionEnum"));
        assertEquals("Short-description of this javadoc is too short.", codeSmellsED.get(0).getSummary().get());
        assertEquals("Long-description of this javadoc is too short.", codeSmellsED.get(1).getSummary().get());

        assertEquals(4, smellMap.keys().count(Optional.of("missing_javadoc.ReadByCharTest(java.lang.String)")));
        List<CodeSmell> codeSmellsCS = (List<CodeSmell>) smellMap.get(Optional.of(
                "missing_javadoc.ReadByCharTest(java.lang.String)"));
        assertEquals(
                "Missing tag @throws IllegalArgumentException in the javadoc.",
                codeSmellsCS.get(0).getSummary().get()
        );
        assertEquals("Missing tag @param text in the javadoc.", codeSmellsCS.get(1).getSummary().get());
        assertEquals("Missing tag @param <T> in the javadoc.", codeSmellsCS.get(2).getSummary().get());
        assertEquals("Short-description of this javadoc is too short.", codeSmellsCS.get(3).getSummary().get());

        assertEquals(
                2,
                smellMap.keys().count(Optional.of("missing_javadoc.ReadByCharTest#fileReaderTest(java.lang.String)"))
        );
        List<CodeSmell> codeSmellsMFR = (List<CodeSmell>) smellMap.get(Optional.of(
                "missing_javadoc.ReadByCharTest#fileReaderTest(java.lang.String)"));
        assertEquals("Missing tag @param file in the javadoc.", codeSmellsMFR.get(0).getSummary().get());
        assertEquals("Short-description of this javadoc is too short.", codeSmellsMFR.get(1).getSummary().get());

        assertEquals(
                3,
                smellMap.keys().count(Optional.of("missing_javadoc.ReadByCharTest#fileReaderTest2(java.lang.String)"))
        );
        List<CodeSmell> codeSmellsMFR2 = (List<CodeSmell>) smellMap.get(Optional.of(
                "missing_javadoc.ReadByCharTest#fileReaderTest2(java.lang.String)"));
        assertEquals(
                "Missing tag @throws FileNotFoundException in the javadoc.",
                codeSmellsMFR2.get(0).getSummary().get()
        );
        assertEquals(
                "Javadoc contains @throws/@exception IOException, but this does not thrown by this or a called executable.",
                codeSmellsMFR2.get(1).getSummary().get()
        );
        assertEquals("Long-description of this javadoc is too short.", codeSmellsMFR2.get(2).getSummary().get());

        assertEquals(2, smellMap.keys().count(Optional.of("missing_javadoc.ReadByCharTest")));
        List<CodeSmell> codeSmellsCR = (List<CodeSmell>) smellMap.get(Optional.of("missing_javadoc.ReadByCharTest"));
        assertEquals(
                "Javadoc contains @param <T>, but this parameter does not exists.",
                codeSmellsCR.get(0).getSummary().get()
        );
        assertEquals("Short-description of this javadoc is too short.", codeSmellsCR.get(1).getSummary().get());

        assertEquals(
                1,
                smellMap.keys()
                        .count(Optional.of("missing_javadoc.ShortDescriptionInterface#counter(java.lang.String)"))
        );
        assertEquals(
                "Method counter(java.lang.String) has no javadoc.",
                ((List<CodeSmell>) smellMap.get(Optional.of(
                        "missing_javadoc.ShortDescriptionInterface#counter(java.lang.String)")))
                        .get(0).getSummary().get()
        );

        assertEquals(2, smellMap.keys().count(Optional.of("missing_javadoc.ShortDescriptionInterface")));
        List<CodeSmell> codeSmellsISD = (List<CodeSmell>) smellMap.get(Optional.of(
                "missing_javadoc.ShortDescriptionInterface"));
        assertEquals("Short-description of this javadoc is too short.", codeSmellsISD.get(0).getSummary().get());
        assertEquals("Long-description of this javadoc is too short.", codeSmellsISD.get(1).getSummary().get());

        assertEquals(1, smellMap.keys().count(Optional.of("spoon.tooShortShortDescriptionPackage")));
        assertEquals(
                "Short-description of this javadoc is too short.",
                ((List<CodeSmell>) smellMap.get(Optional.of("spoon.tooShortShortDescriptionPackage")))
                        .get(0).getSummary().get()
        );
    }

    @Test
    public void testReadByCharWithoutException() throws IOException {
        RevisionMock revision = new RevisionMock(folder);
        revision.addFile(Paths.get("missing_javadoc", "ReadByCharTest.java"));
        revision.addFile(Paths.get("missing_javadoc/annotation", "Override_shortDescription.java"));
        revision.addFile(Paths.get("missing_javadoc/interface", "InterfaceTest_shortDescription.java"));
        revision.addFile(Paths.get("missing_javadoc/package/short_description", "package-info.java"));

        Launcher launcher = new Launcher();
        launcher.getEnvironment().setAutoImports(true);
        launcher.addInputResource(folder.getRoot().getAbsolutePath());
        CtModel model = launcher.buildModel();

        RevisionRange revisionRange = mock(RevisionRange.class);
        when(revisionRange.getRevision()).thenReturn(revision);

        final var env = new Environment(model, revisionRange);

        JavadocDetector javadocDetector = new JavadocDetector(env);
        javadocDetector.withAll(35, 50);
        javadocDetector.withAllAccessModifier();
        javadocDetector.withoutReadByWord();
        javadocDetector.withParam(10);
        javadocDetector.withDeprecated(10);
        javadocDetector.scan(model);
        assertEquals(26, javadocDetector.getCodeSmells().size());
        Multimap smellMap = mappedCodeSmell(javadocDetector.getCodeSmells());

        assertEquals(1, smellMap.keys().count(Optional.of("missing_javadoc")));
        assertEquals(
                "Missing package-info for the package (missing_javadoc).",
                ((List<CodeSmell>) smellMap.get(Optional.of("missing_javadoc")))
                        .get(0).getSummary().get()
        );

        assertEquals(2, smellMap.keys().count(Optional.of("missing_javadoc.Override_shortDescription")));
        List<CodeSmell> codeSmellsOSD = (List<CodeSmell>) smellMap.get(Optional.of(
                "missing_javadoc.Override_shortDescription"));
        assertEquals("Short-description of this javadoc is too short.", codeSmellsOSD.get(0).getSummary().get());
        assertEquals("Long-description of this javadoc is too short.", codeSmellsOSD.get(1).getSummary().get());

        assertEquals(
                2,
                smellMap.keys().count(Optional.of("missing_javadoc.ReadByCharTest#fieldWithMissingTagDeprecated"))
        );
        List<CodeSmell> codeSmellsFMD = (List<CodeSmell>) smellMap.get(Optional.of(
                "missing_javadoc.ReadByCharTest#fieldWithMissingTagDeprecated"));
        assertEquals("Missing tag @deprecated in the javadoc.", codeSmellsFMD.get(0).getSummary().get());
        assertEquals("Short-description of this javadoc is too short.", codeSmellsFMD.get(1).getSummary().get());

        assertEquals(4, smellMap.keys().count(Optional.of("missing_javadoc.ReadByCharTest$DescriptionEnum")));
        List<CodeSmell> codeSmellsED = (List<CodeSmell>) smellMap.get(Optional.of(
                "missing_javadoc.ReadByCharTest$DescriptionEnum"));
        assertEquals(
                "Javadoc contains @deprecated, but the annotation @Deprecated at the javadocable is missing.",
                codeSmellsED.get(0).getSummary().get()
        );
        assertEquals("Short-description of this javadoc is too short.", codeSmellsED.get(1).getSummary().get());
        assertEquals("Long-description of this javadoc is too short.", codeSmellsED.get(2).getSummary().get());
        assertEquals("Description of the @deprecated tag is too short.", codeSmellsED.get(3).getSummary().get());

        assertEquals(4, smellMap.keys().count(Optional.of("missing_javadoc.ReadByCharTest(java.lang.String)")));
        List<CodeSmell> codeSmellsCS = (List<CodeSmell>) smellMap.get(Optional.of(
                "missing_javadoc.ReadByCharTest(java.lang.String)"));
        assertEquals("Missing tag @deprecated in the javadoc.", codeSmellsCS.get(0).getSummary().get());
        assertEquals("Missing tag @param text in the javadoc.", codeSmellsCS.get(1).getSummary().get());
        assertEquals("Missing tag @param <T> in the javadoc.", codeSmellsCS.get(2).getSummary().get());
        assertEquals("Short-description of this javadoc is too short.", codeSmellsCS.get(3).getSummary().get());

        assertEquals(
                3,
                smellMap.keys().count(Optional.of("missing_javadoc.ReadByCharTest#fileReaderTest(java.lang.String)"))
        );
        List<CodeSmell> codeSmellsMFR = (List<CodeSmell>) smellMap.get(Optional.of(
                "missing_javadoc.ReadByCharTest#fileReaderTest(java.lang.String)"));
        assertEquals(
                "Javadoc contains @deprecated, but the annotation @Deprecated at the javadocable is missing.",
                codeSmellsMFR.get(0).getSummary().get()
        );
        assertEquals("Missing tag @param file in the javadoc.", codeSmellsMFR.get(1).getSummary().get());
        assertEquals("Short-description of this javadoc is too short.", codeSmellsMFR.get(2).getSummary().get());

        assertEquals(
                2,
                smellMap.keys().count(Optional.of("missing_javadoc.ReadByCharTest#fileReaderTest2(java.lang.String)"))
        );
        List<CodeSmell> codeSmellsMFR2 = (List<CodeSmell>) smellMap.get(Optional.of(
                "missing_javadoc.ReadByCharTest#fileReaderTest2(java.lang.String)"));
        assertEquals("Missing tag @deprecated in the javadoc.", codeSmellsMFR2.get(0).getSummary().get());
        assertEquals("Long-description of this javadoc is too short.", codeSmellsMFR2.get(1).getSummary().get());

        assertEquals(3, smellMap.keys().count(Optional.of("missing_javadoc.ReadByCharTest")));
        List<CodeSmell> codeSmellsCR = (List<CodeSmell>) smellMap.get(Optional.of("missing_javadoc.ReadByCharTest"));
        assertEquals(
                "Javadoc contains @param <T>, but this parameter does not exists.",
                codeSmellsCR.get(0).getSummary().get()
        );
        assertEquals(
                "Javadoc contains @deprecated, but the annotation @Deprecated at the javadocable is missing.",
                codeSmellsCR.get(1).getSummary().get()
        );
        assertEquals("Short-description of this javadoc is too short.", codeSmellsCR.get(2).getSummary().get());

        assertEquals(
                1,
                smellMap.keys()
                        .count(Optional.of("missing_javadoc.ShortDescriptionInterface#counter(java.lang.String)"))
        );
        assertEquals(
                "Method counter(java.lang.String) has no javadoc.",
                ((List<CodeSmell>) smellMap.get(Optional.of(
                        "missing_javadoc.ShortDescriptionInterface#counter(java.lang.String)")))
                        .get(0).getSummary().get()
        );

        assertEquals(3, smellMap.keys().count(Optional.of("missing_javadoc.ShortDescriptionInterface")));
        List<CodeSmell> codeSmellsISD = (List<CodeSmell>) smellMap.get(Optional.of(
                "missing_javadoc.ShortDescriptionInterface"));
        assertEquals(
                "Javadoc contains @deprecated, but the annotation @Deprecated at the javadocable is missing.",
                codeSmellsISD.get(0).getSummary().get()
        );
        assertEquals("Short-description of this javadoc is too short.", codeSmellsISD.get(1).getSummary().get());
        assertEquals("Long-description of this javadoc is too short.", codeSmellsISD.get(2).getSummary().get());

        assertEquals(1, smellMap.keys().count(Optional.of("spoon.tooShortShortDescriptionPackage")));
        assertEquals(
                "Short-description of this javadoc is too short.",
                ((List<CodeSmell>) smellMap.get(Optional.of("spoon.tooShortShortDescriptionPackage")))
                        .get(0).getSummary().get()
        );
    }
}
