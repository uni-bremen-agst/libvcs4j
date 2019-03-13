package de.unibremen.informatik.st.libvcs4j.testutils;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.PosixFilePermission;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Provides methods to extract compressed resources.
 */
public class ResourceExtractor {

    /**
     * The {@link Logger} of this class.
     */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(ResourceExtractor.class);

    /**
     * Extracts the given .tar.gz resource to a temporarily created directory.
     * For the sake of security, and if supported by the underlying os, the
     * created directory has permissions 700. The created directory will be
     * removed automatically using a shutdown hook.
     *
     * @param pTarGZ
     * 		Path to the resource without preceding '/', for example,
     * 		'lin64.tar.gz'
     * @return
     * 		The absolute path to the temporarily created directory.
     * @throws IOException
     * 		If an error occurred while extracting {@code pTarGZ}.
     */
    public Path extractTarGZ(final String pTarGZ) throws IOException {
        final InputStream is = getClass().getResourceAsStream("/" + pTarGZ);
        if (is == null) {
            throw new FileNotFoundException(
                    String.format("Unable to find '%s'", pTarGZ));
        }

        // directory permissions (only for Unix-like systems)
        final Set<PosixFilePermission> tmpDirPerms = new HashSet<>();
        tmpDirPerms.add(PosixFilePermission.OWNER_READ);
        tmpDirPerms.add(PosixFilePermission.OWNER_WRITE);
        tmpDirPerms.add(PosixFilePermission.OWNER_EXECUTE);

        // file permissions
        final Set<PosixFilePermission> filePerms = new HashSet<>();
        filePerms.add(PosixFilePermission.OWNER_READ);
        filePerms.add(PosixFilePermission.OWNER_WRITE);
        filePerms.add(PosixFilePermission.OWNER_EXECUTE);

        // location of extracted archive
        final Path tmpDir = Files.createTempDirectory(null);
        try { Files.setPosixFilePermissions(tmpDir, tmpDirPerms);
        } catch (final UnsupportedOperationException e) { /* ignored */ }
        LOGGER.info("Created temporary directory '{}'", tmpDir);

        // add shutdown hook to delete `tmpDir`
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                try {
                    FileUtils.deleteDirectory(tmpDir.toFile());
                } catch (final IOException e) {/* ignored */}
            }
        });

        LOGGER.info("Extracting '{}' to '{}'", pTarGZ, tmpDir);
        final List<Entry<Path, Path>> linksToCopy = new ArrayList<>();
        try (BufferedInputStream bis = new BufferedInputStream(is);
             InputStream gis = new GzipCompressorInputStream(bis);
             TarArchiveInputStream in = new TarArchiveInputStream(gis)) {
            TarArchiveEntry entry;
            while (null != (entry = (TarArchiveEntry) in.getNextEntry())) {
                final Path path = tmpDir.resolve(entry.getName());
                if (entry.isDirectory()) {
                    Files.createDirectory(path);
                } else if (entry.isSymbolicLink()) {
                    // Creating a symlink fails on Windows.
                    // Instead, copy the file.
                    final Path from = path.getParent()
                            .resolve(entry.getLinkName());
                    linksToCopy.add(new SimpleEntry<>(from , path));
                } else if (entry.isLink()) {
                    final Path from = Paths.get(tmpDir.toString(),
                            entry.getLinkName());
                    linksToCopy.add(new SimpleEntry<>(from, path));
                } else {
                    try (OutputStream os = Files.newOutputStream(path);
                         BufferedOutputStream out =
                                 new BufferedOutputStream(os)) {
                        int n;
                        final byte[] buffer = new byte[16384];
                        while (-1 != (n = in.read(buffer, 0, buffer.length))) {
                            out.write(buffer, 0, n);
                        }
                    }
                    try {
                        Files.setPosixFilePermissions(path, filePerms);
                    } catch (UnsupportedOperationException e) { /* ignored */ }
                }
            }
        }
        for (final Entry<Path, Path> fromTo : linksToCopy) {
            final Path from = fromTo.getKey();
            final Path to = fromTo.getValue();
            Files.copy(from, to, StandardCopyOption.COPY_ATTRIBUTES);
        }

        return tmpDir.toAbsolutePath();
    }
}
