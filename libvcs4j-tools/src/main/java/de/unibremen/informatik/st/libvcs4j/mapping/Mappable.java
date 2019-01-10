package de.unibremen.informatik.st.libvcs4j.mapping;

import de.unibremen.informatik.st.libvcs4j.VCSFile;

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
	 * Returns the signature of this mappable. A signature uniquely identifies
	 * a mappable within a collection of mappables and allows {@link Mapping}
	 * to map two compatible mappables without comparing their ranges. A
	 * reasonable signature, for instance, could be the fully qualified name of
	 * a class (in case the mappable represents a class). The default
	 * implementation returns an empty {@link Optional}. Thus, by default, a
	 * mappable has no signature.
	 *
	 * @return
	 * 		The signature of this mappable.
	 */
	default Optional<String> getSignature() {
		return Optional.empty();
	}

	/**
	 * Returns the metadata of this mappable. The metadata of a mappable may be
	 * used to determine whether it is compatible with another mappable (as
	 * done by the default implementation of {@link #isCompatible(Mappable)}),
	 * or to store additional information that are required while generating or
	 * querying a {@link Lifespan}. The default implementation returns an empty
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
	 * given mappable are equals according to {@link Object#equals(Object)}. If
	 * this or the given mappable has no metadata, that is, an empty
	 * {@link Optional} is returned by {@link #getMetadata()}, the default
	 * implementation considers them as compatible. Subclasses may provide an
	 * entirely different behaviour though. However, {@code null} arguments are
	 * supported but can never be compatible with an actual mappable. This
	 * property, for the sake of fail-safeness, should not be violated.
	 *
	 * @param mappable
	 * 		The mappable to check.
	 * @return
	 * 		{@code true} if {@code mappable} is compatible with this mappable,
	 * 		{@code false} otherwise.
	 */
	default boolean isCompatible(final Mappable<T> mappable)
			throws NullPointerException {
		if (mappable == null) {
			return false;
		}
		final Optional<T> tm = getMetadata();
		final Optional<T> om = mappable.getMetadata();
		return !tm.isPresent() || !om.isPresent()
				|| tm.get().equals(om.get());
	}
}
