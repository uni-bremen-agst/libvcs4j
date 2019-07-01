package de.unibremen.informatik.st.libvcs4j.spoon.codesmell;

import de.unibremen.informatik.st.libvcs4j.Validate;
import lombok.Getter;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

import static de.unibremen.informatik.st.libvcs4j.spoon.codesmell.Thresholds.Connective.AND;
import static de.unibremen.informatik.st.libvcs4j.spoon.codesmell.Thresholds.Connective.VERUM;

/**
 * Composes zero or more {@link Threshold} instances into a single statement (a
 * tree like structure) which allows to express more complex conditions.
 *
 * For instance:
 *
 *     A, B, C, D: AND
 *        |
 *       / \
 *      E   F: OR
 *
 * is a statement expressing that A, B, C, and D must be fulfilled with B being
 * a sub-statement, expressing that either E or F must be fulfilled. This class
 * implements the {@link Predicate} interface and, accordingly, provides the
 * method {@link #test(Collection)}, which allows to check whether a collection
 * of metrics fulfills a statement. Note that {@link #test(Collection)} always
 * returns {@code true} if {@link #connective} is {@link Connective#VERUM},
 * even its parameter is {@code null} or contains {@code null} values. If
 * {@link #connective} is something else, {@link #test(Collection)} always
 * returns {@code false} for {@code null} and collections containing
 * {@code null}.
 */
public class Thresholds implements Predicate<Collection<Metric>> {

	/**
	 * Defines how {@link #thresholds} and {@link #subStatements} are connected
	 * and when they are considered fulfilled.
	 */
	public enum Connective {

		/**
		 * Always true, even if {@code metrics} or {@code thresholds} is
		 * {@code null} or contains {@code null}.
		 */
		VERUM {
			@Override
			public String toString() {
				return "T";
			}

			@Override
			public boolean isTrue(final Collection<Metric> metrics,
					final Thresholds thresholds) {
				return true;
			}
		},

		/**
		 * True if and only if all thresholds and sub-statements are fulfilled.
		 */
		AND {
			@Override
			public String toString() {
				return "&";
			}

			@Override
			public boolean isTrue(@NonNull final Collection<Metric> metrics,
					@NonNull final Thresholds thresholds)
					throws NullPointerException, IllegalArgumentException {
				Validate.noNullElements(metrics);
				return thresholds.thresholds.stream().allMatch(
						t -> metrics.stream().anyMatch(t)) &&
						thresholds.subStatements.stream().allMatch(
								s -> s.connective.isTrue(metrics, s));
			}
		},

		/**
		 * True if and only if at least one threshold or sub-statement is
		 * fulfilled.
		 */
		OR {
			@Override
			public String toString() {
				return "|";
			}

			@Override
			public boolean isTrue(@NonNull final Collection<Metric> metrics,
					@NonNull final Thresholds thresholds)
					throws NullPointerException, IllegalArgumentException {
				Validate.noNullElements(metrics);
				return thresholds.thresholds.stream().anyMatch(
						t -> metrics.stream().anyMatch(t)) ||
						thresholds.subStatements.stream().anyMatch(
								s -> s.connective.isTrue(metrics, s));
			}
		},

		/**
		 * True if and only if an odd number of thresholds and sub-statements
		 * is fulfilled. It is commonly used with exactly two thresholds or
		 * sub-statements.
		 */
		XOR {
			@Override
			public String toString() {
				return "⊻";
			}

			@Override
			public boolean isTrue(@NonNull final Collection<Metric> metrics,
					@NonNull final Thresholds thresholds)
					throws NullPointerException, IllegalArgumentException {
				Validate.noNullElements(metrics);
				return (thresholds.thresholds.stream().filter(
						t -> metrics.stream().anyMatch(t)).count() +
						thresholds.subStatements.stream().filter(
								s -> s.connective.isTrue(metrics, s)
						).count()) % 2 == 1;
			}
		};

