package de.unibremen.st.libvcs4j;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents the state of a VCS at a certain point in time.
 */
@SuppressWarnings("unused")
public interface Revision {

	/**
	 * Returns the id of this revision. Usually, it is the id of the commit
	 * that yields to this revision. However, a VCS may use a dedicated id to
	 * identify commits and revisions independently.
	 *
	 * @return
	 * 		The id of this revision.
	 */
	String getId();

	/**
	 * @see VCSEngine#getOutput()
	 */
	Path getOutput();

	/**
	 * Returns all non-VCS-specific files.
	 *
	 * @return
	 * 		All non-VCS-specific files.
	 */
	List<VCSFile> getFiles();

	/**
	 * Filters the list of files returned by {@link #getFiles()} and returns
	 * only those whose relative file path ends with {@code suffix}.
	 *
	 * You may use this method to analyze a certain file type only. For
	 * instance, call {@code getFilesBySuffix(".java")} to get only Java files.
	 *
	 * @param suffix
	 * 		The suffix used to filter the files.
	 * @return
	 * 		All file whose relative path ends with {@code suffix}.
	 */
	default List<VCSFile> getFilesBySuffix(final String suffix) {
		return getFiles().stream()
				.filter(f -> f.getPath().endsWith(suffix))
				.collect(Collectors.toList());
	}

	/**
	 * Filters the list of files returned by {@link #getFiles()} and returns
	 * only those whose relative path starts with {@code prefix}.
	 *
	 * You may use this method to analyze files located in a certain directory
	 * only. For instance, call {@code getFilesByPrefix("src/main/java")} to
	 * get only the files located in "src/main/java".
	 *
	 * @param prefix
	 * 		The prefix used to filter the files.
	 * @return
	 * 		All files whose relative path starts with {@code prefix}.
	 */
	default List<VCSFile> getFilesByPrefix(final String prefix) {
		return getFiles().stream()
				.filter(f -> f.getRelativePath().startsWith(prefix))
				.collect(Collectors.toList());
	}
}
