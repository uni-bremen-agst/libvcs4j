public class A {
    private String abc = null;
    private int counter = 1;
	private int var = 0;

	public A() {
		var = 1;
		counter += var;
	}

    public boolean doSomething() {
        abc = "ABC";
        String plusD = abc + "D";
        counter++;
        return plusD.equals("ABCD");
    }

    public void count() {
        counter++;
        System.out.println(counter);
    }

    public void doAnything() {
        int result = counter + 42;
        System.out.println(result);
    }
}
