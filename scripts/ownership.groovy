@Grapes([
		@Grab(group='de.uni-bremen.informatik.st', module='libvcs4j', version='1.4.0'),
		@Grab(group='org.slf4j', module='slf4j-simple', version='1.7.25')
])

import de.unibremen.informatik.st.libvcs4j.*

def vcs = VCSEngineBuilder
		.ofGit("https://github.com/amaembo/streamex.git")
		.build()

def authors = new HashSet<String>()
def rev2ownership = new LinkedHashMap<String, Map<String, Integer>>()

vcs.forEach {
	id = it.revision.id
	rev2ownership.put(id, new HashMap<>())
	it.revision.files.findAll{ !it.isBinary() }.forEach {
		it.readLineInfo().forEach {
			authors.add(it.author)
			rev2ownership.get(id).merge(it.author, 1, Integer.&sum)
		}
	}
}

println("id,${String.join(',', authors)}")
rev2ownership.forEach ({ id, ownership ->
	print("${id}")
	authors.forEach{ print(",${ownership.getOrDefault(it, 0)}") }
	print('\n')
})
