package de.unibremen.informatik.st.libvcs4j.mapping;

import de.unibremen.informatik.st.libvcs4j.FileChange;
import de.unibremen.informatik.st.libvcs4j.RevisionRange;
import de.unibremen.informatik.st.libvcs4j.VCSFile;
import de.unibremen.informatik.st.libvcs4j.Validate;

import java.io.IOException;
import java.util.Map;
import java.util.IdentityHashMap;
import java.util.Collection;
import java.util.Optional;
import java.util.List;
import java.util.ArrayList;
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
		 * Stores the ordinal of the range ({@link RevisionRange#getOrdinal()})
		 * that was passed to
		 * {@link Mapping#map(Collection, Collection, RevisionRange)}.
		 */
		private int ordinal;

		/**
		 * Stores the computed mapping result from
		 * {@link Mapping#map(Collection, Collection, RevisionRange)}.
		 */
		private Map<Mappable<T>, Mappable<T>> mapping = new IdentityHashMap<>();

		/**
		 * Stores all mappables, whose lifespans are ending. These are all the
		 * mappables from the first argument of
		 * {@link Mapping#map(Collection, Collection, RevisionRange)} that
		 * did not have a successor mappable.
		 */
		private List<Mappable<T>> endingLifespans;

		/**
		 * Stores all mappables, whose lifespans are ending. These are all the
		 * mappables from the second argument of
		 * {@link Mapping#map(Collection, Collection, RevisionRange)} that
		 * did not have a predecessor mappable.
		 */
		private List<Mappable<T>> startingLifespans;

		/**
		 * Returns the list of all mappables, whose lifespans are ending.
		 *
		 * @return
		 * 		The list of mappables, whose lifespans are ending.
		 */
		public List<Mappable<T>> getEndingLifespans() {
			return new ArrayList<>(endingLifespans);
		}

		/**
		 * Returns the list of all mappables, whose lifespans are starting.
		 *
		 * @return
		 * 		The list of mappables, whose lifespans are starting.
		 */
		public  List<Mappable<T>> getStartingLifespans() {
			return new ArrayList<>(startingLifespans);
		}

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
			return ordinal;
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
			return new ArrayList<>(mapping.keySet());
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
			return new ArrayList<>(mapping.values());
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
			return Optional.ofNullable(mapping.get(mappable));
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
			return mapping.entrySet()
					.stream()
					.filter(entry -> getSuccessor(entry.getKey()).get() == mappable)
					.map(Map.Entry::getKey)
					.findFirst();
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
		Validate.notNull(from);
		Validate.notNull(to);
		Validate.notNull(range);

		to.forEach(tMappable -> {
			tMappable.getRanges().forEach(fileRange -> {
				if (!fileRange.getFile().getRevision().getId()
						.equals(range.getRevision().getId())) {
					throw new IllegalArgumentException();
				}
			});
		});

		from.forEach(fMappable -> {
			fMappable.getRanges().forEach(fileRange -> {
				if (!fileRange.getFile().getRevision().getId().equals(
						range.getPredecessorRevision()
								.orElseThrow(IllegalArgumentException::new).getId())) {
					throw new IllegalArgumentException();
				}
			});
		});


		//find corresponding file change objects
		final Map<Mappable<T>, Map<FileChange, VCSFile.Range>> hMap = new IdentityHashMap<>();
		for (final Mappable<T> fromMappable : from) {
			final Map<FileChange, VCSFile.Range> map = new IdentityHashMap<>();
			for (final VCSFile.Range fileRange : fromMappable.getRanges()) {
				if (!fileRange.getFile().getRevision().getId()
						.equals(range.getPredecessorRevision()
								.orElseThrow(IllegalArgumentException::new).getId())) {
					throw new IllegalArgumentException();
				}
				range.getFileChanges()
						.stream()
						.filter(fileChange -> fileChange.getType()
								!= FileChange.Type.ADD)
						.forEach(fc -> {
							final VCSFile vcsFile =
									fc.getOldFile().orElseThrow(IllegalArgumentException::new);
							if (fileRange.getFile().getPath()
									.equals(vcsFile.getPath())) {
								map.put(fc, fileRange);
							}
						});
			}
			hMap.put(fromMappable, map);
		}

		//apply filechanges to range
		final Map<Mappable<T>, List<VCSFile.Range>> updatedRanges = new IdentityHashMap<>();
		for (final Map.Entry<Mappable<T>, Map<FileChange, VCSFile.Range>> entry : hMap.entrySet()) {
			final Mappable<T> mappable = entry.getKey();
			final Map<FileChange, VCSFile.Range> map = entry.getValue();
			final List<VCSFile.Range> ranges = new ArrayList<>();
			for (final Map.Entry<FileChange, VCSFile.Range> changes : map.entrySet()) {
				final FileChange fileChange = changes.getKey();
				final VCSFile.Range oldRange = changes.getValue();
				final VCSFile.Range updatedRange =
						oldRange.apply(fileChange).orElseThrow(IOException::new);
				ranges.add(updatedRange);
			}
			updatedRanges.put(mappable, ranges);
		}

		//search for compatible mappables
		final Map<Mappable<T>, Mappable<T>> mappings = new IdentityHashMap<>();
		for (final Map.Entry<Mappable<T>, List<VCSFile.Range>> entry : updatedRanges.entrySet()) {
			final Mappable<T> fromMappable = entry.getKey();
			final List<VCSFile.Range> ranges = entry.getValue();
			for (final Mappable<T> toMappable : to) {
				if (fromMappable.isCompatible(toMappable)
						&& ranges.size() == toMappable.getRanges().size()) {
					boolean compatible = false;
					for (final VCSFile.Range fromRanges : ranges) {
						compatible = false;
						for (final VCSFile.Range toRanges : toMappable.getRanges()) {
							if (fromRanges.getBegin().getOffset()
									== toRanges.getBegin().getOffset()
									&& fromRanges.getEnd().getOffset()
									== toRanges.getEnd().getOffset()) {
								compatible = true;
							}
						}
						if (!compatible) {
							break;
						}
					}
					if (compatible) {
						mappings.put(fromMappable, toMappable);
					}
				}
			}
		}

		final Result<T> result = new Result<>();
		result.startingLifespans = to.stream()
				.filter(tMappable -> !mappings.containsValue(tMappable))
				.collect(Collectors.toList());
		result.endingLifespans = from.stream()
				.filter(fMappable -> !mappings.containsKey(fMappable))
				.collect(Collectors.toList());
		result.ordinal = range.getOrdinal();
		result.mapping = mappings;

		return result;
	}
}
