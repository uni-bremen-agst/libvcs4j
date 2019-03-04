package de.unibremen.informatik.st.libvcs4j.mapping;

import de.unibremen.informatik.st.libvcs4j.FileChange;
import de.unibremen.informatik.st.libvcs4j.LineChange;
import de.unibremen.informatik.st.libvcs4j.Revision;
import de.unibremen.informatik.st.libvcs4j.RevisionRange;
import de.unibremen.informatik.st.libvcs4j.VCSFile;
import de.unibremen.informatik.st.libvcs4j.Validate;
import lombok.NonNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
	 * {@link Mapping#map(Collection, RevisionRange)}.
	 *
	 * @param <T>
	 *     The type of the metadata of a {@link Mappable}.
	 */
	public static class Result<T> {

		/**
		 * Stores the ordinal of the range ({@link RevisionRange#getOrdinal()})
		 * that was passed to {@link Mapping#map(Collection, RevisionRange)} or
		 * {@link Mapping#map(Collection, Collection, RevisionRange)}.
		 */
		private final int ordinal;

		/**
		 * Stores the computed mapping result.
		 */
		private final IdentityHashMap<Mappable<T>, Mappable<T>> mapping;

		/**
		 * Stores all from mappables.
		 */
		private final Collection<Mappable<T>> from;

		/**
		 * Stores all to mappables.
		 */
		private final Collection<Mappable<T>> to;

		private Result(final int ordinal,
				final IdentityHashMap<Mappable<T>, Mappable<T>> mapping,
				final Collection<Mappable<T>> from,
				final Collection<Mappable<T>> to) {
			Validate.noNullElements(mapping.keySet());
			Validate.noNullElements(mapping.values());
			Validate.noNullElements(from);
			Validate.noNullElements(to);
			this.ordinal = ordinal;
			this.mapping = new IdentityHashMap<>(mapping);
			this.from = new ArrayList<>(from);
			this.to = new ArrayList<>(to);
		}

		/**
		 * Returns the ordinal of the range
		 * ({@link RevisionRange#getOrdinal()}) that was passed to
		 * {@link Mapping#map(Collection, RevisionRange)} or
		 * {@link Mapping#map(Collection, Collection, RevisionRange)}.
		 *
		 * @return
		 * 		The ordinal of the range that was passed to
		 * 		{@link Mapping#map(Collection, RevisionRange)} or
		 * 		{@link Mapping#map(Collection, Collection, RevisionRange)}.
		 */
		public int getOrdinal() {
			return ordinal;
		}

		/**
		 * Returns all {@code from} mappables.
		 *
		 * @return
		 * 		All {@code from} mappables.
		 */
		public List<Mappable<T>> getFrom() {
			return new ArrayList<>(from);
		}

		/**
		 * Returns all {@code to} mappables.
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
					.filter(entry -> getSuccessor(entry.getKey())
							.orElseThrow(IllegalStateException::new)
							== mappable)
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
		public List<Mappable<T>> getWithSuccessor() {
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
		public List<Mappable<T>> getWithoutSuccessor() {
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
		public List<Mappable<T>> getWithPredecessor() {
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
		public List<Mappable<T>> getWithoutPredecessor() {
			return getTo().stream()
					.filter(m -> !getPredecessor(m).isPresent())
					.collect(Collectors.toList());
		}
	}

	/**
	 * Stores the mappables of the last call of
	 * {@link #map(Collection, RevisionRange)} and
	 * {@link #map(Collection, Collection, RevisionRange)}.
	 */
	private final List<Mappable<T>> previous;

	/**
	 * Creates a new instance with an empty list of previous mappables (see
	 * {@link #previous}).
	 */
	public Mapping() {
		this(Collections.emptyList());
	}

	/**
	 * Creates a new instance with a given collection of previous mappables
	 * (see {@link #previous}). Filters out {@code null} values.
	 *
	 * @param mappables
	 * 		The mappables which are stored in {@link #previous}.
	 * @throws NullPointerException
	 * 		If {@code mappables} is {@code null}.
	 * @throws IllegalArgumentException
	 * 		If {@code mappables} contains a mappable without a range or if
	 * 		{@code mappables} contains two mappables that refer to different
	 * 		revisions (through the ranges of a mappable).
	 */
	public Mapping(
			@NonNull final Collection<? extends Mappable<T>> mappables) {
		previous = filterOutNull(mappables);
		validateSameRevisions(previous);
	}

	/**
	 * Tries to find a mapping for all mappables in {@link #previous} (from)
	 * and {@code mappables} (to). Automatically updates {@link #previous}
	 * afterwards. That is, after calling this method {@link #previous}
	 * contains the mappables of {@code mappables}. {@code null} values in
	 * {@code mappables} are filtered out.
	 *
	 * @param mappables
	 * 		The "to" mappables.
	 * @param range
	 * 		The revision range containing the mappables of {@code mappables}.
	 * @return
	 * 		The mapping result.
	 * @throws NullPointerException
	 * 		If any of the given arguments is {@code null}.
	 * @throws IllegalArgumentException
	 * 		If {@code mappables} contains a mappable without a range, or if
	 * 		{@code mappables} contains a mappable that does not correspond to
	 * 		{@code range}.
	 * @throws IOException
	 * 		If an error occurred while applying changes (see
	 * 		{@link VCSFile.Range#apply(FileChange)}).
	 */
	public Result<T> map(
			@NonNull final Collection<? extends  Mappable<T>> mappables,
			@NonNull final RevisionRange range) throws IOException {
		final List<Mappable<T>> current = filterOutNull(mappables);
		validateSameRevisions(current);
		validateHaveRevision(current, range.getRevision());

		final IdentityHashMap<Mappable<T>, Mappable<T>> bySignature =
				mapBySignature(previous, current);

		final List<Mappable<T>> from = previous.stream()
				.filter(p -> !bySignature.containsKey(p))
				.collect(Collectors.toList());
		final List<Mappable<T>> to = current.stream()
				.filter(c -> !bySignature.containsValue(c))
				.collect(Collectors.toList());
		final IdentityHashMap<Mappable<T>, Mappable<T>> byPosition =
				mapByPosition(from, to, range);

		bySignature.putAll(byPosition);
		final Result<T> result = new Result<>(
				range.getOrdinal(), bySignature, previous, current);
		previous.clear();
		previous.addAll(current);
		return result;
	}

	/**
	 * Overrides {@link #previous} with {@code from} and delegates {@code to}
	 * and {@code range} to {@link #map(Collection, RevisionRange)}. If this
	 * method throws an exception, {@link #previous} contains its old values.
	 * {@code null} values in {@code from} and {@code to} are filtered out.
	 *
	 * @param from
	 * 		The "from" mappables.
	 * @param to
	 * 		The "to" mappables.
	 * @param range
	 * 		The revision range containing the mappables of {@code from} and
	 * 		{@code to}.
	 * @return
	 * 		The mapping result.
	 * @throws NullPointerException
	 * 		If any of the given arguments is {@code null}.
	 * @throws IllegalArgumentException
	 * 		If {@code range} has no predecessor (see
	 * 		{@link RevisionRange#getPredecessorRevision()}), if {@code from} or
	 * 		{@code to} contain a mappable without a range, or if {@code from}
	 * 		or {@code to} contain a mappable that does not correspond to
	 * 		{@code range}.
	 * @throws IOException
	 * 		If an error occurred while applying changes (see
	 * 		{@link VCSFile.Range#apply(FileChange)}).
	 */
	public Result<T> map(
			@NonNull final Collection<? extends  Mappable<T>> from,
			@NonNull final Collection<? extends  Mappable<T>> to,
			@NonNull final RevisionRange range) throws IOException {
		final List<Mappable<T>> backup = new ArrayList<>(previous);
		try {
			if (!range.getPredecessorRevision().isPresent()) {
				throw new IllegalArgumentException(
						"Revision range without predecessor");
			}
			final List<Mappable<T>> prev = filterOutNull(from);
			validateSameRevisions(prev);
			validateHaveRevision(prev, range.getPredecessorRevision().get());
			previous.clear();
			previous.addAll(prev);
			return map(to, range);
		} catch (final Exception e) {
			previous.clear();
			previous.addAll(backup);
			throw e;
		}
	}

	/////////////////////////// Filter & Validation ///////////////////////////

	private static <T> List<Mappable<T>> filterOutNull(
			@NonNull final Collection<? extends Mappable<T>> mappables) {
		return mappables.stream()
				.filter(Objects::nonNull)
				.collect(Collectors.toList());
	}

	private static void validateNumFileRanges(
			@NonNull final Collection<? extends Mappable<?>> mappables) {
		mappables.stream()
				.filter(Objects::nonNull)
				.map(Mappable::getRanges)
				.filter(List::isEmpty)
				.findAny()
				.ifPresent(m -> {
					throw new IllegalArgumentException(
							"At least one mappable has no range");
				});
	}

	private static void validateSameRevisions(
			@NonNull final Collection<? extends Mappable<?>> mappables) {
		validateNumFileRanges(mappables);
		if (mappables.size() >= 2) {
			final String revision = mappables.iterator().next().getRanges()
					.get(0).getFile().getRevision().getId();
			mappables.stream()
					.map(Mappable::getRanges)
					.flatMap(Collection::stream)
					.map(VCSFile.Range::getFile)
					.map(VCSFile::getRevision)
					.map(Revision::getId)
					.filter(id -> !revision.equals(id))
					.findAny()
					.ifPresent(id -> {
						throw new IllegalArgumentException(String.format(
								"Ambiguous revisions: %s and %s",
								revision, id));
					});
		}
	}

	private static void validateHaveRevision(
			@NonNull final Collection<? extends Mappable<?>> mappables,
			@NonNull final Revision revision) {
		if (!haveRevision(mappables, revision)) {
			throw new IllegalArgumentException(String.format(
					"Mappables don't match revision %s", revision.getId()));
		}
	}

	private static boolean haveRevision(
			@NonNull final Collection<? extends Mappable<?>> mappables,
			@NonNull final Revision revision) {
		if (!mappables.isEmpty()) {
			final String expected = revision.getId();
			return mappables.stream()
					.map(Mappable::getRanges)
					.flatMap(Collection::stream)
					.map(VCSFile.Range::getFile)
					.map(VCSFile::getRevision)
					.map(Revision::getId)
					.allMatch(expected::equals);
		}
		return true;
	}

	//////////////////////////////// Signature ////////////////////////////////

	private static <T> IdentityHashMap<Mappable<T>, Mappable<T>>
	mapBySignature(@NonNull final List<Mappable<T>> from,
				   @NonNull final List<Mappable<T>> to) {
		final IdentityHashMap<Mappable<T>, Mappable<T>> mapping =
				new IdentityHashMap<>();
		from.stream()
				.filter(fm -> fm.getSignature().isPresent())
				.forEach(fm -> {
					final String signature = fm.getSignature()
							.orElseThrow(IllegalStateException::new);
					to.stream()
							.filter(tm -> tm.isCompatible(fm))
							.filter(tm -> tm.getSignature().map(ts ->
									ts.equals(signature)).orElse(false))
							.findAny()
							.ifPresent(tm -> mapping.put(fm , tm));
				});
		return mapping;
	}

	//////////////////////////////// Position /////////////////////////////////

	private static <T> IdentityHashMap<Mappable<T>, Mappable<T>>
	mapByPosition(@NonNull final List<Mappable<T>> from,
				  @NonNull final List<Mappable<T>> to,
				  @NonNull RevisionRange range) throws IOException {
		final IdentityHashMap<Mappable<T>, Mappable<T>> fromToUpdated =
				new IdentityHashMap<>();
		from.forEach(f -> fromToUpdated.put(f, f));
		final boolean applyChanges = range.getPredecessorRevision().isPresent()
				&& haveRevision(from, range.getPredecessorRevision().get());
		if (applyChanges) {
			for (final Mappable<T> f : from) {
				fromToUpdated.put(f, applyChanges(f, range).orElse(null));
			}
		}

		final List<Mappable<T>> toWorker = new ArrayList<>(to);
		final IdentityHashMap<Mappable<T>, Mappable<T>> mapping =
				new IdentityHashMap<>();
		fromToUpdated.forEach((f, u) -> {
			if (u != null) {
				final Iterator<Mappable<T>> it = toWorker.iterator();
				while (it.hasNext()) {
					final Mappable<T> t = it.next();
					if (u.rangesMatch(t)) {
						it.remove();
						mapping.put(f, t);
						break;
					}
				}
			}
		});
		return mapping;
	}

	private static <T> Optional<Mappable<T>> applyChanges(
			@NonNull final Mappable<T> mappable,
			@NonNull final RevisionRange revRange) throws IOException {
		final Revision revision = revRange.getRevision();
		final List<VCSFile.Range> ranges = new ArrayList<>();
		for (final VCSFile.Range range : mappable.getRanges()) {
			// Never returns an addition.
			final Optional<FileChange> fileChange =
					findRelevantChange(range, revRange);

			// One of the files of range does not exist anymore. We are done.
			if (fileChange.isPresent() && fileChange.get().getType()
					== FileChange.Type.REMOVE) {
				return Optional.empty();
			}

			// File of range in current revision.
			final VCSFile file = findRelevantFile(range, revision)
					.orElseThrow(() -> new IllegalArgumentException(
							String.format(
									"Unable to find '%s' in current revision",
									range.getFile().getRelativePath())));

			if (!fileChange.isPresent()) {
				// The file was not changed.
				ranges.add(new VCSFile.Range(file,
						range.getBegin().getOffset(),
						range.getEnd().getOffset(),
						range.getBegin().getTabSize()));
			} else {
				// The file was updated or relocated.
				final Optional<VCSFile.Range> updatedRange =
						range.apply(fileChange.get());
				if (updatedRange.isPresent()) {
					ranges.add(updatedRange.get());
				} else {
					//////////////////////// Heuristic ////////////////////////
					// If applying fileChange results in an empty Optional,
					// either the begin of range or the end of range (or both)
					// do not exist anymore. Let's try to shrink our mappable
					// by one line accordingly.

					// Move begin to next line if necessary.
					final boolean beginDeleted = fileChange.get()
							.computeDiff().stream().anyMatch(lc ->
									lc.getType() == LineChange.Type.DELETE &&
									lc.getLine() == range.getBegin().getLine());
					Optional<VCSFile.Position> begin = Optional.empty();
					if (beginDeleted) {
						final Optional<VCSFile.Position> tmp =
								range.getBegin().nextLine();
						if (tmp.isPresent()) {
							begin = file.positionOf(tmp.get().getOffset(),
									range.getBegin().getTabSize());
						}
					} else {
						begin = file.positionOf(range.getBegin().getOffset(),
								range.getBegin().getTabSize());
					}

					// Move end to previous line if necessary.
					final boolean endDeleted = fileChange.get()
							.computeDiff().stream().anyMatch(lc ->
									lc.getType() == LineChange.Type.DELETE &&
									lc.getLine() == range.getEnd().getLine());
					Optional<VCSFile.Position> end = Optional.empty();
					if (endDeleted) {
						final Optional<VCSFile.Position> tmp =
								range.getEnd().previousLine();
						if (tmp.isPresent()) {
							end = file.positionOf(tmp.get().getOffset(),
									range.getEnd().getTabSize());
						}
					} else {
						end = file.positionOf(range.getEnd().getOffset(),
								range.getEnd().getTabSize());
					}

					// End must be after begin.
					if (begin.isPresent() && end.isPresent()
							&& VCSFile.Position.OFFSET_COMPARATOR.compare(
									begin.get(), end.get()) < 0)  {
						ranges.add(new VCSFile.Range(begin.get(), end.get()));
					} else {
						// Unable to find range in file. We are done.
						return Optional.empty();
					}
				}
			}
		}
		return Optional.of(() -> ranges);
	}

	private static Optional<FileChange> findRelevantChange(
			@NonNull final VCSFile.Range range,
			@NonNull final RevisionRange revRange) {
		final List<FileChange> changes = revRange.getFileChanges().stream()
				.filter(fc -> fc.getType() != FileChange.Type.ADD)
				.filter(fc -> fc.getOldFile()
						.orElseThrow(IllegalStateException::new)
						.getRelativePath()
						.equals(range.getFile().getRelativePath()))
				.collect(Collectors.toList());
		if (changes.size() > 1) {
			throw new IllegalArgumentException(
					"Unexpected number of matching file changes");
		}
		return changes.isEmpty()
				? Optional.empty()
				: Optional.of(changes.get(0));
	}

	private static Optional<VCSFile> findRelevantFile(
			@NonNull final VCSFile.Range range,
			@NonNull final Revision revision) {
		final List<VCSFile> files = revision.getFiles().stream()
				.filter(file -> file.getRelativePath()
						.equals(range.getFile().getRelativePath()))
				.collect(Collectors.toList());
		if (files.size() > 1) {
			throw new IllegalArgumentException(
					"Unexpected number of matching files");
		}
		return files.isEmpty()
				? Optional.empty()
				: Optional.of(files.get(0));
	}
}
