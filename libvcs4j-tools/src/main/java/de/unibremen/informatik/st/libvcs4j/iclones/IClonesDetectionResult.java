package de.unibremen.informatik.st.libvcs4j.iclones;

import de.unibremen.informatik.st.libvcs4j.VCSFile;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * The detection result of {@link IClonesRunner}.
 */
public class IClonesDetectionResult {

    /**
     * The detected violations.
     */
    private final List<IClonesDuplication> violations;

    /**
     * Creates a new instance with given violations.
     *
     * @param violations
     * 		The violations to store (flat copied, {@code null} values are
     * 		filtered out).
     * @throws NullPointerException
     * 		If {@code violations} is {@code null}.
     */
    public IClonesDetectionResult(@NonNull List<IClonesDuplication> violations) {
        this.violations = violations.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * Returns the violations.
     *
     * @return
     * 		A copy of the internal list.
     */
    public List<IClonesDuplication> getViolations() {
        return new ArrayList<>(violations);
    }


    /**
     * Returns all violations detected in {@code file}. Returns an empty list
     * if {@code file} is {@code null} or was not analyzed.
     *
     * @param file
     * 		The requested file.
     * @return
     * 		All violations detected in {@code file}.
     */
    public List<IClonesDuplication> violationsOf(final VCSFile file) {
        if(file == null){return new ArrayList<>();}
        List<IClonesDuplication> violationsOfFile = new ArrayList<IClonesDuplication>();
        for(IClonesDuplication v : violations){
            List<VCSFile.Range> ranges = v.getRanges();
            for(VCSFile.Range range : ranges){
                if(range.getFile().getRelativePath() == file.getRelativePath()){
                    violationsOfFile.add(v);
                    break;
                }
            }
        }
        return violationsOfFile;
    }
}
