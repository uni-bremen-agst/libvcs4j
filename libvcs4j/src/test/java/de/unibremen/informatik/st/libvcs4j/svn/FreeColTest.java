package de.unibremen.informatik.st.libvcs4j.svn;

import de.unibremen.informatik.st.libvcs4j.VCSBaseTest;
import de.unibremen.informatik.st.libvcs4j.VCSEngineBuilder;

public class FreeColTest extends VCSBaseTest {

	@Override
	public String getTarGZFile() {
		return "freecol.tar.gz";
	}

	@Override
	public String getFolderInTarGZ() {
		return "freecol";
	}

	@Override
	protected void setEngine(VCSEngineBuilder builder) {
		builder.withSVN();
	}

	@Override
	protected String getRootCommitIdFile() {
		return "freecol_ids.txt";
	}

	@Override
	protected String getSubDir() {
		return "freecol/trunk";
	}

	@Override
	protected String getSubDirCommitIdFile() {
		return "freecol_trunk_ids.txt";
	}
}
