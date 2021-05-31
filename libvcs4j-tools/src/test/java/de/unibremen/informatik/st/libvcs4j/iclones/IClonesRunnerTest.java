package de.unibremen.informatik.st.libvcs4j.iclones;

import de.unibremen.informatik.st.libvcs4j.VCSFile;
import de.unibremen.informatik.st.libvcs4j.spoon.codesmell.RevisionMock;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.lang.reflect.InvocationTargetException;


import static org.assertj.core.api.Assertions.assertThat;

public class IClonesRunnerTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void IClonesTest() throws IOException, InterruptedException, InvocationTargetException {
        RevisionMock revision = new RevisionMock(folder);
        revision.addFile(Paths.get("cpdtest","Test1.java"));
        revision.addFile(Paths.get("cpdtest","Test2.java"));

        IClonesRunner cpdRunner = new IClonesRunner(100,20);
        IClonesDetectionResult result = cpdRunner.analyze(revision);
        assertThat(result).isNotNull();
    }

}
