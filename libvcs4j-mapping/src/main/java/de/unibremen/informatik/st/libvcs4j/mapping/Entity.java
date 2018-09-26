package de.unibremen.informatik.st.libvcs4j.mapping;

import de.unibremen.informatik.st.libvcs4j.Revision;
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
 * used to decouple mapping related data from VCS specific data to avoid
 * excessively memory usage, which may occur by holding references to outdated
 * revisions ({@link Revision}) and files ({@link VCSFile}). Note that this
 * assumption depends on the metadata of the wrapped mappable though.
 *
 * @param <T>
 *     The type of the metadata of the wrapped {@link Mappable}.
 */
public class Entity<T> {

	/**
	 * The id of the revision of this entity.
	 */
	private final String revisionId;

	/**
	 * The locations of this entity {@code >= 1}.
	 */
	private final List<Location> locations;

	/**
	 * The metadata of this entity.
	 */
	private final T metadata;

	/**
	 * Creates a new entity from the given mappable. Does not hold any
	 * references to {@code mappable} or any of its data, hence allowing
	 * {@code mappable} to be garbage collected.
	 *
	 * @param mappable
	 * 		The mappable to wrap.
	 */
	public Entity(final Mappable<T> mappable) {
		Validate.notNull(mappable);
		Validate.isFalse(mappable.getRanges().isEmpty(),
				"The given mappable has no ranges.");
		revisionId = mappable.getRanges().get(0)
				.getBegin()
				.getFile()
				.getRevision()
				.getId();
		locations = mappable.getRanges().stream()
				.filter(Objects::nonNull)
				.map(Location::new)
				.collect(Collectors.toList());
		metadata = mappable.getMetadata().orElseGet(null);
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
