package de.unibremen.st.libvcs4j.data;

import de.unibremen.st.libvcs4j.Complexity;
import org.apache.commons.lang3.Validate;

public class HalsteadImpl implements Complexity.Halstead {

	private int n1;
	private int n2;
	private int N1;
	private int N2;

	@Override
	public int getn1() {
		return n1;
	}

	public void setn1(final int pn1) {
		Validate.isTrue(pn1 >= 0, "%d < 0", pn1);
		n1 = pn1;
	}

	@Override
	public int getn2() {
		return n2;
	}

	public void setn2(final int pn2) {
		Validate.isTrue(pn2 >= 0, "%d < 0", pn2);
		n2 = pn2;
	}

	@Override
	public int getN1() {
		return N1;
	}

	public void setN1(final int pN1) {
		Validate.isTrue(pN1 >= 0, "%d < 0", pN1);
		N1 = pN1;
	}

	@Override
	public int getN2() {
		return N2;
	}

	public void setN2(final int pN2) {
		Validate.isTrue(pN2 >= 0, "%d < 0", pN2);
		N2 = pN2;
	}
}
