package de.unibremen.informatik.st.libvcs4j.iclones;

import de.unibremen.informatik.st.libvcs4j.VCSFile;
import de.unibremen.informatik.st.libvcs4j.mapping.Mappable;
import lombok.NonNull;
import lombok.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * A readonly representation of a violation detected by CPD.
 */
@Value
public final class IClonesDuplication implements Mappable<String> {

    /**
     * The ranges of this violation.
     */
    @NonNull
    private final List<VCSFile.Range> ranges;

    /**
     * List of amounts of lines that are duplicated
     */
    @NonNull
    private final List<String> lines;

    /**
     * List of amounts of tokens that the duplication shares
     */
    @NonNull
    private final List<String> tokens;

    @Override
    public List<VCSFile.Range> getRanges() {
        return new ArrayList<>(ranges);
    }

    @Override
    public Optional<String> getMetadata() {
        return Optional.of("iclones-clone");
    }
}
