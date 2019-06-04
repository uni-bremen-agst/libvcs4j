package de.unibremen.informatik.st.libvcs4j.spoon;

import de.unibremen.informatik.st.libvcs4j.FileChange;
import de.unibremen.informatik.st.libvcs4j.Revision;
import de.unibremen.informatik.st.libvcs4j.RevisionRange;
import de.unibremen.informatik.st.libvcs4j.VCSFile;
import de.unibremen.informatik.st.libvcs4j.Validate;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spoon.Launcher;
import spoon.reflect.CtModel;
import spoon.reflect.cu.CompilationUnit;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.cu.position.NoSourcePosition;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeParameter;
import spoon.reflect.factory.CompilationUnitFactory;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.compiler.FileSystemFile;
import spoon.support.compiler.FilteringFolder;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static java.lang.System.currentTimeMillis;
import static spoon.SpoonModelBuilder.InputType;

/**
 * Allows to build and incrementally update a {@link CtModel} (which is managed
 * by the result class {@link SpoonModel}, see {@link #getModel()} and
 * {@link #update(RevisionRange)}).
 *
 * This class is somewhat similar to {@link spoon.IncrementalLauncher}, except
 * that it utilizes LibVCS4j's {@link RevisionRange} API to build and update a
 * model. Incremental updates can be enabled and disabled with
 * {@link #setIncremental(boolean)}.
 */
public class SpoonModelBuilder {

	/**
	 * The logger of this class.
	 */
	private static final Logger log =
			LoggerFactory.getLogger(SpoonModelBuilder.class);

	/**
	 * Indicates whether {@link #update(RevisionRange)} updates {@link #model}
	 * incrementally or if a model is build from scratch. The default value is
	 * {@code true}.
	 */
	@Getter
	@Setter
	private boolean incremental = true;

	/**
	 * The model of the last call of {@link #update(RevisionRange)}.
	 */
	private SpoonModel model = null;

	/**
	 * Path to the directory which stores the compiled .class files of the last
	 * call of {@link #update(RevisionRange)}.
	 */
	private Path tmpDir = null;

	/**
	 * Stores all files (as canonical paths) that weren't compiled by the last
	 * call of {@link #update(RevisionRange)}.
	 */
	private final Set<Path> notCompiled = new HashSet<>();

	/**
	 * Returns the last built {@link SpoonModel}.
	 *
	 * @return
	 * 		The last built {@link SpoonModel}.
	 */
	public Optional<SpoonModel> getModel() {
		return Optional.ofNullable(model);
	}

