package de.unibremen.informatik.st.libvcs4j.spoon;

import de.unibremen.informatik.st.libvcs4j.RevisionRange;
import de.unibremen.informatik.st.libvcs4j.Validate;
import de.unibremen.informatik.st.libvcs4j.FileChange;
import de.unibremen.informatik.st.libvcs4j.VCSFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spoon.Launcher;
import spoon.SpoonModelBuilder;
import spoon.reflect.CtModel;
import spoon.reflect.cu.CompilationUnit;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeParameter;
import spoon.reflect.factory.CompilationUnitFactory;
import spoon.reflect.factory.Factory;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.compiler.FileSystemFile;
import spoon.support.compiler.FilteringFolder;
import spoon.support.reflect.declaration.CtModuleImpl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.FileVisitResult;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static java.lang.System.currentTimeMillis;

public class SpoonModel {
	private static final Logger LOGGER = LoggerFactory.getLogger(SpoonModel.class);

	private static final boolean NO_CLASSPATH = true;

	private Optional<CtModel> model = Optional.empty();

	private File temporaryDirectory = null;

	private Set<String> filesNotCompiledSinceLastUpdate = new HashSet<>();

	private enum FileType {OLD_FILES, NEW_FILES}

	public Optional<CtModel> getModel() {
		return model;
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
		final FileType OLD_FILE = FileType.OLD_FILES;

		if (temporaryDirectory == null) {
			createTmpDir();
		}

		if (model.isPresent()) {

			final Factory factory = model.get().getRootPackage().getFactory();
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
			final List<String> filesToBuild = makeStringPathsCanonical(input);

			filesToBuild.addAll(getReferencedTypes(filesToBuild));
			filesNotCompiledSinceLastUpdate.addAll(filesToBuild);

			//delete the removed files from the spoon model
			removeChangedTypes(getAllFilesFromFileChanges(
					revisionRange.getRemovedFiles(), OLD_FILE));

			//delete the relocated files from the spoon model
			removeChangedTypes(getAllFilesFromFileChanges(
					revisionRange.getRelocatedFiles(), OLD_FILE));

			//delete removed files from the set, because they do not exist anymore
			getAllFilesFromFileChanges(revisionRange.getRemovedFiles(), OLD_FILE)
					.forEach(path -> filesNotCompiledSinceLastUpdate.remove(path));

			//delete relocated files from the set, because their path is outdated
			getAllFilesFromFileChanges(revisionRange.getRelocatedFiles(), OLD_FILE)
					.forEach(path -> filesNotCompiledSinceLastUpdate.remove(path));

			//remove the changed classes from spoon model
			removeChangedTypes(filesNotCompiledSinceLastUpdate);

			launcher.addInputResource(
					createInputSource(filesNotCompiledSinceLastUpdate));
			launcher.getModelBuilder()
					.addCompilationUnitFilter(path -> !filesToBuild.contains(path));
			launcher.getModelBuilder()
					.compile(SpoonModelBuilder.InputType.FILES);
			model.get().setBuildModelIsFinished(false);
			try {
				model = Optional.of(launcher.buildModel());
			} catch (Exception e) {
				model = Optional.empty();
				filesNotCompiledSinceLastUpdate.clear();
			}
			//((CtModuleImpl) launcher.getModel().getUnnamedModule()).setRootPackage(launcher.getModel().getRootPackage());

		} else {

			final Launcher launcher = new Launcher();
			launcher.setBinaryOutputDirectory(temporaryDirectory);
			launcher.getEnvironment().setNoClasspath(NO_CLASSPATH);
			//Add the checked out directory here,
			//so we do not have to add each single file
			launcher.addInputResource(
					revisionRange.getRevision().getOutput().toString());
			launcher.getModelBuilder().compile(SpoonModelBuilder.InputType.FILES);
			model = Optional.of(launcher.buildModel());

		}
		LOGGER.info(format("Model built in %d milliseconds",
				currentTimeMillis() - current));
		return model;
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
	private FilteringFolder createInputSource(final Collection<String> input) {
		FilteringFolder folder = new FilteringFolder();
		input.forEach(path -> {
			final Path p = Paths.get(path);
			if (Files.exists(p) && Files.isReadable(p) && Files.isRegularFile(p)) {
				folder.addFile(new FileSystemFile(p.toFile()));
			}
		});
		return folder;
	}

	/**
	 * Removes all source files from {@link CtModel} that are given as input.
	 * These files have changed, so they need to be removed from the
	 * {@link CtModel}. The corresponding binary files are getting deleted as well.
	 *
	 * @param pFileChanges The given collection with absolute paths to source files.
	 */
	private void removeChangedTypes(final Collection<String> pFileChanges) {
		final CtPackage rootPackage = model.get().getRootPackage();
		final CompilationUnitFactory cuFactory = rootPackage.getFactory().CompilationUnit();

		pFileChanges.stream()
				.filter(sourceFile -> sourceFile.endsWith(".java"))
				.forEach(path -> {
					if (cuFactory.getMap().containsKey(path)) {
						final CompilationUnit cu = cuFactory.removeFromCache(path);
						cu.getDeclaredTypes().forEach(type -> {
							final CtPackage pkg = type.getPackage();
							type.delete();
							if (pkg.getTypes().isEmpty()
									&& pkg.getPackages().isEmpty()
									&& !pkg.isUnnamedPackage()) {
								pkg.delete();
							}
						});
						cu.getBinaryFiles().forEach(this::deleteFile);
					}
				});
	}

	/**
	 * Computes all previously not compiled classes. In Detail this means,
	 * that all source files get collected, which do not have one (or more)
	 * corresponding class files on the hard drive.
	 *
	 * @return A list from all the sources files, which did not get compiled
	 * 			correctly in the previous iteration.
	 */
	private List<String> findPreviouslyNotCompiledSources() {
		final List<String> filesNeedToBeRebuild = new ArrayList<>();
		final CtModel currentModel = model.get();
		List<File> expected;
		Validate.notNull(temporaryDirectory);
		final String output = temporaryDirectory.getAbsolutePath();
		for (final CtType type : currentModel.getAllTypes()) {
			final File base = Paths.get(
			        output,
                    type.getPackage().getQualifiedName().replace(".", File.separator))
                    .toFile();
			expected = getExpectedBinaryFiles(base, null, type);

			if (expected.stream().anyMatch(file -> !file.isFile())) {
				filesNeedToBeRebuild.add(
						type.getPosition().getFile().getAbsolutePath());

				//if a class needs to be recompiled, rebuild all classes, that refer to this class
				for (final CtType oldType : currentModel.getAllTypes()) {
					if (oldType.getReferencedTypes().contains(type.getReference())) {
						filesNeedToBeRebuild.add(
								oldType.getPosition().getFile().getAbsolutePath()
						);
					}
				}
			} else {
				filesNotCompiledSinceLastUpdate.remove(
						type.getPosition().getFile().getAbsolutePath()
				);
			}

		}

		return filesNeedToBeRebuild;
	}

	/**
	 * Deletes the given file. It only gets deleted, if it exists, is readable
     * and is writable.
	 *
	 * @param fileToDelete The file that needs to be deleted.
	 */
	private void deleteFile(final File fileToDelete) {
		if (Files.exists(fileToDelete.toPath())
                && Files.isReadable(fileToDelete.toPath())
                && Files.isWritable(fileToDelete.toPath())) {
			fileToDelete.delete();
		}
	}

	/**
	 * Creates a temporary directory in which the class files get stored.
	 * This temporary directory will be deleted on shutdown
	 * (see {@link #recursiveDeleteOnShutdownHook(Path)}).
	 */
	private void createTmpDir() {
		try {
			temporaryDirectory =
					Files.createTempDirectory("tmpClassDirectory").toFile();
			recursiveDeleteOnShutdownHook(temporaryDirectory.toPath());
		} catch (IOException e) {
			LOGGER.error("Failed creating temporary directory");
			e.printStackTrace();
		}
	}

	/**
	 * This method adds a shutdown hook to the VM. This hook deletes the given directory and
	 * all its subdirectories/files.
	 *
	 * @param path The absolute path to the directory that should be deleted on shutdown.
	 */
	private void recursiveDeleteOnShutdownHook(final Path path) {
		Runtime.getRuntime().addShutdownHook(new Thread(
				() -> {
					LOGGER.info("About to delete " + path.toString());
					try {
						Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
							@Override
							public FileVisitResult visitFile(final Path file,
															 @SuppressWarnings("unused") BasicFileAttributes attrs)
									throws IOException {
								Files.delete(file);
								return FileVisitResult.CONTINUE;
							}

							@Override
							public FileVisitResult postVisitDirectory(final Path dir,
																	  final IOException e)
									throws IOException {
								if (e == null) {
									Files.delete(dir);
									return FileVisitResult.CONTINUE;
								}
								// directory iteration failed
								throw e;
							}
						});
					} catch (IOException e) {
						throw new RuntimeException("Failed to delete " + path, e);
					}
				}));
	}

	/**
	 * Returns all old or new files from the given list of {@link FileChange}
	 * objects. Only java source files will be contained in the output.
	 * The parameter {@code fileType} indicates if the old or new files
	 * should be extracted.
	 *
	 * @param fileChanges The list of {@link FileChange} objects.
	 * @param fileType    Indicates which file will be extracted {@link FileType}.
	 * @return List of strings containing all paths to the old or new files
	 */
	private List<String> getAllFilesFromFileChanges(final List<FileChange> fileChanges,
                                                    final FileType fileType) {
		return fileChanges
				.stream()
				.map(fileType == FileType.OLD_FILES
                        ? FileChange::getOldFile
                        : FileChange::getNewFile)
				.map(vcsFile -> vcsFile.orElseThrow(IllegalArgumentException::new))
				.filter(sourceFile -> sourceFile.getPath().endsWith(".java"))
				.map(VCSFile::getPath)
				.collect(Collectors.toList());
	}

	/**
	 *
	 * @param pFileChanges
	 * @return
	 */
	private List<String> getReferencedTypes(final Collection<String> pFileChanges) {
		final CtModel currentModel = model.get();
		final List<String> referencedTypes = new ArrayList<>();
		final Map<String, CompilationUnit> compilationUnitMap =
				currentModel.getRootPackage().getFactory().CompilationUnit().getMap();
		for (final String path : pFileChanges) {
			final CompilationUnit cu;
			if (compilationUnitMap.containsKey(path)) {
				cu = compilationUnitMap.get(path);
			} else {
				continue;
			}
			for (final CtType type : currentModel.getAllTypes()) {
				if (type.getReferencedTypes().contains(cu.getMainType().getReference())) {
					referencedTypes.add(type.getPosition().getFile().getAbsolutePath());
				}
			}
		}
		return referencedTypes;
	}

	/**
	 * Converts the given list of strings (these are actually paths to files)
	 * to canonical paths.
	 *
	 * @param paths The given paths (as a list of strings).
	 * @return The canonical paths.
	 */
	private List<String> makeStringPathsCanonical(final List<String> paths) {
		final List<String> canonicalStringPaths = new ArrayList<>(paths.size());
		for (final String path : paths) {
			try {
				canonicalStringPaths.add(new File(path).getCanonicalPath());
			} catch (IOException e) {
				LOGGER.error("Error making path " + path + " canonical");
			}
		}
		return canonicalStringPaths;
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
                                              final String nameOfParent,
                                              final CtType<?> type) {
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
