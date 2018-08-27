package de.unibremen.informatik.st.libvcs4j.pmd;

import de.unibremen.informatik.st.libvcs4j.VCSFile;
import org.apache.commons.lang3.Validate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * The detection result of {@link PMDRunner}.
 */
public class PMDDetectionResult {

	/**
	 * The separator used in CSV exports.
	 */
	private final static String SEPARATOR = ";";

	/**
	 * The new line character used in CSV exports.
	 */
	private final static char NEW_LINE = '\n';

	/**
	 * The header of the revision column used in CSV exports.
	 */
	private final static String REVISION_HEADER = "id";

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
	 * violations of each revision are collected. Rules are sorted according to
	 * {@link String#CASE_INSENSITIVE_ORDER}.
	 *
	 * @return
	 * 		All rules that were violated.
	 */
	public SortedSet<String> getRules() {
		final SortedSet<String> rules = new TreeSet<>(
				String.CASE_INSENSITIVE_ORDER);
		revision2Violation.values().stream()
				.flatMap(Collection::stream)
				.map(PMDViolation::getRule)
				.forEach(rules::add);
		return rules;
	}

	/**
	 * Returns all violations detected in {@code pRevision}. Returns an empty
	 * list if {@code pRevision} is {@code null} or was not analyzed.
	 *
	 * @param pRevision
	 * 		The revision for which the violations are returned.
	 * @return
	 * 		All violations detected in {@code pRevision}.
	 */
	public List<PMDViolation> getViolationsOf(final String pRevision) {
		return pRevision == null
				? new ArrayList<>()
				: new ArrayList<>(revision2Violation.getOrDefault(
						pRevision, new ArrayList<>()));
	}

	/**
	 * Returns all violations triggered by {@code pRule} in {@code pRevision}.
	 * Returns an empty list if {@code pRule} or {@code pRevision} is
	 * {@code null} or if {@code pRevision} was not analyzed.
	 *
	 * @param pRevision
	 * 		The requested revision.
	 * @param pRule
	 * 		The requested rule.
	 * @return
	 * 		All violations triggered by {@code pRule} in {@code pRevision}.
	 */
	public List<PMDViolation> getViolationsOf(final String pRevision,
			final String pRule) {
		return pRule == null || pRevision == null
				? new ArrayList<>()
				: revision2Violation
						.getOrDefault(pRevision, new ArrayList<>()).stream()
						.filter(v -> v.getRule().equals(pRule))
						.collect(Collectors.toList());
	}

	/**
	 * Returns all violations detected in {@code pFile}. Returns an empty list
	 * if {@code pFile} is {@code null} or was not analyzed.
	 *
	 * @param pFile
	 * 		The file for which the violations are returned.
	 * @return
	 * 		All violations detected in {@code pFile}.
	 */
	public List<PMDViolation> getViolationsOf(final VCSFile pFile) {
		if (pFile == null) {
			return new ArrayList<>();
		} else {
			final String rev = pFile.getRevision().getId();
			return revision2Violation
					.getOrDefault(rev, new ArrayList<>()).stream()
					.filter(v -> v.getFile().getRelativePath()
							.equals(pFile.getRelativePath()))
					.collect(Collectors.toList());
		}
	}

	/**
	 * Creates a CSV export that contains the frequency of the detected
	 * violations for each revision.
	 *
	 * @return
	 * 		A CSV export that contains the frequency of the detected violations
	 * 		for each revision.
	 */
	public String toFrequencyCSV() {
		final List<String> revs = getRevisions();
		final SortedSet<String> rules = getRules();
		final StringBuilder builder = new StringBuilder();
		builder.append(REVISION_HEADER)
				.append(SEPARATOR)
				.append(String.join(SEPARATOR, rules))
				.append(NEW_LINE);
		revs.forEach(rev -> {
			builder.append(rev);
			rules.forEach(rule ->
					builder.append(SEPARATOR)
							.append(getViolationsOf(rev, rule).size()));
			builder.append(NEW_LINE);
		});
		return builder.toString();
	}
}
