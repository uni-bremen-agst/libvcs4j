package de.unibremen.informatik.st.libvcs4j.mapping;

import de.unibremen.informatik.st.libvcs4j.FileChange;
import de.unibremen.informatik.st.libvcs4j.RevisionRange;
import de.unibremen.informatik.st.libvcs4j.VCSFile;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Provides all methods required to map {@link Mappable}s. Given two revisions
 * {@code r_i} and {@code r_i+1}, a mapping assigns to each mappable of
 * {@code r_i} none or exactly one successor mappable of {@code r_i+1}. If a
 * mappable {@code m} has a successor {@code n}, then the predecessor of
 * {@code n} is {@code m}.
 *
 * @param <T>
 *     The type of the metadata of a {@link Mappable}.
 */
public class Mapping<T> {

	/**
	 * Stores the mapping result computed by
	 * {@link Mapping#map(Collection, Collection, RevisionRange)}.
	 *
	 * @param <T>
	 *     The type of the metadata of a {@link Mappable}.
	 */
	public static class Result<T> {

		/**
		 * Returns the ordinal of the range
		 * ({@link RevisionRange#getOrdinal()}) that was passed to
		 * {@link Mapping#map(Collection, Collection, RevisionRange)}.
		 *
		 * @return
		 * 		The ordinal of the range that was passed to
		 * 		{@link Mapping#map(Collection, Collection, RevisionRange)}.
		 */
		public int getOrdinal() {
			throw new UnsupportedOperationException("Not yet implemented");
		}

		/**
		 * Returns all {@code from} mappables. That is, all mappables of the
		 * first argument of
		 * {@link Mapping#map(Collection, Collection, RevisionRange)}.
		 *
		 * @return
		 * 		All {@code from} mappables.
		 */
		public List<Mappable<T>> getFrom() {
			throw new UnsupportedOperationException("Not yet implemented");
		}

		/**
		 * Returns all {@code to} mappables. That is, all mappables of the
		 * second argument of
		 * {@link Mapping#map(Collection, Collection, RevisionRange)}.
		 *
		 * @return
		 * 		All {@code to} mappables.
		 */
		public List<Mappable<T>> getTo() {
			throw new UnsupportedOperationException("Not yet implemented");
		}

		/**
		 * Returns the successor of {@code mappable}. Returns an empty
		 * {@link Optional} if there is no mapping for {@code mappable}, or if
		 * {@code mappable == null}.
		 *
		 * @param mappable
		 * 		The mappable whose successor is requested.
		 * @return
		 * 		The successor of {@code mappable}. An empty {@link Optional}
		 * 		if there is no mapping for {@code mappable}, or if
		 * 		{@code mappable == null}.
		 */
		public Optional<Mappable<T>> getSuccessor(
				final Mappable<T> mappable) {
			throw new UnsupportedOperationException("Not yet implemented");
		}

		/**
		 * Returns the predecessor of {@code mappable}. Returns an empty
		 * {@link Optional} if there is no mappable {@code m} such that
		 * {@code getSuccessor(m).get() == mappable}, or if
		 * {@code mappable == null}.
		 *
		 * @param mappable
		 * 		The mappable whose predecessor is requested.
		 * @return
		 * 		The predecessor of {@code mappable}. An empty {@link Optional}
		 * 		if there is no mappable {@code m} such that
		 * 		{@code getSuccessor(m).get() == mappable}, or if
		 * 		{@code mappable == null}.
		 */
		public Optional<Mappable<T>> getPredecessor(
				final Mappable<T> mappable) {
			throw new UnsupportedOperationException("Not yet implemented");
		}

		/**
		 * Returns all mappables of {@link #getFrom()} with a successor in
		 * {@link #getTo()}.
		 *
		 * @return
		 * 		All mappables with a successor.
		 */
		public List<Mappable<T>> getWithMapping() {
			return getFrom().stream()
					.filter(m -> getSuccessor(m).isPresent())
					.collect(Collectors.toList());
		}

		/**
		 * Returns all mappables of {@link #getFrom()} without a successor in
		 * {@link #getTo()}.
		 *
		 * @return
		 * 		All mappables without a successor.
		 */
		public List<Mappable<T>> getWithoutMapping() {
			return getFrom().stream()
					.filter(m -> !getSuccessor(m).isPresent())
					.collect(Collectors.toList());
		}

		/**
		 * Returns all mappables of {@link #getTo()} with a predecessor in
		 * {@link #getFrom()}.
		 *
		 * @return
		 * 		All mappables with a predecessor.
		 */
		public List<Mappable<T>> getMapped() {
			return getTo().stream()
					.filter(m -> getPredecessor(m).isPresent())
					.collect(Collectors.toList());
		}

		/**
		 * Returns all mappables of {@link #getTo()} without a predecessor in
		 * {@link #getFrom()}.
		 *
		 * @return
		 * 		All mappables without a predecessor.
		 */
		public List<Mappable<T>> getUnmapped() {
			return getTo().stream()
					.filter(m -> !getPredecessor(m).isPresent())
					.collect(Collectors.toList());
		}
	}

	/**
	 * Computes the mapping between {@code from} and {@code to}. {@code null}
	 * values in {@code from} and {@code to} are filtered.
	 *
	 * @param from
	 * 		The collection of (potential) predecessor mappables.
	 * @param to
	 * 		The collection of (potential) successor mappables.
	 * @param range
	 * 		The range that is required to find matches between {@code from} and
	 * 		{@code to}.
	 * @return
	 * 		The resulting mapping.
	 * @throws NullPointerException
	 * 		If any of the given arguments is {@code null}.
	 * @throws IllegalArgumentException
	 * 		If {@code from} contains mappables whose ranges
	 * 		({@link Mappable#getRanges()}) reference a revision that doesn't
	 * 		match to the predecessor revision of {@code range}
	 * 		({@link RevisionRange#getPredecessorRevision()}), or if {@code to}
	 * 		contains mappables whose ranges reference a revision that doesn't
	 * 		match to the current revision of {@code range}
	 * 		({@link RevisionRange#getRevision()}).
	 * @throws IOException
	 * 		If updating a range ({@link VCSFile.Range#apply(FileChange)})
	 * 		fails.
	 */
	public Result<T> map(final Collection<Mappable<T>> from,
			final Collection<Mappable<T>> to, final RevisionRange range)
			throws NullPointerException, IOException {
		throw new UnsupportedOperationException("Not yet implemented");
	}
}
