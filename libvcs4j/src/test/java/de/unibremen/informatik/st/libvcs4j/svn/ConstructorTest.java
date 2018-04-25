package de.unibremen.informatik.st.libvcs4j.svn;

import de.unibremen.informatik.st.libvcs4j.VCSBaseTest;
import de.unibremen.informatik.st.libvcs4j.VCSEngineBuilder;
import de.unibremen.informatik.st.libvcs4j.exception.IllegalIntervalException;
import de.unibremen.informatik.st.libvcs4j.exception.IllegalRepositoryException;
import de.unibremen.informatik.st.libvcs4j.exception.IllegalTargetException;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.assertEquals;

@SuppressWarnings("deprecation")
public class ConstructorTest extends VCSBaseTest {

	@Override
	public String getTarGZFile() {
		return "roolie.tar.gz";
	}

	@Override
	public String getFolderInTarGZ() {
		return "roolie";
	}

	@Override
	protected void setEngine(final VCSEngineBuilder pBuilder) {
		pBuilder.withSVN();
	}

	private void createEngine(final String pInput) {
		VCSEngineBuilder.ofSVN(pInput).build();
	}

	@Test
	public void unsupportedProtocol() {
		thrown.expect(IllegalRepositoryException.class);
		createEngine("unsupported://path");
	}

	@Test
	public void regularFileRepository() throws IOException {
		Path file = Files.createTempFile(null, null);
		file.toFile().deleteOnExit();
		thrown.expect(IllegalRepositoryException.class);
		createEngine("file://" + file.toString());
	}

	@Test
	public void toGreaterHEAD() throws IOException {
		SVNEngine engine = new SVNEngine(
				"file://" + getInput().toString(), "", getTarget(),
				"1", "100");
		List<String> revs = engine.listRevisions();
		assertEquals(64, revs.size());
		for (int i = 0; i < revs.size(); i++) {
			assertEquals(String.valueOf(i+1), revs.get(i));
		}
	}
}
