@Grapes([
		@Grab(group='de.uni-bremen.informatik.st', module='libvcs4j', version='1.4.0'),
		@Grab(group='de.uni-bremen.informatik.st', module='libvcs4j-metrics', version='1.4.0'),
		@Grab(group='org.slf4j', module='slf4j-simple', version='1.7.25')
])

import de.unibremen.informatik.st.libvcs4j.*
import de.unibremen.informatik.st.libvcs4j.metrics.*

engine = VCSEngineBuilder
	.ofGit("https://github.com/amaembo/streamex.git")
	.build()
Metrics metrics = new Metrics()

println "id,date,LOC,SLOC,CLOC,NOT,SNOT,CNOT"
engine.each {
	Size size = new Size(0, 0, 0, 0, 0, 0)
	it.revision.files.each {
		metrics.computeSize(it).ifPresent({s -> size = size.add(s)})
	}
	println	"${it.revision.id},"     +
		"${it.commits[0].dateTime}," +
		"${size.LOC},"               +
		"${size.SLOC},"              +
		"${size.CLOC},"              +
		"${size.NOT},"               +
		"${size.SNOT},"              +
		"${size.CNOT}"
}
