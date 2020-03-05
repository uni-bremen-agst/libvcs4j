package de.unibremen.informatik.st.libvcs4j.spoon.metric;

import de.unibremen.informatik.st.libvcs4j.Validate;
import de.unibremen.informatik.st.libvcs4j.spoon.Cache;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * A metric gatherer of type {@link Integer}.
 */
@NoArgsConstructor
public abstract class IntGatherer extends Gatherer<Integer> {

	/**
	 * Creates a gatherer with given cache (see
	 * {@link de.unibremen.informatik.st.libvcs4j.spoon.Scanner#cache}).
	 *
	 * @param cache
	 * 		The cache that is used to speedup lookups.
	 * @throws NullPointerException
	 * 		If {@code cache} is {@code null}.
	 */
	public IntGatherer(final @NonNull Cache cache)
			throws NullPointerException {
		super(cache);
	}

	@Override
	protected Integer sum(final Integer a, final Integer b)
			throws NullPointerException {
		return Validate.notNull(a) + Validate.notNull(b);
	}

	/**
	 * Increments the metric of the top element of {@link #stack} by 1.
	 */
	void inc() {
		inc(1);
	}
}
