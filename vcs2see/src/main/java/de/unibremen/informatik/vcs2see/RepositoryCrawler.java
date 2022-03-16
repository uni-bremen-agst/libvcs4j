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

import de.unibremen.informatik.st.libvcs4j.RevisionRange;
import de.unibremen.informatik.st.libvcs4j.VCSEngine;
import de.unibremen.informatik.st.libvcs4j.VCSEngineBuilder;

import java.io.IOException;
import java.util.Optional;

/**
 * Component which uses LibVCS4j to traverse the repository and run an analysis on each revision.
 *
 * @author Felix Gaebler
 * @version 1.0.0
 */
public class RepositoryCrawler {

    private VCSEngine engine;

    /**
     * Initializes the repository crawler.
     * Important to set the "repository.temp" properties value.
     */
    public void crawl() {
        PropertiesManager propertiesManager = Vcs2See.getPropertiesManager();
        String path = propertiesManager.getProperty("repository.path").orElseThrow();
        Type type = Type.valueOf(propertiesManager.getProperty("repository.type").orElseThrow());

        switch (type) {
            case GIT:
                engine = VCSEngineBuilder.ofGit(path).build();
                break;

            case HG:
                engine = VCSEngineBuilder.ofHG(path).build();
                break;

            case SVN:
                engine = VCSEngineBuilder.ofSVN(path).build();
                break;

            default:
                engine = VCSEngineBuilder.of(path).build();
                break;
        }

        String temp = engine.getOutput()
                .toFile().getAbsolutePath()
                .replace("\\", "\\\\");
        propertiesManager.setProperty("repository.temp", temp);
    }

    /**
     * Gets the next revision from version control.
     * @return revision
     * @throws IOException exception
     */
    public Optional<RevisionRange> nextRevision() throws IOException {
        return engine.next();
    }

    /**
     * Type of repository. All types supported by LibVCS4j.
     */
    public enum Type {
        GIT, HG, SVN;
    }

}
