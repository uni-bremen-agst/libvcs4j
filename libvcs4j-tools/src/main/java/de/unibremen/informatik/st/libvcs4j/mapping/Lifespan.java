package de.unibremen.informatik.st.libvcs4j.mapping;

import de.unibremen.informatik.st.libvcs4j.Validate;

import java.util.ArrayList;
import java.util.List;

/**
 * Stores a sequence of mapped entities.
 *
 * @param <T>
 *     The type of the metadata of an {@link Entity}.
 */
public class Lifespan<T> {

	/**
	 * Stores the entities of a lifespan.
	 */
	private final List<Entity<T>> entities = new ArrayList<>();

	/**
	 * Creates a new lifespan containing a single entity.
	 *
	 * @param first
	 * 		The first entity of the lifespan to create.
	 * @throws NullPointerException
	 * 		If {@code first} is {@code null}.
	 * @throws IllegalArgumentException
	 * 		If the ordinal of {@code entity} is less than {@code 1}, or if the
	 * 		number of changes of {@code entity} is negative.
	 */
	public Lifespan(final Entity<T> first) throws NullPointerException,
			IllegalArgumentException {
		Validate.notNull(first);
		Validate.isPositive(first.getOrdinal());
		Validate.notNegative(first.getNumChanges());
		entities.add(first);
	}

	/**
	 * Adds the given entity to this lifespan.
	 *
	 * @param entity
	 * 		The entity to add.
	 * @return
	 * 		This lifespan.
	 * @throws NullPointerException
	 * 		If {@code entity} is {@code null}.
	 * @throws IllegalArgumentException
	 * 		If the ordinal of {@code entity} is less than the ordinal of the
	 * 		last entity (see {@link #getLast()}), or if the number of changes
	 * 		of {@code entity} is less than the number of changes of the last
	 * 		entity.
	 */
	public Lifespan<T> add(final Entity<T> entity) throws NullPointerException,
			IllegalArgumentException {
		Validate.notNull(entity);
		final Entity<T> last = getLast();
		Validate.isGreaterThanOrEquals(
				entity.getOrdinal(), last.getOrdinal(),
				"Unexpected ordinal. Expected: >= %d, Actual: %d",
				last.getOrdinal(), entity.getOrdinal());
		Validate.isGreaterThanOrEquals(
				entity.getNumChanges(), last.getNumChanges(),
				"Unexpected number of changes. Expected: >= %d, Actual: %d",
				last.getNumChanges(), entity.getNumChanges());
		entities.add(entity);
		return this;
	}

	/**
	 * Returns a flat copy of the entities of this lifespan.
	 *
	 * @return
	 * 		A flat copy of the entities of this lifespan.
	 */
	public List<Entity<T>> getEntities() {
		return new ArrayList<>(entities);
	}

	/**
	 * Returns the first entity of this lifespan.
	 *
	 * @return
	 * 		The first entity of this lifespan.
	 */
	public Entity<T> getFirst() {
		return entities.get(0);
	}

	/**
	 * Returns the last entity of this lifespan.
	 *
	 * @return
	 * 		The last entity of this lifespan.
	 */
	public Entity<T> getLast() {
		return entities.get(entities.size() - 1);
	}
}
