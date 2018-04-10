/*-------------------------------------------------------------------------+
|                                                                          |
| Copyright 2005-2011 The ConQAT Project                                   |
|                                                                          |
| Licensed under the Apache License, Version 2.0 (the "License");          |
| you may not use this file except in compliance with the License.         |
| You may obtain a copy of the License at                                  |
|                                                                          |
|    http://www.apache.org/licenses/LICENSE-2.0                            |
|                                                                          |
| Unless required by applicable law or agreed to in writing, software      |
| distributed under the License is distributed on an "AS IS" BASIS,        |
| WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. |
| See the License for the specific language governing permissions and      |
| limitations under the License.                                           |
+-------------------------------------------------------------------------*/
package org.conqat.lib.commons.options;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;

import org.conqat.lib.commons.enums.EnumUtils;

/**
 * This class offers a safe and flexible interface to Java properties files.
 * 
 * Property files must follow the format for Java properties files. See Javadoc
 * of {@link java.util.Properties} for details.
 * 
 * @author Florian Deissenboeck
 * @author Axel Gerster
 * @author $Author: kinnen $
 * 
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: B5E82436F60D87EAEC3534F8F8F52E1B
 * 
 * @see java.util.Properties
 */
public class Options {

    /**
     * Returned by <code>countValues</code> when trying to count values of a
     * non-present option.
     */
    public static final int OPTION_NOT_PRESENT = -1;

    /**
     * This implementation is back by a <code>Properties</code> object.
     */
    private Properties properties;

    /**
     * Construct a new <code>Option</code> object holding now options. Use
     * methods {@link #init(String)}or {@link #setOption(String, String)}to
     * store options.
     */
    public Options() {
        init();
    }

    /**
     * This initalizes the <code>Options</code> object by reading a properties
     * file.
     * 
     * @param filename
     *            full-qualified name of the properties file
     * @throws IOException
     *             Thrown if an I/O problem is encountered while reading
     *             properties file.
     */
    public void init(String filename) throws IOException {
        properties = new Properties();
        InputStream inputStream = new FileInputStream(filename);
        properties.load(inputStream);
        inputStream.close();
    }

    /**
     * Init empty <code>Options</code> object. Existing options are cleared.
     */
    public void init() {
        properties = new Properties();
    }

    /**
     * Sets and option. Setting an already existing option overwrites current
     * value.
     * 
     * @param option
     *            name of the option
     * @param value
     *            option's value, must have same format as defined in the
     *            properties file
     * @return <code>true</code> if option was alreay present,
     *         <code>false</code> otherwise
     */
    public boolean setOption(String option, String value) {
        boolean overriden = hasOption(option);
        properties.setProperty(option, value);
        return overriden;
    }

    /**
     * Gets the value for a specified option.
     * 
     * @param option
     *            the name of the option
     * @return the option's value, if the option is not present or has a
     *         <code>null</code> value <code>null</code> is returned. If the
     *         option has a space separated value list, the whole list is
     *         returned. Use {@link #getValues(String)}to access single values.
     */
    public String getValue(String option) {
        if (!hasOption(option)) {
            return null;
        }
        String value = properties.getProperty(option);

        if ("".equals(value)) {
            return null;
        }
        return value;
    }

    /**
     * Return the value for a specified option or a default value if option is
     * not present.
     * 
     * @param option
     *            name of the option
     * @param defaultValue
     *            default value to use, if option is not present
     * @return the option's value or the default value
     */

    public String getValue(String option, String defaultValue) {
        if (hasOption(option)) {
            return getValue(option);
        }
        return defaultValue;
    }

