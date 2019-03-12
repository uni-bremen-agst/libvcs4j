package de.unibremen.informatik.st.libvcs4j.mapping;

import de.unibremen.informatik.st.libvcs4j.Revision;
import de.unibremen.informatik.st.libvcs4j.RevisionRange;
import de.unibremen.informatik.st.libvcs4j.VCSFile;
import de.unibremen.informatik.st.libvcs4j.Validate;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * This class is an alternative representation of a {@link Mappable} which is
 * used to decouple mapping related data from VCS specific data in order to
 * avoid excessively memory usage, which may occur by holding references to
 * outdated revisions ({@link Revision}) and files ({@link VCSFile}). Note that
 * this assumption depends on the actual metadata of the wrapped mappable
 * though. It is primarily used in conjunction with {@link Lifespan} and, thus,
 * provides the attributes {@link #ordinal}, which corresponds to the value of
 * {@link RevisionRange#getOrdinal()} and is used as a serial number, and
 * {@link #numChanges}, which stores the number of changes of an entity since
 * its first occurrence in a {@link Lifespan}.
 *
 * @param <T>
 *     The type of the metadata of the wrapped {@link Mappable}.
 */
public class Entity<T> {

	/**
	 * The ordinal of an entity. Corresponds to the value of
	 * {@link RevisionRange#getOrdinal()}.
	 */
	private final int ordinal;

	/**
	 * The id of the revision of an entity.
	 */
	private final String revisionId;

	/**
	 * The locations of an entity {@code >= 1}.
	 */
	private final List<Location> locations;

	/**
	 * The number of changes of an entity since its first occurrence in a
	 * {@link Lifespan} {@code >= 0}.
	 */
	private final int numChanges;

	/**
	 * The metadata of an entity.
	 */
	private final T metadata;

	/**
	 * Creates a new entity from the given mappable with given ordinal and
	 * number of changes. Does not hold any references to {@code mappable}
	 * except of its metadata, hence allowing {@code mappable} to be garbage
	 * collected.
	 *
	 * @param mappable
	 * 		The mappable to wrap.
	 * @param ordinal
	 * 		The ordinal of the entity to create. Corresponds to the value of
	 * 		{@link RevisionRange#getOrdinal()}. Must be positive. If the entity
	 * 		is not managed by a {@link Lifespan}, pass {@code 1} or use
	 * 		{@link #Entity(Mappable)}.
	 * @param numChanges
	 * 		The number of changes of the entity to create since its first
	 * 		occurrence in a {@link Lifespan}. Must not be negative. If the
	 * 		entity is not managed by a {@link Lifespan}, pass {@code 0} or use
	 * 		{@link #Entity(Mappable)}.
	 * @throws NullPointerException
	 * 		If {@code mappable} is {@code null}.
	 * @throws IllegalArgumentException
	 * 		If {@code mappable} has no ranges, if {@code ordinal <= 0}, or if
	 * 		{@code numChanges < 0}.
	 */
	public Entity(final Mappable<T> mappable, final int ordinal,
			final int numChanges) throws NullPointerException,
			IllegalArgumentException {
		Validate.notNull(mappable);
		Validate.isFalse(mappable.getRanges().isEmpty(),
				"The given mappable has no ranges.");
		Validate.isPositive(ordinal,
				"The ordinal must be positive");
		Validate.notNegative(numChanges,
				"The number of changes must not be negative");
		this.ordinal = ordinal;
		revisionId = mappable.getRanges().get(0)
				.getBegin()
				.getFile()
				.getRevision()
				.getId();
		locations = mappable.getRanges().stream()
				.filter(Objects::nonNull)
				.map(Location::new)
				.collect(Collectors.toList());
		this.numChanges = numChanges;
		metadata = mappable.getMetadata().orElse(null);
	}

	/**
	 * Creates a new entity from the given mappable and sets its ordinal to
	 * {@code 1} and its number of changes to {@code 0}. Does not hold any
	 * references to {@code mappable} except of its metadata, hence allowing
	 * {@code mappable} to be garbage collected. Usually, this constructor is
	 * used to create an entity which is not managed by a {@link Lifespan}.
	 *
	 * @param mappable
	 * 		The mappable to wrap.
	 * @throws NullPointerException
	 * 		If {@code mappable} is {@code null}.
	 * @throws IllegalArgumentException
	 * 		If {@code mappable} has no ranges.
	 */
	public Entity(final Mappable<T> mappable) {
		this(mappable, 1, 0);
	}

	/**
	 * Returns the ordinal of this entity. Corresponds to the value of
	 * {@link RevisionRange#getOrdinal()}.
	 *
	 * @return
	 * 		The ordinal of this entity.
	 */
	public int getOrdinal() {
		return ordinal;
	}

	/**
	 * Returns the id of the revision of this entity.
	 *
	 * @return
	 * 		The id of the revision of this entity.
	 */
	public String getRevisionId() {
		return revisionId;
	}

	/**
	 * Returns a flat copy of the locations of this entity. Contains at least
	 * one location.
	 *
	 * @return
	 * 		A flat copy of the locations of this entity.
	 */
	public List<Location> getLocations() {
		return new ArrayList<>(locations);
	}

	/**
	 * Returns the number of changes of this entity since its first occurrence
	 * in a {@link Lifespan} ({@code >= 0}).
	 *
	 * @return
	 * 		The number of changes of this entity since its first occurrence in
	 * 		a {@link Lifespan} ({@code >= 0}).
	 */
	public int getNumChanges() {
		return numChanges;
	}

	/**
	 * Returns the metadata of this entity.
	 *
	 * @return
	 * 		The metadata of this entity.
	 */
	public Optional<T> getMetadata() {
		return Optional.ofNullable(metadata);
	}

	/**
	 * Decouple the positional information of an {@link Entity} from the VCS
	 * related data of a {@link VCSFile.Range}. The line, column, and offset
	 * values of this class correspond to the line, column, and offset values
	 * of {@link VCSFile.Range}.
	 */
	public static class Location {

		/**
		 * The referenced file.
		 */
		private final Path file;

		/**
		 * The begin line.
		 */
		private final int beginLine;

		/**
		 * The begin column.
		 */
		private final int beginColumn;

		/**
		 * The begin offset.
		 */
		private final int beginOffset;

		/**
		 * The end line.
		 */
		private final int endLine;

		/**
		 * The end column.
		 */
		private final int endColumn;

		/**
		 * The end offset.
		 */
		private final int endOffset;

		/**
		 * Creates a new location from given {@link VCSFile.Range}.
		 *
		 * @param range
		 * 		The range to create the location from.
		 * @throws NullPointerException
		 * 		If {@code range} is {@code null}.
		 */
		public Location(final VCSFile.Range range) {
			Validate.notNull(range);
			file = range.getFile().toRelativePath();
			beginLine   = range.getBegin().getLine();
			beginColumn = range.getBegin().getColumn();
			beginOffset = range.getBegin().getOffset();
			endLine   = range.getEnd().getLine();
			endColumn = range.getEnd().getColumn();
			endOffset = range.getEnd().getOffset();
		}

		/**
		 * Returns the referenced file of this location.
		 *
		 * @return
		 * 		The referenced file of this location.
		 */
		public Path getFile() {
			return file;
		}

		/**
		 * Returns the begin line of this location.
		 *
		 * @return
		 * 		The begin line of this location.
		 */
		public int getBeginLine() {
			return beginLine;
		}

		/**
		 * Returns the begin column of this location.
		 *
		 * @return
		 * 		The begin column of this location.
		 */
		public int getBeginColumn() {
			return beginColumn;
		}

		/**
		 * Returns the begin offset of this location.
		 *
		 * @return
		 * 		The begin offset of this location.
		 */
		public int getBeginOffset() {
			return beginOffset;
		}

		/**
		 * Returns the end line of this location.
		 *
		 * @return
		 * 		The end line of this location.
		 */
		public int getEndLine() {
			return endLine;
		}

		/**
		 * Returns the end column of this location.
		 *
		 * @return
		 * 		The end column of this location.
		 */
		public int getEndColumn() {
			return endColumn;
		}

		/**
		 * Returns the end offset of this location.
		 *
		 * @return
		 * 		The end offset of this location.
		 */
		public int getEndOffset() {
			return endOffset;
		}
	}
}
