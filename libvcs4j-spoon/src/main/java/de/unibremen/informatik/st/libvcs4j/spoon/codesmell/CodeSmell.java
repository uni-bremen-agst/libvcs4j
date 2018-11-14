package de.unibremen.informatik.st.libvcs4j.spoon.codesmell;

import de.unibremen.informatik.st.libvcs4j.VCSFile;
import de.unibremen.informatik.st.libvcs4j.Validate;
import lombok.Getter;
import lombok.NonNull;
import lombok.Value;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * A code smell.
 */
public class CodeSmell {

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
	 * The metrics of a code smell.
	 */
	@NonNull
	private final List<Metric> metrics;

	/**
	 * The ranges of a code smell.
	 */
	@NonNull
	private final List<VCSFile.Range> ranges;

	/**
	 * Creates a new code smell with given definition, metrics, and ranges.
	 *
	 * @param definition
	 * 		The definition of the code smell to create.
	 * @param metrics
	 * 		The metrics of the code smell to create.
	 * @param ranges
	 * 		The ranges of the code smell to create.
	 * @throws NullPointerException
	 * 		If any of the given arguments is {@code null}.
	 * @throws IllegalArgumentException
	 * 		If any of the given arguments contains {@code null}, or if
	 * 		{@code metrics} does not fulfill the thresholds of
	 * 		{@code definition} according to {@link Thresholds#test(Collection)}.
	 */
	public CodeSmell(@NonNull final Definition definition,
			@NonNull final Collection<Metric> metrics,
			@NonNull final List<VCSFile.Range> ranges)
			throws NullPointerException, IllegalArgumentException {
		Validate.noNullElements(metrics);
		Validate.noNullElements(ranges);
		Validate.isTrue(definition.getThresholds().test(metrics));
		this.definition = definition;
		this.metrics = new ArrayList<>(metrics);
		this.ranges = new ArrayList<>(ranges);
	}

	/**
	 * Creates a new code smell with given definition and ranges, but without
	 * any metric.
	 *
	 * @param definition
	 * 		The definition of the code smell to create.
	 * @param ranges
	 * 		The ranges of the code smell to create.
	 * @throws NullPointerException
	 * 		If any of the given arguments is {@code null}.
	 * @throws IllegalArgumentException
	 * 		If any of the given arguments contains {@code null}.
	 */
	public CodeSmell(@NonNull final Definition definition,
			@NonNull final List<VCSFile.Range> ranges)
			throws NullPointerException, IllegalArgumentException {
		this(definition, Collections.emptyList(), ranges);
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
}
