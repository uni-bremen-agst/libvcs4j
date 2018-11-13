package de.unibremen.informatik.st.libvcs4j.spoon.codesmell;

import de.unibremen.informatik.st.libvcs4j.Validate;

import java.util.Optional;
import java.util.function.Predicate;

import static de.unibremen.informatik.st.libvcs4j.spoon.codesmell.Threshold.Relation.relatesTo;

public class Threshold implements Predicate<Metric> {

	/**
	 * The threshold value.
	 */
	private final Metric metric;

	/**
	 * The relation that is used to compare {@link #metric} with other metrics.
	 */
	private final Relation relation;

	/**
	 * Creates a threshold with given metric and relation.
	 *
	 * @param metric
	 * 		The threshold value.
	 * @param relation
	 * 		Describes, when a metric fulfills this threshold.
	 * @throws NullPointerException
	 * 		If any of the given arguments is {@code null}.
	 */
	public Threshold(final Metric metric, final Relation relation)
			throws NullPointerException {
		this.metric = Validate.notNull(metric);
		this.relation = Validate.notNull(relation);
	}

	/**
	 * Defines, when a metric fulfills a threshold.
	 */
	public enum Relation {

		/**
		 * <
		 */
		LESS {
			@Override
			public String toString() {
				return "<";
			}

			@Override
			public boolean relatesTo(final Metric metric,
					final Metric threshold) {
				Validate.notNull(metric);
				Validate.notNull(threshold);
				return metric.getValue().compareTo(threshold.getValue()) < 0;
			}
		},

		/**
		 * <=
		 */
		LESS_EQUALS {
			@Override
			public String toString() {
				return "≤";
			}

			@Override
			public boolean relatesTo(final Metric metric,
					final Metric threshold) {
				Validate.notNull(metric);
				Validate.notNull(threshold);
				return metric.getValue().compareTo(threshold.getValue()) <= 0;
			}
		},

		/**
		 * =
		 */
		EQUALS {
			@Override
			public String toString() {
				return "=";
			}

			@Override
			public boolean relatesTo(final Metric metric,
					final Metric threshold) {
				Validate.notNull(metric);
				Validate.notNull(threshold);
				return metric.getValue().compareTo(threshold.getValue()) == 0;
			}
		},

		/**
		 * >=
		 */
		GREATER_EQUALS {
			@Override
			public String toString() {
				return "≥";
			}

			@Override
			public boolean relatesTo(final Metric metric,
					final Metric threshold) {
				Validate.notNull(metric);
				Validate.notNull(threshold);
				return metric.getValue().compareTo(threshold.getValue()) >= 0;
			}
		},

		/**
		 * >
		 */
		GREATER {
			@Override
			public String toString() {
				return ">";
			}

			@Override
			public boolean relatesTo(final Metric metric,
					final Metric threshold) {
				Validate.notNull(metric);
				Validate.notNull(threshold);
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
		 *      The threshold to check.
		 * @return
		 *      {@code true} if and only if {@code metric} relates to
		 *      {@code threshold}, {@code false} otherwise.
		 * @throws NullPointerException
		 *      If any of the given arguments is {@code null}.
		 */
		public abstract boolean relatesTo(final Metric metric,
				final Metric threshold) throws NullPointerException,
				IllegalArgumentException;

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
		public static boolean relatesTo(final Metric metric,
				final Relation relation, final Metric threshold)
				throws NullPointerException, IllegalArgumentException {
			return Validate.notNull(relation).relatesTo(metric, threshold);
		}
	}

	@Override
	public boolean test(final Metric metric) {
		return Optional.ofNullable(metric)
				.filter(m -> m.getName().equals(this.metric.getName()))
				.filter(m -> relatesTo(metric, relation, this.metric))
				.isPresent();
	}

	/**
	 * Returns the metric of this threshold.
	 *
	 * @return
	 * 		The metric of this threshold.
	 */
	public Metric getMetric() {
		return metric;
	}

	/**
	 * Returns the relation of this threshold.
	 *
	 * @return
	 * 		The relation of this threshold.
	 */
	public Relation getRelation() {
		return relation;
	}
}
