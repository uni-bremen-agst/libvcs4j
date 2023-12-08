package missing_javadoc;

import java.awt.geom.IllegalPathStateException;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.IllegalFormatException;
import static java.util.*;
import static java.util.Collections;

//Codesmell:  1
/**
 * In this class are the different method's for the javadoc - detector test's.
 * one codesmell: too short long - description.
 * @author Michel Krause
 * @version 1.2
 */
public class MethodTest {
    //Codesmell: 1
    private int fieldAccessTest = 4;

// This is the field access test. Getter and Setter is undocumented.
    //Codesmell: 1
    public int getFieldAccessTest() {
        return fieldAccessTest;
    }
    // Codesmell: 1
    public void setFieldAccessTest(int fieldAccessTest) {
        this.fieldAccessTest = fieldAccessTest;
    }

    // Codesmell: 11
	/**
     * the codesmell's are: undocumented parameter method, undocumented generic <E> and the following tags.
     * This is a sentence that no codesmell is thrown for a too short long - description.
     * @param dingdong doesn't exists in this method, this is a codesmell.
     * @param <T> doesn't exists in this method, this is a codesmell.
     * @return the method hasn't a return type, this is a codesmell.
     * @throws IllegalAccessException doesn't exists in this method, this is a codesmell.
     * @exception IllegalArgumentException doesn't exists in this method, this is a codesmell.
     * @deprecated the method hasn't the annotation @Deprecated, so this is a codesmell.
     * @author Michel Krause, is a codesmell because @author is not an allowed tag for method.
     * @version 1.0, is a codemsell because @version is not an allowed tag for method.
     * @serial_Data is a codesmell because the tag name is not correct, the correct tag name is serialData.
     */
    protected <E> void visitCtMethod(CtMethod<T> method) {
        if (method.getDocComment() == null || method.getDocComment().isEmpty()) {
            if (!isFieldAccess(method) || (isFieldAccess(method) && fieldAccessMustBeDocumented)) {
                System.out.println("Die Methode " + method.getSimpleName() + " hat kein Javadoc.");
            }
        }
        super.visitCtMethod(method);
    }

    // Codesmell: 3
    /**
     * the codesmell's in this method are the undocumented exception, the undocumented return and the undocumented deprecated.
     * This is a sentence that no codesmell is thrown for a too short long - description.
     * @param number exists in the method, it's not a codesmell.
     * @param <T> exists in the method, it's not a codesmell.
     */
    @Deprecated
    private <T> int numberBetweenOneAndFour(int number) {
        if (number < 1 || number > 4) {
            throw new IllegalArgumentException("number doesn't match expectations.");
        }
        return number;
    }

    // Codesmell: 10 - 11 (10 if totalLength is selected), every description are too short.
    /**
     * too short short-description.
     * too short long-description.
     * @param c repeated Char.
     * @param repeatNumber how often repeated.
     * @return repeated word.
     * @throws IllegalArgumentException by wrong number.
     * @exception IllegalStateException if >= 0.
     * @see
     * @since
     * @deprecated too short
     * @serialData too short
     */
    @Deprecated
    public String charRepeater (char c, int repeatNumber) {
        String repeatedChar = "";
        if (repeatNumber < 1) {
            throw new IllegalArgumentException("no repeat possible");
            throw new IllegalStateException("only for no codesmell, that IllegalStateException doesn't exists in method");
        }
        for (int i = 0; i < repeatNumber; i++) {
            repeatedChar += c;
        }
        return repeatedChar;
    }

    // Codesmell: 3, missing @return, @param text and the missing description.
    /**
     *
     */
    private char noDescription(String text) {
        text = text.toLowerCase().trim();
        Char c = '';
        switch(text[0]) {
            case 'a':
            case 'e':
            case 'i':
            case 'o':
            case 'u': c = 'V';
                    break;
            default: c = 'K';
                    break;
        }
    }

