package de.unibremen.informatik.st.libvcs4j.pmd;

import de.unibremen.informatik.st.libvcs4j.VCSFile;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
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
class CPDSaxHandler extends DefaultHandler {

    /**
     * The tab size (see {@link VCSFile.Position#tabSize}) which is used to
     * create a position.
     */
    private static final int TAB_SIZE = 8;

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
    private final Map<String, VCSFile> path2File = new HashMap<>();

    /**
     * Stores the detected violations.
     */
    private final List<CPDViolation> violations = new ArrayList<>();

    /**
     * The amount of lines that are duplicated.
     */
    private String lines;

    /**
     * The amount of tokens that are duplicated.
     */
    private String tokens;

    /**
     * List of files that share the duplication.
     */
    private List<VCSFile.Range> ranges = new ArrayList<>();

    /**
     * Creates a new handler which uses the given collection of
     * {@link VCSFile}s to link violations in files detected by CPD to their
     * corresponding {@link VCSFile} instance. {@code null} values in
     * {@code files} are filtered out.
     *
     * @param files
     * 		The files to link violations against.
     * @throws NullPointerException
     * 		If {@code files} is {@code null}.
     */
    public CPDSaxHandler(@NonNull final Collection<VCSFile> files)
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
                path2File.put(f.toFile().getCanonicalPath(), f);
            } catch (final IOException e) {
                log.warn("Unable to get canonical path of file '{}'. " +
                        "Falling back to regular path.", f.getPath());
                path2File.put(f.getPath(), f);
            }
        }
        super.startDocument();
    }

    @Override
    public void startElement(
            final String uri, final String localName, final String qName,
            final Attributes attributes) throws SAXException {
        if (qName.equals("duplication")) {
            lines = attributes.getValue("lines");
            if (lines == null) {
                log.warn("Skipping violation due to missing 'lines' attribute");
                return;
            }
            tokens = attributes.getValue("tokens");
            if (tokens == null) {
                log.warn("Skipping violation due to missing 'tokens' attribute");
                return;
            }
        } else if (qName.equals("file")) {
            final String path = attributes.getValue("path");
            if (path == null) {
                log.warn("Skipping violation due to missing 'path' attribute");
                return;
            }
            final VCSFile file = path2File.get(path);
            if (file == null) {
                log.warn("Skipping violation due to missing file mapping ({})",
                        path);
                return;
            }

            final String bls = attributes.getValue("line");
            if (bls == null) {
                log.warn("Skipping violation due to missing 'line' attribute");
                return;
            }
            final String els = attributes.getValue("endline");
            if (els == null) {
                log.warn("Skipping violation due to missing 'endline' attribute");
                return;
            }
            final String bcs = attributes.getValue("column");
            if (bcs == null) {
                log.warn("Skipping violation due to missing 'column' attribute");
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
                if (!begin.isPresent()) {
                    log.warn("Skipping violation due to not existing begin position. " +
                                    "file: {}, line: {}, column: {}, tab size: {}",
                            file.getPath(), bl, bc, TAB_SIZE);
                    return;
                }
                final int el = parseInt(els);
                final int ec = parseInt(ecs);
                final Optional<VCSFile.Position> end =
                        file.positionOf(el, ec, TAB_SIZE);
                if (!end.isPresent()) {
                    log.warn("Skipping violation due to not existing end position. " +
                                    "file: {}, line: {}, column: {}, tab size: {}",
                            file.getPath(), el, ec, TAB_SIZE);
                    return;
                }

                VCSFile.Range r = new VCSFile.Range(begin.get(), end.get());
                ranges.add(r);

            } catch (final IOException e) {
                log.warn("Skipping violation due to an IO error while creating its range");
            }
        }

        super.startElement(uri, localName, qName, attributes);
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if(qName == "duplication"){
            if(ranges.size() < 2){
                log.warn("Skipping violation because there is too few ranges");
                ranges = new ArrayList<>(); //Initialize ranges for new duplication.
                return;
            }
            final CPDViolation v = new CPDViolation (ranges, parseInt(lines), parseInt(tokens));
            violations.add(v);
            ranges = new ArrayList<>(); //Initialize ranges for new duplication.
        }
    }

    /**
     * Returns a copy of the detected violations.
     *
     * @return
     * 		A copy of the detected violations.
     */
    public List<CPDViolation> getViolations() {
        return new ArrayList<>(violations);
    }
}
