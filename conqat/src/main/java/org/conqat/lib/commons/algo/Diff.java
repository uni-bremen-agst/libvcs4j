/*-------------------------------------------------------------------------+
|                                                                          |
| Copyright 2005-2011 The ConQAT Project                                   |
|                                                                          |
| Licensed under the Apache License, Version 2.0 (the "License");          |
| you may not use this file except in compliance with the License.         |
| You may obtain a copy of the License at                                  |
|                                                                          |
|    http://www.apache.org/licenses/LICENSE-2.0                            |
|                                                                          |
| Unless required by applicable law or agreed to in writing, software      |
| distributed under the License is distributed on an "AS IS" BASIS,        |
| WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. |
| See the License for the specific language governing permissions and      |
| limitations under the License.                                           |
+-------------------------------------------------------------------------*/
package org.conqat.lib.commons.algo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.conqat.lib.commons.assertion.CCSMPre;
import org.conqat.lib.commons.equals.DefaultEquator;
import org.conqat.lib.commons.equals.IEquator;
import org.conqat.lib.commons.string.StringUtils;

/**
 * Implementation of the diff algorithm described in: E.W. Myers: "An O(ND)
 * Difference Algorithm and Its Variations".
 * <p>
 * Let N be the sum of the concatenated input strings and D the size of the
 * delta (i.e. the number of changes required to transform one string into the
 * other). Then the time complexity is O(ND) and the space complexity is O(D^2).
 * 
 * @param <T>
 *            The type of objects for which the diff is constructed.
 * 
 * @author $Author: pfaller $
 * @version $Rev: 50735 $
 * @ConQAT.Rating YELLOW Hash: 32047BB74B87D92D16F59293BD8FA63B
 */
public class Diff<T> {

	/** The first list of objects. */
	private final List<T> a;

	/** The second list of objects. */
	private final List<T> b;

	/** Equator used for comparing elements. */
	private final IEquator<? super T> equator;

	/** Length of {@link #a}. */
	private int n;

	/** Length of {@link #b}. */
	private int m;

	/** The maximal possible difference between {@link #a} and {@link #b}. */
	private final int max;

	/**
	 * Maximal size of the delta produced. If the "real" delta would be larger,
	 * a truncated delta will be created.
	 */
	private final int maxDeltaSize;

	/**
	 * The array for storing the positions on each diagonal. This is an
	 * "unrolled" version compared to the original paper, i.e. we create a new
	 * array for each iteration of the main loop.
	 */
	private final int[][] v;

	/**
	 * This array stores from where we came during the
	 * {@link #calculateDeltaSize()} method. Its structure is the same as
	 * {@link #v}.
	 */
	private final boolean[][] from;

	/**
	 * Hidden constructor. Use one of the {@link #computeDelta(List, List)} or
	 * {@link #computeDelta(Object[], Object[])} methods instead.
	 */
	private Diff(List<T> a, List<T> b, int maxDeltaSize,
			IEquator<? super T> equator) {
		this.a = a;
		this.b = b;
		this.maxDeltaSize = maxDeltaSize;
		this.equator = equator;

		n = a.size();
		m = b.size();
		max = n + m;
		v = new int[max + 1][];
		from = new boolean[max + 1][];
	}

	/** Performs the actual computations. */
	private Delta<T> computeDelta() {
		return constructDelta(calculateDeltaSize());
	}

	/** Constructs the actual delta. */
	private Delta<T> constructDelta(int size) {
		int d = size;
		int k = -size;
		while (v[size][size + k] < n || v[size][d + k] - k < m) {
			++k;
		}

		Delta<T> delta = new Delta<T>(size, n, m);

		int difference = n - m;
		while (d > 0) {
			if (from[d][d + k]) {
				++k;
			} else {
				--k;
			}
			--d;

			int x = v[d][d + k];
			int y = x - k;

			int newDifference = x - y;
			if (newDifference > difference || x >= n) {
				delta.position[d] = y + 1;
				delta.t[d] = b.get(y);
			} else {
				delta.position[d] = -x - 1;
				delta.t[d] = a.get(x);
			}
			difference = newDifference;
		}
		return delta;
	}

