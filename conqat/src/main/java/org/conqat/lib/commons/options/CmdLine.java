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

import java.util.ArrayList;

import org.conqat.lib.commons.string.StringUtils;

/**
 * A very simple class for parsing command line parameters.
 * <p>
 * A typical command line looks like this:
 * <p>
 * <code>-dir src -count occurrences TEST -dde</code>
 * <p>
 * 
 * In this example the minus symbol ('-') is the <i>parameter prefix </i>,
 * <code>dir</code>,<code>count</code> and <code>dde</code> are
 * <i>parameters </i>. Whereas <code>dir</code> has a single <i>value </i>
 * <code>src</code> and <code>count</code> has the two <i>value </i>
 * <code>occurrences</code> and <code>TEST</code>.
 * <p>
 * Typical method calls would have the following results. <table>
 * <tr>
 * <th>method call</th>
 * <th>result</th>
 * </tr>
 * <tr>
 * <td><code>hasParameter("dde")</code></td>
 * <td><code>true</code></td>
 * </tr>
 * <tr>
 * <td><code>hasParameterAndValue("dde")</code></td>
 * <td><code>false</code></td>
 * </tr>
 * <tr>
 * <td><code>hasParameter("TEST")</code></td>
 * <td><code>false</code></td>
 * </tr>
 * <tr>
 * <td><code>getValue("src")</code></td>
 * <td>"dir"</td>
 * </tr>
 * <tr>
 * <td><code>getValue("count")</code></td>
 * <td>"occurrences"</td>
 * </tr>
 * <tr>
 * <td><code>getValues("count")</code></td>
 * <td>["occurrences", "TEST"]</td>
 * </tr>
 * </table>
 * 
 * @deprecated Use the CommandLine class instead.
 * 
 * @author Florian Deissenboeck
 * @author $Author: kinnen $
 * 
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 557DB31C4CD84423CCFF7AD7FF798AFF
 * 
 */
@Deprecated
public class CmdLine {
    /** Parameter store. */
    private final String[] parameters;

    /** The prefix. */
    private final String parameterPrefix;

    /**
     * Create new <code>CmdLine</code> -object from command line arguments.
     * Parameter prefix is "-".
     * 
     * @param params
     *            command line arguments as provided in <code>main()</code>
     *            -method.
     */
    public CmdLine(String[] params) {
        this(params, "-");

    }

    /**
     * Create new <code>CmdLine</code> -object from command line arguments.
     * 
     * @param params
     *            command line arguments as provided in <code>main()</code>
     *            -method.
     * @param parameterPrefix
     *            parameter prefix
     */
    public CmdLine(String[] params, String parameterPrefix) {
        this.parameters = params;
        this.parameterPrefix = parameterPrefix;

    }

    /**
     * Get number of parameters.
     * 
     * @return number of parameters.
     */
    public int getParameterCount() {
        return parameters.length;
    }

    /**
     * Get the values for a parameter.
     * 
     * @param parameterName
     *            name of the parameter.
     * @return the values associated with this parameter. If the parameter is
     *         not present or doesn't habe a value <code>null</code> is
     *         returned.
     */
    public String[] getValues(String parameterName) {
        if (!hasParameter(parameterName)) {
            return null;
        }
        int index = StringUtils.indexOf(parameters, parameterPrefix
                + parameterName);
        ArrayList<String> result = new ArrayList<String>();
        for (int i = index + 1; i < parameters.length; i++) {
            String current = parameters[i].trim();
            if (!isValue(current)) {
                break;
            }
            result.add(current);
        }
        if (result.size() == 0) {
            return null;
        }
        return result.toArray(new String[0]);
    }

    /**
     * Get the value for a parameter.
     * 
     * @param parameterName
     *            name of the parameter.
     * @return the value associated with this parameter. If the parameter is not
     *         present or doesn't habe a value <code>null</code> is returned.
     */
    public String getValue(String parameterName) {
        if (!hasParameter(parameterName)) {
            return null;
        }
        int index = StringUtils.indexOf(parameters, parameterPrefix
                + parameterName);
        String result = parameters[index + 1].trim();
        if (!isValue(result)) {
            return null;
        }
        return result;
    }

    /**
     * Checks if this command line has a certain parameter.
     * 
     * @param parameterName
     *            name of the parameter
     * @return <code>true</code> if parameter is present, <code>false</code>
     *         otherwise.
     */
    public boolean hasParameter(String parameterName) {
        int index = StringUtils.indexOf(parameters, parameterPrefix
                + parameterName);
        return (index != -1);
    }

    /**
     * Checks if this command line has a certain parameter with at least one
     * value.
     * 
     * @param parameterName
     *            name of the parameter
     * @return <code>true</code> if parameter and value is present,
     *         <code>false</code> otherwise.
     */
    public boolean hasParameterAndValue(String parameterName) {
        int index = StringUtils.indexOf(parameters, parameterPrefix
                + parameterName);
        if (index < 0) {
            return false;
        }
        if (index >= parameters.length - 1) {
            return false;
        }
        return isValue(parameters[index + 1]);
    }

    /**
     * Check is a certain string is a value, i.e. is no parameter.
     * 
     * @param string
     *            the string in question.
     * @return <code>true</code> it is a value, <code>false</code>
     *         otherwise.
     */
    private boolean isValue(String string) {
        return (string.trim().indexOf(parameterPrefix) != 0);
    }

}