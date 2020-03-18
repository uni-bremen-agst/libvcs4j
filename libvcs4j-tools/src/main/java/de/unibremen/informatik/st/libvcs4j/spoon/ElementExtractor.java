package de.unibremen.informatik.st.libvcs4j.spoon;

import de.unibremen.informatik.st.libvcs4j.VCSFile;
import lombok.NonNull;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.declaration.*;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Optional;

import static spoon.reflect.cu.SourcePosition.NOPOSITION;

/**
 * Base class for all scanners that need to extract AST nodes from a spoon
 * model and link their positions to the LibVCS4j data model.
 */
public abstract class ElementExtractor extends Scanner {

	@NonNull
	private final Environment environment;

	/**
	 * Creates a new element extractor with given environment.
	 *
	 * @param environment
	 * 		The environment to use.
	 * @throws NullPointerException
	 * 		If {@code environment} is {@code null}.
	 */
	public ElementExtractor(@NonNull final Environment environment)
			throws NullPointerException {
		super(environment.getCache());
		this.environment = environment;
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
	public Optional<VCSFile> findFile(final SourcePosition position) {
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
				.map(p -> environment.getRevision().getFiles().stream()
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
	 * @throws NullPointerException
	 * 		If {@code element} is {@code null}.
	 */
	public int tabSizeOf(@NonNull final CtElement element) {
		return element.getFactory().getEnvironment().getTabulationSize();
	}

	/**
	 * Tries to create a range from the given element. Returns an empty
	 * {@link Optional} if {@code element} is {@code null}, has no position, or
	 * if {@link #findFile(SourcePosition)} is unable to find the corresponding
	 * {@link VCSFile}.
	 *
	 * @param element
	 * 		The element to create the range for.
	 * @return
	 * 		The range of {@code element}.
	 * @throws IOException
	 * 		If an error occurred while parsing the file that contains
	 * 		{@code element}.
	 */
	public Optional<VCSFile.Range> createRange(final CtElement element)
			throws IOException {
		if (element == null || element.isImplicit() ||
				element.getPosition().equals(NOPOSITION)) {
			return Optional.empty();
		}
		final Optional<VCSFile> file = findFile(element.getPosition());
		if (file.isEmpty()) {
			return Optional.empty();
		}
		final int sourceStart = element.getPosition().getSourceStart();
		final int sourceEnd = element.getPosition().getSourceEnd();
		final int tabSize = tabSizeOf(element);
		final VCSFile.Position begin = file.get()
				.positionOf(sourceStart, tabSize)
				.orElseThrow(() -> new IOException(String.format(
						"Begin position (%d) of element '%s' does not exist",
						sourceStart, element)));
		final VCSFile.Position end = file.get()
				.positionOf(sourceEnd, tabSize)
				.orElseThrow(() -> new IOException(String.format(
						"End position (%d) of element '%s' does not exist",
						sourceEnd, element)));
		return Optional.of(new VCSFile.Range(begin, end));
	}
}