	/**
	 * Builds (or incrementally updates) {@link #model}.
	 *
	 * @param range
	 * 		The currently checked out range.
	 * @return
	 * 		The result class that contains {@link #model} and {@code range}.
	 * @throws NullPointerException
	 * 		If {@code range} is {@code null}.
	 * @throws BuildException
	 * 		If an error occurred while building the model.
	 */
	public SpoonModel update(@NonNull final RevisionRange range)
			throws BuildException {
		final long current = currentTimeMillis();
		if (tmpDir == null) {
			tmpDir = createTmpDir();
		}

		final Launcher launcher;
		if (model != null && incremental) {
			final CtPackage rootPackage = model.getCtModel().getRootPackage();
			final Factory factory = rootPackage.getFactory();
			launcher = new Launcher(factory);
			launcher.getModelBuilder().setSourceClasspath(tmpDir.toString());
			notCompiled.addAll(findPreviouslyNotCompiledSources());

			final List<String> input = range.getFileChanges()
					.stream()
					.filter(fc -> fc.getType() != FileChange.Type.REMOVE)
					.map(FileChange::getNewFile)
					.map(f -> f.orElseThrow(IllegalArgumentException::new))
					.filter(f -> f.getPath().endsWith(".java"))
					.map(VCSFile::getPath)
					.collect(Collectors.toList());

			final List<Path> filesToBuild = input.stream()
					.map(this::toCanonicalPath)
					.collect(Collectors.toList());

			filesToBuild.addAll(findReferencingFiles(filesToBuild));
			notCompiled.addAll(filesToBuild);

			// delete the removed files from the spoon model
			removeChangedTypes(extractOldFiles(
					range.getRemovedFiles()));

			// delete the relocated files from the spoon model
			removeChangedTypes(extractOldFiles(
					range.getRelocatedFiles()));

			// delete removed files from the set, because they do not exist
			// anymore
			extractOldFiles(range.getRemovedFiles()).stream()
					.map(this::toCanonicalPath)
					.forEach(notCompiled::remove);

			// delete relocated files from the set, because their path is
			// outdated
			extractOldFiles(range.getRelocatedFiles()).stream()
					.map(this::toCanonicalPath)
					.forEach(notCompiled::remove);

			// remove the changed classes from spoon model
			removeChangedTypes(notCompiled);

			// cleanup package tree.
			removeEmptyPackages(rootPackage);

			launcher.addInputResource(createInputSource(notCompiled));
			launcher.getModelBuilder().addCompilationUnitFilter(path ->
					!notCompiled.contains(toCanonicalPath(path)));
			launcher.getModelBuilder().compile(InputType.FILES);
			model.getCtModel().setBuildModelIsFinished(false);
		} else {
			launcher = new Launcher();
			// Add the checked out directory here, so we do not have to add
			// each single file.
			final Revision revision = range.getRevision();
			launcher.addInputResource(revision.getOutput().toString());
		}

		launcher.getEnvironment().setNoClasspath(true);
		launcher.setBinaryOutputDirectory(tmpDir.toString());
		try {
			launcher.getModelBuilder().compile(InputType.FILES);
			model = new SpoonModel(launcher.buildModel(), range);
			log.info("Model built in {} milliseconds",
					currentTimeMillis() - current);
			return model;
		} catch (final Exception e) {
			model = null;
			notCompiled.clear();
			log.info("Unable to build model", e);
			throw new BuildException(e);
		}
	}

	////////////////////////////// Util Methods ///////////////////////////////

	/**
	 * Transforms the given input into a {@link FilteringFolder}. Each path in
	 * {@code input} has to exist, be readable, and be a regular Java source
	 * file, otherwise it will not be added to the {@link FilteringFolder}.
	 *
	 * @param input
	 * 		The input source files.
	 * @return
	 * 		A {@link FilteringFolder} containing all files from the given
	 * 		input.
	 */
	private FilteringFolder createInputSource(final Collection<Path> input) {
		FilteringFolder folder = new FilteringFolder();
		input.stream()
				.map(Path::toFile)
				.filter(File::exists)
				.filter(File::canRead)
				.forEach(file -> folder.addFile(new FileSystemFile(file)));
		return folder;
	}

