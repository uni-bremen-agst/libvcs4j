
		public class GodClass { // The tabs are on purpose.
	private int a;      // used in: complexMethod, m1, m2, getA
	private char b;     // used in: complexMethod
	private double c;   // used in: complexMethod, m1
	private String d;   // used in: complexMethod, m2
	private boolean e;  // used in: complexMethod, m2
	////// => TCC
	//////    complexMethod, m1
	//////    complexMethod, m2
	//////    complexMethod, m3
	//////    complexMethod, getA
	//////    m1, getA
	//////    m2, getA
	//////------------------------------------
	//////    6 matches / 10 total pairs = 0.6

	// MCC: 10
	public void complexMethod() {
		if (e) {
			while (e) {
				e = b == 'a' ? true : false;
			}
		} else {
			c += 1.0;
		}
		switch (d) {
			case "foo":
				if (e) {}
				break;
			case "bar":
				while (a <= 10) {
					if (a == 5) {}
					a++;
				}
			default:
				break;
		}
	}

	// MCC: 1
	// ATFD: 5
	public void accessData() {
		OtherClass oc = new OtherClass();
		oc.getField1();
		oc.getField2();
		oc.getField3();
		oc.field4;
		oc.field5;
	}

	// MCC: 1
	public void m1() {
		double tmp = c + a;
	}

	// MCC: 2
	public void m2() {
		e = getA() < 5 && d.contains("test");
	}

	// MCC: 1
	public int getA() {
		return a;
	}
}
