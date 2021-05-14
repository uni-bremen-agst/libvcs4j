package de.unibremen.informatik.st.libvcs4j.iclones;

import de.unibremen.informatik.st.libvcs4j.VCSFile;
import lombok.NonNull;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


import java.io.File;
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
 * Handles the XML output of IClones and stores the result in {@link #violations}.
 */
class IClonesSaxHandler extends DefaultHandler {

    /**
     * The tab size (see {@link VCSFile.Position#tabSize}) which is used to
     * create a position.
     */
    private static final int TAB_SIZE = 8;

    /**
     * The logger of this class.
     */
    private static final Logger log =
            LoggerFactory.getLogger(IClonesSaxHandler.class);

    /**
     * Basepath IClones uses
     */
    private String BasePath;

    /**
     * The files to process.
     */
    private final Collection<VCSFile> files;

    /**
     * Used to map paths detected by IClones to {@link VCSFile} instances.
     */
    private final Map<String, VCSFile> path2File = new HashMap<>();

    /**
     * Stores the detected violations.
     */
    private final List<IClonesDuplication> violations = new ArrayList<>();

    /**
     * The amount of lines that are duplicated.
     */
    private List<String> lines;

    /**
     * The amount of tokens that are duplicated.
     */
    private List<String> tokens;

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
    public IClonesSaxHandler(@NonNull final Collection<VCSFile> files)
            throws NullPointerException, IllegalArgumentException {
        this.files = files.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public void startDocument() throws SAXException {
        violations.clear();
        path2File.clear();
        ranges.clear();
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
        if(qName.equals("version")){
            BasePath = attributes.getValue("basepath");
            BasePath = BasePath.replace('/', File.separatorChar );
        }else if (qName.equals("cloneclass")) {
            lines = new ArrayList<String>();
            tokens = new ArrayList<String>();
        } else if (qName.equals("fragment")) {
            final String path = BasePath + attributes.getValue("fileid");
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

            final String bls = attributes.getValue("startline");
            if (bls == null) {
                log.warn("Skipping violation due to missing 'line' attribute");
                return;
            }
            final String els = attributes.getValue("endline");
            if (els == null) {
                log.warn("Skipping violation due to missing 'endline' attribute");
                return;
            }
            tokens.add(attributes.getValue("length"));

            try {
                final int bl = parseInt(bls);
                final Optional<VCSFile.Position> begin =
                        file.positionOf(bl, 1, TAB_SIZE);
                if (!begin.isPresent()) {
                    log.warn("Skipping violation due to not existing begin position. " +
                                    "file: {}, line: {}, column: {}, tab size: {}",
                            file.getPath(), bl, 1, TAB_SIZE);
                    return;
                }
                final int el = parseInt(els);
                final Optional<VCSFile.Position> end =
                        file.positionOf(el, 1, TAB_SIZE);
                if (!end.isPresent()) {
                    log.warn("Skipping violation due to not existing end position. " +
                                    "file: {}, line: {}, column: {}, tab size: {}",
                            file.getPath(), el, 1, TAB_SIZE);
                    return;
                }
                VCSFile.Position endPosition = end.get();
                VCSFile.Position endOfEndLine = endPosition.endOfLine();

                VCSFile.Range r = new VCSFile.Range(begin.get(), endOfEndLine);
                ranges.add(r);
                lines.add(String.valueOf((el-bl)+1));

            } catch (final IOException | IllegalStateException e) {
                log.warn("Skipping violation due to an IO error while creating its range");
            }
        }

        super.startElement(uri, localName, qName, attributes);
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if(qName == "cloneclass"){
            if(ranges.size() < 2){
                log.warn("Skipping violation because there is too few ranges");
                ranges = new ArrayList<>(); //Initialize ranges for new duplication.
                return;
            }
            final IClonesDuplication v = new IClonesDuplication (ranges, lines, tokens);
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
    public List<IClonesDuplication> getViolations() {
        return new ArrayList<>(violations);
    }
}