	/**
	 * Calculates the size of the delta (i.e. the number of additions and
	 * deletions. Additionally the {@link #v} and {@link #from} arrays are
	 * filled.
	 */
	private int calculateDeltaSize() {
		int size = -1;
		for (int d = 0; size < 0 && d <= max; ++d) {
			v[d] = new int[2 * d + 1];
			from[d] = new boolean[2 * d + 1];

			int bestSum = -1;
			for (int k = -d; k <= d; k += 2) {
				int x = 0;
				if (d > 0) {
					if (k == -d
							|| k != d
							&& v[d - 1][d - 1 + k - 1] < v[d - 1][d - 1 + k + 1]) {
						x = v[d - 1][d - 1 + k + 1];
						from[d][d + k] = true;
					} else {
						x = v[d - 1][d - 1 + k - 1] + 1;
						from[d][d + k] = false;
					}
				}
				int y = x - k;
				while (x < n && y < m && equator.equals(a.get(x), b.get(y))) {
					++x;
					++y;
				}
				v[d][d + k] = x;
				if (d >= maxDeltaSize && x <= n && y <= m && x + y > bestSum) {
					bestSum = x + y;

					// truncate strings
					n = Math.min(x, n);
					m = Math.min(y, m);
				}
				if (x >= n && y >= m) {
					size = d;
				}
			}
		}
		return size;
	}

	/**
	 * Applies the diff algorithm on the supplied arrays and returns the delta
	 * between them.
	 * 
	 * @param a
	 *            the first "word", i.e., array of objects to produce a delta
	 *            for.
	 * @param b
	 *            the second "word", i.e., array of objects to produce a delta
	 *            for.
	 * @return a delta containing the differences between a and b.
	 */
	public static <T> Delta<T> computeDelta(T[] a, T[] b) {
		return computeDelta(Arrays.asList(a), Arrays.asList(b));
	}

	/**
	 * Applies the diff algorithm on the supplied arrays and returns the delta
	 * between them.
	 * 
	 * @param a
	 *            the first "word", i.e., array of objects to produce a delta
	 *            for.
	 * @param b
	 *            the second "word", i.e., array of objects to produce a delta
	 *            for.
	 * @param maxDeltaSize
	 *            the maximal size of the delta produced. As the running size
	 *            depends linearly on this size and the space required depends
	 *            quadratically on it, limiting this value can reduce
	 *            calculation overhead at the risk of receiving
	 *            partial/incomplete deltas.
	 * @return a delta containing the differences between a and b.
	 */
	public static <T> Delta<T> computeDelta(T[] a, T[] b, int maxDeltaSize) {
		return computeDelta(Arrays.asList(a), Arrays.asList(b), maxDeltaSize);
	}

	/**
	 * Applies the diff algorithm on the supplied arrays and returns the delta
	 * between them.
	 * 
	 * @param a
	 *            the first "word", i.e., array of objects to produce a delta
	 *            for.
	 * @param b
	 *            the second "word", i.e., array of objects to produce a delta
	 *            for.
	 * @param equator
	 *            an object that can check whether two elements are equal.
	 * 
	 * @return a delta containing the differences between a and b.
	 */
	public static <T> Delta<T> computeDelta(T[] a, T[] b,
			IEquator<? super T> equator) {
		return computeDelta(Arrays.asList(a), Arrays.asList(b), equator);
	}

	/**
	 * Applies the diff algorithm on the supplied lists and returns the delta
	 * between them.
	 * 
	 * @param a
	 *            the first "word", i.e., list of objects to produce a delta
	 *            for.
	 * @param b
	 *            the second "word", i.e., list of objects to produce a delta
	 *            for.
	 * @return a delta containing the differences between a and b.
	 */
	public static <T> Delta<T> computeDelta(List<T> a, List<T> b) {
		return computeDelta(a, b, DefaultEquator.INSTANCE);
	}

	/**
	 * Applies the diff algorithm on the supplied lists and returns the delta
	 * between them.
	 * 
	 * @param a
	 *            the first "word", i.e., list of objects to produce a delta
	 *            for.
	 * @param b
	 *            the second "word", i.e., list of objects to produce a delta
	 *            for.
	 * @param equator
	 *            an object that can check whether two elements are equal.
	 * 
	 * @return a delta containing the differences between a and b.
	 */
	public static <T> Delta<T> computeDelta(List<T> a, List<T> b,
			IEquator<? super T> equator) {
		return computeDelta(a, b, Integer.MAX_VALUE, equator);
	}

	/**
	 * Applies the diff algorithm on the supplied lists and returns the delta
	 * between them.
	 * 
	 * @param a
	 *            the first "word", i.e., list of objects to produce a delta
	 *            for.
	 * @param b
	 *            the second "word", i.e., list of objects to produce a delta
	 *            for.
	 * @param maxDeltaSize
	 *            the maximal size of the delta produced. As the running size
	 *            depends linearly on this size and the space required depends
	 *            quadratically on it, limiting this value can reduce
	 *            calculation overhead at the risk of receiving
	 *            partial/incomplete deltas.
	 * 
	 * @return a delta containing the differences between a and b.
	 */
	public static <T> Delta<T> computeDelta(List<T> a, List<T> b,
			int maxDeltaSize) {
		return computeDelta(a, b, maxDeltaSize, DefaultEquator.INSTANCE);
	}

