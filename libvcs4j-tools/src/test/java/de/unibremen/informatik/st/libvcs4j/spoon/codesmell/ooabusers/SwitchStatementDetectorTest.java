package de.unibremen.informatik.st.libvcs4j.spoon.codesmell.ooabusers;

import de.unibremen.informatik.st.libvcs4j.Revision;
import de.unibremen.informatik.st.libvcs4j.VCSEngine;
import de.unibremen.informatik.st.libvcs4j.VCSFile;
import de.unibremen.informatik.st.libvcs4j.spoon.codesmell.CodeSmell;
import de.unibremen.informatik.st.libvcs4j.spoon.codesmell.RevisionMock;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import spoon.Launcher;
import spoon.reflect.CtModel;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class SwitchStatementDetectorTest {

    private static final String FILES_DIR = "/switch-statement/";

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void simpleDetection() throws IOException {
        RevisionMock revision = new RevisionMock(folder);
        revision.addFile(Paths.get("switch-statement", "A.java"));

        Launcher launcher = new Launcher();
        launcher.addInputResource(folder.getRoot().getAbsolutePath());
        CtModel model = launcher.buildModel();

        SwitchStatementDetector switchStatementDetector =
                new SwitchStatementDetector(revision);
        switchStatementDetector.scan(model);
        assertThat(switchStatementDetector.getCodeSmells()).isNotEmpty();
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
