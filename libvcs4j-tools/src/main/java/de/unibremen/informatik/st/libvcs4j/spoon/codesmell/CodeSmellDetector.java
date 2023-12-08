package de.unibremen.informatik.st.libvcs4j.spoon.codesmell;

import de.unibremen.informatik.st.libvcs4j.VCSFile;
import de.unibremen.informatik.st.libvcs4j.Validate;
import de.unibremen.informatik.st.libvcs4j.spoon.ElementExtractor;
import de.unibremen.informatik.st.libvcs4j.spoon.Environment;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.declaration.CtElement;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static spoon.reflect.cu.SourcePosition.NOPOSITION;

@Slf4j
public abstract class CodeSmellDetector extends ElementExtractor {

	/**
	 * Stores the detected code smells.
	 */
	@NonNull
	private final List<CodeSmell> codeSmells = new ArrayList<>();

	/**
	 * Creates a new detector with given environment.
	 *
	 * @param environment
	 * 		The environment to use.
	 * @throws NullPointerException
	 * 		If {@code environment} is {@code null}.
	 */
	public CodeSmellDetector(@NonNull final Environment environment)
			throws NullPointerException {
		super(environment);
	}

	/**
	 * Returns the definition of this detector.
	 *
	 * @return
	 * 		The definition of this detector.
	 */
	public abstract CodeSmell.Definition getDefinition();

	/**
	 * Returns a copy of the code smells of this detector.
	 *
	 * @return
	 * 		A copy of the code smells of this detector.
	 */
	public List<CodeSmell> getCodeSmells() {
		return new ArrayList<>(codeSmells);
	}

	/**
	 * Tries to add the given element, its metrics, its signature (may be
	 * {@code null}), and its summary (a nullable value describing why
	 * {@code element} is considered a code smell}) as a {@link CodeSmell} with
	 * a single range. On success, the newly created code smell is returned. On
	 * failure, an empty {@link Optional} is returned. Adding an element may
	 * fail, for example, if it is implicit or has no position (see
	 * {@link CtElement#isImplicit()} and {@link SourcePosition#NOPOSITION}).
	 *
	 * @param element
	 * 		The element to add as code smell.
	 * @param metrics
	 * 		The metrics of {@code element}.
	 * @param signature
	 * 		The signature of {@code element}. May be {@code null}.
	 * @param summary
	 * 		The summary describing why {@code element} is considered a code
	 * 		smell. May be {@code null}.
	 * @return
	 * 		The newly created code smell.
	 */
	protected Optional<CodeSmell> addCodeSmell(final CtElement element,
			final List<Metric> metrics, final String signature,
			final String summary) {
		if (filter(element, metrics)) {
			log.debug("Filtering out element: '{}'", element == null
					? null : element.getShortRepresentation());
			return Optional.empty();
		}

		// `element` must have a valid position.
		try {
			final Optional<VCSFile.Range> range = createRange(element);
			if (range.isEmpty()) {
				log.warn("Skipping element with unknown file: '{}' in '{}'",
						element.getShortRepresentation(),
						element.getPosition().getFile());
				return Optional.empty();
			}
			final CodeSmell codeSmell = new CodeSmell(getDefinition(), metrics,
					Collections.singletonList(range.get()), signature,
					summary);
			codeSmells.add(map(codeSmell, Collections.singletonList(element)));
			return Optional.of(codeSmell);
		} catch (final IOException e) {
			log.warn("Skipping element due to an unexpected IOException", e);
			return Optional.empty();
		}
	}

	/**
	 * Tries to add the given element range, its metrics, its signature (may be
	 * {@code null}), and its summary (a nullable value describing why the
	 * element range is considered a code smell}) as a {@link CodeSmell} with a
	 * single range. On success, the newly created code smell is returned. On
	 * failure, an empty {@link Optional} is returned. Adding an element range
	 * may fail, for example, if {@code from} or {@code to} are implicit or
	 * have no position (see {@link CtElement#isImplicit()} and
	 * {@link SourcePosition#NOPOSITION}).
	 *
	 * @param from
	 * 		The from element.
	 * @param to
	 * 		The to element.
	 * @param metrics
	 * 		The metrics of of the element range.
	 * @param signature
	 * 		The signature of the element range. May be {@code null}.
	 * @param summary
	 * 		The summary describing why the element range is considered a code
	 * 		smell. May be {@code null}.
	 * @return
	 * 		The newly created code smell.
	 * @throws IllegalArgumentException
	 * 		If {@code from} and {@code to} are located in different files.
	 */
	protected Optional<CodeSmell> addCodeSmellRange(final CtElement from,
			final CtElement to, final List<Metric> metrics,
			final String signature, final String summary)
			throws IllegalArgumentException {
		if (filter(Arrays.asList(from, to), metrics)) {
			log.debug("Filtering out range: '{}' to '{}'",
					from == null ? null : from.getShortRepresentation(),
					to   == null ? null : to.getShortRepresentation());
			return Optional.empty();
		}

		// `from` and `to` must have a valid position.
		final SourcePosition fromPosition = from.getPosition();
		final SourcePosition toPosition = to.getPosition();
		Validate.isTrue(fromPosition.getFile().equals(toPosition.getFile()));

		try {
			final Optional<VCSFile.Range> range = createRange(from, to);
			if (range.isEmpty()) {
				log.warn("Skipping element range with unknown file: '{}' to '{}' in '{}'",
						from.getShortRepresentation(),
						to.getShortRepresentation(),
						fromPosition.getFile());
				return Optional.empty();
			}
			final CodeSmell codeSmell = new CodeSmell(getDefinition(), metrics,
					Collections.singletonList(range.get()), signature,
					summary);
			codeSmells.add(map(codeSmell, Arrays.asList(from, to)));
			return Optional.of(codeSmell);
		} catch (final IOException e) {
			log.warn("Skipping element range due to an unexpected IOException",
					e);
			return Optional.empty();
		}
	}

