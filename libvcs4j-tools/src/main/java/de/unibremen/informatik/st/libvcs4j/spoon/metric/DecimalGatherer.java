package de.unibremen.informatik.st.libvcs4j.spoon.metric;

import de.unibremen.informatik.st.libvcs4j.Validate;
import de.unibremen.informatik.st.libvcs4j.spoon.Cache;
import lombok.NonNull;

import java.math.BigDecimal;

/**
 * A metric gatherer of type {@link BigDecimal}.
 */
public abstract class DecimalGatherer extends Gatherer<BigDecimal> {

	/**
	 * Creates a gatherer with a new cache (see
	 * {@link de.unibremen.informatik.st.libvcs4j.spoon.Scanner#cache}).
	 */
	public DecimalGatherer() {
	}

	/**
	 * Creates a gatherer with given cache (see
	 * {@link de.unibremen.informatik.st.libvcs4j.spoon.Scanner#cache}).
	 *
	 * @param cache
	 * 		The cache that is used to speedup lookups.
	 * @throws NullPointerException
	 * 		If {@code cache} is {@code null}.
	 */
	public DecimalGatherer(final @NonNull Cache cache)
			throws NullPointerException {
		super(cache);
	}

	@Override
	protected BigDecimal sum(final BigDecimal a, final BigDecimal b)
			throws NullPointerException {
		return Validate.notNull(a).add(Validate.notNull(b));
	}

	/**
	 * Increments the metric of the top element of {@link #stack} by
	 * {@link BigDecimal#ONE}.
	 */
	void inc() {
		inc(BigDecimal.ONE);
	}
}
