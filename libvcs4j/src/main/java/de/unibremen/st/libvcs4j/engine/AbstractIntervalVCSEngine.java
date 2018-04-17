package de.unibremen.st.libvcs4j.engine;

import de.unibremen.st.libvcs4j.exception.IllegalIntervalException;
import de.unibremen.st.libvcs4j.exception.IllegalReturnException;
import org.apache.commons.lang3.Validate;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

/**
 * An {@link AbstractVSCEngine} with interval fields. Three different kinds of
 * intervals are supported: datetime interval, revision interval, and ordinal
 * interval.
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public abstract class AbstractIntervalVCSEngine extends AbstractVSCEngine {

	/* Datetime interval. */
	private final LocalDateTime since, until;

	/* Revision interval. */
	private final String from, to;

	/**
	 * Ordinal interval.
	 */
	private final int start, end;

	/**
	 * Datetime interval constructor. Validates that pSince <= pUntil.
	 */
	public AbstractIntervalVCSEngine(
			final String pRepository, final String pRoot, final Path pTarget,
			final LocalDateTime pSince, final LocalDateTime pUntil)
			throws NullPointerException, IllegalIntervalException {
		super(pRepository, pRoot, pTarget);
		from = to = null;
		start = end = 0;
		since = Validate.notNull(pSince);
		until = Validate.notNull(pUntil);
		IllegalIntervalException.isTrue(!pSince.isAfter(pUntil),
				"Since (%s) after until (%s)", pSince, pUntil);
	}

	/**
	 * Revision interval constructor. Does NOT validate if pFrom <= pTo.
	 */
	public AbstractIntervalVCSEngine(
			final String pRepository, final String pRoot, final Path pTarget,
			final String pFrom, final String pTo)
			throws NullPointerException {
		super(pRepository, pRoot, pTarget);
		since = until = null;
		start = end = 0;
		from = Validate.notNull(pFrom);
		to = Validate.notNull(pTo);
		// We can not validate if from <= to because this requires a method
		// call to a not (yet) fully available subclass instance.
	}

	/**
	 * Ordinal interval constructor. Validates that 1 <= pStart <= pEnd.
	 */
	public AbstractIntervalVCSEngine(
			final String pRepository, final String pRoot, final Path pTarget,
			final int pStart, final int pEnd)
			throws NullPointerException, IllegalIntervalException {
		super(pRepository, pRoot, pTarget);
		since = until = null;
		from = to = null;
		start = pStart;
		end = pEnd;
		IllegalIntervalException.isTrue(start > 0, "Start (%d) <= 0", start);
		IllegalIntervalException.isTrue(end > 0, "End (%d) <= 0", end);
		IllegalIntervalException.isTrue(
				start <= end, "Start (%d) > end (%d)", start, end);
	}

	boolean isDateTimeInterval() {
		return since != null;
	}

	boolean isRevisionInterval() {
		return from != null;
	}

	boolean isOrdinalInterval() {
		return start >= 1;
	}

	@Override
	protected final List<String> listRevisionsImpl() throws IOException {
		final List<String> revisions;
		if (isDateTimeInterval()) {
			revisions = listRevisionsImpl(since, until);
		} else if (isRevisionInterval()) {
			revisions = listRevisionsImpl(from, to);
		} else if (isOrdinalInterval()) {
			revisions = listRevisionsImpl(start, end);
		} else {
			throw new IllegalStateException("Unknown interval type");
		}
		IllegalReturnException.noNullElements(revisions);
		return revisions;
	}

	protected abstract List<String> listRevisionsImpl(
			final LocalDateTime pSince, final LocalDateTime pUntil)
			throws IOException;

	protected abstract List<String> listRevisionsImpl(
			final String pFrom, final String pTo) throws IOException;

	protected abstract List<String> listRevisionsImpl(
			final int pStart, final int pEnd) throws IOException;
}
