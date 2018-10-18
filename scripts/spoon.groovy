@Grapes([
        @Grab(group='de.uni-bremen.informatik.st', module='libvcs4j', version='1.4.0'),
        @Grab(group='org.slf4j', module='slf4j-simple', version='1.7.25')
])

import de.unibremen.informatik.st.libvcs4j.*
import de.unibremen.informatik.st.libvcs4j.spoon.*

def vcs = VCSEngineBuilder
        .ofGit("/home/dominique/git/jython")
        .withRoot("src/org")
        .build()

SpoonModel model = new SpoonModel()

vcs.each {model.update(it)}
