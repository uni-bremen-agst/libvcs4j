package de.unibremen.informatik.st.libvcs4j.spoon;

import de.unibremen.informatik.st.libvcs4j.Revision;
import de.unibremen.informatik.st.libvcs4j.RevisionRange;
import de.unibremen.informatik.st.libvcs4j.VCSFile;
import lombok.NonNull;
import lombok.Value;
import spoon.reflect.CtModel;
import spoon.reflect.cu.CompilationUnit;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.factory.CompilationUnitFactory;
import spoon.reflect.reference.CtTypeReference;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Wraps a {@link CtModel} and {@link RevisionRange}.
 */
@Value
public class SpoonModel {
	@NonNull
	private final CtModel ctModel;
	@NonNull
	private final RevisionRange revisionRange;

	/**
	 * Returns all files referenced by {@code file}.
	 *
	 * @param file
	 * 		The file for which the referenced files are determined.
	 * @return
	 * 		The files referenced by {@code file}.
	 */
	public List<VCSFile> findReferencedFiles(@NonNull final VCSFile file) {
		final CompilationUnitFactory factory = ctModel.getRootPackage()
				.getFactory().CompilationUnit();
		final Map<String, CompilationUnit> compilationUnits = factory.getMap();

		// Make compilation unit paths canonical.
		final Map<Path, CompilationUnit> cPathToUnit = new HashMap<>();
		try {
			for (String path : compilationUnits.keySet()) {
				final CompilationUnit unit = compilationUnits.get(path);
				final Path cPath = new File(path).getCanonicalFile().toPath();
				cPathToUnit.put(cPath, unit);
			}
		} catch (final IOException e) {
			throw new UncheckedIOException(e);
		}
		// Reverse map of `cPathToUnit`.
		final Map<CompilationUnit, Path> unitToCPath = new IdentityHashMap<>();
		cPathToUnit.forEach((p, u) -> unitToCPath.put(u, p));

		// Make VCS file paths canonical.
		final Map<VCSFile, Path> fileToCPath = new IdentityHashMap<>();
		try {
			final Revision revision = revisionRange.getRevision();
			for (final VCSFile vFile : revision.getFiles()) {
				final Path cPath = vFile.toFile().getCanonicalFile().toPath();
				fileToCPath.put(vFile, cPath);
			}
		} catch (final IOException e) {
			throw new UncheckedIOException(e);
		}
		// Reverse map of `fileToCPath`.
		final Map<Path, VCSFile> cPathToFile = new HashMap<>();
		fileToCPath.forEach((f, p) -> cPathToFile.put(p, f));

		// Find files referenced by `file`.
		final List<CompilationUnit> referencedUnits =
				Optional.ofNullable(fileToCPath.get(file))
				.map(cPathToUnit::get)
				.map(cu -> cu.getDeclaredTypes().stream()
						.map(CtElement::getReferencedTypes)
						.flatMap(Collection::stream)
						.map(CtTypeReference::getDeclaration)
						.filter(Objects::nonNull)
						.map(CtElement::getPosition)
						.map(SourcePosition::getCompilationUnit)
						.collect(Collectors.toList()))
				.orElseGet(ArrayList::new);
		final List<VCSFile> referencedFiles = new ArrayList<>();
		for (final CompilationUnit unit : referencedUnits) {
			Optional.ofNullable(unitToCPath.get(unit))
					.map(cPathToFile::get)
					.ifPresent(referencedFiles::add);
		}
		return referencedFiles;
	}
}
