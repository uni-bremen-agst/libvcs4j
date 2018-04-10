import de.unibremen.st.libvcs4j.*

engine = VCSEngineBuilder
	.ofGit("https://github.com/amaembo/streamex.git")
	.build()

println "id,date,LOC,SLOC,CLOC,NOT,SNOT,CNOT"
engine.each {
	size = FSTree.of(it.revision.files).computeSize()
	println	"${it.revision.id},"    +
		"${it.commits[0].dateTime}" +
		"${size.LOC},"              +
		"${size.SLOC},"             +
		"${size.CLOC},"             +
		"${size.NOT},"              +
		"${size.SNOT},"             +
		"${size.CNOT}"
}
