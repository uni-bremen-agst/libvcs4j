package de.unibremen.st.libvcs4j.data;

import de.unibremen.st.libvcs4j.FileChange;
import de.unibremen.st.libvcs4j.VCSFile;

import java.util.Optional;

/**
 * Implementation for {@link FileChange}.
 */
public class FileChangeImpl implements FileChange {

	private VCSFile oldFile;
	private VCSFile newFile;

	@Override
	public Optional<VCSFile> getOldFile() {
		return Optional.ofNullable(oldFile);
	}

	public void setOldFile(final VCSFile pOldFile) {
		oldFile = pOldFile;
	}

	@Override
	public Optional<VCSFile> getNewFile() {
		return Optional.ofNullable(newFile);
	}

	public void setNewFile(VCSFile pNewFile) {
		newFile = pNewFile;
	}
}