    /**
     * Returns the space separated value of an option as array. A option might
     * have more the one space separated value. This method returns them as an
     * array. To allow values containing spaces use double quotes.
     * <p>
     * <i>Example: </i> For the following line in a properties file
     * <p>
     * <code>option=value1 value2 "value 3" value4</code>
     * <p>
     * the method returns this array <br/>
     * 
     * <code><br/>
     * a[0] = &quot;value1&quot;<br/>
     * a[1] = &quot;value2&quot;<br/>
     * a[2] = &quot;value 3&quot;<br/>
     * a[3] = &quot;value4&quot;<br/>
     * </code>
     * 
     * @param option
     *            name of the option
     * @return the array as desribed above
     */
    public String[] getValues(String option) {
        if (!hasOption(option)) {
            return null;
        }
        String values = properties.getProperty(option);

        if ("".equals(values)) {
            return null;
        }

        return parse(values);
    }

    /**
     * Checks if the specified option is present and has a boolean value.
     * Boolean values are <code>true</code>,<code>false</code>,
     * <code>yes</code> and <code>no</code>
     * 
     * @param option
     *            name of the option
     * @return if the option is present and has a boolean value
     *         <code>true</code> is returned, otherwise <code>false</code>
     */
    public boolean hasBooleanValue(String option) {
        if (!hasValue(option)) {
            return false;
        }

        String value = getValue(option);

        return checkTrue(value) || checkFalse(value);
    }

    /**
     * Get the value for an option as <code>boolean</code>.
     * 
     * @param option
     *            name of the option
     * @return the value of this option
     * @throws ValueConversionException
     *             if the option doesn't have a boolean value. Use
     *             {@link #hasBooleanValue(String)}method or default value
     *             enabled version {@link #getBooleanValue(String, boolean)}of
     *             this method to avoid conversion problems.
     */
    public boolean getBooleanValue(String option)
            throws ValueConversionException {
        if (!hasBooleanValue(option)) {
            throw new ValueConversionException(option);
        }

        String value = getValue(option);

        if (checkTrue(value)) {
            return true;
        }

        return false;
    }

    /**
     * Get the value for an option as instance of an enumeration. Enumeration
     * names are matched in non case-sensitive way. Dashes in values are
     * replaced by underscores.
     * <p>
     * Typical usage is:
     * 
     * <pre>
     * Colors color = options.getEnumValue(&quot;enum1&quot;, Colors.class);
     * </pre>
     * 
     * where <code>Colors</code> is an enumeration.
     * 
     * @param <T>
     *            the enumeration
     * @param option
     *            the name of the option
     * @param enumType
     *            the enumeration type
     * @return the enumeration entry
     * @throws ValueConversionException
     *             if the option doesn't have a value of the specified
     *             enumeration. Use {@link #hasEnumValue(String, Class)}method
     *             or default value enabled version
     *             {@link #getEnumValue(String, Enum, Class)}of this method to
     *             avoid conversion problems.
     */
    public <T extends Enum<T>> T getEnumValue(String option, Class<T> enumType)
            throws ValueConversionException {
        if (!hasEnumValue(option, enumType)) {
            throw new ValueConversionException(option);
        }

        String value = getValue(option);

        return EnumUtils.valueOfIgnoreCase(enumType,
                normalizeEnumConstantName(value));
    }

    /**
     * Same as {@link #getEnumValue(String, Class)} but allows to specify
     * default value.
     * 
     * @param <T>
     *            the enumeration
     * @param option
     *            the name of the option
     * @param enumType
     *            the enumeration type
     * @return the enumeration entry
     * 
     */
    public <T extends Enum<T>> T getEnumValue(String option, T defaultValue,
            Class<T> enumType) {

        try {
            return getEnumValue(option, enumType);
        } catch (ValueConversionException e) {
            return defaultValue;
        }

    }

    /**
     * Checks if the specified option is present and has a legal value.
     * 
     * @param option
     *            name of the option
     * @return if the option is present and has a legal value <code>true</code>
     *         is returned, otherwise <code>false</code>
     */
    public <T extends Enum<T>> boolean hasEnumValue(String option,
            Class<T> enumType) {
        if (!hasValue(option)) {
            return false;
        }

        String value = getValue(option);

        return checkEnum(value, enumType);
    }

