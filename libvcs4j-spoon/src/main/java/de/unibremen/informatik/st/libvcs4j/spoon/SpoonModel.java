package de.unibremen.informatik.st.libvcs4j.spoon;

import de.unibremen.informatik.st.libvcs4j.FileChange;
import de.unibremen.informatik.st.libvcs4j.RevisionRange;
import de.unibremen.informatik.st.libvcs4j.VCSFile;
import de.unibremen.informatik.st.libvcs4j.Validate;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spoon.Launcher;
import spoon.SpoonModelBuilder;
import spoon.reflect.CtModel;
import spoon.reflect.cu.CompilationUnit;
import spoon.reflect.cu.SourcePosition;
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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static java.lang.System.currentTimeMillis;

public class SpoonModel {
	private static final Logger LOGGER = LoggerFactory.getLogger(SpoonModel.class);

	private static final boolean NO_CLASSPATH = true;

	private CtModel model = null;

	private File temporaryDirectory = null;

	private Set<Path> filesNotCompiledSinceLastUpdate = new HashSet<>();

	public Optional<CtModel> getModel() {
		return Optional.ofNullable(model);
	}

	/**
	 * Builds the underlying spoon model with the given revision incrementally.
	 * In addition, each increment identifies files that could not be compiled
	 * in the previous step. This method also tries to recompile them in the
	 * next increment.
	 *
	 * @param revisionRange The current checked out revision.
	 *                      Contains a list of {@link FileChange} objects,
	 *                      from which the spoon model will be build.
	 * @return The updated underlying {@link CtModel}. May be an empty
	 * 			{@link Optional}, if spoon fails to build the model.
	 */
	public Optional<CtModel> update(final RevisionRange revisionRange) {
		Validate.notNull(revisionRange);
		final long current = currentTimeMillis();

		if (temporaryDirectory == null) {
			createTmpDir();
		}

		if (model != null) {

			final Factory factory = model.getRootPackage().getFactory();
			final Launcher launcher = new Launcher(factory);
			launcher.getEnvironment().setNoClasspath(NO_CLASSPATH);
			launcher.getModelBuilder()
					.setSourceClasspath(temporaryDirectory.getPath());
			launcher.setBinaryOutputDirectory(temporaryDirectory);
			filesNotCompiledSinceLastUpdate
					.addAll(findPreviouslyNotCompiledSources());

			final List<String> input = revisionRange.getFileChanges()
					.stream()
					.filter(fileChange -> fileChange.getType() != FileChange.Type.REMOVE)
					.map(FileChange::getNewFile)
					.map(vcsFile -> vcsFile.orElseThrow(IllegalArgumentException::new))
					.filter(sourceFile -> sourceFile.getPath().endsWith(".java"))
					.map(VCSFile::getPath)
					.collect(Collectors.toList());

			final List<Path> filesToBuild = input.stream()
					.map(this::toCanonicalPath)
					.collect(Collectors.toList());

			filesToBuild.addAll(findReferencingFiles(filesToBuild));
			filesNotCompiledSinceLastUpdate.addAll(filesToBuild);

			//delete the removed files from the spoon model
			removeChangedTypes(extractOldFiles(
					revisionRange.getRemovedFiles()));

			//delete the relocated files from the spoon model
			removeChangedTypes(extractOldFiles(
					revisionRange.getRelocatedFiles()));

			//delete removed files from the set, because they do not exist anymore
			extractOldFiles(revisionRange.getRemovedFiles())
					.forEach(path -> filesNotCompiledSinceLastUpdate.remove(path));

			//delete relocated files from the set, because their path is outdated
			extractOldFiles(revisionRange.getRelocatedFiles())
					.forEach(path -> filesNotCompiledSinceLastUpdate.remove(path));

			//remove the changed classes from spoon model
			removeChangedTypes(filesNotCompiledSinceLastUpdate);

			launcher.addInputResource(
					createInputSource(filesNotCompiledSinceLastUpdate));
			launcher.getModelBuilder()
					.addCompilationUnitFilter(path ->
							!filesToBuild.contains(toCanonicalPath(path)));
			launcher.getModelBuilder()
					.compile(SpoonModelBuilder.InputType.FILES);
			model.setBuildModelIsFinished(false);
			try {
				model = launcher.buildModel();
			} catch (Exception e) {
				model = null;
				filesNotCompiledSinceLastUpdate.clear();
			}

		} else {

			final Launcher launcher = new Launcher();
			launcher.setBinaryOutputDirectory(temporaryDirectory);
			launcher.getEnvironment().setNoClasspath(NO_CLASSPATH);
			//Add the checked out directory here,
			//so we do not have to add each single file
			launcher.addInputResource(
					revisionRange.getRevision().getOutput().toString());
			launcher.getModelBuilder().compile(SpoonModelBuilder.InputType.FILES);
			model = launcher.buildModel();

		}
		LOGGER.info(format("Model built in %d milliseconds",
				currentTimeMillis() - current));
		return Optional.ofNullable(model);
	}