	/**
	 * Creates a temporary directory that will be deleted on shutdown.
	 *
	 * @return
	 * 		The path to the temporary directory.
	 */
	private Path createTmpDir() throws UncheckedIOException {
		try {
			final Path tmp = Files.createTempDirectory("spoon_model");
			FileUtils.forceDeleteOnExit(tmp.toFile());
			return tmp;
		} catch (final IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	/**
	 * Canonicalizes {@code path} using {@link File#getCanonicalFile()}. Wraps
	 * potential {@link IOException}s with an {@link UncheckedIOException}.
	 *
	 * @param path
	 * 		The path to canonicalize.
	 * @return
	 * 		The canonicalized version of {@code path}.
	 */
	private Path toCanonicalPath(final Path path) {
		try {
			return path.toFile().getCanonicalFile().toPath();
		} catch (final IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	/**
	 * Canonicalizes {@code path} using {@link #toCanonicalPath(Path)}.
	 *
	 * @param path
	 * 		The string path to canonicalize.
	 * @return
	 * 		The canonicalized version of {@code path} as {@link Path}.
	 */
	private Path toCanonicalPath(final String path) {
		return toCanonicalPath(Paths.get(path));
	}

	/**
	 * Canonicalizes {@code file} using {@link #toCanonicalPath(Path)}.
	 *
	 * @param file
	 * 		The file to canonicalize.
	 * @return
	 * 		The canonicalized version of {@code file} as {@link Path}.
	 */
	private Path toCanonicalPath(final File file) {
		return toCanonicalPath(file.toPath());
	}

	/**
	 * Removes all {@link CtType} objects from {@link #model#getModel()} that
	 * belong to one of the files of {@code paths}. These files have changed,
	 * so they need to be removed. The corresponding binary files are deleted
	 * as well.
	 *
	 * @param paths
	 * 		The changed source files.
	 */
	private void removeChangedTypes(final Collection<Path> paths) {
		Validate.validateState(model != null);

		final List<Path> files = paths.stream()
				.map(this::toCanonicalPath)
				.collect(Collectors.toList());
		final CompilationUnitFactory factory = model
				.getCtModel()
				.getRootPackage()
				.getFactory()
				.CompilationUnit();
		final Set<String> unitsToRemove = new HashSet<>();
		factory.getMap().forEach((p, __) -> {
			if (files.contains(toCanonicalPath(p)))	{
				unitsToRemove.add(p);
			}
		});
		unitsToRemove.stream()
				.map(factory::removeFromCache)
				.forEach(cu -> {
					cu.getBinaryFiles().forEach(File::delete);
					cu.getDeclaredTypes().forEach(CtElement::delete);
				});
	}

	/**
	 * Recursively removes all empty packages.
	 *
	 * @param pkg
	 * 		The root package of the package tree to process.
	 */
	private void removeEmptyPackages(final CtPackage pkg) {
		// Avoid ConcurrentModificationException.
		final Set<CtPackage> pkgs = new HashSet<>(pkg.getPackages());
		pkgs.forEach(this::removeEmptyPackages);
		if (pkg.getTypes().isEmpty()
				&& pkg.getPackages().isEmpty()
				&& !pkg.isUnnamedPackage()) {
			final SourcePosition pos = pkg.getPosition();
			// Does not contain a 'package-info.java' file.
			if (pos instanceof NoSourcePosition ||
					// Contained a 'package-info.java' file which has been
					// removed in this update step.
					(pos.getFile() != null && !pos.getFile().exists())) {
				pkg.delete();
			}
		}
	}

	/**
	 * Computes all previously not compiled classes. In Detail this means, that
	 * all source files are collected, which do not have one (or more)
	 * corresponding .class file at {@link #tmpDir}. All paths of the returned
	 * set are canonicalized.
	 *
	 * @return
	 * 		All sources files which were not compiled by the last call of
	 * 		{@link #update(RevisionRange)}.
	 */
	private Set<Path> findPreviouslyNotCompiledSources() {
		Validate.validateState(model != null);
		Validate.validateState(tmpDir != null);

		final String output = tmpDir.toString();
		final Set<Path> result = new HashSet<>();
		model.getCtModel().getAllTypes().parallelStream()
				.filter(t -> t.getPosition().isValidPosition())
				.forEach(type -> {
			final Path canonicalPath = toCanonicalPath(
					type.getPosition().getFile());
			final String pkg = type.getPackage().getQualifiedName();
			final File base = Paths.get(output, pkg.split("\\.")).toFile();
			if (getExpectedBinaryFiles(base, null, type)
					.stream().anyMatch(f -> !f.isFile())) {
				synchronized (result) {
					result.add(canonicalPath);
				}
			} else {
				synchronized (notCompiled) {
					notCompiled.remove(canonicalPath);
				}
			}
		});
		return result;
	}

	/**
	 * Extracts and returns all old files (see {@link FileChange#getOldFile()})
	 * from the given list of {@link FileChange} objects. The returned list of
	 * paths contains only canonicalized paths to java files.
	 *
	 * @param changes
	 * 		The list of {@link FileChange} objects to process.
	 * @return
	 * 		The List of old file paths.
	 */
	private List<Path> extractOldFiles(final List<FileChange> changes) {
		return changes.stream()
				.map(FileChange::getOldFile)
				.map(file -> file.orElseThrow(IllegalArgumentException::new))
				.filter(file -> file.getPath().endsWith(".java"))
				.map(VCSFile::toPath)
				.map(this::toCanonicalPath)
				.collect(Collectors.toList());
	}

	/**
	 * Returns all source files (as canonical paths) that have a reference to a
	 * file in {@code pFiles}. Ignores recursive references. That is, the
	 * returned set of paths does not contain any file of {@code pFiles}
	 * itself.
	 *
	 * @param
	 * 		pFiles The list of files (denoted as paths) to process.
	 * @return
	 * 		All source files that have a reference to a class in
	 * 		{@code pFiles}.
	 */
	private Set<Path> findReferencingFiles(final List<Path> pFiles) {
		Validate.validateState(model != null);

		final Map<String, CompilationUnit> unitMap = model
				.getCtModel()
				.getRootPackage()
				.getFactory()
				.CompilationUnit()
				.getMap();
		final List<Path> files = pFiles.stream()
				.map(this::toCanonicalPath)
				.collect(Collectors.toList());
		final List<CtTypeReference> typeReferencesOfFiles =
				unitMap.keySet().parallelStream()
						.filter(path -> files.contains(toCanonicalPath(path)))
						.map(unitMap::get)
						.map(CompilationUnit::getDeclaredTypes)
						.flatMap(Collection::stream)
						.map(CtType::getReference)
						.collect(Collectors.toList());

		final Set<Path> referencingFiles = new HashSet<>();
		model.getCtModel().getAllTypes().forEach(type ->
				type.getReferencedTypes().parallelStream()
						.filter(typeReferencesOfFiles::contains)
						.findAny()
						.map(CtElement::getPosition)
						.filter(SourcePosition::isValidPosition)
						.map(SourcePosition::getFile)
						.map(this::toCanonicalPath)
						.ifPresent(referencingFiles::add));
		return referencingFiles;
	}

	/**
	 * See https://github.com/INRIA/spoon/pull/2622 for more information.
	 * Recursively computes all expected binary (.class) files for {@code type}
	 * and all its inner/anonymous types. This method is used as a utility
	 * method by {@link #findPreviouslyNotCompiledSources()}.
	 *
	 * @param baseDir      The base directory of {@code type}. That is,
	 *                     the directory where the binary files of {@code type}
	 *                     are stored.
	 * @param nameOfParent The name of the binary file of the parent of
	 * 					   {@code type} without its extension (.class).
	 * 					   For instance, Foo$Bar. Pass {@code null} or
	 *                     an empty string if {@code type} has no parent.
	 * @param type         The root type to start the computation from.
	 * @return All binary (.class) files that should be available for {@code type}
	 * and all its inner/anonymous types.
	 */
	private List<File> getExpectedBinaryFiles(final File baseDir,
			final String nameOfParent, final CtType<?> type) {
		final List<File> binaries = new ArrayList<>();
		final String name = nameOfParent == null || nameOfParent.isEmpty()
				? type.getSimpleName()
				: nameOfParent + "$" + type.getSimpleName();
		binaries.add(new File(baseDir, name + ".class"));
		// Use 'getElements()' rather than 'getNestedTypes()' to also fetch
		// anonymous types.
		type.getElements(new TypeFilter<>(CtType.class)).stream()
				// Exclude 'type' itself.
				.filter(inner -> !inner.equals(type))
				// Exclude types that do not generate a binary file.
				.filter(inner -> !(inner instanceof CtPackage)
						&& !(inner instanceof CtTypeParameter))
				// Include only direct inner types.
				.filter(inner -> inner.getParent(CtType.class).equals(type))
				.forEach(inner -> binaries.addAll(getExpectedBinaryFiles(
						baseDir, name, inner)));
		return binaries;
	}
}