    // Codesmell: 0
    /**
     * This is a method that count the negative from a number.
     * For example: the number is 40, then is the calculated negativ -40.
     * This should be a javadoc, which doesn't throw a codesmell.
     * @param number is the number from which the negative is calculated.
     * @return the negative of the number.
     * @throws IllegalArgumentException if the param number is lower than 1.
     * @exception IllegalStateException if the negative number bigger or equal than 0.
     * @see in this javadoc is only to show that no codesmell is thrown, if the tag description long enough.
     * @since 1.1
     * @deprecated in this method is only to show that no codesmell is thrown, if the tag description long enough.
     * @serialData in this javadoc is only to show that no codesmell is thrown, if the tag description long enough.
     */
    @Deprecated
    public int negativeNumer(int number) {
        if (number <= 0) {
            throw new IllegalArgumentException("number doesn't match the expectations.")
        }
        int negativeNumber = number - number - number;
        if (number >= 0) {
            throw new IllegalStateException("something goes wrong with the calculation.");
        }
        return negativeNumber;
    }

    // Codesmell: 0
    /**
     *  This method is include a JSON - Parser.
     *  The readed json file must be include the key lang and at least one pair with the value de or en, otherwise it throws exceptions.
     * @param filepath is the path where the json file can be found.
     * @return the string that was readed from the json file.
     * @throws FileNotFoundException if the file doesn't exists.
     * @throws ParseException if the file can't be parsed.
     * @throws IllegalFormatException if the json file don't have the key lang or no pairs with the values de or en.
     */
    public String readJSON (String filepath) throws FileNotFoundException, ParseException {
        FileReader fr = new FileReader(filepath);
        JSONArray jArray = (JSONArray) new JSONParser().parse(new BufferedReader (fr));
        String strJason = "[";
        for (Object obj : jArray) {
            JSONObject jo = (JSONObject) obj;
            if (jo.get("lang").equals("de") || jo.get("lang").equals("en")) {
                strJason += jo.toJSONString() + ",\n";
            }
        }
        strJason += "]";
        if (strJson.equals("[]")) {
            throw new IllegalFormatException("the readed json file hasn't the key lang with any value de or en");
        }
        return strJason;
    }

    // Codesmell: 1, the missing javadoc
    void writeToFile(String filenameWithPath, String text) throws FileNotFoundException {
        if (text == null || text.isEmpty() || text.trim().isEmpty()) {
            throw new IllegalArgumentException("text is empty");
        }
        if (filenameWithPath== null || filenameWithPath.isEmpty() || filenameWithPath.trim().isEmpty()) {
            throw new FileNotFoundException("can't create a file with an empty name.");
        }
        PrintWriter pw = new PrintWriter(filenameWithPath);
        pw.write(text);
        pw.write("-----------");
        pw.write(readJSON(filenameWithPath));
        pw.flush();
        pw.close();
    }

    // Codesmell: 2, missing description for IllegalFormatException and ParseException.
    /**
     *  this method combines the method's readJson and writeToFile.
     *  In this javadoc gives the following codesmells: the undocumented ParseException from the method readJSON
     *  and the undocumented IllegalArgumentException from method writeToFile.
     *  It doesn't give extra codesmell's from the loop in method writeToFile to readJSON.
     * @param filePath is the path where the json file can be found.
     * @param filenameWithPath includes the filename and his path, it will created by the PrintWriter.
     * @throws IllegalStateException if the return text from method readJSON is empty.
     * @throws FileNotFoundException if the given file doesn't exists. from method readJSON and writeToFile
     * @throws IllegalArgumentException if the given text is empty.
     */
    protected void readAndWriteJSON(String filePath, String filenameWithPath) {
        String jsonText = readJSON(filePath);
        if (jsonText == null || jsonText.isEmpty()) {
            throw new IllegalStateException("the text from the json can't be empty.");
        }
        writeToFile(filenameWithPath, jsonText);
    }

    //Codesmell: 0 when javadocDetector.withMethodFieldAccess(8 or lower) has been set.
    /**
     * Gets the current value of fieldAccessTest for test.
     * @return the current value of fieldAccessTest
     */
    public Char getFieldAccessTestForDescriptionLength() {
        return fieldAccessTest;
    }
}