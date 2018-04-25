package de.unibremen.informatik.st.libvcs4j.hg;

import de.unibremen.informatik.st.libvcs4j.VCSBaseTest;
import de.unibremen.informatik.st.libvcs4j.VCSEngineBuilder;

public class JavaHGTest extends VCSBaseTest {

	@Override
	protected String getTarGZFile() {
		return "javahg.tar.gz";
	}

	@Override
	protected String getFolderInTarGZ() {
		return "javahg";
	}

	@Override
	protected void setEngine(VCSEngineBuilder builder) {
		builder.withHG();
	}

	@Override
	protected String getIdFile() {
		return "javahg_master_ids.txt";
	}
}
