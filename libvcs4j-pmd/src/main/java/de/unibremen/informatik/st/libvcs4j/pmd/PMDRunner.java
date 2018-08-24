package de.unibremen.informatik.st.libvcs4j.pmd;

import de.unibremen.informatik.st.libvcs4j.Revision;
import de.unibremen.informatik.st.libvcs4j.VCSEngine;
import net.sourceforge.pmd.PMD;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Allows to configure and run PMD on a single {@link Revision} or on all
 * revisions of a {@link VCSEngine}.
 */
public class PMDRunner {

	/**
	 * The logger of this class.
	 */
	private static final Logger log = LoggerFactory.getLogger(PMDRunner.class);

	/**
	 * The PMD rules to apply.
	 */
	private final List<String> rules;

	/**
	 * Creates a new runner with given PMD rules.
	 *
	 * @param pRules
	 * 		The PMD rules to apply.
	 */
	public PMDRunner(final List<String> pRules) {
		Validate.noNullElements(pRules);
		rules = new ArrayList<>();
		pRules.forEach(r -> rules.add(r.trim()));
	}

	/**
	 * Creates a new runner with given PMD rules.
	 *
	 * @param pRules
	 * 		The PMD rules to apply.
	 */
	public PMDRunner(final String... pRules) {
		this(Arrays.asList(Validate.noNullElements(pRules)));
	}

	/**
	 * Analyzes the given revision.
	 *
	 * @param pRevision
	 * 		The revision to analyze.
	 * @return
	 * 		The detection result.
	 * @throws IOException
	 * 		If an error occurred while analyzing {@code pRevision}.
	 */
	public PMDDetectionResult run(final Revision pRevision)
			throws IOException {
		Validate.notNull(pRevision);
		final PMDDetectionResult result = new PMDDetectionResult();
		result.put(pRevision.getId(), detect(pRevision));
		return result;
	}

	/**
	 * Analyzes the given VCS. If an error occurs while analyzing a revision,
	 * this revisions is skipped.
	 *
	 * @param pEngine
	 * 		The VCS to analyze.
	 * @return
	 * 		The detection result.
	 */
	public PMDDetectionResult run(final VCSEngine pEngine) {
		Validate.notNull(pEngine);
		final PMDDetectionResult result = new PMDDetectionResult();
		pEngine.forEach(v -> {
			final String rev = v.getRevision().getId();
			try {
				result.put(rev, detect(v.getRevision()));
			} catch (final IOException e) {
				log.info(String.format("Skipping revision '%s'", rev), e);
			}
		});
		return result;
	}

	/**
	 * This method is used by {@link #run(Revision)} and
	 * {@link #run(VCSEngine)} and may be overridden to extends the default
	 * behaviour of this class.
	 *
	 * @param pRevision
	 * 		The revision to analyze.
	 * @return
	 * 		List of detected violations.
	 * @throws IOException
	 * 		If an error occurred while analyzing {@code pRevision}.
	 */
	protected List<PMDViolation> detect(final Revision pRevision)
			throws IOException {
		final String[] args = {
				pRevision.getOutput().toString(), // input
				"xml",                            // format
				String.join(",", rules)           // rules
		};

		try {
			// Temporarily redirect stdout to a string.
			final PrintStream stdout = System.out;
			final ByteArrayOutputStream bos = new ByteArrayOutputStream();
			final PrintStream ps = new PrintStream(bos);
			System.setOut(ps);
			PMD.run(args);
			System.setOut(stdout);
			// According to PMD the resulting xml is UTF-8 encoded.
			final String output = bos.toString(StandardCharsets.UTF_8.name());

			// Parse output
			final SAXParserFactory factory = SAXParserFactory.newInstance();
			final SAXParser saxParser = factory.newSAXParser();
			final InputStream bis = new ByteArrayInputStream(
					output.getBytes(StandardCharsets.UTF_8.name()));
			PMDSaxHandler handler = new PMDSaxHandler(pRevision.getFiles());
			saxParser.parse(bis, handler);

			// Result
			return handler.getViolations();
		} catch (final UnsupportedOperationException | SAXException
				| ParserConfigurationException e) {
			throw new IOException(e);
		}
	}
}
