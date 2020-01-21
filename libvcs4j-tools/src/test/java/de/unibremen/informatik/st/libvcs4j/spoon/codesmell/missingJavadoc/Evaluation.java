package de.unibremen.informatik.st.libvcs4j.spoon.codesmell.missingJavadoc;


import de.unibremen.informatik.st.libvcs4j.RevisionRange;
import de.unibremen.informatik.st.libvcs4j.VCSEngine;
import de.unibremen.informatik.st.libvcs4j.VCSEngineBuilder;
import de.unibremen.informatik.st.libvcs4j.spoon.BuildException;
import de.unibremen.informatik.st.libvcs4j.spoon.Environment;
import de.unibremen.informatik.st.libvcs4j.spoon.SpoonModelBuilder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import spoon.reflect.CtModel;

import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Evaluation {
    private final String PATH = "C:/analysis";
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private void setTags(JavadocDetector javadocDetector) {
        javadocDetector.withAll(15);
        javadocDetector.withEnum(10);
        javadocDetector.withField(5);
        javadocDetector.withExecutable(10);
        javadocDetector.withMethodFieldAccess(5);
        javadocDetector.withAllAccessModifier();
        javadocDetector.withPackageInfoNeeded();
        javadocDetector.withTypelessPackageInfoNeeded();
        javadocDetector.withDeprecated(5);
        javadocDetector.withReturn(3);
        javadocDetector.withParam(3);
        javadocDetector.withThrows(5);
        javadocDetector.withAuthor(2);
        javadocDetector.withVersion(1);
        javadocDetector.withSee(1);
        javadocDetector.withSince(1);
        javadocDetector.withSerial(5);
        javadocDetector.withSerialField(5);
        javadocDetector.withSerialData(5);
    }


    /**
     * Hauptmethode zum Ausführen der Analyse für mehrere Projekte für die zweite und dritte Forschungsfrage.
     * Dabei sind die Hinweise im Quellcode zum angeben der zu untersuchenden Projekte zu beachten.
     */
    @Test
    public void evaluateQ2ToQ3() throws IOException, BuildException {
        LineNumberReader lnr = new LineNumberReader(new FileReader(PATH + "/analysis_q2-q3.csv"));

        String currentLine;

        /*
            Diese while-Schleife überspringt die bereits analysierten Projekte.
            Dabei gibt die Zahl, in diesem Fall 0 an, bis zu welcher Stelle der Projekte bereits analysiert wurde.
            Die Liste in der zu sehen ist, an welcher Stelle sich ein Projekt befindet, kann in der CSV-Datei
            "analysis_q2-q3 angeschaut werden. Diese befindet sich im analysis-Verzeichnis.
         */
        while (lnr.getLineNumber() < 0 && (currentLine = lnr.readLine()) != null) {
        }
        ArrayList<String> failed = new ArrayList<>();

        /*
            Mittels dieser while-Schleife, wird konfiguriert welche Projekte analysiert werden.
            In diesem Fall werden die Projekte an der 1 bis zur 5 Stelle analysiert.
         */
        while ((currentLine = lnr.readLine()) != null && lnr.getLineNumber() >= 1 && lnr.getLineNumber() <= 5) {
            String[] splittedLine;
            splittedLine = currentLine.split(";");
            Path target = Paths.get(
                    folder.getRoot().getAbsolutePath(),
                    splittedLine[0].substring(splittedLine[0].lastIndexOf("/") + 1)
            );

            try {
                VCSEngine engine = VCSEngineBuilder
                        .ofGit(PATH + splittedLine[0])
                        .withRoot(splittedLine[1])
                        .withTarget(target)
                        .withLatestRevision()
                        .build();

                SpoonModelBuilder builder = new SpoonModelBuilder();
                builder.setIncremental(true);

                for (RevisionRange range : engine) {
                    CtModel model = builder.update(range).getCtModel();
                    JavadocDetector javadocDetector = new JavadocDetector(new Environment(model, range));
                    setTags(javadocDetector);
                    javadocDetector.scan(model);
                    JavadocEvaluator evaluator = new JavadocEvaluator();
                    evaluator.begin(
                            javadocDetector,
                            model,
                            splittedLine[0].substring(splittedLine[0].lastIndexOf("/") + 1)
                    );
                }
            } catch (Exception e) {
                failed.add(splittedLine[0] + " with " + splittedLine[1]);
            }
        }
        System.out.println(failed);
    }

    /**
     * Hauptmethode zum Ausführen der Analyse für mehrere Projekte für die erste Forschungsfrage.
     * Dabei sind die Hinweise im Quellcode zum angeben der zu untersuchenden Projekte zu beachten.
     */
    @Test
    public void evaluateQ1() throws IOException, BuildException {
        LineNumberReader lnr = new LineNumberReader(new FileReader(PATH + "/analysis_q1.csv"));
        ArrayList<String> failed = new ArrayList<>();

        String currentLine;

        /*
            Diese while-Schleife überspringt die bereits analysierten Projekte.
            Dabei gibt die Zahl, in diesem Fall 3 an, bis zu welcher Stelle der Projekte bereits analysiert wurde.
            Die Liste in der zu sehen ist, an welcher Stelle sich ein Projekt befindet, kann in der CSV-Datei
            "analysis_q1 angeschaut werden. Diese befindet sich im analysis-Verzeichnis.
         */
        while (lnr.getLineNumber() < 3 && (currentLine = lnr.readLine()) != null) {

        }

        /*
            Mittels dieser while-Schleife, wird konfiguriert welche Projekte analysiert werden.
            In diesem Fall werden die Projekte an der 4 bis zur 8 Stelle analysiert.
         */
        while ((currentLine = lnr.readLine()) != null && lnr.getLineNumber() >= 4 && lnr.getLineNumber() <= 8) {
            String[] splittedLine;
            splittedLine = currentLine.split(";");
            Path target = Paths.get(
                    folder.getRoot().getAbsolutePath(),
                    splittedLine[0].substring(splittedLine[0].lastIndexOf("/") + 1)
            );
            try {
                VCSEngine engine = VCSEngineBuilder
                        .ofGit(PATH + splittedLine[0])
                        .withRoot(splittedLine[1])
                        .withTarget(target)
                        .build();

                SpoonModelBuilder builder = new SpoonModelBuilder();
                builder.setIncremental(true);

                for (RevisionRange range : engine) {
                    CtModel model = builder.update(range).getCtModel();
                    JavadocDetector javadocDetector = new JavadocDetector(new Environment(model, range));
                    setTags(javadocDetector);
                    javadocDetector.scan(model);
                    JavadocEvaluator evaluator = new JavadocEvaluator();
                    evaluator.beginQ1(javadocDetector, model,
                            splittedLine[0].substring(splittedLine[0].lastIndexOf("/") + 1), range.getOrdinal()
                    );
                }
            } catch (Exception e) {
                failed.add(splittedLine[0] + " with " + splittedLine[1]);
            } catch (Error er) {
                failed.add(splittedLine[0] + " with " + splittedLine[1]);
            }
        }
        System.out.println(failed);
    }

    /**
     * Zum Analysieren eines einzelnenen Projektes für die erste Forschungsfrage.
     */
    @Test
    public void testQ1() throws IOException, BuildException {
        Path target = Paths.get(folder.getRoot().getAbsolutePath(), "target");
        VCSEngine engine = VCSEngineBuilder
                .ofGit("C:/analysis/projekte/libgdx")
                .withRoot("gdx/src")
                .withTarget(target)
                .withStartIdx(3969)
                .build();

        SpoonModelBuilder builder = new SpoonModelBuilder();
        builder.setIncremental(true);

        for (RevisionRange range : engine) {
            try {
                CtModel model = builder.update(range).getCtModel();

                final var env = new Environment(model, range);

                JavadocDetector javadocDetector = new JavadocDetector(env);
                setTags(javadocDetector);
                javadocDetector.scan(model);

                JavadocEvaluator evaluator = new JavadocEvaluator();
                evaluator.beginQ1(javadocDetector, model,
                        "jgit", range.getOrdinal()
                );
            } catch (Exception e) {
                System.out.println(e + " in " + range.getOrdinal());
            } catch (Error er) {
                System.out.println(er + " in " + range.getOrdinal());
            }
        }
    }

    /**
     * Zum Analysieren von einzelnen Projekten für Forschungsfrage zwei und drei.
     * Einige Projekte können nicht mit der setAutoImports Funktion gebuildet werden.
     * Deshalb wird diese Methode benötigt.
     */
    @Test
    public void testQ2toQ3() throws IOException, BuildException {
        Path target = Paths.get(folder.getRoot().getAbsolutePath(), "target");

        VCSEngine engine = VCSEngineBuilder
                .ofGit("C:/analysis/projekte/Agrona")
                .withRoot("src/main")
                .withTarget(target)
                .withLatestRevision()
                .build();

        SpoonModelBuilder builder = new SpoonModelBuilder();
        builder.setAutoImports(false);
        builder.setIncremental(true);

        for (RevisionRange range : engine) {
            CtModel model = builder.update(range).getCtModel();
            JavadocDetector javadocDetector = new JavadocDetector(new Environment(model, range));
            setTags(javadocDetector);
            javadocDetector.scan(model);
            JavadocEvaluator evaluator = new JavadocEvaluator();
            evaluator.begin(javadocDetector, model, "Agrona");
        }
    }
}
