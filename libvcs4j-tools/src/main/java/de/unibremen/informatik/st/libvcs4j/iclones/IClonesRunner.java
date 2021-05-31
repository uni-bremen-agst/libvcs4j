package de.unibremen.informatik.st.libvcs4j.iclones;

import de.unibremen.informatik.st.libvcs4j.Revision;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.xml.sax.SAXException;
import org.buildobjects.process.ProcBuilder;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Allows to configure and run CPD on {@link Revision} instances.
 */
@Slf4j
public class IClonesRunner {

    /**
     * Boolean that represent wether an IClones Jar has been found or not.
     */
    private static boolean IClonesJarFound;

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
                String fileTestOutput = ProcBuilder.run("java", "-jar", IClonesFilePath);
                IClonesJarFound = true;
            } catch (Exception e) {
                IClonesJarFound = false;
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
    public IClonesRunner(final int pMinimumTokens, final int pMinimumBlock) throws FileNotFoundException{
        if(!IClonesJarFound){
            throw new FileNotFoundException("Could not find iclones.jar, please make sure that you have the ICLONES System Environment Variable set correctly.");
        }
        minimumTokens = pMinimumTokens;
        minimumBlock = pMinimumBlock;
    }

    /**
     * ICLones Thread
     */
    private Thread ICThread;

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
            ICThread = new IClonesThread(IClonesFilePath,
                                        revision.getOutput().toString(),
                                        IClonesOutputFile.toString(),
                                        minimumTokens,
                                        minimumBlock);


            ICThread.start();

            // Prepare to parse output, but wait for Thread before actually doing anything
            final SAXParserFactory factory = SAXParserFactory.newInstance();
            final SAXParser saxParser = factory.newSAXParser();
            IClonesSaxHandler handler = new IClonesSaxHandler(revision.getFiles());

            ICThread.join();

            final InputStream bis = new ByteArrayInputStream(Files.readAllBytes(IClonesOutputFile));
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
