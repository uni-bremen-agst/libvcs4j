package de.unibremen.informatik.st.libvcs4j.data;

import de.unibremen.informatik.st.libvcs4j.Revision;
import de.unibremen.informatik.st.libvcs4j.VCSFile;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.apache.commons.lang3.Validate;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Pojo implementation of {@link Revision}.
 */
@Getter
@Setter
public class RevisionImpl extends VCSModelElementImpl implements Revision {

	/**
	 * The id of a revision.
	 */
	@NonNull
	private String id;

	/**
	 * The non-VCS-specific files of a revision.
	 */
	@NonNull
	private List<VCSFile> files;

	@Override
	public List<VCSFile> getFiles() {
		return new ArrayList<>(files);
	}

	/**
	 * Sets the non-VCS-specific files of this revision.
	 *
	 * @param pFiles
	 * 		The files to set.
	 * @throws NullPointerException
	 * 		If {@code pFiles} is {@code null}.
	 * @throws IllegalArgumentException
	 * 		If {@code pFiles} contains {@code null}.
	 */
	public void setFiles(List<VCSFile> pFiles) {
		Validate.noNullElements(pFiles);
		files = new ArrayList<>(pFiles);
	}

	@Override
	public Path getOutput() {
		return getVCSEngine().getOutput();
	}
}