    /**
     * Check if value describe an an element of the enumeration (case-insenstive
     * match).
     * 
     */
    private <T extends Enum<T>> boolean checkEnum(String value,
            Class<T> enumType) {
        T t = EnumUtils.valueOfIgnoreCase(enumType,
                normalizeEnumConstantName(value));
        if (t == null) {
            return false;
        }
        return true;
    }

    /**
     * Get the value for an option as <code>int</code>.
     * 
     * @param option
     *            name of the option
     * @return the value of this option
     * @throws ValueConversionException
     * @throws ValueConversionException
     *             if the option doesn't have a <code>int</code> value. Use
     *             {@link #hasIntValue(String)}method or default value enabled
     *             version {@link #getIntValue(String, int)}of this method to
     *             avoid conversion problems.
     */
    public int getIntValue(String option) throws ValueConversionException {
        if (!hasIntValue(option)) {
            throw new ValueConversionException(option);
        }

        String value = getValue(option);

        return Integer.parseInt(value);
    }

    /**
     * Checks if the specified option is present and has a <code>int</code>
     * value.
     * 
     * @param option
     *            name of the option
     * @return if the option is present and has a <code>int</code> value
     *         <code>true</code> is returned, otherwise <code>false</code>
     */
    public boolean hasIntValue(String option) {
        if (!hasValue(option)) {
            return false;
        }

        String value = getValue(option);

        return checkInt(value);
    }

    /**
     * Same as {@link #getBooleanValue(String)}but allows to specify a default
     * value.
     * 
     * @param option
     *            name of the option
     * @param defaultValue
     *            default value
     * @return return the value of the option if option is present and has a
     *         boolean value, otherwise the default value is returned
     */
    public boolean getBooleanValue(String option, boolean defaultValue) {

        try {
            return getBooleanValue(option);
        } catch (ValueConversionException e) {
            return defaultValue;
        }

    }

    /**
     * Same as {@link #getIntValue(String)}but allows to specify a default
     * value.
     * 
     * @param option
     *            name of the option
     * @param defaultValue
     *            default value
     * @return return the value of the option if option is present and has an
     *         integer value, otherwise the default value is returned
     */
    public int getIntValue(String option, int defaultValue) {
        try {
            return getIntValue(option);
        } catch (ValueConversionException e) {
            return defaultValue;
        }
    }

    /**
     * Checks if a given string represent an integer.
     * 
     * @param value -
     *            the string to check
     * @return <code>true</code> if the string represents an integer,
     *         <code>false</code> otherwise
     */
    private boolean checkInt(String value) {
        try {
            Integer.parseInt(value);
        } catch (NumberFormatException ex) {
            return false;
        }
        return true;
    }

    /**
     * Checks if the string is a boolean literal with value <code>false</code>.
     * Literals <code>false</code> and <code>no</code> are allowed.
     * 
     * @param value
     *            the string to check
     * @return <code>true</code> if the string represents a booleean literal
     *         with value <code>false</code>,<code>false</code> otherwise
     */
    private boolean checkFalse(String value) {
        value = value.trim();

        if (value.toLowerCase().equals("false")) {
            return true;
        }
        if (value.toLowerCase().equals("no")) {
            return true;
        }

        return false;
    }

    /**
     * Checks if the string is a boolean literal with value <code>true</code>.
     * Literals <code>true</code> and <code>yes</code> are allowed.
     * 
     * @param value
     *            the string to check
     * @return <code>true</code> if the string represents a booleean literal
     *         with value <code>true</code>,<code>false</code> otherwise
     */
    private boolean checkTrue(String value) {
        value = value.trim();

        if (value.toLowerCase().equals("true")) {
            return true;
        }
        if (value.toLowerCase().equals("yes")) {
            return true;
        }

        return false;
    }

