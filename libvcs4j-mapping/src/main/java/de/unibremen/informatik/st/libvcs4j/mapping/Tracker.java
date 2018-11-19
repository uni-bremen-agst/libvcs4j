package de.unibremen.informatik.st.libvcs4j.mapping;

import de.unibremen.informatik.st.libvcs4j.Validate;

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
		final List<Mappable<T>> mappedTo = result.getTo();

		if (mappables.isEmpty()) {
			mappedTo.forEach(to -> {
				final Entity<T> entity = new Entity<>(to);
				final Lifespan<T> lifespan = new Lifespan<>(entity);
				localMappables.put(to, lifespan);
				lifespans.add(lifespan);

			});
		} else {
			mappedTo.stream()
					.filter(mappable -> mappables.containsKey(
							result.getPredecessor(mappable).get()))
					.forEach(to -> {
						final Mappable<T> predecessor = result.getPredecessor(to).get();
						final Entity<T> entity = mappables.get(predecessor).getLast();
						final Entity<T> successor =
								new Entity<>(to,
										result.getOrdinal(),
										entity.getNumChanges() + 1);
						final Lifespan<T> updated = mappables.get(predecessor).add(successor);
						localMappables.put(to, updated);
						if (!lifespans.contains(updated)) {
							lifespans.add(updated);
						}

					});
		}


		final List<Mappable<T>> startingLifespans = result.getStartingLifespans();
		startingLifespans.forEach(mappable -> {
			final Entity<T> entity = new Entity<>(mappable);
			final Lifespan<T> startingLifespan = new Lifespan<>(entity);
			lifespans.add(startingLifespan);
			localMappables.put(mappable, startingLifespan);
		});

		mappables = localMappables;

	}
}
