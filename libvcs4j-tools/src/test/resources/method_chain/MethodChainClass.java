public class MethodChainClass {

	public void methodChain() {
		A a = new A();
		System.out.println(a.getB().getC().getD().name());
	}

	private class A {
		public B getB() {
			return null;
		}
	}

	private class B {
		public C getC() {
			return null;
		}
	}

	private class C {
		public D getD() {
			return null;
		}
	}

	private class D {
		public String name() {
			return name;
		}
	}
}