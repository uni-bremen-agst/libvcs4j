package de.unibremen.informatik.st.libvcs4j.svn;

import de.unibremen.informatik.st.libvcs4j.*;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings({"deprecation", "ConstantConditions"})
public class RoolieTest extends VCSBaseTest {

	@Override
	public String getTarGZFile() {
		return "roolie.tar.gz";
	}

	@Override
	public String getFolderInTarGZ() {
		return "roolie";
	}

	@Override
	protected void setEngine(VCSEngineBuilder builder) {
		builder.withSVN();
	}

	@Override
	protected String getRootCommitIdFile() {
		return "roolie_ids.txt";
	}

	@Override
	protected String getSubDir() {
		return "roolie-core/src/main/java/net/sf/roolie/core/util";
	}

	@Override
	protected String getSubDirCommitIdFile() {
		return "roolie_util_ids.txt";
	}

	///////////////////////// Datetime interval tests /////////////////////////

	private SVNEngine createProvider(
			final String pRoot, final String pFrom,final String pTo) {
		return new SVNEngine(
				"file://" + getInput().toString(),
				pRoot,
				getTarget(),
				pFrom, pTo);
	}

	@Test
	public void commitMessage36() throws IOException {
		final VCSEngine engine = createProvider("", "35", "37");
		engine.next();

		assertThat(engine.next())
				.map(r -> r.getLatestCommit().getMessage())
				.hasValue("Added more arguments to test and now tests another rule.");
	}

	@Test
	public void changes36() throws IOException {
		VCSEngine engine = createProvider("", "35", "37");
		engine.next();
		String path = Paths.get(
				getTarget().toAbsolutePath().toString(),
				"Roolie/src/org/roolie/RulesEngine.java").toString();

		RevisionRange range = engine.next().get();
		assertThat(range.getFileChanges())
				.extracting(FileChange::getType)
				.containsExactly(FileChange.Type.MODIFY);
		assertThat(range.getFileChanges())
				.first()
				.matches(fc -> fc.getOldFile().get().getPath().equals(path));
		assertThat(range.getFileChanges())
				.first()
				.matches(fc -> fc.getNewFile().get().getPath().equals(path));
	}

	@Test
	public void fileContent36() throws IOException {
		VCSEngine engine = createBuilder()
				.withFrom("35")
				.withTo("37")
				.build();

		engine.next();
		List<FileChange> changes = engine.next().get().getFileChanges();
		assertThat(changes).hasSize(1);

		byte[] oldContents = changes.get(0).getOldFile().get().readAllBytes();
		byte[] oldContentsExpected = IOUtils.toByteArray(
				getClass().getResourceAsStream("/roolie/RulesEngine.java_35"));
		byte[] newContents = changes.get(0).getNewFile().get().readAllBytes();
		byte[] newContentsExpected = IOUtils.toByteArray(
				getClass().getResourceAsStream("/roolie/RulesEngine.java_36"));

		assertThat(oldContents).isNotEqualTo(newContents);
		assertThat(oldContents).isEqualTo(oldContentsExpected);
		assertThat(newContents).isEqualTo(newContentsExpected);
	}

	@Test
	public void commitMessage41() throws IOException {
		final VCSEngine engine = createProvider("", "40", "42");
		engine.next();

		assertThat(engine.next())
				.map(r -> r.getLatestCommit().getMessage())
				.hasValue("");
	}

	@Test
	public void changes41() throws IOException {
		VCSEngine engine = createProvider("", "40", "42");
		engine.next();

		RevisionRange range = engine.next().get();
		assertThat(range.getFileChanges()).hasSize(46);
		assertThat(range.getFileChanges())
				.allMatch(fc -> fc.getType().equals(FileChange.Type.ADD))
				.allMatch(fc -> Files.isRegularFile(
						fc.getNewFile().get().toPath()));
	}

	@Test
	public void commitMessage61() throws IOException {
		final VCSEngine engine = createProvider("", "60", "62");
		engine.next();

		assertThat(engine.next())
				.map(r -> r.getLatestCommit().getMessage())
				.hasValue("v1.1 - 12/12/2013\n" +
						"* Added support for making rule-defs out of other rule-defs.\n" +
						"* Removed old license information from files.\n" +
						"* Added more unit tests.\n" +
						"* Removed dependency on any parent pom.\n" +
						"* Removed PGP key generation from build.\n");
	}

	///////////////////////// Revision interval tests /////////////////////////

	@Test
	public void toGreaterHEAD() throws IOException {
		SVNEngine engine = (SVNEngine) createBuilder()
				.withFrom("1")
				.withTo("100")
				.build();
		assertThat(engine.listRevisions()).hasSize(64);
	}
}
