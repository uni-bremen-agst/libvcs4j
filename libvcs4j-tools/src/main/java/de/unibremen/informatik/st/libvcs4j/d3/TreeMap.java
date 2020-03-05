package de.unibremen.informatik.st.libvcs4j.d3;

import de.unibremen.informatik.st.libvcs4j.FSTree;
import de.unibremen.informatik.st.libvcs4j.VCSFile;
import de.unibremen.informatik.st.libvcs4j.Validate;

import java.io.BufferedInputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Generates a HTML export that visualizes a collection of {@link VCSFile}
 * instances as a TreeMap with a HeatMap layer. The visualization is created by
 * mapping a file to a {@link Cell} (or a subclass of it), providing the
 * metrics {@link Cell#size} and {@link Cell#color} which are required to setup
 * the layout and fill the areas. While generating the HTML export,
 * {@code null} files, duplicates according to {@link Object#equals(Object)},
 * files without an associated cell, and files with size {@code 0} are
 * filtered.
 */
public class TreeMap {

	/**
	 * Used to check equality of double values.
	 */
	private static final double EPSILON = 0.0001;

	/**
	 * Stores the values that are required to build the TreeMap.
	 */
	public static class Cell {

		/**
		 * Defines the size of the corresponding rectangle.
		 *
		 * {@code size >= 0}
		 */
		private final double size;

		/**
		 * Defines the color intensity of the corresponding rectangle.
		 */
		private final double color;

		/**
		 * Creates a new cell with given size and color. Negative size values
		 * are fit to {@code 0}.
		 *
		 * @param pSize
		 * 		The size of the cell to create.
		 * @param pColor
		 * 		The color of the cell to create.
		 */
		public Cell(final double pSize, double pColor) {
			size = Math.max(0, pSize);
			color = pColor;
		}

		/**
		 * Returns the size of this cell.
		 *
		 * {@code size >= 0}
		 *
		 * @return
		 * 		The size of this cell.
		 */
		public final double getSize() {
			return size;
		}

		/**
		 * Returns the color intensity of this cell.
		 *
		 * @return
		 * 		The color intensity of this cell.
		 */
		public double getColor() {
			return color;
		}

		/**
		 * Aggregates {@code c1} and {@code c2}.
		 *
		 * The default implementation simply adds the size and color of
		 * {@code c1} and {@code c2}.
		 *
		 * @param c1
		 * 		The first value to aggregate.
		 * @param c2
		 * 		The second value to aggregate.
		 * @return
		 * 		The aggregation of {@code c1} and {@code c2}.
		 */
		protected Cell aggregate(final Cell c1, final Cell c2) {
			return new Cell(c1.getSize() + c2.getSize(),
					c1.getColor() + c2.getColor());
		}
	}

	/**
	 * A cell whose color intensity is in proportion to its size. Accordingly,
	 * the domain of {@link #color} is [0, 1].
	 */
	public static class RateCell extends Cell {

		/**
		 * Creates a new cell with given size and rate. Fits {@code pRate} to
		 * its domains.
		 *
		 * @param pSize
		 * 		The size of the cell to create.
		 * @param pRate
		 * 		The rate of the cell to create.
		 */
		public RateCell(final double pSize, final double pRate) {
			super(pSize, Math.min(Math.max(0, pRate), 1));
		}

		@Override
		public final double getColor() {
			return super.getColor();
		}

		@Override
		protected final Cell aggregate(final Cell c1, final Cell c2) {
			final double size = c1.getSize() + c2.getSize();
			final double rate = size == 0
					? 0 : (c1.getSize() * c1.getColor() +
					c2.getSize() * c2.getColor()) / size;
			return new RateCell(size, rate);
		}
	}

	/**
	 * The files to visualize.
	 */
	private final List<VCSFile> files;

	/**
	 * Maps a file to its cell.
	 */
	private final Function<VCSFile, ? extends Cell> cells;

	/**
	 * The minimum color intensity value. {@code < maxColor}
	 */
	private final double minColor;

	/**
	 * The maximum color intensity value. {@code > minColor}
	 */
	private final double maxColor;

	/**
	 * Creates a new TreeMap.
	 *
	 * @param pFiles
	 * 		The files to visualize.
	 * @param pCellFunction
	 * 		The function that is used to map a file to its cell.
	 * @param pMinColor
	 * 		The minimum color intensity value.
	 * @param pMaxColor
	 * 		The maximum color intensity value.
	 * @throws NullPointerException
	 * 		If any of the given arguments is {@code null}.
	 * @throws IllegalArgumentException
	 * 		If {@code pMinColor > pMaxColor}.
	 */
	public TreeMap(final Collection<VCSFile> pFiles,
			final Function<VCSFile, ? extends Cell> pCellFunction,
			final double pMinColor, final double pMaxColor)
			throws NullPointerException, IllegalArgumentException {
		Validate.notNull(pFiles);
		Validate.notNull(pCellFunction);
		if (pMinColor > pMaxColor) {
			throw new IllegalArgumentException(String.format(
					"Minimum color value (%f) > maximum color value (%f)",
					pMinColor, pMaxColor));
		}

		files = pFiles.stream()
				.filter(Objects::nonNull)
				.distinct()
				.collect(Collectors.toList());
		cells = pCellFunction;
		minColor = pMinColor;
		maxColor = pMaxColor;
	}

	/**
	 * Creates a new TreeMap with color domain [0, 1].
	 *
	 * @param pFiles
	 * 		The files to visualize.
	 * @param pCellFunction
	 * 		The function that is used to map a file to its cell.
	 * @throws NullPointerException
	 * 		If any of the given arguments is {@code null}.
	 */
	public TreeMap(final Collection<VCSFile> pFiles,
			final Function<VCSFile, ? extends RateCell> pCellFunction)
			throws NullPointerException {
		this(pFiles, pCellFunction, 0, 1);
	}

	/**
	 * Allows subclasses to map the size value of a cell to a string. This
	 * might be useful in cases where double values, for instance, must be
	 * mapped to ordinary int values. The returned string must be parsable by
	 * {@link Double#valueOf(String)} and must not exceed {@code pSize}'s
	 * domain.
	 *
	 * The default implementation uses {@link Double#toString(double)}.
	 *
	 * @param pSize
	 * 		The size to map.
	 * @return
	 * 		The mapped size.
	 */
	protected String sizeToString(final double pSize) {
		return Double.toString(pSize);
	}

	/**
	 * Allows subclasses to map the color value of a cell to a string. This might be
	 * useful in cases where double values, for instance, must be mapped to ordinary int
	 * values. The returned string must be parsable by {@link Double#valueOf(String)} and
	 * must not exceed {@code pColor}'s domain.
	 *
	 * The default implementation uses {@link Double#toString(double)}.
	 *
	 * @param pColor
	 * 		The color to map.
	 * @return
	 * 		The mapped color.
	 */
	protected String colorToString(final double pColor) {
		return Double.toString(pColor);
	}

	/**
	 * Generates the resulting HTML page as a string.
	 *
	 * @return
	 * 		The resulting HTML page as a string.
	 */
	public String generateHTML() {
		final String json = generateJSON();
		final String d3 = readD3File();
		return readHTMLFile()
				.replace("@MIN_COLOR@", String.valueOf(minColor))
				.replace("@MAX_COLOR@", String.valueOf(maxColor))
				.replace("@D3_SCRIPT@", d3)
				.replace("@JSON_STRING@", json);
	}

	/**
	 * Generates the JSON string that is used by the HTML page.
	 *
	 * @return
	 * 		The JSON string that is used by the HTML page.
	 */
	public String generateJSON() {
		final StringBuilder builder = new StringBuilder();
		final FSTree<Cell> tree = createTree();
		final FSTree.Visitor<Cell> visitor = new FSTree.Visitor<Cell>() {
			@Override
			public void visit(final FSTree<Cell> pTree) {
				super.visit(pTree);
				if (pTree.isRoot() && builder.length() > 0) {
					// Remove ',' generated by last sub node.
					builder.setLength(builder.length() - 1);
				}
			}

			@Override
			protected void visitDirectory(final FSTree<Cell> pDirectory) {
				final String name = pDirectory.getName();
				final Cell cell = pDirectory.getValue()
						.orElseThrow(IllegalStateException::new);
				builder.append("{");
				builder.append("\"name\": ");
				builder.append("\"").append(name).append("\"");
				builder.append(",");
				builder.append("\"color\":");
				builder.append(colorToString(cell.getColor()));
				builder.append(",");
				builder.append("\"children\": [");
				super.visitDirectory(pDirectory);
				// Remove ',' generated by last sub node.
				builder.setLength(builder.length() - 1);
				builder.append("]");
				builder.append("},");
			}

			@Override
			protected void visitFile(final FSTree<Cell> pFile) {
				final String name = pFile.getName();
				final Cell cell = pFile.getValue()
						.orElseThrow(IllegalStateException::new);
				builder.append("{");
				builder.append("\"name\": ");
				builder.append("\"").append(name).append("\"");
				builder.append(",");
				builder.append("\"value\": ");
				builder.append(sizeToString(cell.getSize()));
				builder.append(",");
				builder.append("\"color\" :");
				builder.append(colorToString(cell.getColor()));
				builder.append("},");
				super.visitFile(pFile);
			}
		};
		visitor.visit(tree);
		return builder.toString();
	}

	/**
	 * Reads the D3 script.
	 *
	 * @return
	 * 		The D3 script.
	 */
	private String readD3File() {
		final BufferedInputStream bis = new BufferedInputStream(getClass()
				.getResourceAsStream("/d3.v3.min.js"));
		try (Scanner scanner = new Scanner(bis)) {
			return scanner.useDelimiter("\\A").next();
		}
	}

	/**
	 * Reads the HTML template.
	 *
	 * @return
	 * 		The HTML template.
	 */
	private String readHTMLFile() {
		final BufferedInputStream bis = new BufferedInputStream(getClass()
				.getResourceAsStream("/treemap.html.in"));
		try (Scanner scanner = new Scanner(bis)) {
			return scanner.useDelimiter("\\A").next();
		}
	}

	/**
	 * Creates the tree that is parsed by {@link #generateJSON()}.
	 *
	 * @return
	 * 		The tree that is parsed by {@link #generateJSON()}.
	 */
	private FSTree<Cell> createTree() {
		final Map<VCSFile, Cell> mapping = new HashMap<>();
		final Iterator<VCSFile> iter = files.iterator();
		while (iter.hasNext()) {
			final VCSFile file = iter.next();
			final Cell cell = cells.apply(file);
			if (cell == null) {
				iter.remove();
			} else if (Math.abs(cell.getSize()) < EPSILON) {
				iter.remove();
			} else {
				mapping.put(file, cell);
			}
		}
		return FSTree.of(files,
				mapping::get,
				(c1, c2) -> c1.aggregate(c1, c2))
				.compact();
	}
}
