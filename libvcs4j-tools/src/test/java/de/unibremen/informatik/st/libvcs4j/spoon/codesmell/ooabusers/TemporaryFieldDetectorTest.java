package de.unibremen.informatik.st.libvcs4j.spoon.codesmell.ooabusers;

import de.unibremen.informatik.st.libvcs4j.RevisionRange;
import de.unibremen.informatik.st.libvcs4j.VCSFile;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TemporaryFieldDetectorTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void simpleDetection() throws IOException {
        RevisionMock revision = new RevisionMock(folder);
        revision.addFile(Paths.get("temporary-field", "A.java"));

        RevisionRange revisionRange = mock(RevisionRange.class);
        when(revisionRange.getRevision()).thenReturn(revision);

        Launcher launcher = new Launcher();
        launcher.addInputResource(folder.getRoot().getAbsolutePath());
        CtModel model = launcher.buildModel();

        Environment environment = new Environment(model, revisionRange);

        TemporaryFieldDetector temporaryFieldDetector =
                new TemporaryFieldDetector(environment);
        temporaryFieldDetector.scan(model);
        assertThat(temporaryFieldDetector.getCodeSmells()).hasSize(2);
        CodeSmell codeSmell = temporaryFieldDetector.getCodeSmells().get(0);
        assertThat(codeSmell.getSignature()).isEqualTo(Optional.of("A#abc"));
        VCSFile.Range range = codeSmell.getRanges().get(0);
        assertThat(range.getBegin().getLine()).isEqualTo(2);
        assertThat(range.getBegin().getColumn()).isEqualTo(5);
        assertThat(range.getEnd().getLine()).isEqualTo(2);
        assertThat(range.getEnd().getColumn()).isEqualTo(30);
        codeSmell = temporaryFieldDetector.getCodeSmells().get(1);
        assertThat(codeSmell.getSignature()).isEqualTo(Optional.of("A#var"));
        range = codeSmell.getRanges().get(0);
        assertThat(range.getBegin().getLine()).isEqualTo(4);
        assertThat(range.getBegin().getColumn()).isEqualTo(5);
        assertThat(range.getEnd().getLine()).isEqualTo(4);
        assertThat(range.getEnd().getColumn()).isEqualTo(24);
    }
}
