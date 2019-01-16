package de.unibremen.informatik.st.libvcs4j.mapping;

import de.unibremen.informatik.st.libvcs4j.FileChange;
import de.unibremen.informatik.st.libvcs4j.LineChange;
import de.unibremen.informatik.st.libvcs4j.RevisionRange;
import de.unibremen.informatik.st.libvcs4j.VCSFile;
import de.unibremen.informatik.st.libvcs4j.Validate;

import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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
		 * Stores all mappables of the first argument of
		 * {@link Mapping#map(Collection, Collection, RevisionRange)}
		 */
		private Collection<Mappable<T>> from = new ArrayList<>();

		/**
		 * Stores all mappables of the second argument of
		 * {@link Mapping#map(Collection, Collection, RevisionRange)}
		 */
		private Collection<Mappable<T>> to = new ArrayList<>();

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
			return new ArrayList<>(from);
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
			return new ArrayList<>(to);
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
			return Optional.ofNullable(mappable).map(mapping::get);
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

		final Collection<Mappable<T>> fromFiltered =
				from.stream().filter(Objects::nonNull).collect(Collectors.toList());

		final Collection<Mappable<T>> toFiltered =
				to.stream().filter(Objects::nonNull).collect(Collectors.toList());

		toFiltered.forEach(successor -> successor.getRanges().forEach(fileRange -> {
			if (!fileRange.getFile().getRevision().getId()
					.equals(range.getRevision().getId())) {
				throw new IllegalArgumentException();
			}
		}));

		fromFiltered.forEach(predecessor -> predecessor.getRanges().forEach(fileRange -> {
			if (!fileRange.getFile().getRevision().getId().equals(
					range.getPredecessorRevision()
							.orElseThrow(IllegalArgumentException::new).getId())) {
				throw new IllegalArgumentException();
			}
		}));

		final Result<T> result = new Result<>();
		//first check for equal signatures
		result.mapping.putAll(checkSignatures(fromFiltered, toFiltered));


		//find corresponding file change objects
		final List<CorrespondingFileChanges> hMap =
				findFileChanges(fromFiltered.stream()
						.filter(predecessor -> !result.mapping.containsKey(predecessor))
						.collect(Collectors.toList()), range);

		//apply file change objects to corresponding ranges
		final Map<Mappable<T>, List<VCSFile.Range>> updatedRanges =
				applyFileChanges(hMap);

		//search for compatible mappables
		final Map<Mappable<T>, Mappable<T>> mappings =
				computeMapping(updatedRanges, toFiltered.stream()
						.filter(successor -> !result.mapping.containsValue(successor))
						.collect(Collectors.toList()));

		result.ordinal = range.getOrdinal();
		result.from = fromFiltered;
		result.to = toFiltered;
		result.mapping.putAll(mappings);

		return result;
	}

	/**
	 * Finds the corresponding {@link FileChange} objects from the given
	 * {@link RevisionRange}. This method is used as a utility
	 * method by {@link #map(Collection, Collection, RevisionRange)}.
	 *
	 * @param from
	 * 		The collection of mappables, of which the {@link FileChange} objects
	 * 		need to be found.
	 * @param range
	 * 		The range, which contains the {@link FileChange} objects.
	 * @return
	 * 		Mapping from a {@link Mappable} to a map, which contains the
	 * 		{@link VCSFile.Range} objects as values and the corresponding
	 * 		{@link FileChange} objects as keys.
	 */
	private List<CorrespondingFileChanges> findFileChanges(
			final Collection<Mappable<T>> from, final RevisionRange range) {
		final List<CorrespondingFileChanges> result = new ArrayList<>();
		for (final Mappable<T> predecessor : from) {
			final CorrespondingFileChanges correspondingFileChanges =
					new CorrespondingFileChanges();
			for (final VCSFile.Range fileRange : predecessor.getRanges()) {
				range.getFileChanges()
						.stream()
						.filter(fileChange -> fileChange.getType()
								!= FileChange.Type.ADD)
						.forEach(fc -> {
							final VCSFile vcsFile =
									fc.getOldFile().orElseThrow(IllegalArgumentException::new);
							if (fileRange.getFile().getPath()
									.equals(vcsFile.getPath())) {
								correspondingFileChanges.put(fc, fileRange);
							}
						});
			}
			correspondingFileChanges.mappable = predecessor;
			result.add(correspondingFileChanges);
		}

		return result;
	}

	/**
	 * Applies the given {@link FileChange} object to their corresponding
	 * {@link VCSFile.Range}. This method is used as a utility
	 * method by {@link #map(Collection, Collection, RevisionRange)}.
	 *
	 * @param changesToApply
	 * 		The collection of {@link CorrespondingFileChanges}. Each object
	 * 		holds a {@link Mappable} and a map of its corresponding
	 * 		{@link FileChange} objects.
	 * @throws IOException
	 * 		If updating a range ({@link VCSFile.Range#apply(FileChange)}) fails.
	 * @return
	 * 		Mapping from a {@link Mappable} to a list, which contains the
	 *		updated {@link VCSFile.Range} objects.
	 */
	private Map<Mappable<T>, List<VCSFile.Range>> applyFileChanges(
			final List<CorrespondingFileChanges> changesToApply)
			throws IOException {
		final Map<Mappable<T>, List<VCSFile.Range>> updatedRanges = new IdentityHashMap<>();

		for (final CorrespondingFileChanges correspondingFileChanges : changesToApply) {
			final Mappable<T> mappable = correspondingFileChanges.mappable;
			final Map<VCSFile.Range, FileChange> map = correspondingFileChanges.map;
			final List<VCSFile.Range> ranges = new ArrayList<>();
			for (final Map.Entry<VCSFile.Range, FileChange> changes : map.entrySet()) {
				final FileChange fileChange = changes.getValue();
				final VCSFile.Range oldRange = changes.getKey();
				Optional<VCSFile.Range> updatedRange =
						oldRange.apply(fileChange);
				if (!updatedRange.isPresent()
						&& fileChange.getType() != FileChange.Type.REMOVE) {
					try {
						updatedRange = computeNewRange(fileChange.computeDiff(),
								oldRange,
								fileChange.getNewFile().get());
					} catch (IllegalStateException e) {
						updatedRange = Optional.empty();
					}
				}

				updatedRange.ifPresent(ranges::add);
			}

			if (ranges.isEmpty()) {
				updatedRanges.put(mappable, mappable.getRanges());
			} else {
				updatedRanges.put(mappable, ranges);
			}
		}
		return updatedRanges;
	}

	/**
	 * Computes the mapping from {@link Mappable} objects. But only if they are
	 * compatible. This method is used as a utility method by
	 * {@link #map(Collection, Collection, RevisionRange)}.
	 *
	 * @param updatedRanges
	 * 		Map from {@link Mappable} objects to its updated list of
	 * 		{@link VCSFile.Range} objects.
	 * @param to
	 * 		The collection of (potential) successor mappables.
	 * @return
	 * 		Mapping from a compatible {@link Mappable} to its successor.
	 */
	private Map<Mappable<T>, Mappable<T>> computeMapping(
			final Map<Mappable<T>, List<VCSFile.Range>> updatedRanges,
			final Collection<Mappable<T>> to) {
		final Map<Mappable<T>, Mappable<T>> mappings = new IdentityHashMap<>();
		for (final Map.Entry<Mappable<T>, List<VCSFile.Range>> entry : updatedRanges.entrySet()) {
			final Mappable<T> predecessor = entry.getKey();
			final List<VCSFile.Range> ranges = entry.getValue();
			for (final Mappable<T> successor : to) {
					if (checkForCompatibility(predecessor, successor, ranges)) {
						mappings.put(predecessor, successor);
					}
				}
			}
		return mappings;
	}

	/**
	 * Checks for compatible mappables by comparing their signatures. If the
	 * signatures are equal two mappables are compatible. This method is used
	 * as a utility method by {@link #map(Collection, Collection, RevisionRange)}.
	 *
	 * @param from
	 * 		The collection of (potential) predecessor mappables.
	 * @param to
	 * 		The collection of (potential) successor mappables.
	 * @return
	 * 		Mapping from a compatible {@link Mappable} to its successor.
	 */
	private Map<Mappable<T>, Mappable<T>> checkSignatures(
			final Collection<Mappable<T>> from,
			final Collection<Mappable<T>> to) {
		final Map<Mappable<T>, Mappable<T>> result = new IdentityHashMap<>();

		from.stream()
				.filter(predecessor -> predecessor.getSignature().isPresent())
				.filter(predecessor -> !predecessor.getSignature().get().trim().isEmpty())
				.forEach(predecessor ->
						to.forEach(successor -> {
							if (predecessor.getSignature().get()
									.equals(successor.getSignature().get())) {
								result.put(predecessor, successor);
							}
						}));
		return result;
	}

	/**
	 * Computes the new {@link VCSFile.Range} in the given new file. This method
	 * is used as a utility method by {@link #applyFileChanges(List)}.
	 *
	 * @param lineChanges
	 * 		List of {@link LineChange} objects for the determination if begin
	 * 		and/or end has been deleted.
	 * @param oldRange
	 * 		The given old range.
	 * @param newFile
	 * 		The new file where the new {@link VCSFile.Range} object should be created.
	 * @return
	 * 		{@link Optional#empty()} if computing failed or a {@link Optional} of
	 * 		the newly created {@link VCSFile.Range}.
	 * @throws IOException
	 * 		If reading the content from the new file fails {@link VCSFile#readLines()}.
	 */
	private Optional<VCSFile.Range> computeNewRange(final List<LineChange> lineChanges,
													final VCSFile.Range oldRange,
													final VCSFile newFile)
			throws IOException {
		final VCSFile.Range result;
		boolean beginDeleted = false;
		boolean endDeleted = false;
		for (final LineChange lineChange : lineChanges) {
			if (oldRange.getBegin().getLine() == lineChange.getLine()
					&& lineChange.getType() == LineChange.Type.DELETE) {
				beginDeleted = true;
			}
			if (oldRange.getEnd().getLine() == lineChange.getLine()
					&& lineChange.getType() == LineChange.Type.DELETE) {
				endDeleted = true;
			}
		}
		if (beginDeleted && endDeleted) {
			if (oldRange.getBegin().getLine() == oldRange.getEnd().getLine()) {
				if (oldRange.getBegin().getLine() > newFile.readLines().size()) {
					return Optional.empty();
				}
				result = createRange(oldRange, newFile, 1, 1);
			} else if (oldRange.getEnd().getLine() - oldRange.getBegin().getLine() == 1) {
				result = createRange(oldRange, newFile, -1, -1);
			} else {
				result = createRange(oldRange, newFile, 1, -1);
			}
		} else if (!endDeleted) {
			result = createRange(oldRange, newFile, 1, 0);
		} else {
			result = createRange(oldRange, newFile, 0, -1);
		}
		return Optional.ofNullable(result);
	}

	/**
	 * Creates a new {@link VCSFile.Range} with the line from oldRange, the begin and
	 * end offset in the newFile. This method is used as a utility method by
	 * {@link #computeNewRange(List, VCSFile.Range, VCSFile)}.
	 *
	 * @param oldRange
	 * 		The given oldRange. This method needs the line number returned
	 * 		from {@link VCSFile.Position#getLine()}.
	 * @param newFile
	 * 		The new file where the new {@link VCSFile.Range} object should be created.
	 * @param beginOffset
	 * 		Offset for the beginning line.
	 * @param endOffset
	 * 		Offset for the end line.
	 * @return
	 * 		The newly created {@link VCSFile.Range} in the given new file.
	 * @throws IOException
	 * 		If an error occured while reading the file content in
	 * 		{@link VCSFile#positionOf(int, int, int)}.
	 */
	private VCSFile.Range createRange(final VCSFile.Range oldRange,
									  final VCSFile newFile,
									  final int beginOffset,
									  final int endOffset) throws IOException {
		return new VCSFile.Range(
				newFile.positionOf(oldRange.getBegin().getLine() + beginOffset,
						1,
						oldRange.getBegin().getTabSize())
						.orElseThrow(IllegalStateException::new),
				newFile.positionOf(oldRange.getEnd().getLine() + endOffset,
						1,
						oldRange.getEnd().getTabSize())
						.orElseThrow(IllegalStateException::new).endOfLine());
	}

	/**
	 * Checks two mappables for its compatibility. That is if and only if, all
	 * ranges from the predecessor mappable are equal to the ranges from the
	 * successor mappables. This method is used as a utility method by
	 * {@link #computeMapping(Map, Collection)}.
	 *
	 * @param predecessor
	 * 		The potential successor mappable.
	 * @param successor
	 * 		The potential successor mappable.
	 * @param ranges
	 * 		The updated list of {@link VCSFile.Range} objects
	 * 		from {@param predecessor}.
	 * @return
	 * 		{@code true} if {@param predecessor} and {@param successor} are
	 * 		compatible, otherwise {@code false}.
	 */
	private boolean checkForCompatibility(final Mappable<T> predecessor,
										  final Mappable<T> successor,
										  final List<VCSFile.Range> ranges) {
		boolean compatible = false;
		if (predecessor.isCompatible(successor)
				&& ranges.size() == successor.getRanges().size()) {
			for (final VCSFile.Range fromRanges : ranges) {
				compatible = false;
				for (final VCSFile.Range toRanges : successor.getRanges()) {
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
		}
		return compatible;
	}

	/**
	 * Helper class to encapsulate a relation from a mappable and its
	 * {@link FileChange} objects. Used to make reading the code easier.
	 */
	private final class CorrespondingFileChanges {
		private Mappable<T> mappable;
		private Map<VCSFile.Range, FileChange> map = new HashMap<>();

		void put(final FileChange fileChange, final VCSFile.Range range) {
			map.put(range, fileChange);
		}
	}
}
