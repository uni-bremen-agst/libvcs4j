import de.unibremen.st.libvcs4j.*

engine = VCSEngineBuilder
	.ofGit("https://github.com/amaembo/streamex.git")
	.build()

println "id,LOC,SLOC,CLOC,NOT,SNOT,CNOT"
engine.each {
	size = FSTree.of(it.revision.files).computeSize()
	println	"${it.revision.commitId}," +
		"${size.LOC},"             +
		"${size.SLOC},"            +
		"${size.CLOC},"            +
		"${size.NOT},"             +
		"${size.SNOT},"            +
		"${size.CNOT}"
}
