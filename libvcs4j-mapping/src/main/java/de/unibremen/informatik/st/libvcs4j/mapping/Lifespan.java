package de.unibremen.informatik.st.libvcs4j.mapping;

import de.unibremen.informatik.st.libvcs4j.Validate;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Stores a sequence of mapped entities.
 *
 * @param <T>
 *     The type of the metadata of an {@link Entity}.
 */
public class Lifespan<T> implements Iterable<Entity<T>> {

	/**
	 * Stores the entities of a lifespan.
	 */
	private final List<Entity<T>> entities = new ArrayList<>();

	/**
	 * Creates a new lifespan containing a single entity.
	 *
	 * @param first
	 * 		The first entity of the lifespan to create.
	 */
	public Lifespan(final Entity<T> first) {
		add(first);
	}

	/**
	 * Adds the given entity to this lifespan.
	 *
	 * @param entity
	 * 		The entity to add.
	 * @return
	 * 		This lifespan.
	 */
	public Lifespan<T> add(final Entity<T> entity) {
		Validate.notNull(entity);
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

	@Override
	public Iterator<Entity<T>> iterator() {
		return getEntities().iterator();
	}
}