	/**
	 * Transforms the given Input into a {@link FilteringFolder}.
	 * Each path in the given collection has to exist, be readable and be a
	 * regular Java source file, otherwise it will not be added to the
	 * {@link FilteringFolder}.
	 *
	 * @param input The given collection with absolute paths to source files.
	 * @return A {@link FilteringFolder} containing all files from the given input.
	 */
	private FilteringFolder createInputSource(final Collection<Path> input) {
		FilteringFolder folder = new FilteringFolder();
		input.stream()
				.filter(Files::isRegularFile)
				.filter(Files::isReadable)
				.map(Path::toFile)
				.forEach(file -> folder.addFile(new FileSystemFile(file)));
		return folder;
	}

	/**
	 * Creates a temporary directory in which the class files get stored.
	 * This temporary directory will be deleted on shutdown
	 * (see {@link #recursiveDeleteOnShutdownHook(File)}).
	 */
	private void createTmpDir() {
		try {
			temporaryDirectory =
					Files.createTempDirectory("tmpClassDirectory").toFile();
			recursiveDeleteOnShutdownHook(temporaryDirectory);
		} catch (IOException e) {
			LOGGER.error("Failed creating temporary directory");
			e.printStackTrace();
		}
	}

	/**
	 * This method adds a shutdown hook to the VM. This hook deletes the given directory and
	 * all its subdirectories/files.
	 *
	 * @param file The {@link File} object representing the directory
	 *             that should be deleted on shutdown.
	 */
	private void recursiveDeleteOnShutdownHook(final File file) {
		Runtime.getRuntime().addShutdownHook(new Thread(
				() -> {
					LOGGER.info("About to delete " + file.getAbsolutePath());
					try {
						FileUtils.deleteDirectory(file);
					} catch (IOException e) {
						LOGGER.error("Failed to delete directory "
								+ file.getAbsolutePath());
					}
				}));
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
	 * Removes all source files from {@link CtModel} that are given as input.
	 * These files have changed, so they need to be removed from the
	 * {@link CtModel}. The corresponding binary files are deleted as well.
	 *
	 * @param pPaths The given collection with absolute paths to source files.
	 */
	private void removeChangedTypes(final Collection<Path> pPaths) {
		Validate.validateState(model != null);

		final List<Path> paths = pPaths.stream()
				.map(this::toCanonicalPath)
				.collect(Collectors.toList());
		final CompilationUnitFactory factory = model
				.getRootPackage()
				.getFactory()
				.CompilationUnit();
		final Set<String> unitsToRemove = new HashSet<>();
		factory.getMap().forEach((p, __) -> {
			if (paths.contains(toCanonicalPath(p)))	{
				unitsToRemove.add(p);
			}
		});
		unitsToRemove.stream()
				.map(factory::removeFromCache)
				.forEach(cu -> {
					cu.getDeclaredTypes().forEach(type -> {
						final CtPackage pkg = type.getPackage();
						type.delete();
						if (pkg.getTypes().isEmpty()
								&& pkg.getPackages().isEmpty()
								&& !pkg.isUnnamedPackage()) {
							pkg.delete();
						}
					});
					cu.getBinaryFiles().forEach(File::delete);
				});
	}

	/**
	 * Computes all previously not compiled classes. In Detail this means,
	 * that all source files get collected, which do not have one (or more)
	 * corresponding class file on the hard drive. All paths of the returned
	 * set of paths are canonicalized.
	 *
	 * @return All sources files which did not get compiled correctly in the
	 * previous iteration.
	 */
	private Set<Path> findPreviouslyNotCompiledSources() {
		Validate.validateState(model != null);
		Validate.validateState(temporaryDirectory != null);

		final String output = temporaryDirectory.getAbsolutePath();

		final Set<Path> result = new HashSet<>();
		for (final CtType type : model.getAllTypes()) {
			final Path canonicalPath = toCanonicalPath(
					type.getPosition().getFile());

			final String pkg = type.getPackage().getQualifiedName();
			final File base = Paths.get(output, pkg.split(".")).toFile();

			if (getExpectedBinaryFiles(base, null, type)
					.stream().anyMatch(f -> !f.isFile())) {
				result.add(canonicalPath);
				result.addAll(findReferencingFiles(
						Collections.singletonList(canonicalPath)));
			} else {
				filesNotCompiledSinceLastUpdate.remove(canonicalPath);
			}
		}
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
	 * @param pFiles The list with paths to source pFiles.
	 * @return All source files that have a reference to a class in
	 * {@code pFiles}.
	 */
	private Set<Path> findReferencingFiles(final List<Path> pFiles) {
		Validate.validateState(model != null);

		final Map<String, CompilationUnit> unitMap = model
				.getRootPackage()
				.getFactory()
				.CompilationUnit()
				.getMap();
		final List<Path> files = pFiles.stream()
				.map(this::toCanonicalPath)
				.collect(Collectors.toList());
		final List<CtTypeReference> typeReferencesOfFiles =
				unitMap.keySet().stream()
						.filter(path -> files.contains(toCanonicalPath(path)))
						.map(unitMap::get)
						.map(CompilationUnit::getDeclaredTypes)
						.flatMap(Collection::stream)
						.map(CtType::getReference)
						.collect(Collectors.toList());

		final Set<Path> referencingFiles = new HashSet<>();
		model.getAllTypes().forEach(type -> type.getReferencedTypes().stream()
				.filter(typeReferencesOfFiles::contains)
				.findAny()
				.map(CtElement::getPosition)
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
				.forEach(inner -> {
					binaries.addAll(getExpectedBinaryFiles(
							baseDir, name, inner));
				});
		return binaries;
	}
}
