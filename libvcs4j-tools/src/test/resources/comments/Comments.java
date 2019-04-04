public class Comments {

    public Comments() {
        //Call method with a string
        //filler comment
        //filler comment
        //filler comment
        //filler comment
        //filler comment
        //filler comment
        //filler comment
        //filler comment
        method("TEST");
    }

    public void anotherMethod() {
        //Not implemented
        //LOC is under 10
        //so a code smell should
        //not be detected
    }

    public void method(String chars) {
        //This is a very long comment
        //which indicates that there must be
        //something wrong with this method
        //because its loc to comment ratio
        //is way to high.
        //Refactoring should be considered.
        char result = 'b';
        for (char c : chars.toCharArray()) {
            //test if given char is a
            //unnecessary comment
            if (c == 'a') {
                result = c;
                //test if given char is x
                //unnecessary comment
            } else if (c == 'x') {
                result = 'x';
                //test if given char is y
                //unnecessary comment
            } else {
                result = '0';
            }
        }
        System.out.println(result);
    }
}
