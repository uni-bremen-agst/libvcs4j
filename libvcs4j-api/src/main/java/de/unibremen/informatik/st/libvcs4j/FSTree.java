package de.unibremen.informatik.st.libvcs4j;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Allows to represent a collection of {@link VCSFile} instances as a file
 * system tree with a generic value that may be attached to a file.
 *
 * @param <V>
 *     The attached value.
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class FSTree<V> {

	/**
	 * This path is used for empty trees.
	 */
	public static final String EMPTY_DIRECTORY = "<empty>";

	/**
	 * This path is used for trees with multiple root nodes.
	 */
	public static final String ROOT_DIRECTORY = "/";

	/**
	 * A simple visitor to process {@link FSTree} instances.
	 */
	public class Visitor {

		/**
		 * Delegates {@code pTree} to {@link #visitDirectory(FSTree)} or
		 * {@link #visitFile(FSTree)} depending on whether {@code pTree}
		 * represents a directory or a file.
		 *
		 * @param pTree
		 * 		The tree to visit and delegate.
		 */
		public void visit(final FSTree<V> pTree) {
			if (pTree.isDirectory()) {
				visitDirectory(pTree);
			} else {
				visitFile(pTree);
			}
		}

		/**
		 * Visits the given directory.
		 *
		 * @param pDirectory
		 * 		The directory to visit.
		 */
		protected void visitDirectory(final FSTree<V> pDirectory) {
			assert pDirectory.nodes != null;
			pDirectory.nodes.forEach(this::visit);
		}

		/**
		 * Visits the given file.
		 *
		 * @param pFile
		 * 		The tree containing the file to visit.
		 */
		protected void visitFile(final FSTree<V> pFile) {
			visitFile(pFile.file);
		}

		/**
		 * Visits the given file.
		 *
		 * @param pFile
		 * 		The file to visit.
		 */
		protected void visitFile(final VCSFile pFile) {
		}
	}

	/**
	 * The parent of this tree. Is {@code null} for the root node.
	 */
	private final FSTree<V> parent;

	/**
	 * The relative path (relative to {@link VCSEngine#getRoot()}) of the
	 * referenced file (if {@link #file} is present) or directory (if
	 * {@link #nodes} is present).
	 */
	private final String path;

	/**
	 * The referenced file. Is {@code null} if {@link #nodes} is present.
	 */
	private final VCSFile file;

	/**
	 * The value attached to {@link #file} (if {@link #file} is present). May
	 * be {@code null}.
	 */
	private final V value;

	/**
	 * The referenced sub files and directories. Is {@code null} if
	 * {@link #file} is present.
	 */
	private final List<FSTree<V>> nodes;

	/**
	 * Is used to calculate the value of a directory by aggregating the values
	 * of all sub files and directories.
	 */
	private final BiFunction<V, V, V> aggregator;

	/**
	 * Creates a file with given parent, {@link VCSFile}, and value function.
	 *
	 * @param pParent
	 * 		The parent of the file to create. Use {@code null} for root nodes.
	 * @param pFile
	 * 		The referenced {@link VCSFile} instance.
	 * @param pValueOf
	 * 		The function that is used to map a file to its value. The function
	 * 		may return {@code null}.
	 * @throws NullPointerException
	 *      If {@code pFile} or {@code pValueOf} is {@code null}.
	 */
	private FSTree(final FSTree<V> pParent, final VCSFile pFile,
				   final Function<VCSFile, V> pValueOf) {
		parent = pParent;
		file = Objects.requireNonNull(pFile);
		path = file.toRelativePath().toString();
		value = Objects.requireNonNull(pValueOf).apply(file);
		nodes = null;
		aggregator = null;
	}

	/**
	 * Creates a directory with given parent, relative path, and aggregation
	 * function.
	 *
	 * @param pParent
	 * 		The parent of the directory to create. Use {@code null} for root
	 * 		nodes.
	 * @param pPath
	 *      The relative path of the directory.
	 * @param pAggregator
	 * 		The aggregation function used to calculate the value of a
	 * 		directory. The function must not handle {@code null} values.
	 * @throws NullPointerException
	 *      If {@code pPath} or {@code pAggregator} is {@code null}.
	 */
	private FSTree(final FSTree<V> pParent, final String pPath,
				   final BiFunction<V, V, V> pAggregator) {
		parent = pParent;
		path = Objects.requireNonNull(pPath);
		nodes = new ArrayList<>();
		aggregator = Objects.requireNonNull(pAggregator);
		file = null;
		value = null;
	}

	/**
	 * Creates a tree from the given list of {@link VCSFile} instances.
	 *
	 * @param pFiles
	 * 		The files to create the tree from.
	 * @param pValueOf
	 * 		The function that is used to map a file to its value. The function
	 * 		may return {@code null}.
	 * @param pAggregator
	 * 		The aggregation function used to calculate the value of a
	 * 		directory. The function must not handle {@code null} values.
	 * @param <V>
	 *     	The type of the values attached to files.
	 * @return
	 * 		A tree representing the list of files.
	 * @throws NullPointerException
	 * 		If any of the given arguments is {@code null}.
	 * @throws IllegalArgumentException
	 * 		If {@code pFiles} contains {@code null}.
	 */
	public static <V> FSTree<V> of(
			final Collection<VCSFile> pFiles,
			final Function<VCSFile, V> pValueOf,
			final BiFunction<V, V, V> pAggregator)
			throws NullPointerException, IllegalArgumentException {
		Objects.requireNonNull(pFiles);
		Objects.requireNonNull(pValueOf);
		Objects.requireNonNull(pAggregator);
		if (pFiles.contains(null)) {
			throw new IllegalArgumentException();
		}

		final Set<FSTree<V>> treesWithoutParent = new HashSet<>();
		final Map<String, FSTree<V>> cache = new HashMap<>();
		pFiles.forEach(f -> {
			// (1) "home/user/file.txt" -> "home", "user", "file.txt"
			final Path relativePath = f.toRelativePath();
			final List<String> parts = new ArrayList<>();
			relativePath.forEach(p -> parts.add(p.toString()));

			// (2) For each directory ("home", "user")...
			FSTree<V> parent = null;
			for (int i = 0; i < parts.size() - 1; i++) {
				final String path = String.join(
						File.separator, parts.subList(0, i + 1));
				FSTree<V> dir = cache.get(path);
				if (dir == null) {
					dir = new FSTree<>(parent, path, pAggregator);
					cache.put(path, dir);
					if (parent != null) {
						parent.nodes.add(dir);
					}
				}
				if (parent == null) {
					treesWithoutParent.add(dir);
				}
				parent = dir;
			}

			// (3) Add file ("file.txt").
			final String relativePathStr = relativePath.toString();
			FSTree<V> file = cache.get(relativePathStr);
			if (file == null) {
				file = new FSTree<>(parent, f, pValueOf);
				cache.put(relativePathStr, file);
				if (parent != null) {
					parent.nodes.add(file);
				}
			}
			if (parent == null) {
				treesWithoutParent.add(file);
			}
		});

		if (treesWithoutParent.isEmpty()) {
			return new FSTree<>(null, EMPTY_DIRECTORY, pAggregator);
		} else if (treesWithoutParent.size() > 1) {
			final FSTree<V> root = new FSTree<>(
					null, ROOT_DIRECTORY, pAggregator);
			root.nodes.addAll(treesWithoutParent);
			return root;
		} else {
			return treesWithoutParent.iterator().next();
		}
	}

	/**
	 * Creates a tree from the given list of {@link VCSFile} instances where
	 * the files have to values.
	 *
	 * @param pFiles
	 * 		The files to create the tree from.
	 * @return
	 * 		A tree representing the list of files.
	 * @throws NullPointerException
	 * 		If {@code pFile} is {@code null}.
	 * @throws IllegalArgumentException
	 * 		If {@code pFiles} contains {@code null}.
	 */
	public static FSTree<Void> of(final Collection<VCSFile> pFiles)
			throws NullPointerException, IllegalArgumentException {
		return of(pFiles, f -> null, (v1, v2) -> null);
	}

	/**
	 * Returns the parent of this tree.
	 *
	 * @return
	 * 		The parent of this tree.
	 */
	public Optional<FSTree<V>> getParent() {
		return Optional.ofNullable(parent);
	}

	/**
	 * Returns the relative path (relative to {@link VCSEngine#getRoot()}) of
	 * this file (if {@link #getFile()} is present) or directory (if
	 * {@link #getNodes()} is present).
	 *
	 * @return
	 *      The relative path of this file or directory.
	 */
	public String getPath() {
		return path;
	}

	/**
	 * Returns the referenced {@link VCSFile} if this tree is a file.
	 *
	 * @return
	 *      The referenced {@link VCSFile}.
	 */
	public Optional<VCSFile> getFile() {
		return Optional.ofNullable(file);
	}

	/**
	 * Either returns the value of this file (if this tree represents a file)
	 * or the aggregated values of all (recursively) sub files (if this tree
	 * represents a directory).
	 *
	 * @return
	 * 		The value of this file or the aggregated values of all
	 * 		(recursively) sub files. If a file has no value or a directory
	 * 		contains only files without a value, an empty {@link Optional} is
	 * 		returned.
	 */
	public Optional<V> getValue() {
		final List<V> values = new ArrayList<>();
		final Visitor visitor = new Visitor() {
			@Override
			protected void visitFile(final FSTree<V> pTree) {
				if (pTree.value != null) {
					values.add(pTree.value);
				}
				super.visitFile(pTree);
			}
		};
		visitor.visit(this);
		return values.parallelStream()
				.reduce(aggregator::apply);
	}

	/**
	 * Returns the sub files and directories if this tree is a directory. If
	 * this tree is a file, an empty list is returned.
	 *
	 * @return
	 *      The sub files and directories of this tree.
	 */
	public List<FSTree<V>> getNodes() {
		return nodes == null
				? Collections.emptyList()
				: new ArrayList<>(nodes);
	}

	/**
	 * Returns all files of this tree.
	 *
	 * @return
	 * 		All files of this tree.
	 */
	public List<VCSFile> getFiles() {
		final List<VCSFile> files = new ArrayList<>();
		final Visitor visitor = new Visitor() {
			@Override
			protected void visitFile(final VCSFile pFile) {
				files.add(pFile);
				super.visitFile(pFile);
			}
		};
		visitor.visit(this);
		return files;
	}

	/**
	 * Returns the root of this tree.
	 *
	 * @return
	 * 		The root of this tree.
	 */
	public FSTree<V> getRoot() {
		FSTree<V> root = this;
		while (root.getParent().isPresent()) {
			root = root.getParent().get();
		}
		return root;
	}

	/**
	 * Navigates to the tree located at {@code pPath}. Unlike conventional
	 * navigation rules, this method allows to navigate "beyond" a regular
	 * file. That is, for instance, a file's parent may be addressed like this:
	 *
	 *     "src/A.java/.."
	 *
	 * where "A.java" is a regular file and ".." points to its parent. If
	 * {@code pPath} is empty, {@code this} is returned.
	 *
	 * @param pPath
	 * 		The relative path of the tree to navigate to.
	 * @return
	 * 		The tree located at {@code pPath}, if such a tree exists.
	 */
	public Optional<FSTree<V>> navigateTo(final String pPath) {
		if (pPath.isEmpty()) {
			return Optional.of(this);
		}

		final Queue<String> parts = new ArrayDeque<>();
		parts.addAll(Arrays.asList(pPath.replace("\\", "/").split("/")));
		final String head = parts.poll();
		final String tail = String.join("/", parts);

		switch (head) {
			case ".":
				return navigateTo(tail);
			case "..":
				final Optional<FSTree<V>> parent = getParent();
				return parent.isPresent()
						? parent.get().navigateTo(tail)
						: navigateTo(tail);
			default:
				if (isFile()) {
					return tail.isEmpty() && hasFileName(head)
							? Optional.of(this) : Optional.empty();
				} else {
					for (final FSTree<V> node : nodes) {
						if (node.hasFileName(head)) {
							return node.navigateTo(tail);
						}
					}
					return Optional.empty();
				}
		}
	}

	/**
	 * Returns whether the file name of this tree matches the given file name.
	 *
	 * @param pFileName
	 * 		The file name to match with the file name of this tree.
	 * @return
	 * 		{@code true} if the file of this tree matches {@code pFileName},
	 * 		{@code false} otherwise.
	 */
	private boolean hasFileName(final String pFileName) {
		final String fileName = Paths.get(path).getFileName().toString();
		return fileName.equals(pFileName);
	}

	/**
	 * Returns whether this tree is a file.
	 *
	 * @return
	 * 		{@code true} if this tree is a file, {@code false} otherwise.
	 */
	public boolean isFile() {
		return file != null;
	}

	/**
	 * Returns whether this tree is a directory.
	 *
	 * @return
	 * 		{@code true} if this tree is a directory, {@code false} otherwise.
	 */
	public boolean isDirectory() {
		return file == null;
	}

	/**
	 * Returns whether this tree is a virtual root directory that has been
	 * created to cover multiple actual roots.
	 *
	 * @return
	 * 		{@code true} if this tree is a virtual root directory,
	 * 		{@code false} otherwise.
	 */
	public boolean isVirtualRoot() {
		return path.equals(EMPTY_DIRECTORY) || path.equals("/");
	}
}
