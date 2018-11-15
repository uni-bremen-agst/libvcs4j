package de.unibremen.informatik.st.libvcs4j.spoon.codesmell;

import de.unibremen.informatik.st.libvcs4j.Revision;
import de.unibremen.informatik.st.libvcs4j.VCSFile;
import de.unibremen.informatik.st.libvcs4j.Validate;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.declaration.CtElement;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static spoon.reflect.cu.SourcePosition.NOPOSITION;

@AllArgsConstructor
public abstract class CodeSmellDetector {

	private static final Logger log =
			LoggerFactory.getLogger(CodeSmellDetector.class);

	@NonNull
	private final Revision revision;

	@NonNull
	private final List<CodeSmell> codeSmells = new ArrayList<>();

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
	 * Tries to add the given element and its metrics as a {@link CodeSmell}.
	 * On success, the newly created code smell is returned. On failure, an
	 * empty {@link Optional} is returned. Adding an element may fail, for
	 * example, if the element is implicit ({@link CtElement#isImplicit()}) or
	 * has no position ({@link SourcePosition#NOPOSITION}).
	 *
	 * @param element
	 * 		The element to add as a code smell.
	 * @param metrics
	 * 		The metrics of {@code element}.
	 * @return
	 * 		The newly created code smell.
	 */
	protected Optional<CodeSmell> addCodeSmell(final CtElement element,
			final List<Metric> metrics) {
		if (filter(element, metrics)) {
			log.debug("Filtering out element: '{}'",
					element.getShortRepresentation());
			return Optional.empty();
		}

		final SourcePosition position = element.getPosition();
		final Optional<VCSFile> file = findFile(position);
		if (!file.isPresent()) {
			log.warn("Skipping element with unknown file: '{}' in '{}'",
					element.getShortRepresentation(), position.getFile());
			return Optional.empty();
		}

		try {
			final VCSFile.Range range = new VCSFile.Range(file.get(),
					position.getSourceStart(), position.getSourceEnd(),
					tabSizeOf(element));
			final CodeSmell codeSmell = new CodeSmell(getDefinition(),
					metrics, Collections.singletonList(range));
			codeSmells.add(codeSmell);
			return Optional.of(codeSmell);
		} catch (final IOException e) {
			log.warn("Skipping element due to an unexpected IOException", e);
			return Optional.empty();
		}
	}

	/**
	 * Tries to add the given element range and its metric as a
	 * {@link CodeSmell}. On success, the newly created code smell is returned.
	 * On failure, an empty {@link Optional} is returned. Adding an element
	 * range may fail, for example, if {@code from} or {@code to} are implicit
	 * ({@link CtElement#isImplicit()}) or have no position
	 * ({@link SourcePosition#NOPOSITION}). However, if {@code from} and
	 * {@code to} are located in different files, an
	 * {@link IllegalArgumentException} is thrown.
	 *
	 * @param from
	 * 		The from element.
	 * @param to
	 * 		The to element.
	 * @param metrics
	 * 		The metrics of the code smell.
	 * @return
	 * 		The newly created code smell.
	 * @throws IllegalArgumentException
	 * 		If {@code from} and {@code to} are located in different files.
	 */
	protected Optional<CodeSmell> addCodeSmellRange(final CtElement from,
			final CtElement to, final List<Metric> metrics)
			throws IllegalArgumentException {
		if (filter(Arrays.asList(from, to), metrics)) {
			log.debug("Filtering out range: '{}' to '{}'",
					from.getShortRepresentation(),
					to.getShortRepresentation());
		}

		final SourcePosition fromPosition = from.getPosition();
		final SourcePosition toPosition = to.getPosition();
		Validate.isTrue(fromPosition.getFile().equals(toPosition.getFile()));
		final Optional<VCSFile> file = findFile(fromPosition);
		if (!file.isPresent()) {
			log.warn("Skipping element range with unknown file: '{}' to '{}' in '{}'",
					from.getShortRepresentation(), to.getShortRepresentation(),
					fromPosition.getFile());
			return Optional.empty();
		}

		try {
			final VCSFile.Range range = new VCSFile.Range(file.get(),
					fromPosition.getSourceStart(), toPosition.getSourceEnd(),
					tabSizeOf(from));
			final CodeSmell codeSmell = new CodeSmell(getDefinition(),
					metrics, Collections.singletonList(range));
			codeSmells.add(codeSmell);
			return Optional.of(codeSmell);
		} catch (final IOException e) {
			log.warn("Skipping element range due to an unexpected IOException",
					e);
			return Optional.empty();
		}
	}

	/**
	 * Tries to add the given list of elements and their metrics as a
	 * {@link CodeSmell} with multiple ranges ({@link CodeSmell#getRanges()}).
	 * On success, the newly created code smell is returned. On failure, an
	 * empty {@link Optional} is returned. Adding a list of elements may fail,
	 * for example, if any its element is implicit
	 * ({@link CtElement#isImplicit()}) or has not position
	 * ({@link SourcePosition#NOPOSITION}).
	 *
	 * @param elements
	 * 		The list of elements to add as code smell.
	 * @param metrics
	 * 		The metrics of {@code elements}.
	 * @return
	 * 		The newly created code smell.
	 */
	public Optional<CodeSmell> addCodeSmellWithMultiplePositions(
			final List<CtElement> elements, final List<Metric> metrics) {
		if (elements.isEmpty()) {
			log.debug("Skipping empty element list");
		} else if (filter(elements, metrics)) {
			log.debug("Filtering out element list: '{}'", elements.stream()
					.map(e -> e != null ? e.getShortRepresentation() : null)
					.collect(Collectors.toList()));
		}

		List<VCSFile.Range> ranges = new ArrayList<>();
		try {
			final int tabSize = tabSizeOf(elements.get(0));
			for (final CtElement e : elements) {
				final SourcePosition position = e.getPosition();
				final Optional<VCSFile> file = findFile(position);
				if (!file.isPresent()) {
					log.warn("Skipping element list due to an element with unknown file: '{}' in '{}'",
							e.getShortRepresentation(), position.getFile());
					return Optional.empty();
				}
				final VCSFile.Range range = new VCSFile.Range(file.get(),
						position.getSourceStart(), position.getSourceEnd(),
						tabSize);
				ranges.add(range);
			}
		} catch (final IOException e) {
			log.warn("Skipping element list due to unexpected an IOException",
					e);
			return Optional.empty();
		}
		final CodeSmell codeSmell = new CodeSmell(getDefinition(), metrics,
				ranges);
		codeSmells.add(codeSmell);
		return Optional.of(codeSmell);
	}

	private Optional<VCSFile> findFile(final SourcePosition position) {
		return Optional.ofNullable(position)
				// Make position canonical.
				.map(p -> {
					try {
						return p.getFile().getCanonicalFile();
					} catch (final IOException e) {
						throw new UncheckedIOException(e);
					}
				})
				// Compare with canonicalized vcs files.
				.map(p -> revision.getFiles().stream()
						.filter(f -> {
							try {
								return f.toFile().getCanonicalFile().equals(p);
							} catch (final IOException e) {
								throw new UncheckedIOException(e);
							}
						})
						.findFirst())
				.map(f -> f.orElseGet(null));
	}

	private int tabSizeOf(@NonNull final CtElement element) {
		return element.getFactory().getEnvironment().getTabulationSize();
	}

	private boolean filter(final List<CtElement> elements,
			final List<Metric> metrics) {
		return elements == null || elements.isEmpty() || metrics == null
				|| elements.stream().anyMatch(element ->
						element == null ||
						element.isImplicit() ||
						element.getPosition().equals(NOPOSITION))
				|| metrics.stream().anyMatch(Objects::isNull);
	}

	private boolean filter(final CtElement element,
			final List<Metric> metrics) {
		return filter(Collections.singletonList(element), metrics);
	}
}
