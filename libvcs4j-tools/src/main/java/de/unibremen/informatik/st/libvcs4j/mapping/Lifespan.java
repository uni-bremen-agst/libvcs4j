package de.unibremen.informatik.st.libvcs4j.mapping;

import de.unibremen.informatik.st.libvcs4j.Revision;
import de.unibremen.informatik.st.libvcs4j.RevisionRange;
import de.unibremen.informatik.st.libvcs4j.VCSFile;
import de.unibremen.informatik.st.libvcs4j.Validate;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Optional;

/**
 * Stores a sequence of {@link Entity} instances in a CSV file.
 */
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class Lifespan {

	/**
	 * Charset of output csv.
	 */
	public static final Charset CHARSET = StandardCharsets.UTF_8;

	/**
	 * Delimiter of output csv.
	 */
	public static final String DELIMITER = ";";

	/**
	 * Path to the CSV file containing the results.
	 */
	@Getter
	@NonNull
	private final Path csv;

	/**
	 * Indicates whether the entity passed to {@link #add(Entity)} is the
	 * first one.
	 */
	private boolean first = true;

	/**
	 * Adds the given entity to this lifespan.
	 *
	 * @param entity
	 * 		The entity to add.
	 * @throws NullPointerException
	 * 		If {@code entity} is {@code null}.
	 * @throws IOException
	 * 		If an error occurred while writing {@code entity} to {@link #csv}.
	 */
	void add(@NonNull final Entity entity) throws NullPointerException,
			IOException {
		if (first) {
			final String header = String.join(DELIMITER, "ordinal", "revision",
					"changed", "metadata", "locations") + "\n";
			Files.write(csv, header.getBytes(CHARSET),
					StandardOpenOption.CREATE,
					StandardOpenOption.TRUNCATE_EXISTING);
		}
		Validate.validateState(Files.exists(csv));
		final String changed = entity.isChanged() ? "1" : "0";
		final Mappable<?> mappable = entity.getMappable();
		final Revision revision = mappable.getRanges().get(0)
				.getFile().getRevision();
		final String row = String.join(DELIMITER,
				"\"" + entity.getOrdinal() + "\"",
				"\"" + revision.getId() + "\"",
				"\"" + changed + "\"",
				"\"" + entity.getMetadataAsString().orElse("") + "\"",
				"\"" + toJSONString(mappable.getRanges()) + "\"") + "\n";
		Files.write(csv, row.getBytes(CHARSET),
				StandardOpenOption.APPEND);
		first = false;
	}

	/**
	 * Creates a JSON String from the given list of ranges.
	 *
	 * @param ranges
	 * 		The ranges to serialize. Must contain at least one range.
	 * @return
	 * 		The serialized string.
	 * @throws NullPointerException
	 * 		If {@code ranges} is {@code null}.
	 * @throws IllegalArgumentException
	 * 		If {@code ranges} is {@code empty}.
	 */
	private String toJSONString(@NonNull final List<VCSFile.Range> ranges)
			throws NullPointerException, IllegalArgumentException {
		Validate.notEmpty(ranges, "At least one range must be given");
		final StringBuilder builder = new StringBuilder();
		builder.append("{\"locations\":[");
		ranges.forEach(range -> {
			builder.append("{\"file\":");
			builder.append("\"")
					.append(range.getFile().getRelativePath())
					.append("\"");
			builder.append(",\"beginLine\":");
			builder.append(range.getBegin().getLine());
			builder.append(",\"endLine\":");
			builder.append(range.getEnd().getLine());
			builder.append(",\"beginColumn\":");
			builder.append(range.getBegin().getColumn());
			builder.append(",\"beginTabSize\":");
			builder.append(range.getBegin().getTabSize());
			builder.append(",\"endColumn\":");
			builder.append(range.getEnd().getColumn());
			builder.append(",\"endTabSize\":");
			builder.append(range.getEnd().getTabSize());
			builder.append(",\"beginOffset\":");
			builder.append(range.getBegin().getOffset());
			builder.append(",\"endOffset\":");
			builder.append(range.getEnd().getOffset());
			builder.append("},");
		});
		builder.setLength(builder.length() - 1);
		builder.append("]}");
		return builder.toString();
	}

	/**
	 * Wraps a {@link Mappable} and provides additional data.
	 */
	@RequiredArgsConstructor
	static abstract class Entity {

		/**
		 * The wrapped mappable.
		 */
		@Getter
		private final Mappable<?> mappable;

		/**
		 * The ordinal of a mappable. Corresponds to the value of
		 * {@link RevisionRange#getOrdinal()}.
		 */
		@Getter
		private final int ordinal;

		/**
		 * Indicates whether the contents of an entity changed (with respect to
		 * its predecessor).
		 */
		@Getter
		private final boolean changed;

		/**
		 * Returns the metadata of {@link #mappable} as string.
		 *
		 * @return
		 * 		The string representation of the metadata of {@link #mappable}.
		 */
		abstract Optional<String> getMetadataAsString();
	}
}
