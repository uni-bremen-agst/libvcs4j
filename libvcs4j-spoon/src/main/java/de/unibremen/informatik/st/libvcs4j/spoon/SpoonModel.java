package de.unibremen.informatik.st.libvcs4j.spoon;

import de.unibremen.informatik.st.libvcs4j.RevisionRange;
import de.unibremen.informatik.st.libvcs4j.Validate;
import de.unibremen.informatik.st.libvcs4j.VCSEngineBuilder;
import de.unibremen.informatik.st.libvcs4j.VCSEngine;
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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static java.lang.System.currentTimeMillis;


public class SpoonModel {
    private static final Logger LOGGER = LoggerFactory.getLogger(SpoonModel.class);

    private CtModel model = null;

    private File temporaryDirectory = null;

    private boolean noClasspath = true;

    private Set<String> filesNotCompiledSinceLastUpdate = new HashSet<>();

    private int numberFilesWithoutClassFiles = 0;

    private int numberOfAllSourceFiles = 0;

    public CtModel update(final RevisionRange revisionRange) {
        Validate.notNull(revisionRange);
        final long current = currentTimeMillis();

        if (temporaryDirectory == null) {
            createTmpDir();
        }

        if (model == null) {

            final Launcher launcher = new Launcher();
            launcher.setBinaryOutputDirectory(temporaryDirectory);
            launcher.getEnvironment().setNoClasspath(noClasspath);
            //Add the checked out directory here, so we do not have to add each single file
            launcher.addInputResource(revisionRange.getRevision().getOutput().toString());
            launcher.getModelBuilder().compile(SpoonModelBuilder.InputType.FILES);
            model = launcher.buildModel();

        } else {

            final Factory factory = model.getRootPackage().getFactory();
            final Launcher launcher = new Launcher(factory);
            launcher.getEnvironment().setNoClasspath(noClasspath);
            launcher.getModelBuilder().setSourceClasspath(temporaryDirectory.getPath());
            launcher.setBinaryOutputDirectory(temporaryDirectory);
            filesNotCompiledSinceLastUpdate.addAll(findUncompiledSources(factory));
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
            print(filesNotCompiledSinceLastUpdate);

            removeChangedTypes(getAllOldFilesFromFileChanges(revisionRange.getRemovedFiles()));

            getAllOldFilesFromFileChanges(revisionRange.getRemovedFiles())
                    .forEach(path -> filesNotCompiledSinceLastUpdate.remove(path));

            removeChangedTypes(filesNotCompiledSinceLastUpdate);

            filesNotCompiledSinceLastUpdate.addAll(getAllNewFilesFromFileChanges(revisionRange.getAddedFiles()));

            filesToBuild.addAll(getAllNewFilesFromFileChanges(revisionRange.getAddedFiles()));

            launcher.addInputResource(createInputSource(filesNotCompiledSinceLastUpdate));
            launcher.getModelBuilder().addCompilationUnitFilter(path -> !filesToBuild.contains(path));
            launcher.getModelBuilder().compile(SpoonModelBuilder.InputType.FILES);
            model.setBuildModelIsFinished(false);
            model = launcher.buildModel();

        }
        LOGGER.info(format("Model built in %d milliseconds", currentTimeMillis() - current));

        return model;
    }

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

    private void removeChangedTypes(final Collection<String> pFileChanges) {
        final CtPackage rootPackage = model.getRootPackage();
        final CompilationUnitFactory cuFactory = rootPackage.getFactory().CompilationUnit();

        pFileChanges.stream()
                .filter(sourceFile -> sourceFile.endsWith(".java"))
                .forEach(path -> {
                    if (cuFactory.getMap().containsKey(path)) {
                        final CompilationUnit cu = cuFactory.removeFromCache(path);
                        cu.getDeclaredTypes().forEach(type -> {
                            final CtPackage pkg = type.getPackage();
                            pkg.removeType(type);
//                            if (pkg.getTypes().isEmpty()
//                                    && pkg.getPackages().isEmpty()
//                                    && pkg.getParent() != rootPackage) {
//                                // remove empty packages
//                                final CtPackage parent = (CtPackage) pkg.getParent();
//                                parent.removePackage(pkg);
//                            }
//
                        });
                        cu.getBinaryFiles().forEach(this::deleteFile);
                    }
                });
    }

    private List<String> findUncompiledSources(final Factory factory) {
        final List<String> rebuildedFiles = new ArrayList<>();
        List<File> expected;
        final String output = factory
                .getEnvironment()
                .getBinaryOutputDirectory();
            for (final CtType type : model.getAllTypes()) {
                final File base = Paths.get(output, type.getPackage().getQualifiedName().replace(".", File.separator)).toFile();
                expected = getExpectedBinaryFiles(base, null, type);

                if (expected.stream().anyMatch(file -> !file.isFile())) {
                    rebuildedFiles.add(type.getPosition().getFile().getAbsolutePath());

                    //if a class gets modified, rebuild all classes, that refer to this class
                    for (final CtType oldType : model.getAllTypes()) {
                        if (oldType.getReferencedTypes().contains(type.getReference())) {
                            rebuildedFiles.add(oldType.getPosition().getFile().getAbsolutePath());
                        }
                    }
                } else {
                    filesNotCompiledSinceLastUpdate.remove(type.getPosition().getFile().getAbsolutePath());
                }

            }


        print(rebuildedFiles);
        return rebuildedFiles;
    }

    private void deleteFile(final File fileToDelete) {
        if (Files.exists(fileToDelete.toPath()) && Files.isReadable(fileToDelete.toPath())) {
            fileToDelete.delete();
        }
    }

    private void createTmpDir() {
        try {
            temporaryDirectory = Files.createTempDirectory("tmpClassDirectory").toFile();
            temporaryDirectory.deleteOnExit();
        } catch (IOException e) {
            LOGGER.error("Failed creating temporary directory");
            e.printStackTrace();
        }
    }

    private List<String> getAllNewFilesFromFileChanges(final List<FileChange> fileChanges) {
        return fileChanges.stream()
                .map(FileChange::getNewFile)
                .map(vcsFile -> vcsFile.orElseThrow(IllegalArgumentException::new))
                .filter(sourceFile -> sourceFile.getPath().endsWith(".java"))
                .map(VCSFile::getPath)
                .collect(Collectors.toList());
    }

    private List<String> getAllOldFilesFromFileChanges(final List<FileChange> fileChanges) {
        return fileChanges
                .stream()
                .map(FileChange::getOldFile)
                .map(vcsFile -> vcsFile.orElseThrow(IllegalArgumentException::new))
                .filter(sourceFile -> sourceFile.getPath().endsWith(".java"))
                .map(VCSFile::getPath)
                .collect(Collectors.toList());
    }

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