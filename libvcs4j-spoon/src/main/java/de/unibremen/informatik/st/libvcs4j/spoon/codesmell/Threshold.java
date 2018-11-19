package de.unibremen.informatik.st.libvcs4j.spoon.codesmell;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

import java.util.Optional;
import java.util.function.Predicate;

import static de.unibremen.informatik.st.libvcs4j.spoon.codesmell.Threshold.Relation.relatesTo;

/**
 * An immutable class to represent a {@link Metric} related threshold.
 * Implements the {@link Predicate} interface and provides the method
 * {@link #test(Metric)}, which allows to check whether a metric fulfills a
 * threshold. Note that {@link #test(Metric)} always returns {@code false} for
 * {@code null} as {@code null} never fulfills a threshold.
 */
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class Threshold implements Predicate<Metric> {

	/**
	 * Defines, when a metric fulfills a threshold.
	 */
	public enum Relation {

		/**
		 * {@code <}
		 */
		LESS {
			@Override
			public String toString() {
				return "<";
			}

			@Override
			public boolean relatesTo(@NonNull final Metric metric,
					@NonNull final Metric threshold) {
				return metric.getValue().compareTo(threshold.getValue()) < 0;
			}
		},

		/**
		 * {@code <=}
		 */
		LESS_EQUALS {
			@Override
			public String toString() {
				return "≤";
			}

			@Override
			public boolean relatesTo(@NonNull final Metric metric,
					@NonNull final Metric threshold) {
				return metric.getValue().compareTo(threshold.getValue()) <= 0;
			}
		},

		/**
		 * {@code =}
		 */
		EQUALS {
			@Override
			public String toString() {
				return "=";
			}

			@Override
			public boolean relatesTo(@NonNull final Metric metric,
					@NonNull final Metric threshold) {
				return metric.getValue().compareTo(threshold.getValue()) == 0;
			}
		},

		/**
		 * {@code >=}
		 */
		GREATER_EQUALS {
			@Override
			public String toString() {
				return "≥";
			}

			@Override
			public boolean relatesTo(@NonNull final Metric metric,
					@NonNull final Metric threshold) {
				return metric.getValue().compareTo(threshold.getValue()) >= 0;
			}
		},

		/**
		 * {@code >}
		 */
		GREATER {
			@Override
			public String toString() {
				return ">";
			}

			@Override
			public boolean relatesTo(@NonNull final Metric metric,
					@NonNull final Metric threshold) {
				return metric.getValue().compareTo(threshold.getValue()) > 0;
			}
		};

		/**
		 * Returns {@code true} if and only if {@code metric} relates to
		 * {@code threshold} with respect to {@link #relation}. This method
		 * does not check the names of {@code metric} and {@code threshold}
		 * (see {@link Metric#getName()}), but only their values (see
		 * {@link Metric#getValue()}). This allows users of this class to add
		 * custom rules regarding the names of metrics and thresholds. A
		 * default implementation considering the names is given by
		 * {@link Threshold#test(Metric)}.
		 *
		 * @param metric
		 *      The metric to check.
		 * @param threshold
		 *      The threshold to check against.
		 * @return
		 *      {@code true} if and only if {@code metric} relates to
		 *      {@code threshold}, {@code false} otherwise.
		 * @throws NullPointerException
		 *      If any of the given arguments is {@code null}.
		 */
		public abstract boolean relatesTo(final Metric metric,
				final Metric threshold) throws NullPointerException;

		/**
		 * Convenience method for {@code relation.relatesTo(m, t);}, allowing
		 * one to produce more readable code:
		 *
		 * 		{@code relatesTo(m, LESS, t);}
		 *
		 * @param metric
		 *      The metric to check.
		 * @param relation
		 *      The relation to use.
		 * @param threshold
		 *      The threshold to check.
		 * @return
		 *      {@code true} if and only if {@code metric} relates to
		 *      {@code threshold} with respect to {@code relation},
		 *      {@code false} otherwise.
		 * @throws NullPointerException
		 *      If any of the given arguments is {@code null}.
		 */
		public static boolean relatesTo(@NonNull final Metric metric,
				@NonNull final Relation relation,
				@NonNull final Metric threshold) throws NullPointerException {
			return relation.relatesTo(metric, threshold);
		}
	}

	/**
	 * The threshold value.
	 */
	@Getter
	@NonNull
	private final Metric metric;

	/**
	 * The relation that is used to compare {@link #metric} with other metrics.
	 */
	@Getter
	@NonNull
	private final Relation relation;

	@Override
	public boolean test(final Metric metric) {
		return Optional.ofNullable(metric)
				.filter(m -> m.getName().equals(this.metric.getName()))
				.filter(m -> relatesTo(metric, relation, this.metric))
				.isPresent();
	}
}
