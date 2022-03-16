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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Component which handles reading and writing of a properties file.
 *
 * @author Felix Gaebler
 * @version 1.0.0
 */
public class PropertiesManager {

    private Properties properties;

    private File file;

    /**
     * Load the properties file of the software.
     * @throws IOException exception
     */
    public void loadProperties() throws IOException {
        file = new File("vsc2see.properties");
        if(!file.exists() && !file.createNewFile()) {
            System.err.println("Failed to create properties file.");
            System.exit(1);
        }

        properties = new Properties();
        properties.load(new FileInputStream(file));
    }

    /**
     * Gets all property keys that were set.
     * @return set of keys
     */
    public Set<String> getKeys() {
        return properties.keySet().stream()
                .map(key -> (String) key)
                .collect(Collectors.toSet());
    }

    /**
     * Returns a property from the properties file as {@link Optional <String>}.
     * @param key key to the property
     * @return value as optional
     */
    public Optional<String> getProperty(String key) {
        return Optional.ofNullable(properties.getProperty(key));
    }

    /**
     * Sets a property in the properties file.
     * @param key key to the property
     * @param value value of the property
     */
    public void setProperty(String key, String value) {
        properties.setProperty(key, value);
    }

    /**
     * Removes a property from the properties file.
     * @param key key to the property
     */
    public void removeProperty(String key) {
        properties.remove(key);
    }

    /**
     * Saves the changes in the properties file.
     * @throws IOException exception
     */
    public void saveProperties() throws IOException {
        properties.store(new FileOutputStream(file), "Vsc2See Properties");
    }

}
