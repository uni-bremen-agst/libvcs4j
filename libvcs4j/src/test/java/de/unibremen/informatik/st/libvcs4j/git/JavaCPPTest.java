package de.unibremen.informatik.st.libvcs4j.git;

import de.unibremen.informatik.st.libvcs4j.VCSBaseTest;
import de.unibremen.informatik.st.libvcs4j.VCSEngineBuilder;

public class JavaCPPTest extends VCSBaseTest {

	@Override
	protected String getTarGZFile() {
		return "javacpp.tar.gz";
	}

	@Override
	protected String getFolderInTarGZ() {
		return "javacpp";
	}

	@Override
	protected void setEngine(VCSEngineBuilder builder) {
		builder.withGit();
	}

	@Override
	protected String getIdFile() {
		return "javacpp_master_ids.txt";
	}
}
