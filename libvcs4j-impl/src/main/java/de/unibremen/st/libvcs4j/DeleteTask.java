package de.unibremen.st.libvcs4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Objects;

/**
 * A simple task to delete a file or directory (recursively). Usually, this
 * class is used in shutdown hooks to delete temporarily created directories.
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class DeleteTask extends Thread {

    /**
     * The {@link Logger} of this class.
     */
    private final Logger LOGGER = LoggerFactory.getLogger(DeleteTask.class);

    /**
     * Path to file or directory that will be deleted.
     */
    private final Path path;

    /**
     * Creates a new task that deletes {@code pPath} when calling
     * {@link #run()}.
     *
     * @param pPath
     *      Path to the file or directory to delete.
     * @throws NullPointerException
     *      If {@code pPath == null}.
     */
    public DeleteTask(final Path pPath) {
        path = Objects.requireNonNull(pPath);
    }

    /**
     * Convenience constructor for {@link #DeleteTask(Path)}.
     *
     * @param pPath
     *      Path to the file or directory to delete.
     * @throws NullPointerException
     *      If {@code pPath == null}.
     */
    public DeleteTask(final String pPath) {
        this(Paths.get(pPath));
    }

    @Override
    public void run() {
        LOGGER.info("Deleting directory '{}'", path);
        try {
            if (Files.exists(path)) {
                Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFile(
                            final Path pFile, final BasicFileAttributes pAttrs)
                            throws IOException {
                        Files.delete(pFile);
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult postVisitDirectory(
                            final Path pDir, final IOException pExc)
                            throws IOException {
                        Files.delete(pDir);
                        return FileVisitResult.CONTINUE;
                    }
                });
            }
        } catch (final IOException e) {
            LOGGER.warn("Error while deleting directory '{}'", path, e);
        }
    }
}
