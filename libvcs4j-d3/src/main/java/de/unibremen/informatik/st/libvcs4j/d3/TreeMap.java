package de.unibremen.informatik.st.libvcs4j.d3;

import de.unibremen.informatik.st.libvcs4j.FSTree;
import de.unibremen.informatik.st.libvcs4j.VCSFile;

import java.io.BufferedInputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Generates a HTML export that depicts a collection of {@link VCSFile}
 * instances as a TreeMap with a HeatMap layer.
 */
public class TreeMap {

	/**
	 * Stores the values that are required to build the TreeMap. Subclasses may override
	 * {@link #minColor()}, {@link #maxColor()}, and {@link #aggregate(Cell, Cell)} to
	 * adapt the default behavior.
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
		 *
		 * {@code minColor() <= color <= maxColor()}.
		 */
		private final double color;

		/**
		 * Creates a new cell with given size and color. Fits {@code pSize} and
		 * {@code pColor} to their domains.
		 *
		 * @param pSize
		 * 		The size of the cell to create.
		 * @param pColor
		 * 		The color of the cell to create.
		 */
		public Cell(final double pSize, double pColor) {
			if (pSize < 0) {
				throw new IllegalArgumentException("size < 0");
			}
			size = Math.max(0, pSize);
			color = Math.max(Math.min(maxColor(), pColor), minColor());
		}

		/**
		 * Returns the size of this cell.
		 *
		 * @return
		 * 		The size of this cell.
		 */
		public double getSize() {
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
		 * Returns the minimum color value.
		 *
		 * The default implementation returns {@code 0}.
		 *
		 * @return
		 * 		The minimum color value.
		 */
		protected double minColor() {
			return 0;
		}

		/**
		 * Returns the maximum color value.
		 *
		 * The default implementation returns {@code 1}.
		 *
		 * @return
		 * 		The maximum color value.
		 */
		protected double maxColor() {
			return 1;
		}

		/**
		 * Aggregates {@code c1} and {@code c2}. This method assumes that the color
		 * values of {@code c1} and {@code c2} have the same domain ({@link #minColor()})
		 * and {@link #maxColor()}). Likewise, the domain of the returned cell must be
		 * compatible to {@code c1} and {@code c2}.
		 *
		 * The default implementation adds the size and color of {@code c1} and
		 * {@code c2} and fits the resulting values to their domains.
		 *
		 * @param c1
		 * 		The first value to aggregate.
		 * @param c2
		 * 		The second value to aggregate.
		 * @return
		 * 		The aggregation of {@code c1} and {@code c2}.
		 */
		protected Cell aggregate(final Cell c1, final Cell c2) {
			final double size = Math.max(0, c1.getSize() + c2.getSize());
			final double color = Math.max(Math.min(maxColor(),
					c1.getColor() + c2.getColor()), minColor());
			return new Cell(size, color);
		}
	}

	/**
	 * A cell whose color intensity is in proportion to its size. Accordingly, the domain
	 * of {@link #color} is [0, 1].
	 */
	public static class RateCell extends Cell {

		/**
		 * Creates a new cell with given size and rate. Fits {@code pSize} and
		 * {@code pRate} to their domains.
		 *
		 * @param pSize
		 * 		The size of the cell to create.
		 * @param pRate
		 * 		The rate of the cell to create.
		 */
		public RateCell(final double pSize, final double pRate) {
			super(pSize, pRate);
		}

		@Override
		protected final double minColor() {
			return 0;
		}

		@Override
		protected final double maxColor() {
			return 1;
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
	 * The tree to visualize.
	 */
	private final FSTree<Cell> tree;

	/**
	 * Creates a TreeMap of the given files and mapping function. {@code null}
	 * values in {@code pFiles}, duplicates (according to
	 * {@link VCSFile#equals(Object)}) and files without an associated cell are
	 * filtered.
	 *
	 * @param pFiles
	 * 		The files to visualize.
	 * @param pValue
	 * 		The function that is used to map a file to its cell.
	 * @throws NullPointerException
	 * 		If any of the given arguments is {@code null}.
	 */
	public TreeMap(final List<VCSFile> pFiles,
			final Function<VCSFile, ? extends Cell> pValue) {
		Objects.requireNonNull(pFiles);
		Objects.requireNonNull(pValue);
		final List<VCSFile> files = pFiles.stream()
				.filter(Objects::nonNull)
				.distinct()
				.collect(Collectors.toList());
		final Map<VCSFile, Cell> mapping = new HashMap<>();
		final Iterator<VCSFile> iter = files.iterator();
		while (iter.hasNext()) {
			final VCSFile file = iter.next();
			final Cell cell = pValue.apply(file);
			if (cell == null) {
				iter.remove();
			} else {
				mapping.put(file, cell);
			}
		}
		tree = FSTree
				.of(files,mapping::get, (c1, c2) -> c1.aggregate(c1, c2))
				.compact();
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
	 * Generated the resulting HTML page as a string.
	 *
	 * @return
	 * 		The resulting HTML page as a string.
	 */
	public String generateHTML() {
		final String json = generateJSON();
		final String d3 = readD3File();
		return readHTMLFile()
				.replace("@D3_SCRIPT@", d3)
				.replace("@JSON_STRING@", json);
	}

	/**
	 * Generates the resulting JSON string that is used by the HTML page.
	 *
	 * @return
	 * 		The resulting JSON string that is used by the HTML page.
	 */
	public String generateJSON() {
		final StringBuilder builder = new StringBuilder();
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
				final List<FSTree<Cell>> nodes = pDirectory.getNodes();
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
}
