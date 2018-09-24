@Grapes([
        @Grab(group='de.uni-bremen.informatik.st', module='libvcs4j', version='1.4.0'),
        @Grab(group='de.uni-bremen.informatik.st', module='libvcs4j-d3', version='1.4.0'),
        @Grab(group='org.slf4j', module='slf4j-simple', version='1.7.25')
])

import de.unibremen.informatik.st.libvcs4j.*
import de.unibremen.informatik.st.libvcs4j.d3.*


def USER = 'Tagir Valeev'

engine = VCSEngineBuilder
        .ofGit("https://github.com/amaembo/streamex.git")
        .withLatestRevision()
        .build()
revision = engine.next().get().revision

TreeMap treeMap = new TreeMap(
        revision.files.findAll({ f -> !f.isBinary() }),
        { f ->
            double size = f.readLines().size()
            double lines = (double) f.readLineInfo()
                    .findAll({ it.author == USER }).size()
            new TreeMap.RateCell(size, lines/size) })
println(treeMap.generateHTML())
