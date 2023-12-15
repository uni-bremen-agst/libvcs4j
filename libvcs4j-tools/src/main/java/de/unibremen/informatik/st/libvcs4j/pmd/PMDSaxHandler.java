package de.unibremen.informatik.st.libvcs4j.pmd;

import de.unibremen.informatik.st.libvcs4j.VCSFile;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.lang.Integer.parseInt;

/**
 * Handles the XML output of PMD and stores the result in {@link #violations}.
 */
class PMDSaxHandler extends DefaultHandler {

	/**
	 * The tab size (see {@link VCSFile.Position#getTabSize()}) which is used
	 * for creating positions within the source-code files analyzed by PMD.
	 * <p>
	 * <a href="https://github.com/pmd/pmd/pull/2656">Since mid 2020</a>,
	 * {@code 1} for all languages.
	 */
	private static final int TAB_SIZE = 1;

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
	 * Used to map paths detected by PMD to {@link VCSFile} instances.
	 */
	private final Map<Path, VCSFile> path2File = new HashMap<>();

	/**
	 * Stores the detected violations.
	 */
	private final List<PMDViolation> violations = new ArrayList<>();

	/**
	 * The absolute path to the currently processed file.
	 */
	private Path path;

	/**
	 * Creates a new handler which uses the given collection of
	 * {@link VCSFile}s to link violations in files detected by PMD to their
	 * corresponding {@link VCSFile} instance. {@code null} values in
	 * {@code files} are filtered out.
	 *
	 * @param files
	 * 		The files to link violations against.
	 * @throws NullPointerException
	 * 		If {@code files} is {@code null}.
	 */
	public PMDSaxHandler(@NonNull final Collection<VCSFile> files)
			throws NullPointerException, IllegalArgumentException {
		this.files = files.stream()
				.filter(Objects::nonNull)
				.collect(Collectors.toList());
	}

	@Override
	public void startDocument() throws SAXException {
		violations.clear();
		path2File.clear();
		for (VCSFile f : files) {
			try {
				path2File.put(f.toFile().getCanonicalFile().toPath(), f);
			} catch (final IOException e) {
				log.warn("Unable to get canonical path of file '{}'. " +
						"Falling back to regular path.", f.getPath());
				path2File.put(f.toPath(), f);
			}
		}
		super.startDocument();
	}

	@Override
	public void startElement(
			final String uri, final String localName, final String qName,
			final Attributes attributes) throws SAXException {
		if (qName.equals("file")) {
			path = Paths.get(attributes.getValue("name"));
			try {
				path = path.toFile().getCanonicalFile().toPath();
			} catch (final IOException e) {
				log.warn("Skipping file whose canonical path could not be obtained ({})",
						path);
			}
		} else if (qName.equals("violation")) {
			if (path == null) {
				log.warn("Skipping violation due to missing 'file' attribute");
				return;
			}
			final VCSFile file = path2File.get(path);
			if (file == null) {
				log.warn("Skipping violation due to missing file mapping ({})",
						path);
				return;
			}

			final String rule = attributes.getValue("rule");
			if (rule == null) {
				log.warn("Skipping violation due to missing 'rule' attribute");
				return;
			}

			final String ruleSet = attributes.getValue("ruleset");
			if (ruleSet == null) {
				log.warn("Skipping violation due to missing 'ruleset' attribute");
				return;
			}

			final String bls = attributes.getValue("beginline");
			if (bls == null) {
				log.warn("Skipping violation due to missing 'beginline' attribute");
				return;
			}
			final String els = attributes.getValue("endline");
			if (els == null) {
				log.warn("Skipping violation due to missing 'endline' attribute");
				return;
			}
			final String bcs = attributes.getValue("begincolumn");
			if (bcs == null) {
				log.warn("Skipping violation due to missing 'begincolumn' attribute");
				return;
			}
			final String ecs = attributes.getValue("endcolumn");
			if (ecs == null) {
				log.warn("Skipping violation due to missing 'endcolumn' attribute");
				return;
			}
			try {
				final int bl = parseInt(bls);
				final int bc = parseInt(bcs);
				final Optional<VCSFile.Position> begin =
						file.positionOf(bl, bc, TAB_SIZE);
				if (begin.isEmpty()) {
					log.warn("Skipping violation due to not existing begin position. " +
									"file: {}, line: {}, column: {}, tab size: {}",
							file.getPath(), bl, bc, TAB_SIZE);
					return;
				}
				final int el = parseInt(els);
				final int ec = parseInt(ecs);
				final Optional<VCSFile.Position> end =
						file.positionOf(el, ec, TAB_SIZE);
				if (end.isEmpty()) {
					log.warn("Skipping violation due to not existing end position. " +
									"file: {}, line: {}, column: {}, tab size: {}",
							file.getPath(), el, ec, TAB_SIZE);
					return;
				}

				final PMDViolation v = new PMDViolation(
						begin.get().rangeTo(end.get()),
						rule, ruleSet);
				violations.add(v);
			} catch (final IOException e) {
				log.warn("Skipping violation due to an IO error while creating its range",
						e);
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
