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
import java.util.Optional;

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
	 * Latest revision constructor.
	 */
	public AbstractIntervalVCSEngine(final String pRepository,
			final String pRoot, final Path pTarget)
			throws NullPointerException, IllegalArgumentException {
		super(pRepository, pRoot, pTarget);
		since = until = null;
		from = to = null;
		start = end = -1;
	}

	/**
	 * Interval constructor with given revision list.
	 */
	public AbstractIntervalVCSEngine(final String pRepository,
			final String pRoot, final Path pTarget,
			final List<String> pRevisions) throws NullPointerException,
			IllegalArgumentException {
		super(pRepository, pRoot, pTarget, pRevisions);
		since = until = null;
		from = to = null;
		start = end = -1;
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
		start = end = -1;
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
		start = end = -1;
		from = Validate.notNull(validateMapIntervalRevision(pFrom));
		to = Validate.notNull(validateMapIntervalRevision(pTo));
		// We can not validate if from <= to because this requires a method
		// call to a not (yet) fully available subclass instance.
	}

	/**
	 * Range interval constructor. Validates that {@code 0 <= pStart < pEnd}.
	 */
	public AbstractIntervalVCSEngine(final String pRepository,
			final String pRoot, final Path pTarget, final int pStart,
			final int pEnd) throws NullPointerException,
			IllegalIntervalException {
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
			final Optional<String> latest = getLatestRevision();
			return latest.map(Collections::singletonList)
					.orElseGet(Collections::emptyList);
		}
		IllegalReturnException.noNullElements(revisions);
		return revisions;
	}

	private List<String> listRevisionsImpl(int start, int end)
			throws IOException {
		final LocalDateTime since = VCSEngineBuilder.DEFAULT_SINCE;
		final LocalDateTime until = VCSEngineBuilder.DEFAULT_UNTIL;
		final List<String> revs = listRevisionsImpl(since, until);
		if (start >= revs.size()) {
			return Collections.emptyList();
		}
		return revs.subList(start, Math.min(end, revs.size()));
	}

	protected abstract Optional<String> getLatestRevision() throws IOException;

	protected abstract List<String> listRevisionsImpl(LocalDateTime since,
			LocalDateTime until) throws IOException;

	protected abstract List<String> listRevisionsImpl(String from, String to)
			throws IOException;

	protected abstract LocalDateTime validateMapDateTime(LocalDateTime dt);

	protected abstract String validateMapIntervalRevision(String revision);
}
