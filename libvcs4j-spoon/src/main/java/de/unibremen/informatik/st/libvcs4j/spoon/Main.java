package de.unibremen.informatik.st.libvcs4j.spoon;

import de.unibremen.informatik.st.libvcs4j.RevisionRange;
import de.unibremen.informatik.st.libvcs4j.VCSEngine;
import de.unibremen.informatik.st.libvcs4j.VCSEngineBuilder;

public class Main {

    public static void main(String[] args) {
        SpoonModel spoonModel = new SpoonModel();
        VCSEngine engine = VCSEngineBuilder.ofGit("/home/dominique/git/jython").withRoot("src").build();
        for (RevisionRange range : engine) {
            spoonModel.update(range);
        }
    }
}
