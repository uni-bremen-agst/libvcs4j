package de.unibremen.informatik.st.libvcs4j.mapping;

import de.unibremen.informatik.st.libvcs4j.VCSFile;
import de.unibremen.informatik.st.libvcs4j.Validate;
import lombok.Data;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Allows to automatically track mappables (by processing the results of
 * {@link Mapping.Result}) and writes the results into an output directory
 * ({@link #directory}). A sequence of mapped mappables is managed by an
 * instance of the {@link Lifespan} class, which in turn stores the results in
 * a CSV file ({@link Lifespan#csv}).
 *
 * @param <T>
 *     The type of the metadata of the tracked mappables.
 */
@Slf4j
public class Tracker<T> {

	/**
	 * Name of file containing the lifespan info (stored in
	 * {@link #directory}).
	 */
	private static final String LIFESPAN_INFO_FILE = "lifespan_info.csv";

	/**
	 * Charset of lifespan info file.
	 */
	public static final Charset CHARSET = StandardCharsets.UTF_8;

	/**
	 * The CSV delimiter used in the lifespan info file.
	 */
	public static final String DELIMITER = ";";

	/**
	 * The output directory which contains all output files
	 * ({@link #LIFESPAN_INFO_FILE} and the CSV files of the managed
	 * lifespans).
	 */
	private final Path directory;

	/**
	 * The converter that is used to convert the metadata of a mappable (see
	 * {@link Mappable#getMetadata()}) to a string.
	 */
	private final MetadataConverter<T> converter;

	/**
	 * The lifespans managed by this tracker.
	 */
	private final List<Lifespan> lifespans = new ArrayList<>();

	/**
	 * Maps the "to" mappables (see {@link Mapping.Result#getTo()}) of the last
	 * call of {@link #add(Mapping.Result)} to the lifespans they were assigned
	 * to so that the next call of this method is able to add the successors of
	 * these mappables to their corresponding lifespans. In order to avoid
	 * issue due to inappropriate implementations of {@link Object#hashCode()}
	 * and {@link Object#equals(Object)}, an {@link IdentityHashMap} is used.
	 */
	private final Map<Mappable<T>, Lifespan> mappables =
			new IdentityHashMap<>();

	/**
	 * Indicates whether the mapping result passed to
	 * {@link #add(Mapping.Result)} is the first one.
	 */
	private boolean first = true;

	/**
	 * The id of the next lifespan created.
	 */
	private int nextLifespanId = 1;

	/**
	 * Creates a new tracker with given output directory and converter.
	 *
	 * @param directory
	 * 		The output directory of the created tracker.
	 * @param converter
	 * 		The metadata converter to use.
	 * @throws NullPointerException
	 * 		If any of the given arguments is {@code null}.
	 * @throws IOException
	 * 		If an error occurred while creating {@code directory}.
	 */
	public Tracker(@NonNull final Path directory,
			@NonNull final MetadataConverter<T> converter) throws
			NullPointerException, IllegalArgumentException, IOException {
		log.info("Creating directory structure {}", directory);
		Files.createDirectories(directory);
		this.directory = directory;
		this.converter = converter;
	}

	/**
	 * Returns a flat copy of the lifespans of this tracker.
	 *
	 * @return
	 * 		A flat copy of the lifespans of this tracker.
	 */
	public List<Lifespan> getLifespans() {
		return new ArrayList<>(lifespans);
	}

	/**
	 * Adds the given mapping results to this tracker.
	 *
	 * @param result
	 * 		The mapping result to process.
	 * @throws NullPointerException
	 * 		If {@code result} is {@code null}.
	 * @throws UncheckedIOException
	 * 		If an error occurred while writing results to disk. If this
	 * 		exception is thrown, the state of a tracker is invalid.
	 */
	public void add(@NonNull final Mapping.Result<T> result)
			throws NullPointerException, UncheckedIOException {
		// Identify affected lifespans.
		final List<MappableAdd> toAdd = new ArrayList<>();
		final List<MappableUpdate> toUpdate = new ArrayList<>();
		result.getTo().forEach(to -> {
			final Optional<Mappable<T>> pred = result.getPredecessor(to);
			if (pred.isPresent()) {
				final Mappable<T> from = pred.get();
				final Lifespan lifespan = mappables.get(from);
				if (lifespan == null) {
					log.warn("Found mappable with predecessor but without corresponding lifespan");
					toAdd.add(new MappableAdd(to, new Lifespan(
							directory.resolve(nextLifespanId++ + ".csv"))));
				} else {
					toUpdate.add(new MappableUpdate(lifespan, from, to));
				}
			} else {
				toAdd.add(new MappableAdd(to, new Lifespan(
						directory.resolve(nextLifespanId++ + ".csv"))));
			}
		});
		// Create and update corresponding lifespans.
		try {
			for (final MappableAdd add : toAdd) {
				final Lifespan.Entity entity = new Entity(
						add.getMappable(), result.getOrdinal(), false);
				add.getLifespan().add(entity);
			}
			for (final MappableUpdate update : toUpdate) {
				final boolean changed = contentsDiffer(
						update.getFrom(), update.getTo());
				final Lifespan.Entity entity = new Entity(
						update.getTo(), result.getOrdinal(), changed);
				update.getLifespan().add(entity);
			}
		} catch (final IOException e) {
			throw new UncheckedIOException(e);
		}
		// Update `lifespans` and `mappables`.
		final int ceased = mappables.size() // those active in the last call
				- toUpdate.size(); // those updated in this call
		toAdd.forEach(ma -> lifespans.add(ma.getLifespan()));
		mappables.clear();
		toAdd.forEach(ma -> mappables.put(ma.getMappable(), ma.getLifespan()));
		toUpdate.forEach(mu -> mappables.put(mu.getTo(), mu.getLifespan()));
		// Write lifespan info file.
		try {
			if (first) {
				final String header = String.join(DELIMITER,
						"ordinal", "total", "active", "updated", "added",
						"ceased") + "\n";
				Files.writeString(directory.resolve(LIFESPAN_INFO_FILE),
						header, CHARSET,
						StandardOpenOption.CREATE,
						StandardOpenOption.TRUNCATE_EXISTING);
			}
			final String row = String.join(DELIMITER,
					String.valueOf(result.getOrdinal()),
					String.valueOf(lifespans.size()),
					String.valueOf(mappables.size()),
					String.valueOf(toUpdate.size()),
					String.valueOf(toAdd.size()),
					String.valueOf(ceased)) + "\n";
			Files.writeString(directory.resolve(LIFESPAN_INFO_FILE),
					row, CHARSET,
					StandardOpenOption.APPEND);
			first = false;
		} catch (final IOException e) {
			throw new UncheckedIOException(e);
		}
		log.info("Tracking {} lifespans (active: {}, updated: {}, added: {}, ceased: {})",
				lifespans.size(), mappables.size(), toUpdate.size(),
				toAdd.size(), ceased);
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
	private boolean contentsDiffer(final Mappable<T> from,
			final Mappable<T> to) throws IOException {
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

	/**
	 * Converts the metadata of a mappable to a string.
	 *
	 * @param <T>
	 * 		The type of the metadata.
	 */
	public interface MetadataConverter<T> {

		/**
		 * Converts {@code metadata} to a string.
		 *
		 * @param metadata
		 * 		The metadata to convert.
		 * @return
		 * 		The string representation of {@code metadata}.
		 */
		String toString(T metadata);
	}

	@Data
	private class MappableAdd {
		@NonNull
		private final Mappable<T> mappable;
		@NonNull
		private final Lifespan lifespan;
	}

	@Data
	private class MappableUpdate {
		@NonNull
		private final Lifespan lifespan;
		@NonNull
		private final Mappable<T> from;
		@NonNull
		private final Mappable<T> to;
	}

	private class Entity extends Lifespan.Entity {

		private Entity(Mappable<T> mappable, int ordinal, boolean changed) {
			super(mappable, ordinal, changed);
		}

		@Override
		@SuppressWarnings("unchecked")
		Optional<String> getMetadataAsString() {
			return getMappable()
					.getMetadata()
					.map(m -> (T)m)
					.map(converter::toString);
		}
	}
}
