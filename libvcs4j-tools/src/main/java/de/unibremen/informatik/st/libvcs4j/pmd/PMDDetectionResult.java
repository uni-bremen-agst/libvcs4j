package de.unibremen.informatik.st.libvcs4j.pmd;

import de.unibremen.informatik.st.libvcs4j.VCSFile;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The detection result of {@link PMDRunner}.
 */
public class PMDDetectionResult {

	/**
	 * The detected violations.
	 */
	private final List<PMDViolation> violations;

	/**
	 * Creates a new instance with given violations.
	 *
	 * @param violations
	 * 		The violations to store (flat copied, {@code null} values are
	 * 		filtered out).
	 * @throws NullPointerException
	 * 		If {@code violations} is {@code null}.
	 */
	public PMDDetectionResult(@NonNull List<PMDViolation> violations) {
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
	public List<PMDViolation> getViolations() {
		return new ArrayList<>(violations);
	}

	/**
	 * Returns all violated rules.
	 *
	 * @return
	 * 		All violated rules.
	 */
	public Set<String> violatedRules() {
		return violations.stream()
				.map(PMDViolation::getRule)
				.collect(Collectors.toSet());
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
	public List<PMDViolation> violationsOf(final VCSFile file) {
		return file == null
				? new ArrayList<>()
				: violations.stream()
					.filter(v -> v.getRange().getFile().getRelativePath()
							.equals(file.getRelativePath()))
					.collect(Collectors.toList());
	}
}
