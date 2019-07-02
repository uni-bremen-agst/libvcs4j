package de.unibremen.informatik.st.libvcs4j.spoon.codesmell;

import de.unibremen.informatik.st.libvcs4j.Revision;
import de.unibremen.informatik.st.libvcs4j.VCSFile;
import de.unibremen.informatik.st.libvcs4j.Validate;
import de.unibremen.informatik.st.libvcs4j.spoon.Scanner;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeInformation;

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
public abstract class CodeSmellDetector extends Scanner {

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

		final SourcePosition position = element.getPosition();
		final Optional<VCSFile> file = findFile(position);
		if (!file.isPresent()) {
			log.warn("Skipping element with unknown file: '{}' in '{}'",
					element.getShortRepresentation(), position.getFile());
			return Optional.empty();
		}

		try {
			final VCSFile.Range range = createRange(
					element, element, file.get());
			final CodeSmell codeSmell = new CodeSmell(getDefinition(), metrics,
					Collections.singletonList(range), signature, summary);
			codeSmells.add(codeSmell);
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
			final VCSFile.Range range = createRange(from, to, file.get());
			final CodeSmell codeSmell = new CodeSmell(getDefinition(), metrics,
					Collections.singletonList(range), signature, summary);
			codeSmells.add(codeSmell);
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
	public Optional<CodeSmell> addCodeSmellWithMultiplePositions(
			final List<CtElement> elements, final List<Metric> metrics,
			final String signature, final String summary) {
		if (filter(elements, metrics)) {
			log.debug("Filtering out element list: '{}'", elements == null
					? null : elements.stream()
					.map(e -> e == null ? null : e.getShortRepresentation())
					.collect(Collectors.toList()));
			return Optional.empty();
		}

		List<VCSFile.Range> ranges = new ArrayList<>();
		try {
			for (final CtElement e : elements) {
				final SourcePosition position = e.getPosition();
				final Optional<VCSFile> file = findFile(position);
				if (!file.isPresent()) {
					log.warn("Skipping element list due to an element with unknown file: '{}' in '{}'",
							e.getShortRepresentation(), position.getFile());
					return Optional.empty();
				}
				final VCSFile.Range range = createRange(e, e, file.get());
				ranges.add(range);
			}
		} catch (final IOException e) {
			log.warn("Skipping element list due to unexpected an IOException",
					e);
			return Optional.empty();
		}
		final CodeSmell codeSmell = new CodeSmell(getDefinition(), metrics,
				ranges, signature, summary);
		codeSmells.add(codeSmell);
		return Optional.of(codeSmell);
	}

	/**
	 * Tries to create a signature for {@code element}.
	 *
	 * @param element
	 * 		The element for which the signature is requested.
	 * @return
	 * 		The signature of {@code element}.
	 */
	public Optional<String> createSignature(final CtElement element) {
		try {
			if (element instanceof CtPackage) {
				final CtPackage pkg = (CtPackage) element;
				return Optional.of(pkg.getQualifiedName());
			} else if (element instanceof CtTypeInformation) {
				final CtTypeInformation info = (CtTypeInformation) element;
				return Optional.of(info.getQualifiedName());
			} else if (element instanceof CtConstructor) {
				final CtConstructor constructor = (CtConstructor) element;
				return Optional.of(constructor.getSignature());
			} else if (element instanceof CtExecutable) {
				final CtExecutable exe = (CtExecutable) element;
				return createSignature(exe.getParent(CtType.class))
						.map(s -> s + CtMethod.EXECUTABLE_SEPARATOR)
						.map(s -> s + exe.getSignature());
			} else if (element instanceof CtField) {
				final CtField field = (CtField) element;
				return createSignature(field.getDeclaringType())
						.map(s -> s + CtField.FIELD_SEPARATOR)
						.map(s -> s + field.getSimpleName());
			}
			return Optional.empty();
		} catch (final NullPointerException e) {
			return Optional.empty();
		}
	}

	/**
	 * Maps a Spoon position ({@link SourcePosition}) to the {@link VCSFile}
	 * that contains this position.
	 *
	 * @param position
	 * 		The spoon position to map.
	 * @return
	 * 		The {@link VCSFile} that contains {@code position}.
	 */
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
				.map(f -> f.orElse(null));
	}

	/**
	 * Returns the tab size of the file containing {@code element}.
	 *
	 * @param element
	 * 		The element for which the tab size is requested.
	 * @return
	 * 		The tab size of the file containing {@code element}.
	 */
	private int tabSizeOf(@NonNull final CtElement element) {
		return element.getFactory().getEnvironment().getTabulationSize();
	}

	private VCSFile.Range createRange(final CtElement from, final CtElement to,
			final VCSFile file) throws IOException {
		final int sourceStart = from.getPosition().getSourceStart();
		final int sourceEnd = to.getPosition().getSourceEnd();
		final VCSFile.Position begin = file
				.positionOf(sourceStart, tabSizeOf(from))
				.orElseThrow(() -> new IOException(String.format(
						"Begin position (%d) of element '%s' does not exist",
						sourceStart, from)));
		final VCSFile.Position end = file
				.positionOf(sourceEnd, tabSizeOf(to))
				.orElseThrow(() -> new IOException(String.format(
						"End position (%d) of element '%s' does not exist",
						sourceEnd, to)));
		return new VCSFile.Range(begin, end);
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
