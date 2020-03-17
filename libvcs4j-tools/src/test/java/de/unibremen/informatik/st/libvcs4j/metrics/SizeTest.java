package de.unibremen.informatik.st.libvcs4j.metrics;

import de.unibremen.informatik.st.libvcs4j.VCSFile;
import org.conqat.lib.scanner.ELanguage;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

public class SizeTest {

	@Test
	public void testHelloWorld() throws IOException {
		VCSFile file = mock(VCSFile.class);
		when(file.readContent()).thenReturn(
				"public class Main {\n" +
				"    public static void main(String[] args) {\n" +
				"        System.out.println(\"Hello, World!\");\n" +
				"    }\n" +
				"}\n");

		Metrics metrics = spy(Metrics.class);
		doReturn(Optional.of(ELanguage.JAVA)).when(metrics).getLanguage(file);

		assertThat(metrics.computeSize(file))
				.isNotEmpty()
				.map(size -> {
					assertThat(size.getLOC()).isEqualTo(5);
					assertThat(size.getNOT()).isEqualTo(26);
					assertThat(size.getSLOC()).isEqualTo(5);
					assertThat(size.getSNOT()).isEqualTo(26);
					assertThat(size.getCLOC()).isEqualTo(0);
					assertThat(size.getCNOT()).isEqualTo(0);
					return size;
				});
	}

	@Test
	public void testHelloWorldWithComments() throws IOException {
		VCSFile file = mock(VCSFile.class);
		when(file.readContent()).thenReturn(
				"/* This is a simple Java program.\n" +
				"   FileName : \"HelloWorld.java\". */\n" +
				"class HelloWorld\n" +
				"{\n" +
				"    // Your program begins with a call to main().\n" +
				"    // Prints \"Hello, World\" to the terminal window.\n" +
				"    public static void main(String args[])\n" +
				"    {\n" +
				"        System.out.println(\"Hello, World\");\n" +
				"    }\n" +
				"}");

		Metrics metrics = spy(Metrics.class);
		doReturn(Optional.of(ELanguage.JAVA)).when(metrics).getLanguage(file);

		assertThat(metrics.computeSize(file))
				.isNotEmpty()
				.map(size -> {
					assertThat(size.getLOC()).isEqualTo(11);
					assertThat(size.getNOT()).isEqualTo(28);
					assertThat(size.getSLOC()).isEqualTo(7);
					assertThat(size.getSNOT()).isEqualTo(25);
					assertThat(size.getCLOC()).isEqualTo(4);
					assertThat(size.getCNOT()).isEqualTo(3);
					return size;
				});
	}

	@Test
	public void testCommentsInSameLine() throws IOException {
		VCSFile file = mock(VCSFile.class);
		when(file.readContent()).thenReturn(
				"/* first comment*/ /* second comment */ // third comment");

		Metrics metrics = spy(Metrics.class);
		doReturn(Optional.of(ELanguage.JAVA)).when(metrics).getLanguage(file);

		assertThat(metrics.computeSize(file))
				.isNotEmpty()
				.map(size -> {
					assertThat(size.getLOC()).isEqualTo(1);
					assertThat(size.getNOT()).isEqualTo(3);
					assertThat(size.getSLOC()).isEqualTo(0);
					assertThat(size.getSNOT()).isEqualTo(0);
					assertThat(size.getCLOC()).isEqualTo(1);
					assertThat(size.getCNOT()).isEqualTo(3);
					return size;
				});
	}
}
