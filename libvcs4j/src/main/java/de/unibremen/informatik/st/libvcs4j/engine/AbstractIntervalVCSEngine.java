package de.unibremen.informatik.st.libvcs4j.engine;

import de.unibremen.informatik.st.libvcs4j.VCSEngineBuilder;
import de.unibremen.informatik.st.libvcs4j.Validate;
import de.unibremen.informatik.st.libvcs4j.exception.IllegalIntervalException;
import de.unibremen.informatik.st.libvcs4j.exception.IllegalReturnException;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * An {@link AbstractVSCEngine} with interval fields. Three different kinds of
 * intervals are supported: datetime interval, revision interval, and range
 * interval (inclusive start, exclusive end, origin 0).
 */
public abstract class AbstractIntervalVCSEngine extends AbstractVSCEngine {

	public static final int MIN_START_IDX = 0;

	/* Datetime interval. */
	private final LocalDateTime since, until;

	/* Revision interval. */
	private final String from, to;

	/* Range interval. */
	private final int startIdx, endIdx;

	/**
	 * Latest revision constructor.
	 */
	public AbstractIntervalVCSEngine(final String pRepository,
			final String pRoot, final Path pTarget)
			throws NullPointerException, IllegalArgumentException {
		super(pRepository, pRoot, pTarget);
		since = until = null;
		from = to = null;
		startIdx = endIdx = -1;
	}

	/**
	 * Datetime interval constructor. Validates that {@code pSince <= pUntil}.
	 */
	public AbstractIntervalVCSEngine(final String pRepository,
			final String pRoot, final Path pTarget, final LocalDateTime pSince,
			final LocalDateTime pUntil) throws NullPointerException,
			IllegalIntervalException {
		super(pRepository, pRoot, pTarget);
		from = to = null;
		startIdx = endIdx = -1;
		since = Validate.notNull(validateMapDateTime(pSince));
		until = Validate.notNull(validateMapDateTime(pUntil));
		IllegalIntervalException.isTrue(!since.isAfter(until),
				"Since (%s) after until (%s)", since, until);
	}

	/**
	 * Revision interval constructor. Does NOT validate if {@code pFrom <= pTo}.
	 */
	public AbstractIntervalVCSEngine(final String pRepository,
			final String pRoot, final Path pTarget, final String pFrom,
			final String pTo) throws NullPointerException {
		super(pRepository, pRoot, pTarget);
		since = until = null;
		startIdx = endIdx = -1;
		from = Validate.notNull(validateMapIntervalRevision(pFrom));
		to = Validate.notNull(validateMapIntervalRevision(pTo));
		// We can not validate that from <= to because this requires a method
		// call to a not (yet) fully available subclass instance.
	}

	/**
	 * Range interval constructor. Validates that
	 * {@code 0 <= pStartIdx < pEndIdx}.
	 */
	public AbstractIntervalVCSEngine(final String pRepository,
			final String pRoot, final Path pTarget, final int pStartIdx,
			final int pEndIdx) throws NullPointerException,
			IllegalIntervalException {
		super(pRepository, pRoot, pTarget);
		since = until = null;
		from = to = null;
		startIdx = pStartIdx;
		endIdx = pEndIdx;
		IllegalIntervalException.isTrue(startIdx >= MIN_START_IDX,
				"Start (%d) < %d", startIdx, MIN_START_IDX);
		IllegalIntervalException.isTrue(startIdx < endIdx,
				"Start (%d) >= end (%d)", startIdx, endIdx);
	}

	boolean isDateTimeInterval() {
		return since != null;
	}

	boolean isRevisionInterval() {
		return from != null;
	}

	boolean isRangeInterval() {
		return startIdx >= 0;
	}

	@Override
	protected final List<String> listRevisionsImpl() throws IOException {
		final List<String> revisions;
		if (isDateTimeInterval()) {
			revisions = listRevisionsImpl(since, until);
		} else if (isRevisionInterval()) {
			revisions = listRevisionsImpl(from, to);
		} else if (isRangeInterval()) {
			revisions = listRevisionsImpl(startIdx, endIdx);
		} else {
			final Optional<String> latest = getLatestRevision();
			return latest.map(Collections::singletonList)
					.orElseGet(Collections::emptyList);
		}
		IllegalReturnException.noNullElements(revisions);
		return revisions;
	}

	private List<String> listRevisionsImpl(final int startIdx,
			final int endIdx) throws IOException {
		final List<String> revs = listRevisionsImpl(
				VCSEngineBuilder.DEFAULT_SINCE,
				VCSEngineBuilder.DEFAULT_UNTIL);
		if (startIdx >= revs.size()) {
			return Collections.emptyList();
		}
		return revs.subList(startIdx, Math.min(endIdx, revs.size()));
	}

	protected abstract Optional<String> getLatestRevision() throws IOException;

	protected abstract List<String> listRevisionsImpl(LocalDateTime since,
			LocalDateTime until) throws IOException;

	protected abstract List<String> listRevisionsImpl(String from, String to)
			throws IOException;

	protected abstract LocalDateTime validateMapDateTime(LocalDateTime dt);

	protected abstract String validateMapIntervalRevision(String revision);
}
