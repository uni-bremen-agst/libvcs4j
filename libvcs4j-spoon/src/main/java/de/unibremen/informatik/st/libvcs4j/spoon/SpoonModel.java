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
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeParameter;
import spoon.reflect.factory.CompilationUnitFactory;
import spoon.reflect.factory.Factory;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.compiler.FileSystemFile;
import spoon.support.compiler.FilteringFolder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.FileVisitResult;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.Collection;
import java.util.Set;
import java.util.HashSet;
import java.util.Optional;
import java.util.ArrayList;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static java.lang.System.currentTimeMillis;


public class SpoonModel {
    private static final Logger LOGGER = LoggerFactory.getLogger(SpoonModel.class);

    private Optional<CtModel> model = Optional.empty();

    private File temporaryDirectory = null;

    private boolean noClasspath = true;

    private Set<String> filesNotCompiledSinceLastUpdate = new HashSet<>();

    private int numberFilesWithoutClassFiles = 0;

    private int numberOfAllSourceFiles = 0;

    public Optional<CtModel> getModel() {
        return model;
    }

    public void update(final RevisionRange revisionRange) {
        Validate.notNull(revisionRange);
        final long current = currentTimeMillis();

        if (temporaryDirectory == null) {
            createTmpDir();
        }

        if (model.isPresent()) {

            final Factory factory = model.get().getRootPackage().getFactory();
            final Launcher launcher = new Launcher(factory);
            launcher.getEnvironment().setNoClasspath(noClasspath);
            launcher.getModelBuilder().setSourceClasspath(temporaryDirectory.getPath());
            launcher.setBinaryOutputDirectory(temporaryDirectory);
            filesNotCompiledSinceLastUpdate.addAll(findUncompiledSources(factory));
            print(filesNotCompiledSinceLastUpdate);
            numberFilesWithoutClassFiles = filesNotCompiledSinceLastUpdate.size();
            numberOfAllSourceFiles = factory.CompilationUnit().getMap().values().size();
            LOGGER.info("" + numberFilesWithoutClassFiles + " of " + numberOfAllSourceFiles + " were not compiled");

            final List<String> filesToBuild = revisionRange.getFileChanges()
                    .stream()
                    .filter(fileChange -> fileChange.getType() != FileChange.Type.REMOVE
                            && fileChange.getType() != FileChange.Type.ADD)
                    .map(FileChange::getNewFile)
                    .map(vcsFile -> vcsFile.orElseThrow(IllegalArgumentException::new))
                    .filter(sourceFile -> sourceFile.getPath().endsWith(".java"))
                    .map(VCSFile::getPath)
                    .collect(Collectors.toList());

            filesNotCompiledSinceLastUpdate.addAll(filesToBuild);

            removeChangedTypes(getAllFilesFromFileChanges(revisionRange.getRemovedFiles(), true));

            getAllFilesFromFileChanges(revisionRange.getRemovedFiles(), true)
                    .forEach(path -> filesNotCompiledSinceLastUpdate.remove(path));

            getAllFilesFromFileChanges(revisionRange.getRelocatedFiles(), true)
                    .forEach(path -> filesNotCompiledSinceLastUpdate.remove(path));

            removeChangedTypes(filesNotCompiledSinceLastUpdate);

            filesNotCompiledSinceLastUpdate.addAll(getAllFilesFromFileChanges(revisionRange.getAddedFiles(), false));

            filesToBuild.addAll(getAllFilesFromFileChanges(revisionRange.getAddedFiles(), false));

            launcher.addInputResource(createInputSource(filesNotCompiledSinceLastUpdate));
            launcher.getModelBuilder().addCompilationUnitFilter(path -> !filesToBuild.contains(path));
            launcher.getModelBuilder().compile(SpoonModelBuilder.InputType.FILES);
            model.get().setBuildModelIsFinished(false);
            try {
                model = Optional.of(launcher.buildModel());
            } catch (Exception e) {
                model = Optional.empty();
                filesNotCompiledSinceLastUpdate.clear();
                numberOfAllSourceFiles = 0;
                numberFilesWithoutClassFiles = 0;
            }

        } else {

            final Launcher launcher = new Launcher();
            launcher.setBinaryOutputDirectory(temporaryDirectory);
            launcher.getEnvironment().setNoClasspath(noClasspath);
            //Add the checked out directory here, so we do not have to add each single file
            launcher.addInputResource(revisionRange.getRevision().getOutput().toString());
            launcher.getModelBuilder().compile(SpoonModelBuilder.InputType.FILES);
            model = Optional.of(launcher.buildModel());

        }
        LOGGER.info(format("Model built in %d milliseconds", currentTimeMillis() - current));
    }