	/**
	 * Applies the diff algorithm on the supplied lists and returns the delta
	 * between them.
	 * 
	 * @param a
	 *            the first "word", i.e., list of objects to produce a delta
	 *            for.
	 * @param b
	 *            the second "word", i.e., list of objects to produce a delta
	 *            for.
	 * @param maxDeltaSize
	 *            the maximal size of the delta produced. As the running size
	 *            depends linearly on this size and the space required depends
	 *            quadratically on it, limiting this value can reduce
	 *            calculation overhead at the risk of receiving
	 *            partial/incomplete deltas.
	 * @param equator
	 *            an object that can check whether two elements are equal.
	 * 
	 * @return a delta containing the differences between a and b.
	 */
	public static <T> Delta<T> computeDelta(List<T> a, List<T> b,
			int maxDeltaSize, IEquator<? super T> equator) {
		return new Diff<T>(a, b, maxDeltaSize, equator).computeDelta();
	}

	/**
	 * Objects of this class describe the additions and deletions used to
	 * transform between two words.
	 */
	public static class Delta<T> {

		/** The size of the first word. */
		private final int n;

		/** The size of the second word. */
		private final int m;

		/**
		 * This array stores the position at which a string is changed. If it is
		 * positive, it indicates an addition (i.e. the position is for the
		 * second string). Otherwise it is a deletion (i.e. the (negated)
		 * position is for the first string). To allow storing a sign for
		 * position 0, all positions are incremented before (so this has to be
		 * compensated for).
		 */
		private final int[] position;

		/**
		 * This array stores the characters which are added or deleted
		 * (interpretation depends on {@link #position}).
		 */
		private final T[] t;

		/** Create new delta of given size. */
		@SuppressWarnings("unchecked")
		private Delta(int size, int n, int m) {
			this.n = n;
			this.m = m;
			position = new int[size];
			t = (T[]) new Object[size];
		}

		/**
		 * Returns the size of the delta, i.e. the number of additions and
		 * deletions.
		 */
		public int getSize() {
			return position.length;
		}

		/** Returns the size of the first word the delta was created for. */
		public int getN() {
			return n;
		}

		/** Returns the size of the second word the delta was created for. */
		public int getM() {
			return m;
		}

		/** Returns the i-th element stored as addition or deletion. */
		public T getT(int i) {
			return t[i];
		}

		/**
		 * Returns the i-th element of the change positions. If it is positive,
		 * it indicates an addition (i.e. the position is for the second
		 * string). Otherwise it is a deletion (i.e. the (negated) position is
		 * for the first string). To allow storing a sign for position 0, all
		 * positions are incremented before (so this has to be compensated for).
		 */
		public int getPosition(int i) {
			return position[i];
		}

		/**
		 * Applies the forward patch, i.e. if the first string is inserted, then
		 * the second string is returned. The input word must be of length n,
		 * the output word will be of length m.
		 */
		public List<T> forwardPatch(List<T> a) {
			CCSMPre.isTrue(a.size() == n, "Input word must be of size " + n);
			return doPatch(a, new ArrayList<T>(m), 1);
		}

		/**
		 * Applies the backward patch, i.e. if the second string is inserted,
		 * then the first string is returned. The input word must be of length
		 * m, the output word will be of length n.
		 */
		public List<T> backwardPatch(List<T> b) {
			CCSMPre.isTrue(b.size() == m, "Input word must be of size " + m);
			return doPatch(b, new ArrayList<T>(n), -1);
		}

		/**
		 * Performs the patching from a to b put pre-multiplying the positions
		 * with the given factor.
		 */
		private List<T> doPatch(List<T> a, List<T> b, int positionFactor) {
			int posA = 0;
			int posB = 0;

			for (int j = 0; j < position.length; ++j) {
				int k = position[j] * positionFactor;
				if (k > 0) {
					// add character
					k = k - 1;
					while (posB < k) {
						b.add(a.get(posA));
						++posA;
						++posB;
					}
					b.add(t[j]);
					++posB;
				} else {
					// delete character
					k = -k - 1;
					while (posA < k) {
						b.add(a.get(posA));
						++posA;
						++posB;
					}
					++posA;
				}
			}
			while (posA < a.size()) {
				b.add(a.get(posA));
				++posA;
				++posB;
			}

			return b;
		}

		/** {@inheritDoc} */
		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < position.length; ++i) {
				sb.append(Math.abs(position[i]) - 1);
				if (position[i] > 0) {
					sb.append("+ ");
				} else {
					sb.append("- ");
				}
				sb.append(t[i] + StringUtils.CR);
			}
			return sb.toString();
		}
	}
}