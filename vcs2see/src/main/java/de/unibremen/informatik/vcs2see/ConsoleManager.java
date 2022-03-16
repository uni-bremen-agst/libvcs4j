// Copyright 2022 Felix Gaebler
//
// Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
// associated documentation files (the "Software"), to deal in the Software without restriction, including
// without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the
// following conditions:
//
// The above copyright notice and this permission notice shall be included in all copies or substantial
// portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT
// LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO
// EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
// IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
// THE USE OR OTHER DEALINGS IN THE SOFTWARE.

package de.unibremen.informatik.vcs2see;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Component which processes output and input on the console.
 *
 * @author Felix Gaebler
 * @version 1.0.0
 */
public class ConsoleManager {

    private final BufferedReader reader;

    public ConsoleManager() {
        this.reader = new BufferedReader(new InputStreamReader(System.in));
    }

    /**
     * Reads a line on the input and returns it.
     * @return line read
     * @throws IOException exception
     */
    public String readLine() throws IOException {
        return reader.readLine();
    }

    /**
     * Reads a line on the input with prompt and returns it.
     * @param prompt message which is to be output before the input
     * @return line read
     * @throws IOException exception
     */
    public String readLine(String prompt) throws IOException {
        System.out.print(prompt);
        return reader.readLine();
    }

    /**
     * Prints a line to the console.
     * @param text message to be output
     */
    public void print(String text) {
        System.out.println(text);
    }

    /**
     * Outputs a separator line in the console.
     */
    public void printSeparator() {
        System.out.println("------------------------------------------------------------");
    }

    /**
     * Outputs the logo of the application in the console.
     */
    public void printWelcome() {
        System.out.println("              _____         ___ _____         ");
        System.out.println("             |  |  |___ ___|_  |   __|___ ___ ");
        System.out.println("             |  |  |_ -|  _|  _|__   | -_| -_|");
        System.out.println("              \\___/|___|___|___|_____|___|___|");
    }

}
