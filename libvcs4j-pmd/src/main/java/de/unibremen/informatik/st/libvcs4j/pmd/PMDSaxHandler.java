package de.unibremen.informatik.st.libvcs4j.pmd;

import de.unibremen.informatik.st.libvcs4j.VCSFile;
import de.unibremen.informatik.st.libvcs4j.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
	 * The processed files.
	 */
	private final Map<String, VCSFile> absolutePath2File = new HashMap<>();

	/**
	 * Absolute path to the currently processed file.
	 */
	private String path;

	/**
	 * Stores the detected violations.
	 */
	private final List<PMDViolation> violations = new ArrayList<>();

	/**
	 * Creates a new handler that uses the given collection of files to link
	 * violations to {@link VCSFile}s (see {@link PMDViolation#file}).
	 * {@code null} values in {@code pFiles} are filtered.
	 *
	 * @param pFiles
	 * 		The files to link violations against.
	 * @throws NullPointerException
	 * 		If {@code pFiles} is {@code null}.
	 */
	public PMDSaxHandler(final Collection<VCSFile> pFiles)
			throws NullPointerException, IllegalArgumentException {
		pFiles.stream()
				.filter(Objects::nonNull)
				.forEach(f -> absolutePath2File.put(f.getPath(), f));
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
				Validate.validateState(file != null);

				final int bl = parseInt(attributes.getValue("beginline"));
				final int el = parseInt(attributes.getValue("endline"));
				final int bc = parseInt(attributes.getValue("begincolumn"));
				final int ec = parseInt(attributes.getValue("endcolumn"));

				final String rule = attributes.getValue("rule");
				Validate.validateState(rule != null);

				final String ruleSet = attributes.getValue("ruleset");
				Validate.validateState(ruleSet != null);

				final PMDViolation v = new PMDViolation(file,
						new VCSFile.Position(bl, bc, 4),
						new VCSFile.Position(el, ec, 4),
						rule, ruleSet);
				violations.add(v);
			} catch (final RuntimeException e) {
				log.info("Skipping violation with missing attribute", e);
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
