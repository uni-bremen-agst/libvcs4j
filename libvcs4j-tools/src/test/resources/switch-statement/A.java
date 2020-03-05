public class A {

    public void method(String chars) {
        switch (chars) {
            case "abc":
                if (chars.trim().endsWith("c")) {
                    boolean f = true;
                    if (f) {
                        System.out.println("ABC");
                    }
                }
                break;
            case "d":
                if (chars.trim().endsWith("d")) {
                    boolean f = false;
					if (!f) {
                        System.out.println("D");
                    }
                }
                break;
        }
    }
}