    /**
     *
     * @param input
     * @return
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
     *
     * @param pFileChanges
     */
    private void removeChangedTypes(final Collection<String> pFileChanges) {
        final CtPackage rootPackage = model.get().getRootPackage();
        final CompilationUnitFactory cuFactory = rootPackage.getFactory().CompilationUnit();

        pFileChanges.stream()
                .filter(sourceFile -> sourceFile.endsWith(".java"))
                .forEach(path -> {
                    if (cuFactory.getMap().containsKey(path)) {
                        final CompilationUnit cu = cuFactory.removeFromCache(path);
                        cu.getDeclaredTypes().forEach(CtElement::delete);
                        cu.getBinaryFiles().forEach(this::deleteFile);
                    }
                });
    }

    /**
     *
     * @param factory
     * @return
     */
    private List<String> findUncompiledSources(final Factory factory) {
        final List<String> filesNeedToBeRebuild = new ArrayList<>();
        final CtModel currentModel = model.get();
        List<File> expected;
        final String output = factory
                .getEnvironment()
                .getBinaryOutputDirectory();
            for (final CtType type : currentModel.getAllTypes()) {
                final File base = Paths.get(output, type.getPackage().getQualifiedName().replace(".", File.separator)).toFile();
                expected = getExpectedBinaryFiles(base, null, type);

                if (expected.stream().anyMatch(file -> !file.isFile())) {
                    filesNeedToBeRebuild.add(type.getPosition().getFile().getAbsolutePath());

                    //if a class needs to be recompiled, rebuild all classes, that refer to this class
                    for (final CtType oldType : currentModel.getAllTypes()) {
                        if (oldType.getReferencedTypes().contains(type.getReference())) {
                            filesNeedToBeRebuild.add(oldType.getPosition().getFile().getAbsolutePath());
                        }
                    }
                } else {
                    filesNotCompiledSinceLastUpdate.remove(type.getPosition().getFile().getAbsolutePath());
                }

            }

        return filesNeedToBeRebuild;
    }

    /**
     *
     * @param fileToDelete
     */
    private void deleteFile(final File fileToDelete) {
        if (Files.exists(fileToDelete.toPath()) && Files.isReadable(fileToDelete.toPath())) {
            fileToDelete.delete();
        }
    }

    /**
     *
     */
    private void createTmpDir() {
        try {
            temporaryDirectory = Files.createTempDirectory("tmpClassDirectory").toFile();
            recursiveDeleteOnShutdownHook(temporaryDirectory.toPath());
        } catch (IOException e) {
            LOGGER.error("Failed creating temporary directory");
            e.printStackTrace();
        }
    }

    /**
     *
     * @param path
     */
    private void recursiveDeleteOnShutdownHook(final Path path) {
        Runtime.getRuntime().addShutdownHook(new Thread(
                () -> {
                    try {
                        Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                            @Override
                            public FileVisitResult visitFile(final Path file, @SuppressWarnings("unused") BasicFileAttributes attrs)
                                    throws IOException {
                                Files.delete(file);
                                return FileVisitResult.CONTINUE;
                            }
                            @Override
                            public FileVisitResult postVisitDirectory(final Path dir, final IOException e)
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
                        throw new RuntimeException("Failed to delete "+ path, e);
                    }
                }));
    }

    /**
     *
     * @param fileChanges
     * @param oldFile
     * @return
     */
    private List<String> getAllFilesFromFileChanges(final List<FileChange> fileChanges, final boolean oldFile) {
        return fileChanges.stream()
                .map(oldFile ? FileChange::getOldFile : FileChange::getNewFile)
                .map(vcsFile -> vcsFile.orElseThrow(IllegalArgumentException::new))
                .filter(sourceFile -> sourceFile.getPath().endsWith(".java"))
                .map(VCSFile::getPath)
                .collect(Collectors.toList());
    }

    /**
     *
     * @param baseDir
     * @param nameOfParent
     * @param type
     * @return
     */
    private List<File> getExpectedBinaryFiles(final File baseDir, final String nameOfParent, final CtType<?> type) {
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

    private void print(final Collection<String> list) {
        String result = "{ ";
        for (final String type : list) {
            result += type.substring(type.lastIndexOf(File.separator)+1) + ", ";
        }
        result += "}";
        LOGGER.info("Files need to rebuild: " + result);
    }
}