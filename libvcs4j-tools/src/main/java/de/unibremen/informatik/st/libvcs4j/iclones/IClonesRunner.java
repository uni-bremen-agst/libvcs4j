package de.unibremen.informatik.st.libvcs4j.iclones;

import de.unibremen.informatik.st.libvcs4j.Revision;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.xml.sax.SAXException;
import org.buildobjects.process.ProcBuilder;
import org.buildobjects.process.ProcResult;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Allows to configure and run CPD on {@link Revision} instances.
 */
@Slf4j
public class IClonesRunner {

    /**
     * Path to IClones JAR
     */
    private static String IClonesFilePath;

    /**
     * Static Initializer, used to check for IClones JAR
     */
    static {
        IClonesFilePath = System.getenv("ICLONES");
        if(IClonesFilePath != null) {
            try {
                String fileTestOutput = ProcBuilder.run("java", "-jar", IClonesFilePath + "/jar/iclones.jar");
            } catch (Exception e) {
                //throw new FileNotFoundException("Could not find iclones.jar, please make sure that you have the ICLONES System Environment Variable set correctly.");
            }

        }
    }

    /**
     * Minimum Tokens that have to match for duplicate to be reported
     */
    @Getter
    private int minimumTokens;

    /**
     * Minimum Block Size used for Near-Miss Clone merges
     */
    @Getter
    private int minimumBlock;

    /**
     * Temporary IClones Output File
     */
    private Path IClonesOutputFile;

    /**
     * Creates a new CPD runner.
     */
    public IClonesRunner(final int pMinimumTokens, final int pMinimumBlock){
        minimumTokens = pMinimumTokens;
        minimumBlock = pMinimumBlock;
    }

    /**
     * Analyzes the given revision.
     *
     * @param revision
     * 		The revision to analyze.
     * @return
     * 		The detection result.
     * @throws NullPointerException
     * 		If {@code revision} is {@code null}.
     * @throws IOException
     * 		If an error occurred while analyzing {@code revision}.
     */
    public IClonesDetectionResult analyze (@NonNull final Revision revision)
            throws IOException, InterruptedException {

        IClonesOutputFile = Files.createTempFile("libvcs4j-iclones", null)
                .toAbsolutePath();

        try {


            ProcResult result = new ProcBuilder("java")
                    .withArgs("-jar",IClonesFilePath + "/jar/iclones.jar",
                            "-informat",
                            "single",
                            "-minclone",
                            String.valueOf(minimumTokens),
                            "-minblock",
                            String.valueOf(minimumBlock),
                            "-input",
                            revision.getOutput().toString(),
                            "-outformat",
                            "xml",
                            "-output",
                            IClonesOutputFile.toString())
                    .withNoTimeout()
                    .ignoreExitStatus()
                    .run();

            System.out.println(result.getExecutionTime());

            /**
            Process proc = Runtime.exec(new String[]{"java",
                    "-jar",
                    IClonesFilePath+"/jar/iclones.jar",
                    "-informat",
                    "single",
                    "-minclone",
                    String.valueOf(minimumTokens),
                    "-minblock",
                    String.valueOf(minimumBlock),
                    "-input",
                    revision.getOutput().toString(),
                    "-outformat",
                    "xml",
                    "-output",
                    IClonesOutputFile.toString()});

            proc.waitFor();
            **/

            // Parse output
            final SAXParserFactory factory = SAXParserFactory.newInstance();
            final SAXParser saxParser = factory.newSAXParser();
            final InputStream bis = new ByteArrayInputStream(Files.readAllBytes(IClonesOutputFile));
            IClonesSaxHandler handler = new IClonesSaxHandler(revision.getFiles());
            saxParser.parse(bis, handler);

            // Result
            return new IClonesDetectionResult(handler.getViolations());
        } catch (final UnsupportedOperationException | SAXException
                | ParserConfigurationException e) {
            throw new IOException(e);
        } finally {
            Files.delete(IClonesOutputFile);
        }
    }
}
