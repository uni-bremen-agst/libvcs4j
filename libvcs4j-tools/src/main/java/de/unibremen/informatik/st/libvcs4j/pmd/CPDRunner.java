package de.unibremen.informatik.st.libvcs4j.pmd;

import de.unibremen.informatik.st.libvcs4j.Revision;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.pmd.cpd.CPD;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Allows to configure and run CPD on {@link Revision} instances.
 */
@Slf4j
public class CPDRunner {

    /**
     * Minimum Tokens that have to match for duplicate to be reported
     */
    @Getter
    private int minimumTokens;

    /**
     * Creates a new CPD runner.
     */
    public CPDRunner(final int pMinimumTokens){
        minimumTokens = pMinimumTokens;
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
    public CPDDetectionResult analyze(@NonNull final Revision revision)
            throws IOException {

        final List<String> args = new ArrayList<>();
        // language
        args.add("--language");
        args.add("java");
        // tokens
        args.add("--minimum-tokens");
        args.add(String.valueOf(minimumTokens));
        // input
        args.add("--files");
        args.add(revision.getOutput().toString());
        // format
        args.add("--format");
        args.add("xml");
        // encoding
        args.add("--encoding");
        args.add("utf-8");
        // Skip files that can't be tokenized instead of throwing Exceptions
        args.add("--skip-lexical-errors");
        // Ignore Identifiers, Literals and Annotations to detect Type 2 Clones
        args.add("--ignore-identifiers");
        args.add("--ignore-literals");
        args.add("--ignore-annotations");


        // Temporarily redirect stdout to a string.
        final PrintStream stdout = System.out;
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        final PrintStream ps = new PrintStream(bos);
        System.setOut(ps);
        try {
            System.setProperty("net.sourceforge.pmd.cli.noExit","true");
            CPD.main(args.toArray(String[]::new));
            // According to PMD the resulting xml is UTF-8 encoded.
            final String output = bos.toString(StandardCharsets.UTF_8.name());

            // Parse output
            final SAXParserFactory factory = SAXParserFactory.newInstance();
            final SAXParser saxParser = factory.newSAXParser();
            final InputStream bis = new ByteArrayInputStream(
                    output.getBytes(StandardCharsets.UTF_8.name()));
            CPDSaxHandler handler = new CPDSaxHandler(revision.getFiles());
            saxParser.parse(bis, handler);

            // Result
            return new CPDDetectionResult(handler.getViolations());
        } catch (final UnsupportedOperationException | SAXException
                | ParserConfigurationException e) {
            throw new IOException(e);
        } finally {
            // Make sure to reset stdout.
            System.setOut(stdout);
        }
    }
}
