package de.unibremen.informatik.st.libvcs4j.spoon;

import de.unibremen.informatik.st.libvcs4j.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spoon.Launcher;
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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class Main {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    private CtModel model = null;

    private File temporaryDirectory;

    private boolean noClasspath = true;

    public Main() {
        try {
            temporaryDirectory = Files.createTempDirectory("tempClassDirectory").toFile();
            temporaryDirectory.deleteOnExit();
        } catch (IOException e) {
            LOGGER.error("Failed creating temporary directory");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Main main = new Main();
        VCSEngine single = VCSEngineBuilder
                .ofSingle("/home/dominique/git/pi2-t02g04/uebung04")
                .build();
        RevisionRange range = null;
        try {
            range = single.next().get();
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
        main.update(range);
        if (true) {
            LOGGER.info("BLA");
        }
    }

    public List<String> getInput() {
        return new ArrayList<>(model.getRootPackage()
                .getFactory()
                .CompilationUnit()
                .getMap()
                .keySet());
    }

    public CtModel update(final RevisionRange revisionRange) {
        Validate.notNull(revisionRange);
        if (model == null) {

            final Launcher launcher = new Launcher();
            launcher.setBinaryOutputDirectory(temporaryDirectory);
            launcher.getEnvironment().setNoClasspath(noClasspath);
            //Add the checked out directory here, so we do not have to add each single file
            launcher.addInputResource(revisionRange.getRevision().getOutput().toString());
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
                    .map(VCSFile::getPath)
                    .collect(Collectors.toList());

            try {
                removeChangedTypes(model, revisionRange.getFileChanges());
            } catch (IllegalArgumentException e) {
                throw new IllegalStateException("Error while updating model. Model is inconsistent now.", e);
            }

            final List<String> input = getInput();

            if (!revisionRange.getAddedFiles().isEmpty()) {
                revisionRange.getAddedFiles().forEach(fileChange ->
                        input.add(fileChange.getNewFile().orElseThrow(IllegalArgumentException::new).getPath())
                );
            }

            input.addAll(filesToBuild);
            launcher.addInputResource(createInputSource(input));
            launcher.getModelBuilder().addCompilationUnitFilter(path -> !filesToBuild.contains(path));
            model = launcher.buildModel();

        }
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
                            // remove empty packages
                            final CtPackage parent = (CtPackage) pkg.getParent();
                            parent.removePackage(pkg);
                        }
                        deleteFile(path);
                    });
                });
    }

    private void deleteFile(final String fileName) {
        File fileToDelete = new File(fileName);
        Validate.isTrue(Files.exists(fileToDelete.toPath()), "'%s' does not exist", fileToDelete.toPath());
        Validate.isTrue(Files.isReadable(fileToDelete.toPath()), "'%s' is not readable", fileToDelete.toPath());
        fileToDelete.delete();
    }
}

