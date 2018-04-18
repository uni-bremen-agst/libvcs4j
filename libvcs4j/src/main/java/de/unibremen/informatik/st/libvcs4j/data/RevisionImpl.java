package de.unibremen.informatik.st.libvcs4j.data;

import de.unibremen.informatik.st.libvcs4j.Revision;
import de.unibremen.informatik.st.libvcs4j.VCSEngine;
import de.unibremen.informatik.st.libvcs4j.VCSFile;
import org.apache.commons.lang3.Validate;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation for {@link Revision}.
 */
public class RevisionImpl implements Revision {

	private final VCSEngine engine;
	private String id;
	private List<VCSFile> files;

	public RevisionImpl(VCSEngine pEngine) {
		engine = Validate.notNull(pEngine);
	}

	@Override
	public String getId() {
		return id;
	}

	public void setId(final String pCommitId) {
		id = Validate.notNull(pCommitId);
	}

	@Override
	public List<VCSFile> getFiles() {
		return new ArrayList<>(files);
	}

	public void setFiles(List<VCSFile> pFiles) {
		Validate.noNullElements(pFiles);
		files = new ArrayList<>(pFiles);
	}

	@Override
	public Path getOutput() {
		return engine.getOutput();
	}
}
