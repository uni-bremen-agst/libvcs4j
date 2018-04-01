package de.unibremen.st.libvcs4j.data;

import de.unibremen.st.libvcs4j.Size;

@SuppressWarnings("unused")
public class SizeImpl implements Size {

	private int loc;
	private int sloc;
	private int cloc;
	private int not;
	private int snot;
	private int cnot;

	@Override
	public int getLOC() {
		return loc;
	}

	public void setLOC(final int pLOC) {
		loc = pLOC;
	}

	@Override
	public int getSLOC() {
		return sloc;
	}

	public void setSLOC(final int pSLOC) {
		sloc = pSLOC;
	}

	@Override
	public int getCLOC() {
		return cloc;
	}

	public void setCLOC(final int pCLOC) {
		cloc = pCLOC;
	}

	@Override
	public int getNOT() {
		return not;
	}

	public void setNOT(final int pNOT) {
		not = pNOT;
	}

	@Override
	public int getSNOT() {
		return snot;
	}

	public void setSNOT(final int pSNOT) {
		snot = pSNOT;
	}

	@Override
	public int getCNOT() {
		return cnot;
	}

	public void setCNOT(final int pCNOT) {
		cnot = pCNOT;
	}
}
