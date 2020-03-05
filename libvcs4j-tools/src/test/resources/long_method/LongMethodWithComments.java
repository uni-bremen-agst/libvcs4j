public class LongMethodWithComments {

	// SLOC: 4
	public void methodWithComments() {
		System.out.println("Actually, this method isn't very long...");

		// This comment must not be counted.

		/**
		 * Likewise, this comment must not be counted.
		 */

		/* As well as this comment. */

		for (int i = 0; i < 10; i++) {
			System.out.println(i);
		}
	}
}
