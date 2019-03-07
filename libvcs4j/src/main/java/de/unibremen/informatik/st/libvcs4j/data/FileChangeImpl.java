package de.unibremen.informatik.st.libvcs4j.data;

import de.unibremen.informatik.st.libvcs4j.FileChange;
import de.unibremen.informatik.st.libvcs4j.VCSFile;
import lombok.Setter;
import lombok.ToString;

import java.util.Optional;

/**
 * Pojo implementation of {@link FileChange}.
 */
@Setter
@ToString(of = {"oldFile", "newFile"}, doNotUseGetters = true)
public class FileChangeImpl extends VCSModelElementImpl implements FileChange {

	/**
	 * The old file.
	 */
	private VCSFile oldFile;

	/**
	 * The new file.
	 */
	private VCSFile newFile;

	@Override
	public Optional<VCSFile> getOldFile() {
		return Optional.ofNullable(oldFile);
	}

	@Override
	public Optional<VCSFile> getNewFile() {
		return Optional.ofNullable(newFile);
	}
}