    /**
     * Parses a space separated value list. To use values with spaces, use
     * double quotes.
     * 
     * @param string
     *            the value list to parse
     * @return an array containing the values, quotes are omitted
     */
    private String[] parse(String string) {
        string = string.trim();
        int length = string.length();
        char[] content = new char[length];
        string.getChars(0, length, content, 0);

        ArrayList<String> list = new ArrayList<String>();

        int i = 0;

        int lastPos = 0;
        boolean inQM = false;
        boolean inToken = false;

        while (i < length) {
            switch (content[i]) {
            case ' ':
            case '\t':
                if (inToken && !inQM) {
                    // parameter found
                    String parameter = string.substring(lastPos, i).trim();
                    parameter = parameter.replaceAll("\"", "");
                    list.add(parameter);
                    lastPos = i;
                }

                inToken = false;
                // lastPos++;
                break;
            case '\"':
                inQM = !inQM;
                break;
            default:
                inToken = true;
            }
            i++;
        }

        String parameter = string.substring(lastPos, i).trim();
        parameter = parameter.replaceAll("\"", "");
        list.add(parameter);

        String[] result = new String[list.size()];
        list.toArray(result);

        return result;
    }

    /**
     * Checks if a specified option is present.
     * 
     * @param option
     *            name of the option
     * @return <code>true</code> if option is present, <code>false</code>
     *         otherwise
     */
    public boolean hasOption(String option) {
        return !(properties.getProperty(option) == null);
    }

    /**
     * Checks if specified option has a value.
     * 
     * @param option
     *            name of the option
     * @return <code>true</code> if option is present and has a value,
     *         <code>false</code> otherwise (even if option is present but
     *         doesn't have a value)
     */
    public boolean hasValue(String option) {
        return countValues(option) > 0;
    }

    /**
     * Count the space separated values of an option. Double quotes are taken
     * into account.
     * 
     * @param option
     *            name of the option
     * @return value count
     */
    public int countValues(String option) {
        if (!hasOption(option)) {
            return OPTION_NOT_PRESENT;
        }

        String[] values = getValues(option);

        if (values == null) {
            return 0;
        }

        return values.length;
    }

    /**
     * Returns a list with key-value-pairs as string.
     * 
     * @return key-value-pairs as string
     */
    @Override
	public String toString() {
        StringBuffer buffer = new StringBuffer();
        Iterator<Object> it = properties.keySet().iterator();
        while (it.hasNext()) {
            String key = (String) it.next();
            String value = properties.getProperty(key);
            buffer.append(key + " = " + value);
            if (it.hasNext()) {
                buffer.append(System.getProperty("line.separator"));
            }
        }
        return buffer.toString();
    }

    /**
     * Exception objects of this class are possibly returned by
     * {@link Options#getBooleanValue(String)}and
     * {@link Options#getIntValue(String)}, if corresponding options don't have
     * a boolean respectively integer value.
     * 
     */
    @SuppressWarnings("serial")
    public static class ValueConversionException extends Exception {

        /**
         * Construct new conversion exception.
         * 
         * @param option
         *            name of the option causing the exception
         */
        public ValueConversionException(String option) {
            super("Option: " + option);
        }
    }

    /**
     * Get the value for an option as <code>float</code>.
     * 
     * @param option
     *            name of the option
     * @return the value of this option
     * @throws ValueConversionException
     *             if the option doesn't have a float value.
     */
    public float getFloatValue(String option) throws ValueConversionException {
        if (!hasFloatValue(option)) {
            throw new ValueConversionException(option);
        }

        String value = getValue(option);

        return Float.parseFloat(value);
    }

    /**
     * Checks if the specified option is present and has a float value.
     * 
     * 
     * @param option
     *            name of the option
     * @return if the option is present and has a float value <code>true</code>
     *         is returned, otherwise <code>false</code>
     */
    public boolean hasFloatValue(String option) {
        if (!hasValue(option)) {
            return false;
        }

        String value = getValue(option);

        return checkFloat(value);
    }

    /**
     * Checks if a string contains a float.
     */
    private boolean checkFloat(String value) {
        try {
            Float.parseFloat(value);
        } catch (NumberFormatException ex) {
            return false;
        }
        return true;
    }

    /** Normalize enum constant name. This replaces all dashes with underscores. */
    private String normalizeEnumConstantName(String constantName) {
        return constantName.replaceAll("-", "_");
    }
}