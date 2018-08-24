package de.unibremen.informatik.st.libvcs4j.pmd;

import de.unibremen.informatik.st.libvcs4j.VCSFile;
import org.apache.commons.lang3.Validate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The detection result of {@link PMDRunner}.
 */
public class PMDDetectionResult {

	/**
	 * Stores the detection results. Maps the analyzed revisions to
	 * {@link PMDViolation}s
	 */
	private LinkedHashMap<String, List<PMDViolation>>
			revision2Violation = new LinkedHashMap<>();

	/**
	 * Similar to the {@link java.util.Map} interface, adds a mapping for the
	 * given revision and list of violations. Overrides previous mappings.
	 * Creates a deep copy of {@code pViolations} and all its elements.
	 *
	 * @param pRevision
	 * 		The revision to map.
	 * @param pViolations
	 * 		The violations detected in {@code pRevision}.
	 * @throws NullPointerException
	 * 		If any of the given arguments is {@code null}.
	 * @throws IllegalArgumentException
	 * 		If {@code pViolations} contains {@code null} values.
	 */
	public void put(final String pRevision,
			final List<PMDViolation> pViolations) throws NullPointerException,
			IllegalArgumentException {
		Validate.notNull(pRevision);
		Validate.noNullElements(pViolations);
		final List<PMDViolation> violations = new ArrayList<>();
		pViolations.forEach(v -> violations.add(new PMDViolation(v)));
		revision2Violation.put(pRevision, violations);
	}

	/**
	 * Returns a deep copy of the internal map.
	 *
	 * @return
	 * 		A deep copy of the internal map.
	 */
	public LinkedHashMap<String, List<PMDViolation>> getViolations() {
		final LinkedHashMap<String, List<PMDViolation>>
				result = new LinkedHashMap<>();
		revision2Violation.forEach((r, vs) ->
				result.put(r, new ArrayList<>(vs)));
		return result;
	}

	/**
	 * Returns the analyzed revisions.
	 *
	 * @return
	 * 		The analyzed revisions.
	 */
	public List<String> getRevisions() {
		return new ArrayList<>(revision2Violation.keySet());
	}

	/**
	 * Returns all rules that were violated. That is, the rules of all
	 * violations of each revision are collected.
	 *
	 * @return
	 * 		All rules that were violated.
	 */
	public Set<String> getRules() {
		final Set<String> rules = new HashSet<>();
		revision2Violation.values().stream()
				.flatMap(Collection::stream)
				.map(PMDViolation::getRule)
				.forEach(rules::add);
		return rules;
	}

	/**
	 * Returns all violations detected in {@code pRevision}. Returns an empty
	 * list if {@code pRevision} was not analyzed.
	 *
	 * @param pRevision
	 * 		The revision for which the violations are returned.
	 * @return
	 * 		All violations detected in {@code pRevision}.
	 * @throws NullPointerException
	 * 		If {@code pRevision} is {@code null}.
	 */
	public List<PMDViolation> getViolationsOf(final String pRevision)
			throws NullPointerException {
		Validate.notNull(pRevision);
		return new ArrayList<>(revision2Violation
				.getOrDefault(pRevision, new ArrayList<>()));
	}

	/**
	 * Returns all violations detected in {@code pFile}. Returns an empty list
	 * if {@code pFile} was not analyzed.
	 *
	 * @param pFile
	 * 		The file for which the violations are returned.
	 * @return
	 * 		All violations detected in {@code pFile}.
	 * @throws NullPointerException
	 * 		If {@code pFile} is {@code null}.
	 */
	public List<PMDViolation> getViolationsOf(final VCSFile pFile)
			throws NullPointerException {
		Validate.notNull(pFile);
		final String rev = pFile.getRevision().getId();
		return revision2Violation
				.getOrDefault(rev, new ArrayList<>()).stream()
				.filter(v -> v.getFile().getRelativePath()
						.equals(pFile.getRelativePath()))
				.collect(Collectors.toList());
	}

	/**
	 * Returns all violations triggered by {@code pRule} in {@code pRevision}.
	 * Returns an empty list if {@code pRevision} was not analyzed.
	 *
	 * @param pRevision
	 * 		The requested revision.
	 * @param pRule
	 * 		The requested rule.
	 * @return
	 * 		All violations triggered by {@code pRule} in {@code pRevision}.
	 * @throws NullPointerException
	 * 		If any of the given arguments is {@code null}.
	 */
	public List<PMDViolation> getViolationsOf(final String pRevision,
			final String pRule) throws NullPointerException {
		Validate.notNull(pRevision);
		Validate.notNull(pRule);
		return revision2Violation
				.getOrDefault(pRevision, new ArrayList<>()).stream()
				.filter(v -> v.getRule().equals(pRule))
				.collect(Collectors.toList());
	}
}
