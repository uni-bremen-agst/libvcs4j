package de.unibremen.informatik.st.libvcs4j.data;

import de.unibremen.informatik.st.libvcs4j.FileChange;
import de.unibremen.informatik.st.libvcs4j.VCSFile;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

/**
 * Pojo implementation of {@link FileChange}.
 */
@Getter
@Setter
public class FileChangeImpl extends VCSModelElementImpl implements FileChange {

	/**
	 * The old file.
	 */
	@NonNull
	private VCSFile oldFile;

	/**
	 * The new file.
	 */
	@NonNull
	private VCSFile newFile;
}
