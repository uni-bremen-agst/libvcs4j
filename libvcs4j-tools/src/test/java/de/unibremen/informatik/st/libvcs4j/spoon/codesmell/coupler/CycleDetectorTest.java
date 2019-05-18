package de.unibremen.informatik.st.libvcs4j.spoon.codesmell.coupler;

import de.unibremen.informatik.st.libvcs4j.spoon.codesmell.CodeSmell;
import de.unibremen.informatik.st.libvcs4j.spoon.codesmell.RevisionMock;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import spoon.Launcher;
import spoon.reflect.CtModel;

import java.nio.file.Paths;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class CycleDetectorTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void testSimpleCycleDetection() throws Exception {
        RevisionMock revision = new RevisionMock(folder);
        revision.addFile(Paths.get("cycle", "Cycle.java"));

        Launcher launcher = new Launcher();
        launcher.addInputResource(folder.getRoot().getAbsolutePath());
        CtModel model = launcher.buildModel();

        CycleDetector cycleDetector = new CycleDetector(revision);
        cycleDetector.scan(model);
        List<CodeSmell> codeSmells = cycleDetector.getCodeSmells();

        assertThat(codeSmells).hasSize(1);
        CodeSmell codeSmell = codeSmells.get(0);
        assertThat(codeSmell.getRanges()).hasSize(2);
        assertThat(codeSmell.getRanges()).first()
                .matches(range -> range.getBegin().getLine() == 1)
                .matches(range -> range.getBegin().getColumn() == 1)
                .matches(range -> range.getEnd().getLine() == 3)
                .matches(range -> range.getEnd().getColumn() == 1);
        assertThat(codeSmell.getRanges()).last()
                .matches(range -> range.getBegin().getLine() == 5)
                .matches(range -> range.getBegin().getColumn() == 1)
                .matches(range -> range.getEnd().getLine() == 7)
                .matches(range -> range.getEnd().getColumn() == 1);
    }
}
