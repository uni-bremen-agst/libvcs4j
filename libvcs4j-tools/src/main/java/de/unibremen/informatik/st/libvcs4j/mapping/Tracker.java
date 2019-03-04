package de.unibremen.informatik.st.libvcs4j.mapping;

import de.unibremen.informatik.st.libvcs4j.VCSFile;
import de.unibremen.informatik.st.libvcs4j.Validate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public class Tracker<T> {

	/**
	 * The lifespans managed by this tracker.
	 */
	private List<Lifespan<T>> lifespans = new ArrayList<>();

	/**
	 * Maps the "to" mappables (see {@link Mapping.Result#getTo()}) of the last
	 * call of {@link #add(Mapping.Result)} to the lifespans they were assigned
	 * to so that the next call of this method is able to add the successors of
	 * these mappables to their corresponding lifespans. In order to avoid
	 * issue due to inappropriate implementations of {@link Object#hashCode()}
	 * and {@link Object#equals(Object)} an {@link IdentityHashMap} is used.
	 */
	private Map<Mappable<T>, Lifespan<T>> mappables = new IdentityHashMap<>();

	/**
	 * Adds the given mapping result and updates the lifespans of this tracker
	 * accordingly.
	 *
	 * @param result
	 * 		The mapping result to add.
	 * @throws NullPointerException
	 * 		If {@code result} is {@code null}.
	 */
	public void add(final Mapping.Result<T> result)
			throws NullPointerException {
		Validate.notNull(result);
		final Map<Mappable<T>, Lifespan<T>> localMappables = new IdentityHashMap<>();
		final List<Mappable<T>> mappedTo = result.getWithPredecessor();

		if (mappables.isEmpty()) {
			mappedTo.forEach(to ->
					convertToEntityAndAddToMap(to,
							result.getOrdinal(),
							localMappables));
		} else {
			mappedTo.forEach(to -> {
						final Mappable<T> predecessor =
								result.getPredecessor(to).get();
						final Entity<T> last = mappables.get(predecessor).getLast();
						final int numChanges = incrementNumChanges(predecessor,
								to,
								last.getNumChanges());
						final Entity<T> successor =
								new Entity<>(to, result.getOrdinal(), numChanges);
						//just to be sure
						if (last.getOrdinal() < successor.getOrdinal()) {
							final Lifespan<T> updated =
									mappables.get(predecessor).add(successor);
							localMappables.put(to, updated);
							if (!lifespans.contains(updated)) {
								lifespans.add(updated);
							}
						}

					});
		}

		final List<Mappable<T>> startingLifespans = result.getWithoutPredecessor();
		startingLifespans.forEach(mappable -> convertToEntityAndAddToMap(
				mappable,
				result.getOrdinal(),
				localMappables));
		mappables = localMappables;
	}

	/**
	 * Converts a given mappable to an {@link Entity} and adds it the given map.
	 * A {@link Lifespan} is created aswell. This method is used as a utility
	 * method by {@link #add(Mapping.Result)}.
	 *
	 * @param mappable
	 * 		The mappable which should be converted.
	 * @param ordinal
	 * 		The ordinal of the entity to create. Corresponds to the value of
	 * 		{@link Mapping.Result#getOrdinal()}.
	 * @param map
	 * 		The map on which the new lifespan an its corresponding
	 * 		{@link Lifespan} should be put.
	 */
	private void convertToEntityAndAddToMap(final Mappable<T> mappable,
											final int ordinal,
											final Map<Mappable<T>, Lifespan<T>> map) {
		final Entity<T> entity = new Entity<>(mappable, ordinal, 1);
		final Lifespan<T> startingLifespan = new Lifespan<>(entity);
		lifespans.add(startingLifespan);
		map.put(mappable, startingLifespan);
	}

	/**
	 * Increments the given last number of changes, if there is a change present.
	 * Otherwise this method just returns {@code lastNumChanges}.
	 *
	 * @param predecessor
	 * 		The predecessor mappable.
	 * @param successor
	 *		The successor mappable.
	 * @param lastNumChanges
	 * 		The number of changes, that may be incremented.
	 * @return
	 * 		The incremented number of changes.
	 */
	private int incrementNumChanges(final Mappable<T> predecessor,
									final Mappable<T> successor,
									final int lastNumChanges) {
		for (final VCSFile.Range predRange : predecessor.getRanges()) {
			for (final VCSFile.Range succRange : successor.getRanges()) {
				try {
					if (predRange.getBegin().getOffset()
							== succRange.getBegin().getOffset()
							&& predRange.getEnd().getOffset()
							== succRange.getEnd().getOffset()
							&& !predRange.readContent().equals(
									succRange.readContent())) {
						return lastNumChanges + 1;
					}
				} catch (IOException e) {
					throw new IllegalStateException(e);
				}
			}
		}
		return lastNumChanges;
	}
}
