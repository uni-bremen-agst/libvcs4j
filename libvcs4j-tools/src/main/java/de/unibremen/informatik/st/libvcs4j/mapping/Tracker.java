package de.unibremen.informatik.st.libvcs4j.mapping;

import de.unibremen.informatik.st.libvcs4j.VCSFile;
import de.unibremen.informatik.st.libvcs4j.Validate;
import lombok.Data;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Tracker<T> {

	/**
	 * The logger of this class.
	 */
	private static final Logger log = LoggerFactory.getLogger(Tracker.class);

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

	public List<Lifespan<T>> getLifespans() {
		return new ArrayList<>(lifespans);
	}

	/**
	 * Adds the given mapping result and updates the lifespans of this tracker
	 * accordingly.
	 *
	 * @param result
	 * 		The mapping result to add.
	 * @throws NullPointerException
	 * 		If {@code result} is {@code null}.
	 */
	public void add(@NonNull final Mapping.Result<T> result)
			throws NullPointerException, IOException {
		final List<MappableAdd> toAdd = new ArrayList<>();
		final List<MappableUpdate> toUpdate = new ArrayList<>();
		result.getTo().forEach(to -> {
			final Optional<Mappable<T>> pred = result.getPredecessor(to);
			if (pred.isPresent()) {
				final Mappable<T> from = pred.get();
				final Lifespan<T> lifespan = mappables.get(from);
				if (lifespan == null) {
					log.warn("Found mappable with predecessor but without corresponding lifespan");
					toAdd.add(new MappableAdd(to, new Lifespan<>(
							new Entity<>(to, result.getOrdinal(), 0))));
				} else {
					toUpdate.add(new MappableUpdate(lifespan, from, to));
				}
			} else {
				toAdd.add(new MappableAdd(to, new Lifespan<>(
						new Entity<>(to, result.getOrdinal(), 0))));
			}
		});

		final Map<Mappable<T>, Entity<T>> entities = new IdentityHashMap<>();
		for (MappableUpdate mu : toUpdate) {
			int numChanges = mu.getLifespan().getLast().getNumChanges();
			if (contentsDiffer(mu.getFrom(), mu.getTo())) {
				numChanges++;
			}
			final Entity<T> entity = new Entity<>(
					mu.getTo(), result.getOrdinal(), numChanges);
			entities.put(mu.getTo(), entity);
		}

		toAdd.stream().map(MappableAdd::getLifespan).forEach(lifespans::add);
		toUpdate.forEach(mu -> mu.getLifespan().add(entities.get(mu.getTo())));
		mappables.clear();
		toAdd.forEach(ma -> mappables.put(ma.getMappable(), ma.getLifespan()));
		toUpdate.forEach(mu -> mappables.put(mu.getTo(), mu.getLifespan()));
	}

	/**
	 * Returns whether the contents of {@code from} and {@code to} differ.
	 *
	 * @param from
	 * 		The predecessor of {@code to}.
	 * @param to
	 * 		The successor of {@code from}.
	 * @return
	 * 		{@code true} if the contents of {@code from} and {@code to} differ,
	 * 		{@code false} otherwise.
	 * @throws IOException
	 * 		If an error occurred while reading the contents of {@code from} and
	 * 		{@code to} (using {@link VCSFile.Range#readContent()}.
	 */
	boolean contentsDiffer(final Mappable<T> from, final Mappable<T> to)
			throws IOException {
		final List<VCSFile.Range> fromRanges = from.getRanges();
		final List<VCSFile.Range> toRanges = to.getRanges();
		if (fromRanges.size() != toRanges.size()) {
			return true;
		}

		final List<String> fromContents = new ArrayList<>();
		for (final VCSFile.Range range : fromRanges) {
			fromContents.add(range.readContent());
		}
		final List<String> toContents = new ArrayList<>();
		for (final VCSFile.Range range : toRanges) {
			toContents.add(range.readContent());
		}

		for (String fc : fromContents) {
			final int idx = toContents.indexOf(fc);
			if (idx < 0) {
				return true;
			}
			toContents.remove(idx);
		}
		Validate.validateState(toContents.isEmpty());
		return false;
	}

	@Data
	private class MappableAdd {
		@NonNull
		private final Mappable<T> mappable;
		@NonNull
		private final Lifespan<T> lifespan;
	}

	@Data
	private class MappableUpdate {
		@NonNull
		private final Lifespan<T> lifespan;
		@NonNull
		private final Mappable<T> from;
		@NonNull
		private final Mappable<T> to;
	}
}
