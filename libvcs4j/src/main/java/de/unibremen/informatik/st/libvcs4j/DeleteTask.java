package de.unibremen.informatik.st.libvcs4j;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * Recursively deletes a directory or file. This class is primarily intended to
 * be used in shutdown hooks to delete temporarily created directories.
 */
@AllArgsConstructor
public class DeleteTask extends Thread {

	/**
	 * The {@link Logger} of this class.
	 */
	private static final Logger log =
			LoggerFactory.getLogger(DeleteTask.class);

	/**
	 * Path to file or directory that will be deleted.
	 */
	@NonNull
	private final Path path;

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
		log.info("Deleting '{}'", path);
		try {
			if (path.toFile().exists()) {
				Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
					@Override
					public FileVisitResult visitFile(
							final Path pFile, final BasicFileAttributes pAttrs)
							throws IOException {
						pFile.toFile().setWritable(true);
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
			log.warn("Error while deleting '{}'", path, e);
		}
	}
}
