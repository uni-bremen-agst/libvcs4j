/*-------------------------------------------------------------------------+
|                                                                          |
| Copyright 2005-2011 The ConQAT Project                                   |
|                                                                          |
| Licensed under the Apache License, Version 2.0 (the "License");          |
| you may not use this file except in compliance with the License.         |
| You may obtain a copy of the License at                                  |
|                                                                          |
|    http://www.apache.org/licenses/LICENSE-2.0                            |
|                                                                          |
| Unless required by applicable law or agreed to in writing, software      |
| distributed under the License is distributed on an "AS IS" BASIS,        |
| WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. |
| See the License for the specific language governing permissions and      |
| limitations under the License.                                           |
+-------------------------------------------------------------------------*/
package org.conqat.lib.commons.graph;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;

import javax.imageio.ImageIO;

import org.conqat.lib.commons.io.StreamReaderThread;

/**
 * Java interface to the Graphviz graph drawing toolkit.
 * 
 * @author Florian Deissenboeck
 * @author Benjamin Hummel
 * @author $Author: hummelb $
 * @version $Rev: 50305 $
 * @ConQAT.Rating GREEN Hash: 8E845A841F6C804F8333EE95643B7927
 */
public class GraphvizGenerator {

	/** Path to the layout engine executable. */
	private final String layoutEnginePath;

	/**
	 * Create a new generator that uses <code>dot</code> and expects it to be on
	 * the path.
	 * 
	 */
	public GraphvizGenerator() {
		this("dot");
	}

	/**
	 * Create a new generator by specifying the executable of the layout engine.
	 * This may be used if <code>dot</code> is not on the path or if another
	 * layout engine like <code>neato</code> should be used.
	 * 
	 * @param layoutEnginePath
	 *            path to layout engine excutable
	 */
	public GraphvizGenerator(String layoutEnginePath) {
		this.layoutEnginePath = layoutEnginePath;
	}

	/**
	 * Export a graph to a file.
	 * 
	 * @param description
	 *            the graph description
	 * @param file
	 *            the file to export to.
	 * @param format
	 *            the export format.
	 * @throws IOException
	 *             if an I/O problem occurrs.
	 * @throws GraphvizException
	 *             if Graphviz produced an error (exit code != 0)
	 */
	public void generateFile(String description, File file,
			EGraphvizOutputFormat format) throws IOException, GraphvizException {
		runDot(description, null, "-T" + format.name().toLowerCase(), "-o"
				+ file);
	}

	/**
	 * Export a graph to a file and return the HTML image map code.
	 * 
	 * @param description
	 *            the graph description
	 * @param file
	 *            the file to export to.
	 * @param format
	 *            the export format.
	 * @return the generated image map. These are only area-tags. The map tags
	 *         including the name of the map must be created by the calling
	 *         application.
	 * @throws IOException
	 *             if an I/O problem occurrs.
	 * @throws GraphvizException
	 *             if Graphviz produced an error (exit code != 0)
	 */
	public String generateFileAndImageMap(String description, File file,
			EGraphvizOutputFormat format) throws IOException, GraphvizException {
		TextReader reader = new TextReader();
		runDot(description, reader, "-T" + format.name().toLowerCase(), "-o"
				+ file, "-Tcmap");
		return reader.contents.toString();
	}

	/**
	 * Generate an image from a graph description. This uses Graphviz to
	 * generate a PNG image of the graph and javax.imageio to create the image
	 * object. All communication with Graphviz is handled via streams so no
	 * temporary files are used.
	 * 
	 * @param description
	 *            the graph description.
	 * @return the image
	 * @throws IOException
	 *             if an I/O problem occurrs.
	 * @throws GraphvizException
	 *             if Graphviz produced an error (exit code != 0)
	 */
	public BufferedImage generateImage(String description) throws IOException,
			GraphvizException {
		ImageReader reader = new ImageReader();
		runDot(description, reader, "-Tpng");
		return reader.image;
	}

	/**
	 * Executes DOT, feeding in the provided graph description. The output of
	 * dot may be handled using an {@link IStreamReader}. DOT errorr are handled
	 * in this method.
	 * 
	 * @param description
	 *            the graph description.
	 * @param streamReader
	 *            the reader used for the output stream of DOT. If this is null,
	 *            a dummy reader is used to keep DOT from blocking.
	 * @param arguments
	 *            the arguments passed to DOT.
	 * @throws IOException
	 *             if an I/O problem occurrs.
	 * @throws GraphvizException
	 *             if Graphviz produced an error (exit code != 0)
	 */
	private void runDot(String description, IStreamReader streamReader,
			String... arguments) throws IOException, GraphvizException {

		String[] completeArguments = new String[arguments.length + 1];
		completeArguments[0] = layoutEnginePath;
		for (int i = 0; i < arguments.length; ++i) {
			completeArguments[i + 1] = arguments[i];
		}

		ProcessBuilder builder = new ProcessBuilder(completeArguments);
		Process dotProcess = builder.start();

		// read error for later use
		StreamReaderThread errReader = new StreamReaderThread(
				dotProcess.getErrorStream());

		// pipe graph into dot
		Writer stdIn = new OutputStreamWriter(dotProcess.getOutputStream());
		stdIn.write(description);
		stdIn.close();

		if (streamReader == null) {
			// read dot standard output to drain the buffer, then throw away
			new StreamReaderThread(dotProcess.getInputStream());
		} else {
			// reading may happen in this thread, as stderr is read in a thread
			// of its own
			streamReader.performReading(dotProcess.getInputStream());
		}

		// wait for dot
		try {
			dotProcess.waitFor();
		} catch (InterruptedException e) {
			// ignore this one
		}

		String errorContent = errReader.getContent();
		if (dotProcess.exitValue() != 0
		// recent versions do not exit with non-null on syntax errors
				|| errorContent.contains("syntax error")) {
			throw new GraphvizException(errorContent);
		}
	}

	/**
	 * Interface used from
	 * {@link GraphvizGenerator#runDot(String, org.conqat.lib.commons.graph.GraphvizGenerator.IStreamReader, String[])}
	 * .
	 */
	private static interface IStreamReader {

		/** Perform the desired action on the given input stream. */
		void performReading(InputStream inputStream) throws IOException;
	}

	/** A stream reader for reading an image. */
	private static class ImageReader implements IStreamReader {
		/** The image read. */
		BufferedImage image = null;

		/** {@inheritDoc} */
		@Override
		public void performReading(InputStream inputStream) throws IOException {
			image = ImageIO.read(inputStream);
		}
	}

	/** A stream reader for reading plain text. */
	private static class TextReader implements IStreamReader {
		/** The contents read from the stream. */
		StringBuilder contents = new StringBuilder();

		/** {@inheritDoc} */
		@Override
		public void performReading(InputStream inputStream) throws IOException {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					inputStream));
			char[] buffer = new char[1024];
			int read = 0;
			while ((read = reader.read(buffer)) != -1) {
				contents.append(buffer, 0, read);
			}
		}
	}
}