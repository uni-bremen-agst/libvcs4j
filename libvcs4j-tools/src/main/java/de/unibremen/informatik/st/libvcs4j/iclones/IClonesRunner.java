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
import java.util.ArrayList;
import java.util.List;
import java.nio.file.Files;
import java.nio.file.Path;
//import java.net.URL;
//import java.net.URLClassLoader;
//import java.lang.reflect.Method;
//import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.TimeUnit;

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

            ProcessBuilder pb = new ProcessBuilder("java",
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
                    IClonesOutputFile.toString());

            Process proc = pb.start();
            proc.waitFor(200000, TimeUnit.valueOf("MILLISECONDS"));

            /**
            File file = new File(IClonesFilePath + "/jar/iclones.jar"); // forward slashes with java.io.File works
            URL[] urls = { file.toURI().toURL() };
            URLClassLoader loader = new URLClassLoader(urls);
            Class<?> cls = loader.loadClass("de.uni_bremen.st.iclones.IClones"); // replace the complete class name with the actual main class
            Method main = cls.getDeclaredMethod("main", String[].class); // get the main method using reflection
            String[] args = {"-informat",
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
                    IClonesOutputFile.toString()};
            Object mainObj = main.invoke(null, new Object[] {args}); // static methods are invoked with null as first argument

             **/
            proc.destroyForcibly();
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
