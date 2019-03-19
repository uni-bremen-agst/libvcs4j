package de.unibremen.informatik.st.libvcs4j.spoon.codesmell.ooabusers;

import de.unibremen.informatik.st.libvcs4j.Revision;
import de.unibremen.informatik.st.libvcs4j.VCSEngine;
import de.unibremen.informatik.st.libvcs4j.VCSFile;
import de.unibremen.informatik.st.libvcs4j.spoon.codesmell.CodeSmell;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import spoon.Launcher;
import spoon.SpoonModelBuilder;
import spoon.reflect.CtModel;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SwitchStatementDetectorTest {

    private static final String FILES_DIR = "/switch-statement/";

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    /**
     * The test subject.
     */
    private SwitchStatementDetector switchStatementDetector;

    private CtModel model;

    @Before
    public void setUp() throws Exception {
        Files.write(folder.newFile("A.java").toPath(),
                IOUtils.toByteArray(getClass().getResourceAsStream(
                        FILES_DIR + "A.java")));
        Launcher launcher = new Launcher();
        launcher.getModelBuilder().setSourceClasspath(folder.getRoot().getCanonicalPath());
        launcher.addInputResource(folder.getRoot().getCanonicalPath());
        launcher.setBinaryOutputDirectory(folder.getRoot().getCanonicalPath());
        launcher.getModelBuilder().compile(SpoonModelBuilder.InputType.FILES);
        model = launcher.buildModel();

        Revision revision = mock(Revision.class);
        when(revision.getOutput()).thenReturn(folder.getRoot().toPath());
        List<VCSFile> mockList =
                Arrays.stream(folder.getRoot().list())
                        .map(VCSFileMock::new)
                        .collect(Collectors.toList());
        when(revision.getFiles()).thenReturn(mockList);
        switchStatementDetector = new SwitchStatementDetector(revision);

    }

    @Test
    public void test() {
        switchStatementDetector.scan(model);
        assertThat(switchStatementDetector.getCodeSmells().isEmpty()).isFalse();
        CodeSmell codeSmell = switchStatementDetector.getCodeSmells().get(0);
        VCSFile.Range range = codeSmell.getRanges().get(0);
        assertThat(range.getBegin().getLine()).isEqualTo(4);
        assertThat(range.getBegin().getColumn()).isEqualTo(9);
        assertThat(range.getEnd().getLine()).isEqualTo(21);
        assertThat(range.getEnd().getColumn()).isEqualTo(9);
    }

    ////////////////////////////////////////////////////////////////////////////

    private class VCSFileMock implements VCSFile {

        private final String file;

        VCSFileMock(String file) {
            this.file = file;
        }

        @Override
        public String getRelativePath() {
            return file;
        }

        @Override
        public String getPath() {
            return folder.getRoot().toPath().resolve(file).toString();
        }

        @Override
        public Optional<Charset> guessCharset() throws IOException {
            return Optional.empty();
        }

        @Override
        public byte[] readAllBytes() throws IOException {
            return Files.readAllBytes(Paths.get(this.getPath()));
        }

        @Override
        public Revision getRevision() {
            throw new UnsupportedOperationException();
        }

        @Override
        public VCSEngine getVCSEngine() {
            throw new UnsupportedOperationException();
        }
    }
}
