package de.unibremen.informatik.st.libvcs4j.spoon.codesmell;

import de.unibremen.informatik.st.libvcs4j.VCSFile;
import de.unibremen.informatik.st.libvcs4j.Validate;
import de.unibremen.informatik.st.libvcs4j.mapping.Mappable;
import lombok.Getter;
import lombok.NonNull;
import lombok.Value;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * An unmodifiable representation of a code smell.
 */
public class CodeSmell implements Mappable<String> {

	/**
	 * Describes of what kind a code smell is.
	 */
	@Value
	public static class Definition {

		/**
		 * The name of a code smell.
		 */
		@NonNull
		private final String name;

		/**
		 * Which thresholds must be fulfilled to consider an entity as a code
		 * smell.
		 */
		@NonNull
		private final Thresholds thresholds;
	}

	/**
	 * A code smell's definition.
	 */
	@Getter
	@NonNull
	private final Definition definition;

	/**
	 * The metrics of a code smell. May contain "additional" metrics, that is,
	 * metrics that are not part of {@link #definition}.
	 */
	@NonNull
	private final List<Metric> metrics;

	/**
	 * The ranges of a code smell.
	 */
	@NonNull
	private final List<VCSFile.Range> ranges;

	/**
	 * The signature of a code smell. Allows to uniquely identify a code smell
	 * regardless of its {@link #ranges}. May be {@code null}.
	 */
	private final String signature;

	/**
	 * An optional text that describes why an element is considered a code
	 * smell (for example, if {@link #metrics} is not viable). My be
	 * {@code null}.
	 */
	private final String summary;

	/**
	 * Creates a new code smell with given definition, metrics, ranges,
	 * signature, and summary. Only {@code signature} and {@code summary} may
	 * be {@code null}.
	 *
	 * @param definition
	 * 		The definition of the code smell to create.
	 * @param metrics
	 * 		The metrics of the code smell to create.
	 * @param ranges
	 * 		The ranges of the code smell to create.
	 * @param signature
	 * 		The signature of the code smell to create. May be {@code null}.
	 * @param summary
	 * 		The summary of the code smell to create. May be {@code null}.
	 * @throws NullPointerException
	 * 		If {@code definition}, {@code metrics}, or {@code ranges} is
	 * 		{@code null}.
	 * @throws IllegalArgumentException
	 * 		If any of the given arguments contains {@code null}, or if
	 * 		{@code metrics} does not fulfill the thresholds of
	 * 		{@code definition} according to
	 * 		{@link Thresholds#test(Collection)}.
	 */
	public CodeSmell(@NonNull final Definition definition,
			@NonNull final Collection<Metric> metrics,
			@NonNull final List<VCSFile.Range> ranges,
			final String signature, final String summary)
			throws NullPointerException, IllegalArgumentException {
		Validate.noNullElements(metrics);
		Validate.noNullElements(ranges);
		Validate.isTrue(definition.getThresholds().test(metrics));
		this.definition = definition;
		this.metrics = new ArrayList<>(metrics);
		this.ranges = new ArrayList<>(ranges);
		this.signature = signature;
		this.summary = summary;
	}

	/**
	 * Creates a new code smell with given definition, ranges, signature, and
	 * summary, but without any metric. Only {@code signature} and
	 * {@code summary} may be {@code null}.
	 *
	 * @param definition
	 * 		The definition of the code smell to create.
	 * @param ranges
	 * 		The ranges of the code smell to create.
	 * @param signature
	 * 		The signature of the code smell to create. May be {@code null}.
	 * @param summary
	 * 		The summary of the code smell to create. May be {@code null}.
	 * @throws NullPointerException
	 * 		If any of the given arguments is {@code null}.
	 * @throws IllegalArgumentException
	 * 		If any of the given arguments contains {@code null}.
	 */
	public CodeSmell(@NonNull final Definition definition,
			@NonNull final List<VCSFile.Range> ranges,
			final String signature, final String summary)
			throws NullPointerException, IllegalArgumentException {
		this(definition, Collections.emptyList(), ranges, signature, summary);
	}

	/**
	 * Returns the signature of this code smell.
	 *
	 * @return
	 * 		The signature of this code smell.
	 */
	@Override
	public Optional<String> getSignature() {
		return Optional.ofNullable(signature);
	}

	/**
	 * Returns a copy of the metrics of this code smell.
	 *
	 * @return
	 * 		A copy of the metrics of this code smell.
	 */
	public List<Metric> getMetrics() {
		return new ArrayList<>(metrics);
	}

	/**
	 * Returns a copy of the ranges of this code smell.
	 *
	 * @return
	 * 		A copy of the ranges of this code smell.
	 */
	public List<VCSFile.Range> getRanges() {
		return new ArrayList<>(ranges);
	}

	/**
	 * Returns the summary of this code smell.
	 *
	 * @return
	 * 		The summary of this code smell.
	 */
	public Optional<String> getSummary() {
		return Optional.ofNullable(summary);
	}

	@Override
	public Optional<String> getMetadata() {
		return Optional.of(definition.getName());
	}
}