		/**
		 * Returns {@code true} if and only if all metrics in {@code metrics}
		 * fulfill their corresponding threshold in {@code thresholds} using
		 * {@link Threshold#test(Metric)}.
		 *
		 * @param metrics
		 *      The metrics to check.
		 * @param thresholds
		 *      The thresholds to check against.
		 * @return
		 *      {@code true} if and only if all metrics in {@code metrics}
		 *      fulfill their corresponding threshold in {@code thresholds}
		 *      using {@link Threshold#test(Metric)}, {@code false} otherwise.
		 * @throws NullPointerException
		 *      If any of the given arguments is {@code null} and the
		 *      connective does not support {@code null} values.
		 * @throws IllegalArgumentException
		 *      If {@code metrics} contains {@code null} and the connective
		 *      does not support {@code null} values.
		 */
		public abstract boolean isTrue(
				@NonNull final Collection<Metric> metrics,
				@NonNull final Thresholds thresholds)
				throws NullPointerException, IllegalArgumentException;
	}

	/**
	 * All thresholds of a statement. Might be empty.
	 */
	@NonNull
	private final List<Threshold> thresholds;

	/**
	 * All sub-statements of a statement. Might be empty.
	 */
	@NonNull
	private final List<Thresholds> subStatements;

	/**
	 * The connective of {@link #thresholds} and {@link #subStatements}. The
	 * default value is {@link Connective#VERUM}.
	 */
	@Getter
	@NonNull
	private final Connective connective;

	/**
	 * Creates a new statement with given thresholds, sub-statements, and
	 * connective.
	 *
	 * @param thresholds
	 * 		The list of thresholds of the statement to create.
	 * @param subStatements
	 * 		The list of sub-statements of the statement to create.
	 * @param connective
	 * 		The connective of the statement to create.
	 * @throws NullPointerException
	 * 		If any of the given arguments is {@code null}.
	 * @throws IllegalArgumentException
	 * 		If any of the given arguments contains {@code null}.
	 */
	public Thresholds(@NonNull List<Threshold> thresholds,
			@NonNull List<Thresholds> subStatements,
			@NonNull Connective connective) throws NullPointerException,
			IllegalArgumentException {
		Validate.noNullElements(thresholds);
		Validate.noNullElements(subStatements);
		this.thresholds = new ArrayList<>(thresholds);
		this.subStatements = new ArrayList<>(subStatements);
		this.connective = connective;
	}

	/**
	 * Creates a new statement with given thresholds and connective. The
	 * resulting statement has no sub-statements.
	 *
	 * @param thresholds
	 * 		The list of thresholds of the statement to create.
	 * @param connective
	 * 		The connective of the statement to create.
	 * @throws NullPointerException
	 * 		If any of the given arguments is {@code null}.
	 * @throws IllegalArgumentException
	 * 		If any of the given arguments contains {@code null}.
	 */
	public Thresholds(@NonNull List<Threshold> thresholds,
			@NonNull Connective connective) throws NullPointerException,
			IllegalArgumentException {
		this(thresholds, Collections.emptyList(), connective);
	}

	/**
	 * Creates a new statement with a single threshold. {@link #connective} is
	 * set to {@link Connective#AND}, albeit this doesn't affect
	 * {@link #test(Collection)}.
	 *
	 * @param threshold
	 * 		The threshold of the statement to create.
	 * @throws NullPointerException
	 * 		If {@code threshold} is {@code null}.
	 */
	public Thresholds(@NonNull Threshold threshold)
			throws NullPointerException {
		this(Collections.singletonList(Validate.notNull(threshold)),
				Collections.emptyList(), AND);
	}

	/**
	 * Creates a new statement which is always fulfilled.
	 */
	public Thresholds() {
		this(Collections.emptyList(), Collections.emptyList(), VERUM);
	}

	@Override
	public boolean test(final Collection<Metric> metrics) {
		// VERUM is always true.
		return connective == VERUM ||
				Optional.ofNullable(metrics)
						// A collection containing null never matches.
						.filter(ms -> ms.stream().noneMatch(Objects::isNull))
						.filter(ms -> connective.isTrue(ms, this))
						.isPresent();
	}

	/**
	 * Returns a copy of the thresholds of this statement.
	 *
	 * @return
	 * 		A copy of the thresholds of this statement.
	 */
	public List<Threshold> getThresholds() {
		return new ArrayList<>(thresholds);
	}

	/**
	 * Returns a copy of the sub-statements of this statement.
	 *
	 * @return
	 * 		A copy of the sub-statements of this statement.
	 */
	public List<Thresholds> getSubStatements() {
		return new ArrayList<>(subStatements);
	}
}
