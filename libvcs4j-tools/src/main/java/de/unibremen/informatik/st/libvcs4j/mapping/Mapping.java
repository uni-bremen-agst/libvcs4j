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
		 * that was passed to {@link Mapping#map(Collection, RevisionRange)}.
		 */
		private final int ordinal;

		/**
		 * Stores the computed mapping result from
		 * {@link Mapping#map(Collection, RevisionRange)}.
		 */
		private final IdentityHashMap<Mappable<T>, Mappable<T>> mapping;

		/**
		 * Stores all mappables of the first argument of
		 * {@link Mapping#map(Collection, RevisionRange)}
		 */
		private final Collection<Mappable<T>> from;

		/**
		 * Stores all mappables of the second argument of
		 * {@link Mapping#map(Collection, RevisionRange)}
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
		 * {@link Mapping#map(Collection, RevisionRange)}.
		 *
		 * @return
		 * 		The ordinal of the range that was passed to
		 * 		{@link Mapping#map(Collection, RevisionRange)}.
		 */
		public int getOrdinal() {
			return ordinal;
		}

		/**
		 * Returns all {@code from} mappables. That is, all mappables of the
		 * first argument of {@link Mapping#map(Collection, RevisionRange)}.
		 *
		 * @return
		 * 		All {@code from} mappables.
		 */
		public List<Mappable<T>> getFrom() {
			return new ArrayList<>(from);
		}

		/**
		 * Returns all {@code to} mappables. That is, all mappables of the
		 * second argument of {@link Mapping#map(Collection, RevisionRange)}.
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

	private final List<Mappable<T>> previous;

	public Mapping() {
		this(Collections.emptyList());
	}

	public Mapping(
			@NonNull final Collection<? extends Mappable<T>> mappables) {
		previous = filterOutNull(mappables);
		validateSameRevisions(previous);
	}

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
								"Ambiguous revision: %s and %s",
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
			final String actual = mappables.iterator().next().getRanges()
					.get(0).getFile().getRevision().getId();
			return expected.equals(actual);
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
					if (rangesMatch(u, t)) {
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

	private static <T> boolean rangesMatch(
			@NonNull final Mappable<T> m1,
			@NonNull final Mappable<T> m2) {
		final List<VCSFile.Range> m1Ranges = m1.getRanges();
		final List<VCSFile.Range> m2Ranges = m2.getRanges();
		if (m1Ranges.size() != m2.getRanges().size()) {
			return false;
		}
		m1Ranges.forEach(r1 -> {
			final Iterator<VCSFile.Range> it = m2Ranges.iterator();
			while (it.hasNext()) {
				final VCSFile.Range r2 = it.next();
				// Match path.
				final String r1RelPath = r1.getFile().getRelativePath();
				final String r2RelPath = r2.getFile().getRelativePath();
				final boolean pathMatch = r1RelPath.equals(r2RelPath);
				// Match begin.
				final VCSFile.Position r1Begin = r1.getBegin();
				final VCSFile.Position r2Begin = r2.getBegin();
				final boolean beginMatch = VCSFile.Position
						.OFFSET_COMPARATOR.compare(r1Begin, r2Begin) == 0;
				// Match end.
				final VCSFile.Position r1End = r1.getEnd();
				final VCSFile.Position r2End = r2.getEnd();
				final boolean endMatch = VCSFile.Position
						.OFFSET_COMPARATOR.compare(r1End, r2End) == 0;
				// Do not reuse r2 in case of a match.
				if (pathMatch && beginMatch && endMatch) {
					it.remove();
					break;
				}
			}
		});
		return m2Ranges.isEmpty();
	}
}
