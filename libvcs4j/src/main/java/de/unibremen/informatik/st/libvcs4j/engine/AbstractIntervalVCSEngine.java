package de.unibremen.informatik.st.libvcs4j.engine;

import de.unibremen.informatik.st.libvcs4j.VCSEngineBuilder;
import de.unibremen.informatik.st.libvcs4j.exception.IllegalIntervalException;
import de.unibremen.informatik.st.libvcs4j.exception.IllegalReturnException;
import org.apache.commons.lang3.Validate;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * An {@link AbstractVSCEngine} with interval fields. Three different kinds of
 * intervals are supported: datetime interval, revision interval, and range
 * interval.
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public abstract class AbstractIntervalVCSEngine extends AbstractVSCEngine {

	public static final int MIN_START = 0;

	/* Datetime interval. */
	private final LocalDateTime since, until;

	/* Revision interval. */
	private final String from, to;

	/* Range interval. */
	private final int start, end;

	/**
	 * Interval constructor with given revision list.
	 */
	public AbstractIntervalVCSEngine(
			final String pRepository, final String pRoot, final Path pTarget,
			final List<String> pRevisions) throws NullPointerException,
			IllegalArgumentException {
		super(pRepository, pRoot, pTarget, pRevisions);
		since = until = null;
		from = to = null;
		start = end = -1;
	}

	/**
	 * Datetime interval constructor. Validates that pSince <= pUntil.
	 */
	public AbstractIntervalVCSEngine(
			final String pRepository, final String pRoot, final Path pTarget,
			final LocalDateTime pSince, final LocalDateTime pUntil)
			throws NullPointerException, IllegalIntervalException {
		super(pRepository, pRoot, pTarget);
		from = to = null;
		start = end = -1;
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
		start = end = -1;
		from = Validate.notNull(pFrom);
		to = Validate.notNull(pTo);
		// We can not validate if from <= to because this requires a method
		// call to a not (yet) fully available subclass instance.
	}

	/**
	 * Range interval constructor. Validates that 0 <= pStart < pEnd.
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
		IllegalIntervalException.isTrue(
				start >= MIN_START, "Start (%d) < %d", start, MIN_START);
		IllegalIntervalException.isTrue(
				start < end, "Start (%d) >= end (%d)", start, end);
	}

	boolean isDateTimeInterval() {
		return since != null;
	}

	boolean isRevisionInterval() {
		return from != null;
	}

	boolean isRangeInterval() {
		return start >= 0;
	}

	@Override
	protected final List<String> listRevisionsImpl() throws IOException {
		final List<String> revisions;
		if (isDateTimeInterval()) {
			revisions = listRevisionsImpl(since, until);
		} else if (isRevisionInterval()) {
			revisions = listRevisionsImpl(from, to);
		} else if (isRangeInterval()) {
			revisions = listRevisionsImpl(start, end);
		} else {
			throw new IllegalStateException("Unknown interval type");
		}
		IllegalReturnException.noNullElements(revisions);
		return revisions;
	}

	private List<String> listRevisionsImpl(final int pStart, final int pEnd)
			throws IOException {
		final LocalDateTime since = VCSEngineBuilder.DEFAULT_SINCE;
		final LocalDateTime until = LocalDateTime.of(2200, 1, 1, 0, 0);
		final List<String> revs = listRevisionsImpl(since, until);
		if (pStart >= revs.size()) {
			return Collections.emptyList();
		}
		return revs.subList(pStart, Math.min(pEnd, revs.size()));
	}

	protected abstract List<String> listRevisionsImpl(
			final LocalDateTime pSince, final LocalDateTime pUntil)
			throws IOException;

	protected abstract List<String> listRevisionsImpl(
			final String pFrom, final String pTo) throws IOException;
}
