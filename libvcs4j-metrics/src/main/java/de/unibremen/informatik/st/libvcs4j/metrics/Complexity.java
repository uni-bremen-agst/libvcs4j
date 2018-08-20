package de.unibremen.informatik.st.libvcs4j.metrics;

import org.apache.commons.lang3.Validate;

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
		private final int n1;

		/**
		 * Number of distinct operands.
		 */
		private final int n2;

		/**
		 * Number of operators.
		 */
		private final int N1;

		/**
		 * Number of operands.
		 */
		private final int N2;

		/**
		 * Creates a new halstead with given values.
		 *
		 * @param pn1
		 * 		The number of distinct operators.
		 * @param pn2
		 * 		The number of distinct operands.
		 * @param pN1
		 * 		The number of operators.
		 * @param pN2
		 * 		The number of operands.
		 * @throws IllegalArgumentException
		 * 		If an of the given value is negative.
		 */
		public Halstead(final int pn1, final int pn2,
						final int pN1, final int pN2) {
			Validate.isTrue(pn1 >= 0, "n1 < 0");
			Validate.isTrue(pn2 >= 0, "n2 < 0");
			Validate.isTrue(pN1 >= 0, "N1 < 0");
			Validate.isTrue(pN2 >= 0, "N2 < 0");
			n1 = pn1;
			n2 = pn2;
			N1 = pN1;
			N2 = pN2;
		}

		/**
		 * Copy constructor.
		 *
		 * @param pOther
		 * 		The halstead to copy.
		 * @throws NullPointerException
		 * 		If {@code pOther} is {@code null}.
		 */
		public Halstead(final Halstead pOther) {
			this(pOther.n1, pOther.n2, pOther.N1, pOther.N2);
		}

		/**
		 * Returns the number of distinct operators.
		 *
		 * @return
		 * 		The number of distinct operators.
		 */
		public int getn1() {
			return n1;
		}

		/**
		 * Returns the number of distinct operands.
		 *
		 * @return
		 * 		The number of distinct operands.
		 */
		public int getn2() {
			return n2;
		}

		/**
		 * Returns the number of operators.
		 *
		 * @return
		 * 		The number of operators.
		 */
		public int getN1() {
			return N1;
		}

		/**
		 * Returns the number of operands.
		 *
		 * @return
		 * 		The number of operands.
		 */
		public int getN2() {
			return N2;
		}

		/**
		 * Returns the vocabulary (n = n1 + n2).
		 *
		 * @return
		 * 		The vocabulary.
		 */
		public int getVocabulary() {
			return getn1() + getn2();
		}

		/**
		 * Returns the program length (N = N1 + N2).
		 *
		 * @return
		 * 		The program length.
		 */
		public int getProgramLength() {
			return getN1() + getN2();
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
		 * Returns the estimated difficulty (D = n1/2 * N2/n2).
		 *
		 * @return
		 * 		The estimated difficulty.
		 */
		public double getDifficulty() {
			final int n1 = getn1();
			final int N2 = getN2();
			final int n2 = getn2();
			return n2 == 0 ? 0 : (n1 / 2.0) * ((double)N2 / n2);
		}

		/**
		 * Returns the estimated program level (L = 1/D).
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
		 * Returns the estimated effort (E = D * V).
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
		 * Returns the estimated time in seconds required to program
		 * (T = E/18).
		 *
		 * @see #getEffort()
		 * @return
		 * 		The estimated time in seconds required to program.
		 */
		public double getRequiredTime() {
			return getEffort() / 18;
		}

		/**
		 * Returns the estimated number of bugs (B = E^(2/3) / 3000).
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
		 * @param pHalstead
		 * 		The other halstead.
		 * @return
		 * 		A new instance containing the sum of this and the given
		 * 		halstead.
		 */
		public Halstead add(final Halstead pHalstead) {
			return new Halstead(
					n1 + pHalstead.n1,
					n2 + pHalstead.n2,
					N1 + pHalstead.N1,
					N2 + pHalstead.N2);
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
	private final int mcCabe;

	/**
	 * Halstead complexity.
	 */
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
	public Complexity(final int pMcCabe, final Halstead pHalstead)
			throws NullPointerException, IllegalArgumentException {
		Validate.isTrue(pMcCabe >= 0, "McCabe < 0");
		mcCabe = pMcCabe;
		halstead = Validate.notNull(pHalstead);
	}

	/**
	 * Copy constructor.
	 *
	 * @param pOther
	 * 		The complexity to copy.
	 * @throws NullPointerException
	 * 		If {@code pOther} is {@code null}.
	 */
	public Complexity(final Complexity pOther) {
		this(pOther.mcCabe, pOther.halstead);
	}

	/**
	 * Returns the McCabe complexity.
	 *
	 * @return
	 * 		The McCabe metric.
	 */
	public int getMcCabe() {
		return mcCabe;
	}

	/**
	 * Returns the halstead complexity.
	 *
	 * @return
	 * 		The halstead complexity.
	 */
	public Halstead getHalstead() {
		return halstead;
	}

	/**
	 * Returns the sum of this and the given complexity.
	 *
	 * @param complexity
	 * 		The other complexity.
	 * @return
	 * 		A new instance containing the sum of this and the given complexity.
	 */
	public Complexity add(final Complexity complexity) {
		return new Complexity(
				mcCabe + complexity.mcCabe,
				halstead.add(complexity.halstead));
	}
}