	/**
	 * Tries to add the given list of elements, their metrics, their signature
	 * (may be {@code null}), and their summary (a nullable value describing
	 * why the elements are considered a code smell}) as a {@link CodeSmell}
	 * with multiple ranges. On success, the newly created code smell is
	 * returned. On failure, an empty {@link Optional} is returned. Adding a
	 * list of elements may fail, for example, if any of its values is implicit
	 * or has no position (see {@link CtElement#isImplicit()} and
	 * {@link SourcePosition#NOPOSITION}).
	 *
	 * @param elements
	 * 		The list of elements to add as code smell.
	 * @param metrics
	 * 		The metrics of {@code elements}.
	 * @param signature
	 * 		The signature of {@code elements}. May be {@code null}.
	 * @param summary
	 * 		The summary describing why the elements are considered a code
	 * 		smell. May be {@code null}.
	 * @return
	 * 		The newly created code smell.
	 */
	protected Optional<CodeSmell> addCodeSmellWithMultiplePositions(
			final List<CtElement> elements, final List<Metric> metrics,
			final String signature, final String summary) {
		if (filter(elements, metrics)) {
			log.debug("Filtering out element list: '{}'", elements == null
					? null : elements.stream()
					.map(e -> e == null ? null : e.getShortRepresentation())
					.collect(Collectors.toList()));
			return Optional.empty();
		}

		// All elements in `elements` must have a valid position.
		List<VCSFile.Range> ranges = new ArrayList<>();
		try {
			for (final CtElement e : elements) {
				final Optional<VCSFile.Range> range = createRange(e);
				if (range.isEmpty()) {
					log.warn("Skipping element list due to an element with unknown file: '{}' in '{}'",
							e.getShortRepresentation(),
							e.getPosition().getFile());
					return Optional.empty();
				}
				ranges.add(range.get());
			}
		} catch (final IOException e) {
			log.warn("Skipping element list due to unexpected IOException",
					e);
			return Optional.empty();
		}
		final CodeSmell codeSmell = new CodeSmell(getDefinition(), metrics,
				ranges, signature, summary);
		codeSmells.add(map(codeSmell, elements));
		return Optional.of(codeSmell);
	}

	/**
	 * Allows subclasses to map (i.e. modify) a code smell before being added
	 * to {@link #codeSmells}. The default implementation simply returns the
	 * given code smell.
	 *
	 * @param codeSmell
	 * 		The code smell to map.
	 * @param elements
	 * 		The elements from which {@code codeSmell} was created. Contains at
	 * 		least one element.
	 * @return
	 * 		The mapped code smell.
	 */
	protected CodeSmell map(final @NonNull CodeSmell codeSmell,
			final @NonNull List<CtElement> elements) {
		return codeSmell;
	}

	private Optional<VCSFile.Range> createRange(final CtElement from,
			final CtElement to) throws IOException {
		final Optional<VCSFile.Range> begin = createRange(from);
		final Optional<VCSFile.Range> end = createRange(to);
		return begin.isEmpty() || end.isEmpty()
				? Optional.empty()
				: Optional.of(begin.get().getBegin().rangeTo(
						end.get().getEnd()));
	}

	/**
	 * Returns whether the given elements and metrics should be filtered.
	 *
	 * @param elements
	 * 		The elements to check.
	 * @param metrics
	 * 		The metrics to check
	 * @return
	 * 		{@code true} if the given elements and metrics should be filtered,
	 * 		{@code false} otherwise.
	 */
	private boolean filter(final List<CtElement> elements,
			final List<Metric> metrics) {
		return elements == null || elements.isEmpty() || metrics == null
				|| elements.stream().anyMatch(element ->
						element == null ||
						element.isImplicit() ||
						element.getPosition().equals(NOPOSITION))
				|| metrics.stream().anyMatch(Objects::isNull);
	}

	/**
	 * Returns whether the given element and metrics should be filtered.
	 *
	 * @param element
	 * 		The element to check.
	 * @param metrics
	 * 		The metrics to check.
	 * @return
	 * 		{@code true} if the given element and metrics should be filtered,
	 * 		{@code false} otherwise.
	 */
	private boolean filter(final CtElement element,
			final List<Metric> metrics) {
		return filter(Collections.singletonList(element), metrics);
	}
}
