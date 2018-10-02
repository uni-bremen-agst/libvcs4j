package de.unibremen.informatik.st.libvcs4j.spoon;

import de.unibremen.informatik.st.libvcs4j.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spoon.Launcher;
import spoon.SpoonModelBuilder;
import spoon.reflect.CtModel;
import spoon.reflect.cu.CompilationUnit;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.factory.CompilationUnitFactory;
import spoon.reflect.factory.Factory;
import spoon.support.compiler.FileSystemFile;
import spoon.support.compiler.FileSystemFolder;
import spoon.support.compiler.FilteringFolder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static java.lang.System.currentTimeMillis;


public class SpoonModel {
    private static final Logger LOGGER = LoggerFactory.getLogger(SpoonModel.class);

    private CtModel model = null;

    private File temporaryDirectory = null;

    private boolean noClasspath = true;

    public static void main(String[] args) {
        SpoonModel spoonModel = new SpoonModel();
        VCSEngine engine = VCSEngineBuilder.ofGit("/home/dominique/git/java").build();
        for (RevisionRange range : engine) {
            spoonModel.update(range);
        }
    }

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

            final List<String> filesToBuild = revisionRange.getFileChanges()
                    .stream()
                    .filter(fileChange -> fileChange.getType() != FileChange.Type.REMOVE)
                    .map(FileChange::getNewFile)
                    .map(vcsFile -> vcsFile.orElseThrow(IllegalArgumentException::new))
                    .filter(sourceFile -> sourceFile.getPath().endsWith(".java"))
                    .map(VCSFile::getPath)
                    .collect(Collectors.toList());

            try {
                removeChangedTypes(model, revisionRange.getFileChanges());
            } catch (IllegalArgumentException e) {
                throw new IllegalStateException("Error while updating model. Model is inconsistent now.", e);
            }

            launcher.addInputResource(createInputSource(filesToBuild));
            launcher.getModelBuilder().addCompilationUnitFilter(path -> !filesToBuild.contains(path));
            launcher.getModelBuilder().compile(SpoonModelBuilder.InputType.FILES);
            model.setBuildModelIsFinished(false);
            model = launcher.buildModel();

        }
        LOGGER.info(format("Model built in %d milliseconds", currentTimeMillis() - current));
        return model;
    }

    private FilteringFolder createInputSource(final List<String> input) {
        FilteringFolder folder = new FilteringFolder();
        input.forEach(path -> {
            final Path p = Paths.get(path);
            Validate.isTrue(Files.exists(p), "'%s' does not exist", path);
            Validate.isTrue(Files.isReadable(p), "'%s' is not readable", path);
            if (Files.isRegularFile(p)) {
                folder.addFile(new FileSystemFile(p.toFile()));
            } else if (Files.isDirectory(p)) {
                folder.addFolder(new FileSystemFolder(path));
            }
        });
        return folder;
    }

    private void removeChangedTypes(final CtModel pModel, final List<FileChange> pFileChanges) throws IllegalArgumentException {
        final CtPackage rootPackage = pModel.getRootPackage();
        final CompilationUnitFactory cuFactory = rootPackage.getFactory().CompilationUnit();

        pFileChanges.stream()
                .filter(fileChange -> fileChange.getType() != FileChange.Type.ADD)
                .map(FileChange::getOldFile)
                .map(optional -> optional.orElseThrow(IllegalArgumentException::new))
                .filter(sourceFile -> sourceFile.getPath().endsWith(".java"))
                // avoid mismatches due to paths like 'path/to/../file/file.java'
                .map(sourceFile -> {
                    try {
                        return new File(sourceFile.getPath()).getCanonicalPath();
                    } catch (IOException e) {
                        throw new IllegalArgumentException(e);
                    }
                })
                .forEach(path -> {
                    final CompilationUnit cu = cuFactory.removeFromCache(path);
                    cu.getDeclaredTypes().forEach(type -> {
                        final CtPackage pkg = type.getPackage();
                        pkg.removeType(type);
                        if (pkg.getTypes().isEmpty()
                                && pkg.getPackages().isEmpty()
                                && pkg.getParent() != rootPackage) {
                                //&& !"unnamed module".equals(pkg.getParent().toString())) {
                            // remove empty packages
                            final CtPackage parent = (CtPackage) pkg.getParent();
                            parent.removePackage(pkg);
                        }

                    });
                    cu.getBinaryFiles().forEach(this::deleteFile);
                });
    }

    private void deleteFile(final File fileToDelete) {
        Validate.isTrue(Files.exists(fileToDelete.toPath()), "'%s' does not exist", fileToDelete.toPath());
        Validate.isTrue(Files.isReadable(fileToDelete.toPath()), "'%s' is not readable", fileToDelete.toPath());
        fileToDelete.delete();
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
}