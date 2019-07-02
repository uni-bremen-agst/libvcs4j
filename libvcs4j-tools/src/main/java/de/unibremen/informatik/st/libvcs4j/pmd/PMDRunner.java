package de.unibremen.informatik.st.libvcs4j.pmd;

import de.unibremen.informatik.st.libvcs4j.Revision;
import de.unibremen.informatik.st.libvcs4j.VCSEngine;
import de.unibremen.informatik.st.libvcs4j.Validate;
import lombok.NonNull;
import net.sourceforge.pmd.PMD;
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
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
	 * Creates a new runner with given PMD rules. {@code null} values in
	 * {@code rules} are filtered out. Non-Null values are trimmed with
	 * {@link String#trim()}. If {@code rules} is empty, the ruleset
	 * "rulesets/java/basic.xml" is used as fallback.
	 *
	 * @param rules
	 * 		The PMD rules to apply.
	 * @throws NullPointerException
	 * 		If {@code rules} is {@code null}.
	 */
	public PMDRunner(@NonNull final List<String> rules)
			throws NullPointerException {
		this.rules = rules.stream()
				.filter(Objects::nonNull)
				.map(String::trim)
				.collect(Collectors.toList());
		if (this.rules.isEmpty()) {
			this.rules.add("rulesets/java/basic.xml");
		}
	}

	/**
	 * Creates a new runner with given PMD rules. {@code null} values in
	 * {@code rules} are filtered out. Non-Null values are trimmed with
	 * {@link String#trim()}. If {@code rules} is empty, the ruleset
	 * "rulesets/java/basic.xml" is used as fallback.
	 *
	 * @param rules
	 * 		The PMD rules to apply.
	 * @throws NullPointerException
	 * 		If {@code rules} is {@code null}.
	 */
	public PMDRunner(@NonNull final String... rules)
			throws NullPointerException {
		this(Arrays.asList(rules));
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
	public PMDDetectionResult run(@NonNull final Revision revision)
			throws NullPointerException, IOException {
		final PMDDetectionResult result = new PMDDetectionResult();
		result.put(revision.getId(), detect(revision));
		return result;
	}

	/**
	 * Analyzes the given VCS. If an error occurs while analyzing a revision,
	 * this revisions is skipped.
	 *
	 * @param engine
	 * 		The VCS to analyze.
	 * @return
	 * 		The detection result.
	 * @throws NullPointerException
	 * 		If {@code engine} is {@code null}.
	 */
	public PMDDetectionResult run(@NonNull final VCSEngine engine)
			throws NullPointerException {
		final PMDDetectionResult result = new PMDDetectionResult();
		engine.forEach(v -> {
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
	 * {@link #run(VCSEngine)} to run the actual detection. It may be
	 * overridden to extend the default behaviour of this class.
	 *
	 * @param revision
	 * 		The revision to analyze.
	 * @return
	 * 		List of detected violations.
	 * @throws IOException
	 * 		If an error occurred while analyzing {@code revision}.
	 */
	protected List<PMDViolation> detect(final Revision revision)
			throws IOException {
		Validate.validateState(!rules.isEmpty());

		final String[] args = {
				revision.getOutput().toString(), // input
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
			PMDSaxHandler handler = new PMDSaxHandler(revision.getFiles());
			saxParser.parse(bis, handler);

			// Result
			return handler.getViolations();
		} catch (final UnsupportedOperationException | SAXException
				| ParserConfigurationException e) {
			throw new IOException(e);
		}
	}
}
