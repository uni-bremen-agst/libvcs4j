package de.unibremen.st.libvcs4j.data;

import de.unibremen.st.libvcs4j.Complexity;
import org.apache.commons.lang3.Validate;

@SuppressWarnings("unused")
public class ComplexityImpl implements Complexity {

	private Halstead halstead;
	private int mccabe;

	@Override
	public Halstead getHalstead() {
		return halstead;
	}

	public void setHalstead(final Halstead pHalstead) {
		halstead = Validate.notNull(pHalstead);
	}

	@Override
	public int getMcCabe() {
		return mccabe;
	}

	public void setMccabe(int pMcCabe) {
		Validate.isTrue(pMcCabe >= 0, "%d < 0", pMcCabe);
		mccabe = pMcCabe;
	}
}
