package de.unibremen.informatik.st.libvcs4j.metrics;

import de.unibremen.informatik.st.libvcs4j.Validate;
import lombok.Getter;
import lombok.NonNull;

/**
 * Stores different complexity metrics.
 */
public class Complexity {

	/**
	 * Represents a halstead metric.
	 */
	public static class Halstead {

		/**
		 * An empty (default) halstead.
		 */
		public static final Halstead EMPTY_HALSTEAD = new Halstead(0, 0, 0, 0);

		/**
		 * Number of distinct operators.
		 */
		@Getter
		private final int numDistinctOperators;

		/**
		 * Number of distinct operands.
		 */
		@Getter
		private final int numDistinctOperands;

		/**
		 * Number of operators.
		 */
		@Getter
		private final int numOperators;

		/**
		 * Number of operands.
		 */
		@Getter
		private final int numOperands;

		/**
		 * Creates a new halstead with given values.
		 *
		 * @param n1
		 * 		The number of distinct operators.
		 * @param n2
		 * 		The number of distinct operands.
		 * @param N1
		 * 		The number of operators.
		 * @param N2
		 * 		The number of operands.
		 * @throws IllegalArgumentException
		 * 		If an of the given value is negative.
		 */
		public Halstead(final int n1, final int n2,
						final int N1, final int N2) {
			numDistinctOperators = Validate.notNegative(n1, "n1 < 0");
			numDistinctOperands = Validate.notNegative(n2, "n2 < 0");
			numOperators = Validate.isGreaterThanOrEquals(
					N1, n1, "%d < %d", N1, n1);
			numOperands = Validate.isGreaterThanOrEquals(
					N2, n2, "%d < %d", N2, n2);
		}

		/**
		 * Copy constructor.
		 *
		 * @param other
		 * 		The halstead to copy.
		 * @throws NullPointerException
		 * 		If {@code other} is {@code null}.
		 */
		public Halstead(@NonNull final Halstead other) {
			this(other.getNumDistinctOperators(),
					other.getNumDistinctOperands(),
					other.getNumOperators(),
					other.getNumOperands());
		}

		/**
		 * Returns the vocabulary.
		 * (n = numDistinctOperators + numDistinctOperands)
		 *
		 * @return
		 * 		The vocabulary.
		 */
		public int getVocabulary() {
			return getNumDistinctOperators() + getNumDistinctOperands();
		}

		/**
		 * Returns the program length.
		 * (N = numOperators + numOperands)
		 *
		 * @return
		 * 		The program length.
		 */
		public int getProgramLength() {
			return getNumOperators() + getNumOperands();
		}

		/**
		 * Returns the volume (V = N log2 n).
		 *
		 * @see #getProgramLength()
		 * @see #getVocabulary()
		 * @return
		 * 		The volume.
		 */
		public double getVolume() {
			final int N = getProgramLength();
			final int n = getVocabulary();
			return n == 0 ? 0 : N * (Math.log(n) / Math.log(2));
		}

		/**
		 * Returns the estimated difficulty.
		 * (D = numDistinctOperators/2 * numOperands/numDistinctOperands)
		 *
		 * @return
		 * 		The estimated difficulty.
		 */
		public double getDifficulty() {
			final int n1 = getNumDistinctOperators();
			final int N2 = getNumOperands();
			final int n2 = getNumDistinctOperands();
			return n2 == 0 ? 0 : (n1 / 2.0) * ((double)N2 / n2);
		}

		/**
		 * Returns the estimated program level.
		 * (L = 1/D)
		 *
		 * @see #getDifficulty()
		 * @return
		 * 		The estimated program level.
		 */
		public double getProgramLevel() {
			final double D = getDifficulty();
			return Math.abs(D) < 0.000001 ? 0 : 1/D;
		}

		/**
		 * Returns the estimated effort.
		 * (E = D * V)
		 *
		 * @see #getDifficulty()
		 * @see #getVolume()
		 * @return
		 * 		The estimated effort.
		 */
		public double getEffort() {
			return getDifficulty() * getVolume();
		}

		/**
		 * Returns the estimated time in seconds required to program.
		 * (T = E/18)
		 *
		 * @see #getEffort()
		 * @return
		 * 		The estimated time in seconds required to program.
		 */
		public double getRequiredTime() {
			return getEffort() / 18;
		}

		/**
		 * Returns the estimated number of bugs.
		 * (B = E^(2/3) / 3000)
		 *
		 * @see #getEffort()
		 * @return
		 * 		The estimated number of bugs.
		 */
		public double getBugs() {
			return Math.pow(getEffort(), 2.0/3.0) / 3000;
		}

		/**
		 * Returns the sum of this and the given halstead.
		 *
		 * @param other
		 * 		The other halstead.
		 * @return
		 * 		A new instance containing the sum of this and the given
		 * 		halstead.
		 */
		public Halstead add(final Halstead other) {
			return new Halstead(
					getNumDistinctOperators()+ other.getNumDistinctOperators(),
					getNumDistinctOperands() + other.getNumDistinctOperands(),
					getNumOperators()        + other.getNumOperators(),
					getNumOperands()         + other.getNumOperands());
		}
	}

	/**
	 * An empty (default) complexity.
	 */
	public static final Complexity EMPTY_COMPLEXITY =
			new Complexity(0, Halstead.EMPTY_HALSTEAD);

	/**
	 * McCabe complexity.
	 */
	@Getter
	private final int mcCabe;

	/**
	 * Halstead complexity.
	 */
	@Getter
	@NonNull
	private final Halstead halstead;

	/**
	 * Creates a new complexity with given values.
	 *
	 * @param pMcCabe
	 * 		The McCabe complexity.
	 * @param pHalstead
	 * 		The Halstead complexity.
	 * @throws NullPointerException
	 * 		If {@code pHalstead} is {@code null}.
	 * @throws IllegalArgumentException
	 * 		If {@code pMcCabe} is negative.
	 */
	public Complexity(final int pMcCabe, @NonNull final Halstead pHalstead)
			throws NullPointerException, IllegalArgumentException {
		Validate.isTrue(pMcCabe >= 0, "McCabe < 0");
		mcCabe = pMcCabe;
		halstead = pHalstead;
	}

	/**
	 * Copy constructor.
	 *
	 * @param other
	 * 		The complexity to copy.
	 * @throws NullPointerException
	 * 		If {@code other} is {@code null}.
	 */
	public Complexity(final Complexity other) {
		this(other.getMcCabe(), other.getHalstead());
	}

	/**
	 * Returns the sum of this and the given other.
	 *
	 * @param other
	 * 		The other other.
	 * @return
	 * 		A new instance containing the sum of this and the given other.
	 */
	public Complexity add(final Complexity other) {
		return new Complexity(
				getMcCabe() + other.getMcCabe(),
				getHalstead().add(other.getHalstead()));
	}
}
