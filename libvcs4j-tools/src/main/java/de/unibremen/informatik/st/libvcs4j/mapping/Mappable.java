package de.unibremen.informatik.st.libvcs4j.mapping;

import de.unibremen.informatik.st.libvcs4j.VCSFile;

import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

/**
 * This interface is used to bridge the data of the client code with the
 * {@link Mapping} engine of this module.
 *
 * @param <T>
 * 		The type of the metadata of a mappable.
 */
public interface Mappable<T> {

	/**
	 * Returns the ranges of this mappable. Contains at least one range.
	 *
	 * @return
	 * 		The ranges of this mappable.
	 */
	List<VCSFile.Range> getRanges();

	/**
	 * Returns the signature of this mappable.
	 *
	 * A signature identifies a mappable without consideration of its positions
	 * (see {@link #getRanges()}). A reasonable signature, for instance, could
	 * be the fully qualified name of a class (in case the mappable represents
	 * a class). The default implementation returns an empty {@link Optional}.
	 * Thus, by default, a mappable has no signature.
	 *
	 * @return
	 * 		The signature of this mappable.
	 */
	default Optional<String> getSignature() {
		return Optional.empty();
	}

	/**
	 * Returns the metadata of this mappable.
	 *
	 * The metadata of a mappable may be used to determine whether it is
	 * compatible with another mappable (as done by the default implementation
	 * of {@link #isCompatibleWith(Mappable)}), or to store additional
	 * information that are required while generating or querying a
	 * {@link Lifespan}. The default implementation returns an empty
	 * {@link Optional}.
	 *
	 * @return
	 * 		The metadata of this mappable.
	 */
	default Optional<T> getMetadata() {
		return Optional.empty();
	}

	/**
	 * Returns whether this mappable is compatible with {@code mappable}.
	 *
	 * This method is used by {@link Mapping} to determine whether a mapping
	 * between two mappables is applicable at all. The default implementation
	 * checks whether the metadata (see {@link #getMetadata()}) of this and the
	 * given mappable are equal according to {@link Object#equals(Object)}. If
	 * this or the given mappable has no metadata, that is, an empty
	 * {@link Optional} is returned by {@link #getMetadata()}, the default
	 * implementation considers them as compatible. Subclasses may provide an
	 * entirely different behaviour though. Only the following property, for
	 * the sake of fail-safeness, must not be changed by subclasses: This
	 * method does not throw a {@link NullPointerException} if {@code mappable}
	 * is {@code null}. {@code null} arguments, however, are never compatible.
	 *
	 * @param mappable
	 * 		The mappable to check.
	 * @return
	 * 		{@code true} if {@code mappable} is compatible with this mappable,
	 * 		{@code false} otherwise.
	 */
	default boolean isCompatibleWith(final Mappable<T> mappable) {
		if (mappable == null) {
			return false;
		}
		final Optional<T> tm = getMetadata();
		final Optional<T> om = mappable.getMetadata();
		return !tm.isPresent() || !om.isPresent()
				|| tm.get().equals(om.get());
	}

	/**
	 * Returns whether the signature of this mappable match with the signature
	 * of {@code mappable}.
	 *
	 * This method is used by {@link Mapping} to determine whether two
	 * mappables match without consideration of their positions (see
	 * {@link #rangesMatchWith(Mappable)}). The default implementation checks
	 * the signature of this and the given mappable using
	 * {@link String#equals(Object)}. If this or the given mappable has no
	 * signature, that is, an empty {@link Optional} is returned by
	 * {@link #getSignature()}, the default implementation returns
	 * {@code false}. Subclasses may override this method and provide a
	 * different behaviour. However, note that this may have a negative effect
	 * on mapping results. The following property, for the sake of
	 * fail-safeness, must not be changed by subclasses: This method does not
	 * throw a {@link NullPointerException} if {@code mappable} is
	 * {@code null}. {@code null} arguments, however, never match.
	 *
	 * @param mappable
	 * 		The mappable whose signature to check.
	 * @return
	 * 		{@code true} if the signature of {@code mappable} match with the
	 * 		signature of this mappable, {@code false} otherwise.
	 */
	default boolean signatureMatchesWith(final Mappable<T> mappable) {
		if (mappable == null) {
			return false;
		}
		final Optional<String> ts = getSignature();
		final Optional<String> os = mappable.getSignature();
		return !ts.isPresent() || !os.isPresent()
				|| ts.get().equals(os.get());
	}

	/**
	 * Returns whether the ranges of this mappable match with the ranges of
	 * {@code mappable}.
	 *
	 * This method is used by {@link Mapping} to determine whether two
	 * mappables have the same positions (see {@link #getRanges()}). The
	 * default implementation checks whether this and the given mappable have
	 * the same number of ranges and tries to match each range of this mappable
	 * with a distinct range of {@code mappable}. Two ranges {@code r1} and
	 * {@code r2} match if their relative paths match according to
	 * {@link Path#equals(Object)} and if their begin and end positions match
	 * according to {@link VCSFile.Position#OFFSET_COMPARATOR}. Subclasses may
	 * override this method and provide a different behaviour. However, note
	 * that this may have a negative effect on mapping results. The following
	 * property, for the sake of fail-safeness, must not be changed by
	 * subclasses: This method does not throw a {@link NullPointerException} if
	 * {@code mappable} is {@code null}. {@code null} arguments, however, never
	 * match.
	 *
	 * @param mappable
	 * 		The mappable whose ranges to check.
	 * @return
	 * 		{@code true} if the ranges of {@code mappable} match with the
	 * 		ranges of this mappable, {@code false} otherwise.
	 */
	default boolean rangesMatchWith(final Mappable<T> mappable) {
		if (mappable == null) {
			return false;
		}
		final List<VCSFile.Range> thisRanges = getRanges();
		final List<VCSFile.Range> otherRanges = mappable.getRanges();
		if (thisRanges.size() != mappable.getRanges().size()) {
			return false;
		}
		thisRanges.forEach(tr -> {
			final Iterator<VCSFile.Range> it = otherRanges.iterator();
			while (it.hasNext()) {
				final VCSFile.Range or = it.next();
				// Match path.
				final Path r1RelPath = tr.getFile().toRelativePath();
				final Path r2RelPath = or.getFile().toRelativePath();
				final boolean pathMatch = r1RelPath.equals(r2RelPath);
				// Match begin.
				final VCSFile.Position r1Begin = tr.getBegin();
				final VCSFile.Position r2Begin = or.getBegin();
				final boolean beginMatch = VCSFile.Position
						.OFFSET_COMPARATOR.compare(r1Begin, r2Begin) == 0;
				// Match end.
				final VCSFile.Position r1End = tr.getEnd();
				final VCSFile.Position r2End = or.getEnd();
				final boolean endMatch = VCSFile.Position
						.OFFSET_COMPARATOR.compare(r1End, r2End) == 0;
				// Do not reuse or in case of a match.
				if (pathMatch && beginMatch && endMatch) {
					it.remove();
					break;
				}
			}
		});
		return otherRanges.isEmpty();
	}
}
