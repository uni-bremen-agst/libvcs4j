package de.unibremen.informatik.st.libvcs4j.pmd;

import de.unibremen.informatik.st.libvcs4j.VCSFile;
import de.unibremen.informatik.st.libvcs4j.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.lang.Integer.parseInt;

/**
 * Handles the XML output of PMD and stores the result in {@link #violations}.
 */
class PMDSaxHandler extends DefaultHandler {

	/**
	 * The logger of this class.
	 */
	private static final Logger log =
			LoggerFactory.getLogger(PMDSaxHandler.class);

	/**
	 * The files to process.
	 */
	private final Collection<VCSFile> files;

	/**
	 * Used to map paths detected by PMD to a {@link VCSFile} instance.
	 */
	private final Map<String, VCSFile> absolutePath2File = new HashMap<>();

	/**
	 * Stores the detected violations.
	 */
	private final List<PMDViolation> violations = new ArrayList<>();

	/**
	 * The absolute path to the currently processed file.
	 */
	private String path;

	/**
	 * Creates a new handler that uses the given collection of {@link VCSFile}s
	 * to link violations in files detected by PMD to their corresponding
	 * {@link VCSFile} instance. {@code null} values in {@code pFiles} are
	 * filtered.
	 *
	 * @param pFiles
	 * 		The files to link violations against.
	 * @throws NullPointerException
	 * 		If {@code pFiles} is {@code null}.
	 */
	public PMDSaxHandler(final Collection<VCSFile> pFiles)
			throws NullPointerException, IllegalArgumentException {
		files = Collections.unmodifiableList(
				Validate.notNull(pFiles.stream()
						.filter(Objects::nonNull)
						.collect(Collectors.toList())));
	}

	@Override
	public void startDocument() throws SAXException {
		violations.clear();
		absolutePath2File.clear();
		files.forEach(f -> absolutePath2File.put(f.getPath(), f));
		super.startDocument();
	}

	@Override
	public void startElement(
			final String uri, final String localName, final String qName,
			final Attributes attributes) throws SAXException {
		if (qName.equals("file")) {
			path = attributes.getValue("name");
		} else if (qName.equals("violation") && path != null) {
			try {
				final VCSFile file = absolutePath2File.get(path);
				if (file == null) {
					log.info("Skipping violation due to missing file mapping");
					return;
				}

				final int bl = parseInt(attributes.getValue("beginline"));
				final int el = parseInt(attributes.getValue("endline"));
				final int bc = parseInt(attributes.getValue("begincolumn"));
				final int ec = parseInt(attributes.getValue("endcolumn"));

				final String rule = attributes.getValue("rule");
				Validate.validateState(rule != null);

				final String ruleSet = attributes.getValue("ruleset");
				Validate.validateState(ruleSet != null);

				final PMDViolation v = new PMDViolation(
						new VCSFile.Range(file, bl, bc, el, ec, 4),
						rule, ruleSet);
				violations.add(v);
			} catch (final RuntimeException e) {
				log.warn("Skipping violation due to missing attribute", e);
			} catch (final IOException e) {
				log.warn("Skipping violation due to an IO error while creating its range");
			}
		}

		super.startElement(uri, localName, qName, attributes);
	}

	/**
	 * Returns a copy of the detected violations.
	 *
	 * @return
	 * 		A copy of the detected violations.
	 */
	public List<PMDViolation> getViolations() {
		return new ArrayList<>(violations);
	}
}
