package de.unibremen.informatik.st.libvcs4j;

/**
 * Represents different complexity metrics.
 */
@SuppressWarnings("unused")
public interface Complexity {

	/**
	 * An empty (default) complexity instance.
	 */
	Complexity EMPTY_COMPLEXITY = new Complexity() {
		@Override
		public Halstead getHalstead() {
			return Halstead.EMPTY_HALSTEAD;
		}

		@Override
		public int getMcCabe() {
			return 0;
		}
	};

	/**
	 * Represents a halstead metric.
	 */
	interface Halstead {

		/**
		 * An empty (default) halstead instance.
		 */
		Halstead EMPTY_HALSTEAD = new Halstead() {
			@Override
			public int getn1() {
				return 0;
			}

			@Override
			public int getn2() {
				return 0;
			}

			@Override
			public int getN1() {
				return 0;
			}

			@Override
			public int getN2() {
				return 0;
			}
		};

		/**
		 * Returns the number of distinct operators.
		 *
		 * @return
		 * 		The number of distinct operators.
		 */
		int getn1();

		/**
		 * Returns the number of distinct operands.
		 *
		 * @return
		 * 		The number of distinct operands.
		 */
		int getn2();

		/**
		 * Returns the number of operators.
		 *
		 * @return
		 * 		The number of operators.
		 */
		int getN1();

		/**
		 * Returns the number of operands.
		 *
		 * @return
		 * 		The number of operands.
		 */
		int getN2();

		/**
		 * Returns the vocabulary (n = n1 + n2).
		 *
		 * @return
		 * 		The vocabulary.
		 */
		default int getVocabulary() {
			return getn1() + getn2();
		}

		/**
		 * Returns the program length (N = N1 + N2).
		 *
		 * @return
		 * 		The program length.
		 */
		default int getProgramLength() {
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
		default double getVolume() {
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
		default double getDifficulty() {
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
		default double getProgramLevel() {
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
		default double getEffort() {
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
		default double getRequiredTime() {
			return getEffort() / 18;
		}

		/**
		 * Returns the estimated number of bugs (B = E^(2/3) / 3000).
		 *
		 * @see #getEffort()
		 * @return
		 * 		The estimated number of bugs.
		 */
		default double getBugs() {
			return Math.pow(getEffort(), 2.0/3.0) / 3000;
		}
	}

	/**
	 * Returns the halstead complexity.
	 *
	 * @return
	 * 		The halstead complexity.
	 */
	Halstead getHalstead();

	/**
	 * Returns the McCabe complexity.
	 *
	 * @return
	 * 		The McCabe metric.
	 */
	int getMcCabe();
}
